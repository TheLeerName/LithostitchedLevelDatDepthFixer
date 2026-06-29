package com.theleername.lithostitchedleveldatdepthfixer.mixin;

import com.theleername.lithostitchedleveldatdepthfixer.Main;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.NbtIo;
import net.minecraft.world.level.storage.LevelStorageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.io.IOException;
import java.nio.file.Path;

@Mixin(LevelStorageSource.class)
public class LevelStorageSourceMixin {
	@Redirect(method = "readLevelDataTagRaw", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/nbt/NbtIo;readCompressed(Ljava/nio/file/Path;Lnet/minecraft/nbt/NbtAccounter;)Lnet/minecraft/nbt/CompoundTag;"
	))
	private static CompoundTag worldfix$read(Path path, NbtAccounter original) throws IOException {
		Main.fix(path);
		return NbtIo.readCompressed(path, new NbtAccounter(104857600L, 512));
	}
}