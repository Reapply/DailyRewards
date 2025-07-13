package net.mossmc.promptTest;

import net.mossmc.promptTest.commands.DailyCommand;
import net.mossmc.promptTest.listeners.InventoryListener;
import net.mossmc.promptTest.managers.CooldownManager;
import net.mossmc.promptTest.managers.RewardManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class PromptTest extends JavaPlugin {

    private CooldownManager cooldownManager;
    private RewardManager rewardManager;
    private InventoryListener inventoryListener;

    @Override
    public void onEnable() {
        cooldownManager = new CooldownManager(this);
        rewardManager = new RewardManager();
        inventoryListener = new InventoryListener(this);
        getCommand("dailycrate").setExecutor(new DailyCommand(this));
        getServer().getPluginManager().registerEvents(inventoryListener, this);
        getLogger().info("Daily Reward Crate System enabled with animations!");
    }

    @Override
    public void onDisable() {
        if (cooldownManager != null) {
            cooldownManager.saveCooldowns();
        }
        getLogger().info("Daily Reward Crate System disabled!");
    }

    public CooldownManager getCooldownManager() {
        return cooldownManager;
    }

    public RewardManager getRewardManager() {
        return rewardManager;
    }

    public InventoryListener getInventoryListener() {
        return inventoryListener;
    }
}
