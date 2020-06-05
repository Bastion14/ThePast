package bastion14.thepast.entities;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public class ThePastSpawnEgg extends SpawnEggItem {
    private final Supplier<EntityType<?>> spawn;
    public ThePastSpawnEgg(Supplier<EntityType<?>> typeIn, int primaryColorIn, int secondaryColorIn, Properties builder) {
        super(null, primaryColorIn, secondaryColorIn, builder);
        spawn = typeIn;
    }

    @Override
    public EntityType<?> getType(@Nullable CompoundNBT p_208076_1_) {
        return spawn.get();
    }
}
