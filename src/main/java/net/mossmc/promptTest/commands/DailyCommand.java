package net.mossmc.promptTest.commands;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.mossmc.promptTest.PromptTest;
import net.mossmc.promptTest.gui.DailyCrateGUI;
import net.mossmc.promptTest.listeners.InventoryListener;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DailyCommand implements CommandExecutor {
    private final PromptTest plugin;
    private final MiniMessage miniMessage = MiniMessage.miniMessage();

    public DailyCommand(PromptTest plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage(miniMessage.deserialize("<red>You must be a player to use this command!"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length > 0) {
            String subcommand = args[0].toLowerCase();

            switch (subcommand) {
                case "stats":
                    int claims = plugin.getCooldownManager().getTotalClaims(player.getUniqueId());
                    player.sendMessage(miniMessage.deserialize("<gold>✦ You have claimed " + claims + " daily crates total! ✦"));
                    player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1.0f, 1.0f);
                    return true;

                case "bypass":
                    if (!player.hasPermission("dailycrate.bypass")) {
                        player.sendMessage(miniMessage.deserialize("<red>You don't have permission to use this command!"));
                        return true;
                    }
                    plugin.getCooldownManager().removeCooldown(player.getUniqueId());
                    player.sendMessage(miniMessage.deserialize("<green>✦ Your daily crate cooldown has been bypassed! ✦"));
                    player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f);
                    return true;

                default:
                    player.sendMessage(miniMessage.deserialize("<red>Usage: /dailycrate [reload|stats|bypass]"));
                    return true;
            }
        }

        if (!canOpenCrate(player)) {
            return true;
        }

        player.sendMessage(miniMessage.deserialize("<gold>✦ Opening your daily crate... ✦"));
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0f, 1.0f);

        DailyCrateGUI gui = new DailyCrateGUI(plugin, player);

        InventoryListener listener = getInventoryListener();
        if (listener != null) {
            listener.registerGUI(player, gui);
        }

        gui.openGUI();

        return true;
    }

    private InventoryListener getInventoryListener() {
        return plugin.getInventoryListener();
    }

    private boolean canOpenCrate(Player player) {
        if (plugin.getCooldownManager().isOnCooldown(player.getUniqueId())) {
            String timeLeft = plugin.getCooldownManager().getTimeLeft(player.getUniqueId());
            player.sendMessage(miniMessage.deserialize("<red>⏰ You can open your next crate in " + timeLeft + "."));
            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASS, 1.0f, 0.5f);
            return false;
        }

        if (player.getInventory().firstEmpty() == -1) {
            player.sendMessage(miniMessage.deserialize("<red>📦 Your inventory is full! Please make some space first."));
            player.playSound(player.getLocation(), Sound.ENTITY_ITEM_BREAK, 1.0f, 1.0f);
            return false;
        }

        if (player.getLastDamage() > 0 && (System.currentTimeMillis() - player.getLastDamage()) < 10000) {
            player.sendMessage(miniMessage.deserialize("<red>⚔ You cannot open daily crates while in combat!"));
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_HURT, 1.0f, 1.0f);
            return false;
        }

        return true;
    }
}
