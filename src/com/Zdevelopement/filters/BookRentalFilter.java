package com.Zdevelopement.filters;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.Kdevelopement.data.RentalCarDAOFactory;
import com.Kdevelopement.exceptions.BadRequestException;
import com.Zdevelopement.data.RentalBookingDAOFactory;
import com.Zdevelopement.myFun.CustomFunctions;

import ca.on.senecac.prg556.common.StringHelper;
import ca.senecacollege.prg556.crba.bean.Client;
import ca.senecacollege.prg556.crba.bean.RentalBooking;
import ca.senecacollege.prg556.crba.bean.RentalCar;

public class BookRentalFilter implements Filter
{
    public BookRentalFilter() {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException 
	{
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		Boolean ifLoading = true;
		
		if ("POST".equals(req.getMethod()))
		{
			ifLoading = false;
			
			if (StringHelper.isNotNullOrEmpty(request.getParameter("search")))
			{
				//check pickup date
				String pickup = request.getParameter("pickupDate");
				if (CustomFunctions.CheckDate(pickup))
				{
					request.setAttribute("pickupDate", StringHelper.xmlEscape(pickup));
					request.setAttribute("invalidPickupDate", Boolean.FALSE);
				}
				else
					request.setAttribute("invalidPickupDate", Boolean.TRUE);
				
				//check dropoff date
				String dropoff = request.getParameter("dropoffDate");
				if (CustomFunctions.CheckDate(dropoff))
				{
					request.setAttribute("dropoffDate", StringHelper.xmlEscape(dropoff));
					request.setAttribute("invalidDropoffDate", Boolean.FALSE);
				}
				else
					request.setAttribute("invalidDropoffDate", Boolean.TRUE);
			
				String carSize = request.getParameter("carSize");
				request.setAttribute("carSize", carSize);
				
				Date newPickup = CustomFunctions.ConvertStringToDate(pickup);
				Date newDropoff = CustomFunctions.ConvertStringToDate(dropoff);
				if (newPickup == null || newDropoff == null)
					throw new BadRequestException("Invalid pickup or drop off date");
				
				if (!newPickup.before(newDropoff))
					request.setAttribute("dropOffBeforePickup", Boolean.TRUE);
				else
				{
					request.setAttribute("dropOffBeforePickup", Boolean.FALSE);
					try
					{
						List<RentalCar> cars = null;
						if(carSize.equals("A"))
							cars = RentalCarDAOFactory.getRentalCarDAO().getAvailableCars(newPickup, newDropoff, null);
						else
							cars = RentalCarDAOFactory.getRentalCarDAO().getAvailableCars(newPickup, newDropoff, carSize);						
						
						request.setAttribute("availableCars", cars);
					} catch (Exception e) {}
				}
			} else if (StringHelper.isNotNullOrEmpty(request.getParameter("select")))
			{
				try
				{
					String lplate = request.getParameter("licenseplate");
					Date newPickup = CustomFunctions.ConvertStringToDate(request.getParameter("pickupDate"));
					Date newDropoff = CustomFunctions.ConvertStringToDate(request.getParameter("dropoffDate"));
					HttpSession session = req.getSession();
					Client client = (Client)session.getAttribute("client");
					if(lplate == null || lplate.equals(""))
					{
						throw new BadRequestException("Error handling license plate");
					} else
					{
						RentalCar car = RentalCarDAOFactory.getRentalCarDAO().getRentalCar(lplate);
						if(car == null )
						{
							throw new BadRequestException("No car found with given license plate.");
						} else
						{
							RentalBooking booking = RentalBookingDAOFactory.getRentalBookingDAO().bookRentalCar(client.getId(), lplate, newPickup, newDropoff);
							resp.sendRedirect(req.getContextPath() + "/");
							return;
						}
					}
				} catch (SQLException s)
				{
					throw new ServletException(s);
				} catch (Exception e) {
					throw new BadRequestException(e);
				}
			}
		}
		req.setAttribute("ifLoading", ifLoading);
		chain.doFilter(request, response);
	}

	public void init(FilterConfig fConfig) throws ServletException {}

}
