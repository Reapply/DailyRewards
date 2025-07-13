package net.mossmc.promptTest.managers;

import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.function.BiConsumer;

public class RewardItem {
    private final ItemStack displayItem;
    private final String type;
    private final int amount;
    private final BiConsumer<Player, RewardItem> rewardAction;
    private final String giveMessage;

    public RewardItem(ItemStack displayItem, String type, int amount,
                      BiConsumer<Player, RewardItem> rewardAction, String giveMessage) {
        this.displayItem = displayItem;
        this.type = type;
        this.amount = amount;
        this.rewardAction = rewardAction;
        this.giveMessage = giveMessage;
    }

    public RewardItem(Material material, int amount, String displayName, String lore, String giveMessage) {
        this.displayItem = RewardFactory.createDisplayItem(material, amount, displayName, lore);
        this.type = material.name().toLowerCase();
        this.amount = amount;
        this.rewardAction = (player, reward) -> {
            player.getInventory().addItem(new ItemStack(material, amount));
        };
        this.giveMessage = giveMessage;
    }

    public RewardItem(ItemStack displayItem, String type, int amount, String giveMessage,
                      BiConsumer<Player, RewardItem> customAction) {
        this.displayItem = displayItem;
        this.type = type;
        this.amount = amount;
        this.rewardAction = customAction;
        this.giveMessage = giveMessage;
    }

    public void give(Player player) {
        rewardAction.accept(player, this);

        MiniMessage mini = MiniMessage.miniMessage();
        player.sendMessage(mini.deserialize(giveMessage));
        player.playSound(player.getLocation(), org.bukkit.Sound.ENTITY_PLAYER_LEVELUP, 0.7f, 1.5f);
    }

    // Getters
    public ItemStack getDisplayItem() { return displayItem.clone(); }
    public String getType() { return type; }
    public int getAmount() { return amount; }
}
