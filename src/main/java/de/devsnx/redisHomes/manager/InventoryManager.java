package de.devsnx.redisHomes.manager;

import de.devsnx.redisHomes.RedisHomes;
import de.devsnx.redisHomes.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class InventoryManager {

    private static Inventory homeInventory = Bukkit.createInventory(null, 2*9, "Deine Homes");

    private static ItemStack feuerbrunnen;
    private static ItemStack gruenwald;
    private static ItemStack plotserver;
    private static ItemStack nebelsphere;


    {
        loadItems();
    }
    
    public static Inventory openHomeInventory(Player player) {

        homeInventory.clear();
        
        homeInventory.setItem(0, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(1, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(7, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(8, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());

        homeInventory.setItem(45, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(46, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());

        homeInventory.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("§dClose").build());
        homeInventory.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("§dWeiter").build());

        homeInventory.setItem(52, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(53, new ItemBuilder(Material.PINK_STAINED_GLASS_PANE).setDisplayName("§d").build());

        for (int i = 2; i <= 6; i++) {
            homeInventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§d").build());
        }

        for (int i = 47; i <= 51; i++) {
            homeInventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§d").build());
        }

        /**
         *  Check has Player Home
         */
        if (!RedisHomes.getInstance().getHomeManager().hasHome(player)) {
            homeInventory.setItem(31, new ItemBuilder(Material.BARRIER).setDisplayName("Du hast akutell keine Homes /home").build());
            return homeInventory;
        }


        /**
         *  Add Homes from Database to Inventory
         */
        List<HomeManager.Home> playerHomes = RedisHomes.getInstance().getHomeManager().getAllHomes(player);
        int slot = 9;

        for (HomeManager.Home home : playerHomes) {
            if (slot > 44) {
                /**
                 * Sobald voll ist zweites Inventory erstellen
                 */
            }


            switch (home.getServer()) {

                case "feuerbrunnen":
                    homeInventory.setItem(slot,
                            new ItemBuilder(feuerbrunnen.clone().getType())
                            .setDisplayName(home.getName())
                            .setLore("Right Click to Teleport to home")
                            .build());
                    break;

                case "gruenwald":
                    homeInventory.setItem(slot,
                            new ItemBuilder(gruenwald.clone().getType())
                            .setDisplayName(home.getName())
                            .setLore("Right Click to Teleport to home")
                            .build());
                    break;

                case "plotserver":
                    homeInventory.setItem(slot,
                            new ItemBuilder(plotserver.clone().getType())
                            .setDisplayName(home.getName())
                            .setLore("Right Click to Teleport to home")
                            .build());
                    break;

                case "nebelsphere":
                    homeInventory.setItem(slot,
                            new ItemBuilder(nebelsphere.clone().getType())
                            .setDisplayName(home.getName())
                            .setLore("Right Click to Teleport to home")
                            .build());
                    break;
            }

            slot++;
        }


        return homeInventory;
    }

    private void loadItems() {
        feuerbrunnen = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20").build();
        gruenwald = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef").build();
        plotserver = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("879146316f092d70bca006d34103c5807d1c9585c1e21f6b8df21171981aedba").build();
        nebelsphere = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20").build();
    }
}
