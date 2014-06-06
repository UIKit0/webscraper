package com.websqrd.catbot.db;

import java.sql.Connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractDAO {
	private static Logger logger = LoggerFactory.getLogger(AbstractDAO.class);
	protected Connection conn;
	protected String tableName;
	public AbstractDAO(){
		tableName = getClass().getSimpleName();
	}
	
	public AbstractDAO(Connection conn){
		this();
		this.conn = conn;
	}
}
