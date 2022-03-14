package xyz.lotho.me.skycore.handlers.rank;

import org.bukkit.ChatColor;

public enum RankType {

    STAFF(ChatColor.YELLOW),
    DONATOR(ChatColor.BLUE),
    DEFAULT(ChatColor.GRAY);

    private final ChatColor nameColor;

    private RankType(ChatColor nameColor) {
        this.nameColor = nameColor;
    }

    public ChatColor getNameColor() {
        return this.nameColor;
    }
}
