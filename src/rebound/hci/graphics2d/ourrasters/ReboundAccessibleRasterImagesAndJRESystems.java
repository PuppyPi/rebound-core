package rebound.hci.graphics2d.ourrasters;

import static rebound.hci.graphics2d.ourrasters.AccessibleRasterImages.*;
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.PixelInterleavedSampleModel;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import javax.annotation.Nonnegative;
import javax.imageio.ImageIO;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.temporal.PossiblySnapshotPossiblyLiveValue;
import rebound.bits.BitUtilities;
import rebound.exceptions.NotYetImplementedException;
import rebound.hci.graphics2d.DirectImageUtilities;
import rebound.hci.graphics2d.DirectImageUtilities.TransparentBufferedImage;
import rebound.util.functional.FunctionalInterfaces.UnaryFunctionIntToInt;

public class ReboundAccessibleRasterImagesAndJRESystems
{
	@ThrowAwayValue  //the raw source byte array is *not* modified if the image is (ie, it's never used as a backing for the image!)
	public static AccessibleInterleavedRasterImage load(byte[] data, String filebasename) throws IOException
	{
		BufferedImage i = ImageIO.read(new ByteArrayInputStream(data));  //I guess it doesn't need the extension?? o,0
		return load(i);
	}
	
	@ThrowAwayValue  //the raw source file is *not* modified if the image is (ie, it's never used as a backing for the image!)
	public static AccessibleInterleavedRasterImage load(File file) throws IOException
	{
		BufferedImage i = ImageIO.read(file);
		
		if (i.getColorModel() instanceof IndexColorModel)
		{
			//Convert to a normal color model first!
			i = DirectImageUtilities.convertToComponent(i);
		}
		
		return load(i);
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public static AccessibleInterleavedRasterImage load(BufferedImage image)
	{
		return DirectImageUtilities.wrapOrCopyNontransparentToTransparentBufferedImage(image);
	}
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public static BufferedImage asBufferedImage(AccessibleInterleavedRasterImage image)
	{
		if (image instanceof TransparentBufferedImage)
			return ((TransparentBufferedImage)image).getBufferedImage();
		else
			return toNewBufferedImage(image);
	}
	
	@ThrowAwayValue
	public static BufferedImage toNewBufferedImage(AccessibleInterleavedRasterImage image)
	{
		//TransparentBufferedImage bridgeBetweenApis = DirectImageUtilities.createNewCompatibleTransparentBufferedImage(image);
		
		TransparentBufferedImage bridgeBetweenApis;
		{
			//Todo could single-pixel-packed be used with grayscale color models?!  o,0
			if (image.getPixelFormat().isGrayscale())
				bridgeBetweenApis = DirectImageUtilities.createNewTransparentBufferedImageBackedByByteArray(image.getWidth(), image.getHeight(), image.getPixelFormat());  //Java prefers int[]-backed BufferedImages to an extent iirc :3    (and it DEFINITELY prefers heap array-backed ones to our custom half-baked byte buffer ones!!! X'DDD )
			else
				bridgeBetweenApis = DirectImageUtilities.createNewTransparentBufferedImageBackedByIntArray(image.getWidth(), image.getHeight(), image.getPixelFormat().promote(32));  //Java prefers int[]-backed BufferedImages to an extent iirc :3    (and it DEFINITELY prefers heap array-backed ones to our custom half-baked byte buffer ones!!! X'DDD )
		}
		
		copyPixelData(image, bridgeBetweenApis);
		return bridgeBetweenApis.getBufferedImage();
	}
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static BufferedImage tintRGBAdditive(@ReadonlyValue BufferedImage original, double rAdden, double gAdden, double bAdden)
	{
		return asBufferedImage(ImageTinting.tintRGBAdditive(load(original), rAdden, gAdden, bAdden));
	}
	
	
	@ThrowAwayValue
	public static BufferedImage tintHSVAdditive(@ReadonlyValue BufferedImage original, double hAdden, double sAdden, double vAdden)
	{
		return asBufferedImage(ImageTinting.tintHSVAdditive(load(original), hAdden, sAdden, vAdden));
	}
	
	@ThrowAwayValue
	public static BufferedImage perPixelOp(@ReadonlyValue BufferedImage original, UnaryFunctionIntToInt pixelOperation, SimpleImageColorStorageType operatingPixelFormat)
	{
		return asBufferedImage(ImageTinting.perPixelOp(load(original), pixelOperation, operatingPixelFormat));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static SimpleImageColorStorageType rariPixelFormatFromJava2DDirectColorModel(DirectColorModel dcm)
	{
		return new SimpleImageColorStorageType(dcm.getPixelSize(), dcm.getRedMask(), dcm.getGreenMask(), dcm.getBlueMask(), dcm.getAlphaMask());
	}
	
	public static SimpleImageColorStorageType rariPixelFormatFromJava2DInterleavedComponentColorModel(ComponentColorModel ccm, PixelInterleavedSampleModel sm)
	{
		boolean alpha = ccm.hasAlpha();
		boolean gray = ccm.getColorSpace().getType() == ColorSpace.TYPE_GRAY;
		
		//TODO Check that the ColorSpace is RGB if !gray ^^''
		
		int expectedNumberOfComponents = (gray ? (alpha ? 2 : 1) : (alpha ? 4 : 3));
		
		if (ccm.getNumComponents() != expectedNumberOfComponents)
			throw new NotYetImplementedException(ccm.getNumComponents() + " components found!!  (since alphaPresent="+alpha+", we expected "+expectedNumberOfComponents+"!  "+(alpha ? "R,G,B, and A!)" : "R, G, and B!"));
		
		
		int[] offsets = sm.getBandOffsets();
		int[] sizes = ccm.getComponentSize();
		
		if (offsets.length != expectedNumberOfComponents)
			throw new NotYetImplementedException();
		if (sizes.length != expectedNumberOfComponents)
			throw new NotYetImplementedException();
		
		
		int elementBitlength;
		{
			int t = sm.getDataType();
			
			if (t == DataBuffer.TYPE_BYTE)
				elementBitlength = 8;
			else if (t == DataBuffer.TYPE_USHORT)
				elementBitlength = 16;
			else if (t == DataBuffer.TYPE_SHORT)
				elementBitlength = 16;
			else if (t == DataBuffer.TYPE_INT)
				elementBitlength = 32;
			else
				throw new NotYetImplementedException();
		}
		
		
		int redmask;
		int greenmask;
		int bluemask;
		int alphamask;
		{
			//TODO Are these orderings really hardcoded!?
			
			if (!gray)
			{
				redmask = BitUtilities.getMask32(sizes[0]) << (elementBitlength * offsets[0]);
				greenmask = BitUtilities.getMask32(sizes[1]) << (elementBitlength * offsets[1]);
				bluemask = BitUtilities.getMask32(sizes[2]) << (elementBitlength * offsets[2]);
				alphamask = alpha ? BitUtilities.getMask32(sizes[3]) << (elementBitlength * offsets[3]) : 0;
			}
			else
			{
				redmask = greenmask = bluemask = BitUtilities.getMask32(sizes[0]) << (elementBitlength * offsets[0]);
				alphamask = alpha ? BitUtilities.getMask32(sizes[1]) << (elementBitlength * offsets[1]) : 0;
			}
		}
		
		return new SimpleImageColorStorageType(ccm.getPixelSize(), redmask, greenmask, bluemask, alphamask);
	}
	
	
	public static Color RARItoJava2DColor(SimpleRGBAColor rariColor)
	{
		SimpleRGBAColor c = rariColor;
		SimpleImageColorStorageType format = c.getFormat();
		
		
		return c.hasAlpha() ?
		
		new Color
		(
		scale(c.getRed(), format.getRedBitCount(), 8),
		scale(c.getGreen(), format.getGreenBitCount(), 8),
		scale(c.getBlue(), format.getBlueBitCount(), 8),
		scale(c.getAlpha(), format.getAlphaBitCount(), 8)
		)
		
		:
			
			new Color
			(
			scale(c.getRed(), format.getRedBitCount(), 8),
			scale(c.getGreen(), format.getGreenBitCount(), 8),
			scale(c.getBlue(), format.getBlueBitCount(), 8)
			);
	}
	
	public static SimpleRGBAColor java2DtoRARIColor(Color java2dColor, boolean hasAlpha)
	{
		return new SimpleRGBAColor(hasAlpha ? java2dColor.getRGB() : (java2dColor.getRGB() & 0x00FFFFFF), hasAlpha ? SimpleImageColorStorageType.TYPE_ARGB32 : SimpleImageColorStorageType.TYPE_RGB32);
	}
	
	
	
	
	@Nonnegative
	private static int scale(@Nonnegative int inputValue, int inputBitdepth, int outputBitdepth)
	{
		if (inputBitdepth == outputBitdepth)
			return inputValue;
		else
		{
			int inputMaxValue = (1 << inputBitdepth) - 1;
			int outputMaxValue = (1 << outputBitdepth) - 1;
			
			return (inputValue * outputMaxValue) / inputMaxValue;
		}
	}
}
