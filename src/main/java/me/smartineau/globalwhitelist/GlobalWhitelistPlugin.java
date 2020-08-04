package me.smartineau.globalwhitelist;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.logging.Logger;

import com.mysql.cj.jdbc.MysqlDataSource;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

public final class GlobalWhitelistPlugin extends Plugin {
    private static GlobalWhitelistPlugin instance;
    private Logger logger;
    private GlobalWhitelistAPI api;
    private Connection dbConnection;
    private Configuration config;

    public static GlobalWhitelistPlugin getInstance() {
        return instance;
    }

    @Override
    public void onEnable() {
        logger = getLogger();
        config = _getConfig();

        instance = this;

        logger.info(ChatColor.GREEN + "Enabled GlobalWhitelist" + ChatColor.RESET);

        try {
            MysqlDataSource dataSource = new MysqlDataSource();
            dataSource.setUser(config.getString("db.user"));
            dataSource.setPassword(config.getString("db.password"));
            dataSource.setDatabaseName(config.getString("db.name"));
            dbConnection = dataSource.getConnection();
            initDBTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        api = new GlobalWhitelistAPI();
        logger.info(ChatColor.GREEN + "Successfully connected to MySQL" + ChatColor.RESET);

        getProxy().getPluginManager().registerCommand(this, new WhitelistCommand());
        getProxy().getPluginManager().registerListener(this, new EventListener());
    }

    @Override
    public void onDisable() {
        logger.info(ChatColor.RED + "Disabled GlobalWhitelist" + ChatColor.RESET);
    }

    public Connection getDBConnection() {
        return dbConnection;
    }

    private Configuration _getConfig() {
        if (!this.getDataFolder().exists()) this.getDataFolder().mkdir();
        File file = new File(this.getDataFolder(), "config.yml");
        System.out.println(file);
        System.out.println(this.getResourceAsStream("config.yml"));
        if (!file.exists()) {
            try {
                Files.copy(this.getResourceAsStream("config.yml"), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
            getProxy().getPluginManager().unregisterListeners(this);
            getProxy().getPluginManager().unregisterCommands(this);
            this.onDisable();
            return new Configuration();
        }
        try {
            return ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new Configuration();
    }

    public Configuration getConfig() {
        return config;
    }

    private void initDBTable() throws SQLException {
        dbConnection.createStatement().execute(String.format("CREATE TABLE IF NOT EXISTS %s (id int NOT NULL AUTO_INCREMENT UNIQUE, uuid char(32) NOT NULL UNIQUE, PRIMARY KEY (id));", config.getString("db.table")));
    }
}
