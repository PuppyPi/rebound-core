package rebound.io.streaming.util.usagehelp;

import java.io.IOException;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.BooleanListBackedWriteStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.ByteListBackedWriteStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.CharacterListBackedWriteStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.DoubleListBackedWriteStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.FloatListBackedWriteStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.IntegerListBackedWriteStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.LongListBackedWriteStream;
import rebound.io.streaming.impls.memory.ArrayBackedStreams.ShortListBackedWriteStream;

public class StreamUsageReadAllUtilities
{
	/* <<<
	primxp
	
	public static _$$prim$$_[] readAll(_$$Prim$$_BlockReadStream in) throws IOException
	{
		_$$Primitive$$_ListBackedWriteStream buff = ArrayBackedStreams.newVariableLength_$$Prim$$_ArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.to_$$Prim$$_ArrayPossiblyLive();
	}
	 */
	
	public static boolean[] readAll(BooleanBlockReadStream in) throws IOException
	{
		BooleanListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthBooleanArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toBooleanArrayPossiblyLive();
	}
	
	public static byte[] readAll(ByteBlockReadStream in) throws IOException
	{
		ByteListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthByteArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toByteArrayPossiblyLive();
	}
	
	public static char[] readAll(CharBlockReadStream in) throws IOException
	{
		CharacterListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthCharArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toCharArrayPossiblyLive();
	}
	
	public static short[] readAll(ShortBlockReadStream in) throws IOException
	{
		ShortListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthShortArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toShortArrayPossiblyLive();
	}
	
	public static float[] readAll(FloatBlockReadStream in) throws IOException
	{
		FloatListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthFloatArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toFloatArrayPossiblyLive();
	}
	
	public static int[] readAll(IntBlockReadStream in) throws IOException
	{
		IntegerListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthIntArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toIntArrayPossiblyLive();
	}
	
	public static double[] readAll(DoubleBlockReadStream in) throws IOException
	{
		DoubleListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthDoubleArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toDoubleArrayPossiblyLive();
	}
	
	public static long[] readAll(LongBlockReadStream in) throws IOException
	{
		LongListBackedWriteStream buff = ArrayBackedStreams.newVariableLengthLongArrayBackedWriteStream();
		StreamUsageUtilities.pumpToInputEOF(in, buff);
		return buff.toLongArrayPossiblyLive();
	}
	
	// >>>
}
