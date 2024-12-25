package de.devsnx.redisHomes.manager;

import de.devsnx.redisHomes.RedisHomes;
import de.devsnx.redisHomes.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class InventoryManager {
;
    /*
    private ItemStack feuerbrunnen;
    private ItemStack gruenwald;
    private ItemStack plotserver;
    private ItemStack nebelsphere;

 */
    private ItemStack deletHead;
    private ItemStack nextHead;


    public void ItemManager() {
        /*
        feuerbrunnen = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20").build();
        gruenwald = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef").build();
        plotserver = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("879146316f092d70bca006d34103c5807d1c9585c1e21f6b8df21171981aedba").build();
        nebelsphere = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20").build();
        deletHead = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("3c25c1f333a731bc34358f55c05185114153c3f3d474076f21cc1621b9994662").build();
        nextHead = new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").build();
         */
    }
    
    public Inventory openHomeInventory(Player player) {

        Inventory homeInventory = Bukkit.createInventory(player, 6*9, "Deine Homes");

        homeInventory.setItem(0, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(1, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(7, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(8, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());

        homeInventory.setItem(45, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(46, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());

        homeInventory.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("§dClose").build());

        homeInventory.setItem(52, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());
        homeInventory.setItem(53, new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d").build());

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

            player.sendMessage("Homes:" + home.getServer()+ ":" + home.getName());

            if (slot > 44) {
                homeInventory.setItem(53, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("19bf3292e126a105b54eba713aa1b152d541a1d8938829c56364d178ed22bf").setDisplayName("§dWeiter").build());
                return homeInventory;
            }


            switch (home.getServer()) {


                case "feuerbrunnen-1":

                    homeInventory.setItem(slot, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20")
                            .setDisplayName(home.getName())
                            .setAmount(1)
                            .setLore(Arrays.asList("§aServer Name: §6" + home.getServer(), "§aWorldname: §6" + home.getWorld().getName(), " " , "§bLinks Click zum Teleportieren", "§4Rechts Click zum Löschen."))
                            .build()
                    );

                    break;

                case "plotserver-1":
                    homeInventory.setItem(slot, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullOwner("879146316f092d70bca006d34103c5807d1c9585c1e21f6b8df21171981aedba")
                            .setDisplayName(home.getName())
                            .setAmount(1)
                            .setLore(Arrays.asList("§aServer Name: §6" + home.getServer(), "§aWorldname: §6" + home.getWorld().getName(), " " , "§bLinks Click zum Teleportieren", "§4Rechts Click zum Löschen."))
                            .build()
                    );
                    break;


                case "nebelsphere-1":
                    homeInventory.setItem(slot, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullOwner("25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20")
                            .setDisplayName(home.getName())
                            .setAmount(1)
                            .setLore(Arrays.asList("§aServer Name: §6" + home.getServer(), "§aWorldname: §6" + home.getWorld().getName(), " " , "§bLinks Click zum Teleportieren", "§4Rechts Click zum Löschen."))
                            .build()
                    );

                    break;

                case "gruenwald-1":

                    homeInventory.setItem(slot, new ItemBuilder(Material.PLAYER_HEAD)
                            .setSkullOwner("7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef")
                            .setDisplayName(home.getName())
                            .setAmount(1)
                            .setLore(Arrays.asList("§aServer Name: §6" + home.getServer(), "§aWorldname: §6" + home.getWorld().getName(), " " , "§bLinks Click zum Teleportieren", "§4Rechts Click zum Löschen."))
                            .build()
                    );

                    break;
                    

                default:
                    homeInventory.setItem(slot, new ItemBuilder(Material.BARRIER)
                            .setDisplayName(home.getName())
                            .setAmount(1)
                            .setLore(Arrays.asList("§aServer Name: §6" + home.getServer(), "§aWorldname: §6" + home.getWorld().getName(), " " , "§bLinks Click zum Teleportieren", "§4Rechts Click zum Löschen."))
                            .build()
                    );
                    break;

            }

            slot++;
        }

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
        return homeInventory;
    }

    /**
     * Add Two Item (Teleport and Delete)
     */
    public Inventory openDeletingInventory(Player player, String inventoryName) {

        Inventory deletingInventory = Bukkit.createInventory(null, 3*9, "Löschen? : " + inventoryName);


        for (int i = 0; i < 27; i++) {
            deletingInventory.setItem(i, new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§c").build());
        }

        deletingInventory.setItem(13, new ItemBuilder(Material.PLAYER_HEAD).setSkullOwner("3c25c1f333a731bc34358f55c05185114153c3f3d474076f21cc1621b9994662").setDisplayName(inventoryName).setLore(Arrays.asList("§cLink Click zum löschen")).build());
        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);

        return deletingInventory;
    }

}
