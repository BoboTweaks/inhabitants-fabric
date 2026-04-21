package com.bobotweaks.inhabitants.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.*;
import net.minecraft.world.BlockView;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractBlock.AbstractBlockState.class)
public abstract class ImmaterialMixin {

    @Inject(
        method = "getCollisionShape(Lnet/minecraft/world/BlockView;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/ShapeContext;)Lnet/minecraft/util/shape/VoxelShape;", 
        at = @At("RETURN"), 
        cancellable = true
    )
    private void inhabitants$immaterialCollision(
        BlockView world,
        BlockPos pos,
        ShapeContext context,
        CallbackInfoReturnable<VoxelShape> cir
    ) {
        if (context instanceof EntityShapeContext entityCtx) {
            if (entityCtx.getEntity() instanceof PlayerEntity player) {

                if (player.hasStatusEffect(ModEffects.IMMATERIAL) &&
                    !((AbstractBlock.AbstractBlockState)(Object)this).isOf(Blocks.BEDROCK)) {
                    
                    VoxelShape shape = cir.getReturnValue();
                    if (!shape.isEmpty()) {
                        double shapeMaxY = shape.getMax(Direction.Axis.Y) + pos.getY();
                        
                        double leniency = player.isSneaking() ? 0.05 : 0.001;
                        if (player.getY() >= shapeMaxY - leniency) {
                            return;
                        }
                        
                        BlockPos footPos = BlockPos.ofFloored(player.getX(), player.getY(), player.getZ());
                        BlockPos eyePos = BlockPos.ofFloored(player.getX(), player.getEyeY(), player.getZ());
                        
                        BlockPos wallPosAtFoot = new BlockPos(pos.getX(), footPos.getY(), pos.getZ());
                        BlockPos wallPosAtEye = new BlockPos(pos.getX(), eyePos.getY(), pos.getZ());

                        if (world.getBlockState(wallPosAtFoot).getCollisionShape(world, wallPosAtFoot).isEmpty() ||
                            world.getBlockState(wallPosAtEye).getCollisionShape(world, wallPosAtEye).isEmpty()) {
                            return;
                        }

                        cir.setReturnValue(VoxelShapes.empty());
                    }
                }
            }
        }
    }
}
