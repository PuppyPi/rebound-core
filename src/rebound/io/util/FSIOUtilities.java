package rebound.io.util;

import static rebound.file.FSUtilities.*;
import static rebound.io.util.BasicIOUtilities.*;
import static rebound.io.util.JRECompatIOUtilities.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.ArrayUtilities.*;
import static rebound.util.collections.prim.PrimitiveCollections.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.allowedoperations.FixedLengthValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.exceptions.OverflowException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.file.FSUtilities;
import rebound.io.ChannelProvider;
import rebound.math.SmallIntegerMathUtilities;
import rebound.text.encodings.detection.TextEncodingDetector;
import rebound.util.BufferAllocationType;
import rebound.util.PlatformNIOBufferUtilities;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.RandomAccessFileBackedByteList;
import rebound.util.functional.FunctionInterfaces.UnaryProcedure;

public class FSIOUtilities
{
	/**
	 * @return the number of bytes transferred
	 */
	public static long dumpFileToOutputStream(File f, OutputStream out) throws IOException
	{
		long total = 0;
		FileInputStream in = new FileInputStream(f);
		
		try
		{
			total = JRECompatIOUtilities.pump(in, out);
		}
		catch (IOException exc)
		{
			try
			{
				in.close();
			}
			catch (Exception exc1)
			{
			}
			
			throw exc;
		}
		
		in.close();
		
		return total;
	}
	//Pump>
	
	
	//	public static long scan(File f, byte[] tag) throws IOException, FileNotFoundException
	//	{
	//		return scan(f, tag, 0, tag.length);
	//	}
	//
	//	public static long scan(File f, byte[] tag, int tagOffset, int tagLength) throws IOException, FileNotFoundException
	//	{
	//		InputStream in = new FileInputStream(f);
	//		long rv = 0;
	//		try
	//		{
	//			rv = JRECompatIOUtilities.scan(in, tag, tagOffset, tagLength);
	//		}
	//		catch (IOException exc)
	//		{
	//			closeWithoutError(in);
	//			throw exc;
	//		}
	//
	//		//else
	//		in.close();
	//		return rv;
	//	}
	
	@Nonnull
	public static byte[] readAll(File file) throws IOException
	{
		InputStream in = new FileInputStream(file);
		
		try
		{
			return JRECompatIOUtilities.readAll(in);
		}
		finally
		{
			closeWithoutError(in);
		}
	}
	
	@Nonnull
	public static byte[] readAll(URL url) throws IOException
	{
		InputStream in = url.openStream();
		
		try
		{
			return JRECompatIOUtilities.readAll(in);
		}
		finally
		{
			closeWithoutError(in);
		}
	}
	
	@Nonnull
	public static ByteBuffer readAll(File file, BufferAllocationType allocationType) throws IOException
	{
		long length = file.length();
		
		if (length > Integer.MAX_VALUE)
			throw new OverflowException();
		
		ByteBuffer buffer = PlatformNIOBufferUtilities.allocateByteBuffer((int)length, allocationType);
		
		FileChannel channel = FileChannel.open(file.toPath(), StandardOpenOption.READ);
		
		try
		{
			while (buffer.hasRemaining())
				channel.write(buffer);
		}
		catch (IOException exc)
		{
			closeWithoutError(channel);
			throw exc;
		}
		
		//else
		channel.close();
		
		return buffer;
	}
	
	
	
	@Nonnull
	@ThrowAwayValue
	public static byte[] readSome(File file, long start, int length) throws IOException
	{
		try (InputStream in = new FileInputStream(file))
		{
			if (start > 0)
				skipFully(in, start);
			
			return JRECompatIOUtilities.readFullyToNew(in, length);
		}
	}
	
	@Nonnull
	@SnapshotValue
	@WritableValue
	@FixedLengthValue
	public static ByteList readSomeToList(File file, long start, int length) throws IOException
	{
		return byteArrayAsList(readSome(file, start, length));
	}
	
	
	
	@Nonnull
	@ThrowAwayValue
	public static byte[] readSome(File file, int length) throws IOException
	{
		return readSome(file, 0, length);
	}
	
	@Nonnull
	@SnapshotValue
	@WritableValue
	@FixedLengthValue
	public static ByteList readSomeToList(File file, int length) throws IOException
	{
		return readSomeToList(file, 0, length);
	}
	
	
	
	
	
	
	@Nonnull
	@SnapshotValue
	@WritableValue
	@FixedLengthValue
	public static ByteList readAllToList(File file) throws IOException
	{
		return byteArrayAsList(readAll(file));
	}
	
	@Nonnull
	@LiveValue
	@WritableValue
	public static ByteList fileAsList(File file) throws IOException, UncheckedIOException
	{
		return new RandomAccessFileBackedByteList(new RandomAccessFile(file, "rw"), true);
	}
	
	
	
	
	
