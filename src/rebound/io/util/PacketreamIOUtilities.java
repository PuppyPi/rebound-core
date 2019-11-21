package rebound.io.util;

import static java.util.Objects.*;
import java.io.IOException;
import rebound.exceptions.UnsupportedOptionException;
import rebound.io.ShortReadIOException;
import rebound.io.ShortWriteIOException;
import rebound.io.packeted.InputPacketream;
import rebound.io.packeted.OutputPacketream;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.container.ContainerInterfaces.IntegerContainer;
import rebound.util.container.SimpleContainers.SimpleIntegerContainer;
import rebound.util.functional.ContinueSignal;
import rebound.util.functional.FunctionInterfaces.NullaryFunction;

public class PacketreamIOUtilities
{
	public static int readAllowingShorts(InputPacketream self, byte[] array, int offset, int length) throws IOException
	{
		return self.read(array, offset, length);
	}
	
	public static int readAllowingShorts(InputPacketream self, byte[] array) throws IOException
	{
		return readAllowingShorts(self, array, 0, array.length);
	}
	
	public static int readAllowingShorts(InputPacketream self, Slice<byte[]> arraySlice) throws IOException
	{
		return readAllowingShorts(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static int readAllowingShorts(InputPacketream self, ByteList list) throws IOException
	{
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new UnsupportedOptionException();
		return readAllowingShorts(self, s);
	}
	
	
	
	public static int writeAllowingShorts(OutputPacketream self, byte[] array, int offset, int length) throws IOException
	{
		return self.write(array, offset, length);
	}
	
	public static int writeAllowingShorts(OutputPacketream self, byte[] array) throws IOException
	{
		return writeAllowingShorts(self, array, 0, array.length);
	}
	
	public static int writeAllowingShorts(OutputPacketream self, Slice<byte[]> arraySlice) throws IOException
	{
		return writeAllowingShorts(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static int writeAllowingShorts(OutputPacketream self, ByteList list) throws IOException
	{
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new UnsupportedOptionException();
		return writeAllowingShorts(self, s);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void readFullyOrThrow(InputPacketream self, byte[] array, int offset, int length) throws IOException, ShortReadIOException
	{
		int r = readAllowingShorts(self, array, offset, length);
		if (r != length)
			throw new ShortReadIOException(length, r);
	}
	
	public static void readFullyOrThrow(InputPacketream self, byte[] array) throws IOException, ShortReadIOException
	{
		readFullyOrThrow(self, array, 0, array.length);
	}
	
	public static void readFullyOrThrow(InputPacketream self, Slice<byte[]> arraySlice) throws IOException, ShortReadIOException
	{
		readFullyOrThrow(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void readFullyOrThrow(InputPacketream self, ByteList list) throws IOException, ShortReadIOException
	{
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new UnsupportedOptionException();
		readFullyOrThrow(self, s);
	}
	
	
	
	
	
	public static void writeFullyOrThrow(OutputPacketream self, byte[] array, int offset, int length) throws IOException, ShortWriteIOException
	{
		int r = writeAllowingShorts(self, array, offset, length);
		if (r != length)
			throw new ShortWriteIOException(length, r);
	}
	
	public static void writeFullyOrThrow(OutputPacketream self, byte[] array) throws IOException, ShortWriteIOException
	{
		writeFullyOrThrow(self, array, 0, array.length);
	}
	
	public static void writeFullyOrThrow(OutputPacketream self, Slice<byte[]> arraySlice) throws IOException, ShortWriteIOException
	{
		writeFullyOrThrow(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void writeFullyOrThrow(OutputPacketream self, ByteList list) throws IOException, ShortWriteIOException
	{
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new UnsupportedOptionException();
		writeFullyOrThrow(self, s);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param c will be called exactly once before each packet, like a while() loop :3
	 */
	public static void pumpAllowingShortReadsButNotShortWrites(InputPacketream in, OutputPacketream out, int requestedPacketSizeForReads, NullaryFunction<ContinueSignal> c) throws IOException
	{
		byte[] a = new byte[requestedPacketSizeForReads];
		
		while (requireNonNull(c.f()) == ContinueSignal.Continue)
		{
			int r = readAllowingShorts(in, a, 0, requestedPacketSizeForReads);
			
			writeFullyOrThrow(out, a, 0, r);
		}
	}
	
	
	/**
	 * @param c will be called exactly once before each packet, like a while() loop :3
	 */
	public static void pumpEachPacketFullyOrThrow(InputPacketream in, OutputPacketream out, int packetSize, NullaryFunction<ContinueSignal> c) throws IOException
	{
		byte[] a = new byte[packetSize];
		
		while (requireNonNull(c.f()) == ContinueSignal.Continue)
		{
			readFullyOrThrow(in, a, 0, packetSize);
			writeFullyOrThrow(out, a, 0, packetSize);
		}
	}
	
	
	
	
	
	
	public static void pumpFixedAllowingShortReadsButNotShortWrites(InputPacketream in, OutputPacketream out, int requestedPacketSizeForReads, int numberOfPackets) throws IOException
	{
		IntegerContainer remaining_C = new SimpleIntegerContainer(numberOfPackets);
		
		pumpAllowingShortReadsButNotShortWrites(in, out, requestedPacketSizeForReads, () ->
		{
			int r = remaining_C.get();
			
			if (r == 0)
				return ContinueSignal.Stop;
			else
			{
				remaining_C.set(r - 1);
				return ContinueSignal.Continue;
			}
		});
	}
	
	
	
	public static void pumpFixedEachPacketFullyOrThrow(InputPacketream in, OutputPacketream out, int packetSize, int numberOfPackets) throws IOException
	{
		IntegerContainer remaining_C = new SimpleIntegerContainer(numberOfPackets);
		
		pumpEachPacketFullyOrThrow(in, out, packetSize, () ->
		{
			int r = remaining_C.get();
			
			if (r == 0)
				return ContinueSignal.Stop;
			else
			{
				remaining_C.set(r - 1);
				return ContinueSignal.Continue;
			}
		});
	}
}
