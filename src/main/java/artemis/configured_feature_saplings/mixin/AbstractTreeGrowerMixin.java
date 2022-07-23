package artemis.configured_feature_saplings.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.MushroomBlock;
import net.minecraft.world.level.block.grower.AbstractTreeGrower;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.Random;

@Mixin(AbstractTreeGrower.class)
public class AbstractTreeGrowerMixin {

	@Redirect(
		method = "growTree(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;Ljava/util/Random;)Z",
		at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/levelgen/feature/ConfiguredFeature;place(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/core/BlockPos;)Z"))
	private boolean configured_feature_saplings_fixSaplings(ConfiguredFeature<?,?> staticConfiguredFeature, WorldGenLevel level, ChunkGenerator chunkGenerator, Random randomSource, BlockPos blockPos) {
		Optional<ResourceKey<ConfiguredFeature<?, ?>>> optionalKey = BuiltinRegistries.CONFIGURED_FEATURE.getResourceKey(staticConfiguredFeature);
		if (optionalKey.isPresent()) {
			Registry<ConfiguredFeature<?, ?>> dynamicRegistryCF = level.registryAccess().registryOrThrow(Registry.CONFIGURED_FEATURE_REGISTRY);
			Optional<ConfiguredFeature<?, ?>> dynamicCF = dynamicRegistryCF.getOptional(optionalKey.get().location());
			if (dynamicCF.isPresent()) {
				return dynamicCF.get().place(level, chunkGenerator, randomSource, blockPos);
			}
		}

		return staticConfiguredFeature.place(level, chunkGenerator, randomSource, blockPos);
	}
}