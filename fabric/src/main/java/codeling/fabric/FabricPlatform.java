package codeling.fabric;

import codeling.Permission;
import codeling.platform.Platform;
import com.mojang.authlib.GameProfile;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.fabricmc.fabric.api.entity.FakePlayer;
//? if >=1.21.11 {
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
//?} else {
/*import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
*///?}
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
//? if >=1.21.11 {
import net.minecraft.world.level.gamerules.GameRule;
import net.minecraft.world.level.gamerules.GameRuleCategory;
//?} else {
/*import net.minecraft.world.level.GameRules;
*///?}

/** Fabric's registries are open while mods initialize, so everything is registered at once. */
public final class FabricPlatform implements Platform {
    @Override
    public <T> Supplier<T> register(Registry<? super T> registry, Identifier id, Supplier<T> value) {
        T registered = Registry.register(registry, id, value.get());
        return () -> registered;
    }

    //? if >=1.21.11 {
    @Override
    public Supplier<GameRule<Boolean>> booleanRule(Identifier id, GameRuleCategory category, boolean defaultValue) {
        GameRule<Boolean> rule = GameRuleBuilder.forBoolean(defaultValue).category(category).buildAndRegister(id);
        return () -> rule;
    }

    @Override
    public Supplier<GameRule<Integer>> intRule(Identifier id, GameRuleCategory category, int defaultValue, int min, int max) {
        GameRule<Integer> rule = GameRuleBuilder.forInteger(defaultValue).range(min, max).category(category).buildAndRegister(id);
        return () -> rule;
    }

    @Override
    public <E extends Enum<E>> Supplier<GameRule<E>> enumRule(Identifier id, GameRuleCategory category, E defaultValue) {
        GameRule<E> rule = GameRuleBuilder.forEnum(defaultValue).category(category).buildAndRegister(id);
        return () -> rule;
    }
    //?} else {
    /*@Override
    public Supplier<GameRules.Key<GameRules.BooleanValue>> booleanRule(Identifier id, GameRules.Category category, boolean defaultValue) {
        GameRules.Key<GameRules.BooleanValue> key = GameRuleRegistry.register(id.toString(), category, GameRuleFactory.createBooleanRule(defaultValue));
        return () -> key;
    }

    @Override
    public Supplier<GameRules.Key<GameRules.IntegerValue>> intRule(Identifier id, GameRules.Category category, int defaultValue, int min, int max) {
        GameRules.Key<GameRules.IntegerValue> key = GameRuleRegistry.register(id.toString(), category, GameRuleFactory.createIntRule(defaultValue, min, max));
        return () -> key;
    }

    @Override
    public <E extends Enum<E>> Supplier<? extends GameRules.Key<?>> enumRule(Identifier id, GameRules.Category category, E defaultValue) {
        var key = GameRuleRegistry.register(id.toString(), category, GameRuleFactory.createEnumRule(defaultValue));
        return () -> key;
    }
    *///?}

    @Override
    public Path configDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public Map<String, String> versions() {
        Map<String, String> versions = new LinkedHashMap<>();
        Map.of("codeling", "Halison Code", "minecraft", "Minecraft", "fabricloader", "Fabric Loader", "fabric-api", "Fabric API").entrySet().stream().sorted(Map.Entry.comparingByValue())
                .forEach(e -> FabricLoader.getInstance().getModContainer(e.getKey())
                        .ifPresent(mod -> versions.put(e.getValue(), mod.getMetadata().getVersion().getFriendlyString())));
        return versions;
    }

    @Override
    public ServerPlayer fakePlayer(ServerLevel level, GameProfile profile) {
        return FakePlayer.get(level, profile);
    }

    @Override
    public boolean isFakePlayer(Player player) {
        return player instanceof FakePlayer;
    }

    @Override
    public boolean canSend(ServerPlayer player, CustomPacketPayload.Type<?> type) {
        return ServerPlayNetworking.canSend(player, type);
    }

    @Override
    public void send(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }

    // Fabric API has permissions from 26.1.2 on. Before that most mods use fabric-permissions-api, which permission mods
    // carry with them, so Codeling does not need to. Both are optional: an older Fabric API does not have the module.
    //? if >=26.1 {
    private static final boolean PERMISSIONS = FabricLoader.getInstance().isModLoaded("fabric-permission-api-v1");
    //?} else
    /*private static final boolean PERMISSIONS = FabricLoader.getInstance().isModLoaded("fabric-permissions-api-v0");*/

    @Override
    public boolean allows(ServerPlayer player, Permission permission) {
        boolean byDefault = permission.byDefault(player);
        return PERMISSIONS ? FabricPermissions.check(player, permission, byDefault) : byDefault;
    }

    /** A class of its own, so nothing of the optional API is loaded when it is not there. */
    private static final class FabricPermissions {
        // fabric-permissions-api gets the command source, not the player: it makes the source of a player with a method
        // that changed in 1.21.2, so the wrong build of it would fail.
        static boolean check(ServerPlayer player, Permission permission, boolean byDefault) {
            //? if >=26.1 {
            return ((net.fabricmc.fabric.api.permission.v1.PermissionContextOwner) player)
                    .checkPermission(Identifier.fromNamespaceAndPath(codeling.Codeling.MOD_ID, permission.path()), byDefault);
            //?} else
            /*return me.lucko.fabric.api.permissions.v0.Permissions.check(player.createCommandSourceStack(), permission.node(), byDefault);*/
        }
    }
}
