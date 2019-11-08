/*
 * Created on Sep 14, 2004
 * Rewritten using python and infilegen on Jun 30, 2014
 */

package rebound.bits;

import static rebound.bits.BitUtilities.*;
import static rebound.util.BasicExceptionUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import rebound.io.iio.InputByteStream;
import rebound.io.iio.OutputByteStream;
import rebound.io.iio.RandomAccessBytes;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.objectutil.JavaNamespace;

//TODO!!  Test instanceof ByteListWithMultibyteAccess!! XDD'''
//TODO Are these named right?  putLittleLong56Buffermodifying(.., int offset, ..), etc.   It modifies the ByteOrder but not the position().  Make sure we're consistent with naming!

//TODO Test the non-power-of-two accessors (esp. the ByteBuffer ones!!)  X'D     (and perhaps use the same test for DirectByteList and any ByteListWithMultibyteAccess? :>     That would be way easier if this used instanceof to support things properly X'D )

//TODO Ones like putLittleInt(byte[], int value) for ByteList/etc.!

//Todo Add a suite of the methods for the rebound.io.streaming API when/ifffff that's ever finisheddd! X'DD
//Todo add documentation making it clear that the bytesize-multiplexed methods do UNsigned upcasting from all the sizes to 64bit long's!  \:DD/

