package artemis.configured_feature_saplings.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.Random;

@Mixin(MushroomBlock.class)
public class MushroomBlockMixin {

	@Redirect(
		method = "growMushroom(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/util/RandomSource;)Z",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/util/RandomSource;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean configured_feature_saplings_fixMushrooms(ConfiguredFeature<?,?> staticConfiguredFeature, WorldGenLevel level, ChunkGenerator chunkGenerator, RandomSource random, BlockPos blockPos) {
		Optional<ResourceKey<ConfiguredFeature<?, ?>>> optionalKey = BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(staticConfiguredFeature);
		if (optionalKey.isPresent()) {
			Registry<ConfiguredFeature<?, ?>> dynamicRegistryCF = level.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
			Optional<ConfiguredFeature<?, ?>> dynamicCF = dynamicRegistryCF.getOptional(optionalKey.get().location());
			if (dynamicCF.isPresent()) {
				return dynamicCF.get().place(level, chunkGenerator, random, blockPos);
			}
		}

		return staticConfiguredFeature.place(level, chunkGenerator, random, blockPos);
	}
}