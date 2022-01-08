package rebound.db.sql;

import java.sql.SQLException;
import java.sql.SQLNonTransientConnectionException;
import java.sql.SQLTimeoutException;
import java.sql.SQLTransientException;
import rebound.db.TransientTransactionalExceptionType;
import rebound.exceptions.ClosedExceptionType;

public class UncheckedSQLException
extends RuntimeException
implements TransientTransactionalExceptionType, ClosedExceptionType
{
	private static final long serialVersionUID = 1l;
	
	public UncheckedSQLException(SQLException cause)
	{
		super(cause);
	}
	
	@Override
	public SQLException getCause()
	{
		return (SQLException)super.getCause();
	}
	
	
	
	@Override
	public boolean isTransientTransactionalExceptionType()
	{
		SQLException c = getCause();
		return TransientTransactionalExceptionType.is(c) || c instanceof SQLTransientException;
	}
	
	public boolean isDeadlockOrTimeout()
	{
		return getCause() instanceof SQLTimeoutException;
	}
	
	@Override
	public boolean isClosedDatabaseExceptionType()
	{
		SQLException c = getCause();
		return ClosedExceptionType.is(c) || c instanceof SQLNonTransientConnectionException;  //we'll just assume anything of this type is at least equivalent to the connection being closed (eg, TCP connection timeout)
	}
}
