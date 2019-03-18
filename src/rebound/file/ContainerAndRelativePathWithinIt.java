package rebound.file;

import static java.util.Objects.*;
import javax.annotation.Nonnull;

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
}
