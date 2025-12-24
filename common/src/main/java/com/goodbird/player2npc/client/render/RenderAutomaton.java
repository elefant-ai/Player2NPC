//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.goodbird.player2npc.client.render;

import com.goodbird.player2npc.client.util.ImageDownloadAlt;
import com.goodbird.player2npc.client.util.ResourceDownloader;
import com.goodbird.player2npc.companion.AutomatoneEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import java.io.File;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.PlayerModel;
import net.minecraft.client.model.HumanoidModel.ArmPose;
import net.minecraft.client.model.geom.ModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.*;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.DefaultPlayerSkin;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.item.CrossbowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemUseAnimation;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

public class RenderAutomaton extends LivingEntityRenderer<AutomatoneEntity, AutomatoneRenderState, AutomatoneModel> {
    public RenderAutomaton(EntityRendererProvider.Context ctx) {
        super(ctx, new AutomatoneModel(ctx.bakeLayer(ModelLayers.PLAYER), false), 0.5F);
        boolean slim = false;
        //this.addLayer(new HumanoidArmorLayer(this, new HumanoidArmorModel(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_INNER_ARMOR : ModelLayers.PLAYER_INNER_ARMOR)), new HumanoidArmorModel(ctx.bakeLayer(slim ? ModelLayers.PLAYER_SLIM_OUTER_ARMOR : ModelLayers.PLAYER_OUTER_ARMOR)), ctx.getModelManager()));
        this.addLayer(new PlayerItemInHandLayer(this));
        this.addLayer(new ArrowLayer(this, ctx));
        this.addLayer(new CustomHeadLayer(this, ctx.getModelSet(), ctx.getPlayerSkinRenderCache()));
        this.addLayer(new WingsLayer(this, ctx.getModelSet(), ctx.getEquipmentRenderer()));
        //this.addLayer(new SpinAttackEffectLayer(this, ctx.getModelSet()));
        this.addLayer(new BeeStingerLayer(this, ctx));
    }

//    public void render(AutomatoneEntity automatoneEntity, float f, float g, PoseStack matrixStack, MultiBufferSource vertexConsumerProvider, int i) {
//        try {
//            this.setModelPose(automatoneEntity);
//            super.render(automatoneEntity, f, g, matrixStack, vertexConsumerProvider, i);
//        } catch (Exception var8) {
//        }
//
//    }


    @Override
    public void extractRenderState(AutomatoneEntity livingEntity, AutomatoneRenderState livingEntityRenderState, float f) {
        super.extractRenderState(livingEntity, livingEntityRenderState, f);
        HumanoidRenderState.extractArmedEntityRenderState(livingEntity, livingEntityRenderState, this.itemModelResolver);
        livingEntityRenderState.entity = livingEntity;
        setModelPose(livingEntity, livingEntityRenderState);
    }

    @Override
    public AutomatoneRenderState createRenderState() {
        return new AutomatoneRenderState();
    }

//    public Vec3 getPositionOffset(AutomatoneEntity automatoneEntity, float f) {
//        return automatoneEntity.isCrouching() ? new Vec3((double)0.0F, (double)-0.125F, (double)0.0F) : super.getRenderOffset(automatoneEntity, f);
//    }

    private void setModelPose(AutomatoneEntity player, AutomatoneRenderState state) {
        AutomatoneModel playerEntityModel = (AutomatoneModel)this.getModel();
        if (player.isSpectator()) {
            playerEntityModel.setAllVisible(false);
            playerEntityModel.head.visible = true;
            playerEntityModel.hat.visible = true;
        } else {
            playerEntityModel.setAllVisible(true);
            playerEntityModel.hat.visible = true;
            playerEntityModel.jacket.visible = true;
            playerEntityModel.leftPants.visible = true;
            playerEntityModel.rightPants.visible = true;
            playerEntityModel.leftSleeve.visible = true;
            playerEntityModel.rightSleeve.visible = true;
            state.isCrouching = player.isCrouching();
            HumanoidModel.ArmPose armPose = getArmPose(player, InteractionHand.MAIN_HAND);
            HumanoidModel.ArmPose armPose2 = getArmPose(player, InteractionHand.OFF_HAND);
            if (armPose.isTwoHanded()) {
                armPose2 = player.getOffhandItem().isEmpty() ? ArmPose.EMPTY : ArmPose.ITEM;
            }

            if (player.getMainArm() == HumanoidArm.RIGHT) {
                state.rightArmPose = armPose;
                state.leftArmPose = armPose2;
            } else {
                state.rightArmPose = armPose2;
                state.leftArmPose = armPose;
            }
        }

    }

