package bastion14.thepast.entities.render;

import bastion14.thepast.ThePastMod;
import bastion14.thepast.entities.PhantomAliveEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.renderer.Vector3f;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.renderer.entity.PhantomRenderer;
import net.minecraft.client.renderer.entity.layers.PhantomEyesLayer;
import net.minecraft.client.renderer.entity.model.PhantomModel;
import net.minecraft.util.ResourceLocation;

import javax.annotation.ParametersAreNonnullByDefault;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class PhantomAliveRenderer extends MobRenderer<PhantomAliveEntity, PhantomModel<PhantomAliveEntity>> {
    private static final ResourceLocation PHANTOM_ALIVE_LOCATION = new ResourceLocation(ThePastMod.MODID, "textures/entity/phantom_alive.png");
    private static final ResourceLocation PHANTOM_ORANGE_LOCATION = new ResourceLocation(ThePastMod.MODID, "textures/entity/phantom_orange.png");
    public PhantomAliveRenderer(EntityRendererManager renderManagerIn) {
        super(renderManagerIn, new PhantomModel<>(), 0.75f);
        this.addLayer(new PhantomAliveEyesLayer<>(this));
    }

    /**
     * Returns the location of an entity's texture.
     *
     * @param entity
     */
    @Override
    public ResourceLocation getEntityTexture(PhantomAliveEntity entity) {
        return PHANTOM_ALIVE_LOCATION;
    }

    @Override
    protected void applyRotations(PhantomAliveEntity entityLiving, MatrixStack matrixStackIn, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrixStackIn, ageInTicks, rotationYaw, partialTicks);
        matrixStackIn.rotate(Vector3f.XP.rotation(entityLiving.rotationPitch));
    }

    @Override
    protected void preRenderCallback(PhantomAliveEntity entitylivingbaseIn, MatrixStack matrixStackIn, float partialTickTime) {
        int i = entitylivingbaseIn.getPhantomAliveSize();
        float f = 1.0f + 0.15f * (float)i;
        matrixStackIn.scale(f, f, f);
        matrixStackIn.translate(0.0, 1.3125, .1875);
    }
}
