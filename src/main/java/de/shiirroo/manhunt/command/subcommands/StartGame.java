package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePresetMenu;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import de.shiirroo.manhunt.utilis.config.Config;
import de.shiirroo.manhunt.utilis.repeatingtask.CompassTracker;
import de.shiirroo.manhunt.utilis.vote.BossBarCreator;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.CompassMeta;

import java.util.Calendar;
import java.util.UUID;

public class StartGame extends SubCommand {

    public static BossBarCreator bossBarGameStart = createBossBarGameStart();

    @Override
    public String getName() {
        return "start";
    }

    @Override
    public String getDescription() {
        return "开始猎人游戏";
    }

    @Override
    public String getSyntax() {
        return "/manhunt start";
    }

    @Override
    public Boolean getNeedOp() {
        return true;
    }

    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        CommandBuilder start = new CommandBuilder("start");
        start.addSubCommandBuilder(new CommandBuilder("debug"));
        return start;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "你没权限用这指令");
            return;
        }
        if (ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            player.sendMessage(ManHuntPlugin.getprefix() + "比赛正在进行");
        } else if(args.length == 2 && args[1].equalsIgnoreCase("debug")) {
            player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "您在调试模式下启动游戏，许多功能不可用的。别忘了给自己一个队伍!");
            Start();
        }else if(Bukkit.getOnlinePlayers().size() >= 2) {
            if (GamePresetMenu.preset.setPlayersGroup()) {
                Start();
            }
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "没有足够的玩家");
        }
    }


    public static void Start() {
        if (!ManHuntPlugin.getGameData().getGameStatus().isGameRunning() || !ManHuntPlugin.getGameData().getGameStatus().isStarting()) {
            if (ManHuntPlugin.getGameData().getGameStatus().isReadyForVote()) {
                ManHuntPlugin.getGameData().getGameStatus().setReadyForVote(false);
                if (Ready.ready.getbossBarCreator().isRunning()) {
                    Ready.ready.cancelVote();
                }
            }
            ManHuntPlugin.getGameData().getGameStatus().setStarting(true);
            ManHuntPlugin.getGameData().getPlayerData().updatePlayers(ManHuntPlugin.getTeamManager());
            Bukkit.getLogger().info(ManHuntPlugin.getprefix() + ChatColor.GRAY + "游戏马上开始");
            ManHuntPlugin.GameTimesTimer = 20;
            setGameWorld();
            bossBarGameStart.setBossBarPlayers();
            ManHuntPlugin.getGameData().getGameMode().getRandomBlocks().execute();
            ManHuntPlugin.getGameData().getGameMode().getRandomItems().execute();
            ManHuntPlugin.getGameData().getGameMode().getRandomTP().execute();
            Bukkit.getLogger().info(ManHuntPlugin.getprefix() + ChatColor.GRAY + "速通者快润!!!");
        }
    }

    public static BossBarCreator createBossBarGameStart() {
        return new BossBarCreator(ManHuntPlugin.getPlugin(), ChatColor.DARK_RED + "猎杀" + ChatColor.RED + "将在 " + ChatColor.GOLD + "TIMER" + ChatColor.RED + " 秒后开始", Config.getHuntStartTime())
                .onComplete(vote -> {
                    bossBarGameStart = createBossBarGameStart();
                    ManHuntPlugin.getGameData().getGameStatus().setStarting(false);
                    ManHuntPlugin.getGameData().getGameStatus().setGameRunning(true);
                    setGameStarting();
                    ManHuntPlugin.getGameData().getGameStatus().setGameStartTime(Calendar.getInstance().getTime().getTime());
                    Bukkit.getLogger().info(ManHuntPlugin.getprefix() + ChatColor.GRAY + "猎人开追");
                    ManHuntPlugin.getGameData().getGameMode().getRandomTP().execute();
                })
                .onShortlyComplete(vote -> Bukkit.getOnlinePlayers().forEach(current -> current.playSound(current.getLocation(), Sound.ENTITY_PLAYER_LEVELUP, 1.0f, 1.0f)));
    }

    private static void setGameWorld() {
        ManHuntPlugin.getPlugin().getServer().setDefaultGameMode(GameMode.SURVIVAL);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (!p.getGameMode().equals(GameMode.SPECTATOR)) {
                p.getInventory().clear();
                p.closeInventory();
                p.setWhitelisted(true);
                p.setExp(0);
                p.setLevel(0);
                p.setHealth(20);
                p.setFoodLevel(20);
                p.setTotalExperience(0);
                p.setBedSpawnLocation(p.getWorld().getSpawnLocation());
                p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.DARK_PURPLE + "速通者" + ChatColor.GRAY + "润!!"));
                ManHuntPlugin.getGameData().getGameStatus().getLivePlayerList().add(p.getUniqueId());
                ManHuntPlugin.getGameData().getGameStatus().getStartPlayerList().add(p.getUniqueId());
            }
        }

        Bukkit.getServer().setWhitelist(true);

        for (World w : Bukkit.getWorlds()) {
            w.setDifficulty(Difficulty.NORMAL);
            w.setGameRule(GameRule.DO_MOB_SPAWNING, true);
            w.setThundering(false);
            w.setTime(0);
        }
        ManHuntPlugin.getGameData().getGameMode().getWorldBorderSize().execute();

        for (UUID speedrunnerUUid : ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner)) {
            Player speedRunner = Bukkit.getPlayer(speedrunnerUUid);
            if (speedRunner != null) {
                if (!speedRunner.getGameMode().equals(GameMode.SPECTATOR)) {
                    speedRunner.setGameMode(GameMode.SURVIVAL);
                }
            }
        }
    }

    private static void setGameStarting() {
        for (World w : Bukkit.getWorlds())
            w.setPVP(true);
        for (UUID uuid : ManHuntPlugin.getGameData().getPlayerData().getPlayersWithOutSpeedrunner()) {
            Player player = Bukkit.getPlayer(uuid);
            if (player != null) {
                if (!player.getGameMode().equals(GameMode.SPECTATOR)) {
                    player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(ChatColor.RED + "猎人" + ChatColor.GRAY + "开追!!!"));
                    player.setGameMode(GameMode.SURVIVAL);
                    getCompassTracker(player);
                }
            }
        }
    }

    public static void getCompassTracker(Player player) {
        ItemStack compass = new ItemStack(Material.COMPASS);
        CompassMeta meta = (CompassMeta) compass.getItemMeta();
        meta.setLodestoneTracked(false);
        if (Config.getGiveCompass() && Config.getCompassTracking() && Config.getCompassAutoUpdate()) {
            compass.setItemMeta(meta);
            player.getInventory().addItem(compass);
            CompassTracker.updateCompass(player);
        } else if (Config.getGiveCompass()) {
            compass.setItemMeta(meta);
            player.getInventory().addItem(compass);
        }
    }


}



