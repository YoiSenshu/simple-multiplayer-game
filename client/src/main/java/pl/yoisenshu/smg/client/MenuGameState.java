package pl.yoisenshu.smg.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Button;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextArea;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import org.jetbrains.annotations.NotNull;

class MenuGameState extends GameState {

    private final SimpleMultiplayerGameClient client;
    private Stage stage;
    private Group controlsGroup;

    MenuGameState(@NotNull SimpleMultiplayerGameClient client) {
        this.client = client;
    }

    @Override
    public void init() {
        stage = new Stage(new ScreenViewport());
        stage.getViewport().update(Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), true);
        client.getAssetManager().load("ui/uiskin.json", Skin.class);
        client.getAssetManager().finishLoading();
        initUI();
        Gdx.input.setInputProcessor(stage);
    }

    private void initUI() {
        var skin = client.getAssetManager().get("ui/uiskin.json", Skin.class);

        controlsGroup = new Group();
        controlsGroup.setPosition(0, 0);
        controlsGroup.setSize(stage.getWidth(), stage.getHeight());
        stage.addActor(controlsGroup);

        Label titleLabel = new Label("Simple Multiplayer Game", skin, "default", Color.WHITE);
        titleLabel.setFontScale(3f);
        titleLabel.setSize(stage.getWidth(), 100);
        titleLabel.setAlignment(Align.center);
        titleLabel.setPosition(0, stage.getHeight() - 100);
        controlsGroup.addActor(titleLabel);

        Label usernameLabel = new Label("Username:", skin, "default", Color.WHITE);
        usernameLabel.setFontScale(1f);
        usernameLabel.setSize(190, 20);
        usernameLabel.setAlignment(Align.left);
        usernameLabel.setPosition(stage.getWidth() / 2 - (usernameLabel.getWidth() / 2), stage.getHeight() - 120);
        controlsGroup.addActor(usernameLabel);

        TextArea usernameTextArea = new TextArea("Tester" + ((int) (Math.random() * 1000D)), skin, "default");
        usernameTextArea.setSize(200, 30);
        usernameTextArea.setPosition(stage.getWidth() / 2 - (usernameTextArea.getWidth() / 2), stage.getHeight() - 150);
        usernameTextArea.setMaxLength(16);
        usernameTextArea.setBlinkTime(0.4f);
        controlsGroup.addActor(usernameTextArea);

        Label serverIpLabel = new Label("Server IP:", skin, "default", Color.WHITE);
        serverIpLabel.setFontScale(1f);
        serverIpLabel.setSize(190, 20);
        serverIpLabel.setAlignment(Align.left);
        serverIpLabel.setPosition(stage.getWidth() / 2 - (serverIpLabel.getWidth() / 2), stage.getHeight() - 180);
        controlsGroup.addActor(serverIpLabel);

        TextArea serverIpTextArea = new TextArea("localhost:2323", skin, "default");
        serverIpTextArea.setSize(200, 30);
        serverIpTextArea.setAlignment(Align.center);
        serverIpTextArea.setPosition(stage.getWidth() / 2 - (serverIpTextArea.getWidth() / 2), stage.getHeight() - 210);
        usernameTextArea.setMaxLength(40);
        usernameTextArea.setBlinkTime(0.4f);
        controlsGroup.addActor(serverIpTextArea);

        Button connectButton = new Button(skin, "default");
        connectButton.setSize(200, 30);
        connectButton.setPosition(stage.getWidth() / 2 - (connectButton.getWidth() / 2), stage.getHeight() - 260);
        connectButton.setColor(Color.MAGENTA);
        controlsGroup.addActor(connectButton);

        connectButton.addListener(event -> {
            if(connectButton.isPressed()) {
                String username = usernameTextArea.getText();
                String serverIp = serverIpTextArea.getText();
                var connectionData = ConnectionData.fromRawData(username, serverIp);

                if(connectionData.isEmpty()) {
                    return false;
                }

                client.changeGameState(new ConnectingGameState(client, connectionData.get()));
                return true;
            }
            return false;
        });

        Label connectButtonLabel = new Label("Connect", skin, "default", Color.WHITE);
        connectButtonLabel.setSize(connectButton.getWidth(), connectButton.getHeight());
        connectButtonLabel.setAlignment(Align.center);
        connectButton.add(connectButtonLabel);
    }

    @Override
    public void update(float deltaTime) {
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

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
        controlsGroup.setPosition(0, 0);
        controlsGroup.setSize(stage.getWidth(), stage.getHeight());
    }
}