	@Nonnull
	public static String readAllText(File file, String encoding) throws IOException
	{
		return readAllText(file, Charset.forName(encoding));
	}
	
	@Nonnull
	public static String readAllText(File file, Charset encoding) throws IOException
	{
		if (encoding == null)
			encoding = Charset.defaultCharset();
		
		InputStream in = new FileInputStream(file);
		
		try
		{
			return TextIOUtilities.readAllText(in, encoding);
		}
		finally
		{
			closeWithoutError(in);
		}
	}
	
	@Nonnull
	public static String readAllText(File file, TextEncodingDetector encodingDetector) throws IOException
	{
		try (InputStream in = new FileInputStream(file))
		{
			return TextIOUtilities.readAllText(in, encodingDetector);
		}
	}
	
	@Nonnull
	public static String readAllText(File file) throws IOException
	{
		try (InputStream in = new FileInputStream(file))
		{
			return TextIOUtilities.readAllText(in);
		}
	}
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, byte[] data) throws IOException
	{
		writeAll(file, data, false);
	}
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, byte[] data, boolean append) throws IOException
	{
		writeAll(file, wholeArraySliceByte(data), append);
	}
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, Slice<byte[]> data) throws IOException
	{
		writeAll(file, data, false);
	}
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, Slice<byte[]> data, boolean append) throws IOException
	{
		try (OutputStream out = new FileOutputStream(file, append))
		{
			out.write(data.getUnderlying(), data.getOffset(), data.getLength());
		}
	}
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, ByteBuffer data, boolean append) throws IOException
	{
		try (FileChannel channel = !append ? FileChannel.open(file.toPath(), StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING) : FileChannel.open(file.toPath(), StandardOpenOption.APPEND, StandardOpenOption.CREATE))
		{
			while (data.hasRemaining())
				channel.write(data);
		}
	}
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, ByteBuffer data) throws IOException
	{
		writeAll(file, data, true);
	}
	
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, ByteList data) throws IOException
	{
		writeAll(file, data, false);
	}
	
	/**
	 * Guaranteed to work even if the file is a symlink!  (Ie, it won't delete and recreate it and break the symlinkness! It'll put data directly into the file)
	 */
	public static void writeAll(File file, ByteList data, boolean append) throws IOException
	{
		writeAll(file, data.toByteArraySlicePossiblyLive(), append);
	}
	
	
	
	
	
	
	public static void writeAllText(File file, String data) throws IOException
	{
		writeAllText(file, data, false);
	}
	
	public static void writeAllText(File file, String data, String encoding) throws IOException
	{
		writeAll(file, data.getBytes(encoding));
	}
	
	public static void writeAllText(File file, String data, Charset encoding) throws IOException
	{
		writeAll(file, data.getBytes(encoding));
	}
	
	public static void writeAllText(File file, String data, boolean append) throws IOException
	{
		writeAllText(file, data, StandardCharsets.UTF_8, append);
	}
	
	public static void writeAllText(File file, String data, String encoding, boolean append) throws IOException
	{
		writeAll(file, data.getBytes(encoding), append);
	}
	
	public static void writeAllText(File file, String data, Charset encoding, boolean append) throws IOException
	{
		writeAll(file, data.getBytes(encoding), append);
	}
	
	
	
	
	
	
	
	
	public static void writeAllIfNotAlready(File file, byte[] data) throws IOException
	{
		if (lexists(file))
		{
			if (file.isFile())
			{
				byte[] already = readAll(file);
				if (Arrays.equals(data, already))
					return;
			}
			
			//else
			throw new IOException("Differing file contents! at "+repr(file.getAbsolutePath()));
		}
		else
		{
			writeAll(file, data);
		}
	}
	
	
	public static void writeAllTextIfNotAlready(File file, String data) throws IOException
	{
		if (lexists(file))
		{
			if (file.isFile())
			{
				String already = readAllText(file);
				if (data.equals(already))
					return;
			}
			
			//else
			throw new IOException("Differing file contents! at "+repr(file.getAbsolutePath()));
		}
		else
		{
			writeAllText(file, data);
		}
	}
	
	
	
	
	
	
	
	
	
	public static byte[] readSectionOfFileToMemory(File f, long offset, int length) throws IOException
	{
		//Todo use RandomAccessFile :3
		
		try (InputStream in = new FileInputStream(f))
		{
			if (offset > 0)
				JRECompatIOUtilities.skipFully(in, offset);
			
			byte[] buffer = new byte[length];
			
			JRECompatIOUtilities.readFully(in, buffer);
			
			return buffer;
		}
	}
	
	public static void readSectionOfFileToOtherFile(File source, long offset, long length, File dest) throws IOException
	{
		//Todo use RandomAccessFile's :3
		
		try (InputStream in = new FileInputStream(source))
		{
			if (offset > 0)
				JRECompatIOUtilities.skipFully(in, offset);
			
			try (OutputStream out = new FileOutputStream(dest))
			{
				JRECompatIOUtilities.pumpFixed(in, out, length);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ChannelProvider<FileChannel> getChannelProviderForLocalFile(final File file, final String mode)
	{
		return new ChannelProvider<FileChannel>
		()
		{
			@Override
			public FileChannel open() throws IOException
			{
				@SuppressWarnings("resource")
				RandomAccessFile raf = new RandomAccessFile(file, mode);
				return raf.getChannel();
			}
		};
	}
	
	public static ChannelProvider<FileChannel> getChannelProviderForLocalFile(final File file)
	{
		return getChannelProviderForLocalFile(file, "rw"); //everypony loves defaults!
	}
	
	/**
	 * :DD!
	 */
	public static MappedByteBuffer fileAsByteBufferReadonly(File file) throws IOException
	{
		if (!file.isFile())
			throw new IOException("File is not a file!!: "+repr(file.getAbsolutePath()));
		
		long length = file.length();
		return fileAsByteBufferReadonly(file, 0, length);
	}
	
	public static MappedByteBuffer fileAsByteBuffeReadwrite(File file) throws IOException
	{
		if (!file.isFile())
			throw new IOException("File is not a file!!: "+repr(file.getAbsolutePath()));
		
		long length = file.length();
		return fileAsByteBufferReadwrite(file, 0, length);
	}
	
	public static MappedByteBuffer fileAsByteBufferReadonly(File file, long offset, long length) throws IOException
	{
		FileChannel fc = FSUtilities.openReadonlyNIO(file);
		return fc.map(MapMode.READ_ONLY, offset, length);
	}
	
	public static MappedByteBuffer fileAsByteBufferReadwrite(File file, long offset, long length) throws IOException
	{
		FileChannel fc = FSUtilities.openReadwriteNIO(file);
		return fc.map(MapMode.READ_WRITE, offset, length);
	}
	
	
	
	
	
	
	
	
	
	
	public static void doOnLines(File f, UnaryProcedure<String> process) throws IOException
	{
		try (BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(f))))
		{
			while (true)
			{
				String line = in.readLine();
				if (line == null)
					break;
				
				process.f(line);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO Resolve these duplicates XD''
	
	
	public static boolean compare(File a, File b) throws IOException
	{
		if (!a.isFile() || !b.isFile())
			throw new IOException("Cannot compare non-files");
		
		if (a.length() != b.length())
			return false;
		
		FileInputStream inA = null;
		FileInputStream inB = null;
		
		try
		{
			inA = new FileInputStream(a);
			inB = new FileInputStream(b);
			boolean results = JRECompatIOUtilities.compare(inA, inB);
			inA.close();
			inB.close();
			return results;
		}
		catch (IOException exc)
		{
			closeWithoutError(inA);
			closeWithoutError(inB);
			throw exc;
		}
	}
	
	
	
	
	/**
	 * This shall not do any symlinky/hardlinky funny business!
	 * It simply purely compares the contents of the two files, byte-for-byte, as would be read by {@link FileInputStream}.
	 * (Though it compares the {@link File#length() lengths} first and quick-fails (returns false) if they're different.)
	 */
	public static boolean fileeq(File a, File b) throws IOException
	{
		return filecmp(a, b) == 0;
	}
	
	/**
	 * This shall not do any symlinky/hardlinky funny business!
	 * It simply purely compares the contents of the two files, byte-for-byte, as would be read by {@link FileInputStream}.
	 * (Though it compares the {@link File#length() lengths} first and quick-returns {@link SmallIntegerMathUtilities#cmp(long, long) cmp}(a.{@link File#length() length}(), b.{@link File#length() length}()) if they're different.)
	 */
	public static int filecmp(File a, File b) throws IOException
	{
		if (!a.isFile())
			throw new IOException("Not a file: "+repr(a.getAbsolutePath()));
		if (!b.isFile())
			throw new IOException("Not a file: "+repr(b.getAbsolutePath()));
		
		int c = cmp(a.length(), b.length());
		if (c != 0)
			return c;
		
		try (InputStream inA = new FileInputStream(a))
		{
			try (InputStream inB = new FileInputStream(b))
			{
				return streamcmp(inA, inB);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Nonnull
	public static String readAllTextRE(File file, String encoding)
	{
		try
		{
			return readAllText(file, encoding);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	@Nonnull
	public static String readAllTextRE(File file, Charset encoding)
	{
		try
		{
			return readAllText(file, encoding);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	@Nonnull
	public static String readAllTextRE(File file)
	{
		try
		{
			return readAllText(file);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	@Nonnull
	public static String readAllTextRE(File file, TextEncodingDetector encodingDetector)
	{
		try
		{
			return readAllText(file, encodingDetector);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
}
