package nl.sugcube.crystalquest.command;

import nl.sugcube.crystalquest.Broadcast;
import nl.sugcube.crystalquest.CrystalQuest;
import nl.sugcube.crystalquest.game.Arena;
import nl.sugcube.crystalquest.game.CrystalQuestTeam;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.stream.Collectors;

/**
 * @author SugarCaney
 */
public class CommandSetTeams extends CrystalQuestCommand {

    public CommandSetTeams() {
        super("setteams", "commands.setteams-usage", 3);

        addPermissions(
                "crystalquest.admin"
        );

        addAutoCompleteMeta(0, AutoCompleteArgument.ARENA);

        for (int i = 1; i <= 8; i++) {
            addAutoCompleteMeta(i, AutoCompleteArgument.TEAMS);
        }
    }

    @Override
    protected void executeImpl(CrystalQuest plugin, CommandSender sender, String... arguments) {
        try {
            Arena arena;
            try {
                arena = plugin.arenaManager.getArena(Integer.parseInt(arguments[0]) - 1);
            }
            catch (Exception e) {
                arena = plugin.arenaManager.getArena(arguments[0]);
            }

            // Check if the arenas exists.
            if (arena == null) {
                sender.sendMessage(Broadcast.get("arena.no-exist"));
                return;
            }

            Collection<CrystalQuestTeam> teams;
            try {
                teams = new ArrayList<>();
                teams.add(CrystalQuestTeam.valueOfName(arguments[1]));
                teams.add(CrystalQuestTeam.valueOfName(arguments[2]));

                for (int i = 3; i < arguments.length; i++) {
                    teams.add(CrystalQuestTeam.valueOfName(arguments[i]));
                }

                if (new HashSet<>(teams).size() != teams.size()) {
                    sender.sendMessage(Broadcast.get("commands.setteams-double"));
                    return;
                }

                arena.setTeams(teams);
            }
            catch (IllegalArgumentException iae) {
                sender.sendMessage(Broadcast.get("commands.invalid-teams"));
                return;
            }

            String addedTeams = teams.stream().map(CrystalQuestTeam::getName).collect(Collectors.joining(", "));
            sender.sendMessage(Broadcast.TAG + Broadcast.get("commands.setteams-set")
                    .replace("%arena%", arguments[0])
                    .replace("%amount%", addedTeams));
            arena.resetArena(false);
        }
        catch (Exception e) {
            sender.sendMessage(Broadcast.get("commands.setteams-error"));
        }
    }

    @Override
    protected boolean assertSender(CommandSender sender) {
        return true;
    }
}
