package rebound.io;

import java.io.IOException;
import rebound.annotations.hints.IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.hints.IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser;
import rebound.annotations.semantic.temporal.ConstantReturnValue;

public abstract class OutputStreamWithBlockPaddingAwareness
extends OutputStreamWithFinalizationNeedsAwareness
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
	
	
	
	
	public abstract void writeZeroPadding() throws IOException;
	
	
	
	@IntendedToOptionallyBeSubclassedImplementedOrOverriddenByApiUser
	@Override
	public void finalize() throws IOException
	{
		writeZeroPadding();
	}
	
	
	
	
	
	
	
	
	
	
	public static abstract class DefaultOutputStreamWithBlockPaddingAwareness
	extends OutputStreamWithBlockPaddingAwareness
	{
		protected byte[] zeroPaddingBuffer = null;
		
		
		@IntendedToNOTBeSubclassedImplementedOrOverriddenByApiUser
		@Override
		public void writeZeroPadding() throws IOException
		{
			if (zeroPaddingBuffer == null)
				zeroPaddingBuffer = new byte[getBlockSize()];
			
			this.write(zeroPaddingBuffer, 0, getRemainingToBeBlockPadded());
		}
	}
}
