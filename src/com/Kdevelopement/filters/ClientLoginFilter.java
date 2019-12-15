package com.Kdevelopement.filters;

import java.io.IOException;
import java.sql.SQLException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import ca.on.senecac.prg556.common.StringHelper;
import ca.senecacollege.prg556.crba.bean.Client;

import com.Kdevelopement.data.ClientDAOFactory;
import com.Kdevelopement.exceptions.BadRequestException;

public class ClientLoginFilter implements Filter
{
    public ClientLoginFilter() {}

	public void destroy() {}

	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException
	{
		HttpServletRequest req = (HttpServletRequest)request;
		HttpServletResponse resp = (HttpServletResponse)response;
		
		String username = req.getParameter("username");
		String password = req.getParameter("password");
		
		if ("POST".equals(req.getMethod()) && StringHelper.isNotNullOrEmpty(request.getParameter("login")))
		{
			if ((username != null && !username.equals("")) && (password != null && !password.equals("")))
			{
				try
				{
					Client temp = ClientDAOFactory.getClientDAO().authenticateClient(username, password);
					if (temp != null)
					{
						HttpSession session = req.getSession();
						session.setAttribute("client", temp);
						resp.sendRedirect(req.getContextPath() + "/");
						return;
					} else
					{
						req.setAttribute("username",StringHelper.xmlEscape(username));
						req.setAttribute("error", Boolean.TRUE);
					}
				} catch (NumberFormatException e)
				{
					throw new BadRequestException(e);
				} catch (SQLException sqle)
				{
					throw new ServletException(sqle);
				}
			}
			
		}
		chain.doFilter(request, response);

	}
	
	public void init(FilterConfig fConfig) throws ServletException {}

}
