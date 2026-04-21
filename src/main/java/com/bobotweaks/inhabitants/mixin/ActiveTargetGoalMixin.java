package com.bobotweaks.inhabitants.mixin;

import com.bobotweaks.inhabitants.init.ModEffects;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.ActiveTargetGoal;
import net.minecraft.entity.ai.goal.TrackTargetGoal;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.registry.tag.EntityTypeTags;

import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ActiveTargetGoal.class)
public abstract class ActiveTargetGoalMixin extends TrackTargetGoal {

    @Shadow
    protected LivingEntity targetEntity;

    protected ActiveTargetGoalMixin(MobEntity mob, boolean checkVisibility) {
        super(mob, checkVisibility);
    }

    @Inject(method = "findClosestTarget", at = @At("TAIL"))
    private void inhabitants$ignorePlayersWithUndeadDisguise(CallbackInfo ci) {
        if (this.targetEntity instanceof PlayerEntity player
            && this.mob != null
            && this.mob.getType().isIn(EntityTypeTags.UNDEAD)
            && player.hasStatusEffect(ModEffects.UNDEAD_DISGUISE)) {
            
            this.targetEntity = null;
            this.mob.setTarget(null);
        }
    }
}
