package me.smartineau.globalwhitelist;

import java.io.IOException;

import com.google.common.collect.ImmutableSet;

import me.smartineau.globalwhitelist.exceptions.PlayerAlreadyWhitelistedException;
import me.smartineau.globalwhitelist.exceptions.PlayerNotFoundException;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

public class WhitelistCommand extends Command implements TabExecutor {
    final GlobalWhitelistAPI api;
    final GlobalWhitelistPlugin plugin;

    public WhitelistCommand() {
        super("gwhitelist", "globalwhitelistplugin.use");
        api = GlobalWhitelistAPI.getInstance();
        plugin = GlobalWhitelistPlugin.getInstance();
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        boolean syntaxError = false;

        if (args.length == 2) {
            final String playerName = args[1];
            switch (args[0].toLowerCase()) {
                case "add":
                    try {
                        api.addPlayerToWhitelist(api.getPlayerUUID(playerName));
                        final TextComponent text = new TextComponent("Le joueur a été ajouté sur la liste blanche de ce serveur!");
                        text.setColor(ChatColor.GREEN);
                        sender.sendMessage(text);
                    } catch (IOException | InterruptedException e) {
                        e.printStackTrace();
                    } catch (PlayerAlreadyWhitelistedException e) {
                        final TextComponent text = new TextComponent("Ce joueur est déjà sur la liste blanche de ce serveur!");
                        text.setColor(ChatColor.RED);
                        sender.sendMessage(text);
                    } catch (PlayerNotFoundException e) {
                        final TextComponent text = new TextComponent("Ce joueur n'existe pas!");
                        text.setColor(ChatColor.RED);
                        sender.sendMessage(text);
                    }
                    api.removeFailedLoginAttempt(playerName);
                    break;

                case "remove":
                    try {
                        api.removePlayerFromWhitelist(api.getPlayerUUID(playerName));

                        final TextComponent senderResponsetext = new TextComponent("Le joueur a été supprimé de la liste blanche de ce serveur!");
                        senderResponsetext.setColor(ChatColor.GREEN);
                        sender.sendMessage(senderResponsetext);
                        
                        final TextComponent disconnectText = new TextComponent("Vous n'êtes plus sur la liste blanche de ce serveur!");
                        disconnectText.setColor(ChatColor.RED);
                        disconnectText.setBold(true);
                        plugin.getProxy().getPlayer(playerName).disconnect(disconnectText);
					} catch (IOException | InterruptedException e) {
						e.printStackTrace();
					} catch (PlayerNotFoundException e) {
                        final TextComponent text = new TextComponent("Ce joueur n'existe pas!");
                        text.setColor(ChatColor.RED);
                        sender.sendMessage(text);
                    }
                    break;

                default:
                    syntaxError = true;
                    break;
            }
        } else {
            syntaxError = true;
        }

        if (syntaxError) {
            final TextComponent text = new TextComponent("Syntaxe invalide! Veuillez utiliser /gwhitelist <add|remove> <player name>");
            text.setColor(ChatColor.RED);
            sender.sendMessage(text);
        }
    }

    @Override
    public Iterable<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 1) return ImmutableSet.of("add", "remove");
        else if (args.length == 2) {
            switch (args[0]) {
                case "add":
                    api.updateFailedLoginAttempts();
                    return api.getFailedLoginAttempts().keySet();
                case "remove":
                    return api.getWhitelistedPlayers();
            }
        }
        return ImmutableSet.of();
    }
}