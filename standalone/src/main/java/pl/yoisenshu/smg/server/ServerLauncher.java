package pl.yoisenshu.smg.server;

/** Launches the server application. */
public class ServerLauncher {
    public static void main(String[] args) throws InterruptedException {
        SimpleMultiplayerGameServer server = new SimpleMultiplayerGameServer();
        server.start();
    }
}
