package de.shiirroo.manhunt.event.menu.menus.setting.gamesave;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.event.Events;
import de.shiirroo.manhunt.event.menu.*;
import de.shiirroo.manhunt.event.menu.menus.setting.WorldMenu;
import de.shiirroo.manhunt.gamedata.GameData;
import de.shiirroo.manhunt.utilis.repeatingtask.GameTimes;
import de.shiirroo.manhunt.world.save.SaveGame;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class GameSaveMenu extends Menu {


    public GameSaveMenu(PlayerMenuUtility playerMenuUtility) {
        super(playerMenuUtility);
    }

    @Override
    public String getMenuName() {
        return ChatColor.DARK_GREEN + "Game Save";
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
        //如果打开的人是OP
        if (e.getWhoClicked().isOp()) {
            if (ManHuntPlugin.getGameData().getGameStatus().getAutoSave().saveExists() && Objects.equals(e.getCurrentItem(), SaveGameItem(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()))) {
                MenuManager.getMenu(LoadSaveMenu.class, uuid).setName("从自动保存加载").setBack(hasBack).open();
            } else if (!ManHuntPlugin.getGameData().getGameStatus().getAutoSave().saveExists() && Objects.equals(e.getCurrentItem(), makeSaveGameItem(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()))) { //&& StartGame.bossBarGameStart != null && StartGame.bossBarGameStart.isRunning()){
                MenuManager.getMenu(LoadSaveMenu.class, uuid).setName("保存到自动保存").setBack(hasBack).open();
            } else {
                for (SaveGame savegame : WorldMenu.gameList) {
                    if (savegame.saveExists() && Objects.equals(e.getCurrentItem(), SaveGameItem(savegame))) {
                        MenuManager.getMenu(LoadSaveMenu.class, uuid).setName("加载" + savegame.getSaveName()).setBack(hasBack).open();
                        break;
                    } else if (!savegame.saveExists() && Objects.equals(e.getCurrentItem(), makeSaveGameItem(savegame))) {//&& StartGame.bossBarGameStart != null && StartGame.bossBarGameStart.isRunning()){
                        MenuManager.getMenu(LoadSaveMenu.class, uuid).setName("保存" + savegame.getSaveName()).setBack(hasBack).open();
                        break;
                    }
                }
            }
        }
        if (Objects.equals(e.getCurrentItem(), BACK_ITEM)) {
            back();
        } else if (Objects.equals(e.getCurrentItem(), CLOSE_ITEM)) {
            e.getWhoClicked().closeInventory();
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
        if (ManHuntPlugin.getGameData().getGameStatus().getAutoSave().saveExists()) {
            inventory.setItem(10, isLoading(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()) ? saveGameIsLoading(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()) : SaveGameItem(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()));
        } else {
            inventory.setItem(10, isLoading(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()) ? saveGameIsLoading(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()) : makeSaveGameItem(ManHuntPlugin.getGameData().getGameStatus().getAutoSave()));
        }
        for (SaveGame savegame : WorldMenu.gameList) {
            if (savegame.saveExists()) {
                inventory.setItem(11 + savegame.getSaveSlot(), isLoading(savegame) ? saveGameIsLoading(savegame) : SaveGameItem(savegame));
            } else {
                inventory.setItem(11 + savegame.getSaveSlot(), isLoading(savegame) ? saveGameIsLoading(savegame) : makeSaveGameItem(savegame));
            }
        }
        if (hasBack)
            inventory.setItem(31, BACK_ITEM);
        else
            inventory.setItem(31, CLOSE_ITEM);
        setFillerGlass(false);
    }

    private ItemStack saveGameIsLoading(SaveGame saveGame) {
        ItemStack saveGameIsLoadingItem = new ItemStack(Material.CLOCK);
        ItemMeta im = saveGameIsLoadingItem.getItemMeta();
        if (saveGame.getSaveSlot() == 0) {
            im.setDisplayName(ChatColor.GOLD + "-- 自动保存 --");
        } else {
            im.setDisplayName(ChatColor.GOLD + "-- 保存 " + saveGame.getSaveSlot() + " --");
        }
        List<String> listLore = new ArrayList<>(Arrays.asList("", ChatColor.BLUE + "正在加载..: "));
        im.setLore(listLore);
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        saveGameIsLoadingItem.setItemMeta(im);
        return saveGameIsLoadingItem;
    }

    private boolean isLoading(SaveGame saveGame) {
        return saveGame.isLoading();
    }


    private ItemStack SaveGameItem(SaveGame saveGame) {
        ItemStack GroupMenuGUI = new ItemStack(Material.WRITTEN_BOOK);
        ItemMeta im = GroupMenuGUI.getItemMeta();
        if (saveGame.getSaveSlot() == 0) {
            im.setDisplayName(ChatColor.GRAY + "----- " + ChatColor.GREEN + "自动保存" + ChatColor.GRAY + " -----");
        } else {
            im.setDisplayName(ChatColor.GRAY + "----- " + ChatColor.GREEN + "保存-" + saveGame.getSaveSlot() + ChatColor.GRAY + " -----");
        }
        List<String> listLore = new ArrayList<>(List.of(""));
        GameData gameData = saveGame.getGameSaveData();
        if (gameData.getGameStatus().isGameRunning()) {
            listLore.add(ChatColor.YELLOW + "➢ " + ChatColor.GOLD + "ID : " + ChatColor.GREEN + gameData.getId().getMostSignificantBits());
            listLore.add(ChatColor.YELLOW + "➢ " + ChatColor.GOLD + "游戏时间 : " + Events.getTimeString(false, GameTimes.getStartTime(gameData.getGameStatus().getGameStartTime(), gameData.getGamePause().getPauseList(), gameData.getGamePause().getUnPauseList())));
            listLore.add(ChatColor.YELLOW + "➢ " + ChatColor.GOLD + "暂停次数 : " + ChatColor.GREEN + gameData.getGamePause().getUnPauseList().size());
            listLore.add(ChatColor.YELLOW + "➢ " + ChatColor.GOLD + "存活玩家 : " + ChatColor.GREEN + gameData.getGameStatus().getLivePlayerList().size() + ChatColor.GRAY + " | " + ChatColor.GREEN + gameData.getGameStatus().getStartPlayerList().size());

        }
        listLore.addAll(Arrays.asList("", ChatColor.YELLOW + "● 创建于: " + ChatColor.GREEN + saveGame.getDateString()));
        im.setLore(listLore);
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        GroupMenuGUI.setItemMeta(im);
        return GroupMenuGUI;
    }

    private ItemStack makeSaveGameItem(SaveGame saveGame) {
        ItemStack GroupMenuGUI = new ItemStack(Material.WRITABLE_BOOK);
        ItemMeta im = GroupMenuGUI.getItemMeta();
        if (saveGame.getSaveSlot() == 0) {
            im.setDisplayName(ChatColor.GRAY + "----- " + ChatColor.GREEN + "自动保存" + ChatColor.GRAY + " -----");
        } else {
            im.setDisplayName(ChatColor.GRAY + "----- " + ChatColor.GREEN + "保存-" + saveGame.getSaveSlot() + ChatColor.GRAY + " -----");
        }
        if(!ManHuntPlugin.getGameData().getGameStatus().isGameRunning()){
            im.setLore(List.of("", ChatColor.RED  + "只能在",ChatColor.RED  +"游戏期间保存"));
        }
        im.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
        GroupMenuGUI.setItemMeta(im);
        return GroupMenuGUI;
    }

}
