package rebound.exceptions;

/**
 * + Note, a {@link ClassCastException} may always be thrown instead of this!  You should catch them together and consider declarations of 'throws {@link GenericDataStructuresFormatException}' to be 'throws {@link GenericDataStructuresFormatException}, {@link ClassCastException}'
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
public class GenericDataStructuresFormatException
extends StructureFormatException
{
	private static final long serialVersionUID = 1L;
	
	public GenericDataStructuresFormatException()
	{
		super();
	}
	
	public GenericDataStructuresFormatException(String message)
	{
		super(message);
	}
	
	public GenericDataStructuresFormatException(Throwable cause)
	{
		super(cause);
	}
	
	public GenericDataStructuresFormatException(String message, Throwable cause)
	{
		super(message, cause);
	}
}
