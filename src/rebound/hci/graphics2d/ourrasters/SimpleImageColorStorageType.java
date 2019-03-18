package rebound.hci.graphics2d.ourrasters;

import static rebound.bits.BitUtilities.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import javax.annotation.concurrent.Immutable;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.concurrency.immutability.StaticallyConcurrentlyImmutable;
import rebound.exceptions.NullEnumValueIllegalArgumentException;
import rebound.exceptions.UnexpectedHardcodedEnumValueException;
import rebound.hci.graphics2d.ourrasters.SimpleImageColorStorageType.SimpleImageColorStorageTypeElement.SimpleImageColorStorageTypeUnusedElement;
import rebound.hci.graphics2d.ourrasters.SimpleImageColorStorageType.SimpleImageColorStorageTypeElement.SimpleImageColorStorageTypeUsedElement;
import rebound.math.SmallIntegerMathUtilities;

@Immutable
@HashableType
public class SimpleImageColorStorageType
implements StaticallyConcurrentlyImmutable
{
	//Note: as per Western culture (given this is in English), the names are given in big-endian-bits, corresponding to the West's convention of writing the Arabic place value system in big endian with respect to writing direction  ;>
	public static final SimpleImageColorStorageType TYPE_ABGR32 = new SimpleImageColorStorageType(32, 0x000000FF, 0x0000FF00, 0x00FF0000, 0xFF000000);  //my new favorite :>   (think about little endian and the order of the EM spectrum / rainbow, and that 'alpha' is an extension  ;> )
	public static final SimpleImageColorStorageType TYPE_RGBA32 = new SimpleImageColorStorageType(32, 0xFF000000, 0x00FF0000, 0x0000FF00, 0x000000FF);  //apparently other people's favorite (SDL, OpenGL)
	public static final SimpleImageColorStorageType TYPE_BGRA32 = new SimpleImageColorStorageType(32, 0x0000FF00, 0x00FF0000, 0xFF000000, 0x000000FF);
	public static final SimpleImageColorStorageType TYPE_ARGB32 = new SimpleImageColorStorageType(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0xFF000000);  //my old favorite
	
	
	public static final SimpleImageColorStorageType TYPE_BGR32 = new SimpleImageColorStorageType(32, 0x000000FF, 0x0000FF00, 0x00FF0000, 0);
	public static final SimpleImageColorStorageType TYPE_BGR24 = new SimpleImageColorStorageType(24, 0x000000FF, 0x0000FF00, 0x00FF0000, 0);  //The ones 3-byte packed, no alpha uses :>
	
	public static final SimpleImageColorStorageType TYPE_RGB32 = new SimpleImageColorStorageType(32, 0x00FF0000, 0x0000FF00, 0x000000FF, 0);
	public static final SimpleImageColorStorageType TYPE_RGB24 = new SimpleImageColorStorageType(24, 0x00FF0000, 0x0000FF00, 0x000000FF, 0);  //The ones 3-byte packed, no alpha uses :>
	
	
	
	
	public static final SimpleImageColorStorageType TYPE_R5G6B5_16 = new SimpleImageColorStorageType(16, 0xF800, 0x07E0, 0x001F, 0);
	
	public static final SimpleImageColorStorageType TYPE_B5G6R5_16 = new SimpleImageColorStorageType(16, 0x001F, 0x07E0, 0xF800, 0);
	
	
	public static final SimpleImageColorStorageType TYPE_R5G5B5_16 = new SimpleImageColorStorageType(16, 0x7C00, 0x03E0, 0x001F, 0);
	public static final SimpleImageColorStorageType TYPE_R5G5B5_15 = new SimpleImageColorStorageType(15, 0x7C00, 0x03E0, 0x001F, 0);
	
	public static final SimpleImageColorStorageType TYPE_B5G5R5_16 = new SimpleImageColorStorageType(16, 0x001F, 0x03E0, 0x7C00, 0);
	public static final SimpleImageColorStorageType TYPE_B5G5R5_15 = new SimpleImageColorStorageType(15, 0x001F, 0x03E0, 0x7C00, 0);
	
	
	
	
	public static final SimpleImageColorStorageType TYPE_GRAYSCALE_32 = new SimpleImageColorStorageType(32, 0xFFFFFFFF, 0xFFFFFFFF, 0xFFFFFFFF, 0);
	public static final SimpleImageColorStorageType TYPE_GRAYSCALE_24 = new SimpleImageColorStorageType(24, 0xFFFFFF, 0xFFFFFF, 0xFFFFFF, 0);
	public static final SimpleImageColorStorageType TYPE_GRAYSCALE_16 = new SimpleImageColorStorageType(16, 0xFFFF, 0xFFFF, 0xFFFF, 0);
	public static final SimpleImageColorStorageType TYPE_GRAYSCALE_8 = new SimpleImageColorStorageType(8, 0xFF, 0xFF, 0xFF, 0);
	
	public static final SimpleImageColorStorageType TYPE_BLACK_AND_WHITE = new SimpleImageColorStorageType(1, 0x1, 0x1, 0x1, 0);
	
	//If you want alpha with your gray, you can concoct it yourself! :>
	
	
	/* ok, I cheated xD  :

	for a in [False, True]:
		for pos in ["after", "before"] if a == True else [None]:
			for rev in [False, True]:
				c = "BGR";
				if (a):
					if (pos == "before"):
						c = c + "A";
					else:
						c = "A" + c;
				if (rev):
					l = list(c);
					l.reverse();
					c = reduce(lambda a,b: a+b, l);
				print(c);
	 */
	
	
	
	
	protected final int bits;
	protected final int alphaMask, redMask, greenMask, blueMask;
	
	public SimpleImageColorStorageType(int bits, int redMask, int greenMask, int blueMask, int alphaMask)
	{
		super();
		
		this.bits = bits;
		this.redMask = redMask;
		this.greenMask = greenMask;
		this.blueMask = blueMask;
		this.alphaMask = alphaMask;
		
		if (bits < getSmallestNumberOfBitsRequiredIncludingUnusedLowBits()) throw new IllegalArgumentException("Invalid color storage format: too few bits for masks given: "+this);
		
		//Note: increasing bits might be useful eg to promote it to correspond to a 64 bit word, so that is allowed.
		//Note: overlapping color masks is useful for grayscale, so that is allowed.
		//Note: a null storage type (all zero masks) would need zero bits and might be useful  (/shrugs!)
	}
	
	
	/**
	 * Construct the storage format type with the fewest possible bits required.
	 * (ie, this.bits == {@link #getSmallestNumberOfBitsRequiredIncludingUnusedLowBits()})
	 */
	public SimpleImageColorStorageType(int redMask, int greenMask, int blueMask, int alphaMask)
	{
		super();
		
		//Calculate minimal bits requires
		int smallestBits = 0;
		{
			int totalMask = redMask | greenMask | blueMask | alphaMask;
			
			int highestBitUsed = 0;
			{
				if (getTotalMask() == 0)
					highestBitUsed = -1;
				else
					highestBitUsed = dcd32(getHighestOneBit(totalMask));
			}
			
			smallestBits = highestBitUsed + 1;
		}
		
		
		
		
		this.bits = smallestBits;
		this.redMask = redMask;
		this.greenMask = greenMask;
		this.blueMask = blueMask;
		this.alphaMask = alphaMask;
		
		
		if (smallestBits == 0) throw new IllegalArgumentException("Invalid color storage format: invalid number of bits: "+this);
	}
	
	
	
	
	
	
	public static SimpleImageColorStorageType arbitraryPositionsForGivenBitcounts(int redBitcount, int greenBitcount, int blueBitcount, int alphaBitcount)
	{
		return new SimpleImageColorStorageType(
		
		redBitcount + greenBitcount + blueBitcount + alphaBitcount,
		
		((1 << redBitcount) - 1) << 0,
		((1 << greenBitcount) - 1) << redBitcount,
		((1 << blueBitcount) - 1) << (redBitcount + greenBitcount),
		((1 << alphaBitcount) - 1) << (redBitcount + greenBitcount + blueBitcount)
		);
	}
	
	
	
	
	
	/**
	 * Note: this is probably only useful for contiguous masks XD'
	 */
	public static int getMaxValueOfContiguousMask(int mask) throws IllegalArgumentException
	{
		int bitCount = getNumberOfOneBits(mask);
		return (1 << bitCount) - 1;
	}
	
	/**
	 * The Pre Mask (as I call it XD''), is a value which, when multiplied an integer between 0 and the {@link #getMaxValueOfContiguousMask(int) max value} (usually 255), will produce the properly shifted value, *even with multiple components orred together*! :D
	 * Eg, 0x010100, if multiplied by 0xFF would produce 0xFFFF00, and proportionately for values in [0x00, 0xFF]  :3
	 * 
	 * But NOTE: this particular function only works for contiguous masks! X'D
	 * But you can OR together contiguous premasks  ;D
	 */
	public static int getPreMaskOfContiguousMask(int mask) throws IllegalArgumentException
	{
		int preMask = getLowestOneBit(mask);
		
		assert isContiguousOnes(mask) ? preMask * getMaxValueOfContiguousMask(mask) == mask : true;
		
		return preMask;
	}
	
	
	
	
	public int getBits()
	{
		return this.bits;
	}
	
	public int getRedMask()
	{
		return this.redMask;
	}
	
	public int getGreenMask()
	{
		return this.greenMask;
	}
	
	public int getBlueMask()
	{
		return this.blueMask;
	}
	
	public int getAlphaMask()
	{
		return this.alphaMask;
	}
	
	
	
	public boolean hasAlpha()
	{
		return this.alphaMask != 0;
	}
	
	public boolean hasRed()
	{
		return this.redMask != 0;
	}
	
	public boolean hasGreen()
	{
		return this.greenMask != 0;
	}
	
	public boolean hasBlue()
	{
		return this.blueMask != 0;
	}
	
	
	
	public int getRedShift()  //..red shift..XD!!
	{
		return dcd32(getLowestOneBit(this.redMask));
	}
	
	public int getGreenShift()
	{
		return dcd32(getLowestOneBit(this.greenMask));
	}
	
	public int getBlueShift()  //..blue shift..XD!!
	{
		return dcd32(getLowestOneBit(this.blueMask));
	}
	
	public int getAlphaShift()
	{
		return dcd32(getLowestOneBit(this.alphaMask));
	}
	
	
	public int getRedBitCount()
	{
		return getNumberOfOneBits(this.redMask);
	}
	
	public int getGreenBitCount()
	{
		return getNumberOfOneBits(this.greenMask);
	}
	
	public int getBlueBitCount()
	{
		return getNumberOfOneBits(this.blueMask);
	}
	
	public int getAlphaBitCount()
	{
		return getNumberOfOneBits(this.alphaMask);
	}
	
	
	
	public int getRedMaxValue()
	{
		return (1 << getRedBitCount()) - 1;
	}
	
	public int getGreenMaxValue()
	{
		return (1 << getGreenBitCount()) - 1;
	}
	
	public int getBlueMaxValue()
	{
		return (1 << getBlueBitCount()) - 1;
	}
	
	public int getAlphaMaxValue()
	{
		return (1 << getAlphaBitCount()) - 1;
	}
	
	
	
	public int getTotalMask()
	{
		return this.redMask | this.greenMask | this.blueMask | this.alphaMask;
	}
	
	public int getTotalColorMask()
	{
		return this.redMask | this.greenMask | this.blueMask;
	}
	
	
	public int getLowestBitUsed()
	{
		if (getTotalMask() == 0)
			return -1;
		else
			return dcd32(getLowestOneBit(getTotalMask()));
	}
	
	public int getHighestBitUsed()
	{
		if (getTotalMask() == 0)
			return -1;
		else
			return dcd32(getHighestOneBit(getTotalMask()));
	}
	
	public int getSmallestNumberOfBitsRequiredIncludingUnusedLowBits()
	{
		return getHighestBitUsed()+1;
	}
	
	public int getNumberOfLowOrderBitsUnused()
	{
		return getLowestBitUsed();
	}
	
	public int getNumberOfHighOrderBitsUnused()
	{
		return this.bits - getHighestBitUsed() - 1;
	}
	
	public int getTotalNumberOfBitsMasked()
	{
		return getNumberOfOneBits(getTotalMask());
	}
	
	public int getTotalNumberOfColorBitsMasked()
	{
		return getNumberOfOneBits(getTotalColorMask());
	}
	
	
	
	public boolean isComponentsOverlapping()
	{
		int currentMask = 0;
		
		//if ((currentMask & redMask) != 0)
		//	return true;
		currentMask = this.redMask;
		
		if ((currentMask & this.greenMask) != 0)
			return true;
		currentMask |= this.greenMask;
		
		if ((currentMask & this.blueMask) != 0)
			return true;
		currentMask |= this.blueMask;
		
		if ((currentMask & this.alphaMask) != 0)
			return true;
		//currentMask |= alphaMask;
		
		return false;
	}
	
	
	public boolean isRedContiguous()
	{
		return isContiguousOnes(this.redMask);
	}
	
	public boolean isGreenContiguous()
	{
		return isContiguousOnes(this.greenMask);
	}
	
	public boolean isBlueContiguous()
	{
		return isContiguousOnes(this.blueMask);
	}
	
	public boolean isAlphaContiguous()
	{
		return isContiguousOnes(this.alphaMask);
	}
	
	public boolean isEachComponentContiguous()
	{
		return isRedContiguous() && isGreenContiguous() && isBlueContiguous() && isAlphaContiguous();
	}
	
	/**
	 * This isn't defined unless the components are non-overlapping and non-intermingled (which they aren't if they are non-overlapping and each contiguous in themselves :> )
	 */
	public boolean isNoSpaceBetweenComponents()
	{
		return isContiguousOnes(getTotalMask());
	}
	
	
	public boolean isComponentsSameSizeOptionalAlpha()
	{
		if (!(getRedBitCount() == getGreenBitCount() && getGreenBitCount() == getBlueBitCount()))
			return false;
		
		if (hasAlpha())
			if (getAlphaBitCount() != getRedBitCount())
				return false;
		
		return true;
	}
	
	public void ensureComponentsSameSizeOptionalAlpha()
	{
		if (!isComponentsSameSizeOptionalAlpha()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
	}
	
	public int getEqualBitCountOfEachComponent()
	{
		ensureComponentsSameSizeOptionalAlpha();
		return getRedBitCount();
	}
	
	
	
	
	public boolean isGrayscale()
	{
		return this.redMask == this.greenMask && this.greenMask == this.blueMask;
	}
	
	public int getGrayscaleMask()
	{
		int m = getRedMask();
		if (getGreenMask() != m) throw new SimpleImageColorStorageTypeNotSupportedException(this);
		if (getBlueMask() != m) throw new SimpleImageColorStorageTypeNotSupportedException(this);
		return m;
	}
	
	public int getGrayscaleBitCount()
	{
		int bc = getRedBitCount();
		if (getGreenBitCount() != bc) throw new SimpleImageColorStorageTypeNotSupportedException(this);
		if (getBlueBitCount() != bc) throw new SimpleImageColorStorageTypeNotSupportedException(this);
		return bc;
	}
	
	public int getGrayscaleShift()
	{
		int s = getRedShift();
		if (getGreenShift() != s) throw new SimpleImageColorStorageTypeNotSupportedException(this);
		if (getBlueShift() != s) throw new SimpleImageColorStorageTypeNotSupportedException(this);
		return s;
	}
	
	
	
	public boolean isNullFormat()
	{
		return this.alphaMask == 0 && this.redMask == 0 && this.greenMask == 0 && this.blueMask == 0;
	}
	
	
	
	
	public void ensureColorsPresent() throws SimpleImageColorStorageTypeNotSupportedException
	{
		if (!hasRed() || !hasGreen() || !hasBlue()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
	}
	
	public void ensureAlphaPresent() throws SimpleImageColorStorageTypeNotSupportedException
	{
		if (!hasAlpha()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
	}
	
	public void ensureAlphaNotPresent() throws SimpleImageColorStorageTypeNotSupportedException
	{
		if (hasAlpha()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
	}
	
	public void ensureColorsAndAlphaPresent() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureColorsPresent();
		ensureAlphaPresent();
	}
	
	public void ensureNonOverlapping() throws SimpleImageColorStorageTypeNotSupportedException
	{
		if (isComponentsOverlapping()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
	}
	
	public void ensureEachComponentIsContiguous() throws SimpleImageColorStorageTypeNotSupportedException
	{
		if (!isEachComponentContiguous()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
	}
	
	public void ensureNoSpaceBetweenComponents() throws SimpleImageColorStorageTypeNotSupportedException
	{
		if (!isNoSpaceBetweenComponents()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
	}
	
	
	
	
	/**
	 * Note!: does not guarantee no space between components! (see {@link #ensureNoSpaceBetweenComponents()} :> )
	 */
	public void ensureStandardColorWithOptionalAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureColorsPresent();
		ensureNonOverlapping();
		ensureEachComponentIsContiguous();
	}
	
	public void ensureStandardColorWithNoAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureStandardColorWithOptionalAlpha();
		ensureAlphaNotPresent();
	}
	
	public void ensureStandardColorWithMandatoryAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureStandardColorWithOptionalAlpha();
		ensureAlphaPresent();
	}
	
	
	
	public void ensureStandardGrayscaleWithOptionalAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		if (!isGrayscale()) throw new SimpleImageColorStorageTypeNotSupportedException(this);
		ensureColorsPresent();
		ensureEachComponentIsContiguous();
	}
	
	public void ensureStandardGrayscaleWithNoAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureStandardGrayscaleWithOptionalAlpha();
		ensureAlphaNotPresent();
	}
	
	public void ensureStandardGrayscaleWithMandatoryAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureStandardGrayscaleWithOptionalAlpha();
		ensureAlphaPresent();
	}
	
	
	
	
	
	public static enum SimpleImageColorStorageTypeComponent
	{
		Red,
		Green,
		Blue,
		Alpha,
	}
	
	public int getMask(SimpleImageColorStorageTypeComponent component)
	{
		if (component == SimpleImageColorStorageTypeComponent.Red) return getRedMask();
		else if (component == SimpleImageColorStorageTypeComponent.Green) return getGreenMask();
		else if (component == SimpleImageColorStorageTypeComponent.Blue) return getBlueMask();
		else if (component == SimpleImageColorStorageTypeComponent.Alpha) return getAlphaMask();
		else if (component == null) throw new NullEnumValueIllegalArgumentException();
		else throw new UnexpectedHardcodedEnumValueException();
	}
	
	public boolean hasComponent(SimpleImageColorStorageTypeComponent component)
	{
		if (component == SimpleImageColorStorageTypeComponent.Red) return hasRed();
		else if (component == SimpleImageColorStorageTypeComponent.Green) return hasGreen();
		else if (component == SimpleImageColorStorageTypeComponent.Blue) return hasBlue();
		else if (component == SimpleImageColorStorageTypeComponent.Alpha) return hasAlpha();
		else if (component == null) throw new NullEnumValueIllegalArgumentException();
		else throw new UnexpectedHardcodedEnumValueException();
	}
	
	public int getShift(SimpleImageColorStorageTypeComponent component)
	{
		if (component == SimpleImageColorStorageTypeComponent.Red) return getRedShift();
		else if (component == SimpleImageColorStorageTypeComponent.Green) return getGreenShift();
		else if (component == SimpleImageColorStorageTypeComponent.Blue) return getBlueShift();
		else if (component == SimpleImageColorStorageTypeComponent.Alpha) return getAlphaShift();
		else if (component == null) throw new NullEnumValueIllegalArgumentException();
		else throw new UnexpectedHardcodedEnumValueException();
	}
	
	public int getBitCount(SimpleImageColorStorageTypeComponent component)
	{
		if (component == SimpleImageColorStorageTypeComponent.Red) return getRedBitCount();
		else if (component == SimpleImageColorStorageTypeComponent.Green) return getGreenBitCount();
		else if (component == SimpleImageColorStorageTypeComponent.Blue) return getBlueBitCount();
		else if (component == SimpleImageColorStorageTypeComponent.Alpha) return getAlphaBitCount();
		else if (component == null) throw new NullEnumValueIllegalArgumentException();
		else throw new UnexpectedHardcodedEnumValueException();
	}
	
	public boolean isContiguous(SimpleImageColorStorageTypeComponent component)
	{
		if (component == SimpleImageColorStorageTypeComponent.Red) return isRedContiguous();
		else if (component == SimpleImageColorStorageTypeComponent.Green) return isGreenContiguous();
		else if (component == SimpleImageColorStorageTypeComponent.Blue) return isBlueContiguous();
		else if (component == SimpleImageColorStorageTypeComponent.Alpha) return isAlphaContiguous();
		else if (component == null) throw new NullEnumValueIllegalArgumentException();
		else throw new UnexpectedHardcodedEnumValueException();
	}
	
	
	
	/**
	 * Little-endian  (*cough* like it should be *cough* XD )
	 */
	public SimpleImageColorStorageTypeComponent[] getStandardComponentOrderingOptionalAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureStandardColorWithOptionalAlpha();
		
		SimpleImageColorStorageTypeComponent[] sortedComponents = new SimpleImageColorStorageTypeComponent[hasAlpha() ? 4 : 3];
		
		//Arbitrary initial ordering ^_^
		sortedComponents[0] = SimpleImageColorStorageTypeComponent.Red;
		sortedComponents[1] = SimpleImageColorStorageTypeComponent.Green;
		sortedComponents[2] = SimpleImageColorStorageTypeComponent.Blue;
		if (hasAlpha()) sortedComponents[3] = SimpleImageColorStorageTypeComponent.Alpha;
		
		Arrays.sort(sortedComponents, new Comparator<SimpleImageColorStorageTypeComponent>()
		{
			@Override
			public int compare(SimpleImageColorStorageTypeComponent a, SimpleImageColorStorageTypeComponent b)
			{
				int shiftValueA = getShift(a);
				int shiftValueB = getShift(b);
				return SmallIntegerMathUtilities.cmp(shiftValueA, shiftValueB);
			}
		});
		
		return sortedComponents;
	}
	
	public SimpleImageColorStorageTypeComponent[] getStandardComponentOrderingNoAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureStandardColorWithNoAlpha();
		return getStandardComponentOrderingOptionalAlpha();
	}
	
	public SimpleImageColorStorageTypeComponent[] getStandardComponentOrderingMandatoryAlpha() throws SimpleImageColorStorageTypeNotSupportedException
	{
		ensureStandardColorWithMandatoryAlpha();
		return getStandardComponentOrderingOptionalAlpha();
	}
	
	
	
	
	public int[] getMasksRGBAOptionalAlpha()
	{
		int[] masks = new int[hasAlpha() ? 4 : 3];
		masks[0] = this.redMask;
		masks[1] = this.greenMask;
		masks[2] = this.blueMask;
		
		if (hasAlpha())
			masks[3] = this.alphaMask;
		
		return masks;
	}
	
	public int[] getMasksRGBAMandatoryAlpha()
	{
		ensureAlphaPresent();
		int[] masks = getMasksRGBAOptionalAlpha();
		if (masks.length != 4) throw new AssertionError();
		return masks;
	}
	
	public int[] getMasksRGBANoAlpha()
	{
		ensureAlphaNotPresent();
		int[] masks = getMasksRGBAOptionalAlpha();
		if (masks.length != 3) throw new AssertionError();
		return masks;
	}
	
	
	
	public int[] getMasks(SimpleImageColorStorageTypeComponent[] ordering)
	{
		//hasXYZ and bijectivity and etc. intentionally not checked (they might want that for some reason!)  :>
		int[] masks = new int[ordering.length];
		for (int i = 0; i < ordering.length; i++)
			masks[i] = getMask(ordering[i]);
		return masks;
	}
	
	
	
	
	public int[] getShiftsRGBAOptionalAlpha()
	{
		int[] shifts = new int[hasAlpha() ? 4 : 3];
		shifts[0] = this.redMask;
		shifts[1] = this.greenMask;
		shifts[2] = this.blueMask;
		
		if (hasAlpha())
			shifts[3] = this.alphaMask;
		
		return shifts;
	}
	
	public int[] getShiftsRGBAMandatoryAlpha()
	{
		ensureAlphaPresent();
		int[] shifts = getShiftsRGBAOptionalAlpha();
		if (shifts.length != 4) throw new AssertionError();
		return shifts;
	}
	
	public int[] getShiftsRGBANoAlpha()
	{
		ensureAlphaNotPresent();
		int[] shifts = getShiftsRGBAOptionalAlpha();
		if (shifts.length != 3) throw new AssertionError();
		return shifts;
	}
	
	
	
	public int[] getShifts(SimpleImageColorStorageTypeComponent[] ordering)
	{
		//hasXYZ and bijectivity and etc. intentionally not checked (they might want that for some reason!)  :>
		int[] shifts = new int[ordering.length];
		for (int i = 0; i < ordering.length; i++)
			shifts[i] = getShift(ordering[i]);
		return shifts;
	}
	
	
	
	
	public int[] getBitCountsRGBAOptionalAlpha()
	{
		int[] bitCounts = new int[hasAlpha() ? 4 : 3];
		bitCounts[0] = getRedBitCount();
		bitCounts[1] = getGreenBitCount();
		bitCounts[2] = getBlueBitCount();
		
		if (hasAlpha())
			bitCounts[3] = getAlphaBitCount();
		
		return bitCounts;
	}
	
	public int[] getBitCountsRGBAMandatoryAlpha()
	{
		ensureAlphaPresent();
		int[] bitCounts = getBitCountsRGBAOptionalAlpha();
		if (bitCounts.length != 4) throw new AssertionError();
		return bitCounts;
	}
	
	public int[] getBitCountsRGBANoAlpha()
	{
		ensureAlphaNotPresent();
		int[] bitCounts = getBitCountsRGBAOptionalAlpha();
		if (bitCounts.length != 3) throw new AssertionError();
		return bitCounts;
	}
	
	
	
	public int[] getBitCounts(SimpleImageColorStorageTypeComponent[] ordering)
	{
		//hasXYZ and bijectivity and etc. intentionally not checked (they might want that for some reason!)  :>
		int[] bitCounts = new int[ordering.length];
		for (int i = 0; i < ordering.length; i++)
			bitCounts[i] = getBitCount(ordering[i]);
		return bitCounts;
	}
	
	
	
	
	
	
	public static abstract class SimpleImageColorStorageTypeElement
	{
		protected final int shift, bitCount;
		
		public SimpleImageColorStorageTypeElement(int shift, int bitCount)
		{
			this.shift = shift;
			this.bitCount = bitCount;
		}
		
		/**
		 * aka, offset :>
		 * (little-bit-endian)
		 */
		public int getShift()
		{
			return this.shift;
		}
		
		public int getBitCount()
		{
			return this.bitCount;
		}
		
		
		
		/**
		 * aka color/alpha element :>
		 */
		public static class SimpleImageColorStorageTypeUsedElement
		extends SimpleImageColorStorageTypeElement
		{
			protected final SimpleImageColorStorageTypeComponent component;
			
			public SimpleImageColorStorageTypeUsedElement(int shift, int bitCount, SimpleImageColorStorageTypeComponent component)
			{
				super(shift, bitCount);
				this.component = component;
			}
			
			public SimpleImageColorStorageTypeComponent getComponent()
			{
				return this.component;
			}
		}
		
		
		/**
		 * aka padding
		 */
		public static class SimpleImageColorStorageTypeUnusedElement
		extends SimpleImageColorStorageTypeElement
		{
			public SimpleImageColorStorageTypeUnusedElement(int shift, int bitCount)
			{
				super(shift, bitCount);
			}
		}
	}
	
	
	/**
	 * This searches (conceptually) bit-by-bit and returns a {@link SimpleImageColorStorageTypeElement} for each contiguous region of bits :>
	 * Note: this works even if all the bits used by a component are not contiguous, so in (only) that case, it can return multiple used-elements for the same component! xD
	 */
	public SimpleImageColorStorageTypeElement[] inspect(boolean includeUnused, boolean includeUsed)
	{
		ArrayList<SimpleImageColorStorageTypeElement> elements = new ArrayList<SimpleImageColorStorageTypeElement>();
		
		int currentPosition = 0;
		while (currentPosition < this.bits && currentPosition < 32) //bits can extend past 32; that just specifies extra unused high-order bits ^_^
		{
			int usedBitsRed = getNumberOfContiguousBitsOfAGivenValue(this.redMask, currentPosition, this.bits - currentPosition, true);
			int usedBitsGreen = getNumberOfContiguousBitsOfAGivenValue(this.greenMask, currentPosition, this.bits - currentPosition, true);
			int usedBitsBlue = getNumberOfContiguousBitsOfAGivenValue(this.blueMask, currentPosition, this.bits - currentPosition, true);
			int usedBitsAlpha = getNumberOfContiguousBitsOfAGivenValue(this.alphaMask, currentPosition, this.bits - currentPosition, true);
			
			int usedBits = SmallIntegerMathUtilities.getTheOnlyOneNotzeroAsserting(usedBitsRed, usedBitsGreen, usedBitsBlue, usedBitsAlpha);
			
			SimpleImageColorStorageTypeComponent component = null;
			boolean used = false;
			{
				if (usedBitsRed != 0) component = SimpleImageColorStorageTypeComponent.Red;
				else if (usedBitsGreen != 0) component = SimpleImageColorStorageTypeComponent.Green;
				else if (usedBitsBlue != 0) component = SimpleImageColorStorageTypeComponent.Blue;
				else if (usedBitsAlpha != 0) component = SimpleImageColorStorageTypeComponent.Alpha;
				else component = null; //unused!
				
				used = component != null;
			}
			
			
			if ((used && !includeUsed) || (!used && !includeUnused))
				continue; //skip it; poor element ._.
			
			if (used)
			{
				if (usedBits <= 0) throw new AssertionError();
				
				elements.add(new SimpleImageColorStorageTypeUsedElement(currentPosition, usedBits, component));
				currentPosition += usedBits;
			}
			else
			{
				//Calculate number of UNused bits!
				int unusedBits = 0;
				{
					int unusedBitsRed = getNumberOfContiguousBitsOfAGivenValue(this.redMask, currentPosition, this.bits - currentPosition, false);
					int unusedBitsGreen = getNumberOfContiguousBitsOfAGivenValue(this.greenMask, currentPosition, this.bits - currentPosition, false);
					int unusedBitsBlue = getNumberOfContiguousBitsOfAGivenValue(this.blueMask, currentPosition, this.bits - currentPosition, false);
					int unusedBitsAlpha = getNumberOfContiguousBitsOfAGivenValue(this.alphaMask, currentPosition, this.bits - currentPosition, false);
					
					unusedBits = SmallIntegerMathUtilities.least(unusedBitsRed, unusedBitsGreen, unusedBitsBlue, unusedBitsAlpha);
					
					if (unusedBits <= 0) throw new AssertionError();
				}
				
				elements.add(new SimpleImageColorStorageTypeUnusedElement(currentPosition, unusedBits));
				currentPosition += unusedBits;
			}
		}
		
		if (currentPosition < this.bits) throw new AssertionError();
		
		return elements.toArray(new SimpleImageColorStorageTypeElement[elements.size()]);
	}
	
	/**
	 * inspect(true, true)
	 * (include everything :> )
	 * This is guaranteed to return at least one element (even if it's just a bunch of unused-ness) :>
	 */
	public SimpleImageColorStorageTypeElement[] inspect()
	{
		return inspect(true, true);
	}
	
	
	
	
	
	@Override
	public SimpleImageColorStorageType clone()
	{
		return new SimpleImageColorStorageType(this.bits, this.redMask, this.greenMask, this.blueMask, this.alphaMask);
	}
	
	/**
	 * Creates an analogous ImageColorStorageType, just with extra wasted space at the high end of the bit field (useful for, eg, putting RGB into a 32 bit integer ;>  )
	 * @return never null
	 */
	public SimpleImageColorStorageType promote(int morebits)
	{
		if (morebits < this.bits)
			throw new IllegalArgumentException("promoting to less bits? :\\");
		else if (morebits == this.bits)
			return this;
		else
		{
			return new SimpleImageColorStorageType(morebits, this.redMask, this.greenMask, this.blueMask, this.alphaMask);
		}
	}
	
	/**
	 * Creates an analogous ImageColorStorageType with extra wasted space at a higher
	 * @return <code>null</code> iff it would have cut into
	 */
	public SimpleImageColorStorageType diffbits(int diffbits)
	{
		if (diffbits == this.bits)
			return this;
		
		//if (diffbits != this.bits)
		if (diffbits < getSmallestNumberOfBitsRequiredIncludingUnusedLowBits())
			throw new IllegalArgumentException(diffbits+" is too few bits to hold masks for: "+this+"   :\\");
		
		return new SimpleImageColorStorageType(diffbits, this.redMask, this.greenMask, this.blueMask, this.alphaMask);
	}
	
	
	
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.bits;
		result = prime * result + this.redMask;
		result = prime * result + this.greenMask;
		result = prime * result + this.blueMask;
		result = prime * result + this.alphaMask;
		return result;
	}
	
	public int hashCodeDisregardingBitlength()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + this.redMask;
		result = prime * result + this.greenMask;
		result = prime * result + this.blueMask;
		result = prime * result + this.alphaMask;
		return result;
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (obj == this) return true;
		if (!(obj instanceof SimpleImageColorStorageType)) return false;
		SimpleImageColorStorageType other = (SimpleImageColorStorageType)obj;
		
		return
		other.redMask == this.redMask &&
		other.greenMask == this.greenMask &&
		other.blueMask == this.blueMask &&
		other.alphaMask == this.alphaMask &&
		other.bits == this.bits;
	}
	
	public boolean equalsDisregardingBitlength(SimpleImageColorStorageType other)
	{
		if (other == this) return true;
		if (other == null) return false;
		
		return
		other.redMask == this.redMask &&
		other.greenMask == this.greenMask &&
		other.blueMask == this.blueMask &&
		other.alphaMask == this.alphaMask;
	}
	
	/**
	 * Like {@link #equals(Object)}, but allows the formats to disagree about whether they *have* alpha or not.
	 * If they both have alpha then the alpha masks DO have to match!!
	 */
	public boolean equalsSaveForAlphaPresence(Object obj)
	{
		if (this == obj) return true;
		if (obj == null) return false;
		if (getClass() != obj.getClass()) return false;
		SimpleImageColorStorageType other = (SimpleImageColorStorageType)obj;
		return
		other.redMask == this.redMask &&
		other.greenMask == this.greenMask &&
		other.blueMask == this.blueMask &&
		
		(other.alphaMask == 0 || this.alphaMask == 0 || other.alphaMask == this.alphaMask) &&
		
		other.bits == this.bits;
	}
	
	
	public static boolean canConvertFromOneToOther(SimpleImageColorStorageType source, SimpleImageColorStorageType dest)
	{
		if (source.hasAlpha() && !dest.hasAlpha())
			return false;
		
		if (dest.isGrayscale() && !source.isGrayscale())
			return false;
		
		if (source.hasRed() != dest.hasRed())
			return false;
		if (source.hasGreen() != dest.hasGreen())
			return false;
		if (source.hasBlue() != dest.hasBlue())
			return false;
		
		return true;
	}
	
	
	
	
	
	
	public boolean isSuperformatOrEquivalentPurelyByBitcounts(SimpleImageColorStorageType other)
	{
		return
		getAlphaBitCount() >= other.getAlphaBitCount() &&
		getRedBitCount() >= other.getRedBitCount() &&
		getGreenBitCount() >= other.getGreenBitCount() &&
		getBlueBitCount() >= other.getBlueBitCount();
	}
	
	public boolean isSubformatOrEquivalentPurelyByBitcounts(SimpleImageColorStorageType other)
	{
		return
		getAlphaBitCount() <= other.getAlphaBitCount() &&
		getRedBitCount() <= other.getRedBitCount() &&
		getGreenBitCount() <= other.getGreenBitCount() &&
		getBlueBitCount() <= other.getBlueBitCount();
	}
	
	public boolean isEquivalentPurelyByBitcounts(SimpleImageColorStorageType other)
	{
		return
		getAlphaBitCount() == other.getAlphaBitCount() &&
		getRedBitCount() == other.getRedBitCount() &&
		getGreenBitCount() == other.getGreenBitCount() &&
		getBlueBitCount() == other.getBlueBitCount();
	}
	
	
	
	public boolean isSuperformatPurelyByBitcounts(SimpleImageColorStorageType other)
	{
		return isSuperformatOrEquivalentPurelyByBitcounts(other) && !isEquivalentPurelyByBitcounts(other);
	}
	
	public boolean isSubformatPurelyByBitcounts(SimpleImageColorStorageType other)
	{
		return isSubformatOrEquivalentPurelyByBitcounts(other) && !isEquivalentPurelyByBitcounts(other);
	}
	
	
	
	
	
	
	
	
	
	/**
	 * @return {r, g, b} or {r, g, b, a} depending on {@link #hasAlpha()} :33
	 */
	public int[] unpackRGB(int packed32)
	{
		boolean hasAlpha = hasAlpha();
		
		int r = (packed32 & getRedMask()) >>> getRedShift();  //XDD
		int g = (packed32 & getGreenMask()) >>> getGreenShift();
				int b = (packed32 & getBlueMask()) >>> getBlueShift();  //XDD   //ECLIPSE INDENTER, WHAT GIVES!? And why is it MIGRATING!!? X'DDD
		int a = hasAlpha ? (packed32 & getAlphaMask()) >>> getAlphaShift() : 0;
		
		return hasAlpha ? new int[]{r, g, b, a} : new int[]{r, g, b};
	}
	
	
	public int[] unpackRGBA(int packed32)
	{
		boolean hasAlpha = hasAlpha();
		
		int r = (packed32 & getRedMask()) >>> getRedShift();  //XDD
		int g = (packed32 & getGreenMask()) >>> getGreenShift();
		int b = (packed32 & getBlueMask()) >>> getBlueShift();  //XDD   //ECLIPSE INDENTER, WHAT GIVES!? And why is it MIGRATING!!? X'DDD
		int a = hasAlpha ? (packed32 & getAlphaMask()) >>> getAlphaShift() : 0;
		
		return new int[]{r, g, b, a};
	}
	
	
	public int packRGB(int r, int g, int b)
	{
		return packRGB(r, g, b, hasAlpha() ? (1 << getAlphaBitCount()) - 1 : 0);
	}
	
	public int packRGB(int r, int g, int b, int a)
	{
		int packed = 0;
		
		if ((r & getMask32(getRedBitCount())) != r)
			throw new IllegalArgumentException("Red is out of range for "+getRedBitCount()+" bits!");
		packed |= r << getRedShift();
		
		if ((g & getMask32(getGreenBitCount())) != g)
			throw new IllegalArgumentException("Green is out of range for "+getGreenBitCount()+" bits!");
		packed |= g << getGreenShift();
		
		if ((b & getMask32(getBlueBitCount())) != b)
			throw new IllegalArgumentException("Blue is out of range for "+getBlueBitCount()+" bits!");
		packed |= b << getBlueShift();
		
		if (hasAlpha())
		{
			if ((a & getMask32(getAlphaBitCount())) != a)
				throw new IllegalArgumentException("Alpha is out of range for "+getAlphaBitCount()+" bits!");
			packed |= a << getAlphaShift();
		}
		
		return packed;
	}
	
	public int packRGB(int[] values)
	{
		if (values.length == 3)
			return packRGB(values[0], values[1], values[2]);
		else if (values.length == 4)
			return packRGB(values[0], values[1], values[2], values[3]);
		else
			throw new IllegalArgumentException("Need 3 (RGB) or 4 (RGBA) values, not "+values.length+" values!");
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int convertPacked32(int packedInput, SimpleImageColorStorageType inputFormat, SimpleImageColorStorageType outputFormat)
	{
		if (inputFormat.equalsDisregardingBitlength(outputFormat))
		{
			return packedInput;
		}
		else
		{
			//Todo [should we] support down-converting![?]
			return outputFormat.packRGB(inputFormat.unpackRGB(packedInput));
		}
	}
	
	
	
	
	//They're all really the same thing XDDD
	public static int convertRGB32beToBGR32be(int argbInput)
	{
		return reverseLowThreeBytes(argbInput);
	}
	
	public static int convertBGR32beToRGB32be(int abgrInput)
	{
		return reverseLowThreeBytes(abgrInput);
	}
	
	
	public static int convertARGB32beToABGR32be(int argbInput)
	{
		return reverseLowThreeBytes(argbInput);
	}
	
	public static int convertABGR32beToARGB32be(int abgrInput)
	{
		return reverseLowThreeBytes(abgrInput);
	}
	
	
	
	public static int reverseLowThreeBytes(int input)
	{
		int output = Integer.reverseBytes(input) >>> 8;
			
			//ECLIPSE INDENTER, WHAT GIVES!? And why is it MIGRATING!!? X'DDD
			output &= 0x00FFFFFF;
			output |= input & 0xFF000000;
			return output;
	}
	
	
	public static int promoteGrayscaleToColoredNoAlpha(int grayscale)
	{
		return grayscale | (grayscale << 8) | (grayscale << 16);
	}
	
	public static int promoteAGbe16ToACCCbe32(int grayscaleAndAlpha)
	{
		int grayscale = grayscaleAndAlpha & 0x000000FF;
		return grayscale | (grayscale << 8) | (grayscale << 16) | ((grayscaleAndAlpha << 16) & 0xFF000000);
	}
	
	
	
	
	
	
	
	
	
	
	public static SimpleImageColorStorageType leastCommonDenominator(SimpleImageColorStorageType a, SimpleImageColorStorageType b)
	{
		if (a.isSuperformatOrEquivalentPurelyByBitcounts(b))
			return a;
		if (b.isSuperformatOrEquivalentPurelyByBitcounts(a))
			return b;
		
		return SimpleImageColorStorageType.arbitraryPositionsForGivenBitcounts(
		SmallIntegerMathUtilities.greatest(a.getRedBitCount(), b.getRedBitCount()),
		SmallIntegerMathUtilities.greatest(a.getGreenBitCount(), b.getGreenBitCount()),
		SmallIntegerMathUtilities.greatest(a.getBlueBitCount(), b.getBlueBitCount()),
		SmallIntegerMathUtilities.greatest(a.getAlphaBitCount(), b.getAlphaBitCount())
		);
	}
	
	
	
	
	
	
	@Override
	public String toString()
	{
		return "SimpleImageColorStorageType [bits=" + this.bits + ", alphaMask=" + this.alphaMask + ", redMask=" + this.redMask + ", greenMask=" + this.greenMask + ", blueMask=" + this.blueMask + "]";
	}
}
