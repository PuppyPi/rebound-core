package rebound.hci.graphics2d.ourrasters;

import static java.util.Objects.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import java.lang.reflect.Array;
import javax.annotation.Nonnull;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray;

public abstract class AbstractAccessibleInterleavedRasterImageArray
extends AbstractAccessibleInterleavedRasterImage
implements AccessibleInterleavedRasterImageArray
{
	protected int offsetInElements;
	protected int lengthInElements;
	
	protected void validateLongWay(int width, int height, SimpleImageColorStorageType pixelFormat, Object backing, int offsetInElements, int lengthInElements)
	{
		int backingCapacity = Array.getLength(backing);
		int elementsPerPixel = getElementsPerPixel();
		
		AbstractAccessibleInterleavedRasterImage.validateLongWay(width, height, pixelFormat, offsetInElements, lengthInElements, backingCapacity, elementsPerPixel);
	}
	
	
	
	@Override
	public int getUnderlyingBackingOffsetInElements()
	{
		return this.offsetInElements;
	}
	
	@Override
	public int getUnderlyingBackingLengthInElements()
	{
		return this.lengthInElements;
	}
	
	
	
	
	
	
	
	
	
	
	/* <<<
python

p(primxp.primxp(prims=primxp.someprims(["byte", "short", "int"]), source = """
	public static class SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_
	extends AbstractAccessibleInterleavedRasterImageArray
	implements AccessibleInterleavedRasterImageArray_$$Prim$$_
	{
		protected _$$prim$$_[] backing;
		
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull _$$prim$$_[] backing, int offsetInElements, int lengthInElements)
		{
			validateDims(width, height);
			
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
				
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull _$$prim$$_[] backing, int offsetInElements)
		{
			validateDims(width, height);
			
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull _$$prim$$_[] backing)
		{
			this(width, height, pixelFormat, backing, 0);
		}
		
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat)
		{
			validateDims(width, height);
			
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.backing = new _$$prim$$_[lengthInElements];
			
			this.width = width;
			this.height = height;
			this.offsetInElements = 0;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		
		
		
		@Override
		public _$$prim$$_[] getUnderlyingBackingArray_$$Prim$$_()
		{
			return backing;
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_ clone()
		{
			_$$prim$$_[] clonedBackingSubregion;
			{
				if (this.backing.length == getUnderlyingBackingLengthInElements())
				{
					clonedBackingSubregion = this.backing.clone();
				}
				else
				{
					//No need to receate a larger-than-necessary array!! XDD
					clonedBackingSubregion = new _$$prim$$_[getUnderlyingBackingLengthInElements()];
					System.arraycopy(this.backing, this.offsetInElements, clonedBackingSubregion, 0, clonedBackingSubregion.length);
				}
			}
			
			return new SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(width, height, pixelFormat, clonedBackingSubregion);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_ newCompatibleInstance()
		{
			_$$prim$$_[] newCompatibleBacking = new _$$prim$$_[getUnderlyingBackingLengthInElements()];
			return new SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(width, height, pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_ newCompatibleInstance(int newWidth, int newHeight)
		{
			_$$prim$$_[] newCompatibleBacking = new _$$prim$$_[ceilingDivision(newWidth * newHeight * this.pixelFormat.getBits(), _$$primlen$$_)];
			return new SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(newWidth, newHeight, this.pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_ newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat)
		{
			_$$prim$$_[] newCompatibleBacking = new _$$prim$$_[ceilingDivision(newWidth * newHeight * newPixelFormat.getBits(), _$$primlen$$_)];
			return new SimpleAccessibleInterleavedRasterImageArray_$$Prim$$_(newWidth, newHeight, newPixelFormat, newCompatibleBacking);
		}
	}
	
	
	
	
	
	"""))
	 */
	
	public static class SimpleAccessibleInterleavedRasterImageArrayByte
	extends AbstractAccessibleInterleavedRasterImageArray
	implements AccessibleInterleavedRasterImageArrayByte
	{
		protected byte[] backing;
		
		public SimpleAccessibleInterleavedRasterImageArrayByte(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull byte[] backing, int offsetInElements, int lengthInElements)
		{
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
			
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayByte(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull byte[] backing, int offsetInElements)
		{
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayByte(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull byte[] backing)
		{
			this(width, height, pixelFormat, backing, 0);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayByte(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat)
		{
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.backing = new byte[lengthInElements];
			
			this.width = width;
			this.height = height;
			this.offsetInElements = 0;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, this.backing, this.offsetInElements, lengthInElements);
		}
		
		
		
		
		@Override
		public byte[] getUnderlyingBackingArrayByte()
		{
			return this.backing;
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayByte clone()
		{
			byte[] clonedBackingSubregion;
			{
				if (this.backing.length == getUnderlyingBackingLengthInElements())
				{
					clonedBackingSubregion = this.backing.clone();
				}
				else
				{
					//No need to receate a larger-than-necessary array!! XDD
					clonedBackingSubregion = new byte[getUnderlyingBackingLengthInElements()];
					System.arraycopy(this.backing, this.offsetInElements, clonedBackingSubregion, 0, clonedBackingSubregion.length);
				}
			}
			
			return new SimpleAccessibleInterleavedRasterImageArrayByte(this.width, this.height, this.pixelFormat, clonedBackingSubregion);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayByte newCompatibleInstance()
		{
			byte[] newCompatibleBacking = new byte[getUnderlyingBackingLengthInElements()];
			return new SimpleAccessibleInterleavedRasterImageArrayByte(this.width, this.height, this.pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayByte newCompatibleInstance(int newWidth, int newHeight)
		{
			byte[] newCompatibleBacking = new byte[ceilingDivision(newWidth * newHeight * this.pixelFormat.getBits(), 8)];
			return new SimpleAccessibleInterleavedRasterImageArrayByte(newWidth, newHeight, this.pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayByte newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat)
		{
			byte[] newCompatibleBacking = new byte[ceilingDivision(newWidth * newHeight * newPixelFormat.getBits(), 8)];
			return new SimpleAccessibleInterleavedRasterImageArrayByte(newWidth, newHeight, newPixelFormat, newCompatibleBacking);
		}
	}
	
	
	
	
	
	
	public static class SimpleAccessibleInterleavedRasterImageArrayShort
	extends AbstractAccessibleInterleavedRasterImageArray
	implements AccessibleInterleavedRasterImageArrayShort
	{
		protected short[] backing;
		
		public SimpleAccessibleInterleavedRasterImageArrayShort(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull short[] backing, int offsetInElements, int lengthInElements)
		{
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
			
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayShort(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull short[] backing, int offsetInElements)
		{
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayShort(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull short[] backing)
		{
			this(width, height, pixelFormat, backing, 0);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayShort(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat)
		{
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.backing = new short[lengthInElements];
			
			this.width = width;
			this.height = height;
			this.offsetInElements = 0;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, this.backing, this.offsetInElements, lengthInElements);
		}
		
		
		
		
		@Override
		public short[] getUnderlyingBackingArrayShort()
		{
			return this.backing;
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayShort clone()
		{
			short[] clonedBackingSubregion;
			{
				if (this.backing.length == getUnderlyingBackingLengthInElements())
				{
					clonedBackingSubregion = this.backing.clone();
				}
				else
				{
					//No need to receate a larger-than-necessary array!! XDD
					clonedBackingSubregion = new short[getUnderlyingBackingLengthInElements()];
					System.arraycopy(this.backing, this.offsetInElements, clonedBackingSubregion, 0, clonedBackingSubregion.length);
				}
			}
			
			return new SimpleAccessibleInterleavedRasterImageArrayShort(this.width, this.height, this.pixelFormat, clonedBackingSubregion);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayShort newCompatibleInstance()
		{
			short[] newCompatibleBacking = new short[getUnderlyingBackingLengthInElements()];
			return new SimpleAccessibleInterleavedRasterImageArrayShort(this.width, this.height, this.pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayShort newCompatibleInstance(int newWidth, int newHeight)
		{
			short[] newCompatibleBacking = new short[ceilingDivision(newWidth * newHeight * this.pixelFormat.getBits(), 16)];
			return new SimpleAccessibleInterleavedRasterImageArrayShort(newWidth, newHeight, this.pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayShort newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat)
		{
			short[] newCompatibleBacking = new short[ceilingDivision(newWidth * newHeight * newPixelFormat.getBits(), 16)];
			return new SimpleAccessibleInterleavedRasterImageArrayShort(newWidth, newHeight, newPixelFormat, newCompatibleBacking);
		}
	}
	
	
	
	
	
	
	public static class SimpleAccessibleInterleavedRasterImageArrayInt
	extends AbstractAccessibleInterleavedRasterImageArray
	implements AccessibleInterleavedRasterImageArrayInt
	{
		protected int[] backing;
		
		public SimpleAccessibleInterleavedRasterImageArrayInt(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull int[] backing, int offsetInElements, int lengthInElements)
		{
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
			
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayInt(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull int[] backing, int offsetInElements)
		{
			this.backing = requireNonNull(backing);
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.width = width;
			this.height = height;
			this.offsetInElements = offsetInElements;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, backing, offsetInElements, lengthInElements);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayInt(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat, @Nonnull int[] backing)
		{
			this(width, height, pixelFormat, backing, 0);
		}
		
		public SimpleAccessibleInterleavedRasterImageArrayInt(int width, int height, @Nonnull SimpleImageColorStorageType pixelFormat)
		{
			this.pixelFormat = requireNonNull(pixelFormat);
			
			int elementsOrBytesPerPixel = getElementsPerPixel();  //NOTE!: requires pixelFormat to be set!!!
			int lengthInElements = width * height * elementsOrBytesPerPixel;
			
			this.backing = new int[lengthInElements];
			
			this.width = width;
			this.height = height;
			this.offsetInElements = 0;
			this.lengthInElements = lengthInElements;
			
			validateLongWay(width, height, pixelFormat, this.backing, this.offsetInElements, lengthInElements);
		}
		
		
		
		
		@Override
		public int[] getUnderlyingBackingArrayInt()
		{
			return this.backing;
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayInt clone()
		{
			int[] clonedBackingSubregion;
			{
				if (this.backing.length == getUnderlyingBackingLengthInElements())
				{
					clonedBackingSubregion = this.backing.clone();
				}
				else
				{
					//No need to receate a larger-than-necessary array!! XDD
					clonedBackingSubregion = new int[getUnderlyingBackingLengthInElements()];
					System.arraycopy(this.backing, this.offsetInElements, clonedBackingSubregion, 0, clonedBackingSubregion.length);
				}
			}
			
			return new SimpleAccessibleInterleavedRasterImageArrayInt(this.width, this.height, this.pixelFormat, clonedBackingSubregion);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayInt newCompatibleInstance()
		{
			int[] newCompatibleBacking = new int[getUnderlyingBackingLengthInElements()];
			return new SimpleAccessibleInterleavedRasterImageArrayInt(this.width, this.height, this.pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayInt newCompatibleInstance(int newWidth, int newHeight)
		{
			int[] newCompatibleBacking = new int[ceilingDivision(newWidth * newHeight * this.pixelFormat.getBits(), 32)];
			return new SimpleAccessibleInterleavedRasterImageArrayInt(newWidth, newHeight, this.pixelFormat, newCompatibleBacking);
		}
		
		@Override
		public SimpleAccessibleInterleavedRasterImageArrayInt newCompatibleInstance(int newWidth, int newHeight, SimpleImageColorStorageType newPixelFormat)
		{
			int[] newCompatibleBacking = new int[ceilingDivision(newWidth * newHeight * newPixelFormat.getBits(), 32)];
			return new SimpleAccessibleInterleavedRasterImageArrayInt(newWidth, newHeight, newPixelFormat, newCompatibleBacking);
		}
	}
	
	
	
	
	
	
	//>>>
}
