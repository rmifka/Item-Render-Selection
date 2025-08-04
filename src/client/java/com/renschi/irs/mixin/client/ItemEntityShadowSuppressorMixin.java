package com.renschi.irs.mixin.client;

import com.renschi.irs.RegexFilterManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderer.class)
public abstract class ItemEntityShadowSuppressorMixin<T extends Entity> {

    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true)
    public void shouldRender(T entity, Frustum frustum, double d, double e, double f, CallbackInfoReturnable<Boolean> ci) {
        if (entity.getType()
                .equals(EntityType.ITEM)
                && RegexFilterManager.shouldHide(entity.getName()
                .getString())) {
            ci.setReturnValue(false);
        }
    }
}
