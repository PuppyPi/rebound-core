package rebound.util;

/**
 * @see Either
 */
public class SuccessOrFailure<S, F>
{
	public static <S, F> SuccessOrFailure<S, F> forSuccess(S value)
	{
		return new SuccessOrFailure<>(false, value);
	}
	
	public static <S, F> SuccessOrFailure<S, F> forFailure(F value)
	{
		return new SuccessOrFailure<>(true, value);
	}
	
	
	
	protected final boolean isSuccess;
	protected final Object value;
	
	protected SuccessOrFailure(boolean success, Object value)
	{
		this.isSuccess = success;
		this.value = value;
	}
	
	
	
	public boolean isSuccess()
	{
		return isSuccess;
	}
	
	public boolean isFailure()
	{
		return !isSuccess;
	}
	
	public Object getValue()
	{
		return value;
	}
	
	
	public S getValueIfSuccess() throws NotThisException
	{
		if (isFailure())
			throw new NotThisException();
		else
			return (S)getValue();
	}
	
	public F getValueIfFailure() throws NotThisException
	{
		if (isFailure())
			return (F)getValue();
		else
			throw new NotThisException();
	}
}
