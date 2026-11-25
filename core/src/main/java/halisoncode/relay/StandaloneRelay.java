package halisoncode.relay;

import halisoncode.log.Log;
import java.nio.file.Path;

/**
 * Runs the editor + relay server without Minecraft, for headless tests and target development.
 * Port: first argument or system property halisoncode.port (default {@value RelayServer#DEFAULT_PORT}).
 * With --print-url it prints the editor URL including this run's token to stdout, the stand-in for
 * the in-game "open editor" action. The token dies with the process. With --print-launcher it writes the launcher
 * page that /halisoncode opens (issue #191) and prints its path, so the editor suite can open it as the game does. With
 * --log extended logging is on (issue #279).
 */
public final class StandaloneRelay {
    public static void main(String[] args) throws Exception {
        int port = Integer.getInteger("halisoncode.port", RelayServer.DEFAULT_PORT);
        boolean printUrl = false;
        boolean printLauncher = false;
        for (String a : args) {
            if (a.equals("--print-url")) printUrl = true;
            else if (a.equals("--print-launcher")) printLauncher = true;
            else if (a.equals("--log")) Log.extendedOn(Log.MAX_MINUTES);
            else port = Integer.parseInt(a);
        }
        RelayServer server = new RelayServer(port).start();
        Runtime.getRuntime().addShutdownHook(new Thread(server::close));
        System.out.println("Halison Code relay on http://127.0.0.1:" + port + "/");
        if (printUrl) System.out.println("EDITOR_URL " + server.editorUrl());
        if (printLauncher) {
            Path launcher = EditorLauncher.launch(EditorLauncher.DIR, server.editorUrl(), path -> { });
            Runtime.getRuntime().addShutdownHook(new Thread(() -> EditorLauncher.delete(launcher)));
            System.out.println("EDITOR_LAUNCHER " + launcher);
        }
        Thread.currentThread().join();
    }
}
