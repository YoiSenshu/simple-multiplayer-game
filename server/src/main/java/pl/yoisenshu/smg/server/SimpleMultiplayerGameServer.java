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
import pl.yoisenshu.smg.server.entity.BaseEntity;
import pl.yoisenshu.smg.network.packet.PacketDecoder;
import pl.yoisenshu.smg.network.packet.PacketEncoder;
import pl.yoisenshu.smg.network.connection.ClientConnection;
import pl.yoisenshu.smg.network.packet.server.ServerChatMessagePacket;
import pl.yoisenshu.smg.network.packet.server.ServerEntityRemovedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerLoginSuccessPacket;
import pl.yoisenshu.smg.network.packet.server.ServerPlayerJoinedPacket;
import pl.yoisenshu.smg.network.packet.server.ServerWorldDataPacket;
import pl.yoisenshu.smg.network.packet.util.ExceptionHandler;
import pl.yoisenshu.smg.server.entity.Player;
import pl.yoisenshu.smg.world.Position;

import java.time.Instant;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

public class SimpleMultiplayerGameServer {

    public static final int PORT = 2323;

    private ServerBootstrap serverBootstrap;
    private Channel channel;

    private final String worldName = "World " + Instant.now().getNano();
    @Getter private int tick = 0;
    @Getter final Map<Integer, BaseEntity> entities = new ConcurrentHashMap<>();
    @Getter final Map<Channel, Player> players = new ConcurrentHashMap<>();

    public SimpleMultiplayerGameServer() throws InterruptedException {}

    public void start() throws InterruptedException {

        var tickTask = new Timer();
        tickTask.schedule(new TimerTask() {
            @Override
            public void run() {
                onTick();
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
                                getNewEntityId(),
                                new Position(100, 100),
                                new ClientConnection(ctx.channel()),
                                packet.getUsername(),
                                packet.getSkinColor()
                            );
                            entities.put(player.getId(), player);
                            players.put(ctx.channel(), player);

                            player.sendPacket(new ServerLoginSuccessPacket(player.getId(), player.getPosition()));

                            System.out.println("[Server] Player " + player.getUsername() + " logged in with entity ID: " + player.getId());

                            System.out.println("[Server] Sending world data to " + player.getUsername() + " with entity ID: " + player.getId());
                            player.sendPacket(createWorldDataPacket());
                            System.out.println("[Server] World data sent to " + player.getUsername() + " with entity ID: " + player.getId());

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
        for (BaseEntity entity : entities.values()) {
            if(entity.isRemoved()) {
                entities.remove(entity.getId());
                players.forEach((c, p) -> p.sendPacket(new ServerEntityRemovedPacket(entity.getId())));
                continue;
            }
            entity.tick(tick);
        }
    }

    public int getNewEntityId() {
        return entities.keySet().stream()
            .mapToInt(Integer::intValue)
            .max()
            .orElse(0) + 1;
    }

    private ServerWorldDataPacket createWorldDataPacket() {
        Set<ServerWorldDataPacket.PlayerData> playerData = new HashSet<>();
        for (Player handle : players.values()) {
            playerData.add(new ServerWorldDataPacket.PlayerData(
                handle.getId(),
                handle.getUsername(),
                handle.getPosition(),
                handle.getSkinColor()
            ));
        }
        return new ServerWorldDataPacket(worldName, playerData);
    }

    public void shutdown() {
        System.out.println("[Server] Shutting down the server...");
        channel.close();
    }

    public void broadcastMessage(@NotNull String message) {
        System.out.println("[Server] [Broadcast] " + message);
        players.forEach((c, p) -> p.sendPacket(new ServerChatMessagePacket(message)));
    }
}
