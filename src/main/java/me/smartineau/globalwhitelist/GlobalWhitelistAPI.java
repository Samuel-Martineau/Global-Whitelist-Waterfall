package me.smartineau.globalwhitelist;

import java.sql.Connection;

public class GlobalWhitelistAPI {
    private static GlobalWhitelistAPI instance;
    private Connection dbConnection;

    public GlobalWhitelistAPI() {
        instance = this;
        this.dbConnection = GlobalWhitelistPlugin.getInstance().getDBConnection();
    }

    public static GlobalWhitelistAPI getInstance() {
        return instance;
    }

    public void whitelistPlayer(String playerName) {
        final String uuid = getPlayerUUID(playerName);
        dbConnection.createStatement().execute(
          String.format("INSERT INTO %s (uuid) values(%s)", GlobalWhitelistPlugin.getInstance().getConfig().getString("db.table"), uuid)
        );
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
}
