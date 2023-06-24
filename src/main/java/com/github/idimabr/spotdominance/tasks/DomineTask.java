package com.github.idimabr.spotdominance.tasks;

import br.com.ystoreplugins.product.yclans.ClanAPIHolder;
import br.com.ystoreplugins.product.yclans.internal.ClanHolder;
import br.com.ystoreplugins.product.yclans.internal.ClanPlayerHolder;
import com.github.idimabr.spotdominance.SpotDominance;
import com.github.idimabr.spotdominance.managers.DominanceManager;
import com.github.idimabr.spotdominance.model.Dominance;
import com.github.idimabr.spotdominance.util.ConfigUtil;
import com.github.idimabr.spotdominance.util.WGUtil;
import com.google.common.collect.Maps;
import com.sk89q.worldguard.bukkit.WGBukkit;
import com.sk89q.worldguard.protection.managers.RegionManager;
import lombok.AllArgsConstructor;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

@AllArgsConstructor
public class DomineTask extends BukkitRunnable {

    private ConfigUtil config;
    private DominanceManager manager;
    private ClanAPIHolder api;

    @Override
    public void run() {
        final Map<String, Dominance> cache = manager.getCache();

        for (Dominance domain : cache.values()) {
            final String id = domain.getId();
            final String name = domain.getName();
            final String region = domain.getRegion();

            final int min = domain.getMinMembers();
            if(WGUtil.getAmountPlayersInRegion(region) < min)continue;

            final Map.Entry<String, Integer> entryClan = WGUtil.getDominationClan(region);
            if(entryClan == null) continue;

            final String topClanTag = entryClan.getKey();
            final Integer topClanMembers = entryClan.getValue();

            final String clanDominated = domain.getClan();

            // DISPUTA PELA ÁREA
            if (clanDominated != null && !topClanTag.equals(clanDominated)) {
                domain.dispute();

                for (Player player : WGUtil.getPlayersInRegion(region)) {
                    sendActionBar(player,
                            config.getString("Dominance." + id + ".messages.actionbar.dispute")
                                    .replace("{time}", domain.getCounterDispute()+"")
                    );
                }

                if(domain.getCounterDispute() == 0){
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        config.getStringList("Dominance." + id + ".messages.disputed")
                                .stream()
                                .map($ -> $.replace("&","§")
                                        .replace("{name}", name)
                                        .replace("{old_clan}", domain.getClan() == null ? "Nenhum" : domain.getClan())
                                        .replace("{clan}", topClanTag)
                                        .replace("{size}", topClanMembers +"")
                                ).forEach(player::sendMessage);
                    }
                    domain.setClan(null);
                }
            }

            if(clanDominated == null){
                for (Player player : WGUtil.getPlayersInRegion(region)) {
                    sendActionBar(player,
                            config.getString("Dominance." + id + ".messages.actionbar.normal")
                                    .replace("&","§")
                                    .replace("{clan}", topClanTag)
                                    .replace("{name}", name)
                                    .replace("{size}", topClanMembers +"")
                                    .replace("{time}", domain.getCounterDomine()+"")
                    );
                }
            }

            if(domain.getCounterDomine() == 0 && clanDominated == null){
                domain.setClan(topClanTag);
                for (Player player : Bukkit.getOnlinePlayers()) {
                    config.getStringList("Dominance." + id + ".messages.dominated")
                            .stream()
                            .map($ -> $.replace("&","§")
                                    .replace("{name}", name)
                                    .replace("{clan}", topClanTag)
                                    .replace("{size}", topClanMembers +"")
                            ).forEach(player::sendMessage);
                }
            }

            if(topClanTag.equals(clanDominated)){
                for (Player player : WGUtil.getPlayersInRegion(region)) {
                    sendActionBar(player,
                            config.getString("Dominance." + id + ".messages.actionbar.dominated")
                                    .replace("&","§")
                                    .replace("{clan}", topClanTag)
                                    .replace("{name}", name)
                    );
                }
            }

            if(clanDominated != null && domain.getCounterReward() == 0){
                for (Player player : WGUtil.getPlayersInRegion(region)) {

                    final ClanHolder clan = api.getPlayer(player).getClan();
                    if(clan != null && clan.getTag().equals(clanDominated)){
                        for (String reward : domain.getRewards()) {
                            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), reward.replace("{player}", player.getName()));
                        }
                    }

                    config.getStringList("Dominance." + id + ".messages.reward-receive")
                            .stream()
                            .map($ -> $.replace("&","§")
                                    .replace("{name}", name)
                                    .replace("{clan}", clanDominated)
                            ).forEach(player::sendMessage);
                }
            }

            domain.counting();
        }
    }

    public void sendActionBar(Player player, String message){
        PacketPlayOutChat packet = new PacketPlayOutChat(IChatBaseComponent.ChatSerializer.a("{\"text\":\"" + message.replace("&","§") + "\"}"), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
