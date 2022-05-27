package rebound.io.util;

import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.ByteChannel;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.bits.Unsigned;
import rebound.exceptions.BinarySyntaxIOException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NotYetImplementedException;
import rebound.io.CloseableList;
import rebound.io.CloseableSimpleIterator;
import rebound.io.DelegatingCloseableList;
import rebound.io.DelegatingCloseableSimpleIterator;
import rebound.text.StringUtilities;
import rebound.util.BufferAllocationType;
import rebound.util.PlatformNIOBufferUtilities;
import rebound.util.ProgressObserver;
import rebound.util.collections.DefaultList;
import rebound.util.collections.DefaultReadonlyList;
import rebound.util.collections.Mapper;
import rebound.util.collections.SimpleIterator;
import rebound.util.functional.FunctionInterfaces.NullaryFunction;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;
import rebound.util.objectutil.JavaNamespace;

public class ExtraIOUtilities
implements JavaNamespace
{
	public static interface SynchronousConsumer
	{
		/**
		 * Consume some data from buffers.
		 * The guarantee is that you can read <code>length</code> bytes starting at <code>offset</code> from both buffers.
		 * If more data is to be consumed, return <code>null</code>. (note: it will be assumed that you consumed all this data (<code>length</code> bytes))
		 * Otherwise, consumption stops and the return value is passed through {@link ExtraIOUtilities#consumeInSync(InputStream, InputStream, SynchronousConsumer, int)}.
		 * @return <code>null</code> to continue, anything else to abort
		 */
		public Object consume(byte[] bufferA, byte[] bufferB, int offset, int length);
		
		/**
		 * Indicates that EOF was reached in one or both streams.
		 * Note: you're guaranteed that <code>a || b</code> will be <code>true</code>.
		 * Synchronous consumption stops after [an] eof[s] is[are] reached, so whatever this returns will be returned by {@link ExtraIOUtilities#consumeInSync(InputStream, InputStream, SynchronousConsumer, int)}
		 * @param a whether or not stream A reached EOF
		 * @param b whether or not stream B reached EOF
		 * @return this will be returned from consume() no matter what it is (since synchronous consuming can't happen after eof)
		 */
		public Object eofReached(boolean a, boolean b);
	}
	
	
	
	
	
	public static class InputStreamChannelAdapter
	extends InputStream
	implements ReadableByteChannel
	{
		protected ReadableByteChannel underlying;
		
		protected ByteBuffer copyBuffer;
		
		protected ByteBuffer lastWrappedBuffer;
		
		
		/**
		 * @param copyBuffer can be <code>null</code> to mandate that wrapping the passed byte[] array should always be done (even if a different byte[] array is passed each time and a new wrapping ByteBuffer must be created each read)
		 */
		public InputStreamChannelAdapter(ReadableByteChannel underlying, ByteBuffer copyBuffer)
		{
			this.underlying = underlying;
			this.copyBuffer = copyBuffer;
		}
		
		
		/* From InputStream.java:
		 * 		<p> If <code>len</code> is zero, then no bytes are read and
		 * 		<code>0</code> is returned; otherwise, there is an attempt to read at
		 * 		least one byte. If no byte is available because the stream is at end of
		 * 		file, the value <code>-1</code> is returned; otherwise, at least one
		 * 		byte is read and stored into <code>b</code>.
		 */
		protected int readAtLeastOne(ByteBuffer buffer) throws IOException
		{
			while (true)
			{
				int amt = this.underlying.read(buffer);
				if (amt == -1)
					return -1;
				else if (amt == 0)
					continue;
				else
					return amt;
			}
		}
		
		@Override
		public int read(byte[] b, int off, int len) throws IOException
		{
			if (!this.underlying.isOpen())
				throw new IOException("It's closed, yo!");
			
			if (len == 0)
				return 0;
			
			if (this.lastWrappedBuffer == null || this.lastWrappedBuffer.array() != b)
			{
				if (this.copyBuffer == null || b.length >= 512)
				{
					this.lastWrappedBuffer = ByteBuffer.wrap(b);
				}
			}
			
			if (this.lastWrappedBuffer != null && this.lastWrappedBuffer.array() == b)
			{
				//Hey; I remember you! :D  (XD)
				this.lastWrappedBuffer.position(off);
				this.lastWrappedBuffer.limit(off+len);
				return readAtLeastOne(this.lastWrappedBuffer);
			}
			else
			{
				if (this.copyBuffer == null)
					throw new ImpossibleException("it should have been wrapped! ;_;");
				
				this.copyBuffer.position(0);
				int maxAmount = Math.min(len, this.copyBuffer.capacity());
				this.copyBuffer.limit(maxAmount);
				int amountRead = readAtLeastOne(this.copyBuffer);
				if (amountRead == -1)
					return -1;
				this.copyBuffer.get(b, off, amountRead); //copy!
				return amountRead;
			}
		}
		
		@Override
		public int read() throws IOException
		{
			this.copyBuffer.position(0);
			this.copyBuffer.limit(0+1);
			int amount = forceReadNIO(this.underlying, this.copyBuffer);
			if (amount < 1)
				return -1;
			else
				return Unsigned.upcast(this.copyBuffer.get());
		}
		
		@Override
		public long skip(long amountToSkip) throws IOException
		{
			long amountSoFar = 0;
			
			while (amountSoFar < amountToSkip)
			{
				this.copyBuffer.position(0);
				
				if (amountToSkip - amountSoFar > Integer.MAX_VALUE)
					this.copyBuffer.limit(this.copyBuffer.remaining());
				else
					this.copyBuffer.limit(Math.min(this.copyBuffer.remaining(), (int)(amountToSkip - amountSoFar)));
				
				int amt = this.underlying.read(this.copyBuffer);
				
				if (amt == -1)
					return amountSoFar;
				
				amountSoFar += amt;
			}
			
			return amountSoFar;
		}
		
		
		
		@Override
		public boolean isOpen()
		{
			return this.underlying.isOpen();
		}
		
		@Override
		public void close() throws IOException
		{
			this.underlying.close();
		}
		
		@Override
		public int read(ByteBuffer dst) throws IOException
		{
			return this.underlying.read(dst);
		}
	}
	
	public static class IODirectionNotSupportedException
	extends IOException
	{
		private static final long serialVersionUID = 1L;
	}
	
	public static interface HappyByteChannel
	//extends GeneralChannel<ByteBuffer>
	extends ByteChannel
	{
		public boolean supportsDirection(boolean direction);
		public boolean willHaveToCopyForNondirectBuffers(boolean direction);
		public int io(boolean direction, ByteBuffer buffer) throws IOException;
		public int io(boolean direction, byte[] buf, int offset, int length) throws IOException;
		public int io(boolean direction, HappyByteChannel stream, int maxAmount) throws IOException;
		public int io(boolean direction, ReadableByteChannel stream, int maxAmount) throws IOException;
		public int io(boolean direction, WritableByteChannel stream, int maxAmount) throws IOException;
		public int io(boolean direction, ByteChannel stream, int maxAmount) throws IOException;
		public int io(boolean direction, InputStream stream, int maxAmount) throws IOException;
		public int io(boolean direction, OutputStream stream, int maxAmount) throws IOException;
		public int skip(int maxAmount); //implicitly a read operation
		public int writesame(byte value, int maxAmount); //implicitly a write operation :>
	}
	
	public static final SynchronousConsumer CONSUMER_COMPARISON = new SynchronousConsumer()
	{
		@Override
		public Object eofReached(boolean a, boolean b)
		{
			//If one stream but not the other reaches eof, then the streams are of different lengths ==> comparison fails
			//If both streams reach eof, well that's the only way the comparison fully succeeds.
			return a && b;
		}
		
		@Override
		public Object consume(byte[] bufferA, byte[] bufferB, int offset, int length)
		{
			int stop = offset + length;
			for (int index = offset; index < stop; index++)
				if (bufferA[index] != bufferB[index])
					//abort!  we know right here that the comparison fails, no more data need be read
					return false;
			
			//continue on
			return null;
		}
	};
	//Chanelssss!
	public static final boolean READ = false;
	public static final boolean WRITE = true;
	
	public static Object consumeInSync(InputStream a, InputStream b, SynchronousConsumer consumer, int bufferSize) throws IOException
	{
		//buffers
		byte[] bufferA = new byte[bufferSize], bufferB = new byte[bufferSize];
		
		//Amount of available data in each buffer
		int fillA = 0, fillB = 0;
		
		//used for passing to consumer.consume()
		int offset = 0, length = 0;
		
		//returned from consumer.consume()
		Object rv = null;
		
		
		
		/*
		 * The algorithm is this:
		 * 	1. Read as much as you can into both buffers
		 * 	2. 'Consume' the data they have in common. (consume here means to test for equality, and if a mismatch is found fail the method [return false] immediately, so anything coming after a consumtion means that the consumed data was valid [equivalent])
		 * 	3. If the amounts of data in both buffers was equal:
		 * 		3t1. Goto step 1.
		 * 	3. Otherwise
		 * 		3f1. Move up [offset] to the first byte only the more filled buffer has
		 * 		3f2. Make [fillX] be the amount of data which that one buffer has that the other one doesn't (where X is the more filled buffer)
		 * 		3f3. Keep reading data into the shorted buffer (not X), and consuming it, until it has caught up
		 * 		3f4. Goto step 1.
		 */
		
		while (true)
		{
			if (fillA == 0 && fillB == 0)
			{
				fillA = a.read(bufferA, 0, bufferSize);
				fillB = b.read(bufferB, 0, bufferSize);
				offset = 0;
			}
			else
			{
				if (fillA == 0)
				{
					//Meaning fillA == 0 && fillB > 0
					
					//Try to get A caught up with B
					fillA = a.read(bufferA, offset, fillB);
					if (fillA > fillB)
						throw new IllegalStateException("InputStream "+a+" read more bytes ("+fillA+") than was requested ("+fillB+")");
				}
				else
				{
					//Meaning fillB == 0 && fillA > 0
					
					//Try to get B caught up with A
					fillB = b.read(bufferB, offset, fillA);
					if (fillB > fillA)
						throw new IllegalStateException("InputStream "+b+" read more bytes ("+fillB+") than was requested ("+fillA+")");
				}
			}
			
			//EOF
			if (fillA < 0 || fillB < 0)
			{
				/*
				 * If they happen at the same time, the lengths of the streams are equal, if not they are not equal.
				 * This has to happen because a block read that requests data past EOF won't read all of it and so things go normally until new data is requested, and if they are the same length this will happen:
				 * 	1. One reads ahead of the other.
				 * 	2. The other keeps reading until it catches up (it won't read past the point where the other is at (fillX), so it won't get EOF at this point)
				 * 	3. They both read simultaneously and get -1
				 * 			--Alternately--
				 * 	1. They both read the same amount
				 * 	2. On next read they both signal EOF.
				 * 
				 * If they are not the same length, this happens:
				 * 	1. One reads ahead of the other.
				 * 	2. The other keeps reading until it catches up.
				 * 	3. 	--In the process of catching up, it encounteres an EOF signal
				 * 			--Alternately--
				 * 	1. They both read the same amount of data.
				 * 	2. On next read one gets EOF but the other doesn't because they are different lengths.
				 */
				return consumer.eofReached(fillA < 0, fillB < 0);
			}
			
			
			//Compare the gathered data (but only what *both* buffers have)
			{
				length = fillA < fillB ? fillA : fillB;
				
				//<Consume bufferA and bufferB from $offset to $stop (exclusive)
				rv = consumer.consume(bufferA, bufferB, offset, length);
				if (rv != null) //non-null specially means: Abort
					return rv;
				//Consume>
				
				//Once data is compared, bump up the markers which indicate which buffer still has data (or that there was an equal amount consumed and so is no more left)
				{
					offset += length;
					fillA -= length;
					fillB -= length;
				}
			}
		}
	}
	
	public static Object readObjectThrowingSyntaxExceptionInsteadOfClassNotFoundException(ObjectInputStream oin) throws IOException, BinarySyntaxIOException
	{
		try
		{
			return oin.readObject();
		}
		catch (ClassNotFoundException exc)
		{
			throw BinarySyntaxIOException.inst("Available class file mismatch; ClassNotFoundException on deserialization: "+StringUtilities.repr(exc.getMessage()));
		}
	}
	
	public static int forceReadNIO(ReadableByteChannel channel, ByteBuffer buffer) throws IOException
	{
		int amount = 0;
		while (buffer.hasRemaining())
		{
			int amt = channel.read(buffer);
			if (amt == -1)
				break;
			amount += amt;
		}
		return amount;
	}
	
	public static int forceWriteNIO(WritableByteChannel channel, ByteBuffer buffer) throws IOException
	{
		int amount = 0;
		while (buffer.hasRemaining())
		{
			int amt = channel.write(buffer);
			if (amt == -1)
				break;
			amount += amt;
		}
		return amount;
	}
	
	public static int forceIO(Object channel, ByteBuffer buffer, boolean direction) throws IOException
	{
		if (direction == READ)
			return forceReadNIO((ReadableByteChannel)channel, buffer);
		else
			return forceWriteNIO((WritableByteChannel)channel, buffer);
	}
	
	public static void enforceReadNIO(ReadableByteChannel channel, ByteBuffer buffer) throws IOException, EOFException
	{
		int originalAmount = buffer.remaining();
		int readAmount = forceReadNIO(channel, buffer);
		if (readAmount < originalAmount)
			throw new EOFException();
	}
	
	public static void enforceWriteNIO(WritableByteChannel channel, ByteBuffer buffer) throws IOException, EOFException
	{
		int originalAmount = buffer.remaining();
		int readAmount = forceWriteNIO(channel, buffer);
		if (readAmount < originalAmount)
			throw new EOFException();
	}
	
	public static void enforceIO(Object channel, ByteBuffer buffer, boolean direction) throws IOException, EOFException
	{
		int originalAmount = buffer.remaining();
		int readAmount = forceIO(channel, buffer, direction);
		if (readAmount < originalAmount)
			throw new EOFException();
	}
	
	public static ByteChannel decorateDirectOnlyByteChannelForNondirectCopying(final ByteChannel rwChannel)
	{
		return decorateDirectOnlyByteChannelForNondirectCopying(rwChannel, -1);
	}
	
	/**
	 * @param bufferSize can be -1 to indicate auto-compute (currently: size of first nondirect buffer passed)
	 */
	public static ByteChannel decorateDirectOnlyByteChannelForNondirectCopying(final ByteChannel rwChannel, final int bufferSize)
	{
		return new ByteChannel()
		{
			public int io(boolean direction, ByteBuffer buffer) throws IOException
			{
				if (!rwChannel.isOpen())
					throw new IOException("It's closed, yo!");
				
				
				//TODO
				throw new NotYetImplementedException();
			}
			
			
			@Override
			public int read(ByteBuffer dst) throws IOException
			{
				return io(READ, dst);
			}
			
			@Override
			public int write(ByteBuffer src) throws IOException
			{
				return io(WRITE, src);
			}
			
			@Override
			public boolean isOpen()
			{
				return rwChannel.isOpen();
			}
			
			@Override
			public void close() throws IOException
			{
				rwChannel.close();
			}
		};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//See NIOBufferUtilities! :D
	
	
	
	//Todo writes!
	
	
	
	
	
	
	//Todo discardByReading(ReadableByteChannel channel, long amount)
	
	
	
	
	
	
	
	
	
	
	//Todo an adapter for WritableByteChannels -> OutputStreams
	
	
	
	//Todo HappyByteChannel
	
	
	
	//Todo
	//	public static class HappyByteChannelAdapter
	//	implements HappyByteChannel
	//	{
	//		protected ReadableByteChannel underlyingR;
	//		protected WritableByteChannel underlyingW;
	//
	//	}
	
	
	
	
	
	
	//Todo decorateDirectOnlyByteChannelForNondirectCopying for Readable-only and Writable-only
	
	
	
	//Todo ObjectBuffer<T>  ;>
	//		+ [Readable/Writable]SimpleChannel<B extends Buffer> (logical superinterface of ByteChannel)
	
	
	
	
	
	
	
	//Todo public static ChannelProvider<SocketChannel> getChannelProviderForTCPClientConnection(...)
	
	
	//todo buffered channel decorators; IMPLEMENT java.io.Flushable!!  (for writable channel things, of course)
	
	
	
	public static ByteBuffer readAllToNIOBuffer(InputStream in, BufferAllocationType bufferAllocationType) throws IOException
	{
		//Todo better way X'D
		byte[] all = JRECompatIOUtilities.readAll(in);
		ByteBuffer buffer = PlatformNIOBufferUtilities.allocateByteBuffer(all.length, bufferAllocationType);
		buffer.put(all);
		buffer.position(0);
		return buffer;
	}
	
	public static void readFully(InputStream in, byte[] buff, ProgressObserver progressObserver) throws EOFException, IOException
	{
		//Use other method rather than handle possibility of null
		if (progressObserver == null)
			JRECompatIOUtilities.readFully(in, buff);
		else
			ExtraIOUtilities.readFully(in, buff, 0, buff.length, progressObserver);
	}
	
	/**
	 * Tries until the specified number of bytes are actually read.
	 * @throws EOFException If the underlying stream returns -1 bytes read
	 * @throws IOException If the underlying stream throws one
	 */
	public static void readFully(InputStream in, byte[] buff, int offset, int len, ProgressObserver progressObserver) throws EOFException, IOException
	{
		//Use other method rather than handle possibility of null
		if (progressObserver == null)
		{
			JRECompatIOUtilities.readFully(in, buff, offset, len);
		}
		else
		{
			progressObserver.update(0.0);
			
			//Note: unless we read in chunks at a time, or the inputstream does in fact take the liberty of reading less than requested, the progress observer won't be notified properly
			int read = 0;
			int r = 0;
			int l = 0;
			while (true)
			{
				l = len - read;
				if (l > 65536) //limit the amount read in each cycle to update the progress observer
					l = 65536;
				
				r = in.read(buff, offset+read, l);
				
				if (r < 0)
					throw new EOFException("Premature EOF");
				
				read += r;
				
				if (read >= len)
				{
					progressObserver.update(1.0);
					return;
				}
				
				progressObserver.update((double)read/(double)len);
			}
		}
	}
	
	//Todo CharBuffer readAllToNIOBuffer(Reader r, BAT)
	
	//Todo writeAll(Buffer src, OutputStream dest)
	
	//Todo readAllToNIOBuffer(File f)
	//Todo writeAll(Buffer src, File dest)
	
	
	
	
	
	
	
	
	
	public static <I, O> CloseableSimpleIterator<O> mapCloseable(Mapper<I, O> mapper, CloseableSimpleIterator<? extends I> underlying)
	{
		SimpleIterator<O> mappedNormally = map(mapper, underlying);
		return new DelegatingCloseableSimpleIterator<>(underlying, mappedNormally);
	}
	
	public static <E> CloseableSimpleIterator<E> filterCloseable(Predicate<E> predicate, CloseableSimpleIterator<E> underlying)
	{
		SimpleIterator<E> mappedNormally = filter(predicate, underlying);
		return new DelegatingCloseableSimpleIterator<>(underlying, mappedNormally);
	}
	
	
	
	@WritableValue
	public static @Nonnull <I, O> CloseableList<O> mappedCloseableListView(@Nonnull UnaryFunction<I, O> mapperForward, @Nonnull UnaryFunction<O, I> mapperReverse, @WritableValue @Nonnull CloseableList<I> underlying)
	{
		class MCL
		implements DefaultList<O>, CloseableList<O>
		{
			@Override
			public int size()
			{
				return underlying.size();
			}
			
			@Override
			public boolean isEmpty()
			{
				return underlying.isEmpty();
			}
			
			@Override
			public void clear()
			{
				underlying.clear();
			}
			
			@Override
			public O get(int index)
			{
				return mapperForward.f(underlying.get(index));
			}
			
			@Override
			public O set(int index, O element)
			{
				I prev = underlying.set(index, mapperReverse.f(element));
				return mapperForward.f(prev);
			}
			
			@Override
			public void add(int index, O element)
			{
				underlying.add(index, mapperReverse.f(element));
			}
			
			@Override
			public O remove(int index)
			{
				return mapperForward.f(underlying.remove(index));
			}
			
			@Override
			public void close()
			{
				underlying.close();
			}
		}
		
		return new MCL();
	}
	
	
	
	
	@ReadonlyValue
	public static @Nonnull <I, O> CloseableList<O> mappedCloseableListViewReadonly(@Nonnull UnaryFunction<I, O> mapperForward, @ReadonlyValue @Nonnull CloseableList<I> underlying)
	{
		class MCL
		implements DefaultReadonlyList<O>, CloseableList<O>
		{
			@Override
			public int size()
			{
				return underlying.size();
			}
			
			@Override
			public boolean isEmpty()
			{
				return underlying.isEmpty();
			}
			
			@Override
			public O get(int index)
			{
				return mapperForward.f(underlying.get(index));
			}
			
			@Override
			public void close()
			{
				underlying.close();
			}
		}
		
		return new MCL();
	}
	
	
	
	
	
	
	public static <E> Set<E> toSetClosing(NullaryFunction<CloseableSimpleIterator<E>> opener)
	{
		Set<E> s = new HashSet<>();
		try (CloseableSimpleIterator<E> i = opener.f())
		{
			for (E e : singleUseIterable(i))
				s.add(e);
		}
		return s;
	}
	
	public static int countClosing(NullaryFunction<CloseableSimpleIterator<?>> opener)
	{
		int n = 0;
		try (CloseableSimpleIterator<?> i = opener.f())
		{
			for (@SuppressWarnings("unused") Object e : singleUseIterable(i))
				n = safe_inc_s32(n);
		}
		return n;
	}
	
	
	
	
	
	public static <E> CloseableSimpleIterator<E> noopCloseable(SimpleIterator<E> underlying)
	{
		return new DelegatingCloseableSimpleIterator<>(() -> {}, underlying);
	}
	
	public static <E> CloseableList<E> noopCloseable(List<E> underlying)
	{
		return new DelegatingCloseableList<>(() -> {}, underlying);
	}
}
