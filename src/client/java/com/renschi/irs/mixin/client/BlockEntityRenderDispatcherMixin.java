package com.renschi.irs.mixin.client;

import com.renschi.irs.RegexFilterManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityRenderDispatcher.class)
public class BlockEntityRenderDispatcherMixin {

    @Inject(
            method = "shouldRender",
            at = @At("HEAD"),
            cancellable = true
    )
    private <E extends Entity> void onShouldRenderEntity(E entity, Frustum frustum, double d, double e, double f, CallbackInfoReturnable<Boolean> ci) {
        if (entity instanceof ItemEntity && RegexFilterManager.isSuppressed((ItemEntity) entity)) {
            ci.setReturnValue(false);
        }
    }
}

