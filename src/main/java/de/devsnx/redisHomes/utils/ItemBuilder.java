package de.devsnx.redisHomes.utils;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.profile.PlayerProfile;
import org.bukkit.profile.PlayerTextures;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ItemBuilder {

    private ItemStack itemStack;
    private int amount = 1, customModelData = 0, potionDuration = 0, potionAmplifier = 0;
    private String name, skullOwner;
    private UUID uuid;
    private List<String> lore = new ArrayList<>();
    private HashMap<Enchantment, Integer> enchantments = new HashMap<>();
    private Color color;
    private PotionEffectType potionEffectType;
    private PotionType potionType;
    private boolean unbreakable = false, hideFlags = false, glow = false;

    public ItemBuilder(final Material material) {
        this.itemStack = new ItemStack(material);
    }

    public ItemBuilder(final ItemStack itemStack) {
        this.itemStack = itemStack.clone();
    }

    public ItemBuilder setAmount(int amount) {
        this.amount = amount;
        return this;
    }

    public ItemBuilder setCustomModelData(final int customModelData) {
        this.customModelData = customModelData;
        return this;
    }

    public ItemBuilder setDisplayName(final String name) {
        this.name = name;
        return this;
    }

    public ItemBuilder setSkullOwner(final String skullOwner) {
        this.skullOwner = skullOwner;
        return this;
    }

    public ItemBuilder setSkullOwner(final UUID uuid) {
        this.uuid = uuid;
        return this;
    }

    public ItemBuilder setLore(final List<String> lore) {
        this.lore = new ArrayList<>(lore);
        return this;
    }

    public ItemBuilder addEnchantment(final Enchantment enchantment, final int level) {
        this.enchantments.put(enchantment, level);
        return this;
    }

    public ItemBuilder setColor(final Color color) {
        this.color = color;
        return this;
    }

    public ItemBuilder setPotionEffectType(final PotionEffectType potionEffectType, final int duration, final int amplifier) {
        this.potionEffectType = potionEffectType;
        this.potionDuration = duration;
        this.potionAmplifier = amplifier;
        return this;
    }

    public ItemBuilder setPotionType(final PotionType potionType) {
        this.potionType = potionType;
        return this;
    }

    public ItemBuilder setUnbreakable(final boolean unbreakable) {
        this.unbreakable = unbreakable;
        return this;
    }

    public ItemBuilder setHideFlags(final boolean hideFlags) {
        this.hideFlags = hideFlags;
        return this;
    }

    public ItemBuilder setGlow(final boolean glow) {
        this.glow = glow;
        return this;
    }

    public ItemStack build() {
        if (this.itemStack == null) {
            throw new IllegalStateException("ItemStack cannot be null");
        }

        this.itemStack.setAmount(this.amount);
        ItemMeta itemMeta = this.itemStack.getItemMeta();
        if (itemMeta == null) {
            throw new IllegalStateException("ItemMeta cannot be null");
        }

        itemMeta.setUnbreakable(this.unbreakable);

        if (this.name != null) {
            itemMeta.setDisplayName(this.name);
        }
        if (!this.lore.isEmpty()) {
            itemMeta.setLore(this.lore);
        }

        if (!this.enchantments.isEmpty()) {
            for (final Enchantment enchantment : this.enchantments.keySet()) {
                itemMeta.addEnchant(enchantment, this.enchantments.get(enchantment), true);
            }
        }

        if (this.customModelData > 0) {
            itemMeta.setCustomModelData(this.customModelData);
            this.hideFlags = true;
        }

        if (this.hideFlags) {
            itemMeta.addItemFlags(ItemFlag.values());
        }


        this.itemStack.setItemMeta(itemMeta);

        if (this.color != null) {
            if (this.itemStack.getType().equals(Material.FIREWORK_STAR)) {
                FireworkEffectMeta fireworkEffectMeta = (FireworkEffectMeta) this.itemStack.getItemMeta();
                FireworkEffect fireworkEffect = FireworkEffect.builder().withColor(this.color).build();
                fireworkEffectMeta.setEffect(fireworkEffect);
                this.itemStack.setItemMeta(fireworkEffectMeta);
            } else if (this.itemStack.getItemMeta() instanceof LeatherArmorMeta) {
                LeatherArmorMeta leatherArmorMeta = (LeatherArmorMeta) this.itemStack.getItemMeta();
                leatherArmorMeta.setColor(this.color);
                this.itemStack.setItemMeta(leatherArmorMeta);
            }
        }

        if (this.uuid != null && this.itemStack.getItemMeta() instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(this.uuid);
            skullMeta.setOwnerProfile(playerProfile);
            this.itemStack.setItemMeta(skullMeta);
        }

        if (this.skullOwner != null && this.itemStack.getItemMeta() instanceof SkullMeta) {
            SkullMeta skullMeta = (SkullMeta) this.itemStack.getItemMeta();
            PlayerProfile playerProfile = Bukkit.createPlayerProfile(UUID.randomUUID());
            PlayerTextures playerTextures = playerProfile.getTextures();
            try {
                playerTextures.setSkin(new URL("http://textures.minecraft.net/texture/" + this.skullOwner));
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            playerProfile.setTextures(playerTextures);
            skullMeta.setOwnerProfile(playerProfile);
            this.itemStack.setItemMeta(skullMeta);
        }

        if (this.potionEffectType != null && this.itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) this.itemStack.getItemMeta();
            potionMeta.addCustomEffect(new PotionEffect(this.potionEffectType, this.potionDuration, this.potionAmplifier), true);
            this.itemStack.setItemMeta(potionMeta);
        }

        if (this.potionType != null && this.itemStack.getItemMeta() instanceof PotionMeta) {
            PotionMeta potionMeta = (PotionMeta) this.itemStack.getItemMeta();
            potionMeta.setBasePotionType(this.potionType);
            this.itemStack.setItemMeta(potionMeta);
        }

        return this.itemStack;
    }
}
