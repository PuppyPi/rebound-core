package rebound.dataformats.xml;

import static java.util.Objects.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.SequenceInputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import java.util.Arrays;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.bits.Endianness;
import rebound.exceptions.ImpossibleException;
import rebound.io.util.JRECompatIOUtilities;
import rebound.text.encodings.CharsetAndEncodingUtilities;
import rebound.text.encodings.UnicodeByteOrderMark;
import rebound.util.collections.Slice;

// https://www.w3.org/TR/REC-xml/

public class XMLEncodingDetection
{
	public static final Charset XMLDefaultCharset = StandardCharsets.UTF_8;
	
	
	
	public static class XMLDeclarationAndExtra
	{
		public byte[] bytes;
		public boolean areBytesAnXMLDeclaration;
		public @Nullable Object knownFromBOMOrXMLWideCharDetection;
		
		public XMLDeclarationAndExtra(byte[] bytes, boolean areBytesAnXMLDeclaration, @Nullable Object knownFromBOMOrXMLWideCharDetection)
		{
			this.bytes = bytes;
			this.areBytesAnXMLDeclaration = areBytesAnXMLDeclaration;
			this.knownFromBOMOrXMLWideCharDetection = knownFromBOMOrXMLWideCharDetection;
		}
		
		public XMLDeclarationAndExtra(byte[] bytes, boolean areBytesAnXMLDeclaration)
		{
			this(bytes, areBytesAnXMLDeclaration, null);
		}
	}
	
	
	
	public static enum XMLWideCharDetection
	{
		LE,
		BE,
		;
		
		@Nullable
		public static Endianness wcdToEndianness(XMLWideCharDetection wcd)
		{
			return wcd == null ? null : (wcd == LE ? Endianness.Little : Endianness.Big);
		}
	}
	
	
	
	
	private static final byte[] XMLDeclarationOpeningAfterSecond = new byte[]{0x78, 0x6D, 0x6C};  //"xml"
	private static final byte[] XMLDeclarationOpeningAfterFirstLE = new byte[]{0x6D, 0, 0x6C, 0};  //"ml"
	private static final byte[] XMLDeclarationOpeningAfterFirstBE = new byte[]{0, 0x6D, 0, 0x6C};  //"ml"
	
