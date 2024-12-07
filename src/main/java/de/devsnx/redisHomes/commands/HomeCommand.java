package de.devsnx.redisHomes.commands;

import de.devsnx.redisHomes.RedisHomes;
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
            return false;
        }

        Player player = (Player) sender;

        if (args.length == 0) {
            player.sendMessage(getMessage("syntax"));
            return false;
        }

        String homeName = args[0];

        switch (command.getName().toLowerCase()) {
            case "sethome":
                if (homeManager.existsHome(player, homeName)) {
                    player.sendMessage(getMessage("exists").replace("%home%", homeName));
                    break;
                }

                homeManager.setHome(player, homeName, player.getLocation());

                player.sendMessage(getMessage("created").replace("%home%", homeName));
                break;

            case "home":
                if (!homeManager.existsHome(player, homeName)) {
                    player.sendMessage(getMessage("notexists").replace("%home%", homeName));
                    break;
                }
                homeManager.teleportToServerAndHome(player, homeName);
                break;

            case "delhome":
                if (!homeManager.existsHome(player, homeName)) {
                    player.sendMessage(getMessage("notexists").replace("%home%", homeName));
                    break;
                }
                homeManager.deleteHome(player, homeName);
                player.sendMessage(getMessage("deleted").replace("%home%", homeName));
                break;

            default:
                return false;
        }
        return true;
    }

    private String getMessage (String path) {
        String message = RedisHomes.getInstance().getMessageManager().getMessage("messages.home."+path).replace("&", "§");
        return message;
    }
}