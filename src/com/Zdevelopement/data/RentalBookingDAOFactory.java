package com.Zdevelopement.data;

import ca.senecacollege.prg556.crba.dao.RentalBookingDAO;

public class RentalBookingDAOFactory {
	
	public static RentalBookingDAO getRentalBookingDAO()
	{
		return new RentalBookingData(DataSourceFactory.getDataSource());
	}
	
}
