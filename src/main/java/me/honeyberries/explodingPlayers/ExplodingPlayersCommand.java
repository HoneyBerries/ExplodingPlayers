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

    // Get online player by UUID or username
    public Player getOnlinePlayer(String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getPlayer(uuid); // Returns null if player is offline
        } catch (IllegalArgumentException e) {
            return Bukkit.getPlayer(identifier); // Fallback to name lookup
        }
    }

    // Get offline player by UUID or username
    public OfflinePlayer getOfflinePlayer(String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getOfflinePlayer(identifier);
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Ensure the sender provided enough arguments
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) { // Add a player to the list
                Player player = getOnlinePlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("Player not found or is offline. Use a valid name or UUID.");
                    return true;
                }

                ExplodingPlayersSettings.getInstance().addPlayerToExplodingList(player);
                sender.sendMessage("Added " + player.getName() + " to the exploding players list.");
                return true;
            } else if (args[0].equalsIgnoreCase("remove")) { // Remove a player from the list
                Player player = getOnlinePlayer(args[1]);
                if (player == null) {
                    sender.sendMessage("Player not found or is offline. Use a valid name or UUID.");
                    return true;
                }

                ExplodingPlayersSettings.getInstance().removePlayerFromExplodingList(player);
                sender.sendMessage("Removed " + player.getName() + " from the exploding players list.");
                return true;
            }
        } else if (args.length == 1) { // Commands without a player argument
            if (args[0].equalsIgnoreCase("list")) { // List all exploding players
                ArrayList<String> explodingPlayers = ExplodingPlayersSettings.getInstance().getListOfExplodingPlayers();
                ArrayList<String> playerNames = new ArrayList<>();

                for (String uuidString : explodingPlayers) {
                    try {
                        UUID uuid = UUID.fromString(uuidString);
                        Player onlinePlayer = Bukkit.getPlayer(uuid);
                        if (onlinePlayer != null) {
                            playerNames.add(onlinePlayer.getName()); // Use name if online
                        } else {
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                            if (offlinePlayer != null && offlinePlayer.getName() != null) {
                                playerNames.add(offlinePlayer.getName()); // Use name if offline
                            } else {
                                Bukkit.getLogger().warning("Failed to find name for UUID: " + uuidString);
                            }
                        }
                    } catch (IllegalArgumentException e) {
                        Bukkit.getLogger().warning("Invalid UUID in configuration: " + uuidString);
                    }
                }

                if (playerNames.isEmpty()) {
                    sender.sendMessage("No players are currently marked as exploding.");
                } else {
                    sender.sendMessage("Exploding Players: " + String.join(", ", playerNames));
                }
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) { // Reload the plugin configuration
                ExplodingPlayersSettings.getInstance().load();
                sender.sendMessage("Configuration successfully reloaded.");
                return true;
            }
        }

        // If the command syntax is incorrect
        sender.sendMessage("Invalid command. Usage: /explodingPlayers <add|remove|list|reload> [player]");
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1) { // First argument can be: add, remove, list, reload
            return Arrays.asList("add", "remove", "list", "reload");
        } else if (args.length == 2) { // Second argument: suggest online player names for add/remove
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                String partialPlayerName = args[1];
                List<String> matchingNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                        matchingNames.add(player.getName());
                    }
                }
                return matchingNames;
            }
        }

        return new ArrayList<>(); // No suggestions for other cases
    }
}
