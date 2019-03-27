package rebound.hci.graphics2d.ourrasters;

import static java.lang.Math.*;
import static rebound.math.SmallFloatMathUtilities.*;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImage.AccessibleInterleavedRasterImageArray.AccessibleInterleavedRasterImageArrayInt;
import rebound.hci.graphics2d.ourrasters.AccessibleInterleavedRasterImageBackingAllocationType.ArrayAccessibleInterleavedRasterImageBackingAllocationType.ArrayIntAccessibleInterleavedRasterImageBackingAllocationType;
import rebound.math.SmallIntegerMathUtilities;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToInt;
import from.java.awt.ColorHSVRGBConversionCode;

public class ImageTinting
{
	public static enum TintingAlgorithm
	{
		RGBAdditive,
		HSVAdditive,
	}
	
	
	public static abstract class TintingOperation
	{
		@ThrowAwayValue
		public abstract AccessibleInterleavedRasterImage runOutOfPlace(@ReadonlyValue AccessibleInterleavedRasterImage input);
		
		public abstract SimpleRGBAColor tintColor(@ReadonlyValue SimpleRGBAColor input);
		
		
		
		public static class RGBAdditiveTintingOperation
		extends TintingOperation
		{
			protected final double rAdden, gAdden, bAdden;
			
			public RGBAdditiveTintingOperation(double rAdden, double gAdden, double bAdden)
			{
				this.rAdden = rAdden;
				this.gAdden = gAdden;
				this.bAdden = bAdden;
			}
			
			@ThrowAwayValue
			@Override
			public AccessibleInterleavedRasterImage runOutOfPlace(@ReadonlyValue AccessibleInterleavedRasterImage input)
			{
				return tintRGBAdditive(input, this.rAdden, this.gAdden, this.bAdden);
			}
			
			@Override
			public SimpleRGBAColor tintColor(@ReadonlyValue SimpleRGBAColor input)
			{
				return tintRGBAdditive(input, this.rAdden, this.gAdden, this.bAdden);
			}
			
			public double getRedAdden()
			{
				return this.rAdden;
			}
			
			public double getGreenAdden()
			{
				return this.gAdden;
			}
			
			public double getBlueAdden()
			{
				return this.bAdden;
			}
		}
		
		
		public static class HSVAdditiveTintingOperation
		extends TintingOperation
		{
			protected final double hAdden, sAdden, vAdden;
			
			public HSVAdditiveTintingOperation(double hAdden, double sAdden, double vAdden)
			{
				this.hAdden = hAdden;
				this.sAdden = sAdden;
				this.vAdden = vAdden;
			}
			
			@ThrowAwayValue
			@Override
			public AccessibleInterleavedRasterImage runOutOfPlace(@ReadonlyValue AccessibleInterleavedRasterImage input)
			{
				return tintHSVAdditive(input, this.hAdden, this.sAdden, this.vAdden);
			}
			
			@Override
			public SimpleRGBAColor tintColor(@ReadonlyValue SimpleRGBAColor input)
			{
				return tintHSVAdditive(input, this.hAdden, this.sAdden, this.vAdden);
			}
			
			public double getHueAdden()
			{
				return this.hAdden;
			}
			
			public double getSaturationAdden()
			{
				return this.sAdden;
			}
			
			public double getValueAdden()
			{
				return this.vAdden;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param rAdden [0, 255]
	 * @param gAdden [0, 255]
	 * @param bAdden [0, 255]
	 */
	@ThrowAwayValue
	public static AccessibleInterleavedRasterImage tintRGBAdditive(@ReadonlyValue AccessibleInterleavedRasterImage original, double rAdden, double gAdden, double bAdden)
	{
		SimpleImageColorStorageType operatingPixelFormat = original.getPixelFormat().hasAlpha() ? SimpleImageColorStorageType.TYPE_ABGR32 : SimpleImageColorStorageType.TYPE_BGR32;
		
		return perPixelOp(original, pixelValue -> tintRGBAdditive(operatingPixelFormat, pixelValue, rAdden, gAdden, bAdden), operatingPixelFormat);
	}
	
	public static SimpleRGBAColor tintRGBAdditive(@ReadonlyValue SimpleRGBAColor original, double rAdden, double gAdden, double bAdden)
	{
		return new SimpleRGBAColor(tintRGBAdditive(original.getFormat(), original.getPackedValue(), rAdden, gAdden, bAdden), original.getFormat());
	}
	
	public static int tintRGBAdditive(@ReadonlyValue SimpleImageColorStorageType operatingPixelFormat, int inputPixelValue, double rAdden, double gAdden, double bAdden)
	{
		int[] components = operatingPixelFormat.unpackRGB(inputPixelValue);
		components[0] = SmallIntegerMathUtilities.truncate((int)round(components[0] + (rAdden * 255d)), 0, 255);
		components[1] = SmallIntegerMathUtilities.truncate((int)round(components[1] + (gAdden * 255d)), 0, 255);
		components[2] = SmallIntegerMathUtilities.truncate((int)round(components[2] + (bAdden * 255d)), 0, 255);
		return operatingPixelFormat.packRGB(components);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @param hAdden [0, 1]
	 * @param sAdden [0, 1]
	 * @param vAdden [0, 1]
	 */
	@ThrowAwayValue
	public static AccessibleInterleavedRasterImage tintHSVAdditive(@ReadonlyValue AccessibleInterleavedRasterImage original, double hAdden, double sAdden, double vAdden)
	{
		SimpleImageColorStorageType operatingPixelFormat = original.getPixelFormat().hasAlpha() ? SimpleImageColorStorageType.TYPE_ABGR32 : SimpleImageColorStorageType.TYPE_BGR32;
		
		return perPixelOp(original, pixelValue -> tintHSVAdditive(operatingPixelFormat, pixelValue, hAdden, sAdden, vAdden), operatingPixelFormat);
	}
	
	public static SimpleRGBAColor tintHSVAdditive(@ReadonlyValue SimpleRGBAColor original, double hAdden, double sAdden, double vAdden)
	{
		return new SimpleRGBAColor(tintHSVAdditive(original.getFormat(), original.getPackedValue(), hAdden, sAdden, vAdden), original.getFormat());
	}
	
	public static int tintHSVAdditive(@ReadonlyValue SimpleImageColorStorageType operatingPixelFormat, int inputPixelValue, double hAdden, double sAdden, double vAdden)
	{
		int[] components = operatingPixelFormat.unpackRGB(inputPixelValue);
		
		int r = components[0];
		int g = components[1];
		int b = components[2];
		
		double h, s, v;
		{
			float[] hsbvals = new float[3];
			ColorHSVRGBConversionCode.RGBtoHSB(r, g, b, hsbvals);
			h = hsbvals[0];
			s = hsbvals[1];
			v = hsbvals[2];
		}
		
		h = truncate(h + hAdden, 0, 1);
		s = truncate(s + sAdden, 0, 1);
		v = truncate(v + vAdden, 0, 1);
		
		int p = SimpleImageColorStorageType.convertARGB32beToABGR32be(ColorHSVRGBConversionCode.HSVtoRGB((float)h, (float)s, (float)v) & 0x00FFFFFF);
		
		if (operatingPixelFormat.hasAlpha())
		{
			int a = components[3];
			
			//Merge the RGB and A
			p |= (a & 0xFF) << 24;
		}
		
		return p;
	}
	
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public static AccessibleInterleavedRasterImage perPixelOp(@ReadonlyValue AccessibleInterleavedRasterImage original, UnaryFunctionIntToInt pixelOperation, SimpleImageColorStorageType operatingPixelFormat)
	{
		AccessibleInterleavedRasterImageArrayInt image = (AccessibleInterleavedRasterImageArrayInt) AccessibleRasterImages.convertToNew(original, operatingPixelFormat, ArrayIntAccessibleInterleavedRasterImageBackingAllocationType.I);
		
		int[] imageData = image.getUnderlyingBackingArrayInt();
		int n = imageData.length;
		
		
		//DOO EEET /:DD/
		for (int i = 0; i < n; i++)
			imageData[i] = pixelOperation.f(imageData[i]);
		
		
		return image;
	}
}
