package com.Kdevelopement.data;

import com.Zdevelopement.data.DataSourceFactory;

import ca.senecacollege.prg556.crba.dao.ClientDAO;

public class ClientDAOFactory
{
	public ClientDAOFactory() {}
	
	public static ClientDAO getClientDAO()
	{
		return new ClientData(DataSourceFactory.getDataSource());
	}

}
