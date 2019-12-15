package com.Kdevelopement.filters;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ca.on.senecac.prg556.common.StringHelper;
import ca.senecacollege.prg556.crba.bean.Client;
import ca.senecacollege.prg556.crba.bean.RentalBooking;

import com.Kdevelopement.exceptions.BadRequestException;
import com.Zdevelopement.data.RentalBookingDAOFactory;

public class ShowBookingsFilter implements Filter
{
    public ShowBookingsFilter() {}
    
	public void destroy() {}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest)request;
		HttpSession session = req.getSession();
		Client client = (Client) session.getAttribute("client");
		Integer clientID = client.getId();
		
		if ("POST".equals(req.getMethod()) && StringHelper.isNotNullOrEmpty(request.getParameter("cancel")))
		{
			try
			{
				Integer bookingNo = Integer.parseInt(req.getParameter("bookingNo"));
				RentalBookingDAOFactory.getRentalBookingDAO().cancelRentalBooking(bookingNo);
			} catch (Exception e)
			{
				throw new BadRequestException(e);
			}
		}
		
		try
		{
			List<RentalBooking> cars = RentalBookingDAOFactory.getRentalBookingDAO().getRentalBookings(clientID);
			req.setAttribute("carsBooked", cars);
		} catch (SQLException e)
		{
			throw new ServletException(e);
		}
		
		chain.doFilter(request, response);
	}
	
	public void init(FilterConfig fConfig) throws ServletException {}
}
