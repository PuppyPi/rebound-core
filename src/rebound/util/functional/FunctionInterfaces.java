/*
 * Created on Jan 12, 2014
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.functional;

import rebound.annotations.semantic.AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse;
import rebound.util.objectutil.JavaNamespace;

//TODO Passthrough's for source type = return type :>

//Todo Make Abstract classes for each of these to easily implement ProvidesPrimaryMethodHandle :>!
//			+ Less necessary with Java 8 lambdas :>

@AccessedDynamicallyOrExternallyToJavaOrKnownToBeInImportantSerializedUse
public class FunctionInterfaces
implements JavaNamespace
{
	//public static final Runnable NullaryProcedureNoop = () -> {};
	//public static final MethodHandle Runnable_run = lookupInstanceNonoverloadedMethod(Runnable.class, "run");
	
	
	
	@FunctionalInterface
	public static interface VariadicFunction<Input, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunction.class, "f");
		public Output f(Input... input);
		
		//public static final VariadicFunction<?, ?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface VariadicProcedure<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunction.class, "f");
		public void f(Input... input);
		
		//public static final VariadicProcedure<?, ?> Noop = (input) -> null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp

	public static interface _$$Prim$$_EqualityComparator
	{
		public boolean _$$prim$$_sEqual(_$$prim$$_ a, _$$prim$$_ b);
	}

	public static interface _$$Prim$$_Comparator
	{
		public int compare_$$Prim$$_s(_$$prim$$_ a, _$$prim$$_ b);
	}
	
	 */
	
	public static interface BooleanEqualityComparator
	{
		public boolean booleansEqual(boolean a, boolean b);
	}
	
	public static interface BooleanComparator
	{
		public int compareBooleans(boolean a, boolean b);
	}
	
	
	public static interface ByteEqualityComparator
	{
		public boolean bytesEqual(byte a, byte b);
	}
	
	public static interface ByteComparator
	{
		public int compareBytes(byte a, byte b);
	}
	
	
	public static interface CharEqualityComparator
	{
		public boolean charsEqual(char a, char b);
	}
	
	public static interface CharComparator
	{
		public int compareChars(char a, char b);
	}
	
	
	public static interface ShortEqualityComparator
	{
		public boolean shortsEqual(short a, short b);
	}
	
	public static interface ShortComparator
	{
		public int compareShorts(short a, short b);
	}
	
	
	public static interface FloatEqualityComparator
	{
		public boolean floatsEqual(float a, float b);
	}
	
	public static interface FloatComparator
	{
		public int compareFloats(float a, float b);
	}
	
	
	public static interface IntEqualityComparator
	{
		public boolean intsEqual(int a, int b);
	}
	
	public static interface IntComparator
	{
		public int compareInts(int a, int b);
	}
	
	
	public static interface DoubleEqualityComparator
	{
		public boolean doublesEqual(double a, double b);
	}
	
	public static interface DoubleComparator
	{
		public int compareDoubles(double a, double b);
	}
	
	
	public static interface LongEqualityComparator
	{
		public boolean longsEqual(long a, long b);
	}
	
	public static interface LongComparator
	{
		public int compareLongs(long a, long b);
	}
	
	
	//>>>
	
	
	
	
	
	
	//NullaryFunctionToObject = NullaryFunction  ^_^
	//UnaryFunctionObjectToObject = UnaryFunction  ^_^
	//BinaryFunctionObjectObjectToObject = BinaryFunction ^_^
	//TrinaryFunctionObjectObjectObjectToObject = TrinaryFunction  ^_^
	//...
	
	//NullaryProcedure = Runnable   YD
	//UnaryProcedureObject = UnaryProcedure  ^_^
	//BinaryProcedureObjectObject = BinaryProcedure  ^_^
	//TrinaryProcedureObjectObjectObject = TrinaryProcedure  ^_^
	//...
	
	
	
	
	/* <<<
python

primdata = primxp.AllPrimsAndObject;
prims = primxp.DefaultKeyOrdering;
primprims = set(prims); primprims.remove("Object");

explicitArityNames = ["Nullary", "Unary", "Binary", "Trinary", "Quaternary", "Quinary", "Senary", "Septenary", "Octary", "Nonary", "Decary"];

def getArityName(arity):
	if (arity < len(explicitArityNames)):
		return explicitArityNames[arity];
	else:
		return "Arity"+str(arity);


# Functions! \o/
class GenericTypeName(unicode):
	pass;

def genFunction(inputTypes, outputType, arityName, p=p):
	typename = arityName+("Function" if outputType != "void" else "Procedure");
	
	if (isany(lambda t: t != "Object" and t != "void", inputTypes+[outputType])):
		typename += concat(*map(capitalize, inputTypes))+("To"+capitalize(outputType) if outputType != "void" else "");
	
	genericizedInputTypes = map(lambda i: GenericTypeName("Input"+(str(i) if len(inputTypes) > 1 else "")) if inputTypes[i] == "Object" else inputTypes[i], xrange(len(inputTypes)));
	genericizedOutputType = GenericTypeName("Output") if outputType == "Object" else outputType;
	genericParameters = filter(lambda t: isinstance(t, GenericTypeName), genericizedInputTypes + [genericizedOutputType]);
	
	if (outputType == "void"):
		defaultValues = [];
	elif (outputType == "Object"):
		defaultValues = [("null", "Null")];
	elif (outputType == "boolean"):
		defaultValues = [("false", "False"), ("true", "True")];
	else:
		defaultValues = [(primdata[outputType]["primdef"], "Zero")];
	
	
	genericParametersString = "<"+(", ".join(genericParameters))+">" if isnotempty(genericParameters) else "";
	wildcardGenericParametersString = "<"+(", ".join(("?",) * len(genericParameters)))+">" if isnotempty(genericParameters) else "";
	lambdaParameters = ", ".join(map(lambda i: "input"+(str(i) if len(inputTypes) > 1 else ""), xrange(len(inputTypes))));
	
	p("""
@FunctionalInterface
public static interface """+typename+genericParametersString+"""
{
	//public static final MethodHandle f = lookupInstanceNonoverloadedMethod("""+typename+""".class, "f");
	public """+genericizedOutputType+" f("+(", ".join(map(lambda i: genericizedInputTypes[i]+" input"+(str(i) if len(inputTypes) > 1 else ""), xrange(len(inputTypes)))))+");\n");
	
	if (len(defaultValues) > 0):
		p("		\n");
		for valueLiteral, valueName in defaultValues:
			p("		//public static final "+typename+wildcardGenericParametersString+" Always"+valueName+" = ("+lambdaParameters+") -> "+valueLiteral+";\n");
	
	if (outputType == "void"):
		p("		\n");
		p("		//public static final "+typename+wildcardGenericParametersString+" Noop = ("+lambdaParameters+") -> {};\n");
	
	p("	}\n\t\n");
#


# Procedures! \o/
def genProcedure(inputTypes, arityName, p=p):
	genFunction(inputTypes, "void", arityName, p=p);
#


def genFunctions(arity, arityName=Default, p=p):
	"Generates 9 ^ (arity + 1) interfaces ^w^"
	
	if (arityName == Default):
		arityName = getArityName(arity);
	
	permutation = [0] * (arity + 1);  #the last one is the output ^_^
	
	while True:
		genFunction(inputTypes=map(prims.__getitem__, permutation[:-1]), outputType=prims[permutation[-1]], arityName=arityName, p=p);
		
		if (not incrementInplace(permutation, len(prims))):
			break;



def genProcedures(arity, arityName=Default, p=p):
	"Generates 9 ^ arity interfaces ^w^"
	
	if (arityName == Default):
		arityName = getArityName(arity);
	
	permutation = [0] * (arity + 0);  #no last one for output ;>
	
	while True:
		genProcedure(inputTypes=map(prims.__getitem__, permutation), arityName=arityName, p=p);
		
		if (not incrementInplace(permutation, len(prims))):
			break;



def genObjectFunction(arity, arityName=Default, p=p):
	"Generates.. 1 interface XD"
	
	if (arityName == Default):
		arityName = getArityName(arity);
	
	genFunction(["Object"] * arity, "Object", arityName, p=p);


def genObjectProcedure(arity, arityName=Default, p=p):
	"Generates.. 1 interface XD"
	
	if (arityName == Default):
		arityName = getArityName(arity);
	
	genProcedure(["Object"] * arity, arityName, p=p);
	



genFunctions(0);	#9
genFunctions(1);	#81
#genFunctions(2);	#729

#[--genProcedures(0);	#1--]			just use java.lang.Runnable! ^_~
genProcedures(1);	#9
genProcedures(2);	#81
#genProcedures(3);	#729

#Note: 254 is the maximum number of input parameters to a Java function, apparently! ^w^''
for arity in range(2, 16): genObjectFunction(arity);
for arity in range(3, 16): genObjectProcedure(arity);

					#1664 total!
	 */
	
	@FunctionalInterface
	public static interface NullaryFunctionToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToBoolean.class, "f");
		public boolean f();
		
		//public static final NullaryFunctionToBoolean AlwaysFalse = () -> false;
		//public static final NullaryFunctionToBoolean AlwaysTrue = () -> true;
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunctionToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToByte.class, "f");
		public byte f();
		
		//public static final NullaryFunctionToByte AlwaysZero = () -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunctionToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToChar.class, "f");
		public char f();
		
		//public static final NullaryFunctionToChar AlwaysZero = () -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunctionToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToShort.class, "f");
		public short f();
		
		//public static final NullaryFunctionToShort AlwaysZero = () -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunctionToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToFloat.class, "f");
		public float f();
		
		//public static final NullaryFunctionToFloat AlwaysZero = () -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunctionToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToInt.class, "f");
		public int f();
		
		//public static final NullaryFunctionToInt AlwaysZero = () -> 0;
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunctionToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToDouble.class, "f");
		public double f();
		
		//public static final NullaryFunctionToDouble AlwaysZero = () -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunctionToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunctionToLong.class, "f");
		public long f();
		
		//public static final NullaryFunctionToLong AlwaysZero = () -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface NullaryFunction<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NullaryFunction.class, "f");
		public Output f();
		
		//public static final NullaryFunction<?> AlwaysNull = () -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToBoolean.class, "f");
		public boolean f(boolean input);
		
		//public static final UnaryFunctionBooleanToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionBooleanToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToBoolean.class, "f");
		public boolean f(byte input);
		
		//public static final UnaryFunctionByteToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionByteToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToBoolean.class, "f");
		public boolean f(char input);
		
		//public static final UnaryFunctionCharToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionCharToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToBoolean.class, "f");
		public boolean f(short input);
		
		//public static final UnaryFunctionShortToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionShortToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToBoolean.class, "f");
		public boolean f(float input);
		
		//public static final UnaryFunctionFloatToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionFloatToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToBoolean.class, "f");
		public boolean f(int input);
		
		//public static final UnaryFunctionIntToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionIntToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToBoolean.class, "f");
		public boolean f(double input);
		
		//public static final UnaryFunctionDoubleToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionDoubleToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToBoolean.class, "f");
		public boolean f(long input);
		
		//public static final UnaryFunctionLongToBoolean AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionLongToBoolean AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToBoolean<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToBoolean.class, "f");
		public boolean f(Input input);
		
		//public static final UnaryFunctionObjectToBoolean<?> AlwaysFalse = (input) -> false;
		//public static final UnaryFunctionObjectToBoolean<?> AlwaysTrue = (input) -> true;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToByte.class, "f");
		public byte f(boolean input);
		
		//public static final UnaryFunctionBooleanToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToByte.class, "f");
		public byte f(byte input);
		
		//public static final UnaryFunctionByteToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToByte.class, "f");
		public byte f(char input);
		
		//public static final UnaryFunctionCharToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToByte.class, "f");
		public byte f(short input);
		
		//public static final UnaryFunctionShortToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToByte.class, "f");
		public byte f(float input);
		
		//public static final UnaryFunctionFloatToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToByte.class, "f");
		public byte f(int input);
		
		//public static final UnaryFunctionIntToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToByte.class, "f");
		public byte f(double input);
		
		//public static final UnaryFunctionDoubleToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToByte.class, "f");
		public byte f(long input);
		
		//public static final UnaryFunctionLongToByte AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToByte<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToByte.class, "f");
		public byte f(Input input);
		
		//public static final UnaryFunctionObjectToByte<?> AlwaysZero = (input) -> ((byte)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToChar.class, "f");
		public char f(boolean input);
		
		//public static final UnaryFunctionBooleanToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToChar.class, "f");
		public char f(byte input);
		
		//public static final UnaryFunctionByteToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToChar.class, "f");
		public char f(char input);
		
		//public static final UnaryFunctionCharToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToChar.class, "f");
		public char f(short input);
		
		//public static final UnaryFunctionShortToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToChar.class, "f");
		public char f(float input);
		
		//public static final UnaryFunctionFloatToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToChar.class, "f");
		public char f(int input);
		
		//public static final UnaryFunctionIntToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToChar.class, "f");
		public char f(double input);
		
		//public static final UnaryFunctionDoubleToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToChar.class, "f");
		public char f(long input);
		
		//public static final UnaryFunctionLongToChar AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToChar<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToChar.class, "f");
		public char f(Input input);
		
		//public static final UnaryFunctionObjectToChar<?> AlwaysZero = (input) -> ((char)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToShort.class, "f");
		public short f(boolean input);
		
		//public static final UnaryFunctionBooleanToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToShort.class, "f");
		public short f(byte input);
		
		//public static final UnaryFunctionByteToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToShort.class, "f");
		public short f(char input);
		
		//public static final UnaryFunctionCharToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToShort.class, "f");
		public short f(short input);
		
		//public static final UnaryFunctionShortToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToShort.class, "f");
		public short f(float input);
		
		//public static final UnaryFunctionFloatToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToShort.class, "f");
		public short f(int input);
		
		//public static final UnaryFunctionIntToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToShort.class, "f");
		public short f(double input);
		
		//public static final UnaryFunctionDoubleToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToShort.class, "f");
		public short f(long input);
		
		//public static final UnaryFunctionLongToShort AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToShort<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToShort.class, "f");
		public short f(Input input);
		
		//public static final UnaryFunctionObjectToShort<?> AlwaysZero = (input) -> ((short)0);
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToFloat.class, "f");
		public float f(boolean input);
		
		//public static final UnaryFunctionBooleanToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToFloat.class, "f");
		public float f(byte input);
		
		//public static final UnaryFunctionByteToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToFloat.class, "f");
		public float f(char input);
		
		//public static final UnaryFunctionCharToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToFloat.class, "f");
		public float f(short input);
		
		//public static final UnaryFunctionShortToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToFloat.class, "f");
		public float f(float input);
		
		//public static final UnaryFunctionFloatToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToFloat.class, "f");
		public float f(int input);
		
		//public static final UnaryFunctionIntToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToFloat.class, "f");
		public float f(double input);
		
		//public static final UnaryFunctionDoubleToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToFloat.class, "f");
		public float f(long input);
		
		//public static final UnaryFunctionLongToFloat AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToFloat<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToFloat.class, "f");
		public float f(Input input);
		
		//public static final UnaryFunctionObjectToFloat<?> AlwaysZero = (input) -> 0.0f;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToInt.class, "f");
		public int f(boolean input);
		
		//public static final UnaryFunctionBooleanToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToInt.class, "f");
		public int f(byte input);
		
		//public static final UnaryFunctionByteToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToInt.class, "f");
		public int f(char input);
		
		//public static final UnaryFunctionCharToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToInt.class, "f");
		public int f(short input);
		
		//public static final UnaryFunctionShortToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToInt.class, "f");
		public int f(float input);
		
		//public static final UnaryFunctionFloatToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToInt.class, "f");
		public int f(int input);
		
		//public static final UnaryFunctionIntToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToInt.class, "f");
		public int f(double input);
		
		//public static final UnaryFunctionDoubleToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToInt.class, "f");
		public int f(long input);
		
		//public static final UnaryFunctionLongToInt AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToInt<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToInt.class, "f");
		public int f(Input input);
		
		//public static final UnaryFunctionObjectToInt<?> AlwaysZero = (input) -> 0;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToDouble.class, "f");
		public double f(boolean input);
		
		//public static final UnaryFunctionBooleanToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToDouble.class, "f");
		public double f(byte input);
		
		//public static final UnaryFunctionByteToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToDouble.class, "f");
		public double f(char input);
		
		//public static final UnaryFunctionCharToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToDouble.class, "f");
		public double f(short input);
		
		//public static final UnaryFunctionShortToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToDouble.class, "f");
		public double f(float input);
		
		//public static final UnaryFunctionFloatToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToDouble.class, "f");
		public double f(int input);
		
		//public static final UnaryFunctionIntToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToDouble.class, "f");
		public double f(double input);
		
		//public static final UnaryFunctionDoubleToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToDouble.class, "f");
		public double f(long input);
		
		//public static final UnaryFunctionLongToDouble AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToDouble<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToDouble.class, "f");
		public double f(Input input);
		
		//public static final UnaryFunctionObjectToDouble<?> AlwaysZero = (input) -> 0.0d;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToLong.class, "f");
		public long f(boolean input);
		
		//public static final UnaryFunctionBooleanToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToLong.class, "f");
		public long f(byte input);
		
		//public static final UnaryFunctionByteToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToLong.class, "f");
		public long f(char input);
		
		//public static final UnaryFunctionCharToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToLong.class, "f");
		public long f(short input);
		
		//public static final UnaryFunctionShortToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToLong.class, "f");
		public long f(float input);
		
		//public static final UnaryFunctionFloatToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToLong.class, "f");
		public long f(int input);
		
		//public static final UnaryFunctionIntToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToLong.class, "f");
		public long f(double input);
		
		//public static final UnaryFunctionDoubleToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToLong.class, "f");
		public long f(long input);
		
		//public static final UnaryFunctionLongToLong AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionObjectToLong<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionObjectToLong.class, "f");
		public long f(Input input);
		
		//public static final UnaryFunctionObjectToLong<?> AlwaysZero = (input) -> 0l;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionBooleanToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionBooleanToObject.class, "f");
		public Output f(boolean input);
		
		//public static final UnaryFunctionBooleanToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionByteToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionByteToObject.class, "f");
		public Output f(byte input);
		
		//public static final UnaryFunctionByteToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionCharToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionCharToObject.class, "f");
		public Output f(char input);
		
		//public static final UnaryFunctionCharToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionShortToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionShortToObject.class, "f");
		public Output f(short input);
		
		//public static final UnaryFunctionShortToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionFloatToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionFloatToObject.class, "f");
		public Output f(float input);
		
		//public static final UnaryFunctionFloatToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionIntToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionIntToObject.class, "f");
		public Output f(int input);
		
		//public static final UnaryFunctionIntToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionDoubleToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionDoubleToObject.class, "f");
		public Output f(double input);
		
		//public static final UnaryFunctionDoubleToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunctionLongToObject<Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunctionLongToObject.class, "f");
		public Output f(long input);
		
		//public static final UnaryFunctionLongToObject<?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryFunction<Input, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryFunction.class, "f");
		public Output f(Input input);
		
		//public static final UnaryFunction<?, ?> AlwaysNull = (input) -> null;
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureBoolean.class, "f");
		public void f(boolean input);
		
		//public static final UnaryProcedureBoolean Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureByte.class, "f");
		public void f(byte input);
		
		//public static final UnaryProcedureByte Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureChar.class, "f");
		public void f(char input);
		
		//public static final UnaryProcedureChar Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureShort.class, "f");
		public void f(short input);
		
		//public static final UnaryProcedureShort Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureFloat.class, "f");
		public void f(float input);
		
		//public static final UnaryProcedureFloat Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureInt.class, "f");
		public void f(int input);
		
		//public static final UnaryProcedureInt Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureDouble.class, "f");
		public void f(double input);
		
		//public static final UnaryProcedureDouble Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedureLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedureLong.class, "f");
		public void f(long input);
		
		//public static final UnaryProcedureLong Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface UnaryProcedure<Input>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(UnaryProcedure.class, "f");
		public void f(Input input);
		
		//public static final UnaryProcedure<?> Noop = (input) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanBoolean.class, "f");
		public void f(boolean input0, boolean input1);
		
		//public static final BinaryProcedureBooleanBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteBoolean.class, "f");
		public void f(byte input0, boolean input1);
		
		//public static final BinaryProcedureByteBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharBoolean.class, "f");
		public void f(char input0, boolean input1);
		
		//public static final BinaryProcedureCharBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortBoolean.class, "f");
		public void f(short input0, boolean input1);
		
		//public static final BinaryProcedureShortBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatBoolean.class, "f");
		public void f(float input0, boolean input1);
		
		//public static final BinaryProcedureFloatBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntBoolean.class, "f");
		public void f(int input0, boolean input1);
		
		//public static final BinaryProcedureIntBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleBoolean.class, "f");
		public void f(double input0, boolean input1);
		
		//public static final BinaryProcedureDoubleBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongBoolean
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongBoolean.class, "f");
		public void f(long input0, boolean input1);
		
		//public static final BinaryProcedureLongBoolean Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectBoolean<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectBoolean.class, "f");
		public void f(Input0 input0, boolean input1);
		
		//public static final BinaryProcedureObjectBoolean<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanByte.class, "f");
		public void f(boolean input0, byte input1);
		
		//public static final BinaryProcedureBooleanByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteByte.class, "f");
		public void f(byte input0, byte input1);
		
		//public static final BinaryProcedureByteByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharByte.class, "f");
		public void f(char input0, byte input1);
		
		//public static final BinaryProcedureCharByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortByte.class, "f");
		public void f(short input0, byte input1);
		
		//public static final BinaryProcedureShortByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatByte.class, "f");
		public void f(float input0, byte input1);
		
		//public static final BinaryProcedureFloatByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntByte.class, "f");
		public void f(int input0, byte input1);
		
		//public static final BinaryProcedureIntByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleByte.class, "f");
		public void f(double input0, byte input1);
		
		//public static final BinaryProcedureDoubleByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongByte
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongByte.class, "f");
		public void f(long input0, byte input1);
		
		//public static final BinaryProcedureLongByte Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectByte<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectByte.class, "f");
		public void f(Input0 input0, byte input1);
		
		//public static final BinaryProcedureObjectByte<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanChar.class, "f");
		public void f(boolean input0, char input1);
		
		//public static final BinaryProcedureBooleanChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteChar.class, "f");
		public void f(byte input0, char input1);
		
		//public static final BinaryProcedureByteChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharChar.class, "f");
		public void f(char input0, char input1);
		
		//public static final BinaryProcedureCharChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortChar.class, "f");
		public void f(short input0, char input1);
		
		//public static final BinaryProcedureShortChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatChar.class, "f");
		public void f(float input0, char input1);
		
		//public static final BinaryProcedureFloatChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntChar.class, "f");
		public void f(int input0, char input1);
		
		//public static final BinaryProcedureIntChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleChar.class, "f");
		public void f(double input0, char input1);
		
		//public static final BinaryProcedureDoubleChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongChar
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongChar.class, "f");
		public void f(long input0, char input1);
		
		//public static final BinaryProcedureLongChar Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectChar<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectChar.class, "f");
		public void f(Input0 input0, char input1);
		
		//public static final BinaryProcedureObjectChar<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanShort.class, "f");
		public void f(boolean input0, short input1);
		
		//public static final BinaryProcedureBooleanShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteShort.class, "f");
		public void f(byte input0, short input1);
		
		//public static final BinaryProcedureByteShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharShort.class, "f");
		public void f(char input0, short input1);
		
		//public static final BinaryProcedureCharShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortShort.class, "f");
		public void f(short input0, short input1);
		
		//public static final BinaryProcedureShortShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatShort.class, "f");
		public void f(float input0, short input1);
		
		//public static final BinaryProcedureFloatShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntShort.class, "f");
		public void f(int input0, short input1);
		
		//public static final BinaryProcedureIntShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleShort.class, "f");
		public void f(double input0, short input1);
		
		//public static final BinaryProcedureDoubleShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongShort
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongShort.class, "f");
		public void f(long input0, short input1);
		
		//public static final BinaryProcedureLongShort Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectShort<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectShort.class, "f");
		public void f(Input0 input0, short input1);
		
		//public static final BinaryProcedureObjectShort<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanFloat.class, "f");
		public void f(boolean input0, float input1);
		
		//public static final BinaryProcedureBooleanFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteFloat.class, "f");
		public void f(byte input0, float input1);
		
		//public static final BinaryProcedureByteFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharFloat.class, "f");
		public void f(char input0, float input1);
		
		//public static final BinaryProcedureCharFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortFloat.class, "f");
		public void f(short input0, float input1);
		
		//public static final BinaryProcedureShortFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatFloat.class, "f");
		public void f(float input0, float input1);
		
		//public static final BinaryProcedureFloatFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntFloat.class, "f");
		public void f(int input0, float input1);
		
		//public static final BinaryProcedureIntFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleFloat.class, "f");
		public void f(double input0, float input1);
		
		//public static final BinaryProcedureDoubleFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongFloat
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongFloat.class, "f");
		public void f(long input0, float input1);
		
		//public static final BinaryProcedureLongFloat Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectFloat<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectFloat.class, "f");
		public void f(Input0 input0, float input1);
		
		//public static final BinaryProcedureObjectFloat<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanInt.class, "f");
		public void f(boolean input0, int input1);
		
		//public static final BinaryProcedureBooleanInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteInt.class, "f");
		public void f(byte input0, int input1);
		
		//public static final BinaryProcedureByteInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharInt.class, "f");
		public void f(char input0, int input1);
		
		//public static final BinaryProcedureCharInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortInt.class, "f");
		public void f(short input0, int input1);
		
		//public static final BinaryProcedureShortInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatInt.class, "f");
		public void f(float input0, int input1);
		
		//public static final BinaryProcedureFloatInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntInt.class, "f");
		public void f(int input0, int input1);
		
		//public static final BinaryProcedureIntInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleInt.class, "f");
		public void f(double input0, int input1);
		
		//public static final BinaryProcedureDoubleInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongInt
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongInt.class, "f");
		public void f(long input0, int input1);
		
		//public static final BinaryProcedureLongInt Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectInt<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectInt.class, "f");
		public void f(Input0 input0, int input1);
		
		//public static final BinaryProcedureObjectInt<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanDouble.class, "f");
		public void f(boolean input0, double input1);
		
		//public static final BinaryProcedureBooleanDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteDouble.class, "f");
		public void f(byte input0, double input1);
		
		//public static final BinaryProcedureByteDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharDouble.class, "f");
		public void f(char input0, double input1);
		
		//public static final BinaryProcedureCharDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortDouble.class, "f");
		public void f(short input0, double input1);
		
		//public static final BinaryProcedureShortDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatDouble.class, "f");
		public void f(float input0, double input1);
		
		//public static final BinaryProcedureFloatDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntDouble.class, "f");
		public void f(int input0, double input1);
		
		//public static final BinaryProcedureIntDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleDouble.class, "f");
		public void f(double input0, double input1);
		
		//public static final BinaryProcedureDoubleDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongDouble
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongDouble.class, "f");
		public void f(long input0, double input1);
		
		//public static final BinaryProcedureLongDouble Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectDouble<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectDouble.class, "f");
		public void f(Input0 input0, double input1);
		
		//public static final BinaryProcedureObjectDouble<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanLong.class, "f");
		public void f(boolean input0, long input1);
		
		//public static final BinaryProcedureBooleanLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteLong.class, "f");
		public void f(byte input0, long input1);
		
		//public static final BinaryProcedureByteLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharLong.class, "f");
		public void f(char input0, long input1);
		
		//public static final BinaryProcedureCharLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortLong.class, "f");
		public void f(short input0, long input1);
		
		//public static final BinaryProcedureShortLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatLong.class, "f");
		public void f(float input0, long input1);
		
		//public static final BinaryProcedureFloatLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntLong.class, "f");
		public void f(int input0, long input1);
		
		//public static final BinaryProcedureIntLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleLong.class, "f");
		public void f(double input0, long input1);
		
		//public static final BinaryProcedureDoubleLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongLong
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongLong.class, "f");
		public void f(long input0, long input1);
		
		//public static final BinaryProcedureLongLong Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureObjectLong<Input0>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureObjectLong.class, "f");
		public void f(Input0 input0, long input1);
		
		//public static final BinaryProcedureObjectLong<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureBooleanObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureBooleanObject.class, "f");
		public void f(boolean input0, Input1 input1);
		
		//public static final BinaryProcedureBooleanObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureByteObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureByteObject.class, "f");
		public void f(byte input0, Input1 input1);
		
		//public static final BinaryProcedureByteObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureCharObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureCharObject.class, "f");
		public void f(char input0, Input1 input1);
		
		//public static final BinaryProcedureCharObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureShortObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureShortObject.class, "f");
		public void f(short input0, Input1 input1);
		
		//public static final BinaryProcedureShortObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureFloatObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureFloatObject.class, "f");
		public void f(float input0, Input1 input1);
		
		//public static final BinaryProcedureFloatObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureIntObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureIntObject.class, "f");
		public void f(int input0, Input1 input1);
		
		//public static final BinaryProcedureIntObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureDoubleObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureDoubleObject.class, "f");
		public void f(double input0, Input1 input1);
		
		//public static final BinaryProcedureDoubleObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedureLongObject<Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedureLongObject.class, "f");
		public void f(long input0, Input1 input1);
		
		//public static final BinaryProcedureLongObject<?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryProcedure<Input0, Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1);
		
		//public static final BinaryProcedure<?, ?> Noop = (input0, input1) -> {};
	}
	
	
	@FunctionalInterface
	public static interface BinaryFunction<Input0, Input1, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1);
		
		//public static final BinaryFunction<?, ?, ?> AlwaysNull = (input0, input1) -> null;
	}
	
	
	@FunctionalInterface
	public static interface TrinaryFunction<Input0, Input1, Input2, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(TrinaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2);
		
		//public static final TrinaryFunction<?, ?, ?, ?> AlwaysNull = (input0, input1, input2) -> null;
	}
	
	
	@FunctionalInterface
	public static interface QuaternaryFunction<Input0, Input1, Input2, Input3, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(QuaternaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3);
		
		//public static final QuaternaryFunction<?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3) -> null;
	}
	
	
	@FunctionalInterface
	public static interface QuinaryFunction<Input0, Input1, Input2, Input3, Input4, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(QuinaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4);
		
		//public static final QuinaryFunction<?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4) -> null;
	}
	
	
	@FunctionalInterface
	public static interface SenaryFunction<Input0, Input1, Input2, Input3, Input4, Input5, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(SenaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5);
		
		//public static final SenaryFunction<?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5) -> null;
	}
	
	
	@FunctionalInterface
	public static interface SeptenaryFunction<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(SeptenaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6);
		
		//public static final SeptenaryFunction<?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6) -> null;
	}
	
	
	@FunctionalInterface
	public static interface OctaryFunction<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(OctaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7);
		
		//public static final OctaryFunction<?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7) -> null;
	}
	
	
	@FunctionalInterface
	public static interface NonaryFunction<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NonaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8);
		
		//public static final NonaryFunction<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7, input8) -> null;
	}
	
	
	@FunctionalInterface
	public static interface DecaryFunction<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(DecaryFunction.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9);
		
		//public static final DecaryFunction<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9) -> null;
	}
	
	
	@FunctionalInterface
	public static interface Arity11Function<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity11Function.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10);
		
		//public static final Arity11Function<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10) -> null;
	}
	
	
	@FunctionalInterface
	public static interface Arity12Function<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity12Function.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11);
		
		//public static final Arity12Function<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11) -> null;
	}
	
	
	@FunctionalInterface
	public static interface Arity13Function<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11, Input12, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity13Function.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11, Input12 input12);
		
		//public static final Arity13Function<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11, input12) -> null;
	}
	
	
	@FunctionalInterface
	public static interface Arity14Function<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11, Input12, Input13, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity14Function.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11, Input12 input12, Input13 input13);
		
		//public static final Arity14Function<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11, input12, input13) -> null;
	}
	
	
	@FunctionalInterface
	public static interface Arity15Function<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11, Input12, Input13, Input14, Output>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity15Function.class, "f");
		public Output f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11, Input12 input12, Input13 input13, Input14 input14);
		
		//public static final Arity15Function<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> AlwaysNull = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11, input12, input13, input14) -> null;
	}
	
	
	@FunctionalInterface
	public static interface TrinaryProcedure<Input0, Input1, Input2>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(TrinaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2);
		
		//public static final TrinaryProcedure<?, ?, ?> Noop = (input0, input1, input2) -> {};
	}
	
	
	@FunctionalInterface
	public static interface QuaternaryProcedure<Input0, Input1, Input2, Input3>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(QuaternaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3);
		
		//public static final QuaternaryProcedure<?, ?, ?, ?> Noop = (input0, input1, input2, input3) -> {};
	}
	
	
	@FunctionalInterface
	public static interface QuinaryProcedure<Input0, Input1, Input2, Input3, Input4>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(QuinaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4);
		
		//public static final QuinaryProcedure<?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4) -> {};
	}
	
	
	@FunctionalInterface
	public static interface SenaryProcedure<Input0, Input1, Input2, Input3, Input4, Input5>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(SenaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5);
		
		//public static final SenaryProcedure<?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5) -> {};
	}
	
	
	@FunctionalInterface
	public static interface SeptenaryProcedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(SeptenaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6);
		
		//public static final SeptenaryProcedure<?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6) -> {};
	}
	
	
	@FunctionalInterface
	public static interface OctaryProcedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(OctaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7);
		
		//public static final OctaryProcedure<?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7) -> {};
	}
	
	
	@FunctionalInterface
	public static interface NonaryProcedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(NonaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8);
		
		//public static final NonaryProcedure<?, ?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7, input8) -> {};
	}
	
	
	@FunctionalInterface
	public static interface DecaryProcedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(DecaryProcedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9);
		
		//public static final DecaryProcedure<?, ?, ?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9) -> {};
	}
	
	
	@FunctionalInterface
	public static interface Arity11Procedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity11Procedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10);
		
		//public static final Arity11Procedure<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10) -> {};
	}
	
	
	@FunctionalInterface
	public static interface Arity12Procedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity12Procedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11);
		
		//public static final Arity12Procedure<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11) -> {};
	}
	
	
	@FunctionalInterface
	public static interface Arity13Procedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11, Input12>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity13Procedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11, Input12 input12);
		
		//public static final Arity13Procedure<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11, input12) -> {};
	}
	
	
	@FunctionalInterface
	public static interface Arity14Procedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11, Input12, Input13>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity14Procedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11, Input12 input12, Input13 input13);
		
		//public static final Arity14Procedure<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11, input12, input13) -> {};
	}
	
	
	@FunctionalInterface
	public static interface Arity15Procedure<Input0, Input1, Input2, Input3, Input4, Input5, Input6, Input7, Input8, Input9, Input10, Input11, Input12, Input13, Input14>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(Arity15Procedure.class, "f");
		public void f(Input0 input0, Input1 input1, Input2 input2, Input3 input3, Input4 input4, Input5 input5, Input6 input6, Input7 input7, Input8 input8, Input9 input9, Input10 input10, Input11 input11, Input12 input12, Input13 input13, Input14 input14);
		
		//public static final Arity15Procedure<?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?> Noop = (input0, input1, input2, input3, input4, input5, input6, input7, input8, input9, input10, input11, input12, input13, input14) -> {};
	}
	
	
	//>>>
	
	
	
	
	
	
	
	
	//Shelved, mostly unnecessary with Java 8  :>
	
	//	public static interface UnaryFunctionAnyToObject<ObjectInput, Output>
	//	extends
	//	UnaryFunction<ObjectInput, Output>,
	//	//UnaryFunction_$$Prim$$_ToObject<Output>,
	//	UnaryFunctionBooleanToObject<Output>,
	//	UnaryFunctionByteToObject<Output>,
	//	UnaryFunctionCharToObject<Output>,
	//	UnaryFunctionShortToObject<Output>,
	//	UnaryFunctionFloatToObject<Output>,
	//	UnaryFunctionIntToObject<Output>,
	//	UnaryFunctionDoubleToObject<Output>,
	//	UnaryFunctionLongToObject<Output>
	//	{
	//	}
	//
	//
	//
	//
	//	public static interface UnaryFunctionAnyToBoolean<ObjectInput>
	//	extends
	//	UnaryFunctionObjectToBoolean<ObjectInput>,
	//	//UnaryFunction_$$Prim$$_ToBoolean,
	//	UnaryFunctionBooleanToBoolean,
	//	UnaryFunctionByteToBoolean,
	//	UnaryFunctionCharToBoolean,
	//	UnaryFunctionShortToBoolean,
	//	UnaryFunctionFloatToBoolean,
	//	UnaryFunctionIntToBoolean,
	//	UnaryFunctionDoubleToBoolean,
	//	UnaryFunctionLongToBoolean
	//	{
	//	}
	//
	//
	//
	//
	//	public static interface UnaryProcedureAny<ObjectInput>
	//	extends
	//	UnaryProcedure<ObjectInput>,
	//	//UnaryProcedure_$$Prim$$_,
	//	UnaryProcedureBoolean,
	//	UnaryProcedureByte,
	//	UnaryProcedureChar,
	//	UnaryProcedureShort,
	//	UnaryProcedureFloat,
	//	UnaryProcedureInt,
	//	UnaryProcedureDouble,
	//	UnaryProcedureLong
	//	{
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@FunctionalInterface
	public static interface BinaryFunctionToBoolean<Input0, Input1>
	{
		//public static final MethodHandle f = lookupInstanceNonoverloadedMethod(BinaryFunction.class, "f");
		public boolean f(Input0 input0, Input1 input1);
		
		//public static final BinaryFunction<?, ?, ?> AlwaysNull = (input0, input1) -> null;
	}
	
	
	@FunctionalInterface
	public static interface BinaryFunctionLongLongToBoolean
	{
		public boolean f(long input0, long input1);
	}
	
	@FunctionalInterface
	public static interface BinaryFunctionIntIntToBoolean
	{
		public boolean f(int input0, int input1);
	}
}
