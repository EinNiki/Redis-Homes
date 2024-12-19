package de.devsnx.redisHomes.manager;

import de.devsnx.redisHomes.RedisHomes;
import de.devsnx.redisHomes.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import java.util.Arrays;
import java.util.List;

public class InventoryManager {

    private static Inventory homeInventory = Bukkit.createInventory(null, 6*9, "Deine Homes");
    private static Inventory homeInventory2 = Bukkit.createInventory(null, 6*9, "Deine Homes 2");
    private static Inventory homeInventoryNONAME;
    
    private static ItemStack feuerbrunnen;
    private static ItemStack gruenwald;
    private static ItemStack plotserver;
    private static ItemStack nebelsphere;
    private static ItemStack deletHead;
    private static ItemStack nextHead;


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
                homeInventory.setItem(53, new ItemBuilder(nextHead.clone().getType()).setDisplayName("§dWeiter").build());
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

    /**
     * Add Two Item (Teleport and Delete)
     */
    public static Inventory openTeleportToHomeORDeletingTheHome(Player player, String inventoryName) {

        homeInventoryNONAME = Bukkit.createInventory(null, 3*9, inventoryName);
        String serverNameOfTheHome = RedisHomes.getInstance().getHomeManager().getHome(player, inventoryName).getServer();
        String worldNameOfTheHome = RedisHomes.getInstance().getHomeManager().getHome(player, inventoryName).getWorld().getName();

        for (int i = 0; i < 27; i++) {
            homeInventoryNONAME.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§c").build());
        }

        homeInventoryNONAME.setItem(12, new ItemBuilder(Material.ENDER_PEARL).setDisplayName("Teleportiere dich zu deinem Home")
                .setLore(Arrays.asList(
                        "Server Name: " + serverNameOfTheHome,
                        "World Name: " + worldNameOfTheHome
                )).build());
        homeInventoryNONAME.setItem(14, new ItemBuilder(deletHead.clone().getType()).setDisplayName("Lösche dein Home").setLore("Shitf CLick to Deleting").build());

        return homeInventoryNONAME;
    }

    private void loadItems() {
        feuerbrunnen = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20").build();
        gruenwald = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef").build();
        plotserver = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("879146316f092d70bca006d34103c5807d1c9585c1e21f6b8df21171981aedba").build();
        nebelsphere = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20").build();
        deletHead = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("3c25c1f333a731bc34358f55c05185114153c3f3d474076f21cc1621b9994662").build();
        nextHead = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
    }
}
