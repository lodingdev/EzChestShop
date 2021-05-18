package me.deadlight.ezchestshop;
import com.bgsoftware.wildchests.api.WildChests;
import com.bgsoftware.wildchests.api.WildChestsAPI;
import com.bgsoftware.wildchests.api.handlers.ChestsManager;
import me.deadlight.ezchestshop.Commands.Ecsadmin;
import me.deadlight.ezchestshop.Commands.MainCommands;
import me.deadlight.ezchestshop.Listeners.BlockBreakListener;
import me.deadlight.ezchestshop.Listeners.ChestOpeningEvent;
import me.deadlight.ezchestshop.Listeners.PlayerLookingAtChestShop;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;

public final class EzChestShop extends JavaPlugin {

    private static EzChestShop plugin;
    private static FileConfiguration languages;

    private static Economy econ = null;

    public boolean integrationWildChests = false;
    public ChestsManager wchests = null;

    public static boolean protocollib = false;
    @Override
    public void onEnable() {
        plugin = this;
        logConsole("&c[&eEzChestShop&c] &aEnabling EzChestShop - version 1.2.7");
        saveDefaultConfig();
        // Plugin startup logic
        if (!setupEconomy() ) {

            logConsole("&c[&eEzChestShop&c] &4Cannot find vault and economy plugin. Self disabling... &ePlease note that you need vault and at least one economy plugin installed.");
            Bukkit.getPluginManager().disablePlugin(this);
        }

        if (getServer().getPluginManager().getPlugin("ProtocolLib") != null) {
            protocollib = true;
            logConsole("&c[&eEzChestShop&c] &aProtocollib is installed. Enabling holograms functionality.");
        } else {
            logConsole("&c[&eEzChestShop&c] &eProtocollib is not installed. Plugin will not support holograms.");
        }

        registerListeners();
        registerCommands();
        try {
            Utils.checkForConfigYMLupdate();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadLanguages();
        //metrics
        Metrics metrics = new Metrics(this, 10756);

        //integration boolean changer
        if (getServer().getPluginManager().getPlugin("WildChests") != null) {
            integrationWildChests = true;
            wchests = WildChestsAPI.getInstance().getChestsManager();
        }

    }

    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new ChestOpeningEvent(), this);
        getServer().getPluginManager().registerEvents(new PlayerLookingAtChestShop(), this);
        getServer().getPluginManager().registerEvents(new BlockBreakListener(), this);
    }
    private void registerCommands() {
        getCommand("ecs").setExecutor(new MainCommands());
        getCommand("ecsadmin").setExecutor(new Ecsadmin());

    }


    public void loadLanguages() {
        LanguageManager lm = new LanguageManager();
        File customConfigFile = new File(getDataFolder(), "languages.yml");
        if (!customConfigFile.exists()) {
            logConsole("&c[&eEzChestShop&c] &eGenerating language.yml file...");
            customConfigFile.getParentFile().mkdirs();
            saveResource("languages.yml", false);
            languages = YamlConfiguration.loadConfiguration(customConfigFile);
            lm.setLanguageConfig(languages);
            logConsole("&c[&eEzChestShop&c] &elanguage.yml successfully loaded");
        } else {
            languages = YamlConfiguration.loadConfiguration(customConfigFile);
            lm.setLanguageConfig(languages);
            logConsole("&c[&eEzChestShop&c] &elanguage.yml successfully loaded");
        }
    }





    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }


    public static EzChestShop getPlugin() {
        return plugin;
    }

    public void logConsole(String str) {
        getServer().getConsoleSender().sendMessage(Utils.color(str));
    }

    private boolean setupEconomy() {
        if (getServer().getPluginManager().getPlugin("Vault") == null) {
            return false;
        }
        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp == null) {
            return false;
        }
        econ = rsp.getProvider();
        return econ != null;
    }

    public static Economy getEconomy() {
        return econ;
    }

    public static FileConfiguration getLanguages() {
        return languages;
    }




}

