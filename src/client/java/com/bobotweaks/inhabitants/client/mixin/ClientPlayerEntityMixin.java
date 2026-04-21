package com.bobotweaks.inhabitants.client.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;
import com.bobotweaks.inhabitants.networking.payloads.AscendPayload;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

import net.minecraft.block.ShapeContext;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayerEntity.class)
public abstract class ClientPlayerEntityMixin {

    @Inject(
        method = "tickMovement",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/network/ClientPlayerEntity;jump()V"
        )
    )
    private void inhabitants$immaterialJump(CallbackInfo ci) {
        ClientPlayerEntity player = (ClientPlayerEntity) (Object) this;

        if (player.hasStatusEffect(ModEffects.IMMATERIAL)) {

            if (inhabitants$isInsideBlock(player)) {
                ClientPlayNetworking.send(new AscendPayload());
            }
        }
    }

    @Unique
    private boolean inhabitants$isInsideBlock(ClientPlayerEntity player) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        for (double dy : new double[]{0.1, 0.8, 1.62}) {
            
            BlockPos pos = BlockPos.ofFloored(px, py + dy, pz);
            if (!player.getEntityWorld().getBlockState(pos)
                .getCollisionShape(player.getEntityWorld(), pos, ShapeContext.absent())
                .isEmpty())
            {
                return true;
            }
        }

        return false;
    }
}
