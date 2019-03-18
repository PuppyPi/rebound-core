package rebound.hci.graphics2d.ourrasters;

import javax.annotation.concurrent.Immutable;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;

/**
 * Note that the bitlength of the format doesn't factor into equals()/hashCode() here!
 * (because it's packed in an int anyways..so storage bitlength doesn't matter in this context XD'')
 * 
 * @author Puppy Pie ^w^
 */
@Immutable
@HashableType
public class SimpleRGBAColor
implements StaticallyConcurrentlyImmutable
{
	public static final SimpleRGBAColor OneKindOfTransparent = new SimpleRGBAColor(0, new SimpleImageColorStorageType(1, 0, 0, 0, 1));
	
	
	
	
	protected final int packedValue;
	protected final SimpleImageColorStorageType format;
	
	
	public SimpleRGBAColor(int packedValue, SimpleImageColorStorageType colorFormat)
	{
		this.format = colorFormat;
		this.packedValue = packedValue;
	}
	
	
	public SimpleRGBAColor(int r8bit, int g8bit, int b8bit)
	{
		this.format = SimpleImageColorStorageType.TYPE_BGR32;
		this.packedValue = this.format.packRGB(r8bit, g8bit, b8bit);
	}
	
	public SimpleRGBAColor(int r8bit, int g8bit, int b8bit, int a8bit)
	{
		this.format = SimpleImageColorStorageType.TYPE_ABGR32;
		this.packedValue = this.format.packRGB(r8bit, g8bit, b8bit, a8bit);
	}
	
	/**
	 * + Sets alpha to max-value (fully opaque) if the format has alpha :33
	 */
	public SimpleRGBAColor(int r, int g, int b, SimpleImageColorStorageType format)
	{
		this.format = format;
		this.packedValue = format.packRGB(r, g, b);
	}
	
	public SimpleRGBAColor(int r, int g, int b, int a, SimpleImageColorStorageType format)
	{
		this.format = format;
		this.packedValue = format.packRGB(r, g, b, a);
	}
	
	
	
	
	
	
	public int getPackedValue()
	{
		return this.packedValue;
	}
	
	public SimpleImageColorStorageType getFormat()
	{
		return this.format;
	}
	
	
	
	public int getPackedValueInAnotherFormat(SimpleImageColorStorageType otherFormat)
	{
		return SimpleImageColorStorageType.convertPacked32(getPackedValue(), getFormat(), otherFormat);
	}
	
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.packedValue;
		result = prime * result + ((this.format == null) ? 0 : this.format.hashCodeDisregardingBitlength());   //Note the 'DisregardingBitlength'!!
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
		SimpleRGBAColor other = (SimpleRGBAColor) obj;
		if (this.packedValue != other.packedValue)
			return false;
		if (this.format == null)
		{
			if (other.format != null)
				return false;
		}
		else if (!this.format.equalsDisregardingBitlength(other.format))   //Note the 'DisregardingBitlength'!!
			return false;
		return true;
	}
	
	
	
	
	
	
	
	public boolean hasAlpha()
	{
		return this.format.hasAlpha();
	}
	
	
	/**
	 * @return 0 if {@link #hasAlpha()} == false, NOT 255!!   (or 127 o 15 or 1 or ... hence why it's not 255 XD' )
	 */
	public int getAlpha()
	{
		return (this.format.getAlphaMask() & this.packedValue) >>> this.format.getAlphaShift();
	}
	
	public int getRed()
	{
		return (this.format.getRedMask() & this.packedValue) >>> this.format.getRedShift();
	}
	
	public int getGreen()
	{
		return (this.format.getGreenMask() & this.packedValue) >>> this.format.getGreenShift();
	}
	
	public int getBlue()
	{
		return (this.format.getBlueMask() & this.packedValue) >>> this.format.getBlueShift();
	}
	
	
	
	
	
	public double getAlphaNormalized()
	{
		return hasAlpha() ? (double)getAlpha() / (double)this.format.getAlphaMaxValue() : 1;
	}
	
	public double getRedNormalized()
	{
		return (double)getRed() / (double)this.format.getRedMaxValue();
	}
	
	public double getGreenNormalized()
	{
		return (double)getGreen() / (double)this.format.getGreenMaxValue();
	}
	
	public double getBlueNormalized()
	{
		return (double)getBlue() / (double)this.format.getBlueMaxValue();
	}
}
