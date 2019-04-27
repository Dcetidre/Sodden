package toyhippogriff.sodden.item;

import net.minecraft.block.BambooBlock;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CactusBlock;
import net.minecraft.block.ChorusFlowerBlock;
import net.minecraft.block.CocoaBlock;
import net.minecraft.block.ConnectedPlantBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.FireBlock;
import net.minecraft.block.FluidDrainable;
import net.minecraft.block.GrassBlock;
import net.minecraft.block.PlantBlock;
import net.minecraft.block.SpreadableBlock;
import net.minecraft.block.SugarCaneBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.RayTraceContext;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.DecoratedFeatureConfig;
import net.minecraft.world.gen.feature.FlowerFeature;
import toyhippogriff.sodden.api.item.WateringCanMaterial;
import toyhippogriff.sodden.config.Configuration;

import java.util.List;

public class WateringCanItem extends Item
{
    private WateringCanMaterial material;

    public WateringCanItem(WateringCanMaterial material)
    {
        super(new Item.Settings().itemGroup(ItemGroup.TOOLS).durability(material.getCapacity()));

        this.material = material;
    }

    public WateringCanMaterial getMaterial()
    {
        return material;
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity player, Hand hand)
    {
        ItemStack itemStack = player.getStackInHand(hand);
        HitResult hitResult = Item.getHitResult(world, player, RayTraceContext.FluidHandling.SOURCE_ONLY);
        HitResult.Type type = hitResult.getType();

        if(type == HitResult.Type.BLOCK)
        {
            BlockHitResult blockHitResult = (BlockHitResult) hitResult;
            BlockPos blockPos = blockHitResult.getBlockPos();
            BlockState blockState = world.getBlockState(blockPos);

            if(!player.isSneaking() && this.tryFill(world, blockPos, blockState, player, itemStack))
            {
                return new TypedActionResult<>(ActionResult.SUCCESS, itemStack);
            }

            if(itemStack.getDamage() > material.getCapacity() - material.getUsage())
            {
                return new TypedActionResult<>(ActionResult.FAIL, itemStack);
            }

            int radius = material.getRadius();

            if(Configuration.SPAWN_PARTICLES)
            {
                this.addSplashParticles(world, blockPos, radius);
            }

            Iterable<BlockPos> affectedArea = BlockPos.iterate(blockPos.add(-radius, -1, -radius), blockPos.add(radius, 1, radius));

            if(Configuration.MOISTURIZE_FARMLAND)
            {
                this.moisturizeFarmland(world, affectedArea);
            }

            if(!world.isClient)
            {
                this.waterPlants(world, affectedArea);

                if(!player.isCreative())
                {
                    itemStack.setDamage(Math.min(itemStack.getDamage() + material.getUsage(), material.getCapacity()));
                }
            }
        }

        return new TypedActionResult<>(ActionResult.PASS, itemStack);
    }

    public boolean tryFill(World world, BlockPos blockPos, BlockState blockState, PlayerEntity player, ItemStack itemStack)
    {
        FluidState fluidState = world.getFluidState(blockPos);

        if(fluidState.matches(FluidTags.WATER))
        {
            itemStack.setDamage(Math.max(itemStack.getDamage() - (material.getUsage() * 1000), 0));

            if(Configuration.CONSUME_WATER)
            {
                Block block = blockState.getBlock();

                if(block instanceof FluidDrainable)
                {
                    FluidDrainable fluidDrainable = (FluidDrainable) block;

                    if(world.canPlayerModifyAt(player, blockPos))
                    {
                        fluidDrainable.tryDrainFluid(world, blockPos, blockState);
                    }
                }
            }

            player.playSound(SoundEvents.ITEM_BUCKET_FILL, 1.0F, 1.0F);

            return true;
        }

        return false;
    }

    public void addSplashParticles(World world, BlockPos blockPos, int radius)
    {
        int centerX = blockPos.getX();
        int centerY = blockPos.getY() + 1;
        int centerZ = blockPos.getZ();

        for(int x = centerX - radius; x <= centerX + radius; x++)
        {
            for(int z = centerZ - radius; z <= centerZ + radius; z++)
            {
                world.addParticle(ParticleTypes.SPLASH, x + world.random.nextDouble(), centerY, z + world.random.nextDouble(), 0.0D, 0.0D, 0.0D);
            }
        }
    }

    public void moisturizeFarmland(World world, Iterable<BlockPos> affectedArea)
    {
        affectedArea.forEach(blockPos ->
        {
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if(block instanceof FarmlandBlock)
            {
                int moisture = blockState.get(FarmlandBlock.MOISTURE);

                if(moisture < 7)
                {
                    boolean moisturizeFarmland = world.random.nextInt(7) == 0;

                    if(moisturizeFarmland)
                    {
                        world.setBlockState(blockPos, blockState.with(FarmlandBlock.MOISTURE, 7), 3);
                    }
                }
            }
        });
    }

    public void waterPlants(World world, Iterable<BlockPos> affectedArea)
    {
        boolean growCrops = world.random.nextFloat() < material.getCropGrowthChance();
        boolean growFlowers = world.random.nextFloat() < material.getFlowerGrowthChance();
        boolean spread = world.random.nextFloat() < material.getSpreadChance();

        affectedArea.forEach(blockPos ->
        {
            BlockState blockState = world.getBlockState(blockPos);
            Block block = blockState.getBlock();

            if(growCrops)
            {
                if(block instanceof PlantBlock || block instanceof ConnectedPlantBlock || block instanceof SugarCaneBlock || block instanceof CactusBlock || block instanceof CocoaBlock || block instanceof BambooBlock || block instanceof ChorusFlowerBlock)
                {
                    world.getBlockTickScheduler().schedule(blockPos, block, 0);
                }
            }

            if(growFlowers && block instanceof GrassBlock)
            {
                BlockPos aboveBlockPos = blockPos.up();

                if(world.isAir(aboveBlockPos))
                {
                    List<ConfiguredFeature<?>> features = world.getBiome(blockPos).getFlowerFeatures();

                    if(!features.isEmpty())
                    {
                        ConfiguredFeature feature = features.get(0);
                        DecoratedFeatureConfig featureConfig = (DecoratedFeatureConfig) feature.config;
                        FlowerFeature flowerFeature = (FlowerFeature) featureConfig.feature.feature;
                        BlockState flowerBlockState = flowerFeature.getFlowerToPlace(world.random, aboveBlockPos);

                        boolean growFlower = world.random.nextInt(8) == 0;

                        if(growFlower && flowerBlockState.canPlaceAt(world, blockPos))
                        {
                            world.setBlockState(aboveBlockPos, flowerBlockState, 3);
                        }
                    }
                }
            }

            if(spread && block instanceof SpreadableBlock)
            {
                world.getBlockTickScheduler().schedule(blockPos, block, 0);
            }

            if(Configuration.EXTINGUISH_FIRE && block instanceof FireBlock)
            {
                boolean extinguishFire = world.random.nextInt(3) == 0;

                if(extinguishFire)
                {
                    world.clearBlockState(blockPos, false);
                }
            }
        });
    }
}