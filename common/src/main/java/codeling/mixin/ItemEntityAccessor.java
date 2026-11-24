package codeling.mixin;

import java.util.UUID;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/** Who alone may pick an item up (issue #53); vanilla has only the setter. Null: everybody. */
@Mixin(ItemEntity.class)
public interface ItemEntityAccessor {
    @Accessor("target")
    UUID codeling$target();
}
