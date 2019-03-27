package rebound.io;

import static rebound.util.collections.CollectionUtilities.*;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.util.collections.ArrayListWithIdentity;
import rebound.util.collections.HashSetWithIdentity;
import rebound.util.collections.SimpleTable;
import rebound.util.collections.maps.HashMapWithIdentity;
import rebound.util.functional.FunctionInterfaces.BinaryProcedure;
import rebound.util.objectutil.JavaNamespace;

public class StandardExternalizationFormats
implements JavaNamespace
{
	public static void checkExternalizedFormatVersion(int actualVersionNumber, int expectedVersionNumber) throws IOException
	{
		if (actualVersionNumber != expectedVersionNumber)
			//Note that it's not the worst thing in the world :)
			//With custom-done externalization format versions, you can just add code to handle the other version, unlike serialVersionUID!
			throw new InvalidExternalizationFormatDeserializationException("Got version "+actualVersionNumber+", Expected version "+expectedVersionNumber+"  xP");
	}
	
	public static class InvalidExternalizationFormatDeserializationException
	extends IOException
	{
		private static final long serialVersionUID = 1L;
		
		
		public InvalidExternalizationFormatDeserializationException()
		{
			super();
		}
		
		public InvalidExternalizationFormatDeserializationException(String message, Throwable cause)
		{
			super(message, cause);
		}
		
		public InvalidExternalizationFormatDeserializationException(String message)
		{
			super(message);
		}
		
		public InvalidExternalizationFormatDeserializationException(Throwable cause)
		{
			super(cause);
		}
	}
	
	
	
	
	
	
	
	
	
	public static void writeExternalList(@ReadonlyValue List list, ObjectOutput out) throws IOException
	{
		writeExternalCollectionUncompacted(list, out);
	}
	
	public static void readExternalList(@WritableValue List list, ObjectInput in) throws IOException, ClassNotFoundException
	{
		readExternalCollectionUncompacted(list, in);
	}
	
	
	public static void writeExternalSet(@ReadonlyValue Set set, ObjectOutput out) throws IOException
	{
		writeExternalCollectionUncompacted(set, out);
	}
	
	public static void readExternalSet(@WritableValue Set set, ObjectInput in) throws IOException, ClassNotFoundException
	{
		readExternalCollectionUncompacted(set, in);
	}
	
	
	public static void writeExternalCollectionUncompacted(@ReadonlyValue Collection collection, ObjectOutput out) throws IOException
	{
		Object[] array = collection.toArray();  //important if it's a weak collection! \o/
		int n = array.length;
		
		out.writeInt(n);
		
		for (int i = 0; i < n; i++)
			out.writeObject(array[i]);
	}
	
	public static void readExternalCollectionUncompacted(@WritableValue Collection collection, ObjectInput in) throws IOException, ClassNotFoundException
	{
		collection.clear();
		
		int size = in.readInt();
		
		if (size < 0)
			throw new IllegalArgumentException("Negative collection/list element count!!");
		
		for (int i = 0; i < size; i++)
			collection.add(in.readObject());
	}
	
	
	
	
	public static void writeExternalMap(@ReadonlyValue Map map, ObjectOutput out) throws IOException
	{
		Object[] entries = map.entrySet().toArray();  //important if it's a weak collection! \o/
		int n = entries.length;
		
		out.writeInt(n);
		
		for (int i = 0; i < n; i++)
		{
			Entry e = (Entry)entries[i];
			out.writeObject(e.getKey());
			out.writeObject(e.getValue());
		}
	}
	
	public static void readExternalMap(@WritableValue Map map, ObjectInput in) throws IOException, ClassNotFoundException
	{
		readExternalMap(map, in, map::put);
	}
	
	public static void readExternalMap(@WritableValue Map map, ObjectInput in, BinaryProcedure<Object, Object> put) throws IOException, ClassNotFoundException
	{
		map.clear();
		
		int size = in.readInt();
		
		if (size < 0)
			throw new IllegalArgumentException("Negative map entry count!!");
		
		for (int i = 0; i < size; i++)
			put.f(in.readObject(), in.readObject());
	}
	
	
	
	
	
	
	
	public static void writeExternalTable(@ReadonlyValue SimpleTable table, ObjectOutput out) throws IOException
	{
		out.writeInt(table.getNumberOfColumns());
		out.writeInt(table.getNumberOfRows());
		
		for (int r = 0; r < table.getNumberOfRows(); r++)
		{
			for (int c = 0; c < table.getNumberOfColumns(); c++)
			{
				out.writeObject(table.getCellContents(c, r));
			}
		}
	}
	
	public static void readExternalTable(@WritableValue SimpleTable table, ObjectInput in) throws IOException, ClassNotFoundException
	{
		int numberOfColumns = in.readInt();
		int numberOfRows = in.readInt();
		table.clearAndRedim(numberOfColumns, numberOfRows);
		
		for (int r = 0; r < numberOfRows; r++)
		{
			for (int c = 0; c < numberOfColumns; c++)
			{
				table.setCellContents(c, r, in.readObject());
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static List readExternalListToNewDefault(ObjectInput in) throws IOException, ClassNotFoundException
	{
		List l = new ArrayListWithIdentity();
		readExternalList(l, in);
		return l;
	}
	
	public static Set readExternalSetToNewDefault(ObjectInput in) throws IOException, ClassNotFoundException
	{
		Set s = new HashSetWithIdentity();
		readExternalSet(s, in);
		return s;
	}
	
	public static Collection readExternalCollectionUncompactedToNewDefault(ObjectInput in) throws IOException, ClassNotFoundException
	{
		Collection c = new ArrayListWithIdentity();
		readExternalCollectionUncompacted(c, in);
		return c;
	}
	
	
	public static Map readExternalMapToNewDefaultMap(ObjectInput in) throws IOException, ClassNotFoundException
	{
		Map m = new HashMapWithIdentity();
		readExternalMap(m, in);
		return m;
	}
	
	
	public static SimpleTable readExternalTableToNewDefault(ObjectInput in) throws IOException, ClassNotFoundException
	{
		SimpleTable table = newtableBlank();
		readExternalTable(table, in);
		return table;
	}
}
