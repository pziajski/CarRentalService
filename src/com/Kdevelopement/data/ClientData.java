package com.Kdevelopement.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import ca.senecacollege.prg556.crba.bean.Client;
import ca.senecacollege.prg556.crba.dao.ClientDAO;

class ClientData implements ClientDAO
{
	private DataSource ds;
	
	public ClientData(DataSource ds)
	{
		this.ds = ds;
	}
	
	@Override
	public Client authenticateClient(String username, String password) throws SQLException
	{
		//Search for a client with specified username/password in client table
		//if found, method will return a client object with clientid, firstname, lastname.
		//if no client found, return null.
		try(Connection conn = this.ds.getConnection())
		{
			try(PreparedStatement statement = conn.prepareStatement("SELECT id, first_name, last_name FROM client WHERE username = ? AND password = ?"))
			{
				statement.setString(1, username);
				statement.setString(2, password);
				try (ResultSet rs = statement.executeQuery())
				{
					if (rs.next())
					{
						int id = rs.getInt("id");
						String fn = rs.getString("first_name");
						String ln = rs.getString("last_name");
						return new Client(id, fn, ln);
					} else
					{
						return null;
					}
				}
			}
		}
	}
	
	@Override
	public Client getClient(int id) throws SQLException
	{
		try(Connection conn = this.ds.getConnection())
		{
			try(PreparedStatement statement = conn.prepareStatement("SELECT id, first_name, last_name FROM client WHERE id = ?", ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY))
			{
				statement.setInt(1, id);
				try(ResultSet rs = statement.executeQuery())
				{
					if (rs.next())
					{
						int clientId = rs.getInt("id");
						String fn = rs.getString("first_name");
						String ln = rs.getString("last_name");
						return new Client(clientId, fn, ln);
					}
					else
					{
						return null;
					}
				}
			}
		}
	}
}

