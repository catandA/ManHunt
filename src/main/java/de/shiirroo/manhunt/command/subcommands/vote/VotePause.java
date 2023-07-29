package de.shiirroo.manhunt.command.subcommands.vote;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.utilis.vote.VoteCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.Calendar;

public class VotePause extends Vote {

    @Override
    protected VoteCreator voteCreator() {
        return new VoteCreator(true, ManHuntPlugin.getPlugin(), ChatColor.GRAY + "暂停游戏？" + ChatColor.GOLD + "VOTEPLAYERS "
                + ChatColor.BLACK + "| " + ChatColor.GOLD + "ONLINEPLAYERS" + ChatColor.GRAY + " [ " + ChatColor.GREEN + "TIMER " + ChatColor.GRAY + "]", 30);
    }

    @Override
    protected void editBossBarCreator() {
        getBossBarCreator().setHowManyPlayersinPercent(75);
        getBossBarCreator().onComplete(aBoolean -> {
            if (aBoolean) {
                ManHuntPlugin.getGameData().getGamePause().setPause(true);
                ManHuntPlugin.getGameData().getGamePause().getPauseList().add(Calendar.getInstance().getTime().getTime());
            }
            VoteCommand.resetVote();
            ManHuntPlugin.getGameData().getPlayerData().updatePlayers(ManHuntPlugin.getTeamManager());
        });
        getBossBarCreator().onShortlyComplete(aBoolean1 -> Bukkit.getOnlinePlayers().forEach(current -> current.playSound(current.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)));
    }

    @Override
    protected boolean requirement() {
        return !ManHuntPlugin.getGameData().getGamePause().isPause();
    }

    @Override
    protected String requirementMessage() {
        return ManHuntPlugin.getprefix() + "只有在没有暂停的情况下，您才能投票支持暂停";
    }
}
