package com.gecpp.fm.Util;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Set;


//import com.mchange.v2.c3p0.DataSources;
import com.mchange.v2.c3p0.PooledDataSource;
import com.mchange.v2.c3p0.C3P0Registry;
//import com.mchange.v2.c3p0.ComboPooledDataSource;


/**
 * C3P0連接池在context關閉的時候（關閉tomcat服務器或者reload context），不會回收資源，導致內存泄露。Tomcat會報如下錯誤：
 * <br />SEVERE: The web application [/xxx] appears to have started a thread named [com.mchange.v2.async.ThreadPoolAsynchronousRunner$PoolThread-#1] but has failed to stop it. This is very likely to create a memory leak.
 * 
 * 本類在context關閉的時候釋放c3p0連接池，解決此類存泄露問題。
 * 
 * 另外，JDBC驅動包(例如ojdbc6.jar)放在$TOMCAT_HOME/lib下面，不要放在WEB-INF/lib里，可以解決JDBC驅動導致內存泄露的問題。此時Tomcat報如下錯誤:
 * SEVERE: A web application registered the JBDC driver [oracle.jdbc.OracleDriver] but failed to unregister it when the web application was stopped. To prevent a memory leak, the JDBC Driver has been forcibly unregistered.
 */
@WebListener()
public class DbClose implements ServletContextListener {
 
	@Override
    public void contextInitialized(ServletContextEvent sce) {
    	 System.out.println("Context Init Eleven"+ sce.getServletContext() +" initialized");
    }
 
	@Override
    public void contextDestroyed(ServletContextEvent sce) {
    	
        System.out.println("Context Destroyed by Eleven" + sce.getServletContext().toString());
       
        try
        { 
            System.out.println("\nStart c3P0Registry Clear");
            try {
            	 C3P0Registry.getNumPooledDataSources();
            	 
            	    Iterator<?> it = C3P0Registry.getPooledDataSources().iterator();
            	    while (it.hasNext()) {
            	        try {
            	            PooledDataSource dataSource = (PooledDataSource) it.next();
            	            dataSource.close();
            	        } catch (Exception e) {
            	        	System.out.println("\nError when closing connections ..." + e.getMessage());
            	        }
            	    }

            	    // This manually unregisters JDBC drivers, which prevents Tomcat 7 from
            	    // complaining about memory leaks with this class
            	    Enumeration<Driver> drivers = DriverManager.getDrivers();
            	    while (drivers.hasMoreElements()) {
            	        Driver driver = drivers.nextElement();
            	        try {
            	            DriverManager.deregisterDriver(driver);
            	            System.out.printf("\n" + String.format("Unregistering jdbc driver: %s", driver));
            	        } catch (SQLException e) {
            	        	System.out.printf("\n" + String.format("Error deregistering driver %s", driver),e);
            	        }
            	    }

            	    // Waiting for daemon close() c3p0 jdbc pool thread
            	    Set<Thread> threadSet = Thread.getAllStackTraces().keySet();
            	    for (Thread th : threadSet) {
            	        if (th.isDaemon()) {
            	            try {
            	                if (th.getName().equals("Resource Destroyer in BasicResourcePool.close()")) {
            	                    th.join();
            	                }
            	            } catch (Exception ex) {
            	            	System.out.println("\nShutdown waiting was interrupted ...");
            	            }
            	        }
            	    }
            	    // Clear all thread local variables, this prevents Tomcat 7 from
            	    // complaining about memory leaks
            	    System.out.println("\nFinished c3P0Registry Clear");
            } catch (Exception Err) {
            	
            	
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
          
        //等待連接池關閉線程退出，避免Tomcat報線程未關閉導致memory leak的錯誤
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
 
}