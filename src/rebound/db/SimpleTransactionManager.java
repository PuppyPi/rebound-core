package rebound.db;

public interface SimpleTransactionManager
{
	/**
	 * It's called "commit" but really it means "end transaction" and applies even to read-only transactions as it must of course X3
	 * 
	 * If it fails you should still run {@link #rollback()}, but there's no guarantees that it WON'T be committed!!  It could go either way!
	 *  (Eg, if it finishes committing, but as the TCP packet or intra-machine inter-process communication system call indicating it successfully committed is on the way, the process gets killed or the network goes down or something!)
	 */
	public void commit() throws RuntimeException;
	
	public void rollback();
}
