package com.brandon3055.draconicevolution.mixin;

import codechicken.lib.util.SneakyUtils;
import com.brandon3055.draconicevolution.client.render.item.RenderModularStaff;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.entity.LivingEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Created by brandon3055 on 4/2/21
 */
@Mixin(PlayerModel.class)
public class PlayerModelMixin {

    public PlayerModel<?> getThis() {
        return SneakyUtils.unsafeCast(this);
    }

    @Inject(
            method = "setupAnim(Lnet/minecraft/entity/LivingEntity;FFFFF)V",
            at = @At("RETURN")
    )
    public void afterSetupAnim(LivingEntity entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        RenderModularStaff.doMixinStuff(entity, getThis());
    }
}
