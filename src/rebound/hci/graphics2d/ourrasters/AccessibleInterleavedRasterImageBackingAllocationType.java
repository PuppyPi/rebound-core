package rebound.hci.graphics2d.ourrasters;

import static java.util.Objects.*;
import static rebound.util.BasicExceptionUtilities.*;
import java.nio.ByteBuffer;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayByte;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayInt;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayShort;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageByteBuffer;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType.ArrayByteAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType.ArrayIntAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType.ArrayShortAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.util.BufferAllocationType;
import rebound.util.PlatformNIOBufferUtilities;

@HashableType
public interface AccessibleInterleavedRasterImageBackingAllocationType
{
	public static boolean isCompatibleForPassThroughAndCopyIsNotNeeded(AccessibleInterleavedRasterImageBackingAllocationType sourceType, AccessibleInterleavedRasterImageBackingAllocationType requestedOutputType)
	{
		if (sourceType instanceof ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)
		{
			if (requestedOutputType instanceof ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)
				return BufferAllocationType.isCompatibleForPassThroughAndCopyIsNotNeeded(((ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)sourceType).getBufferAllocationType(), ((ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)requestedOutputType).getBufferAllocationType());
			else
				return false;  //no array and no bytebuffer types are compatible!
		}
		else
		{
			if (requestedOutputType instanceof ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)
				return false;  //no array and no bytebuffer types are compatible!
			else
				return sourceType == requestedOutputType;
		}
	}
	
	public static boolean isCompatibleForPassThroughAndCopyIsNotNeeded(AccessibleInterleavedRasterImage source, AccessibleInterleavedRasterImageBackingAllocationType requestedOutputType)
	{
		return isCompatibleForPassThroughAndCopyIsNotNeeded(getBackingTypeForImage(source), requestedOutputType);
	}
	
	
	
	
	
	
	public static interface ArrayAccessibleInterleavedRasterImageBackingAllocationType
	extends AccessibleInterleavedRasterImageBackingAllocationType
	{
		public static enum ArrayByteAccessibleInterleavedRasterImageBackingAllocationType implements ArrayAccessibleInterleavedRasterImageBackingAllocationType { I }
		public static enum ArrayShortAccessibleInterleavedRasterImageBackingAllocationType implements ArrayAccessibleInterleavedRasterImageBackingAllocationType { I }
		public static enum ArrayIntAccessibleInterleavedRasterImageBackingAllocationType implements ArrayAccessibleInterleavedRasterImageBackingAllocationType { I }
	}
	
	
	public static class ByteBufferAccessibleInterleavedRasterImageBackingAllocationType
	implements AccessibleInterleavedRasterImageBackingAllocationType
	{
		protected final @Nonnull BufferAllocationType bufferAllocationType;
		
		public ByteBufferAccessibleInterleavedRasterImageBackingAllocationType(@Nonnull BufferAllocationType bufferAllocationType)
		{
			this.bufferAllocationType = requireNonNull(bufferAllocationType);
		}
		
		public BufferAllocationType getBufferAllocationType()
		{
			return bufferAllocationType;
		}
		
		
		@Override
		public int hashCode()
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((bufferAllocationType == null) ? 0 : bufferAllocationType.hashCode());
			return result;
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			ByteBufferAccessibleInterleavedRasterImageBackingAllocationType other = (ByteBufferAccessibleInterleavedRasterImageBackingAllocationType) obj;
			if (bufferAllocationType != other.bufferAllocationType)
				return false;
			return true;
		}
		
		//		@Override
		//		public String toString()
		//		{
		//			return "ByteBufferAccessibleInterleavedRasterImageBackingType [bufferAllocationType=" + bufferAllocationType + "]";
		//		}
	}
	
	
	
	
	
	
	
	
	
	
	public static Class getBackingApiTypeForImage(AccessibleInterleavedRasterImage image)
	{
		if (AccessibleInterleavedRasterImageArrayByte.is(image))
			return byte[].class;
		else if (AccessibleInterleavedRasterImageArrayShort.is(image))
			return short[].class;
		else if (AccessibleInterleavedRasterImageArrayInt.is(image))
			return int[].class;
		else if (AccessibleInterleavedRasterImageByteBuffer.is(image))
			return ByteBuffer.class;
		else
			throw newClassCastExceptionOrNullPointerException(image);
	}
	
	
	
	
	
	public static Class getBackingApiClass(AccessibleInterleavedRasterImageBackingAllocationType backingAllocationType)
	{
		if (backingAllocationType == ArrayByteAccessibleInterleavedRasterImageBackingAllocationType.I)
			return byte[].class;
		else if (backingAllocationType == ArrayShortAccessibleInterleavedRasterImageBackingAllocationType.I)
			return short[].class;
		else if (backingAllocationType == ArrayIntAccessibleInterleavedRasterImageBackingAllocationType.I)
			return int[].class;
		else if (backingAllocationType instanceof ByteBufferAccessibleInterleavedRasterImageBackingAllocationType)
			return ByteBuffer.class;
		else
			throw newClassCastExceptionOrNullPointerException(backingAllocationType);
	}
	
	
	
	
	
	public static AccessibleInterleavedRasterImageBackingAllocationType getBackingTypeForImage(AccessibleInterleavedRasterImage image)
	{
		if (AccessibleInterleavedRasterImageArrayByte.is(image))
			return ArrayByteAccessibleInterleavedRasterImageBackingAllocationType.I;
		else if (AccessibleInterleavedRasterImageArrayShort.is(image))
			return ArrayByteAccessibleInterleavedRasterImageBackingAllocationType.I;
		else if (AccessibleInterleavedRasterImageArrayInt.is(image))
			return ArrayByteAccessibleInterleavedRasterImageBackingAllocationType.I;
		else if (AccessibleInterleavedRasterImageByteBuffer.is(image))
		{
			ByteBuffer backing = ((AccessibleInterleavedRasterImageByteBuffer)image).getUnderlyingBackingByteBuffer();
			
			BufferAllocationType backingBufferAllocationType = PlatformNIOBufferUtilities.getBufferAllocationType(backing);
			
			return new ByteBufferAccessibleInterleavedRasterImageBackingAllocationType(backingBufferAllocationType);
		}
		else
			throw newClassCastExceptionOrNullPointerException(image);
	}
}
