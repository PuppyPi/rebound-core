package rebound.util.collections.prim;

import java.io.IOException;
import java.io.InputStream;
import rebound.annotations.semantic.SignalType;
import rebound.util.collections.maps.StreamableMap;

/**
 * @see StreamableMap
 * @see StreamableTextString
 */
@SignalType
public interface StreamableByteString
{
	public InputStream open() throws IOException;
}
