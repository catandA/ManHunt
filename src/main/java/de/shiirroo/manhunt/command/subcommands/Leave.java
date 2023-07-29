package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class Leave extends SubCommand {

    @Override
    public String getName() {
        return "leave";
    }

    @Override
    public String getDescription() {
        return "离开队伍";
    }

    @Override
    public String getSyntax() {
        return "/manhunt leave";
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
        if (ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId()) == null) {
            player.sendMessage(ManHuntPlugin.getprefix() + "你没加队伍");
        } else if (!ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            if (!ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId()).equals(ManHuntRole.Unassigned)) {
                player.sendMessage(ManHuntPlugin.getprefix() + "你离开了队伍：" + ChatColor.GOLD + ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId()));
                ManHuntPlugin.getGameData().getPlayerData().setRole(player, ManHuntRole.Unassigned, ManHuntPlugin.getTeamManager());
                TeamChat.leaveChat(player);
            } else {
                player.sendMessage(ManHuntPlugin.getprefix() + "你不能离开这个队伍");
            }
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + "正在比赛时无法离队");
        }
    }

}



