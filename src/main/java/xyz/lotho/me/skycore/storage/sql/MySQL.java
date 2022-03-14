package xyz.lotho.me.skycore.storage.sql;

import xyz.lotho.me.skycore.SkyCore;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySQL {

    protected SkyCore instance;
    protected Connection connection;
    protected String url;

    protected String username, password;

    public MySQL(SkyCore instance) {
        this.instance = instance;

        this.username = this.instance.config.get().getString("database.username");
        this.password = this.instance.config.get().getString("database.password");
        this.url = this.instance.config.get().getString("database.url");

        this.connect();
    }

    public void connect() {
        try {
            this.setConnection(DriverManager.getConnection(this.url, this.username, this.password));
        } catch (SQLException exception) {
            exception.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (this.connection != null && !this.connection.isClosed()) {
                this.connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnectionOpen() {
        boolean open = false;

        try {
            open = !this.connection.isClosed();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return open;
    }

    public Connection getConnection() {
        try {
            if (this.connection == null || this.connection.isClosed()) {
                this.setConnection(DriverManager.getConnection(this.url, this.username, this.password));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return this.connection;
    }

    public void setConnection(Connection connection) {
        this.connection = connection;
    }
}
