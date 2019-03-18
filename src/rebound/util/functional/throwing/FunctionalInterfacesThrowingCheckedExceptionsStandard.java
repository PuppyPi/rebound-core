package rebound.util.functional.throwing;

import java.io.IOException;

public class FunctionalInterfacesThrowingCheckedExceptionsStandard
{
	@FunctionalInterface
	public static interface RunnableThrowingAnything
	{
		public void run() throws Throwable;
	}
	
	@FunctionalInterface
	public static interface UnaryProcedureThrowingAnything<Input>
	{
		public void f(Input input) throws Throwable;
	}
	
	@FunctionalInterface
	public static interface NullaryFunctionThrowingAnything<Output>
	{
		public Output f() throws Throwable;
	}
	
	@FunctionalInterface
	public static interface UnaryFunctionThrowingAnything<Input, Output>
	{
		public Output f(Input input) throws Throwable;
	}
	
	
	
	
	
	
	@FunctionalInterface
	public static interface RunnableThrowingIOException
	{
		public void run() throws IOException;
	}
	
	@FunctionalInterface
	public static interface UnaryProcedureThrowingIOException<Input>
	{
		public void f(Input input) throws IOException;
	}
	
	@FunctionalInterface
	public static interface NullaryFunctionThrowingIOException<Output>
	{
		public Output f() throws IOException;
	}
	
	@FunctionalInterface
	public static interface UnaryFunctionThrowingIOException<Input, Output>
	{
		public Output run(Input input) throws IOException;
	}
}
