package de.shiirroo.manhunt.event;

import de.shiirroo.manhunt.*;
import de.shiirroo.manhunt.command.subcommands.Ready;
import de.shiirroo.manhunt.command.subcommands.StartGame;
import de.shiirroo.manhunt.command.subcommands.VoteCommand;
import de.shiirroo.manhunt.event.menu.Menu;
import de.shiirroo.manhunt.event.player.onPlayerJoin;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import de.shiirroo.manhunt.utilis.repeatingtask.GameTimes;
import de.shiirroo.manhunt.world.PlayerWorld;
import de.shiirroo.manhunt.world.Worldreset;
import net.kyori.adventure.text.Component;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerCommandEvent;
import org.bukkit.event.server.ServerListPingEvent;
import java.io.IOException;
import java.util.*;


public class Events implements Listener {

    public static Map<UUID, ManHuntRole> players = new HashMap<>();
    public static Map<UUID, Menu> playerMenu = new HashMap<>();
    public static Map<UUID, Long> playerExit = new HashMap<>();
    public static Map<UUID, PlayerWorld> playerWorldMap = new HashMap<>();
    public static Date gameStartTime;

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerCommandEvent(ServerCommandEvent event) throws IOException {
        List<String> chars = new ArrayList<>(Arrays.asList(event.getCommand().split(" ")));
        if (StartGame.gameRunning == null) {
            boolean eventBool = UpdatePlayerInventory(chars, null);
            if (eventBool) {
                event.setCancelled(eventBool);
            }
        }
        if (chars.size() >= 2) {
            if ((chars.get(0).equalsIgnoreCase("/reload") || chars.get(0).equalsIgnoreCase("reload")) && chars.get(1).equalsIgnoreCase("confirm")) {
                Worldreset.reset();
            }
        }
    }

    public static boolean UpdatePlayerInventory(List<String> chars, String PlayerName) {
        if (chars.size() == 2 && (chars.get(0).equalsIgnoreCase("/op") || chars.get(0).equalsIgnoreCase("/deop") || chars.get(0).equalsIgnoreCase("op") || chars.get(0).equalsIgnoreCase("deop"))) {
            Player UpdatePlayer = Bukkit.getPlayer(chars.get(1));
            if (UpdatePlayer != null && UpdatePlayer.isOnline()) {
                if (UpdatePlayer.isOp()) {
                    UpdatePlayer.setOp(false);
                    ManHuntPlugin.getTeamManager().changePlayerName(UpdatePlayer, ManHuntPlugin.getPlayerData().getPlayerRole(UpdatePlayer));
                    if (!UpdatePlayer.getName().equalsIgnoreCase(PlayerName)) {
                        UpdatePlayer.sendMessage(ManHuntPlugin.getprefix() + "Your operator has been removed");
                    }
                } else {
                    UpdatePlayer.setOp(true);
                    ManHuntPlugin.getTeamManager().changePlayerName(UpdatePlayer, ManHuntPlugin.getPlayerData().getPlayerRole(UpdatePlayer));
                    if (!UpdatePlayer.getName().equalsIgnoreCase(PlayerName)) {
                        UpdatePlayer.sendMessage(ManHuntPlugin.getprefix() + "You became promoted to operator and can now execute ManHunt commands.");
                    }
                }
                playerMenu.get(UpdatePlayer.getUniqueId()).setMenuItems();
                return true;
            }
        }
        return false;
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onServerListPingEvent(ServerListPingEvent event) {
        Player player = null;
        if (onPlayerJoin.playerIP.get(event.getAddress().getHostAddress()) != null) {
            player = onPlayerJoin.playerIP.get(event.getAddress().getHostAddress());
        }

        if (StartGame.gameRunning == null && Ready.ready != null && !Ready.ready.getbossBarCreator().isRunning())
            event.motd(Component.text(ManHuntPlugin.getprefix() + "Game is not" + ChatColor.GREEN + " running.." + "\n" + ManHuntPlugin.getprefix() + ChatColor.GREEN + "You can join the server"));
        else if (Ready.ready != null && Ready.ready.getbossBarCreator().isRunning() && Ready.ready.getbossBarCreator().getTimer() > 3)
            event.motd(Component.text(ManHuntPlugin.getprefix() + "Game is" + ChatColor.GREEN + " ready to start in " + ChatColor.GOLD + Ready.ready.getbossBarCreator().getTimer() + ChatColor.GREEN + " sec\n" + ManHuntPlugin.getprefix() + ChatColor.GREEN + "You can join the server"));
        else if (Ready.ready != null && Ready.ready.getbossBarCreator().isRunning() && Ready.ready.getbossBarCreator().getTimer() <= 3)
            event.motd(Component.text(ManHuntPlugin.getprefix() + "Game is" + ChatColor.GREEN + " ready to start in " + ChatColor.GOLD + Ready.ready.getbossBarCreator().getTimer() + ChatColor.GREEN + " sec\n" + ManHuntPlugin.getprefix() + ChatColor.GREEN + "You can´t join the server"));
        else if (StartGame.gameRunning != null && StartGame.gameRunning.isRunning() && StartGame.gameRunning.getBossBar() != null)
            event.motd(Component.text(ManHuntPlugin.getprefix() + "Game is" + ChatColor.YELLOW + " starting in " + ChatColor.GOLD + StartGame.gameRunning.getTimer() + ChatColor.YELLOW + " sec\n" + ManHuntPlugin.getprefix() + ChatColor.YELLOW + (player != null && player.isWhitelisted() ? ChatColor.GOLD + player.getName() + ChatColor.GREEN + " you can join the server" : ChatColor.RED + "You can´t join the server")));
        else if (StartGame.gameRunning != null && !VoteCommand.pause) {

            event.motd(Component.text(ManHuntPlugin.getprefix() + "Game is" + ChatColor.RED + " running since: " + ChatColor.GRAY + getTimeString(true,  GameTimes.getStartTime()) +
                        "\n" + ManHuntPlugin.getprefix() + ChatColor.YELLOW + (player != null && player.isWhitelisted() ? ChatColor.GOLD + player.getName() + ChatColor.GREEN + " you can join the server" : ChatColor.RED + "You can´t join the server")));
        }
        else if (StartGame.gameRunning != null) {
            event.motd(Component.text(ManHuntPlugin.getprefix() + "Game is" + ChatColor.AQUA + " paused since: " + ChatColor.GRAY + getTimeString(true, Calendar.getInstance().getTime().getTime() - VoteCommand.pauseList.get((VoteCommand.pauseList.size() - 1))) + "\n" + ManHuntPlugin.getprefix() + ChatColor.YELLOW + (player != null && player.isWhitelisted() ? ChatColor.GOLD + player.getName() + ChatColor.GREEN + " you can join the server" : ChatColor.RED + "You can´t join the server")));



        }   if (StartGame.gameRunning == null) {
                event.setMaxPlayers(event.getNumPlayers());
            } else {
                event.setMaxPlayers((int) Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count());
            }
    }

        public static String getTimeString(Boolean space, Long time){
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
}