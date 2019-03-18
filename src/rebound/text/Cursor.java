/*
 * Created on Jun 9, 2008
 * 	by the great Eclipse(c)
 */
package rebound.text;

/**
 * A proxy which can be used by external parsing utilities to give both information about the data parsed, and the lexical size it took (eg for a number parser which doesn't know ahead of time how many digits there will be).<br>
 * @author RProgrammer
 */
public interface Cursor
{
	public int getCursor();
	
	public void setCursor(int cursor);
}
