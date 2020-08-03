package me.smartineau.globalwhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;

public class WhitelistCommand extends Command {
    public WhitelistCommand(GlobalWhitelistPlugin globalWhitelistPlugin) {
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
                    api.whitelistPlayer(playerName);
                    break;
    
                case "remove":
                    
                    break;
                    
                default:
                    break;
            }
        }
    }
}