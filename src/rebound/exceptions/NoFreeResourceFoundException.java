/*
 * Created on Jan 27, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.exceptions;

import java.io.File;

/**
 * When checking for a free/available resource (eg, {@link File#createTempFile(String, String) checking for a free file}, or a free port, etc.),
 * if none is found, throw this.
 * @author RProgrammer
 */
public class NoFreeResourceFoundException
extends AttemptsExhaustedException
{
	private static final long serialVersionUID = 1L;
}
