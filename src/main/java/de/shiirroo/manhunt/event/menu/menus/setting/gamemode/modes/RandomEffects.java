package de.shiirroo.manhunt.event.menu.menus.setting.gamemode.modes;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.event.menu.menus.setting.gamemode.CustomGameMode;
import de.shiirroo.manhunt.utilis.Utilis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class RandomEffects extends CustomGameMode implements Serializable {
    @Override
    public ItemStack displayItem() {
        return potionItem();
    }

    @Override
    public Object defaultValue() {
        return minValue();
    }

    @Override
    protected Object minValue() {
        return false;
    }

    @Override
    protected Object maxValue() {
        return true;
    }

    @Override
    public void init(Player p) {
        if (value.equals(maxValue())) {
            value = minValue();
        } else {
            value = maxValue();
        }
    }

    @Override
    public void execute() {
        if ((boolean) value) {
            for (Player player : Bukkit.getOnlinePlayers()) {
                if (player.getGameMode().equals(GameMode.SURVIVAL)) {
                    int id = Utilis.generateRandomInt(PotionEffectType.values().length - 1);
                    for (PotionEffectType potionEffect : PotionEffectType.values()) {
                        if (id > 0) id--;
                        if (id == 0) {
                            int time = Utilis.generateRandomInt(10) + 6;
                            int strength = Utilis.generateRandomInt(2) + 1;
                            if (potionEffect.equals(PotionEffectType.HARM)) {
                                strength = 1;
                            }
                            String[] strings = potionEffect.getName().split("_");
                            StringBuilder s = new StringBuilder();
                            for (String string : strings) {
                                s.append(string.substring(0, 1).toUpperCase()).append(string.substring(1).toLowerCase()).append(" ");
                            }
                            player.addPotionEffect(new PotionEffect(potionEffect, time * 20, strength - 1, true, false));
                            player.sendMessage(ManHuntPlugin.getprefix() + "你拥有了 " + ChatColor.GOLD + s + strength + (potionEffect.isInstant() ? "" : String.valueOf(ChatColor.GREEN) + time + ChatColor.GRAY + " 秒"));
                            break;
                        }
                    }
                }
            }
        }
    }

    private ItemStack potionItem() {
        ItemStack potion = new ItemStack(Material.POTION, 1);
        PotionMeta meta = (PotionMeta) potion.getItemMeta();
        meta.setBasePotionData(new PotionData(PotionType.POISON));
        String s = value.toString().substring(0, 1).toUpperCase() + value.toString().substring(1).toLowerCase();
        meta.setDisplayName(ChatColor.DARK_AQUA + DisplayName() + ChatColor.GRAY + ": " + ((boolean) value ? ChatColor.GREEN : ChatColor.RED) + s);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "每个玩家每分钟都会获得一种随机的药水效果")));
        potion.setItemMeta(meta);
        return potion;
    }
}