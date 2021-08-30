package com.birds.cellulardata.service;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
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
	
	private void createUser(String user) {
		String create_user_table = "CREATE TABLE IF NOT EXISTS " + user +  " (\n" + "	id integer PRIMARY KEY,\n"
				+ "	date integer NOT NULL UNIQUE,\n" + "	data integer NOT NULL\n" + ");";

		excecute(create_user_table);
	}
	
	private Boolean exists(String fileName) {
		File file = new File(fileName);
		if (file.exists()) {
			return true;
		}
		return false;
	}
	
	private void excecute(String sql) {
		
	}
}