/**
 * Utility methods for getting/putting, packing/unpacking, reading/relaying primitive values of all multiples of 8 bits up to 64 (long/double), in/out of
 * 	• byte[]s
 * 	• {@link Slice}<byte[]>s
 * 	• {@link ByteList}s
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

primsExtras = [
	["int24", "Int24", "int", 24],
	["long40", "Long40", "long", 40],
	["long48", "Long48", "long", 48],
	["long56", "Long56", "long", 56],
]

iprims = [
	["char", "Char", "char", 16],
	["short", "Short", "short", 16],
	["uint24", "UInt24", "int", 24],
	["int", "Int", "int", 32],
	["ulong40", "ULong40", "long", 40],
	["ulong48", "ULong48", "long", 48],
	["ulong56", "ULong56", "long", 56],
	["long", "Long", "long", 64],
]

oprims = [
	["char", "Char", "char", 16],
	["short", "Short", "short", 16],
	["int24", "Int24", "int", 24],
	["int", "Int", "int", 32],
	["long40", "Long40", "long", 40],
	["long48", "Long48", "long", 48],
	["long56", "Long56", "long", 56],
	["long", "Long", "long", 64],
]

fl = [
	["float", "Float", "float", 32],
	["double", "Double", "double", 64],
];

si = [
	["sint24", "SInt24", "int", 24],
	["slong40", "SLong40", "long", 40],
	["slong48", "SLong48", "long", 48],
	["slong56", "SLong56", "long", 56],
];

alliprims = iprims + fl + si;
alloprims = oprims + fl;



def isnew(logiprim):
	return logiprim[-1].isdigit();



apis = [
[  [ "get", "", [["byte[]", "source"]], None, "source[byteIndex]"],                                     [ "put", "", [["byte[]", "dest"]], None, "dest[byteIndex] = %"]                                   ],
[  [ "get", "", [["byte[]", "source"], ["int", "offset"]], None, "source[offset+byteIndex]"],           [ "put", "", [["byte[]", "dest"], ["int", "offset"]], None, "dest[offset+byteIndex] = %"]         ],
[  [ "get", "", [["Slice<byte[]>", "source"]], None, "source.getUnderlying()[source.getOffset()+byteIndex]"],           [ "put", "", [["Slice<byte[]>", "dest"]], None, "dest.getUnderlying()[dest.getOffset()+byteIndex] = %"]         ],
[  [ "get", "", [["Slice<byte[]>", "source"], ["int", "offset"]], None, "source.getUnderlying()[source.getOffset()+offset+byteIndex]"],           [ "put", "", [["Slice<byte[]>", "dest"], ["int", "offset"]], None, "dest.getUnderlying()[dest.getOffset()+offset+byteIndex] = %"]         ],
[  [ "get", "", [["ByteList", "source"]], None, "source.getByte(byteIndex)"],           [ "set", "", [["ByteList", "dest"]], None, "dest.setByte(byteIndex, %)"]         ],
[  [ "get", "", [["ByteList", "source"], ["int", "offset"]], None, "source.getByte(offset+byteIndex)"],           [ "put", "", [["ByteList", "dest"], ["int", "offset"]], None, "dest.setByte(offset+byteIndex, %)"]         ],
[  None,                                                                    [ "add", "", [["ByteList", "dest"]], None, "dest.addByte(%)"]         ],
[  None,                                                                    [ "add", "", [["ByteList", "dest"], ["int", "offset"]], None, "dest.insertByte(offset+byteIndex, %)"]         ],
[  [ "get", "", [["InputStream", "source"]], "IOException, EOFException", "getByte(source)"],           [ "put", "", [["OutputStream", "dest"]], "IOException, EOFException", "dest.write(%)"]            ],
[  [ "get", "", [["InputByteStream", "source"]], "IOException, EOFException", "getByte(source)"],  [ "put", "", [["OutputByteStream", "dest"]], "IOException, EOFException", "dest.write(%)"]            ],
[  [ "get", "", [["ByteBlockReadStream", "source"]], "IOException, EOFException", "getByte(source)"],   [ "put", "", [["ByteBlockWriteStream", "dest"]], "IOException, EOFException", "dest.write(%)"]            ],
];

allapis = apis + [
[  [ "get", "", [["ByteBuffer", "source"]], None, "source.get(byteIndex)"],                             [ "put", "", [["ByteBuffer", "dest"]], None, "dest.put(byteIndex, %)"]                            ],
[  [ "get", "", [["ByteBuffer", "source"], ["int", "offset"]], None, "source.get(offset+byteIndex)"],   [ "put", "", [["ByteBuffer", "dest"], ["int", "offset"]], None, "dest.put(offset+byteIndex, %)"]  ],
]

iapis = filter(lambda a: a != None, map(lambda (inform, outform): inform, apis));
oapis = filter(lambda a: a != None, map(lambda (inform, outform): outform, apis));

alliapis = filter(lambda a: a != None, map(lambda (inform, outform): inform, allapis));
alloapis = filter(lambda a: a != None, map(lambda (inform, outform): outform, allapis));




def argslist(args):
	return ", ".join(map(lambda (t, n): n, args));

def argsdecl(args):
	return ", ".join(map(lambda (t, n): t+" "+n, args));







s = "";

_s = "";   #for disabling certain blocks ^_~




# Core functions! ^w^

for littleEndian in [True, False]:
	
	for logiprim, clogiprim, physprim, bitlen in iprims:
		for namePrefix, nameSuffix, args, throws, expr in iapis:
			s += """
			public static """+physprim+" "+namePrefix+("Little" if littleEndian else "Big")+clogiprim+nameSuffix+"("+argsdecl(args)+")"+(" throws "+throws if throws != None else "")+"""
			{
				"""+physprim+""" rv = 0;\n""";
			for byteIndex in range(bitlen/8):
				s += "rv |= (("+expr.replace("byteIndex", str(byteIndex))+") & 0xFF"+("l" if bitlen > 32 else "")+") << "+str( (byteIndex*8) if littleEndian else ((bitlen/8 - byteIndex - 1)*8) )+";\n";
			s += """return rv;
			}
			""";
		
	for logiprim, clogiprim, physprim, bitlen in oprims:
		for namePrefix, nameSuffix, args, throws, expr in oapis:
			s += """
			public static void """+namePrefix+("Little" if littleEndian else "Big")+clogiprim+nameSuffix+"("+argsdecl(args+[[physprim, "value"]])+")"+(" throws "+throws if throws != None else "")+"""
			{
			""";
			for byteIndex in range(bitlen/8):
				extractExpr = "(value >>> "+str( (byteIndex*8) if littleEndian else ((bitlen/8 - byteIndex - 1)*8) )+") & 0xFF";
				s += expr.replace("byteIndex", str(byteIndex)).replace("%", "(byte)("+extractExpr+")")+";\n";
			s += """}
			""";

s += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";





# Signed upcasters :>
for littleEndian in [True, False]:
	
	for logiprim, clogiprim, physprim, bitlen in primsExtras:
		for namePrefix, nameSuffix, args, throws, expr in iapis:
			s += """
			public static """+physprim+" "+namePrefix+("LittleS" if littleEndian else "BigS")+clogiprim+nameSuffix+"("+argsdecl(args)+")"+(" throws "+throws if throws != None else "")+"""
			{
				return signedUpcast"""+str(bitlen)+"("+namePrefix+("LittleU" if littleEndian else "BigU")+clogiprim+nameSuffix+"("+argslist(args)+"""));
			}
			""";
s += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";






# IEEE754 Floating point converters! :D

for littleEndian in [True, False]:
	for floatprim, bitprim in [["float", "int"], ["double", "long"]]:
		
		for namePrefix, nameSuffix, args, throws, expr in iapis:
			s += """
			public static """+floatprim+" "+namePrefix+("Little" if littleEndian else "Big")+capitalize(floatprim)+nameSuffix+"("+argsdecl(args)+")"+(" throws "+throws if throws != None else "")+"""
			{
				return """+capitalize(floatprim)+"."+bitprim+"BitsTo"+capitalize(floatprim)+"("+namePrefix+("Little" if littleEndian else "Big")+capitalize(bitprim)+nameSuffix+"("+argslist(args)+"""));
			}
			""";
		
		for namePrefix, nameSuffix, args, throws, expr in oapis:
			s += """
			public static void """+namePrefix+("Little" if littleEndian else "Big")+capitalize(floatprim)+nameSuffix+"("+argsdecl(args+[[floatprim, "value"]])+")"+(" throws "+throws if throws != None else "")+"""
			{
				"""+namePrefix+("Little" if littleEndian else "Big")+capitalize(bitprim)+nameSuffix+"("+argslist(args+[[bitprim, capitalize(floatprim)+"."+floatprim+"ToRaw"+capitalize(bitprim)+"Bits"+"(value)"]])+""");
			}
			""";
#

s += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";





# Runtime-endianness specification conveniences! \o/

for logiprim, clogiprim, physprim, bitlen in alliprims:
		
		for namePrefix, nameSuffix, args, throws, expr in alliapis:
			s += """
				public static """+physprim+" "+namePrefix+clogiprim+nameSuffix+"("+argsdecl(args+[["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
				{
					if (endianness == Endianness.Little)
						return """+namePrefix+"Little"+clogiprim+nameSuffix+"("+argslist(args)+""");
					else if (endianness == Endianness.Big)
						return """+namePrefix+"Big"+clogiprim+nameSuffix+"("+argslist(args)+""");
					else
						throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
				}
			""";
		

for logiprim, clogiprim, physprim, bitlen in alloprims:
		
		for namePrefix, nameSuffix, args, throws, expr in alloapis:
			s += """
				public static void """+namePrefix+clogiprim+nameSuffix+"("+argsdecl(args+[[physprim, "value"], ["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
				{
					if (endianness == Endianness.Little)
						"""+namePrefix+"Little"+clogiprim+nameSuffix+"("+argslist(args+[[physprim, "value"]])+""");
					else if (endianness == Endianness.Big)
						"""+namePrefix+"Big"+clogiprim+nameSuffix+"("+argslist(args+[[physprim, "value"]])+""");
					else
						throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
				}
			""";
		
		s += """
			public static byte[] pack"""+clogiprim+"("+argsdecl([[physprim, "value"], ["Endianness", "endianness"]])+""")
			{
				if (endianness == Endianness.Little)
					return packLittle"""+clogiprim+nameSuffix+"("+argslist([[physprim, "value"]])+""");
				else if (endianness == Endianness.Big)
					return packBig"""+clogiprim+nameSuffix+"("+argslist([[physprim, "value"]])+""");
				else
					throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
			}
		""";

s += "\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n\n";





# Array nicenesses! :D

for e in ["Little", "Big"]:
	for logiprim, clogiprim, physprim, bitlen in alloprims:
		
		s += """
			public static byte[] pack"""+e+clogiprim+"("+physprim+""" value)
			{
				byte[] dest = new byte["""+str(bitlen / 8)+"""];
				"""+namePrefix+e+clogiprim+nameSuffix+"""(dest, value);
				return dest;
			}
		""";







# Dynamic number of bytes!

for namePrefix, nameSuffix, args, throws, expr in alliapis:
	s += """
		public static long """+namePrefix+"LittleUnsigned"+nameSuffix+"("+argsdecl(args+[["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					return """+(expr.replace("byteIndex", "0"))+""" & 0xFFl;
				case 2:
					return """+namePrefix+"LittleShort"+nameSuffix+"("+argslist(args)+""") & 0xFFFFl;
				case 3:
					return """+namePrefix+"LittleUInt24"+nameSuffix+"("+argslist(args)+""") & 0xFF_FFFFl;
				case 4:
					return """+namePrefix+"LittleInt"+nameSuffix+"("+argslist(args)+""") & 0xFFFF_FFFFl;
				case 5:
					return """+namePrefix+"LittleULong40"+nameSuffix+"("+argslist(args)+""") & 0xFF_FFFF_FFFFl;
				case 6:
					return """+namePrefix+"LittleULong48"+nameSuffix+"("+argslist(args)+""") & 0xFFFF_FFFF_FFFFl;
				case 7:
					return """+namePrefix+"LittleULong56"+nameSuffix+"("+argslist(args)+""") & 0xFF_FFFF_FFFF_FFFFl;
				case 8:
					return """+namePrefix+"LittleLong"+nameSuffix+"("+argslist(args)+""");
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static long """+namePrefix+"BigUnsigned"+nameSuffix+"("+argsdecl(args+[["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					return """+(expr.replace("byteIndex", "0"))+""" & 0xFFl;
				case 2:
					return """+namePrefix+"BigShort"+nameSuffix+"("+argslist(args)+""") & 0xFFFFl;
				case 3:
					return """+namePrefix+"BigUInt24"+nameSuffix+"("+argslist(args)+""") & 0xFF_FFFFl;
				case 4:
					return """+namePrefix+"BigInt"+nameSuffix+"("+argslist(args)+""") & 0xFFFF_FFFFl;
				case 5:
					return """+namePrefix+"BigULong40"+nameSuffix+"("+argslist(args)+""") & 0xFF_FFFF_FFFFl;
				case 6:
					return """+namePrefix+"BigULong48"+nameSuffix+"("+argslist(args)+""") & 0xFFFF_FFFF_FFFFl;
				case 7:
					return """+namePrefix+"BigULong56"+nameSuffix+"("+argslist(args)+""") & 0xFF_FFFF_FFFF_FFFFl;
				case 8:
					return """+namePrefix+"BigLong"+nameSuffix+"("+argslist(args)+""");
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static long """+namePrefix+"Unsigned"+nameSuffix+"("+argsdecl(args+[["int", "numberOfBytes"], ["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			if (endianness == Endianness.Little)
				return """+namePrefix+"LittleUnsigned"+nameSuffix+"("+argslist(args+[["int", "numberOfBytes"]])+""");
			else if (endianness == Endianness.Big)
				return """+namePrefix+"BigUnsigned"+nameSuffix+"("+argslist(args+[["int", "numberOfBytes"]])+""");
			else
				throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
		}
	""";

for namePrefix, nameSuffix, args, throws, expr in alloapis:
	s += """
		public static void """+namePrefix+"Little"+nameSuffix+"("+argsdecl(args+[["long", "value"], ["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					"""+(expr.replace("byteIndex", "0").replace("%", "(byte)value"))+"""; break;
				case 2:
					"""+namePrefix+"LittleShort"+nameSuffix+"("+argslist(args)+""", (short)value); break;
				case 3:
					"""+namePrefix+"LittleInt24"+nameSuffix+"("+argslist(args)+""", (int)value); break;
				case 4:
					"""+namePrefix+"LittleInt"+nameSuffix+"("+argslist(args)+""", (int)value); break;
				case 5:
					"""+namePrefix+"LittleLong40"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				case 6:
					"""+namePrefix+"LittleLong48"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				case 7:
					"""+namePrefix+"LittleLong56"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				case 8:
					"""+namePrefix+"LittleLong"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static void """+namePrefix+"Big"+nameSuffix+"("+argsdecl(args+[["long", "value"], ["int", "numberOfBytes"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			switch (numberOfBytes)
			{
				case 1:
					"""+(expr.replace("byteIndex", "0").replace("%", "(byte)value"))+"""; break;
				case 2:
					"""+namePrefix+"BigShort"+nameSuffix+"("+argslist(args)+""", (short)value); break;
				case 3:
					"""+namePrefix+"BigInt24"+nameSuffix+"("+argslist(args)+""", (int)value); break;
				case 4:
					"""+namePrefix+"BigInt"+nameSuffix+"("+argslist(args)+""", (int)value); break;
				case 5:
					"""+namePrefix+"BigLong40"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				case 6:
					"""+namePrefix+"BigLong48"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				case 7:
					"""+namePrefix+"BigLong56"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				case 8:
					"""+namePrefix+"BigLong"+nameSuffix+"("+argslist(args)+""", (long)value); break;
				default:
					throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
			}
		}
		
		public static void """+namePrefix+nameSuffix+"("+argsdecl(args+[["long", "value"], ["int", "numberOfBytes"], ["Endianness", "endianness"]])+")"+(" throws "+throws if throws != None else "")+"""
		{
			if (endianness == Endianness.Little)
				"""+namePrefix+"Little"+nameSuffix+"("+argslist(args+[["long", "value"], ["int", "numberOfBytes"]])+""");
			else if (endianness == Endianness.Big)
				"""+namePrefix+"Big"+nameSuffix+"("+argslist(args+[["long", "value"], ["int", "numberOfBytes"]])+""");
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
	
	public static char getLittleChar(Slice<byte[]> source)
	{
		char rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(Slice<byte[]> source, int offset)
	{
		char rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(ByteList source)
	{
		char rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 0;
		rv |= ((source.getByte(1)) & 0xFF) << 8;
		return rv;
	}
	
	public static char getLittleChar(ByteList source, int offset)
	{
		char rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
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
	
	public static short getLittleShort(Slice<byte[]> source)
	{
		short rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(Slice<byte[]> source, int offset)
	{
		short rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(ByteList source)
	{
		short rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 0;
		rv |= ((source.getByte(1)) & 0xFF) << 8;
		return rv;
	}
	
	public static short getLittleShort(ByteList source, int offset)
	{
		short rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
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
	
	public static int getLittleUInt24(byte[] source)
	{
		int rv = 0;
		rv |= ((source[0]) & 0xFF) << 0;
		rv |= ((source[1]) & 0xFF) << 8;
		rv |= ((source[2]) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(byte[] source, int offset)
	{
		int rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 0;
		rv |= ((source[offset+1]) & 0xFF) << 8;
		rv |= ((source[offset+2]) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(Slice<byte[]> source)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(Slice<byte[]> source, int offset)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(ByteList source)
	{
		int rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 0;
		rv |= ((source.getByte(1)) & 0xFF) << 8;
		rv |= ((source.getByte(2)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(ByteList source, int offset)
	{
		int rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(InputStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(InputByteStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		return rv;
	}
	
	public static int getLittleUInt24(ByteBlockReadStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 0;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 16;
		return rv;
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
	
	public static int getLittleInt(Slice<byte[]> source)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFF) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFF) << 24;
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
	
	public static int getLittleInt(ByteList source)
	{
		int rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 0;
		rv |= ((source.getByte(1)) & 0xFF) << 8;
		rv |= ((source.getByte(2)) & 0xFF) << 16;
		rv |= ((source.getByte(3)) & 0xFF) << 24;
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
	
	public static long getLittleULong40(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 0;
		rv |= ((source[1]) & 0xFFl) << 8;
		rv |= ((source[2]) & 0xFFl) << 16;
		rv |= ((source[3]) & 0xFFl) << 24;
		rv |= ((source[4]) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 0;
		rv |= ((source[offset+1]) & 0xFFl) << 8;
		rv |= ((source[offset+2]) & 0xFFl) << 16;
		rv |= ((source[offset+3]) & 0xFFl) << 24;
		rv |= ((source[offset+4]) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 0;
		rv |= ((source.getByte(1)) & 0xFFl) << 8;
		rv |= ((source.getByte(2)) & 0xFFl) << 16;
		rv |= ((source.getByte(3)) & 0xFFl) << 24;
		rv |= ((source.getByte(4)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 0;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong40(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		return rv;
	}
	
	public static long getLittleULong48(byte[] source)
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
	
	public static long getLittleULong48(byte[] source, int offset)
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
	
	public static long getLittleULong48(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+5]) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleULong48(Slice<byte[]> source, int offset)
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
	
	public static long getLittleULong48(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 0;
		rv |= ((source.getByte(1)) & 0xFFl) << 8;
		rv |= ((source.getByte(2)) & 0xFFl) << 16;
		rv |= ((source.getByte(3)) & 0xFFl) << 24;
		rv |= ((source.getByte(4)) & 0xFFl) << 32;
		rv |= ((source.getByte(5)) & 0xFFl) << 40;
		return rv;
	}
	
	public static long getLittleULong48(ByteList source, int offset)
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
	
	public static long getLittleULong48(InputStream source) throws IOException, EOFException
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
	
	public static long getLittleULong48(InputByteStream source) throws IOException, EOFException
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
	
	public static long getLittleULong48(ByteBlockReadStream source) throws IOException, EOFException
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
	
	public static long getLittleULong56(byte[] source)
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
	
	public static long getLittleULong56(byte[] source, int offset)
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
	
	public static long getLittleULong56(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+5]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+6]) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleULong56(Slice<byte[]> source, int offset)
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
	
	public static long getLittleULong56(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 0;
		rv |= ((source.getByte(1)) & 0xFFl) << 8;
		rv |= ((source.getByte(2)) & 0xFFl) << 16;
		rv |= ((source.getByte(3)) & 0xFFl) << 24;
		rv |= ((source.getByte(4)) & 0xFFl) << 32;
		rv |= ((source.getByte(5)) & 0xFFl) << 40;
		rv |= ((source.getByte(6)) & 0xFFl) << 48;
		return rv;
	}
	
	public static long getLittleULong56(ByteList source, int offset)
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
	
	public static long getLittleULong56(InputStream source) throws IOException, EOFException
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
	
	public static long getLittleULong56(InputByteStream source) throws IOException, EOFException
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
	
	public static long getLittleULong56(ByteBlockReadStream source) throws IOException, EOFException
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
	
	public static long getLittleLong(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 0;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+5]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+6]) & 0xFFl) << 48;
		rv |= ((source.getUnderlying()[source.getOffset()+7]) & 0xFFl) << 56;
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
	
	public static long getLittleLong(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 0;
		rv |= ((source.getByte(1)) & 0xFFl) << 8;
		rv |= ((source.getByte(2)) & 0xFFl) << 16;
		rv |= ((source.getByte(3)) & 0xFFl) << 24;
		rv |= ((source.getByte(4)) & 0xFFl) << 32;
		rv |= ((source.getByte(5)) & 0xFFl) << 40;
		rv |= ((source.getByte(6)) & 0xFFl) << 48;
		rv |= ((source.getByte(7)) & 0xFFl) << 56;
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
	
	public static void putLittleChar(Slice<byte[]> dest, char value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleChar(Slice<byte[]> dest, int offset, char value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void setLittleChar(ByteList dest, char value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleChar(ByteList dest, int offset, char value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void addLittleChar(ByteList dest, char value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
	}
	
	public static void addLittleChar(ByteList dest, int offset, char value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
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
	
	public static void putLittleShort(Slice<byte[]> dest, short value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void putLittleShort(Slice<byte[]> dest, int offset, short value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
	}
	
	public static void setLittleShort(ByteList dest, short value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void putLittleShort(ByteList dest, int offset, short value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
	}
	
	public static void addLittleShort(ByteList dest, short value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
	}
	
	public static void addLittleShort(ByteList dest, int offset, short value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
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
	
	public static void putLittleInt24(Slice<byte[]> dest, int value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 16) & 0xFF);
	}
	
	public static void putLittleInt24(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
	}
	
	public static void setLittleInt24(ByteList dest, int value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(2, (byte)((value >>> 16) & 0xFF));
	}
	
	public static void putLittleInt24(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
	}
	
	public static void addLittleInt24(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
	}
	
	public static void addLittleInt24(ByteList dest, int offset, int value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 16) & 0xFF));
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
	
	public static void putLittleInt(Slice<byte[]> dest, int value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void putLittleInt(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
	}
	
	public static void setLittleInt(ByteList dest, int value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(3, (byte)((value >>> 24) & 0xFF));
	}
	
	public static void putLittleInt(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
	}
	
	public static void addLittleInt(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
	}
	
	public static void addLittleInt(ByteList dest, int offset, int value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 24) & 0xFF));
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
	
	public static void putLittleLong40(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 32) & 0xFF);
	}
	
	public static void putLittleLong40(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 32) & 0xFF);
	}
	
	public static void setLittleLong40(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(4, (byte)((value >>> 32) & 0xFF));
	}
	
	public static void putLittleLong40(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 32) & 0xFF));
	}
	
	public static void addLittleLong40(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
	}
	
	public static void addLittleLong40(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 32) & 0xFF));
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
	
	public static void putLittleLong48(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+5] = (byte)((value >>> 40) & 0xFF);
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
	
	public static void setLittleLong48(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(4, (byte)((value >>> 32) & 0xFF));
		dest.setByte(5, (byte)((value >>> 40) & 0xFF));
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
	
	public static void addLittleLong48(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
	}
	
	public static void addLittleLong48(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.insertByte(offset+5, (byte)((value >>> 40) & 0xFF));
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
	
	public static void putLittleLong56(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+5] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+6] = (byte)((value >>> 48) & 0xFF);
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
	
	public static void setLittleLong56(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(4, (byte)((value >>> 32) & 0xFF));
		dest.setByte(5, (byte)((value >>> 40) & 0xFF));
		dest.setByte(6, (byte)((value >>> 48) & 0xFF));
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
	
	public static void addLittleLong56(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 0) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 48) & 0xFF));
	}
	
	public static void addLittleLong56(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.insertByte(offset+5, (byte)((value >>> 40) & 0xFF));
		dest.insertByte(offset+6, (byte)((value >>> 48) & 0xFF));
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
	
	public static void putLittleLong(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 0) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+5] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+6] = (byte)((value >>> 48) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+7] = (byte)((value >>> 56) & 0xFF);
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
	
	public static void setLittleLong(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 0) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(4, (byte)((value >>> 32) & 0xFF));
		dest.setByte(5, (byte)((value >>> 40) & 0xFF));
		dest.setByte(6, (byte)((value >>> 48) & 0xFF));
		dest.setByte(7, (byte)((value >>> 56) & 0xFF));
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
	
	public static void addLittleLong(ByteList dest, long value)
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
	
	public static void addLittleLong(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 0) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 32) & 0xFF));
		dest.insertByte(offset+5, (byte)((value >>> 40) & 0xFF));
		dest.insertByte(offset+6, (byte)((value >>> 48) & 0xFF));
		dest.insertByte(offset+7, (byte)((value >>> 56) & 0xFF));
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
	
	public static char getBigChar(Slice<byte[]> source)
	{
		char rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(Slice<byte[]> source, int offset)
	{
		char rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(ByteList source)
	{
		char rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 8;
		rv |= ((source.getByte(1)) & 0xFF) << 0;
		return rv;
	}
	
	public static char getBigChar(ByteList source, int offset)
	{
		char rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 0;
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
	
	public static short getBigShort(Slice<byte[]> source)
	{
		short rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(Slice<byte[]> source, int offset)
	{
		short rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(ByteList source)
	{
		short rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 8;
		rv |= ((source.getByte(1)) & 0xFF) << 0;
		return rv;
	}
	
	public static short getBigShort(ByteList source, int offset)
	{
		short rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 0;
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
	
	public static int getBigUInt24(byte[] source)
	{
		int rv = 0;
		rv |= ((source[0]) & 0xFF) << 16;
		rv |= ((source[1]) & 0xFF) << 8;
		rv |= ((source[2]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(byte[] source, int offset)
	{
		int rv = 0;
		rv |= ((source[offset+0]) & 0xFF) << 16;
		rv |= ((source[offset+1]) & 0xFF) << 8;
		rv |= ((source[offset+2]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(Slice<byte[]> source)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(Slice<byte[]> source, int offset)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFF) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(ByteList source)
	{
		int rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 16;
		rv |= ((source.getByte(1)) & 0xFF) << 8;
		rv |= ((source.getByte(2)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(ByteList source, int offset)
	{
		int rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFF) << 16;
		rv |= ((source.getByte(offset+1)) & 0xFF) << 8;
		rv |= ((source.getByte(offset+2)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(InputStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(InputByteStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
	}
	
	public static int getBigUInt24(ByteBlockReadStream source) throws IOException, EOFException
	{
		int rv = 0;
		rv |= ((getByte(source)) & 0xFF) << 16;
		rv |= ((getByte(source)) & 0xFF) << 8;
		rv |= ((getByte(source)) & 0xFF) << 0;
		return rv;
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
	
	public static int getBigInt(Slice<byte[]> source)
	{
		int rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFF) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFF) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFF) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFF) << 0;
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
	
	public static int getBigInt(ByteList source)
	{
		int rv = 0;
		rv |= ((source.getByte(0)) & 0xFF) << 24;
		rv |= ((source.getByte(1)) & 0xFF) << 16;
		rv |= ((source.getByte(2)) & 0xFF) << 8;
		rv |= ((source.getByte(3)) & 0xFF) << 0;
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
	
	public static long getBigULong40(byte[] source)
	{
		long rv = 0;
		rv |= ((source[0]) & 0xFFl) << 32;
		rv |= ((source[1]) & 0xFFl) << 24;
		rv |= ((source[2]) & 0xFFl) << 16;
		rv |= ((source[3]) & 0xFFl) << 8;
		rv |= ((source[4]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(byte[] source, int offset)
	{
		long rv = 0;
		rv |= ((source[offset+0]) & 0xFFl) << 32;
		rv |= ((source[offset+1]) & 0xFFl) << 24;
		rv |= ((source[offset+2]) & 0xFFl) << 16;
		rv |= ((source[offset+3]) & 0xFFl) << 8;
		rv |= ((source[offset+4]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(Slice<byte[]> source, int offset)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+0]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+1]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+2]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+3]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+offset+4]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 32;
		rv |= ((source.getByte(1)) & 0xFFl) << 24;
		rv |= ((source.getByte(2)) & 0xFFl) << 16;
		rv |= ((source.getByte(3)) & 0xFFl) << 8;
		rv |= ((source.getByte(4)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(ByteList source, int offset)
	{
		long rv = 0;
		rv |= ((source.getByte(offset+0)) & 0xFFl) << 32;
		rv |= ((source.getByte(offset+1)) & 0xFFl) << 24;
		rv |= ((source.getByte(offset+2)) & 0xFFl) << 16;
		rv |= ((source.getByte(offset+3)) & 0xFFl) << 8;
		rv |= ((source.getByte(offset+4)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(InputStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(InputByteStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong40(ByteBlockReadStream source) throws IOException, EOFException
	{
		long rv = 0;
		rv |= ((getByte(source)) & 0xFFl) << 32;
		rv |= ((getByte(source)) & 0xFFl) << 24;
		rv |= ((getByte(source)) & 0xFFl) << 16;
		rv |= ((getByte(source)) & 0xFFl) << 8;
		rv |= ((getByte(source)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong48(byte[] source)
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
	
	public static long getBigULong48(byte[] source, int offset)
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
	
	public static long getBigULong48(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+5]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong48(Slice<byte[]> source, int offset)
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
	
	public static long getBigULong48(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 40;
		rv |= ((source.getByte(1)) & 0xFFl) << 32;
		rv |= ((source.getByte(2)) & 0xFFl) << 24;
		rv |= ((source.getByte(3)) & 0xFFl) << 16;
		rv |= ((source.getByte(4)) & 0xFFl) << 8;
		rv |= ((source.getByte(5)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong48(ByteList source, int offset)
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
	
	public static long getBigULong48(InputStream source) throws IOException, EOFException
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
	
	public static long getBigULong48(InputByteStream source) throws IOException, EOFException
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
	
	public static long getBigULong48(ByteBlockReadStream source) throws IOException, EOFException
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
	
	public static long getBigULong56(byte[] source)
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
	
	public static long getBigULong56(byte[] source, int offset)
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
	
	public static long getBigULong56(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 48;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+5]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+6]) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong56(Slice<byte[]> source, int offset)
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
	
	public static long getBigULong56(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 48;
		rv |= ((source.getByte(1)) & 0xFFl) << 40;
		rv |= ((source.getByte(2)) & 0xFFl) << 32;
		rv |= ((source.getByte(3)) & 0xFFl) << 24;
		rv |= ((source.getByte(4)) & 0xFFl) << 16;
		rv |= ((source.getByte(5)) & 0xFFl) << 8;
		rv |= ((source.getByte(6)) & 0xFFl) << 0;
		return rv;
	}
	
	public static long getBigULong56(ByteList source, int offset)
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
	
	public static long getBigULong56(InputStream source) throws IOException, EOFException
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
	
	public static long getBigULong56(InputByteStream source) throws IOException, EOFException
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
	
	public static long getBigULong56(ByteBlockReadStream source) throws IOException, EOFException
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
	
	public static long getBigLong(Slice<byte[]> source)
	{
		long rv = 0;
		rv |= ((source.getUnderlying()[source.getOffset()+0]) & 0xFFl) << 56;
		rv |= ((source.getUnderlying()[source.getOffset()+1]) & 0xFFl) << 48;
		rv |= ((source.getUnderlying()[source.getOffset()+2]) & 0xFFl) << 40;
		rv |= ((source.getUnderlying()[source.getOffset()+3]) & 0xFFl) << 32;
		rv |= ((source.getUnderlying()[source.getOffset()+4]) & 0xFFl) << 24;
		rv |= ((source.getUnderlying()[source.getOffset()+5]) & 0xFFl) << 16;
		rv |= ((source.getUnderlying()[source.getOffset()+6]) & 0xFFl) << 8;
		rv |= ((source.getUnderlying()[source.getOffset()+7]) & 0xFFl) << 0;
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
	
	public static long getBigLong(ByteList source)
	{
		long rv = 0;
		rv |= ((source.getByte(0)) & 0xFFl) << 56;
		rv |= ((source.getByte(1)) & 0xFFl) << 48;
		rv |= ((source.getByte(2)) & 0xFFl) << 40;
		rv |= ((source.getByte(3)) & 0xFFl) << 32;
		rv |= ((source.getByte(4)) & 0xFFl) << 24;
		rv |= ((source.getByte(5)) & 0xFFl) << 16;
		rv |= ((source.getByte(6)) & 0xFFl) << 8;
		rv |= ((source.getByte(7)) & 0xFFl) << 0;
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
	
	public static void putBigChar(Slice<byte[]> dest, char value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigChar(Slice<byte[]> dest, int offset, char value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void setBigChar(ByteList dest, char value)
	{
		dest.setByte(0, (byte)((value >>> 8) & 0xFF));
		dest.setByte(1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigChar(ByteList dest, int offset, char value)
	{
		dest.setByte(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigChar(ByteList dest, char value)
	{
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigChar(ByteList dest, int offset, char value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 0) & 0xFF));
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
	
	public static void putBigShort(Slice<byte[]> dest, short value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigShort(Slice<byte[]> dest, int offset, short value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void setBigShort(ByteList dest, short value)
	{
		dest.setByte(0, (byte)((value >>> 8) & 0xFF));
		dest.setByte(1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigShort(ByteList dest, int offset, short value)
	{
		dest.setByte(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigShort(ByteList dest, short value)
	{
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigShort(ByteList dest, int offset, short value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 0) & 0xFF));
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
	
	public static void putBigInt24(Slice<byte[]> dest, int value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt24(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void setBigInt24(ByteList dest, int value)
	{
		dest.setByte(0, (byte)((value >>> 16) & 0xFF));
		dest.setByte(1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(2, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt24(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigInt24(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigInt24(ByteList dest, int offset, int value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 0) & 0xFF));
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
	
	public static void putBigInt(Slice<byte[]> dest, int value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigInt(Slice<byte[]> dest, int offset, int value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void setBigInt(ByteList dest, int value)
	{
		dest.setByte(0, (byte)((value >>> 24) & 0xFF));
		dest.setByte(1, (byte)((value >>> 16) & 0xFF));
		dest.setByte(2, (byte)((value >>> 8) & 0xFF));
		dest.setByte(3, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigInt(ByteList dest, int offset, int value)
	{
		dest.setByte(offset+0, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigInt(ByteList dest, int value)
	{
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigInt(ByteList dest, int offset, int value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 0) & 0xFF));
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
	
	public static void putBigLong40(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void putBigLong40(Slice<byte[]> dest, int offset, long value)
	{
		dest.getUnderlying()[dest.getOffset()+offset+0] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+1] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+2] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+3] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+offset+4] = (byte)((value >>> 0) & 0xFF);
	}
	
	public static void setBigLong40(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 32) & 0xFF));
		dest.setByte(1, (byte)((value >>> 24) & 0xFF));
		dest.setByte(2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(3, (byte)((value >>> 8) & 0xFF));
		dest.setByte(4, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void putBigLong40(ByteList dest, int offset, long value)
	{
		dest.setByte(offset+0, (byte)((value >>> 32) & 0xFF));
		dest.setByte(offset+1, (byte)((value >>> 24) & 0xFF));
		dest.setByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.setByte(offset+3, (byte)((value >>> 8) & 0xFF));
		dest.setByte(offset+4, (byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigLong40(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigLong40(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 32) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 0) & 0xFF));
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
	
	public static void putBigLong48(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+5] = (byte)((value >>> 0) & 0xFF);
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
	
	public static void setBigLong48(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 40) & 0xFF));
		dest.setByte(1, (byte)((value >>> 32) & 0xFF));
		dest.setByte(2, (byte)((value >>> 24) & 0xFF));
		dest.setByte(3, (byte)((value >>> 16) & 0xFF));
		dest.setByte(4, (byte)((value >>> 8) & 0xFF));
		dest.setByte(5, (byte)((value >>> 0) & 0xFF));
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
	
	public static void addBigLong48(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigLong48(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 40) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 32) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+5, (byte)((value >>> 0) & 0xFF));
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
	
	public static void putBigLong56(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 48) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+5] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+6] = (byte)((value >>> 0) & 0xFF);
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
	
	public static void setBigLong56(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 48) & 0xFF));
		dest.setByte(1, (byte)((value >>> 40) & 0xFF));
		dest.setByte(2, (byte)((value >>> 32) & 0xFF));
		dest.setByte(3, (byte)((value >>> 24) & 0xFF));
		dest.setByte(4, (byte)((value >>> 16) & 0xFF));
		dest.setByte(5, (byte)((value >>> 8) & 0xFF));
		dest.setByte(6, (byte)((value >>> 0) & 0xFF));
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
	
	public static void addBigLong56(ByteList dest, long value)
	{
		dest.addByte((byte)((value >>> 48) & 0xFF));
		dest.addByte((byte)((value >>> 40) & 0xFF));
		dest.addByte((byte)((value >>> 32) & 0xFF));
		dest.addByte((byte)((value >>> 24) & 0xFF));
		dest.addByte((byte)((value >>> 16) & 0xFF));
		dest.addByte((byte)((value >>> 8) & 0xFF));
		dest.addByte((byte)((value >>> 0) & 0xFF));
	}
	
	public static void addBigLong56(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 48) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 40) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 32) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+5, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+6, (byte)((value >>> 0) & 0xFF));
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
	
	public static void putBigLong(Slice<byte[]> dest, long value)
	{
		dest.getUnderlying()[dest.getOffset()+0] = (byte)((value >>> 56) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+1] = (byte)((value >>> 48) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+2] = (byte)((value >>> 40) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+3] = (byte)((value >>> 32) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+4] = (byte)((value >>> 24) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+5] = (byte)((value >>> 16) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+6] = (byte)((value >>> 8) & 0xFF);
		dest.getUnderlying()[dest.getOffset()+7] = (byte)((value >>> 0) & 0xFF);
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
	
	public static void setBigLong(ByteList dest, long value)
	{
		dest.setByte(0, (byte)((value >>> 56) & 0xFF));
		dest.setByte(1, (byte)((value >>> 48) & 0xFF));
		dest.setByte(2, (byte)((value >>> 40) & 0xFF));
		dest.setByte(3, (byte)((value >>> 32) & 0xFF));
		dest.setByte(4, (byte)((value >>> 24) & 0xFF));
		dest.setByte(5, (byte)((value >>> 16) & 0xFF));
		dest.setByte(6, (byte)((value >>> 8) & 0xFF));
		dest.setByte(7, (byte)((value >>> 0) & 0xFF));
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
	
	public static void addBigLong(ByteList dest, long value)
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
	
	public static void addBigLong(ByteList dest, int offset, long value)
	{
		dest.insertByte(offset+0, (byte)((value >>> 56) & 0xFF));
		dest.insertByte(offset+1, (byte)((value >>> 48) & 0xFF));
		dest.insertByte(offset+2, (byte)((value >>> 40) & 0xFF));
		dest.insertByte(offset+3, (byte)((value >>> 32) & 0xFF));
		dest.insertByte(offset+4, (byte)((value >>> 24) & 0xFF));
		dest.insertByte(offset+5, (byte)((value >>> 16) & 0xFF));
		dest.insertByte(offset+6, (byte)((value >>> 8) & 0xFF));
		dest.insertByte(offset+7, (byte)((value >>> 0) & 0xFF));
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int getLittleSInt24(byte[] source)
	{
		return signedUpcast24(getLittleUInt24(source));
	}
	
	public static int getLittleSInt24(byte[] source, int offset)
	{
		return signedUpcast24(getLittleUInt24(source, offset));
	}
	
	public static int getLittleSInt24(Slice<byte[]> source)
	{
		return signedUpcast24(getLittleUInt24(source));
	}
	
	public static int getLittleSInt24(Slice<byte[]> source, int offset)
	{
		return signedUpcast24(getLittleUInt24(source, offset));
	}
	
	public static int getLittleSInt24(ByteList source)
	{
		return signedUpcast24(getLittleUInt24(source));
	}
	
	public static int getLittleSInt24(ByteList source, int offset)
	{
		return signedUpcast24(getLittleUInt24(source, offset));
	}
	
	public static int getLittleSInt24(InputStream source) throws IOException, EOFException
	{
		return signedUpcast24(getLittleUInt24(source));
	}
	
	public static int getLittleSInt24(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast24(getLittleUInt24(source));
	}
	
	public static int getLittleSInt24(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast24(getLittleUInt24(source));
	}
	
	public static long getLittleSLong40(byte[] source)
	{
		return signedUpcast40(getLittleULong40(source));
	}
	
	public static long getLittleSLong40(byte[] source, int offset)
	{
		return signedUpcast40(getLittleULong40(source, offset));
	}
	
	public static long getLittleSLong40(Slice<byte[]> source)
	{
		return signedUpcast40(getLittleULong40(source));
	}
	
	public static long getLittleSLong40(Slice<byte[]> source, int offset)
	{
		return signedUpcast40(getLittleULong40(source, offset));
	}
	
	public static long getLittleSLong40(ByteList source)
	{
		return signedUpcast40(getLittleULong40(source));
	}
	
	public static long getLittleSLong40(ByteList source, int offset)
	{
		return signedUpcast40(getLittleULong40(source, offset));
	}
	
	public static long getLittleSLong40(InputStream source) throws IOException, EOFException
	{
		return signedUpcast40(getLittleULong40(source));
	}
	
	public static long getLittleSLong40(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast40(getLittleULong40(source));
	}
	
	public static long getLittleSLong40(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast40(getLittleULong40(source));
	}
	
	public static long getLittleSLong48(byte[] source)
	{
		return signedUpcast48(getLittleULong48(source));
	}
	
	public static long getLittleSLong48(byte[] source, int offset)
	{
		return signedUpcast48(getLittleULong48(source, offset));
	}
	
	public static long getLittleSLong48(Slice<byte[]> source)
	{
		return signedUpcast48(getLittleULong48(source));
	}
	
	public static long getLittleSLong48(Slice<byte[]> source, int offset)
	{
		return signedUpcast48(getLittleULong48(source, offset));
	}
	
	public static long getLittleSLong48(ByteList source)
	{
		return signedUpcast48(getLittleULong48(source));
	}
	
	public static long getLittleSLong48(ByteList source, int offset)
	{
		return signedUpcast48(getLittleULong48(source, offset));
	}
	
	public static long getLittleSLong48(InputStream source) throws IOException, EOFException
	{
		return signedUpcast48(getLittleULong48(source));
	}
	
	public static long getLittleSLong48(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast48(getLittleULong48(source));
	}
	
	public static long getLittleSLong48(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast48(getLittleULong48(source));
	}
	
	public static long getLittleSLong56(byte[] source)
	{
		return signedUpcast56(getLittleULong56(source));
	}
	
	public static long getLittleSLong56(byte[] source, int offset)
	{
		return signedUpcast56(getLittleULong56(source, offset));
	}
	
	public static long getLittleSLong56(Slice<byte[]> source)
	{
		return signedUpcast56(getLittleULong56(source));
	}
	
	public static long getLittleSLong56(Slice<byte[]> source, int offset)
	{
		return signedUpcast56(getLittleULong56(source, offset));
	}
	
	public static long getLittleSLong56(ByteList source)
	{
		return signedUpcast56(getLittleULong56(source));
	}
	
	public static long getLittleSLong56(ByteList source, int offset)
	{
		return signedUpcast56(getLittleULong56(source, offset));
	}
	
	public static long getLittleSLong56(InputStream source) throws IOException, EOFException
	{
		return signedUpcast56(getLittleULong56(source));
	}
	
	public static long getLittleSLong56(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast56(getLittleULong56(source));
	}
	
	public static long getLittleSLong56(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast56(getLittleULong56(source));
	}
	
	public static int getBigSInt24(byte[] source)
	{
		return signedUpcast24(getBigUInt24(source));
	}
	
	public static int getBigSInt24(byte[] source, int offset)
	{
		return signedUpcast24(getBigUInt24(source, offset));
	}
	
	public static int getBigSInt24(Slice<byte[]> source)
	{
		return signedUpcast24(getBigUInt24(source));
	}
	
	public static int getBigSInt24(Slice<byte[]> source, int offset)
	{
		return signedUpcast24(getBigUInt24(source, offset));
	}
	
	public static int getBigSInt24(ByteList source)
	{
		return signedUpcast24(getBigUInt24(source));
	}
	
	public static int getBigSInt24(ByteList source, int offset)
	{
		return signedUpcast24(getBigUInt24(source, offset));
	}
	
	public static int getBigSInt24(InputStream source) throws IOException, EOFException
	{
		return signedUpcast24(getBigUInt24(source));
	}
	
	public static int getBigSInt24(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast24(getBigUInt24(source));
	}
	
	public static int getBigSInt24(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast24(getBigUInt24(source));
	}
	
	public static long getBigSLong40(byte[] source)
	{
		return signedUpcast40(getBigULong40(source));
	}
	
	public static long getBigSLong40(byte[] source, int offset)
	{
		return signedUpcast40(getBigULong40(source, offset));
	}
	
	public static long getBigSLong40(Slice<byte[]> source)
	{
		return signedUpcast40(getBigULong40(source));
	}
	
	public static long getBigSLong40(Slice<byte[]> source, int offset)
	{
		return signedUpcast40(getBigULong40(source, offset));
	}
	
	public static long getBigSLong40(ByteList source)
	{
		return signedUpcast40(getBigULong40(source));
	}
	
	public static long getBigSLong40(ByteList source, int offset)
	{
		return signedUpcast40(getBigULong40(source, offset));
	}
	
	public static long getBigSLong40(InputStream source) throws IOException, EOFException
	{
		return signedUpcast40(getBigULong40(source));
	}
	
	public static long getBigSLong40(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast40(getBigULong40(source));
	}
	
	public static long getBigSLong40(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast40(getBigULong40(source));
	}
	
	public static long getBigSLong48(byte[] source)
	{
		return signedUpcast48(getBigULong48(source));
	}
	
	public static long getBigSLong48(byte[] source, int offset)
	{
		return signedUpcast48(getBigULong48(source, offset));
	}
	
	public static long getBigSLong48(Slice<byte[]> source)
	{
		return signedUpcast48(getBigULong48(source));
	}
	
	public static long getBigSLong48(Slice<byte[]> source, int offset)
	{
		return signedUpcast48(getBigULong48(source, offset));
	}
	
	public static long getBigSLong48(ByteList source)
	{
		return signedUpcast48(getBigULong48(source));
	}
	
	public static long getBigSLong48(ByteList source, int offset)
	{
		return signedUpcast48(getBigULong48(source, offset));
	}
	
	public static long getBigSLong48(InputStream source) throws IOException, EOFException
	{
		return signedUpcast48(getBigULong48(source));
	}
	
	public static long getBigSLong48(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast48(getBigULong48(source));
	}
	
	public static long getBigSLong48(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast48(getBigULong48(source));
	}
	
	public static long getBigSLong56(byte[] source)
	{
		return signedUpcast56(getBigULong56(source));
	}
	
	public static long getBigSLong56(byte[] source, int offset)
	{
		return signedUpcast56(getBigULong56(source, offset));
	}
	
	public static long getBigSLong56(Slice<byte[]> source)
	{
		return signedUpcast56(getBigULong56(source));
	}
	
	public static long getBigSLong56(Slice<byte[]> source, int offset)
	{
		return signedUpcast56(getBigULong56(source, offset));
	}
	
	public static long getBigSLong56(ByteList source)
	{
		return signedUpcast56(getBigULong56(source));
	}
	
	public static long getBigSLong56(ByteList source, int offset)
	{
		return signedUpcast56(getBigULong56(source, offset));
	}
	
	public static long getBigSLong56(InputStream source) throws IOException, EOFException
	{
		return signedUpcast56(getBigULong56(source));
	}
	
	public static long getBigSLong56(InputByteStream source) throws IOException, EOFException
	{
		return signedUpcast56(getBigULong56(source));
	}
	
	public static long getBigSLong56(ByteBlockReadStream source) throws IOException, EOFException
	{
		return signedUpcast56(getBigULong56(source));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static float getLittleFloat(byte[] source)
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static float getLittleFloat(byte[] source, int offset)
	{
		return Float.intBitsToFloat(getLittleInt(source, offset));
	}
	
	public static float getLittleFloat(Slice<byte[]> source)
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static float getLittleFloat(Slice<byte[]> source, int offset)
	{
		return Float.intBitsToFloat(getLittleInt(source, offset));
	}
	
	public static float getLittleFloat(ByteList source)
	{
		return Float.intBitsToFloat(getLittleInt(source));
	}
	
	public static float getLittleFloat(ByteList source, int offset)
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
	
	public static void putLittleFloat(Slice<byte[]> dest, float value)
	{
		putLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(Slice<byte[]> dest, int offset, float value)
	{
		putLittleInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void setLittleFloat(ByteList dest, float value)
	{
		setLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putLittleFloat(ByteList dest, int offset, float value)
	{
		putLittleInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void addLittleFloat(ByteList dest, float value)
	{
		addLittleInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void addLittleFloat(ByteList dest, int offset, float value)
	{
		addLittleInt(dest, offset, Float.floatToRawIntBits(value));
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
	
	public static double getLittleDouble(Slice<byte[]> source)
	{
		return Double.longBitsToDouble(getLittleLong(source));
	}
	
	public static double getLittleDouble(Slice<byte[]> source, int offset)
	{
		return Double.longBitsToDouble(getLittleLong(source, offset));
	}
	
	public static double getLittleDouble(ByteList source)
	{
		return Double.longBitsToDouble(getLittleLong(source));
	}
	
	public static double getLittleDouble(ByteList source, int offset)
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
	
	public static void putLittleDouble(Slice<byte[]> dest, double value)
	{
		putLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(Slice<byte[]> dest, int offset, double value)
	{
		putLittleLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void setLittleDouble(ByteList dest, double value)
	{
		setLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putLittleDouble(ByteList dest, int offset, double value)
	{
		putLittleLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void addLittleDouble(ByteList dest, double value)
	{
		addLittleLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void addLittleDouble(ByteList dest, int offset, double value)
	{
		addLittleLong(dest, offset, Double.doubleToRawLongBits(value));
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
	
	public static float getBigFloat(Slice<byte[]> source)
	{
		return Float.intBitsToFloat(getBigInt(source));
	}
	
	public static float getBigFloat(Slice<byte[]> source, int offset)
	{
		return Float.intBitsToFloat(getBigInt(source, offset));
	}
	
	public static float getBigFloat(ByteList source)
	{
		return Float.intBitsToFloat(getBigInt(source));
	}
	
	public static float getBigFloat(ByteList source, int offset)
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
	
	public static void putBigFloat(Slice<byte[]> dest, float value)
	{
		putBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(Slice<byte[]> dest, int offset, float value)
	{
		putBigInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void setBigFloat(ByteList dest, float value)
	{
		setBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void putBigFloat(ByteList dest, int offset, float value)
	{
		putBigInt(dest, offset, Float.floatToRawIntBits(value));
	}
	
	public static void addBigFloat(ByteList dest, float value)
	{
		addBigInt(dest, Float.floatToRawIntBits(value));
	}
	
	public static void addBigFloat(ByteList dest, int offset, float value)
	{
		addBigInt(dest, offset, Float.floatToRawIntBits(value));
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
	
	public static double getBigDouble(Slice<byte[]> source)
	{
		return Double.longBitsToDouble(getBigLong(source));
	}
	
	public static double getBigDouble(Slice<byte[]> source, int offset)
	{
		return Double.longBitsToDouble(getBigLong(source, offset));
	}
	
	public static double getBigDouble(ByteList source)
	{
		return Double.longBitsToDouble(getBigLong(source));
	}
	
	public static double getBigDouble(ByteList source, int offset)
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
	
	public static void putBigDouble(Slice<byte[]> dest, double value)
	{
		putBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(Slice<byte[]> dest, int offset, double value)
	{
		putBigLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void setBigDouble(ByteList dest, double value)
	{
		setBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void putBigDouble(ByteList dest, int offset, double value)
	{
		putBigLong(dest, offset, Double.doubleToRawLongBits(value));
	}
	
	public static void addBigDouble(ByteList dest, double value)
	{
		addBigLong(dest, Double.doubleToRawLongBits(value));
	}
	
	public static void addBigDouble(ByteList dest, int offset, double value)
	{
		addBigLong(dest, offset, Double.doubleToRawLongBits(value));
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
	
	public static char getChar(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source);
		else if (endianness == Endianness.Big)
			return getBigChar(source);
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
	
	public static char getChar(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleChar(source);
		else if (endianness == Endianness.Big)
			return getBigChar(source);
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
	
	public static short getShort(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source);
		else if (endianness == Endianness.Big)
			return getBigShort(source);
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
	
	public static short getShort(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleShort(source);
		else if (endianness == Endianness.Big)
			return getBigShort(source);
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
	
	public static int getUInt24(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigUInt24(source, offset);
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
	
	public static int getInt(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source);
		else if (endianness == Endianness.Big)
			return getBigInt(source);
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
	
	public static int getInt(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleInt(source);
		else if (endianness == Endianness.Big)
			return getBigInt(source);
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
	
	public static long getULong40(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source);
		else if (endianness == Endianness.Big)
			return getBigULong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source);
		else if (endianness == Endianness.Big)
			return getBigULong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source);
		else if (endianness == Endianness.Big)
			return getBigULong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source);
		else if (endianness == Endianness.Big)
			return getBigULong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source);
		else if (endianness == Endianness.Big)
			return getBigULong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source);
		else if (endianness == Endianness.Big)
			return getBigULong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source);
		else if (endianness == Endianness.Big)
			return getBigULong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source);
		else if (endianness == Endianness.Big)
			return getBigULong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source);
		else if (endianness == Endianness.Big)
			return getBigULong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source);
		else if (endianness == Endianness.Big)
			return getBigULong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source);
		else if (endianness == Endianness.Big)
			return getBigULong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source);
		else if (endianness == Endianness.Big)
			return getBigULong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source);
		else if (endianness == Endianness.Big)
			return getBigULong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source);
		else if (endianness == Endianness.Big)
			return getBigULong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source);
		else if (endianness == Endianness.Big)
			return getBigULong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source);
		else if (endianness == Endianness.Big)
			return getBigULong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source);
		else if (endianness == Endianness.Big)
			return getBigULong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source);
		else if (endianness == Endianness.Big)
			return getBigULong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source);
		else if (endianness == Endianness.Big)
			return getBigULong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source);
		else if (endianness == Endianness.Big)
			return getBigULong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source);
		else if (endianness == Endianness.Big)
			return getBigULong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleULong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigULong56(source, offset);
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
	
	public static long getLong(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source);
		else if (endianness == Endianness.Big)
			return getBigLong(source);
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
	
	public static long getLong(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleLong(source);
		else if (endianness == Endianness.Big)
			return getBigLong(source);
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
	
	public static float getFloat(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source);
		else if (endianness == Endianness.Big)
			return getBigFloat(source);
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
	
	public static float getFloat(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleFloat(source);
		else if (endianness == Endianness.Big)
			return getBigFloat(source);
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
	
	public static double getDouble(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source);
		else if (endianness == Endianness.Big)
			return getBigDouble(source);
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
	
	public static double getDouble(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleDouble(source);
		else if (endianness == Endianness.Big)
			return getBigDouble(source);
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
	
	public static int getSInt24(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static int getSInt24(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSInt24(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSInt24(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong40(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong40(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong40(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong48(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong48(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong48(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(byte[] source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(byte[] source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(Slice<byte[]> source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(Slice<byte[]> source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(ByteList source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(ByteList source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source, offset);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(InputStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(InputByteStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(ByteBlockReadStream source, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(ByteBuffer source, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getSLong56(ByteBuffer source, int offset, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleSLong56(source, offset);
		else if (endianness == Endianness.Big)
			return getBigSLong56(source, offset);
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
	
	public static void putChar(Slice<byte[]> dest, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			putBigChar(dest, value);
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
	
	public static void setChar(ByteList dest, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			setBigChar(dest, value);
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
	
	public static void addChar(ByteList dest, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleChar(dest, value);
		else if (endianness == Endianness.Big)
			addBigChar(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addChar(ByteList dest, int offset, char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleChar(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigChar(dest, offset, value);
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
	
	public static byte[] packChar(char value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleChar(value);
		else if (endianness == Endianness.Big)
			return packBigChar(value);
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
	
	public static void putShort(Slice<byte[]> dest, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			putBigShort(dest, value);
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
	
	public static void setShort(ByteList dest, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			setBigShort(dest, value);
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
	
	public static void addShort(ByteList dest, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleShort(dest, value);
		else if (endianness == Endianness.Big)
			addBigShort(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addShort(ByteList dest, int offset, short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleShort(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigShort(dest, offset, value);
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
	
	public static byte[] packShort(short value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleShort(value);
		else if (endianness == Endianness.Big)
			return packBigShort(value);
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
	
	public static void putInt24(Slice<byte[]> dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt24(dest, value);
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
	
	public static void setInt24(ByteList dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			setBigInt24(dest, value);
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
	
	public static void addInt24(ByteList dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleInt24(dest, value);
		else if (endianness == Endianness.Big)
			addBigInt24(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addInt24(ByteList dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleInt24(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigInt24(dest, offset, value);
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
	
	public static byte[] packInt24(int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleInt24(value);
		else if (endianness == Endianness.Big)
			return packBigInt24(value);
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
	
	public static void putInt(Slice<byte[]> dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			putBigInt(dest, value);
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
	
	public static void setInt(ByteList dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			setBigInt(dest, value);
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
	
	public static void addInt(ByteList dest, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleInt(dest, value);
		else if (endianness == Endianness.Big)
			addBigInt(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addInt(ByteList dest, int offset, int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleInt(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigInt(dest, offset, value);
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
	
	public static byte[] packInt(int value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleInt(value);
		else if (endianness == Endianness.Big)
			return packBigInt(value);
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
	
	public static void putLong40(Slice<byte[]> dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong40(dest, value);
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
	
	public static void setLong40(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			setBigLong40(dest, value);
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
	
	public static void addLong40(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong40(dest, value);
		else if (endianness == Endianness.Big)
			addBigLong40(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addLong40(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong40(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigLong40(dest, offset, value);
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
	
	public static byte[] packLong40(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong40(value);
		else if (endianness == Endianness.Big)
			return packBigLong40(value);
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
	
	public static void putLong48(Slice<byte[]> dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong48(dest, value);
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
	
	public static void setLong48(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			setBigLong48(dest, value);
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
	
	public static void addLong48(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong48(dest, value);
		else if (endianness == Endianness.Big)
			addBigLong48(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addLong48(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong48(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigLong48(dest, offset, value);
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
	
	public static byte[] packLong48(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong48(value);
		else if (endianness == Endianness.Big)
			return packBigLong48(value);
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
	
	public static void putLong56(Slice<byte[]> dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong56(dest, value);
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
	
	public static void setLong56(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			setBigLong56(dest, value);
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
	
	public static void addLong56(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong56(dest, value);
		else if (endianness == Endianness.Big)
			addBigLong56(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addLong56(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong56(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigLong56(dest, offset, value);
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
	
	public static byte[] packLong56(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong56(value);
		else if (endianness == Endianness.Big)
			return packBigLong56(value);
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
	
	public static void putLong(Slice<byte[]> dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			putBigLong(dest, value);
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
	
	public static void setLong(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			setBigLong(dest, value);
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
	
	public static void addLong(ByteList dest, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong(dest, value);
		else if (endianness == Endianness.Big)
			addBigLong(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addLong(ByteList dest, int offset, long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleLong(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigLong(dest, offset, value);
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
	
	public static byte[] packLong(long value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleLong(value);
		else if (endianness == Endianness.Big)
			return packBigLong(value);
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
	
	public static void putFloat(Slice<byte[]> dest, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			putBigFloat(dest, value);
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
	
	public static void setFloat(ByteList dest, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			setBigFloat(dest, value);
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
	
	public static void addFloat(ByteList dest, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleFloat(dest, value);
		else if (endianness == Endianness.Big)
			addBigFloat(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addFloat(ByteList dest, int offset, float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleFloat(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigFloat(dest, offset, value);
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
	
	public static byte[] packFloat(float value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return packLittleFloat(value);
		else if (endianness == Endianness.Big)
			return packBigFloat(value);
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
	
	public static void putDouble(Slice<byte[]> dest, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			putBigDouble(dest, value);
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
	
	public static void setDouble(ByteList dest, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			setBigDouble(dest, value);
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
	
	public static void addDouble(ByteList dest, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleDouble(dest, value);
		else if (endianness == Endianness.Big)
			addBigDouble(dest, value);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addDouble(ByteList dest, int offset, double value, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittleDouble(dest, offset, value);
		else if (endianness == Endianness.Big)
			addBigDouble(dest, offset, value);
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
	
	public static long getLittleUnsigned(byte[] source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[0] & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(byte[] source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[0] & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(byte[] source, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(byte[] source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[offset+0] & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(byte[] source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source[offset+0] & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(byte[] source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, offset, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(Slice<byte[]> source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getUnderlying()[source.getOffset()+0] & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(Slice<byte[]> source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getUnderlying()[source.getOffset()+0] & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(Slice<byte[]> source, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(Slice<byte[]> source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getUnderlying()[source.getOffset()+offset+0] & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(Slice<byte[]> source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getUnderlying()[source.getOffset()+offset+0] & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(Slice<byte[]> source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, offset, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(ByteList source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getByte(0) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(ByteList source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getByte(0) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(ByteList source, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(ByteList source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getByte(offset+0) & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(ByteList source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.getByte(offset+0) & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(ByteList source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, offset, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(InputStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(InputStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(InputStream source, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(InputByteStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(InputByteStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(InputByteStream source, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(ByteBlockReadStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(ByteBlockReadStream source, int numberOfBytes) throws IOException, EOFException
	{
		switch (numberOfBytes)
		{
			case 1:
				return getByte(source) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(ByteBlockReadStream source, int numberOfBytes, Endianness endianness) throws IOException, EOFException
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(ByteBuffer source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(0) & 0xFFl;
			case 2:
				return getLittleShort(source) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(ByteBuffer source, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(0) & 0xFFl;
			case 2:
				return getBigShort(source) & 0xFFFFl;
			case 3:
				return getBigUInt24(source) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(ByteBuffer source, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static long getLittleUnsigned(ByteBuffer source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(offset+0) & 0xFFl;
			case 2:
				return getLittleShort(source, offset) & 0xFFFFl;
			case 3:
				return getLittleUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getLittleInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getLittleULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getLittleULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getLittleULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getLittleLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getBigUnsigned(ByteBuffer source, int offset, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				return source.get(offset+0) & 0xFFl;
			case 2:
				return getBigShort(source, offset) & 0xFFFFl;
			case 3:
				return getBigUInt24(source, offset) & 0xFF_FFFFl;
			case 4:
				return getBigInt(source, offset) & 0xFFFF_FFFFl;
			case 5:
				return getBigULong40(source, offset) & 0xFF_FFFF_FFFFl;
			case 6:
				return getBigULong48(source, offset) & 0xFFFF_FFFF_FFFFl;
			case 7:
				return getBigULong56(source, offset) & 0xFF_FFFF_FFFF_FFFFl;
			case 8:
				return getBigLong(source, offset);
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static long getUnsigned(ByteBuffer source, int offset, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			return getLittleUnsigned(source, offset, numberOfBytes);
		else if (endianness == Endianness.Big)
			return getBigUnsigned(source, offset, numberOfBytes);
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
				putLittleLong40(dest, (long)value); break;
			case 6:
				putLittleLong48(dest, (long)value); break;
			case 7:
				putLittleLong56(dest, (long)value); break;
			case 8:
				putLittleLong(dest, (long)value); break;
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
				putBigLong40(dest, (long)value); break;
			case 6:
				putBigLong48(dest, (long)value); break;
			case 7:
				putBigLong56(dest, (long)value); break;
			case 8:
				putBigLong(dest, (long)value); break;
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
				putLittleLong40(dest, offset, (long)value); break;
			case 6:
				putLittleLong48(dest, offset, (long)value); break;
			case 7:
				putLittleLong56(dest, offset, (long)value); break;
			case 8:
				putLittleLong(dest, offset, (long)value); break;
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
				putBigLong40(dest, offset, (long)value); break;
			case 6:
				putBigLong48(dest, offset, (long)value); break;
			case 7:
				putBigLong56(dest, offset, (long)value); break;
			case 8:
				putBigLong(dest, offset, (long)value); break;
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
	
	public static void putLittle(Slice<byte[]> dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.getUnderlying()[dest.getOffset()+0] = (byte)value; break;
			case 2:
				putLittleShort(dest, (short)value); break;
			case 3:
				putLittleInt24(dest, (int)value); break;
			case 4:
				putLittleInt(dest, (int)value); break;
			case 5:
				putLittleLong40(dest, (long)value); break;
			case 6:
				putLittleLong48(dest, (long)value); break;
			case 7:
				putLittleLong56(dest, (long)value); break;
			case 8:
				putLittleLong(dest, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void putBig(Slice<byte[]> dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.getUnderlying()[dest.getOffset()+0] = (byte)value; break;
			case 2:
				putBigShort(dest, (short)value); break;
			case 3:
				putBigInt24(dest, (int)value); break;
			case 4:
				putBigInt(dest, (int)value); break;
			case 5:
				putBigLong40(dest, (long)value); break;
			case 6:
				putBigLong48(dest, (long)value); break;
			case 7:
				putBigLong56(dest, (long)value); break;
			case 8:
				putBigLong(dest, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void put(Slice<byte[]> dest, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			putLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			putBig(dest, value, numberOfBytes);
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
				putLittleLong40(dest, offset, (long)value); break;
			case 6:
				putLittleLong48(dest, offset, (long)value); break;
			case 7:
				putLittleLong56(dest, offset, (long)value); break;
			case 8:
				putLittleLong(dest, offset, (long)value); break;
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
				putBigLong40(dest, offset, (long)value); break;
			case 6:
				putBigLong48(dest, offset, (long)value); break;
			case 7:
				putBigLong56(dest, offset, (long)value); break;
			case 8:
				putBigLong(dest, offset, (long)value); break;
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
	
	public static void setLittle(ByteList dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.setByte(0, (byte)value); break;
			case 2:
				setLittleShort(dest, (short)value); break;
			case 3:
				setLittleInt24(dest, (int)value); break;
			case 4:
				setLittleInt(dest, (int)value); break;
			case 5:
				setLittleLong40(dest, (long)value); break;
			case 6:
				setLittleLong48(dest, (long)value); break;
			case 7:
				setLittleLong56(dest, (long)value); break;
			case 8:
				setLittleLong(dest, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void setBig(ByteList dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.setByte(0, (byte)value); break;
			case 2:
				setBigShort(dest, (short)value); break;
			case 3:
				setBigInt24(dest, (int)value); break;
			case 4:
				setBigInt(dest, (int)value); break;
			case 5:
				setBigLong40(dest, (long)value); break;
			case 6:
				setBigLong48(dest, (long)value); break;
			case 7:
				setBigLong56(dest, (long)value); break;
			case 8:
				setBigLong(dest, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void set(ByteList dest, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			setLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			setBig(dest, value, numberOfBytes);
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
				putLittleLong40(dest, offset, (long)value); break;
			case 6:
				putLittleLong48(dest, offset, (long)value); break;
			case 7:
				putLittleLong56(dest, offset, (long)value); break;
			case 8:
				putLittleLong(dest, offset, (long)value); break;
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
				putBigLong40(dest, offset, (long)value); break;
			case 6:
				putBigLong48(dest, offset, (long)value); break;
			case 7:
				putBigLong56(dest, offset, (long)value); break;
			case 8:
				putBigLong(dest, offset, (long)value); break;
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
	
	public static void addLittle(ByteList dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.addByte((byte)value); break;
			case 2:
				addLittleShort(dest, (short)value); break;
			case 3:
				addLittleInt24(dest, (int)value); break;
			case 4:
				addLittleInt(dest, (int)value); break;
			case 5:
				addLittleLong40(dest, (long)value); break;
			case 6:
				addLittleLong48(dest, (long)value); break;
			case 7:
				addLittleLong56(dest, (long)value); break;
			case 8:
				addLittleLong(dest, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void addBig(ByteList dest, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.addByte((byte)value); break;
			case 2:
				addBigShort(dest, (short)value); break;
			case 3:
				addBigInt24(dest, (int)value); break;
			case 4:
				addBigInt(dest, (int)value); break;
			case 5:
				addBigLong40(dest, (long)value); break;
			case 6:
				addBigLong48(dest, (long)value); break;
			case 7:
				addBigLong56(dest, (long)value); break;
			case 8:
				addBigLong(dest, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void add(ByteList dest, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittle(dest, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			addBig(dest, value, numberOfBytes);
		else
			throw newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(endianness);
	}
	
	public static void addLittle(ByteList dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.insertByte(offset+0, (byte)value); break;
			case 2:
				addLittleShort(dest, offset, (short)value); break;
			case 3:
				addLittleInt24(dest, offset, (int)value); break;
			case 4:
				addLittleInt(dest, offset, (int)value); break;
			case 5:
				addLittleLong40(dest, offset, (long)value); break;
			case 6:
				addLittleLong48(dest, offset, (long)value); break;
			case 7:
				addLittleLong56(dest, offset, (long)value); break;
			case 8:
				addLittleLong(dest, offset, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void addBig(ByteList dest, int offset, long value, int numberOfBytes)
	{
		switch (numberOfBytes)
		{
			case 1:
				dest.insertByte(offset+0, (byte)value); break;
			case 2:
				addBigShort(dest, offset, (short)value); break;
			case 3:
				addBigInt24(dest, offset, (int)value); break;
			case 4:
				addBigInt(dest, offset, (int)value); break;
			case 5:
				addBigLong40(dest, offset, (long)value); break;
			case 6:
				addBigLong48(dest, offset, (long)value); break;
			case 7:
				addBigLong56(dest, offset, (long)value); break;
			case 8:
				addBigLong(dest, offset, (long)value); break;
			default:
				throw numberOfBytes <= 0 ? new IllegalArgumentException("Invalid number of bytes!: "+numberOfBytes) : new UnsupportedOperationException("Unsupported number of bytes: "+numberOfBytes);
		}
	}
	
	public static void add(ByteList dest, int offset, long value, int numberOfBytes, Endianness endianness)
	{
		if (endianness == Endianness.Little)
			addLittle(dest, offset, value, numberOfBytes);
		else if (endianness == Endianness.Big)
			addBig(dest, offset, value, numberOfBytes);
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
				putLittleLong40(dest, (long)value); break;
			case 6:
				putLittleLong48(dest, (long)value); break;
			case 7:
				putLittleLong56(dest, (long)value); break;
			case 8:
				putLittleLong(dest, (long)value); break;
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
				putBigLong40(dest, (long)value); break;
			case 6:
				putBigLong48(dest, (long)value); break;
			case 7:
				putBigLong56(dest, (long)value); break;
			case 8:
				putBigLong(dest, (long)value); break;
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
				putLittleLong40(dest, (long)value); break;
			case 6:
				putLittleLong48(dest, (long)value); break;
			case 7:
				putLittleLong56(dest, (long)value); break;
			case 8:
				putLittleLong(dest, (long)value); break;
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
				putBigLong40(dest, (long)value); break;
			case 6:
				putBigLong48(dest, (long)value); break;
			case 7:
				putBigLong56(dest, (long)value); break;
			case 8:
				putBigLong(dest, (long)value); break;
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
				putLittleLong40(dest, (long)value); break;
			case 6:
				putLittleLong48(dest, (long)value); break;
			case 7:
				putLittleLong56(dest, (long)value); break;
			case 8:
				putLittleLong(dest, (long)value); break;
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
				putBigLong40(dest, (long)value); break;
			case 6:
				putBigLong48(dest, (long)value); break;
			case 7:
				putBigLong56(dest, (long)value); break;
			case 8:
				putBigLong(dest, (long)value); break;
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
				putLittleLong40(dest, (long)value); break;
			case 6:
				putLittleLong48(dest, (long)value); break;
			case 7:
				putLittleLong56(dest, (long)value); break;
			case 8:
				putLittleLong(dest, (long)value); break;
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
				putBigLong40(dest, (long)value); break;
			case 6:
				putBigLong48(dest, (long)value); break;
			case 7:
				putBigLong56(dest, (long)value); break;
			case 8:
				putBigLong(dest, (long)value); break;
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
				putLittleLong40(dest, offset, (long)value); break;
			case 6:
				putLittleLong48(dest, offset, (long)value); break;
			case 7:
				putLittleLong56(dest, offset, (long)value); break;
			case 8:
				putLittleLong(dest, offset, (long)value); break;
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
				putBigLong40(dest, offset, (long)value); break;
			case 6:
				putBigLong48(dest, offset, (long)value); break;
			case 7:
				putBigLong56(dest, offset, (long)value); break;
			case 8:
				putBigLong(dest, offset, (long)value); break;
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
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int getLittleUInt24Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		int rv = source.getShort() & 0xFFFF;
		rv |= (source.get() & 0xFF) << 16;
		return rv;
	}
	
	public static int getBigUInt24Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		int rv = (source.getShort() & 0xFFFF) << 8;
		rv |= source.get() & 0xFF;
		return rv;
	}
	
	
	public static int getLittleUInt24Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		int rv = source.getShort(offset) & 0xFFFF;
		rv |= (source.get(offset+2) & 0xFF) << 16;
		return rv;
	}
	
	public static int getBigUInt24Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		int rv = (source.getShort(offset) & 0xFFFF) << 8;
		rv |= source.get(offset+2) & 0xFF;
		return rv;
	}
	
	
	
	
	public static void putLittleInt24Buffermodifying(ByteBuffer source, int value)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putShort((short)value);
		source.put((byte)(value >>> 16));
	}
	
	public static void putBigInt24Buffermodifying(ByteBuffer source, int value)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putShort((short)(value >>> 8));
		source.put((byte)(value));
	}
	
	
	public static void putLittleInt24Buffermodifying(ByteBuffer source, int offset, int value)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putShort(offset, (short)value);
		source.put(offset+2, (byte)(value >>> 16));
	}
	
	public static void putBigInt24Buffermodifying(ByteBuffer source, int offset, int value)
	{
		//Note that we can't just use ints and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putShort(offset, (short)(value >>> 8));
		source.put(offset+2, (byte)(value));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static long getLittleULong40Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		long rv = source.getInt() & 0xFFFF_FFFFl;
		rv |= (source.get() & 0xFFl) << 32l;
		return rv;
	}
	
	public static long getBigULong40Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		long rv = (source.getInt() & 0xFFFF_FFFFl) << 8l;
		rv |= source.get() & 0xFFl;
		return rv;
	}
	
	
	public static long getLittleULong40Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		long rv = source.getInt(offset) & 0xFFFF_FFFFl;
		rv |= (source.get(offset+4) & 0xFFl) << 32l;
		return rv;
	}
	
	public static long getBigULong40Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		long rv = (source.getInt(offset) & 0xFFFF_FFFFl) << 8l;
		rv |= source.get(offset+4) & 0xFFl;
		return rv;
	}
	
	
	
	
	public static void putLittleLong40Buffermodifying(ByteBuffer source, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt((int)value);
		source.put((byte)(value >>> 32));
	}
	
	public static void putBigLong40Buffermodifying(ByteBuffer source, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt((int)(value >>> 8));
		source.put((byte)(value));
	}
	
	
	public static void putLittleLong40Buffermodifying(ByteBuffer source, int offset, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt(offset, (int)value);
		source.put(offset+4, (byte)(value >>> 32));
	}
	
	public static void putBigLong40Buffermodifying(ByteBuffer source, int offset, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt(offset, (int)(value >>> 8));
		source.put(offset+4, (byte)(value));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static long getLittleULong48Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		long rv = source.getInt() & 0xFFFF_FFFFl;
		rv |= (source.getShort() & 0xFFFFl) << 32l;
		return rv;
	}
	
	public static long getBigULong48Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		long rv = (source.getInt() & 0xFFFF_FFFFl) << 16l;
		rv |= source.getShort() & 0xFFFFl;
		return rv;
	}
	
	
	public static long getLittleULong48Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		long rv = source.getInt(offset) & 0xFFFF_FFFFl;
		rv |= (source.getShort(offset+4) & 0xFFFFl) << 32l;
		return rv;
	}
	
	public static long getBigULong48Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		long rv = (source.getInt(offset) & 0xFFFF_FFFFl) << 16l;
		rv |= source.getShort(offset+4) & 0xFFFFl;
		return rv;
	}
	
	
	
	
	public static void putLittleLong48Buffermodifying(ByteBuffer source, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt((int)value);
		source.putShort((short)(value >>> 32));
	}
	
	public static void putBigLong48Buffermodifying(ByteBuffer source, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt((int)(value >>> 16));
		source.putShort((short)(value));
	}
	
	
	public static void putLittleLong48Buffermodifying(ByteBuffer source, int offset, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt(offset, (int)value);
		source.putShort(offset+4, (short)(value >>> 32));
	}
	
	public static void putBigLong48Buffermodifying(ByteBuffer source, int offset, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt(offset, (int)(value >>> 16));
		source.putShort(offset+4, (short)(value));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static long getLittleULong56Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		long rv = source.getInt() & 0xFFFF_FFFFl;
		rv |= (source.getShort() & 0xFFFFl) << 32l;
		rv |= (source.get() & 0xFFl) << 48l;
		return rv;
	}
	
	public static long getBigULong56Buffermodifying(ByteBuffer source)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		long rv = (source.getInt() & 0xFFFF_FFFFl) << 24l;
		rv |= (source.getShort() & 0xFFFFl) << 8l;
		rv |= source.get() & 0xFFl;
		return rv;
	}
	
	
	public static long getLittleULong56Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		long rv = source.getInt(offset) & 0xFFFF_FFFFl;
		rv |= (source.getShort(offset+4) & 0xFFFFl) << 32l;
		rv |= (source.get(offset+6) & 0xFFl) << 48l;
		return rv;
	}
	
	public static long getBigULong56Buffermodifying(ByteBuffer source, int offset)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		long rv = (source.getInt(offset) & 0xFFFF_FFFFl) << 24l;
		rv |= (source.getShort(offset+4) & 0xFFFFl) << 8l;
		rv |= source.get(offset+6) & 0xFFl;
		return rv;
	}
	
	
	
	
	public static void putLittleLong56Buffermodifying(ByteBuffer source, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt((int)value);
		source.putShort((short)(value >>> 32));
		source.put((byte)(value >>> 48));
	}
	
	public static void putBigLong56Buffermodifying(ByteBuffer source, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt((int)(value >>> 24));
		source.putShort((short)(value >> 8));
		source.put((byte)(value));
	}
	
	
	public static void putLittleLong56Buffermodifying(ByteBuffer source, int offset, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt(offset, (int)value);
		source.putShort(offset+4, (short)(value >>> 32));
		source.put(offset+6, (byte)(value >>> 48));
	}
	
	public static void putBigLong56Buffermodifying(ByteBuffer source, int offset, long value)
	{
		//Note that we can't just use longs and masking, because that might run off past the end! X'D
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt(offset, (int)(value >>> 24));
		source.putShort(offset+4, (short)(value >>> 8));
		source.put(offset+6, (byte)(value));
	}
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:uint24,ulong40,ulong48,ulong56$$_

	public static _$$litprim$$_ get_$$Prim$$_Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittle_$$Prim$$_Buffermodifying(source);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBig_$$Prim$$_Buffermodifying(source);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static _$$litprim$$_ get_$$Prim$$_Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittle_$$Prim$$_Buffermodifying(source, offset);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBig_$$Prim$$_Buffermodifying(source, offset);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void put_$$SLPrim$$_Buffermodifying(ByteBuffer source, _$$litprim$$_ value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittle_$$SLPrim$$_Buffermodifying(source, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBig_$$SLPrim$$_Buffermodifying(source, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void put_$$SLPrim$$_Buffermodifying(ByteBuffer source, int offset, _$$litprim$$_ value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittle_$$SLPrim$$_Buffermodifying(source, offset, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBig_$$SLPrim$$_Buffermodifying(source, offset, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	
	
	 */
	
	
	public static int getUInt24Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleUInt24Buffermodifying(source);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigUInt24Buffermodifying(source);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static int getUInt24Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleUInt24Buffermodifying(source, offset);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigUInt24Buffermodifying(source, offset);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24Buffermodifying(ByteBuffer source, int value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleInt24Buffermodifying(source, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigInt24Buffermodifying(source, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putInt24Buffermodifying(ByteBuffer source, int offset, int value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleInt24Buffermodifying(source, offset, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigInt24Buffermodifying(source, offset, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	
	
	
	
	public static long getULong40Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleULong40Buffermodifying(source);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigULong40Buffermodifying(source);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong40Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleULong40Buffermodifying(source, offset);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigULong40Buffermodifying(source, offset);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40Buffermodifying(ByteBuffer source, long value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleLong40Buffermodifying(source, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigLong40Buffermodifying(source, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong40Buffermodifying(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleLong40Buffermodifying(source, offset, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigLong40Buffermodifying(source, offset, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	
	
	
	
	public static long getULong48Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleULong48Buffermodifying(source);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigULong48Buffermodifying(source);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong48Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleULong48Buffermodifying(source, offset);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigULong48Buffermodifying(source, offset);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48Buffermodifying(ByteBuffer source, long value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleLong48Buffermodifying(source, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigLong48Buffermodifying(source, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong48Buffermodifying(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleLong48Buffermodifying(source, offset, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigLong48Buffermodifying(source, offset, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	
	
	
	
	public static long getULong56Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleULong56Buffermodifying(source);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigULong56Buffermodifying(source);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static long getULong56Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			return getLittleULong56Buffermodifying(source, offset);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			return getBigULong56Buffermodifying(source, offset);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56Buffermodifying(ByteBuffer source, long value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleLong56Buffermodifying(source, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigLong56Buffermodifying(source, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	public static void putLong56Buffermodifying(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		if (endianness == ByteOrder.LITTLE_ENDIAN)
			putLittleLong56Buffermodifying(source, offset, value);
		else if (endianness == ByteOrder.BIG_ENDIAN)
			putBigLong56Buffermodifying(source, offset, value);
		else
			throw newClassCastExceptionOrNullPointerException(endianness);
	}
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:sint24,slong40,slong48,slong56$$_
	public static _$$litprim$$_ getLittle_$$Prim$$_Buffermodifying(ByteBuffer source)
	{
		return signedUpcast_$$primlen$$_(getLittleU_$$SLPrim$$_Buffermodifying(source));
	}
	
	public static _$$litprim$$_ getBig_$$Prim$$_Buffermodifying(ByteBuffer source)
	{
		return signedUpcast_$$primlen$$_(getBigU_$$SLPrim$$_Buffermodifying(source));
	}
	
	public static _$$litprim$$_ get_$$Prim$$_Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		return signedUpcast_$$primlen$$_(getU_$$SLPrim$$_Buffermodifying(source, endianness));
	}
	
	
	public static _$$litprim$$_ getLittle_$$Prim$$_Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast_$$primlen$$_(getLittleU_$$SLPrim$$_Buffermodifying(source, offset));
	}
	
	public static _$$litprim$$_ getBig_$$Prim$$_Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast_$$primlen$$_(getBigU_$$SLPrim$$_Buffermodifying(source, offset));
	}
	
	public static _$$litprim$$_ get_$$Prim$$_Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		return signedUpcast_$$primlen$$_(getU_$$SLPrim$$_Buffermodifying(source, offset, endianness));
	}
	
	
	
	 */
	
	public static int getLittleSInt24Buffermodifying(ByteBuffer source)
	{
		return signedUpcast24(getLittleUInt24Buffermodifying(source));
	}
	
	public static int getBigSInt24Buffermodifying(ByteBuffer source)
	{
		return signedUpcast24(getBigUInt24Buffermodifying(source));
	}
	
	public static int getSInt24Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		return signedUpcast24(getUInt24Buffermodifying(source, endianness));
	}
	
	
	public static int getLittleSInt24Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast24(getLittleUInt24Buffermodifying(source, offset));
	}
	
	public static int getBigSInt24Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast24(getBigUInt24Buffermodifying(source, offset));
	}
	
	public static int getSInt24Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		return signedUpcast24(getUInt24Buffermodifying(source, offset, endianness));
	}
	
	
	
	
	public static long getLittleSLong40Buffermodifying(ByteBuffer source)
	{
		return signedUpcast40(getLittleULong40Buffermodifying(source));
	}
	
	public static long getBigSLong40Buffermodifying(ByteBuffer source)
	{
		return signedUpcast40(getBigULong40Buffermodifying(source));
	}
	
	public static long getSLong40Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		return signedUpcast40(getULong40Buffermodifying(source, endianness));
	}
	
	
	public static long getLittleSLong40Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast40(getLittleULong40Buffermodifying(source, offset));
	}
	
	public static long getBigSLong40Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast40(getBigULong40Buffermodifying(source, offset));
	}
	
	public static long getSLong40Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		return signedUpcast40(getULong40Buffermodifying(source, offset, endianness));
	}
	
	
	
	
	public static long getLittleSLong48Buffermodifying(ByteBuffer source)
	{
		return signedUpcast48(getLittleULong48Buffermodifying(source));
	}
	
	public static long getBigSLong48Buffermodifying(ByteBuffer source)
	{
		return signedUpcast48(getBigULong48Buffermodifying(source));
	}
	
	public static long getSLong48Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		return signedUpcast48(getULong48Buffermodifying(source, endianness));
	}
	
	
	public static long getLittleSLong48Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast48(getLittleULong48Buffermodifying(source, offset));
	}
	
	public static long getBigSLong48Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast48(getBigULong48Buffermodifying(source, offset));
	}
	
	public static long getSLong48Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		return signedUpcast48(getULong48Buffermodifying(source, offset, endianness));
	}
	
	
	
	
	public static long getLittleSLong56Buffermodifying(ByteBuffer source)
	{
		return signedUpcast56(getLittleULong56Buffermodifying(source));
	}
	
	public static long getBigSLong56Buffermodifying(ByteBuffer source)
	{
		return signedUpcast56(getBigULong56Buffermodifying(source));
	}
	
	public static long getSLong56Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		return signedUpcast56(getULong56Buffermodifying(source, endianness));
	}
	
	
	public static long getLittleSLong56Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast56(getLittleULong56Buffermodifying(source, offset));
	}
	
	public static long getBigSLong56Buffermodifying(ByteBuffer source, int offset)
	{
		return signedUpcast56(getBigULong56Buffermodifying(source, offset));
	}
	
	public static long getSLong56Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		return signedUpcast56(getULong56Buffermodifying(source, offset, endianness));
	}
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:multibytes$$_
	
	public static _$$prim$$_ getLittle_$$Prim$$_Buffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.get_$$Prim$$_();
	}
	
	public static _$$prim$$_ getBig_$$Prim$$_Buffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.get_$$Prim$$_();
	}
	
	public static _$$prim$$_ get_$$Prim$$_Buffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		source.order(endianness);
		return source.get_$$Prim$$_();
	}
	
	
	public static _$$prim$$_ getLittle_$$Prim$$_Buffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.get_$$Prim$$_(offset);
	}
	
	public static _$$prim$$_ getBig_$$Prim$$_Buffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.get_$$Prim$$_(offset);
	}
	
	public static _$$prim$$_ get_$$Prim$$_Buffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		source.order(endianness);
		return source.get_$$Prim$$_(offset);
	}
	
	
	
	
	public static void putLittle_$$Prim$$_Buffermodifying(ByteBuffer source, _$$prim$$_ value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.put_$$Prim$$_(value);
	}
	
	public static void putBig_$$Prim$$_Buffermodifying(ByteBuffer source, _$$prim$$_ value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.put_$$Prim$$_(value);
	}
	
	public static void put_$$Prim$$_Buffermodifying(ByteBuffer source, _$$prim$$_ value, ByteOrder endianness)
	{
		source.order(endianness);
		source.put_$$Prim$$_(value);
	}
	
	
	public static void putLittle_$$Prim$$_Buffermodifying(ByteBuffer source, int offset, _$$prim$$_ value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.put_$$Prim$$_(offset, value);
	}
	
	public static void putBig_$$Prim$$_Buffermodifying(ByteBuffer source, int offset, _$$prim$$_ value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.put_$$Prim$$_(offset, value);
	}
	
	public static void put_$$Prim$$_Buffermodifying(ByteBuffer source, int offset, _$$prim$$_ value, ByteOrder endianness)
	{
		source.order(endianness);
		source.put_$$Prim$$_(offset, value);
	}
	
	
	
	
	
	
	 */
	
	
	public static char getLittleCharBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getChar();
	}
	
	public static char getBigCharBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getChar();
	}
	
	public static char getCharBuffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getChar();
	}
	
	
	public static char getLittleCharBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getChar(offset);
	}
	
	public static char getBigCharBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getChar(offset);
	}
	
	public static char getCharBuffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getChar(offset);
	}
	
	
	
	
	public static void putLittleCharBuffermodifying(ByteBuffer source, char value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putChar(value);
	}
	
	public static void putBigCharBuffermodifying(ByteBuffer source, char value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putChar(value);
	}
	
	public static void putCharBuffermodifying(ByteBuffer source, char value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putChar(value);
	}
	
	
	public static void putLittleCharBuffermodifying(ByteBuffer source, int offset, char value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putChar(offset, value);
	}
	
	public static void putBigCharBuffermodifying(ByteBuffer source, int offset, char value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putChar(offset, value);
	}
	
	public static void putCharBuffermodifying(ByteBuffer source, int offset, char value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putChar(offset, value);
	}
	
	
	
	
	
	
	
	
	public static short getLittleShortBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getShort();
	}
	
	public static short getBigShortBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getShort();
	}
	
	public static short getShortBuffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getShort();
	}
	
	
	public static short getLittleShortBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getShort(offset);
	}
	
	public static short getBigShortBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getShort(offset);
	}
	
	public static short getShortBuffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getShort(offset);
	}
	
	
	
	
	public static void putLittleShortBuffermodifying(ByteBuffer source, short value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putShort(value);
	}
	
	public static void putBigShortBuffermodifying(ByteBuffer source, short value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putShort(value);
	}
	
	public static void putShortBuffermodifying(ByteBuffer source, short value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putShort(value);
	}
	
	
	public static void putLittleShortBuffermodifying(ByteBuffer source, int offset, short value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putShort(offset, value);
	}
	
	public static void putBigShortBuffermodifying(ByteBuffer source, int offset, short value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putShort(offset, value);
	}
	
	public static void putShortBuffermodifying(ByteBuffer source, int offset, short value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putShort(offset, value);
	}
	
	
	
	
	
	
	
	
	public static float getLittleFloatBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getFloat();
	}
	
	public static float getBigFloatBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getFloat();
	}
	
	public static float getFloatBuffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getFloat();
	}
	
	
	public static float getLittleFloatBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getFloat(offset);
	}
	
	public static float getBigFloatBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getFloat(offset);
	}
	
	public static float getFloatBuffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getFloat(offset);
	}
	
	
	
	
	public static void putLittleFloatBuffermodifying(ByteBuffer source, float value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putFloat(value);
	}
	
	public static void putBigFloatBuffermodifying(ByteBuffer source, float value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putFloat(value);
	}
	
	public static void putFloatBuffermodifying(ByteBuffer source, float value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putFloat(value);
	}
	
	
	public static void putLittleFloatBuffermodifying(ByteBuffer source, int offset, float value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putFloat(offset, value);
	}
	
	public static void putBigFloatBuffermodifying(ByteBuffer source, int offset, float value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putFloat(offset, value);
	}
	
	public static void putFloatBuffermodifying(ByteBuffer source, int offset, float value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putFloat(offset, value);
	}
	
	
	
	
	
	
	
	
	public static int getLittleIntBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getInt();
	}
	
	public static int getBigIntBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getInt();
	}
	
	public static int getIntBuffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getInt();
	}
	
	
	public static int getLittleIntBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getInt(offset);
	}
	
	public static int getBigIntBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getInt(offset);
	}
	
	public static int getIntBuffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getInt(offset);
	}
	
	
	
	
	public static void putLittleIntBuffermodifying(ByteBuffer source, int value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt(value);
	}
	
	public static void putBigIntBuffermodifying(ByteBuffer source, int value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt(value);
	}
	
	public static void putIntBuffermodifying(ByteBuffer source, int value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putInt(value);
	}
	
	
	public static void putLittleIntBuffermodifying(ByteBuffer source, int offset, int value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putInt(offset, value);
	}
	
	public static void putBigIntBuffermodifying(ByteBuffer source, int offset, int value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putInt(offset, value);
	}
	
	public static void putIntBuffermodifying(ByteBuffer source, int offset, int value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putInt(offset, value);
	}
	
	
	
	
	
	
	
	
	public static double getLittleDoubleBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getDouble();
	}
	
	public static double getBigDoubleBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getDouble();
	}
	
	public static double getDoubleBuffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getDouble();
	}
	
	
	public static double getLittleDoubleBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getDouble(offset);
	}
	
	public static double getBigDoubleBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getDouble(offset);
	}
	
	public static double getDoubleBuffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getDouble(offset);
	}
	
	
	
	
	public static void putLittleDoubleBuffermodifying(ByteBuffer source, double value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putDouble(value);
	}
	
	public static void putBigDoubleBuffermodifying(ByteBuffer source, double value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putDouble(value);
	}
	
	public static void putDoubleBuffermodifying(ByteBuffer source, double value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putDouble(value);
	}
	
	
	public static void putLittleDoubleBuffermodifying(ByteBuffer source, int offset, double value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putDouble(offset, value);
	}
	
	public static void putBigDoubleBuffermodifying(ByteBuffer source, int offset, double value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putDouble(offset, value);
	}
	
	public static void putDoubleBuffermodifying(ByteBuffer source, int offset, double value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putDouble(offset, value);
	}
	
	
	
	
	
	
	
	
	public static long getLittleLongBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getLong();
	}
	
	public static long getBigLongBuffermodifying(ByteBuffer source)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getLong();
	}
	
	public static long getLongBuffermodifying(ByteBuffer source, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getLong();
	}
	
	
	public static long getLittleLongBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		return source.getLong(offset);
	}
	
	public static long getBigLongBuffermodifying(ByteBuffer source, int offset)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		return source.getLong(offset);
	}
	
	public static long getLongBuffermodifying(ByteBuffer source, int offset, ByteOrder endianness)
	{
		source.order(endianness);
		return source.getLong(offset);
	}
	
	
	
	
	public static void putLittleLongBuffermodifying(ByteBuffer source, long value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putLong(value);
	}
	
	public static void putBigLongBuffermodifying(ByteBuffer source, long value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putLong(value);
	}
	
	public static void putLongBuffermodifying(ByteBuffer source, long value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putLong(value);
	}
	
	
	public static void putLittleLongBuffermodifying(ByteBuffer source, int offset, long value)
	{
		source.order(ByteOrder.LITTLE_ENDIAN);
		source.putLong(offset, value);
	}
	
	public static void putBigLongBuffermodifying(ByteBuffer source, int offset, long value)
	{
		source.order(ByteOrder.BIG_ENDIAN);
		source.putLong(offset, value);
	}
	
	public static void putLongBuffermodifying(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		source.order(endianness);
		source.putLong(offset, value);
	}
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:char,short,int,long,float,double,sint24,slong40,slong48,slong56,uint24,ulong40,ulong48,ulong56$$_
	
	public static _$$litprim$$_ getLittle_$$Prim$$_(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		_$$litprim$$_ rv;
		try
		{
			rv = getLittle_$$Prim$$_Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static _$$litprim$$_ getBig_$$Prim$$_(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		_$$litprim$$_ rv;
		try
		{
			rv = getBig_$$Prim$$_Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static _$$litprim$$_ get_$$Prim$$_(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		_$$litprim$$_ rv;
		try
		{
			rv = get_$$Prim$$_Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static _$$litprim$$_ getLittle_$$Prim$$_(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		_$$litprim$$_ rv;
		try
		{
			rv = getLittle_$$Prim$$_Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static _$$litprim$$_ getBig_$$Prim$$_(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		_$$litprim$$_ rv;
		try
		{
			rv = getBig_$$Prim$$_Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static _$$litprim$$_ get_$$Prim$$_(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		_$$litprim$$_ rv;
		try
		{
			rv = get_$$Prim$$_Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	 */
	
	
	public static char getLittleChar(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		char rv;
		try
		{
			rv = getLittleCharBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static char getBigChar(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		char rv;
		try
		{
			rv = getBigCharBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static char getChar(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		char rv;
		try
		{
			rv = getCharBuffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static char getLittleChar(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		char rv;
		try
		{
			rv = getLittleCharBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static char getBigChar(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		char rv;
		try
		{
			rv = getBigCharBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static char getChar(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		char rv;
		try
		{
			rv = getCharBuffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static short getLittleShort(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		short rv;
		try
		{
			rv = getLittleShortBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static short getBigShort(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		short rv;
		try
		{
			rv = getBigShortBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static short getShort(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		short rv;
		try
		{
			rv = getShortBuffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static short getLittleShort(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		short rv;
		try
		{
			rv = getLittleShortBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static short getBigShort(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		short rv;
		try
		{
			rv = getBigShortBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static short getShort(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		short rv;
		try
		{
			rv = getShortBuffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static float getLittleFloat(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		float rv;
		try
		{
			rv = getLittleFloatBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static float getBigFloat(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		float rv;
		try
		{
			rv = getBigFloatBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static float getFloat(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		float rv;
		try
		{
			rv = getFloatBuffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static float getLittleFloat(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		float rv;
		try
		{
			rv = getLittleFloatBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static float getBigFloat(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		float rv;
		try
		{
			rv = getBigFloatBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static float getFloat(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		float rv;
		try
		{
			rv = getFloatBuffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static int getLittleInt(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getLittleIntBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static int getBigInt(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getBigIntBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static int getInt(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getIntBuffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static int getLittleInt(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getLittleIntBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static int getBigInt(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getBigIntBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static int getInt(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getIntBuffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static double getLittleDouble(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		double rv;
		try
		{
			rv = getLittleDoubleBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static double getBigDouble(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		double rv;
		try
		{
			rv = getBigDoubleBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static double getDouble(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		double rv;
		try
		{
			rv = getDoubleBuffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static double getLittleDouble(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		double rv;
		try
		{
			rv = getLittleDoubleBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static double getBigDouble(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		double rv;
		try
		{
			rv = getBigDoubleBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static double getDouble(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		double rv;
		try
		{
			rv = getDoubleBuffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static long getLittleLong(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleLongBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getBigLong(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigLongBuffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getLong(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLongBuffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static long getLittleLong(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleLongBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getBigLong(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigLongBuffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getLong(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLongBuffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static int getLittleSInt24(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getLittleSInt24Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static int getBigSInt24(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getBigSInt24Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static int getSInt24(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getSInt24Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static int getLittleSInt24(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getLittleSInt24Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static int getBigSInt24(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getBigSInt24Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static int getSInt24(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getSInt24Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static long getLittleSLong40(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleSLong40Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getBigSLong40(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigSLong40Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getSLong40(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getSLong40Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static long getLittleSLong40(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleSLong40Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getBigSLong40(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigSLong40Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getSLong40(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getSLong40Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static long getLittleSLong48(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleSLong48Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getBigSLong48(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigSLong48Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getSLong48(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getSLong48Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static long getLittleSLong48(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleSLong48Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getBigSLong48(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigSLong48Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getSLong48(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getSLong48Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static long getLittleSLong56(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleSLong56Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getBigSLong56(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigSLong56Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getSLong56(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getSLong56Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static long getLittleSLong56(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleSLong56Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getBigSLong56(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigSLong56Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getSLong56(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getSLong56Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static int getLittleUInt24(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getLittleUInt24Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static int getBigUInt24(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getBigUInt24Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static int getUInt24(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getUInt24Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static int getLittleUInt24(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getLittleUInt24Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static int getBigUInt24(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getBigUInt24Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static int getUInt24(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		int rv;
		try
		{
			rv = getUInt24Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static long getLittleULong40(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleULong40Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getBigULong40(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigULong40Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getULong40(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getULong40Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static long getLittleULong40(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleULong40Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getBigULong40(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigULong40Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getULong40(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getULong40Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static long getLittleULong48(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleULong48Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getBigULong48(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigULong48Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getULong48(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getULong48Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static long getLittleULong48(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleULong48Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getBigULong48(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigULong48Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getULong48(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getULong48Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	
	
	public static long getLittleULong56(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleULong56Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getBigULong56(ByteBuffer source)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigULong56Buffermodifying(source);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	public static long getULong56(ByteBuffer source, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getULong56Buffermodifying(source, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
		return rv;
	}
	
	
	public static long getLittleULong56(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getLittleULong56Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getBigULong56(ByteBuffer source, int offset)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getBigULong56Buffermodifying(source, offset);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	public static long getULong56(ByteBuffer source, int offset, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		long rv;
		try
		{
			rv = getULong56Buffermodifying(source, offset, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
		return rv;
	}
	
	
	
	
	
	
	
	
	// >>>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
primxp
_$$primxpconf:char,short,int,long,float,double,uint24,ulong40,ulong48,ulong56$$_
	
	
	public static void putLittle_$$SLPrim$$_(ByteBuffer source, _$$litprim$$_ value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittle_$$SLPrim$$_Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBig_$$SLPrim$$_(ByteBuffer source, _$$litprim$$_ value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBig_$$SLPrim$$_Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void put_$$SLPrim$$_(ByteBuffer source, _$$litprim$$_ value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			put_$$SLPrim$$_Buffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittle_$$SLPrim$$_(ByteBuffer source, int offset, _$$litprim$$_ value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittle_$$SLPrim$$_Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBig_$$SLPrim$$_(ByteBuffer source, int offset, _$$litprim$$_ value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBig_$$SLPrim$$_Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBig_$$SLPrim$$_(ByteBuffer source, int offset, _$$litprim$$_ value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			put_$$SLPrim$$_Buffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	 */
	
	
	
	public static void putLittleChar(ByteBuffer source, char value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleCharBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigChar(ByteBuffer source, char value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigCharBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putChar(ByteBuffer source, char value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putCharBuffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleChar(ByteBuffer source, int offset, char value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleCharBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigChar(ByteBuffer source, int offset, char value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigCharBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigChar(ByteBuffer source, int offset, char value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putCharBuffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleShort(ByteBuffer source, short value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleShortBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigShort(ByteBuffer source, short value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigShortBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putShort(ByteBuffer source, short value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putShortBuffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleShort(ByteBuffer source, int offset, short value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleShortBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigShort(ByteBuffer source, int offset, short value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigShortBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigShort(ByteBuffer source, int offset, short value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putShortBuffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleFloat(ByteBuffer source, float value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleFloatBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigFloat(ByteBuffer source, float value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigFloatBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putFloat(ByteBuffer source, float value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putFloatBuffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleFloat(ByteBuffer source, int offset, float value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleFloatBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigFloat(ByteBuffer source, int offset, float value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigFloatBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigFloat(ByteBuffer source, int offset, float value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putFloatBuffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleInt(ByteBuffer source, int value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleIntBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigInt(ByteBuffer source, int value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigIntBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putInt(ByteBuffer source, int value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putIntBuffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleInt(ByteBuffer source, int offset, int value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleIntBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigInt(ByteBuffer source, int offset, int value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigIntBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigInt(ByteBuffer source, int offset, int value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putIntBuffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleDouble(ByteBuffer source, double value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleDoubleBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigDouble(ByteBuffer source, double value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigDoubleBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putDouble(ByteBuffer source, double value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putDoubleBuffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleDouble(ByteBuffer source, int offset, double value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleDoubleBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigDouble(ByteBuffer source, int offset, double value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigDoubleBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigDouble(ByteBuffer source, int offset, double value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putDoubleBuffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleLong(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLongBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigLong(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLongBuffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putLong(ByteBuffer source, long value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLongBuffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleLong(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLongBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLongBuffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLongBuffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleInt24(ByteBuffer source, int value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleInt24Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigInt24(ByteBuffer source, int value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigInt24Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putInt24(ByteBuffer source, int value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putInt24Buffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleInt24(ByteBuffer source, int offset, int value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleInt24Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigInt24(ByteBuffer source, int offset, int value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigInt24Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigInt24(ByteBuffer source, int offset, int value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putInt24Buffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleLong40(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLong40Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigLong40(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLong40Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putLong40(ByteBuffer source, long value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLong40Buffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleLong40(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLong40Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong40(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLong40Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong40(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLong40Buffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleLong48(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLong48Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigLong48(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLong48Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putLong48(ByteBuffer source, long value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLong48Buffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleLong48(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLong48Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong48(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLong48Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong48(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLong48Buffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	
	
	
	public static void putLittleLong56(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLong56Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putBigLong56(ByteBuffer source, long value)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLong56Buffermodifying(source, value);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	public static void putLong56(ByteBuffer source, long value, ByteOrder endianness)
	{
		int originalPosition = source.position();
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLong56Buffermodifying(source, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
			source.position(originalPosition);
		}
	}
	
	
	public static void putLittleLong56(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLittleLong56Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong56(ByteBuffer source, int offset, long value)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putBigLong56Buffermodifying(source, offset, value);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	public static void putBigLong56(ByteBuffer source, int offset, long value, ByteOrder endianness)
	{
		ByteOrder originalByteOrder = source.order();
		try
		{
			putLong56Buffermodifying(source, offset, value, endianness);
		}
		finally
		{
			source.order(originalByteOrder);
		}
	}
	
	
	
	// >>>
}
