package rebound.exceptions;

/**
 * This exception indicates a syntatical anomaly in the data.
 * A Syntax exception (for the purposes of this java class) is any error in a block of data which can be detected simply by looking at that block, with no external information needed--in other words, stateless.
 * Note that this is not restricted to textual data (although the lineNumber and charPos convenience instantiators prolly wouldn't make much sense for non-text data XD )
 * @author RProgrammer
 */
public class TextSyntaxIOException
extends SyntaxIOException
{
	private static final long serialVersionUID = 1L;
	
	protected TextSyntaxIOException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	
	
	
	public static TextSyntaxIOException inst()
	{
		return new TextSyntaxIOException(null, null);
	}
	
	public static TextSyntaxIOException inst(String message)
	{
		return new TextSyntaxIOException(message, null);
	}
	
	public static TextSyntaxIOException inst(Throwable cause)
	{
		return new TextSyntaxIOException(null, cause);
	}
	
	public static TextSyntaxIOException inst(String message, Throwable cause)
	{
		return new TextSyntaxIOException(message, cause);
	}
	
	
	/**
	 * Instantiates a SyntaxEception which appends <code>"    Character: [$p]"</code> to the message.
	 */
	public static TextSyntaxIOException instCharPos(String message, int pos)
	{
		return new TextSyntaxIOException(message+"    At character: ["+pos+"]", null);
	}
	
	/**
	 * Instantiates a syntax exception which appends <code>"    Line: $l"</code> to the message.
	 */
	public static TextSyntaxIOException instLineNumber(String message, int lineNumber)
	{
		return new TextSyntaxIOException(message+"    At line: "+lineNumber, null);
	}
	
	/**
	 * Instantiates a syntax exception which appends <code>"    At line $l, column $c"</code> to the message.
	 */
	public static TextSyntaxIOException instLineAndColumnNumber(String message, int lineNumber, int columnNumber)
	{
		return new TextSyntaxIOException(message+"    At line "+lineNumber+", column "+columnNumber, null);
	}
	
	
	
	
	
	
	@Override
	public TextSyntaxException toSyntaxRuntimeException()
	{
		return TextSyntaxException.inst(getMessage(), getCause());
	}
}