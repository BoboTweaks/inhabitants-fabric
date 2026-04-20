package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.client.items.SpikeDrillClient;
import com.bobotweaks.inhabitants.init.ModItems;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.render.command.OrderedRenderCommandQueue;
import net.minecraft.client.render.item.HeldItemRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemDisplayContext;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.client.MinecraftClient;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HeldItemRenderer.class)
public abstract class HeldItemRendererMixin {
 
    @Shadow
    private MinecraftClient client;
 
    @Shadow
    private ItemStack mainHand;
 
    @Shadow
    private ItemStack offHand;

    @Shadow
    public abstract void renderItem(
        LivingEntity entity,
        ItemStack stack,
        ItemDisplayContext renderMode,
        MatrixStack matrices,
        OrderedRenderCommandQueue vertexConsumers,
        int light
    );
 
    @Inject(
        method = "updateHeldItems",
        at = @At("HEAD")
    )
    private void inhabitants$stabilizeDrillSwap(CallbackInfo ci) {
        if (this.client.player == null) return;
 
        ItemStack currentMain = this.client.player.getMainHandStack();
        if (currentMain.isOf(ModItems.SPIKE_DRILL) && this.mainHand.isOf(ModItems.SPIKE_DRILL)) {
            this.mainHand = currentMain;
        }
 
        ItemStack currentOff = this.client.player.getOffHandStack();
        if (currentOff.isOf(ModItems.SPIKE_DRILL) && this.offHand.isOf(ModItems.SPIKE_DRILL)) {
            this.offHand = currentOff;
        }
    }

    @Inject(
        method = "renderFirstPersonItem",
        at = @At("HEAD"),
        cancellable = true
    )
    private void inhabitants$applyFpvAnimation(
        AbstractClientPlayerEntity player,
        float tickDelta,
        float pitch,
        Hand hand,
        float swingProgress,
        ItemStack item,
        float equipProgress,
        MatrixStack matrices,
        OrderedRenderCommandQueue vertexConsumers,
        int light,
        CallbackInfo ci
    ) {
        if (!item.isOf(ModItems.SPIKE_DRILL) ||
            !(player instanceof ClientPlayerEntity clientPlayer)) return;

        Arm arm = (hand == Hand.MAIN_HAND) ? player.getMainArm() : player.getMainArm().getOpposite();
        boolean isRight = arm == Arm.RIGHT;
        int i = isRight ? 1 : -1;

        matrices.push();

        boolean animated = SpikeDrillClient.applyHandTransform(
            matrices,
            clientPlayer,
            arm,
            item,
            tickDelta,
            equipProgress,
            swingProgress
        );

        if (!animated) {
            matrices.translate(i * 0.56F, -0.52F + equipProgress * -0.6F, -0.72F);
        }

        renderItem(
            player,
            item,
            isRight ? ItemDisplayContext.FIRST_PERSON_RIGHT_HAND : ItemDisplayContext.FIRST_PERSON_LEFT_HAND,
            matrices,
            vertexConsumers,
            light
        );

        matrices.pop();
        ci.cancel();
    }
}
