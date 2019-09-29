package rebound.util.collections.prim;

import static java.util.Objects.*;
import static rebound.util.Primitives.*;
import static rebound.util.objectutil.ObjectUtilities.*;
import javax.annotation.Nonnull;
import rebound.exceptions.ReadonlyUnsupportedOperationException;
import rebound.util.collections.prim.PrimitiveCollections.CharacterList;

//TODO Test! :D
public class CharSequenceBackedReadonlyCharacterList
implements CharacterList
{
	protected final @Nonnull CharSequence underlying;
	
	public CharSequenceBackedReadonlyCharacterList(@Nonnull CharSequence underlying)
	{
		this.underlying = requireNonNull(underlying);
	}
	
	
	
	
	@Override
	public CharacterList clone()
	{
		if (isTrueAndNotNull(isThreadUnsafelyImmutable(underlying)))
			return this;
		else
			return new CharSequenceBackedReadonlyCharacterList(attemptClone(this.underlying));
	}
	
	@Override
	public int size()
	{
		return underlying.length();
	}
	
	@Override
	public CharacterList subList(int fromIndex, int toIndex)
	{
		return fromIndex == 0 && toIndex == this.size() ? this : new CharSequenceBackedReadonlyCharacterList(underlying.subSequence(fromIndex, toIndex));
	}
	
	@Override
	public char getChar(int index)
	{
		return underlying.charAt(index);
	}
	
	
	
	
	
	
	
	@Override
	public void clear()
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void setChar(int index, char value)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void removeRange(int start, int pastEnd) throws IndexOutOfBoundsException
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void insertChar(int index, char value)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
	
	@Override
	public void setSizeChar(int newSize, char elementToAddIfGrowing)
	{
		throw new ReadonlyUnsupportedOperationException();
	}
}
