package de.shiirroo.manhunt.event.menu.menus.setting.gamemode.modes;

import de.shiirroo.manhunt.event.menu.menus.setting.gamemode.CustomGameMode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

public class WorldBorderSize extends CustomGameMode implements Serializable {
    @Override
    public ItemStack displayItem() {
        return worldBorderItem();
    }

    @Override
    public Object defaultValue() {
        return 20000000;
    }


    @Override
    public Object minValue() {
        return 1000;
    }

    @Override
    public Object maxValue() {
        return defaultValue();
    }

    @Override
    public void init(Player player) {
        openAnvilGUI(player, "");
    }

    @Override
    public void execute() {
        for (World w : Bukkit.getWorlds()) {
            w.getWorldBorder().reset();
            w.getWorldBorder().setCenter(w.getSpawnLocation());
            w.getWorldBorder().setSize((int) getValue());
        }
    }

    private ItemStack worldBorderItem() {
        ItemStack itemStack = new ItemStack(Material.BARRIER);
        ItemMeta itemMeta = itemStack.getItemMeta();
        itemMeta.setDisplayName(DisplayName() + ChatColor.GRAY + ": " + ChatColor.GREEN + value);
        itemMeta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "设置世界边界大小")));
        itemStack.setItemMeta(itemMeta);
        return itemStack;
    }
}
