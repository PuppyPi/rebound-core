/*
 * Created on Mar 11, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.classhacking.jre;

import static rebound.util.AngryReflectionUtility.*;
import rebound.util.objectutil.JavaNamespace;

public class JREGlassBox
implements JavaNamespace
{
	public static final CollectionsGlassBox Collections = new CollectionsGlassBox();
	
	public static class CollectionsGlassBox
	{
		protected CollectionsGlassBox() {}
		
		public static final Class UnmodifiableCollection = forNameMandatory("java.util.Collections$UnmodifiableCollection");
		public static final Class UnmodifiableSet = forNameMandatory("java.util.Collections$UnmodifiableSet");
		public static final Class UnmodifiableSortedSet = forNameMandatory("java.util.Collections$UnmodifiableSortedSet");
		public static final Class UnmodifiableList = forNameMandatory("java.util.Collections$UnmodifiableList");
		public static final Class UnmodifiableRandomAccessList = forNameMandatory("java.util.Collections$UnmodifiableRandomAccessList");
		
		public static final Class UnmodifiableMap = forNameMandatory("java.util.Collections$UnmodifiableMap");
		public static final Class UnmodifiableEntrySet = forNameMandatory("java.util.Collections$UnmodifiableMap$UnmodifiableEntrySet");
		//public static final Class UnmodifiableEntry = forNameMandatory("java.util.Collections$UnmodifiableMap$UnmodifiableEntry");   //not present in newer JDKs???
		public static final Class UnmodifiableSortedMap = forNameMandatory("java.util.Collections$UnmodifiableSortedMap");
		
		public static final Class SynchronizedCollection = forNameMandatory("java.util.Collections$SynchronizedCollection");
		public static final Class SynchronizedSet = forNameMandatory("java.util.Collections$SynchronizedSet");
		public static final Class SynchronizedSortedSet = forNameMandatory("java.util.Collections$SynchronizedSortedSet");
		public static final Class SynchronizedList = forNameMandatory("java.util.Collections$SynchronizedList");
		public static final Class SynchronizedRandomAccessList = forNameMandatory("java.util.Collections$SynchronizedRandomAccessList");
		
		public static final Class SynchronizedMap = forNameMandatory("java.util.Collections$SynchronizedMap");
		public static final Class SynchronizedSortedMap = forNameMandatory("java.util.Collections$SynchronizedSortedMap");
		
		public static final Class CheckedCollection = forNameMandatory("java.util.Collections$CheckedCollection");
		public static final Class CheckedSet = forNameMandatory("java.util.Collections$CheckedSet");
		public static final Class CheckedSortedSet = forNameMandatory("java.util.Collections$CheckedSortedSet");
		public static final Class CheckedList = forNameMandatory("java.util.Collections$CheckedList");
		public static final Class CheckedRandomAccessList = forNameMandatory("java.util.Collections$CheckedRandomAccessList");
		
		public static final Class CheckedMap = forNameMandatory("java.util.Collections$CheckedMap");
		public static final Class CheckedEntrySet = forNameMandatory("java.util.Collections$CheckedMap$CheckedEntrySet");
		//public static final Class CheckedEntry = forNameMandatory("java.util.Collections$CheckedMap$CheckedEntry");   //not present in newer JDKs???
		public static final Class CheckedSortedMap = forNameMandatory("java.util.Collections$CheckedSortedMap");
		
		public static final Class EmptyIterator = forNameMandatory("java.util.Collections$EmptyIterator");
		public static final Class EmptyListIterator = forNameMandatory("java.util.Collections$EmptyListIterator");
		public static final Class EmptyEnumeration = forNameMandatory("java.util.Collections$EmptyEnumeration");
		public static final Class EmptySet = forNameMandatory("java.util.Collections$EmptySet");
		public static final Class EmptyList = forNameMandatory("java.util.Collections$EmptyList");
		public static final Class EmptyMap = forNameMandatory("java.util.Collections$EmptyMap");
		
		public static final Class SingletonSet = forNameMandatory("java.util.Collections$SingletonSet");
		public static final Class SingletonList = forNameMandatory("java.util.Collections$SingletonList");
		public static final Class SingletonMap = forNameMandatory("java.util.Collections$SingletonMap");
		
		public static final Class CopiesList = forNameMandatory("java.util.Collections$CopiesList");
		public static final Class ReverseComparator = forNameMandatory("java.util.Collections$ReverseComparator");
		public static final Class ReverseComparator2 = forNameMandatory("java.util.Collections$ReverseComparator2");
		public static final Class SetFromMap = forNameMandatory("java.util.Collections$SetFromMap");
		public static final Class AsLIFOQueue = forNameMandatory("java.util.Collections$AsLIFOQueue");
	}
	
	
	
	
	
	
	
	
	
	
	
	public static final ArraysGlassBox Arrays = new ArraysGlassBox();
	
	public static class ArraysGlassBox
	{
		protected ArraysGlassBox() {}
		
		public static final Class LegacyMergeSort = forNameMandatory("java.util.Arrays$LegacyMergeSort");
		public static final Class ArrayList = forNameMandatory("java.util.Arrays$ArrayList");
	}
}
