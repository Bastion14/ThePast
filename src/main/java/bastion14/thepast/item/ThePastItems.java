package bastion14.thepast.item;

import bastion14.thepast.ThePastMod;
import bastion14.thepast.dim.ThePastDimensions;
import bastion14.thepast.entities.ThePastEntities;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.dimension.OverworldDimension;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ThePastItems {
    public static final DeferredRegister<Item> ITEMS = new DeferredRegister<>(ForgeRegistries.ITEMS, ThePastMod.MODID);

    public static final RegistryObject<ThePastSpawnEgg> PHANTOM_ALIVE_SPAWNER =
            ITEMS.register("phantom_spawn_egg", ()->new ThePastSpawnEgg(ThePastEntities.PHANTOM_ALIVE::get, 0xFF668888, 8978176, new Item.Properties().group(ItemGroup.MISC)));

    public static final RegistryObject<DebugTimeMachine> DEBUG_TIME_MACHINE =
            ITEMS.register("debug_time_machine", ()-> new DebugTimeMachine(new Item.Properties().maxStackSize(1), ThePastDimensions.getSupplier()));
    public static final RegistryObject<DebugTimeMachine> DEBUG_TIME_MACHINE_OVERWORLD =
            ITEMS.register("debug_time_machine_overworld", ()-> new DebugTimeMachine(new Item.Properties().maxStackSize(1), ()->DimensionType.OVERWORLD));

    public static void registerItems(){
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
