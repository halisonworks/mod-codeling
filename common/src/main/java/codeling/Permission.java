package codeling;

import codeling.platform.Platform;
import java.util.Locale;
import java.util.function.Predicate;
import net.minecraft.server.level.ServerPlayer;

/**
 * What a permission mod can allow or refuse each player (issue #51), as {@code codeling.robot} and so on. The default is
 * Codeling's own rule; it stands when there is no permission mod, or it says nothing about the player.
 */
public enum Permission {
    /** /robot commands, and programs that use a robot. */
    ROBOT(player -> true),
    /** The calls that build many blocks: blocks.place, blocks.fill, the shapes and the maze. */
    BUILD(player -> true),
    /** Items without gathering them: setItem, /robot give and building without items. */
    FREE_ITEMS(player -> player.isCreative() || Codeling.freeItems(player.level().getServer())),
    /** player.teleport, as /tp would allow it. */
    TELEPORT(player -> McCompat.isSingleplayerOwner(player) || McCompat.isGamemaster(player));

    private final Predicate<ServerPlayer> byDefault;

    Permission(Predicate<ServerPlayer> byDefault) {
        this.byDefault = byDefault;
    }

    /** The last part of the name, for example {@code free_items}. */
    public String path() {
        return name().toLowerCase(Locale.ROOT);
    }

    /** The whole name, for example {@code codeling.free_items}. */
    public String node() {
        return Codeling.MOD_ID + "." + path();
    }

    public boolean byDefault(ServerPlayer player) {
        return byDefault.test(player);
    }

    public boolean allows(ServerPlayer player) {
        return Platform.INSTANCE.allows(player, this);
    }
}
