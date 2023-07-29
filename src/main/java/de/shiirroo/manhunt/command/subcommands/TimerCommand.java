package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TimerCommand extends SubCommand {

    @Override
    public String getName() {
        return "timer";
    }

    @Override
    public String getDescription() {
        return "显示游玩时间";
    }

    @Override
    public String getSyntax() {
        return "/manhunt timer";
    }

    @Override
    public Boolean getNeedOp() {
        return false;
    }

    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        return null;
    }


    @Override
    public void perform(Player player, String[] args) {
        if (args.length == 1) {
            if (ManHuntPlugin.getGameData().getGamePlayer().getPlayerShowGameTimer().contains(player.getUniqueId())) {
                ManHuntPlugin.getGameData().getGamePlayer().getPlayerShowGameTimer().remove(player.getUniqueId());
                player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "不再显示任何播放时间");
            } else {
                ManHuntPlugin.getGameData().getGamePlayer().getPlayerShowGameTimer().add(player.getUniqueId());
                player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.GREEN + "现在显示游玩时间");
            }
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "命令无法像这样执行");
        }
    }
}
