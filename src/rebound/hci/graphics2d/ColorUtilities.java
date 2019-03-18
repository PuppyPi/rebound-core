/*
 * Created on Feb 28, 2012
 * 	by the great Eclipse(c)
 */
package rebound.hci.graphics2d;

import static rebound.math.SmallIntegerMathUtilities.*;
import java.awt.Color;
import java.awt.Paint;
import javax.annotation.Nullable;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NotANumberException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.TextSyntaxException;
import rebound.hci.graphics2d.ourrasters.ReboundAccessibleRasterImagesAndJRESystems;
import rebound.hci.graphics2d.ourrasters.SimpleImageColorStorageType;
import rebound.hci.graphics2d.ourrasters.SimpleRGBAColor;
import rebound.math.SmallFloatMathUtilities;
import rebound.math.SmallIntegerMathUtilities;
import rebound.text.StringUtilities;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.objectutil.ObjectUtilities;

public class ColorUtilities
implements JavaNamespace
{
	public static final Color Transparent = new Color(0, 0, 0, 0);
	
	public static boolean isTransparent(@Nullable Paint p)
	{
		return p == null || (p instanceof Color && ((Color)p).getAlpha() == 0);
	}
	
	
	
	public static int floatToInt8BitClamped(float f)
	{
		if (Float.isNaN(f))
			throw new NotANumberException();
		
		/*
		 * Using 256 not 255 ensures there are exactly 256 (equally-sized) intervals over the domain that correspond to the integer buckets :3
		 */
		int i = (int)(f * 256);
		return greatest(0, least(255, i));
	}
	
	public static int floatToInt8BitClamped(double f)
	{
		if (Double.isNaN(f))
			throw new NotANumberException();
		
		/*
		 * Using 256 not 255 ensures there are exactly 256 (equally-sized) intervals over the domain that correspond to the integer buckets :3
		 */
		int i = (int)(f * 256);
		return greatest(0, least(255, i));
	}
	
	
	
	
	public static int floatToInt8Bit(double f)
	{
		if (f < 0 || f > 1)
			throw new IllegalArgumentException();
		if (Double.isNaN(f))
			throw new NotANumberException();
		
		/*
		 * Using 256 not 255 ensures there are exactly 256 (equally-sized) intervals over the domain that correspond to the integer buckets :3
		 */
		int i = (int)(f * 256);
		return i == 256 ? 255 : i;
	}
	
	public static int floatToInt8Bit(float f)
	{
		if (f < 0 || f > 1)
			throw new IllegalArgumentException();
		if (Double.isNaN(f))
			throw new NotANumberException();
		
		/*
		 * Using 256 not 255 ensures there are exactly 256 (equally-sized) intervals over the domain that correspond to the integer buckets :3
		 * The only inconsistency is that one of the buckets will get one extra infinitescimal real-number value assigned to it.
		 * 		(ie, if you assign the low bound to the bucket, then its high bound goes to the next one by the same rule!  ..but what happens to the last one?! \o/ )
		 * Here we arbitrarily assign the extra point to the high bucket (255), this way, if they
		 *  used the real number domain of [0,1) then there wouldn't be that extra point, and
		 *  everything would be all happy! :D  (as opposed to [0,1] which we normally accept ^^' )
		 */
		int i = (int)(f * 256);
		return i == 256 ? 255 : i;
	}
	
	
	
	
	
	public static float int8BitToFloat(int i)
	{
		if (i < 0 || i >= 256)
			throw new IllegalArgumentException();
		
		//We use the 1.0 shortcut instead of letting it naturally be 255/256, for compatibility
		//with other codes ^^'
		//This makes the range be [0,1] instead of (the probably better,) [0,1)   ^^'
		return i == 255 ? 1f : i / 256f;
	}
	
	public static double int8BitToDouble(int i)
	{
		if (i < 0 || i >= 256)
			throw new IllegalArgumentException();
		
		//We use the 1.0 shortcut instead of letting it naturally be 255/256, for compatibility
		//with other codes ^^'
		//This makes the range be [0,1] instead of (the probably better,) [0,1)   ^^'
		return i == 255 ? 1d : i / 256d;
	}
	
	
	
	
	public static int getDistinctiveColorCodeRGB32_Periodic(int index)
	{
		return (index*(45605197)) & 0x00FFFFFF;
	}
	
	public static Color getDistinctiveColor_Periodic(int index)
	{
		return new Color(getDistinctiveColorCodeRGB32_Periodic(index));
	}
	
	
	
	
	
	public static Color hsbadd(Color c, double addToHue, double addToSaturation, double addToBrightness)
	{
		return hsbmuladd(c, addToHue, addToSaturation, addToBrightness, 1, 1, 1);
	}
	
	public static Color hsbmul(Color c, double multiplyToHue, double multiplyToSaturation, double multiplyToBrightness)
	{
		return hsbmuladd(c, 0, 0, 0, multiplyToHue, multiplyToSaturation, multiplyToBrightness);
	}
	
	public static Color hsbmuladd(Color c, double addToHue, double addToSaturation, double addToBrightness, double multiplyToHue, double multiplyToSaturation, double multiplyToBrightness)
	{
		double[] a = RGBobjectToHSBfloats(c);
		double h = SmallFloatMathUtilities.progmod(a[0]*multiplyToHue + addToHue, 1);  //wrap ;D
		double s = SmallFloatMathUtilities.greatest(0, SmallFloatMathUtilities.least(1, a[1]*multiplyToSaturation + addToSaturation));  //truncate :3
		double b = SmallFloatMathUtilities.greatest(0, SmallFloatMathUtilities.least(1, a[2]*multiplyToBrightness + addToBrightness));  //truncate :3
		return HSBfloatsToRGBobject(h, s, b);
	}
	
	//public static Color ahsbcol(double hue, double saturation, double brightness, double alpha)
	
	//public static Color ahsbcol(double hue, double saturation, double brightness, int alpha)
	
	
	
	
	public static Color setAlpha(Color c, double newAlpha)
	{
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), floatToInt8Bit(newAlpha));
	}
	
	
	
	
	
	
	//Commutative!
	public static Color comp(Color a, Color b)
	{
		int R = SmallIntegerMathUtilities.least(a.getRed() + b.getRed(), 255);
		int G = SmallIntegerMathUtilities.least(a.getGreen() + b.getGreen(), 255);
		int B = SmallIntegerMathUtilities.least(a.getBlue() + b.getBlue(), 255);  //hence the caps XD'
		return new Color(R, G, B);
	}
	
	//Commutative!
	public static Color comp(Color a, double aα, Color b, double bα)
	{
		int R = SmallIntegerMathUtilities.least((int)Math.round(a.getRed()*aα + b.getRed()*bα), 255);
		int G = SmallIntegerMathUtilities.least((int)Math.round(a.getGreen()*aα + b.getGreen()*bα), 255);
		int B = SmallIntegerMathUtilities.least((int)Math.round(a.getBlue()*aα + b.getBlue()*bα), 255);  //hence the caps XD'
		return new Color(R, G, B);
	}
	
	//NONcommutative!
	public static Color compOver(Color a, double aα, Color b, double bα)
	{
		//This is how normal codes do alpha compositing :>
		return comp(a, aα, b, bα * (1 - aα));
	}
	
	
	
	
	//Commutative!
	public static double[] physComp(double[] subtrativeTransmittivitiesA, double[] subtrativeTransmittivitiesB, double[] additiveBackLightingIntensities)
	{
		//This is how physics does alpha compositing ^_^  /shot XD    (eg, holding translucent tinted pieces of plastic over each other against a backlight ^w^ )
		double intensityOfWavelength0 = additiveBackLightingIntensities[0] * subtrativeTransmittivitiesA[0] * subtrativeTransmittivitiesB[0];
		double intensityOfWavelength1 = additiveBackLightingIntensities[1] * subtrativeTransmittivitiesA[1] * subtrativeTransmittivitiesB[1];
		double intensityOfWavelength2 = additiveBackLightingIntensities[2] * subtrativeTransmittivitiesA[2] * subtrativeTransmittivitiesB[2];
		return new double[]{intensityOfWavelength0, intensityOfWavelength1, intensityOfWavelength2};
	}
	//note: transmissivity = e ^ (-absorption coefficient * thickness of material)   ^w^
	
	
	//Commutative!
	public static Color physComp(Color subtrativeTransmittivitiesA, Color subtrativeTransmittivitiesB, Color additiveBackLightingIntensities)
	{
		return RGBfloatsToRGBobjectOptionalAlpha(physComp(RGBobjectToRGBfloats(subtrativeTransmittivitiesA), RGBobjectToRGBfloats(subtrativeTransmittivitiesB), RGBobjectToRGBfloats(additiveBackLightingIntensities)));
	}
	
	public static Color physCompWhiteBacklight(Color subtrativeTransmittivitiesA, Color subtrativeTransmittivitiesB)
	{
		return physComp(subtrativeTransmittivitiesA, subtrativeTransmittivitiesB, Color.white);
	}
	
	
	
	//Commutative!
	public static Color rgbavg(Color a, Color b)
	{
		int R = SmallIntegerMathUtilities.roundingIntegerDivision(a.getRed() + b.getRed(), 2);
		int G = SmallIntegerMathUtilities.roundingIntegerDivision(a.getGreen() + b.getGreen(), 2);
		int B = SmallIntegerMathUtilities.roundingIntegerDivision(a.getBlue() + b.getBlue(), 2); //hence the caps XD'
		return new Color(R, G, B);
	}
	
	//Commutative!
	public static Color hsbavg(Color a, Color b)
	{
		double[] A = RGBobjectToHSBfloats(a);
		double[] B = RGBobjectToHSBfloats(b);
		return HSBfloatsToRGBobject(SmallFloatMathUtilities.modularAverageNormalized(A[0], B[0]), (A[1]+B[1])/2d, (A[2]+B[2])/2d);
	}
	
	
	
	
	public static Color rgbinvert(Color c)
	{
		return new Color(255 - c.getRed(), 255 - c.getGreen(), 255 - c.getBlue());
	}
	
	
	
	
	
	
	
	public static enum ColorComponent
	{
		RED, GREEN, BLUE,
		HUE, SATURATION, BRIGHTNESS,
		//LIGHTNESS  (not supported by the Javas ._. )
	}
	
	public static double getComponentFloating(Color c, ColorComponent component)
	{
		if (component == ColorComponent.RED)
			return int8BitToDouble(c.getRed());
		else if (component == ColorComponent.GREEN)
			return int8BitToDouble(c.getGreen());
		else if (component == ColorComponent.BLUE)
			return int8BitToDouble(c.getBlue());
		
		else if (component == ColorComponent.HUE || component == ColorComponent.SATURATION || component == ColorComponent.BRIGHTNESS)
		{
			float[] hsb = new float[3];
			Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
			if (component == ColorComponent.HUE)
				return hsb[0];
			else if (component == ColorComponent.SATURATION)
				return hsb[1];
			else if (component == ColorComponent.BRIGHTNESS)
				return hsb[2];
			else
				throw new ImpossibleException();
		}
		
		else
			throw new IllegalArgumentException(ObjectUtilities.toStringNT(component));
	}
	
	public static Color deriveColorBySettingComponentFloating(Color c, ColorComponent component, double newValue)
	{
		if (component == ColorComponent.RED)
			return new Color(floatToInt8Bit(newValue), c.getGreen(), c.getBlue());
		else if (component == ColorComponent.GREEN)
			return new Color(c.getRed(), floatToInt8Bit(newValue), c.getBlue());
		else if (component == ColorComponent.BLUE)
			return new Color(c.getRed(), c.getGreen(), floatToInt8Bit(newValue));
		
		else if (component == ColorComponent.HUE || component == ColorComponent.SATURATION || component == ColorComponent.BRIGHTNESS)
		{
			float[] hsb = new float[3];
			Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), hsb);
			if (component == ColorComponent.HUE)
				hsb[0] = (float)newValue;
			else if (component == ColorComponent.SATURATION)
				hsb[1] = (float)newValue;
			else if (component == ColorComponent.BRIGHTNESS)
				hsb[2] = (float)newValue;
			else
				throw new ImpossibleException();
			return new Color(c.getAlpha() << 24 | Color.HSBtoRGB(hsb[0], hsb[1], hsb[2]), true);
		}
		
		else
			throw new IllegalArgumentException(ObjectUtilities.toStringNT(component));
	}
	
	public static Color deriveColorByMultiplyingComponent(Color c, ColorComponent component, double factor)
	{
		double originalComponent = getComponentFloating(c, component);
		double newValue = originalComponent * factor;
		return deriveColorBySettingComponentFloating(c, component, newValue);
	}
	
	//Todo ..add..(..., int), ..add..(..., double)
	
	
	
	
	
	
	public static Color parseHexColorWithPoundSign(String str)
	{
		return ReboundAccessibleRasterImagesAndJRESystems.RARItoJava2DColor(parseHexColorWithPoundSignToSC(str));
	}
	
	public static SimpleRGBAColor parseHexColorWithPoundSignToSC(String str)
	{
		String withoutPound = str.substring(1);
		return parseHexColorWithOUTPoundSignToSC(withoutPound);
	}
	
	
	
	public static Color parseHexColorWithOUTPoundSign(String str)
	{
		return ReboundAccessibleRasterImagesAndJRESystems.RARItoJava2DColor(parseHexColorWithOUTPoundSignToSC(str));
	}
	
	public static SimpleRGBAColor parseHexColorWithOUTPoundSignToSC(String withoutPound)
	{
		int rgbvalue = Integer.parseInt(withoutPound, 16);
		boolean hasAlpha = withoutPound.length() > 6;
		return new SimpleRGBAColor(rgbvalue, hasAlpha ? SimpleImageColorStorageType.TYPE_ARGB32 : SimpleImageColorStorageType.TYPE_RGB32);
	}
	
	
	public static Color parseRPStandardColorSyntax(String str) throws TextSyntaxException
	{
		return ReboundAccessibleRasterImagesAndJRESystems.RARItoJava2DColor(parseRPStandardColorSyntaxToSC(str));
	}
	
	public static SimpleRGBAColor parseRPStandardColorSyntaxToSC(String str) throws TextSyntaxException
	{
		try
		{
			if (str.startsWith("#"))
			{
				return parseHexColorWithPoundSignToSC(str);
			}
			else
			{
				String[] funk = StringUtilities.parseSimpleNonnestedFunctionInvocationExpression(str);
				
				if (funk != null)
				{
					String fname = funk[0];
					
					int numParameters = funk.length - 1;
					
					if (fname.equalsIgnoreCase("rgb"))
					{
						if (numParameters == 3 || numParameters == 4)
						{
							boolean hasAlpha = numParameters == 4;
							
							// 8bit color components depth :333
							
							int r8bit = Integer.parseInt(funk[1+0]);
							int g8bit = Integer.parseInt(funk[1+1]);
							int b8bit = Integer.parseInt(funk[1+2]);
							int a8bit = hasAlpha ? Integer.parseInt(funk[1+3]) : 0;
							
							return hasAlpha ? new SimpleRGBAColor(r8bit, g8bit, b8bit, a8bit) : new SimpleRGBAColor(r8bit, g8bit, b8bit);
						}
					}
					else if (fname.equalsIgnoreCase("hsb") || fname.equalsIgnoreCase("hsv"))
					{
						if (numParameters == 3 || numParameters == 4)
						{
							boolean hasAlpha = numParameters == 4;
							
							double h = Double.parseDouble(funk[1+0]);
							double s = Double.parseDouble(funk[1+1]);
							double v = Double.parseDouble(funk[1+2]);
							//double a = hasAlpha ? Double.parseDouble(funk[1+3]) : 0;
							int a8bit = hasAlpha ? Integer.parseInt(funk[1+3]) : 0;
							
							Color c = HSBfloatsToRGBobject(h / 360.0, s / 100.0, v / 100.0);
							
							if (hasAlpha)
							{
								// 8bit color components depth :333
								
								int r8bit = c.getRed();
								int g8bit = c.getGreen();
								int b8bit = c.getBlue();
								
								//	int a8bit = (int)Math.round(a * 255);
								//	if (a8bit < 0)
								//		a8bit = 0;
								//	else if (a8bit > 255)
								//		a8bit = 255;
								
								return hasAlpha ? new SimpleRGBAColor(r8bit, g8bit, b8bit, a8bit) : new SimpleRGBAColor(r8bit, g8bit, b8bit);
							}
							else
							{
								return ReboundAccessibleRasterImagesAndJRESystems.java2DtoRARIColor(c, false);
							}
						}
					}
					else if (fname.equalsIgnoreCase("hsl"))
					{
						throw TextSyntaxException.inst(new NotYetImplementedException("sorries .____."));
					}
				}
			}
			
			throw TextSyntaxException.inst("I dunknow what format it's in.. :/  "+StringUtilities.repr(str));
		}
		catch (NumberFormatException exc)
		{
			throw TextSyntaxException.inst(exc);
		}
	}
	
	
	
	
	
	
	//Todo versions for SimpleRGBAColor and etc. :>
	
	public static String formatColorHexWithoutPoundSign(Color c)
	{
		return StringUtilities.getSimpleLeadingZeroPaddedIntegerRepresentation(c.getRGB() & 0x00FFFFFF, 6, 16).toUpperCase();
	}
	
	
	public static String formatColorHexWithPoundSign(Color c)
	{
		return '#' + formatColorHexWithoutPoundSign(c);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static int ARGBtoABGR(int argb)
	{
		return reverseLowThreeBytesInInt32(argb);
	}
	
	public static int ABGRtoARGB(int abgr)
	{
		return reverseLowThreeBytesInInt32(abgr);
	}
	
	public static int reverseLowThreeBytesInInt32(int x)
	{
		return
		((x & 0x000000FF) << 16) |
		((x & 0x0000FF00)) |
		((x & 0x00FF0000) >> 16) |
		((x & 0xFF000000));
	}
	
	
	
	
	public static int rgb(int r, int g, int b)
	{
		return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}
	
	public static int argb(int a, int r, int g, int b)
	{
		return ((a & 0xFF) << 24) | ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | (b & 0xFF);
	}
	
	
	
	public static int bgr(int r, int g, int b)
	{
		return ((r & 0xFF) << 0) | ((g & 0xFF) << 8) | ((b & 0xFF) << 16);
	}
	
	public static int abgr(int r, int g, int b, int a)
	{
		return ((r & 0xFF) << 0) | ((g & 0xFF) << 8) | ((b & 0xFF) << 16) | ((a & 0xFF) << 24);
	}
	
	
	
	
	
	
	
	/**
	 * Note: all values musht be normalized from [0,1] ^_^
	 */
	public static Color HSBfloatsToRGBobject(double hue, double saturation, double brightness)
	{
		return new Color(Color.HSBtoRGB((float)hue, (float)saturation, (float)brightness));
	}
	
	/**
	 * @return three values: Hue, Saturation, Brightness/Value; all in [0,1)  ^_^
	 */
	public static double[] RGBobjectToHSBfloats(Color c)
	{
		float[] a = new float[3];
		Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), a);
		return new double[]{a[0], a[1], a[2]};
	}
	
	public static double[] RGBobjectToRGBfloats(Color c)
	{
		return new double[]{int8BitToDouble(c.getRed()), int8BitToDouble(c.getGreen()), int8BitToDouble(c.getBlue())};
	}
	
	/**
	 * Values nominally in the domain [0, 1), but 1 is accepted as a shorthand/special-value for maximum-value :3
	 */
	public static Color RGBfloatsToRGBobjectOptionalAlpha(double... rgbfloats)
	{
		if (rgbfloats.length == 3)
			return new Color(floatToInt8Bit(rgbfloats[0]), floatToInt8Bit(rgbfloats[1]), floatToInt8Bit(rgbfloats[2]));
		else if (rgbfloats.length == 4)
			return new Color(floatToInt8Bit(rgbfloats[0]), floatToInt8Bit(rgbfloats[1]), floatToInt8Bit(rgbfloats[2]), floatToInt8Bit(rgbfloats[3]));
		else
			throw new IllegalArgumentException();
	}
	
	/**
	 * Values in range [0, 256)  :>
	 */
	public static Color RGBintsToRGBobject(int r, int g, int b)
	{
		return new Color(r, g, b);
	}
	
	
	
	
	
	
	
	
	/*
	 * Adapted from java.awt.Color :3
	 */
	public static double[] HSBfloatsToRGBfloats(double hue, double saturation, double brightness)
	{
		double r = 0, g = 0, b = 0;
		
		if (saturation == 0)
		{
			r = g = b = brightness;
		}
		else
		{
			double h = (hue - Math.floor(hue)) * 6.0;
			double f = h - Math.floor(h);
			double p = brightness * (1.0 - saturation);
			double q = brightness * (1.0 - saturation * f);
			double t = brightness * (1.0 - (saturation * (1.0 - f)));
			switch ((int) h)
			{
				case 0:
					r = brightness;
					g = t;
					b = p;
					break;
				case 1:
					r = q;
					g = brightness;
					b = p;
					break;
				case 2:
					r = p;
					g = brightness;
					b = t;
					break;
				case 3:
					r = p;
					g = q;
					b = brightness;
					break;
				case 4:
					r = t;
					g = p;
					b = brightness;
					break;
				case 5:
					r = brightness;
					g = p;
					b = q;
					break;
			}
		}
		
		return new double[]{r, g, b};
	}
	
	
	
	public static String sicstToString(SimpleImageColorStorageType pixelFormat)
	{
		return "Color format: {"+pixelFormat.getBits()+" bits; R="+Integer.toBinaryString(pixelFormat.getRedMask())+
		", G="+Integer.toBinaryString(pixelFormat.getGreenMask())+
		", B="+Integer.toBinaryString(pixelFormat.getBlueMask())+
		", A="+Integer.toBinaryString(pixelFormat.getAlphaMask())+
		"}";
	}
	
	
	
	public static String rariColorToString(SimpleRGBAColor rariColor)
	{
		SimpleRGBAColor c = rariColor;
		SimpleImageColorStorageType format = c.getFormat();
		
		
		return
		"RGB Color [" +
		
			(
			c.hasAlpha() ?
			
			(
			"R="+c.getRed()+"/"+format.getRedMaxValue() + ", " +
			"G="+c.getGreen()+"/"+format.getGreenMaxValue() + ", " +
			"B="+c.getBlue()+"/"+format.getBlueMaxValue() + ", " +
			"A="+c.getAlpha()+"/"+format.getAlphaMaxValue()
			)
			
			:
				
				(
				"R="+c.getRed()+"/"+format.getRedMaxValue() + ", " +
				"G="+c.getGreen()+"/"+format.getGreenMaxValue() + ", " +
				"B="+c.getBlue()+"/"+format.getBlueMaxValue() + ", "
				)
				
			) +
			
			"]";
	}
}
