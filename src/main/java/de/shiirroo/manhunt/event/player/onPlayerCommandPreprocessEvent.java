package de.shiirroo.manhunt.event.player;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.subcommands.Ready;
import de.shiirroo.manhunt.command.subcommands.StartGame;
import de.shiirroo.manhunt.command.subcommands.StopGame;
import de.shiirroo.manhunt.command.subcommands.vote.VoteCommand;
import de.shiirroo.manhunt.event.Events;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class onPlayerCommandPreprocessEvent implements Listener {

    public static void removeBossBar() {
        if (ManHuntPlugin.getGameData().getGameStatus().isStarting()) {
            StartGame.bossBarGameStart.cancel();
            StopGame.ResetGameWorld();
        } else if (VoteCommand.getVote() != null && VoteCommand.getVote().getVoteCreator().getbossBarCreator().isRunning()) {
            VoteCommand.getVote().getVoteCreator().cancelVote();
            VoteCommand.resetVote();
        } else if (ManHuntPlugin.getWorldreset().getWorldReset().isRunning()) {
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.kickPlayer(ManHuntPlugin.getprefix() + "World is Resetting..");
            }
            ManHuntPlugin.getPlugin().getConfig().set("isReset", true);
            ManHuntPlugin.getPlugin().saveConfig();
            Bukkit.spigot().restart();
        } else if (Ready.ready.getbossBarCreator().isRunning()) {
            Ready.ready.cancelVote();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void PlayerCommandPreprocessEvent(PlayerCommandPreprocessEvent event) {
        if (event.getPlayer().isOp()) {
            List<String> chars = new ArrayList<>(Arrays.asList(event.getMessage().split(" ")));
            boolean eventBool = Events.UpdatePlayerInventory(chars, event.getPlayer().getName());
            if (eventBool) {
                event.setCancelled(true);
                if (!event.getPlayer().getName().equalsIgnoreCase(chars.get(1))) {
                    if (chars.get(0).equalsIgnoreCase("/op"))
                        event.getPlayer().sendMessage(ManHuntPlugin.getprefix() + ChatColor.GOLD + chars.get(1) + ChatColor.GRAY + " 已被提升为管理员");
                    else if (chars.get(0).equalsIgnoreCase("/deop"))
                        event.getPlayer().sendMessage(ManHuntPlugin.getprefix() + ChatColor.GOLD + chars.get(1) + ChatColor.GRAY + " 现在不再是管理员");
                }
            }

        }
        if (event.getPlayer().isOp()) {
            List<String> chars = new ArrayList<>(Arrays.asList(event.getMessage().split(" ")));
            if ((chars.size() == 1 && chars.get(0).equalsIgnoreCase("/reload")) ||
                    (chars.size() == 2 && chars.get(0).equalsIgnoreCase("/reload") && chars.get(0).equalsIgnoreCase("confirm"))
            ) {
                if (chars.get(0).equalsIgnoreCase("/reload")) {
                    event.setCancelled(true);
                    if (ManHuntPlugin.getGameData().getGameStatus().isGame()) {
                        removeBossBar();
                    }
                    Bukkit.reload();
                }
            }
        }
    }

}
