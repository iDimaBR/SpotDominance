package com.github.idimabr.spotdominance.util;

import br.com.ystoreplugins.product.yclans.internal.ClanHolder;
import br.com.ystoreplugins.product.yclans.internal.ClanPlayerHolder;
import com.github.idimabr.spotdominance.SpotDominance;
import com.google.common.collect.Maps;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class WGUtil {

    public static String getRegion(Player player){
        final RegionContainer regionContainer = WorldGuardPlugin.inst().getRegionContainer();
        return regionContainer.get(player.getWorld())
                .getApplicableRegions(player.getLocation())
                .getRegions()
                .stream()
                .map(ProtectedRegion::getId)
                .findFirst()
                .orElse(null);
    }

    public static List<Player> getPlayersInRegion(String regionName){
        final List<Player> regionPlayers = new ArrayList<>();
        final RegionContainer regionContainer = WorldGuardPlugin.inst().getRegionContainer();
        for (Player player : Bukkit.getOnlinePlayers()) {
            final RegionManager regionManager = regionContainer.get(player.getWorld());
            for (ProtectedRegion region : regionManager.getApplicableRegions(player.getLocation())) {
                if(region.getId().equalsIgnoreCase(regionName)){
                    regionPlayers.add(player);
                }
            }
        }
        return regionPlayers;
    }

    public static int getAmountPlayersInRegion(String regionName){
        return getPlayersInRegion(regionName).size();
    }

    public static List<Map.Entry<String, Integer>> getClansInRegion(String regionName){
        final Map<String, Integer> clanCount = Maps.newConcurrentMap();

        for (Player player : getPlayersInRegion(regionName)) {
            final ClanPlayerHolder clanPlayer = SpotDominance.getPlugin().getApi().getPlayer(player);
            final ClanHolder clan = clanPlayer.getClan();
            if(clan == null) continue;

            clanCount.put(clan.getTag(), clanCount.getOrDefault(clan.getTag(), 0) + 1);
        }
        return clanCount.entrySet().stream().sorted((e1, e2) -> Integer.compare(e2.getValue(), e1.getValue())).collect(Collectors.toList());
    }

    public static Map.Entry<String, Integer> getDominationClan(String region){
        final List<Map.Entry<String, Integer>> clans = getClansInRegion(region);
        if(clans.size() < 2) return null;

        final Map.Entry<String, Integer> clan1 = clans.get(0);
        final Map.Entry<String, Integer> clan2 = clans.get(1);
        if(clan1.getValue() == clan2.getValue()) return null;

        return clan1;
    }
}
