package rebound.hci.graphics2d.ourrasters;

import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.math.geom2d.SmallIntegerBasicGeometry2D.*;
import static rebound.util.BasicExceptionUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.temporal.PossiblySnapshotPossiblyLiveValue;
import rebound.bits.Bytes;
import rebound.exceptions.NotYetImplementedException;
import rebound.hci.graphics2d.ourrasters.AbstractAccessibleInterleavedRasterImageArray.SimpleAccessibleInterleavedRasterImageArrayByte;
import rebound.hci.graphics2d.ourrasters.AbstractAccessibleInterleavedRasterImageArray.SimpleAccessibleInterleavedRasterImageArrayInt;
import rebound.hci.graphics2d.ourrasters.AbstractAccessibleInterleavedRasterImageArray.SimpleAccessibleInterleavedRasterImageArrayShort;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayByte;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayInt;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayShort;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageByteBuffer;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType.ArrayByteAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType.ArrayIntAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType.ArrayShortAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ByteBufferAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.math.SmallIntegerMathUtilities;
import rebound.math.geom.ints.analogoustojavaawt.IntPoint;
import rebound.math.geom.ints.analogoustojavaawt.IntRectangle;
import rebound.math.geom2d.LosslessRandRAffineTransform;
import rebound.util.BufferAllocationType;
import rebound.util.NIOBufferUtilities;
import rebound.util.PlatformNIOBufferUtilities;
import rebound.util.collections.ArrayUtilities;

/**
 * See also ReboundAccessibleRasterImagesAndJRESystems in another package which can depend on Java2D and thus contains bridging code!  \:D/
 */
public class AccessibleRasterImages
{
	public static AccessibleInterleavedRasterImage createNew(int width, int height, SimpleImageColorStorageType pixelFormat, AccessibleInterleavedRasterImageBackingAllocationType backingType)
	{
		if (backingType instanceof ArrayAccessibleInterleavedRasterImageBackingAllocationType)
		{
			if (backingType == ArrayByteAccessibleInterleavedRasterImageBackingAllocationType.I)
			{
				return createNewBackedByByteArray(width, height, pixelFormat);
			}
			else if (backingType == ArrayShortAccessibleInterleavedRasterImageBackingAllocationType.I)
			{
				return createNewBackedByShortArray(width, height, pixelFormat);
			}
			else if (backingType == ArrayIntAccessibleInterleavedRasterImageBackingAllocationType.I)
			{
				return createNewBackedByIntArray(width, height, pixelFormat);
			}
			else
			{
				throw new IllegalArgumentException();
			}
		}
		else if (backingType instanceof ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)
		{
			return createNewBackedByByteBuffer(width, height, pixelFormat, ((ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)backingType).getBufferAllocationType());
		}
		else
		{
			throw new IllegalArgumentException();
		}
	}
	
	public static AccessibleInterleavedRasterImage createNewCompatible(int width, int height, AccessibleInterleavedRasterImage compatibleWithThisOne)
	{
		return createNew(width, height, compatibleWithThisOne.getPixelFormat(), AccessibleInterleavedRasterImageBackingAllocationType.getBackingTypeForImage(compatibleWithThisOne));
	}
	
	public static AccessibleInterleavedRasterImage createNewCompatible(AccessibleInterleavedRasterImage compatibleWithThisOne)
	{
		return createNewCompatible(compatibleWithThisOne.getWidth(), compatibleWithThisOne.getHeight(), compatibleWithThisOne);
	}
	
	
	
	
	public static AccessibleInterleavedRasterImageArrayByte createNewBackedByByteArray(int width, int height, SimpleImageColorStorageType pixelFormat)
	{
		int nPixels = width * height;
		int nElements = ceilingDivision(nPixels * pixelFormat.getBits(), 8);
		byte[] backing = new byte[nElements];
		return new SimpleAccessibleInterleavedRasterImageArrayByte(width, height, pixelFormat, backing);
	}
	
	public static AccessibleInterleavedRasterImageArrayShort createNewBackedByShortArray(int width, int height, SimpleImageColorStorageType pixelFormat)
	{
		int nPixels = width * height;
		int nElements = ceilingDivision(nPixels * pixelFormat.getBits(), 16);
		short[] backing = new short[nElements];
		return new SimpleAccessibleInterleavedRasterImageArrayShort(width, height, pixelFormat, backing);
	}
	
	public static AccessibleInterleavedRasterImageArrayInt createNewBackedByIntArray(int width, int height, SimpleImageColorStorageType pixelFormat)
	{
		int nPixels = width * height;
		int nElements = ceilingDivision(nPixels * pixelFormat.getBits(), 32);
		int[] backing = new int[nElements];
		return new SimpleAccessibleInterleavedRasterImageArrayInt(width, height, pixelFormat, backing);
	}
	
