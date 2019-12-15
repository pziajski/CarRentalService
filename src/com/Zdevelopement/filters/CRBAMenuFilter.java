package com.Zdevelopement.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.Kdevelopement.data.ClientDAOFactory;
import com.Zdevelopement.data.RentalBookingDAOFactory;

import ca.senecacollege.prg556.crba.bean.Client;

public class CRBAMenuFilter implements Filter {

    public CRBAMenuFilter() {}

	public void destroy() {}
	
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest)request;
		try
		{
			HttpSession session = req.getSession();
			Client client = (Client)session.getAttribute("client");
			int numberOfBookings = RentalBookingDAOFactory.getRentalBookingDAO().getRentalBookings(client.getId()).size();
			request.setAttribute("numberOfBookings", numberOfBookings);
			chain.doFilter(request, response);
		} catch (Exception e)
		{
			throw new ServletException(e);
		}
	}

	public void init(FilterConfig fConfig) throws ServletException {}

}
