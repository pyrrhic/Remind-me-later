package com.angulartest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.angulartest.utilities.MyConstants;

@Repository
public class NoSendListDAO {
	@Autowired
    private JdbcTemplate jdbcTemplate;
	
	public void addToNoSendList(String contact) {
		String sql = "INSERT INTO " +  MyConstants.SCHEMA_NAME + ".nosendlist " +
					 "(contact) " +
				     "VALUES(?)";
		
		jdbcTemplate.update(sql, new Object[] { contact });
	}
		
	public boolean isContactOnNoSendList(String contact) {
		String sql = "SELECT contact FROM " + MyConstants.SCHEMA_NAME + ".nosendlist " +
				     "WHERE contact = ?"; 
		
		List<String> c = jdbcTemplate.query(sql, new Object[] { contact }, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				
				return rs.getString("contact");
			}
		});
		
		return (c.size() > 0) ? true : false;
	}
}
