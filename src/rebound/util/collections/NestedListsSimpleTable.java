package rebound.util.collections;

import static rebound.util.collections.CollectionUtilities.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.LiveValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.exceptions.NonrectangularException;
import rebound.util.objectutil.PubliclyCloneable;

public class NestedListsSimpleTable<E>
implements SimpleTable<E>, PubliclyCloneable<NestedListsSimpleTable<E>>
{
	protected List<List<E>> rows;
	
	public NestedListsSimpleTable(int numberOfInitialColumns, int numberOfInitialRows)
	{
		clearAndRedim(numberOfInitialColumns, numberOfInitialRows);
	}
	
	public NestedListsSimpleTable()
	{
		this(0, 0);
	}
	
	
	public NestedListsSimpleTable(@SnapshotValue @ReadonlyValue SimpleTable<E> otherTable)
	{
		this();
		setFrom(otherTable);
	}
	
	public NestedListsSimpleTable(@WritableValue @LiveValue List<List<E>> rows) throws NonrectangularException
	{
		if (rows.size() > 1)
		{
			int w0 = rows.get(0).size();
			
			Iterator<List<E>> i = rows.iterator();
			
			i.next();
			
			while (i.hasNext())
			{
				List<E> row = i.next();
				
				if (row.size() != w0)
					throw new NonrectangularException();
			}
		}
		
		
		this.rows = rows;
	}
	
	
	@Override
	public boolean isReadableTable()
	{
		return true;
	}
	
	@Override
	public boolean isWritableTable()
	{
		return true;
	}
	
	
	
	public void appendRowLIVE(List<E> row)
	{
		if (!this.isEmpty() && row.size() != this.getNumberOfColumns())
			throw new NonrectangularException();
		
		rows.add(row);
	}
	
	
	
	
	
	
	@Override
	public void redimToZeroByZeroErasingAllContents()
	{
		this.rows.clear();
	}
	
	@Override
	public void redimPossiblyWithoutClearing(int numberOfColumns, int numberOfRows)
	{
		this.rows = newFilledWithNulls(numberOfRows);
		
		for (int r = 0; r < numberOfRows; r++)
			this.rows.set(r, newFilledWithNulls(numberOfColumns));
	}
	
	@Override
	public void clearAndRedim(int numberOfColumns, int numberOfRows)
	{
		this.rows = newFilledWithNulls(numberOfRows);
		
		for (int r = 0; r < numberOfRows; r++)
			this.rows.set(r, newFilledWithNulls(numberOfColumns));
	}
	
	
	
	@Override
	public NestedListsSimpleTable<E> clone()
	{
		return new NestedListsSimpleTable<>(this);
	}
	
	
	
	
	@Override
	public int getNumberOfColumns()
	{
		//We make sure they never fall out of dimension ^wwwwwwwwwww^
		return this.rows.isEmpty() ? 0 : this.rows.get(0).size();
	}
	
	@Override
	public int getNumberOfRows()
	{
		return this.rows.size();
	}
	
	
	@Override
	public E getCellContents(int columnIndex, int rowIndex)
	{
		return this.rows.get(rowIndex).get(columnIndex);
	}
	
	@Override
	public void setCellContents(int columnIndex, int rowIndex, E newValue)
	{
		this.rows.get(rowIndex).set(columnIndex, newValue);
	}
	
	@Override
	public void deleteRow(int rowIndex)
	{
		this.rows.remove(rowIndex);
	}
	
	@Override
	public void insertEmptyRow(int insertionRowIndex)
	{
		if (isEmpty())
			throw new IllegalStateException("You have to redim a table fully when it's empty, because 0xH or Wx0 are degenerate invalid dimensions--they're different from 0x0 but still have 0 cells!! \\o/");
		
		this.rows.add(insertionRowIndex, newFilledWithNulls(getNumberOfColumns()));
	}
	
	
	@Override
	public void deleteColumn(int columnIndex)
	{
		for (int r = 0; r < getNumberOfRows(); r++)
		{
			this.rows.get(r).remove(columnIndex);
		}
	}
	
	@Override
	public void insertEmptyColumn(int insertionColumnIndex)
	{
		if (isEmpty())
			throw new IllegalStateException("You have to redim a table fully when it's empty, because 0xH or Wx0 are degenerate invalid dimensions--they're different from 0x0 but still have 0 cells!! \\o/");
		
		for (int r = 0; r < getNumberOfRows(); r++)
		{
			this.rows.get(r).add(insertionColumnIndex, null);
		}
	}
	
	
	
	/**
	 * Don't rely on this being live, since other impls or this in the future, may internally use lists whose sizes are not the actual width of the table!!
	 * (In that case, this method call will still work, it'll simply make a copy//snapshot of the table's external form!)
	 */
	@PossiblySnapshotPossiblyLiveValue
	@Override
	public List<List<E>> toListOfRowsPossiblyLive()
	{
		return this.rows;
	}
	
	@PossiblySnapshotPossiblyLiveValue
	@Override
	public List<E> rowToListPossiblyLive(int index)
	{
		return this.rows.get(index);
	}
	
	
	
	
	
	
	@Override
	public boolean equals(Object obj)
	{
		return obj instanceof SimpleTable && eqv(this, (SimpleTable)obj);
	}
	
	@Override
	public int hashCode()
	{
		return SimpleTable.defaultHashcodeOfContents(this);
	}
	
	
	
	
	
	
	
	protected static <D> List<D> newFilledWithNulls(int size)
	{
		List<D> list = new ArrayList<>(size);
		for (int i = 0; i < size; i++)
			list.add(null);
		return list;
	}
}
