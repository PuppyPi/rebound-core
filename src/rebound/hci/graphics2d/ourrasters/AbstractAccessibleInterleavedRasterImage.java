package rebound.hci.graphics2d.ourrasters;

public abstract class AbstractAccessibleInterleavedRasterImage
implements AccessibleInterleavedRasterImage
{
	protected int width, height;
	protected SimpleImageColorStorageType pixelFormat;
	
	
	protected static void validateLongWay(int width, int height, SimpleImageColorStorageType pixelFormat, int offsetInElementsOrBytes, int lengthInElementsOrBytes, int backingCapacity, int elementsOrBytesPerPixel)
	{
		if (offsetInElementsOrBytes < 0)
			throw new IndexOutOfBoundsException("Negative offset! x'D");
		
		if (offsetInElementsOrBytes + lengthInElementsOrBytes > backingCapacity)
			throw new IndexOutOfBoundsException("Extends past end of underlying array!!");
		
		
		
		if ((lengthInElementsOrBytes % elementsOrBytesPerPixel) != 0)
			throw new IllegalArgumentException("Image dimensions don't work!: Backing size isn't a multiple of pixel size!!!");
		
		int numberOfPixels = lengthInElementsOrBytes / elementsOrBytesPerPixel;
		
		
		if (width * height != numberOfPixels)
			throw new IllegalArgumentException("Image dimensions don't work!: width * height != backing length / length of pixel !!!");
	}
	
	protected static void validateDims(int width, int height)
	{
		if (width < 0) throw new IllegalArgumentException("Negative width given for image!!!: "+width);
		if (height < 0) throw new IllegalArgumentException("Negative height given for image!!!: "+height);
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
	
	
	@Override
	public abstract AccessibleInterleavedRasterImage clone();
}
