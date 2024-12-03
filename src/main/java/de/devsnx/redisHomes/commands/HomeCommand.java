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
                if (homeManager.homeExists(player, homeName)) {
                    player.sendMessage("§cHome '" + homeName + "' existiert bereits.");
                    break;
                }

                homeManager.saveHome(player, homeName,
                        Bukkit.getServer().getName(),
                        player.getLocation().getX(),
                        player.getLocation().getY(),
                        player.getLocation().getZ(),
                        player.getLocation().getYaw(),
                        player.getLocation().getPitch());

                player.sendMessage("§aHome '" + homeName + "' gesetzt.");
                break;

            case "home":
                if (!homeManager.homeExists(player, homeName)) {
                    player.sendMessage("§cHome '" + homeName + "' existiert nicht.");
                    break;
                }
                homeManager.teleportToServerAndHome(player, homeName);
                break;

            case "delhome":
                if (!homeManager.homeExists(player, homeName)) {
                    player.sendMessage("§cHome '" + homeName + "' existiert nicht.");
                    break;
                }
                homeManager.removeHome(player, homeName);
                player.sendMessage("§cHome '" + homeName + "' gelöscht.");
                break;

            default:
                player.sendMessage("§cUngültiger Befehl.");
                return false;
        }
        return true;
    }
}