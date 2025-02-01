package de.devsnx.redisHomes.listener;

import de.devsnx.redisHomes.RedisHomes;
import de.devsnx.redisHomes.manager.InventoryManager;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class InventoryClickListener implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (!(event.getWhoClicked() instanceof Player)) return;
        Player player = (Player) event.getWhoClicked();
        ItemStack clickedItem = event.getCurrentItem();
        String inventoryTitle = event.getView().getTitle();

        if (inventoryTitle.startsWith(InventoryManager.INVENTORY_TITLE) || inventoryTitle.startsWith(InventoryManager.DELETE_INVENTORY_TITLE)) {
            event.setCancelled(true);

            if (clickedItem != null && clickedItem.hasItemMeta() && clickedItem.getItemMeta().hasDisplayName()) {
                String itemName = clickedItem.getItemMeta().getDisplayName();

                if (inventoryTitle.startsWith(InventoryManager.INVENTORY_TITLE)) {
                    handleHomeInventoryClick(player, itemName, event);
                } else if (inventoryTitle.startsWith(InventoryManager.DELETE_INVENTORY_TITLE)) {
                    handleDeletingInventoryClick(player, itemName, event);
                }
            }
        }
    }

    private void handleHomeInventoryClick(Player player, String itemName, InventoryClickEvent event) {
        RedisHomes plugin = RedisHomes.getInstance();
        String cleanItemName = removeColorCodes(itemName);

        switch (itemName) {
            case "§dVorherige Seite":
                openInventoryPage(player, getCurrentPage(event.getView().getTitle()) - 1);
                break;
            case "§dNächste Seite":
                openInventoryPage(player, getCurrentPage(event.getView().getTitle()) + 1);
                break;
            case "§dClose":

                Bukkit.getScheduler().runTaskLater(RedisHomes.getInstance(), () -> {
                    player.closeInventory();
                    player.playSound(player.getLocation(), Sound.BLOCK_CHEST_CLOSE, 1.0F, 1.0F);
                }, 4L);

                break;
            default:
                if (plugin.getHomeManager().existsHome(player, cleanItemName)) {
                    if (event.isLeftClick()) {
                        teleportToHome(player, cleanItemName);
                    } else if (event.isRightClick()) {
                        openDeletingInventory(player, cleanItemName);
                    }
                }
                break;
        }
    }

    private void handleDeletingInventoryClick(Player player, String itemName, InventoryClickEvent event) {
        RedisHomes plugin = RedisHomes.getInstance();
        String cleanItemName = removeColorCodes(itemName);

        if (plugin.getHomeManager().existsHome(player, cleanItemName)) {
            if (event.isLeftClick()) {
                deleteHome(player, cleanItemName);
            } else {
                player.playSound(player.getLocation(), Sound.ENTITY_VILLAGER_NO, 1.0F, 1.0F);
            }
        }
    }

    private void openInventoryPage(Player player, int page) {
        player.openInventory(RedisHomes.getInstance().getInventoryManager().openHomeInventory(player, page));
    }

    private void teleportToHome(Player player, String homeName) {

        Bukkit.getScheduler().runTaskLater(RedisHomes.getInstance(), () -> {
            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_PLAYER_TELEPORT, 1.0F, 1.0F);
            RedisHomes.getInstance().getHomeManager().teleportToServerAndHome(player, homeName);
        }, 4L);

    }

    private void openDeletingInventory(Player player, String homeName) {

        Bukkit.getScheduler().runTaskLater(RedisHomes.getInstance(), () -> {
            player.closeInventory();
            player.openInventory(RedisHomes.getInstance().getInventoryManager().openDeletingInventory(player, homeName));
        }, 4L);
    }

    private void deleteHome(Player player, String homeName) {

        Bukkit.getScheduler().runTaskLater(RedisHomes.getInstance(), () -> {

            player.closeInventory();
            player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_DEATH, 1.0F, 1.0F);
            RedisHomes.getInstance().getHomeManager().deleteHome(player, homeName);
            player.sendMessage(RedisHomes.getInstance().getMessageManager().getMessage("messages.home.deleted").replace("&", "§").replace("%home%", homeName));

        }, 4L);
    }

    private int getCurrentPage(String title) {
        String[] parts = title.split(" ");
        return Integer.parseInt(parts[parts.length - 1]) - 1;
    }

    private String removeColorCodes(String input) {
        return input.replaceAll("(?i)§[0-9a-fk-or]", ""); // Entfernt alle Farbzeichen
    }
}
