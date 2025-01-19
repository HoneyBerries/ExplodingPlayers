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

import java.util.*;

public class ExplodingPlayersCommand implements CommandExecutor, TabExecutor {

    // Get online player by UUID or username
    public static Player getOnlinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getPlayer(uuid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getPlayer(identifier);
        }
    }

    // Get offline player by UUID or username
    public static OfflinePlayer getOfflinePlayer(@NotNull String identifier) {
        try {
            UUID uuid = UUID.fromString(identifier);
            return Bukkit.getOfflinePlayer(uuid);
        } catch (IllegalArgumentException e) {
            return Bukkit.getOfflinePlayer(identifier);
        }
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 2) {
            handleTwoArgCommands(sender, args);
        } else if (args.length == 1) {
            handleSingleArgCommands(sender, args);
        } else {
            sendInvalidSyntaxMessage(sender);
        }
        return true;
    }

    private void handleTwoArgCommands(CommandSender sender, String[] args) {
        String action = args[0].toLowerCase();
        String target = args[1];

        switch (action) {
            case "add":
                if (sender.hasPermission("explodingPlayers.command.edit")) {
                    addPlayer(sender, target);
                } else {
                    sendNoPermissionMessage(sender);
                }
                break;

            case "remove":
                if (sender.hasPermission("explodingPlayers.command.edit")) {
                    removePlayer(sender, target);
                } else {
                    sendNoPermissionMessage(sender);
                }
                break;

            case "power":
                if (sender.hasPermission("explodingPlayers.command.edit")) {
                    setExplosionPower(sender, target);
                } else {
                    sendNoPermissionMessage(sender);
                }
                break;

            default:
                sendInvalidSyntaxMessage(sender);
                break;
        }
    }

    private void handleSingleArgCommands(CommandSender sender, String[] args) {
        String action = args[0].toLowerCase();

        switch (action) {
            case "list":
                if (sender.hasPermission("explodingPlayers.command.view")) {
                    listExplodingPlayers(sender);
                } else {
                    sendNoPermissionMessage(sender);
                }
                break;

            case "reload":
                if (sender.hasPermission("explodingPlayers.command.reload")) {
                    reloadConfig(sender);
                } else {
                    sendNoPermissionMessage(sender);
                }
                break;

            default:
                sendInvalidSyntaxMessage(sender);
                break;
        }
    }

    private void addPlayer(CommandSender sender, String target) {
        Player player = getOnlinePlayer(target);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or is offline. Enter a valid username!");
        } else if (ExplodingPlayersSettings.getInstance().getListOfExplodingPlayers().contains(player.getUniqueId())) {
            sender.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + " is already in the list!");
        } else {
            ExplodingPlayersSettings.getInstance().addPlayerToExplodingList(player);
            sender.sendMessage(ChatColor.GRAY + "Successfully added " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + " to the exploding players list.");
            Bukkit.getLogger().info("Added " + player.getName());
        }
    }

    private void removePlayer(CommandSender sender, String target) {
        Player player = getOnlinePlayer(target);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found or is offline. Use a valid username!");
        } else {
            String uuidString = player.getUniqueId().toString();
            if (ExplodingPlayersSettings.getInstance().getListOfExplodingPlayers().contains(uuidString)) {
                ExplodingPlayersSettings.getInstance().removePlayerFromExplodingList(player);
                sender.sendMessage(ChatColor.GRAY + "Successfully removed " + ChatColor.GREEN + player.getName() + ChatColor.GRAY + " from the exploding players list.");
                Bukkit.getLogger().info("Removed " + player.getName());
            } else {
                sender.sendMessage(ChatColor.GREEN + player.getName() + ChatColor.GRAY + " was never in the list");
            }
        }
    }


    private void setExplosionPower(CommandSender sender, String powerInput) {
        try {
            float explosionForce = Float.parseFloat(powerInput);
            if (explosionForce >= 0f) {
                ExplodingPlayersSettings.getInstance().setExplosionPower(explosionForce);
                sender.sendMessage(ChatColor.GRAY + "Set the explosion power to " + ChatColor.GREEN + explosionForce);
                Bukkit.getLogger().info("Explosion power: " + explosionForce);
            } else {
                sender.sendMessage(ChatColor.RED + "You can't go negative!");
            }
        } catch (NumberFormatException e) {
            sender.sendMessage(ChatColor.RED + "You did not enter a valid number for the explosion power!");
        }
    }

    private void listExplodingPlayers(CommandSender sender) {
        List<String> explodingPlayers = ExplodingPlayersSettings.getInstance().getListOfExplodingPlayers();
        List<String> playerNames = new ArrayList<>();

        for (String uuidString : explodingPlayers) {
            try {
                UUID uuid = UUID.fromString(uuidString);
                Player onlinePlayer = Bukkit.getPlayer(uuid);
                if (onlinePlayer != null) {
                    playerNames.add(onlinePlayer.getName());
                } else {
                    OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
                    if (offlinePlayer != null && offlinePlayer.getName() != null) {
                        playerNames.add(offlinePlayer.getName());
                    } else {
                        Bukkit.getLogger().warning("Failed to find name for UUID: " + uuidString);
                    }
                }
            } catch (IllegalArgumentException e) {
                Bukkit.getLogger().warning("Invalid UUID in configuration: " + uuidString);
            }
        }

        playerNames = new ArrayList<>(new LinkedHashSet<>(playerNames));
        playerNames.sort(String::compareTo);

        if (playerNames.isEmpty()) {
            sender.sendMessage(ChatColor.GRAY + "No players are currently marked as exploding.");
        } else {
            sender.sendMessage(ChatColor.GRAY + "Exploding Players: " + ChatColor.GREEN + String.join(", ", playerNames));
        }
    }

    private void reloadConfig(CommandSender sender) {
        ExplodingPlayersSettings.getInstance().load();
        sender.sendMessage(ChatColor.GREEN + "Configuration successfully reloaded.");
    }

    private void sendInvalidSyntaxMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "Invalid command syntax!");
        sender.sendMessage(ChatColor.GOLD + "Usage: /explodingPlayers <add|remove|list|power|reload> [player]");
    }

    private void sendNoPermissionMessage(CommandSender sender) {
        sender.sendMessage(ChatColor.RED + "You do not have the permissions to perform this action.");
    }

    @Override
    public List<String> onTabComplete(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (args.length == 1) {
            return Arrays.asList("add", "remove", "list", "power", "reload");
        } else if (args.length == 2 && (args[0].equalsIgnoreCase("add") || args[0].equalsIgnoreCase("remove"))) {
            String partialPlayerName = args[1].toLowerCase();
            List<String> matchingNames = new ArrayList<>();
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getName().toLowerCase().startsWith(partialPlayerName)) {
                    matchingNames.add(player.getName());
                }
            }
            return matchingNames;
        }
        return List.of();
    }
}
