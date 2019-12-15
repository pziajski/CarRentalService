package com.Kdevelopement.listeners;

import javax.annotation.Resource;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import javax.sql.DataSource;

import com.Zdevelopement.data.DataSourceFactory;

@WebListener
public class CRBAContextListener implements ServletContextListener{
	
	@Resource(name="CarRentalBookingDS")
	private DataSource ds;
	
	@Override
	public void contextDestroyed(ServletContextEvent a) {
		
		DataSourceFactory.setDataSource(null);
		
	}

	@Override
	public void contextInitialized(ServletContextEvent context) {
		
		DataSourceFactory.setDataSource(ds);
		
	}
	
}
