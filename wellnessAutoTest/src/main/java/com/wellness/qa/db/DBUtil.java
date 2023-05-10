package com.wellness.qa.db;


import com.wellness.qa.apiUtils.db.JdbcUtil;
import com.wellness.qa.config.EnvConfig;



import java.sql.Connection;

public class DBUtil {

    public static Connection getConnection(String dbName) {
        return JdbcUtil.getConnection(dbName,  EnvConfig.ENV);
    }
}
