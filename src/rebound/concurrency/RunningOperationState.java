package rebound.concurrency;

/**
 * The Finite State Machine diagram that describes the monotonicity of this is:
 * 		• {@link #Unstarted} →
 * 			• {@link #Complete}
 * 			• {@link #CancelledBeforeStarting}.
 * 			• {@link #Running}
 * 				• {@link #CancelledWhileRunning}.
 * 				• {@link #Complete}.
 */
public enum RunningOperationState
{
	/**
	 * This is always the initial state (though of course it may stay in this state so briefly that you may never encounter it here XD ).
	 */
	Unstarted,
	
	/**
	 * This is a final state.  Once an operation is in this state, it is guaranteed to never go back to any other!
	 */
	CancelledBeforeStarting,
	
	Running,
	
	/**
	 * This is a final state.  Once an operation is in this state, it is guaranteed to never go back to any other!
	 */
	CancelledWhileRunning,
	
	/**
	 * This is a final state.  Once an operation is in this state, it is guaranteed to never go back to any other!
	 */
	Complete,
}
