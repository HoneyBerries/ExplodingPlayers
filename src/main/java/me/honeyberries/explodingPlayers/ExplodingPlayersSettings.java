package me.honeyberries.explodingPlayers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ExplodingPlayersSettings {

    public static final ExplodingPlayersSettings instance = new ExplodingPlayersSettings();
    private File configFile;
    private YamlConfiguration yamlConfig;
    private ArrayList<String> listOfExplodingPlayers = new ArrayList<>();
    private float explosionPower;
    private List<String> uuidStrings = new ArrayList<>();

    private ExplodingPlayersSettings() {
        // Singleton pattern
    }

    public static ExplodingPlayersSettings getInstance() {
        return instance;
    }

    // Method to load the configuration file at startup
    public void load() {
        configFile = new File(ExplodingPlayers.getInstance().getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            ExplodingPlayers.getInstance().saveResource("config.yml", false);
        }

        yamlConfig = new YamlConfiguration();
        yamlConfig.options().parseComments(true);

        try {
            yamlConfig.load(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Configuration File failed to be loaded ;(");
        }

        // Update the list of exploding players
        try {
            this.uuidStrings = yamlConfig.getStringList("exploding-players");
            this.listOfExplodingPlayers = new ArrayList<>(uuidStrings);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to get list of exploding players. Defaulting to none.");
            this.uuidStrings = new ArrayList<>();
            this.listOfExplodingPlayers = new ArrayList<>();
        }


        // Update the explosionPower field
        try {
            this.explosionPower = (float) yamlConfig.getDouble("explosion-power");
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Failed to load explosion power. Defaulting to 0.");
            this.explosionPower = 0.0f; // Default value
        }
    }

    // Method to save data
    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Configuration File failed to be saved ;(");
        }
    }

    // Method to edit the configuration file
    public void set(String path, Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }

    // Method to add a player to the exploding list
    public void addPlayerToExplodingList(Player person) {
        UUID personUUID = person.getUniqueId();

        // Check if the player is already in the list
        if (!listOfExplodingPlayers.contains(personUUID.toString())) {
            // Add them if not in the list
            listOfExplodingPlayers.add(personUUID.toString());
        }

        ExplodingPlayersSettings.getInstance().set("exploding-players", listOfExplodingPlayers.stream().toList());
        Bukkit.getLogger().info("Added " + person.getName());
    }

    // Method to remove a player from the exploding list
    public void removePlayerFromExplodingList(Player person1) {
        UUID person1UUID = person1.getUniqueId();

        // Check if the player is in the list
        if (listOfExplodingPlayers.contains(person1UUID.toString())) {
            // Remove them if already in the list
            listOfExplodingPlayers.remove(person1UUID.toString());
        }

        ExplodingPlayersSettings.getInstance().set("exploding-players", listOfExplodingPlayers.stream().toList());
        Bukkit.getLogger().info("Removed " + person1.getName());
    }

    // Method to get the listOfExplodingPlayers
    public ArrayList<String> getListOfExplodingPlayers() {
        return listOfExplodingPlayers;
    }

    // Method to get explosionPower
    public float getExplosionPower() {
        Bukkit.getLogger().info("Fetching explosion power: " + explosionPower);
        return explosionPower;
    }
}
