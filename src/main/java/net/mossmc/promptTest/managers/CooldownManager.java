package net.mossmc.promptTest.managers;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class CooldownManager {
    private final JavaPlugin plugin;
    private final Map<UUID, Long> cooldowns;
    private final Map<UUID, Integer> claimCounts;
    private final File dataFile;
    private FileConfiguration dataConfig;

    public CooldownManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.cooldowns = new HashMap<>();
        this.claimCounts = new HashMap<>();
        this.dataFile = new File(plugin.getDataFolder(), "data.yml");
        loadCooldowns();
    }

    public boolean isOnCooldown(UUID playerId) {
        if (!cooldowns.containsKey(playerId)) {
            return false;
        }

        long lastClaim = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        long cooldownTime = 24 * 60 * 60 * 1000; // 24 hours in milliseconds

        return (currentTime - lastClaim) < cooldownTime;
    }

    public void setCooldown(UUID playerId) {
        cooldowns.put(playerId, System.currentTimeMillis());
        claimCounts.put(playerId, claimCounts.getOrDefault(playerId, 0) + 1);
        saveCooldowns();
    }

    public void removeCooldown(UUID playerId) {
        cooldowns.remove(playerId);
        saveCooldowns();
    }

    public String getTimeLeft(UUID playerId) {
        if (!isOnCooldown(playerId)) {
            return "0s";
        }

        long lastClaim = cooldowns.get(playerId);
        long currentTime = System.currentTimeMillis();
        long cooldownTime = 24 * 60 * 60 * 1000; // 24 hours
        long timeLeft = cooldownTime - (currentTime - lastClaim);

        long hours = TimeUnit.MILLISECONDS.toHours(timeLeft);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(timeLeft) % 60;

        return hours + "h " + minutes + "m";
    }

    public int getTotalClaims(UUID playerId) {
        return claimCounts.getOrDefault(playerId, 0);
    }

    private void loadCooldowns() {
        if (!dataFile.exists()) {
            plugin.getDataFolder().mkdirs();
            try {
                dataFile.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Could not create data file!");
                return;
            }
        }

        dataConfig = YamlConfiguration.loadConfiguration(dataFile);

        if (dataConfig.contains("cooldowns")) {
            for (String key : dataConfig.getConfigurationSection("cooldowns").getKeys(false)) {
                UUID playerId = UUID.fromString(key);
                long cooldown = dataConfig.getLong("cooldowns." + key);
                cooldowns.put(playerId, cooldown);
            }
        }

        if (dataConfig.contains("claims")) {
            for (String key : dataConfig.getConfigurationSection("claims").getKeys(false)) {
                UUID playerId = UUID.fromString(key);
                int claims = dataConfig.getInt("claims." + key);
                claimCounts.put(playerId, claims);
            }
        }
    }

    public void saveCooldowns() {
        for (Map.Entry<UUID, Long> entry : cooldowns.entrySet()) {
            dataConfig.set("cooldowns." + entry.getKey().toString(), entry.getValue());
        }

        for (Map.Entry<UUID, Integer> entry : claimCounts.entrySet()) {
            dataConfig.set("claims." + entry.getKey().toString(), entry.getValue());
        }

        try {
            dataConfig.save(dataFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save data file!");
        }
    }
}
