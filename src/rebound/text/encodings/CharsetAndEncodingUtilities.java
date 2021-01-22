package rebound.text.encodings;

import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.bits.Endianness;

public class CharsetAndEncodingUtilities
{
	/**
	 * Currently only handles disambiguating UTF-16, but this is where you'd add support for other byte-ordering-ambiguous encodings/charsets :3
	 */
	public static Charset forNameWithExplicitByteOrdering(@Nonnull String name, @Nullable Endianness endianness) throws UnsupportedCharsetException
	{
		if (endianness == null)
			return Charset.forName(name);
		else
		{
			Charset c = Charset.forName(name);
			
			if (eq(c, StandardCharsets.UTF_16))
			{
				return endianness == Endianness.Little ? StandardCharsets.UTF_16LE : StandardCharsets.UTF_16BE;
			}
			else
			{
				return c;
			}
		}
	}
}
