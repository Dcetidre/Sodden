package toyhippogriff.sodden.config;

import com.google.gson.Gson;
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
    public static final JsonParser PARSER = new JsonParser();
    public static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    public static final File FILE = new File(FabricLoader.getInstance().getConfigDirectory(), "sodden.json");

    public static boolean CONSUME_WATER = false;
    public static boolean EXTINGUISH_FIRE = true;

    public static void load()
    {
        try(FileReader fileReader = new FileReader(FILE))
        {
            JsonObject jsonObject = PARSER.parse(fileReader).getAsJsonObject();

            JsonObject materialsJsonObject = jsonObject.getAsJsonObject("materials");

            WateringCanMaterials.WOOD.loadFromJson(materialsJsonObject.getAsJsonObject("wood"));
            WateringCanMaterials.STONE.loadFromJson(materialsJsonObject.getAsJsonObject("stone"));
            WateringCanMaterials.IRON.loadFromJson(materialsJsonObject.getAsJsonObject("iron"));
            WateringCanMaterials.DIAMOND.loadFromJson(materialsJsonObject.getAsJsonObject("diamond"));
            WateringCanMaterials.GOLD.loadFromJson(materialsJsonObject.getAsJsonObject("gold"));

            Configuration.CONSUME_WATER = jsonObject.getAsJsonPrimitive("consume_water").getAsBoolean();
            Configuration.EXTINGUISH_FIRE = jsonObject.getAsJsonPrimitive("extinguish_fire").getAsBoolean();
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

            materialsJsonObject.add("wood", GSON.toJsonTree(WateringCanMaterials.WOOD));
            materialsJsonObject.add("stone", GSON.toJsonTree(WateringCanMaterials.STONE));
            materialsJsonObject.add("iron", GSON.toJsonTree(WateringCanMaterials.IRON));
            materialsJsonObject.add("diamond", GSON.toJsonTree(WateringCanMaterials.DIAMOND));
            materialsJsonObject.add("gold", GSON.toJsonTree(WateringCanMaterials.GOLD));

            jsonObject.add("materials", materialsJsonObject);

            jsonObject.addProperty("consume_water", CONSUME_WATER);
            jsonObject.addProperty("extinguish_fire", EXTINGUISH_FIRE);

            GSON.toJson(jsonObject, fileWriter);
        }
        catch(IOException exception)
        {
            Sodden.LOG.warn("Failed to find or create configuration file");
        }
    }
}