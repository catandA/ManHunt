package de.shiirroo.manhunt.event.menu.menus.setting.gamemode.modes;

import de.shiirroo.manhunt.event.menu.menus.setting.gamemode.CustomGameMode;
import de.shiirroo.manhunt.utilis.Utilis;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class RandomItems extends CustomGameMode {

    public final List<Material> mat = Arrays.stream(Material.values()).filter(Material::isItem).collect(Collectors.toList());
    public HashMap<Material, Material> randomItems = new HashMap<>();

    @Override
    public ItemStack displayItem() {
        return randomItemsItem();
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
            randomItems.clear();
            for (Material material : mat) {
                createRandom(material);
            }
        }
    }

    public HashMap<Material, Material> getRandomItems() {
        return randomItems;
    }

    public void setRandomItems(HashMap<Material, Material> randomItems) {
        this.randomItems = randomItems;
    }

    public void createRandom(Material material) {
        Material newMaterial = mat.get(Utilis.generateRandomInt(mat.size()));
        while (randomItems.containsValue(newMaterial)) {
            newMaterial = mat.get(Utilis.generateRandomInt(mat.size()));
        }
        randomItems.put(material, newMaterial);
    }

    private ItemStack randomItemsItem() {
        ItemStack itemStack = new ItemStack(Material.WOODEN_AXE, 1);
        ItemMeta meta = itemStack.getItemMeta();
        String s = value.toString().substring(0, 1).toUpperCase() + value.toString().substring(1).toLowerCase();
        meta.setDisplayName(ChatColor.GRAY + DisplayName() + ChatColor.GRAY + ": " + ((boolean) value ? ChatColor.GREEN : ChatColor.RED) + s);
        meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        meta.addItemFlags(ItemFlag.HIDE_ATTRIBUTES);
        meta.setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "所有合成的物品都是随机的")));
        itemStack.setItemMeta(meta);
        return itemStack;
    }

}
