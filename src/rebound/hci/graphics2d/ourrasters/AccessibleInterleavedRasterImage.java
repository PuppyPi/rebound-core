package rebound.hci.graphics2d.ourrasters;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.FunctionalityType;
import rebound.annotations.semantic.StaticTraitPredicate;
import rebound.annotations.semantic.TraitPredicate;
import rebound.bits.Bytes;
import rebound.exceptions.NotYetImplementedException;
import rebound.util.objectutil.PubliclyCloneable;

/**
 * This only supports (the overwhelmingly most common) interleaved-component form data (ie, {RGBRGBRGBRGBRGB} instead of {RRRRRGGGGGBBBBB} or {{RRRRR}, {GGGGG}, {BBBBB}} !!
 * 
 * @author Puppy Pie ^w^
 */
public interface AccessibleInterleavedRasterImage<I extends AccessibleInterleavedRasterImage<I>>
extends PubliclyCloneable<I>
{
	public int getWidth();
	
	public int getHeight();
	
	@Nonnull
	public SimpleImageColorStorageType getPixelFormat();
	
	
	@Nonnull
	public Object getUnderlyingBacking();
	
	
	
	public int getUnderlyingBackingOffsetInBytes();
	
	public int getUnderlyingBackingLengthInBytes();
	
	
	public default int getBitsPerPixel()
	{
		requireNonNull(getPixelFormat());
		return getPixelFormat().getBits();
	}
	
	public default int getBytesPerPixel()
	{
		return ceilingDivision(getBitsPerPixel(), 8);
	}
	
	
	
	@Override
	//This changes the (non-)generics erasure type! ^_~
	public I clone();
	
	/**
	 * Like {@link #clone()} but doesn't bother copying the data!
	 */
	public I newCompatibleInstance();
	public I newCompatibleInstance(int newWidth, int newHeight);
	public I newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat);
	
	
	
	
	
	//< Slow but functional accessors ^^'
	public int getPixelValue(int x, int y);
	public void setPixelValue(int x, int y, int packedPixelValueInOurPixelFormat);
	
	
	public default int getPixelValue(int x, int y, SimpleImageColorStorageType destPixelFormat)
	{
		return SimpleImageColorStorageType.convertPacked32(this.getPixelValue(x, y), this.getPixelFormat(), destPixelFormat);
	}
	
	public default void setPixelValue(int x, int y, int packedPixelValueInGivenPixelFormat, SimpleImageColorStorageType givenPixelFormat)
	{
		setPixelValue(x, y, SimpleImageColorStorageType.convertPacked32(packedPixelValueInGivenPixelFormat, givenPixelFormat, this.getPixelFormat()));
	}
	
	
	public default SimpleRGBAColor getPixelValueObject(int x, int y)
	{
		return new SimpleRGBAColor(getPixelValue(x, y), getPixelFormat());
	}
	
	public default void setPixelValueObject(int x, int y, SimpleRGBAColor color)
	{
		setPixelValue(x, y, color.getPackedValue(), color.getFormat());
	}
	//Slow but functional accessors ^^' >
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * This is the preferred one if we don't have to deal with AWT, since it can support all backings in a unified way--even direct/C-heap memory for interop with native code!!! :DDD
	 */
	@FunctionalityType
	public static interface AccessibleInterleavedRasterImageByteBuffer<I extends AccessibleInterleavedRasterImageByteBuffer<I>>
	extends AccessibleInterleavedRasterImage<I>
	{
		public ByteBuffer getUnderlyingBackingByteBuffer();
		
		//Todo ^^'
		//		public boolean isDirect();
		//
		//		public long getNativePointerIfDirect();
		
		
		
		
		@Override
		public default Object getUnderlyingBacking()
		{
			return getUnderlyingBackingByteBuffer();
		}
		
		
		
		
		@Override
		//This changes the (non-)generics erasure type! ^_~
		public I clone();
		@Override
		public I newCompatibleInstance();
		@Override
		public I newCompatibleInstance(int newWidth, int newHeight);
		@Override
		public I newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat);
		
		
		
		
		@Override
		public default int getPixelValue(int x, int y)
		{
			int bitsPerPixel = getPixelFormat().getBits();
			if ((bitsPerPixel % 8) != 0)
				throw new NotYetImplementedException("Pixels are not aligned to bytes, they span across them!!!");
			
			int bytesPerPixel = bitsPerPixel / 8;
			
			int pixelIndex = y * getWidth() + x;
			int byteIndex = pixelIndex * bytesPerPixel;
			
			//It's all intrinsically little-endian because red being first in the pixel format means it comes first in the sequence of bytes that represents each pixel!
			// ( the endianness can easily be flipped by simply changing the pixel format! ^wwwwwwwwww^ )
			
			return (int)Bytes.getLittle(getUnderlyingBackingByteBuffer(), getUnderlyingBackingOffsetInBytes()+byteIndex, bytesPerPixel);
		}
		
		@Override
		public default void setPixelValue(int x, int y, int packedPixelValueInOurPixelFormat)
		{
			int bitsPerPixel = getPixelFormat().getBits();
			if ((bitsPerPixel % 8) != 0)
				throw new NotYetImplementedException("Pixels are not aligned to bytes, they span across them!!!");
			
			int bytesPerPixel = bitsPerPixel / 8;
			
			int pixelIndex = y * getWidth() + x;
			int byteIndex = pixelIndex * bytesPerPixel;
			
			//It's all intrinsically little-endian because red being first in the pixel format means it comes first in the sequence of bytes that represents each pixel!
			// ( the endianness can easily be flipped by simply changing the pixel format! ^wwwwwwwwww^ )
			
			Bytes.putLittle(getUnderlyingBackingByteBuffer(), getUnderlyingBackingOffsetInBytes()+byteIndex, packedPixelValueInOurPixelFormat, bytesPerPixel);
		}
		
		
		
		
		
		//<<< tp AccessibleInterleavedRasterImageByteBuffer
		@TraitPredicate
		public default boolean isAccessibleInterleavedRasterImageByteBuffer()
		{
			return true;
		}
		
		@StaticTraitPredicate
		public static boolean is(Object x)
		{
			return x instanceof AccessibleInterleavedRasterImageByteBuffer && ((AccessibleInterleavedRasterImageByteBuffer)x).isAccessibleInterleavedRasterImageByteBuffer();
		}
		//>>>
	}
	
	
	
	
	
	@FunctionalityType
	public static interface AccessibleInterleavedRasterImageArray<I extends AccessibleInterleavedRasterImageArray<I>>
	extends AccessibleInterleavedRasterImage<I>
	{
		public int getElementBitlength();
		public int getElementBytelength();
		
		public default int getElementsPerPixel()
		{
			return ceilingDivision(getBitsPerPixel(), getElementBitlength());
		}
		
		
		
		public int getUnderlyingBackingOffsetInElements();
		
		public int getUnderlyingBackingLengthInElements();
		
		
		@Override
		public default int getUnderlyingBackingOffsetInBytes()
		{
			return getUnderlyingBackingOffsetInElements() * getElementBytelength();
		}
		
		@Override
		public default int getUnderlyingBackingLengthInBytes()
		{
			return getUnderlyingBackingLengthInElements() * getElementBytelength();
		}
		
		
		
		
		@Override
		//This changes the (non-)generics erasure type! ^_~
		public I clone();
		@Override
		public I newCompatibleInstance();
		@Override
		public I newCompatibleInstance(int newWidth, int newHeight);
		@Override
		public I newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat);
		
		
		
		
		//<<< tp AccessibleInterleavedRasterImageArray
		@TraitPredicate
		public default boolean isAccessibleInterleavedRasterImageArray()
		{
			return true;
		}
		
		@StaticTraitPredicate
		public static boolean is(Object x)
		{
			return x instanceof AccessibleInterleavedRasterImageArray && ((AccessibleInterleavedRasterImageArray)x).isAccessibleInterleavedRasterImageArray();
		}
		//>>>
		
		
		
		
		
		
		
		
		
		
		@FunctionalityType
		public static interface AccessibleInterleavedRasterImageArrayByte<I extends AccessibleInterleavedRasterImageArrayByte<I>>
		extends AccessibleInterleavedRasterImageArray<I>
		{
			public byte[] getUnderlyingBackingArrayByte();
			
			@Override
			public default Object getUnderlyingBacking()
			{
				return getUnderlyingBackingArrayByte();
			}
			
			
			@Override
			public default int getElementBitlength()
			{
				return 8;
			}
			
			@Override
			public default int getElementBytelength()
			{
				return 1;
			}
			
			
			
			
			@Override
			//This changes the (non-)generics erasure type! ^_~
			public I clone();
			@Override
			public I newCompatibleInstance();
			@Override
			public I newCompatibleInstance(int newWidth, int newHeight);
			@Override
			public I newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat);
			
			
			
			
			@Override
			public default int getPixelValue(int x, int y)
			{
				int bitsPerPixel = getPixelFormat().getBits();
				if ((bitsPerPixel % 8) != 0)
					throw new NotYetImplementedException("Pixels are not aligned to bytes, they span across them!!!");
				
				int bytesPerPixel = bitsPerPixel / 8;
				
				int pixelIndex = y * getWidth() + x;
				int byteIndex = pixelIndex * bytesPerPixel;
				
				//It's all intrinsically little-endian because red being first in the pixel format means it comes first in the sequence of bytes that represents each pixel!
				// ( the endianness can easily be flipped by simply changing the pixel format! ^wwwwwwwwww^ )
				
				return (int)Bytes.getLittle(getUnderlyingBackingArrayByte(), getUnderlyingBackingOffsetInBytes()+byteIndex, bytesPerPixel);
			}
			
			@Override
			public default void setPixelValue(int x, int y, int packedPixelValueInOurPixelFormat)
			{
				int bitsPerPixel = getPixelFormat().getBits();
				if ((bitsPerPixel % 8) != 0)
					throw new NotYetImplementedException("Pixels are not aligned to bytes, they span across them!!!");
				
				int bytesPerPixel = bitsPerPixel / 8;
				
				int pixelIndex = y * getWidth() + x;
				int byteIndex = pixelIndex * bytesPerPixel;
				
				//It's all intrinsically little-endian because red being first in the pixel format means it comes first in the sequence of bytes that represents each pixel!
				// ( the endianness can easily be flipped by simply changing the pixel format! ^wwwwwwwwww^ )
				
				Bytes.putLittle(getUnderlyingBackingArrayByte(), getUnderlyingBackingOffsetInBytes()+byteIndex, packedPixelValueInOurPixelFormat, bytesPerPixel);
			}
			
			
			
			
			//<<< tp AccessibleInterleavedRasterImageArrayByte
			@TraitPredicate
			public default boolean isAccessibleInterleavedRasterImageArrayByte()
			{
				return true;
			}
			
			@StaticTraitPredicate
			public static boolean is(Object x)
			{
				return x instanceof AccessibleInterleavedRasterImageArrayByte && ((AccessibleInterleavedRasterImageArrayByte)x).isAccessibleInterleavedRasterImageArrayByte();
			}
			//>>>
		}
		
		
		
		
		@FunctionalityType
		public static interface AccessibleInterleavedRasterImageArrayShort<I extends AccessibleInterleavedRasterImageArrayShort<I>>
		extends AccessibleInterleavedRasterImageArray<I>
		{
			public short[] getUnderlyingBackingArrayShort();
			
			@Override
			public default Object getUnderlyingBacking()
			{
				return getUnderlyingBackingArrayShort();
			}
			
			
			
			@Override
			public default int getElementBitlength()
			{
				return 16;
			}
			
			@Override
			public default int getElementBytelength()
			{
				return 2;
			}
			
			
			
			
			@Override
			//This changes the (non-)generics erasure type! ^_~
			public I clone();
			@Override
			public I newCompatibleInstance();
			@Override
			public I newCompatibleInstance(int newWidth, int newHeight);
			@Override
			public I newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat);
			
			
			
			
			@Override
			public default int getPixelValue(int x, int y)
			{
				if (getPixelFormat().getBits() != 16)
					throw new NotYetImplementedException("If you want to use something other than 16 bits per pixel, use a byte[] or ByteBuffer not a short[]--we don't need to kill ourselves dealing with pixels striding across array elements!! X''D");
				
				int pixelIndex = y * getWidth() + x;
				
				return getUnderlyingBackingArrayShort()[getUnderlyingBackingOffsetInElements() + pixelIndex] & 0xFFFF;
			}
			
			@Override
			public default void setPixelValue(int x, int y, int packedPixelValueInOurPixelFormat)
			{
				if (getPixelFormat().getBits() != 16)
					throw new NotYetImplementedException("If you want to use something other than 16 bits per pixel, use a byte[] or ByteBuffer not a short[]--we don't need to kill ourselves dealing with pixels striding across array elements!! X''D");
				
				int pixelIndex = y * getWidth() + x;
				
				getUnderlyingBackingArrayShort()[getUnderlyingBackingOffsetInElements() + pixelIndex] = (short)packedPixelValueInOurPixelFormat;
			}
			
			
			
			
			//<<< tp AccessibleInterleavedRasterImageArrayShort
			@TraitPredicate
			public default boolean isAccessibleInterleavedRasterImageArrayShort()
			{
				return true;
			}
			
			@StaticTraitPredicate
			public static boolean is(Object x)
			{
				return x instanceof AccessibleInterleavedRasterImageArrayShort && ((AccessibleInterleavedRasterImageArrayShort)x).isAccessibleInterleavedRasterImageArrayShort();
			}
			//>>>
		}
		
		
		
		
		@FunctionalityType
		public static interface AccessibleInterleavedRasterImageArrayInt<I extends AccessibleInterleavedRasterImageArrayInt<I>>
		extends AccessibleInterleavedRasterImageArray<I>
		{
			public int[] getUnderlyingBackingArrayInt();
			
			@Override
			public default Object getUnderlyingBacking()
			{
				return getUnderlyingBackingArrayInt();
			}
			
			
			
			@Override
			public default int getElementBitlength()
			{
				return 32;
			}
			
			@Override
			public default int getElementBytelength()
			{
				return 4;
			}
			
			
			
			
			@Override
			//This changes the (non-)generics erasure type! ^_~
			public I clone();
			@Override
			public I newCompatibleInstance();
			@Override
			public I newCompatibleInstance(int newWidth, int newHeight);
			@Override
			public I newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat);
			
			
			
			
			@Override
			public default int getPixelValue(int x, int y)
			{
				if (getPixelFormat().getBits() != 32)
					throw new NotYetImplementedException("If you want to use something other than 32 bits per pixel, use a byte[] or ByteBuffer not an int[]--we don't need to kill ourselves dealing with pixels striding across array elements!! X''D");
				
				int pixelIndex = y * getWidth() + x;
				
				return getUnderlyingBackingArrayInt()[getUnderlyingBackingOffsetInElements() + pixelIndex];
			}
			
			@Override
			public default void setPixelValue(int x, int y, int packedPixelValueInOurPixelFormat)
			{
				if (getPixelFormat().getBits() != 32)
					throw new NotYetImplementedException("If you want to use something other than 32 bits per pixel, use a byte[] or ByteBuffer not an int[]--we don't need to kill ourselves dealing with pixels striding across array elements!! X''D");
				
				int pixelIndex = y * getWidth() + x;
				
				getUnderlyingBackingArrayInt()[getUnderlyingBackingOffsetInElements() + pixelIndex] = packedPixelValueInOurPixelFormat;
			}
			
			
			
			
			//<<< tp AccessibleInterleavedRasterImageArrayInt
			@TraitPredicate
			public default boolean isAccessibleInterleavedRasterImageArrayInt()
			{
				return true;
			}
			
			@StaticTraitPredicate
			public static boolean is(Object x)
			{
				return x instanceof AccessibleInterleavedRasterImageArrayInt && ((AccessibleInterleavedRasterImageArrayInt)x).isAccessibleInterleavedRasterImageArrayInt();
			}
			//>>>
		}
	}
	
	
	
	
	
	/**
	 * For dealing with both types of backings, ugh x'D
	 */
	public static int getElementBitlengthOr8IfNotArray(AccessibleInterleavedRasterImage image)
	{
		if (image instanceof AccessibleInterleavedRasterImageArray)
			return ((AccessibleInterleavedRasterImageArray) image).getElementBitlength();
		else
			return 8;
	}
	
	
	public static int getOffsetInElementsOrBytesIfNotArray(AccessibleInterleavedRasterImage image)
	{
		if (image instanceof AccessibleInterleavedRasterImageArray)
			return ((AccessibleInterleavedRasterImageArray) image).getUnderlyingBackingOffsetInElements();
		else
			return image.getUnderlyingBackingOffsetInBytes();
	}
	
	public static int getLengthInElementsOrBytesIfNotArray(AccessibleInterleavedRasterImage image)
	{
		if (image instanceof AccessibleInterleavedRasterImageArray)
			return ((AccessibleInterleavedRasterImageArray) image).getUnderlyingBackingLengthInElements();
		else
			return image.getUnderlyingBackingLengthInBytes();
	}
}




//Todo-lp support uninterleaved-yet-monolithic array forms (ie, RRRGGGBBB instead of RGBRGBRGB!)
//Todo-Llp support uninterleaved-separate backing forms (ie, {RRR} {GGG} {BBB} instead of {RGBRGBRGB}!)
