package net.mossmc.promptTest.gui;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.mossmc.promptTest.PromptTest;
import net.mossmc.promptTest.managers.RewardItem;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.*;

public class DailyCrateGUI {
    private final PromptTest plugin;
    private final Player player;
    private final Inventory inventory;
    private final Map<Integer, RewardItem> slotRewards;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    private BukkitTask animationTask;
    private BukkitTask revealTask;
    private boolean isAnimating = false;
    private boolean isRevealed = false;
    private int animationTick = 0;
    private final Random random = new Random();
    private final Material[] animationMaterials = {
            Material.YELLOW_STAINED_GLASS_PANE,
            Material.ORANGE_STAINED_GLASS_PANE,
            Material.RED_STAINED_GLASS_PANE,
            Material.PURPLE_STAINED_GLASS_PANE,
            Material.BLUE_STAINED_GLASS_PANE,
            Material.CYAN_STAINED_GLASS_PANE,
            Material.LIME_STAINED_GLASS_PANE
    };

    public DailyCrateGUI(PromptTest plugin, Player player) {
        this.plugin = plugin;
        this.player = player;
        String title = legacySerializer.serialize(miniMessage.deserialize("<gold>Daily Reward Crate"));
        this.inventory = Bukkit.createInventory(null, 27, title);
        this.slotRewards = new HashMap<>();
        setupGUI();
    }

    private void setupGUI() {
        ItemStack filler = createFillerItem();
        for (int i = 0; i < 27; i++) {
            inventory.setItem(i, filler);
        }

        List<RewardItem> rewards = plugin.getRewardManager().getRandomRewards(6);
        int[] rewardSlots = {10, 11, 12, 14, 15, 16};

        for (int i = 0; i < Math.min(rewards.size(), rewardSlots.length); i++) {
            int slot = rewardSlots[i];
            RewardItem reward = rewards.get(i);
            slotRewards.put(slot, reward);
        }

        ItemStack infoItem = createAnimatedInfoItem();
        inventory.setItem(13, infoItem);
        startEntranceAnimation();
    }

    private void startEntranceAnimation() {
        isAnimating = true;
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 0.8f);

