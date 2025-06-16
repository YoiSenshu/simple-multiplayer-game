package pl.yoisenshu.smg.client.world;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.entity.ClientBomb;
import pl.yoisenshu.smg.client.entity.ClientEntity;
import pl.yoisenshu.smg.client.player.ControllablePlayer;
import pl.yoisenshu.smg.client.player.ClientPlayer;
import pl.yoisenshu.smg.player.PlayerView;

import java.time.Instant;
import java.util.Map;

public class WorldRenderer {

    private final Texture grassTexture;
    private final Map<PlayerView.SkinColor, Texture> playerTextures;
    private final BitmapFont font;
    private final Texture bombTexture;

    public WorldRenderer(
        @NonNull Texture grassTexture,
        Map<PlayerView.SkinColor, Texture> playerTextures,
        @NotNull BitmapFont font,
        @NotNull Texture bombTexture
    ) {
        this.grassTexture = grassTexture;
        this.playerTextures = playerTextures;
        this.font = font;
        this.bombTexture = bombTexture;
    }

    public void draw(@NotNull SpriteBatch spriteBatch, ClientWorld world, float deltaTime) {
        spriteBatch.draw(grassTexture, 0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());

        ControllablePlayer player = world.getControllablePlayer();

        for (ClientPlayer clientPlayer : world.getPlayers()) {
            if(clientPlayer.getId() == player.getId()) {
                continue;
            }
            spriteBatch.draw(playerTextures.get(clientPlayer.getSkinColor()), clientPlayer.getPosition().x(), clientPlayer.getPosition().y(), 50, 50);
        }

        spriteBatch.draw(playerTextures.get(player.getSkinColor()), player.getPosition().x(), player.getPosition().y(), 50, 50);

        font.setColor(Color.BLACK);

        for (ClientPlayer clientPlayer : world.getPlayers()) {
            font.draw(
                spriteBatch,
                clientPlayer.getUsername() + "[" + clientPlayer.getId() + "]",
                clientPlayer.getPosition().x(),
                clientPlayer.getPosition().y() + 70,
                50,
                Align.center,
                false
            );
        }

        font.setColor(Color.RED);

        for (ClientEntity entity : world.getEntities()) {
            if(entity instanceof ClientBomb bomb) {
                spriteBatch.draw(bombTexture, bomb.getPosition().x(), bomb.getPosition().y(), 30, 30);

                double timeLeft = bomb.getExplosionTime().toEpochMilli() - Instant.now().toEpochMilli();
                timeLeft /= 1000.0;
                timeLeft = Math.round(timeLeft * 100D) / 100D;

                font.draw(
                    spriteBatch,
                    String.format("%.2fs [%d]", timeLeft, bomb.getId()),
                    bomb.getPosition().x(),
                    bomb.getPosition().y() + 40,
                    30,
                    Align.center,
                    false
                );
            }
        }
    }
}
