package me.smartineau.globalwhitelist;

import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.util.stream.Collectors;

public class WhitelistCommand extends Command implements TabExecutor {
    public WhitelistCommand() {
        super("whitelist");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        final GlobalWhitelistAPI api = GlobalWhitelistAPI.getInstance();

        boolean syntaxError = false;
        String reason = "";

        if (args.length == 2) {
            final String playerName = args[1];
            switch (args[0].toLowerCase()) {
                case "add":
                    try {
                        api.whitelistPlayer(playerName);
                    } catch (PlayerNotFoundException e) {
                        e.printStackTrace();
                    }
                    break;

                case "remove":

                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2)
            return ImmutableSet.of(GlobalWhitelistPlugin.getInstance().getProxy().getPlayers().stream().map(CommandSender::getName).collect(Collectors.joining()));
        return ImmutableSet.of();
    }
}