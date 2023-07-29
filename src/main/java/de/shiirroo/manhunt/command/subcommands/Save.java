package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.event.menu.MenuManager;
import de.shiirroo.manhunt.event.menu.MenuManagerException;
import de.shiirroo.manhunt.event.menu.MenuManagerNotSetupException;
import de.shiirroo.manhunt.event.menu.menus.setting.gamesave.GameSaveMenu;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Save extends SubCommand {

    @Override
    public String getName() {
        return "save";
    }

    @Override
    public String getDescription() {
        return "保存游戏数据以便稍后继续";
    }

    @Override
    public String getSyntax() {
        return "/manhunt save";
    }

    @Override
    public Boolean getNeedOp() {
        return true;
    }


    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        return null;
    }


    @Override
    public void perform(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "你没权限用这指令");
            return;
        }
        if (args.length == 1) {
            try {
                MenuManager.getMenu(GameSaveMenu.class, player.getUniqueId()).setBack(false).open();
            } catch (MenuManagerException | MenuManagerNotSetupException e) {
                Bukkit.getLogger().info(ManHuntPlugin.getprefix() + ChatColor.RED + "打开存档菜单时发生错误");
            }
        }
    }
}
