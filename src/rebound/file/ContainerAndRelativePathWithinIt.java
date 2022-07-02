package rebound.file;

import static java.util.Objects.*;
import javax.annotation.Nonnull;
import rebound.annotations.semantic.operationspecification.HashableType;

@HashableType  //If C is!
public class ContainerAndRelativePathWithinIt<C>
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
		return "ContainerAndRelativePathWithinIt [containingNamespace=" + containingNamespace + ", relativePathWithinNamespace=" + relativePathWithinNamespace + "]";
	}
}
