package me.honeyberries.explodingPlayers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ExplodingPlayersCommand implements CommandExecutor, TabExecutor {

    // Get online player by UUID or username
    public Player getOnlinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getPlayer(uuid); // Returns null if player is offline
        } catch (IllegalArgumentException e) {
            return Bukkit.getPlayer(identifier); // Fallback to name lookup
        }
    }

    // Get offline player by UUID or username
    public OfflinePlayer getOfflinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getOfflinePlayer(identifier);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        // Command with exactly 2 arguments
        if (args.length == 2) {
            if (args[0].equalsIgnoreCase("add")) { // Add a player to the list
                Player player = getOnlinePlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found or is offline. Enter a valid username!");
                    return true;
                }

                ExplodingPlayersSettings.getInstance().addPlayerToExplodingList(player);
                sender.sendMessage("Successfully added " + player.getName() + " to the exploding players list.");
                Bukkit.getLogger().info("Successfully added " + player.getName());
                return true;

            } else if (args[0].equalsIgnoreCase("remove")) { // Remove a player from the list
                Player player = getOnlinePlayer(args[1]);
                if (player == null) {
                    sender.sendMessage(ChatColor.RED + "Player not found or is offline. Use a valid username!");
                    return true;
                }

                ExplodingPlayersSettings.getInstance().removePlayerFromExplodingList(player);
                sender.sendMessage("Successfully removed" + player.getName() + " from the exploding players list.");
                Bukkit.getLogger().info("Successfully removed " + player.getName());
                return true;

            } else if (args[0].equalsIgnoreCase("power")) { //Set the explosion power

                try {
                    float explosionForce = Float.parseFloat(args[1]);
                    if (explosionForce >= 0f) {
                        ExplodingPlayersSettings.getInstance().setExplosionPower(explosionForce);
                        Bukkit.getLogger().info("Set explosion power to " + explosionForce);
                        sender.sendMessage(ChatColor.GREEN + "Set the explosion power to " + explosionForce);
                    } else {
                        sender.sendMessage(ChatColor.RED + "You can't go negative!");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    sender.sendMessage(ChatColor.RED + "You did not enter a valid number for the explosion power!");
                }

                return true;
            }
        }


        else if (args.length == 1) { // Commands with exactly 1 argument

            if (args[0].equalsIgnoreCase("list")) { // List all exploding players
                ArrayList<String> explodingPlayers = ExplodingPlayersSettings.getInstance().getListOfExplodingPlayers();
                ArrayList<String> playerNames = new ArrayList<>();

                //turn all UUID's into usernames
                for (String uuidString : explodingPlayers) {
                    try {
                        UUID uuid = UUID.fromString(uuidString);
                        Player onlinePlayer = Bukkit.getPlayer(uuid);
                        if (onlinePlayer != null) {// Player is online
                            playerNames.add(onlinePlayer.getName());
                        } else { //Player is offline
                            OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                            if (offlinePlayer != null && offlinePlayer.getName() != null) {
                                playerNames.add(offlinePlayer.getName());
                            } else { //When everything fails
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
                sender.sendMessage(ChatColor.GREEN + "Configuration successfully reloaded.");
                return true;
            }
        }

        // If the command syntax is incorrect
        sender.sendMessage(ChatColor.RED + "Invalid command syntax!");
        sender.sendMessage("Usage: /explodingPlayers <add|remove|list|power|reload> [player]");
        return true;
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) { // First argument can be: add, remove, list, reload
            return Arrays.asList("add", "remove", "list", "power", "reload");
        } else if (args.length == 2) { // Second argument: suggest online player names for add/remove
            if (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove")) {
                String partialPlayerName = args[1];
                List<String> matchingNames = new ArrayList<>();
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (player.getName().toLowerCase().startsWith(partialPlayerName.toLowerCase())) {
                        matchingNames.add(player.getName()); //returns the suggestion for the players
                    }
                }
                return matchingNames;
            }
        }
        return new ArrayList<>(); // No suggestions for other cases
    }
}
