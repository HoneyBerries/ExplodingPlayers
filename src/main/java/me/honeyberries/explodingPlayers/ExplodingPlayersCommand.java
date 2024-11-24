package me.honeyberries.explodingPlayers;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ExplodingPlayersCommand implements CommandExecutor, TabExecutor {



    public OfflinePlayer getOnlineOfflinePlayer(String identifier) {

        // Check if the input is a valid UUID
        try {
            UUID uuid = UUID.fromString(identifier);

            // First, check if the player is online using UUID
            Player onlinePlayer = Bukkit.getPlayer(uuid);
            if (onlinePlayer != null) {
                return onlinePlayer; // Return online Player object
            }

            // If the player is offline, get the OfflinePlayer by UUID
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
            if (offlinePlayer.hasPlayedBefore()) {
                return offlinePlayer; // This will return null if the player is offline
            }

        } catch (IllegalArgumentException e) {
            // If the input is not a valid UUID, treat it as a username
            Player onlinePlayer = Bukkit.getPlayer(identifier);
            if (onlinePlayer != null) {
                return onlinePlayer; // Return online Player object
            }

            // If the player is not online, get the OfflinePlayer by identifier
            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(identifier);
            if (offlinePlayer.hasPlayedBefore()) {
                return offlinePlayer; // This will return null if the player is offline
            }
        }

        // If the player does not exist or has never joined, return null
        return null;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        // ran /explodingPlayers add|remove HoneyBerries
        if (args.length == 2) {

            if (args[0].equalsIgnoreCase("add")) { //use /explodingPlayers add HoneyBerries

                Player player = (Player) getOnlineOfflinePlayer(args[1]);
                ExplodingPlayersSettings.getInstance().addPlayerToExplodingList(player);

                sender.sendMessage("You added " + player.getName() + " to the exploding player list!");
                return true;
            }



            else if (args[0].equalsIgnoreCase("remove")) { //use /explodingPlayers remove HoneyBerries

                Player player = (Player) getOnlineOfflinePlayer(args[1]);
                ExplodingPlayersSettings.getInstance().removePlayerFromExplodingList(player);

                sender.sendMessage("You removed " + player.getName() + " from the exploding player list");
                return true;
            }

            else {
                return false;
            }
        }



        else if (args.length == 1 && args[0].equalsIgnoreCase("list")) {  // use /explodingPlayers list
            ArrayList<String> playerUsernames = new ArrayList<>();
            ArrayList<String> playerUUIDs = ExplodingPlayersSettings.getInstance().getListOfExplodingPlayers();

            for (String uuidString : playerUUIDs) {
                try {
                    UUID uuid = UUID.fromString(uuidString);

                    // Check if the player is online
                    Player onlinePlayer = Bukkit.getPlayer(uuid);
                    if (onlinePlayer != null) {
                        // Player is online, get their name
                        playerUsernames.add(onlinePlayer.getName());
                    } else {
                        // Player is offline, use OfflinePlayer
                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                        String username = offlinePlayer.getName();

                        if (username != null) {
                            playerUsernames.add(username);
                        } else {
                            Bukkit.getLogger().warning("Failed to retrieve name for UUID: " + uuidString);
                        }
                    }
                } catch (IllegalArgumentException e) {
                    Bukkit.getLogger().warning("Invalid UUID format in config: " + uuidString);
                }
            }


            sender.sendMessage("Exploding Players: " + String.join(", ", playerUsernames));
            return true;
        }


        else { //bad command syntax
            return false;
        }
    }


    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {

    if (args.length == 1) { //first argument can be: add|remove|list

        return Arrays.asList("add", "remove", "list");

    }

    else if (args.length == 2) {

            String partialPlayerName = args[1]; // What the user has typed so far
            ArrayList<String> playerNames = new ArrayList<>();

            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                    playerNames.add(player.getName());
                }
            }

            return playerNames; // Return matching names

        }

    else return new ArrayList<>();
    }
}
