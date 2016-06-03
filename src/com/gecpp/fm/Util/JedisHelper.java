package com.gecpp.fm.Util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import redis.clients.jedis.Jedis;

public class JedisHelper {
	
	static final boolean DEBUG_BUILD = false;
	
	// 讀取redis
		private static String RedisUrl;
		
		public static String loadRedisServer() {
			
			loadParams();
			
			return RedisUrl;
		}
		
		private static void loadParams() {
			
			Context envurl;
			String entry;

			try {
				
				if(DEBUG_BUILD == true)
				{
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.redis.debug");
					RedisUrl = entry;
				}
				else
				{
					
					envurl = (Context) new InitialContext().lookup("java:comp/env");
					entry = (String) envurl.lookup("om.param.redis");
					RedisUrl = entry;
				}
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
}
