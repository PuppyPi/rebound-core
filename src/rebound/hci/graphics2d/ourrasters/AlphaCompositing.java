package rebound.hci.graphics2d.ourrasters;

import rebound.util.objectutil.JavaNamespace;

public class AlphaCompositing
implements JavaNamespace
{
	public static int alphaCompositeAlphaComponent8bit(int bottomAlpha, int topAlpha)
	{
		//		//newAlphaN = topAlphaN  +  bottomAlphaN * (1 - topAlphaN);
		//		//newAlpha/255 = topAlpha/255  +  bottomAlpha/255 * (1 - topAlpha/255);
		//		//newAlpha = topAlpha  +  bottomAlpha * (1 - topAlpha/255);
		//		//newAlpha = topAlpha  +  bottomAlpha * (255 - topAlpha) / 255;
		//
		//		//newAlpha = (topAlpha * 255  +  bottomAlpha * (255 - topAlpha)) / 255;
		//
		//		//The bottom two are perfectly equivalent because the remainder that the division might chop off would only get added to topAlpha*255 ...which is a multiple of 255!! XDD, and thus won't have a remainder of its own, so the overall remainder in the numerator will be *identical* :D!!
		//		//The higher one is faster, so we use that ^^'
		//
		//		/*
		//		 * Property checklist!:
		//		 * 		Bottom Top     Output
		//		 * 		---------------------------------
		//		 * 		min    min     min (implied)
		//		 * 		min    b       b
		//		 * 		a      min     a
		//		 * 		MAX    *       MAX
		//		 * 		*      MAX     MAX
		//		 * 		MAX    MAX     MAX (implied)
		//		 *
		//		 * + It should be commutative!! \o/
		//		 */
		//
		//		return topAlpha  +  ((bottomAlpha * (255 - topAlpha)) / 255);
		
		
		
		//Edit: we analyzed it muchly 8>  XDDD
		int b = bottomAlpha;
		int t = topAlpha;
		
		return b + t - ((b * t) / 255);  //this is the one we called A3 :>>>
	}
	
	
	
	//newAlphaN = topAlphaN  +  bottomAlphaN * (1 - topAlphaN);
	//o = t  +  b * (1 - t);
	//o = t  +  b  -  b*t
	//o = b  +  t  -  b*t
	
	//O/255 = B/255 + T/255 - B/255*T/255
	//O = B + T - (B*T)/255
	//shoot, better make an A3!! XDD''
	//edit: GLAD I DID OR C2 WOULDN'T HAVE EXISTED!!
	// I wonder what else exists in Possibility that we never wrote down o,o
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static int alphaCompositeColorComponent8bit(int bottomComponent, int bottomAlpha, int topComponent, int topAlpha)
	{
		//		/*
		//		 * n = ( t*ta + b*ba*(1-ta) ) / na
		//		 *
		//		 * n = ( t*ta + b*ba*(1-ta) ) / (ta + ba*(1-ta))
		//		 *
		//		 * N/255 = ( T/255*Ta/255 + B/255*Ba/255*(1-Ta/255) ) / (Ta/255 + Ba/255*(1-Ta/255))
		//		 * N/255 = ( T*Ta/255 + B*Ba/255*(1-Ta/255) ) / (Ta + Ba*(1-Ta/255))
		//		 * N = ( T*Ta + B*Ba*(1-Ta/255) ) / (Ta + Ba*(1-Ta/255))
		//		 * N = ( T*Ta + B*Ba*(255-Ta)/255 ) / (Ta + Ba*(255-Ta)/255)
		//		 *
		//		 * N = ( (T*Ta*255 + B*Ba*(255-Ta))/255 ) / ((Ta*255 + Ba*(255-Ta))/255)
		//		 * N = ( T*Ta*255 + B*Ba*(255-Ta) ) / ( Ta*255 + Ba*(255-Ta) )
		//		 */
		//
		//		/*
		//		 * Property checklist!:
		//		 * 		+ If alpha == 0, color component should not affect the result!
		//		 * 		+ If top alpha == max, output color should = top color
		//		 * 		+ If top alpha == 0, output color should = bottom color
		//		 */
		//
		//		int n = topComponent * topAlpha  +  bottomComponent * bottomAlpha * (255 - topAlpha);
		//		int d = topAlpha  +  bottomAlpha * (255 - topAlpha);
		//
		//		return n / d;
		
		
		
		//Edit: we analyzed it muchly 8>  XDDD
		int B = bottomComponent;
		int Ba = bottomAlpha;
		int T = topComponent;
		int Ta = topAlpha;
		
		return ( T*Ta*255 + B*Ba*(255-Ta) ) / ( (Ba+Ta)*255 - Ba*Ta );  //this is the one we called C3 :>>>
	}
}
