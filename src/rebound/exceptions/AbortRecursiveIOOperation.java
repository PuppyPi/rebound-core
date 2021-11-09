package rebound.exceptions;

import java.io.IOException;

public class AbortRecursiveIOOperation
extends IOException
{
	private static final long serialVersionUID = 1l;
	
	public static final AbortRecursiveIOOperation I = new AbortRecursiveIOOperation();  //for performance :3
}
