package de.shiirroo.manhunt.event.menu.menus.setting;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.event.menu.*;
import de.shiirroo.manhunt.event.menu.menus.setting.gamemode.GameModeMenu;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePresetMenu;
import de.shiirroo.manhunt.utilis.Utilis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class SettingsMenu extends Menu implements Serializable {

    public static final HashMap<UUID, Menu> ConfigMenu = new HashMap<>();
    public static final HashMap<UUID, Menu> GamePreset = new HashMap<>();
    public static final HashMap<UUID, Menu> GameMode = new HashMap<>();
    public static final HashMap<UUID, Menu> PlayerConfigMenu = new HashMap<>();

    public SettingsMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return "设置菜单";
    }

    @Override
    public InventoryType getInventoryType() {
        return InventoryType.CHEST;
    }

    @Override
    public int getSlots() {
        return 36;
    }

    @Override
    public boolean cancelAllClicks() {
        return true;
    }

    @Override
    public void handleMenuClickEvent(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException {
        Player p = (Player) e.getWhoClicked();
        if (!ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            if (Objects.equals(e.getCurrentItem(), ConfigGame())) {
                ConfigMenu.put(p.getUniqueId(), MenuManager.getMenu(ConfigMenu.class, p.getUniqueId()).open());
            } else if (Objects.equals(e.getCurrentItem(), CLOSE_ITEM)) {
                p.closeInventory();
            } else if (Objects.equals(e.getCurrentItem(), GamePresets())) {
                GamePreset.put(p.getUniqueId(), MenuManager.getMenu(GamePresetMenu.class, p.getUniqueId()).open());
            } else if (Objects.equals(e.getCurrentItem(), GameMode())) {
                GameMode.put(p.getUniqueId(), MenuManager.getMenu(GameModeMenu.class, p.getUniqueId()).open());
            } else if (Objects.requireNonNull(e.getCurrentItem()).getItemMeta() != null && e.getCurrentItem().getItemMeta() != null && e.getCurrentItem().getItemMeta() instanceof SkullMeta i) {
                if (Objects.equals(i.getOwner(), Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName())) {
                    PlayerConfigMenu.put(p.getUniqueId(), MenuManager.getMenu(PlayerConfigMenu.class, p.getUniqueId()).open());
                }
            }
        }
    }

    @Override
    public void handlePlayerDropItemEvent(PlayerDropItemEvent e) {

    }

    @Override
    public void handlePlayerInteractEvent(PlayerInteractEvent e) {
    }

    @Override
    public void setMenuItems() {
        inventory.setItem(10, ConfigGame());
        inventory.setItem(12, PlayerSetting());
        inventory.setItem(14, GameMode());
        inventory.setItem(16, GamePresets());
        inventory.setItem(31, CLOSE_ITEM);
        setFillerGlass(false);
    }

    private ItemStack ConfigGame() {
        ItemStack GroupMenuGUI = new ItemStack(Material.COMPARATOR);
        ItemMeta im = GroupMenuGUI.getItemMeta();
        im.setDisplayName(ChatColor.GOLD + "" + ChatColor.BOLD + "猎人" + ChatColor.RED + ChatColor.BOLD + "游戏" + ChatColor.DARK_GRAY + ChatColor.BOLD + " 配置");
        if(!getPlayer().isOp()) im.setLore(List.of("", ChatColor.GOLD + "➤ "  + ChatColor.GRAY + "浏览模式"));
        else im.setLore(List.of("", ChatColor.GOLD + "➤ "  + ChatColor.GREEN + "编辑模式"));
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        GroupMenuGUI.setItemMeta(im);
        return GroupMenuGUI;
    }

    private ItemStack PlayerSetting() {
        ItemStack playHead = Utilis.getPlayHead();
        SkullMeta im = (SkullMeta) playHead.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + "用户配置");
        im.setOwner(Objects.requireNonNull(Bukkit.getPlayer(uuid)).getName());
        playHead.setItemMeta(im);
        return playHead;
    }

    private ItemStack GameMode() {
        ItemStack GroupMenuGUI = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta im = GroupMenuGUI.getItemMeta();
        im.setDisplayName(ChatColor.RED + "" + ChatColor.BOLD + "游戏模式");
        if(!getPlayer().isOp()) im.setLore(List.of("", ChatColor.GOLD + "➤ "  + ChatColor.GRAY + "浏览模式"));
        else im.setLore(List.of("", ChatColor.GOLD + "➤ "  + ChatColor.GREEN + "编辑模式"));
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        GroupMenuGUI.setItemMeta(im);
        return GroupMenuGUI;
    }

    private ItemStack GamePresets() {
        ItemStack GroupMenuGUI = new ItemStack(Material.ENCHANTED_BOOK);
        ItemMeta im = GroupMenuGUI.getItemMeta();
        im.setDisplayName(ChatColor.BLUE +""+ ChatColor.BOLD + "游戏预设");
        if(!getPlayer().isOp()) im.setLore(List.of("", ChatColor.GOLD + "➤ "  + ChatColor.GRAY + "浏览模式"));
        else im.setLore(List.of("", ChatColor.GOLD + "➤ "  + ChatColor.GREEN + "编辑模式"));
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        GroupMenuGUI.setItemMeta(im);
        return GroupMenuGUI;
    }

}
