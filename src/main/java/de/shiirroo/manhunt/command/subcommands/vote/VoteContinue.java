package de.shiirroo.manhunt.command.subcommands.vote;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import de.shiirroo.manhunt.utilis.repeatingtask.CompassTracker;
import de.shiirroo.manhunt.utilis.vote.VoteCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;

import java.util.Calendar;

public class VoteContinue extends Vote {
    @Override
    protected VoteCreator voteCreator() {
        return new VoteCreator(true, ManHuntPlugin.getPlugin(), ChatColor.GRAY + "继续游戏？" + ChatColor.GOLD + "VOTEPLAYERS " + ChatColor.BLACK + "| " + ChatColor.GOLD + "GAMEPLAYERS" + ChatColor.GRAY + " [ " + ChatColor.GREEN + "TIMER " + ChatColor.GRAY + "]", 30);
    }

    @Override
    protected void editBossBarCreator() {
        getBossBarCreator().setHowManyPlayers(ManHuntPlugin.getGameData().getGameStatus().getLivePlayerList().size());
        getBossBarCreator().setHowManyPlayersinPercent(100);
        getBossBarCreator().onComplete(aBoolean -> {
            if (aBoolean) {
                ManHuntPlugin.getGameData().getGamePause().setPause(false);
                ManHuntPlugin.getGameData().getGamePause().getUnPauseList().add(Calendar.getInstance().getTime().getTime());
                Bukkit.getOnlinePlayers().forEach(p ->
                {
                    CompassTracker.setPlayerlast(p);
                    if (!ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(p.getUniqueId()).equals(ManHuntRole.Speedrunner))
                        CompassTracker.updateCompass(p);
                });
            }
            VoteCommand.resetVote();
            ManHuntPlugin.getGameData().getPlayerData().updatePlayers(ManHuntPlugin.getTeamManager());
        });
        getBossBarCreator().onShortlyComplete(aBoolean1 -> Bukkit.getOnlinePlayers().forEach(current -> current.playSound(current.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)));
    }

    @Override
    protected boolean requirement() {
        return ManHuntPlugin.getGameData().getGamePause().isPause();
    }

    @Override
    protected String requirementMessage() {
        return ManHuntPlugin.getprefix() + "只有在没有暂停的情况下，才能结束暂停";
    }
}
