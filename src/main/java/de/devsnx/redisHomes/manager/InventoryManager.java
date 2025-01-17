package de.devsnx.redisHomes.manager;

import de.devsnx.redisHomes.RedisHomes;
import de.devsnx.redisHomes.utils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class InventoryManager {

    private static final int ITEMS_PER_PAGE = 36;
    private static final int INVENTORY_SIZE = 6 * 9;
    public static final String INVENTORY_TITLE = "Deine Homes - Seite ";
    public static final String DELETE_INVENTORY_TITLE = "Löschen? : ";

    private static final Map<String, String> SERVER_SKULL_TEXTURES = Map.of(
            "feuerbrunnen-1", "7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef",
            "nebelsphere-1", "25a71ef5a046c0f0150343f85c1c90f752ee0a390753bb9a40e0828e71edfd20",
            "plotserver-1", "879146316f092d70bca006d34103c5807d1c9585c1e21f6b8df21171981aedba",
            "gruenwald-1", "7be942a3410c8081d3e4b2d634ddaabccb50ba6f39131f57877b402f6b315cef"
    );

    @NotNull
    public Inventory openHomeInventory(Player player, int page) {
        Inventory homeInventory = Bukkit.createInventory(player, INVENTORY_SIZE, INVENTORY_TITLE + (page + 1));
        setInventoryBorder(homeInventory);

        List<HomeManager.Home> playerHomes = RedisHomes.getInstance().getHomeManager().getAllHomes(player);

        if (playerHomes.isEmpty()) {
            homeInventory.setItem(31, new ItemBuilder(Material.BARRIER)
                    .setDisplayName("Du hast aktuell keine Homes /home")
                    .build());
            return homeInventory;
        }

        int startIndex = page * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, playerHomes.size());

        for (int i = startIndex; i < endIndex; i++) {
            setHomeItem(homeInventory, 9 + (i - startIndex), playerHomes.get(i));
        }

        setPaginationButtons(homeInventory, page, playerHomes.size());

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);
        return homeInventory;
    }

    private void setInventoryBorder(Inventory inventory) {
        ItemBuilder purpleGlass = new ItemBuilder(Material.PURPLE_STAINED_GLASS_PANE).setDisplayName("§d");
        ItemBuilder blackGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§d");

        for (int i : new int[]{0, 1, 7, 8, 45, 46, 52, 53}) {
            inventory.setItem(i, purpleGlass.build());
        }

        for (int i = 2; i <= 6; i++) {
            inventory.setItem(i, blackGlass.build());
        }

        for (int i = 47; i <= 51; i++) {
            inventory.setItem(i, blackGlass.build());
        }

        inventory.setItem(49, new ItemBuilder(Material.BARRIER).setDisplayName("§dClose").build());
    }

    private void setHomeItem(Inventory inventory, int slot, HomeManager.Home home) {
        inventory.setItem(slot, new ItemBuilder(Material.PLAYER_HEAD)
                .setDisplayName(home.getName())
                .setAmount(1)
                .setLore(Arrays.asList(
                        "§aServer Name: §6" + home.getServer(),
                        "§aWorldname: §6" + home.getWorld().getName(),
                        " ",
                        "§bLinks Click zum Teleportieren",
                        "§4Rechts Click zum Löschen."
                ))
                .setSkullOwner(SERVER_SKULL_TEXTURES.getOrDefault(home.getServer(), ""))
                .build());
    }

    private void setPaginationButtons(Inventory inventory, int page, int totalHomes) {
        if (page > 0) {
            inventory.setItem(48, new ItemBuilder(Material.ARROW).setDisplayName("§dVorherige Seite").build());
        }

        if ((page + 1) * ITEMS_PER_PAGE < totalHomes) {
            inventory.setItem(50, new ItemBuilder(Material.ARROW).setDisplayName("§dNächste Seite").build());
        }
    }

    public Inventory openDeletingInventory(Player player, String inventoryName) {
        Inventory deletingInventory = Bukkit.createInventory(null, 3 * 9, DELETE_INVENTORY_TITLE + inventoryName);

        ItemBuilder blackGlass = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).setDisplayName("§c");
        for (int i = 0; i < 27; i++) {
            deletingInventory.setItem(i, blackGlass.build());
        }

        deletingInventory.setItem(13, new ItemBuilder(Material.PLAYER_HEAD)
                .setSkullOwner("3c25c1f333a731bc34358f55c05185114153c3f3d474076f21cc1621b9994662")
                .setDisplayName(inventoryName)
                .setLore(Arrays.asList("§cLinks Click zum löschen"))
                .build());

        player.playSound(player.getLocation(), Sound.BLOCK_CHEST_OPEN, 1.0F, 1.0F);

        return deletingInventory;
    }
}
