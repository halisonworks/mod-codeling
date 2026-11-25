package halisoncode.gametest;

import halisoncode.Permission;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Stands in for a permission mod (issue #51): answers what a test has set, and nothing about anybody else. */
final class PermissionOverrides {
    private static final Map<UUID, Map<String, Boolean>> SET = new ConcurrentHashMap<>();

    static {
        //? if >=26.1 {
        net.fabricmc.fabric.api.permission.v1.PermissionEvents.ON_REQUEST.register(new net.fabricmc.fabric.api.permission.v1.PermissionEvents.OnRequest() {
            @Override
            public <T> T handlePermissionRequest(net.fabricmc.fabric.api.permission.v1.PermissionContext context, net.fabricmc.fabric.api.permission.v1.PermissionNode<T> node) {
                // As LuckPerms names them: halisoncode:robot is halisoncode.robot.
                return node.cast(SET.getOrDefault(context.uuid(), Map.of()).get(node.key().getNamespace() + "." + node.key().getPath()));
            }
        });
        //?} else {
        /*me.lucko.fabric.api.permissions.v0.PermissionCheckEvent.EVENT.register((source, name) -> {
            var entity = source instanceof net.minecraft.commands.CommandSourceStack s ? s.getEntity() : null;
            Boolean value = entity == null ? null : SET.getOrDefault(entity.getUUID(), Map.of()).get(name);
            return net.fabricmc.fabric.api.util.TriState.of(value);
        });
        *///?}
    }

    private PermissionOverrides() {
    }

    static void set(UUID player, Permission permission, boolean value) {
        SET.computeIfAbsent(player, p -> new ConcurrentHashMap<>()).put(permission.node(), value);
    }

    static void clear(UUID player) {
        SET.remove(player);
    }
}
