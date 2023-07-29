package de.shiirroo.manhunt.world;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.utilis.Utilis;
import de.shiirroo.manhunt.utilis.vote.BossBarCreator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.Arrays;

public class Worldreset {

    private final BossBarCreator worldReset = creatorWorldReset();

    public void reset() {
        if (Bukkit.getWorlds().size() == 0) {
            Arrays.asList("world", "world_nether", "world_the_end").forEach(worldName -> {
                Bukkit.unloadWorld(worldName, false);
                Utilis.deleteRecursively(new File(worldName), false);
            });
        } else {
            Bukkit.getWorlds().forEach(world -> {
                Bukkit.unloadWorld(world, false);
                Utilis.deleteRecursively(new File(world.getName()), false);
            });
        }
    }

    public BossBarCreator getWorldReset() {
        return worldReset;
    }

    public void resetBossBar() {
        Bukkit.getLogger().info(ManHuntPlugin.getprefix() + "正在重置世界");
        worldReset.setBossBarPlayers();
    }

    private BossBarCreator creatorWorldReset() {
        BossBarCreator bossBarCreator = new BossBarCreator(ManHuntPlugin.getPlugin(), ChatColor.GREEN + "世界将会重置于" + ChatColor.RED + " TIMER", 30);
        bossBarCreator.onComplete(aBoolean -> {
            Bukkit.setWhitelist(true);
            for (Player p : Bukkit.getOnlinePlayers()) {
                p.kickPlayer(ManHuntPlugin.getprefix() + "世界重置中..");
            }
            ManHuntPlugin.getPlugin().getConfig().set("isReset", true);
            ManHuntPlugin.getPlugin().saveConfig();
            Bukkit.spigot().restart();
        });
        return bossBarCreator;
    }

}
