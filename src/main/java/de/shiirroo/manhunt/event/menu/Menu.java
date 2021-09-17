package de.shiirroo.manhunt.event.menu;

import de.shiirroo.manhunt.teams.model.ManHuntRole;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.DyeColor;
import org.bukkit.Material;
import org.bukkit.block.banner.Pattern;
import org.bukkit.block.banner.PatternType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.BannerMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public abstract class Menu implements InventoryHolder {

    protected PlayerMenuUtility playerMenuUtility;
    protected Player p;
    protected Inventory inventory;
    protected PlayerInventory inventoryPlayer;
    protected ItemStack FILLER_GLASS = makeItem(Material.GRAY_STAINED_GLASS_PANE, " ");
    protected ItemStack CLOSE_ITEM = makeItem(Material.BARRIER, ChatColor.RED + "CLOSE");
    List<DyeColor> colorBACK = Arrays.asList(DyeColor.BLACK, DyeColor.BLACK, DyeColor.WHITE,DyeColor.WHITE, DyeColor.WHITE);
    List<PatternType> patternTypeBACK = Arrays.asList(PatternType.STRIPE_LEFT, PatternType.STRIPE_MIDDLE, PatternType.STRIPE_TOP,PatternType.STRIPE_BOTTOM, PatternType.CURLY_BORDER);
    protected ItemStack BACK_ITEM = getItemStackBanner(ChatColor.GREEN + "BACK", Material.WHITE_BANNER, colorBACK, patternTypeBACK, "#5555FF");
    protected InventoryType inventoryType;
    protected String name;
    public Menu(PlayerMenuUtility playerMenuUtility) {
        this.playerMenuUtility = playerMenuUtility;
        this.p = playerMenuUtility.getOwner();
    }

    public abstract String getMenuName();

    public abstract InventoryType getInventoryType();

    public abstract int getSlots();

    public abstract boolean cancelAllClicks();

    public abstract void handleMenuClickEvent(InventoryClickEvent e) throws MenuManagerNotSetupException, MenuManagerException;

    public abstract void handlePlayerDropItemEvent(PlayerDropItemEvent e) throws MenuManagerNotSetupException, MenuManagerException;

    public abstract void handlePlayerInteractEvent(PlayerInteractEvent e) throws MenuManagerNotSetupException, MenuManagerException;

    public abstract void setMenuItems();

    public void open(String name) {
        this.name = name;

        if((getInventoryType() == null || getInventoryType().equals(InventoryType.CHEST)) && getSlots() != 0) {
            inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        } else if(getInventoryType().equals(InventoryType.PLAYER)){
            inventory = playerMenuUtility.getOwner().getInventory();
            playerMenuUtility.setData(this.playerMenuUtility.getOwner().getUniqueId().toString(), this);

        } else {
            inventory = Bukkit.createInventory(this, getInventoryType(), getMenuName());
        }

        this.setMenuItems();

       if(!getInventoryType().equals(InventoryType.PLAYER))
           playerMenuUtility.getOwner().openInventory(inventory);
        playerMenuUtility.pushMenu(this);
    }

    public void back() throws MenuManagerException, MenuManagerNotSetupException {
        MenuManager.openMenu(playerMenuUtility.lastMenu().getClass(), playerMenuUtility.getOwner(), null);
    }

    protected void reloadItems() {
        for (int i = 0; i < inventory.getSize(); i++){
            inventory.setItem(i, null);
        }
        setMenuItems();
    }

    protected void reload() throws MenuManagerException, MenuManagerNotSetupException {
        p.closeInventory();
        MenuManager.openMenu(this.getClass(), p, null);
    }




    @Override
    public @NotNull Inventory getInventory() {
        return inventory;
    }

    public void setFillerGlass(Boolean showID){
        for (int i = 0; i < inventory.getSize(); i++) {
            if (inventory.getItem(i) == null){
                if(showID)
                    inventory.setItem(i, makeItem(Material.GRAY_STAINED_GLASS_PANE, ChatColor.GOLD + (String.valueOf(i))));
                 else
                inventory.setItem(i, FILLER_GLASS);
            }
        }
    }

    protected ItemStack makeItem(Material material, String displayName) {

        ItemStack item = new ItemStack(material);
        ItemMeta itemMeta = item.getItemMeta();
        assert itemMeta != null;
        itemMeta.setDisplayName(displayName);
        item.setItemMeta(itemMeta);

        return item;
    }

    protected ItemStack getItemStackBanner(String displayname, Material banner, List<DyeColor> dyeColors, List<PatternType> patternTypes, String hex){
        ItemStack is = new ItemStack(banner);
        BannerMeta bannerMeta = (BannerMeta) is.getItemMeta();
        if(dyeColors.size() == patternTypes.size()) {
            for (int i = 0; i != dyeColors.size(); i++)
                bannerMeta.addPattern(new Pattern(dyeColors.get(i), patternTypes.get(i)));
            bannerMeta.displayName(Component.text(displayname).color(TextColor.fromHexString(hex)));
            bannerMeta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
            is.setItemMeta(bannerMeta);
        }
        return is;
    }

    private boolean isNewVersionHead(){
        return Arrays.stream(Material.values()).map(Material::name).collect(Collectors.toList()).contains("PLAYER_HEAD");
    }
    protected ItemStack getPlayHead(){
    Material type = Material.matchMaterial(isNewVersionHead() ? "PLAYER_HEAD": "SKULL_ITEM");
        assert type != null;
        ItemStack playHead = new ItemStack(type, 1);
        if(!isNewVersionHead())
            playHead.setDurability((short) 3);
    return playHead;
    }
}

