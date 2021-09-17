package de.shiirroo.manhunt.utilis.vote;

import de.shiirroo.manhunt.ManHuntPlugin;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class Vote {

    private final Set<UUID> votePlayers = new HashSet<>();
    private final BossBarCreator bossBarCreator;
    private final Boolean bossbarForVote;


    public Vote(Boolean bossbarForVote, Plugin plugin, String title, Integer voteTime){
        this.bossBarCreator = new BossBarCreator(plugin, title, voteTime, bossbarForVote, votePlayers);
        this.bossbarForVote = bossbarForVote;

    }

    public BossBarCreator getbossBarCreator() {
        return this.bossBarCreator;
    }


    public Set<UUID> getPlayers() {
        return votePlayers;
    }

    public void addVote(Player player) {
        if(Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() > 1){
        this.votePlayers.add(player.getUniqueId());
        ManHuntPlugin.getPlayerData().updatePlayers(ManHuntPlugin.getTeamManager());
        if(this.bossBarCreator.isRunning())
            this.bossBarCreator.getBossBar().setTitle(this.bossBarCreator.updateBossBarTitle());
        }
    }

    public void cancelVote(){
        this.bossBarCreator.cancel();
        this.votePlayers.clear();
    }

    public void startVote(){
        this.bossBarCreator.setBossBarPlayers();
    }

    public void removeVote(Player player){
        if(this.hasPlayerVote(player)) {
            this.votePlayers.remove(player.getUniqueId());
        }
        if(Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() -1 == 1 && this.votePlayers.size() <= 1 && bossbarForVote) {
            this.bossBarCreator.getCompleteFunction().accept(false);;
            this.cancelVote();
        }
    }

    public boolean hasPlayerVote(Player player) {
        if(votePlayers.contains(player.getUniqueId())){
            return true;
        }
        return false;
    }

}
