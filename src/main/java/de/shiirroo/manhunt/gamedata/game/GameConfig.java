package de.shiirroo.manhunt.gamedata.game;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.utilis.config.ConfigCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.Serial;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

public class GameConfig implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private LinkedHashSet<ConfigCreator> configCreatorsSett = new LinkedHashSet<>();

    public GameConfig(GameConfig gameConfig) {
        configCreatorsSett.addAll(gameConfig.getConfigCreatorsSett());
    }

    public GameConfig(Plugin plugin) {
        LinkedHashSet<ConfigCreator> newConfigCreatorsSett = new LinkedHashSet<>();
        Bukkit.getLogger().info(ManHuntPlugin.getprefix() + "配置已加载");
        newConfigCreatorsSett.add(new ConfigCreator("猎人等待时间", 5, 999, 120).configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "猎人们可以开始狩猎的时间"))));
        newConfigCreatorsSett.add(new ConfigCreator("刺客即杀").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "刺客可以秒杀速通者,", ChatColor.GRAY + "或者每次攻击移除一件护甲"))));
        newConfigCreatorsSett.add(new ConfigCreator("追踪指南针").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "在主世界用指南针指向速通者"))));
        newConfigCreatorsSett.add(new ConfigCreator("发放指南针").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "在游戏开始的时候", ChatColor.GRAY + "给猎人指南针"))));
        newConfigCreatorsSett.add(new ConfigCreator("粒子效果追踪").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "用粒子效果显示速通者在哪", ChatColor.GRAY + "只在手上有指南针的时候有效"))));
        newConfigCreatorsSett.add(new ConfigCreator("冻结刺客").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "速通者盯着刺客的时候能将其冻结"))));
        newConfigCreatorsSett.add(new ConfigCreator("BOSS栏指南针").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "在BOSS栏显示速通者的位置", "", ChatColor.DARK_RED + "! ALPHA TEST !"))));
        newConfigCreatorsSett.add(new ConfigCreator("显示进度").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "显示", ChatColor.GRAY + "玩家的进度"))));
        newConfigCreatorsSett.add(new ConfigCreator("指南针自动更新").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "指南针自动更新"))));
        newConfigCreatorsSett.add(new ConfigCreator("指南针激活间隔", 5, 300, 15).configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "猎人能更新指南针的时间"))));
        newConfigCreatorsSett.add(new ConfigCreator("速通者几率", 1, 99, 40).configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "成为速通者的百分比概率"))));
        newConfigCreatorsSett.add(new ConfigCreator("玩家离开时生成僵尸").configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "有玩家退出的时候生成一个玩家僵尸"))));
        newConfigCreatorsSett.add(new ConfigCreator("准备时间", 5, 120, 15).configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "游戏开始前的准备时间"))));
        newConfigCreatorsSett.add(new ConfigCreator("重置时间", 2, 100, 8).configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "游戏将自行重置的时间(以小时为单位)"))));
        newConfigCreatorsSett.add(new ConfigCreator("最大玩家数", 2, 100, 10).configCreator(plugin)
                .setLore(new ArrayList<>(Arrays.asList("", ChatColor.GRAY + "猎杀的", ChatColor.GRAY + "最大玩家数"))));
        Bukkit.getLogger().info(ManHuntPlugin.getprefix() + "配置已重载");
        plugin.saveConfig();
        configCreatorsSett = newConfigCreatorsSett.stream().sorted(Comparator.comparing(ConfigCreator::getConfigName)).collect(Collectors.toCollection(LinkedHashSet::new));
    }


    public LinkedHashSet<ConfigCreator> getConfigCreatorsSett() {
        return configCreatorsSett;
    }

    public ConfigCreator getConfigCreators(String ConfigName) {
        Optional<ConfigCreator> configCreator = getConfigCreatorsSett().stream().filter(config -> config.getConfigName().equalsIgnoreCase(ConfigName)).findFirst();
        return configCreator.orElse(null);
    }

}
