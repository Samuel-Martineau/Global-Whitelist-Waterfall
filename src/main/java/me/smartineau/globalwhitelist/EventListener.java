package me.smartineau.globalwhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class EventListener implements Listener {
    final private GlobalWhitelistAPI api;

    public EventListener() {
        api = GlobalWhitelistAPI.getInstance();
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        String uuid = event.getConnection().getUniqueId().toString().replace("-", "");

        final boolean isWhitelisted = api.isWhitelisted(uuid);
        if (!isWhitelisted) {
            final TextComponent text = new TextComponent("Vous n'êtes pas sur la liste blanche de ce serveur!");
            text.setColor(ChatColor.RED);
            text.setBold(true);
            event.getConnection().disconnect(text);

            api.addFailedLoginAttempt(event.getConnection().getName());
        }
    }
}