package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class StopGame extends SubCommand {

    @Override
    public String getName() {
        return "stop";
    }

    @Override
    public String getDescription() {
        return "停止比赛";
    }

    @Override
    public String getSyntax() {
        return "/manhunt stop";
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
        if (ManHuntPlugin.getGameData().getGameStatus().isStarting() || ManHuntPlugin.getGameData().getGameStatus().isGameRunning()) {
            ResetGameWorld();
            player.sendMessage(ManHuntPlugin.getprefix() + "游戏停止并重置");
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + "游戏不在进行");
        }
    }

    public static void ResetGameWorld() {
        StartGame.bossBarGameStart.cancel();
        StartGame.bossBarGameStart = StartGame.createBossBarGameStart();
        Ready.ready.cancelVote();
        Ready.ready = Ready.setReadyVote();
        ManHuntPlugin.GameTimesTimer = 1;
        if (ManHuntPlugin.getWorldreset().getWorldReset().isRunning()) {
            ManHuntPlugin.getWorldreset().getWorldReset().cancel();
        }
        ManHuntPlugin.getGameData().reset();
        ManHuntPlugin.setUPWorld();
        ManHuntPlugin.getPlugin().getServer().setDefaultGameMode(GameMode.ADVENTURE);
        Bukkit.getLogger().info(ManHuntPlugin.getprefix() + ChatColor.GRAY + "游戏已停止");
    }

}





