package de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.presets;

import com.google.common.collect.Lists;
import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePreset;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePresetMenu;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import de.shiirroo.manhunt.utilis.Utilis;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Dream extends GamePreset implements Serializable {

    @Override
    public String getSpeedRunnersMaxSize() {
        return "1";
    }

    @Override
    public String getAssassinMaxSize() {
        return "1";
    }

    @Override
    public String getHunterMaxSize() {
        return "ထ";
    }


    @Override
    public ItemStack displayItem() {
        ItemStack playHead = Utilis.getPlayHead();
        SkullMeta im = (SkullMeta) playHead.getItemMeta();
        im.setDisplayName(ChatColor.GREEN + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "DREAM");
        im.setOwner("Dream");
        List<String> loreString = Lists.newArrayList("", ChatColor.GOLD + "➤ " + ChatColor.GRAY + "玩" + ChatColor.GREEN + "Dream的" + ChatColor.GRAY + "经典猎人游戏",
                "", ChatColor.YELLOW + "● " + ChatColor.GOLD + getSpeedRunnersMaxSize() + "x " + ManHuntRole.Speedrunner.getChatColor() + ManHuntRole.Speedrunner,
                ChatColor.YELLOW + "● " + ChatColor.GOLD + getAssassinMaxSize() + "x " + ManHuntRole.Assassin.getChatColor() + ManHuntRole.Assassin, ChatColor.YELLOW
                        + "● " + ChatColor.GOLD + getHunterMaxSize() + "x " + ManHuntRole.Hunter.getChatColor() + ManHuntRole.Hunter,
                "");
        makeConfig().forEach((s, o) -> loreString.add(
                (ChatColor.YELLOW + "➢ " + ChatColor.GOLD + s + " : " + (o instanceof Boolean ? ((Boolean) o ? ChatColor.GREEN + o.toString().substring(0, 1).toUpperCase() + o.toString().substring(1) : ChatColor.RED + o.toString().substring(0, 1).toUpperCase() + o.toString().substring(1)) : ChatColor.GREEN + o.toString()))
        ));
        loreString.add(" ");
        loreString.add(GamePresetMenu.preset.presetName().equalsIgnoreCase(this.getClass().getName()) ? ChatColor.GREEN + "" + ChatColor.BOLD + "⇨ 已选择预设" : ChatColor.DARK_GRAY + "⇨ 选择预设");
        im.setLore(loreString);
        playHead.setItemMeta(im);
        return playHead;
    }

    @Override
    public int getSpeedRunnerSize() {
        return 1;
    }

    @Override
    public boolean setPlayersGroup() {
        if (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().anyMatch(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)) && Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1) {
            while (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() < getSpeedRunnerSize()) {
                int speedrunnerPlayerID = Utilis.generateRandomInt((int) ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).count());
                Player SpeedrunnerPlayer = Bukkit.getPlayer(ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).toList().get(speedrunnerPlayerID));
                if (SpeedrunnerPlayer != null)
                    ManHuntPlugin.getGameData().getPlayerData().setRole(SpeedrunnerPlayer, ManHuntRole.Speedrunner, ManHuntPlugin.getTeamManager());
            }
            if (Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 2 && ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().anyMatch(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR))) {
                int AssassinPlayerID = Utilis.generateRandomInt((int) ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).count());
                Player AssassinPlayer = Bukkit.getPlayer(ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).toList().get(AssassinPlayerID));
                if (AssassinPlayer != null)
                    ManHuntPlugin.getGameData().getPlayerData().setRole(AssassinPlayer, ManHuntRole.Assassin, ManHuntPlugin.getTeamManager());
            }
            if (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().anyMatch(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)))
                ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).forEach(uuid -> ManHuntPlugin.getGameData().getPlayerData().setRole(Objects.requireNonNull(Bukkit.getPlayer(uuid)), ManHuntRole.Hunter, ManHuntPlugin.getTeamManager()));
            return true;
        }
        return Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1 && ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() == 1 && ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Assassin).size() == 1;
    }

    @Override
    public HashMap<String, Object> makeConfig() {
        HashMap<String, Object> defaultConfig = new LinkedHashMap<>();
        defaultConfig.put("猎人等待时间", 120);
        defaultConfig.put("刺客即杀", true);
        defaultConfig.put("追踪指南针", true);
        defaultConfig.put("发放指南针", true);
        defaultConfig.put("粒子效果追踪", false);
        defaultConfig.put("冻结刺客", true);
        defaultConfig.put("显示进度", true);
        defaultConfig.put("指南针自动更新", false);
        defaultConfig.put("指南针激活间隔", 15);
        defaultConfig.put("玩家离开时生成僵尸", false);
        defaultConfig.put("准备时间", 15);
        return defaultConfig;
    }
}
