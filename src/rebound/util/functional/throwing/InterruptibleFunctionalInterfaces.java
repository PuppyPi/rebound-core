/*
 * Created on Jul 16, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.functional.throwing;

import rebound.util.objectutil.JavaNamespace;

public class InterruptibleFunctionalInterfaces
implements JavaNamespace
{
	@FunctionalInterface
	public static interface InterruptibleRunnable
	{
		public void run() throws InterruptedException;
	}
	
	
	@FunctionalInterface
	public static interface InterruptibleUnaryProcedure<Input>
	{
		public void f(Input input) throws InterruptedException;
	}
	
	@FunctionalInterface
	public static interface InterruptibleUnaryProcedureLong
	{
		public void f(long input) throws InterruptedException;
	}
	
	
	
	
	
	@FunctionalInterface
	public static interface InterruptibleNullaryFunction<Output>
	{
		public Output f() throws InterruptedException;
	}
	
	@FunctionalInterface
	public static interface InterruptibleNullaryFunctionToBoolean
	{
		public boolean f() throws InterruptedException;
	}
	
	
	@FunctionalInterface
	public static interface InterruptibleUnaryFunction<Input, Output>
	{
		public Output f(Input input) throws InterruptedException;
	}
	
	@FunctionalInterface
	public static interface InterruptibleUnaryFunctionLongToBoolean
	{
		public boolean f(long input) throws InterruptedException;
	}
}
