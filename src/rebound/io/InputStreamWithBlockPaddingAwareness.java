package rebound.io;

import java.io.IOException;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.temporal.ConstantReturnValue;
import rebound.bits.DataEncodingUtilities;
import rebound.exceptions.BinarySyntaxIOException;
import rebound.io.util.JRECompatIOUtilities;

public abstract class InputStreamWithBlockPaddingAwareness
extends InputStreamWithFinalizationNeedsAwareness
{
	@ConstantReturnValue
	public abstract int getBlockSize();
	
	public abstract int getCurrentModulus();
	
	
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	public int getRemainingToBeBlockPadded()
	{
		int blockSize = this.getBlockSize();
		int currentModulus = this.getCurrentModulus();
		
		//return currentModulus == 0 ? 0 : blockSize - currentModulus;
		return (blockSize - currentModulus) % blockSize;
	}
	
	
	
	
	public abstract void skipPadding() throws IOException;
	
	@Override
	public void senderWillHaveFinalized() throws IOException
	{
		skipPadding();
	}
	
	
	
	
	
	
	
	
	
	
	public static abstract class DefaultInputStreamWithBlockPaddingAwareness
	extends InputStreamWithBlockPaddingAwareness
	{
		protected byte[] paddingSkipBuffer = null;
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void skipPadding() throws IOException
		{
			if (paddingSkipBuffer == null)
				paddingSkipBuffer = new byte[getBlockSize()];
			
			int n = getRemainingToBeBlockPadded();
			
			JRECompatIOUtilities.readFully(this, paddingSkipBuffer, 0, n);
			
			for (int i = 0; i < n; i++)
			{
				if (paddingSkipBuffer[i] != 0)
					throw BinarySyntaxIOException.inst("Expected "+n+" bytes of all zeros as padding, instead got: "+DataEncodingUtilities.encodeHex(paddingSkipBuffer, 0, n, DataEncodingUtilities.HEX_UPPERCASE, " "));
			}
		}
	}
}
