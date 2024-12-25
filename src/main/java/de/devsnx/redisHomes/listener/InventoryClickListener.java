package de.devsnx.redisHomes.listener;

import de.devsnx.redisHomes.RedisHomes;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();

        if (event.getView().getTitle().equalsIgnoreCase("Deine Homes") || event.getView().getTitle().startsWith("Löschen? : ")) {
            event.setCancelled(true);

            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();

                if (event.getView().getTitle().equalsIgnoreCase("Deine Homes")) {

                    if (RedisHomes.getInstance().getHomeManager().existsHome(player, itemName)) {
                        if (event.isLeftClick()) {

                            player.closeInventory();
                            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1.0F, 1.0F);
                            RedisHomes.getInstance().getHomeManager().teleportToServerAndHome(player, clickedItem.getItemMeta().getDisplayName());

                        } else if (event.isRightClick()) {

                            player.closeInventory();
                            player.openInventory(RedisHomes.getInstance().getInventoryManager().openDeletingInventory(player, itemName));
                        }
                    }


                } else if (event.getView().getTitle().startsWith("Löschen? : ")) {

                    event.setCancelled(true);
                    if (RedisHomes.getInstance().getHomeManager().existsHome(player, itemName)) {
                        if (event.isLeftClick()) {

                            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 1.0F, 1.0F);
                            RedisHomes.getInstance().getHomeManager().deleteHome(player, itemName);
                            player.closeInventory();
                            player.sendMessage("§cDu hast erfolgreich das Home " + itemName + " gelöscht.");

                        } else {
                            player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
                        }
                    }
                }
            }
        }
    }
}
