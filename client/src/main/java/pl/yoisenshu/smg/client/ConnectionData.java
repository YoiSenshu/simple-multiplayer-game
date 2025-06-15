package pl.yoisenshu.smg.client;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Range;
import pl.yoisenshu.smg.player.Player;

import java.util.Optional;
import java.util.Random;

record ConnectionData (
    @NotNull String username,
    @NotNull String host,
    @Range(from = 0, to = 10_000) int port,
    @NotNull Player.SkinColor skinColor
) {

    private static final String USERNAME_REGEX = "^[a-zA-Z0-9_]{1,16}$";
    private static final int DEFAULT_PORT = 2323;

    @NotNull
    static Optional<ConnectionData> fromRawData(@NotNull String rawUsername, @NotNull String rawServerIp) {
        String username = rawUsername.trim();
        if(!username.matches(USERNAME_REGEX)) {
            return Optional.empty();
        }
        String host = rawServerIp.trim().substring(0, rawServerIp.indexOf(':'));
        int port;
        if(!rawServerIp.contains(":")) {
            port = DEFAULT_PORT;
        } else {
            String portString = rawServerIp.substring(rawServerIp.indexOf(':') + 1);
            try {
                port = Integer.parseInt(portString);
            } catch (NumberFormatException e) {
                return Optional.empty();
            }
        }
        if(port < 0 || port > 10_000) {
            return Optional.empty();
        }

        Random random = new Random();
        Player.SkinColor skinColor = Player.SkinColor.values()[random.nextInt(Player.SkinColor.values().length)];

        return Optional.of(new ConnectionData(username, host, port, skinColor));
    }

    @NotNull
    public String getAddress() {
        return host + ":" + port;
    }
}
