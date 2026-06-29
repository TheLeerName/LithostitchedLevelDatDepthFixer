package com.theleername.lithostitchedleveldatdepthfixer.mixin;

import com.theleername.lithostitchedleveldatdepthfixer.Main;
import net.minecraft.world.level.storage.LevelStorageSource.LevelStorageAccess;
import net.minecraft.world.level.storage.LevelStorageSource.LevelDirectory;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LevelStorageAccess.class)
public class LevelStorageAccessMixin {
	@Shadow
	@Final
	private LevelDirectory levelDirectory;

	@Unique
	private boolean worldfix$checked = false;

	@Inject(method = "getDataTag(Z)Lcom/mojang/serialization/Dynamic;", at = @At("HEAD"))
	private void worldfix$fixLevelDat( boolean useFallback, CallbackInfoReturnable<?> cir) {
		if (worldfix$checked)
			return;
		worldfix$checked = true;

		Main.fix(useFallback ? levelDirectory.oldDataFile() : levelDirectory.dataFile());
	}
}