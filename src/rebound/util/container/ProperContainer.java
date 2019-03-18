/*
 * Created on May 19, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.container;

import rebound.util.objectutil.Copyable;
import rebound.util.objectutil.Instantiator;
import rebound.util.objectutil.PubliclyCloneable;

//Todo one for 'any' (has nine fields and a 'type' field)

public interface ProperContainer<ProperType extends ProperContainer<ProperType>>
extends PubliclyCloneable<ProperType>, Copyable, Instantiator<ProperType>
{
	//All the logic/api-specs of a container is defined in existing rebound interfaces (or Object)! XD   (we'll just copy it all here for clarity)
	
	
	//Super-important!
	@Override
	public boolean equals(Object other);
	
	
	//Less-important
	@Override
	public ProperType clone();
	
	@Override
	public int hashCode();
}
