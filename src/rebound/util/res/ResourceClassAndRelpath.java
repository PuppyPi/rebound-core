package rebound.util.res;

import javax.annotation.concurrent.Immutable;
import rebound.annotations.semantic.operationspecification.HashableType;

@Immutable
@HashableType
public class ResourceClassAndRelpath
{
	protected final Class baseClass;
	protected final String pathRelativeToClassFile;
	
	public ResourceClassAndRelpath(Class baseClass, String pathRelativeToClassFile)
	{
		this.baseClass = baseClass;
		this.pathRelativeToClassFile = pathRelativeToClassFile;
	}
	
	public Class getBaseClass()
	{
		return this.baseClass;
	}
	
	public String getPathRelativeToClassFile()
	{
		return this.pathRelativeToClassFile;
	}
	
	@Override
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.baseClass == null) ? 0 : this.baseClass.hashCode());
		result = prime * result + ((this.pathRelativeToClassFile == null) ? 0 : this.pathRelativeToClassFile.hashCode());
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
		ResourceClassAndRelpath other = (ResourceClassAndRelpath) obj;
		if (this.baseClass == null)
		{
			if (other.baseClass != null)
				return false;
		}
		else if (!this.baseClass.equals(other.baseClass))
			return false;
		if (this.pathRelativeToClassFile == null)
		{
			if (other.pathRelativeToClassFile != null)
				return false;
		}
		else if (!this.pathRelativeToClassFile.equals(other.pathRelativeToClassFile))
			return false;
		return true;
	}
	
	@Override
	public String toString()
	{
		return "ResourceClassAndRelpath [c=" + this.baseClass + ", pathRelativeToClassFile=" + this.pathRelativeToClassFile + "]";
	}
}
