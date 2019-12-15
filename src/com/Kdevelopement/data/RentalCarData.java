package com.Kdevelopement.data;

import java.util.Date;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.LinkedList;
import java.util.List;

import com.Zdevelopement.data.DataSourceFactory;

import ca.senecacollege.prg556.crba.bean.RentalBooking;
import ca.senecacollege.prg556.crba.bean.RentalCar;
import ca.senecacollege.prg556.crba.dao.RentalCarDAO;

class RentalCarData implements RentalCarDAO
{
	public RentalCarData() {}
	
	public List<RentalCar> getAvailableCars(Date pickup, Date dropoff, String size) throws SQLException
	{
		List<RentalCar> cars = new LinkedList<RentalCar>();
		try (Connection conn = DataSourceFactory.getDataSource().getConnection())
		{
			try (Statement stmt = conn.createStatement())
			{
				String query;
				if (size != null)
				{
					query = "SELECT carmake, MIN(plate) as plate, carsize FROM rentalcar WHERE plate NOT IN (SELECT license_plate FROM rentalbooking WHERE (? BETWEEN pickup_date AND dropoff_date) OR (? BETWEEN pickup_date AND dropoff_date)) AND carsize = ? GROUP BY carsize, carmake ORDER BY carsize, carmake";
				} else
				{
					query = "SELECT carmake, MIN(plate) as plate, carsize FROM rentalcar WHERE plate NOT IN (SELECT license_plate FROM rentalbooking WHERE (? BETWEEN pickup_date AND dropoff_date) OR (? BETWEEN pickup_date AND dropoff_date)) GROUP BY carsize, carmake ORDER BY carsize, carmake";
				}
				try (PreparedStatement pstmt = conn.prepareStatement(query))
				{
					if (size != null)
					{
						pstmt.setTimestamp(1, new Timestamp(pickup.getTime()));
						pstmt.setTimestamp(2, new Timestamp(dropoff.getTime()));
						pstmt.setString(3, size);
					} else
					{
						pstmt.setTimestamp(1, new Timestamp(pickup.getTime()));
						pstmt.setTimestamp(2, new Timestamp(dropoff.getTime()));
					}
					try (ResultSet rslt = pstmt.executeQuery())
					{
						while (rslt.next())
						{
							cars.add(new RentalCar(rslt.getString("carmake"), rslt.getString("plate"), rslt.getString("carsize")));
						}
					}
				}
			}
		}
		return cars;
	}
	
	public RentalCar getRentalCar(String plate) throws SQLException
	{
		RentalCar car = null;
		try (Connection conn = DataSourceFactory.getDataSource().getConnection())
		{
			try (Statement stmt = conn.createStatement())
			{
				String search = "SELECT carmake, plate, carsize FROM rentalcar WHERE plate = ?";
				try (PreparedStatement pstmt = conn.prepareStatement(search))
				{
					pstmt.setString(1, plate);
					try (ResultSet rslt = pstmt.executeQuery())
					{
						if (rslt.next())
						{
							car = new RentalCar(rslt.getString("carmake"), rslt.getString("plate"), rslt.getString("carsize"));
						}
					}
				}
			}
		}
		return car;
	}
}
