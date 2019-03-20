package toyhippogriff.sodden.api.item;

public interface WateringCanMaterial
{
    int getCapacity();

    int getUsage();

    int getRadius();

    float getCropGrowthChance();

    float getFlowerGrowthChance();

    float getSpreadChance();
}