/*
 * Created on Feb 6, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking.jre;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeMap;
import java.util.TreeSet;

public class BetterJREGlassbox
{
	public static final Class Type_Arrays_asList = Arrays.asList(new Object[0]).getClass();
	
	public static final Class Type_Collections_emptyEnumeration = Collections.emptyEnumeration().getClass();
	public static final Class Type_Collections_emptyIterator = Collections.emptyIterator().getClass();
	public static final Class Type_Collections_emptyList = Collections.emptyList().getClass();
	public static final Class Type_Collections_emptyListIterator = Collections.emptyListIterator().getClass();
	public static final Class Type_Collections_emptyMap = Collections.emptyMap().getClass();
	public static final Class Type_Collections_emptySet = Collections.emptySet().getClass();
	
	public static final Class Type_Collections_unmodifiableCollection = Collections.unmodifiableCollection(new ArrayList()).getClass();
	public static final Class Type_Collections_unmodifiableList = Collections.unmodifiableList(new ArrayList()).getClass();
	public static final Class Type_Collections_unmodifiableMap = Collections.unmodifiableMap(new HashMap()).getClass();
	public static final Class Type_Collections_unmodifiableSet = Collections.unmodifiableSet(new HashSet()).getClass();
	public static final Class Type_Collections_unmodifiableSortedMap = Collections.unmodifiableSortedMap(new TreeMap()).getClass();
	public static final Class Type_Collections_unmodifiableSortedSet = Collections.unmodifiableSortedSet(new TreeSet()).getClass();
}
