package rebound.exceptions;

/**
 * This exception indicates a syntatical anomaly in the data.
 * A Syntax exception (for the purposes of this java class) is any error in a block of data which can be detected simply by looking at that block, with no external information needed--in other words, stateless.
 * Note that this is not restricted to textual data (although the lineNumber and charPos convenience instantiators prolly wouldn't make much sense for non-text data XD )
 * @author RProgrammer
 */
public class TextSyntaxException
extends SyntaxException
{
	private static final long serialVersionUID = 1L;
	
	protected TextSyntaxException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	
	
	
	public static TextSyntaxException inst()
	{
		return new TextSyntaxException(null, null);
	}
	
	public static TextSyntaxException inst(String message)
	{
		return new TextSyntaxException(message, null);
	}
	
	public static TextSyntaxException inst(Throwable cause)
	{
		return new TextSyntaxException(null, cause);
	}
	
	public static TextSyntaxException inst(String message, Throwable cause)
	{
		return new TextSyntaxException(message, cause);
	}
	
	
	/**
	 * Instantiates a SyntaxEception which appends <code>"    Character: [$p]"</code> to the message.
	 */
	public static TextSyntaxException instCharPos(String message, long pos)
	{
		return new TextSyntaxException(message+"    At character: ["+pos+"]", null);
	}
	
	/**
	 * Instantiates a syntax exception which appends <code>"    Line: $l"</code> to the message.
	 */
	public static TextSyntaxException instLineNumber(String message, long lineNumber)
	{
		return new TextSyntaxException(message+"    At line: "+lineNumber, null);
	}
	
	/**
	 * Instantiates a syntax exception which appends <code>"    At line $l, column $c"</code> to the message.
	 */
	public static TextSyntaxException instLineAndColumnNumber(String message, long lineNumber, long columnNumber)
	{
		return new TextSyntaxException(message+"    At line "+lineNumber+", column "+columnNumber, null);
	}
	
	
	
	public TextSyntaxCheckedException toSyntaxCheckedException()
	{
		return TextSyntaxCheckedException.inst(getMessage(), this);
	}
}