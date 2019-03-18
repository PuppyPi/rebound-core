package rebound.util.collections;

import java.util.List;

public interface ListWithSetSize<E>
extends List<E>
{
	/**
	 * @see CollectionUtilities#setListSize(List, int, Object)
	 */
	public void setSize(int newSize, E elementToAddIfGrowing);  //note that this might throw whatever exceptions add(elementToAddIfGrowing) would if we're growing it!! (eg, ClassCastException for Lists that take only certain types, such as primitive lists!!)  \o/
	
	/**
	 * Note that, if increasing, this adds UNDEFINED CONTENTS, NOT NULLS/ZEROS!!
	 */
	public void setSize(int newSize);
}
