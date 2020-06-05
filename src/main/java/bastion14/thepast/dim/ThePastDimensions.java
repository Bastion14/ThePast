package bastion14.thepast.dim;

import bastion14.thepast.ThePastMod;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.common.ModDimension;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ThePastDimensions {
    private static final Logger LOGGER = LogManager.getLogger("DimensionsRegistry");

    public static final DeferredRegister<ModDimension> DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, ThePastMod.MODID);

    public static final RegistryObject<ModDimension> THE_PAST_DIMENSION = DIMENSIONS.register("thepastdimension", ThePastModDimension::new);
    private static DimensionType THE_PAST_TYPE;

    public static void registerDimensions(){
        DIMENSIONS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }

    @SubscribeEvent
    public static void registerDimensionsActually(RegisterDimensionsEvent rde){
        LOGGER.info("Registering the past");
//        if(DimensionType.byName(THE_PAST_DIMENSION.getId()) == null){
            THE_PAST_TYPE = DimensionManager.registerOrGetDimension(THE_PAST_DIMENSION.getId(), THE_PAST_DIMENSION.get(), null, true);
            LOGGER.info("It's type is {}", THE_PAST_TYPE);
//        }
    }

    public static void teleportPlayer(ServerPlayerEntity player, DimensionType type, BlockPos destPos){
        ServerWorld nextWorld = player.getServer().getWorld(type);
        nextWorld.getChunk(destPos);
        player.teleport(nextWorld, destPos.getX(), destPos.getY(), destPos.getZ(), player.rotationYaw, player.rotationPitch);
    }
    public static Supplier<DimensionType> getSupplier(){
        return ()-> {
            LOGGER.info("Retrieved the past, and it is {}", THE_PAST_TYPE);
            return THE_PAST_TYPE;
        };

    }
}
