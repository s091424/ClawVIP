package me.clayclaw.bukkit.vip;

import me.clayclaw.bukkit.vip.bridge.PlaceholderService;
import me.clayclaw.bukkit.vip.bstats.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.*;

public class ClawVIP extends JavaPlugin {

    private static ClawVIP instance;
    private Metrics metrics;

    private ServiceManager serviceManager;
    private static ConfigOption configOption;

    private File langConfigFile;
    private static FileConfiguration langConfig;

    public static String language;

    @Override
    public void onEnable() {
        instance = this;

        initConfig(false);
        initLanguage();

        serviceManager = new ServiceManager();
        serviceManager.enable();

        metrics = new Metrics(this);

        Bukkit.getScheduler().runTaskLater(this, () -> {
            System.out.println("   _____ _              __      _______ _____  \n" +
                    "  / ____| |             \\ \\    / /_   _|  __ \\ \n" +
                    " | |    | | __ ___      _\\ \\  / /  | | | |__) |\n" +
                    " | |    | |/ _` \\ \\ /\\ / /\\ \\/ /   | | |  ___/ \n" +
                    " | |____| | (_| |\\ V  V /  \\  /   _| |_| |     \n" +
                    "  \\_____|_|\\__,_| \\_/\\_/    \\/   |_____|_|     ");
            getServer().getLogger().info(ChatColor.GOLD + "      by ClayClaw");
            getServer().getLogger().info(BuiltinMessage.getMessage("WELCOME"));
        },1);
    }

    public void initLanguage(){
        language = (ClawVIP.getConfigOption().getLanguage().equals("zh_TW")) ? "zh_TW" : "zh_CN";
    }

    @Override
    public void onDisable() {
        serviceManager.disable();
    }

    public static ClawVIP getInstance() {
        return instance;
    }

    public ServiceManager getServiceManager() {
        return serviceManager;
    }

    public void initConfig(boolean reload) {

        File file = new File(getDataFolder() + File.separator + "config.yml");

        if (!file.exists()) {
            getConfig().options().copyDefaults(true);
            saveConfig();
        } else {
            if(reload){
                try {
                    getConfig().load(file);
                } catch (IOException | InvalidConfigurationException e) {
                    e.printStackTrace();
                }
            }
            saveConfig();
            reloadConfig();
        }

        configOption = new ConfigOption();
        initLangConfig(reload);

    }

    public void initLangConfig(boolean reload) {
        langConfigFile = new File(getDataFolder()
                + File.separator +"lang"+ File.separator + configOption.getLanguage() + ".yml");
        new File("/plugins/ClawVIP/lang").mkdir();
        langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
        if (!langConfigFile.exists()) {
            if(configOption.getLanguage().equals("zh_CN")) {
                loadLangConfig("zh_CN", reload);
                getServer().getLogger().info(BuiltinMessage.getMessage("CREATELANGFILE") + " zh_CN");
            }else if(configOption.getLanguage().equalsIgnoreCase("zh_TW")) {
                loadLangConfig("zh_TW", reload);
                getServer().getLogger().info(BuiltinMessage.getMessage("CREATELANGFILE") + " zh_TW");
            } else if(configOption.getLanguage().equalsIgnoreCase("en_US")){
                loadLangConfig("en_US", reload);
                getServer().getLogger().info(BuiltinMessage.getMessage("CREATELANGFILE") + " en_US");
            }else{
                getServer().getLogger().severe(BuiltinMessage.getMessage("FILENOTFOUND")
                        + configOption.getLanguage());
            }
        }
        langConfig = YamlConfiguration.loadConfiguration(langConfigFile);
        try {
            langConfig.save(langConfigFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadLangConfig(String lang, boolean reload){
        try {
            Reader defConfigStream =
                    new InputStreamReader(getResource(lang+".yml"), "UTF-8");
            YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(defConfigStream);
            langConfig.setDefaults(defConfig);
            defConfigStream.close();
            langConfig.options().copyDefaults(true);
            if(!reload){
                langConfig.save(langConfigFile);
            }else{
                langConfig.load(langConfigFile);
            }
        } catch (IOException | InvalidConfigurationException e) {
            e.printStackTrace();
        }
    }

    public static String getPAPIConvertedString(String s, Player p) {
        return PlaceholderService.getPAPIConvertedString(s, p);
    }

    public static List<String> getPAPIConvertedStringList(List<String> sl, Player p) {
        ArrayList<String> output = new ArrayList<>();
        sl.stream().forEachOrdered(s -> output.add(getPAPIConvertedString(s, p)));
        return output;
    }

    public static String getLanguageString(String langNode) {
        return (!Objects.isNull(langConfig.getString(langNode)))
                ? ChatColor.translateAlternateColorCodes('&',langConfig.getString(langNode))
                : BuiltinMessage.getMessage("STRNOTFOUND") + langNode;
    }

    public static List<String> getLanguageStringList(String langNode) {
        return (!Objects.isNull(langConfig.getStringList(langNode)))
                ? ClawLib.convertColorCodeList(langConfig.getStringList(langNode))
                : Arrays.asList(BuiltinMessage.getMessage("STRLISTNOTFOUND") + langNode);
    }

    public static ConfigOption getConfigOption() {
        return configOption;
    }

    public static void debug(Object obj){
        Bukkit.getLogger().info("" + obj);
    }
}
