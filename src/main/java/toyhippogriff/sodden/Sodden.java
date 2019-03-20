package toyhippogriff.sodden;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.tag.TagRegistry;
import net.minecraft.item.Item;
import net.minecraft.tag.Tag;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import toyhippogriff.sodden.config.Configuration;
import toyhippogriff.sodden.item.WateringCanItem;
import toyhippogriff.sodden.item.WateringCanMaterials;

public class Sodden implements ModInitializer
{
    public static final Logger LOG = LogManager.getLogger("Sodden");

    public static final Item WOODEN_WATERING_CAN = new WateringCanItem(WateringCanMaterials.WOOD);
    public static final Item STONE_WATERING_CAN = new WateringCanItem(WateringCanMaterials.STONE);
    public static final Item IRON_WATERING_CAN = new WateringCanItem(WateringCanMaterials.IRON);
    public static final Item DIAMOND_WATERING_CAN = new WateringCanItem(WateringCanMaterials.DIAMOND);
    public static final Item GOLDEN_WATERING_CAN = new WateringCanItem(WateringCanMaterials.GOLD);

    public static final Tag<Item> WATERING_CANS = TagRegistry.item(new Identifier("sodden", "watering_cans"));

    @Override
    public void onInitialize()
    {
        Configuration.load();

        Registry.register(Registry.ITEM, new Identifier("sodden", "wooden_watering_can"), WOODEN_WATERING_CAN);
        Registry.register(Registry.ITEM, new Identifier("sodden", "stone_watering_can"), STONE_WATERING_CAN);
        Registry.register(Registry.ITEM, new Identifier("sodden", "iron_watering_can"), IRON_WATERING_CAN);
        Registry.register(Registry.ITEM, new Identifier("sodden", "diamond_watering_can"), DIAMOND_WATERING_CAN);
        Registry.register(Registry.ITEM, new Identifier("sodden", "golden_watering_can"), GOLDEN_WATERING_CAN);
    }
}