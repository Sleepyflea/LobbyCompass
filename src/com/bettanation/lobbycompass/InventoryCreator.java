package com.bettanation.lobbycompass;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public final class InventoryCreator {
   List<Option> options;
   String name;
   int size = 0;
   boolean enabled = false;
   ItemStack emptyItem;
   boolean usePapi = false;

   String convert(String a) {
      return ChatColor.translateAlternateColorCodes('&', a);
   }

   void loadOptions(LobbyCompass pl, FileConfiguration cfg) {
      if (this.options == null) {
         this.options = new ArrayList();
      } else {
         this.options.clear();
      }

      this.usePapi = pl.isUsingPapi;
      this.emptyItem = new ItemStack(Material.getMaterial(cfg.getString("empty-slot.item")));
      ItemMeta emptyMeta = this.emptyItem.getItemMeta();
      if (emptyMeta != null) {
         emptyMeta.setDisplayName(this.convert(cfg.getString("empty-slot.name")));
         if (cfg.getBoolean("empty-slot.glowing")) {
            emptyMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            emptyMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         }

         List<String> emptyLore = cfg.getStringList("empty-slot.lore");
         List<String> nemptyLore = new ArrayList();
         if (emptyLore != null) {
            emptyLore.forEach((str) -> {
               nemptyLore.add(this.convert(str));
            });
         }

         emptyMeta.setLore(nemptyLore);
         this.emptyItem.setItemMeta(emptyMeta);
      }

      this.name = this.convert(cfg.getString("gui-title"));
      this.size = cfg.getInt("gui-line-count");
      if (this.size >= 1 && this.size <= 6) {
         this.size *= 9;
         ConfigurationSection sect = cfg.getConfigurationSection("data");
         Set<String> keys = sect.getKeys(false);
         keys.forEach((key) -> {
            Option opt = new Option();
            opt.name = this.convert(cfg.getString("data." + key + ".name"));
            opt.material = Material.getMaterial(cfg.getString("data." + key + ".item").toUpperCase().replace(" ", "_"));
            if (opt.material == null) {
               pl.getLogger().warning(opt.name + " -> Material: Is not a valid material!");
            }

            opt.cmds = cfg.getStringList("data." + key + ".cmds");
            opt.glowing = cfg.getBoolean("data." + key + ".glowing");
            List<String> lore = cfg.getStringList("data." + key + ".lore");
            opt.lore = new ArrayList();
            if (lore != null) {
               lore.forEach((item) -> {
                  opt.lore.add(this.convert(item));
               });
            }

            opt.sound = Sound.valueOf(cfg.getString("data." + key + ".sound").replace(".", "_").toUpperCase().replace(" ", "_"));
            opt.playsound = cfg.getBoolean("data." + key + ".play-sound");
            opt.executedByPlayer = cfg.getBoolean("data." + key + ".executed-by-player");
            opt.cost = cfg.getInt("data." + key + ".cost");
            opt.positionInInventory = cfg.getInt("data." + key + ".position-in-inventory");
            this.options.add(opt);
         });
      } else {
         pl.getLogger().warning("Invalid Configuration Option 'gui-line-count', must be at least 1 and at most 6");
      }
   }

   Inventory createInventory(Player plr) {
      Inventory inv = Bukkit.createInventory((InventoryHolder)null, this.size, this.name);
      this.options.forEach((opt) -> {
         ItemStack st = new ItemStack(opt.material);
         ItemMeta mt = st.getItemMeta();
         mt.setDisplayName(this.usePapi ? PlaceholderAPI.setPlaceholders(plr, opt.name) : opt.name.replace("%player%", plr.getName()));
         List<String> lore = new ArrayList();
         opt.lore.forEach((lr) -> {
            lore.add(this.usePapi ? PlaceholderAPI.setPlaceholders(plr, lr) : lr.replace("%player%", plr.getName()));
         });
         mt.setLore(lore);
         if (opt.glowing) {
            mt.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
            mt.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
         }

         mt.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
         st.setItemMeta(mt);
         inv.setItem(opt.positionInInventory, st);
      });

      for(int i = 0; i < inv.getSize(); ++i) {
         if (inv.getItem(i) == null) {
            inv.setItem(i, this.emptyItem);
         }
      }

      return inv;
   }
}