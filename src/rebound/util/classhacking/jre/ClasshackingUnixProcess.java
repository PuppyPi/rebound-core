package rebound.util.classhacking.jre;

import java.lang.reflect.Field;
import rebound.exceptions.ImpossibleException;
import rebound.util.classhacking.ClasshackingUtilities;
import rebound.util.classhacking.HackedClassOrMemberUnavailableException;

public class ClasshackingUnixProcess
{
	protected Class class_UNIXProcess;
	
	protected Field field_UNIXProcess_pid;
	
	
	
	public ClasshackingUnixProcess() throws HackedClassOrMemberUnavailableException
	{
		class_UNIXProcess = ClasshackingUtilities.classhackingForName("java.lang.UNIXProcess");
		
		try
		{
			field_UNIXProcess_pid = class_UNIXProcess.getDeclaredField("pid");
			field_UNIXProcess_pid.setAccessible(true);
		}
		catch (NoSuchFieldException exc)
		{
			throw new HackedClassOrMemberUnavailableException(exc);
		}
	}
	
	
	
	public int getPID(Process p) throws SecurityException, IllegalArgumentException
	{
		try
		{
			return (Integer)field_UNIXProcess_pid.get(p);
		}
		catch (IllegalArgumentException exc)
		{
			throw new HackedClassOrMemberUnavailableException(exc);
		}
		catch (IllegalAccessException exc)
		{
			//SecurityException? yus, IllegalAccessException? hmmm..
			throw new ImpossibleException(exc);
		}
	}
}
