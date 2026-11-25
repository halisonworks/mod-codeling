package halisoncode.gametest;

import halisoncode.client.HalisonCodeClient;
import halisoncode.relay.RelayServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import net.fabricmc.fabric.api.client.gametest.v1.FabricClientGameTest;
import net.fabricmc.fabric.api.client.gametest.v1.context.ClientGameTestContext;
import net.fabricmc.fabric.api.client.gametest.v1.context.TestSingleplayerContext;

/**
 * Issue #8: another program has the editor's port. /halisoncode says so, takes no other port (projects are stored per
 * address), and starts the editor server when the port is free again.
 */
public class EditorPortClientGameTest implements FabricClientGameTest {
    @Override
    public void runTest(ClientGameTestContext context) {
        int port = HalisonCodeClient.relay().port();
        try (TestSingleplayerContext world = context.worldBuilder().create()) {
            world.getConnection().waitForChunksRender();
            context.runOnClient(mc -> HalisonCodeClient.stopping());
            try (ServerSocket other = new ServerSocket()) {
                other.bind(new InetSocketAddress("127.0.0.1", port));
                context.runOnClient(mc -> mc.getConnection().sendCommand("halisoncode"));
                context.waitTicks(5);
                if (context.computeOnClient(mc -> HalisonCodeClient.relay()) != null) throw new AssertionError("the editor server took another port");
                context.takeScreenshot("editor-port-busy");
            } catch (IOException e) {
                throw new AssertionError("port " + port + " was not free after the editor server closed", e);
            }
            // Not with /halisoncode: that opens a browser.
            RelayServer relay = context.computeOnClient(mc -> HalisonCodeClient.start(mc) ? HalisonCodeClient.relay() : null);
            if (relay == null || relay.port() != port) throw new AssertionError("the editor server did not start again on port " + port);
        }
    }
}
