package de.devsnx.redisHomes.commands;

import de.devsnx.redisHomes.manager.HomeManager;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 03.12.2024 13:09
 */

public class HomeCommand implements CommandExecutor {

    private HomeManager homeManager;

    public HomeCommand(HomeManager homeManager) {
        this.homeManager = homeManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("§cNur Spieler können diesen Befehl verwenden.");
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage("§cVerwendung: /home <HomeName> | /sethome <HomeName> | /delhome <HomeName>");
            return false;
        }

        String homeName = args[0];

        switch (command.getName().toLowerCase()) {
            case "sethome":
                if (homeManager.existsHome(player, homeName)) {
                    player.sendMessage("§cHome '" + homeName + "' existiert bereits.");
                    break;
                }

                homeManager.setHome(player, homeName, player.getLocation());

                player.sendMessage("§aHome '" + homeName + "' gesetzt.");
                break;

            case "home":
                if (!homeManager.existsHome(player, homeName)) {
                    player.sendMessage("§cHome '" + homeName + "' existiert nicht.");
                    break;
                }
                homeManager.teleportToServerAndHome(player, homeName);
                break;

            case "delhome":
                if (!homeManager.existsHome(player, homeName)) {
                    player.sendMessage("§cHome '" + homeName + "' existiert nicht.");
                    break;
                }
                homeManager.deleteHome(player, homeName);
                player.sendMessage("§cHome '" + homeName + "' gelöscht.");
                break;

            default:
                player.sendMessage("§cUngültiger Befehl.");
                return false;
        }
        return true;
    }
}