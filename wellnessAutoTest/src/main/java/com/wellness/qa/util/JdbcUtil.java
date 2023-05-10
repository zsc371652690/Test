package com.wellness.qa.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JdbcUtil {
    private static final Logger log = LoggerFactory.getLogger(com.wellness.qa.apiUtils.db.JdbcUtil.class);

    public JdbcUtil() {
    }

    public static String getDrivverClassFromUrl(String url) {
        if (url.startsWith("jdbc:mysql")) {
            return "com.mysql.cj.jdbc.Driver";
        } else if (url.startsWith("jdbc:oracle")) {
            return "oracle.jdbc.driver.OracleDeriver";
        } else {
            log.error("unsupported jdbc url");
            return null;
        }
    }

    public static Connection getConnection(String url, String userName, String password) {
        Connection connection = null;

        try {
            Class.forName(getDrivverClassFromUrl(url));
            connection = DriverManager.getConnection(url, userName, password);
        } catch (SQLException | ClassNotFoundException var5) {
            log.error("DB connection initial failed!");
            var5.printStackTrace();
        }

        return connection;
    }

    public static Connection getConnection(String dbName, Map<String, String> ENV) {
        String url = (String)ENV.get("db." + dbName + ".url");
        String userName = (String)ENV.get("db." + dbName + ".username");
        String password = (String)ENV.get("db." + dbName + ".password");
        return getConnection(url, userName, password);
    }

    public static void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException var2) {
                log.error("Close connection failed!");
                var2.printStackTrace();
            }
        }

    }
}
