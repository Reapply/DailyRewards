package net.mossmc.promptTest.managers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class RewardFactory {
    private static final MiniMessage mini = MiniMessage.miniMessage();
    private static final LegacyComponentSerializer legacy = LegacyComponentSerializer.legacySection();

    public static ItemStack createDisplayItem(Material material, int amount, String name, String loreLine) {
        return createDisplayItem(material, amount, name, List.of(loreLine));
    }

    public static ItemStack createDisplayItem(Material material, int amount, String name, List<String> lore) {
        ItemStack item = new ItemStack(material, amount);
        ItemMeta meta = item.getItemMeta();

        if (meta != null) {
            meta.setDisplayName(legacy.serialize(mini.deserialize(name)));

            List<String> convertedLore = lore.stream()
                    .map(line -> legacy.serialize(mini.deserialize(line)))
                    .toList();

            meta.setLore(convertedLore);
            meta.addEnchant(Enchantment.LURE, 1, true);
            meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
            item.setItemMeta(meta);
        }

        return item;
    }
}