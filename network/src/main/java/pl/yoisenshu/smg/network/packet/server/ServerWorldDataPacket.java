package pl.yoisenshu.smg.network.packet.server;

import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.network.packet.PacketType;
import pl.yoisenshu.smg.network.packet.util.PacketUtil;
import pl.yoisenshu.smg.player.Player;
import pl.yoisenshu.smg.world.Position;

import java.util.HashSet;
import java.util.Set;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ServerWorldDataPacket implements ClientboundPacket {

    private String worldName;
    private Set<PlayerData> players;

    @Override
    public void encode(@NotNull ByteBuf out) {

        PacketUtil.writeString(out, worldName);

        out.writeInt(players.size());
        for (PlayerData player : players) {
            out.writeInt(player.entityId());
            byte[] playerNameBytes = player.username().getBytes();
            out.writeInt(playerNameBytes.length);
            out.writeBytes(playerNameBytes);
            out.writeInt(player.position().x());
            out.writeInt(player.position().y());
            out.writeInt(player.skinColor().ordinal());
        }
    }

    @Override
    public void decode(@NotNull ByteBuf in) {

        this.worldName = PacketUtil.readString(in);

        int playerCount = in.readInt();
        this.players = new HashSet<>();

        for (int i = 0; i < playerCount; i++) {
            int entityId = in.readInt();
            int playerNameLength = in.readInt();
            byte[] playerNameBytes = new byte[playerNameLength];
            in.readBytes(playerNameBytes);
            String playerName = new String(playerNameBytes);
            int x = in.readInt();
            int y = in.readInt();
            Player.SkinColor skinColor = Player.SkinColor.values()[in.readInt()];
            Position position = new Position(x, y);
            this.players.add(new PlayerData(entityId, playerName, position, skinColor));
        }
    }

    @Override
    public @NotNull PacketType getType() {
        return PacketType.Clientbound.WORLD_DATA;
    }

    public record PlayerData(
        int entityId,
        @NotNull String username,
        @NotNull Position position,
        @NotNull Player.SkinColor skinColor
    ) {}
}
