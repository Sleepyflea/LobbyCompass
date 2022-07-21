package com.bettanation.lobbycompass;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.TabCompleter;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

public final class LobbyCompass extends JavaPlugin {
   CommandExecutor cmdProc = new CommandEvents(this);
   InventoryCreator inventoryCreator = new InventoryCreator();
   InventoryEvents inventoryEvents = new InventoryEvents(this);
   PlayerEvents playerEvents = new PlayerEvents(this);
   String prefix = "§f[§eLobbyCompass§f] ";
   ItemStack compassItem;
   int getCompassSlot;
   List<String> compassWorlds;
   boolean canDropCompass;
   boolean compassPlaysSound;
   Sound compassSound;
   Economy eco;
   boolean isUsingPapi = false;
   boolean usingEco = false;

   void reload() {
      this.usingEco = Bukkit.getPluginManager().getPlugin("Vault") != null;
      if (this.usingEco) {
         RegisteredServiceProvider<Economy> rspe = Bukkit.getServicesManager().getRegistration(Economy.class);
         this.eco = (Economy)rspe.getProvider();
      }

      if (!(new File(this.getDataFolder().getPath() + "/config")).exists()) {
         this.saveDefaultConfig();
         this.reloadConfig();
      }

      this.isUsingPapi = Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null;
      this.compassItem = new ItemStack(Material.getMaterial(this.getConfig().getString("compass.item").toUpperCase().replace(" ", "_")));
      ItemMeta compassMeta = this.compassItem.getItemMeta();
      compassMeta.setDisplayName(this.inventoryCreator.convert(this.getConfig().getString("compass.name")));
      List<String> compassLore = new ArrayList();
      List<String> oldList = this.getConfig().getStringList("compass.lore");
      if (oldList != null) {
         oldList.forEach((str) -> {
            compassLore.add(this.inventoryCreator.convert(str));
         });
      }

      compassMeta.setLore(compassLore);
      if (this.getConfig().getBoolean("compass.glowing")) {
         compassMeta.addEnchant(Enchantment.PROTECTION_ENVIRONMENTAL, 1, true);
         compassMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      }
      this.compassItem.setItemMeta(compassMeta);
      compassMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ATTRIBUTES});
      compassMeta.addItemFlags(new ItemFlag[]{ItemFlag.HIDE_ENCHANTS});
      this.getCompassSlot = this.getConfig().getInt("compass.slot");
      this.compassWorlds = this.getConfig().getStringList("worlds");
      this.compassPlaysSound = this.getConfig().getBoolean("compass.play-sound");
      this.compassSound = Sound.valueOf(this.getConfig().getString("compass.sound").replace(".", "_").toUpperCase().replace(" ", "_"));
      this.canDropCompass = this.getConfig().getBoolean("can-drop-compass");
      this.inventoryCreator.loadOptions(this, this.getConfig());
      Bukkit.getOnlinePlayers().forEach((plr) -> {
      boolean compassWorldsH = !plr.getWorld().equals(this.compassWorlds);
      });
   }

   public void onEnable() {
      this.getCommand("lcs").setExecutor(this.cmdProc);
      this.getCommand("lcs").setTabCompleter((TabCompleter)this.cmdProc);
      Bukkit.getPluginManager().registerEvents(new InventoryEvents(this), this);
      Bukkit.getPluginManager().registerEvents(this.playerEvents, this);
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "BungeeCord");
      this.reload();
   }
}
