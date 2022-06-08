package rebound.util.classhacking.jre;

import static rebound.util.AngryReflectionUtility.*;
import static rebound.util.BasicExceptionUtilities.*;
import java.awt.Point;
import java.awt.image.DataBuffer;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.UnreachableCodeError;


public class ClasshackingSunWritableRaster
{
	protected static final Constructor newSunWritableRaster;
	
	static
	{
		Class c = forName("sun.awt.image.SunWritableRaster");
		
		if (c == null)
			newSunWritableRaster = null;  //Todo find a replacement for this in Java 15!  (or whatever version after 8 it was removed in!)
		else
			newSunWritableRaster = getConstructor(c, new Class[]{SampleModel.class, DataBuffer.class, Point.class});
	}
	
	
	public static boolean has()
	{
		return newSunWritableRaster != null;
	}
	
	public static WritableRaster newSunWritableRaster(SampleModel sampleModel, DataBuffer dataBuffer, Point origin)
	{
		try
		{
			return (WritableRaster) newSunWritableRaster.newInstance(sampleModel, dataBuffer, origin);
		}
		catch (IllegalAccessException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (IllegalArgumentException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (InstantiationException exc)
		{
			throw new ImpossibleException(exc);
		}
		catch (InvocationTargetException exc)
		{
			rethrowSafe(exc);
			throw new UnreachableCodeError();
		}
	}
}
