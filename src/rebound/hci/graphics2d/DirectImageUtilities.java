/*
 * Created on May 31, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.hci.graphics2d;

import static java.util.Objects.*;
import static rebound.hci.graphics2d.ourrasters.AccessibleRasterImages.*;
import static rebound.hci.graphics2d.ourrasters.SimpleImageColorStorageType.*;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.DataBufferDouble;
import java.awt.image.DataBufferFloat;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.MultiPixelPackedSampleModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.File;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import rebound.annotations.hints.GenerallySupposedToBeCached;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NotYetImplementedException;
import rebound.hci.graphics2d.DirectImageUtilities.TransparentBufferedImage.AbstractTransparentBufferedImage;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayByte;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayInt;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayShort;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageByteBuffer;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType;
import rebound.hci.graphics2d.ourrasters.AccessibleRasterImages;
import rebound.hci.graphics2d.ourrasters.ReboundAccessibleRasterImagesAndJRESystems;
import rebound.hci.graphics2d.ourrasters.SimpleImageColorStorageType;
import rebound.hci.graphics2d.ourrasters.SimpleImageColorStorageTypeNotSupportedException;
import rebound.math.SmallIntegerMathUtilities;
import rebound.text.StringUtilities;
import rebound.util.AngryReflectionUtility;
import rebound.util.BufferAllocationType;
import rebound.util.NIOBufferUtilities;
import rebound.util.PlatformNIOBufferUtilities;
import rebound.util.classhacking.jre.ClasshackingSunWritableRaster;
import rebound.util.objectutil.BasicObjectUtilities;
import rebound.util.objectutil.JavaNamespace;


//TODO Make the actual Java AWT BufferredImage be created Lazilyyyyyyyyyy (because it might not ever be usedddddd)!!!  :DDDD


//Todo NIOooo!  (we needs a new WritableRaster impl);   also let data type be arbitrary! :D


public class DirectImageUtilities
implements JavaNamespace
{
	public static SimpleImageColorStorageType PreferredColorFormatForJava2D_NoAlpha32Bit = SimpleImageColorStorageType.TYPE_RGB32;
	public static SimpleImageColorStorageType PreferredColorFormatForJava2D_NoAlpha24Bit = SimpleImageColorStorageType.TYPE_RGB24;
	public static SimpleImageColorStorageType PreferredColorFormatForJava2D_Alpha32Bit = SimpleImageColorStorageType.TYPE_ARGB32;
	
	
	
	
	
	
	
	/**
	 * This is really only valid for formats with a storage bit length of a multiple of this array's component type's bit length (incl. 1  :>  ).
	 * If there are more than {@link AccessibleInterleavedRasterImageArray#getElementBitlength() L} bits in the format (an integer multiple of L!), it would represent multiple primitive values spanning each pixel.
	 *
	 * Note: currently only single-buffer backings are supported (eg, a single primitive array for all "bands"//channels, rather than three for R,G,B separately or something); sorries ._.
	 */
	@SuppressWarnings("javadoc")
	public static interface TransparentBufferedImage<I extends TransparentBufferedImage<I>>
	extends AccessibleInterleavedRasterImage<I>
	{
		@GenerallySupposedToBeCached
		@Nonnull
		public BufferedImage getBufferedImage();
		
		
		
		
		
		public static abstract class AbstractTransparentBufferedImage
		implements TransparentBufferedImage
		{
			@Override
			public TransparentBufferedImage clone()
			{
				TransparentBufferedImage copy = createNewTransparentBufferedImage(getWidth(), getHeight(), getPixelFormat(), getUnderlyingBacking().getClass());
				
				int lengthInElements = AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(this);
				
				if (AccessibleInterleavedRasterImage.getLengthInElementsOrBytesIfNotArray(copy) != lengthInElements)
					throw new ImpossibleException();
				
				NIOBufferUtilities.arrayOrBufferCopy(getUnderlyingBacking(), AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(this), copy.getUnderlyingBacking(), AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(copy), lengthInElements);
				
				return copy;
			}
			
			@Override
			public String toString()
			{
				return "Backing-Memory-Transparent BufferedImage, "+getWidth()+"x"+getHeight()+" pixels, "+getUnderlyingBacking().getClass().getSimpleName()+" backing @ "+AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(this)+" for "+AccessibleInterleavedRasterImage.getLengthInElementsOrBytesIfNotArray(this)+" elements! :DDD";
			}
		}
	}
	
	
	
	
	
	
	public static boolean areAwtBufferedImagesCurrentlyPixelwiseEquivalent(BufferedImage a, BufferedImage b)
	{
		TransparentBufferedImage ta = wrapOrCopyNontransparentToTransparentBufferedImage(a);
		TransparentBufferedImage tb = wrapOrCopyNontransparentToTransparentBufferedImage(b);
		
		return AccessibleRasterImages.areAccessibleInterleavedRasterImagesCurrentlyEqual(ta, tb);
	}
	
	
	
	
	
	
	
	
	
	//	/**
	//	 * NOTE!: unsupported by Java2D  (DataBuffer, specifically)
	//	 * @author RProgrammer
	//	 */
	//	public static interface TransparentBufferedImageArrayLong
	//	extends TransparentBufferedImage
	//	{
	//		public long[] getUnderlyingBackingArrayLong();
	//	}
	
	public static interface TransparentBufferedImageArrayInt<I extends TransparentBufferedImageArrayInt<I>>
	extends TransparentBufferedImage<I>, AccessibleInterleavedRasterImageArrayInt<I>
	{
	}
	
	public static interface TransparentBufferedImageArrayShort<I extends TransparentBufferedImageArrayShort<I>>
	extends TransparentBufferedImage<I>, AccessibleInterleavedRasterImageArrayShort<I>
	{
	}
	
	public static interface TransparentBufferedImageArrayByte<I extends TransparentBufferedImageArrayByte<I>>
	extends TransparentBufferedImage<I>, AccessibleInterleavedRasterImageArrayByte<I>
	{
	}
	
	public static interface TransparentBufferedImageByteBuffer<I extends TransparentBufferedImageByteBuffer<I>>
	extends TransparentBufferedImage<I>, AccessibleInterleavedRasterImageByteBuffer<I>
	{
	}
	
	
	
	
	
	protected static class PatheticallyLazyTransparentBufferedImageImpl
	extends AbstractTransparentBufferedImage
	implements TransparentBufferedImage,
	TransparentBufferedImageArrayByte,
	TransparentBufferedImageArrayShort,
	TransparentBufferedImageArrayInt,
	//TransparentBufferedImageArrayLong,
	TransparentBufferedImageByteBuffer
	{
		protected final Object underlyingBacking;
		protected final int underlyingBackingOffset;
		protected final BufferedImage bufferedImage;
		protected final int width, height;
		protected final SimpleImageColorStorageType pixelFormat;
		
		public PatheticallyLazyTransparentBufferedImageImpl(@Nonnull Object underlyingBacking, int underlyingBackingOffset, @Nonnull BufferedImage bufferedImage, int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat)
		{
			super();
			this.underlyingBacking = requireNonNull(underlyingBacking);
			this.underlyingBackingOffset = underlyingBackingOffset;
			this.bufferedImage = requireNonNull(bufferedImage);
			this.width = width;
			this.height = height;
			this.pixelFormat = requireNonNull(pixelFormat);
		}
		
		@Override
		public int getElementBitlength()
		{
			if (this.underlyingBacking instanceof byte[]) return 8;
			else if (this.underlyingBacking instanceof short[]) return 16;
			else if (this.underlyingBacking instanceof int[]) return 32;
			else if (this.underlyingBacking instanceof ByteBuffer) throw new NotYetImplementedException();
			else throw new IllegalStateException();
		}
		
		@Override
		public int getElementBytelength()
		{
			return getElementBitlength() / 8;
		}
		
		@Override
		public int getUnderlyingBackingLengthInElements()
		{
			return SmallIntegerMathUtilities.ceilingDivision(this.pixelFormat.getBits() * this.width * this.height, getElementBitlength());
		}
		
		
		
		
		@Override
		public boolean isAccessibleInterleavedRasterImageArray()
		{
			return !(this.underlyingBacking instanceof Buffer);
		}
		
		@Override
		public boolean isAccessibleInterleavedRasterImageArrayByte()
		{
			return this.underlyingBacking instanceof byte[];
		}
		
		@Override
		public boolean isAccessibleInterleavedRasterImageArrayShort()
		{
			return this.underlyingBacking instanceof short[];
		}
		
		@Override
		public boolean isAccessibleInterleavedRasterImageArrayInt()
		{
			return this.underlyingBacking instanceof int[];
		}
		
		
		@Override
		public boolean isAccessibleInterleavedRasterImageByteBuffer()
		{
			return this.underlyingBacking instanceof ByteBuffer;
		}
		
		
		
		@Override
		public PatheticallyLazyTransparentBufferedImageImpl clone()
		{
			return (PatheticallyLazyTransparentBufferedImageImpl)tbiCopyOf(this);
		}
		
		
		
		@Override
		public PatheticallyLazyTransparentBufferedImageImpl newCompatibleInstance()
		{
			//Todo ^^"
			throw new NotYetImplementedException();
		}
		
		@Override
		public PatheticallyLazyTransparentBufferedImageImpl newCompatibleInstance(int newWidth, int newHeight)
		{
			//Todo ^^"
			throw new NotYetImplementedException();
		}
		
		@Override
		public PatheticallyLazyTransparentBufferedImageImpl newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat)
		{
			//Todo ^^"
			throw new NotYetImplementedException();
		}
		
		
		
		@Override
		public int getPixelValue(int x, int y)
		{
			if (isAccessibleInterleavedRasterImageArrayByte())
				return TransparentBufferedImageArrayByte.super.getPixelValue(x, y);
			else if (isAccessibleInterleavedRasterImageArrayShort())
				return TransparentBufferedImageArrayShort.super.getPixelValue(x, y);
			else if (isAccessibleInterleavedRasterImageArrayInt())
				return TransparentBufferedImageArrayInt.super.getPixelValue(x, y);
			else if (isAccessibleInterleavedRasterImageByteBuffer())
				return TransparentBufferedImageByteBuffer.super.getPixelValue(x, y);
			else
				throw new ImpossibleException();
		}
		
		@Override
		public void setPixelValue(int x, int y, int packedPixelValueInOurPixelFormat)
		{
			if (isAccessibleInterleavedRasterImageArrayByte())
				TransparentBufferedImageArrayByte.super.setPixelValue(x, y, packedPixelValueInOurPixelFormat);
			else if (isAccessibleInterleavedRasterImageArrayShort())
				TransparentBufferedImageArrayShort.super.setPixelValue(x, y, packedPixelValueInOurPixelFormat);
			else if (isAccessibleInterleavedRasterImageArrayInt())
				TransparentBufferedImageArrayInt.super.setPixelValue(x, y, packedPixelValueInOurPixelFormat);
			else if (isAccessibleInterleavedRasterImageByteBuffer())
				TransparentBufferedImageByteBuffer.super.setPixelValue(x, y, packedPixelValueInOurPixelFormat);
			else
				throw new ImpossibleException();
		}
		
		
		
		
		
		@Override
		public Object getUnderlyingBacking()
		{
			return this.underlyingBacking;
		}
		
		@Override
		public int getUnderlyingBackingOffsetInElements()
		{
			return this.underlyingBackingOffset;
		}
		
		@Override
		public BufferedImage getBufferedImage()
		{
			return this.bufferedImage;
		}
		
		@Override
		public int getWidth()
		{
			return this.width;
		}
		
		@Override
		public int getHeight()
		{
			return this.height;
		}
		
		@Override
		public SimpleImageColorStorageType getPixelFormat()
		{
			return this.pixelFormat;
		}
		
		
		
		//<Pathetic part xD
		@Override
		public byte[] getUnderlyingBackingArrayByte()
		{
			return (byte[])getUnderlyingBacking();
		}
		
		@Override
		public short[] getUnderlyingBackingArrayShort()
		{
			return (short[])getUnderlyingBacking();
		}
		
		@Override
		public int[] getUnderlyingBackingArrayInt()
		{
			return (int[])getUnderlyingBacking();
		}
		
		//		@Override
		//		public long[] getUnderlyingBackingArrayLong()
		//		{
		//			return (long[])getUnderlyingBacking();
		//		}
		
		@Override
		public ByteBuffer getUnderlyingBackingByteBuffer()
		{
			return (ByteBuffer)getUnderlyingBacking();
		}
		//Pathetic part xD >
	}
	
	
	
	
	
	/*
	 * Java2D notes:
	 * 		+ Only three types are really optimized: BYTE, USHORT, and INT
	 * 			See source for: java.awt.image.Raster.createWritableRaster(SampleModel sm, DataBuffer db, Point location)
	 *
	 * 		+ DataBuffer<<Prim>>'s have their data arrays STOLEN, so that's why DataBuffers aren't ridiculously slow XD
	 * 			See: sun.awt.image.SunWritableRaster.DataStealer   (socute! X> )
	 */
	
	
	public static TransparentBufferedImage createNewCompatibleTransparentBufferedImage(AccessibleInterleavedRasterImage original) throws IllegalArgumentException
	{
		return createNewCompatibleTransparentBufferedImage(original, original.getWidth(), original.getHeight());
	}
	
	public static TransparentBufferedImage createNewCompatibleTransparentBufferedImage(AccessibleInterleavedRasterImage original, int width, int height) throws IllegalArgumentException
	{
		if (original.getUnderlyingBacking() instanceof byte[])
			return createNewTransparentBufferedImageBackedByByteArray(width, height, original.getPixelFormat());
		else if (original.getUnderlyingBacking() instanceof short[])
			return createNewTransparentBufferedImageBackedByShortArray(width, height, original.getPixelFormat());
		else if (original.getUnderlyingBacking() instanceof int[])
			return createNewTransparentBufferedImageBackedByIntArray(width, height, original.getPixelFormat());
		else if (original.getUnderlyingBacking() instanceof Buffer)
			return createNewTransparentBufferedImageBackedByByteBuffer(width, height, original.getPixelFormat(), PlatformNIOBufferUtilities.getBufferAllocationType(((AccessibleInterleavedRasterImageByteBuffer)original).getUnderlyingBackingByteBuffer()));
		else
			throw new IllegalStateException(original.getUnderlyingBacking().getClass().getName());
	}
	
	
	
	public static TransparentBufferedImageArrayByte createNewTransparentBufferedImageBackedByByteArray(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		return createTransparentBufferedImageBackedByByteArray(width, height, pixelFormat, new byte[getImageBackingLengthBytes(width, height, pixelFormat)], 0);
	}
	
	public static TransparentBufferedImageArrayShort createNewTransparentBufferedImageBackedByShortArray(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		return createTransparentBufferedImageBackedByShortArray(width, height, pixelFormat, new short[getImageBackingLengthShorts(width, height, pixelFormat)], 0);
	}
	
	public static TransparentBufferedImageArrayInt createNewTransparentBufferedImageBackedByIntArray(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		return createTransparentBufferedImageBackedByIntArray(width, height, pixelFormat, new int[getImageBackingLengthInts(width, height, pixelFormat)], 0);
	}
	
	
	public static TransparentBufferedImageByteBuffer createNewTransparentBufferedImageBackedByByteBuffer(int width, int height, SimpleImageColorStorageType pixelFormat, BufferAllocationType bufferAllocationType) throws IllegalArgumentException
	{
		int logicalPrimitiveComponentTypeBitLength = 8; //only pixel-interleaved-type things are supported for nows ._.
		return createTransparentBufferedImageBackedByByteBuffer(width, height, pixelFormat, PlatformNIOBufferUtilities.allocateByteBuffer(getImageBackingMinimumLength(width, height, pixelFormat, logicalPrimitiveComponentTypeBitLength), bufferAllocationType));
	}
	
	public static TransparentBufferedImageByteBuffer createNewTransparentBufferedImageBackedByByteBuffer(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		return createNewTransparentBufferedImageBackedByByteBuffer(width, height, pixelFormat, BufferAllocationType.PREFERABLY_DIRECT);
	}
	
	
	public static TransparentBufferedImage createNewTransparentBufferedImage(int width, int height, SimpleImageColorStorageType pixelFormat, Class backingType) throws IllegalArgumentException
	{
		if (backingType == byte[].class)
			return createNewTransparentBufferedImageBackedByByteArray(width, height, pixelFormat);
		else if (backingType == short[].class)
			return createNewTransparentBufferedImageBackedByShortArray(width, height, pixelFormat);
		else if (backingType == int[].class)
			return createNewTransparentBufferedImageBackedByIntArray(width, height, pixelFormat);
		else if (backingType == ByteBuffer.class)
			return createNewTransparentBufferedImageBackedByByteBuffer(width, height, pixelFormat);
		else
			throw new IllegalArgumentException("Unsupported or Invalid backing type: "+AngryReflectionUtility.getCodeName(backingType));
	}
	
	
	public static TransparentBufferedImage createNewTransparentBufferedImage(int width, int height, SimpleImageColorStorageType pixelFormat, Class backingType, BufferAllocationType bufferAllocationTypeIfByteBuffer) throws IllegalArgumentException
	{
		if (backingType == byte[].class)
			return createNewTransparentBufferedImageBackedByByteArray(width, height, pixelFormat);
		else if (backingType == short[].class)
			return createNewTransparentBufferedImageBackedByShortArray(width, height, pixelFormat);
		else if (backingType == int[].class)
			return createNewTransparentBufferedImageBackedByIntArray(width, height, pixelFormat);
		else if (backingType == ByteBuffer.class)
			return createNewTransparentBufferedImageBackedByByteBuffer(width, height, pixelFormat, bufferAllocationTypeIfByteBuffer);
		else
			throw new IllegalArgumentException("Unsupported or Invalid backing type: "+AngryReflectionUtility.getCodeName(backingType));
	}
	
	
	
	
	
	
	public static int getImageBackingMinimumLength(int width, int height, SimpleImageColorStorageType pixelFormat, int logicalPrimitiveComponentTypeBitLength) throws IllegalArgumentException
	{
		//packed formula: (width * height * pixelFormat.getBits()) \ logicalPrimitiveComponentTypeBitLength
		//unpacked formula: width * height * (pixelFormat.getBits() \ logicalPrimitiveComponentTypeBitLength)
		return SmallIntegerMathUtilities.ceilingDivision(width * height * pixelFormat.getBits(), logicalPrimitiveComponentTypeBitLength); //packed formula; since SimpleImageColorStorageType can encode padding bits :>
	}
	
	public static int getImageBackingLengthInts(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		return getImageBackingMinimumLength(width, height, pixelFormat, 32);
	}
	
	public static int getImageBackingLengthShorts(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		return getImageBackingMinimumLength(width, height, pixelFormat, 16);
	}
	
	public static int getImageBackingLengthBytes(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		return getImageBackingMinimumLength(width, height, pixelFormat, 8);
	}
	
	
	
	
	
	
	
	
	
	public static TransparentBufferedImageArrayByte createTransparentBufferedImageBackedByByteArray(int width, int height, SimpleImageColorStorageType pixelFormat, byte[] backing, int offset) throws IllegalArgumentException
	{
		return (TransparentBufferedImageArrayByte)createTransparentBufferedImageBackedByArray(width, height, pixelFormat, backing, offset);
	}
	
	public static TransparentBufferedImageArrayShort createTransparentBufferedImageBackedByShortArray(int width, int height, SimpleImageColorStorageType pixelFormat, short[] backing, int offset) throws IllegalArgumentException
	{
		return (TransparentBufferedImageArrayShort)createTransparentBufferedImageBackedByArray(width, height, pixelFormat, backing, offset);
	}
	
	public static TransparentBufferedImageArrayInt createTransparentBufferedImageBackedByIntArray(int width, int height, SimpleImageColorStorageType pixelFormat, int[] backing, int offset) throws IllegalArgumentException
	{
		return (TransparentBufferedImageArrayInt)createTransparentBufferedImageBackedByArray(width, height, pixelFormat, backing, offset);
	}
	
	
	public static TransparentBufferedImageByteBuffer createTransparentBufferedImageBackedByByteBuffer(int width, int height, SimpleImageColorStorageType pixelFormat, ByteBuffer backing) throws IllegalArgumentException
	{
		if (backing == null) throw new NullPointerException();
		
		return (TransparentBufferedImageByteBuffer)_createTransparentOrOpaqueBufferedImagePossiblyBackedByArray(width, height, pixelFormat, backing, 0);
	}
	
	
	public static TransparentBufferedImage createTransparentBufferedImageBackedByCopyOfByteBuffer(int width, int height, SimpleImageColorStorageType pixelFormat, ByteBuffer backing) throws IllegalArgumentException
	{
		if (pixelFormat.getBits() == 8)
		{
			byte[] copy = NIOBufferUtilities.copyToNewArray(backing);
			return createTransparentBufferedImageBackedByByteArray(width, height, pixelFormat, copy, 0);
		}
		else if (pixelFormat.getBits() == 16)
		{
			short[] copy = NIOBufferUtilities.copyToNewArray(backing.asShortBuffer());
			return createTransparentBufferedImageBackedByShortArray(width, height, pixelFormat, copy, 0);
		}
		else if (pixelFormat.getBits() == 32)
		{
			int[] copy = NIOBufferUtilities.copyToNewArray(backing.asIntBuffer());
			return createTransparentBufferedImageBackedByIntArray(width, height, pixelFormat, copy, 0);
		}
		
		else
		{
			throw new SimpleImageColorStorageTypeNotSupportedException(pixelFormat);
		}
	}
	
	
	
	/**
	 * Note: only {@link SinglePixelPackedSampleModel} (1 pixel to 1 backing-element) and {@link PixelInterleavedSampleModel} (1 pixel component to 1 backing-element) are supported
	 */
	public static TransparentBufferedImage createTransparentBufferedImageBackedByArray(int width, int height, SimpleImageColorStorageType pixelFormat, Object backing, int backingOffsetInElements) throws IllegalArgumentException
	{
		if (backing == null) throw new NullPointerException();
		
		return (TransparentBufferedImage)_createTransparentOrOpaqueBufferedImagePossiblyBackedByArray(width, height, pixelFormat, backing, backingOffsetInElements);
	}
	
	
	
	/**
	 * @param backing null for opaque images ;>
	 */
	protected static Object _createTransparentOrOpaqueBufferedImagePossiblyBackedByArray(int width, int height, SimpleImageColorStorageType pixelFormat, Object backing, int backingOffsetInElements) throws IllegalArgumentException
	{
		//Validate!
		int primitiveComponentTypeBitLength = 0;
		int backingOffsetInBits = 0;
		int backingCapacityInBits = 0;
		int backingCapacityInElements = 0;
		int imageSizeInBits = 0;
		int imageSizeInElements = 0;
		{
			if (width <= 0 || height <= 0)
				throw new IllegalArgumentException();
			
			if (pixelFormat == null)
				throw new NullPointerException();
			
			if (pixelFormat.isGrayscale())
				pixelFormat.ensureStandardGrayscaleWithOptionalAlpha();
			else
				pixelFormat.ensureStandardColorWithOptionalAlpha();
			
			//primitiveComponentTypeBitLength
			//backingCapacityInElements
			{
				if (backing == null)
				{
					//opaque ;>
					
					if (pixelFormat.getBits() == 32)
						primitiveComponentTypeBitLength = 32;
					
					else if (pixelFormat.getBits() == 24)
						primitiveComponentTypeBitLength = 8;  //odd one out; note that all of this is due to non-arbitrary bitfield primitive type silliness xD
					
					else if (pixelFormat.getBits() == 16)
						primitiveComponentTypeBitLength = pixelFormat.isGrayscale() ? 8 : 16;  //Todo could single-pixel-packed be used with grayscale color models?!  o,0
					
					else if (pixelFormat.getBits() == 8)
						primitiveComponentTypeBitLength = 8;
					
					else
						throw new IllegalArgumentException("Invalid padded element bitlength for opaque BufferedIage: "+pixelFormat.getBits()+" (only 8, 16, and 32 are supported in Java2D; sorries ._. )");
					
					imageSizeInBits = pixelFormat.getBits() * width * height;
					imageSizeInElements = SmallIntegerMathUtilities.ceilingDivision(imageSizeInBits, primitiveComponentTypeBitLength);
					
					backingCapacityInElements = imageSizeInElements; //make it whatever size it needs to be ^^
					backingOffsetInElements = 0;
				}
				else
				{
					if (backing instanceof byte[]) { backingCapacityInElements = ((byte[])backing).length; primitiveComponentTypeBitLength = 8; }
					else if (backing instanceof short[]) { backingCapacityInElements = ((short[])backing).length; primitiveComponentTypeBitLength = 16; }
					else if (backing instanceof int[]) { backingCapacityInElements = ((int[])backing).length; primitiveComponentTypeBitLength = 32; }
					else if (backing instanceof ByteBuffer) { backingCapacityInElements = ((ByteBuffer)backing).remaining(); backingOffsetInElements = ((ByteBuffer)backing).position(); primitiveComponentTypeBitLength = 8; }
					else throw new IllegalArgumentException("Invalid backing type: "+backing.getClass().getName());
					
					imageSizeInBits = pixelFormat.getBits() * width * height;
					imageSizeInElements = SmallIntegerMathUtilities.ceilingDivision(imageSizeInBits, primitiveComponentTypeBitLength);
				}
			}
			
			backingCapacityInBits = backingCapacityInElements * primitiveComponentTypeBitLength;
			backingOffsetInBits = backingOffsetInElements * primitiveComponentTypeBitLength;
			
			//Check offset is valid!
			if (backingOffsetInElements < 0 || backingOffsetInElements > backingCapacityInElements) throw new IllegalArgumentException();
			
			
			//Check backing is big enough!
			if (imageSizeInElements + backingOffsetInElements > backingCapacityInElements)
				throw new IllegalArgumentException("Backing is not large enough for the image!  (image is "+imageSizeInBits+" bits, backing starts at "+backingOffsetInBits+" bits and is "+backingCapacityInBits+" bits long in total)");
		}
		
		
		
		/*
		 * The rule is: we use single-pixel-packed if a pixel's bitlength is equal to the (logical) primitive bitlength,
		 * and a pixel interleaved across multiple elements otherwise.
		 * Other formats are not supported (eg, multiple-pixels-per-element, partial-pixel-per-element (between fully packed and fully component-interleaved), unaligned things, and *definitely* not multiple-backings ("banks") ), sorries ._.
		 * I would just like to point out that this entire subsystem would be unnecessary if Java (or any language!) supported arbitrary-bitlength bitfields, which probably wouldn't reduce performance very much at all given the only preferred bitlength is the system bus word-length, eg, 32 or 64 bits  (and also 4096 or whatever page file boundaries are of course)    /sighhhhhh ._.
		 * 		(also, if [un]signedness was supported generally, then all the non-floating-point primitives (6) would be unified ._. )
		 */
		boolean singlePixelPacked = false; //as opposed to pixel-interleaved
		{
			if (pixelFormat.isGrayscale())
			{
				//then single pixel packed flag is not used!
				//Todo could single-pixel-packed be used with grayscale color models?!  o,0
			}
			else
			{
				if (pixelFormat.getBits() == primitiveComponentTypeBitLength)
				{
					singlePixelPacked = true;
				}
				else
				{
					if (pixelFormat.getEqualBitCountOfEachComponent() != primitiveComponentTypeBitLength)
						throw new SimpleImageColorStorageTypeNotSupportedException(pixelFormat);
					
					if (pixelFormat.getBits() % primitiveComponentTypeBitLength != 0)
						throw new SimpleImageColorStorageTypeNotSupportedException(pixelFormat);
					
					//Paddings must be component
					if (pixelFormat.getNumberOfLowOrderBitsUnused() % primitiveComponentTypeBitLength != 0)
						throw new SimpleImageColorStorageTypeNotSupportedException(pixelFormat);
					
					if (pixelFormat.getNumberOfHighOrderBitsUnused() % primitiveComponentTypeBitLength != 0)
						throw new SimpleImageColorStorageTypeNotSupportedException(pixelFormat);
				}
			}
		}
		
		
		
		DataBuffer dataBuffer = null;
		int dataBufferDataType = 0;
		{
			if (backing == null)
			{
				if (primitiveComponentTypeBitLength == 8)
				{
					dataBuffer = new DataBufferByte(backingCapacityInElements);
					dataBufferDataType = DataBuffer.TYPE_BYTE;
				}
				else if (primitiveComponentTypeBitLength == 16)
				{
					dataBuffer = new DataBufferUShort(backingCapacityInElements);
					dataBufferDataType = DataBuffer.TYPE_USHORT;
				}
				else if (primitiveComponentTypeBitLength == 32)
				{
					dataBuffer = new DataBufferInt(backingCapacityInElements);
					dataBufferDataType = DataBuffer.TYPE_INT;
				}
				else
					throw new ImpossibleException("we already validated it! ;_;");
			}
			else if (backing instanceof byte[])
			{
				dataBuffer = new DataBufferByte((byte[])backing, backingCapacityInElements - backingOffsetInElements, backingOffsetInElements);
				dataBufferDataType = DataBuffer.TYPE_BYTE;
			}
			else if (backing instanceof short[])
			{
				dataBuffer = new DataBufferUShort((short[])backing, backingCapacityInElements - backingOffsetInElements, backingOffsetInElements);
				dataBufferDataType = DataBuffer.TYPE_USHORT;
			}
			else if (backing instanceof int[])
			{
				dataBuffer = new DataBufferInt((int[])backing, backingCapacityInElements - backingOffsetInElements, backingOffsetInElements);
				dataBufferDataType = DataBuffer.TYPE_INT;
			}
			else if (backing instanceof ByteBuffer)
			{
				dataBuffer = new DataBufferNIO((ByteBuffer)backing, backingOffsetInElements);
				dataBufferDataType = DataBuffer.TYPE_BYTE;
			}
			else throw new ImpossibleException(); //type already validated!
		}
		
		
		
		SampleModel sampleModel = null;
		{
			//Note: DirectColorModel mandates the masks/offsets be in the order {R, G, B, A} or {R, G, B}  (though of course the masks can specify the components in a different order in the backing element, you know, 'cause that's why they're there xD )
			
			if (pixelFormat.isGrayscale())
			{
				//sampleModel = new SinglePixelPackedSampleModel(dataBufferDataType, width, height, new int[]{pixelFormat.getGrayscaleMask()});  //oy Java2D XD
				
				int[] offsets = null;
				{
					offsets = new int[pixelFormat.hasAlpha() ? 2 : 1];
					offsets[0] = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getGrayscaleShift(), primitiveComponentTypeBitLength);
					if (pixelFormat.hasAlpha()) offsets[1] = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getAlphaShift(), primitiveComponentTypeBitLength);
				}
				
				int pixelStride = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getBits(), primitiveComponentTypeBitLength);
				
				sampleModel = new PixelInterleavedSampleModel(dataBufferDataType, width, height, pixelStride, width*pixelStride, offsets);
			}
			else
			{
				if (singlePixelPacked)
				{
					sampleModel = new SinglePixelPackedSampleModel(dataBufferDataType, width, height, pixelFormat.getMasksRGBAOptionalAlpha());
				}
				else //just pixel-interleaved ._.
				{
					int[] offsets = null;
					{
						offsets = new int[pixelFormat.hasAlpha() ? 4 : 3];
						offsets[0] = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getRedShift(), primitiveComponentTypeBitLength);
						offsets[1] = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getGreenShift(), primitiveComponentTypeBitLength);
						offsets[2] = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getBlueShift(), primitiveComponentTypeBitLength);
						if (pixelFormat.hasAlpha()) offsets[3] = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getAlphaShift(), primitiveComponentTypeBitLength);
					}
					
					int pixelStride = SmallIntegerMathUtilities.safe_div_s32(pixelFormat.getBits(), primitiveComponentTypeBitLength);
					
					sampleModel = new PixelInterleavedSampleModel(dataBufferDataType, width, height, pixelStride, width*pixelStride, offsets);
				}
			}
		}
		
		
		
		WritableRaster raster;
		{
			if (backing instanceof ByteBuffer && ClasshackingSunWritableRaster.has())  //hopefully the else{} clause will work on newer-than-Java-8 JRE's even if the backing is a ByteBuffer!  (until we get find a replacement for SunWritableRaster at least!)
			{
				raster = ClasshackingSunWritableRaster.newSunWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
			}
			else
			{
				raster = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
			}
		}
		
		
		
		ColorModel colorModel = null;
		{
			if (pixelFormat.isGrayscale())
			{
				colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_GRAY), pixelFormat.hasAlpha() ? new int[]{pixelFormat.getGrayscaleBitCount(), pixelFormat.getAlphaBitCount()} : new int[]{pixelFormat.getGrayscaleBitCount()}, pixelFormat.hasAlpha(), false, pixelFormat.hasAlpha() ? Transparency.TRANSLUCENT : Transparency.OPAQUE, dataBufferDataType);
			}
			else
			{
				if (singlePixelPacked)
				{
					colorModel = new DirectColorModel(pixelFormat.getBits(), pixelFormat.getRedMask(), pixelFormat.getGreenMask(), pixelFormat.getBlueMask(), pixelFormat.getAlphaMask());
				}
				else
				{
					colorModel = new ComponentColorModel(ColorSpace.getInstance(ColorSpace.CS_sRGB), pixelFormat.getBitCountsRGBAOptionalAlpha(), pixelFormat.hasAlpha(), false, pixelFormat.hasAlpha() ? Transparency.TRANSLUCENT : Transparency.OPAQUE, dataBufferDataType);
				}
			}
		}
		
		
		
		if (!colorModel.isCompatibleRaster(raster))
		{
			System.err.println("><!");
			String msg = "";
			
			msg += "ColorModel: ("+colorModel.getClass().getName()+"), color space="+colorModel.getColorSpace()+", pixel size="+colorModel.getPixelSize()+", number of components="+colorModel.getNumComponents()+", number of color components="+colorModel.getNumColorComponents()+", component sizes="+StringUtilities.repr(colorModel.getComponentSize())+", transfer type="+colorModel.getTransferType()+"\n";
			if (colorModel instanceof DirectColorModel)
			{
				//'bits' is getPixelSize() ^^
				DirectColorModel cm = (DirectColorModel)colorModel;
				msg += "\tredMask="+cm.getRedMask()+", greenMask="+cm.getGreenMask()+", blueMask="+cm.getBlueMask()+", alphaMask="+cm.getAlphaMask()+"\n";
			}
			else if (colorModel instanceof ComponentColorModel)
			{
				//everything already printed! XD
			}
			else
			{
				throw new ImpossibleException();
			}
			
			msg += "SampleModel: ("+sampleModel.getClass().getName()+"), number of bands="+sampleModel.getNumBands()+", sample sizes="+StringUtilities.repr(sampleModel.getSampleSize())+", data type="+sampleModel.getDataType()+", transfer type="+sampleModel.getTransferType()+"\n";
			if (sampleModel instanceof SinglePixelPackedSampleModel)
			{
				SinglePixelPackedSampleModel sm = (SinglePixelPackedSampleModel)sampleModel;
				msg += "\tmasks="+StringUtilities.repr(sm.getBitMasks())+"\n";
			}
			else if (sampleModel instanceof PixelInterleavedSampleModel)
			{
				PixelInterleavedSampleModel sm = (PixelInterleavedSampleModel)sampleModel;
				msg += "\toffsets="+StringUtilities.repr(sm.getBandOffsets())+"\n";
			}
			else
			{
				throw new ImpossibleException();
			}
			
			msg += "Raster: ("+raster.getClass().getName()+"), number of bands="+raster.getNumBands()+", transfer type="+raster.getTransferType()+"\n";
			
			System.err.println("Incompatible ColorModel and Raster+SampleModel! ><\n"+msg);
		}
		
		
		
		//Put it all together in that Java2D way! :D
		BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, null);
		
		
		if (backing == null)
			return bufferedImage;
		else
			return new PatheticallyLazyTransparentBufferedImageImpl(backing, backingOffsetInElements, bufferedImage, width, height, pixelFormat);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(BufferedImage extantBufferedImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType)
	{
		return wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(extantBufferedImage, destinationPixelFormat, destinationTransparentImageType, true);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(BufferedImage extantBufferedImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, BufferAllocationType bufferAllocationTypeIfNIO)
	{
		return wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(extantBufferedImage, destinationPixelFormat, destinationTransparentImageType, bufferAllocationTypeIfNIO, true);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D convertTransparentBufferedImageToGivenPixelFormat(TransparentBufferedImage extantImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType)
	{
		return convertTransparentBufferedImageToGivenPixelFormat(extantImage, destinationPixelFormat, destinationTransparentImageType, true);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D convertTransparentBufferedImageToGivenPixelFormat(TransparentBufferedImage extantImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, BufferAllocationType bufferAllocationTypeIfNIO)
	{
		return convertTransparentBufferedImageToGivenPixelFormat(extantImage, destinationPixelFormat, destinationTransparentImageType, bufferAllocationTypeIfNIO, true);
	}
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static TransparentBufferedImage copyNontransparentToTransparentBufferedImage(BufferedImage extantBufferedImage)
	{
		TransparentBufferedImage t = wrapOrCopyNontransparentToTransparentBufferedImage(extantBufferedImage);
		
		boolean copiedInFirstStep = t.getBufferedImage() != extantBufferedImage;
		
		if (copiedInFirstStep)
			return t;
		else
			return (TransparentBufferedImage) t.clone();
	}
	
	
	
	@ThrowAwayValue
	public static <D extends TransparentBufferedImage> D copyNontransparentToTransparentBufferedImageOfGivenPixelFormat(BufferedImage extantBufferedImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType)
	{
		return wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(extantBufferedImage, destinationPixelFormat, destinationTransparentImageType, false);
	}
	
	@ThrowAwayValue
	public static <D extends TransparentBufferedImage> D copyNontransparentToTransparentBufferedImageOfGivenPixelFormat(BufferedImage extantBufferedImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, BufferAllocationType bufferAllocationTypeIfNIO)
	{
		return wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(extantBufferedImage, destinationPixelFormat, destinationTransparentImageType, bufferAllocationTypeIfNIO, false);
	}
	
	@ThrowAwayValue
	public static <D extends TransparentBufferedImage> D copyConvertTransparentBufferedImageToGivenPixelFormat(TransparentBufferedImage extantImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType)
	{
		return convertTransparentBufferedImageToGivenPixelFormat(extantImage, destinationPixelFormat, destinationTransparentImageType, false);
	}
	
	@ThrowAwayValue
	public static <D extends TransparentBufferedImage> D copyConvertTransparentBufferedImageToGivenPixelFormat(TransparentBufferedImage extantImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, BufferAllocationType bufferAllocationTypeIfNIO)
	{
		return convertTransparentBufferedImageToGivenPixelFormat(extantImage, destinationPixelFormat, destinationTransparentImageType, bufferAllocationTypeIfNIO, false);
	}
	
	
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(BufferedImage extantBufferedImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, boolean passThroughWithoutCopyingIfPossible)
	{
		return wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(extantBufferedImage, destinationPixelFormat, destinationTransparentImageType, BufferAllocationType.PREFERABLY_DIRECT, passThroughWithoutCopyingIfPossible);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D wrapOrCopyNontransparentToTransparentBufferedImageOfGivenPixelFormat(BufferedImage extantBufferedImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, BufferAllocationType bufferAllocationTypeIfNIO, boolean passThroughWithoutCopyingIfPossible)
	{
		//Todolp a faster impl?? ^^'''
		
		TransparentBufferedImage t = wrapOrCopyNontransparentToTransparentBufferedImage(extantBufferedImage);
		
		boolean copiedInFirstStep = t.getBufferedImage() != extantBufferedImage;
		
		return convertTransparentBufferedImageToGivenPixelFormat(t, destinationPixelFormat, destinationTransparentImageType, bufferAllocationTypeIfNIO, copiedInFirstStep || passThroughWithoutCopyingIfPossible);
	}
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D convertTransparentBufferedImageToGivenPixelFormat(TransparentBufferedImage extantImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, boolean passThroughWithoutCopyingIfPossible)
	{
		return convertTransparentBufferedImageToGivenPixelFormat(extantImage, destinationPixelFormat, destinationTransparentImageType, BufferAllocationType.PREFERABLY_DIRECT, passThroughWithoutCopyingIfPossible);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static <D extends TransparentBufferedImage> D convertTransparentBufferedImageToGivenPixelFormat(TransparentBufferedImage extantImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> destinationTransparentImageType, BufferAllocationType bufferAllocationTypeIfNIO, boolean passThroughWithoutCopyingIfPossible)
	{
		Class destBackingType = getBackingApiTypeForJava2dInterfacingWrapperType(destinationTransparentImageType);
		
		
		if (passThroughWithoutCopyingIfPossible)
		{
			Class sourceBackingType = AccessibleInterleavedRasterImageBackingAllocationType.getBackingApiTypeForImage(extantImage);
			
			if (sourceBackingType == destBackingType)
			{
				SimpleImageColorStorageType currentPixelFormat = extantImage.getPixelFormat();
				
				if (BasicObjectUtilities.eq(currentPixelFormat, destinationPixelFormat))
				{
					if (destBackingType == ByteBuffer.class)
					{
						if (BufferAllocationType.isCompatibleForPassThroughAndCopyIsNotNeeded(((AccessibleInterleavedRasterImageByteBuffer)extantImage).getUnderlyingBackingByteBuffer(), bufferAllocationTypeIfNIO))
						{
							return (D)extantImage;
						}
					}
					else
					{
						return (D)extantImage;
					}
				}
			}
		}
		
		
		
		//We have to create a new one at this point regardless of pass-through enabled-ness!
		TransparentBufferedImage newImage = createNewTransparentBufferedImage(extantImage.getWidth(), extantImage.getHeight(), destinationPixelFormat, destBackingType, bufferAllocationTypeIfNIO);
		copyPixelData(extantImage, newImage);
		return (D)newImage;
	}
	
	public static Class getBackingApiTypeForJava2dInterfacingWrapperType(Class<? extends TransparentBufferedImage> java2dInterfacingWrapperType)
	{
		if (java2dInterfacingWrapperType == TransparentBufferedImageArrayByte.class)
			return byte[].class;
		else if (java2dInterfacingWrapperType == TransparentBufferedImageArrayShort.class)
			return short[].class;
		else if (java2dInterfacingWrapperType == TransparentBufferedImageArrayInt.class)
			return int[].class;
		else if (java2dInterfacingWrapperType == TransparentBufferedImageByteBuffer.class)
			return ByteBuffer.class;
		else
			throw new IllegalArgumentException("Invalid AccessibleInterleavedRasterImage subinterface!");
	}
	
	
	
	
	//	public static <D extends TransparentBufferedImage> D wrapOrCopyNontransparentToTransparentBufferedImageOfSimilarPixelFormatWithBestOrdering(BufferedImage extantBufferedImage, SimpleImageColorStorageType destinationPixelFormat, Class<D> transparentImageType)
	//	{
	//		throw new NotYetImplementedException();
	//	}
	
	
	
	//Todo implement ._.
	/**
	 * NOTE: There is no guarantee as to whether the returned {@link TransparentBufferedImage} will be using *the same underlying image* as the {@link BufferedImage} or if it will be a copy!!
	 * Particularly important ofc, if you modify either one!! X'D!
	 */
	@PossiblySnapshotPossiblyLiveValue
	public static TransparentBufferedImage wrapOrCopyNontransparentToTransparentBufferedImage(BufferedImage extantBufferedImage)
	{
		//Pull the thing out of an existing nice proper Java-ey BufferedImage (if possible)  x>
		
		
		
		DataBuffer dataBuffer = extantBufferedImage.getRaster().getDataBuffer();
		
		//Grab underlying backing!
		Object underlyingBacking = null;
		{
			//Array types
			
			if (dataBuffer.getNumBanks() != 1)
			{
				throw new NotYetImplementedException();
			}
			
			//Note: getData() may cause reallocation/de-optimization by forcing the underlying array into the Java heap which prevents C pointers from pointing to is since things can get moved around with the awesome dynamic memory reorganization things that Java does
			if (dataBuffer instanceof DataBufferByte)
				underlyingBacking = ((DataBufferByte)dataBuffer).getData();
			else if (dataBuffer instanceof DataBufferShort)
				underlyingBacking = ((DataBufferShort)dataBuffer).getData();
			else if (dataBuffer instanceof DataBufferUShort)
				underlyingBacking = ((DataBufferShort)dataBuffer).getData();
			else if (dataBuffer instanceof DataBufferInt)
				underlyingBacking = ((DataBufferInt)dataBuffer).getData();
			else if (dataBuffer instanceof DataBufferFloat)
				throw new IllegalArgumentException("Floating-point backing image data is not currently supported, only integer types, sorry ._.");
			else if (dataBuffer instanceof DataBufferDouble)
				throw new IllegalArgumentException("Floating-point backing image data is not currently supported, only integer types, sorry ._.");
			else
				throw new IllegalArgumentException(dataBuffer.getClass().getName()+" ...   I..do not know of this DataBuffer type!  Please enlighten me oh User!");
		}
		
		
		
		
		
		SimpleImageColorStorageType pixelFormat;
		
		ColorModel cm = extantBufferedImage.getColorModel();
		SampleModel sm = extantBufferedImage.getSampleModel();
		
		if (sm instanceof MultiPixelPackedSampleModel)
			throw new NotYetImplementedException();
		
		if (cm instanceof DirectColorModel)
		{
			if (sm instanceof SinglePixelPackedSampleModel)
			{
				DirectColorModel dcm = (DirectColorModel) cm;
				
				pixelFormat = ReboundAccessibleRasterImagesAndJRESystems.rariPixelFormatFromJava2DDirectColorModel(dcm);
			}
			else
			{
				throw new NotYetImplementedException();
			}
		}
		else if (cm instanceof ComponentColorModel)
		{
			ComponentColorModel ccm = (ComponentColorModel) cm;
			
			if (sm instanceof PixelInterleavedSampleModel)
			{
				pixelFormat = ReboundAccessibleRasterImagesAndJRESystems.rariPixelFormatFromJava2DInterleavedComponentColorModel(ccm, (PixelInterleavedSampleModel) sm);
			}
			else
			{
				throw new NotYetImplementedException();
			}
		}
		else
		{
			throw new NotYetImplementedException();
		}
		
		
		
		
		
		
		return new PatheticallyLazyTransparentBufferedImageImpl(underlyingBacking, dataBuffer.getOffset(), extantBufferedImage, extantBufferedImage.getWidth(), extantBufferedImage.getHeight(), pixelFormat);
	}
	
	
	
	
	
	
	//	public static BufferedImage createJava2DDefaultOpaqueBufferedImage(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	//	{
	//		//Todo!!
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static TransparentBufferedImage tbiCopyOf(AccessibleInterleavedRasterImage original)
	{
		return tbiCopyOf(original, original.getWidth(), original.getHeight());
	}
	
	public static TransparentBufferedImage tbiCopyOf(AccessibleInterleavedRasterImage original, int newWidth, int newHeight)
	{
		TransparentBufferedImage dest = createNewCompatibleTransparentBufferedImage(original, newWidth, newHeight);
		copyPixelData(original, dest);
		return dest;
	}
	
	
	
	
	public static void copyPixels(TransparentBufferedImage source, int sourceXStart, int sourceYStart, TransparentBufferedImage dest, int destXStart, int destYStart, int width, int height) throws IllegalArgumentException
	{
		if (width == 0 || height == 0) return;
		if (source == dest) return;
		
		
		
		if (width < 0 || height < 0) throw new IllegalArgumentException("Invalid [sub]dimensions: "+width+"x"+height);
		
		if (sourceXStart < 0 || sourceYStart < 0 || destXStart < 0 || destYStart < 0) throw new IllegalArgumentException("Invalid positions: "+sourceXStart+","+sourceYStart+"   "+destXStart+","+destYStart);
		
		if (width+sourceXStart > source.getWidth() || height+sourceYStart > source.getHeight())
			throw new IllegalArgumentException("Requested dimensions out of bounds of source! ("+sourceXStart+","+sourceYStart+"  "+width+"x"+height+")  (source is "+source.getWidth()+"x"+source.getHeight()+")");
		
		if (width+destXStart > dest.getWidth() || height+destYStart > dest.getHeight())
			throw new IllegalArgumentException("Requested dimensions out of bounds of dest! ("+destXStart+","+destYStart+"  "+width+"x"+height+")  (dest is "+dest.getWidth()+"x"+dest.getHeight()+")");
		
		
		
		//Todo cross-convert formats / encodings / etc. if the bitdepths match ._.
		
		if (!BasicObjectUtilities.eq(source.getPixelFormat(), dest.getPixelFormat()))
			throw new IllegalArgumentException("Incompatible images!");
		
		if (source.getUnderlyingBacking().getClass() != dest.getUnderlyingBacking().getClass())
			throw new IllegalArgumentException("Incompatible image backings!");
		
		
		
		int sourceBufferOffset = AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(source);
		int destBufferOffset = AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(dest);
		
		//Only pixel-interleaved / single-pixel-packed models are supported >,>
		if (source.getPixelFormat().getBits() % AccessibleInterleavedRasterImage.getElementBitlengthOr8IfNotArray(source) != 0) throw new NotYetImplementedException();
		int pixelStride = source.getPixelFormat().getBits() / AccessibleInterleavedRasterImage.getElementBitlengthOr8IfNotArray(source);
		
		Object tokenBacking = source.getUnderlyingBacking();
		
		if (tokenBacking.getClass().isArray())
		{
			Object sourceBackingArray = source.getUnderlyingBacking();
			Object destBackingArray = dest.getUnderlyingBacking();
			
			if (source.getWidth() == dest.getWidth() && dest.getWidth() == width)
			{
				//OPTIMIZATION! 8|
				int scanlineStride = width;
				System.arraycopy
				(
				sourceBackingArray, (sourceYStart*scanlineStride + sourceXStart)*pixelStride + sourceBufferOffset,
				destBackingArray, (destYStart*scanlineStride + destXStart)*pixelStride + destBufferOffset,
				height*width*pixelStride
				);
			}
			else
			{
				for (int y = 0; y < height; y++)
				{
					System.arraycopy
					(
					sourceBackingArray, ((y+sourceYStart)*source.getWidth() + sourceXStart)*pixelStride + sourceBufferOffset,
					destBackingArray, ((y+destYStart)*dest.getWidth() + destXStart)*pixelStride + destBufferOffset,
					width*pixelStride
					);
				}
			}
		}
		else if (tokenBacking instanceof Buffer)
		{
			throw new NotYetImplementedException();
		}
		else
		{
			throw new IllegalStateException(tokenBacking.getClass().getName());
		}
	}
	
	/**
	 * Copy pixels from source to dest, using whichever dimensions are smaller.
	 */
	public static void copyPixels(TransparentBufferedImage source, TransparentBufferedImage dest) throws IllegalArgumentException
	{
		copyPixels(source, 0, 0, dest, 0, 0, Math.min(source.getWidth(), dest.getWidth()), Math.min(source.getHeight(), dest.getHeight()));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Todo!
	//	public static class OffscreenNIOSurfaceData
	//	extends SurfaceData
	//	{
	//
	//
	//		public OffscreenNIOSurfaceData(SurfaceType surfaceType, ColorModel cm)
	//		{
	//			super(surfaceType, cm);
	//		}
	//
	//		@Override
	//		public SurfaceData getReplacement()
	//		{
	//			return null;
	//		}
	//
	//		@Override
	//		public GraphicsConfiguration getDeviceConfiguration()
	//		{
	//			return null;
	//		}
	//
	//		@Override
	//		public Raster getRaster(int x, int y, int w, int h)
	//		{
	//			return null;
	//		}
	//
	//		@Override
	//		public Rectangle getBounds()
	//		{
	//			return null;
	//		}
	//
	//		@Override
	//		public Object getDestination()
	//		{
	//			return null;
	//		}
	//	}
	
	
	
	
	
	
	
	
	
	public static class DataBufferNIO
	extends DataBuffer
	{
		protected ByteBuffer buffer;
		protected int absoluteOffset;
		//protected int lengthToRestrainOurselvesTo; //changed mah mind; /decided it should be checked earliers, and not on each access (I mean, Java2D is really the only thing accessing it; I think we can assume that's correct! XD )
		
		public DataBufferNIO(ByteBuffer buffer, int absoluteOffset) //, int lengthToRestrainOurselvesTo)
		{
			super(DataBuffer.TYPE_BYTE, buffer.capacity() - absoluteOffset); //lengthToRestrainOurselvesTo);
			this.buffer = buffer;
			this.absoluteOffset = absoluteOffset;
			//this.lengthToRestrainOurselvesTo = lengthToRestrainOurselvesTo;
		}
		
		@Override
		public int getElem(int bank, int i)
		{
			if (bank != 0) throw new IllegalArgumentException("Invalid bank: "+bank+" :P");
			int absIndex = this.absoluteOffset+i;
			//if (absIndex >= lengthToRestrainOurselvesTo) throw new IndexOutOfBoundsException("Relative index: "+i+", Absolute index: "+absIndex);
			return this.buffer.get(absIndex);
		}
		
		@Override
		public void setElem(int bank, int i, int val)
		{
			if (bank != 0) throw new IllegalArgumentException("Invalid bank: "+bank+" :P");
			//if (val < 0 || val > 255) throw new IllegalArgumentException("Invalid bitfield value for 8 bits!: "+val);  //maybe they expect us to truncatingly cast!  /shrugs
			
			int absIndex = this.absoluteOffset+i;
			//if (absIndex >= lengthToRestrainOurselvesTo) throw new IndexOutOfBoundsException("Relative index: "+i+", Absolute index: "+absIndex);
			this.buffer.put(absIndex, (byte)val);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	////////Atticcccc!////////
	
	
	/*old docs ._.
	 * Le master function! 8|
	 * This will wrap either a primitive array into a buffered image or vice versa, or create them both, or just the (non-transparent) BufferedImage! :D
	 *
	 * This will check all the nice dimensions, stride lengths, and format/storage-type things and throw {@link IllegalArgumentException} if they are not all happy.
	 *
	 * @param objectToWrap One of: <code>null</code> or [sub-Class of] DataBuffer.class indicating to create a non-transparent BufferedImage, primitive array to wrap, BufferedImage to pull out the backing array of, or a Class object specifying the type of underlying transparent backing to create (eg, int[].class) :>
	 * @param pixelFormat Can only be null if we are wrapping a BufferedImage that specifies this
	 * @param width Can only be -1 (unspecified) if we are wrapping a BufferedImage that specifies this
	 * @param height Can only be -1 (unspecified) if we are wrapping a BufferedImage that specifies this
	 * @throw {@link IllegalArgumentException} If anything is unhappy.
	 * @return either a {@link BufferedImage} or a {@link TransparentBufferedImage}
	 */
	
	
	
	/*
	public static Object getDirectImageDataBufferArray(BufferedImage image)
	{
		//return ((DataBufferInt)(image.getRaster().getDataBuffer())).getData();
	}
	 */
	
	
	/*
	public static LightweightBufferedImageInt createLightweightBufferedImageIntPerPixel(final int width, final int height, final SimpleImageColorStorageType pixelFormat)
	{
		final boolean alpha = pixelFormat.hasAlpha();
		final int[] directBufferArray = new int[width * height];
		final BufferedImage bufferedImage = wrapDirectDataBufferIntPerPixel(directBufferArray, width, pixelFormat);

		return new LightweightBufferedImageInt()
		{
			@Override
			public int[] getUnderlyingBacking()
			{
				return directBufferArray;
			}

			@Override
			public BufferedImage getBufferedImage()
			{
				return bufferedImage;
			}

			@Override
			public int getWidth()
			{
				return width;
			}

			@Override
			public int getHeight()
			{
				return directBufferArray.length / width;
			}

			@Override
			public boolean hasAlpha()
			{
				return alpha;
			}
		};
	}


	public static BufferedImage wrapDirectDataBufferIntARGB(int[] imageBuffer, int width)
	{
	}

	public static BufferedImage wrapDirectDataBufferIntRGBA(int[] imageBuffer, int width)
	{
		if ((imageBuffer.length % width) != 0)
			throw new IllegalArgumentException("Image data array's length ("+imageBuffer.length+") is not a multiple of width ("+width+")");

		int height = imageBuffer.length / width;

		DataBuffer dataBuffer = new DataBufferInt(imageBuffer, imageBuffer.length);
		SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, width, height, new int[]{0xFF000000, 0x00FF0000, 0x0000FF00, 0x000000FF});
		WritableRaster raster = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
		ColorModel colorModel = ColorModel.getRGBdefault();
		return new BufferedImage(colorModel, raster, false, null);
	}


	//	public static BufferedImage wrapDirectDataBufferIntARGB(int[] imageBuffer, int bufferWidth, int restrictedL, int restrictedT, int restrictedW, int restrictedH)
	//	{
	//		if ((imageBuffer.length % bufferWidth) != 0)
	//			throw new IllegalArgumentException("Image data array's length ("+imageBuffer.length+") is not a multiple of bufferWidth ("+bufferWidth+")");
	//
	//		int bufferHeight = imageBuffer.length / bufferWidth;
	//
	//
	//		DataBuffer dataBuffer = new DataBufferInt(imageBuffer, imageBuffer.length);
	//		SampleModel sampleModel = new SinglePixelPackedSampleModel(DataBuffer.TYPE_INT, bufferWidth, bufferHeight, new int[]{0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000});
	//		WritableRaster rasterAll = Raster.createWritableRaster(sampleModel, dataBuffer, new Point(0, 0));
	//		WritableRaster raster = rasterAll.createWritableChild(restrictedL, restrictedT, restrictedW, restrictedH, 0, 0, null);
	//		ColorModel colorModel = ColorModel.getRGBdefault();
	//		return new BufferedImage(colorModel, raster, false, null);
	//	}





	public static LightweightBufferedImage3ByteRGB createLightweightBufferedImage3ByteRGB(final int width, final int height)
	{
		/*
	 * Shamelessly copied from case TYPE_3BYTE_BGR: of BufferedImage constructor (int width, int height, int imageType)
	 * ._.
	 * /
		ColorSpace cs = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		int[] nBits = {8, 8, 8};
		int[] bOffs = {0, 1, 2}; //RPMOD: RGB not BGR  (BGR ONLY?! REALLY?  WHYYYYYY NOO RRRGGGBBB JAVAAAAA? :< )
		ColorModel colorModel = new ComponentColorModel(cs, nBits, false, false,
			Transparency.OPAQUE,
			DataBuffer.TYPE_BYTE);
		WritableRaster raster = Raster.createInterleavedRaster(DataBuffer.TYPE_BYTE,
			width, height,
			width*3, 3,
			bOffs, null);



		final BufferedImage bufferedImage = new BufferedImage(colorModel, raster, false, null);
		final byte[] directBuffer = getDirectImageDataBuffer_3BYTE_RGB(bufferedImage);

		return new LightweightBufferedImage3ByteRGB()
		{
			@Override
			public byte[] getUnderlyingBacking()
			{
				return directBuffer;
			}

			@Override
			public BufferedImage getBufferedImage()
			{
				return bufferedImage;
			}

			@Override
			public int getWidth()
			{
				return width;
			}

			@Override
			public int getHeight()
			{
				int len = directBuffer.length;
				if (len % 3 != 0)
					throw new IllegalStateException("pixel data buffer length is not a multiple of 3, BUT IT'S 3-BYTE PACKED RGB! :O");
				int pixels = len / 3;
				if (pixels % width != 0)
					throw new IllegalStateException("pixel data buffer ("+pixels+" pixels) is not a multiple of stride-length/image-width ("+width+")");
				return len / width;
			}
		};
	}
	 */
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static BufferedImage createNewOpaqueBufferedImageBackedByByteArray(int width, int height, SimpleImageColorStorageType pixelFormat) throws IllegalArgumentException
	{
		if (pixelFormat == null) throw new NullPointerException();
		
		if (pixelFormat.equals(TYPE_ARGB32))
			return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		else if (pixelFormat.equals(TYPE_ABGR32))
			return new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR);
		else if (pixelFormat.equals(TYPE_RGB32))
			return new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		else if (pixelFormat.equals(TYPE_BGR32))
			return new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
		else if (pixelFormat.equals(TYPE_BGR24))
			return new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
		
		else if (pixelFormat.equals(TYPE_B5G6R5_16))
			return new BufferedImage(width, height, BufferedImage.TYPE_USHORT_565_RGB);
		else if (pixelFormat.equals(TYPE_B5G5R5_16))
			return new BufferedImage(width, height, BufferedImage.TYPE_USHORT_555_RGB);
		
		else if (pixelFormat.equals(TYPE_GRAYSCALE_8))
			return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
		else if (pixelFormat.equals(TYPE_GRAYSCALE_16))
			return new BufferedImage(width, height, BufferedImage.TYPE_USHORT_GRAY);
		else if (pixelFormat.equals(TYPE_BLACK_AND_WHITE))
			return new BufferedImage(width, height, BufferedImage.TYPE_BYTE_BINARY);
		
		
		else
		{
			return (BufferedImage)_createTransparentOrOpaqueBufferedImagePossiblyBackedByArray(width, height, pixelFormat, null, 0);  //Todo debug/test this in opaque-mode XD
		}
	}
	
	
	
	
	
	//Namely, from IndexedColorModel! :D
	public static BufferedImage convertToComponent(BufferedImage i)
	{
		final int w = i.getWidth();
		final int h = i.getHeight();
		
		final BufferedImage component;
		{
			ColorModel cm = i.getColorModel();
			
			//Todo preserve grayscale if used!
			//			if (cm.getColorSpace().getType() == ColorSpace.TYPE_GRAY)
			//				component = cm.hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB;
			//			else
			component = new BufferedImage(w, h, cm.hasAlpha() ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
		}
		
		
		
		component.createGraphics().drawImage(i, 0, 0, null);
		
		return component;
	}
	
	
	
	
	
	
	
	
	public static void debugDumpImageToFile(TransparentBufferedImage image, File file)
	{
		ImageUtilities.debugDumpImageToFile(image.getBufferedImage(), file);
	}
}