        animationTask = new BukkitRunnable() {
            @Override
            public void run() {
                animationTick++;

                if (animationTick <= 20) {
                    animateBorder();
                } else if (animationTick <= 40) {
                    animateSlotReveal();
                } else if (animationTick <= 80) {
                    animateMystery();
                } else {
                    revealRewards();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private void animateBorder() {
        int[] borderSlots = {0, 1, 2, 3, 4, 5, 6, 7, 8, 17, 26, 25, 24, 23, 22, 21, 20, 19, 18, 9};
        int wavePosition = animationTick % borderSlots.length;

        for (int slot : borderSlots) {
            inventory.setItem(slot, createFillerItem());
        }

        for (int i = 0; i < borderSlots.length; i++) {
            int slot = borderSlots[i];
            if (Math.abs(i - wavePosition) <= 2) {
                inventory.setItem(slot, createWaveItem(Material.YELLOW_STAINED_GLASS, "<gold>✦"));
            }
        }

        if (animationTick % 10 == 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 0.3f, 1.5f + (animationTick * 0.05f));
        }
    }

    private void animateSlotReveal() {
        int[] rewardSlots = {10, 11, 12, 14, 15, 16};
        int currentSlot = (animationTick - 21) / 3;

        if (currentSlot < rewardSlots.length) {
            int slot = rewardSlots[currentSlot];
            inventory.setItem(slot, createMysteryItem());
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.5f, 1.0f);
        }
    }

    private void animateMystery() {
        int[] rewardSlots = {10, 11, 12, 14, 15, 16};

        for (int slot : rewardSlots) {
            Material randomMaterial = animationMaterials[random.nextInt(animationMaterials.length)];
            ItemStack mysteryItem = createCyclingMysteryItem(randomMaterial);
            inventory.setItem(slot, mysteryItem);
        }

        int timeLeft = 4 - ((animationTick - 40) / 10);
        inventory.setItem(13, createCountdownInfoItem(timeLeft));

        if (animationTick % 4 == 0) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_HAT, 0.2f, 1.5f);
        }
    }

    private void revealRewards() {
        isAnimating = false;
        isRevealed = true;

        player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 0.8f, 1.2f);
        player.playSound(player.getLocation(), Sound.BLOCK_ANVIL_LAND, 0.3f, 2.0f);

        int[] rewardSlots = {10, 11, 12, 14, 15, 16};

        revealTask = new BukkitRunnable() {
            int revealIndex = 0;

            @Override
            public void run() {
                if (revealIndex < rewardSlots.length) {
                    int slot = rewardSlots[revealIndex];
                    RewardItem reward = slotRewards.get(slot);
                    if (reward != null) {
                        inventory.setItem(slot, reward.getDisplayItem());
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.6f, 1.0f + (revealIndex * 0.1f));
                    }
                    revealIndex++;
                } else {
                    inventory.setItem(13, createFinalInfoItem());
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 1.0f, 2.0f);
                    startIdleAnimation();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 8L);
    }

    private void startIdleAnimation() {
        animationTask = new BukkitRunnable() {
            int glowTick = 0;

            @Override
            public void run() {
                if (!isInventoryOpen()) {
                    this.cancel();
                    return;
                }

                glowTick++;

                if (glowTick % 20 == 0) {
                    int[] borderSlots = {0, 2, 6, 8, 18, 20, 24, 26};
                    int randomSlot = borderSlots[random.nextInt(borderSlots.length)];
                    inventory.setItem(randomSlot, createSparkleItem());

                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        if (isInventoryOpen()) {
                            inventory.setItem(randomSlot, createFillerItem());
                        }
                    }, 10L);
                }

                if (glowTick % 60 == 0) {
                    for (int i = 0; i <= 8; i++) {
                        if (i != 4) {
                            inventory.setItem(i, createFillerItem());
                        }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 4L);
    }

    private boolean isInventoryOpen() {
        return player.getOpenInventory().getTopInventory().equals(inventory);
    }

    private ItemStack createFillerItem() {
        ItemStack item = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(" ");
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createWaveItem(Material material, String symbol) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize(symbol)));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createMysteryItem() {
        ItemStack item = new ItemStack(Material.GRAY_SHULKER_BOX);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<gray>???</gray>")));
            meta.setLore(List.of(legacySerializer.serialize(miniMessage.deserialize("<dark_gray>Preparing reward..."))));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createCyclingMysteryItem(Material material) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<rainbow>???</rainbow>")));
            meta.setLore(List.of(legacySerializer.serialize(miniMessage.deserialize("<gray>Rolling for reward..."))));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createAnimatedInfoItem() {
        ItemStack item = new ItemStack(Material.ENDER_CHEST);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<gold>Opening Daily Crate...")));
            meta.setLore(List.of(
                    legacySerializer.serialize(miniMessage.deserialize("<gray>Please wait while we")),
                    legacySerializer.serialize(miniMessage.deserialize("<gray>prepare your rewards!"))
            ));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createCountdownInfoItem(int timeLeft) {
        ItemStack item = new ItemStack(Material.CLOCK);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<yellow>Revealing in " + timeLeft + "...")));
            meta.setLore(List.of(legacySerializer.serialize(miniMessage.deserialize("<gray>Rolling for the best rewards!"))));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createFinalInfoItem() {
        ItemStack item = new ItemStack(Material.CHEST);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<gold>Daily Reward Crate")));
            List<String> lore = List.of(
                    "<green>Choose your reward!",
                    "<gray>Click on one of the glowing",
                    "<gray>items to claim your reward!",
                    "",
                    "<red>You can only choose ONE!"
            ).stream().map(line -> legacySerializer.serialize(miniMessage.deserialize(line))).toList();
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createSparkleItem() {
        ItemStack item = new ItemStack(Material.YELLOW_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<yellow>✦")));
            item.setItemMeta(meta);
        }
        return item;
    }

    public void openGUI() {
        player.openInventory(inventory);
    }

    public boolean isValidSlot(int slot) {
        return isRevealed && slotRewards.containsKey(slot);
    }

    public RewardItem getReward(int slot) {
        return slotRewards.get(slot);
    }

    public Inventory getInventory() {
        return inventory;
    }

    public boolean isAnimating() {
        return isAnimating;
    }

    public void cleanup() {
        if (animationTask != null && !animationTask.isCancelled()) {
            animationTask.cancel();
        }
        if (revealTask != null && !revealTask.isCancelled()) {
            revealTask.cancel();
        }
    }
}