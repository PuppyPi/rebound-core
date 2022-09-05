package rebound.io;

import java.io.IOException;
import rebound.annotations.semantic.temporal.ConstantReturnValue;

public abstract class InputStreamWithFinalizationNeedsAwareness
extends AbstractInputStream
{
	@ConstantReturnValue
	public abstract void senderWillHaveFinalized() throws IOException;
}
