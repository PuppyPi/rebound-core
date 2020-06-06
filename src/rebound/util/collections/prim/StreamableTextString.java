package rebound.util.collections.prim;

import java.io.IOException;
import java.io.Reader;
import rebound.annotations.semantic.SignalType;
import rebound.util.collections.maps.StreamableMap;

/**
 * @see StreamableMap
 * @see StreamableByteString
 */
@SignalType
public interface StreamableTextString
{
	public Reader open() throws IOException;
}
