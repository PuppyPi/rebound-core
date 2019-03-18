package rebound.hci.graphics2d.ourrasters;

public class SimpleImageColorStorageTypeNotSupportedException
extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	protected final SimpleImageColorStorageType simpleImageColorStorageType;
	
	public SimpleImageColorStorageTypeNotSupportedException(SimpleImageColorStorageType simpleImageColorStorageType)
	{
		super("SimpleImageColorStorageType not supported: "+simpleImageColorStorageType);
		this.simpleImageColorStorageType = simpleImageColorStorageType;
	}
	
	public SimpleImageColorStorageType getSimpleImageColorStorageType()
	{
		return this.simpleImageColorStorageType;
	}
}
