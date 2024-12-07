package de.devsnx.redisHomes.commands;

import de.devsnx.redisHomes.RedisHomes;
import de.devsnx.redisHomes.manager.HomeManager;
import org.bukkit.command.CommandExecutor;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.stream.Collectors;

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
            if(command.getName().equals("homes")) {
                if(!homeManager.getAllHomes(player).isEmpty()) {
                    String homes = homeManager.getAllHomes(player).stream()
                            .map(HomeManager.Home::getName)
                            .collect(Collectors.joining(getMessage("homes.split") + getMessage("homes.color")));
                    player.sendMessage(getMessage("home.homes").replace("%homes%", getMessage("homes.color") + homes));
                    return true;
                } else {
                    player.sendMessage(getMessage("home.nohomes"));
                    return true;
                }
            } else {
                player.sendMessage(getMessage("home.syntax"));
                return false;
            }
        }else if(args.length == 1) {
            String homeName = args[0];

            switch (command.getName().toLowerCase()) {
                case "sethome":
                    if (homeManager.existsHome(player, homeName)) {
                        player.sendMessage(getMessage("home.exists").replace("%home%", homeName));
                        break;
                    }

                    homeManager.setHome(player, homeName, player.getLocation());

                    player.sendMessage(getMessage("home.created").replace("%home%", homeName));
                    break;

                case "home":
                    if (!homeManager.existsHome(player, homeName)) {
                        player.sendMessage(getMessage("home.notexists").replace("%home%", homeName));
                        break;
                    }
                    homeManager.teleportToServerAndHome(player, homeName);
                    break;

                case "delhome":
                    if (!homeManager.existsHome(player, homeName)) {
                        player.sendMessage(getMessage("home.notexists").replace("%home%", homeName));
                        break;
                    }
                    homeManager.deleteHome(player, homeName);
                    player.sendMessage(getMessage("home.deleted").replace("%home%", homeName));
                    break;

                default:
                    return false;
            }
        }else{
            player.sendMessage(getMessage("home.syntax"));
            return false;
        }
        return true;
    }

    private String getMessage (String path) {
        String message = RedisHomes.getInstance().getMessageManager().getMessage("messages."+path).replace("&", "§");
        return message;
    }
}