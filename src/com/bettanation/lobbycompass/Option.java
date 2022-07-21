package com.bettanation.lobbycompass;

import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;

public final class Option {
   String name;
   List<String> lore;
   Material material;
   List<String> cmds;
   boolean glowing;
   boolean playsound;
   Sound sound;
   boolean executedByPlayer;
   int cost;
   int positionInInventory;
}