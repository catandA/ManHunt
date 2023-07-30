package de.shiirroo.manhunt.command.subcommands;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import de.shiirroo.manhunt.event.menu.Menu;
import de.shiirroo.manhunt.event.menu.menus.setting.SettingsMenu;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.GamePresetMenu;
import de.shiirroo.manhunt.event.menu.menus.setting.gamepreset.presets.Custom;
import de.shiirroo.manhunt.utilis.Utilis;
import de.shiirroo.manhunt.utilis.config.Config;
import de.shiirroo.manhunt.utilis.config.ConfigCreator;
import net.wesjd.anvilgui.AnvilGUI;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.UUID;

public class ConfigManHunt extends SubCommand {

    public static CommandBuilder configCommand;

    @Override
    public String getName() {
        return "config";
    }

    @Override
    public String getDescription() {
        return "更改配置";
    }

    @Override
    public String getSyntax() {
        return "/manhunt config [名称]";
    }

    @Override
    public Boolean getNeedOp() {
        return true;
    }

    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        configCommand = new CommandBuilder("config", getNeedOp());
        for (ConfigCreator configCreator : ManHuntPlugin.getGameData().getGameConfig().getConfigCreatorsSett()) {
            if (!configCreator.getConfigName().equalsIgnoreCase("BossbarCompass")) {
                CommandBuilder configSetting = new CommandBuilder(configCreator.getConfigName());
                if (configCreator.getConfigSetting() instanceof Boolean) {
                    configSetting.addSubCommandBuilder(new CommandBuilder("True"));
                    configSetting.addSubCommandBuilder(new CommandBuilder("False"));
                }
                configCommand.addSubCommandBuilder(configSetting);
            }
        }
        return configCommand;
    }

    @Override
    public void perform(Player player, String[] args) {
        if (!player.isOp()) {
            player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "你没权限用这指令");
        } else if (args.length == 1) {
            player.sendMessage(ManHuntPlugin.getprefix() + getDescription());
        } else if (args.length == 2 && !ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            ConfigCreator configCreator = ManHuntPlugin.getGameData().getGameConfig().getConfigCreators(args[1]);
            if (configCreator != null && configCreator.getConfigSetting() instanceof Integer) {
                AnvilGUISetup(player, configCreator);
            } else {
                getConfigCommands(player, args[1]);
            }
        } else if (args.length > 3) {
            player.sendMessage(ManHuntPlugin.getprefix() + getDescription());
        } else if (!ManHuntPlugin.getGameData().getGameStatus().isGame()) {
            changeBoolConfig(player, args);
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + "正在游戏时没法改指令");
        }
    }

    private void changeBoolConfig(Player player, String[] args) {
        Optional<ConfigCreator> configCreator = ManHuntPlugin.getGameData().getGameConfig().getConfigCreatorsSett().stream().filter(c -> args[1].equalsIgnoreCase(c.getConfigName())).findFirst();
        if (configCreator.isPresent()) {
            ConfigCreator creator = configCreator.get();
            if (creator.getConfigSetting() instanceof Boolean && args[2].equalsIgnoreCase("True") || args[2].equalsIgnoreCase("False")) {
                Boolean b = Boolean.valueOf(args[2].toLowerCase());
                Bukkit.getLogger().info(ManHuntPlugin.getprefix() + creator.getConfigName() + " " + b + " " + args[2]);
                if (creator.getConfigSetting().equals(b)) {
                    player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.GOLD + creator.getConfigName().substring(0, 1).toUpperCase() + creator.getConfigName().substring(1) + ChatColor.GRAY + " is already : " + (b ? ChatColor.GREEN : ChatColor.RED) + args[2].substring(0, 1).toUpperCase() + args[2].substring(1));
                } else {
                    resetPreset(player);
                    creator.setConfigSetting(b, ManHuntPlugin.getPlugin());
                    if (GamePresetMenu.preset.presetName().equalsIgnoreCase(new Custom().presetName()) && GamePresetMenu.customHashMap != null) {
                        GamePresetMenu.customHashMap.put(creator.getConfigName(), b);
                    }

                    if (creator.getConfigName().equalsIgnoreCase("ShowAdvancement")) {
                        ShowAdvancement(b);
                    }

                    SettingsMenu.ConfigMenu.values().forEach(Menu::setMenuItems);

                    player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.GOLD + creator.getConfigName().substring(0, 1).toUpperCase() + creator.getConfigName().substring(1) + ChatColor.GRAY + " switched to : " + (b ? ChatColor.GREEN : ChatColor.RED) + args[2].substring(0, 1).toUpperCase() + args[2].substring(1));
                }
            }
        }
    }

    private void getConfigCommands(Player player, String args) {
        for (ConfigCreator configCreator : ManHuntPlugin.getGameData().getGameConfig().getConfigCreatorsSett()) {
            if (args.equalsIgnoreCase(configCreator.getConfigName()) && configCreator.getConfigSetting() instanceof Boolean) {
                String s = String.valueOf(configCreator.getConfigSetting()).substring(0, 1).toUpperCase() + String.valueOf(configCreator.getConfigSetting()).substring(1).toLowerCase();

                if (configCreator.getConfigSetting().equals(true)) {
                    player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.GOLD + configCreator.getConfigName() + ChatColor.GRAY + " : " + ChatColor.GREEN + s);
                    return;
                } else if (configCreator.getConfigSetting().equals(false)) {
                    player.sendMessage(ManHuntPlugin.getprefix() + ChatColor.GOLD + configCreator.getConfigName() + ChatColor.GRAY + " : " + ChatColor.RED + s);
                    return;

                }
            }
        }
    }

    public static void resetPreset(Player player) {
        if (!GamePresetMenu.preset.presetName().equalsIgnoreCase(new Custom().presetName())) {
            Bukkit.getLogger().info(ManHuntPlugin.getprefix() + "游戏预设 : 自定义");
            player.sendMessage(ManHuntPlugin.getprefix() + "您更改了配置，游戏预设已更改回自定义");
            GamePresetMenu.preset = new Custom();
            GamePresetMenu.setFooderPreset(player);
            for (Menu menu : SettingsMenu.GamePreset.values()) menu.setMenuItems();
        }
    }

    public static void ShowAdvancement(Boolean bool) {
        for (World w : Bukkit.getWorlds()) {
            w.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, bool);
        }
    }

    public static void AnvilGUISetup(Player player, ConfigCreator configCreator) {
        String DisplayText = "";
        String addon = "";
        switch (configCreator.getConfigName()) {
            case "ReadyStartTime" -> {
                DisplayText = "Ready Time:";
                addon = "s";
            }
            case "CompassTriggerTimer" -> {
                DisplayText = "TriggerTime:";
                addon = "s";
            }
            case "GameResetTime" -> {
                DisplayText = "Reset Time:";
                addon = "h";
            }
            case "HuntStartTime" -> {
                DisplayText = "Hunt time:";
                addon = "s";
            }
            case "SpeedrunnerOpportunity" -> {
                DisplayText = "Opportunity:";
                addon = "%";
            }
            case "MaxPlayerSize" -> DisplayText = "MaxPlayers:";
        }


        AnvilGUI(player, DisplayText, configCreator.getConfigName(), configCreator.getMin(), configCreator.getMax(), addon, (Integer) configCreator.getConfigSetting());
    }

    public static void AnvilGUI(Player player, String DisplayText, String ConfigValue, Integer lowestValue, Integer highestValue, String addon, Integer current) {
        new AnvilGUI.Builder()
                .onClick((slot, stateSnapshot) -> {
                    String stateSnapshotString = stateSnapshot.getText().replace(DisplayText + " ", "");
                    if (Utilis.isNumeric(stateSnapshotString)) {
                        Integer input = Integer.parseInt(stateSnapshotString);
                        if (input >= lowestValue && input <= highestValue) {
                            ConfigCreator configCreator = ManHuntPlugin.getGameData().getGameConfig().getConfigCreators(ConfigValue);
                            if (configCreator != null) {
                                configCreator.setConfigSetting(input, ManHuntPlugin.getPlugin());
                                stateSnapshot.getPlayer().sendMessage(ManHuntPlugin.getprefix() + ChatColor.GOLD + ConfigValue + ChatColor.GRAY + " switched to" + " " + ChatColor.GREEN + input + " " + ChatColor.GRAY + addon);
                                for (UUID uuid : SettingsMenu.ConfigMenu.keySet()) {
                                    SettingsMenu.ConfigMenu.get(uuid).setMenuItems();
                                }
                            }
                            if (ConfigValue.equalsIgnoreCase("MaxPlayerSize")) {
                                if (current > input) {
                                    for (int i = Config.getMaxPlayerSize() - input; i < Config.getMaxPlayerSize(); i++) {
                                        Player SpecatorPlayer = Bukkit.getPlayer(ManHuntPlugin.getGameData().getGamePlayer().getPlayers().get(i));
                                        if (SpecatorPlayer != null) SpecatorPlayer.setGameMode(GameMode.SPECTATOR);
                                    }
                                }

                            }
                            if (ConfigValue.equalsIgnoreCase("HuntStartTime"))
                                StartGame.bossBarGameStart.setTime(input);
                            if (ConfigValue.equalsIgnoreCase("ReadyStartTime"))
                                Ready.ready.getbossBarCreator().setTime(input);
                            if (!ConfigValue.equalsIgnoreCase("GameResetTime")) {
                                if (GamePresetMenu.preset.presetName().equalsIgnoreCase(new Custom().presetName()) && GamePresetMenu.customHashMap != null) {
                                    GamePresetMenu.customHashMap.put(ConfigValue, input);
                                }
                                if (!ConfigValue.equalsIgnoreCase("MaxPlayerSize")) resetPreset(stateSnapshot.getPlayer());
                            }
                            if (SettingsMenu.ConfigMenu != null && SettingsMenu.ConfigMenu.get(stateSnapshot.getPlayer().getUniqueId()) != null)
                                SettingsMenu.ConfigMenu.get(stateSnapshot.getPlayer().getUniqueId()).open();
                            return AnvilGUI.Response.close();
                        }
                        stateSnapshot.getPlayer().sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "无效输入" + ChatColor.GRAY + "输入一个在此范围内的数" + ChatColor.GOLD + lowestValue + ChatColor.GRAY + " - " + ChatColor.GOLD + highestValue);
                    } else {
                        stateSnapshot.getPlayer().sendMessage(ManHuntPlugin.getprefix() + ChatColor.RED + "无效输入");
                    }
                    return AnvilGUI.Response.text(ChatColor.GRAY + DisplayText + " " + ChatColor.GREEN + ManHuntPlugin.getGameData().getGameConfig().getConfigCreators(ConfigValue).getConfigSetting());
                })
                .text(ChatColor.GRAY + DisplayText + " " + ChatColor.GREEN + ManHuntPlugin.getGameData().getGameConfig().getConfigCreators(ConfigValue).getConfigSetting())
                .itemLeft(new ItemStack(Material.CLOCK))
                .title(ChatColor.DARK_GRAY + DisplayText + " " + ChatColor.DARK_PURPLE + lowestValue + ChatColor.DARK_GRAY + " - " + ChatColor.DARK_PURPLE + highestValue + " " + addon)
                .plugin(ManHuntPlugin.getPlugin())
                .open(player);
    }
}



