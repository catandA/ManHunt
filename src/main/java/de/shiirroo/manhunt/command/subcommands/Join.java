package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePresetMenu;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class Join extends SubCommand {

    @Override
    public String getName() {
        return "join";
    }

    @Override
    public String getDescription() {
        return "您可以加入组" + setDescription();
    }

    @Override
    public String getSyntax() {
        return "/manhunt join [组名]";
    }

    @Override
    public Boolean getNeedOp() {
        return true;
    }

    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        CommandBuilder join = new CommandBuilder(getName());
        for (ManHuntRole m : ManHuntRole.values()) {
            if (m.equals(ManHuntRole.Unassigned)) continue;
            join.addSubCommandBuilder(new CommandBuilder(m.toString()));
        }
        return join;
    }

    public ArrayList<String> setDescription() {
        ArrayList description = new ArrayList<>();
        for (ManHuntRole m : ManHuntRole.values()) {
            if (m.equals(ManHuntRole.Unassigned)) continue;
            description.add(m);
        }
        return description;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            player.sendMessage(ManHuntPlugin.getprefix() + "运行比赛时无法换队伍");
        } else if(Ready.ready != null && !Ready.ready.getbossBarCreator().isRunning() && !Ready.ready.hasPlayerVote(player)){
            List<String> list = Stream.of(ManHuntRole.values()).map(ManHuntRole::toString).toList();
            if (args.length == 1) {
                player.sendMessage(ManHuntPlugin.getprefix() + getDescription());
            } else {
                String group = args[1].substring(0, 1).toUpperCase() + args[1].substring(1);
                if (list.contains(group)) {
                    joinGroup(player, ManHuntRole.valueOf(group));
                } else {
                    player.sendMessage(ManHuntPlugin.getprefix() + "未找到： " + ChatColor.GOLD + group);
                }
            }
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + "准备状态下无法换队伍");
        }
    }

    public static boolean joinGroup(Player player, ManHuntRole manHuntRole) {
        ManHuntRole mHR = ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId());
        if(!mHR.equals(manHuntRole)) {
        if (GamePresetMenu.preset.getMaxPlayerPerSize(manHuntRole).equalsIgnoreCase("ထ")
                || Integer.parseInt(GamePresetMenu.preset.getMaxPlayerPerSize(manHuntRole)) > ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(manHuntRole).size()) {
            if (!mHR.equals(ManHuntRole.Unassigned)) {
                    ManHuntPlugin.getGameData().getPlayerData().setRole(player, manHuntRole, ManHuntPlugin.getTeamManager());
                    player.sendMessage(ManHuntPlugin.getprefix() + "你离开了" + ChatColor.GOLD + mHR + ChatColor.GRAY + "并加入了新队伍：" + ChatColor.GOLD + manHuntRole);
                    TeamChat.leaveChat(player);
                    return true;
                } else {
                    ManHuntPlugin.getGameData().getPlayerData().reset(player, ManHuntPlugin.getTeamManager());
                    ManHuntPlugin.getGameData().getPlayerData().setRole(player, manHuntRole, ManHuntPlugin.getTeamManager());
                    player.sendMessage(ManHuntPlugin.getprefix() + "您已加入该队：" + ChatColor.GOLD + manHuntRole);
                    TeamChat.leaveChat(player);
                    return true;

                }
            } else {
                ManHuntPlugin.getGameData().getPlayerData().setRole(player, manHuntRole, ManHuntPlugin.getTeamManager());
                player.sendMessage(ManHuntPlugin.getprefix() + "您已加入该队：" + ChatColor.GOLD + manHuntRole);
                return true;
            }
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + "您不能加入同一队");
        }
        player.sendMessage(ManHuntPlugin.getprefix() + "你要加的队伍满人了");
        return false;
    }
}



