package bastion14.thepast;

import bastion14.thepast.config.ThePastConfiguration;
import bastion14.thepast.dim.ThePastDimension;
import bastion14.thepast.dim.ThePastDimensions;
import bastion14.thepast.entities.ThePastEntities;
import bastion14.thepast.entities.render.PhantomAliveRenderer;
import bastion14.thepast.item.ThePastItems;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.SpawnReason;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.Heightmap;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ThePastMod.MODID)
public class ThePastMod {
    public static final String MODID = "thepast";
    private static final Logger LOGGER = LogManager.getLogger(MODID + "-main");
    public ThePastMod(){
        //do some initialization
        //load the configuration file.
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, ThePastConfiguration.COMMON_SPEC);
        //register things using DeferredRegister now, since that's preferred.

        ThePastEntities.registerDeferred();
        ThePastItems.registerItems();
        ThePastDimensions.registerDimensions();
    }
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    static class SetupEvents {
        @SubscribeEvent
        public static void initCommon(final FMLCommonSetupEvent event){
            // do something here, probably, eventually.
        }
        @SubscribeEvent
        public static void initClient(final FMLClientSetupEvent clientEvent){
            LOGGER.debug("Creating entity registerer!");
            RenderingRegistry.registerEntityRenderingHandler(ThePastEntities.PHANTOM_ALIVE.get(), PhantomAliveRenderer::new);
        }
        @SubscribeEvent
        public static void onLoadingComplete(final FMLLoadCompleteEvent lce){
            LOGGER.info("Loading complete. registering spawn conditions. ");
            EntitySpawnPlacementRegistry.register(
                    ThePastEntities.PHANTOM_ALIVE.get(),
                    EntitySpawnPlacementRegistry.PlacementType.NO_RESTRICTIONS,
                    Heightmap.Type.MOTION_BLOCKING_NO_LEAVES,
                    (type, world, reason, pos, rand) -> reason != SpawnReason.NATURAL || world.getDimension() instanceof ThePastDimension
            );
            for(Biome biome : ForgeRegistries.BIOMES) {
                biome.getSpawns(EntityClassification.MONSTER).add(new Biome.SpawnListEntry(ThePastEntities.PHANTOM_ALIVE.get(), 1, 3, 5));
            }
        }
    }

}
