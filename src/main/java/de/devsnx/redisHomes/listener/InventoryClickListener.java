package de.devsnx.redisHomes.listener;

import de.devsnx.redisHomes.RedisHomes;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Map;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();

        if (event.getHotbarButton() != -1) {

            item = event.getView().getBottomInventory().getItem(event.getHotbarButton());

            if (item == null) {

                item = event.getCurrentItem();

            }
        }

        if (item == null || item.getType() == Material.AIR || item.hasItemMeta()) {return;}

        if (event.getView().getTitle().equalsIgnoreCase("Deine Homes")) {

            event.setCancelled(true);

            String itemName = item.getItemMeta().getDisplayName();

            if (RedisHomes.getInstance().getHomeManager().existsHome(player, itemName)) {
                player.closeInventory();
                RedisHomes.getInstance().getHomeManager().teleportToServerAndHome(player, itemName);
            }

        }
    }

}
