/*
 * Created on Sep 14, 2004
 * Rewritten using python and infilegen on Jun 30, 2014
 */

package rebound.bits;

import static rebound.util.BasicExceptionUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import rebound.io.iio.InputByteStream;
import rebound.io.iio.OutputByteStream;
import rebound.io.iio.RandomAccessBytes;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.objectutil.JavaNamespace;

//Todo Add a suite of the methods for the rebound.io.streaming API when/ifffff that's ever finisheddd! X'DD
//Todo add documentation making it clear that the bytesize-multiplexed methods do UNsigned upcasting from all the sizes to 64bit long's!  \:DD/

/**
 * Utility methods for getting/putting, packing/unpacking, reading/relaying primitive values of all multiples of 8 bits up to 64 (long/double), in/out of
 * 	• byte[]s
 * 	• {@link ByteBuffer}s
 * 	• {@link InputStream}s / {@link OutputStream}s
 * 	• {@link InputByteStream}s / {@link OutputByteStream}s  (including, eg, {@link RandomAccessBytes}! :DD )
 * 
 * \:DDD/
 */
public class Bytes
implements JavaNamespace
{
	//Java.IO (SIO)  :D
	public static byte getByte(InputStream source) throws IOException, EOFException
	{
		int v = source.read();
		
		if (v == -1)
			throw new EOFException();
		
		return (byte)v;
	}
	
	public static byte getByte(InputByteStream source) throws IOException, EOFException
	{
		int v = source.read();
		
		if (v == -1)
			throw new EOFException();
		
		return (byte)v;
	}
	
	//RIO :D
	public static byte getByte(ByteBlockReadStream source) throws IOException, EOFException
	{
		int v = source.read();
		
		if (v == -1)
			throw new EOFException();
		
		return (byte)v;
	}
	
	
	
	
	
	/* <<<
python

prims = [
	["char", "char", 16],
	["short", "short", 16],
	["int24", "int", 24],
	["int", "int", 32],
	["long40", "long", 40],
	["long48", "long", 48],
	["long56", "long", 56],
	["long", "long", 64],
]

allprims = prims + [
	["float", "float", 32],
	["double", "double", 64],
];

def isnew(logiprim):
	return logiprim[-1].isdigit();



apis = [
[  [ [["byte[]", "source"]], None, "source[byteIndex]"],                                     [ [["byte[]", "dest"]], None, "dest[byteIndex] = %"]                                   ],
[  [ [["byte[]", "source"], ["int", "offset"]], None, "source[offset+byteIndex]"],           [ [["byte[]", "dest"], ["int", "offset"]], None, "dest[offset+byteIndex] = %"]         ],
[  [ [["Slice<byte[]>", "source"], ["int", "offset"]], None, "source.getUnderlying()[source.getOffset()+offset+byteIndex]"],           [ [["Slice<byte[]>", "dest"], ["int", "offset"]], None, "dest.getUnderlying()[dest.getOffset()+offset+byteIndex] = %"]         ],
[  [ [["ByteList", "source"], ["int", "offset"]], None, "source.getByte(offset+byteIndex)"],           [ [["ByteList", "dest"], ["int", "offset"]], None, "dest.setByte(offset+byteIndex, %)"]         ],
[  None,                                                                                               [ [["ByteList", "dest"]], None, "dest.addByte(%)"]         ],
[  [ [["ByteBuffer", "source"]], None, "source.get(byteIndex)"],                             [ [["ByteBuffer", "dest"]], None, "dest.put(byteIndex, %)"]                            ],
[  [ [["ByteBuffer", "source"], ["int", "offset"]], None, "source.get(offset+byteIndex)"],   [ [["ByteBuffer", "dest"], ["int", "offset"]], None, "dest.put(offset+byteIndex, %)"]  ],
[  [ [["InputStream", "source"]], "IOException, EOFException", "getByte(source)"],           [ [["OutputStream", "dest"]], "IOException, EOFException", "dest.write(%)"]            ],
[  [ [["InputByteStream", "source"]], "IOException, EOFException", "getByte(source)"],  [ [["OutputByteStream", "dest"]], "IOException, EOFException", "dest.write(%)"]            ],
[  [ [["ByteBlockReadStream", "source"]], "IOException, EOFException", "getByte(source)"],   [ [["ByteBlockWriteStream", "dest"]], "IOException, EOFException", "dest.write(%)"]            ],
];

iapis = filter(lambda a: a != None, map(lambda (inform, outform): inform, apis));
oapis = filter(lambda a: a != None, map(lambda (inform, outform): outform, apis));



def argslist(args):
	return ", ".join(map(lambda (t, n): n, args));

def argsdecl(args):
	return ", ".join(map(lambda (t, n): t+" "+n, args));







s = "";

_s = "";   #for disabling certain blocks ^_~




# Core functions! ^w^

for littleEndian in [True, False]:
	for logiprim, physprim, bitlen in prims:
		clogiprim = capitalize(logiprim);
		
		for args, throws, expr in iapis:
			s += """
			public static """+physprim+" get"+("Little" if littleEndian else "Big")+clogiprim+"("+argsdecl(args)+")"+(" throws "+throws if throws != None else "")+"""
			{
				"""+physprim+""" rv = 0;\n""";
			for byteIndex in range(bitlen/8):
				s += "rv |= (("+expr.replace("byteIndex", str(byteIndex))+") & 0xFF"+("l" if bitlen > 32 else "")+") << "+str( (byteIndex*8) if littleEndian else ((bitlen/8 - byteIndex - 1)*8) )+";\n";
			s += """return rv;
			}
			""";
		
		for args, throws, expr in oapis:
			s += """
			public static void put"""+("Little" if littleEndian else "Big")+clogiprim+"("+argsdecl(args+[[physprim, "value"]])+")"+(" throws "+throws if throws != None else "")+"""
			{
			""";
			for byteIndex in range(bitlen/8):
				extractExpr = "(value >>> "+str( (byteIndex*8) if littleEndian else ((bitlen/8 - byteIndex - 1)*8) )+") & 0xFF";
				s += expr.replace("byteIndex", str(byteIndex)).replace("%", "(byte)("+extractExpr+")")+";\n";
			s += """}
			""";

s += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";




# IEEE754 Floating point converters! :D

for littleEndian in [True, False]:
	for floatprim, bitprim in [["float", "int"], ["double", "long"]]:
		
		for args, throws, expr in iapis:
			s += """
			public static """+floatprim+" get"+("Little" if littleEndian else "Big")+capitalize(floatprim)+"("+argsdecl(args)+")"+(" throws "+throws if throws != None else "")+"""
			{
				return """+capitalize(floatprim)+"."+bitprim+"BitsTo"+capitalize(floatprim)+"(get"+("Little" if littleEndian else "Big")+capitalize(bitprim)+"("+argslist(args)+"""));
			}
			""";
		
		for args, throws, expr in oapis:
			s += """
			public static void put"""+("Little" if littleEndian else "Big")+capitalize(floatprim)+"("+argsdecl(args+[[floatprim, "value"]])+")"+(" throws "+throws if throws != None else "")+"""
			{
				put"""+("Little" if littleEndian else "Big")+capitalize(bitprim)+"("+argslist(args+[[bitprim, capitalize(floatprim)+"."+floatprim+"ToRaw"+capitalize(bitprim)+"Bits"+"(value)"]])+""");
			}
			""";
#

s += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";





# Runtime-endianness specification conveniences! \o/

for logiprim, physprim, bitlen in allprims:
		clogiprim = capitalize(logiprim);
		
		for args, throws, expr in iapis:
			s += """
				public static """+physprim+" get"+clogiprim+"("+argsdecl(args+[["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
				{
					if (endianness == Endianness.Little)
						return getLittle"""+clogiprim+"("+argslist(args)+""");
					else if (endianness == Endianness.Big)
						return getBig"""+clogiprim+"("+argslist(args)+""");
					else
						throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
				}
			""";
		
		for args, throws, expr in oapis:
			s += """
				public static void put"""+clogiprim+"("+argsdecl(args+[[physprim, "value"], ["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
				{
					if (endianness == Endianness.Little)
						putLittle"""+clogiprim+"("+argslist(args+[[physprim, "value"]])+""");
					else if (endianness == Endianness.Big)
						putBig"""+clogiprim+"("+argslist(args+[[physprim, "value"]])+""");
					else
						throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
				}
			""";
		
		s += """
			public static byte[] pack"""+clogiprim+"("+argsdecl([[physprim, "value"], ["Endianness", "endianness"]])+""")
			{
				if (endianness == Endianness.Little)
					return packLittle"""+clogiprim+"("+argslist([[physprim, "value"]])+""");
				else if (endianness == Endianness.Big)
					return packBig"""+clogiprim+"("+argslist([[physprim, "value"]])+""");
				else
					throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
			}
		""";

s += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";





# Array nicenesses! :D

for e in ["Little", "Big"]:
	for logiprim, physprim, bitlen in allprims:
		clogiprim = capitalize(logiprim);
		s += """
			public static byte[] pack"""+e+clogiprim+"("+physprim+""" value)
			{
				byte[] dest = new byte["""+str(bitlen / 8)+"""];
				put"""+e+clogiprim+"""(dest, value);
				return dest;
			}
		""";







# Dynamic number of bytes!

for args, throws, expr in iapis:
	s += """
		public static long getLittle("""+argsdecl(args+[["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					return """+(expr.replace("byteIndex", "0"))+""" & 0xFFl;
				case 2:
					return getLittleShort("""+argslist(args)+""") & 0xFFFFl;
				case 3:
					return getLittleInt24("""+argslist(args)+""") & 0xFF_FFFFl;
				case 4:
					return getLittleInt("""+argslist(args)+""") & 0xFFFF_FFFFl;
				case 5:
					return getLittleLong40("""+argslist(args)+""") & 0xFF_FFFF_FFFFl;
				case 6:
					return getLittleLong48("""+argslist(args)+""") & 0xFFFF_FFFF_FFFFl;
				case 7:
					return getLittleLong56("""+argslist(args)+""") & 0xFF_FFFF_FFFF_FFFFl;
				case 8:
					return getLittleLong("""+argslist(args)+""");
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static long getBig("""+argsdecl(args+[["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					return """+(expr.replace("byteIndex", "0"))+""" & 0xFFl;
				case 2:
					return getBigShort("""+argslist(args)+""") & 0xFFFFl;
				case 3:
					return getBigInt24("""+argslist(args)+""") & 0xFF_FFFFl;
				case 4:
					return getBigInt("""+argslist(args)+""") & 0xFFFF_FFFFl;
				case 5:
					return getBigLong40("""+argslist(args)+""") & 0xFF_FFFF_FFFFl;
				case 6:
					return getBigLong48("""+argslist(args)+""") & 0xFFFF_FFFF_FFFFl;
				case 7:
					return getBigLong56("""+argslist(args)+""") & 0xFF_FFFF_FFFF_FFFFl;
				case 8:
					return getBigLong("""+argslist(args)+""");
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static long get("""+argsdecl(args+[["int", "numberOfBytes"], ["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			if (endianness == Endianness.Little)
				return getLittle("""+argslist(args+[["int", "numberOfBytes"]])+""");
			else if (endianness == Endianness.Big)
				return getBig("""+argslist(args+[["int", "numberOfBytes"]])+""");
			else
				throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
		}
	""";

for args, throws, expr in oapis:
	s += """
		public static void putLittle("""+argsdecl(args+[["long", "value"], ["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					"""+(expr.replace("byteIndex", "0").replace("%", "(byte)value"))+"""; break;
				case 2:
					putLittleShort("""+argslist(args)+""", (short)value); break;
				case 3:
					putLittleInt24("""+argslist(args)+""", (int)value); break;
				case 4:
					putLittleInt("""+argslist(args)+""", (int)value); break;
				case 5:
					putLittleLong40("""+argslist(args)+""", (long)value); break;
				case 6:
					putLittleLong48("""+argslist(args)+""", (long)value); break;
				case 7:
					putLittleLong56("""+argslist(args)+""", (long)value); break;
				case 8:
					putLittleLong("""+argslist(args)+""", (long)value); break;
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static void putBig("""+argsdecl(args+[["long", "value"], ["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					"""+(expr.replace("byteIndex", "0").replace("%", "(byte)value"))+"""; break;
				case 2:
					putBigShort("""+argslist(args)+""", (short)value); break;
				case 3:
					putBigInt24("""+argslist(args)+""", (int)value); break;
				case 4:
					putBigInt("""+argslist(args)+""", (int)value); break;
				case 5:
					putBigLong40("""+argslist(args)+""", (long)value); break;
				case 6:
					putBigLong48("""+argslist(args)+""", (long)value); break;
				case 7:
					putBigLong56("""+argslist(args)+""", (long)value); break;
				case 8:
					putBigLong("""+argslist(args)+""", (long)value); break;
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static void put("""+argsdecl(args+[["long", "value"], ["int", "numberOfBytes"], ["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			if (endianness == Endianness.Little)
				putLittle("""+argslist(args+[["long", "value"], ["int", "numberOfBytes"]])+""");
			else if (endianness == Endianness.Big)
				putBig("""+argslist(args+[["long", "value"], ["int", "numberOfBytes"]])+""");
			else
				throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
		}
	""";



s += """
	public static byte[] packLittle("""+argsdecl([["long", "value"], ["int", "numberOfBytes"]])+""")
	{
		byte[] dest = new byte[numberOfBytes];
		putLittle(dest, value, numberOfBytes);
		return dest;
	}
	
	public static byte[] packBig("""+argsdecl([["long", "value"], ["int", "numberOfBytes"]])+""")
	{
		byte[] dest = new byte[numberOfBytes];
		putBig(dest, value, numberOfBytes);
		return dest;
	}
	
	
	public static byte[] pack("""+argsdecl([["long", "value"], ["int", "numberOfBytes"], ["Endianness", "endianness"]])+""")
	{
		if (endianness == Endianness.Little)
			return packLittle("""+argslist([["long", "value"], ["int", "numberOfBytes"]])+""");
		else if (endianness == Endianness.Big)
			return packBig("""+argslist([["long", "value"], ["int", "numberOfBytes"]])+""");
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
""";




p(s);


	 */
	
	public static char getLittleChar(byte[] source)
	{
		char rv = 0;
		rv |= ((source[0]) & 0xFF) << 0;
		rv |= ((source[1]) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(byte[] source, int offset)
	{
		char rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 0;
		rv |= ((source[offset+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(Slice<byte[]> source, int offset)
	{
		char rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(ByteList source, int offset)
	{
		char rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(ByteBuffer source)
	{
		char rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 0;
		rv |= ((source.get(1)) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(ByteBuffer source, int offset)
	{
		char rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 0;
		rv |= ((source.get(offset+1)) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(InputStream source) throws IOException, EOFException
	{
		char rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(InputByteStream source) throws IOException, EOFException
	{
		char rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(ByteBlockReadStream source) throws IOException, EOFException
	{
		char rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		return rv;
	}
	
	public static void putLittleChar(byte[] dest, char value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleChar(byte[] dest, int offset, char value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleChar(Slice<byte[]> dest, int offset, char value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleChar(ByteList dest, int offset, char value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleChar(ByteList dest, char value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleChar(ByteBuffer dest, char value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleChar(ByteBuffer dest, int offset, char value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleChar(OutputStream dest, char value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleChar(OutputByteStream dest, char value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleChar(ByteBlockWriteStream dest, char value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
	}
	
	public static short getLittleShort(byte[] source)
	{
		short rv = 0;
		rv |= ((source[0]) & 0xFF) << 0;
		rv |= ((source[1]) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(byte[] source, int offset)
	{
		short rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 0;
		rv |= ((source[offset+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(Slice<byte[]> source, int offset)
	{
		short rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(ByteList source, int offset)
	{
		short rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(ByteBuffer source)
	{
		short rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 0;
		rv |= ((source.get(1)) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(ByteBuffer source, int offset)
	{
		short rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 0;
		rv |= ((source.get(offset+1)) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(InputStream source) throws IOException, EOFException
	{
		short rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(InputByteStream source) throws IOException, EOFException
	{
		short rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(ByteBlockReadStream source) throws IOException, EOFException
	{
		short rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		return rv;
	}
	
	public static void putLittleShort(byte[] dest, short value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleShort(byte[] dest, int offset, short value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleShort(Slice<byte[]> dest, int offset, short value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleShort(ByteList dest, int offset, short value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleShort(ByteList dest, short value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleShort(ByteBuffer dest, short value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleShort(ByteBuffer dest, int offset, short value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleShort(OutputStream dest, short value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleShort(OutputByteStream dest, short value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleShort(ByteBlockWriteStream dest, short value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
	}
	
	public static int getLittleInt24(byte[] source)
	{
		int rv = 0;
		rv |= ((source[0]) & 0xFF) << 0;
		rv |= ((source[1]) & 0xFF) << 8;
		rv |= ((source[2]) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(byte[] source, int offset)
	{
		int rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 0;
		rv |= ((source[offset+1]) & 0xFF) << 8;
		rv |= ((source[offset+2]) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(Slice<byte[]> source, int offset)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(ByteList source, int offset)
	{
		int rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(ByteBuffer source)
	{
		int rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 0;
		rv |= ((source.get(1)) & 0xFF) << 8;
		rv |= ((source.get(2)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(ByteBuffer source, int offset)
	{
		int rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 0;
		rv |= ((source.get(offset+1)) & 0xFF) << 8;
		rv |= ((source.get(offset+2)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(InputStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(InputByteStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleInt24(ByteBlockReadStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		return rv;
	}
	
	public static void putLittleInt24(byte[] dest, int value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
		dest[2] = (byte)((value >>> 16) & 0xFF);
	}
	
	public static void putLittleInt24(byte[] dest, int offset, int value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
		dest[offset+2] = (byte)((value >>> 16) & 0xFF);
	}
	
	public static void putLittleInt24(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
	}
	
	public static void putLittleInt24(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
	}
	
	public static void putLittleInt24(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
	}
	
	public static void putLittleInt24(ByteBuffer dest, int value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
		dest.put(2, (byte)((value >>> 16) & 0xFF));
	}
	
	public static void putLittleInt24(ByteBuffer dest, int offset, int value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 16) & 0xFF));
	}
	
	public static void putLittleInt24(OutputStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
	}
	
	public static void putLittleInt24(OutputByteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
	}
	
	public static void putLittleInt24(ByteBlockWriteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
	}
	
	public static int getLittleInt(byte[] source)
	{
		int rv = 0;
		rv |= ((source[0]) & 0xFF) << 0;
		rv |= ((source[1]) & 0xFF) << 8;
		rv |= ((source[2]) & 0xFF) << 16;
		rv |= ((source[3]) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(byte[] source, int offset)
	{
		int rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 0;
		rv |= ((source[offset+1]) & 0xFF) << 8;
		rv |= ((source[offset+2]) & 0xFF) << 16;
		rv |= ((source[offset+3]) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(Slice<byte[]> source, int offset)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFF) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(ByteList source, int offset)
	{
		int rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFF) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(ByteBuffer source)
	{
		int rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 0;
		rv |= ((source.get(1)) & 0xFF) << 8;
		rv |= ((source.get(2)) & 0xFF) << 16;
		rv |= ((source.get(3)) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(ByteBuffer source, int offset)
	{
		int rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 0;
		rv |= ((source.get(offset+1)) & 0xFF) << 8;
		rv |= ((source.get(offset+2)) & 0xFF) << 16;
		rv |= ((source.get(offset+3)) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(InputStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(InputByteStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 24;
		return rv;
	}
	
	public static int getLittleInt(ByteBlockReadStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 24;
		return rv;
	}
	
	public static void putLittleInt(byte[] dest, int value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
		dest[2] = (byte)((value >>> 16) & 0xFF);
		dest[3] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void putLittleInt(byte[] dest, int offset, int value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
		dest[offset+2] = (byte)((value >>> 16) & 0xFF);
		dest[offset+3] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void putLittleInt(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void putLittleInt(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
	}
	
	public static void putLittleInt(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
	}
	
	public static void putLittleInt(ByteBuffer dest, int value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
		dest.put(2, (byte)((value >>> 16) & 0xFF));
		dest.put(3, (byte)((value >>> 24) & 0xFF));
	}
	
	public static void putLittleInt(ByteBuffer dest, int offset, int value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 24) & 0xFF));
	}
	
	public static void putLittleInt(OutputStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
	}
	
	public static void putLittleInt(OutputByteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
	}
	
	public static void putLittleInt(ByteBlockWriteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
	}
	
	public static long getLittleLong40(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 0;
		rv |= ((source[1]) & 0xFFl) << 8;
		rv |= ((source[2]) & 0xFFl) << 16;
		rv |= ((source[3]) & 0xFFl) << 24;
		rv |= ((source[4]) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 0;
		rv |= ((source[offset+1]) & 0xFFl) << 8;
		rv |= ((source[offset+2]) & 0xFFl) << 16;
		rv |= ((source[offset+3]) & 0xFFl) << 24;
		rv |= ((source[offset+4]) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 0;
		rv |= ((source.get(1)) & 0xFFl) << 8;
		rv |= ((source.get(2)) & 0xFFl) << 16;
		rv |= ((source.get(3)) & 0xFFl) << 24;
		rv |= ((source.get(4)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 0;
		rv |= ((source.get(offset+1)) & 0xFFl) << 8;
		rv |= ((source.get(offset+2)) & 0xFFl) << 16;
		rv |= ((source.get(offset+3)) & 0xFFl) << 24;
		rv |= ((source.get(offset+4)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleLong40(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		return rv;
	}
	
	public static void putLittleLong40(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
		dest[2] = (byte)((value >>> 16) & 0xFF);
		dest[3] = (byte)((value >>> 24) & 0xFF);
		dest[4] = (byte)((value >>> 32) & 0xFF);
	}
	
	public static void putLittleLong40(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
		dest[offset+2] = (byte)((value >>> 16) & 0xFF);
		dest[offset+3] = (byte)((value >>> 24) & 0xFF);
		dest[offset+4] = (byte)((value >>> 32) & 0xFF);
	}
	
	public static void putLittleLong40(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 32) & 0xFF);
	}
	
	public static void putLittleLong40(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 32) & 0xFF));
	}
	
	public static void putLittleLong40(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
	}
	
	public static void putLittleLong40(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
		dest.put(2, (byte)((value >>> 16) & 0xFF));
		dest.put(3, (byte)((value >>> 24) & 0xFF));
		dest.put(4, (byte)((value >>> 32) & 0xFF));
	}
	
	public static void putLittleLong40(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 32) & 0xFF));
	}
	
	public static void putLittleLong40(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
	}
	
	public static void putLittleLong40(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
	}
	
	public static void putLittleLong40(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
	}
	
	public static long getLittleLong48(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 0;
		rv |= ((source[1]) & 0xFFl) << 8;
		rv |= ((source[2]) & 0xFFl) << 16;
		rv |= ((source[3]) & 0xFFl) << 24;
		rv |= ((source[4]) & 0xFFl) << 32;
		rv |= ((source[5]) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 0;
		rv |= ((source[offset+1]) & 0xFFl) << 8;
		rv |= ((source[offset+2]) & 0xFFl) << 16;
		rv |= ((source[offset+3]) & 0xFFl) << 24;
		rv |= ((source[offset+4]) & 0xFFl) << 32;
		rv |= ((source[offset+5]) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+5]) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+5)) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 0;
		rv |= ((source.get(1)) & 0xFFl) << 8;
		rv |= ((source.get(2)) & 0xFFl) << 16;
		rv |= ((source.get(3)) & 0xFFl) << 24;
		rv |= ((source.get(4)) & 0xFFl) << 32;
		rv |= ((source.get(5)) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 0;
		rv |= ((source.get(offset+1)) & 0xFFl) << 8;
		rv |= ((source.get(offset+2)) & 0xFFl) << 16;
		rv |= ((source.get(offset+3)) & 0xFFl) << 24;
		rv |= ((source.get(offset+4)) & 0xFFl) << 32;
		rv |= ((source.get(offset+5)) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleLong48(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		return rv;
	}
	
	public static void putLittleLong48(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
		dest[2] = (byte)((value >>> 16) & 0xFF);
		dest[3] = (byte)((value >>> 24) & 0xFF);
		dest[4] = (byte)((value >>> 32) & 0xFF);
		dest[5] = (byte)((value >>> 40) & 0xFF);
	}
	
	public static void putLittleLong48(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
		dest[offset+2] = (byte)((value >>> 16) & 0xFF);
		dest[offset+3] = (byte)((value >>> 24) & 0xFF);
		dest[offset+4] = (byte)((value >>> 32) & 0xFF);
		dest[offset+5] = (byte)((value >>> 40) & 0xFF);
	}
	
	public static void putLittleLong48(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+5] = (byte)((value >>> 40) & 0xFF);
	}
	
	public static void putLittleLong48(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+5, (byte)((value >>> 40) & 0xFF));
	}
	
	public static void putLittleLong48(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
	}
	
	public static void putLittleLong48(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
		dest.put(2, (byte)((value >>> 16) & 0xFF));
		dest.put(3, (byte)((value >>> 24) & 0xFF));
		dest.put(4, (byte)((value >>> 32) & 0xFF));
		dest.put(5, (byte)((value >>> 40) & 0xFF));
	}
	
	public static void putLittleLong48(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.put(offset+5, (byte)((value >>> 40) & 0xFF));
	}
	
	public static void putLittleLong48(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
	}
	
	public static void putLittleLong48(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
	}
	
	public static void putLittleLong48(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
	}
	
	public static long getLittleLong56(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 0;
		rv |= ((source[1]) & 0xFFl) << 8;
		rv |= ((source[2]) & 0xFFl) << 16;
		rv |= ((source[3]) & 0xFFl) << 24;
		rv |= ((source[4]) & 0xFFl) << 32;
		rv |= ((source[5]) & 0xFFl) << 40;
		rv |= ((source[6]) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 0;
		rv |= ((source[offset+1]) & 0xFFl) << 8;
		rv |= ((source[offset+2]) & 0xFFl) << 16;
		rv |= ((source[offset+3]) & 0xFFl) << 24;
		rv |= ((source[offset+4]) & 0xFFl) << 32;
		rv |= ((source[offset+5]) & 0xFFl) << 40;
		rv |= ((source[offset+6]) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+5]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+6]) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+5)) & 0xFFl) << 40;
		rv |= ((source.getByte(offset+6)) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 0;
		rv |= ((source.get(1)) & 0xFFl) << 8;
		rv |= ((source.get(2)) & 0xFFl) << 16;
		rv |= ((source.get(3)) & 0xFFl) << 24;
		rv |= ((source.get(4)) & 0xFFl) << 32;
		rv |= ((source.get(5)) & 0xFFl) << 40;
		rv |= ((source.get(6)) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 0;
		rv |= ((source.get(offset+1)) & 0xFFl) << 8;
		rv |= ((source.get(offset+2)) & 0xFFl) << 16;
		rv |= ((source.get(offset+3)) & 0xFFl) << 24;
		rv |= ((source.get(offset+4)) & 0xFFl) << 32;
		rv |= ((source.get(offset+5)) & 0xFFl) << 40;
		rv |= ((source.get(offset+6)) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleLong56(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		return rv;
	}
	
	public static void putLittleLong56(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
		dest[2] = (byte)((value >>> 16) & 0xFF);
		dest[3] = (byte)((value >>> 24) & 0xFF);
		dest[4] = (byte)((value >>> 32) & 0xFF);
		dest[5] = (byte)((value >>> 40) & 0xFF);
		dest[6] = (byte)((value >>> 48) & 0xFF);
	}
	
	public static void putLittleLong56(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
		dest[offset+2] = (byte)((value >>> 16) & 0xFF);
		dest[offset+3] = (byte)((value >>> 24) & 0xFF);
		dest[offset+4] = (byte)((value >>> 32) & 0xFF);
		dest[offset+5] = (byte)((value >>> 40) & 0xFF);
		dest[offset+6] = (byte)((value >>> 48) & 0xFF);
	}
	
	public static void putLittleLong56(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+5] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+6] = (byte)((value >>> 48) & 0xFF);
	}
	
	public static void putLittleLong56(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+5, (byte)((value >>> 40) & 0xFF));
		dest.setByte(offset+6, (byte)((value >>> 48) & 0xFF));
	}
	
	public static void putLittleLong56(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 48) & 0xFF));
	}
	
	public static void putLittleLong56(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
		dest.put(2, (byte)((value >>> 16) & 0xFF));
		dest.put(3, (byte)((value >>> 24) & 0xFF));
		dest.put(4, (byte)((value >>> 32) & 0xFF));
		dest.put(5, (byte)((value >>> 40) & 0xFF));
		dest.put(6, (byte)((value >>> 48) & 0xFF));
	}
	
	public static void putLittleLong56(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.put(offset+5, (byte)((value >>> 40) & 0xFF));
		dest.put(offset+6, (byte)((value >>> 48) & 0xFF));
	}
	
	public static void putLittleLong56(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
	}
	
	public static void putLittleLong56(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
	}
	
	public static void putLittleLong56(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
	}
	
	public static long getLittleLong(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 0;
		rv |= ((source[1]) & 0xFFl) << 8;
		rv |= ((source[2]) & 0xFFl) << 16;
		rv |= ((source[3]) & 0xFFl) << 24;
		rv |= ((source[4]) & 0xFFl) << 32;
		rv |= ((source[5]) & 0xFFl) << 40;
		rv |= ((source[6]) & 0xFFl) << 48;
		rv |= ((source[7]) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 0;
		rv |= ((source[offset+1]) & 0xFFl) << 8;
		rv |= ((source[offset+2]) & 0xFFl) << 16;
		rv |= ((source[offset+3]) & 0xFFl) << 24;
		rv |= ((source[offset+4]) & 0xFFl) << 32;
		rv |= ((source[offset+5]) & 0xFFl) << 40;
		rv |= ((source[offset+6]) & 0xFFl) << 48;
		rv |= ((source[offset+7]) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+5]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+6]) & 0xFFl) << 48;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+7]) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+5)) & 0xFFl) << 40;
		rv |= ((source.getByte(offset+6)) & 0xFFl) << 48;
		rv |= ((source.getByte(offset+7)) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 0;
		rv |= ((source.get(1)) & 0xFFl) << 8;
		rv |= ((source.get(2)) & 0xFFl) << 16;
		rv |= ((source.get(3)) & 0xFFl) << 24;
		rv |= ((source.get(4)) & 0xFFl) << 32;
		rv |= ((source.get(5)) & 0xFFl) << 40;
		rv |= ((source.get(6)) & 0xFFl) << 48;
		rv |= ((source.get(7)) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 0;
		rv |= ((source.get(offset+1)) & 0xFFl) << 8;
		rv |= ((source.get(offset+2)) & 0xFFl) << 16;
		rv |= ((source.get(offset+3)) & 0xFFl) << 24;
		rv |= ((source.get(offset+4)) & 0xFFl) << 32;
		rv |= ((source.get(offset+5)) & 0xFFl) << 40;
		rv |= ((source.get(offset+6)) & 0xFFl) << 48;
		rv |= ((source.get(offset+7)) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 56;
		return rv;
	}
	
	public static long getLittleLong(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 56;
		return rv;
	}
	
	public static void putLittleLong(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 0) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
		dest[2] = (byte)((value >>> 16) & 0xFF);
		dest[3] = (byte)((value >>> 24) & 0xFF);
		dest[4] = (byte)((value >>> 32) & 0xFF);
		dest[5] = (byte)((value >>> 40) & 0xFF);
		dest[6] = (byte)((value >>> 48) & 0xFF);
		dest[7] = (byte)((value >>> 56) & 0xFF);
	}
	
	public static void putLittleLong(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 0) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
		dest[offset+2] = (byte)((value >>> 16) & 0xFF);
		dest[offset+3] = (byte)((value >>> 24) & 0xFF);
		dest[offset+4] = (byte)((value >>> 32) & 0xFF);
		dest[offset+5] = (byte)((value >>> 40) & 0xFF);
		dest[offset+6] = (byte)((value >>> 48) & 0xFF);
		dest[offset+7] = (byte)((value >>> 56) & 0xFF);
	}
	
	public static void putLittleLong(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+5] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+6] = (byte)((value >>> 48) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+7] = (byte)((value >>> 56) & 0xFF);
	}
	
	public static void putLittleLong(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+5, (byte)((value >>> 40) & 0xFF));
		dest.setByte(offset+6, (byte)((value >>> 48) & 0xFF));
		dest.setByte(offset+7, (byte)((value >>> 56) & 0xFF));
	}
	
	public static void putLittleLong(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 48) & 0xFF));
		dest.addByte((byte)((value >>> 56) & 0xFF));
	}
	
	public static void putLittleLong(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 0) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
		dest.put(2, (byte)((value >>> 16) & 0xFF));
		dest.put(3, (byte)((value >>> 24) & 0xFF));
		dest.put(4, (byte)((value >>> 32) & 0xFF));
		dest.put(5, (byte)((value >>> 40) & 0xFF));
		dest.put(6, (byte)((value >>> 48) & 0xFF));
		dest.put(7, (byte)((value >>> 56) & 0xFF));
	}
	
	public static void putLittleLong(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.put(offset+5, (byte)((value >>> 40) & 0xFF));
		dest.put(offset+6, (byte)((value >>> 48) & 0xFF));
		dest.put(offset+7, (byte)((value >>> 56) & 0xFF));
	}
	
	public static void putLittleLong(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 56) & 0xFF));
	}
	
	public static void putLittleLong(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 56) & 0xFF));
	}
	
	public static void putLittleLong(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 0) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 56) & 0xFF));
	}
	
	public static char getBigChar(byte[] source)
	{
		char rv = 0;
		rv |= ((source[0]) & 0xFF) << 8;
		rv |= ((source[1]) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(byte[] source, int offset)
	{
		char rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 8;
		rv |= ((source[offset+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(Slice<byte[]> source, int offset)
	{
		char rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(ByteList source, int offset)
	{
		char rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(ByteBuffer source)
	{
		char rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 8;
		rv |= ((source.get(1)) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(ByteBuffer source, int offset)
	{
		char rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 8;
		rv |= ((source.get(offset+1)) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(InputStream source) throws IOException, EOFException
	{
		char rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(InputByteStream source) throws IOException, EOFException
	{
		char rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(ByteBlockReadStream source) throws IOException, EOFException
	{
		char rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static void putBigChar(byte[] dest, char value)
	{
		dest[0] = (byte)((value >>> 8) & 0xFF);
		dest[1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigChar(byte[] dest, int offset, char value)
	{
		dest[offset+0] = (byte)((value >>> 8) & 0xFF);
		dest[offset+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigChar(Slice<byte[]> dest, int offset, char value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigChar(ByteList dest, int offset, char value)
	{
		dest.setByte(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigChar(ByteList dest, char value)
	{
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigChar(ByteBuffer dest, char value)
	{
		dest.put(0, (byte)((value >>> 8) & 0xFF));
		dest.put(1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigChar(ByteBuffer dest, int offset, char value)
	{
		dest.put(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigChar(OutputStream dest, char value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigChar(OutputByteStream dest, char value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigChar(ByteBlockWriteStream dest, char value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static short getBigShort(byte[] source)
	{
		short rv = 0;
		rv |= ((source[0]) & 0xFF) << 8;
		rv |= ((source[1]) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(byte[] source, int offset)
	{
		short rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 8;
		rv |= ((source[offset+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(Slice<byte[]> source, int offset)
	{
		short rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(ByteList source, int offset)
	{
		short rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(ByteBuffer source)
	{
		short rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 8;
		rv |= ((source.get(1)) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(ByteBuffer source, int offset)
	{
		short rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 8;
		rv |= ((source.get(offset+1)) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(InputStream source) throws IOException, EOFException
	{
		short rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(InputByteStream source) throws IOException, EOFException
	{
		short rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(ByteBlockReadStream source) throws IOException, EOFException
	{
		short rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static void putBigShort(byte[] dest, short value)
	{
		dest[0] = (byte)((value >>> 8) & 0xFF);
		dest[1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigShort(byte[] dest, int offset, short value)
	{
		dest[offset+0] = (byte)((value >>> 8) & 0xFF);
		dest[offset+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigShort(Slice<byte[]> dest, int offset, short value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigShort(ByteList dest, int offset, short value)
	{
		dest.setByte(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigShort(ByteList dest, short value)
	{
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigShort(ByteBuffer dest, short value)
	{
		dest.put(0, (byte)((value >>> 8) & 0xFF));
		dest.put(1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigShort(ByteBuffer dest, int offset, short value)
	{
		dest.put(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigShort(OutputStream dest, short value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigShort(OutputByteStream dest, short value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigShort(ByteBlockWriteStream dest, short value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static int getBigInt24(byte[] source)
	{
		int rv = 0;
		rv |= ((source[0]) & 0xFF) << 16;
		rv |= ((source[1]) & 0xFF) << 8;
		rv |= ((source[2]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(byte[] source, int offset)
	{
		int rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 16;
		rv |= ((source[offset+1]) & 0xFF) << 8;
		rv |= ((source[offset+2]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(Slice<byte[]> source, int offset)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(ByteList source, int offset)
	{
		int rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 16;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(ByteBuffer source)
	{
		int rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 16;
		rv |= ((source.get(1)) & 0xFF) << 8;
		rv |= ((source.get(2)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(ByteBuffer source, int offset)
	{
		int rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 16;
		rv |= ((source.get(offset+1)) & 0xFF) << 8;
		rv |= ((source.get(offset+2)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(InputStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(InputByteStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt24(ByteBlockReadStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static void putBigInt24(byte[] dest, int value)
	{
		dest[0] = (byte)((value >>> 16) & 0xFF);
		dest[1] = (byte)((value >>> 8) & 0xFF);
		dest[2] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt24(byte[] dest, int offset, int value)
	{
		dest[offset+0] = (byte)((value >>> 16) & 0xFF);
		dest[offset+1] = (byte)((value >>> 8) & 0xFF);
		dest[offset+2] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt24(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt24(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt24(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt24(ByteBuffer dest, int value)
	{
		dest.put(0, (byte)((value >>> 16) & 0xFF));
		dest.put(1, (byte)((value >>> 8) & 0xFF));
		dest.put(2, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt24(ByteBuffer dest, int offset, int value)
	{
		dest.put(offset+0, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt24(OutputStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt24(OutputByteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt24(ByteBlockWriteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static int getBigInt(byte[] source)
	{
		int rv = 0;
		rv |= ((source[0]) & 0xFF) << 24;
		rv |= ((source[1]) & 0xFF) << 16;
		rv |= ((source[2]) & 0xFF) << 8;
		rv |= ((source[3]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(byte[] source, int offset)
	{
		int rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 24;
		rv |= ((source[offset+1]) & 0xFF) << 16;
		rv |= ((source[offset+2]) & 0xFF) << 8;
		rv |= ((source[offset+3]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(Slice<byte[]> source, int offset)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(ByteList source, int offset)
	{
		int rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 24;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 16;
		rv |= ((source.getByte(offset+2)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+3)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(ByteBuffer source)
	{
		int rv = 0;
		rv |= ((source.get(0)) & 0xFF) << 24;
		rv |= ((source.get(1)) & 0xFF) << 16;
		rv |= ((source.get(2)) & 0xFF) << 8;
		rv |= ((source.get(3)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(ByteBuffer source, int offset)
	{
		int rv = 0;
		rv |= ((source.get(offset+0)) & 0xFF) << 24;
		rv |= ((source.get(offset+1)) & 0xFF) << 16;
		rv |= ((source.get(offset+2)) & 0xFF) << 8;
		rv |= ((source.get(offset+3)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(InputStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 24;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(InputByteStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 24;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigInt(ByteBlockReadStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 24;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static void putBigInt(byte[] dest, int value)
	{
		dest[0] = (byte)((value >>> 24) & 0xFF);
		dest[1] = (byte)((value >>> 16) & 0xFF);
		dest[2] = (byte)((value >>> 8) & 0xFF);
		dest[3] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt(byte[] dest, int offset, int value)
	{
		dest[offset+0] = (byte)((value >>> 24) & 0xFF);
		dest[offset+1] = (byte)((value >>> 16) & 0xFF);
		dest[offset+2] = (byte)((value >>> 8) & 0xFF);
		dest[offset+3] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt(ByteBuffer dest, int value)
	{
		dest.put(0, (byte)((value >>> 24) & 0xFF));
		dest.put(1, (byte)((value >>> 16) & 0xFF));
		dest.put(2, (byte)((value >>> 8) & 0xFF));
		dest.put(3, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt(ByteBuffer dest, int offset, int value)
	{
		dest.put(offset+0, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt(OutputStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt(OutputByteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt(ByteBlockWriteStream dest, int value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static long getBigLong40(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 32;
		rv |= ((source[1]) & 0xFFl) << 24;
		rv |= ((source[2]) & 0xFFl) << 16;
		rv |= ((source[3]) & 0xFFl) << 8;
		rv |= ((source[4]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 32;
		rv |= ((source[offset+1]) & 0xFFl) << 24;
		rv |= ((source[offset+2]) & 0xFFl) << 16;
		rv |= ((source[offset+3]) & 0xFFl) << 8;
		rv |= ((source[offset+4]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 32;
		rv |= ((source.get(1)) & 0xFFl) << 24;
		rv |= ((source.get(2)) & 0xFFl) << 16;
		rv |= ((source.get(3)) & 0xFFl) << 8;
		rv |= ((source.get(4)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 32;
		rv |= ((source.get(offset+1)) & 0xFFl) << 24;
		rv |= ((source.get(offset+2)) & 0xFFl) << 16;
		rv |= ((source.get(offset+3)) & 0xFFl) << 8;
		rv |= ((source.get(offset+4)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong40(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static void putBigLong40(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 32) & 0xFF);
		dest[1] = (byte)((value >>> 24) & 0xFF);
		dest[2] = (byte)((value >>> 16) & 0xFF);
		dest[3] = (byte)((value >>> 8) & 0xFF);
		dest[4] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong40(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 32) & 0xFF);
		dest[offset+1] = (byte)((value >>> 24) & 0xFF);
		dest[offset+2] = (byte)((value >>> 16) & 0xFF);
		dest[offset+3] = (byte)((value >>> 8) & 0xFF);
		dest[offset+4] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong40(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong40(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong40(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong40(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 32) & 0xFF));
		dest.put(1, (byte)((value >>> 24) & 0xFF));
		dest.put(2, (byte)((value >>> 16) & 0xFF));
		dest.put(3, (byte)((value >>> 8) & 0xFF));
		dest.put(4, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong40(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 32) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong40(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong40(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong40(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static long getBigLong48(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 40;
		rv |= ((source[1]) & 0xFFl) << 32;
		rv |= ((source[2]) & 0xFFl) << 24;
		rv |= ((source[3]) & 0xFFl) << 16;
		rv |= ((source[4]) & 0xFFl) << 8;
		rv |= ((source[5]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 40;
		rv |= ((source[offset+1]) & 0xFFl) << 32;
		rv |= ((source[offset+2]) & 0xFFl) << 24;
		rv |= ((source[offset+3]) & 0xFFl) << 16;
		rv |= ((source[offset+4]) & 0xFFl) << 8;
		rv |= ((source[offset+5]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+5]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 40;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+5)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 40;
		rv |= ((source.get(1)) & 0xFFl) << 32;
		rv |= ((source.get(2)) & 0xFFl) << 24;
		rv |= ((source.get(3)) & 0xFFl) << 16;
		rv |= ((source.get(4)) & 0xFFl) << 8;
		rv |= ((source.get(5)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 40;
		rv |= ((source.get(offset+1)) & 0xFFl) << 32;
		rv |= ((source.get(offset+2)) & 0xFFl) << 24;
		rv |= ((source.get(offset+3)) & 0xFFl) << 16;
		rv |= ((source.get(offset+4)) & 0xFFl) << 8;
		rv |= ((source.get(offset+5)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong48(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static void putBigLong48(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 40) & 0xFF);
		dest[1] = (byte)((value >>> 32) & 0xFF);
		dest[2] = (byte)((value >>> 24) & 0xFF);
		dest[3] = (byte)((value >>> 16) & 0xFF);
		dest[4] = (byte)((value >>> 8) & 0xFF);
		dest[5] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong48(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 40) & 0xFF);
		dest[offset+1] = (byte)((value >>> 32) & 0xFF);
		dest[offset+2] = (byte)((value >>> 24) & 0xFF);
		dest[offset+3] = (byte)((value >>> 16) & 0xFF);
		dest[offset+4] = (byte)((value >>> 8) & 0xFF);
		dest[offset+5] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong48(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+5] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong48(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 40) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+5, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong48(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong48(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 40) & 0xFF));
		dest.put(1, (byte)((value >>> 32) & 0xFF));
		dest.put(2, (byte)((value >>> 24) & 0xFF));
		dest.put(3, (byte)((value >>> 16) & 0xFF));
		dest.put(4, (byte)((value >>> 8) & 0xFF));
		dest.put(5, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong48(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 40) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 32) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+5, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong48(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong48(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong48(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static long getBigLong56(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 48;
		rv |= ((source[1]) & 0xFFl) << 40;
		rv |= ((source[2]) & 0xFFl) << 32;
		rv |= ((source[3]) & 0xFFl) << 24;
		rv |= ((source[4]) & 0xFFl) << 16;
		rv |= ((source[5]) & 0xFFl) << 8;
		rv |= ((source[6]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 48;
		rv |= ((source[offset+1]) & 0xFFl) << 40;
		rv |= ((source[offset+2]) & 0xFFl) << 32;
		rv |= ((source[offset+3]) & 0xFFl) << 24;
		rv |= ((source[offset+4]) & 0xFFl) << 16;
		rv |= ((source[offset+5]) & 0xFFl) << 8;
		rv |= ((source[offset+6]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 48;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+5]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+6]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 48;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 40;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+5)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+6)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 48;
		rv |= ((source.get(1)) & 0xFFl) << 40;
		rv |= ((source.get(2)) & 0xFFl) << 32;
		rv |= ((source.get(3)) & 0xFFl) << 24;
		rv |= ((source.get(4)) & 0xFFl) << 16;
		rv |= ((source.get(5)) & 0xFFl) << 8;
		rv |= ((source.get(6)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 48;
		rv |= ((source.get(offset+1)) & 0xFFl) << 40;
		rv |= ((source.get(offset+2)) & 0xFFl) << 32;
		rv |= ((source.get(offset+3)) & 0xFFl) << 24;
		rv |= ((source.get(offset+4)) & 0xFFl) << 16;
		rv |= ((source.get(offset+5)) & 0xFFl) << 8;
		rv |= ((source.get(offset+6)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong56(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static void putBigLong56(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 48) & 0xFF);
		dest[1] = (byte)((value >>> 40) & 0xFF);
		dest[2] = (byte)((value >>> 32) & 0xFF);
		dest[3] = (byte)((value >>> 24) & 0xFF);
		dest[4] = (byte)((value >>> 16) & 0xFF);
		dest[5] = (byte)((value >>> 8) & 0xFF);
		dest[6] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong56(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 48) & 0xFF);
		dest[offset+1] = (byte)((value >>> 40) & 0xFF);
		dest[offset+2] = (byte)((value >>> 32) & 0xFF);
		dest[offset+3] = (byte)((value >>> 24) & 0xFF);
		dest[offset+4] = (byte)((value >>> 16) & 0xFF);
		dest[offset+5] = (byte)((value >>> 8) & 0xFF);
		dest[offset+6] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong56(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 48) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+5] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+6] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong56(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 48) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 40) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+5, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+6, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong56(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 48) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong56(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 48) & 0xFF));
		dest.put(1, (byte)((value >>> 40) & 0xFF));
		dest.put(2, (byte)((value >>> 32) & 0xFF));
		dest.put(3, (byte)((value >>> 24) & 0xFF));
		dest.put(4, (byte)((value >>> 16) & 0xFF));
		dest.put(5, (byte)((value >>> 8) & 0xFF));
		dest.put(6, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong56(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 48) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 40) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 32) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+5, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+6, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong56(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong56(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong56(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static long getBigLong(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 56;
		rv |= ((source[1]) & 0xFFl) << 48;
		rv |= ((source[2]) & 0xFFl) << 40;
		rv |= ((source[3]) & 0xFFl) << 32;
		rv |= ((source[4]) & 0xFFl) << 24;
		rv |= ((source[5]) & 0xFFl) << 16;
		rv |= ((source[6]) & 0xFFl) << 8;
		rv |= ((source[7]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 56;
		rv |= ((source[offset+1]) & 0xFFl) << 48;
		rv |= ((source[offset+2]) & 0xFFl) << 40;
		rv |= ((source[offset+3]) & 0xFFl) << 32;
		rv |= ((source[offset+4]) & 0xFFl) << 24;
		rv |= ((source[offset+5]) & 0xFFl) << 16;
		rv |= ((source[offset+6]) & 0xFFl) << 8;
		rv |= ((source[offset+7]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 56;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 48;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+5]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+6]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+7]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 56;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 48;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 40;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+5)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+6)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+7)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(ByteBuffer source)
	{
		long rv = 0;
		rv |= ((source.get(0)) & 0xFFl) << 56;
		rv |= ((source.get(1)) & 0xFFl) << 48;
		rv |= ((source.get(2)) & 0xFFl) << 40;
		rv |= ((source.get(3)) & 0xFFl) << 32;
		rv |= ((source.get(4)) & 0xFFl) << 24;
		rv |= ((source.get(5)) & 0xFFl) << 16;
		rv |= ((source.get(6)) & 0xFFl) << 8;
		rv |= ((source.get(7)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(ByteBuffer source, int offset)
	{
		long rv = 0;
		rv |= ((source.get(offset+0)) & 0xFFl) << 56;
		rv |= ((source.get(offset+1)) & 0xFFl) << 48;
		rv |= ((source.get(offset+2)) & 0xFFl) << 40;
		rv |= ((source.get(offset+3)) & 0xFFl) << 32;
		rv |= ((source.get(offset+4)) & 0xFFl) << 24;
		rv |= ((source.get(offset+5)) & 0xFFl) << 16;
		rv |= ((source.get(offset+6)) & 0xFFl) << 8;
		rv |= ((source.get(offset+7)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 56;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 56;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigLong(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 56;
		rv |= ((getByte(source)) & 0xFFl) << 48;
		rv |= ((getByte(source)) & 0xFFl) << 40;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static void putBigLong(byte[] dest, long value)
	{
		dest[0] = (byte)((value >>> 56) & 0xFF);
		dest[1] = (byte)((value >>> 48) & 0xFF);
		dest[2] = (byte)((value >>> 40) & 0xFF);
		dest[3] = (byte)((value >>> 32) & 0xFF);
		dest[4] = (byte)((value >>> 24) & 0xFF);
		dest[5] = (byte)((value >>> 16) & 0xFF);
		dest[6] = (byte)((value >>> 8) & 0xFF);
		dest[7] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong(byte[] dest, int offset, long value)
	{
		dest[offset+0] = (byte)((value >>> 56) & 0xFF);
		dest[offset+1] = (byte)((value >>> 48) & 0xFF);
		dest[offset+2] = (byte)((value >>> 40) & 0xFF);
		dest[offset+3] = (byte)((value >>> 32) & 0xFF);
		dest[offset+4] = (byte)((value >>> 24) & 0xFF);
		dest[offset+5] = (byte)((value >>> 16) & 0xFF);
		dest[offset+6] = (byte)((value >>> 8) & 0xFF);
		dest[offset+7] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 56) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 48) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+5] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+6] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+7] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 56) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 48) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 40) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+5, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+6, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+7, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 56) & 0xFF));
		dest.addByte((byte)((value >>> 48) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong(ByteBuffer dest, long value)
	{
		dest.put(0, (byte)((value >>> 56) & 0xFF));
		dest.put(1, (byte)((value >>> 48) & 0xFF));
		dest.put(2, (byte)((value >>> 40) & 0xFF));
		dest.put(3, (byte)((value >>> 32) & 0xFF));
		dest.put(4, (byte)((value >>> 24) & 0xFF));
		dest.put(5, (byte)((value >>> 16) & 0xFF));
		dest.put(6, (byte)((value >>> 8) & 0xFF));
		dest.put(7, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong(ByteBuffer dest, int offset, long value)
	{
		dest.put(offset+0, (byte)((value >>> 56) & 0xFF));
		dest.put(offset+1, (byte)((value >>> 48) & 0xFF));
		dest.put(offset+2, (byte)((value >>> 40) & 0xFF));
		dest.put(offset+3, (byte)((value >>> 32) & 0xFF));
		dest.put(offset+4, (byte)((value >>> 24) & 0xFF));
		dest.put(offset+5, (byte)((value >>> 16) & 0xFF));
		dest.put(offset+6, (byte)((value >>> 8) & 0xFF));
		dest.put(offset+7, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong(OutputStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 56) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong(OutputByteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 56) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong(ByteBlockWriteStream dest, long value) throws IOException, EOFException
	{
		dest.write((byte)((value >>> 56) & 0xFF));
		dest.write((byte)((value >>> 48) & 0xFF));
		dest.write((byte)((value >>> 40) & 0xFF));
		dest.write((byte)((value >>> 32) & 0xFF));
		dest.write((byte)((value >>> 24) & 0xFF));
		dest.write((byte)((value >>> 16) & 0xFF));
		dest.write((byte)((value >>> 8) & 0xFF));
		dest.write((byte)((value >>> 0) & 0xFF));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static float getLittleFloat(byte[] source)
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static float getLittleFloat(byte[] source, int offset)
	{
		return Float.intBitsToFloat(getLittleInt(source, offset));
	}
	
	public static float getLittleFloat(Slice<byte[]> source, int offset)
	{
		return Float.intBitsToFloat(getLittleInt(source, offset));
	}
	
	public static float getLittleFloat(ByteList source, int offset)
	{
		return Float.intBitsToFloat(getLittleInt(source, offset));
	}
	
	public static float getLittleFloat(ByteBuffer source)
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static float getLittleFloat(ByteBuffer source, int offset)
	{
		return Float.intBitsToFloat(getLittleInt(source, offset));
	}
	
	public static float getLittleFloat(InputStream source) throws IOException, EOFException
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static float getLittleFloat(InputByteStream source) throws IOException, EOFException
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static float getLittleFloat(ByteBlockReadStream source) throws IOException, EOFException
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static void putLittleFloat(byte[] dest, float value)
	{
		putLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(byte[] dest, int offset, float value)
	{
		putLittleInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(Slice<byte[]> dest, int offset, float value)
	{
		putLittleInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(ByteList dest, int offset, float value)
	{
		putLittleInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(ByteList dest, float value)
	{
		putLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(ByteBuffer dest, float value)
	{
		putLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(ByteBuffer dest, int offset, float value)
	{
		putLittleInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(OutputStream dest, float value) throws IOException, EOFException
	{
		putLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(OutputByteStream dest, float value) throws IOException, EOFException
	{
		putLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(ByteBlockWriteStream dest, float value) throws IOException, EOFException
	{
		putLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static double getLittleDouble(byte[] source)
	{
		return Double.longBitsToDouble(getLittleLong(source));
	}
	
	public static double getLittleDouble(byte[] source, int offset)
	{
		return Double.longBitsToDouble(getLittleLong(source, offset));
	}
	
	public static double getLittleDouble(Slice<byte[]> source, int offset)
	{
		return Double.longBitsToDouble(getLittleLong(source, offset));
	}
	
	public static double getLittleDouble(ByteList source, int offset)
	{
		return Double.longBitsToDouble(getLittleLong(source, offset));
	}
	
	public static double getLittleDouble(ByteBuffer source)
	{
		return Double.longBitsToDouble(getLittleLong(source));
	}
	
	public static double getLittleDouble(ByteBuffer source, int offset)
	{
		return Double.longBitsToDouble(getLittleLong(source, offset));
	}
	
	public static double getLittleDouble(InputStream source) throws IOException, EOFException
	{
		return Double.longBitsToDouble(getLittleLong(source));
	}
	
	public static double getLittleDouble(InputByteStream source) throws IOException, EOFException
	{
		return Double.longBitsToDouble(getLittleLong(source));
	}
	
	public static double getLittleDouble(ByteBlockReadStream source) throws IOException, EOFException
	{
		return Double.longBitsToDouble(getLittleLong(source));
	}
	
	public static void putLittleDouble(byte[] dest, double value)
	{
		putLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(byte[] dest, int offset, double value)
	{
		putLittleLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(Slice<byte[]> dest, int offset, double value)
	{
		putLittleLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(ByteList dest, int offset, double value)
	{
		putLittleLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(ByteList dest, double value)
	{
		putLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(ByteBuffer dest, double value)
	{
		putLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(ByteBuffer dest, int offset, double value)
	{
		putLittleLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(OutputStream dest, double value) throws IOException, EOFException
	{
		putLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(OutputByteStream dest, double value) throws IOException, EOFException
	{
		putLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(ByteBlockWriteStream dest, double value) throws IOException, EOFException
	{
		putLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static float getBigFloat(byte[] source)
	{
		return Float.intBitsToFloat(getBigInt(source));
	}
	
	public static float getBigFloat(byte[] source, int offset)
	{
		return Float.intBitsToFloat(getBigInt(source, offset));
	}
	
	public static float getBigFloat(Slice<byte[]> source, int offset)
	{
		return Float.intBitsToFloat(getBigInt(source, offset));
	}
	
	public static float getBigFloat(ByteList source, int offset)
	{
		return Float.intBitsToFloat(getBigInt(source, offset));
	}
	
	public static float getBigFloat(ByteBuffer source)
	{
		return Float.intBitsToFloat(getBigInt(source));
	}
	
	public static float getBigFloat(ByteBuffer source, int offset)
	{
		return Float.intBitsToFloat(getBigInt(source, offset));
	}
	
	public static float getBigFloat(InputStream source) throws IOException, EOFException
	{
		return Float.intBitsToFloat(getBigInt(source));
	}
	
	public static float getBigFloat(InputByteStream source) throws IOException, EOFException
	{
		return Float.intBitsToFloat(getBigInt(source));
	}
	
	public static float getBigFloat(ByteBlockReadStream source) throws IOException, EOFException
	{
		return Float.intBitsToFloat(getBigInt(source));
	}
	
	public static void putBigFloat(byte[] dest, float value)
	{
		putBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(byte[] dest, int offset, float value)
	{
		putBigInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(Slice<byte[]> dest, int offset, float value)
	{
		putBigInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(ByteList dest, int offset, float value)
	{
		putBigInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(ByteList dest, float value)
	{
		putBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(ByteBuffer dest, float value)
	{
		putBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(ByteBuffer dest, int offset, float value)
	{
		putBigInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(OutputStream dest, float value) throws IOException, EOFException
	{
		putBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(OutputByteStream dest, float value) throws IOException, EOFException
	{
		putBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(ByteBlockWriteStream dest, float value) throws IOException, EOFException
	{
		putBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static double getBigDouble(byte[] source)
	{
		return Double.longBitsToDouble(getBigLong(source));
	}
	
	public static double getBigDouble(byte[] source, int offset)
	{
		return Double.longBitsToDouble(getBigLong(source, offset));
	}
	
	public static double getBigDouble(Slice<byte[]> source, int offset)
	{
		return Double.longBitsToDouble(getBigLong(source, offset));
	}
	
	public static double getBigDouble(ByteList source, int offset)
	{
		return Double.longBitsToDouble(getBigLong(source, offset));
	}
	
	public static double getBigDouble(ByteBuffer source)
	{
		return Double.longBitsToDouble(getBigLong(source));
	}
	
	public static double getBigDouble(ByteBuffer source, int offset)
	{
		return Double.longBitsToDouble(getBigLong(source, offset));
	}
	
	public static double getBigDouble(InputStream source) throws IOException, EOFException
	{
		return Double.longBitsToDouble(getBigLong(source));
	}
	
	public static double getBigDouble(InputByteStream source) throws IOException, EOFException
	{
		return Double.longBitsToDouble(getBigLong(source));
	}
	
	public static double getBigDouble(ByteBlockReadStream source) throws IOException, EOFException
	{
		return Double.longBitsToDouble(getBigLong(source));
	}
	
	public static void putBigDouble(byte[] dest, double value)
	{
		putBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(byte[] dest, int offset, double value)
	{
		putBigLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(Slice<byte[]> dest, int offset, double value)
	{
		putBigLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(ByteList dest, int offset, double value)
	{
		putBigLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(ByteList dest, double value)
	{
		putBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(ByteBuffer dest, double value)
	{
		putBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(ByteBuffer dest, int offset, double value)
	{
		putBigLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(OutputStream dest, double value) throws IOException, EOFException
	{
		putBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(OutputByteStream dest, double value) throws IOException, EOFException
	{
		putBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(ByteBlockWriteStream dest, double value) throws IOException, EOFException
	{
		putBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static char getChar(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source);
		else if (endianness == Endianness.Big)
			return getBigChar(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source, offset);
		else if (endianness == Endianness.Big)
			return getBigChar(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source, offset);
		else if (endianness == Endianness.Big)
			return getBigChar(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source, offset);
		else if (endianness == Endianness.Big)
			return getBigChar(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source);
		else if (endianness == Endianness.Big)
			return getBigChar(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source, offset);
		else if (endianness == Endianness.Big)
			return getBigChar(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source);
		else if (endianness == Endianness.Big)
			return getBigChar(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source);
		else if (endianness == Endianness.Big)
			return getBigChar(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static char getChar(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source);
		else if (endianness == Endianness.Big)
			return getBigChar(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(byte[] dest, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(byte[] dest, int offset, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(Slice<byte[]> dest, int offset, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(ByteList dest, int offset, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(ByteList dest, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(ByteBuffer dest, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(ByteBuffer dest, int offset, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(OutputStream dest, char value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(OutputByteStream dest, char value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putChar(ByteBlockWriteStream dest, char value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packChar(char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleChar(value);
		else if (endianness == Endianness.Big)
			return packBigChar(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source);
		else if (endianness == Endianness.Big)
			return getBigShort(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source, offset);
		else if (endianness == Endianness.Big)
			return getBigShort(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source, offset);
		else if (endianness == Endianness.Big)
			return getBigShort(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source, offset);
		else if (endianness == Endianness.Big)
			return getBigShort(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source);
		else if (endianness == Endianness.Big)
			return getBigShort(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source, offset);
		else if (endianness == Endianness.Big)
			return getBigShort(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source);
		else if (endianness == Endianness.Big)
			return getBigShort(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source);
		else if (endianness == Endianness.Big)
			return getBigShort(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static short getShort(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source);
		else if (endianness == Endianness.Big)
			return getBigShort(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(byte[] dest, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(byte[] dest, int offset, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(Slice<byte[]> dest, int offset, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(ByteList dest, int offset, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(ByteList dest, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(ByteBuffer dest, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(ByteBuffer dest, int offset, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(OutputStream dest, short value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(OutputByteStream dest, short value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putShort(ByteBlockWriteStream dest, short value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packShort(short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleShort(value);
		else if (endianness == Endianness.Big)
			return packBigShort(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source);
		else if (endianness == Endianness.Big)
			return getBigInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source);
		else if (endianness == Endianness.Big)
			return getBigInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source);
		else if (endianness == Endianness.Big)
			return getBigInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source);
		else if (endianness == Endianness.Big)
			return getBigInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt24(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleInt24(source);
		else if (endianness == Endianness.Big)
			return getBigInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(byte[] dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(byte[] dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(Slice<byte[]> dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(ByteList dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(ByteList dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(ByteBuffer dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(ByteBuffer dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(OutputStream dest, int value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(OutputByteStream dest, int value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24(ByteBlockWriteStream dest, int value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packInt24(int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleInt24(value);
		else if (endianness == Endianness.Big)
			return packBigInt24(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source);
		else if (endianness == Endianness.Big)
			return getBigInt(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source);
		else if (endianness == Endianness.Big)
			return getBigInt(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source, offset);
		else if (endianness == Endianness.Big)
			return getBigInt(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source);
		else if (endianness == Endianness.Big)
			return getBigInt(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source);
		else if (endianness == Endianness.Big)
			return getBigInt(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getInt(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source);
		else if (endianness == Endianness.Big)
			return getBigInt(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(byte[] dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(byte[] dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(Slice<byte[]> dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(ByteList dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(ByteList dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(ByteBuffer dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(ByteBuffer dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(OutputStream dest, int value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(OutputByteStream dest, int value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt(ByteBlockWriteStream dest, int value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packInt(int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleInt(value);
		else if (endianness == Endianness.Big)
			return packBigInt(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source);
		else if (endianness == Endianness.Big)
			return getBigLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source);
		else if (endianness == Endianness.Big)
			return getBigLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source);
		else if (endianness == Endianness.Big)
			return getBigLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source);
		else if (endianness == Endianness.Big)
			return getBigLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong40(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong40(source);
		else if (endianness == Endianness.Big)
			return getBigLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(byte[] dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(byte[] dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(Slice<byte[]> dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(ByteBuffer dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(ByteBuffer dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(OutputStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(OutputByteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40(ByteBlockWriteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packLong40(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong40(value);
		else if (endianness == Endianness.Big)
			return packBigLong40(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source);
		else if (endianness == Endianness.Big)
			return getBigLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source);
		else if (endianness == Endianness.Big)
			return getBigLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source);
		else if (endianness == Endianness.Big)
			return getBigLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source);
		else if (endianness == Endianness.Big)
			return getBigLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong48(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong48(source);
		else if (endianness == Endianness.Big)
			return getBigLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(byte[] dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(byte[] dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(Slice<byte[]> dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(ByteBuffer dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(ByteBuffer dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(OutputStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(OutputByteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48(ByteBlockWriteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packLong48(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong48(value);
		else if (endianness == Endianness.Big)
			return packBigLong48(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source);
		else if (endianness == Endianness.Big)
			return getBigLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source);
		else if (endianness == Endianness.Big)
			return getBigLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source);
		else if (endianness == Endianness.Big)
			return getBigLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source);
		else if (endianness == Endianness.Big)
			return getBigLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong56(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong56(source);
		else if (endianness == Endianness.Big)
			return getBigLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(byte[] dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(byte[] dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(Slice<byte[]> dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(ByteBuffer dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(ByteBuffer dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(OutputStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(OutputByteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56(ByteBlockWriteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packLong56(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong56(value);
		else if (endianness == Endianness.Big)
			return packBigLong56(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source);
		else if (endianness == Endianness.Big)
			return getBigLong(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source);
		else if (endianness == Endianness.Big)
			return getBigLong(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source, offset);
		else if (endianness == Endianness.Big)
			return getBigLong(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source);
		else if (endianness == Endianness.Big)
			return getBigLong(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source);
		else if (endianness == Endianness.Big)
			return getBigLong(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLong(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source);
		else if (endianness == Endianness.Big)
			return getBigLong(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(byte[] dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(byte[] dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(Slice<byte[]> dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(ByteBuffer dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(ByteBuffer dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(OutputStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(OutputByteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong(ByteBlockWriteStream dest, long value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packLong(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong(value);
		else if (endianness == Endianness.Big)
			return packBigLong(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source);
		else if (endianness == Endianness.Big)
			return getBigFloat(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source, offset);
		else if (endianness == Endianness.Big)
			return getBigFloat(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source, offset);
		else if (endianness == Endianness.Big)
			return getBigFloat(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source, offset);
		else if (endianness == Endianness.Big)
			return getBigFloat(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source);
		else if (endianness == Endianness.Big)
			return getBigFloat(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source, offset);
		else if (endianness == Endianness.Big)
			return getBigFloat(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source);
		else if (endianness == Endianness.Big)
			return getBigFloat(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source);
		else if (endianness == Endianness.Big)
			return getBigFloat(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static float getFloat(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source);
		else if (endianness == Endianness.Big)
			return getBigFloat(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(byte[] dest, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(byte[] dest, int offset, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(Slice<byte[]> dest, int offset, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(ByteList dest, int offset, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(ByteList dest, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(ByteBuffer dest, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(ByteBuffer dest, int offset, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(OutputStream dest, float value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(OutputByteStream dest, float value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putFloat(ByteBlockWriteStream dest, float value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packFloat(float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleFloat(value);
		else if (endianness == Endianness.Big)
			return packBigFloat(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source);
		else if (endianness == Endianness.Big)
			return getBigDouble(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source, offset);
		else if (endianness == Endianness.Big)
			return getBigDouble(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source, offset);
		else if (endianness == Endianness.Big)
			return getBigDouble(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source, offset);
		else if (endianness == Endianness.Big)
			return getBigDouble(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source);
		else if (endianness == Endianness.Big)
			return getBigDouble(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source, offset);
		else if (endianness == Endianness.Big)
			return getBigDouble(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source);
		else if (endianness == Endianness.Big)
			return getBigDouble(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source);
		else if (endianness == Endianness.Big)
			return getBigDouble(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static double getDouble(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source);
		else if (endianness == Endianness.Big)
			return getBigDouble(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(byte[] dest, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(byte[] dest, int offset, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(Slice<byte[]> dest, int offset, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(ByteList dest, int offset, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(ByteList dest, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(ByteBuffer dest, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(ByteBuffer dest, int offset, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, offset, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, offset, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(OutputStream dest, double value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(OutputByteStream dest, double value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putDouble(ByteBlockWriteStream dest, double value, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packDouble(double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleDouble(value);
		else if (endianness == Endianness.Big)
			return packBigDouble(value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static byte[] packLittleChar(char value)
	{
		byte[] dest = new byte[2];
		putLittleChar(dest, value);
		return dest;
	}
	
	public static byte[] packLittleShort(short value)
	{
		byte[] dest = new byte[2];
		putLittleShort(dest, value);
		return dest;
	}
	
	public static byte[] packLittleInt24(int value)
	{
		byte[] dest = new byte[3];
		putLittleInt24(dest, value);
		return dest;
	}
	
	public static byte[] packLittleInt(int value)
	{
		byte[] dest = new byte[4];
		putLittleInt(dest, value);
		return dest;
	}
	
	public static byte[] packLittleLong40(long value)
	{
		byte[] dest = new byte[5];
		putLittleLong40(dest, value);
		return dest;
	}
	
	public static byte[] packLittleLong48(long value)
	{
		byte[] dest = new byte[6];
		putLittleLong48(dest, value);
		return dest;
	}
	
	public static byte[] packLittleLong56(long value)
	{
		byte[] dest = new byte[7];
		putLittleLong56(dest, value);
		return dest;
	}
	
	public static byte[] packLittleLong(long value)
	{
		byte[] dest = new byte[8];
		putLittleLong(dest, value);
		return dest;
	}
	
	public static byte[] packLittleFloat(float value)
	{
		byte[] dest = new byte[4];
		putLittleFloat(dest, value);
		return dest;
	}
	
	public static byte[] packLittleDouble(double value)
	{
		byte[] dest = new byte[8];
		putLittleDouble(dest, value);
		return dest;
	}
	
	public static byte[] packBigChar(char value)
	{
		byte[] dest = new byte[2];
		putBigChar(dest, value);
		return dest;
	}
	
	public static byte[] packBigShort(short value)
	{
		byte[] dest = new byte[2];
		putBigShort(dest, value);
		return dest;
	}
	
	public static byte[] packBigInt24(int value)
	{
		byte[] dest = new byte[3];
		putBigInt24(dest, value);
		return dest;
	}
	
	public static byte[] packBigInt(int value)
	{
		byte[] dest = new byte[4];
		putBigInt(dest, value);
		return dest;
	}
	
	public static byte[] packBigLong40(long value)
	{
		byte[] dest = new byte[5];
		putBigLong40(dest, value);
		return dest;
	}
	
	public static byte[] packBigLong48(long value)
	{
		byte[] dest = new byte[6];
		putBigLong48(dest, value);
		return dest;
	}
	
	public static byte[] packBigLong56(long value)
	{
		byte[] dest = new byte[7];
		putBigLong56(dest, value);
		return dest;
	}
	
	public static byte[] packBigLong(long value)
	{
		byte[] dest = new byte[8];
		putBigLong(dest, value);
		return dest;
	}
	
	public static byte[] packBigFloat(float value)
	{
		byte[] dest = new byte[4];
		putBigFloat(dest, value);
		return dest;
	}
	
	public static byte[] packBigDouble(double value)
	{
		byte[] dest = new byte[8];
		putBigDouble(dest, value);
		return dest;
	}
	
	public static long getLittle(byte[] source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[0] & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(byte[] source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[0] & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(byte[] source, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittle(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(byte[] source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[offset+0] & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(byte[] source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[offset+0] & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(byte[] source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittle(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, offset, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(Slice<byte[]> source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getUnderlying()[source.getOffset()+offset+0] & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(Slice<byte[]> source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getUnderlying()[source.getOffset()+offset+0] & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(Slice<byte[]> source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittle(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, offset, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(ByteList source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getByte(offset+0) & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(ByteList source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getByte(offset+0) & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(ByteList source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittle(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, offset, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(ByteBuffer source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(0) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(ByteBuffer source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(0) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(ByteBuffer source, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittle(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(ByteBuffer source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(offset+0) & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(ByteBuffer source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(offset+0) & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(ByteBuffer source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittle(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, offset, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(InputStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(InputStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(InputStream source, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittle(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(InputByteStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(InputByteStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(InputByteStream source, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittle(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittle(ByteBlockReadStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBig(ByteBlockReadStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigLong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigLong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigLong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long get(ByteBlockReadStream source, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittle(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBig(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(byte[] dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest[0] = (byte)value; break;
			case 2:
				putLittleShort(dest, (short)value); break;
			case 3:
				putLittleInt24(dest, (int)value); break;
			case 4:
				putLittleInt(dest, (int)value); break;
			case 5:
				putLittleLong40(dest, value); break;
			case 6:
				putLittleLong48(dest, value); break;
			case 7:
				putLittleLong56(dest, value); break;
			case 8:
				putLittleLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(byte[] dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest[0] = (byte)value; break;
			case 2:
				putBigShort(dest, (short)value); break;
			case 3:
				putBigInt24(dest, (int)value); break;
			case 4:
				putBigInt(dest, (int)value); break;
			case 5:
				putBigLong40(dest, value); break;
			case 6:
				putBigLong48(dest, value); break;
			case 7:
				putBigLong56(dest, value); break;
			case 8:
				putBigLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(byte[] dest, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(byte[] dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest[offset+0] = (byte)value; break;
			case 2:
				putLittleShort(dest, offset, (short)value); break;
			case 3:
				putLittleInt24(dest, offset, (int)value); break;
			case 4:
				putLittleInt(dest, offset, (int)value); break;
			case 5:
				putLittleLong40(dest, offset, value); break;
			case 6:
				putLittleLong48(dest, offset, value); break;
			case 7:
				putLittleLong56(dest, offset, value); break;
			case 8:
				putLittleLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(byte[] dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest[offset+0] = (byte)value; break;
			case 2:
				putBigShort(dest, offset, (short)value); break;
			case 3:
				putBigInt24(dest, offset, (int)value); break;
			case 4:
				putBigInt(dest, offset, (int)value); break;
			case 5:
				putBigLong40(dest, offset, value); break;
			case 6:
				putBigLong48(dest, offset, value); break;
			case 7:
				putBigLong56(dest, offset, value); break;
			case 8:
				putBigLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(byte[] dest, int offset, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, offset, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, offset, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(Slice<byte[]> dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)value; break;
			case 2:
				putLittleShort(dest, offset, (short)value); break;
			case 3:
				putLittleInt24(dest, offset, (int)value); break;
			case 4:
				putLittleInt(dest, offset, (int)value); break;
			case 5:
				putLittleLong40(dest, offset, value); break;
			case 6:
				putLittleLong48(dest, offset, value); break;
			case 7:
				putLittleLong56(dest, offset, value); break;
			case 8:
				putLittleLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(Slice<byte[]> dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)value; break;
			case 2:
				putBigShort(dest, offset, (short)value); break;
			case 3:
				putBigInt24(dest, offset, (int)value); break;
			case 4:
				putBigInt(dest, offset, (int)value); break;
			case 5:
				putBigLong40(dest, offset, value); break;
			case 6:
				putBigLong48(dest, offset, value); break;
			case 7:
				putBigLong56(dest, offset, value); break;
			case 8:
				putBigLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(Slice<byte[]> dest, int offset, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, offset, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, offset, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(ByteList dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.setByte(offset+0, (byte)value); break;
			case 2:
				putLittleShort(dest, offset, (short)value); break;
			case 3:
				putLittleInt24(dest, offset, (int)value); break;
			case 4:
				putLittleInt(dest, offset, (int)value); break;
			case 5:
				putLittleLong40(dest, offset, value); break;
			case 6:
				putLittleLong48(dest, offset, value); break;
			case 7:
				putLittleLong56(dest, offset, value); break;
			case 8:
				putLittleLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(ByteList dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.setByte(offset+0, (byte)value); break;
			case 2:
				putBigShort(dest, offset, (short)value); break;
			case 3:
				putBigInt24(dest, offset, (int)value); break;
			case 4:
				putBigInt(dest, offset, (int)value); break;
			case 5:
				putBigLong40(dest, offset, value); break;
			case 6:
				putBigLong48(dest, offset, value); break;
			case 7:
				putBigLong56(dest, offset, value); break;
			case 8:
				putBigLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(ByteList dest, int offset, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, offset, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, offset, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(ByteList dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.addByte((byte)value); break;
			case 2:
				putLittleShort(dest, (short)value); break;
			case 3:
				putLittleInt24(dest, (int)value); break;
			case 4:
				putLittleInt(dest, (int)value); break;
			case 5:
				putLittleLong40(dest, value); break;
			case 6:
				putLittleLong48(dest, value); break;
			case 7:
				putLittleLong56(dest, value); break;
			case 8:
				putLittleLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(ByteList dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.addByte((byte)value); break;
			case 2:
				putBigShort(dest, (short)value); break;
			case 3:
				putBigInt24(dest, (int)value); break;
			case 4:
				putBigInt(dest, (int)value); break;
			case 5:
				putBigLong40(dest, value); break;
			case 6:
				putBigLong48(dest, value); break;
			case 7:
				putBigLong56(dest, value); break;
			case 8:
				putBigLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(ByteList dest, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(ByteBuffer dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.put(0, (byte)value); break;
			case 2:
				putLittleShort(dest, (short)value); break;
			case 3:
				putLittleInt24(dest, (int)value); break;
			case 4:
				putLittleInt(dest, (int)value); break;
			case 5:
				putLittleLong40(dest, value); break;
			case 6:
				putLittleLong48(dest, value); break;
			case 7:
				putLittleLong56(dest, value); break;
			case 8:
				putLittleLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(ByteBuffer dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.put(0, (byte)value); break;
			case 2:
				putBigShort(dest, (short)value); break;
			case 3:
				putBigInt24(dest, (int)value); break;
			case 4:
				putBigInt(dest, (int)value); break;
			case 5:
				putBigLong40(dest, value); break;
			case 6:
				putBigLong48(dest, value); break;
			case 7:
				putBigLong56(dest, value); break;
			case 8:
				putBigLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(ByteBuffer dest, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(ByteBuffer dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.put(offset+0, (byte)value); break;
			case 2:
				putLittleShort(dest, offset, (short)value); break;
			case 3:
				putLittleInt24(dest, offset, (int)value); break;
			case 4:
				putLittleInt(dest, offset, (int)value); break;
			case 5:
				putLittleLong40(dest, offset, value); break;
			case 6:
				putLittleLong48(dest, offset, value); break;
			case 7:
				putLittleLong56(dest, offset, value); break;
			case 8:
				putLittleLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(ByteBuffer dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.put(offset+0, (byte)value); break;
			case 2:
				putBigShort(dest, offset, (short)value); break;
			case 3:
				putBigInt24(dest, offset, (int)value); break;
			case 4:
				putBigInt(dest, offset, (int)value); break;
			case 5:
				putBigLong40(dest, offset, value); break;
			case 6:
				putBigLong48(dest, offset, value); break;
			case 7:
				putBigLong56(dest, offset, value); break;
			case 8:
				putBigLong(dest, offset, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(ByteBuffer dest, int offset, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, offset, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, offset, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(OutputStream dest, long value, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.write((byte)value); break;
			case 2:
				putLittleShort(dest, (short)value); break;
			case 3:
				putLittleInt24(dest, (int)value); break;
			case 4:
				putLittleInt(dest, (int)value); break;
			case 5:
				putLittleLong40(dest, value); break;
			case 6:
				putLittleLong48(dest, value); break;
			case 7:
				putLittleLong56(dest, value); break;
			case 8:
				putLittleLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(OutputStream dest, long value, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.write((byte)value); break;
			case 2:
				putBigShort(dest, (short)value); break;
			case 3:
				putBigInt24(dest, (int)value); break;
			case 4:
				putBigInt(dest, (int)value); break;
			case 5:
				putBigLong40(dest, value); break;
			case 6:
				putBigLong48(dest, value); break;
			case 7:
				putBigLong56(dest, value); break;
			case 8:
				putBigLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(OutputStream dest, long value, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(OutputByteStream dest, long value, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.write((byte)value); break;
			case 2:
				putLittleShort(dest, (short)value); break;
			case 3:
				putLittleInt24(dest, (int)value); break;
			case 4:
				putLittleInt(dest, (int)value); break;
			case 5:
				putLittleLong40(dest, value); break;
			case 6:
				putLittleLong48(dest, value); break;
			case 7:
				putLittleLong56(dest, value); break;
			case 8:
				putLittleLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(OutputByteStream dest, long value, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.write((byte)value); break;
			case 2:
				putBigShort(dest, (short)value); break;
			case 3:
				putBigInt24(dest, (int)value); break;
			case 4:
				putBigInt(dest, (int)value); break;
			case 5:
				putBigLong40(dest, value); break;
			case 6:
				putBigLong48(dest, value); break;
			case 7:
				putBigLong56(dest, value); break;
			case 8:
				putBigLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(OutputByteStream dest, long value, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void putLittle(ByteBlockWriteStream dest, long value, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.write((byte)value); break;
			case 2:
				putLittleShort(dest, (short)value); break;
			case 3:
				putLittleInt24(dest, (int)value); break;
			case 4:
				putLittleInt(dest, (int)value); break;
			case 5:
				putLittleLong40(dest, value); break;
			case 6:
				putLittleLong48(dest, value); break;
			case 7:
				putLittleLong56(dest, value); break;
			case 8:
				putLittleLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(ByteBlockWriteStream dest, long value, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.write((byte)value); break;
			case 2:
				putBigShort(dest, (short)value); break;
			case 3:
				putBigInt24(dest, (int)value); break;
			case 4:
				putBigInt(dest, (int)value); break;
			case 5:
				putBigLong40(dest, value); break;
			case 6:
				putBigLong48(dest, value); break;
			case 7:
				putBigLong56(dest, value); break;
			case 8:
				putBigLong(dest, value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(ByteBlockWriteStream dest, long value, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			putLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static byte[] packLittle(long value, int numberOfBytes)
	{
		byte[] dest = new byte[numberOfBytes];
		putLittle(dest, value, numberOfBytes);
		return dest;
	}
	
	public static byte[] packBig(long value, int numberOfBytes)
	{
		byte[] dest = new byte[numberOfBytes];
		putBig(dest, value, numberOfBytes);
		return dest;
	}
	
	
	public static byte[] pack(long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittle(value, numberOfBytes);
		else if (endianness == Endianness.Big)
			return packBig(value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	// >>>
}
