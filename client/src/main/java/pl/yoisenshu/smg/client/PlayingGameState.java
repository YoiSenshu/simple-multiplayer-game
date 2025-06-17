package pl.yoisenshu.smg.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import pl.yoisenshu.smg.client.player.ControllablePlayer;
import pl.yoisenshu.smg.client.player.ClientPlayerData;
import pl.yoisenshu.smg.client.world.ClientWorld;
import pl.yoisenshu.smg.client.world.WorldData;
import pl.yoisenshu.smg.client.world.WorldRenderer;
import pl.yoisenshu.smg.player.PlayerView;
import pl.yoisenshu.smg.world.Position;

import java.time.Instant;
import java.util.Map;
import java.util.stream.Collectors;

class PlayingGameState extends GameState {

    private static final int WALK_SPEED = 2;

    private final SimpleMultiplayerGameClient client;
    private Stage stage;

    // UI elements
    private Label debugLabel;
    private Group pauseGroup;
    private Group chatGroup;
    private TextArea chatInput;
    private Label chatHistoryLabel;

    private SpriteBatch spriteBatch;
    private WorldRenderer worldRenderer;

    private boolean paused = false;
    private boolean chatting = false;

    private final WorldData initialWorldData;
    private final ClientPlayerData initialPlayerData;

    @Getter private ClientWorld remoteWorld;

    PlayingGameState(
        @NotNull SimpleMultiplayerGameClient client,
        @NotNull WorldData worldData,
        @NotNull ClientPlayerData playerData
        ) {
        this.client = client;
        this.initialWorldData = worldData;
        this.initialPlayerData = playerData;
    }

