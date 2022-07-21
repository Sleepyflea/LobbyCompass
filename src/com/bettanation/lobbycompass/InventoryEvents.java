package com.bettanation.lobbycompass;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import me.clip.placeholderapi.PlaceholderAPI;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public final class InventoryEvents implements Listener {

    LobbyCompass pl;

    InventoryEvents(LobbyCompass plugin) {
        this.pl = plugin;
    }

    @EventHandler
    void onItemClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(this.pl.inventoryCreator.name)) {
            event.setCancelled(true);
            if (event.getCurrentItem() != null) {
                Player plr = (Player) event.getWhoClicked();
                this.pl.inventoryCreator.options.forEach((item) -> {
                    if (item.positionInInventory == event.getSlot()) {
                        boolean execute = true;
                        if (this.pl.usingEco) {
                            if (item.cost > 0) {
                                if (this.pl.eco.has((Player) event.getWhoClicked(), (double) item.cost)) {
                                    EconomyResponse res = this.pl.eco.withdrawPlayer((Player) event.getWhoClicked(), (double) item.cost);
                                    if (!res.transactionSuccess()) {
                                        plr.sendMessage(this.pl.prefix + "§c§lSorry! §7It seems there was an error withdrawing the funds from your account!");
                                    } else {
                                        plr.sendMessage(this.pl.prefix + "§c§l" + this.pl.eco.format((double) item.cost) + "§c was taken from your account!");
                                    }
                                } else {
                                    execute = false;
                                    plr.sendMessage(this.pl.prefix + "§cYou DO NOT have enough money to do that! You need at least §l" + this.pl.eco.format((double) item.cost) + "§c to use that!");
                                }
                            }

                            if (item.cost < 0) {
                                this.pl.getLogger().warning("§fCannot withdraw a negative amount from a balance! [\"" + item.name + "§f\" -> cost]");
                            }
                        }

                        if (execute) {
                            item.cmds.forEach((str) -> {
                                if (str.trim().toLowerCase().startsWith("server")) {
                                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                    DataOutputStream dos = new DataOutputStream(baos);

                                    try {
                                        dos.writeUTF("Connect");
                                        dos.writeUTF(str.trim().split(" ")[1]);
                                    } catch (Exception var8) {
                                        this.pl.getLogger().warning("Failed to write communication with BungeeCord.");
                                        return;
                                    }

                                    byte[] fmsg = baos.toByteArray();
                                    plr.sendPluginMessage(this.pl, "BungeeCord", fmsg);
                                } else {
                                    if (item.executedByPlayer) {
                                        Bukkit.dispatchCommand(plr, this.pl.isUsingPapi ? PlaceholderAPI.setPlaceholders(plr, str) : str.replace("%player%", event.getWhoClicked().getName()));
                                    } else {
                                        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), this.pl.isUsingPapi ? PlaceholderAPI.setPlaceholders(plr, str) : str.replace("%player%", event.getWhoClicked().getName()));
                                    }

                                    if (item.playsound) {
                                        if (item.sound != null) {
                                            ((Player) event.getWhoClicked()).playSound(event.getWhoClicked().getLocation(), item.sound, 1.0F, 1.0F);
                                        } else {
                                            this.pl.getLogger().warning("§fInvalid Configuration Option For \"" + item.name + "§f\" -> Sound");
                                        }
                                    }
                                }
                            });
                            plr.closeInventory();
                            plr.updateInventory();
                        }
                    }
                });
            }
        }
    }
}
