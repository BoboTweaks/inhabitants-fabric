package com.bobotweaks.inhabitants.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.*;
import net.minecraft.entity.damage.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.*;

import java.util.*;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity {

    @Unique
    private static final Map<UUID, Boolean> inhabitants$sneakStates = new HashMap<>();

    public LivingEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method = "damage", at = @At("HEAD"), cancellable = true)
    private void inhabitants$cancelInWallDamage(
        ServerWorld world,
        DamageSource source,
        float amount,
        CallbackInfoReturnable<Boolean> cir
    ) {
        LivingEntity self = (LivingEntity) (Object) this;

        if (self.hasStatusEffect(ModEffects.IMMATERIAL) && source.isOf(DamageTypes.IN_WALL)) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void inhabitants$immaterialTick(CallbackInfo ci) {

        LivingEntity self = (LivingEntity) (Object) this;
        if (!(self instanceof PlayerEntity player) || self.getEntityWorld().isClient()) return;

        if (player.hasStatusEffect(ModEffects.IMMATERIAL)) {
            inhabitants$handleImmaterialDescend(player);
        }
    }

    @Unique
    private void inhabitants$handleImmaterialDescend(PlayerEntity player) {
        boolean isSneaking = player.isSneaking();
        boolean wasSneaking = inhabitants$sneakStates.getOrDefault(player.getUuid(), false);

        if (isSneaking && !wasSneaking) {
            if (inhabitants$isInsideBlock(player)) {
                double nudgeX = player.getX();
                double nudgeY = player.getY() - 0.4;
                double nudgeZ = player.getZ();

                BlockPos targetPos = BlockPos.ofFloored(nudgeX, nudgeY, nudgeZ);

                if (targetPos.getY() > player.getEntityWorld().getBottomY() &&
                    !player.getEntityWorld().getBlockState(targetPos).isOf(Blocks.BEDROCK)) {
                    
                    if (player instanceof ServerPlayerEntity spe) {
                        spe.requestTeleport(nudgeX, nudgeY, nudgeZ);
                        spe.setVelocity(spe.getVelocity().x, -1.0, spe.getVelocity().z);
                        spe.velocityDirty = true;
                    }
                }
            }
        }

        inhabitants$sneakStates.put(player.getUuid(), isSneaking);
    }

    @Unique
    private boolean inhabitants$isInsideBlock(PlayerEntity player) {
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
