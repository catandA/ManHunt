package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Reset extends SubCommand {

    @Override
    public String getName() {
        return "reset";
    }

    @Override
    public String getDescription() {
        return "重置世界";
    }

    @Override
    public String getSyntax() {
        return "/manhunt reset";
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
        Bukkit.setWhitelist(true);
        for (Player p : Bukkit.getOnlinePlayers()) {
            p.kickPlayer(ManHuntPlugin.getprefix() + "重置世界中..");
        }
        ManHuntPlugin.getPlugin().getConfig().set("isReset", true);
        ManHuntPlugin.getPlugin().saveConfig();
        Bukkit.spigot().restart();
    }
}



