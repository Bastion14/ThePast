package bastion14.thepast.entities;

import bastion14.thepast.ThePastMod;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;
import net.minecraft.world.IEntityReader;
import net.minecraft.world.World;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

public class ThePastEntities {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = new DeferredRegister<>(ForgeRegistries.ENTITIES, ThePastMod.MODID);

    public static final RegistryObject<EntityType<PhantomAliveEntity>> PHANTOM_ALIVE = ENTITY_TYPES.register("phantom_alive", ()-> EntityType.Builder.create(PhantomAliveEntity::new, EntityClassification.CREATURE).size(0.5f, 0.5f).build("phantom_alive"));

    public static void registerDeferred(){
        ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
}
