package de.devsnx.redisHomes.listener;

import de.devsnx.redisHomes.RedisHomes;
import de.devsnx.redisHomes.manager.InventoryManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {

        Player player = (Player) event.getWhoClicked();

        ItemStack item = event.getCurrentItem();
        String itemName = item.getItemMeta().getDisplayName();

        if (event.getHotbarButton() != -1) {

            item = event.getView().getBottomInventory().getItem(event.getHotbarButton());

            if (item == null) {

                item = event.getCurrentItem();

            }
        }

        if (item == null || item.getType() == Material.AIR || item.hasItemMeta()) {return;}

        /**
         *  Click action, open other Inventory
         */

        if (event.getView().getTitle().equalsIgnoreCase("Deine Homes")) {

            event.setCancelled(true);

            if (RedisHomes.getInstance().getHomeManager().existsHome(player, itemName)) {
                player.closeInventory();
                player.openInventory(InventoryManager.openTeleportToHomeORDeletingTheHome(player, itemName));
            }

        }

        /**
         *  Delete or Teleport to Player Homes
         */

        if (event.getView().getTitle().startsWith("No Title")) {
            if (event.getSlot() == 12 || event.getSlot() == 14) {
                String[] array = event.getView().getTitle().split(" ");
                String nameOfHome = array[2];

                if (RedisHomes.getInstance().getHomeManager().existsHome(player, nameOfHome)) {
                    if (event.getSlot() == 12) {
                        player.closeInventory();
                        RedisHomes.getInstance().getHomeManager().teleportToServerAndHome(player, nameOfHome);
                    }
                    if (event.getSlot() == 14) {
                        if (event.isShiftClick()) {
                            player.closeInventory();
                            player.sendMessage("Du hast erfolgreich dein Home (" + nameOfHome + ") gelöscht");
                            RedisHomes.getInstance().getHomeManager().deleteHome(player, nameOfHome);
                        }
                    }
                }
            }
        }
    }
}
