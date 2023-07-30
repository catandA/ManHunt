package de.shiirroo.manhunt.utilis.config;

import de.shiirroo.manhunt.ManHuntPlugin;

import java.io.Serializable;

public class Config implements Serializable {

    public static Integer getHuntStartTime() {
        return (Integer) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("猎人等待时间").getConfigSetting();
    }

    public static Boolean getAssassinsInstaKill() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("刺客即杀").getConfigSetting();
    }

    public static Boolean getCompassTracking() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("追踪指南针").getConfigSetting();
    }

    public static Boolean getGiveCompass() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("发放指南针").getConfigSetting();
    }

    public static Boolean getCompassParticleToSpeedrunner() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("粒子效果追踪").getConfigSetting();
    }

    public static Boolean getFreezeAssassin() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("冻结刺客").getConfigSetting();
    }

    public static Boolean getBossbarCompass() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("BOSS栏指南针").getConfigSetting();
    }

    public static Integer getMaxPlayerSize() {
        return (Integer) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("最大玩家数").getConfigSetting();
    }

    public static Boolean getShowAdvancement() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("显示进度").getConfigSetting();
    }

    public static Boolean getCompassAutoUpdate() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("指南针自动更新").getConfigSetting();
    }

    public static Integer getCompassTriggerTimer() {
        return (Integer) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("指南针激活间隔").getConfigSetting();
    }

    public static Integer getSpeedrunnerOpportunity() {
        return (Integer) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("速通者几率").getConfigSetting();
    }

    public static Integer getReadyStartTime() {
        return (Integer) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("准备时间").getConfigSetting();
    }

    public static Integer getGameResetTime() {
        return (Integer) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("重置时间").getConfigSetting();
    }

    public static Boolean getSpawnPlayerLeaveZombie() {
        return (Boolean) ManHuntPlugin.getGameData().getGameConfig().getConfigCreators("玩家离开时生成僵尸").getConfigSetting();
    }

    public static void relodConfig() {
        ManHuntPlugin.getPlugin().reloadConfig();
        ManHuntPlugin.getGameData().reloadGameConfig(ManHuntPlugin.getPlugin());
    }
}
