package rebound.util.classhacking.jre;

import static rebound.util.BasicExceptionUtilities.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.nio.file.FileStore;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.util.classhacking.ClasshackingUtilities;
import rebound.util.classhacking.HackedClassOrMemberUnavailableException;

public class ClasshackingSunUnixFileStore
{
	protected Class class_UnixFileStore;
	protected Class class_UnixMountEntry;
	
	protected Method method_UnixFileStore_entry;  //packagedefault UnixMountEntry UnixFileStore.entry()
	protected Method method_UnixMountEntry_dir;   //packagedefault byte[] UnixMountEntry.dir()
	
	
	public ClasshackingSunUnixFileStore() throws HackedClassOrMemberUnavailableException
	{
		class_UnixFileStore = ClasshackingUtilities.classhackingForName("sun.nio.fs.UnixFileStore");
		class_UnixMountEntry = ClasshackingUtilities.classhackingForName("sun.nio.fs.UnixMountEntry");
		
		try
		{
			method_UnixFileStore_entry = class_UnixFileStore.getDeclaredMethod("entry");
		}
		catch (NoSuchMethodException exc)
		{
			throw new HackedClassOrMemberUnavailableException(exc);
		}
		
		try
		{
			method_UnixMountEntry_dir = class_UnixMountEntry.getDeclaredMethod("dir");
		}
		catch (NoSuchMethodException exc)
		{
			throw new HackedClassOrMemberUnavailableException(exc);
		}
		
		method_UnixFileStore_entry.setAccessible(true);
		method_UnixMountEntry_dir.setAccessible(true);
	}
	
	
	
	
	public boolean isSupportedFileStore(FileStore fileStore)
	{
		return class_UnixFileStore.isInstance(fileStore);
	}
	
	public String getFileStoreMountPoint(FileStore fileStore)
	{
		if (isSupportedFileStore(fileStore))
		{
			Object mountEntry;
			try
			{
				mountEntry = method_UnixFileStore_entry.invoke(fileStore);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exc)
			{
				throw new WrappedThrowableRuntimeException(exc);
			}
			
			byte[] dir;
			try
			{
				dir = (byte[]) method_UnixMountEntry_dir.invoke(mountEntry);
			}
			catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException exc)
			{
				throw new WrappedThrowableRuntimeException(exc);
			}
			
			return new String(dir, Charset.defaultCharset());
		}
		else
		{
			throw newClassCastExceptionOrNullPointerException(fileStore, class_UnixFileStore);
		}
	}
}
