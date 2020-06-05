package bastion14.thepast.item;

import bastion14.thepast.dim.ThePastDimensions;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BucketItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.function.Supplier;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class DebugTimeMachine extends Item {
    private final Supplier<DimensionType> destination;
    public DebugTimeMachine(Properties properties, Supplier<DimensionType> type) {
        super(properties);
        destination = type;
    }

    /**
     * Called to trigger the item's "innate" right click behavior. To handle when this item is used on a Block, see
     * {@link #onItemUse}.
     *
     * @param worldIn
     * @param playerIn
     * @param handIn
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if(!worldIn.isRemote){
            ThePastDimensions.teleportPlayer((ServerPlayerEntity) playerIn, destination.get(), playerIn.getPosition());
        }
        return super.onItemRightClick(worldIn, playerIn, handIn);
    }
}
