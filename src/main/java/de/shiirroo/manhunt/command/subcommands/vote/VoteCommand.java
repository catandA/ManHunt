package de.shiirroo.manhunt.command.subcommands.vote;

import de.shiirroo.manhunt.ManHuntPlugin;
import de.shiirroo.manhunt.command.CommandBuilder;
import de.shiirroo.manhunt.command.SubCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class VoteCommand extends SubCommand {

    private static Vote vote;

    public static void resetVote() {
        vote = null;
    }

    public static Vote getVote() {
        return vote;
    }

    @Override
    public String getName() {
        return "vote";
    }

    @Override
    public String getDescription() {
        return "为其中一个可用投票或创建一个新投票";
    }

    @Override
    public String getSyntax() {
        return "/manhunt vote 或者 vote [投票项目]";
    }

    @Override
    public Boolean getNeedOp() {
        return false;
    }

    @Override
    public CommandBuilder getSubCommandsArgs(String[] args) {
        CommandBuilder cm = new CommandBuilder("Vote");
        if(VoteCommand.getVote() == null) {
            CommandBuilder create = new CommandBuilder("Create");
            create.addSubCommandBuilder(new CommandBuilder("Skip-Day"));
            create.addSubCommandBuilder(new CommandBuilder("Skip-Night"));
            create.addSubCommandBuilder(new CommandBuilder("Pause"));
            cm.addSubCommandBuilder(create);
        }
        return cm;

    }

    @Override
    public void perform(Player player, String[] args) {
        if (ManHuntPlugin.getGameData().getGameStatus().isGameRunning() && ManHuntPlugin.getGameData().getGameStatus().getLivePlayerList().contains(player.getUniqueId())) {
            if (vote != null && args.length == 1) {
                if (vote.getVoteCreator().hasPlayerVote(player)) {
                    player.sendMessage(ManHuntPlugin.getprefix() + "你投了票了");
                } else {
                    vote.getVoteCreator().addVote(player);
                }
            } else if (vote == null) {
                if (Bukkit.getOnlinePlayers().stream().filter(e -> !e.getGameMode().equals(GameMode.SPECTATOR)).count() >= 2 && args.length == 3 && args[1].equalsIgnoreCase("Create")) {
                    Vote createVote = null;
                    switch (args[2].toLowerCase()) {
                        case "skip-night" -> createVote = new VoteSkipNight();
                        case "skip-day" -> createVote = new VoteSkipDay();
                        case "pause" -> setPause(player);
                    }
                    if (createVote != null) {
                        createVote.startVote(player);
                        if (createVote.requirement()) {
                            vote = createVote;
                        }
                    }
                } else {
                    player.sendMessage(ManHuntPlugin.getprefix() + "现在没有投票");
                }
            } else {
                player.sendMessage(ManHuntPlugin.getprefix() + "现在已经有个投票项目了");
            }
        } else {
            player.sendMessage(ManHuntPlugin.getprefix() + "目前无法开始投票");
        }
    }


    public void setPause(Player player) {
        Vote votePause = new VotePause();
        Vote voteContinue = new VoteContinue();
        if (votePause.requirement()) {
            vote = votePause;
            vote.startVote(player);
        } else if (voteContinue.requirement()) {
            vote = new VoteContinue();
            vote.startVote(player);
        }
    }
}