package com.anstrat.server.util;

import java.util.Iterator;

public class StringUtils {
	
	public static String join(String delimiter, Iterable<?> list) {
	     StringBuilder builder = new StringBuilder();
	     Iterator<?> iterator = list.iterator();
	     
	     while (iterator.hasNext()) {
	         builder.append(iterator.next());
	         
	         if (!iterator.hasNext()) {
	           break;                  
	         }
	         
	         builder.append(delimiter);
	     }
	     
	     return builder.toString();
	 }
}
