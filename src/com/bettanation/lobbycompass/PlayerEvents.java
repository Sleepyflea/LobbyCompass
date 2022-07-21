package com.bettanation.lobbycompass;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;

public final class PlayerEvents implements Listener {

    LobbyCompass pl;

    PlayerEvents(LobbyCompass plugin) {
        this.pl = plugin;
    }

    @EventHandler
    void onItemUse(PlayerInteractEvent event) {
        boolean execute = false;
        if (event.getAction() == Action.RIGHT_CLICK_AIR || event.getAction() == Action.RIGHT_CLICK_BLOCK
                || event.getAction() == Action.LEFT_CLICK_AIR || event.getAction() == Action.LEFT_CLICK_BLOCK) {
            if (!event.getPlayer().getInventory().getItemInOffHand().equals(new ItemStack(Material.AIR))) {
                execute = execute || event.getPlayer().getInventory().getItemInOffHand().isSimilar(this.pl.compassItem);
            }

            if (!event.getPlayer().getInventory().getItemInOffHand().equals(new ItemStack(Material.AIR))) {
                execute = execute || event.getPlayer().getInventory().getItemInMainHand().isSimilar(this.pl.compassItem);
            }

            if (execute) {
                event.setCancelled(true);
                if (event.getPlayer().hasPermission("lcs.use")) {
                    event.getPlayer().openInventory(this.pl.inventoryCreator.createInventory(event.getPlayer()));
                    if (this.pl.compassPlaysSound) {
                        event.getPlayer().playSound(event.getPlayer().getLocation(), this.pl.compassSound, 1.0F, 1.0F);
                    }
                }
            }

        }
    }

    public void giveCompass(Player p){
    if(p.getInventory().getItem(this.pl.getCompassSlot) == null){
        p.getInventory().setItem(this.pl.getCompassSlot, this.pl.compassItem);
        return;
        }
    if(p.getInventory().firstEmpty() != -1){
        p.getInventory().addItem(this.pl.compassItem);
        return;
        }
        p.getWorld().dropItem(p.getLocation(), this.pl.compassItem);
    }
    
    @EventHandler
    void onDropItem(PlayerDropItemEvent event) {
        if (!this.pl.canDropCompass) {
            if (event.getItemDrop() != null && event.getItemDrop().getItemStack() != null && event.getItemDrop().getItemStack().isSimilar(this.pl.compassItem)) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    void onPlayerDeath(PlayerDeathEvent event) {
        List<ItemStack> p = event.getDrops();
        if (p.contains(this.pl.compassItem) && !this.pl.getConfig().getBoolean("drop-on-death")) {
            p.remove(this.pl.compassItem);
        }
    }

    @EventHandler
    void onPlayerRespawn(PlayerRespawnEvent event) {
        Player p = event.getPlayer();
        List<String> itemworld = this.pl.getConfig().getStringList("worlds");
        boolean onrespawn = this.pl.getConfig().getBoolean("receive-on-respawn");
        if (onrespawn && !p.getInventory().contains(this.pl.compassItem) && itemworld.contains(p.getWorld().getName())) {
            giveCompass(p);
        }
    }
    
        @EventHandler
    void onPlayerJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();
        List<String> itemworld = this.pl.getConfig().getStringList("worlds");
        boolean onjoin = this.pl.getConfig().getBoolean("receive-on-join");
        if (onjoin && !p.getInventory().contains(this.pl.compassItem) && itemworld.contains(p.getWorld().getName())) {
            giveCompass(p);
        }
    }

    @EventHandler
    void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        Player p = event.getPlayer();
        List<String> itemworld = this.pl.getConfig().getStringList("worlds");
        boolean onworldreturn = this.pl.getConfig().getBoolean("receive-on-world-return");
        if (p.getInventory().contains(this.pl.compassItem) && !itemworld.contains(p.getWorld().getName())) {
            p.getInventory().remove(this.pl.compassItem);
        }
        if (p.getInventory().getItemInOffHand().isSimilar(this.pl.compassItem) && !itemworld.contains(p.getWorld().getName())) {
            p.getInventory().setItemInOffHand(new ItemStack(Material.AIR));
        }
        if (onworldreturn && !p.getInventory().contains(this.pl.compassItem) && !p.getInventory().getItemInOffHand().isSimilar(this.pl.compassItem) && itemworld.contains(p.getWorld().getName())) {
            giveCompass(p);
        }
    }
}
