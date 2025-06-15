package pl.yoisenshu.smg.player;

import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.entity.Entity;

public interface Player extends Entity {

    @NotNull String getUsername();

    @NotNull SkinColor getSkinColor();

    @Getter
    enum SkinColor {
        GREEN("player_green.png"),
        ORANGE("player_orange.png"),
        RED("player_red.png"),
        YELLOW("player_yellow.png"),;
        private final String texture;

        SkinColor(String texture) {
            this.texture = texture;
        }
    }
}
