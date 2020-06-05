package bastion14.thepast.entities.render;

import bastion14.thepast.ThePastMod;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.AbstractEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPredicate;
import net.minecraft.util.ResourceLocation;

public class PhantomAliveEyesLayer<T extends Entity> extends AbstractEyesLayer<T, PhantomModel<T>> {
    private static final RenderType PHANTOM_EYES = RenderType.getEyes(new ResourceLocation(ThePastMod.MODID, "textures/entity/phantom_eyes_alive.png"));
    public PhantomAliveEyesLayer(IEntityRenderer<T, PhantomModel<T>> p_i226039_1_) {
        super(p_i226039_1_);
    }

    @Override
    public RenderType getRenderType() {
        return PHANTOM_EYES;
    }
}
