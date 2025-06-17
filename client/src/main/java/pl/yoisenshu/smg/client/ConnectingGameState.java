package pl.yoisenshu.smg.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.connection.ServerConnection;
import pl.yoisenshu.smg.client.player.ClientPlayerData;
import pl.yoisenshu.smg.client.world.WorldData;
import pl.yoisenshu.smg.network.packet.client.ClientLoginPacket;
import pl.yoisenshu.smg.network.packet.server.ServerWorldDataPacket;
import pl.yoisenshu.smg.world.Position;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

@Slf4j
public class ConnectingGameState extends GameState {

    private final SimpleMultiplayerGameClient client;
    private final ConnectionData connectionData;
    private Stage stage;
    private Label connectionProgressLabel;
    private TextButton cancelButton; // TODO zrobić
    private boolean connectAttempted = false;

    // after login success
    private int playerEntityId;
    private Position playerPosition;


    ConnectingGameState(@NotNull SimpleMultiplayerGameClient client, @NotNull ConnectionData connectionData) {
        this.client = client;
        this.connectionData = connectionData;
    }

    @SneakyThrows
    @Override
    public void init() {
        stage = new Stage(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);

        client.getAssetManager().load("ui/uiskin.json", Skin.class);
        client.getAssetManager().finishLoading();
        initUI();
        Gdx.input.setInputProcessor(stage);

        CompletableFuture
            .supplyAsync(() -> client.getLegacyConnectionManager().connect(connectionData.host(), connectionData.port()))
            .thenCompose(future -> future)
            .thenAccept(connection -> {
                connectAttempted = true;
                client.setConnection(connection);

                connectionProgressLabel.setText("Logging in...");
                connection.sendPacket(new ClientLoginPacket(connectionData.username(), connectionData.skinColor()));
            })
            .exceptionally(throwable -> {
                System.out.println("Connection active: " + client.getConnection().isActive());
                throwable.printStackTrace(System.err);
                String errorMessage = "Connection failed: " + throwable.getMessage();
                connectAttempted = true;
                client.changeGameState(new DisconnectedGameState(client, errorMessage));
                return null;
            });
    }

    private void initUI() {
        Skin skin = client.getAssetManager().get("ui/uiskin.json", Skin.class);

        connectionProgressLabel = new Label("Connecting to server...", skin);
        connectionProgressLabel.setSize(stage.getWidth(), 50);
        connectionProgressLabel.setPosition(0, stage.getHeight() / 2 - connectionProgressLabel.getHeight() / 2);
        connectionProgressLabel.setAlignment(Align.center);
        stage.addActor(connectionProgressLabel);

        cancelButton = new TextButton("Cancel", skin);
        cancelButton.setSize(200, 50);
        cancelButton.setPosition(stage.getWidth() / 2 - cancelButton.getWidth() / 2, 150);
        cancelButton.addListener(event -> {
            if (cancelButton.isPressed()) {
                if(client.getConnection() != null) {
                    client.getConnection().close();
                    client.setConnection(null);
                }
                client.changeGameState(new MenuGameState(client));
                return true;
            }
            return false;
        });
        stage.addActor(cancelButton);
    }

    @Override
    public void update(float deltaTime) {
        ServerConnection connection = client.getConnection();
        if (connection != null && !connection.isActive()) {
            System.out.println("Connection active: False");
            client.changeGameState(new DisconnectedGameState(client, "Disconnected from server"));
        }
        stage.act(deltaTime);
    }

    @Override
    public void render(float deltaTime) {
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    public void notifyLogged(int playerEntityId, Position position) {
        this.playerEntityId = playerEntityId;
        this.playerPosition = position;
        connectionProgressLabel.setText("Downloading world data...");

        log.debug("Logged in with entity ID: {} at position: {}", playerEntityId, position);
    }

    public void notifyWorldDownloaded(String worldName, Set<ServerWorldDataPacket.PlayerData> players) {
        var worldData = new WorldData(worldName, players, playerEntityId, playerPosition);
        log.debug("World downloaded: {}, players: {}", worldName, players.size());
        client.changeGameState(new PlayingGameState(client, worldData, new ClientPlayerData(connectionData.username(), connectionData.skinColor(), playerEntityId, playerPosition)));
    }
}
