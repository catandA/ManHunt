package de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.presets;

import com.google.common.collect.Lists;
import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePreset;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePresetMenu;
import de.shiirroo.manhunt.teams.model.ManHuntRole;
import de.shiirroo.manhunt.utilis.Utilis;
import de.shiirroo.manhunt.utilis.config.Config;
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

public class Custom extends GamePreset implements Serializable {


    @Override
    public String getSpeedRunnersMaxSize() {
        return "ထ";
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
        ItemStack itemStack = new ItemStack(Material.WRITABLE_BOOK, 1);
        ItemMeta im = itemStack.getItemMeta();
        im.setDisplayName(ChatColor.YELLOW + "" + ChatColor.BOLD + ChatColor.UNDERLINE + "自定义");
        List<String> loreString = Lists.newArrayList("", ChatColor.GOLD + "➤ " + ChatColor.GRAY + "玩你自己的猎人游戏模式",
                "", ChatColor.YELLOW + "● " + ChatColor.GOLD + getSpeedRunnersMaxSize() + "x " + ManHuntRole.Speedrunner.getChatColor() + ManHuntRole.Speedrunner,
                ChatColor.YELLOW + "● " + ChatColor.GOLD + getAssassinMaxSize() + "x " + ManHuntRole.Assassin.getChatColor() + ManHuntRole.Assassin, ChatColor.YELLOW
                        + "● " + ChatColor.GOLD + getHunterMaxSize() + "x " + ManHuntRole.Hunter.getChatColor() + ManHuntRole.Hunter,
                "");
        String s = GamePresetMenu.checkCustom() ? ChatColor.BLUE + "" + ChatColor.BOLD + "⇨ 重载预设" : ChatColor.GREEN + "" + ChatColor.BOLD + "⇨ 已选中预设";

        loreString.add(GamePresetMenu.preset.presetName().equalsIgnoreCase(this.getClass().getName()) ? s : ChatColor.DARK_GRAY + "⇨ 选择预设");

        im.setLore(loreString);
        im.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        itemStack.setItemMeta(im);
        return itemStack;
    }

    @Override
    public int getSpeedRunnerSize() {
        long Opportunity = (Config.getSpeedrunnerOpportunity() / 100) * Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count();
        if (Opportunity <= 1)
            return 0;
        return (int) Math.floor(Opportunity);
    }


    public boolean setPlayersGroup() {
        if (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().anyMatch(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)) && Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1) {
            while (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() <= getSpeedRunnerSize()) {
                int speedrunnerPlayerID = Utilis.generateRandomInt((int) ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).count());
                Player SpeedrunnerPlayer = Bukkit.getPlayer(ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).toList().get(speedrunnerPlayerID));
                if (SpeedrunnerPlayer != null)
                    ManHuntPlugin.getGameData().getPlayerData().setRole(SpeedrunnerPlayer, ManHuntRole.Speedrunner, ManHuntPlugin.getTeamManager());
            }
            while (ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().anyMatch(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR))) {
                int hunterPlayerID = Utilis.generateRandomInt((int) ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).count());
                Player hunterPlayer = Bukkit.getPlayer(ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Unassigned).stream().filter(uuid -> !Objects.requireNonNull(Bukkit.getPlayer(uuid)).getGameMode().equals(GameMode.SPECTATOR)).toList().get(hunterPlayerID));
                if (hunterPlayer != null)
                    ManHuntPlugin.getGameData().getPlayerData().setRole(hunterPlayer, Utilis.generateRandomInt(2) == 0 ? ManHuntRole.Hunter : ManHuntRole.Assassin, ManHuntPlugin.getTeamManager());
            }
            return true;
        }
        return Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1 && ManHuntPlugin.getGameData().getPlayerData().getPlayersByRole(ManHuntRole.Speedrunner).size() >= 1;
    }

    @Override
    public HashMap<String, Object> makeConfig() {
        if (GamePresetMenu.customHashMap == null) {
            GamePresetMenu.customHashMap = new LinkedHashMap<>();
            GamePresetMenu.customHashMap.put("猎人等待时间", Config.getHuntStartTime());
            GamePresetMenu.customHashMap.put("刺客即杀", Config.getAssassinsInstaKill());
            GamePresetMenu.customHashMap.put("追踪指南针", Config.getCompassTracking());
            GamePresetMenu.customHashMap.put("发放指南针", Config.getGiveCompass());
            GamePresetMenu.customHashMap.put("粒子效果追踪", Config.getCompassParticleToSpeedrunner());
            GamePresetMenu.customHashMap.put("冻结刺客", Config.getFreezeAssassin());
            GamePresetMenu.customHashMap.put("显示进度", Config.getShowAdvancement());
            GamePresetMenu.customHashMap.put("指南针自动更新", Config.getCompassAutoUpdate());
            GamePresetMenu.customHashMap.put("指南针激活间隔", Config.getCompassTriggerTimer());
            GamePresetMenu.customHashMap.put("速通者几率", Config.getSpeedrunnerOpportunity());
            GamePresetMenu.customHashMap.put("玩家离开时生成僵尸", Config.getSpawnPlayerLeaveZombie());
            GamePresetMenu.customHashMap.put("准备时间", Config.getReadyStartTime());
        }
        return GamePresetMenu.customHashMap;
    }
}


