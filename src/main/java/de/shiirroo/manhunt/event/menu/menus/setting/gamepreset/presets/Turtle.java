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
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;

public class Turtle extends GamePreset implements Serializable {

    @Override
    public String getSpeedRunnersMaxSize() {
        return String.valueOf(getSpeedRunnerSize());
    }

    @Override
    public String getAssassinMaxSize() {
        return "ထ";
    }

    @Override
    public String getHunterMaxSize() {
        return "ထ";
    }

    @Override
    public ItemStack displayItem() {
        ItemStack itemStack = new ItemStack(Material.TURTLE_EGG, 1);
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(ChatColor.AQUA + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "Turtle");
        List<String> loreString = Lists.newArrayList("", ChatColor.GOLD + "➤ " + ChatColor.GRAY + "玩" + ChatColor.AQUA + "乌龟" + ChatColor.GRAY + "猎人模式",
                "", ChatColor.YELLOW + "● " + ChatColor.GOLD + "min. " + (Integer.parseInt(getSpeedRunnersMaxSize())) + " " + ManHuntRole.Speedrunner.getChatColor() + ManHuntRole.Speedrunner,
                ChatColor.YELLOW + "● " + ChatColor.GOLD + getAssassinMaxSize() + "x " + ManHuntRole.Assassin.getChatColor() + ManHuntRole.Assassin, ChatColor.YELLOW
                        + "● " + ChatColor.GOLD + getHunterMaxSize() + "x " + ManHuntRole.Hunter.getChatColor() + ManHuntRole.Hunter,
                "");

        makeConfig().forEach((s, o) -> loreString.add(
                (ChatColor.YELLOW + "➢ " + ChatColor.GOLD + s + " : " + (o instanceof Boolean ? ((Boolean) o ? ChatColor.GREEN + o.toString().substring(0, 1).toUpperCase() + o.toString().substring(1) : ChatColor.RED + o.toString().substring(0, 1).toUpperCase() + o.toString().substring(1)) : ChatColor.GREEN + o.toString()))
        ));
        loreString.add(" ");
        loreString.add(GamePresetMenu.preset.presetName().equalsIgnoreCase(this.getClass().getName()) ? ChatColor.GREEN + "" + ChatColor.BOLD + "⇨ 已选择预设" : ChatColor.DARK_GRAY + "⇨ 选择预设");
        im.setLore(loreString);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(im);
        return itemStack;
    }

    @Override
    public int getSpeedRunnerSize() {
        long Opportunity = (((int) makeConfig().get("速通者几率")) / 100) * Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count();
        if (Opportunity <= 1)
            return 1;
        return Math.round(Opportunity);
    }


    public boolean setPlayersGroup() {
        if (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().anyMatch(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)) && Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1) {
            while (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() < getSpeedRunnerSize()) {
                int speedrunnerPlayerID = Utilis.generateRandomInt((int) ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).count());
                Player SpeedrunnerPlayer = Bukkit.getPlayer(ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).toList().get(speedrunnerPlayerID));
                if (SpeedrunnerPlayer != null) {
                    ManHuntPlugin.getGameData().getPlayerData().setRole(SpeedrunnerPlayer, ManHuntRole.Speedrunner, ManHuntPlugin.getTeamManager());
                }
            }
            while (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().anyMatch(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR))) {
                int hunterPlayerID = Utilis.generateRandomInt((int) ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).count());
                Player SpeedrunnerPlayer = Bukkit.getPlayer(ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).toList().get(hunterPlayerID));
                if (SpeedrunnerPlayer != null) {
                    ManHuntPlugin.getGameData().getPlayerData().setRole(SpeedrunnerPlayer, Utilis.generateRandomInt(2) == 0 ? ManHuntRole.Hunter : ManHuntRole.Assassin, ManHuntPlugin.getTeamManager());
                }
            }
            return true;
        }
        return Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1 && ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() >= getSpeedRunnerSize();
    }

    @Override
    public HashMap<String, Object> makeConfig() {
        HashMap<String, Object> defaultConfig = new LinkedHashMap<>();
        defaultConfig.put("猎人等待时间", 200);
        defaultConfig.put("刺客即杀", false);
        defaultConfig.put("追踪指南针", true);
        defaultConfig.put("发放指南针", false);
        defaultConfig.put("粒子效果追踪", false);
        defaultConfig.put("冻结刺客", true);
        defaultConfig.put("显示进度", false);
        defaultConfig.put("指南针自动更新", false);
        defaultConfig.put("指南针激活间隔", 30);
        defaultConfig.put("速通者几率", 60);
        defaultConfig.put("玩家离开时生成僵尸", false);
        defaultConfig.put("准备时间", 30);
        return defaultConfig;
    }
}