	public static AccessibleInterleavedRasterImageByteBuffer createNewBackedByByteBuffer(int width, int height, SimpleImageColorStorageType pixelFormat, BufferAllocationType bufferAllocationType)
	{
		int nPixels = width * height;
		int nBytes = ceilingDivision(nPixels * pixelFormat.getBits(), 8);
		ByteBuffer backing = PlatformNIOBufferUtilities.allocateByteBuffer(nBytes, bufferAllocationType);
		
		return new SimpleAccessibleInterleavedRasterImageByteBuffer(width, height, pixelFormat, backing);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean areAccessibleInterleavedRasterImagesCurrentlyEqual(AccessibleInterleavedRasterImage a, AccessibleInterleavedRasterImage b)
	{
		if (!eq(a.getPixelFormat(), b.getPixelFormat()))
			return false;
		
		
		
		Object thisBacking = a.getUnderlyingBacking();
		
		Object otherBacking = b.getUnderlyingBacking();
		
		if (thisBacking.getClass() != otherBacking.getClass())
			return false;
		
		
		
		int lengthInElements = AccessibleInterleavedRasterImage.getLengthInElementsOrBytesIfNotArray(a);
		
		if (AccessibleInterleavedRasterImage.getLengthInElementsOrBytesIfNotArray(b) != lengthInElements)
			return false;
		
		
		return NIOBufferUtilities.arraysOrBuffersEqual(thisBacking, AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(a), otherBacking, AccessibleInterleavedRasterImage.getOffsetInElementsOrBytesIfNotArray(b), lengthInElements);
	}
	
	
	
	public static boolean areAccessibleInterleavedRasterImagesCurrentlyPixelwiseEquivalent(AccessibleInterleavedRasterImage a, AccessibleInterleavedRasterImage b)
	{
		if (eq(a.getPixelFormat(), b.getPixelFormat()))
		{
			return areAccessibleInterleavedRasterImagesCurrentlyEqual(a, b);
		}
		else
		{
			//Todo compare in-place ^^''
			
			SimpleImageColorStorageType lcd = SimpleImageColorStorageType.leastCommonDenominator(a.getPixelFormat(), b.getPixelFormat());
			AccessibleInterleavedRasterImage newA = convertOrReturnIfAlreadyInDesiredFormat(a, lcd);
			AccessibleInterleavedRasterImage newB = convertOrReturnIfAlreadyInDesiredFormat(b, lcd);
			
			return areAccessibleInterleavedRasterImagesCurrentlyEqual(newA, newB);
		}
	}
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static AccessibleInterleavedRasterImage convertOrReturnIfAlreadyInDesiredFormat(@PossiblySnapshotPossiblyLiveValue AccessibleInterleavedRasterImage sourceImage, SimpleImageColorStorageType destinationPixelFormat)
	{
		if (eq(sourceImage.getPixelFormat(), destinationPixelFormat))
		{
			return sourceImage;
		}
		
		
		AccessibleInterleavedRasterImage destinationImage = sourceImage.newCompatibleInstance();
		convertPixelFormat(sourceImage, destinationImage);
		return destinationImage;
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static AccessibleInterleavedRasterImage convertOrReturnIfAlreadyInDesiredFormat(@PossiblySnapshotPossiblyLiveValue AccessibleInterleavedRasterImage sourceImage, SimpleImageColorStorageType destinationPixelFormat, AccessibleInterleavedRasterImageBackingAllocationType destinationBackingType)
	{
		if (eq(sourceImage.getPixelFormat(), destinationPixelFormat))
		{
			Class sourceBackingApiType = AccessibleInterleavedRasterImageBackingAllocationType.getBackingApiTypeForImage(sourceImage);
			Class destBackingApiType = AccessibleInterleavedRasterImageBackingAllocationType.getBackingApiClass(destinationBackingType);
			
			if (sourceBackingApiType == destBackingApiType)
			{
				Object backingApiType = sourceBackingApiType;  // == destBackingApiType  :33
				
				if (backingApiType == ByteBuffer.class)
				{
					ByteBuffer sourceBacking = ((AccessibleInterleavedRasterImageByteBuffer)sourceImage).getUnderlyingBackingByteBuffer();
					BufferAllocationType destByteBufferType = ((ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)destinationBackingType).getBufferAllocationType();
					
					if (PlatformNIOBufferUtilities.isGarbageCollected(sourceBacking) == destByteBufferType.isGarbageCollected())
					{
						if (destByteBufferType == BufferAllocationType.PREFERABLY_DIRECT)
						{
							//"Preferrable" means either way is fine!  So it's compatible with eitherrrrrr!! :DDD
							return sourceImage;
						}
						
						boolean sourceDirect = sourceBacking.isDirect();
						boolean destDirect = destByteBufferType.isDirect();
						
						if (sourceDirect == destDirect)
						{
							//FINALLY WE CAN TELL IT'S COMPATIBLE XDD'''
							return sourceImage;
						}
					}
				}
				else
				{
					//Arrays are allllllwayyyyyyys compatibleeeeeeee!! :DDD
					return sourceImage;
				}
			}
		}
		
		
		
		
		
		//Gotta create a new oneeeeeee :333
		return convertToNew(sourceImage, destinationPixelFormat, destinationBackingType);
	}
	
	
	
	
	
	
	@ThrowAwayValue
	public static AccessibleInterleavedRasterImage convertToNew(@PossiblySnapshotPossiblyLiveValue AccessibleInterleavedRasterImage sourceImage, SimpleImageColorStorageType destinationPixelFormat)
	{
		AccessibleInterleavedRasterImage destinationImage = sourceImage.newCompatibleInstance();
		convertPixelFormat(sourceImage, destinationImage);
		return destinationImage;
	}
	
	
	@ThrowAwayValue
	public static AccessibleInterleavedRasterImage convertToNew(@PossiblySnapshotPossiblyLiveValue AccessibleInterleavedRasterImage sourceImage, SimpleImageColorStorageType destinationPixelFormat, AccessibleInterleavedRasterImageBackingAllocationType destinationBackingType)
	{
		AccessibleInterleavedRasterImage destinationImage = createNew(sourceImage.getWidth(), sourceImage.getHeight(), destinationPixelFormat, destinationBackingType);
		convertPixelFormat(sourceImage, destinationImage);
		return destinationImage;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static void convertPixelFormat(AccessibleInterleavedRasterImage source, AccessibleInterleavedRasterImage dest)
	{
		if (source.getPixelFormat().equals(dest.getPixelFormat()))
		{
			//Many fasters! :DDD
			copyPixelData(source, dest);
			return;
		}
		
		
		
		int w = source.getWidth();
		int h = source.getHeight();
		if (w != dest.getWidth())
			throw new IllegalArgumentException("Widths don't match!!");
		if (h != dest.getHeight())
			throw new IllegalArgumentException("Heights don't match!!");
		
		convertPixelFormat(source, 0, 0, dest, 0, 0, w, h);
	}
	
	public static void convertPixelFormat(AccessibleInterleavedRasterImage source, int sourceX, int sourceY, AccessibleInterleavedRasterImage dest, int destX, int destY, int subregionWidth, int subregionHeight)
	{
		SimpleImageColorStorageType sourcePixelFormat = source.getPixelFormat();
		SimpleImageColorStorageType destPixelFormat = dest.getPixelFormat();
		
		if (eq(sourcePixelFormat, destPixelFormat))
		{
			//Many fasters! :DDD
			copyPixelData(source, sourceX, sourceY, dest, destX, destY, subregionWidth, subregionHeight);
			return;
		}
		
		
		if (!SimpleImageColorStorageType.canConvertFromOneToOther(sourcePixelFormat, destPixelFormat))
			throw new IllegalArgumentException("Pixel formats can't be converted!!: "+sourcePixelFormat+" -> "+destPixelFormat);
		
		
		
		IntRectangle sourceMaxBounds = irect(0, 0, source.getWidth(), source.getHeight());
		IntRectangle destMaxBounds = irect(0, 0, dest.getWidth(), dest.getHeight());
		
		IntRectangle pixelsInSource = irect(sourceX, sourceY, subregionWidth, subregionHeight);
		IntRectangle.intersect(sourceMaxBounds, pixelsInSource, pixelsInSource);
		
		IntRectangle pixelsInDest = irect(destX, destY, pixelsInSource.width, pixelsInSource.height);
		IntRectangle.intersect(destMaxBounds, pixelsInDest, pixelsInDest);
		
		
		
		if (source.getUnderlyingBacking() instanceof int[] && dest.getUnderlyingBacking() instanceof int[] && sourcePixelFormat.getBits() == 32 && destPixelFormat.getBits() == 32)
		{
			int[] src = (int[]) source.getUnderlyingBacking();
			int[] dst = (int[]) dest.getUnderlyingBacking();
			int srcOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
			int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int sS = source.getWidth();
			int dS = dest.getWidth();
			
			
			for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
			{
				int dY = destY + destYr;
				int sY = sourceY + destYr;
				
				for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
				{
					int dX = destX + destXr;
					int sX = sourceX + destXr;
					
					int pixelValueInSouceFormat = src[srcOffset + (sY * sS + sX)];
					
					int pixelValueInDestFormat = SimpleImageColorStorageType.convertPacked32(pixelValueInSouceFormat, sourcePixelFormat, destPixelFormat);
					
					dst[dstOffset + (dY * dS + dX)] = pixelValueInDestFormat;
				}
			}
		}
		else if (source.getUnderlyingBacking() instanceof byte[] && dest.getUnderlyingBacking() instanceof int[] && sourcePixelFormat.getBits() == 32 && destPixelFormat.getBits() == 32)
		{
			byte[] src = (byte[]) source.getUnderlyingBacking();
			final int srcBytesPerPixel = 4;
			int[] dst = (int[]) dest.getUnderlyingBacking();
			int srcOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
			int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int sS = source.getWidth();
			int dS = dest.getWidth();
			
			
			for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
			{
				int dY = destY + destYr;
				int sY = sourceY + destYr;
				
				for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
				{
					int dX = destX + destXr;
					int sX = sourceX + destXr;
					
					int pixelValueInSouceFormat = Bytes.getLittleInt(src, srcOffset + ((sY * sS + sX) * srcBytesPerPixel));
					
					int pixelValueInDestFormat = SimpleImageColorStorageType.convertPacked32(pixelValueInSouceFormat, sourcePixelFormat, destPixelFormat);
					
					dst[dstOffset + (dY * dS + dX)] = pixelValueInDestFormat;
				}
			}
		}
		else if (source.getUnderlyingBacking() instanceof byte[] && dest.getUnderlyingBacking() instanceof int[] && sourcePixelFormat.getBits() == 24 && destPixelFormat.getBits() == 32)
		{
			byte[] src = (byte[]) source.getUnderlyingBacking();
			final int srcBytesPerPixel = 3;
			int[] dst = (int[]) dest.getUnderlyingBacking();
			int srcOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
			int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int sS = source.getWidth();
			int dS = dest.getWidth();
			
			
			for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
			{
				int dY = destY + destYr;
				int sY = sourceY + destYr;
				
				for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
				{
					int dX = destX + destXr;
					int sX = sourceX + destXr;
					
					int pixelValueInSouceFormat = Bytes.getLittleInt24(src, srcOffset + ((sY * sS + sX) * srcBytesPerPixel));
					
					int pixelValueInDestFormat = SimpleImageColorStorageType.convertPacked32(pixelValueInSouceFormat, sourcePixelFormat, destPixelFormat);
					
					dst[dstOffset + (dY * dS + dX)] = pixelValueInDestFormat;
				}
			}
		}
		else
		{
			for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
			{
				int dY = destY + destYr;
				int sY = sourceY + destYr;
				
				for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
				{
					int dX = destX + destXr;
					int sX = sourceX + destXr;
					
					dest.setPixelValue(dX, dY, source.getPixelValue(sX, sY), source.getPixelFormat());
				}
			}
		}
	}
	
	
	
	
	
	
	
	//	public static void convertPixelFormatAndPixellyIntegerScaleUp(AccessibleInterleavedRasterImage source, int sourceX, int sourceY, AccessibleInterleavedRasterImage dest, int destX, int destY, int subregionWidthInSource, int subregionHeightInSource, int scaleFactor)
	//	{
	//		if (source.getPixelFormat().equals(dest.getPixelFormat()))
	//		{
	//			//Many fasters! :DDD
	//			pixellyIntegerScaleUp(source, sourceX, sourceY, dest, destX, destY, subregionWidthInSource, subregionHeightInSource, scaleFactor);
	//			return;
	//		}
	//
	//		//TODO check boundssssssss!!
	//
	//		//TODO OOOOOOOOOOOO x'D
	//		throw new NotYetImplementedException();
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void copyPixelData(AccessibleInterleavedRasterImage source, AccessibleInterleavedRasterImage dest)
	{
		int w = source.getWidth();
		int h = source.getHeight();
		if (w != dest.getWidth())
			throw new IllegalArgumentException("Widths don't match!!");
		if (h != dest.getHeight())
			throw new IllegalArgumentException("Heights don't match!!");
		
		
		copyPixelData(source, 0, 0, dest, 0, 0, w, h);
	}
	
	public static void copyPixelData(@ReadonlyValue AccessibleInterleavedRasterImage source, int sourceX, int sourceY, @WritableValue AccessibleInterleavedRasterImage dest, int destX, int destY)
	{
		copyPixelData(source, sourceX, sourceY, dest, destX, destY, source.getWidth(), source.getHeight());
	}
	
	public static void copyPixelData(@ReadonlyValue AccessibleInterleavedRasterImage source, int sourceX, int sourceY, @WritableValue AccessibleInterleavedRasterImage dest, int destX, int destY, int subregionWidthInDest, int subregionHeightInDest)
	{
		if (sourceX < 0) throw new IllegalArgumentException();
		if (sourceY < 0) throw new IllegalArgumentException();
		if (destX < 0) throw new IllegalArgumentException();
		if (destY < 0) throw new IllegalArgumentException();
		if (subregionWidthInDest < 0) throw new IllegalArgumentException();
		if (subregionHeightInDest < 0) throw new IllegalArgumentException();
		
		
		if (subregionWidthInDest == 0 || subregionHeightInDest == 0)
			//Zero pixels to process! XD'
			return;
		
		
		SimpleImageColorStorageType pixelFormat;
		{
			SimpleImageColorStorageType sourcePixelFormat = source.getPixelFormat();
			SimpleImageColorStorageType destPixelFormat = dest.getPixelFormat();
			if (!SimpleImageColorStorageType.canConvertFromOneToOther(sourcePixelFormat, destPixelFormat))
				throw new IllegalArgumentException("Pixel formats can't be converted!!: "+sourcePixelFormat+" -> "+destPixelFormat);
			pixelFormat = sourcePixelFormat;
		}
		
		
		IntRectangle sourceMaxBounds = irect(0, 0, source.getWidth(), source.getHeight());
		IntRectangle destMaxBounds = irect(0, 0, dest.getWidth(), dest.getHeight());
		
		IntRectangle pixelsInSource = irect(sourceX, sourceY, subregionWidthInDest, subregionHeightInDest);
		IntRectangle.intersect(sourceMaxBounds, pixelsInSource, pixelsInSource);
		
		IntRectangle pixelsInDest = irect(destX, destY, pixelsInSource.width, pixelsInSource.height);
		IntRectangle.intersect(destMaxBounds, pixelsInDest, pixelsInDest);
		
		
		
		if (!eq(source.getPixelFormat(), dest.getPixelFormat()))
		{
			convertPixelFormat(source, sourceX, sourceY, dest, destX, destY, subregionWidthInDest, subregionHeightInDest);
		}
		else
		{
			if (source.getUnderlyingBacking() instanceof int[] && dest.getUnderlyingBacking() instanceof int[] && pixelFormat.getBits() == 32)
			{
				int[] src = (int[]) source.getUnderlyingBacking();
				int[] dst = (int[]) dest.getUnderlyingBacking();
				int srcOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
				int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
				
				int sS = source.getWidth();
				int dS = dest.getWidth();
				
				
				boolean sourceHasAlpha = source.getPixelFormat().hasAlpha();
				boolean destHasAlpha = dest.getPixelFormat().hasAlpha();
				
				if (sourceHasAlpha == destHasAlpha)
				{
					for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
					{
						int dY = destY + destYr;
						int sY = sourceY + destYr;
						
						for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
						{
							int dX = destX + destXr;
							int sX = sourceX + destXr;
							
							dst[dstOffset + (dY * dS + dX)] = src[srcOffset + (sY * sS + sX)];
						}
					}
				}
				else
				{
					int sourceNonalphaMaskIfNecessary = destHasAlpha ? 0xFFFF_FFFF : ~source.getPixelFormat().getAlphaMask();  //if source has alpha (and dest doesn't!), we have to remove the alpha of each pixel, setting it to zero!!
					int destOpaquePressIfNecessary = destHasAlpha ? dest.getPixelFormat().getAlphaMask() : 0;  //if dest has alpha (but source doesn't and is uniformly perfectly opaque!), then we have to set dest's pixels to their maximum alpha value for each pixel!!
					
					for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
					{
						int dY = destY + destYr;
						int sY = sourceY + destYr;
						
						for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
						{
							int dX = destX + destXr;
							int sX = sourceX + destXr;
							
							int sourcePixelValue = src[srcOffset + (sY * sS + sX)];
							sourcePixelValue &= sourceNonalphaMaskIfNecessary;
							dst[dstOffset + (dY * dS + dX)] = sourcePixelValue | destOpaquePressIfNecessary;
						}
					}
				}
			}
			else
			{
				//Todo make other important cases faster! ^^"
				
				for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
				{
					int dY = destY + destYr;
					int sY = sourceY + destYr;
					
					for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
					{
						int dX = destX + destXr;
						int sX = sourceX + destXr;
						
						dest.setPixelValue(dX, dY, source.getPixelValue(sX, sY));
					}
				}
			}
		}
	}
	
	
	
	
	
	
	public static void copyPixelDataPossiblyTiling(@ReadonlyValue AccessibleInterleavedRasterImage source, int sourceX, int sourceY, @WritableValue AccessibleInterleavedRasterImage dest, int destX, int destY, int subregionWidthInDest, int subregionHeightInDest, boolean tileX, boolean tileY, int zoomFactor)
	{
		if (!tileX && !tileY && zoomFactor == 1)
		{
			copyPixelData(source, sourceX, sourceY, dest, destX, destY, subregionWidthInDest, subregionHeightInDest);
			return;
		}
		
		
		if (sourceX < 0) throw new IllegalArgumentException();
		if (sourceY < 0) throw new IllegalArgumentException();
		if (destX < 0) throw new IllegalArgumentException();
		if (destY < 0) throw new IllegalArgumentException();
		if (subregionWidthInDest < 0) throw new IllegalArgumentException();
		if (subregionHeightInDest < 0) throw new IllegalArgumentException();
		if (zoomFactor < 1) throw new IllegalArgumentException("Illegal zoom factor!!: "+zoomFactor);
		
		
		if (subregionWidthInDest == 0 || subregionHeightInDest == 0)
			//Zero pixels to process! XD'
			return;
		
		
		
		
		
		SimpleImageColorStorageType pixelFormat;
		{
			SimpleImageColorStorageType sourcePixelFormat = source.getPixelFormat();
			SimpleImageColorStorageType destPixelFormat = dest.getPixelFormat();
			if (!sourcePixelFormat.equalsSaveForAlphaPresence(destPixelFormat))
				throw new IllegalArgumentException("Pixel formats don't match!!: "+sourcePixelFormat+" -> "+destPixelFormat);
			pixelFormat = sourcePixelFormat;
		}
		
		
		IntRectangle sourceMaxBounds = irect(0, 0, source.getWidth(), source.getHeight());
		IntRectangle destMaxBounds = irect(0, 0, dest.getWidth(), dest.getHeight());
		
		IntRectangle pixelsInSource = irect(sourceX, sourceY, subregionWidthInDest / zoomFactor, subregionHeightInDest / zoomFactor);
		IntRectangle.intersect(sourceMaxBounds, pixelsInSource, pixelsInSource);
		
		IntRectangle pixelsInDest = irect(destX, destY, tileX ? subregionWidthInDest : pixelsInSource.width * zoomFactor, tileY ? subregionHeightInDest : pixelsInSource.height * zoomFactor);   //NOTE the difference here for tiling!!
		IntRectangle.intersect(destMaxBounds, pixelsInDest, pixelsInDest);
		
		
		
		if (source.getUnderlyingBacking() instanceof int[] && dest.getUnderlyingBacking() instanceof int[] && pixelFormat.getBits() == 32)
		{
			int[] src = (int[]) source.getUnderlyingBacking();
			int[] dst = (int[]) dest.getUnderlyingBacking();
			int srcOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
			int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int sS = source.getWidth();
			int dS = dest.getWidth();
			
			boolean sourceHasAlpha = source.getPixelFormat().hasAlpha();
			boolean destHasAlpha = dest.getPixelFormat().hasAlpha();
			
			if (sourceHasAlpha == destHasAlpha)
			{
				for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
				{
					int dY = destY + destYr;
					int sY = sourceY + ((destYr / zoomFactor) % pixelsInSource.height);
					
					for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
					{
						int dX = destX + destXr;
						int sX = sourceX + ((destXr / zoomFactor) % pixelsInSource.width);
						
						dst[dstOffset + (dY * dS + dX)] = src[srcOffset + (sY * sS + sX)];
					}
				}
			}
			else
			{
				for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
				{
					int sourceNonalphaMaskIfNecessary = destHasAlpha ? 0xFFFF_FFFF : ~source.getPixelFormat().getAlphaMask();  //if source has alpha (and dest doesn't!), we have to remove the alpha of each pixel, setting it to zero!!
					int destOpaquePressIfNecessary = destHasAlpha ? dest.getPixelFormat().getAlphaMask() : 0;  //if dest has alpha (but source doesn't and is uniformly perfectly opaque!), then we have to set dest's pixels to their maximum alpha value for each pixel!!
					
					int dY = destY + destYr;
					int sY = sourceY + ((destYr / zoomFactor) % pixelsInSource.height);
					
					for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
					{
						int dX = destX + destXr;
						int sX = sourceX + ((destXr / zoomFactor) % pixelsInSource.width);
						
						int sourcePixelValue = src[srcOffset + (sY * sS + sX)];
						sourcePixelValue &= sourceNonalphaMaskIfNecessary;
						dst[dstOffset + (dY * dS + dX)] = sourcePixelValue | destOpaquePressIfNecessary;
					}
				}
			}
		}
		else
		{
			//Todo ^^"
			throw new NotYetImplementedException();
		}
	}
	
	
	
	
	
	
	/**
	 * All-or-nothing "alpha compositing" XD''
	 */
	public static void copyPixelDataBitmapAlphaCompositing(@ReadonlyValue AccessibleInterleavedRasterImage source, int sourceX, int sourceY, @WritableValue AccessibleInterleavedRasterImage dest, int destX, int destY, int subregionWidthInDest, int subregionHeightInDest, boolean tileX, boolean tileY, int zoomFactor)
	{
		if (sourceX < 0) throw new IllegalArgumentException();
		if (sourceY < 0) throw new IllegalArgumentException();
		if (destX < 0) throw new IllegalArgumentException();
		if (destY < 0) throw new IllegalArgumentException();
		if (subregionWidthInDest < 0) throw new IllegalArgumentException();
		if (subregionHeightInDest < 0) throw new IllegalArgumentException();
		if (zoomFactor < 1) throw new IllegalArgumentException("Illegal zoom factor!!: "+zoomFactor);
		
		
		if (subregionWidthInDest == 0 || subregionHeightInDest == 0)
			//Zero pixels to process! XD'
			return;
		
		
		
		boolean sourceHasAlpha, destHasAlpha;
		SimpleImageColorStorageType pixelFormat;
		{
			SimpleImageColorStorageType sourcePixelFormat = source.getPixelFormat();
			SimpleImageColorStorageType destPixelFormat = dest.getPixelFormat();
			if (!sourcePixelFormat.equalsSaveForAlphaPresence(destPixelFormat))
				throw new IllegalArgumentException("Pixel formats don't match!!: "+sourcePixelFormat+" -> "+destPixelFormat);
			pixelFormat = sourcePixelFormat;
			sourceHasAlpha = sourcePixelFormat.hasAlpha();
			destHasAlpha = sourcePixelFormat.hasAlpha();
		}
		
		if (!sourceHasAlpha)
		{
			//Faster! :D
			copyPixelDataPossiblyTiling(source, sourceX, sourceY, dest, destX, destY, subregionWidthInDest, subregionHeightInDest, tileX, tileY, zoomFactor);
			return;
		}
		
		
		
		
		
		IntRectangle sourceMaxBounds = irect(0, 0, source.getWidth(), source.getHeight());
		IntRectangle destMaxBounds = irect(0, 0, dest.getWidth(), dest.getHeight());
		
		IntRectangle pixelsInSource = irect(sourceX, sourceY, subregionWidthInDest / zoomFactor, subregionHeightInDest / zoomFactor);
		IntRectangle.intersect(sourceMaxBounds, pixelsInSource, pixelsInSource);
		
		IntRectangle pixelsInDest = irect(destX, destY, tileX ? subregionWidthInDest : pixelsInSource.width * zoomFactor, tileY ? subregionHeightInDest : pixelsInSource.height * zoomFactor);   //NOTE the difference here for tiling!!
		IntRectangle.intersect(destMaxBounds, pixelsInDest, pixelsInDest);
		
		
		
		if (source.getUnderlyingBacking() instanceof int[] && dest.getUnderlyingBacking() instanceof int[] && pixelFormat.getBits() == 32)
		{
			int[] src = (int[]) source.getUnderlyingBacking();
			int[] dst = (int[]) dest.getUnderlyingBacking();
			int srcOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
			int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int sS = source.getWidth();
			int dS = dest.getWidth();
			
			int sourceAlphaMask = source.getPixelFormat().getAlphaMask();
			
			if (!destHasAlpha)
			{
				int sourceNonalphaMask = ~sourceAlphaMask;
				
				for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
				{
					int dY = destY + destYr;
					int sY = sourceY + ((destYr / zoomFactor) % pixelsInSource.height);
					
					for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
					{
						int dX = destX + destXr;
						int sX = sourceX + ((destXr / zoomFactor) % pixelsInSource.width);
						
						int sourcePixelValue = src[srcOffset + (sY * sS + sX)];
						
						boolean sourceIsFullyTransparent = (sourcePixelValue & sourceAlphaMask) == 0;
						
						if (!sourceIsFullyTransparent)
							dst[dstOffset + (dY * dS + dX)] = sourcePixelValue & sourceNonalphaMask;  //this is the difference! we haves to mask out the alpha when going from an alpha-having format to a non-alpha format!
					}
				}
			}
			else
			{
				for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
				{
					int dY = destY + destYr;
					int sY = sourceY + ((destYr / zoomFactor) % pixelsInSource.height);
					
					for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
					{
						int dX = destX + destXr;
						int sX = sourceX + ((destXr / zoomFactor) % pixelsInSource.width);
						
						int sourcePixelValue = src[srcOffset + (sY * sS + sX)];
						
						boolean sourceIsFullyTransparent = (sourcePixelValue & sourceAlphaMask) == 0;
						
						if (!sourceIsFullyTransparent)
							dst[dstOffset + (dY * dS + dX)] = sourcePixelValue;  //see above :33
					}
				}
			}
		}
		else
		{
			//Todo ^^"
			throw new NotYetImplementedException();
		}
	}
	
	
	
	
	
	
	
	public static void copyPixelDataAlphaCompositing(@ReadonlyValue AccessibleInterleavedRasterImage source, int sourceX, int sourceY, @WritableValue AccessibleInterleavedRasterImage dest, int destX, int destY, int subregionWidthInDest, int subregionHeightInDest, boolean tileX, boolean tileY, int zoomFactor, double alphaMultiplicationFactor)
	{
		if (!Double.isFinite(alphaMultiplicationFactor)) throw new IllegalArgumentException();
		if (alphaMultiplicationFactor < 0) throw new IllegalArgumentException();
		
		
		if (sourceX < 0) throw new IllegalArgumentException();
		if (sourceY < 0) throw new IllegalArgumentException();
		if (destX < 0) throw new IllegalArgumentException();
		if (destY < 0) throw new IllegalArgumentException();
		if (subregionWidthInDest < 0) throw new IllegalArgumentException();
		if (subregionHeightInDest < 0) throw new IllegalArgumentException();
		if (zoomFactor < 1) throw new IllegalArgumentException("Illegal zoom factor!!: "+zoomFactor);
		
		
		if (subregionWidthInDest == 0 || subregionHeightInDest == 0)
			//Zero pixels to process! XD'
			return;
		
		
		
		SimpleImageColorStorageType sourcePixelFormat = source.getPixelFormat();
		SimpleImageColorStorageType destPixelFormat = dest.getPixelFormat();
		boolean sourceHasAlpha = sourcePixelFormat.hasAlpha();
		boolean destHasAlpha = destPixelFormat.hasAlpha();
		
		int redBitcount, greenBitcount, blueBitcount, alphaBitcount;
		{
			redBitcount = sourcePixelFormat.getRedBitCount();
			greenBitcount = sourcePixelFormat.getGreenBitCount();
			blueBitcount = sourcePixelFormat.getBlueBitCount();
			
			if (destPixelFormat.getRedBitCount() != redBitcount)
				throw new NotYetImplementedException("Red bitdepths don't match!!");
			if (destPixelFormat.getGreenBitCount() != greenBitcount)
				throw new NotYetImplementedException("Green bitdepths don't match!!");
			if (destPixelFormat.getBlueBitCount() != blueBitcount)
				throw new NotYetImplementedException("Blue bitdepths don't match!!");
			
			if (sourceHasAlpha)
			{
				alphaBitcount = sourcePixelFormat.getAlphaBitCount();
				
				if (destHasAlpha)
				{
					if (destPixelFormat.getAlphaBitCount() != alphaBitcount)
						throw new NotYetImplementedException("Alpha bitdepths don't match!!");
				}
			}
			else
			{
				if (alphaMultiplicationFactor == 1)
				{
					//Faster! :D
					copyPixelDataPossiblyTiling(source, sourceX, sourceY, dest, destX, destY, subregionWidthInDest, subregionHeightInDest, tileX, tileY, zoomFactor);
					return;
				}
				else
				{
					if (destHasAlpha)
					{
						alphaBitcount = destPixelFormat.getAlphaBitCount();
					}
					else
					{
						alphaBitcount = 8;  //TODO another one once supported XD'''''
					}
				}
			}
		}
		
		//int redMaxvalue = (1 << redBitcount) - 1;
		//int greenMaxvalue = (1 << greenBitcount) - 1;
		//int blueMaxvalue = (1 << blueBitcount) - 1;
		int alphaMaxvalue = (1 << alphaBitcount) - 1;
		
		
		
		int sourceRedMask = sourcePixelFormat.getRedMask();
		int sourceGreenMask = sourcePixelFormat.getGreenMask();
		int sourceBlueMask = sourcePixelFormat.getBlueMask();
		int sourceAlphaMask = sourcePixelFormat.getAlphaMask();
		
		int sourceRedShift = sourcePixelFormat.getRedShift();
		int sourceGreenShift = sourcePixelFormat.getGreenShift();
		int sourceBlueShift = sourcePixelFormat.getBlueShift();
		int sourceAlphaShift = sourcePixelFormat.getAlphaShift();
		
		
		int destRedMask = destPixelFormat.getRedMask();
		int destGreenMask = destPixelFormat.getGreenMask();
		int destBlueMask = destPixelFormat.getBlueMask();
		int destAlphaMask = destPixelFormat.getAlphaMask();
		
		int destRedShift = destPixelFormat.getRedShift();
		int destGreenShift = destPixelFormat.getGreenShift();
		int destBlueShift = destPixelFormat.getBlueShift();
		int destAlphaShift = destPixelFormat.getAlphaShift();
		
		
		
		
		
		
		IntRectangle sourceMaxBounds = irect(0, 0, source.getWidth(), source.getHeight());
		IntRectangle destMaxBounds = irect(0, 0, dest.getWidth(), dest.getHeight());
		
		IntRectangle pixelsInSource = irect(sourceX, sourceY, subregionWidthInDest / zoomFactor, subregionHeightInDest / zoomFactor);
		IntRectangle.intersect(sourceMaxBounds, pixelsInSource, pixelsInSource);
		
		IntRectangle pixelsInDest = irect(destX, destY, tileX ? subregionWidthInDest : pixelsInSource.width * zoomFactor, tileY ? subregionHeightInDest : pixelsInSource.height * zoomFactor);   //NOTE the difference here for tiling!!
		IntRectangle.intersect(destMaxBounds, pixelsInDest, pixelsInDest);
		
		
		
		if (source.getUnderlyingBacking() instanceof int[] && dest.getUnderlyingBacking() instanceof int[] && sourcePixelFormat.getBits() == 32 && destPixelFormat.getBits() == 32 && redBitcount == 8 && greenBitcount == 8 && blueBitcount == 8 && alphaBitcount == 8)
		{
			int[] sourceData = (int[]) source.getUnderlyingBacking();
			int[] destData = (int[]) dest.getUnderlyingBacking();
			int sourceOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
			int destOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int sourceStride = source.getWidth();
			int destStride = dest.getWidth();
			
			
			
			if (destHasAlpha)
			{
				for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
				{
					int dY = destY + destYr;
					int sY = sourceY + ((destYr / zoomFactor) % pixelsInSource.height);
					
					for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
					{
						int dX = destX + destXr;
						int sX = sourceX + ((destXr / zoomFactor) % pixelsInSource.width);
						
						int sourcePixelValue = sourceData[sourceOffset + (sY * sourceStride + sX)];
						int destOriginalPixelValue = destData[destOffset + (dY * destStride + dX)];
						
						int sourceR = (sourcePixelValue & sourceRedMask) >>> sourceRedShift;
				int sourceG = (sourcePixelValue & sourceGreenMask) >>> sourceGreenShift;
		int sourceB = (sourcePixelValue & sourceBlueMask) >>> sourceBlueShift;
		int sourceA = sourceHasAlpha ? (sourcePixelValue & sourceAlphaMask) >>> sourceAlphaShift : alphaMaxvalue;
		
		if (alphaMultiplicationFactor != 1)
			sourceA = SmallIntegerMathUtilities.least(alphaMaxvalue, (int) (sourceA * alphaMultiplicationFactor));
		
		int destOriginalR = (destOriginalPixelValue & destRedMask) >>> destRedShift;
				int destOriginalG = (destOriginalPixelValue & destGreenMask) >>> destGreenShift;
		int destOriginalB = (destOriginalPixelValue & destBlueMask) >>> destBlueShift;
		int destOriginalA = (destOriginalPixelValue & destAlphaMask) >>> destAlphaShift;
		
		int destNewR = AlphaCompositing.alphaCompositeColorComponent8bit(destOriginalR, destOriginalA, sourceR, sourceA);
		int destNewG = AlphaCompositing.alphaCompositeColorComponent8bit(destOriginalG, destOriginalA, sourceG, sourceA);
		int destNewB = AlphaCompositing.alphaCompositeColorComponent8bit(destOriginalB, destOriginalA, sourceB, sourceA);
		int destNewA = AlphaCompositing.alphaCompositeAlphaComponent8bit(destOriginalA, sourceA);
		
		int destNewPixelValue = 0;
		destNewPixelValue |= destNewR << destRedShift;
		destNewPixelValue |= destNewG << destGreenShift;
		destNewPixelValue |= destNewB << destBlueShift;
		destNewPixelValue |= destNewA << destAlphaShift;
		
		destData[destOffset + (dY * destStride + dX)] = destNewPixelValue;
					}
				}
			}
			else
			{
				for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
				{
					int dY = destY + destYr;
					int sY = sourceY + ((destYr / zoomFactor) % pixelsInSource.height);
					
					for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
					{
						int dX = destX + destXr;
						int sX = sourceX + ((destXr / zoomFactor) % pixelsInSource.width);
						
						int sourcePixelValue = sourceData[sourceOffset + (sY * sourceStride + sX)];
						int destOriginalPixelValue = destData[destOffset + (dY * destStride + dX)];
						
						int sourceR = (sourcePixelValue & sourceRedMask) >>> sourceRedShift;
				int sourceG = (sourcePixelValue & sourceGreenMask) >>> sourceGreenShift;
		int sourceB = (sourcePixelValue & sourceBlueMask) >>> sourceBlueShift;
		int sourceA = sourceHasAlpha ? (sourcePixelValue & sourceAlphaMask) >>> sourceAlphaShift : alphaMaxvalue;
		
		if (alphaMultiplicationFactor != 1)
			sourceA = SmallIntegerMathUtilities.least(alphaMaxvalue, (int) (sourceA * alphaMultiplicationFactor));
		
		int destOriginalR = (destOriginalPixelValue & destRedMask) >>> destRedShift;
		int destOriginalG = (destOriginalPixelValue & destGreenMask) >>> destGreenShift;
		int destOriginalB = (destOriginalPixelValue & destBlueMask) >>> destBlueShift;
		int destOriginalA = alphaMaxvalue;
		
		int destNewR = AlphaCompositing.alphaCompositeColorComponent8bit(destOriginalR, destOriginalA, sourceR, sourceA);
		int destNewG = AlphaCompositing.alphaCompositeColorComponent8bit(destOriginalG, destOriginalA, sourceG, sourceA);
		int destNewB = AlphaCompositing.alphaCompositeColorComponent8bit(destOriginalB, destOriginalA, sourceB, sourceA);
		
		int destNewPixelValue = 0;
		destNewPixelValue |= destNewR << destRedShift;
		destNewPixelValue |= destNewG << destGreenShift;
		destNewPixelValue |= destNewB << destBlueShift;
		
		destData[destOffset + (dY * destStride + dX)] = destNewPixelValue;
					}
				}
			}
			
		}
		
		
		
		else
		{
			//Todo other backings and bitdepths ^^"
			throw new NotYetImplementedException();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static void pixellyIntegerScaleUp(@ReadonlyValue AccessibleInterleavedRasterImage source, int sourceX, int sourceY, @WritableValue AccessibleInterleavedRasterImage dest, int destX, int destY, int subregionWidthInSource, int subregionHeightInSource, int scaleFactor)
	{
		SimpleImageColorStorageType pixelFormat;
		{
			SimpleImageColorStorageType sourcePixelFormat = source.getPixelFormat();
			SimpleImageColorStorageType destPixelFormat = dest.getPixelFormat();
			if (!sourcePixelFormat.equals(destPixelFormat))
				throw new IllegalArgumentException("Pixel formats don't match!!: "+sourcePixelFormat+" -> "+destPixelFormat);
			pixelFormat = sourcePixelFormat;
		}
		
		
		
		IntRectangle sourceMaxBounds = irect(0, 0, source.getWidth(), source.getHeight());
		IntRectangle destMaxBounds = irect(0, 0, dest.getWidth(), dest.getHeight());
		
		IntRectangle pixelsInSource = irect(sourceX, sourceY, subregionWidthInSource, subregionHeightInSource);
		IntRectangle.intersect(sourceMaxBounds, pixelsInSource, pixelsInSource);
		
		IntRectangle pixelsInDest = irect(destX, destY, pixelsInSource.width * scaleFactor, pixelsInSource.height * scaleFactor);
		IntRectangle.intersect(destMaxBounds, pixelsInDest, pixelsInDest);
		
		
		
		if (source.getUnderlyingBacking() instanceof int[] && dest.getUnderlyingBacking() instanceof int[] && pixelFormat.getBits() == 32)
		{
			int[] src = (int[]) source.getUnderlyingBacking();
			int[] dst = (int[]) dest.getUnderlyingBacking();
			int srcOffset = ((AccessibleInterleavedRasterImageArrayInt)source).getUnderlyingBackingOffsetInElements();
			int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int sS = source.getWidth();
			int dS = dest.getWidth();
			
			
			for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
			{
				int dY = destY + destYr;
				int sY = sourceY + destYr / scaleFactor;
				
				for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
				{
					int dX = destX + destXr;
					int sX = sourceX + destXr / scaleFactor;
					
					dst[dstOffset + (dY * dS + dX)] = src[srcOffset + (sY * sS + sX)];
				}
			}
		}
		else
		{
			//Todo ^^"
			throw new NotYetImplementedException();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	//TODO OOOOOOOOOOO   SUPPORT ALPHA FILLING!!!
	
	public static void fill(SimpleRGBAColor color, @WritableValue AccessibleInterleavedRasterImage dest, int x, int y, int w, int h)
	{
		fill(color.getPackedValue(), color.getFormat(), dest, x, y, w, h);
	}
	
	
	public static void fill(int fillColorValue, SimpleImageColorStorageType fillColorFormat, @WritableValue AccessibleInterleavedRasterImage dest, int x, int y, int w, int h)
	{
		if (fillColorFormat.hasAlpha())
			throw new NotYetImplementedException();
		
		
		int fillColorValueInDestFormat = SimpleImageColorStorageType.convertPacked32(fillColorValue, fillColorFormat, dest.getPixelFormat());
		
		
		
		IntRectangle destMaxBounds = irect(0, 0, dest.getWidth(), dest.getHeight());
		
		IntRectangle pixelsInDest = irect(x, y, w, h);
		IntRectangle.intersect(destMaxBounds, pixelsInDest, pixelsInDest);
		
		
		
		if (dest.getUnderlyingBacking() instanceof int[] && dest.getPixelFormat().getBits() == 32)
		{
			int[] dst = (int[]) dest.getUnderlyingBacking();
			int dstOffset = ((AccessibleInterleavedRasterImageArrayInt)dest).getUnderlyingBackingOffsetInElements();
			
			int dS = dest.getWidth();
			
			
			for (int destYr = 0; destYr < pixelsInDest.height; destYr++)
			{
				int dY = y + destYr;
				
				for (int destXr = 0; destXr < pixelsInDest.width; destXr++)
				{
					int dX = x + destXr;
					
					dst[dstOffset + (dY * dS + dX)] = fillColorValueInDestFormat;
				}
			}
		}
		else
		{
			//Todo ^^"
			throw new NotYetImplementedException();
		}
	}
	
	
	
	//public static void fillAlphaCompositing(int fillColorValue, SimpleImageColorStorageType fillColorFormat, @WritableValue AccessibleInterleavedRasterImage dest, int x, int y, int w, int h)
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	@Nonnull
	public static AccessibleInterleavedRasterImageArrayInt getAsIntArray(@Nonnull AccessibleInterleavedRasterImage source)
	{
		if (AccessibleInterleavedRasterImageArrayInt.is(source))
		{
			return (AccessibleInterleavedRasterImageArrayInt) source;
		}
		
		else if (AccessibleInterleavedRasterImageArrayByte.is(source))
		{
			byte[] src = ((AccessibleInterleavedRasterImageArrayByte) source).getUnderlyingBackingArrayByte();
			int[] dst = ArrayUtilities.mergeElements8to32LE(src);
			
			return new SimpleAccessibleInterleavedRasterImageArrayInt(source.getWidth(), source.getHeight(), source.getPixelFormat(), dst);
		}
		
		else if (AccessibleInterleavedRasterImageArrayShort.is(source))
		{
			short[] src = ((AccessibleInterleavedRasterImageArrayShort) source).getUnderlyingBackingArrayShort();
			int[] dst = ArrayUtilities.mergeElements16to32LE(src);
			
			return new SimpleAccessibleInterleavedRasterImageArrayInt(source.getWidth(), source.getHeight(), source.getPixelFormat(), dst);
		}
		
		else if (AccessibleInterleavedRasterImageByteBuffer.is(source))
		{
			ByteBuffer src = ((AccessibleInterleavedRasterImageByteBuffer) source).getUnderlyingBackingByteBuffer();
			int[] dst = ArrayUtilities.mergeElementsBytesToIntsLE(src);
			
			return new SimpleAccessibleInterleavedRasterImageArrayInt(source.getWidth(), source.getHeight(), source.getPixelFormat(), dst);
		}
		
		else
		{
			throw newClassCastExceptionOrNullPointerException(source);
		}
	}
	
	
	
	
	
	
	
	
	
	public static void setAllAlphaValuesInPlace(@WritableValue AccessibleInterleavedRasterImage image, double newAlphaNormalized)
	{
		SimpleImageColorStorageType pixelFormat = image.getPixelFormat();
		
		if (!pixelFormat.hasAlpha())
			throw new IllegalArgumentException("Can't set alpha on an image without an alpha channel!!! XDDD'");
		
		
		if (AccessibleInterleavedRasterImageArrayInt.is(image))
		{
			int w = image.getWidth();
			int h = image.getHeight();
			
			int[] imageData = ((AccessibleInterleavedRasterImageArrayInt)image).getUnderlyingBackingArrayInt();
			int imageDataOffset = ((AccessibleInterleavedRasterImageArrayInt)image).getUnderlyingBackingOffsetInElements();
			
			int alphaMask = image.getPixelFormat().getAlphaMask();
			int colorMask = ~alphaMask;
			
			int alphaShift = image.getPixelFormat().getAlphaShift();
			
			
			int maxAlpha = ((1 << image.getPixelFormat().getAlphaBitCount()) - 1);
			int newAlphaInAppropriateBitdepth = (int)Math.round(newAlphaNormalized * maxAlpha);
			if (newAlphaInAppropriateBitdepth > maxAlpha) newAlphaInAppropriateBitdepth = maxAlpha;
			if (newAlphaInAppropriateBitdepth < 0) newAlphaInAppropriateBitdepth = 0;
			
			int newAlphaPreShifted = newAlphaInAppropriateBitdepth << alphaShift;
			
			if ((newAlphaPreShifted & alphaMask) != newAlphaPreShifted)
				throw new AssertionError();
			
			
			
			for (int y = 0; y < h; y++)
			{
				for (int x = 0; x < w; x++)
				{
					int i = y * w + x;
					
					int originalPixelValue = imageData[imageDataOffset + i];
					
					int colorBits = originalPixelValue & colorMask;
					int newPixelValue = colorBits | newAlphaPreShifted;
					
					imageData[imageDataOffset + i] = newPixelValue;
				}
			}
		}
		else
		{
			throw new NotYetImplementedException();
		}
	}
	
	
	
	
	
	
	
	
	
	public static void multiplyAllAlphaValuesInPlace(@WritableValue AccessibleInterleavedRasterImage image, double alphaFactor)
	{
		SimpleImageColorStorageType pixelFormat = image.getPixelFormat();
		
		if (!pixelFormat.hasAlpha())
			throw new IllegalArgumentException("Can't set alpha on an image without an alpha channel!!! XDDD'");
		
		
		if (AccessibleInterleavedRasterImageArrayInt.is(image))
		{
			int w = image.getWidth();
			int h = image.getHeight();
			
			int[] imageData = ((AccessibleInterleavedRasterImageArrayInt)image).getUnderlyingBackingArrayInt();
			int imageDataOffset = ((AccessibleInterleavedRasterImageArrayInt)image).getUnderlyingBackingOffsetInElements();
			
			int alphaMask = image.getPixelFormat().getAlphaMask();
			int colorMask = ~alphaMask;
			
			int maxAlpha = ((1 << image.getPixelFormat().getAlphaBitCount()) - 1);
			int alphaShift = image.getPixelFormat().getAlphaShift();
			
			
			for (int y = 0; y < h; y++)
			{
				for (int x = 0; x < w; x++)
				{
					int i = y * w + x;
					
					int originalPixelValue = imageData[imageDataOffset + i];
					
					int colorBits = originalPixelValue & colorMask;
					
					int originalAlphaInt = (originalPixelValue & alphaMask) >> alphaShift;
				int newAlphaInAppropriateBitdepth = (int) Math.round(originalAlphaInt * alphaFactor);
				
				if (newAlphaInAppropriateBitdepth > maxAlpha) newAlphaInAppropriateBitdepth = maxAlpha;
				if (newAlphaInAppropriateBitdepth < 0) newAlphaInAppropriateBitdepth = 0;
				
				int newAlphaPreShifted = newAlphaInAppropriateBitdepth << alphaShift;
				
				if ((newAlphaPreShifted & alphaMask) != newAlphaPreShifted)
					throw new AssertionError();
				
				int newPixelValue = colorBits | newAlphaPreShifted;
				
				imageData[imageDataOffset + i] = newPixelValue;
				}
			}
		}
		else
		{
			throw new NotYetImplementedException();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	public static AccessibleInterleavedRasterImage losslessRandRTransformOutOfPlace(@ReadonlyValue AccessibleInterleavedRasterImage srcImage, LosslessRandRAffineTransform transform)
	{
		int w = srcImage.getWidth();
		int h = srcImage.getHeight();
		
		if (AccessibleInterleavedRasterImageArrayInt.is(srcImage))
		{
			AccessibleInterleavedRasterImageArrayInt srcImageAcc = (AccessibleInterleavedRasterImageArrayInt) srcImage;
			int[] srcBacking = srcImageAcc.getUnderlyingBackingArrayInt();
			int srcBackingOffset = srcImageAcc.getUnderlyingBackingOffsetInElements();
			
			AccessibleInterleavedRasterImageArrayInt destImageAcc = createNewBackedByIntArray(w, h, srcImage.getPixelFormat());
			int[] destBacking = destImageAcc.getUnderlyingBackingArrayInt();
			int destBackingOffset = destImageAcc.getUnderlyingBackingOffsetInElements();
			
			for (int srcY = 0; srcY < h; srcY++)
			{
				for (int srcX = 0; srcX < w; srcX++)
				{
					int destX, destY;
					{
						//Thank HEAVENS for inlining and compiler optimizationsssss!! XDDD :'DDD
						IntPoint newP = transform.transformIntegerPointInsideOriginCorneredRectangleOP(ipoint(srcX, srcY), w, h);
						destX = newP.x;
						destY = newP.y;
					}
					
					int srcI = srcY * w + srcX;
					int dstI = destY * w + destX;
					
					//destImage.setPixelValue(destX, destY, srcImage.getPixelValue(srcX, srcY));
					destBacking[dstI + destBackingOffset] = srcBacking[srcI + srcBackingOffset];
				}
			}
			
			return destImageAcc;
		}
		
		else
		{
			AccessibleInterleavedRasterImage destImage = createNewCompatible(srcImage);
			
			for (int srcY = 0; srcY < h; srcY++)
			{
				for (int srcX = 0; srcX < w; srcX++)
				{
					int destX, destY;
					{
						//Thank HEAVENS for inlining and compiler optimizationsssss!! XDDD :'DDD
						IntPoint newP = transform.transformIntegerPointInsideOriginCorneredRectangleOP(ipoint(srcX, srcY), w, h);
						destX = newP.x;
						destY = newP.y;
					}
					
					destImage.setPixelValue(destX, destY, srcImage.getPixelValue(srcX, srcY));
				}
			}
			
			return destImage;
		}
	}
}
