package de.devsnx.redisHomes.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.potion.PotionEffectType;

/**
 * @author Marvin Hänel (DevSnx)
 * @since 07.12.2024 12:35
 */

public class PlayerJoinListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        player.addPotionEffect(PotionEffectType.BLINDNESS.createEffect(4 * 20, 0));
    }
}