    private static HumanoidModel.ArmPose getArmPose(AutomatoneEntity player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        if (itemStack.isEmpty()) {
            return ArmPose.EMPTY;
        } else {
            if (player.getUsedItemHand() == hand && player.getUseItemRemainingTicks() > 0) {
                ItemUseAnimation useAction = itemStack.getUseAnimation();
                if (useAction == ItemUseAnimation.BLOCK) {
                    return ArmPose.BLOCK;
                }

                if (useAction == ItemUseAnimation.BOW) {
                    return ArmPose.BOW_AND_ARROW;
                }

                if (useAction == ItemUseAnimation.SPEAR) {
                    return ArmPose.THROW_SPEAR;
                }

                if (useAction == ItemUseAnimation.CROSSBOW && hand == player.getUsedItemHand()) {
                    return ArmPose.CROSSBOW_CHARGE;
                }

                if (useAction == ItemUseAnimation.SPYGLASS) {
                    return ArmPose.SPYGLASS;
                }

                if (useAction == ItemUseAnimation.TOOT_HORN) {
                    return ArmPose.TOOT_HORN;
                }

                if (useAction == ItemUseAnimation.BRUSH) {
                    return ArmPose.BRUSH;
                }
            } else if (!player.swinging && itemStack.is(Items.CROSSBOW) && CrossbowItem.isCharged(itemStack)) {
                return ArmPose.CROSSBOW_HOLD;
            }

            return ArmPose.ITEM;
        }
    }

    @Override
    public ResourceLocation getTextureLocation(AutomatoneRenderState livingEntityRenderState) {
        AutomatoneEntity npc = livingEntityRenderState.entity;
        return getTextureLocation(npc);
    }

    public ResourceLocation getTextureLocation(AutomatoneEntity npc) {
        if (npc.textureLocation == null) {
            try {
                boolean fixSkin = true;
                File file = ResourceDownloader.getUrlFile(npc.getCharacter().skinURL(), fixSkin);
                npc.textureLocation = ResourceDownloader.getUrlResourceLocation(npc.getCharacter().skinURL(), fixSkin);
                this.loadSkin(file, npc.textureLocation, npc.getCharacter().skinURL(), fixSkin);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return npc.textureLocation == null ? DefaultPlayerSkin.getDefaultTexture() : npc.textureLocation;
    }

    private void loadSkin(File file, ResourceLocation resource, String par1Str, boolean fix64) {
        TextureManager texturemanager = Minecraft.getInstance().getTextureManager();
        AbstractTexture object = texturemanager.getTexture(resource);
        if (object == null) {
            ResourceDownloader.load(new ImageDownloadAlt(file, par1Str, resource, DefaultPlayerSkin.getDefaultTexture(), fix64, () -> {
            }));
        }

    }

    @Override
    protected void scale(AutomatoneRenderState livingEntityRenderState, PoseStack poseStack) {
        poseStack.scale(0.9375F, 0.9375F, 0.9375F);
    }

    @Override
    protected void setupRotations(AutomatoneRenderState livingEntityRenderState, PoseStack matrixStack, float f, float g) {
        AutomatoneEntity automatoneEntity = livingEntityRenderState.entity;
        float h = 0;
        float i = automatoneEntity.getSwimAmount(h);
        if (automatoneEntity.isFallFlying()) {
            super.setupRotations(livingEntityRenderState, matrixStack, f, g);
            float j = (float)automatoneEntity.getFallFlyingTicks() + h;
            float k = Mth.clamp(j * j / 100.0F, 0.0F, 1.0F);
            if (!automatoneEntity.isAutoSpinAttack()) {
                matrixStack.mulPose(Axis.XP.rotationDegrees(k * (-90.0F - automatoneEntity.getXRot())));
            }

            Vec3 vec3d = automatoneEntity.getViewVector(h);
            Vec3 vec3d2 = automatoneEntity.lerpVelocity(h);
            double d = vec3d2.horizontalDistanceSqr();
            double e = vec3d.horizontalDistanceSqr();
            if (d > (double)0.0F && e > (double)0.0F) {
                double l = (vec3d2.x * vec3d.x + vec3d2.z * vec3d.z) / Math.sqrt(d * e);
                double m = vec3d2.x * vec3d.z - vec3d2.z * vec3d.x;
                matrixStack.mulPose(Axis.YP.rotation((float)(Math.signum(m) * Math.acos(l))));
            }
        } else if (i > 0.0F) {
            super.setupRotations(livingEntityRenderState, matrixStack, f, g);
            float j = automatoneEntity.isInWater() ? -90.0F - automatoneEntity.getXRot() : -90.0F;
            float k = Mth.lerp(i, 0.0F, j);
            matrixStack.mulPose(Axis.XP.rotationDegrees(k));
            if (automatoneEntity.isVisuallySwimming()) {
                matrixStack.translate(0.0F, -1.0F, 0.3F);
            }
        } else {
            super.setupRotations(livingEntityRenderState, matrixStack, f, g);
        }

    }
}