	@Nonnull
	public static XMLDeclarationAndExtra readXMLDeclaration(InputStream in) throws IOException
	{
		ByteArrayOutputStream buf = new ByteArrayOutputStream();
		
		int b0 = in.read();
		if (b0 != -1)  buf.write(b0);
		
		while (b0 == 0 || b0 == ' ' || b0 == '\t' || b0 == '\r' || b0 == '\n')
		{
			b0 = in.read();
			if (b0 != -1)  buf.write(b0);
		}
		
		//Byte Order Marks (BOM's)
		//  UTF8: EF BB BF
		//  UTF16LE: FF FE
		//  UTF16BE: FE FF
		
		if (b0 == 0xEF)
		{
			int b1 = in.read();
			if (b1 != -1)  buf.write(b1);
			
			if (b1 == 0xBB)
			{
				int b2 = in.read();
				if (b2 != -1)  buf.write(b2);
				
				if (b2 == 0xBF)
				{
					//UTF-8! :D
					return new XMLDeclarationAndExtra(buf.toByteArray(), true, UnicodeByteOrderMark.UTF8);
				}
				else if (b2 == -1)
				{
					return new XMLDeclarationAndExtra(buf.toByteArray(), false);
				}
				else
				{
					return new XMLDeclarationAndExtra(buf.toByteArray(), false);
				}
			}
			else if (b1 == -1)
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
			else
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
		}
		else if (b0 == 0xFF)
		{
			int b1 = in.read();
			if (b1 != -1)  buf.write(b1);
			
			if (b1 == 0xFE)
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), true, UnicodeByteOrderMark.UTF16LE);
			}
			else if (b1 == -1)
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
			else
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
		}
		else if (b0 == 0xFE)
		{
			int b1 = in.read();
			if (b1 != -1)  buf.write(b1);
			
			if (b1 == 0xFF)
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), true, UnicodeByteOrderMark.UTF16BE);
			}
			else if (b1 == -1)
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
			else
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
		}
		else if (b0 == -1)
		{
			return new XMLDeclarationAndExtra(buf.toByteArray(), false);
		}
		else if (b0 == '<')
		{
			int b1 = in.read();
			if (b1 != -1)  buf.write(b1);
			
			if (b1 == -1)
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
			else if (b0 == '<' && b1 == '?')  //Normal UTF8 / ISO-8859-1 / ASCII declaration type :3
			{
				// "<?xml" but since we read the first and second, it's "xml" X3
				byte[] open = JRECompatIOUtilities.readAsMuchAsPossibleToNew(in, 3);
				buf.write(open);
				
				if (!Arrays.equals(open, XMLDeclarationOpeningAfterSecond))
					return new XMLDeclarationAndExtra(buf.toByteArray(), false);
				
				
				boolean inQuestionMark = false;
				
				while (true)
				{
					int c = in.read();
					if (c != -1)  buf.write(c);
					
					if (c == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false);
					
					else
					{
						if (c == '?')
						{
							inQuestionMark = true;
						}
						else if (c == '>')
						{
							if (inQuestionMark)
								break;
						}
						else
						{
							inQuestionMark = false;
						}
					}
				}
				
				return new XMLDeclarationAndExtra(buf.toByteArray(), true);
			}
			else if (b0 == '<' && b1 == 0)  //UTF16 declaration type :3
			{
				/*
				 * If it's LE:   ' '  0      '<'  0      '?'  0
				 * If it's BE:    0  ' '      0  '<'      0  '?'
				 */
				boolean little = (buf.size() % 2) == 0;
				
				
				// Read the '?' in "<?"    :>
				if (little)
				{
					int a = in.read();
					if (a == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
					else
						buf.write(a);
					
					
					int b = in.read();
					if (b == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
					else
						buf.write(b);
					
					
					int c = a | (b << 8);
					
					if (c != '?')
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
				}
				else
				{
					int a = in.read();
					if (a == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
					else
						buf.write(a);
					
					if (a != '?')
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
				}
				
				
				
				
				//Skip whitespace :>
				while (true)
				{
					int a = in.read();
					if (a == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
					else
						buf.write(a);
					
					
					int b = in.read();
					if (b == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
					else
						buf.write(b);
					
					
					int c;
					{
						if (little)
							c = a | (b << 8);
						else
							c = b | (a << 8);
					}
					
					if (c == 'x')
						break;
					else if (c == ' ' || c == 0 || c == '\t' || c == '\n' || c == '\r')
						continue;
					else
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
				}
				
				
				
				
				// "<?xml" but since we read the first bits, it's "ml" X3
				
				byte[] x = little ? XMLDeclarationOpeningAfterFirstLE : XMLDeclarationOpeningAfterFirstBE;
				
				byte[] open = JRECompatIOUtilities.readAsMuchAsPossibleToNew(in, x.length);
				buf.write(open);
				
				if (!Arrays.equals(open, x))
					return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
				
				
				
				boolean inQuestionMark = false;
				
				while (true)
				{
					int a = in.read();
					if (a == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
					else
						buf.write(a);
					
					
					int b = in.read();
					if (b == -1)
						return new XMLDeclarationAndExtra(buf.toByteArray(), false, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
					else
						buf.write(b);
					
					
					int c;
					{
						if (little)
							c = a | (b << 8);
						else
							c = b | (a << 8);
					}
					
					
					
					//else
					{
						if (c == '?')
						{
							inQuestionMark = true;
						}
						else if (c == '>')
						{
							if (inQuestionMark)
								break;
						}
						else
						{
							inQuestionMark = false;
						}
					}
				}
				
				return new XMLDeclarationAndExtra(buf.toByteArray(), true, little ? XMLWideCharDetection.LE : XMLWideCharDetection.BE);
			}
			//TODO UCS4!
			else
			{
				return new XMLDeclarationAndExtra(buf.toByteArray(), false);
			}
		}
		else
		{
			return new XMLDeclarationAndExtra(buf.toByteArray(), false);
		}
	}
	
	
	@Nonnull
	public static XMLDeclarationAndExtra readXMLDeclarationFromMemory(byte[] b)
	{
		return readXMLDeclarationFromMemory(b, 0, b.length);
	}
	
	@Nonnull
	public static XMLDeclarationAndExtra readXMLDeclarationFromMemory(Slice<byte[]> b)
	{
		return readXMLDeclarationFromMemory(b.getUnderlying(), b.getOffset(), b.getLength());
	}
	
	@Nonnull
	public static XMLDeclarationAndExtra readXMLDeclarationFromMemory(byte[] b, int offset, int length)
	{
		try
		{
			return readXMLDeclaration(new ByteArrayInputStream(b, offset, length));
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * encoding will either be a {@link UnicodeByteOrderMark}, a {@link String}, or null  :D
	 * (we leave it as a {@link String} if explicitly given so you can handle errors from missing encodings ^^ )
	 * 
	 * Note that the string may be "UTF-8" or etc. XD
	 */
	public static class XMLEncodingAndPushedbackStream
	{
		public @Nullable Object encoding;
		public @Nullable XMLWideCharDetection wcd;
		public @Nonnull InputStream stream;
		
		public XMLEncodingAndPushedbackStream(@Nullable Object encoding, @Nullable XMLWideCharDetection wcd, @Nonnull InputStream stream)
		{
			this.encoding = encoding;
			this.wcd = wcd;
			this.stream = requireNonNull(stream);
		}
		
		public @Nonnull Charset encodingToCharset()
		{
			return encodingDeclarationTypeToSemanticEncoding(encoding, wcd);
		}
		
		public @Nullable Charset encodingToCharsetOrNull()
		{
			return encodingDeclarationTypeToSemanticEncodingOrNull(encoding, wcd);
		}
	}
	
	
	public static XMLEncodingAndPushedbackStream readAndUnreadXMLDeclaration(InputStream in) throws IOException, UnsupportedCharsetException
	{
		XMLDeclarationAndExtra r = readXMLDeclaration(in);
		
		XMLWideCharDetection wcd = r.knownFromBOMOrXMLWideCharDetection instanceof XMLWideCharDetection ? (XMLWideCharDetection)r.knownFromBOMOrXMLWideCharDetection : null;
		
		Object encoding;
		{
			if (r.knownFromBOMOrXMLWideCharDetection instanceof UnicodeByteOrderMark)
			{
				encoding = r.knownFromBOMOrXMLWideCharDetection;
			}
			else if (r.areBytesAnXMLDeclaration)
			{
				encoding = getEncodingNameFromXMLDeclaration(r.bytes, wcd);
			}
			else
			{
				encoding = null;
			}
		}
		
		return new XMLEncodingAndPushedbackStream(encoding, wcd, new SequenceInputStream(new ByteArrayInputStream(r.bytes), in));
	}
	
	
	
	
	
	
	
	
	
	
	
	public static @Nonnull Charset readXMLEncoding(InputStream in) throws IOException, UnsupportedCharsetException
	{
		Charset c = readDeclaredXMLEncodingOrNull(in);
		return c == null ? XMLDefaultCharset : c;
	}
	
	public static @Nullable Charset readDeclaredXMLEncodingOrNull(InputStream in) throws IOException, UnsupportedCharsetException
	{
		return toXMLDeclarationForEncodingOrNull(readXMLDeclaration(in));
	}
	
	
	
	public static @Nonnull Charset getXMLEncoding(byte[] data) throws UnsupportedCharsetException
	{
		Charset c = getDeclaredXMLEncodingOrNull(data);
		return c == null ? XMLDefaultCharset : c;
	}
	
	public static @Nullable Charset getDeclaredXMLEncodingOrNull(byte[] data) throws UnsupportedCharsetException
	{
		return toXMLDeclarationForEncodingOrNull(readXMLDeclarationFromMemory(data));
	}
	
	
	
	public static @Nonnull Charset getXMLEncoding(Slice<byte[]> data) throws UnsupportedCharsetException
	{
		Charset c = getDeclaredXMLEncodingOrNull(data);
		return c == null ? XMLDefaultCharset : c;
	}
	
	public static @Nullable Charset getDeclaredXMLEncodingOrNull(Slice<byte[]> data) throws UnsupportedCharsetException
	{
		return toXMLDeclarationForEncodingOrNull(readXMLDeclarationFromMemory(data));
	}
	
	
	
	public static @Nonnull Charset getXMLEncoding(byte[] data, int offset, int length) throws UnsupportedCharsetException
	{
		Charset c = getDeclaredXMLEncodingOrNull(data, offset, length);
		return c == null ? XMLDefaultCharset : c;
	}
	
	public static @Nullable Charset getDeclaredXMLEncodingOrNull(byte[] data, int offset, int length) throws UnsupportedCharsetException
	{
		return toXMLDeclarationForEncodingOrNull(readXMLDeclarationFromMemory(data, offset, length));
	}
	
	
	
	
	
	
	
	
	public static @Nullable Charset toXMLDeclarationForEncodingOrNull(XMLDeclarationAndExtra r) throws UnsupportedCharsetException
	{
		XMLWideCharDetection wcd = r.knownFromBOMOrXMLWideCharDetection instanceof XMLWideCharDetection ? (XMLWideCharDetection)r.knownFromBOMOrXMLWideCharDetection : null;
		
		Object encoding;
		{
			if (r.knownFromBOMOrXMLWideCharDetection instanceof UnicodeByteOrderMark)
			{
				encoding = r.knownFromBOMOrXMLWideCharDetection;
			}
			else if (r.areBytesAnXMLDeclaration)
			{
				encoding = getEncodingNameFromXMLDeclaration(r.bytes, wcd);
			}
			else
			{
				encoding = null;
			}
		}
		
		return encodingDeclarationTypeToSemanticEncoding(encoding, wcd);
	}
	
	
	
	
	public static @Nonnull Charset encodingDeclarationTypeToSemanticEncoding(@Nullable Object encoding, @Nullable XMLWideCharDetection wcd) throws UnsupportedCharsetException
	{
		Charset c = encodingDeclarationTypeToSemanticEncodingOrNull(encoding, wcd);
		return c == null ? XMLDefaultCharset : c;
	}
	
	public static @Nullable Charset encodingDeclarationTypeToSemanticEncodingOrNull(@Nullable Object encoding, @Nullable XMLWideCharDetection wcd) throws UnsupportedCharsetException
	{
		if (encoding == null)
			return null;
		else if (encoding instanceof UnicodeByteOrderMark)
			return ((UnicodeByteOrderMark)encoding).getEncoding();
		else
			return CharsetAndEncodingUtilities.forNameWithExplicitByteOrdering((String)encoding, XMLWideCharDetection.wcdToEndianness(wcd));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Charset getNominalCharsetForParsingXMLDeclaration(@Nullable XMLWideCharDetection wcd)
	{
		return wcd == null ? StandardCharsets.UTF_8 : (wcd == XMLWideCharDetection.LE ? StandardCharsets.UTF_16LE : StandardCharsets.UTF_16BE);
	}
	
	
	@Nullable
	public static String getEncodingNameFromXMLDeclaration(byte[] declaration, @Nullable XMLWideCharDetection wcd)
	{
		String decl = new String(declaration, getNominalCharsetForParsingXMLDeclaration(wcd));
		
		/*
		 * From https://www.w3.org/TR/REC-xml/  (2018-12-28 19 z)
		 * 
			[23]   	XMLDecl	   ::=   	'<?xml' VersionInfo EncodingDecl? SDDecl? S? '?>'
			[24]   	VersionInfo	   ::=   	S 'version' Eq ("'" VersionNum "'" | '"' VersionNum '"')
			[25]   	Eq	   ::=   	S? '=' S?
			[26]   	VersionNum	   ::=   	'1.' [0-9]+
			[32]   	SDDecl	   ::=   	S 'standalone' Eq (("'" ('yes' | 'no') "'") | ('"' ('yes' | 'no') '"'))
		 * 
		 * 
		 * Meaning the phrase "encoding" can't appear anywhere except where we expect it, or possibly inside the encoding itself XD   Which if it did, there would be the attribute-ish key "encoding" that would come first! XD
		 */
		
		String t = "encoding";
		int i = decl.toLowerCase().indexOf(t);
		
		if (i == -1)
		{
			return null;
		}
		else
		{
			i += t.length();
			
			int length = decl.length();
			
			
			//between "encoding" and "="
			while (i < length && Character.isWhitespace(decl.charAt(i)))
				i++;
			
			
			if (i >= length || decl.charAt(i) != '=')
				return null;
			
			i++;  //skip the '=' :>
			
			//between "=" and "\"" or "'"
			while (i < length && Character.isWhitespace(decl.charAt(i)))
				i++;
			
			if (i >= length)
				return null;
			else
			{
				char q = decl.charAt(i);
				
				if (q == '"' || q == '\'')
				{
					i++;  //skip the '"' or '\'' :>
					
					int start = i;
					
					while (i < length && decl.charAt(i) != q)
						i++;
					
					if (i >= length)
						return null;
					else
					{
						assert decl.charAt(i) == q;
						
						int end = i;
						
						return decl.substring(start, end);
					}
				}
				else
				{
					return null;
				}
			}
		}
	}
}
