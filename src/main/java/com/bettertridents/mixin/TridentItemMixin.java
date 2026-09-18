package com.bettertridents.mixin;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.TridentItem;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(TridentItem.class)
public abstract class TridentItemMixin {

    @Unique
    private static boolean isSnowingAt(LivingEntity entity) {
        Level level = entity.level();
        BlockPos pos = entity.blockPosition();

        return level.isRaining()
                && level.canSeeSky(pos)
                && level.getBiome(pos).value().coldEnoughToSnow(pos, level.getSeaLevel());
    }

    @Redirect(
            method = {"use", "releaseUsing"},
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;isInWaterOrRain()Z"
            )
    )
    private boolean allowRiptideInSnow(Player player) {
        return player.isInWaterOrRain() || isSnowingAt(player);
    }
}