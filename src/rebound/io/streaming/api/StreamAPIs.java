package rebound.io.streaming.api;

import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnegative;
import rebound.annotations.hints.FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.util.collections.Slice;

public class StreamAPIs
{
	/**
	 * A {@link UnitReadStream} that deals in Objects.
	 * @author RProgrammer
	 */
	public static interface ReferenceUnitReadStream<D>
	extends UnitReadStream<D>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public D read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default D readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	
	
	/**
	 * A {@link UnitWriteStream} that deals in Objects.
	 * @author RProgrammer
	 */
	public static interface ReferenceUnitWriteStream<D>
	extends UnitWriteStream<D>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(D unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(D unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * A {@link BlockReadStream} that deals in Objects.
	 * @author RProgrammer
	 */
	public static interface ReferenceBlockReadStream<D>
	extends BlockReadStream<D, D[]>, ReferenceUnitReadStream<D>
	{
		/**
		 * See {@link BlockReadStream}
		 * Note: The buffer may be of a supertype (eg, an Object[])
		 */
		@Nonnegative
		public int read(D[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(D[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<D[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	/**
	 * A {@link BlockWriteStream} that deals in Objects.
	 * @author RProgrammer
	 */
	public static interface ReferenceBlockWriteStream<D>
	extends BlockWriteStream<D, D[]>, ReferenceUnitWriteStream<D>
	{
		/**
		 * See {@link BlockWriteStream}
		 * Note: The buffer may be of a supertype (eg, an Object[])
		 */
		@Nonnegative
		public int write(D[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(D[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<D[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	
	
	
	
	
	public static interface _$$Prim$$_UnitReadStream
	extends UnitReadStream<_$$Primitive$$_>
	{
		/**
	 * See {@link UnitReadStream}
		 ⎋a/
		public _$$prim$$_ read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default _$$Primitive$$_ readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface _$$Prim$$_UnitWriteStream
	extends UnitWriteStream<_$$Primitive$$_>
	{
		/**
	 * See {@link UnitWriteStream}
		 ⎋a/
		public void write(_$$prim$$_ unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(_$$Primitive$$_ unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
 	
	
	
	public static interface _$$Prim$$_BlockReadStream
	extends BlockReadStream<_$$Primitive$$_, _$$prim$$_[]>, _$$Prim$$_UnitReadStream
	{
		/**
	 * See {@link BlockReadStream}
		 ⎋a/
		@Nonnegative
		public int read(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(_$$prim$$_[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<_$$prim$$_[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface _$$Prim$$_BlockWriteStream
	extends BlockWriteStream<_$$Primitive$$_, _$$prim$$_[]>, _$$Prim$$_UnitWriteStream
	{
		/**
	 * See {@link BlockWriteStream}
		 ⎋a/
		@Nonnegative
		public int write(_$$prim$$_[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(_$$prim$$_[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<_$$prim$$_[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	 */
	
	
	
	
	
	
	
	
	public static interface BooleanUnitReadStream
	extends UnitReadStream<Boolean>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public boolean read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Boolean readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface BooleanUnitWriteStream
	extends UnitWriteStream<Boolean>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(boolean unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Boolean unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface BooleanBlockReadStream
	extends BlockReadStream<Boolean, boolean[]>, BooleanUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(boolean[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<boolean[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface BooleanBlockWriteStream
	extends BlockWriteStream<Boolean, boolean[]>, BooleanUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(boolean[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(boolean[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<boolean[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface ByteUnitReadStream
	extends UnitReadStream<Byte>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public byte read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Byte readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface ByteUnitWriteStream
	extends UnitWriteStream<Byte>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(byte unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Byte unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface ByteBlockReadStream
	extends BlockReadStream<Byte, byte[]>, ByteUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(byte[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<byte[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface ByteBlockWriteStream
	extends BlockWriteStream<Byte, byte[]>, ByteUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(byte[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(byte[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<byte[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface CharUnitReadStream
	extends UnitReadStream<Character>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public char read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Character readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface CharUnitWriteStream
	extends UnitWriteStream<Character>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(char unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Character unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface CharBlockReadStream
	extends BlockReadStream<Character, char[]>, CharUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(char[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(char[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<char[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface CharBlockWriteStream
	extends BlockWriteStream<Character, char[]>, CharUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(char[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(char[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<char[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface ShortUnitReadStream
	extends UnitReadStream<Short>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public short read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Short readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface ShortUnitWriteStream
	extends UnitWriteStream<Short>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(short unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Short unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface ShortBlockReadStream
	extends BlockReadStream<Short, short[]>, ShortUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(short[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(short[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<short[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface ShortBlockWriteStream
	extends BlockWriteStream<Short, short[]>, ShortUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(short[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(short[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<short[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface FloatUnitReadStream
	extends UnitReadStream<Float>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public float read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Float readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface FloatUnitWriteStream
	extends UnitWriteStream<Float>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(float unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Float unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface FloatBlockReadStream
	extends BlockReadStream<Float, float[]>, FloatUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(float[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(float[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<float[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface FloatBlockWriteStream
	extends BlockWriteStream<Float, float[]>, FloatUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(float[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(float[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<float[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface IntUnitReadStream
	extends UnitReadStream<Integer>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public int read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Integer readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface IntUnitWriteStream
	extends UnitWriteStream<Integer>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(int unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Integer unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface IntBlockReadStream
	extends BlockReadStream<Integer, int[]>, IntUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(int[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(int[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<int[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface IntBlockWriteStream
	extends BlockWriteStream<Integer, int[]>, IntUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(int[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(int[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<int[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface DoubleUnitReadStream
	extends UnitReadStream<Double>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public double read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Double readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface DoubleUnitWriteStream
	extends UnitWriteStream<Double>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(double unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Double unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface DoubleBlockReadStream
	extends BlockReadStream<Double, double[]>, DoubleUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(double[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(double[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<double[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface DoubleBlockWriteStream
	extends BlockWriteStream<Double, double[]>, DoubleUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(double[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(double[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<double[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static interface LongUnitReadStream
	extends UnitReadStream<Long>
	{
		/**
		 * See {@link UnitReadStream}
		 */
		public long read() throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default Long readPossiblyBoxing() throws EOFException, IOException, ClosedStreamException
		{
			return read();
		}
	}
	
	public static interface LongUnitWriteStream
	extends UnitWriteStream<Long>
	{
		/**
		 * See {@link UnitWriteStream}
		 */
		public void write(long unit) throws EOFException, IOException, ClosedStreamException;
		
		@Override
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default void writePossiblyUnboxing(Long unit) throws EOFException, IOException, ClosedStreamException
		{
			write(unit);
		}
	}
	
	
	
	
	
	public static interface LongBlockReadStream
	extends BlockReadStream<Long, long[]>, LongUnitReadStream
	{
		/**
		 * See {@link BlockReadStream}
		 */
		@Nonnegative
		public int read(long[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int read(long[] buffer) throws IOException, ClosedStreamException
		{
			return read(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int read(Slice<long[]> buffer) throws IOException, ClosedStreamException
		{
			return read(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	public static interface LongBlockWriteStream
	extends BlockWriteStream<Long, long[]>, LongUnitWriteStream
	{
		/**
		 * See {@link BlockWriteStream}
		 */
		@Nonnegative
		public int write(long[] buffer, int offset, int length) throws IOException, ClosedStreamException;
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		public default int write(long[] buffer) throws IOException, ClosedStreamException
		{
			return write(buffer, 0, buffer.length);
		}
		
		
		@FinalButForDeserializationOrUnofficialInitializerOrInterfaceLimitations
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public default int write(Slice<long[]> buffer) throws IOException, ClosedStreamException
		{
			return write(buffer.getUnderlying(), buffer.getOffset(), buffer.getLength());
		}
	}
	
	
	
	
	
	
	
	
	
	
	//>>>
}
