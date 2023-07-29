package de.shiirroo.manhunt.command.subcommands.extra;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.utilis.config.Config;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Reload extends SubCommand {


    @Override
    public String getName() {
        return "reload";
    }

    @Override
    public String getDescription() {
        return "重载配置文件";
    }

    @Override
    public String getSyntax() {
        return "/manhunt reload";
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
    public void perform(Player p, String[] args) {
        if (!p.isOp()) {
            p.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "你没权限用");
            return;
        }
        Config.relodConfig();
        p.sendMessage(ManHuntPlugin.getprefix() + "配置已重载");
        Bukkit.getLogger().info(ManHuntPlugin.getprefix() + ChatColor.GRAY + "配置已重载");
    }
}



