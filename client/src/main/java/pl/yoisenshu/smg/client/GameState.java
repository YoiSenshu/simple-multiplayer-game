package pl.yoisenshu.smg.client;

public abstract class GameState {

    public void init() {}

    public void update(float deltaTime) {}

    public void render(float deltaTime) {}

    public void dispose() {}
}
