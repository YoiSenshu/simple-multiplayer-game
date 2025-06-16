package pl.yoisenshu.smg.client;

public abstract class GameState {

    public void init() {}

    public void update(float deltaTime) {}

    public void render(float deltaTime) {}

    public void pause() {}

    public void resume() {}

    public void resize(int width, int height) {}

    public void dispose() {}
}
