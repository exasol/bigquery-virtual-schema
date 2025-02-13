package com.exasol.adapter.dialects.bigquery;

import java.sql.*;

public class BigQTester {
    public static void main(final String[] args) throws SQLException {
        final Connection connection = DriverManager.getConnection(
                "jdbc:bigquery://http://192.168.56.7:9050;ProjectId=myProject;RootURL=http://192.168.56.7:9050;OAuthType=2;OAuthAccessToken=dummy-token",
                null, null);
        connection.getMetaData().getTables(null, null, null, null);
    }
}