    @Override
    public void init() {
        stage = new Stage(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        spriteBatch = new SpriteBatch();

        client.getAssetManager().load("ui/uiskin.json", Skin.class);
        client.getAssetManager().load("texture/grass.png", Texture.class);
        client.getAssetManager().load("texture/player_red.png", Texture.class);
        client.getAssetManager().load("texture/player_orange.png", Texture.class);
        client.getAssetManager().load("texture/player_green.png", Texture.class);
        client.getAssetManager().load("texture/player_yellow.png", Texture.class);
        client.getAssetManager().load("texture/bomb.png", Texture.class);
        client.getAssetManager().finishLoading();

        Skin skin = client.getAssetManager().get("ui/uiskin.json", Skin.class);

        initUI();
        Gdx.input.setInputProcessor(stage);

        remoteWorld = new ClientWorld(
            client,
            client.getConnection(),
            initialWorldData,
            initialPlayerData
        );
        worldRenderer = new WorldRenderer(
            client.getAssetManager().get("texture/grass.png", Texture.class),
            Map.of(
                PlayerView.SkinColor.RED, client.getAssetManager().get("texture/player_red.png", Texture.class),
                PlayerView.SkinColor.ORANGE, client.getAssetManager().get("texture/player_orange.png", Texture.class),
                PlayerView.SkinColor.YELLOW, client.getAssetManager().get("texture/player_yellow.png", Texture.class),
                PlayerView.SkinColor.GREEN, client.getAssetManager().get("texture/player_green.png", Texture.class)
            ),
            skin.getFont("font"),
            client.getAssetManager().get("texture/bomb.png", Texture.class)
        );
    }

    private void initUI() {
        Skin skin = client.getAssetManager().get("ui/uiskin.json", Skin.class);
        debugLabel = new Label("", skin, "default", Color.BLACK);
        debugLabel.setPosition(10, Gdx.graphics.getHeight() - 10);
        debugLabel.setAlignment(Align.topLeft);
        stage.addActor(debugLabel);

        // pause
        pauseGroup = new Group();
        pauseGroup.setSize(300, 300);
        pauseGroup.setPosition(stage.getWidth() / 2f - pauseGroup.getWidth() / 2f,             stage.getHeight() / 2f - pauseGroup.getHeight() / 2f);
        stage.addActor(pauseGroup);
        pauseGroup.setVisible(false);

        Label pauseLabel = new Label("Game Paused", skin, "default", Color.BLACK);
        pauseLabel.setFontScale(3);
        pauseLabel.setSize(pauseGroup.getWidth(), 50);
        pauseLabel.setPosition(0, pauseGroup.getHeight() - pauseLabel.getHeight());
        pauseLabel.setAlignment(Align.center);
        pauseGroup.addActor(pauseLabel);

        TextButton resumeButton = new TextButton("Resume", skin);
        resumeButton.setSize(pauseGroup.getWidth(), 50);
        resumeButton.setPosition(0, pauseGroup.getHeight() - pauseLabel.getHeight() - resumeButton.getHeight());
        resumeButton.addListener(event -> {
            if(resumeButton.isPressed() && paused) {
                paused = false;
                return true;
            }
            return false;
        });
        pauseGroup.addActor(resumeButton);

        TextButton disconnectButton = new TextButton("Disconnect", skin);
        disconnectButton.setSize(pauseGroup.getWidth(), 50);
        disconnectButton.setPosition(0, pauseGroup.getHeight() - pauseLabel.getHeight() - resumeButton.getHeight() - disconnectButton.getHeight() - 20);
        disconnectButton.addListener(event -> {
            if(disconnectButton.isPressed() && paused) {
                remoteWorld.getControllablePlayer().disconnect("Disconnect button pressed.");
                return true;
            }
            return false;
        });
        pauseGroup.addActor(disconnectButton);


        // chat
        chatGroup = new Group();
        chatGroup.setSize(300, 300);
        chatGroup.setPosition(0, 0);
        chatGroup.setVisible(false);
        stage.addActor(chatGroup);

        chatHistoryLabel = new Label("", skin, "default", Color.BLACK);
        chatHistoryLabel.setSize(chatGroup.getWidth(), chatGroup.getHeight() - 50);
        chatHistoryLabel.setPosition(0, 50);
        chatHistoryLabel.setAlignment(Align.bottomLeft);
        chatHistoryLabel.setWrap(true);
        chatGroup.addActor(chatHistoryLabel);

        chatInput = new TextArea("", skin);
        chatInput.setSize(chatGroup.getWidth(), 25);
        chatInput.setPosition(0, 0);
        chatInput.setMessageText("Type your message here...");
        chatInput.setOnlyFontChars(true);
        stage.setKeyboardFocus(chatInput);
        chatInput.setTextFieldListener((textField, c) -> {
            if(c == '\n' || c == '\r') {
                String message = textField.getText().trim();
                if(!message.isEmpty()) {
                    remoteWorld.getControllablePlayer().sendMessage(message);
                    textField.setText("");
                }
            }
        });
        chatInput.setMaxLength(100);
        chatGroup.setBounds(10, 10, 10, 10);
        chatGroup.setColor(Color.WHITE);
        chatGroup.addActor(chatInput);
    }

    @Override
    public void update(float deltaTime) {

        // disconnected?
        var connection = client.getConnection();
        if(connection == null || !connection.isActive()) {
            System.out.println("Connection active: False");
            client.changeGameState(new DisconnectedGameState(client, "Disconnected."));
            return;
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
            if(chatting) {
                chatting = false;
            } else {
                paused = !paused;
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.T) || Gdx.input.isKeyJustPressed(Input.Keys.SLASH)) {
            if(!paused && !chatting) {
                chatting = true;
                chatInput.setText("");
            }
        }

        pauseGroup.setVisible(paused);

        if(chatting) {
            chatGroup.setVisible(true);
            var messages = remoteWorld.getChatHistory()
                .keySet()
                .stream()
                .sorted(Instant::compareTo)
                .map(instant -> remoteWorld.getChatHistory().get(instant))
                .collect(Collectors.joining("\n"));
            chatHistoryLabel.setText(messages);
            chatInput.setVisible(true);
            stage.setKeyboardFocus(chatInput);
        } else {
            Instant deadline = Instant.now().minusSeconds(5);
            Instant lastMessageTime = remoteWorld.getChatHistory()
                .keySet()
                .stream()
                .max(Instant::compareTo)
                .orElse(null);

            if(lastMessageTime != null && lastMessageTime.isAfter(deadline)) {

                var lastMessages = remoteWorld.getChatHistory().entrySet()
                    .stream()
                    .filter(entry -> entry.getKey().isAfter(deadline))
                    .sorted(Map.Entry.comparingByKey())
                    .map(Map.Entry::getValue)
                    .collect(Collectors.joining("\n"));

                chatHistoryLabel.setText(lastMessages);
                chatGroup.setVisible(true);
                chatInput.setVisible(false);
            } else {
                chatGroup.setVisible(false);
            }
        }

        ControllablePlayer player = remoteWorld.getControllablePlayer();
        Position position = player.getPosition();

        // movement
        if(!paused && !chatting) {
            if(Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
                position = new Position(position.x(), position.y() + WALK_SPEED);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
                position = new Position(position.x(), position.y() - WALK_SPEED);
            }
            if(Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
                position = new Position(position.x() - WALK_SPEED, position.y());
            }
            if(Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
                position = new Position(position.x() + WALK_SPEED, position.y());
            }
            if(position.x() != player.getPosition().x() || position.y() != player.getPosition().y()) {
                player.move(position);
            }
        }

        if(Gdx.input.isKeyJustPressed(Input.Keys.B) && !paused && !chatting) {
            player.placeBomb();
        }

        stage.act(deltaTime);
        debugLabel.setText(getDebugText());
    }

    @Override
    public void render(float deltaTime) {
        spriteBatch.begin();
        worldRenderer.draw(spriteBatch, remoteWorld, deltaTime);
        spriteBatch.end();
        stage.draw();
    }

    @Override
    public void dispose() {
        stage.dispose();
    }

    private String getDebugText() {
        return "FPS: " + Gdx.graphics.getFramesPerSecond() + "\n" +
               "Paused: " + paused + "\n" +
                "Chatting: " + chatting;
    }
}
