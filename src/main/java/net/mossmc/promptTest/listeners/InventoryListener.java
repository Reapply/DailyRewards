package net.mossmc.promptTest.listeners;

import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.mossmc.promptTest.PromptTest;
import net.mossmc.promptTest.gui.DailyCrateGUI;
import net.mossmc.promptTest.managers.RewardItem;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;

public class InventoryListener implements Listener {
    private final PromptTest plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();
    private final LegacyComponentSerializer legacySerializer = LegacyComponentSerializer.legacySection();
    private final Map<Player, DailyCrateGUI> activeGUIs = new HashMap<>();

    public InventoryListener(PromptTest plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;

        Player player = (Player) event.getWhoClicked();
        String expectedTitle = legacySerializer.serialize(miniMessage.deserialize("<gold>Daily Reward Crate"));
        if (!event.getView().getTitle().equals(expectedTitle)) return;

        event.setCancelled(true);

        DailyCrateGUI gui = activeGUIs.get(player);
        if (gui == null) return;

        if (gui.isAnimating()) {
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 0.5f, 0.5f);
            return;
        }

        int slot = event.getRawSlot();
        if (!gui.isValidSlot(slot)) return;

        RewardItem reward = gui.getReward(slot);
        if (reward == null) return;

        startRewardSelectionAnimation(player, gui, slot, reward);
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        if (!(event.getPlayer() instanceof Player)) return;

        Player player = (Player) event.getPlayer();
        String expectedTitle = legacySerializer.serialize(miniMessage.deserialize("<gold>Daily Reward Crate"));
        if (!event.getView().getTitle().equals(expectedTitle)) return;

        DailyCrateGUI gui = activeGUIs.remove(player);
        if (gui != null) gui.cleanup();
    }

    private void startRewardSelectionAnimation(Player player, DailyCrateGUI gui, int selectedSlot, RewardItem reward) {
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 2.0f);

        new BukkitRunnable() {
            int animTick = 0;

            @Override
            public void run() {
                animTick++;

                if (animTick <= 15) {
                    int[] allRewardSlots = {10, 11, 12, 14, 15, 16};
                    for (int slot : allRewardSlots) {
                        if (slot != selectedSlot && gui.isValidSlot(slot)) {
                            gui.getInventory().setItem(slot, createDimmedItem());
                        }
                    }

                    if (animTick % 4 < 2) {
                        gui.getInventory().setItem(selectedSlot, createHighlightedItem(reward));
                    } else {
                        gui.getInventory().setItem(selectedSlot, reward.getDisplayItem());
                    }

                    if (animTick % 5 == 0) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_CHIME, 0.3f, 1.5f);
                    }
                } else {
                    gui.getInventory().setItem(selectedSlot, createSelectedItem(reward));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);

                    try {
                        player.spawnParticle(Particle.HAPPY_VILLAGER, player.getLocation().add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
                    } catch (Exception ignored) {}

                    new BukkitRunnable() {
                        @Override
                        public void run() {
                            giveReward(player, reward);
                            plugin.getCooldownManager().setCooldown(player.getUniqueId());
                            player.closeInventory();
                            player.sendMessage(miniMessage.deserialize("<green>✦ You have claimed your daily reward! ✦"));
                        }
                    }.runTaskLater(plugin, 20L);

                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 2L);
    }

    private ItemStack createDimmedItem() {
        ItemStack item = new ItemStack(Material.GRAY_STAINED_GLASS_PANE);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<dark_gray>Not Selected")));
            item.setItemMeta(meta);
        }
        return item;
    }

    private ItemStack createHighlightedItem(RewardItem reward) {
        ItemStack original = reward.getDisplayItem();
        ItemStack highlighted = original.clone();
        ItemMeta meta = highlighted.getItemMeta();
        if (meta != null) {
            String originalName = meta.getDisplayName();
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<yellow>» ")) + originalName + legacySerializer.serialize(miniMessage.deserialize(" <yellow>«")));
            highlighted.setItemMeta(meta);
        }
        return highlighted;
    }

    private ItemStack createSelectedItem(RewardItem reward) {
        ItemStack original = reward.getDisplayItem();
        ItemStack selected = original.clone();
        ItemMeta meta = selected.getItemMeta();
        if (meta != null) {
            String originalName = meta.getDisplayName();
            meta.setDisplayName(legacySerializer.serialize(miniMessage.deserialize("<green>✓ SELECTED: ")) + originalName);
            selected.setItemMeta(meta);
        }
        return selected;
    }

    private void giveReward(Player player, RewardItem reward) {
        reward.give(player);
    }

    public void registerGUI(Player player, DailyCrateGUI gui) {
        activeGUIs.put(player, gui);
    }
}