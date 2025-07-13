package net.mossmc.promptTest.managers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class RewardRegistry {
    private static final List<RewardItem> REWARDS = new ArrayList<>();

    static {
        registerCommonRewards();
        registerUncommonRewards();
        registerRareRewards();
        registerEpicRewards();
        registerLegendaryRewards();
        registerSpecialRewards();
    }

    private static void registerCommonRewards() {
        REWARDS.add(new RewardItem(Material.IRON_INGOT, 16,
                "<white>16x Iron Ingots", "<gray>Useful crafting material!",
                "<white>✦ You received 16x Iron Ingots! ✦"));

        REWARDS.add(new RewardItem(Material.COAL, 24,
                "<dark_gray>24x Coal", "<gray>Essential fuel for smelting!",
                "<dark_gray>✦ You received 24x Coal! ✦"));

        REWARDS.add(new RewardItem(Material.ARROW, 32,
                "<white>32x Arrows", "<gray>Perfect for your bow!",
                "<white>✦ You received 32x Arrows! ✦"));

        REWARDS.add(new RewardItem(Material.COOKED_BEEF, 20,
                "<brown>20x Cooked Beef", "<gray>Delicious and nutritious!",
                "<brown>✦ You received 20x Cooked Beef! ✦"));

        REWARDS.add(new RewardItem(Material.OAK_LOG, 32,
                "<brown>32x Oak Logs", "<gray>Building materials galore!",
                "<brown>✦ You received 32x Oak Logs! ✦"));

        REWARDS.add(new RewardItem(Material.COBBLESTONE, 64,
                "<gray>64x Cobblestone", "<gray>The foundation of every build!",
                "<gray>✦ You received 64x Cobblestone! ✦"));
    }

    private static void registerUncommonRewards() {
        REWARDS.add(new RewardItem(Material.GOLD_INGOT, 8,
                "<yellow>8x Gold Ingots", "<gray>Shiny golden bars!",
                "<yellow>✦ You received 8x Gold Ingots! ✦"));

        REWARDS.add(new RewardItem(Material.REDSTONE, 16,
                "<red>16x Redstone", "<gray>Power your contraptions!",
                "<red>✦ You received 16x Redstone! ✦"));

        REWARDS.add(new RewardItem(Material.EXPERIENCE_BOTTLE, 10,
                "<green>10x XP Bottles", "<gray>Instant experience!",
                "<green>✦ You received 10x XP Bottles! ✦"));

        REWARDS.add(new RewardItem(Material.EMERALD, 8,
                "<green>8x Emerald", "<gray>Valuable trading currency!",
                "<green>✦ You received 8x Emeralds! ✦"));
    }

    private static void registerRareRewards() {
        REWARDS.add(new RewardItem(Material.DIAMOND, 5,
                "<aqua>5x Diamond", "<gray>Shiny precious gems!",
                "<aqua>✦ You received 5x Diamonds! ✦"));

        REWARDS.add(new RewardItem(Material.ENDER_PEARL, 3,
                "<dark_green>3x Ender Pearls", "<gray>Teleportation magic!",
                "<dark_green>✦ You received 3x Ender Pearls! ✦"));

        REWARDS.add(new RewardItem(Material.BLAZE_ROD, 5,
                "<yellow>5x Blaze Rods", "<gray>Fiery brewing ingredients!",
                "<yellow>✦ You received 5x Blaze Rods! ✦"));

        REWARDS.add(new RewardItem(Material.ENCHANTED_BOOK, 1,
                "<purple>Random Enchanted Book", "<gray>Knowledge is power!",
                "<purple>✦ You received an Enchanted Book! ✦"));
    }

    private static void registerEpicRewards() {
        REWARDS.add(new RewardItem(Material.NETHERITE_INGOT, 1,
                "<dark_red>1x Netherite Ingot", "<gray>The ultimate crafting material!",
                "<dark_red>✦ You received a Netherite Ingot! ✦"));

        REWARDS.add(new RewardItem(Material.ELYTRA, 1,
                "<light_purple>Elytra", "<gray>Wings of freedom!",
                "<light_purple>✦ You received an Elytra! ✦"));

        REWARDS.add(new RewardItem(Material.TOTEM_OF_UNDYING, 1,
                "<gold>Totem of Undying", "<gray>Cheat death itself!",
                "<gold>✦ You received a Totem of Undying! ✦"));
    }

    private static void registerLegendaryRewards() {
        REWARDS.add(new RewardItem(Material.NETHER_STAR, 1,
                "<white>Nether Star", "<gray>Power of the Wither!",
                "<white>✦ You received a Nether Star! ✦"));

        REWARDS.add(new RewardItem(Material.ENCHANTED_GOLDEN_APPLE, 1,
                "<gold>Enchanted Golden Apple", "<gray>The ultimate healing fruit!",
                "<gold>✦ You received an Enchanted Golden Apple! ✦"));
    }

    private static void registerSpecialRewards() {
        ItemStack moneyDisplay = RewardFactory.createDisplayItem(Material.GOLD_INGOT, 1,
                "<yellow>$1,000", "<gray>Cold hard cash!");
        REWARDS.add(new RewardItem(moneyDisplay, "money", 1000,
                "<yellow>✦ You received $1,000! ✦<gray> (Economy integration needed)",
                (player, reward) -> {
                    player.sendMessage(MiniMessage.miniMessage().deserialize(
                            "<gray>(Economy integration not implemented)"));
                }));

        REWARDS.add(createRandomPotionReward());
    }

    private static RewardItem createRandomPotionReward() {
        PotionEffectType[] effects = {
                PotionEffectType.SPEED, PotionEffectType.STRENGTH, PotionEffectType.REGENERATION,
                PotionEffectType.FIRE_RESISTANCE, PotionEffectType.WATER_BREATHING,
                PotionEffectType.NIGHT_VISION, PotionEffectType.INVISIBILITY,
                PotionEffectType.JUMP_BOOST, PotionEffectType.RESISTANCE
        };

        return new RewardItem(
                RewardFactory.createDisplayItem(Material.POTION, 1,
                        "<light_purple>Random Effect Potion",
                        "<gray>A mysterious potion with a random beneficial effect!"),
                "random_potion", 1,
                "<light_purple>✦ You received a Random Effect Potion! ✦",
                (player, reward) -> {
                    Random random = new Random();
                    PotionEffectType selectedEffect = effects[random.nextInt(effects.length)];

                    ItemStack potion = new ItemStack(Material.POTION, 1);
                    PotionMeta meta = (PotionMeta) potion.getItemMeta();
                    if (meta != null) {
                        meta.addCustomEffect(new PotionEffect(selectedEffect, 3600, 1), true);
                        potion.setItemMeta(meta);
                    }

                    player.getInventory().addItem(potion);
                }
        );
    }

    public static List<RewardItem> getAllRewards() {
        return new ArrayList<>(REWARDS);
    }

    public static List<RewardItem> getRandomRewards(int count) {
        List<RewardItem> available = new ArrayList<>(REWARDS);
        List<RewardItem> selected = new ArrayList<>();
        Random random = new Random();

        while (available.size() < count) {
            available.addAll(REWARDS);
        }

        for (int i = 0; i < count && !available.isEmpty(); i++) {
            int index = random.nextInt(available.size());
            selected.add(available.remove(index));
        }

        return selected;
    }
}