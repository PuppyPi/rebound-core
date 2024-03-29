package rebound.text.encodings.detection;

import static rebound.io.util.TextIOUtilities.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.nio.charset.UnsupportedCharsetException;
import javax.annotation.Nullable;
import rebound.exceptions.ImpossibleException;
import rebound.text.encodings.detection.util.ExhhaustiveTextEncodingDetector;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.NullaryFunctionThrowingIOException;

/**
 * Note that all decoding here should perform the equivalent of {@link CodingErrorAction#REPORT} on decoding errors! (hence the <code>throws {@link CharacterCodingException}</code> X3)
 * Because it shouldn't happen if the encoding was detected correctly!! XD
 */
public interface TextEncodingDetector
{
	/**
	 * @return null if-and-only-if it couldn't be determined
	 * @throws UnsupportedCharsetException if it was detected, but there is no {@link Charset} object registered for it!
	 */
	public default @Nullable Charset detectEncoding(byte[] in, int offset, int length) throws UnsupportedCharsetException
	{
		try
		{
			return detectEncoding(() -> new ByteArrayInputStream(in));
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	/**
	 * @return null if-and-only-if it couldn't be determined
	 * @throws UnsupportedCharsetException if it was detected, but there is no {@link Charset} object registered for it!
	 */
	public default @Nullable Charset detectEncoding(byte[] in) throws UnsupportedCharsetException
	{
		return detectEncoding(in, 0, in.length);
	}
	
	/**
	 * @return null if-and-only-if it couldn't be determined
	 * @throws UnsupportedCharsetException if it was detected, but there is no {@link Charset} object registered for it!
	 */
	public default @Nullable Charset detectEncoding(Slice<byte[]> in) throws UnsupportedCharsetException
	{
		return detectEncoding(in.getUnderlying(), in.getOffset(), in.getLength());
	}
	
	/**
	 * @return null if-and-only-if it couldn't be determined
	 * @throws UnsupportedCharsetException if it was detected, but there is no {@link Charset} object registered for it!
	 */
	public default @Nullable Charset detectEncoding(ByteList in) throws UnsupportedCharsetException
	{
		return detectEncoding(in.toByteArray());
	}
	
	
	
	/**
	 * @return null if-and-only-if it couldn't be determined
	 * @throws UnsupportedCharsetException if it was detected, but there is no {@link Charset} object registered for it!
	 */
	public @Nullable Charset detectEncoding(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException, UnsupportedCharsetException;
	
	
	
	/**
	 * The maximum number of bytes we need 
	 * @return 0 for "no limit", eg, for {@link ExhhaustiveTextEncodingDetector}.
	 */
	public default int headSize()
	{
		//TODO Support this better in implementations XD'''
		return 1024;
	}
	
	
	
	
	
	
	
	
	public default String decodeToMemory(byte[] in, int offset, int length, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		try
		{
			return readAllToString(decodeToStream(in, offset, length, defaultIfUndetectable));
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	public default String decodeToMemory(byte[] in, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(in, 0, in.length, defaultIfUndetectable);
	}
	public default String decodeToMemory(Slice<byte[]> in, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(in.getUnderlying(), in.getOffset(), in.getLength(), defaultIfUndetectable);
	}
	public default String decodeToMemory(ByteList in, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(in.toByteArray(), defaultIfUndetectable);
	}
	
	public default String decodeToMemory(NullaryFunctionThrowingIOException<InputStream> opener, Charset defaultIfUndetectable) throws IOException, CharacterCodingException, UnsupportedCharsetException
	{
		return readAllToString(decodeToStream(opener, defaultIfUndetectable));
	}
	
	
	
	public default Reader decodeToStream(byte[] in, int offset, int length, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		try
		{
			return decodeToStream(() -> new ByteArrayInputStream(in, offset, length), defaultIfUndetectable);
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	public default Reader decodeToStream(byte[] in, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(in, 0, in.length, defaultIfUndetectable);
	}
	public default Reader decodeToStream(Slice<byte[]> in, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(in.getUnderlying(), in.getOffset(), in.getLength(), defaultIfUndetectable);
	}
	public default Reader decodeToStream(ByteList in, Charset defaultIfUndetectable) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(in.toByteArray(), defaultIfUndetectable);
	}
	
	public default Reader decodeToStream(NullaryFunctionThrowingIOException<InputStream> opener, Charset defaultIfUndetectable) throws IOException, CharacterCodingException, UnsupportedCharsetException
	{
		@Nullable Charset detected;
		{
			detected = detectEncoding(opener);
		}
		
		InputStream in = opener.f();
		return new InputStreamReader(in, detected == null ? defaultIfUndetectable : detected);
	}
	
	
	
	
	
	
	
	public default String decodeToMemory(byte[] in, int offset, int length) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(in, offset, length, StandardCharsets.UTF_8);
	}
	public default String decodeToMemory(byte[] in) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(in, 0, in.length);
	}
	public default String decodeToMemory(Slice<byte[]> in) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(in.getUnderlying(), in.getOffset(), in.getLength());
	}
	public default String decodeToMemory(ByteList in) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(in.toByteArray());
	}
	
	public default String decodeToMemory(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException, CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToMemory(opener, StandardCharsets.UTF_8);
	}
	
	
	
	public default Reader decodeToStream(byte[] in, int offset, int length) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(in, offset, length, StandardCharsets.UTF_8);
	}
	public default Reader decodeToStream(byte[] in) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(in, 0, in.length);
	}
	public default Reader decodeToStream(Slice<byte[]> in) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(in.getUnderlying(), in.getOffset(), in.getLength());
	}
	public default Reader decodeToStream(ByteList in) throws CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(in.toByteArray());
	}
	
	public default Reader decodeToStream(NullaryFunctionThrowingIOException<InputStream> opener) throws IOException, CharacterCodingException, UnsupportedCharsetException
	{
		return decodeToStream(opener, StandardCharsets.UTF_8);
	}
}
