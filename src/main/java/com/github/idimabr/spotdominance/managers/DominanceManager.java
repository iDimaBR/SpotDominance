package com.github.idimabr.spotdominance.managers;

import com.github.idimabr.spotdominance.SpotDominance;
import com.github.idimabr.spotdominance.model.Dominance;
import com.github.idimabr.spotdominance.util.ConfigUtil;
import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.ConfigurationSection;

import java.util.List;
import java.util.Map;

@RequiredArgsConstructor @Getter
public class DominanceManager {

    private Map<String, Dominance> cache = Maps.newHashMap();
    private final ConfigUtil config;

    public void load(){
        final ConfigurationSection main = config.getConfigurationSection("Dominance");
        for (String id : main.getKeys(false)) {
            final ConfigurationSection domain = main.getConfigurationSection(id);

            final String region = domain.getString("region");
            final String name = domain.getString("name");

            final int minMembers = domain.getInt("settings.min_members");
            final int rewardTime = domain.getInt("settings.reward_time");
            final int dominanceTime = domain.getInt("settings.dominance_time");
            final int disputeTime = domain.getInt("settings.dispute_time");

            final List<String> rewardList = domain.getStringList("rewards");

            final Dominance dominance = new Dominance(
                    id,
                    region,
                    name,
                    minMembers,
                    rewardTime,
                    dominanceTime,
                    disputeTime,
                    rewardTime,
                    dominanceTime,
                    disputeTime,
                    rewardList,
                    null
            );

            cache.put(id, dominance);
            System.out.println("Dominio '" + id + "' carregado em cache.");
        }
    }

}
