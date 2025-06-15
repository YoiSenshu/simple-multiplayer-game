package pl.yoisenshu.smg.client;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
import lombok.SneakyThrows;
import org.jetbrains.annotations.NotNull;

public class DisconnectedGameState extends GameState {

    private final SimpleMultiplayerGameClient client;
    private final String disconnectionMessage;
    private Stage stage;
    private TextButton returnToMenuButton;

    public DisconnectedGameState(@NotNull SimpleMultiplayerGameClient client, @NotNull String disconnectionMessage) {
        System.err.println("DisconnectedGameState initialized with message: " + disconnectionMessage);
        this.client = client;
        this.disconnectionMessage = disconnectionMessage;
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
    }

    private void initUI() {
        Skin skin = client.getAssetManager().get("ui/uiskin.json", Skin.class);

        Label disconnectionMessageLabel = new Label(disconnectionMessage, skin);
        disconnectionMessageLabel.setSize(stage.getWidth(), 50);
        disconnectionMessageLabel.setPosition(0, stage.getHeight() / 2 - disconnectionMessageLabel.getHeight() / 2);
        disconnectionMessageLabel.setAlignment(Align.center);
        stage.addActor(disconnectionMessageLabel);

        returnToMenuButton = new TextButton("Return to Menu", skin);
        returnToMenuButton.setSize(200, 50);
        returnToMenuButton.setPosition(stage.getWidth() / 2 - returnToMenuButton.getWidth() / 2, 150);
        returnToMenuButton.addListener(event -> {
            if (returnToMenuButton.isPressed()) {
                client.changeGameState(new MenuGameState(client));
                return true;
            }
            return false;
        });
        stage.addActor(returnToMenuButton);
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
}
