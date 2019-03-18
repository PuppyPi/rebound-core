package rebound.util.collections;

@FunctionalInterface
public interface Mapper<InputType, OutputType>
{
	public OutputType f(InputType input) throws FilterAwayReturnPath;
}