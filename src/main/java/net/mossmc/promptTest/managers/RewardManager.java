package net.mossmc.promptTest.managers;

import java.util.List;

public class RewardManager {

    public List<RewardItem> getRandomRewards(int count) {
        return RewardRegistry.getRandomRewards(count);
    }
}