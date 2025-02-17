package me.honeyberries.explodingPlayers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

public class ExplodingPlayersSettings {

    public static final ExplodingPlayersSettings instance = new ExplodingPlayersSettings();
    private File configFile;
    private YamlConfiguration yamlConfig;
    private ArrayList<String> listOfExplodingPlayers = new ArrayList<>();
    private float explosionPower;

    private ExplodingPlayersSettings() {
        // Singleton pattern
    }

    public static ExplodingPlayersSettings getInstance() {
        return instance;
    }

    // Method to load the configuration file at startup
    public void load() {
        this.configFile = new File(ExplodingPlayers.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            ExplodingPlayers.getInstance().saveResource("config.yml", false);
        }

        this.yamlConfig = new YamlConfiguration();
        yamlConfig.options().parseComments(true);

        try {
            yamlConfig.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            ExplodingPlayers.getInstance().getLogger().warning("Configuration File failed to be loaded ;(");
        }

        // Update the list of exploding players
        try {
            this.listOfExplodingPlayers = new ArrayList<>(new LinkedHashSet<>(yamlConfig.getStringList("exploding-players")));
            ExplodingPlayers.getInstance().getLogger().info("List of exploding UUID's" + listOfExplodingPlayers.toString());

        } catch (Exception e) {
            e.printStackTrace();
            ExplodingPlayers.getInstance().getLogger().warning("Failed to get list of exploding players. Defaulting to no one!");
            this.listOfExplodingPlayers.clear();
        }


        // Update the explosionPower field
        try {
            this.explosionPower = (float) yamlConfig.getDouble("explosion-power");
            ExplodingPlayers.getInstance().getLogger().info("Explosion power is " + explosionPower);
        } catch (Exception e) {
            e.printStackTrace();
            ExplodingPlayers.getInstance().getLogger().warning("Failed to load explosion power. Defaulting to 0!");
            this.explosionPower = 0f; // Default value
        }
        ExplodingPlayers.getInstance().getLogger().info("Successfully loaded the exploding players config!");
    }

    // Method to save data
    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            ExplodingPlayers.getInstance().getLogger().warning("Configuration File failed to be saved ;(");
        }
    }

    // Method to edit the configuration file
    public void set(@NotNull String path, @NotNull Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }

    // Method to add a player to the exploding list
    public void addPlayerToExplodingList(@NotNull Player person) {
        UUID personUUID = person.getUniqueId();

        // Check if the player is already in the list
        if (!listOfExplodingPlayers.contains(personUUID.toString())) {
            // Add them if not in the list
            listOfExplodingPlayers.add(personUUID.toString());
        }

        listOfExplodingPlayers = new ArrayList<>(new LinkedHashSet<>(listOfExplodingPlayers));
        ExplodingPlayersSettings.getInstance().set("exploding-players", listOfExplodingPlayers.stream().toList());

    }

    // Method to remove a player from the exploding list
    public void removePlayerFromExplodingList(@NotNull Player person1) {
        UUID person1UUID = person1.getUniqueId();

        // Remove them if already in the list, else do nothing
        listOfExplodingPlayers.remove(person1UUID.toString());

        listOfExplodingPlayers = new ArrayList<>(new LinkedHashSet<>(listOfExplodingPlayers));
        ExplodingPlayersSettings.getInstance().set("exploding-players", listOfExplodingPlayers.stream().toList());
    }

    public ArrayList<String> getListOfExplodingPlayers() {
        return listOfExplodingPlayers;
    }

    public float getExplosionPower() {
        return explosionPower;
    }

    public void setExplosionPower(float explosionPower) {
        this.explosionPower = explosionPower;
        ExplodingPlayersSettings.getInstance().set("explosion-power", explosionPower);
    }
}
