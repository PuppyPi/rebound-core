package rebound.util.uid;

public class ClassUIDNotFoundException
extends UIDNotFoundException
{
	private static final long serialVersionUID = 1L;
	
	protected final Class c;
	
	public ClassUIDNotFoundException(Class c)
	{
		this.c = c;
	}
	
	public Class getClassThatIsMissingItsUID()
	{
		return this.c;
	}
}
