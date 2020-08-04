package me.smartineau.globalwhitelist;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;

import com.google.common.collect.ImmutableSet;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import me.smartineau.globalwhitelist.exceptions.PlayerAlreadyWhitelistedException;
import me.smartineau.globalwhitelist.exceptions.PlayerNotFoundException;
import net.md_5.bungee.config.Configuration;

public class GlobalWhitelistAPI {
    private static GlobalWhitelistAPI instance;
    private final Connection dbConnection;
    private final Configuration config;
    private final HashMap<String, Instant> failedLoginAttempts = new HashMap<String, Instant>();

    public GlobalWhitelistAPI() {
        instance = this;
        dbConnection = GlobalWhitelistPlugin.getInstance().getDBConnection();
        config = GlobalWhitelistPlugin.getInstance().getConfig();
    }

    public static GlobalWhitelistAPI getInstance() {
        return instance;
    }

    public HashMap<String, Instant> getFailedLoginAttempts() {
        return new HashMap<String, Instant>(failedLoginAttempts);
    }

    public void updateFailedLoginAttempts() {
        final HashMap<String, Instant> failedLoginAttempts = this.failedLoginAttempts;
        for (String playerName : failedLoginAttempts.keySet()) {
            if (failedLoginAttempts.get(playerName).isBefore(Instant.now().minusSeconds(60 * 10))) {
                failedLoginAttempts.remove(playerName);
            }
        }
    }

    public void addFailedLoginAttempt(String playerName) {
        if (!failedLoginAttempts.containsKey(playerName)) {
            failedLoginAttempts.put(playerName, Instant.now());
        } else {
            failedLoginAttempts.replace(playerName, Instant.now());
        }
    }

    public void removeFailedLoginAttempt(String playerName) {
        failedLoginAttempts.remove(playerName);
    }

    public Iterable<String> getWhitelistedPlayers() {
        final ArrayList<String> playerNames = new ArrayList<String>();
        final ResultSet resultSet;
        try {
            resultSet = dbConnection.createStatement().executeQuery("SELECT uuid FROM whitelist;");
            while (resultSet.next()) {
                try {
                    playerNames.add(getPlayerName(resultSet.getString(1)));
                } catch (PlayerNotFoundException | IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
            return playerNames;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ImmutableSet.of();
    }

    public boolean isWhitelisted(String uuid) {
        try {
            ResultSet resultSet = dbConnection.createStatement().executeQuery(String.format("SELECT * FROM %s WHERE uuid = `%s` LIMIT 1;", config.getString("db.table"), uuid));
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void addPlayerToWhitelist(String uuid) throws PlayerAlreadyWhitelistedException {
        try {
            dbConnection.createStatement().execute(
                    String.format("INSERT INTO %s (uuid) values(`%s`)", config.getString("db.table"), uuid)
            );
        } catch (SQLException e) {
            if (e instanceof SQLIntegrityConstraintViolationException) {
                throw new PlayerAlreadyWhitelistedException();
            }
            e.printStackTrace();
        }
    }

    public void removePlayerFromWhitelist(String uuid) {
        try {
			dbConnection.createStatement().execute(
			    String.format("DELETE FROM %s WHERE uuid = `%s` LIMIT 1;", config.getString("db.table"), uuid)
			);
		} catch (SQLException e) {
			e.printStackTrace();
		}
    }

    public String getPlayerUUID(String playerName) throws PlayerNotFoundException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://api.mojang.com/users/profiles/minecraft/" + playerName)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        if (status == 200) {
            String rawRes = response.body();
            JsonParser parser = new JsonParser();
            JsonObject parsedRes = (JsonObject) parser.parse(rawRes);
            return parsedRes.get("id").toString().replace("\"", "");
        } else throw new PlayerNotFoundException();
    }

    public String getPlayerName(String playerUUID) throws PlayerNotFoundException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder().uri(URI.create("https://sessionserver.mojang.com/session/minecraft/profile/" + playerUUID)).build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        int status = response.statusCode();
        if (status == 200) {
            String rawRes = response.body();
            JsonParser parser = new JsonParser();
            JsonObject parsedRes = (JsonObject) parser.parse(rawRes);
            return parsedRes.get("name").toString().replace("\"", "");
        } else throw new PlayerNotFoundException();
    }
}
