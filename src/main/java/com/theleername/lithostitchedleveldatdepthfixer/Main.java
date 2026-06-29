package com.theleername.lithostitchedleveldatdepthfixer;

import com.mojang.logging.LogUtils;
import net.minecraft.nbt.NbtAccounter;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.Tag;
import org.slf4j.Logger;

import java.nio.file.Files;
import java.nio.file.Path;

public final class Main {
	private static final Logger LOGGER = LogUtils.getLogger();

	public static void fix(Path levelDat) {
		try {
			if (levelDat == null)
				return;
			if (!Files.exists(levelDat))
				return;

			CompoundTag root = NbtIo.readCompressed(levelDat, NbtAccounter.unlimitedHeap());
			CompoundTag data = root.getCompound("Data");
			CompoundTag worldGen = data.getCompound("WorldGenSettings");
			CompoundTag dimensions = worldGen.getCompound("dimensions");
			CompoundTag overworld = dimensions.getCompound("minecraft:overworld");
			CompoundTag generator = overworld.getCompound("generator");
			CompoundTag settings = generator.getCompound("settings");
			CompoundTag surfaceRule = settings.getCompound("surface_rule");
			ListTag sequence = surfaceRule.getList("sequence", Tag.TAG_COMPOUND);

			Tag tag = sequence.get(1);
			if (!(tag instanceof CompoundTag compound))
				return;
			if (!compound.contains("type"))
				return;
			if (!compound.getString("type").equals("lithostitched:transient_merged"))
				return;

			sequence.remove(1);
			NbtIo.writeCompressed(root, levelDat);
			LOGGER.info("Removed lithostitched transient_merged from {}", levelDat);
		} catch (Exception e) {
			LOGGER.error("Failed to repair level.dat", e);
		}
	}
}