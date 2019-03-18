package rebound.exceptions;

import rebound.text.parsing.apis.tokenstream.SimpleTokenWithLineAndColumnNumbers;
import rebound.text.parsing.apis.tokenstream.WherefulToken;

/**
 * This exception indicates a syntatical anomaly in the data.
 * A Syntax exception (for the purposes of this java class) is any error in a block of data which can be detected simply by looking at that block, with no external information needed--in other words, stateless.
 * Note that this is not restricted to textual data (although the lineNumber and charPos convenience instantiators prolly wouldn't make much sense for non-text data XD )
 * @author RProgrammer
 */
public class TextSyntaxCheckedException
extends SyntaxCheckedException
{
	private static final long serialVersionUID = 1L;
	
	protected TextSyntaxCheckedException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	
	
	
	
	public static TextSyntaxCheckedException inst()
	{
		return new TextSyntaxCheckedException(null, null);
	}
	
	public static TextSyntaxCheckedException inst(String message)
	{
		return new TextSyntaxCheckedException(message, null);
	}
	
	public static TextSyntaxCheckedException inst(Throwable cause)
	{
		return new TextSyntaxCheckedException(null, cause);
	}
	
	public static TextSyntaxCheckedException inst(String message, Throwable cause)
	{
		return new TextSyntaxCheckedException(message, cause);
	}
	
	
	public static TextSyntaxCheckedException instCharPos(String message, int characterIndex, Throwable cause)
	{
		if (characterIndex == -1)
			return inst(message);
		else
			return new TextSyntaxCheckedException(message+"    At character: ["+characterIndex+"]", cause);
	}
	
	public static TextSyntaxCheckedException instLineNumber(String message, int lineNumber, Throwable cause)
	{
		if (lineNumber == -1)
			return inst(message);
		else
			return new TextSyntaxCheckedException(message+"    At line: "+lineNumber, cause);
	}
	
	public static TextSyntaxCheckedException instLineAndColumnNumber(String message, int lineNumber, int columnNumber, Throwable cause)
	{
		if (columnNumber == -1)
			return instLineNumber(message, lineNumber, cause);
		else
			return new TextSyntaxCheckedException(message+"    At line "+lineNumber+", column "+columnNumber, cause);
	}
	
	public static TextSyntaxCheckedException instCharPosAndLineAndColumnNumber(String message, int characterIndex, int lineNumber, int columnNumber, Throwable cause)
	{
		if (message == null)
			message = "Syntax Error!";
		
		if (characterIndex == -1)
			return instLineAndColumnNumber(message, lineNumber, columnNumber, cause);
		else if (lineNumber == -1)
			return instCharPos(message, characterIndex, cause);
		else if (columnNumber == -1)  //&& the other two are given!
			return new TextSyntaxCheckedException(message+"    At character ["+characterIndex+"], (line "+lineNumber+")", cause);
		else
			return new TextSyntaxCheckedException(message+"    At character ["+characterIndex+"], (line "+lineNumber+", column "+columnNumber+")", cause);
	}
	
	
	
	
	
	public static TextSyntaxCheckedException instCharPos(String message, int characterIndex)
	{
		return instCharPos(message, characterIndex, null);
	}
	
	public static TextSyntaxCheckedException instLineNumber(String message, int lineNumber)
	{
		return instLineNumber(message, lineNumber, null);
	}
	
	public static TextSyntaxCheckedException instLineAndColumnNumber(String message, int lineNumber, int columnNumber)
	{
		return instLineAndColumnNumber(message, lineNumber, columnNumber, null);
	}
	
	public static TextSyntaxCheckedException instCharPosAndLineAndColumnNumber(String message, int characterIndex, int lineNumber, int columnNumber)
	{
		return instCharPosAndLineAndColumnNumber(message, characterIndex, lineNumber, columnNumber, null);
	}
	
	
	
	
	
	//Nooooot sure if these should be here or elsewhere but whatever, I can move 'em later ^^''
	public static TextSyntaxCheckedException instPos(String message, WherefulToken token, Throwable cause)
	{
		return instCharPosAndLineAndColumnNumber(message, token.getStartingCharacterIndexInSource(), SimpleTokenWithLineAndColumnNumbers.getStartingLineNumberInSource1Based(token), SimpleTokenWithLineAndColumnNumbers.getStartingColumnNumberInSource1Based(token), cause);
	}
	
	public static TextSyntaxCheckedException instPos(String message, WherefulToken token)
	{
		return instPos(message, token, null);
	}
	
	public static TextSyntaxCheckedException instPos(WherefulToken token)
	{
		return instPos(null, token, null);
	}
	
	
	
	
	
	@Override
	public TextSyntaxException toSyntaxRuntimeException()
	{
		return TextSyntaxException.inst(getMessage(), this);
	}
}