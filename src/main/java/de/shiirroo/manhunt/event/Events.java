package de.shiirroo.manhunt.event;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.subcommands.Ready;
import de.shiirroo.manhunt.command.subcommands.StartGame;
import de.shiirroo.manhunt.event.menu.Menu;
import de.shiirroo.manhunt.event.menu.menus.PlayerMenu;
import de.shiirroo.manhunt.event.player.onPlayerCommandPreprocessEvent;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import de.shiirroo.manhunt.utilis.repeatingtask.GameTimes;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class Events implements Listener, Serializable {

    public static boolean UpdatePlayerInventory(List<String> chars, String PlayerName) {
        if (chars.size() == 2 && (chars.get(0).equalsIgnoreCase("/op") || chars.get(0).equalsIgnoreCase("/deop") || chars.get(0).equalsIgnoreCase("op") || chars.get(0).equalsIgnoreCase("deop"))) {
            Player UpdatePlayer = Bukkit.getPlayer(chars.get(1));
            if (UpdatePlayer != null && UpdatePlayer.isOnline()) {
                if (UpdatePlayer.isOp()) {
                    UpdatePlayer.setOp(false);
                    ManHuntPlugin.getTeamManager().changePlayerName(UpdatePlayer, ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(UpdatePlayer.getUniqueId()));
                    if (!UpdatePlayer.getName().equalsIgnoreCase(PlayerName)) {
                        UpdatePlayer.sendMessage(ManHuntPlugin.getprefix() + "您的操作员已被移除");
                    }


                    if(PlayerMenu.SettingMenu.containsKey(UpdatePlayer.getUniqueId())){
                        PlayerMenu.SettingMenu.get(UpdatePlayer.getUniqueId()).setMenuItems();
                    }
                } else {
                    UpdatePlayer.setOp(true);
                    ManHuntPlugin.getTeamManager().changePlayerName(UpdatePlayer, ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(UpdatePlayer.getUniqueId()));
                    if (!UpdatePlayer.getName().equalsIgnoreCase(PlayerName)) {
                        UpdatePlayer.sendMessage(ManHuntPlugin.getprefix() + "您被提升为操作员，现在可以执行 ManHunt 命令");
                    }
                    if(PlayerMenu.SettingMenu.containsKey(UpdatePlayer.getUniqueId())){
                        PlayerMenu.SettingMenu.get(UpdatePlayer.getUniqueId()).setMenuItems();
                    }
                }
                if (!ManHuntPlugin.getGameData().getGameStatus().isGame())
                    ManHuntPlugin.playerMenu.get(UpdatePlayer.getUniqueId()).setMenuItems();
                return true;
            }
        }
        return false;
    }

    public static String getTimeString(Boolean space, Long time) {
        String pauseString = ChatColor.GRAY + "";
        long diffSeconds = time / 1000 % 60;
        long diffMinutes = time / (60 * 1000) % 60;
        long diffHours = time / (60 * 60 * 1000);
        if (diffHours != 0) {
            if (space) pauseString = "  ";
            pauseString += "[ " + ChatColor.GREEN + "" + diffHours + ChatColor.GRAY + " h : " + ChatColor.GREEN + diffMinutes + ChatColor.GRAY + " m";
        } else if (diffMinutes != 0) {
            if (space) pauseString = "     ";
            pauseString += "[ " + ChatColor.GREEN + "" + diffMinutes + ChatColor.GRAY + " m : " + ChatColor.GREEN + diffSeconds + ChatColor.GRAY + " s";
        } else {
            if (space) pauseString = "          ";
            pauseString += "[ " + ChatColor.GREEN + "" + diffSeconds + ChatColor.GRAY + " s";
        }
        return pauseString + ChatColor.GRAY + " ]";
    }

    public static boolean cancelEvent(Player player) {
        return (!player.getGameMode().equals(GameMode.CREATIVE) && ((ManHuntPlugin.getGameData().getGameStatus().isGame() && ManHuntPlugin.getGameData().getGamePause().isPause()) ||
                (ManHuntPlugin.getGameData().getGamePlayer().getIsFrozen().entrySet().stream().anyMatch(uuiduuidEntry -> uuiduuidEntry.getValue().equals(player.getUniqueId()))) ||
                (!ManHuntPlugin.getGameData().getPlayerData().getPlayerRoleByUUID(player.getUniqueId()).equals(ManHuntRole.Speedrunner)
                        && ManHuntPlugin.getGameData().getGameStatus().isStarting()) || (ManHuntPlugin.getGameData().getGameStatus().isReadyForVote() && !ManHuntPlugin.getGameData().getGameStatus().isGame())));
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerCommandEvent(ServerCommandEvent event) {
        List<String> chars = new ArrayList<>(Arrays.asList(event.getCommand().split(" ")));
        boolean eventBool = UpdatePlayerInventory(chars, null);
        if (eventBool) {
            event.setCancelled(true);
        }

        if ((chars.size() == 1 && (chars.get(0).equalsIgnoreCase("/reload") || chars.get(0).equalsIgnoreCase("reload"))) ||
                (chars.size() == 2 && (chars.get(0).equalsIgnoreCase("/reload") || chars.get(0).equalsIgnoreCase("reload")) && chars.get(0).equalsIgnoreCase("confirm"))) {
            event.setCancelled(true);
            onPlayerCommandPreprocessEvent.removeBossBar();
            Bukkit.reload();
        }
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPingEvent(ServerListPingEvent event) {
        Player player = null;
        if (ManHuntPlugin.getGameData().getGamePlayer().getPlayerIP() != null && ManHuntPlugin.getGameData().getGamePlayer().getPlayerIP().get(event.getAddress().getHostAddress()) != null && event.getAddress().getHostAddress() != null) {
            player = Bukkit.getPlayer(ManHuntPlugin.getGameData().getGamePlayer().getPlayerIP().get(event.getAddress().getHostAddress()));
        }

        if (ManHuntPlugin.getGameData().getGameStatus().isReadyForVote())
            event.setMotd(ManHuntPlugin.getprefix() + "猎杀还没" + ChatColor.GREEN + "开始.." + "\n" + ManHuntPlugin.getprefix() + ChatColor.GREEN + "快进来玩");
        else if (Ready.ready.getbossBarCreator().isRunning() && Ready.ready.getbossBarCreator().getTimer() > 3)
            event.setMotd(ManHuntPlugin.getprefix() + "猎杀" + ChatColor.GREEN + " 即将开始于" + ChatColor.GOLD + Ready.ready.getbossBarCreator().getTimer() + ChatColor.GREEN + " 秒\n" + ManHuntPlugin.getprefix() + ChatColor.GREEN + "快进来玩");
        else if (Ready.ready.getbossBarCreator().isRunning() && Ready.ready.getbossBarCreator().getTimer() <= 3)
            event.setMotd(ManHuntPlugin.getprefix() + "猎杀" + ChatColor.GREEN + " 即将开始于" + ChatColor.GOLD + Ready.ready.getbossBarCreator().getTimer() + ChatColor.GREEN + " 秒\n" + ManHuntPlugin.getprefix() + ChatColor.GREEN + "你不许参加淫趴");
        else if (ManHuntPlugin.getGameData().getGameStatus().isStarting())
            event.setMotd(ManHuntPlugin.getprefix() + "猎杀" + ChatColor.YELLOW + " 即将开始于" + ChatColor.GOLD + StartGame.bossBarGameStart.getTimer() + ChatColor.YELLOW + " 秒\n" + ManHuntPlugin.getprefix() + ChatColor.YELLOW + (player != null && player.isWhitelisted() ? ChatColor.GOLD + player.getName() + ChatColor.GREEN + "快进来玩" : ChatColor.RED + "你不许参加淫趴"));
        else if (!ManHuntPlugin.getGameData().getGamePause().isPause()) {
            event.setMotd(ManHuntPlugin.getprefix() + "猎杀" + ChatColor.RED + " 开始于: " + ChatColor.GRAY + getTimeString(true, GameTimes.getStartTime(ManHuntPlugin.getGameData().getGameStatus().getGameStartTime(), ManHuntPlugin.getGameData().getGamePause().getPauseList(), ManHuntPlugin.getGameData().getGamePause().getUnPauseList())) +
                    "\n" + ManHuntPlugin.getprefix() + ChatColor.YELLOW + (player != null && player.isWhitelisted() ? ChatColor.GOLD + player.getName() + ChatColor.GREEN + " 快进来玩" : ChatColor.RED + "你不许参加淫趴"));
        } else
            event.setMotd(ManHuntPlugin.getprefix() + "猎杀" + ChatColor.AQUA + " 暂停于: " + ChatColor.GRAY + getTimeString(true, Calendar.getInstance().getTime().getTime() - ManHuntPlugin.getGameData().getGamePause().getPauseList().get((ManHuntPlugin.getGameData().getGamePause().getPauseList().size() - 1))) + "\n" + ManHuntPlugin.getprefix() + ChatColor.YELLOW + (player != null && player.isWhitelisted() ? ChatColor.GOLD + player.getName() + ChatColor.GREEN + "快进来玩" : ChatColor.RED + "你不许参加淫趴"));

        if (!ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            event.setMaxPlayers(event.getNumPlayers());
        } else {
            event.setMaxPlayers((int) Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count());
        }

    }

}