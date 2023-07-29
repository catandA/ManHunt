package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePresetMenu;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import de.shiirroo.manhunt.utilis.config.Config;
import de.shiirroo.manhunt.utilis.vote.VoteCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

import java.text.DecimalFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public class Ready extends SubCommand {


    @Override
    public String getName() {
        return "ready";
    }

    @Override
    public String getDescription() {
        return "准备开始猎杀";
    }

    @Override
    public String getSyntax() {
        return "/manhunt ready";
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
    public void perform(Player p, String[] args) {
        if (!ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            if (!setReady(p)) {
                DecimalFormat df = new DecimalFormat("#.#");
                p.sendMessage(ManHuntPlugin.getprefix() + "你太快了，慢点 [ " + ChatColor.RED + df.format(getPlayerCooldownTime(p)/1000) + ChatColor.GRAY + " ]");
            }
        } else {
            p.sendMessage(ManHuntPlugin.getprefix() + "正在游戏时无法更改准备状态");
        }
    }
    public static boolean setReady(Player p) {
        if(Bukkit.getOnlinePlayers().size() >= 2) {
            if (isPlayerHasCooldown(p)) {
                if (ready.hasPlayerVote(p)) {
                    readyRemove(p, false);
                    return true;
                } else return readyAdd(p);
            }
        } else {
            p.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "没人啊啊啊啊没法开始");
            return true;
        }
        return false;

    }

    public static VoteCreator setReadyVote() {
        VoteCreator readyVote = new VoteCreator(false, ManHuntPlugin.getPlugin(), ChatColor.GREEN + "游戏将会开始于" + ChatColor.GOLD + "TIMER", Config.getReadyStartTime());
        readyVote.getbossBarCreator().onComplete(aBoolean -> {
                    ready = setReadyVote();
                    if (aBoolean) {
                        if (GamePresetMenu.preset.setPlayersGroup())
                            ManHuntPlugin.getGameData().getGameStatus().setReadyForVote(false);
                        StartGame.Start();
                    }
                    ManHuntPlugin.getGameData().getPlayerData().updatePlayers(ManHuntPlugin.getTeamManager());
                }
        );
        readyVote.getbossBarCreator().onShortlyComplete(aBoolean -> Bukkit.getOnlinePlayers().forEach(current -> current.playSound(current.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)));
        return readyVote;
    }

    public static boolean readyAdd(Player p) {
        if (startGame()) {
            ready.addVote(p);
            ManHuntPlugin.getGameData().getGamePlayer().getPlayerBlockReadyTime().put(p.getUniqueId(), (new Date().getTime() + 5000L));
            ManHuntPlugin.playerMenu.get(p.getUniqueId()).setMenuItems();
            return true;
        }

        return false;
    }

    public static void readyRemove(Player p, Boolean LeaveOrJoin) {
        if (ready.hasPlayerVote(p)) {
            ManHuntPlugin.getGameData().getGamePlayer().getPlayerBlockReadyTime().remove(p.getUniqueId());
            ready.removeVote(p);
            ready.getbossBarCreator().cancel();
            ManHuntPlugin.getGameData().getGamePlayer().getPlayerBlockReadyTime().put(p.getUniqueId(), (new Date().getTime() + 5000L));
            if (p.isOnline()) {
                ManHuntPlugin.playerMenu.get(p.getUniqueId()).setMenuItems();
            }
        }
        if (LeaveOrJoin) {
            setOtherPlayerUnready();
        }
    }

    public static Double getPlayerCooldownTime(Player p){
        Long cooldown = ManHuntPlugin.getGameData().getGamePlayer().getPlayerBlockReadyTime().get(p.getUniqueId());
        if (cooldown != null) return (double) (cooldown - new Date().getTime());
        return 0D;
    }


    public static boolean isPlayerHasCooldown(Player p) {
        Long cooldown = ManHuntPlugin.getGameData().getGamePlayer().getPlayerBlockReadyTime().get(p.getUniqueId());
        if (cooldown == null) return true;
        return (new Date().getTime() - cooldown) > 0;
    }

    public static VoteCreator ready = setReadyVote();

    public static boolean startGame() {
        if (Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1 && (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() != 0
                || ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).size() >= 1)) {
            if (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() == Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count()
                    || ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Hunter).size() == Bukkit.getOnlinePlayers().size() ||
                    ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Assassin).size() == Bukkit.getOnlinePlayers().size()) {
                return false;
            }
            if (!GamePresetMenu.preset.getSpeedRunnersMaxSize().equalsIgnoreCase("ထ")) {
                if (Integer.parseInt(GamePresetMenu.preset.getSpeedRunnersMaxSize()) < ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() ||
                        ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() != Integer.parseInt(GamePresetMenu.preset.getSpeedRunnersMaxSize())) {
                    return false;
                }
            }
            if (!GamePresetMenu.preset.getHunterMaxSize().equalsIgnoreCase("ထ")) {
                if (Integer.parseInt(GamePresetMenu.preset.getHunterMaxSize()) < ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Hunter).size() |
                        ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Hunter).size() != Integer.parseInt(GamePresetMenu.preset.getHunterMaxSize())) {
                    return false;
                }
            }
            if (!GamePresetMenu.preset.getAssassinMaxSize().equalsIgnoreCase("ထ")) {
                if (Integer.parseInt(GamePresetMenu.preset.getAssassinMaxSize()) < ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Assassin).size() ||
                        ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Assassin).size() != Integer.parseInt(GamePresetMenu.preset.getAssassinMaxSize())) {
                    return false;
                }
            }

            if ((ready.getPlayers().size() + 1) == Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count()) {
                ready.startVote();
            }
            return true;
        }
        return false;
    }

    private static void setOtherPlayerUnready() {
        if (ready.getPlayers().size() >= 1) {
            Optional<UUID> uuid = ready.getPlayers().stream().findFirst();
            if (uuid.isPresent()) {
                if (ready.hasPlayerVote(Objects.requireNonNull(Bukkit.getPlayer(uuid.get()))))
                    ready.removeVote(Bukkit.getPlayer(uuid.get()));
                ManHuntPlugin.playerMenu.get(uuid.get()).setMenuItems();
                ManHuntPlugin.getGameData().getPlayerData().setUpdateRole(Objects.requireNonNull(Bukkit.getPlayer(uuid.get())), ManHuntPlugin.getTeamManager());
            }
        }
    }
}