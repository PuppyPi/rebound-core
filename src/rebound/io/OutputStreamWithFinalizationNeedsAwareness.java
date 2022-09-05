package rebound.io;

import java.io.IOException;
import java.io.OutputStream;
import rebound.annotations.semantic.temporal.ConstantReturnValue;

public abstract class OutputStreamWithFinalizationNeedsAwareness
extends OutputStream
{
	@ConstantReturnValue
	public abstract void finalize() throws IOException;
}
