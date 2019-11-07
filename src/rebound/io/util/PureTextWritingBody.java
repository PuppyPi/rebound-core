package rebound.io.util;

import java.io.IOException;
import java.io.Writer;

public interface PureTextWritingBody
{
	public void run(Writer out, String encodingName) throws IOException;
}