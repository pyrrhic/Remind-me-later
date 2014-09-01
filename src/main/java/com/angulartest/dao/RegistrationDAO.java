package com.angulartest.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Repository;

import com.angulartest.model.RegistrationFO;
import com.angulartest.utility.MyConstants;

@Repository
public class RegistrationDAO {
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public RegistrationDAO(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public void register(RegistrationFO regUser) {
		Date d = new Date(Calendar.getInstance().getTimeInMillis());

		String sql = "INSERT INTO " + MyConstants.SCHEMA_NAME + ".USERS "
				+ "(USERNAME, PASSWORD, ENABLED, LAST_LOGIN, AUTHORITY) VALUES (?, ?, ? ,?, ?)";

		jdbcTemplate.update(sql, new Object[] { regUser.getEmail(), regUser.getPassword(), Boolean.valueOf(true), d, "ROLE_USER" });
	}

	public boolean isEmailRegistered(String email) {
		final String scopeIssueEmailVar = email;

		String sql = "SELECT username FROM " + MyConstants.SCHEMA_NAME + ".users WHERE username = ?";

		List<String> emails = jdbcTemplate.query(sql, new Object[] { email }, new RowMapper<String>() {
			public String mapRow(ResultSet rs, int rowNum) throws SQLException {
				String returnedEmail = rs.getString("username");
				if (returnedEmail.equals(scopeIssueEmailVar)) {
					return returnedEmail;
				}

				return "";
			}
		});
		
		boolean isRegistered = false;
		for (String e : emails) {
			if (e.length() > 0) {
				isRegistered = true;
				break;
			}
		}

		return isRegistered;
	}
}
