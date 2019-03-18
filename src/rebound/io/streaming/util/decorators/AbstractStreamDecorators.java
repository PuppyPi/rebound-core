package rebound.io.streaming.util.decorators;

import java.io.EOFException;
import java.io.IOException;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ReferenceBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockWriteStream;

public class AbstractStreamDecorators
{
	public static abstract class AbstractReferenceBlockReadStreamDecorator<D>
	extends AbstractStreamDecorator<ReferenceBlockReadStream<D>>
	implements ReferenceBlockReadStream<D>
	{
		protected AbstractReferenceBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractReferenceBlockReadStreamDecorator(ReferenceBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public D read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(D[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractReferenceBlockWriteStreamDecorator<D>
	extends AbstractWriteStreamDecorator<ReferenceBlockWriteStream<D>>
	implements ReferenceBlockWriteStream<D>
	{
		protected ReferenceBlockWriteStream<D> underlying;
		
		protected AbstractReferenceBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractReferenceBlockWriteStreamDecorator(ReferenceBlockWriteStream<D> underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(D unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(D[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	public static abstract class Abstract_$$Prim$$_BlockReadStreamDecorator
	extends AbstractStreamDecorator<_$$Prim$$_BlockReadStream>
	implements _$$Prim$$_BlockReadStream
	{
		protected Abstract_$$Prim$$_BlockReadStreamDecorator()
		{
			super();
		}
		
		protected Abstract_$$Prim$$_BlockReadStreamDecorator(_$$Prim$$_BlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		public _$$prim$$_ read() throws EOFException, IOException, ClosedStreamException
		{
			return underlying.read();
		}
		
		public int read(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class Abstract_$$Prim$$_BlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<_$$Prim$$_BlockWriteStream>
	implements _$$Prim$$_BlockWriteStream
	{
		protected _$$Prim$$_BlockWriteStream underlying;
		
		protected Abstract_$$Prim$$_BlockWriteStreamDecorator()
		{
			super();
		}
		
		protected Abstract_$$Prim$$_BlockWriteStreamDecorator(_$$Prim$$_BlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		public void write(_$$prim$$_ unit) throws EOFException, IOException, ClosedStreamException
		{
			underlying.write(unit);
		}

		public int write(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	 */
	
	public static abstract class AbstractBooleanBlockReadStreamDecorator
	extends AbstractStreamDecorator<BooleanBlockReadStream>
	implements BooleanBlockReadStream
	{
		protected AbstractBooleanBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractBooleanBlockReadStreamDecorator(BooleanBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public boolean read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractBooleanBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<BooleanBlockWriteStream>
	implements BooleanBlockWriteStream
	{
		protected BooleanBlockWriteStream underlying;
		
		protected AbstractBooleanBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractBooleanBlockWriteStreamDecorator(BooleanBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(boolean unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	public static abstract class AbstractByteBlockReadStreamDecorator
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ByteBlockReadStream
	{
		protected AbstractByteBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractByteBlockReadStreamDecorator(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public byte read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractByteBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements ByteBlockWriteStream
	{
		protected ByteBlockWriteStream underlying;
		
		protected AbstractByteBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractByteBlockWriteStreamDecorator(ByteBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(byte unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	public static abstract class AbstractCharBlockReadStreamDecorator
	extends AbstractStreamDecorator<CharBlockReadStream>
	implements CharBlockReadStream
	{
		protected AbstractCharBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractCharBlockReadStreamDecorator(CharBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractCharBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<CharBlockWriteStream>
	implements CharBlockWriteStream
	{
		protected CharBlockWriteStream underlying;
		
		protected AbstractCharBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractCharBlockWriteStreamDecorator(CharBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(char unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	public static abstract class AbstractShortBlockReadStreamDecorator
	extends AbstractStreamDecorator<ShortBlockReadStream>
	implements ShortBlockReadStream
	{
		protected AbstractShortBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractShortBlockReadStreamDecorator(ShortBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractShortBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<ShortBlockWriteStream>
	implements ShortBlockWriteStream
	{
		protected ShortBlockWriteStream underlying;
		
		protected AbstractShortBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractShortBlockWriteStreamDecorator(ShortBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(short unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	public static abstract class AbstractFloatBlockReadStreamDecorator
	extends AbstractStreamDecorator<FloatBlockReadStream>
	implements FloatBlockReadStream
	{
		protected AbstractFloatBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractFloatBlockReadStreamDecorator(FloatBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractFloatBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<FloatBlockWriteStream>
	implements FloatBlockWriteStream
	{
		protected FloatBlockWriteStream underlying;
		
		protected AbstractFloatBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractFloatBlockWriteStreamDecorator(FloatBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(float unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	public static abstract class AbstractIntBlockReadStreamDecorator
	extends AbstractStreamDecorator<IntBlockReadStream>
	implements IntBlockReadStream
	{
		protected AbstractIntBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractIntBlockReadStreamDecorator(IntBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractIntBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<IntBlockWriteStream>
	implements IntBlockWriteStream
	{
		protected IntBlockWriteStream underlying;
		
		protected AbstractIntBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractIntBlockWriteStreamDecorator(IntBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(int unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	public static abstract class AbstractDoubleBlockReadStreamDecorator
	extends AbstractStreamDecorator<DoubleBlockReadStream>
	implements DoubleBlockReadStream
	{
		protected AbstractDoubleBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractDoubleBlockReadStreamDecorator(DoubleBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractDoubleBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<DoubleBlockWriteStream>
	implements DoubleBlockWriteStream
	{
		protected DoubleBlockWriteStream underlying;
		
		protected AbstractDoubleBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractDoubleBlockWriteStreamDecorator(DoubleBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(double unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	public static abstract class AbstractLongBlockReadStreamDecorator
	extends AbstractStreamDecorator<LongBlockReadStream>
	implements LongBlockReadStream
	{
		protected AbstractLongBlockReadStreamDecorator()
		{
			super();
		}
		
		protected AbstractLongBlockReadStreamDecorator(LongBlockReadStream underlying)
		{
			super(underlying);
		}
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			return this.underlying.read();
		}
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.read(buffer, offset, length);
		}
	}
	
	
	
	public static abstract class AbstractLongBlockWriteStreamDecorator
	extends AbstractWriteStreamDecorator<LongBlockWriteStream>
	implements LongBlockWriteStream
	{
		protected LongBlockWriteStream underlying;
		
		protected AbstractLongBlockWriteStreamDecorator()
		{
			super();
		}
		
		protected AbstractLongBlockWriteStreamDecorator(LongBlockWriteStream underlying)
		{
			this.underlying = underlying;
		}
		
		
		
		@Override
		public void write(long unit) throws EOFException, IOException, ClosedStreamException
		{
			this.underlying.write(unit);
		}
		
		@Override
		public int write(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			return this.underlying.write(buffer, offset, length);
		}
	}
	
	
	
	
	
	
	// >>>
}
