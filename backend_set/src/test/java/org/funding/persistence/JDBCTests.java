package org.funding.persistence;

import lombok.extern.log4j.Log4j2;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.DriverManager;

import static org.junit.jupiter.api.Assertions.fail;

@Log4j2
public class JDBCTests {
    @BeforeAll
    public static void setup() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    @DisplayName("JDBC 드라이버 연결")
    public void testConnection() {
        String url = "jdbc:mysql://localhost:3306/fund_DB";
        try(Connection con = DriverManager.getConnection(url, "root", "1111")) {
            log.info(con);
        } catch(Exception e) {
            fail(e.getMessage());
        }
    }

}
