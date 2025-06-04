package xmod.utils;

import java.io.File;

import java.nio.file.Path;
import java.nio.file.Paths;

 
/** 
* Class of static methods that are useful tools for the project
* @author ELS 
*/
public class Utils
{
	/** Empty constructor, object does not need to be instantiated as all methods in class are static */
	public Utils()
	{
		//empty
	}

    /**
	* Pauses current thread for specified number of milliseconds
	* @param ms integer giving the number of milliseconds to sleep
	*/
	public static void pause(int ms)
	{
		try
		{
			Thread.sleep(ms);
		}
		catch(InterruptedException ex)
		{
			Thread.currentThread().interrupt();
		}
	};
}