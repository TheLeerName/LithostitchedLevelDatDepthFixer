package com.theleername.lithostitchedleveldatdepthfixer.mixin;

import com.theleername.lithostitchedleveldatdepthfixer.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.nio.file.Path;

@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {
	@Inject(method = "readLevelDataTagRaw", at = @At("HEAD"))
	private static void worldfix$repair(Path levelPath,CallbackInfoReturnable<CompoundTag> cir) {
		Main.fix(levelPath);
	}

	@ModifyArg(method = "readLevelDataTagRaw", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/nbt/NbtIo;readCompressed(Ljava/nio/file/Path;Lnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/CompoundTag;"
	), index = 1)
	private static NbtAccounter worldfix$depth(NbtAccounter original) {
		return new NbtAccounter(104857600L, Main.MAX_DEPTH);
	}
}