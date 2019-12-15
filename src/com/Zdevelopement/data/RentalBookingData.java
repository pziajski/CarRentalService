package com.Zdevelopement.data;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import javax.sql.DataSource;
import com.Zdevelopement.myFun.CustomFunctions;
import ca.senecacollege.prg556.crba.bean.RentalBooking;
import ca.senecacollege.prg556.crba.dao.RentalBookingDAO;

class RentalBookingData implements RentalBookingDAO
{
	private DataSource ds;
	
	public RentalBookingData (DataSource ds)
	{
		this.ds = ds;
	}
	
	public RentalBooking bookRentalCar(int clientId, String plate, Date pickup, Date dropoff) throws SQLException
	{
		RentalBooking booking = null;
		try (Connection conn = this.ds.getConnection())
		{
			try (Statement stmt = conn.createStatement())
			{
				String query = String.format("INSERT INTO rentalbooking (client_id, license_plate, pickup_date, dropoff_date) VALUES (?, ?, ?, ?)");				
				try (PreparedStatement pstmt = conn.prepareStatement(query))
				{
					pstmt.setInt(1, clientId);
					pstmt.setString(2, plate);
					pstmt.setTimestamp(3, new Timestamp(pickup.getTime()));
					pstmt.setTimestamp(4, new Timestamp(dropoff.getTime()));
					pstmt.executeUpdate();
				}
				
				String search = "SELECT book_num, rentalcar.carmake AS carmake, rentalcar.carsize AS carsize FROM rentalbooking INNER JOIN rentalcar ON rentalbooking.license_plate = rentalcar.plate WHERE (pickup_date = ?) AND (dropoff_date = ?) AND (license_plate = ?)";
				try (PreparedStatement pstmt = conn.prepareStatement(search))
				{
					pstmt.setTimestamp(1, new Timestamp(pickup.getTime()));
					pstmt.setTimestamp(2, new Timestamp(dropoff.getTime()));
					pstmt.setString(3, plate);
					try (ResultSet rslt = pstmt.executeQuery())
					{
						if (rslt.next())
						{
							booking = new RentalBooking(rslt.getInt("book_num"), rslt.getString("carmake"), plate, rslt.getString("carsize"), pickup, dropoff);
						}
					}
				}
			}
		}
		return booking;
	}

	public void cancelRentalBooking(int bookingNo) throws SQLException
	{
		try (Connection conn = this.ds.getConnection())
		{
			try (Statement stmt = conn.createStatement())
			{
				String query = String.format("DELETE FROM rentalbooking WHERE book_num = ?");				
				try (PreparedStatement pstmt = conn.prepareStatement(query))
				{
					pstmt.setInt(1, bookingNo);
					pstmt.executeUpdate();
				}
			}
		}
	}

	public RentalBooking getRentalBooking(int bookingNo) throws SQLException
	{
		RentalBooking booking;
		try (Connection conn = this.ds.getConnection())
		{
			try (Statement stmt = conn.createStatement())
			{
				String query = "SELECT rentalbooking.book_num, rentalbooking.license_plate, rentalbooking.pickup_date, rentalbooking.dropoff_date, rentalcar.carmake, rentalcar.carsize FROM rentalbooking INNER JOIN rentalcar ON rentalbooking.license_plate = rentalcar.plate WHERE rentalbooking.book_num = ?";
				try (PreparedStatement pstmt = conn.prepareStatement(query))
				{
					pstmt.setInt(1, bookingNo);
					try (ResultSet rslt = pstmt.executeQuery())
					{
						if (rslt.next())
						{
							booking = new RentalBooking(Integer.parseInt(rslt.getString("book_num")), rslt.getString("carmake"), rslt.getString("license_plate"), rslt.getString("carsize"), CustomFunctions.ConvertStringToDate(rslt.getString("pickup_date")), CustomFunctions.ConvertStringToDate(rslt.getString("dropoff_date")));
							return booking;
						}
					}
				}
			}
		}
		
		return null;
	}

	public List<RentalBooking> getRentalBookings(int clientId) throws SQLException
	{
		List<RentalBooking> bookings = new LinkedList<RentalBooking>();
		try (Connection conn = this.ds.getConnection())
		{
			try (Statement stmt = conn.createStatement())
			{
				String query = "SELECT rentalbooking.book_num, rentalbooking.license_plate, rentalbooking.pickup_date, rentalbooking.dropoff_date, rentalcar.carmake, rentalcar.carsize FROM rentalbooking INNER JOIN rentalcar ON rentalbooking.license_plate = rentalcar.plate WHERE rentalbooking.client_id = ?";
				try (PreparedStatement pstmt = conn.prepareStatement(query))
				{
					pstmt.setInt(1, clientId);
					try (ResultSet rslt = pstmt.executeQuery())
					{
						while (rslt.next())
						{
							bookings.add(new RentalBooking(Integer.parseInt(rslt.getString("book_num")),
									rslt.getString("carmake"), rslt.getString("license_plate"), rslt.getString("carsize"),
									new Date(rslt.getTimestamp("pickup_date").getTime()),
									new Date(rslt.getTimestamp("dropoff_date").getTime()))
							);
						}
					}
				}
			}
		}
		return bookings;
	}
}
