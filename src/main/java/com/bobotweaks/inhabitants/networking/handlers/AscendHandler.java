package com.bobotweaks.inhabitants.networking.handlers;

import com.bobotweaks.inhabitants.init.ModEffects;
import com.bobotweaks.inhabitants.networking.payloads.AscendPayload;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.ShapeContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class AscendHandler {
    public static void receive(AscendPayload payload, ServerPlayNetworking.Context context) {
        ServerPlayerEntity player = context.player();
        
        context.server().execute(() -> {
            if (player.hasStatusEffect(ModEffects.IMMATERIAL)) {

                if (isInsideBlock(player)) {
                    player.teleport(
                        player.getX(),
                        player.getY() + 1.0,
                        player.getZ(),
                        true
                    );
                }
            }
        });
    }

    private static boolean isInsideBlock(ServerPlayerEntity player) {
        double px = player.getX();
        double py = player.getY();
        double pz = player.getZ();

        for (double dy : new double[]{0.1, 0.8, 1.62}) {
            BlockPos pos = BlockPos.ofFloored(px, py + dy, pz);

            if (!player.getEntityWorld().getBlockState(pos)
                .getCollisionShape(player.getEntityWorld(), pos, ShapeContext.absent())
                .isEmpty()) {
                return true;
            }
        }
        
        return false;
    }
}
