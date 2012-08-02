package com.anstrat.server.old;

import java.net.InetAddress;

/**
 * Represents a connection with an InetAddress and a port to be able to identify one uniquely.
 * @author jay
 *
 */
@Deprecated
public class Connection
{
	public InetAddress ia;
	public int port;
	
	/**
	 * Simple constructor.
	 * @param ia The connection's InetAddress.
	 * @param port The connection's port.
	 */
	public Connection(InetAddress ia, int port)
	{
		this.ia = ia;
		this.port = port;
	}
	
	/**
	 * Your classical equals() implementation.
	 */
	@Override
	public boolean equals(Object o){
		if(o==null || !(o instanceof Connection)){
			return false;
		}
		
		Connection c = (Connection)o;
		return this.ia.equals(c.ia) && this.port == c.port;
	}
	
	/**
	 * Simple hash method.
	 */
	@Override
	public int hashCode(){
		return ia.hashCode() * 13 + port * 23;
	}
	
	/**
	 * Familiar string representation of the connection.
	 */
	@Override
	public String toString()
	{
		return ia.toString()+":"+port;
	}
}