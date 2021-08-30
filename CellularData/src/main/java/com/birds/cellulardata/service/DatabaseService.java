package com.birds.cellulardata.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class DatabaseService {
	
	private final static Logger LOGGER = Logger.getLogger(DatabaseService.class.getName());

	@Value("${sqlite-db-name}")
	String dbName;
	
	String url;

	public void initialize() {
		connectOrCreate();
	}

	private void connectOrCreate() {
		Boolean exists = exists(dbName);
		url = "jdbc:sqlite:" + dbName;

		if (exists) {
			LOGGER.fine("An existing Database has been found.");
		} else {
			LOGGER.fine("A new database will be created.");
		}

		try (Connection conn = DriverManager.getConnection(url)) {
			if (!exists) {
				LOGGER.fine("A new database has been created.");
			}
			LOGGER.info("Connection to SQLite has been established.");
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	}
	
	private void checkExists(String user) {
		String create_user_table = "CREATE TABLE IF NOT EXISTS " + user +  " (\n" + "	id integer PRIMARY KEY,\n"
				+ "	date integer NOT NULL UNIQUE,\n" + "	data real NOT NULL\n" + ");";

		excecute(create_user_table);
	}
	
	private Connection connect() {
		Connection conn = null;
		try {
			conn = DriverManager.getConnection(url);
		} catch (SQLException e) {
			System.out.println(e.getMessage());
		}
		return conn;
	}
	
	private Boolean exists(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	public void excecute(String sql) {
		try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	}
	
	public void insertData(String user, long date, double dataUsage) {
		checkExists(user);
		String sql = "INSERT OR REPLACE INTO " + user + "(date, data) VALUES(?,?)";
		try (Connection conn = this.connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
			pstmt.setLong(1, date);
			pstmt.setDouble(2, dataUsage);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			LOGGER.severe(e.getMessage());
		}
	}
	
	
}
