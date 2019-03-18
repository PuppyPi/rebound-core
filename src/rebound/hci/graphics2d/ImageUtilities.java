/*
 * Created on Jul 23, 2008
 * 	by the great Eclipse(c)
 */
package rebound.hci.graphics2d;

import static java.util.Objects.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.color.ColorSpace;
import java.awt.geom.AffineTransform;
import java.awt.geom.NoninvertibleTransformException;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.ColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferInt;
import java.awt.image.DataBufferShort;
import java.awt.image.DataBufferUShort;
import java.awt.image.DirectColorModel;
import java.awt.image.Raster;
import java.awt.image.SampleModel;
import java.awt.image.SinglePixelPackedSampleModel;
import java.awt.image.WritableRaster;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Array;
import java.net.URL;
import java.util.Collection;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import javax.annotation.Nonnull;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import rebound.bits.Endianness;
import rebound.bits.Unsigned;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.UnsupportedFormatException;
import rebound.exceptions.UnsupportedOptionException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.file.FSUtilities;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.collections.ArrayUtilities;
import rebound.util.objectutil.JavaNamespace;

public class ImageUtilities
implements JavaNamespace
{
	//<Async
	
	/*
	//Deprecated because WHAT IF THE CODE THAT MAKES IT LEARN ITS SIZE IS ON THE SAME THREAD--THEN THE WHOLE APPLICATION WOULD FREEZE X"D''''
	@Deprecated
	protected static int getDimensionNow(Image image, final boolean widthOrHeight)
	{
		//Try it once before the extra stuff ^^'
		{
			int instantValue = widthOrHeight ? image.getWidth(null) : image.getHeight(null);
			
			if (instantValue != -1)
				return instantValue;
		}
		
		
		
		class container
		{
			int value;
		}
		
		final container container = new container();
		
		
		ImageObserver observer = new ImageObserver()
		{
			//Todo debug this code
			public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height)
			{
				int flag = widthOrHeight ? ImageObserver.WIDTH : ImageObserver.HEIGHT;
				
				synchronized (container)
				{
					if ((infoflags & flag) != 0)
					{
						container.value = widthOrHeight ? width : height;
						container.notify();
						return false;
					}
					else
					{
						return true;
					}
				}
			}
		};
		
		synchronized (container)
		{
			int instantValue = widthOrHeight ? image.getWidth(observer) : image.getHeight(observer);
			
			if (instantValue != -1)
			{
				return instantValue;
			}
			else
			{
				try
				{
					container.wait();
				}
				catch (InterruptedException exc)
				{
				}
			}
		}
		
		synchronized (container)  //happens-befores are quite important for nonvolatile fields!
		{
			return container.value;
		}
	}
	
	
	
	/**
	 * Gets the width of an {@link Image} blocking if necessary.
	 * @return The width of the image, never -1 (under normal parameters)
	 * /
	@Deprecated
	protected static int getWidthBlocking(Image image)
	{
		return getDimensionNow(image, true);
	}
	
	/**
	 * Gets the height of an {@link Image} blocking if necessary.
	 * @return The width of the image, never -1 (under normal parameters)
	 * /
	@Deprecated
	protected static int getHeightBlocking(Image image)
	{
		return getDimensionNow(image, false);
	}
	 */
	
	
	
	
	public static int getWidthFailing(Image image) throws UnsupportedOptionException
	{
		int v = image.getWidth(null);
		if (v == -1)
			throw new UnsupportedOptionException("We need the image to be able to provide its width instantly!");
		else if (v < 0)
			throw new ImpossibleException("Image width illegally was: "+v);
		else
			return v;
	}
	
	public static int getHeightFailing(Image image) throws UnsupportedOptionException
	{
		int v = image.getHeight(null);
		if (v == -1)
			throw new UnsupportedOptionException("We need the image to be able to provide its height instantly!");
		else if (v < 0)
			throw new ImpossibleException("Image height illegally was: "+v);
		else
			return v;
	}
	//Async>
	
	
	
	
	
	
	
	//<Image object creation
	public static BufferedImage createCompatibleImage(BufferedImage original, int width, int height, Map<String, Object> properties)
	{
		Hashtable<String, Object> htproperties = null;
		{
			if (properties == null)
				htproperties = null;
			else if (properties instanceof Hashtable)
				htproperties = (Hashtable<String, Object>)properties;
			else
				htproperties = new Hashtable<String, Object>(properties);
		}
		
		WritableRaster raster = null;
		{
			SampleModel newmodel = original.getRaster().getSampleModel().createCompatibleSampleModel(width, height);
			DataBuffer buffer = newmodel.createDataBuffer();
			raster = Raster.createWritableRaster(newmodel, buffer, new Point(0, 0));
		}
		
		return new BufferedImage
		(
		original.getColorModel(),
		raster,
		original.isAlphaPremultiplied(),
		htproperties
		);
	}
	
	public static BufferedImage createCompatibleImage(BufferedImage original, int width, int height)
	{
		return createCompatibleImage(original, width, height, getBufferedImageProperties(original));
	}
	
	public static BufferedImage createCompatibleImage(BufferedImage original)
	{
		return createCompatibleImage(original, original.getWidth(), original.getHeight());
	}
	
	
	
	
	public static Map<String, Object> getBufferedImageProperties(BufferedImage img, Map<String, Object> map)
	{
		if (img.getPropertyNames() != null)
		{
			for (String name : img.getPropertyNames())
				map.put(name, img.getProperty(name));
		}
		return map;
	}
	
	public static Map<String, Object> getBufferedImageProperties(BufferedImage img)
	{
		return getBufferedImageProperties(img, new Hashtable<String, Object>());  //hashtable because BufferedImage.<init> likes it that way >,>
	}
	
	
	
	
	public static BufferedImage createNewTransparentBufferedImage(int width, int height)
	{
		return new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
	}
	
	public static BufferedImage createNewTransparentOnePixelBufferedImage()
	{
		return createNewTransparentBufferedImage(1, 1);
	}
	
	
	
	
	protected static final int[] BITMASKS_565 = {0xF800, 0x7E0, 0x1F};
	protected static final int[] BITMASKS_555 = {0x7C00, 0x3E0, 0x1F};
	
	public static BufferedImage wrapRawImageData_5X5_bytes(int width, int height, byte[] data, int offset, int length, boolean is565, Endianness endianness)
	{
		short[] shortedData = endianness == Endianness.Big ? ArrayUtilities.mergeElements8to16BE(data, offset, length) : ArrayUtilities.mergeElements8to16LE(data, offset, length);
		return wrapRawImageData_5X5_shorts(width, height, shortedData, length / 2, is565);
	}
	
	
	
	
	public static BufferedImage wrapRawImageData_5X5_shorts(int width, int height, short[] data, int length, boolean is565)
	{
		int[] bitmasks = is565 ? BITMASKS_565 : BITMASKS_555;
		int bits = is565 ? 16 : 15;
		
		ColorModel cm = new DirectColorModel(bits, bitmasks[0], bitmasks[1], bitmasks[2]);
		SampleModel sm = new SinglePixelPackedSampleModel(DataBuffer.TYPE_USHORT, width, height, bitmasks);
		DataBuffer db = new DataBufferUShort(data, length);
		WritableRaster raster = Raster.createWritableRaster(sm, db, null);
		BufferedImage img = new BufferedImage(cm, raster, false, null);
		
		return img;
	}
	//Image object creation>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Image data extraction
	public static byte[] extractRawImageData_5X5_bytes(BufferedImage img, boolean is565, Endianness endianness)
	{
		short[] shorts = extractRawImageData_5X5_shorts(img, is565);
		byte[] bytes = endianness == Endianness.Big ? ArrayUtilities.splitElements16to8BE(shorts, 0, shorts.length) : ArrayUtilities.splitElements16to8LE(shorts, 0, shorts.length);
		return bytes;
	}
	
	
	public static short[] extractRawImageData_5X5_shorts(BufferedImage img, boolean is565)
	{
		//Automatic colorspace conversion
		BufferedImage rgb = null;
		{
			if (img.getColorModel().getColorSpace().getType() != ColorSpace.TYPE_RGB)
			{
				ColorConvertOp converter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
				BufferedImage dest = null, src = img;
				dest = new BufferedImage(src.getWidth(), src.getHeight(), is565 ? BufferedImage.TYPE_USHORT_565_RGB : BufferedImage.TYPE_USHORT_555_RGB);
				converter.filter(src, dest);
				rgb = dest;
			}
			else
			{
				rgb = img;
			}
		}
		
		
		
		//Copy to $data
		{
			WritableRaster raster = rgb.getRaster();
			SampleModel sm = raster.getSampleModel();
			DataBuffer db = raster.getDataBuffer();
			
			if (sm instanceof SinglePixelPackedSampleModel && (sm.getDataType() == DataBuffer.TYPE_USHORT || sm.getDataType() == DataBuffer.TYPE_SHORT) && db.getNumBanks() == 1)
			{
				short[] data = null;
				int offset = 0;
				
				if (db instanceof DataBufferUShort)
				{
					DataBufferUShort dbus = (DataBufferUShort)db;
					data = dbus.getData();
					offset = dbus.getOffset();
				}
				else if (db instanceof DataBufferShort)
				{
					DataBufferShort dbss = (DataBufferShort)db;
					data = dbss.getData();
					dbss.getOffset();
				}
				
				if (data != null)
				{
					if (offset == 0)
						return data;
					else
					{
						short[] translated = new short[db.getSize()];
						System.arraycopy(data, offset, translated, 0, translated.length);
						return translated;
					}
				}
			}
			
			//Obviously it has an incompatible storage model
			//Compact the expanded form produced by the raster
			{
				int[] expandedSamples = raster.getPixels(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), (int[])null);
				int len = expandedSamples.length;
				short[] data = new short[len];
				
				if (is565)
				{
					for (int i = 0; i < len; i++)
					{
						int expanded = expandedSamples[i];
						data[i] = (short)(((expanded & 0x1F0000) >>> 5) | ((expanded & 0x3F00) >>> 3) | (expanded & 0x1F));
					}
				}
				else
				{
					for (int i = 0; i < len; i++)
					{
						int expanded = expandedSamples[i];
						data[i] = (short)(((expanded & 0x1F0000) >>> 6) | ((expanded & 0x1F00) >>> 3) | (expanded & 0x1F));
					}
				}
				
				return data;
			}
		}
	}
	
	
	
	
	
	public static byte[] extractRawImageData_ARGB32_bytes(BufferedImage img, Endianness endianness)
	{
		//TODO USE IT DIRECTLY IF ALREADY IS A BYTE[] X""DDD   (and better ways in general!!!)
		int[] shorts = extractRawImageData_ARGB32_ints(img);
		byte[] bytes = endianness == Endianness.Big ? ArrayUtilities.splitElements32to8BE(shorts, 0, shorts.length) : ArrayUtilities.splitElements32to8LE(shorts, 0, shorts.length);
		return bytes;
	}
	
	
	//TODO THIS IS BROKEN, IT CONVERTS A BYTE[] RASTER INTO AN INT[] RASTER OF JUST 0-255 INTS FOR EACH *BYTE* INSTEAD OF EACH PIXEL!!! X"D
	public static int[] extractRawImageData_ARGB32_ints(BufferedImage img)
	{
		//Automatic colorspace conversion
		BufferedImage rgb = null;
		{
			if (img.getColorModel().getColorSpace().getType() != ColorSpace.TYPE_RGB)
			{
				ColorConvertOp converter = new ColorConvertOp(ColorSpace.getInstance(ColorSpace.CS_sRGB), null);
				BufferedImage dest = null, src = img;
				dest = new BufferedImage(src.getWidth(), src.getHeight(), BufferedImage.TYPE_INT_ARGB);
				converter.filter(src, dest);
				rgb = dest;
			}
			else
			{
				rgb = img;
			}
		}
		
		
		
		
		//Copy to $data
		{
			WritableRaster raster = rgb.getRaster();
			SampleModel sm = raster.getSampleModel();
			DataBuffer db = raster.getDataBuffer();
			
			if (sm instanceof SinglePixelPackedSampleModel && (sm.getDataType() == DataBuffer.TYPE_INT) && db.getNumBanks() == 1)
			{
				int[] data = null;
				int offset = 0;
				
				if (db instanceof DataBufferInt)
				{
					DataBufferInt dbus = (DataBufferInt)db;
					data = dbus.getData();
					offset = dbus.getOffset();
				}
				
				if (data != null)
				{
					if (offset == 0)
						return data;
					else
					{
						int[] translated = new int[db.getSize()];
						System.arraycopy(data, offset, translated, 0, translated.length);
						return translated;
					}
				}
			}
			
			//Obviously it has an incompatible storage model
			//Compact the expanded form produced by the raster
			{
				//EDIT: SEE ABOVE--THIS DOESN'T DO WHAT WE THOUGHT IT DID X'DD
				//return raster.getPixels(raster.getMinX(), raster.getMinY(), raster.getWidth(), raster.getHeight(), (int[])null);
				
				throw new NotYetImplementedException();
			}
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * Extracts alpha information in the form of unsigned bytes (0-255).<br>
	 * If this image doesn't support alpha, then <code>null</code> is returned, not an array full of 0xFF bytes.<br>
	 */
	public static byte[] getAlpha(BufferedImage image)
	{
		if (!image.getColorModel().hasAlpha())
			return null;
		
		int width = image.getWidth();
		int height = image.getHeight();
		Raster raster = image.getRaster();
		ColorModel cm = image.getColorModel();
		SampleModel sm = raster.getSampleModel();
		DataBuffer db = raster.getDataBuffer();
		
		byte[] alphaMap = new byte[width * height];
		
		Raster alphaRaster = image.getAlphaRaster();
		
		if (alphaRaster != null)
		{
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					alphaMap[y*width + x] = (byte)raster.getSample(x, y, 3); //3rd (last) sample is alpha
				}
			}
		}
		else
		{
			Object sampleArray = Array.newInstance(getComponentTypeForDataBufferType(raster.getSampleModel().getTransferType()), raster.getSampleModel().getNumDataElements());
			
			for (int y = 0; y < height; y++)
			{
				for (int x = 0; x < width; x++)
				{
					sm.getDataElements(x, y, sampleArray, db);
					alphaMap[y*width + x] = (byte)cm.getAlpha(sampleArray);
				}
			}
		}
		
		return alphaMap;
	}
	//Image data extraction>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Simple effects
	/**
	 * Note: this only applies to the single color given; no threshold (nearby colors) or fuzziness (partial alpha).
	 */
	public static BufferedImage colorToAlpha(BufferedImage image, int color)
	{
		color = color & 0x00FFFFFF; //trim alpha, if given
		
		BufferedImage dest = null;
		{
			if (image.getAlphaRaster() != null)
			{
				dest = image;
			}
			else
			{
				//Allocate a new buffered image
				dest = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
				Graphics2D g = dest.createGraphics();
				g.drawImage(image, 0, 0, null);
				g.dispose();
			}
		}
		
		//This is slow, but.. meh.
		
		int w = image.getWidth();
		int h = image.getHeight();
		for (int y = 0; y < h; y++)
		{
			for (int x = 0; x < w; x++)
			{
				int v = image.getRGB(x, y);
				if ((v & 0x00FFFFFF) == color) //see, no threshold at all
				{
					dest.setRGB(x, y, v & 0x00FFFFFF); //if we don't set alpha to FF (opaque), it will be 00 (transparent)
				}
			}
		}
		
		return dest;
	}
	//Simple effects>
	
	
	
	
	
	
	//<Simple utilities
	public static Class getComponentTypeForDataBufferType(int dataType)
	{
		switch (dataType)
		{
			case DataBuffer.TYPE_BYTE:
				return byte.class;
				
			case DataBuffer.TYPE_USHORT:
				return short.class;
				
			case DataBuffer.TYPE_SHORT:
				return short.class;
				
			case DataBuffer.TYPE_INT:
				return int.class;
				
			case DataBuffer.TYPE_FLOAT:
				return float.class;
				
			case DataBuffer.TYPE_DOUBLE:
				return double.class;
				
			default:
				throw new IllegalArgumentException("Invalid DataBuffer.TYPE_xxx value: "+dataType);
		}
	}
	
	
	public static void setAllPixels(int[] imageBuffer, int value)
	{
		int len = imageBuffer.length;
		for (int i = 0; i < len; i++)
			imageBuffer[i] = value;
	}
	
	
	public static int setAlpha(int originalColor, double alpha)
	{
		return setAlpha(originalColor, (int)(alpha*255));
	}
	
	public static int setAlpha(int originalColor, int alpha)
	{
		if (alpha < 0) alpha = 0;
		else if (alpha > 255) alpha = 255;
		
		return (originalColor & 0x00FFFFFF) | (alpha << 24);
	}
	
	
	
	/**
	 * @return the color (unsigned cast will be positive in long); -1 if not all same color; -2 if image is empty
	 */
	public static long isAllSameAlphaColor_INTARGB(int[] image, int width)
	{
		if ((image.length % width) != 0)
			throw new IllegalArgumentException("Image buffer size not multiple of width.");
		
		if (image.length == 0)
			return -2l;
		
		int firstColor = image[0];
		
		for (int i = 1; i < image.length; i++)
		{
			if (image[i] != firstColor)
				return -1l;
		}
		
		return Unsigned.upcast(firstColor);
	}
	
	
	/**
	 * Does not include alpha in calculations
	 * @return the color (unsigned cast will be positive in long); -1 if not all same color; -2 if image is empty
	 */
	public static long isAllSameNonalphaColor_INTARGB(int[] image, int width)
	{
		if ((image.length % width) != 0)
			throw new IllegalArgumentException("Image buffer size not multiple of width.");
		
		if (image.length == 0)
			return -2l;
		
		int firstColor = image[0] & 0x00FFFFFF;
		
		for (int i = 1; i < image.length; i++)
		{
			if ((image[i] & 0x00FFFFFF) != firstColor)
				return -1l;
		}
		
		return Unsigned.upcast(firstColor);
	}
	//Simple utilities>
	
	
	
	
	
	
	//<Basic image manipulation
	
	//<Geometrical (splicing, splitting, combining, tiling, ...)
	/**
	 * Combines the given raster tiling of images into one extant or newly allocated (if null) [A]RGB image.
	 * The images are ordered in raster order of x inside, increasing; y outside, increasing (and BufferedImages use left-handed top-left origin coordinate system).
	 * @return dest or the newly allocated image if dest was null
	 */
	public static BufferedImage combineImages(BufferedImage dest, int destX, int destY, int stride, BufferedImage... images)
	{
		//Validate
		{
			if (stride < 1)
				throw new IllegalArgumentException("Invalid stride (width in tiles): "+stride);
			
			if (images == null)
				throw new NullPointerException();
			
			if (images.length % stride != 0)
				throw new IllegalArgumentException("Invalid number of tiles, "+images.length+" is not a multiple of stride ("+stride+")");
			
			//Validate that images are proper width/height along columns/rows
			{
				//Rows
				{
					for (int y = 0; y < images.length/stride; y++)
					{
						int firstDim = -1;
						
						for (int x = 0; x < stride; x++)
						{
							if (images[y*stride+x] != null)
							{
								if (firstDim == -1)
								{
									firstDim = images[y*stride+x].getHeight();
									if (firstDim < 0) throw new ImpossibleException("Buffered image height < 0: "+firstDim);
								}
								else
								{
									if (images[y*stride+x].getHeight() != firstDim)
										throw new IllegalArgumentException("Not all image tiles in row y="+y+" are the same height.");
								}
							}
						}
					}
				}
				
				
				//Columns
				{
					for (int x = 0; x < stride; x++)
					{
						int firstDim = -1;
						
						for (int y = 0; y < images.length/stride; y++)
						{
							if (images[y*stride+x] != null)
							{
								if (firstDim == -1)
								{
									firstDim = images[y*stride+x].getWidth();
									if (firstDim < 0) throw new ImpossibleException("Buffered image width < 0: "+firstDim);
								}
								else
								{
									if (images[y*stride+x].getWidth() != firstDim)
										throw new IllegalArgumentException("Not all image tiles in column x="+x+" are the same width.");
								}
							}
						}
					}
				}
			}
		}
		
		
		
		//Prepare the new image
		{
			//Calculate width and height of tiling
			int width = 0, height = 0;
			{
				//Calculate height
				for (int y = 0; y < images.length/stride; y++)
				{
					for (int x = 0; x < stride; x++)
					{
						if (images[y*stride+x] != null) //all null run counts 0 pixels in size
						{
							height += images[y*stride+x].getHeight();
							break; //next row
						}
					}
				}
				
				//Calculate width
				for (int x = 0; x < stride; x++)
				{
					for (int y = 0; y < images.length/stride; y++)
					{
						if (images[y*stride+x] != null) //all null run counts 0 pixels in size
						{
							width += images[y*stride+x].getWidth();
							break; //next row
						}
					}
				}
			}
			
			
			if (dest == null)
			{
				//Create one of the proper size
				
				//Give it alpha if at least one of the sub images has it
				boolean hasAlpha = false;
				{
					for (int i = 0; i < images.length; i++)
					{
						if (images[i].getColorModel().hasAlpha())
						{
							hasAlpha = true;
							break;
						}
					}
				}
				
				dest = new BufferedImage(width+destX, height+destY, hasAlpha ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
			}
			else
			{
				if (dest.getWidth() < width+destX || dest.getHeight() < height+destY)
					throw new IllegalArgumentException("Dest image is too small for tiling size + offset");
			}
		}
		
		
		//Combine
		{
			Graphics g = dest.createGraphics();
			
			int x = 0, y = 0;
			
			for (int tileY = 0; tileY < images.length/stride; tileY++)
			{
				for (int tileX = 0; tileX < stride; tileX++)
				{
					int columnWidth = 0;
					{
						if (images[tileY*stride+tileX] != null)
							columnWidth = images[tileY*stride+tileX].getWidth();
						else
						{
							for (int tileY2 = 0; tileY2 < images.length/stride; tileY2++)
							{
								if (images[tileY2*stride+tileX] != null)
								{
									columnWidth = images[tileY2*stride+tileX].getWidth();
									break;
								}
							}
						}
					}
					
					if (images[tileY*stride+tileX] != null) //null is 'transparent' not black
					{
						g.drawImage(images[tileY*stride+tileX], x, y, null);
					}
					
					x += columnWidth;
				}
				
				
				int rowHeight = 0;
				{
					for (int tileX2 = 0; tileX2 < stride; tileX2++)
					{
						if (images[tileY*stride+tileX2] != null)
						{
							rowHeight = images[tileY*stride+tileX2].getHeight();
							break;
						}
					}
				}
				
				
				//Advance cursor
				x = 0;
				y += rowHeight;
			}
			
			g.dispose();
		}
		
		
		return dest;
	}
	
	
	public static BufferedImage combineImages(int stride, BufferedImage... images)
	{
		return combineImages(null, 0, 0, stride, images);
	}
	
	
	
	
	
	
	
	
	
	//	//edit: AffineTransform doesn't retranslate to center the origin, so you need to create a new AffineTransform for every transformation instance ><   oh wells XD
	//	protected static final AffineTransformOp[] RightAngleRotators =
	//	{
	//	 new AffineTransformOp(AffineTransform.getQuadrantRotateInstance(0), AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
	//	 new AffineTransformOp(AffineTransform.getQuadrantRotateInstance(1), AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
	//	 new AffineTransformOp(AffineTransform.getQuadrantRotateInstance(2), AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
	//	 new AffineTransformOp(AffineTransform.getQuadrantRotateInstance(3), AffineTransformOp.TYPE_NEAREST_NEIGHBOR),
	//	};
	//	protected static final AffineTransformOp HorizontalFlipper = new AffineTransformOp(AffineTransform.getScaleInstance(-1, 1), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	//	protected static final AffineTransformOp VerticalFlipper = new AffineTransformOp(AffineTransform.getScaleInstance(1, -1), AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
	
	
	/**
	 * @param quadrantRotations The number of counter-clockwise 90 degree rotations to perform ^_^
	 * @return source if quadrant rotations is a multiple of 4 (ie, 360 degree rotation!), and so would leave the image unmodified
	 */
	public static BufferedImage rotateRightAngles(BufferedImage source, int quadrantRotations)
	{
		quadrantRotations = SmallIntegerMathUtilities.progmod(quadrantRotations, 4);  //0 = 0, 1 = 1, 2 = 2, 3 = 3, 4 = 0, 5 = 1, 6 = 2, 7 = 3, 8 = 0, 9 = 1, 10 = 2, 11 = 3, 12 = 0, 13 = 1, ...    0 = 0, -1 = 3, -2 = 2, -3 = 1, -4 = 0, -5 = 3, -6 = 2, -7 = 1, ...  :>
		
		if (quadrantRotations == 0)
			return source;
		
		//boolean xyReversed = quadrantRotations % 2 != 0;
		//BufferedImage dest = !xyReversed ? createCompatibleImage(source, source.getWidth(), source.getHeight()) : createCompatibleImage(source, source.getHeight(), source.getWidth());
		
		AffineTransform xform = new AffineTransform();
		if (quadrantRotations == 1)
			xform.translate(source.getHeight(), 0);
		else if (quadrantRotations == 2)
			xform.translate(source.getWidth(), source.getHeight());
		else if (quadrantRotations == 3)
			xform.translate(0, source.getWidth());
		xform.quadrantRotate(SmallIntegerMathUtilities.progmod(quadrantRotations, 4));
		
		//		Point2D r = new Point2D.Double();
		//		xform.transform(new Point2D.Double(0, 0), r); System.out.println("DBG) "+r);
		//		xform.transform(new Point2D.Double(source.getWidth(), 0), r); System.out.println("DBG) "+r);
		//		xform.transform(new Point2D.Double(source.getWidth(), source.getHeight()), r); System.out.println("DBG) "+r);
		//		xform.transform(new Point2D.Double(0, source.getHeight()), r); System.out.println("DBG) "+r);
		
		
		return new AffineTransformOp(xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(source, null);
	}
	
	
	
	
	
	public static BufferedImage flipHorizontal(BufferedImage source)
	{
		//BufferedImage dest = createCompatibleImage(source);
		AffineTransform xform = new AffineTransform();
		xform.translate(source.getWidth(), 0);
		xform.scale(-1, 1);
		try
		{
			xform.invert();
		}
		catch (NoninvertibleTransformException exc)
		{
			throw new ImpossibleException();
		}
		return new AffineTransformOp(xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(source, null);
	}
	
	public static BufferedImage flipVertical(BufferedImage source)
	{
		//BufferedImage dest = createCompatibleImage(source);
		AffineTransform xform = new AffineTransform();
		xform.translate(0, source.getHeight());
		xform.scale(1, -1);
		try
		{
			xform.invert();
		}
		catch (NoninvertibleTransformException exc)
		{
			throw new ImpossibleException();
		}
		return new AffineTransformOp(xform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR).filter(source, null);
	}
	
	
	
	//Todo rotateRightAnglesInPlace, flipHorizontal/VerticalInPlace!
	
	
	//Geometrical>
	
	//Basic image manipulation>
	
	
	
	
	public static void debugDumpImageToFile(BufferedImage image, File file)
	{
		try
		{
			ImageIO.write(image, "png", file);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@Nonnull
	public static BufferedImage read(@Nonnull File file, @Nonnull ImageReader reader) throws FileNotFoundException, IOException
	{
		requireNonNull(file);
		
		try (InputStream in = new FileInputStream(file))
		{
			return read(in, reader);
		}
	}
	
	@Nonnull
	public static BufferedImage read(@Nonnull URL url, @Nonnull ImageReader reader) throws IOException
	{
		requireNonNull(url);
		
		try (InputStream in = url.openStream())
		{
			return read(in, reader);
		}
	}
	
	@Nonnull
	public static BufferedImage read(@Nonnull byte[] data, @Nonnull ImageReader reader) throws IOException
	{
		requireNonNull(data);
		
		ByteArrayInputStream in = new ByteArrayInputStream(data);
		return read(in, reader);
	}
	
	@Nonnull
	public static BufferedImage read(@Nonnull InputStream in, @Nonnull ImageReader reader) throws IOException
	{
		requireNonNull(in);
		requireNonNull(reader);
		
		ImageInputStream iout = ImageIO.createImageInputStream(in);  //for separate line numbers in error stack traces ^.~
		reader.setInput(iout, true);
		BufferedImage image = reader.read(0);
		
		requireNonNull(image);
		
		return image;
	}
	
	
	
	
	
	
	
	
	public static void write(@Nonnull BufferedImage image, @Nonnull OutputStream out, @Nonnull ImageWriter writer) throws IOException
	{
		requireNonNull(image);
		requireNonNull(out);
		requireNonNull(writer);
		
		ImageOutputStream iout = ImageIO.createImageOutputStream(out);  //for separate line numbers in error stack traces ^.~
		writer.setOutput(iout);
		writer.write(image);
		iout.flush();
	}
	
	public static void write(@Nonnull BufferedImage image, @Nonnull File file, @Nonnull ImageWriter writer) throws FileNotFoundException, IOException
	{
		requireNonNull(file);
		
		try (OutputStream out = new FileOutputStream(file))
		{
			write(image, out, writer);
		}
	}
	
	@Nonnull
	public static byte[] write(@Nonnull BufferedImage image, @Nonnull ImageWriter writer) throws IOException
	{
		ByteArrayOutputStream buff = new ByteArrayOutputStream();
		
		write(image, buff, writer);
		
		byte[] a = buff.toByteArray();
		requireNonNull(a);
		return a;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ImageReadingAndFormatDetectionResults
	{
		public BufferedImage image;
		public ImageReader formatReader;
		public ImageWriter formatWriter;
		
		public ImageReadingAndFormatDetectionResults(BufferedImage image, ImageReader formatReader, ImageWriter formatWriter)
		{
			this.image = image;
			this.formatReader = formatReader;
			this.formatWriter = formatWriter;
		}
	}
	
	
	public static ImageReadingAndFormatDetectionResults readImageDetectingFormat(File file, boolean requireCorrespondingWriter) throws FileNotFoundException, IOException
	{
		String ext = FSUtilities.getFilenameExtension(file);
		
		if (ext == null)
		{
			throw new UnsupportedFormatException("File has no extension to tell the image format from!! X'DD   "+repr(file));
		}
		
		Iterator<ImageReader> readersI = ImageIO.getImageReadersBySuffix(ext);
		Collection<ImageReader> readers = toCollection(readersI);
		
		if (readers.isEmpty())
		{
			throw new UnsupportedFormatException(ImageIO.class.getName()+" gives no ImageReaders for the extension "+repr(ext)+" so we cans not decode the image file..sorry!");
		}
		else
		{
			ImageReader reader = getArbitraryElementThrowing(readers);
			
			if (reader == null)
				throw new ImpossibleException();
			
			
			
			
			ImageWriter writer = ImageIO.getImageWriter(reader);
			
			if (writer == null)
			{
				Iterator<ImageWriter> writersI = ImageIO.getImageWritersBySuffix(ext);
				Collection<ImageWriter> writers = toCollection(writersI);
				
				if (writers.isEmpty())
				{
					if (requireCorrespondingWriter)
						throw new UnsupportedFormatException(ImageIO.class.getName()+" gives no ImageWriters for the extension "+repr(ext)+"  (and the reader it gave has no corresponding writer!! \\o/)");
					else
						writer = null;
				}
				else
				{
					writer = getArbitraryElementThrowing(writers);
					
					if (writer == null)
						throw new ImpossibleException();
				}
			}
			
			
			
			
			BufferedImage image = ImageUtilities.read(file, reader);
			
			if (image == null)
				throw new ImpossibleException();
			
			return new ImageReadingAndFormatDetectionResults(image, reader, writer);
		}
	}
	
	
	public static ImageReadingAndFormatDetectionResults readImageDetectingFormat(File file) throws FileNotFoundException, IOException
	{
		return readImageDetectingFormat(file, false);
	}
}
