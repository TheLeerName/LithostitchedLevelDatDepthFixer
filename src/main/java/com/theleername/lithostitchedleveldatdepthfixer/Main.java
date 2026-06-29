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
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public final class Main {
	public static final int MAX_DEPTH = 4096;
	private static final Logger LOGGER = LogUtils.getLogger();
	private static final Set<Path> FIXED = ConcurrentHashMap.newKeySet();

	public static void fix(Path levelDat) {
		if (levelDat == null)
			return;
		if (!FIXED.add(levelDat.toAbsolutePath()))
			return;
		if (!Files.exists(levelDat))
			return;

		try {
			CompoundTag root = NbtIo.readCompressed(levelDat, new NbtAccounter(Long.MAX_VALUE, MAX_DEPTH));
			CompoundTag data = root.getCompound("Data");
			CompoundTag worldGen = data.getCompound("WorldGenSettings");
			CompoundTag dimensions = worldGen.getCompound("dimensions");
			for (var dimensionName : dimensions.getAllKeys()) {
				if (dimensionName == null) continue;
				if (!dimensions.contains(dimensionName, Tag.TAG_COMPOUND)) continue;
				CompoundTag dimension = dimensions.getCompound(dimensionName);

				if (!dimension.contains("generator", Tag.TAG_COMPOUND)) continue;
				CompoundTag generator = dimension.getCompound("generator");

				if (!generator.contains("settings", Tag.TAG_COMPOUND)) continue;
				CompoundTag settings = generator.getCompound("settings");

				if (!settings.contains("surface_rule", Tag.TAG_COMPOUND)) continue;
				CompoundTag surfaceRule = settings.getCompound("surface_rule");

				if (!surfaceRule.contains("sequence", Tag.TAG_LIST)) continue;
				ListTag sequence = surfaceRule.getList("sequence", Tag.TAG_COMPOUND);

				if (sequence.size() < 2) continue;
				Tag tag = sequence.get(1);
				if (!(tag instanceof CompoundTag compound))
					continue;
					if (!compound.contains("type"))
					continue;
				if (!compound.getString("type").equals("lithostitched:transient_merged"))
					continue;

				sequence.remove(1);
			}
			NbtIo.writeCompressed(root, levelDat);
			FIXED.add(levelDat);
			LOGGER.info("Removed lithostitched:transient_merged from {}", levelDat);
		} catch (Exception e) {
			LOGGER.error("Failed to repair level.dat", e);
		}
	}
}