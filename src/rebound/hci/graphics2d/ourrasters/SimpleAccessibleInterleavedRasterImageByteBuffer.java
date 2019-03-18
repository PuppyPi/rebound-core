package rebound.hci.graphics2d.ourrasters;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageByteBuffer;
import rebound.util.BufferAllocationType;
import rebound.util.NIOBufferUtilities;
import rebound.util.PlatformNIOBufferUtilities;

public class SimpleAccessibleInterleavedRasterImageByteBuffer
extends AbstractAccessibleInterleavedRasterImage
implements AccessibleInterleavedRasterImageByteBuffer
{
	protected ByteBuffer backing;
	protected int offsetInBytes;
	protected int lengthInBytes;
	
	public SimpleAccessibleInterleavedRasterImageByteBuffer(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull ByteBuffer backing, int offsetInBytes, int lengthInBytes)
	{
		validateDims(width, height);
		
		this.backing = requireNonNull(backing);
		this.pixelFormat = requireNonNull(pixelFormat);
		
		this.width = width;
		this.height = height;
		this.offsetInBytes = offsetInBytes;
		this.lengthInBytes = lengthInBytes;
		
		validateLongWay(width, height, pixelFormat, backing, offsetInBytes, lengthInBytes);
	}
	
	public SimpleAccessibleInterleavedRasterImageByteBuffer(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull ByteBuffer backing, int offsetInBytes)
	{
		validateDims(width, height);
		
		this.backing = requireNonNull(backing);
		this.pixelFormat = requireNonNull(pixelFormat);
		
		int elementsOrBytesPerPixel = getBytesPerPixel();  //NOTE!: requires pixelFormat to be set!!!
		int lengthInBytes = width * height * elementsOrBytesPerPixel;
		
		this.width = width;
		this.height = height;
		this.offsetInBytes = offsetInBytes;
		this.lengthInBytes = lengthInBytes;
		
		validateLongWay(width, height, pixelFormat, backing, offsetInBytes, lengthInBytes);
	}
	
	public SimpleAccessibleInterleavedRasterImageByteBuffer(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull ByteBuffer backing)
	{
		this(width, height, pixelFormat, backing, 0);
	}
	
	public SimpleAccessibleInterleavedRasterImageByteBuffer(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull BufferAllocationType bufferAllocationType)
	{
		validateDims(width, height);
		
		this.pixelFormat = requireNonNull(pixelFormat);
		
		int elementsOrBytesPerPixel = getBytesPerPixel();  //NOTE!: requires pixelFormat to be set!!!
		int lengthInBytes = width * height * elementsOrBytesPerPixel;
		
		this.backing = PlatformNIOBufferUtilities.allocateByteBuffer(lengthInBytes, bufferAllocationType);
		
		this.width = width;
		this.height = height;
		this.offsetInBytes = 0;
		this.lengthInBytes = lengthInBytes;
		
		validateLongWay(width, height, pixelFormat, this.backing, this.offsetInBytes, lengthInBytes);
	}
	
	
	
	
	
	protected void validateLongWay(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull ByteBuffer backing, int offsetInBytes, int lengthInBytes)
	{
		int backingCapacity = backing.capacity();
		int bytesPerPixel = getBytesPerPixel();
		
		AbstractAccessibleInterleavedRasterImage.validateLongWay(width, height, pixelFormat, offsetInBytes, lengthInBytes, backingCapacity, bytesPerPixel);
	}
	
	
	
	@Override
	public int getUnderlyingBackingOffsetInBytes()
	{
		return this.offsetInBytes;
	}
	
	@Override
	public int getUnderlyingBackingLengthInBytes()
	{
		return this.lengthInBytes;
	}
	
	
	
	
	@Override
	public ByteBuffer getUnderlyingBackingByteBuffer()
	{
		return this.backing;
	}
	
	
	
	@Override
	public SimpleAccessibleInterleavedRasterImageByteBuffer clone()
	{
		ByteBuffer clonedBackingSubregion = NIOBufferUtilities.cloneBuffer(this.backing);
		return new SimpleAccessibleInterleavedRasterImageByteBuffer(this.width, this.height, this.pixelFormat, clonedBackingSubregion);
	}
	
	@Override
	public SimpleAccessibleInterleavedRasterImageByteBuffer newCompatibleInstance()
	{
		ByteBuffer newCompatibleBacking = PlatformNIOBufferUtilities.allocateCompatibleBuffer(this.backing);
		return new SimpleAccessibleInterleavedRasterImageByteBuffer(this.width, this.height, this.pixelFormat, newCompatibleBacking);
	}
	
	@Override
	public SimpleAccessibleInterleavedRasterImageByteBuffer newCompatibleInstance(int newWidth, int newHeight)
	{
		ByteBuffer newCompatibleBacking = PlatformNIOBufferUtilities.allocateByteBuffer(ceilingDivision(newWidth * newHeight * this.pixelFormat.getBits(), 8), PlatformNIOBufferUtilities.getBufferAllocationType(this.backing));
		return new SimpleAccessibleInterleavedRasterImageByteBuffer(newWidth, newHeight, this.pixelFormat, newCompatibleBacking);
	}
	
	@Override
	public SimpleAccessibleInterleavedRasterImageByteBuffer newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat)
	{
		ByteBuffer newCompatibleBacking = PlatformNIOBufferUtilities.allocateByteBuffer(ceilingDivision(newWidth * newHeight * newPixelFormat.getBits(), 8), PlatformNIOBufferUtilities.getBufferAllocationType(this.backing));
		return new SimpleAccessibleInterleavedRasterImageByteBuffer(newWidth, newHeight, newPixelFormat, newCompatibleBacking);
	}
}
