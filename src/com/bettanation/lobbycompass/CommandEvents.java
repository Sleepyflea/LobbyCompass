package com.bettanation.lobbycompass;

import java.util.Arrays;
import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public final class CommandEvents implements CommandExecutor, TabCompleter {
   LobbyCompass pl;

   CommandEvents(LobbyCompass plugin) {
      this.pl = plugin;
   }

   @Override
   public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
      if (command.getName().equalsIgnoreCase("lcs")) {
         if (args.length <= 1) {
            return Arrays.asList("reload", "version", "get");
         }
      }
      return null;
   }

   public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
      if (!command.getName().equalsIgnoreCase("lcs")) {
         return false;
      } else {
         Player p = null;
         if (sender instanceof Player) {
            p = (Player)sender;
            if (args.length <= 0) {
               if (!p.hasPermission("lcs.use")) {
                  sender.sendMessage("§4You don't have permission to use this command!");
               } else {
                  p.openInventory(this.pl.inventoryCreator.createInventory(p));
                  p.setf
               }
               return true;
            }
            if (args[0].equalsIgnoreCase("reload") && p.hasPermission("lcs.reload")) {
               if (!p.isOp()) {
                  p.sendMessage("§4You don't have permission to use this command!");
               } else {
                  sender.sendMessage("§6Reloading Configuration...");
                  this.pl.reloadConfig();
                  this.pl.reload();
                  sender.sendMessage("§aDone!");
               }
               return true;
            }
            if (args[0].equalsIgnoreCase("get") && p.hasPermission("lcs.get")) {
               List<String> itemworld = this.pl.getConfig().getStringList("worlds");
                if (!p.getInventory().contains(this.pl.compassItem) && itemworld.contains(p.getWorld().getName())) {
                  p.getInventory().setItem(this.pl.getCompassSlot, this.pl.compassItem);
               } else {
                    if(p.getInventory().contains(this.pl.compassItem)){
                        p.sendMessage("§4You already have a compass!");
                    }
                }
                return true;
            }
                        
            if (args[0].equalsIgnoreCase("version") && p.isOp()) {
               sender.sendMessage("LobbyCompass version: " + this.pl.getDescription().getVersion());
            } else {
               p.sendMessage("§4You don't have permission to use this command!");
            }
            return true;
         }
      }
      return true;
   }
}