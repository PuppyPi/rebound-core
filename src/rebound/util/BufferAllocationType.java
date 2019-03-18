package rebound.util;

import java.nio.Buffer;

public enum BufferAllocationType
{
	JAVAHEAP,
	PREFERABLY_DIRECT,
	NECESSARILY_DIRECT_POSSIBLYEXCESSSIZE_PAGEALIGNED_GARBAGECOLLECTED,  //ie, the normal Java NIO way! (which could end up allocating as much as 4KB
	NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_GARBAGECOLLECTED,  //ie, malloc + GC  ^www^
	
	NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED,  //ie, malloc XD
	;
	
	
	public boolean isDirect()
	{
		return this != JAVAHEAP;
	}
	
	public boolean isNecessarilyDirect()
	{
		return this != JAVAHEAP && this != PREFERABLY_DIRECT;
	}
	
	public boolean isGarbageCollected()
	{
		return this != NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED;
	}
	
	
	
	
	public static boolean isCompatibleForPassThroughAndCopyIsNotNeeded(BufferAllocationType sourceType, BufferAllocationType requestedOutputType)
	{
		//	if (sourceType.isGarbageCollected() != requestedOutputType.isGarbageCollected())
		//		return false;
		
		if (requestedOutputType == JAVAHEAP || requestedOutputType == NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_GARBAGECOLLECTED || requestedOutputType == NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED)
			return sourceType == requestedOutputType;
		
		if (requestedOutputType == PREFERABLY_DIRECT)
			return sourceType != NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_NOTGARBAGECOLLECTED;
		
		if (requestedOutputType == NECESSARILY_DIRECT_POSSIBLYEXCESSSIZE_PAGEALIGNED_GARBAGECOLLECTED)
			return sourceType == requestedOutputType || sourceType == NECESSARILY_DIRECT_EXACTSIZE_NOTPAGEALIGNED_GARBAGECOLLECTED;
		
		throw new AssertionError(requestedOutputType);
	}
	
	public static boolean isCompatibleForPassThroughAndCopyIsNotNeeded(Buffer source, BufferAllocationType requestedOutputType)
	{
		return isCompatibleForPassThroughAndCopyIsNotNeeded(PlatformNIOBufferUtilities.getBufferAllocationType(source), requestedOutputType);
	}
}