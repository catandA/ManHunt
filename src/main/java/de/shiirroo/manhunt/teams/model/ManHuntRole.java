package de.shiirroo.manhunt.teams.model;

import org.bukkit.ChatColor;

public enum ManHuntRole {
    Assassin(ChatColor.BLUE),
    Speedrunner(ChatColor.DARK_PURPLE),
    Hunter(ChatColor.RED),
    Unassigned(ChatColor.YELLOW);

    private final ChatColor chatColor;

    ManHuntRole(ChatColor chatColor) {
        this.chatColor = chatColor;
    }


    public ChatColor getChatColor() {
        return chatColor;
    }

    @Override
    public String toString() {
        return switch (this) {
            case Assassin -> "刺客";
            case Speedrunner -> "速通者";
            case Hunter -> "猎人";
            case Unassigned -> "无业游民";
        };
    }
}
