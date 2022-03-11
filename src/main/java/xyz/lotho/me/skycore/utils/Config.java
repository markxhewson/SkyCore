package xyz.lotho.me.skycore.utils;

import org.bukkit.configuration.file.YamlConfiguration;
import xyz.lotho.me.skycore.SkyCore;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

public class Config {

    private SkyCore instance;
    private YamlConfiguration getConfigFile;
    private File configFile;

    public Config(SkyCore instance, String configName) {
        this.instance = instance;

        create(configName);
    }

    public void create(String configName) {
        if (!instance.getDataFolder().exists()) {
            instance.getDataFolder().mkdir();
        }

        configFile = new File(instance.getDataFolder(), configName);

        if (!configFile.exists()) {
            try (InputStream inputStream = instance.getResource(configName)) {
                if (inputStream != null) {
                    Files.copy(inputStream, configFile.toPath());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        getConfigFile = YamlConfiguration.loadConfiguration(configFile);
    }

    public void save() {
        try {
            getConfigFile.save(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public YamlConfiguration get() {
        return getConfigFile;
    }
}
