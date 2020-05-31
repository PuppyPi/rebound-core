package rebound.io.util;

import static java.util.Objects.*;
import static rebound.util.collections.PolymorphicCollectionUtilities.*;
import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.exceptions.ClosedIOException;
import rebound.exceptions.SlowVersionUnsupportedException;
import rebound.io.ShortReadIOException;
import rebound.io.ShortWriteIOException;
import rebound.io.packeted.BidirectionalPacketream;
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
	public static int receiveAllowingShorts(InputPacketream self, @WritableValue byte[] array, int offset, int length) throws IOException
	{
		return self.receive(array, offset, length);
	}
	
	public static int receiveAllowingShorts(InputPacketream self, @WritableValue byte[] array) throws IOException
	{
		return receiveAllowingShorts(self, array, 0, array.length);
	}
	
	public static int receiveAllowingShorts(InputPacketream self, @WritableValue Slice<byte[]> arraySlice) throws IOException
	{
		return receiveAllowingShorts(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static int receiveAllowingShorts(InputPacketream self, @WritableValue ByteList list) throws IOException
	{
		ensureWritableCollection(list);
		
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new SlowVersionUnsupportedException();
		return receiveAllowingShorts(self, s);
	}
	
	
	
	public static int sendAllowingShorts(OutputPacketream self, @ReadonlyValue byte[] array, int offset, int length) throws IOException
	{
		return self.send(array, offset, length);
	}
	
	public static int sendAllowingShorts(OutputPacketream self, @ReadonlyValue byte[] array) throws IOException
	{
		return sendAllowingShorts(self, array, 0, array.length);
	}
	
	public static int sendAllowingShorts(OutputPacketream self, @ReadonlyValue Slice<byte[]> arraySlice) throws IOException
	{
		return sendAllowingShorts(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static int sendAllowingShorts(OutputPacketream self, @ReadonlyValue ByteList list) throws IOException
	{
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new SlowVersionUnsupportedException();
		return sendAllowingShorts(self, s);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void receiveFullyOrThrow(InputPacketream self, @WritableValue byte[] array, int offset, int length) throws IOException, ShortReadIOException
	{
		int r = receiveAllowingShorts(self, array, offset, length);
		if (r != length)
			throw new ShortReadIOException(length, r);
	}
	
	public static void receiveFullyOrThrow(InputPacketream self, @WritableValue byte[] array) throws IOException, ShortReadIOException
	{
		receiveFullyOrThrow(self, array, 0, array.length);
	}
	
	public static void receiveFullyOrThrow(InputPacketream self, @WritableValue Slice<byte[]> arraySlice) throws IOException, ShortReadIOException
	{
		receiveFullyOrThrow(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void receiveFullyOrThrow(InputPacketream self, @WritableValue ByteList list) throws IOException, ShortReadIOException
	{
		ensureWritableCollection(list);
		
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new SlowVersionUnsupportedException();
		receiveFullyOrThrow(self, s);
	}
	
	
	
	
	
	public static void sendFullyOrThrow(OutputPacketream self, @ReadonlyValue byte[] array, int offset, int length) throws IOException, ShortWriteIOException
	{
		int r = sendAllowingShorts(self, array, offset, length);
		if (r != length)
			throw new ShortWriteIOException(length, r);
	}
	
	public static void sendFullyOrThrow(OutputPacketream self, @ReadonlyValue byte[] array) throws IOException, ShortWriteIOException
	{
		sendFullyOrThrow(self, array, 0, array.length);
	}
	
	public static void sendFullyOrThrow(OutputPacketream self, @ReadonlyValue Slice<byte[]> arraySlice) throws IOException, ShortWriteIOException
	{
		sendFullyOrThrow(self, arraySlice.getUnderlying(), arraySlice.getOffset(), arraySlice.getLength());
	}
	
	public static void sendFullyOrThrow(OutputPacketream self, @ReadonlyValue ByteList list) throws IOException, ShortWriteIOException
	{
		Slice<byte[]> s = list.toByteArraySliceLiveOrNull();
		if (s == null)
			throw new SlowVersionUnsupportedException();
		sendFullyOrThrow(self, s);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param c will be called exactly once before each packet, like a while() loop :3
	 */
	public static void pumpAllowingShortReceivesButNotShortSends(InputPacketream in, OutputPacketream out, int requestedPacketSizeForReceives, NullaryFunction<ContinueSignal> c) throws IOException
	{
		byte[] a = new byte[requestedPacketSizeForReceives];
		
		while (requireNonNull(c.f()) == ContinueSignal.Continue)
		{
			int r = receiveAllowingShorts(in, a, 0, requestedPacketSizeForReceives);
			
			sendFullyOrThrow(out, a, 0, r);
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
			receiveFullyOrThrow(in, a, 0, packetSize);
			sendFullyOrThrow(out, a, 0, packetSize);
		}
	}
	
	
	
	
	
	
	public static void pumpFixedAllowingShortReceivesButNotShortSends(InputPacketream in, OutputPacketream out, int requestedPacketSizeForReceives, int numberOfPackets) throws IOException
	{
		IntegerContainer remaining_C = new SimpleIntegerContainer(numberOfPackets);
		
		pumpAllowingShortReceivesButNotShortSends(in, out, requestedPacketSizeForReceives, () ->
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
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Wrap a bidirectional packetream as a {@link BidirectionalPacketream} X'D
	 * 
	 * Sigh, Java typing X'D
	 * I wonder if this is a problem in Haskell? :>
	 */
	public static <T extends InputPacketream & OutputPacketream> BidirectionalPacketream wrapToConvenienceAPI(T x)
	{
		return new BidirectionalPacketream()
		{
			@Override
			public int send(byte[] array, int offset, int length) throws IOException, EOFException, ClosedIOException
			{
				return x.send(array, offset, length);
			}
			
			@Override
			public int receive(byte[] array, int offset, int length) throws IOException, EOFException, ClosedIOException
			{
				return x.receive(array, offset, length);
			}
		};
	}
	
	
	
	/**
	 * Wrap a closeable bidirectional packetream as a {@link Closeable} {@link BidirectionalPacketream} X'D
	 * 
	 * Sigh, Java typing X'D
	 * I wonder if this is a problem in Haskell? :>
	 */
	public static <T extends InputPacketream & OutputPacketream & Closeable, O extends BidirectionalPacketream & Closeable> O wrapToCloseableConvenienceAPI(T x)
	{
		class c
		implements BidirectionalPacketream, Closeable
		{
			@Override
			public int send(byte[] array, int offset, int length) throws IOException, EOFException, ClosedIOException
			{
				return x.send(array, offset, length);
			}
			
			@Override
			public int receive(byte[] array, int offset, int length) throws IOException, EOFException, ClosedIOException
			{
				return x.receive(array, offset, length);
			}
			
			@Override
			public void close() throws IOException
			{
				x.close();
			}
		};
		
		
		return (O)new c();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static enum NullOutputPacketreamGobbling
	implements OutputPacketream, Closeable
	{
		I;
		
		
		@Override
		public int send(byte[] b, int off, int len)
		{
			//no op.
			return len;  //gobble any data! X3
		}
		
		@Override
		public void close() throws IOException
		{
		}
	}
	
	
	public static enum NullOutputPacketreamRejecting
	implements OutputPacketream, Closeable
	{
		I;
		
		
		@Override
		public int send(byte[] b, int off, int len)
		{
			//no op.
			return 0;  //reject any data!
		}
		
		@Override
		public void close() throws IOException
		{
		}
	}
	
	
	public static enum EmptyInputPacketreamRejecting
	implements InputPacketream, Closeable
	{
		I;
		
		
		@Override
		public int receive(byte[] b, int off, int len)
		{
			//no op.
			return 0;  //reject any requests for data!
		}
		
		@Override
		public void close() throws IOException
		{
		}
	}
}
