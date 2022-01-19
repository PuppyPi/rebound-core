package rebound.db;

public interface RWTransactionManager
extends SimpleTransactionManager
{
	/**
	 * tl;dr — If the first action is a read but there *could* be writes later on, call this before the first read or else deadlocks may occur!!  (If the first action is always a write, then you don't need to call this)
	 * 
	 * Oftentimes a backing will consider readonly and readwrite transactions.
	 * If two readwrite transactions start, only one will begin.
	 * But if they both start out as readonly, then the system can't know ahead of time that they will become readwrite transactions and they'll cause a deadlock!!
	 * And some SQL systems that have this happen (in some configuration[s]) don't do deadlock detection so this is actually a serious problem in actual practice! (eg, in H2 when configured for full ACID).
	 * 
	 * So with this method, if your first action will be a read not a write but future actions *may* include at least one write, then you've *got* to call this method to be reliably ACID compliant!!
	 * (This is basically like doing a write that doest nothing, like SQL INSERTing then DELETEing a record..but this is way faster XD' )
	 */
	public void potentiallyWritableTransaction();
}
