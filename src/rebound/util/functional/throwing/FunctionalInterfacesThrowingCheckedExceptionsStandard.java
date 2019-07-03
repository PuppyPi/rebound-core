package rebound.util.functional.throwing;

import java.io.IOException;
import rebound.exceptions.BinarySyntaxCheckedException;

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
		public Output f(Input input) throws IOException;
	}
	
	
	
	
	
	
	@FunctionalInterface
	public static interface RunnableThrowingBinarySyntaxCheckedException
	{
		public void run() throws BinarySyntaxCheckedException;
	}
	
	@FunctionalInterface
	public static interface UnaryProcedureThrowingBinarySyntaxCheckedException<Input>
	{
		public void f(Input input) throws BinarySyntaxCheckedException;
	}
	
	@FunctionalInterface
	public static interface NullaryFunctionThrowingBinarySyntaxCheckedException<Output>
	{
		public Output f() throws BinarySyntaxCheckedException;
	}
	
	@FunctionalInterface
	public static interface UnaryFunctionThrowingBinarySyntaxCheckedException<Input, Output>
	{
		public Output f(Input input) throws BinarySyntaxCheckedException;
	}
	
	
	
	
	
	
	public static interface BinaryProcedureThrowingIOException<Input0, Input1>
	{
		public void f(Input0 input0, Input1 input1) throws IOException;
	}
}
