package pl.yoisenshu.smg.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.network.packet.PacketDecoder;
import pl.yoisenshu.smg.network.packet.PacketEncoder;
import pl.yoisenshu.smg.network.connection.ClientConnection;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;
import pl.yoisenshu.smg.network.packet.server.ServerEntityRemovedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerLoginSuccessPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerJoinedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerWorldDataPacket;
import pl.yoisenshu.smg.network.packet.util.ExceptionHandler;
import pl.yoisenshu.smg.server.entity.Entity;
import pl.yoisenshu.smg.server.entity.Player;
import pl.yoisenshu.smg.server.world.World;
import pl.yoisenshu.smg.world.Position;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class SimpleMultiplayerGameServer {

    public static final int PORT = 2323;

    private ServerBootstrap serverBootstrap;
    private Channel channel;

    @Getter
    private long tick = 0;
    private final ConcurrentHashMap<Channel, Player> players = new ConcurrentHashMap<>();
    @Getter
    private World world;

    public SimpleMultiplayerGameServer() {}

    public void start() throws InterruptedException {

        world = new World(
            "World_" + Instant.now().toEpochMilli() % 1000
        );

        var tickTask = new Timer();
        tickTask.schedule(new TimerTask() {
            @Override
            public void run() {
                for (@NotNull Player player : players.values()) {
                    if(!player.isOnline() || player.isRemoved()) {
                        if(!player.isRemoved()) {
                            player.remove();
                        }
                        players.remove(player.getConnection().getChannel());
                    }
                }
                world.tick(tick);
            }
        }, 5000, 40); // ~ 13 milisekund opóźnienia było przy 50ms

        EventLoopGroup boss = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        var server = this;
        try {
            serverBootstrap = new ServerBootstrap();
            serverBootstrap.group(boss, worker)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel ch) {
                        ChannelPipeline pip = ch.pipeline();
                        pip.addLast(new PacketEncoder());
                        pip.addLast(new PacketDecoder(PacketDecoder.PacketBound.SERVERBOUND));
                        pip.addLast(new ExceptionHandler("Server"));

                        pip.addLast(new LoginHandler((ctx, packet) -> {
                            System.out.println("[Server] New connection from " + ctx.channel().remoteAddress() + " with username: " + packet.getUsername());
                            if(players.containsKey(ctx.channel())) {
                                System.out.println("[Server] CLOSING CONNECTION!");
                                ctx.close();
                                return;
                            }

                            var player = new Player(
                                world,
                                new Position(100, 100),
                                new ClientConnection(ctx.channel()),
                                packet.getUsername(),
                                packet.getSkinColor()
                            );
                            players.put(ctx.channel(), player);

                            player.sendPacket(new ServerLoginSuccessPacket(player.getId(), player.getPosition()));

                            world.addPlayer(player);

                            System.out.println("[Server] Player " + player.getUsername() + " logged in with entity ID: " + player.getId());

                            broadcastMessage("Player " + player.getUsername() + " has joined the game!");

                            players.forEach((c, p) -> {
                                if (p.getId() != player.getId()) {
                                    p.sendPacket(new ServerPlayerJoinedPacket(
                                        player.getId(),
                                        player.getPosition(),
                                        player.getUsername(),
                                        player.getSkinColor()
                                    ));
                                }
                            });
                        }));

                        pip.addLast(new ConnectionCloseHandler(server));
                        pip.addLast(new DisconnectHandler(server));
                        pip.addLast(new ChatMessageHandler(server));
                        pip.addLast(new PlayerMoveHandler(server));
                        pip.addLast(new PlayerBombPlaceHandler(server));
                    }
                });

            ChannelFuture f = serverBootstrap.bind(PORT).sync();
            channel = f.channel();
            System.out.println("[Server] Game server started on port " + PORT);
            f.channel().closeFuture().sync();
        } finally {
            boss.shutdownGracefully();
            worker.shutdownGracefully();
            tickTask.cancel();
        }
    }

    private void onTick() {
        tick++;
        for (Entity entity : world.getEntities()) {
            if(entity.isRemoved()) {
                entity.remove();
                players.forEach((c, p) -> p.sendPacket(new ServerEntityRemovedPacket(entity.getId())));
                continue;
            }
            entity.tick(tick);
        }
    }

    public ServerWorldDataPacket createWorldDataPacket() {
        Set<ServerWorldDataPacket.PlayerData> playerData = new HashSet<>();
        for (Player handle : players.values()) {
            playerData.add(new ServerWorldDataPacket.PlayerData(
                handle.getId(),
                handle.getUsername(),
                handle.getPosition(),
                handle.getSkinColor()
            ));
        }
        return new ServerWorldDataPacket(world.getName(), playerData);
    }

    public void shutdown() {
        System.out.println("[Server] Shutting down the server...");
        channel.close();
    }

    public void broadcastMessage(@NotNull String message) {
        System.out.println("[Server] [Broadcast] " + message);
        players.forEach((c, p) -> p.sendPacket(new ServerChatMessagePacket(message)));
    }

    @Nullable
    public Player getPlayerByChannel(@NotNull Channel channel) {
        return players.get(channel);
    }

    @NotNull
    public Set<Player> getOnlinePlayers() {
        return players.values().stream().filter(Player::isOnline).collect(Collectors.toUnmodifiableSet());
    }
}
