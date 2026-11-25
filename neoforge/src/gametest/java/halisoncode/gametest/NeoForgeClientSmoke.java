package halisoncode.gametest;

import halisoncode.HalisonCode;
import halisoncode.client.HalisonCodeClient;
import halisoncode.relay.RelayServer;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import net.minecraft.client.Camera;
import net.minecraft.client.MouseHandler;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
//? if >=26.1 {
import net.neoforged.neoforge.client.event.lifecycle.ClientStartedEvent;
//?} else {
/*import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.client.event.ClientTickEvent;
*///?}

/**
 * Client smoke check for NeoForge (issue #187), run by ./gradlew :neoforge:runClientSmoke (system property
 * halisoncode.clientSmoke). NeoForge has no client GameTest API like Fabric's, so this waits for the client to start,
 * checks that the camera mixins were merged and that the relay serves the editor from the mod jar, logs the result and
 * quits; a failure exits with status 1 so the Gradle task fails. Never logs the editor URL: it holds the token.
 */
@EventBusSubscriber(modid = HalisonCode.MOD_ID, value = Dist.CLIENT)
public final class NeoForgeClientSmoke {
    private NeoForgeClientSmoke() {
    }

    //? if >=26.1 {
    // Lowest priority: after HalisonCodeNeoForgeClient has started the relay for the same event.
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void started(ClientStartedEvent event) {
        if (!Boolean.getBoolean("halisoncode.clientSmoke")) return;
        var client = event.getClient();
    //?} else {
    /*private static boolean done;

    // NeoForge 21.1 has no client started event: HalisonCodeNeoForgeClient starts the relay on the first tick, and this
    // waits for the resource load to end (quitting during it trips vanilla's TextureManager.close).
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void started(ClientTickEvent.Post event) {
        Minecraft client = Minecraft.getInstance();
        if (done || !Boolean.getBoolean("halisoncode.clientSmoke") || client.getOverlay() != null) return;
        done = true;
    *///?}
        List<String> failures = new ArrayList<>();
        if (!merged(Camera.class, "halisoncode$robotCamera")) failures.add("CameraMixin not applied");
        if (!merged(MouseHandler.class, "halisoncode$orbit") || !merged(MouseHandler.class, "halisoncode$zoom")) failures.add("MouseHandlerMixin not applied");
        //? if >=26.1 {
        if (!merged(net.minecraft.client.Minecraft.class, "halisoncode$keepRunning")) failures.add("PauseOnLostFocusMixin not applied");
        //?} else
        /*if (!merged(net.minecraft.client.renderer.GameRenderer.class, "halisoncode$keepRunning")) failures.add("PauseOnLostFocusMixin not applied");*/
        //? if <1.21.2
        /*if (!merged(net.minecraft.client.renderer.GameRenderer.class, "halisoncode$noHandOnRobot")) failures.add("GameRendererMixin not applied");*/
        RelayServer relay = HalisonCodeClient.relay();
        if (relay == null) {
            failures.add("relay not started");
        } else {
            try (HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build()) {
                HttpResponse<String> index = http.send(HttpRequest.newBuilder(URI.create("http://127.0.0.1:" + relay.port() + "/")).build(),
                        HttpResponse.BodyHandlers.ofString());
                if (index.statusCode() != 200 || !index.body().contains("<html")) failures.add("editor index: HTTP " + index.statusCode());
            } catch (Exception e) {
                failures.add("editor index: " + e);
            }
        }
        if (failures.isEmpty()) {
            HalisonCode.LOG.info("Halison Code client smoke: OK (mixins merged, relay on port {} serves the editor)", relay.port());
            client.stop();
        } else {
            HalisonCode.LOG.error("Halison Code client smoke: FAILED {}", failures);
            Runtime.getRuntime().halt(1);
        }
    }

    // Mixin merges a handler into the target under a name that keeps the handler's own name.
    private static boolean merged(Class<?> target, String handler) {
        return Arrays.stream(target.getDeclaredMethods()).map(Method::getName).anyMatch(name -> name.contains(handler));
    }
}
