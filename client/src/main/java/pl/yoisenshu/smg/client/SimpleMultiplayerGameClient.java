package pl.yoisenshu.smg.client;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.utils.ScreenUtils;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import pl.yoisenshu.smg.client.handler.BombExplodedHandler;
import pl.yoisenshu.smg.client.handler.BombPlacedHandler;
import pl.yoisenshu.smg.client.handler.ChatMessageHandler;
import pl.yoisenshu.smg.client.handler.EntityRemovedHandler;
import pl.yoisenshu.smg.client.handler.LoginSuccessHandler;
import pl.yoisenshu.smg.client.handler.PlayerJoinHandler;
import pl.yoisenshu.smg.client.handler.PlayerMoveHandler;
import pl.yoisenshu.smg.client.handler.PlayerLeftHandler;
import pl.yoisenshu.smg.client.handler.WorldDataHandler;
import pl.yoisenshu.smg.client.world.ClientWorld;
import pl.yoisenshu.smg.network.packet.PacketDecoder;
import pl.yoisenshu.smg.network.packet.PacketEncoder;
import pl.yoisenshu.smg.network.packet.util.ExceptionHandler;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.client.connection.LegacyConnectionManager;

import static pl.yoisenshu.smg.network.packet.Packet.MAX_PACKET_LENGTH;

public class SimpleMultiplayerGameClient extends ApplicationAdapter {

    // game states
    @Getter
    private GameState currentGameState;
    private GameState nextGameState;

    @Getter private AssetManager assetManager;
    @Getter private LegacyConnectionManager legacyConnectionManager;
    @Getter @Setter private ServerConnection connection;

    @Override
    public void create() {
        assetManager = new AssetManager();

        legacyConnectionManager = new LegacyConnectionManager(channel -> {
            var pip = channel.pipeline();
            pip.addLast(new LengthFieldBasedFrameDecoder(MAX_PACKET_LENGTH, 1, 4));
            pip.addLast(new PacketDecoder(PacketDecoder.PacketBound.CLIENTBOUND));
            pip.addLast(new PacketEncoder());
            pip.addLast(new LoginSuccessHandler(this));
            pip.addLast(new WorldDataHandler(this));
            pip.addLast(new ChatMessageHandler(this));
            pip.addLast(new PlayerJoinHandler(this));
            pip.addLast(new PlayerLeftHandler(this));
            pip.addLast(new PlayerMoveHandler(this));
            pip.addLast(new EntityRemovedHandler(this));
            pip.addLast(new BombPlacedHandler(this));
            pip.addLast(new BombExplodedHandler(this));

            pip.addLast(new ExceptionHandler("client"));
        });

        currentGameState = new MenuGameState(this);
        currentGameState.init();
    }

    @Override
    public void render() {
        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        float deltaTime = Gdx.graphics.getDeltaTime();
        if(nextGameState != null) {
            currentGameState.dispose();
            currentGameState = nextGameState;
            nextGameState = null;
            currentGameState.init();
        }
        currentGameState.update(deltaTime);
        currentGameState.render(deltaTime);
    }

    @Override
    public void resize (int width, int height) {
        if(currentGameState != null) {
            currentGameState.resize(width, height);
        }
    }

    @Override
    public void pause () {
        if(currentGameState != null) {
            currentGameState.pause();
        }
    }

    @Override
    public void resume () {
        if(currentGameState != null) {
            currentGameState.resume();
        }
    }

    @Override
    public void dispose() {
        if(currentGameState != null) {
            currentGameState.dispose();
        }
        assetManager.dispose();
        legacyConnectionManager.shutdown();
    }

    public void changeGameState(@NotNull GameState newState) {
        nextGameState = newState;
    }

    @Nullable
    public ClientWorld getCurrentWorld() {
        if (currentGameState instanceof PlayingGameState playingGameState) {
            return playingGameState.getRemoteWorld();
        }
        return null;
    }
}
