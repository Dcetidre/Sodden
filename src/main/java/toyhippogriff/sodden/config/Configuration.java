package toyhippogriff.sodden.config;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.fabricmc.loader.api.FabricLoader;
import toyhippogriff.sodden.Sodden;
import toyhippogriff.sodden.item.WateringCanMaterials;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class Configuration
{
    public static final File FILE = new File(FabricLoader.getInstance().getConfigDirectory(), "sodden.json");

    public static boolean CONSUME_WATER = false;
    public static boolean MOISTURIZE_FARMLAND = true;
    public static boolean EXTINGUISH_FIRE = true;
    public static boolean SPAWN_PARTICLES = true;

    public static void load()
    {
        try(FileReader fileReader = new FileReader(FILE))
        {
            JsonObject jsonObject = new JsonParser().parse(fileReader).getAsJsonObject();

            JsonObject materialsJsonObject = jsonObject.getAsJsonObject("materials");

            WateringCanMaterials.WOOD.deserialize(materialsJsonObject.getAsJsonObject("wood"));
            WateringCanMaterials.STONE.deserialize(materialsJsonObject.getAsJsonObject("stone"));
            WateringCanMaterials.IRON.deserialize(materialsJsonObject.getAsJsonObject("iron"));
            WateringCanMaterials.DIAMOND.deserialize(materialsJsonObject.getAsJsonObject("diamond"));
            WateringCanMaterials.GOLD.deserialize(materialsJsonObject.getAsJsonObject("gold"));

            Configuration.CONSUME_WATER = jsonObject.getAsJsonPrimitive("consume_water").getAsBoolean();
            Configuration.MOISTURIZE_FARMLAND = jsonObject.getAsJsonPrimitive("moisturize_farmland").getAsBoolean();
            Configuration.EXTINGUISH_FIRE = jsonObject.getAsJsonPrimitive("extinguish_fire").getAsBoolean();
            Configuration.SPAWN_PARTICLES = jsonObject.getAsJsonPrimitive("spawn_partciles").getAsBoolean();
        }
        catch(IOException exception)
        {
            Configuration.save();
        }
    }

    public static void save()
    {
        try(FileWriter fileWriter = new FileWriter(FILE))
        {
            JsonObject jsonObject = new JsonObject();

            JsonObject materialsJsonObject = new JsonObject();

            materialsJsonObject.add("wood", WateringCanMaterials.WOOD.serialize());
            materialsJsonObject.add("stone", WateringCanMaterials.STONE.serialize());
            materialsJsonObject.add("iron", WateringCanMaterials.IRON.serialize());
            materialsJsonObject.add("diamond", WateringCanMaterials.DIAMOND.serialize());
            materialsJsonObject.add("gold", WateringCanMaterials.GOLD.serialize());

            jsonObject.add("materials", materialsJsonObject);

            jsonObject.addProperty("consume_water", CONSUME_WATER);
            jsonObject.addProperty("moisturize_farmland", MOISTURIZE_FARMLAND);
            jsonObject.addProperty("extinguish_fire", EXTINGUISH_FIRE);
            jsonObject.addProperty("spawn_particles", SPAWN_PARTICLES);

            new GsonBuilder().setPrettyPrinting().create().toJson(jsonObject, fileWriter);
        }
        catch(IOException exception)
        {
            Sodden.LOG.warn("Failed to find or create configuration file");
        }
    }
}