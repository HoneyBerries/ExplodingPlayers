package me.honeyberries.explodingPlayers;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ExplodingPlayersSettings {

    public static final ExplodingPlayersSettings instance = new ExplodingPlayersSettings();
    private File configFile;
    private YamlConfiguration yamlConfig;
    private ArrayList<String> listOfExplodingPlayers = new ArrayList<>();

    private ExplodingPlayersSettings() {
        //Singleton pattern
    }

    public static ExplodingPlayersSettings getInstance() {
        return instance;
    }

    //method to get the listOfExplodingPlayers
    public ArrayList<String> getListOfExplodingPlayers() {
        return listOfExplodingPlayers;
    }

    //method to load the configuration file at startup
    public void load () {

        //make the File object configFile for the configuration file
        configFile = new File(ExplodingPlayers.getInstance().getDataFolder(), "config.yml");

        //checks if the file exists. If not, create it.
        if (!configFile.exists()) {
            ExplodingPlayers.getInstance().saveResource("config.yml", false);
        }

        //load the config file to a YamlConfiguration object
        yamlConfig = new YamlConfiguration();

        //allow comments in the configuration file
        yamlConfig.options().parseComments(true);

        
        //attempt to load the configuration file into Yaml system
        try {
            yamlConfig.load(configFile);
        }

        catch (Exception e) {
            e.printStackTrace();
            Bukkit.getLogger().warning("Configuration File failed to be loaded ;(");
        }

        //get List<String> of exploding players' uuid's
        List<String> uuidStrings = ExplodingPlayers.getInstance().getConfig().getStringList("exploding-players");


        //turn it into an ArrayList<String> since it is better
        for (String uuidStringTemp:uuidStrings) {
            try {
                listOfExplodingPlayers.add(uuidStringTemp);
            }

            catch (IllegalArgumentException exc) {
                exc.printStackTrace();
                Bukkit.getLogger().warning("Loading the list of exploding players failed ;(");
                Bukkit.getLogger().warning("Most likely the server admin is dumb!");
            }

        //hopefully successfully get the list of exploding players
        }
        //System.out.println("List of players who shall EXPLODE:");
        //System.out.println(listOfExplodingPlayers);


    }

    //method to save data
    public void saveConfig() {
        try {
            yamlConfig.save(configFile);
        } catch (Exception exce) {
            exce.printStackTrace();
            Bukkit.getLogger().warning("Configuration File failed to be saved ;(");
        }


    }

    //method to edit the configuration file
    public void set (String path, Object value) {
        yamlConfig.set(path, value);
        saveConfig();
    }


    //method to add a player to the exploding list
    public void addPlayerToExplodingList(Player person) {
        UUID personUUID = person.getUniqueId();

        //check if the player is already in the list
        if (!listOfExplodingPlayers.contains(personUUID.toString())) {
            //add them if not in the list
            listOfExplodingPlayers.add(personUUID.toString());

        }

        ExplodingPlayersSettings.getInstance().set("exploding-players", listOfExplodingPlayers.stream().toList());
        Bukkit.getLogger().info("Added " + person.getName());

    }


    //method to remove a player from the exploding list
    public void removePlayerFromExplodingList(Player person1) {
        UUID person1UUID = person1.getUniqueId();
        //check if the player is in the list
        if (listOfExplodingPlayers.contains(person1UUID.toString())) {
            //remove them if already in the list
            listOfExplodingPlayers.remove(person1UUID.toString());

        }

        ExplodingPlayersSettings.getInstance().set("exploding-players", listOfExplodingPlayers.stream().toList());
        Bukkit.getLogger().info("Removed " + person1.getName());

    }

}
