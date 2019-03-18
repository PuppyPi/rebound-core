package rebound.exceptions;

/**
 * + Note, a {@link ClassCastException} may always be thrown instead of this!  You should catch them together and consider declarations of 'throws {@link GenericDatastructuresFormatException}' to be 'throws {@link GenericDatastructuresFormatException}, {@link ClassCastException}'
 * 
 * Eg, you've parsed the JSON or XML successfully and it's valid! :D
 * ..except it's not XD
 * 
 * The actual parsed *generic* datastructures are still capable of exist in more ways than they are supposed to be for your *usage* of them! ^_~
 * 
 * (the generalization of "parsing errors" and "generic DS format errors" among other things are possible when input data to a function supports more states than are considered valid)
 * (OH—WHEN THE "PARSER" IS NOT REVERSE-SURJECTIVE!! 8D )
 * 
 * @author RProgrammer
 */
public class GenericDatastructuresFormatException
extends StructureFormatException
{
	private static final long serialVersionUID = 1L;
	
	public GenericDatastructuresFormatException()
	{
		super();
	}
	
	public GenericDatastructuresFormatException(String message)
	{
		super(message);
	}
	
	public GenericDatastructuresFormatException(Throwable cause)
	{
		super(cause);
	}
	
	public GenericDatastructuresFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
