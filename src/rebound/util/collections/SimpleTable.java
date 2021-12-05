package rebound.util.collections;

import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import javax.annotation.Nonnegative;
import javax.annotation.Nullable;
import rebound.annotations.hints.IntendedToBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.allowedoperations.ReadonlyValue;
import rebound.annotations.semantic.reachability.PossiblySnapshotPossiblyLiveValue;
import rebound.annotations.semantic.reachability.SnapshotValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.exceptions.NoSuchElementReturnPath;
import rebound.exceptions.NonrectangularException;
import rebound.exceptions.NotSupportedReturnPath;
import rebound.exceptions.NotYetImplementedException;
import rebound.util.functional.ContinueSignal;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;
import rebound.util.functional.FunctionInterfaces.UnaryProcedure;
import rebound.util.objectutil.Copyable;
import rebound.util.objectutil.Equivalenceable;

// ^wwwwwwwwwww^
public interface SimpleTable<E>
extends Copyable, Equivalenceable
{
	@Nonnegative
	public int getNumberOfColumns();
	
	@Nonnegative
	public int getNumberOfRows();
	
	
	public boolean isReadableTable();
	
	public boolean isWritableTable();
	
	
	
	public default int getNumberOfCells()
	{
		return getNumberOfColumns() * getNumberOfRows();
	}
	
	public default boolean isEmpty()
	{
		return getNumberOfCells() == 0;
	}
	
	
	
	
	
	@Nullable  //Iff the value put in was null!
	public E getCellContents(int columnIndex, int rowIndex) throws IndexOutOfBoundsException;
	public void setCellContents(int columnIndex, int rowIndex, @Nullable E newValue) throws IndexOutOfBoundsException;
	
	
	public default E getrpCellContents(int columnIndex, int rowIndex) throws NoSuchElementReturnPath
	{
		if (columnIndex < 0 || columnIndex >= getNumberOfColumns())
			throw NoSuchElementReturnPath.I;
		if (rowIndex < 0 || rowIndex >= getNumberOfRows())
			throw NoSuchElementReturnPath.I;
		return getCellContents(columnIndex, rowIndex);
	}
	
	
	
	
	public void deleteColumn(int columnIndex);
	public void deleteRow(int rowIndex);
	
	/**
	 * "Empty" means nulls if reference-backed and 0 or '\u0000' or 0.0 or false if primitive-backed.
	 */
	public void insertEmptyColumn(int insertionColumnIndex);
	
	/**
	 * "Empty" means nulls if reference-backed and 0 or '\u0000' or 0.0 or false if primitive-backed.
	 */
	public void insertEmptyRow(int insertionRowIndex);
	
	
	/**
	 * @see #insertEmptyColumn(int)
	 */
	public default void appendEmptyColumn()
	{
		insertEmptyColumn(getNumberOfColumns());
	}
	
	/**
	 * @see #insertEmptyRow(int)
	 */
	public default void appendEmptyRow()
	{
		insertEmptyRow(getNumberOfRows());
	}
	
	
	
	
	public default void insertNewColumn(int insertionColumnIndex, E valueForNewCells)
	{
		insertEmptyColumn(insertionColumnIndex);
		int nr = this.getNumberOfRows();  //can change from one loop iteration to another!
		for (int r = 0; r < nr; r++)
			this.setCellContents(insertionColumnIndex, r, valueForNewCells);
	}
	
	public default void insertNewRow(int insertionRowIndex, E valueForNewCells)
	{
		insertEmptyRow(insertionRowIndex);
		int nc = this.getNumberOfColumns();  //can change from one loop iteration to another!
		for (int c = 0; c < nc; c++)
			this.setCellContents(c, insertionRowIndex, valueForNewCells);
	}
	
	
	/**
	 * @see #insertNewColumn(int, Object)
	 */
	public default void appendNewColumn(E valueForNewCells)
	{
		insertNewColumn(getNumberOfColumns(), valueForNewCells);
	}
	
	/**
	 * @see #insertNewRow(int, Object)
	 */
	public default void appendNewRow(E valueForNewCells)
	{
		insertNewRow(getNumberOfRows(), valueForNewCells);
	}
	
	
	
	
	public void redimToZeroByZeroErasingAllContents();
	public void redimPossiblyWithoutClearing(int numberOfColumns, int numberOfRows);
	
	@IntendedToBeSubclassedImplementedOrOverriddenByApiUser
	public default void clearAndRedim(int numberOfColumns, int numberOfRows)
	{
		redimPossiblyWithoutClearing(numberOfColumns, numberOfRows);
		redimToZeroByZeroErasingAllContents();
	}
	
	
	
	
	public default void reorderColumn(int oldIndex, int newIndex)
	{
		defaultReorderColumn(oldIndex, newIndex);
	}
	
	public default void reorderRow(int oldIndex, int newIndex)
	{
		defaultReorderRow(oldIndex, newIndex);
	}
	
	
	
	public static void defaultReorderColumn(int oldIndex, int newIndex)
	{
		//TODO!
		throw new NotYetImplementedException();
	}
	
	public static void defaultReorderRow(int oldIndex, int newIndex)
	{
		//TODO!
		throw new NotYetImplementedException();
	}
	
	
	
	
	
	public static <E> void defaultCopyRegion(SimpleTable<E> source, int columnOffsetInSource, int rowOffsetInSource, SimpleTable<? super E> dest, int columnOffsetInDest, int rowOffsetInDest, int widthInColumns, int heightInRows)
	{
		for (int r = 0; r < heightInRows; r++)
		{
			for (int c = 0; c < widthInColumns; c++)
			{
				//Separate lines for separate line numbers in case one fails ^^'
				E v = source.getCellContents(c+columnOffsetInSource, r+rowOffsetInSource);
				dest.setCellContents(c+columnOffsetInDest, r+rowOffsetInDest, v);
			}
		}
	}
	
	
	
	
	
	
	
	/**
	 * REDIMS AS WELL!!!
	 */
	@Override
	public default void setFrom(@ReadonlyValue @SnapshotValue Object source) throws ClassCastException
	{
		defaultSetFrom(this, source);
	}
	
	/**
	 * REDIMS AS WELL!!!
	 */
	public static <E> void defaultSetFrom(SimpleTable<E> self, @ReadonlyValue @SnapshotValue Object source) throws ClassCastException
	{
		SimpleTable<E> sourceTable = (SimpleTable<E>) source;
		
		int w = sourceTable.getNumberOfColumns();
		int h = sourceTable.getNumberOfRows();
		
		self.clearAndRedim(w, h);
		
		defaultCopyRegion(sourceTable, 0, 0, self, 0, 0, w, h);
	}
	
	
	
	
	
	
	/**
	 * REDIMS AS WELL!!!
	 */
	public default void setFromListOfRows(@ReadonlyValue @SnapshotValue List<List<E>> rows) throws NonrectangularException
	{
		defaultSetFromListOfRows(this, rows);
	}
	
	/**
	 * REDIMS AS WELL!!!
	 */
	public static <E> void defaultSetFromListOfRows(SimpleTable<E> self, @ReadonlyValue @SnapshotValue List<List<E>> rows) throws NonrectangularException
	{
		if (rows.isEmpty())
			self.clearAndRedim(0, 0);
		else
		{
			int w = rows.get(0).size();
			
			self.clearAndRedim(w, rows.size());
			
			for (int r = 0; r < self.getNumberOfRows(); r++)
			{
				List<E> row = rows.get(r);
				
				if (row.size() != w)
					throw new NonrectangularException("The list-of-lists was not rectangular!  Not all rows were the same size!");
				
				for (int c = 0; c < self.getNumberOfColumns(); c++)
				{
					E cell = row.get(c);
					self.setCellContents(c, r, cell);
				}
			}
		}
	}
	
	
	
	
	
	/**
	 * REDIMS AS WELL!!!
	 */
	public default void setFromArrayOfArrays(@ReadonlyValue @SnapshotValue E[][] source) throws NonrectangularException
	{
		defaultSetFromArrayOfArrays(this, source);
	}
	
	/**
	 * REDIMS AS WELL!!!
	 */
	public static <E> void defaultSetFromArrayOfArrays(SimpleTable<E> self, @ReadonlyValue @SnapshotValue E[][] source) throws NonrectangularException
	{
		if (source.length == 0)
			self.clearAndRedim(0, 0);
		else
		{
			int w = source[0].length;
			
			self.clearAndRedim(w, source.length);
			
			for (int r = 0; r < self.getNumberOfRows(); r++)
			{
				E[] row = source[r];
				
				if (row.length != w)
					throw new NonrectangularException("The list-of-lists was not rectangular!  Not all rows were the same size!");
				
				for (int c = 0; c < self.getNumberOfColumns(); c++)
				{
					E cell = row[c];
					self.setCellContents(c, r, cell);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	public default void setRowFromList(int r, @ReadonlyValue @SnapshotValue List<E> source) throws NonrectangularException
	{
		defaultSetRowFromList(this, r, source);
	}
	
	public static <E> void defaultSetRowFromList(SimpleTable<E> self, int r, @ReadonlyValue @SnapshotValue List<E> source) throws NonrectangularException
	{
		int w = self.getNumberOfColumns();
		
		if (source.size() != w)
			throw new IllegalArgumentException("Wrong size");
		
		for (int c = 0; c < w; c++)
		{
			E cell = source.get(c);
			self.setCellContents(c, r, cell);
		}
	}
	
	
	
	
	
	public default void setRowFromArray(int r, @ReadonlyValue @SnapshotValue E... source) throws NonrectangularException
	{
		defaultSetRowFromArray(this, r, source);
	}
	
	public static <E> void defaultSetRowFromArray(SimpleTable<E> self, int r, @ReadonlyValue @SnapshotValue E... source) throws NonrectangularException
	{
		int w = self.getNumberOfColumns();
		
		if (source.length != w)
			throw new IllegalArgumentException("Wrong size");
		
		for (int c = 0; c < w; c++)
		{
			E cell = source[c];
			self.setCellContents(c, r, cell);
		}
	}
	
	
	
	
	
	
	
	
	
	public default void setColumnFromList(int r, @ReadonlyValue @SnapshotValue List<E> source) throws NonrectangularException
	{
		defaultSetColumnFromList(this, r, source);
	}
	
	public static <E> void defaultSetColumnFromList(SimpleTable<E> self, int c, @ReadonlyValue @SnapshotValue List<E> source) throws NonrectangularException
	{
		int h = self.getNumberOfRows();
		
		if (source.size() != h)
			throw new IllegalArgumentException("Wrong size");
		
		for (int r = 0; r < h; r++)
		{
			E cell = source.get(r);
			self.setCellContents(c, r, cell);
		}
	}
	
	
	
	
	
	public default void setColumnFromArray(int r, @ReadonlyValue @SnapshotValue E... source) throws NonrectangularException
	{
		defaultSetColumnFromArray(this, r, source);
	}
	
	public static <E> void defaultSetColumnFromArray(SimpleTable<E> self, int c, @ReadonlyValue @SnapshotValue E... source) throws NonrectangularException
	{
		int h = self.getNumberOfRows();
		
		if (source.length != h)
			throw new IllegalArgumentException("Wrong size");
		
		for (int r = 0; r < h; r++)
		{
			E cell = source[r];
			self.setCellContents(c, r, cell);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@ThrowAwayValue
	public default List<List<E>> toListOfRows()
	{
		return defaultToListOfRows(this);
	}
	
	@ThrowAwayValue
	public static <E> List<List<E>> defaultToListOfRows(SimpleTable<E> self)
	{
		List<List<E>> rows = new ArrayList<>();
		
		int h = self.getNumberOfRows();
		
		for (int r = 0; r < h; r++)
		{
			rows.add(self.rowToList(r));
		}
		
		return rows;
	}
	
	
	
	
	
	
	@ThrowAwayValue
	public default List<E> rowToList(int r)
	{
		return defaultRowToList(this, r);
	}
	
	@ThrowAwayValue
	public static <E> List<E> defaultRowToList(SimpleTable<E> self, int r)
	{
		int w = self.getNumberOfColumns();
		
		List<E> row = new ArrayList<>();
		
		for (int c = 0; c < w; c++)
		{
			E cell = self.getCellContents(c, r);
			
			row.add(cell);
		}
		
		return row;
	}
	
	
	
	
	
	
	@ThrowAwayValue
	public default List<E> columnToList(int c)
	{
		return defaultColumnToList(this, c);
	}
	
	@ThrowAwayValue
	public static <E> List<E> defaultColumnToList(SimpleTable<E> self, int c)
	{
		int h = self.getNumberOfRows();
		
		List<E> column = new ArrayList<>();
		
		for (int r = 0; r < h; r++)
		{
			E cell = self.getCellContents(c, r);
			
			column.add(cell);
		}
		
		return column;
	}
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue
	public default List<List<E>> toListOfRowsPossiblyLive()
	{
		return toListOfRows();
	}
	
	@PossiblySnapshotPossiblyLiveValue
	public default List<E> rowToListPossiblyLive(int index)
	{
		return rowToList(index);
	}
	
	
	
	
	
	
	
	
	
	public default boolean forAll(Predicate<E> predicate)
	{
		return forAll(predicate, 0, 0, this.getNumberOfColumns(), this.getNumberOfRows());
	}
	
	public default boolean forAny(Predicate<E> predicate)
	{
		return forAny(predicate, 0, 0, this.getNumberOfColumns(), this.getNumberOfRows());
	}
	
	
	
	public default boolean forAllInColumn(Predicate<E> predicate, int columnIndex)
	{
		return forAll(predicate, columnIndex, 0, 1, this.getNumberOfRows());
	}
	
	public default boolean forAnyInColumn(Predicate<E> predicate, int columnIndex)
	{
		return forAny(predicate, columnIndex, 0, 1, this.getNumberOfRows());
	}
	
	
	
	public default boolean forAllInRow(Predicate<E> predicate, int rowIndex)
	{
		return forAll(predicate, 0, rowIndex, this.getNumberOfColumns(), 1);
	}
	
	public default boolean forAnyInRow(Predicate<E> predicate, int rowIndex)
	{
		return forAny(predicate, 0, rowIndex, this.getNumberOfColumns(), 1);
	}
	
	
	
	
	
	
	
	public default boolean forAll(Predicate<E> predicate, int firstColumn, int firstRow, int numberOfColumns, int numberOfRows)
	{
		int pastLastColumn = firstColumn + numberOfColumns;
		int pastLastRow = firstRow + numberOfRows;
		
		for (int r = firstRow; r < pastLastRow; r++)
		{
			for (int c = firstColumn; c < pastLastColumn; c++)
			{
				if (!predicate.test(this.getCellContents(c, r)))
					return false;
			}
		}
		
		return true;
	}
	
	
	public default boolean forAny(Predicate<E> predicate, int firstColumn, int firstRow, int numberOfColumns, int numberOfRows)
	{
		int pastLastColumn = firstColumn + numberOfColumns;
		int pastLastRow = firstRow + numberOfRows;
		
		for (int r = firstRow; r < pastLastRow; r++)
		{
			for (int c = firstColumn; c < pastLastColumn; c++)
			{
				if (predicate.test(this.getCellContents(c, r)))
					return true;
			}
		}
		
		return false;
	}
	
	
	
	
	
	
	
	
	public default void mapInPlace(UnaryFunction<E, E> mapper)
	{
		int w = this.getNumberOfColumns();
		int h = this.getNumberOfRows();
		
		for (int r = 0; r < h; r++)
		{
			for (int c = 0; c < w; c++)
			{
				this.setCellContents(c, r, mapper.f(this.getCellContents(c, r)));
			}
		}
	}
	
	
	public default void apply(UnaryProcedure<E> observer)
	{
		applyStoppable(v ->
		{
			observer.f(v);
			return ContinueSignal.Continue;
		});
	}
	
	public default ContinueSignal applyStoppable(UnaryFunction<E, ContinueSignal> observer)
	{
		int w = this.getNumberOfColumns();
		int h = this.getNumberOfRows();
		
		for (int r = 0; r < h; r++)
		{
			for (int c = 0; c < w; c++)
			{
				ContinueSignal rv = observer.f(this.getCellContents(c, r));
				if (rv == ContinueSignal.Stop)
					return rv;
			}
		}
		
		return ContinueSignal.Continue;
	}
	
	
	
	
	
	public default void setAllToSameValue(E value)
	{
		setAllToSameValue(value, 0, 0, this.getNumberOfColumns(), this.getNumberOfRows());
	}
	
	public default void setAllToSameValue(E value, int firstColumn, int firstRow, int numberOfColumns, int numberOfRows)
	{
		int pastLastColumn = firstColumn + numberOfColumns;
		int pastLastRow = firstRow + numberOfRows;
		
		for (int r = firstRow; r < pastLastRow; r++)
		{
			for (int c = firstColumn; c < pastLastColumn; c++)
			{
				this.setCellContents(c, r, value);
			}
		}
	}
	
	
	
	
	
	
	
	
	
	@Override
	public default boolean equivalent(Object other) throws NotSupportedReturnPath
	{
		return other instanceof SimpleTable ? defaultEquivalent(this, (SimpleTable)other) : false;
	}
	
	@Override
	public default int hashCodeOfContents()
	{
		return defaultHashcodeOfContents(this);
	}
	
	
	public static boolean defaultEquivalent(SimpleTable<?> a, SimpleTable<?> b)
	{
		int w = a.getNumberOfColumns();
		int h = a.getNumberOfRows();
		
		if (b.getNumberOfColumns() != w || b.getNumberOfRows() != h)
			return false;
		
		return a.equalsRegion(b, 0, 0, w, h);
	}
	
	
	public static int defaultHashcodeOfContents(SimpleTable<?> t)
	{
		int hashCode = 1;
		{
			int w = t.getNumberOfColumns();
			int h = t.getNumberOfRows();
			
			for (int r = 0; r < h; r++)
			{
				for (int c = 0; c < w; c++)
				{
					Object e = t.getCellContents(c, r);
					
					hashCode = 31*hashCode + (e == null ? 0 : e.hashCode());
				}
			}
		}
		
		return hashCode;
	}
	
	
	public default boolean equalsRegion(SimpleTable<?> otherTable, int firstColumn, int firstRow, int numberOfColumns, int numberOfRows)
	{
		int pastLastColumn = firstColumn + numberOfColumns;
		int pastLastRow = firstRow + numberOfRows;
		
		for (int r = firstRow; r < pastLastRow; r++)
		{
			for (int c = firstColumn; c < pastLastColumn; c++)
			{
				if (!eq(this.getCellContents(c, r), otherTable.getCellContents(c, r)))
					return false;
			}
		}
		
		return true;
	}
	
	
	
	public default int indexOfInRow(E value, int row)
	{
		for (int i = 1; i < this.getNumberOfColumns(); i++)
			if (eq(this.getCellContents(i, row), value))
				return i;
		return -1;
	}
	
	public default int indexOfInColumn(String value, int column)
	{
		for (int i = 1; i < this.getNumberOfRows(); i++)
			if (eq(this.getCellContents(column, i), value))
				return i;
		return -1;
	}
}









/*
//TODO MERGE THIS WITH SIMPLETABLE<E>!!  \:DDD/
//Todo oh..we can just make multiple large fixed-size planets instead of individually-infinite PCG worlds for now! XDD'

public interface TwoDArray<E>
{
	@SignalInterface
	public static interface CompletelyFixedDimensionTwoDArray<E>
	extends TwoDArray<E>
	{
		@ThrowAwayValue
		public Rectangle getFixedDimensions();
	}
	
	
	@SignalInterface
	public static interface CompletelyVariableDimensionTwoDArray<E>
	extends TwoDArray<E>
	{
		/**
 * Every cell outside this rectangle will be null!
 * But there are no guarantees it can't be smaller! \o/
 * (that would be the axis-aligned rectangular "hull"!)
 * 
 * (Eg, this might be based on the chunks in a chunk-based illusory 2D array!
 * ..Some of the chunks all around the edges might just happen to contain all nulls though (making it equivalent to them being absent!)..so no guarantees XDD')
 * /
		@ThrowAwayValue
		public Rectangle getSomeKindOfQuickUpperBound();
	}
}
 */
