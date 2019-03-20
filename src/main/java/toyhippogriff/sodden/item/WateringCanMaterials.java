package toyhippogriff.sodden.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.annotations.JsonAdapter;
import toyhippogriff.sodden.api.item.WateringCanMaterial;

import java.lang.reflect.Type;

@JsonAdapter(WateringCanMaterials.Serializer.class)
public enum WateringCanMaterials implements WateringCanMaterial
{
    WOOD(4000, 50, 0, 0.35F, 0.0F, 0.15F),
    STONE(16000, 100, 1, 0.4F, 0.0F, 0.25F),
    IRON(64000, 250, 2, 0.45F, 0.0F, 0.35F),
    DIAMOND(256000, 500, 3, 0.5F, 0.0F, 0.45F),
    GOLD(16000, 50, 1, 0.0F, 0.15F, 0.0F);

    private int capacity;
    private int usage;
    private int radius;
    private float cropGrowthChance;
    private float flowerGrowthChance;
    private float spreadChance;

    WateringCanMaterials(int capacity, int usage, int radius, float cropGrowthChance, float flowerGrowthChance, float spreadChance)
    {
        this.capacity = capacity;
        this.usage = usage;
        this.radius = radius;
        this.cropGrowthChance = cropGrowthChance;
        this.flowerGrowthChance = flowerGrowthChance;
        this.spreadChance = spreadChance;
    }

    @Override
    public int getCapacity()
    {
        return capacity;
    }

    @Override
    public int getUsage()
    {
        return usage;
    }

    @Override
    public int getRadius()
    {
        return radius;
    }

    @Override
    public float getCropGrowthChance()
    {
        return cropGrowthChance;
    }

    @Override
    public float getFlowerGrowthChance()
    {
        return flowerGrowthChance;
    }

    @Override
    public float getSpreadChance()
    {
        return spreadChance;
    }

    public void loadFromJson(JsonElement jsonElement)
    {
        JsonObject jsonObject = jsonElement.getAsJsonObject();

        this.capacity = jsonObject.getAsJsonPrimitive("capacity").getAsInt();
        this.usage = jsonObject.getAsJsonPrimitive("usage").getAsInt();
        this.radius = jsonObject.getAsJsonPrimitive("radius").getAsInt();
        this.cropGrowthChance = jsonObject.getAsJsonPrimitive("crop_growth_chance").getAsFloat();
        this.flowerGrowthChance = jsonObject.getAsJsonPrimitive("flower_growth_chance").getAsFloat();
        this.spreadChance = jsonObject.getAsJsonPrimitive("spread_chance").getAsFloat();
    }

    public static class Serializer implements JsonSerializer<WateringCanMaterials>
    {
        @Override
        public JsonElement serialize(WateringCanMaterials instance, Type type, JsonSerializationContext context)
        {
            JsonObject jsonObject = new JsonObject();

            jsonObject.addProperty("capacity", instance.capacity);
            jsonObject.addProperty("usage", instance.usage);
            jsonObject.addProperty("radius", instance.radius);
            jsonObject.addProperty("crop_growth_chance", instance.cropGrowthChance);
            jsonObject.addProperty("flower_growth_chance", instance.flowerGrowthChance);
            jsonObject.addProperty("spread_chance", instance.spreadChance);

            return jsonObject;
        }
    }
}