package rebound.math.geom2d;

import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.math.geom2d.SmallIntegerBasicGeometry2D.*;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.math.SmallIntegerMathUtilities;
import rebound.math.geom.ints.analogoustojavaawt.IntPoint;
import rebound.math.geom.ints.analogoustojavaawt.IntRectangle;

/**
 * Lossless 2D raster transforms form a Symmetry Group with eight elements :>
 * 
 * If you start with the standard 6 transforms (1 noop aka identity, 2 reflections, and 3 rotations), then you have to include two more for it to be complete and close up on itself ^_^   (though how you name them is arbitrary of courses :> )
 * 
 * Naming conventions here!:
 * 		Simple to name :33
 * 			Identity (Do Nothing XD ) = Rotate 0
 * 			Flip/Reflect X
 * 			Flip/Reflect Y
 * 			Rotate 90°
 * 			Rotate 270°
 * 
 * 		Multiple ways of thinking about itttt!! \:DD/
 * 			Rotate 180° = Flip X AND Y = Flip Both!!
 * 			Flip/Reflect about the Y=X diagonal line on a cartesian graph!! (I here call it the "cis" diagonal, since X and Y are the same sign!) = Swap Axes!!
 * 			Flip/Reflect about the Y=-X or -Y=X diagonal line on a cartesian graph!! (I here call it the "trans" diagonal, since X and Y are opposite signs and it doesn't matter which!! \:DD/ ) = Swap Axes then Flip Both = Flip Both then Swap Axes!!
 * 
 * Flip X then Rotate 90 = Flip Trans
 * Rotate 90 then Flip X = Flip Cis
 * Flip Y then Rotate 90 = Flip Cis
 * Rotate 90 then Flip Y = Flip Trans
 * 
 * Flip X then Rotate 270 = Flip Cis
 * Rotate 270 then Flip X = Flip Trans
 * Flip Y then Rotate 270 = Flip Trans
 * Rotate 270 then Flip Y = Flip Cis
 * 
 * + ...&etc.  There are of course 64 possible combinations XD   (but only 8 unique outputs!! :3333 )
 * 
 * + And of course, implicitly, negative rotations and rotations beyond 270° (3 quadrants) wrap around according to {@link SmallIntegerMathUtilities#progmod(int, int) translation-invariant modulo} 4   ^wwwwww^
 * + And of course reflections/flippings don't matter which "direction in 3D" you "flip" the "2D object" XDD :333
 * 
 * + Something interesting :333
 * 		You can describe all transformations in terms of Flips/Reflections (and No-Flip for identity XD ), but you can *not* describe them all in terms of Rotations!! \o/
 * 		Interesting :>>>
 * 
 * 
 * + Note that the naming scheme here is independent of the handedness of the coordinate system!
 * 		So forward/positive rotation will be counter-clockwise in Right-Handed and clockwise in Left-Handed!
 * 		And the Cis Axis is UpRight-DownLeft in Right-Handed and DownRight-UpLeft in Left-Handed!
 * 		And the Trans Axis is DownRight-UpLeft in Right-Handed and UpRight-DownLeft in Left-Handed!
 */
public enum LosslessRandRAffineTransform
{
	//THE ORDER IS IMPORTANT DO. NOT. MESS WITH IT!!!
	Identity,
	FlipX,
	FlipY,
	Rotate180akaFlipBoth,  //also Flip Both :33
	FlipCisAkaSwapAxes,  //also, FlipX then Rotate 90, and Swap Axes!!  \:DDD/
	Rotate270,
	Rotate90,
	FlipTrans,  //also, FlipX then Rotate 90, and Swap Axes + Rotate 180, and Swap Axes + Flip Both :DD
	;
	
	private static final int[] CompositionMatrix = {
		0,1,2,3,4,5,6,7,
		1,0,3,2,5,4,7,6,
		2,3,0,1,6,7,4,5,
		3,2,1,0,7,6,5,4,
		4,6,5,7,0,2,1,3,
		5,7,4,6,1,3,0,2,
		6,4,7,5,2,0,3,1,
		7,5,6,4,3,1,2,0,
	};
	
	
	
	
	//This form is nice because going from a translationless matrix to a translation-including matrix format is just as simple as concatenating or truncating the translation vector elements from the end!!  IN ANY NUMBER OF DIMENSIONSSS!! \:DDD/
	@ThrowAwayValue
	public byte[] getMatrixInRCFormCloning()
	{
		if (swapAxes())
			return new byte[]{0, invertInputX() ? (byte)-1 : (byte)1, invertInputY() ? (byte)-1 : (byte)1, 0};
		else
			return new byte[]{invertInputX() ? (byte)-1 : (byte)1, 0, 0, invertInputY() ? (byte)-1 : (byte)1};
	}
	
	
	
	
	public boolean invertInputX()
	{
		return (ordinal() & 0b001) != 0;
	}
	
	public boolean invertInputY()
	{
		return (ordinal() & 0b010) != 0;
	}
	
	
	public boolean swapAxes()
	{
		return (ordinal() & 0b100) != 0;
	}
	
	
	
	
	
	
	@ThrowAwayValue
	//Thank HEAVENS for inlining and compiler optimizationsssss!! XDDD :'DDD
	public IntPoint transformIntegerPointOP(@ReadonlyValue @SnapshotValue IntPoint input)
	{
		byte[] m = getMatrixInRCFormCloning();
		
		int x = input.x;
		int y = input.y;
		
		return ipoint(
		x * m[0] + y * m[2],
		x * m[1] + y * m[3]
		);
	}
	
	
	@ThrowAwayValue
	//Thank HEAVENS for inlining and compiler optimizationsssss!! XDDD :'DDD
	public IntPoint transformIntegerPointInRectangleOP(@ReadonlyValue @SnapshotValue IntPoint input, IntRectangle space)
	{
		int x = input.x - space.x;
		int y = input.y - space.y;
		
		IntPoint p = transformIntegerPointOP(ipoint(x, y));
		
		return ipoint(
		SmallIntegerMathUtilities.progmod(p.x, space.width) + space.x,
		SmallIntegerMathUtilities.progmod(p.y, space.height) + space.y
		);
	}
	
	
	
	
	//Todo public static LosslessRandRAffineTransform compose(LosslessRandRAffineTransform before, LosslessRandRAffineTransform after)!!  \:DDD/
	
	
	
	
	protected static final LosslessRandRAffineTransform[] Rotations = {
		Identity,
		Rotate90,
		Rotate180akaFlipBoth,
		Rotate270,
	};
	
	public static LosslessRandRAffineTransform getRotation(int quadrants)
	{
		quadrants = progmod(quadrants, 4);
		return Rotations[quadrants];
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private static final LosslessRandRAffineTransform[] Values = values();
	
	/**
	 * Null (on inputs) = Identity :3
	 */
	@Nonnull
	public static LosslessRandRAffineTransform compose(@Nullable LosslessRandRAffineTransform first, @Nullable LosslessRandRAffineTransform second)
	{
		if (first == null) first = Identity;
		if (second == null) second = Identity;
		
		int a = first.ordinal();
		int b = second.ordinal();
		
		final int n = 8;
		
		int i = a * n + b;
		
		return Values[CompositionMatrix[i]];
	}
}
