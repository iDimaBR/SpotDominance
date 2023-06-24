package com.github.idimabr.spotdominance;

import br.com.ystoreplugins.product.yclans.ClanAPIHolder;
import com.github.idimabr.spotdominance.managers.DominanceManager;
import com.github.idimabr.spotdominance.tasks.DomineTask;
import com.github.idimabr.spotdominance.util.ConfigUtil;
import lombok.Getter;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

@Getter
public final class SpotDominance extends JavaPlugin {

    private static SpotDominance plugin;
    private ConfigUtil config;
    private DominanceManager manager;
    ClanAPIHolder api;

    @Override
    public void onLoad() {
        this.config = new ConfigUtil(this, "config");
    }

    @Override
    public void onEnable() {
        // Plugin startup logic
        this.plugin = this;

        loadHooks();
        loadManagers();
        loadTasks();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    private void loadHooks(){
        RegisteredServiceProvider<ClanAPIHolder> rsp = getServer().getServicesManager().getRegistration(ClanAPIHolder.class);
        if (rsp == null) {
            getLogger().warning("Dependencia yClans nao encontrada.");
            getServer().getPluginManager().disablePlugin(this);
        }else{
            api = rsp.getProvider();
        }
    }

    private void loadManagers(){
        this.manager = new DominanceManager(config);
        this.manager.load();
    }

    private void loadTasks(){
        new DomineTask(config, manager, api).runTaskTimer(this, 20L, 20L);
    }

    public static SpotDominance getPlugin(){
        return plugin;
    }
}
