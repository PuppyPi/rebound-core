package rebound.file;

import java.io.File;
import javax.annotation.Nonnull;

public class ContainingFolderAndRelativePathWithinIt
extends ContainerAndRelativePathWithinIt<File>
{
	public ContainingFolderAndRelativePathWithinIt(File containingNamespace, String relativePathWithinNamespace)
	{
		super(containingNamespace, relativePathWithinNamespace);
	}
	
	
	
	
	
	protected transient File actualFileCache;
	
	@Nonnull
	public File getActualFile()
	{
		if (this.actualFileCache == null)
			this.actualFileCache = new File(getContainingNamespace(), getRelativePathWithinNamespace());
		return this.actualFileCache;
	}
}
