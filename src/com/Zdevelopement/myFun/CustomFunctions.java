package com.Zdevelopement.myFun;

import java.text.SimpleDateFormat;
import java.util.Date;

public class CustomFunctions {

	public CustomFunctions() {}
	
	public static Date ConvertStringToDate(String date)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("MMMM dd, yyyy");
		
		try
		{
			return formatter.parse(date);
		} catch (Exception e) {
			return null;
		}
	}
	
	public static Boolean CheckDate(String date)
	{
		try
		{
			if (date == null)
				return false;
			if (date.equals(""))
				return false;
			Date temp = ConvertStringToDate(date);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

}
