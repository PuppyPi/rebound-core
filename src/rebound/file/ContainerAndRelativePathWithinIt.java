package rebound.file;

import static java.util.Objects.*;
import static rebound.text.StringUtilities.*;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.operationspecification.HashableType;
import rebound.text.StringUtilities.Reprable;

@HashableType  //If C is!
public class ContainerAndRelativePathWithinIt<C>
implements Reprable
{
	protected final C containingNamespace;
	protected final String relativePathWithinNamespace;
	
	public ContainerAndRelativePathWithinIt(@Nonnull C containingNamespace, @Nonnull String relativePathWithinNamespace)
	{
		this.containingNamespace = requireNonNull(containingNamespace);
		this.relativePathWithinNamespace = requireNonNull(relativePathWithinNamespace);
	}
	
	
	@Nonnull
	public C getContainingNamespace()
	{
		return this.containingNamespace;
	}
	
	@Nonnull
	public String getRelativePathWithinNamespace()
	{
		return this.relativePathWithinNamespace;
	}
	
	
	
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((containingNamespace == null) ? 0 : containingNamespace.hashCode());
		result = prime * result + ((relativePathWithinNamespace == null) ? 0 : relativePathWithinNamespace.hashCode());
		return result;
	}
	
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ContainerAndRelativePathWithinIt other = (ContainerAndRelativePathWithinIt) obj;
		return equalsFields(other);
	}
	
	/**
	 * This way lets equals() be subclassed efficiently without having to rewrite this :3
	 */
	public boolean equalsFields(ContainerAndRelativePathWithinIt other)
	{
		if (containingNamespace == null)
		{
			if (other.containingNamespace != null)
				return false;
		}
		else if (!containingNamespace.equals(other.containingNamespace))
			return false;
		if (relativePathWithinNamespace == null)
		{
			if (other.relativePathWithinNamespace != null)
				return false;
		}
		else if (!relativePathWithinNamespace.equals(other.relativePathWithinNamespace))
			return false;
		return true;
	}
	
	
	@Override
	public String toString()
	{
		return containingNamespace + "/" + relativePathWithinNamespace;
	}
	
	@Override
	public String reprThis()
	{
		return "("+repr(containingNamespace) + ") / " + repr(relativePathWithinNamespace);
	}
}
