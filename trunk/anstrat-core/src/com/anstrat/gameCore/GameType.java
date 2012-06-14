package com.anstrat.gameCore;

/**
 * Helper class for game type flags.
 * @author jay
 *
 */
public abstract class GameType {
	
	public static final int TYPE_RANDOM = 			0x00000001;
	public static final int TYPE_DEFAULT = 			0x00000002;
	public static final int TYPE_DEFAULT_RANDOM = 	0x00000004;
	public static final int TYPE_CUSTOM = 			0x00000008;
	public static final int TYPE_ALL = TYPE_RANDOM | TYPE_DEFAULT | TYPE_DEFAULT_RANDOM | TYPE_CUSTOM;
	
	public static boolean validFlags(int flags)
	{
		return (flags & TYPE_ALL) > 0;
	}
	
	public static boolean accept_random(int flags)
	{
		return (flags & TYPE_RANDOM) > 0;
	}
	
	public static boolean accept_default(int flags)
	{
		return (flags & TYPE_DEFAULT) > 0;
	}
	
	public static boolean accept_default_random(int flags)
	{
		return (flags & TYPE_DEFAULT_RANDOM) > 0;
	}
	
	public static boolean accept_custom(int flags)
	{
		return (flags & TYPE_CUSTOM) > 0;
	}
}