package com.Kdevelopement.data;

import ca.senecacollege.prg556.crba.dao.RentalCarDAO;

public class RentalCarDAOFactory
{
	public RentalCarDAOFactory() {}
	
	public static RentalCarDAO getRentalCarDAO()
	{
		return new RentalCarData();
	}

}
