package rebound.io.streaming.util;

import java.io.EOFException;
import java.io.IOException;
import rebound.bits.Bytes;
import rebound.io.streaming.api.ClosedStreamException;
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
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockWriteStream;
import rebound.io.streaming.util.decorators.AbstractStreamDecorator;
import rebound.io.streaming.util.decorators.AbstractWriteStreamDecorator;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.objectutil.Trimmable;


public class PrimitiveConverterStreams
{
	/* <<<
	python
	
	
	s = """
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteTo_$$Prim$$__$$E$$_
	extends AbstractDecoratorStream<ByteBlockReadStream>
	implements _$$Prim$$_BlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteTo_$$Prim$$__$$E$$_(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * _$$primlen$$_;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, _$$primlen$$_);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public _$$prim$$_ read() throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			
			int byteLength = _$$primlen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.get_$$Endianness$$__$$Prim$$_(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * _$$primlen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, _$$primlen$$_);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.get_$$Endianness$$__$$Prim$$_(tempBuffer, i * _$$primlen$$_);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStream_$$Prim$$_ToByte_$$E$$_
	extends AbstractDecoratorWriteStream<ByteBlockWriteStream>
	implements _$$Prim$$_BlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStream_$$Prim$$_ToByte_$$E$$_(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return eof || underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * _$$primlen$$_;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = underlying.skip(r);
			if (a < r)
				eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, _$$primlen$$_);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(_$$prim$$_ unit) throws EOFException, IOException, ClosedStreamException
		{
			if (eof)
				throw new EOFException();
			
			int byteLength = _$$primlen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.put_$$Endianness$$__$$Prim$$_(tempBuffer, _$$primlen$$_, unit);
			
			
			
			int a = underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (eof)
				return 0;
			
			int byteLength = length * _$$primlen$$_;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			

			
			for (int i = 0; i < length; i++)
			{
				Bytes.put_$$Endianness$$__$$Prim$$_(tempBuffer, i * _$$primlen$$_, buffer[offset+i]);
			}
			
			
			
			int a = underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, _$$primlen$$_);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	"""
	
	
	
	
	
	prims = [
		["char", "char", 16],
		["short", "short", 16],
		#["int24", "int", 24],
		["int", "int", 32],
		#["long40", "long", 40],
		#["long48", "long", 48],
		#["long56", "long", 56],
		["long", "long", 64],
		["float", "float", 32],
		["double", "double", 64],
	]
	
	
	for logiprim, physprim, bitlen in prims:
		Logiprim = logiprim.capitalize()
		Physprim = physprim.capitalize()
		bytelen = bitlen / 8
		
		for endianness in ["little", "big"]:
			Endianness = endianness.capitalize()
			E = Endianness[0]+"E"
			
			r = s
			r = r.replace("_$$E$$_", E)
			r = r.replace("_$$Endianness$$_", Endianness)
			r = r.replace("_$$Prim$$_", Physprim)
			r = r.replace("_$$prim$$_", physprim)
			r = r.replace("_$$primlen$$_", str(bytelen))
			
			p(r)
	 */
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToCharLE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements CharBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToCharLE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getLittleChar(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getLittleChar(tempBuffer, i * 2);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamCharToByteLE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements CharBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamCharToByteLE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(char unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putLittleChar(tempBuffer, 2, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putLittleChar(tempBuffer, i * 2, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToCharBE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements CharBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToCharBE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public char read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getBigChar(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getBigChar(tempBuffer, i * 2);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamCharToByteBE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements CharBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamCharToByteBE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(char unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putBigChar(tempBuffer, 2, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(char[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putBigChar(tempBuffer, i * 2, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToShortLE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToShortLE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getLittleShort(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getLittleShort(tempBuffer, i * 2);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamShortToByteLE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements ShortBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamShortToByteLE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(short unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putLittleShort(tempBuffer, 2, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putLittleShort(tempBuffer, i * 2, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToShortBE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements ShortBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToShortBE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public short read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getBigShort(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getBigShort(tempBuffer, i * 2);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamShortToByteBE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements ShortBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamShortToByteBE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 2;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(short unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putBigShort(tempBuffer, 2, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(short[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 2;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putBigShort(tempBuffer, i * 2, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 2);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToIntLE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToIntLE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getLittleInt(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getLittleInt(tempBuffer, i * 4);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamIntToByteLE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements IntBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamIntToByteLE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(int unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putLittleInt(tempBuffer, 4, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putLittleInt(tempBuffer, i * 4, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToIntBE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements IntBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToIntBE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public int read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getBigInt(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getBigInt(tempBuffer, i * 4);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamIntToByteBE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements IntBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamIntToByteBE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(int unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putBigInt(tempBuffer, 4, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(int[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putBigInt(tempBuffer, i * 4, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToLongLE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToLongLE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getLittleLong(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getLittleLong(tempBuffer, i * 8);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamLongToByteLE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements LongBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamLongToByteLE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(long unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putLittleLong(tempBuffer, 8, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putLittleLong(tempBuffer, i * 8, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToLongBE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements LongBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToLongBE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public long read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getBigLong(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getBigLong(tempBuffer, i * 8);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamLongToByteBE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements LongBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamLongToByteBE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(long unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putBigLong(tempBuffer, 8, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(long[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putBigLong(tempBuffer, i * 8, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToFloatLE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToFloatLE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getLittleFloat(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getLittleFloat(tempBuffer, i * 4);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamFloatToByteLE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements FloatBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamFloatToByteLE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(float unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putLittleFloat(tempBuffer, 4, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putLittleFloat(tempBuffer, i * 4, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToFloatBE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements FloatBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToFloatBE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public float read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getBigFloat(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getBigFloat(tempBuffer, i * 4);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamFloatToByteBE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements FloatBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamFloatToByteBE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 4;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(float unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putBigFloat(tempBuffer, 4, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(float[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 4;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putBigFloat(tempBuffer, i * 4, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 4);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToDoubleLE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToDoubleLE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getLittleDouble(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getLittleDouble(tempBuffer, i * 8);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamDoubleToByteLE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements DoubleBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamDoubleToByteLE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(double unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putLittleDouble(tempBuffer, 8, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putLittleDouble(tempBuffer, i * 8, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterReadStreamByteToDoubleBE
	extends AbstractStreamDecorator<ByteBlockReadStream>
	implements DoubleBlockReadStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterReadStreamByteToDoubleBE(ByteBlockReadStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
		}
		
		
		
		
		@Override
		public double read() throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
			else
			{
				return Bytes.getBigDouble(tempBuffer, 0);
			}
		}
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			int a = this.underlying.read(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullRead = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we read the last byte fully if the last one is the one we partially read and stopped at!!!
			
			for (int i = 0; i < fullRead; i++)
			{
				buffer[i] = Bytes.getBigDouble(tempBuffer, i * 8);
			}
			
			return fullRead;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	
	
	
	public static class PrimitiveConverterWriteStreamDoubleToByteBE
	extends AbstractWriteStreamDecorator<ByteBlockWriteStream>
	implements DoubleBlockWriteStream, Trimmable
	{
		protected boolean eof;  //us keeping our own 'eof' is important so we don't read partial bytes in case become eof, then cease to be eof (eg, the underlying store expands!!)
		
		public PrimitiveConverterWriteStreamDoubleToByteBE(ByteBlockWriteStream underlying)
		{
			super(underlying);
		}
		
		@Override
		public boolean isEOF() throws IOException, ClosedStreamException
		{
			return this.eof || this.underlying.isEOF();
		}
		
		
		
		@Override
		public long skip(long amount) throws IOException, ClosedStreamException, IllegalArgumentException
		{
			long r = amount * 8;  //todo: check for overflow.     ..but when is that gonna happen with a long!? XDD
			long a = this.underlying.skip(r);
			if (a < r)
				this.eof = true;  //don't read partial bytes if they cease to be eof (eg, the underlying store expands!!)
			
			return SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
		}
		
		
		
		
		
		
		
		
		@Override
		public void write(double unit) throws EOFException, IOException, ClosedStreamException
		{
			if (this.eof)
				throw new EOFException();
			
			int byteLength = 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			Bytes.putBigDouble(tempBuffer, 8, unit);
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
			{
				this.eof = true;
				throw new EOFException();
			}
		}
		
		
		
		
		
		
		
		protected byte[] tempBuffer;
		
		@Override
		public int write(double[] buffer, int offset, int length) throws IOException, ClosedStreamException
		{
			if (this.eof)
				return 0;
			
			int byteLength = length * 8;
			
			
			byte[] tempBuffer = this.tempBuffer;
			
			if (tempBuffer == null || tempBuffer.length < byteLength)
			{
				tempBuffer = new byte[byteLength];
				this.tempBuffer = tempBuffer;
			}
			
			
			
			for (int i = 0; i < length; i++)
			{
				Bytes.putBigDouble(tempBuffer, i * 8, buffer[offset+i]);
			}
			
			
			
			int a = this.underlying.write(tempBuffer, 0, byteLength);
			
			if (a < byteLength)
				this.eof = true;
			
			int fullWritten = SmallIntegerMathUtilities.floorDivision(a, 8);  //partial bytes only happen on eof, so do floor not ceiling so it won't look like we wrote the last byte fully if the last one is the one we partially written and stopped at!!!
			
			
			return fullWritten;
		}
		
		
		
		
		@Override
		public TrimmableTrimRV couldYouMaybeUseALittleLessMemoryIfYouDontMind()
		{
			this.tempBuffer = null;
			
			return TrimmableTrimRV.DontKeepInvoking;
		}
	}
	
	
	
	
	
	
	//>>>
}
