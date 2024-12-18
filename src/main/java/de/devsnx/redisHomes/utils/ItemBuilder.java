package de.devsnx.redisHomes.utils;

import org.bukkit.Bukkit;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;
import org.jetbrains.annotations.NotNull;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {
    private ItemStack stack;
    private String skullOwner;
    private UUID uuid;
    private ItemStack itemStack;

    private final ItemMeta meta;

    public ItemBuilder(Material material) {
        this.stack = new ItemStack(material);
        this.meta = this.stack.getItemMeta();
    }

    public ItemMeta getItemMeta() {
        return stack.getItemMeta();
    }


    public ItemBuilder setGlow(boolean glow) {
        if (glow) {
            addEnchant(Enchantment.KNOCKBACK, 1);
            addItemFlag(ItemFlag.HIDE_ENCHANTS);
        } else {
            ItemMeta meta = getItemMeta();
            for (Enchantment enchantment : meta.getEnchants().keySet()) {
                meta.removeEnchant(enchantment);
            }
        }
        return this;
    }


    public ItemBuilder setUnbreakable(boolean unbreakable) {
        ItemMeta meta = stack.getItemMeta();
        meta.setUnbreakable(unbreakable);
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setSkullOwner(String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    public ItemBuilder setSkullOwner(UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public ItemBuilder setAmount(int amount) {
        stack.setAmount(amount);
        return this;
    }

    public ItemBuilder setItemMeta(ItemMeta meta) {
        stack.setItemMeta(meta);
        return this;
    }

    public ItemBuilder setHead(String owner) {
        SkullMeta meta = (SkullMeta) stack.getItemMeta();
        meta.setOwner(owner);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setDisplayName(String displayname) {
        ItemMeta meta = getItemMeta();
        meta.setDisplayName(displayname);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder setItemStack (ItemStack stack) {
        this.stack = stack;
        return this;
    }

    public ItemBuilder setLore(String... lore){
        ItemMeta im = stack.getItemMeta();
        im.setLore(Arrays.asList(lore));
        stack.setItemMeta(im);
        return this;
    }

    public ItemBuilder setLore(List<String> lore) {
        ItemMeta im = stack.getItemMeta();
        im.setLore(lore);
        stack.setItemMeta(im);
        return this;
    }

    public ItemBuilder addEnchant(Enchantment enchantment, int level) {
        ItemMeta meta = getItemMeta();
        meta.addEnchant(enchantment, level, true);
        setItemMeta(meta);
        return this;
    }

    public ItemBuilder addItemFlag(ItemFlag flag) {
        ItemMeta meta = getItemMeta();
        meta.addItemFlags(flag);
        setItemMeta(meta);
        return this;
    }

    public ItemStack build() {

        if (this.uuid != null) {
            SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
            @NotNull PlayerProfile playerProfile = Bukkit.createPlayerProfile(this.uuid);
            PlayerTextures playerTextures = playerProfile.getTextures();
            playerProfile.setTextures(playerTextures);
            skullMeta.setOwnerProfile(playerProfile);
            itemStack.setItemMeta(skullMeta);
        }

        if (this.skullOwner != null) {
            SkullMeta skullMeta = (SkullMeta)itemStack.getItemMeta();
            org.bukkit.profile.@NotNull PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.fromString("4fbecd49-c7d4-4c18-8410-adf7a7348728"));
            PlayerTextures playerTextures = playerProfile.getTextures();

            try {
                playerTextures.setSkin(new URL("http://textures.minecraft.net/texture/" + this.skullOwner.toLowerCase()));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }

            playerProfile.setTextures(playerTextures);
            skullMeta.setOwnerProfile(playerProfile);
            itemStack.setItemMeta(skullMeta);
        }

        return stack;
    }
}
