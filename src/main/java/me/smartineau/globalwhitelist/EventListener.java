package me.smartineau.globalwhitelist;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.event.EventHandler;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EventListener implements Listener {
    GlobalWhitelistAPI api;
    Connection dbConnection;
    Configuration configuration;

    public EventListener() {
        api = GlobalWhitelistPlugin.getInstance().getAPI();
        dbConnection = GlobalWhitelistPlugin.getInstance().getDBConnection();
        configuration = GlobalWhitelistPlugin.getInstance().getConfig();
    }

    @EventHandler
    public void onLogin(LoginEvent event) {
        String uuid = event.getConnection().getUniqueId().toString();

        PreparedStatement query = null;
        try {
            query = dbConnection.prepareStatement(String.format("SELECT * FROM %s WHERE uuid = ? LIMIT 1;", configuration.getString("db.table")));
            query.setString(1, uuid);
            ResultSet resultSet = query.executeQuery();

            System.out.println();
            final Boolean isWhitelisted = resultSet.next();
            if (isWhitelisted) {
                final TextComponent text = new TextComponent("You're not whitelisted on this server!");
                text.setColor(ChatColor.RED);
                text.setBold(true);
                event.getConnection().disconnect(text);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    @EventHandler
    public List<String> onTabComplete(TabCompleteEvent event) {
        final List<String> suggestions = new ArrayList<String>();
        suggestions.add("hello");
        return suggestions;
    }
}