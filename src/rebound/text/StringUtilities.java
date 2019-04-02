/*
 * Created on Jul 27, 2007
 * 	by the great Eclipse(c)
 */
package rebound.text;

import static rebound.bits.Unsigned.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.text.CharacterPredicates.*;
import static rebound.util.collections.ArrayUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import java.io.CharArrayReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CharsetEncoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.function.Predicate;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.semantic.SignalType;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.annotations.semantic.simpledata.ActuallyUnsignedValue;
import rebound.annotations.semantic.temporal.PossiblySnapshotPossiblyLiveValue;
import rebound.bits.DataEncodingUtilities;
import rebound.bits.Unsigned;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NonSingletonException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.ReturnPath.SingletonReturnPath;
import rebound.exceptions.TextSyntaxCheckedException;
import rebound.exceptions.TextSyntaxException;
import rebound.exceptions.UnreachableCodeException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.io.TextIOUtilities;
import rebound.io.ucs4.UCS4Reader;
import rebound.io.ucs4.UCS4ReaderFromNormalUTF16Reader;
import rebound.io.ucs4.UCS4Writer;
import rebound.math.SmallIntegerMathUtilities;
import rebound.text.CharacterPredicates.NaiveCharacterSequencePattern;
import rebound.text.StringUtilities.RPBasicNaiveParsingSyntaxDescription.RPBasicNaiveParsingSyntaxStateDescription;
import rebound.util.BasicExceptionUtilities;
import rebound.util.NIOBufferUtilities;
import rebound.util.Primitives;
import rebound.util.ScanDirection;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.BasicCollectionUtilities;
import rebound.util.collections.IdentityHashSet;
import rebound.util.collections.PolymorphicCollectionUtilities;
import rebound.util.collections.Slice;
import rebound.util.collections.prim.PrimitiveCollections.ByteList;
import rebound.util.collections.prim.PrimitiveCollections.ImmutableByteArrayList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerArrayList;
import rebound.util.collections.prim.PrimitiveCollections.IntegerList;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionCharToBoolean;
import rebound.util.functional.FunctionalUtilities.SingletonCharEqualityPredicate;
import rebound.util.objectutil.JavaNamespace;
import rebound.util.objectutil.ObjectUtilities;
import rebound.util.uid.UIDUtilities;

public class StringUtilities
implements JavaNamespace
{
	public static final String SYSTEM_EOL = System.getProperty("line.separator"); //todo-lp Split and make all uses of this explicit in that they use the current platform's convention, while providing a cross-platform alternative;    edit: current idea is just to use a simple standard, like "\n", and have filters for when things go into or outof the internals here (a la Universal Newlines of Python ^_^ )
	public static final int NUMBER_OF_CHARACTERS = 65536; //Number of values that can be stored in a 'char' primitive type
	
	
	//<Alphabets
	public static final char[] ALPHABET_ENGLISH_US_LOWER = new char[]{'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	public static final char[] ALPHABET_ENGLISH_US_UPPER = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
	public static final char[] ALPHABET_BASE_10_DIGITS = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
	
	public static final char[] ALPHABET_ENGLISH_US_UPPER__ENGLISH_US_LOWER = new char[]{'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	public static final char[] ALPHABET_BASE_10_DIGITS__ENGLISH_US_UPPER__ENGLISH_US_LOWER = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
	//Alphabets>
	
	
	
	public static final String[] EmptyStringArray = new String[0];
	
	
	
	
	
	
	
	
	
	
	//TODO one of these for general unconverted textthings :>
	public static class TransparentArraySharingCharSequence
	implements CharSequence
	{
		public char[] source;
		public int offset;
		public int length;
		
		public TransparentArraySharingCharSequence()
		{
		}
		
		public TransparentArraySharingCharSequence(char[] source)
		{
			this(source, 0, source.length);
		}
		
		public TransparentArraySharingCharSequence(char[] source, int offset, int length)
		{
			this.source = source;
			this.offset = offset;
			this.length = length;
		}
		
		
		
		@Override
		public char charAt(int index)
		{
			return this.source[this.offset+index];
		}
		
		@Override
		public int length()
		{
			return this.length == -1 ? this.source.length - this.offset : this.length;
		}
		
		@Override
		public CharSequence subSequence(int start, int end)
		{
			return new TransparentArraySharingCharSequence(this.source, start+this.offset, end-start);
		}
		
		@Override
		public String toString()
		{
			return new String(this.source, this.offset, this.length);
		}
	}
	
	
	
	
	
	public static String replace(String original, char target, char replacement)
	{
		StringBuilder buff = new StringBuilder();
		for (char c : original.toCharArray())
		{
			if (c == target)
				buff.append(replacement);
			else
				buff.append(c);
		}
		return buff.toString();
	}
	
	public static String replace(String original, char target, String replacement)
	{
		StringBuilder buff = new StringBuilder();
		for (char c : original.toCharArray())
		{
			if (c == target)
				buff.append(replacement);
			else
				buff.append(c);
		}
		return buff.toString();
	}
	
	public static String replace(String original, UnaryFunctionCharToBoolean target, String replacement)
	{
		StringBuilder buff = new StringBuilder();
		for (char c : original.toCharArray())
		{
			if (target.f(c))
				buff.append(replacement);
			else
				buff.append(c);
		}
		return buff.toString();
	}
	
	
	/**
	 * Repeatedly replaces text multiple times atomically, so the indexes remain static.<br>
	 * There are some constraints to the replacements:
	 * <ul>
	 * <li>No two replacements may overlap (excluding multiple insertions(start=end) at the same point, which are performed in the order of the given List)</li>
	 * <li>The list must be in order, from left/first/low to right/last/high</li>
	 * </ul>
	 * Note: It a replacement's start and end are the same, then an insertion is performed.<br>
	 * @throws IllegalArgumentException If the replacements are out of order or (end < start)
	 */
	public static String applyReplacements(String source, Iterable<StaticReplacement> replacements)
	{
		StringBuilder buff = new StringBuilder();
		
		int position = 0; //position in the input
		
		int amt = 0;
		String r = null; int s = 0, e = 0;
		for (StaticReplacement replacement : replacements)
		{
			r = replacement.getReplacement();
			s = replacement.getStart();
			e = replacement.getEnd();
			
			//Copy up to the start of the next replacement
			{
				amt = s - position;
				
				if (amt < 0)
					throw new IllegalArgumentException("Replacements were overlapping");
				
				if (amt > 0)
					buff.append(source.substring(position, s));
				
				position += amt;
			}
			
			//Write the replacement to the output, and skip the replaced input
			{
				if (r != null)
					buff.append(r);
				
				if (e < s)
					throw new IllegalArgumentException("The end cannot come before the start.");
				
				position += e - s;
			}
		}
		
		
		//Copy the remainder of the input
		{
			buff.append(source.substring(position));
		}
		
		return buff.toString();
	}
	
	
	/**
	 * replaces text while copying data from one stream to another.
	 * There are some constraints to the replacements:
	 * <ul>
	 * <li>No two replacements may overlap (excluding multiple insertions(start=end) at the same point, which are performed in the order of the given List)</li>
	 * <li>The list must be in order, from left/first/low to right/last/high</li>
	 * </ul>
	 * Note: It a replacement's start and end are the same, then an insertion is performed.<br>
	 * 
	 * @throws IllegalArgumentException If the replacements are out of order or (end < start)
	 */
	public static void applyReplacements(Reader source, Writer sink, Iterable<Replacement> replacements) throws IllegalArgumentException, IOException
	{
		int position = 0; //position in the input
		
		int amt = 0;
		int s = 0, e = 0;
		for (Replacement replacement : replacements)
		{
			s = replacement.getStart();
			e = replacement.getEnd();
			
			//Pump up to the start of the next replacement
			{
				amt = s - position;
				
				if (amt < 0)
					throw new IllegalArgumentException("Replacements were overlapping");
				
				if (amt > 0)
					TextIOUtilities.pumpFixed(source, sink, amt);
				
				position += amt;
			}
			
			//Write the replacement to the output, and skip the replaced input
			{
				replacement.write(sink);
				
				if (e < s)
					throw new IllegalArgumentException("The end cannot come before the start.");
				if (e > s)
				{
					TextIOUtilities.discard(source, e-s);
					position += e - s;
				}
			}
		}
		
		
		//Copy the remainder of the input
		{
			TextIOUtilities.pump(source, sink);
		}
	}
	
	
	//	Add if needed
	//	public static Replacement[] sortReplacements(Replacement[] replacements)
	//	{
	//
	//	}
	
	
	
	public static abstract class Replacement
	{
		protected int start, end;
		
		public Replacement()
		{
			super();
		}
		
		public Replacement(int start, int end)
		{
			super();
			this.start = start;
			this.end = end;
		}
		
		public int getStart()
		{
			return this.start;
		}
		public void setStart(int start)
		{
			this.start = start;
		}
		public int getEnd()
		{
			return this.end;
		}
		public void setEnd(int end)
		{
			this.end = end;
		}
		
		public abstract void write(Writer out) throws IOException;
	}
	
	public static class StaticReplacement
	extends Replacement
	{
		protected String replacement;
		
		public StaticReplacement()
		{
			super();
		}
		
		public StaticReplacement(int start, int end, String replacement)
		{
			super();
			this.start = start;
			this.end = end;
			this.replacement = replacement;
		}
		
		public String getReplacement()
		{
			return this.replacement;
		}
		
		public void setReplacement(String replacement)
		{
			this.replacement = replacement;
		}
		
		@Override
		public void write(Writer out) throws IOException
		{
			String r = getReplacement();
			if (r != null)
				out.write(r);
		}
	}
	
	
	//Todo versions of these for char[]'s instead of Strings
	
	public static String applyMultipleCharReplacements(String str, char[] targets, char replacement)
	{
		char[] oldc = str.toCharArray();
		char[] newc = new char[oldc.length];
		int i = 0, e = 0;
		char c = 0;
		
		for (i = 0; i < newc.length; i++)
		{
			c = oldc[i];
			
			e = 0;
			for (e = 0; e < targets.length; e++)
			{
				if (c == targets[e])
				{
					newc[i] = replacement;
					break;
				}
			}
			
			if (e == targets.length)
				//if no targets matched c
				newc[i] = c;
		}
		
		return new String(newc);
	}
	
	public static String applyMultipleCharReplacements(String str, char[] targets, char[] replacements)
	{
		char[] oldc = str.toCharArray();
		char[] newc = new char[oldc.length];
		int i = 0, e = 0;
		char c = 0;
		
		for (i = 0; i < newc.length; i++)
		{
			c = oldc[i];
			
			e = 0;
			for (e = 0; e < targets.length; e++)
			{
				if (c == targets[e])
				{
					newc[i] = replacements[e];
					break;
				}
			}
			
			if (e == targets.length)
				//if no targets matched c
				newc[i] = c;
		}
		
		return new String(newc);
	}
	
	
	
	public static char[] applyMultiplePositionBasedCharReplacementsCA(Object text, char replacement, int[] positions)
	{
		char[] ca = textthingToPossiblyUnclonedCharArray(text);
		
		for (int position : positions)
		{
			ca[position] = replacement; //throws exception if out of bounds ^_^
		}
		
		return ca;
	}
	
	public static String applyMultiplePositionBasedCharReplacementsS(Object text, char replacement, int[] positions)
	{
		return new String(applyMultiplePositionBasedCharReplacementsCA(text, replacement, positions));
	}
	
	
	
	
	
	public static interface ReplacementBoss
	{
		public String whatDoBoss(int replacementIndex, String matchingSubstring);
	}
	
	
	public static String replaceAll(String original, String simpleCaseSensitiveNonregexPattern, ReplacementBoss replacementBoss)
	{
		StringBuilder buff = new StringBuilder();
		
		int replacementIndex = 0;
		int cursor = 0;
		
		while (true)
		{
			if (cursor >= original.length())
				break;
			
			int nextStart = original.indexOf(simpleCaseSensitiveNonregexPattern, cursor);
			
			if (nextStart == -1)
				break;
			
			int nextEnd = nextStart + simpleCaseSensitiveNonregexPattern.length();
			
			//Append all the stuff up to this point :>
			buff.append(original, cursor, nextStart);
			
			//Append the boss' replacement :>
			buff.append(replacementBoss.whatDoBoss(replacementIndex, original.substring(nextStart, nextEnd)));
			
			replacementIndex++;
			
			
			//Advance the cursor :>
			cursor = nextEnd;
		}
		
		if (cursor < original.length())
		{
			//Append the remainder of the stuff :>
			buff.append(original, cursor, original.length());
		}
		
		
		return buff.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static enum WhatToDoWithEmpties
	{
		LeaveInEmpties,
		LeaveOutEmpties,
	}
	
	
	
	
	/**
	 * @param limitInMaximumNumberOfDelimiterSplits use -1 for no-limit :33
	 */
	public static String[] split(String original, char delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (whatToDoWithEmpties == null)
			throw new NullPointerException();
		
		
		boolean leaveInEmpties = whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties;
		
		char[] data = original.toCharArray();
		ArrayList<String> tokens = new ArrayList<String>(6);
		int tokenStart = 0;
		for (int i = 0; i < data.length && (limitInMaximumNumberOfDelimiterSplits == -1 || tokens.size() < limitInMaximumNumberOfDelimiterSplits); i++)
		{
			if (data[i] == delimiter)
			{
				if (leaveInEmpties || i - tokenStart != 0)
				{
					tokens.add(new String(data, tokenStart, i - tokenStart));
				}
				
				tokenStart = i + 1; //Token doesn't include the delimiter
			}
		}
		
		tokens.add(new String(data, tokenStart, data.length - tokenStart));
		
		return tokens.toArray(new String[tokens.size()]);
	}
	
	public static String[] rsplit(String original, char delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (whatToDoWithEmpties == null) throw new NullPointerException();
		
		
		boolean leaveInEmpties = whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties;
		
		char[] data = original.toCharArray();
		ArrayList<String> tokens = new ArrayList<String>(6);
		int tokenEnd = data.length;
		for (int i = data.length-1; i >= 0 && (limitInMaximumNumberOfDelimiterSplits == -1 || tokens.size() < limitInMaximumNumberOfDelimiterSplits); i--)
		{
			if (data[i] == delimiter)
			{
				if (leaveInEmpties || tokenEnd-(i+1) != 0)
				{
					tokens.add(new String(data, i+1, tokenEnd-(i+1)));
				}
				
				tokenEnd = i;
			}
		}
		
		tokens.add(new String(data, 0, tokenEnd));
		
		String[] reversed = new String[tokens.size()];
		for (int i = 0; i < reversed.length; i++)
			reversed[i] = tokens.get(reversed.length - i - 1);
		return reversed;
	}
	
	
	
	public static String[] split(String original, char delimiter, int limit)
	{
		return split(original, delimiter, limit, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	public static String[] rsplit(String original, char delimiter, int limit)
	{
		return rsplit(original, delimiter, limit, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	public static String[] split(String original, char delimiter)
	{
		return split(original, delimiter, -1);
	}
	
	
	
	public static String[] split(String original, char delimiter, int limitInMaximumNumberOfDelimiterSplits, ScanDirection direction)
	{
		if (direction == ScanDirection.Forward)
			return split(original, delimiter, limitInMaximumNumberOfDelimiterSplits);
		else if (direction == ScanDirection.Reverse)
			return rsplit(original, delimiter, limitInMaximumNumberOfDelimiterSplits);
		throw BasicExceptionUtilities.newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(direction);
	}
	
	public static String[] split(String original, char delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties, ScanDirection direction)
	{
		if (direction == ScanDirection.Forward)
			return split(original, delimiter, limitInMaximumNumberOfDelimiterSplits, whatToDoWithEmpties);
		else if (direction == ScanDirection.Reverse)
			return rsplit(original, delimiter, limitInMaximumNumberOfDelimiterSplits, whatToDoWithEmpties);
		throw BasicExceptionUtilities.newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(direction);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static String[] split(String original, UnaryFunctionCharToBoolean delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (whatToDoWithEmpties == null)
			throw new NullPointerException();
		
		
		boolean leaveInEmpties = whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties;
		
		char[] data = original.toCharArray();
		ArrayList<String> tokens = new ArrayList<String>(6);
		int tokenStart = 0;
		for (int i = 0; i < data.length && (limitInMaximumNumberOfDelimiterSplits == -1 || tokens.size() < limitInMaximumNumberOfDelimiterSplits); i++)
		{
			if (delimiter.f(data[i]))
			{
				if (leaveInEmpties || i - tokenStart != 0)
				{
					tokens.add(new String(data, tokenStart, i - tokenStart));
				}
				
				tokenStart = i + 1; //Token doesn't include the delimiter
			}
		}
		
		tokens.add(new String(data, tokenStart, data.length - tokenStart));
		
		return tokens.toArray(new String[tokens.size()]);
	}
	
	public static String[] rsplit(String original, UnaryFunctionCharToBoolean delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (whatToDoWithEmpties == null) throw new NullPointerException();
		
		
		boolean leaveInEmpties = whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties;
		
		char[] data = original.toCharArray();
		ArrayList<String> tokens = new ArrayList<String>(6);
		int tokenEnd = data.length;
		for (int i = data.length-1; i >= 0 && (limitInMaximumNumberOfDelimiterSplits == -1 || tokens.size() < limitInMaximumNumberOfDelimiterSplits); i--)
		{
			if (delimiter.f(data[i]))
			{
				if (leaveInEmpties || tokenEnd-(i+1) != 0)
				{
					tokens.add(new String(data, i+1, tokenEnd-(i+1)));
				}
				
				tokenEnd = i;
			}
		}
		
		tokens.add(new String(data, 0, tokenEnd));
		
		String[] reversed = new String[tokens.size()];
		for (int i = 0; i < reversed.length; i++)
			reversed[i] = tokens.get(reversed.length - i - 1);
		return reversed;
	}
	
	
	
	public static String[] split(String original, UnaryFunctionCharToBoolean delimiter, int limit)
	{
		return split(original, delimiter, limit, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	public static String[] rsplit(String original, UnaryFunctionCharToBoolean delimiter, int limit)
	{
		return rsplit(original, delimiter, limit, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	public static String[] split(String original, UnaryFunctionCharToBoolean delimiter)
	{
		return split(original, delimiter, -1);
	}
	
	
	
	public static String[] split(String original, UnaryFunctionCharToBoolean delimiter, int limitInMaximumNumberOfDelimiterSplits, ScanDirection direction)
	{
		if (direction == ScanDirection.Forward)
			return split(original, delimiter, limitInMaximumNumberOfDelimiterSplits);
		else if (direction == ScanDirection.Reverse)
			return rsplit(original, delimiter, limitInMaximumNumberOfDelimiterSplits);
		throw BasicExceptionUtilities.newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(direction);
	}
	
	public static String[] split(String original, UnaryFunctionCharToBoolean delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties, ScanDirection direction)
	{
		if (direction == ScanDirection.Forward)
			return split(original, delimiter, limitInMaximumNumberOfDelimiterSplits, whatToDoWithEmpties);
		else if (direction == ScanDirection.Reverse)
			return rsplit(original, delimiter, limitInMaximumNumberOfDelimiterSplits, whatToDoWithEmpties);
		throw BasicExceptionUtilities.newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(direction);
	}
	
	
	//	Old element predicate - based split :P
	//
	//	public static String[] split(String source, UnaryFunctionCharToBoolean delimiter, int limit, WhatToDoWithEmpties whatToDoWithEmpties)
	//	{
	//		//To-do this algorithm is good for streaming frames, but recode this for speed in the in-memory case
	//
	//		ArrayList<String> substrings = new ArrayList<String>();
	//
	//		char[] cs = source.toCharArray();
	//		StringBuilder buf = new StringBuilder();
	//
	//		for (int i = 0; i < cs.length; i++)
	//		{
	//			if (limit != -1 && substrings.size() >= limit)
	//				break;
	//
	//			char c = cs[i];
	//
	//			if (delimiter.f(c))
	//			{
	//				if (whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties || buf.length() != 0)
	//				{
	//					substrings.add(buf.toString());
	//					buf.setLength(0);
	//				}
	//			}
	//			else
	//			{
	//				buf.append(c);
	//			}
	//		}
	//
	//
	//		//The EOF-is-delimiter code pattern :>
	//		if (whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties || buf.length() != 0)
	//		{
	//			substrings.add(buf.toString());
	//			//buf.setLength(0);
	//		}
	//
	//
	//		return substrings.toArray(new String[substrings.size()]);
	//	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String[] split(String original, String delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (delimiter.length() == 0)
			throw new IllegalArgumentException();
		
		List<String> substrings = new ArrayList<>();
		
		int currentPos = 0;
		
		while (true)
		{
			if (limitInMaximumNumberOfDelimiterSplits != -1 && substrings.size() >= limitInMaximumNumberOfDelimiterSplits)
				break;
			
			int nextMatchNearside = original.indexOf(delimiter, currentPos);
			
			if (nextMatchNearside == -1)
				break;
			
			if (whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties || currentPos != nextMatchNearside)
				substrings.add(original.substring(currentPos, nextMatchNearside));
			
			currentPos = nextMatchNearside + delimiter.length();
		}
		
		if (whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties || currentPos != original.length())
			substrings.add(original.substring(currentPos));
		
		return substrings.toArray(new String[substrings.size()]);
	}
	
	
	public static String[] rsplit(String original, String delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (delimiter.length() == 0)
			throw new IllegalArgumentException();
		
		List<String> substrings = new ArrayList<>();
		
		int currentPos = original.length();
		int lastMark = original.length();
		
		while (true)
		{
			if (limitInMaximumNumberOfDelimiterSplits != -1 && substrings.size() >= limitInMaximumNumberOfDelimiterSplits)
				break;
			
			int nextMatchFarside = original.lastIndexOf(delimiter, currentPos);
			
			if (nextMatchFarside == -1)
				break;
			
			if (whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties || currentPos != nextMatchFarside)
				substrings.add(original.substring(nextMatchFarside + delimiter.length(), lastMark));
			
			//quirk of impl of scanning in that it uses partially L2R start-indexes in a R2L scanner makes this an asymmetry :P
			lastMark = nextMatchFarside;
			if (nextMatchFarside < delimiter.length())
			{
				break;
			}
			else
			{
				currentPos = nextMatchFarside-delimiter.length();
			}
		}
		
		if (whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties || currentPos != original.length())
			substrings.add(original.substring(0, lastMark));
		
		String[] a = substrings.toArray(new String[substrings.size()]);
		ArrayUtilities.reverse(a);
		return a;
	}
	
	
	
	public static String[] split(String original, String delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties, ScanDirection direction)
	{
		if (direction == ScanDirection.Forward)
			return split(original, delimiter, limitInMaximumNumberOfDelimiterSplits, whatToDoWithEmpties);
		else if (direction == ScanDirection.Reverse)
			return rsplit(original, delimiter, limitInMaximumNumberOfDelimiterSplits, whatToDoWithEmpties);
		throw BasicExceptionUtilities.newUnexpectedHardcodedEnumValueExceptionOrNullPointerException(direction);
	}
	
	
	public static String[] split(String original, String delimiter, int limitInMaximumNumberOfDelimiterSplits, ScanDirection direction)
	{
		return split(original, delimiter, limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties.LeaveInEmpties, direction);
	}
	
	public static String[] split(String original, String delimiter, int limit)
	{
		return split(original, delimiter, limit, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	public static String[] rsplit(String original, String delimiter, int limit)
	{
		return rsplit(original, delimiter, limit, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	
	
	public static String[] split(String original, String delimiter)
	{
		return split(original, delimiter, -1, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	public static String[] rsplit(String original, String delimiter)
	{
		return rsplit(original, delimiter, -1, WhatToDoWithEmpties.LeaveInEmpties);
	}
	
	
	
	
	
	
	public static String[] splitwhitespace(String original)
	{
		return splitwhitespace(original, -1);
	}
	
	public static String[] splitwhitespace(String original, int limitInMaximumNumberOfDelimiterSplits)
	{
		return split(original, WHITESPACE_PATTERN, limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties.LeaveOutEmpties);
	}
	
	public static String[] rsplitwhitespace(String original, int limitInMaximumNumberOfDelimiterSplits)
	{
		return rsplit(original, WHITESPACE_PATTERN, limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties.LeaveOutEmpties);
	}
	
	
	public static String[] splitlines(String s, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		//Todo support all three newlines, "\n", "\r", and "\r\n" more efficiently ^^''
		if (s.indexOf('\r') != -1)
			s = universalNewlines(s);
		
		return split(s, '\n', -1, whatToDoWithEmpties);
	}
	
	public static String[] splitlines(String s)
	{
		return splitlines(s, WhatToDoWithEmpties.LeaveInEmpties); //leaving them in keeps more information and allows us to reconstitute the original input; which is many important for (eg source code) rewriters! 0,0     the lossless option is usually a good default option methinks ^^
	}
	
	public static String[] splitlinesLeavingOffEmptyTrailingLine(String s)
	{
		if (s.isEmpty())
			return EmptyStringArray;
		else
			return splitlines(removeTrailingLineBreak(s));
	}
	
	public static String removeTrailingLineBreak(String s)
	{
		return rtrimstr(rtrimstr(rtrimstr(s, "\r\n"), "\n"), "\r");
	}
	
	/**
	 * @return {@link #splitlines(String)}.length  :33
	 */
	public static int countLines(CharSequence s)
	{
		return count(s, '\n') + 1;
	}
	
	
	public static String joinlines(String[] lines)
	{
		return joinStrings(lines, '\n');
	}
	
	public static String joinlines(Iterable<String> lines)
	{
		return joinStrings(lines, '\n');
	}
	
	
	public static String joinlinesIncludingTrailingNewline(String[] lines)
	{
		StringBuilder buff = new StringBuilder();
		for (String line : lines)
		{
			buff.append(line);
			buff.append('\n');
		}
		return buff.toString();
	}
	
	public static String joinlinesIncludingTrailingNewline(Iterable<String> lines)
	{
		StringBuilder buff = new StringBuilder();
		for (String line : lines)
		{
			buff.append(line);
			buff.append('\n');
		}
		return buff.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static <E> List<List<E>> split(List<E> original, Predicate<E> delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (whatToDoWithEmpties == null)
			throw new NullPointerException();
		
		
		boolean leaveInEmpties = whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties;
		
		int length = original.size();
		
		ArrayList<List<E>> tokens = new ArrayList<>(6);
		
		int tokenStart = 0;
		for (int i = 0; i < length && (limitInMaximumNumberOfDelimiterSplits == -1 || tokens.size() < limitInMaximumNumberOfDelimiterSplits); i++)
		{
			if (delimiter.test(original.get(i)))
			{
				if (leaveInEmpties || i - tokenStart != 0)
				{
					tokens.add(original.subList(tokenStart, i - tokenStart));
				}
				
				tokenStart = i + 1; //Token doesn't include the delimiter
			}
		}
		
		tokens.add(original.subList(tokenStart, length - tokenStart));
		
		return tokens;
	}
	
	public static <E> List<List<E>> rsplit(List<E> original, Predicate<E> delimiter, int limitInMaximumNumberOfDelimiterSplits, WhatToDoWithEmpties whatToDoWithEmpties)
	{
		if (whatToDoWithEmpties == null) throw new NullPointerException();
		
		
		boolean leaveInEmpties = whatToDoWithEmpties == WhatToDoWithEmpties.LeaveInEmpties;
		
		int length = original.size();
		
		ArrayList<List<E>> tokens = new ArrayList<>(6);
		int tokenEnd = length;
		for (int i = length-1; i >= 0 && (limitInMaximumNumberOfDelimiterSplits == -1 || tokens.size() < limitInMaximumNumberOfDelimiterSplits); i--)
		{
			if (delimiter.test(original.get(i)))
			{
				if (leaveInEmpties || tokenEnd-(i+1) != 0)
				{
					tokens.add(original.subList(i+1, tokenEnd-(i+1)));
				}
				
				tokenEnd = i;
			}
		}
		
		tokens.add(original.subList(0, tokenEnd));
		
		Collections.reverse(tokens);
		return tokens;
	}
	
	
	
	
	@Nullable
	public static String[] splitonceOrNull(String s, char del)
	{
		int i = s.indexOf(del);
		return i == -1 ? null : new String[]{s.substring(0, i), s.substring(i+1)};
	}
	
	
	
	
	
	
	
	
	public static String[] splitOnceCamelCaseName(String camelCaseName)
	{
		int c = findCapitalLetter(camelCaseName, 0);
		
		if (c == -1)
			return new String[]{camelCaseName};
		else
			return new String[]{camelCaseName.substring(0, c), camelCaseName.substring(c, camelCaseName.length())};
	}
	
	//Todo splitCamelCaseName()!
	
	
	public static int findCapitalLetter(String s, int startingIndex)
	{
		int n = s.length();
		for (int i = startingIndex; i < n; i++)
			if (Character.isUpperCase(s.charAt(i)))
				return i;
		return -1;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Produces an array of single-character strings for each character in the original string! ^_^
	 */
	public static String[] explodeString(String string)
	{
		String[] a = new String[string.length()];
		for (int i = 0; i < a.length; i++)
			a[i] = new String(new char[]{string.charAt(i)});
		return a;
	}
	
	
	
	
	public static int indexOf(Object originalString, UnaryFunctionCharToBoolean charTarget)
	{
		return indexOf(originalString, charTarget, 0, getLength(originalString));
	}
	
	public static int indexOf(Object originalString, UnaryFunctionCharToBoolean charTarget, int start)
	{
		return indexOf(originalString, charTarget, start, getLength(originalString) - start);
	}
	
	public static int indexOf(Object originalString, UnaryFunctionCharToBoolean charTarget, int start, int length)
	{
		char[] ca = textthingToPossiblyUnclonedCharArray(originalString);
		int e = start + length;
		for (int i = start; i < e; i++)
		{
			if (charTarget.f(ca[i]))
				return i;
		}
		
		return -1;
	}
	
	
	
	public static int rindexOf(Object originalString, UnaryFunctionCharToBoolean charTarget)
	{
		int n = getLength(originalString);
		return rindexOf(originalString, charTarget, n-1, n);
	}
	
	public static int rindexOf(Object originalString, UnaryFunctionCharToBoolean charTarget, int indexOfFirstCharacterToTest)
	{
		return rindexOf(originalString, charTarget, indexOfFirstCharacterToTest, indexOfFirstCharacterToTest+1);
	}
	
	public static int rindexOf(Object originalString, UnaryFunctionCharToBoolean charTarget, int indexOfFirstCharacterToTest, int numberOfCharactersToTest)
	{
		char[] ca = textthingToPossiblyUnclonedCharArray(originalString);
		int e = indexOfFirstCharacterToTest - numberOfCharactersToTest;
		for (int i = indexOfFirstCharacterToTest; i > e; i--)
		{
			if (charTarget.f(ca[i]))
				return i;
		}
		
		return -1;
	}
	
	
	
	@ThrowAwayValue
	public static IntegerList indexOfAllL(Object originalString, char target)
	{
		IntegerList indexes = new IntegerArrayList();
		
		char[] ca = textthingToPossiblyUnclonedCharArray(originalString);
		int len = ca.length;
		for (int i = 0; i < len; i++)
		{
			if (ca[i] == target)
				indexes.addInt(i);
		}
		
		return indexes;
	}
	
	
	@ThrowAwayValue
	public static IntegerList indexOfAllL(String originalString, String target)
	{
		IntegerList indexes = new IntegerArrayList();
		
		//Ordering of lines very important here XD'
		int position = 0;
		while (true)
		{
			position = originalString.indexOf(target, position+1);
			
			if (position == -1)
				break;
			
			indexes.addInt(position);
		}
		
		return indexes;
	}
	
	
	public static int[] indexOfAllA(Object originalString, char target)
	{
		return indexOfAllL(originalString, target).toIntArrayPossiblyLive();
	}
	
	public static int[] indexOfAllA(String originalString, String target)
	{
		return indexOfAllL(originalString, target).toIntArrayPossiblyLive();
	}
	
	
	
	
	
	
	
	
	
	
	public static String concatList(Iterable<?> tokens)
	{
		StringBuilder buff = new StringBuilder();
		for (Object e : tokens)
		{
			if (isTextthing(e))
				appendTextthing(buff, e);
			else
				buff.append(String.valueOf(e));
		}
		return buff.toString();
	}
	
	public static String concatArray(Object[] tokens)
	{
		StringBuilder buff = new StringBuilder();
		for (Object e : tokens)
		{
			if (isTextthing(e))
				appendTextthing(buff, e);
			else
				buff.append(String.valueOf(e));
		}
		return buff.toString();
	}
	
	public static String concatVarargs(Object... tokens)
	{
		StringBuilder buff = new StringBuilder();
		for (Object e : tokens)
		{
			if (isTextthing(e))
				appendTextthing(buff, e);
			else
				buff.append(String.valueOf(e));
		}
		return buff.toString();
	}
	
	
	
	
	public static int concatLength(Iterable<Object> tokens)
	{
		int total = 0;
		for (Object e : tokens)
		{
			if (isTextthing(e))
				total += getLength(e);
			else
				total += String.valueOf(e).length();
		}
		return total;
	}
	
	public static int concatLength(Object... tokens)
	{
		int total = 0;
		for (Object e : tokens)
		{
			if (isTextthing(e))
				total += getLength(e);
			else
				total += String.valueOf(e).length();
		}
		return total;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * -finds the unique String index identified by the given Line and Column numbers.
	 * Note that while String indices start at 0, Line and Column numbers start at 1.
	 */
	public static int getStringIndex(int line, int col, String text)
	{
		int pos = 0;
		
		int l = 0;
		while (++l < line)
		{
			pos = text.indexOf(SYSTEM_EOL, pos+1);
		}
		pos += (col - 1);
		
		return pos;
	}
	
	/**
	 * Converts a one-dimensional string index into a Line and Column number.
	 * Note that while String indices start at 0, Line and Column numbers start at 1.
	 * @param pos The string index
	 * @param text The string
	 * @return new int[]{line, col}
	 */
	public static int[] getLineAndCol(int pos, String text)
	{
		int line = 1;
		int col = 1;
		
		
		int cpos = text.indexOf(SYSTEM_EOL);
		int lpos = -1;
		while (cpos != -1 && cpos < pos)
		{
			lpos = cpos;
			cpos = text.indexOf(SYSTEM_EOL, cpos+1);
			line++;
		}
		
		col = cpos - lpos;
		
		return new int[]{line, col};
	}
	
	/**
	 * Converts a one-dimensional string index (0-based, of course) into a Line and Column number.
	 * @param pos The string index
	 * @param text The string
	 * @return Just the line number (1-based)
	 */
	public static int getLineNumber(int pos, String text)
	{
		int line = 1;
		//		int col = 1;
		
		
		int cpos = text.indexOf(SYSTEM_EOL);
		//		int lpos = -1;
		while (cpos != -1 && cpos < pos)
		{
			//			lpos = cpos;
			cpos = text.indexOf(SYSTEM_EOL, cpos+1);
			line++;
		}
		
		//		col = cpos - lpos;
		
		return line;
	}
	
	/**
	 * Converts a one-dimensional string index (0-based, of course) into a Line and Column number.
	 * @param pos The string index
	 * @param text The string
	 * @return Just the column number (1-based)
	 */
	public static int getColumnNumber(int pos, String text)
	{
		//		int line = 1;
		int col = 1;
		
		
		int cpos = text.indexOf(SYSTEM_EOL);
		int lpos = -1;
		while (cpos != -1 && cpos < pos)
		{
			lpos = cpos;
			cpos = text.indexOf(SYSTEM_EOL, cpos+1);
			//			line++;
		}
		
		col = cpos - lpos;
		
		return col;
	}
	
	
	/**
	 * Counts occurrences of a specified char in a string.
	 */
	public static int getCharCount(String text, char c)
	{
		int count = 0;
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
			if (chars[i] == c)
				count++;
		return count;
	}
	
	
	
	/**
	 * Counts types of characters in a String.
	 * @return An array of counts. The <code>char</code> value (casted to (int)) are the indexes, the count are the values
	 */
	public static int[] getCharCounts(String text)
	{
		int[] counts = new int[NUMBER_OF_CHARACTERS];
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
			counts[chars[i]]++;
		return counts;
	}
	
	
	/**
	 * Counts the number of times 'c' occurs in succession at the beginning of the string.
	 */
	public static int getLeadingCharCount(String text, char c)
	{
		char[] chars = text.toCharArray();
		for (int i = 0; i < chars.length; i++)
			if (chars[i] != c)
				return i;
		return chars.length;
	}
	
	
	
	
	
	//<Uniformity
	/**
	 * Tests if there is not more than 1 species of character in a given string.<br>
	 * All uniformity tests are defined to return true if given <code>""</code> and to throw a {@link NullPointerException} if given a <code>null</code> string.
	 */
	public static boolean isUniform(String text)
	{
		if (text.length() == 0)
			return true;
		
		return isUniform(text, text.charAt(0));
	}
	
	/**
	 * Tests if a string is composed entirely of the given character.
	 */
	public static boolean isUniform(String text, char c)
	{
		int len = text.length();
		for (int i = 0; i < len; i++)
			if (text.charAt(i) != c)
				return false;
		return true;
	}
	
	/**
	 * Tests if a string is composed of only characters in a given set.
	 */
	public static boolean isUniform(String text, char[] alphabet)
	{
		int len = text.length();
		char curr = 0;
		StringLoop: for (int i = 0; i < len; i++)
		{
			curr = text.charAt(i);
			AlphabetLoop: for (int e = 0; e < alphabet.length; e++)
				if (curr == alphabet[e])
					continue StringLoop;
			return false; //Flow can only reach here if $curr didn't match any members of the given alphabet
		}
		return true;
	}
	
	/**
	 * Tests if a string is composed of only characters in a given set.
	 */
	public static boolean isUniform(String text, CharSequence alphabet)
	{
		int alphabetLength = alphabet.length();
		int len = text.length();
		char curr = 0;
		StringLoop: for (int i = 0; i < len; i++)
		{
			curr = text.charAt(i);
			AlphabetLoop: for (int e = 0; e < alphabetLength; e++)
				if (curr == alphabet.charAt(e))
					continue StringLoop;
			return false; //Flow can only reach here if $curr didn't match any members of the given alphabet
		}
		return true;
	}
	
	
	/**
	 * Tests if there is not a single char which doesn't meet the criteria in {@link Character#isWhitespace(char)} present within the given string.
	 */
	public static boolean isUniformWhitespace(String text)
	{
		int len = text.length();
		for (int i = 0; i < len; i++)
			if (!Character.isWhitespace(text.charAt(i)))
				return false;
		return true;
	}
	
	
	public static boolean isUniform(Object text, UnaryFunctionCharToBoolean characterPattern)
	{
		if (text == null)
			throw new NullPointerException();
		
		char[] ca = textthingToPossiblyUnclonedCharArray(text);
		int len = ca.length;
		
		for (int i = 0; i < len; i++)
			if (!characterPattern.f(ca[i]))
				return false;
		return true;
	}
	//Uniformity>
	
	
	
	
	
	public static boolean equals(CharSequence a, CharSequence b)
	{
		return a == b || (a != null && a.equals(b));
	}
	
	public static boolean equals(CharSequence a, char[] b)
	{
		if (a == null && b == null)
			return true;
		else if (a == null || b == null)
			return false;
		else if (a.length() != b.length)
			return false;
		else
			for (int i = 0; i < b.length; i++)
				if (a.charAt(i) != b[i])
					return false;
		return true;
	}
	
	public static boolean equals(char[] a, CharSequence b)
	{
		if (a == null && b == null)
			return true;
		else if (a == null || b == null)
			return false;
		else if (a.length != b.length())
			return false;
		else
			for (int i = 0; i < a.length; i++)
				if (a[i] != b.charAt(i))
					return false;
		return true;
	}
	
	public static boolean equals(char[] a, char[] b)
	{
		if (a == b)
			return true;
		else if (a == null || b == null)
			return false;
		else if (a.length != b.length)
			return false;
		else
			for (int i = 0; i < a.length; i++)
				if (a[i] != b[i])
					return false;
		return true;
	}
	
	
	
	public static boolean equalsIgnoreCase(CharSequence a, CharSequence b)
	{
		if (a == null && b == null)
			return true;
		else if (a == null || b == null)
			return false;
		else if (a.length() != b.length())
			return false;
		else
			for (int i = 0; i < a.length(); i++)
				if (Character.toLowerCase(a.charAt(i)) != Character.toLowerCase(b.charAt(i)))
					return false;
		return true;
	}
	
	public static boolean equalsIgnoreCase(CharSequence a, char[] b)
	{
		if (a == null && b == null)
			return true;
		else if (a == null || b == null)
			return false;
		else if (a.length() != b.length)
			return false;
		else
			for (int i = 0; i < b.length; i++)
				if (Character.toLowerCase(a.charAt(i)) != Character.toLowerCase(b[i]))
					return false;
		return true;
	}
	
	public static boolean equalsIgnoreCase(char[] a, CharSequence b)
	{
		if (a == null && b == null)
			return true;
		else if (a == null || b == null)
			return false;
		else if (a.length != b.length())
			return false;
		else
			for (int i = 0; i < a.length; i++)
				if (Character.toLowerCase(a[i]) != Character.toLowerCase(b.charAt(i)))
					return false;
		return true;
	}
	
	public static boolean equalsIgnoreCase(char[] a, char[] b)
	{
		if (a == b)
			return true;
		else if (a == null || b == null)
			return false;
		else if (a.length != b.length)
			return false;
		else
			for (int i = 0; i < a.length; i++)
				if (Character.toLowerCase(a[i]) != Character.toLowerCase(b[i]))
					return false;
		return true;
	}
	
	public static boolean isCharactersEqualCaseInsensitively(char a, char b)
	{
		//Copied from java.lang.String:
		/*
	    // Unfortunately, conversion to uppercase does not work properly
	    // for the Georgian alphabet, which has strange rules about case
	    // conversion.  So we need to make one last check before
	    // exiting.
		 */
		return a == b || Character.toUpperCase(a) == Character.toUpperCase(b) || Character.toLowerCase(a) == Character.toLowerCase(b);
	}
	
	
	public static boolean arrayMatchesCaseInsensitive(char[] a, int aOffset, char[] b, int bOffset, int length) throws IndexOutOfBoundsException
	{
		if (aOffset < 0 || aOffset > a.length) aOffset = SmallIntegerMathUtilities.progmod(aOffset, a.length);
		if (bOffset < 0 || bOffset > b.length) bOffset = SmallIntegerMathUtilities.progmod(bOffset, b.length);
		
		if (aOffset+length > a.length)
			throw new IndexOutOfBoundsException();
		if (bOffset+length > b.length)
			throw new IndexOutOfBoundsException();
		
		for (int i = 0; i < length; i++)
			if (!isCharactersEqualCaseInsensitively(a[aOffset+i], b[bOffset+i]))
				return false;
		return true;
	}
	
	
	
	public static boolean startsWith(Object longer, Object shorter)
	{
		if (longer instanceof String && shorter instanceof String)
		{
			return ((String)longer).startsWith((String)shorter);
		}
		
		//No more speedy shortcuts are available (although if these are actually raw char arrays, this is the same as String.startsWith and is no less speedy! ^_^ )    (I think XD')
		else
		{
			if (getLength(shorter) > getLength(longer))
				return false;
			
			char[] l = textthingToPossiblyUnclonedCharArray(longer);
			char[] s = textthingToPossiblyUnclonedCharArray(shorter);
			
			int minlen = s.length; //we already verified that
			
			for (int i = 0; i < minlen; i++)
			{
				if (l[i] != s[i])
					return false;
			}
			
			return true;
		}
	}
	
	public static boolean endsWith(Object longer, Object shorter)
	{
		if (longer instanceof String && shorter instanceof String)
		{
			return ((String)longer).startsWith((String)shorter);
		}
		
		//No more speedy shortcuts are available
		else
		{
			if (getLength(shorter) > getLength(longer))
				return false;
			
			char[] l = textthingToPossiblyUnclonedCharArray(longer);
			char[] s = textthingToPossiblyUnclonedCharArray(shorter);
			
			int minlen = s.length; //we already verified that
			
			for (int i = 0; i < minlen; i++)
			{
				if (l[l.length-i-1] != s[s.length-i-1])
					return false;
			}
			
			return true;
		}
	}
	
	
	
	public static boolean startsWithCaseInsensitively(Object longer, Object shorter)
	{
		if (longer instanceof String && shorter instanceof String)
		{
			return ((String)longer).startsWith((String)shorter);
		}
		
		//No more speedy shortcuts are available
		else
		{
			if (getLength(shorter) > getLength(longer))
				return false;
			
			char[] l = textthingToPossiblyUnclonedCharArray(longer);
			char[] s = textthingToPossiblyUnclonedCharArray(shorter);
			
			int minlen = s.length; //we already verified that
			
			for (int i = 0; i < minlen; i++)
			{
				if (Character.toLowerCase(l[i]) != Character.toLowerCase(s[i]))
					return false;
			}
			
			return true;
		}
	}
	
	public static boolean endsWithCaseInsensitively(Object longer, Object shorter)
	{
		if (longer instanceof String && shorter instanceof String)
		{
			return ((String)longer).startsWith((String)shorter);
		}
		
		//No more speedy shortcuts are available
		else
		{
			if (getLength(shorter) > getLength(longer))
				return false;
			
			char[] l = textthingToPossiblyUnclonedCharArray(longer);
			char[] s = textthingToPossiblyUnclonedCharArray(shorter);
			
			int minlen = s.length; //we already verified that
			
			for (int i = 0; i < minlen; i++)
			{
				if (Character.toLowerCase(l[l.length-i-1]) != Character.toLowerCase(s[s.length-i-1]))
					return false;
			}
			
			return true;
		}
	}
	
	
	
	public static boolean contains(String s, UnaryFunctionCharToBoolean test)
	{
		return contains(s.toCharArray(), test);
	}
	
	public static boolean contains(char[] s, UnaryFunctionCharToBoolean test)
	{
		for (char c : s)
			if (test.f(c))
				return true;
		return false;
	}
	
	public static boolean contains(String s, UnaryFunctionCharToBoolean... tests)
	{
		return contains(s.toCharArray(), tests);
	}
	
	public static boolean contains(char[] s, UnaryFunctionCharToBoolean... tests)
	{
		for (char c : s)
			for (UnaryFunctionCharToBoolean test : tests)
				if (test.f(c))
					return true;
		return false;
	}
	
	
	
	public static boolean contains(String s, char c)
	{
		return s.indexOf(c) != -1;
	}
	
	public static boolean containsWhitespace(String s)
	{
		return contains(s, WHITESPACE_PATTERN);
	}
	
	
	
	
	
	
	
	public static int count(CharSequence s, UnaryFunctionCharToBoolean test)
	{
		int count = 0;
		
		final int n = s.length();
		for (int i = 0; i < n; i++)
			if (test.f(s.charAt(i)))
				count++;
		
		return count;
	}
	
	public static int count(CharSequence s, char c)
	{
		int count = 0;
		
		final int n = s.length();
		for (int i = 0; i < n; i++)
			if (s.charAt(i) == c)
				count++;
		
		return count;
	}
	
	
	
	
	
	public static boolean forAll(UnaryFunctionCharToBoolean test, CharSequence s)
	{
		return forAll(test, s, 0, s.length());
	}
	
	public static boolean forAll(char c, CharSequence s)
	{
		return forAll(c, s, 0, s.length());
	}
	
	
	
	public static boolean forAll(UnaryFunctionCharToBoolean test, CharSequence s, int offset, int length)
	{
		final int e = offset + length;
		for (int i = offset; i < e; i++)
			if (!test.f(s.charAt(i)))
				return false;
		
		return true;
	}
	
	public static boolean forAll(char c, CharSequence s, int offset, int length)
	{
		final int e = offset + length;
		for (int i = offset; i < e; i++)
			if (s.charAt(i) != c)
				return false;
		
		return true;
	}
	
	
	
	public static boolean forAny(UnaryFunctionCharToBoolean test, CharSequence s)
	{
		final int n = s.length();
		for (int i = 0; i < n; i++)
			if (test.f(s.charAt(i)))
				return true;
		
		return false;
	}
	
	public static boolean forAny(char c, CharSequence s)
	{
		final int n = s.length();
		for (int i = 0; i < n; i++)
			if (s.charAt(i) == c)
				return true;
		
		return false;
	}
	
	
	
	
	
	public static boolean isAllWhitespace(CharSequence s)
	{
		return forAll(WHITESPACE_PATTERN, s);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static boolean startsWithPrefixAndWhitespaceOrEOF(String mainString, String prefix)
	{ return startsWithPrefixAndWhitespace(mainString, prefix, true); }
	
	public static boolean startsWithPrefixAndWhitespaceNoEOF(String mainString, String prefix)
	{ return startsWithPrefixAndWhitespace(mainString, prefix, false); }
	
	public static boolean startsWithPrefixAndWhitespace(String mainString, String prefix, boolean onEof)
	{
		if (!mainString.startsWith(prefix))
			return false;
		
		int n = prefix.length();
		
		if (mainString.length() == n)
			return onEof;
		
		if (Character.isWhitespace(mainString.charAt(n)))
			return true;
		
		return false;
	}
	
	
	
	
	
	
	
	public static String applyCaseMap(Object original, boolean[] caseMap)
	{
		if (caseMap.length != getLength(original))
			throw new IllegalArgumentException("wrong lengths!");
		
		char[] s = textthingToNewCharArray(original);
		applyCaseMap(s, caseMap);
		return new String(s);
	}
	
	public static void applyCaseMap(char[] s, boolean[] caseMap)
	{
		if (caseMap.length != s.length)
			throw new IllegalArgumentException("wrong lengths!");
		
		for (int i = 0; i < s.length; i++)
		{
			s[i] = caseMap[i] ? Character.toUpperCase(s[i]) : Character.toLowerCase(s[i]);
		}
	}
	
	
	
	public static String alltrimMultiple(String str, char[] targets)
	{
		char[] oldc = str.toCharArray();
		char[] newc = new char[oldc.length];
		int newcLen = 0;
		int i = 0, e = 0;
		char c = 0;
		
		for (i = 0; i < oldc.length; i++)
		{
			c = oldc[i];
			
			e = 0;
			for (e = 0; e < targets.length; e++)
			{
				if (c == targets[e])
				{
					//skip this character in oldc
					break;
				}
			}
			
			if (e == targets.length)
			{
				//if no targets matched c
				newc[newcLen] = c;
				newcLen++;
			}
		}
		
		return new String(newc, 0, newcLen);
	}
	
	public static String alltrim(String str, char target)
	{
		char[] oldc = str.toCharArray();
		char[] newc = new char[oldc.length];
		int newcLen = 0;
		int i = 0;
		char c = 0;
		
		for (i = 0; i < oldc.length; i++)
		{
			c = oldc[i];
			
			if (c == target)
			{
				//skip this character in oldc
			}
			else
			{
				//if no targets matched c
				newc[newcLen] = c;
				newcLen++;
			}
		}
		
		return new String(newc, 0, newcLen);
	}
	
	
	public static String ltrim(String s, char c)
	{
		if (s == null)
			return null;
		int len = s.length();
		if (len == 0)
			return s;
		
		int firstKeeper = len;
		for (int i = 0; i < len && firstKeeper == len; i++)
			if (c != s.charAt(i))
				firstKeeper = i;
		return s.substring(firstKeeper);
	}
	
	public static String ltrim(String s, UnaryFunctionCharToBoolean pattern)
	{
		if (s == null)
			return null;
		int len = s.length();
		if (len == 0)
			return s;
		
		int firstKeeper = len;
		for (int i = 0; i < len && firstKeeper == len; i++)
			if (!pattern.f(s.charAt(i)))
				firstKeeper = i;
		return s.substring(firstKeeper);
	}
	
	
	public static String rtrim(String s, char c)
	{
		if (s == null)
			return null;
		int len = s.length();
		if (len == 0)
			return s;
		
		int lastKeeper = -1;
		for (int i = len-1; i >= 0 && lastKeeper == -1; i--)
			if (c != s.charAt(i))
				lastKeeper = i;
		return s.substring(0, lastKeeper+1);
	}
	
	public static String rtrim(String s, UnaryFunctionCharToBoolean pattern)
	{
		if (s == null)
			return null;
		int len = s.length();
		if (len == 0)
			return s;
		
		int lastKeeper = -1;
		for (int i = len-1; i >= 0 && lastKeeper == -1; i--)
			if (!pattern.f(s.charAt(i)))
				lastKeeper = i;
		return s.substring(0, lastKeeper+1);
	}
	
	
	public static String trim(String s, char c)
	{
		return ltrim(rtrim(s, c), c);
	}
	
	public static String trim(String s, UnaryFunctionCharToBoolean pattern)
	{
		return ltrim(rtrim(s, pattern), pattern);
	}
	
	
	
	public static String ltrim(String s)
	{
		return ltrim(s, WHITESPACE_PATTERN);
	}
	public static String rtrim(String s)
	{
		return rtrim(s, WHITESPACE_PATTERN);
	}
	public static String trim(String s)
	{
		return trim(s, WHITESPACE_PATTERN);
	}
	
	
	
	
	public static String ltrimstr(String str, String trimmand)
	{
		if (str.startsWith(trimmand))
			return str.substring(trimmand.length());
		else
			return str;
	}
	
	public static String rtrimstr(String str, String trimmand)
	{
		if (str.endsWith(trimmand))
			return str.substring(0, str.length() - trimmand.length());
		else
			return str;
	}
	
	
	public static String ltrimstrCaseInsensitive(String str, String trimmand)
	{
		if (str.startsWith(trimmand))
			return str.substring(trimmand.length());
		else
			return str;
	}
	
	public static String rtrimstrCaseInsensitive(String str, String trimmand)
	{
		if (str.endsWith(trimmand))
			return str.substring(0, str.length() - trimmand.length());
		else
			return str;
	}
	
	
	
	
	
	
	
	
	public static String ltrimstrRP(String str, String trimmand) throws NoStringToRemoveReturnPath
	{
		if (str.startsWith(trimmand))
			return str.substring(trimmand.length());
		else
			throw NoStringToRemoveReturnPath.I;
	}
	
	public static String rtrimstrRP(String str, String trimmand) throws NoStringToRemoveReturnPath
	{
		if (str.endsWith(trimmand))
			return str.substring(0, str.length() - trimmand.length());
		else
			throw NoStringToRemoveReturnPath.I;
	}
	
	
	
	/* <<<
	 * srp
	 * 
	 * NoStringToRemoveReturnPath
	 */
	public static class NoStringToRemoveReturnPath
	extends SingletonReturnPath
	{
		private static final long serialVersionUID = 1L;
		
		public static final NoStringToRemoveReturnPath I = new NoStringToRemoveReturnPath();
		protected NoStringToRemoveReturnPath() {}
		
		
		public static class NoStringToRemoveException
		extends RuntimeException
		{
			private static final long serialVersionUID = 1L;
			
			public NoStringToRemoveException()
			{
				super();
			}
			
			public NoStringToRemoveException(String message)
			{
				super(message);
			}
			
			public NoStringToRemoveException(Throwable cause)
			{
				super(cause);
			}
			
			public NoStringToRemoveException(String message, Throwable cause)
			{
				super(message, cause);
			}
		}
		
		@Override
		public NoStringToRemoveException toException()
		{
			return new NoStringToRemoveException();
		}
	}
	// >>>
	
	
	
	
	
	
	
	
	
	public static String lmatchingStr(String str, UnaryFunctionCharToBoolean charPattern)
	{
		int length = str.length();
		
		int lengthOfMatching;
		{
			lengthOfMatching = length;  //the value if pattern is true for all ^_~
			
			for (int i = 0; i < length; i++)
			{
				if (!charPattern.f(str.charAt(i)))
				{
					lengthOfMatching = i;
					break;
				}
			}
		}
		
		return str.substring(0, lengthOfMatching);
	}
	
	public static String rmatchingStr(String str, UnaryFunctionCharToBoolean charPattern)
	{
		int length = str.length();
		
		int lengthOfMatching;
		{
			lengthOfMatching = length;  //the value if pattern is true for all ^_~
			
			for (int i = length; i > 0; i--)
			{
				if (!charPattern.f(str.charAt(i-1)))
				{
					lengthOfMatching = length - i;
					break;
				}
			}
		}
		
		return str.substring(length - lengthOfMatching);
	}
	
	
	
	public static String lmatchingWhitespace(String str)
	{
		return lmatchingStr(str, WHITESPACE_PATTERN);
	}
	
	public static String rmatchingWhitespace(String str)
	{
		return rmatchingStr(str, WHITESPACE_PATTERN);
	}
	
	
	
	
	
	
	
	/**
	 * The capitalization rules (and pretty much everything else!) in {@link StringUtilities} are intended for use in computer formats that happen to use ASCII (eg, converting a field name to a getter).
	 * For actual natural human language, use HumanLanguageUtilities.
	 */
	public static String capitalize(String s)
	{
		if (s == null || s.isEmpty())
			return s;
		else if (s.length() == 1)
			return String.valueOf(Character.toUpperCase(s.charAt(0)));
		else
			return Character.toUpperCase(s.charAt(0)) + s.substring(1);
	}
	
	/**
	 * The capitalization rules (and pretty much everything else!) in {@link StringUtilities} are intended for use in computer formats that happen to use ASCII (eg, converting a field name to a getter).
	 * For actual natural human language, use HumanLanguageUtilities.
	 */
	public static String decapitalize(String s)
	{
		if (s == null || s.isEmpty())
			return s;
		else if (s.length() == 1)
			return String.valueOf(Character.toLowerCase(s.charAt(0)));
		else
			return Character.toLowerCase(s.charAt(0)) + s.substring(1);
	}
	
	
	public static void appendMultiple(StringBuilder out, char c, int count)
	{
		for (int i = 0; i < count; i++)
			out.append(c);
	}
	
	public static String mul(char c, int count)
	{
		if (count <= 0)
			return "";
		
		char[] a = new char[count];
		for (int i = 0; i < count; i++)
			a[i] = c;
		return new String(a);
	}
	
	public static String mul(String s, int count)
	{
		if (count <= 0)
			return "";
		
		char[] c = s.toCharArray();
		
		char[] a = new char[c.length * count];
		for (int i = 0; i < count; i++)
			System.arraycopy(c, 0, a, i*c.length, c.length);
		return new String(a);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static int getMaxLength(CharSequence... strings)
	{
		int m = 0;
		for (CharSequence x : strings)
			if (x.length() > m)
				m = x.length();
		return m;
	}
	
	public static int getMaxLength(Iterable<CharSequence> strings)
	{
		int m = 0;
		for (CharSequence x : strings)
			if (x.length() > m)
				m = x.length();
		return m;
	}
	
	public static int getMaxEnumNameLength(Class<? extends Enum> enumClass)
	{
		int m = 0;
		for (Enum e : enumClass.getEnumConstants())
			if (e.name().length() > m)
				m = e.name().length();
		return m;
	}
	
	public static int getMaxEnumTostringLength(Class<? extends Enum> enumClass)
	{
		int m = 0;
		for (Enum e : enumClass.getEnumConstants())
			if (e.toString().length() > m)
				m = e.toString().length();
		return m;
	}
	
	
	
	
	
	
	
	
	
	public static String concatLines(String... lines)
	{
		return joinStrings(lines, '\n');
		
		//		if (lines.length == 0)
		//			return "";
		//
		//		StringBuilder rv = new StringBuilder();
		//		for (String line : lines)
		//		{
		//			rv.append(line);
		//			rv.append('\n');
		//		}
		//		return rv.toString();
	}
	
	public static String concatLines(Iterable<String> lines)
	{
		return joinStrings(lines, '\n');
		
		//		StringBuilder rv = new StringBuilder();
		//		for (String line : lines)
		//		{
		//			rv.append(line);
		//			rv.append('\n');
		//		}
		//		return rv.toString();
	}
	
	
	
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue  //Future-proofing ^^'
	public static char[] universalNewlinesCharArrayOPC(char[] oldc)
	{
		//Todo redo this with a better algorithm that marks position ranges and uses System.arraycopy over them, instead of doing it character-by-character :>
		
		int oldLength = oldc.length;
		
		
		//Check that it needs to actually be modified! XD
		{
			boolean clean = true;
			
			for (char c : oldc)
			{
				if (c == '\r')
				{
					clean = false;
					break;
				}
			}
			
			
			//I think this's pretty self-explanatory code right here! ^w^  XD    maybe :D?
			if (clean)
				return oldc;
		}
		
		
		char[] newc = new char[oldLength];
		
		
		char c = 0;
		int e = 0;
		for (int i = 0; i < oldc.length; i++)
		{
			c = oldc[i];
			if (c == '\r')
			{
				if (i < oldc.length-1)
				{
					if (oldc[i+1] == '\n')
						i++; //skip the '\n' in "\r\n"
				}
				newc[e] = '\n'; //"\r" alone gets converted just like "\r\n"
				e++;
			}
			else
			{
				//'\n' just goes straight through
				newc[e] = c;
				e++;
			}
		}
		
		int newLength = e;
		
		return newLength == oldLength ? newc : ArrayUtilities.slice(newc, 0, newLength);
	}
	
	
	@PossiblySnapshotPossiblyLiveValue  //Future-proofing ^^'
	public static String universalNewlines(String original)
	{
		char[] ca0 = original.toCharArray();
		char[] ca1 = universalNewlinesCharArrayOPC(ca0);
		
		if (ca1 == ca0)
			return original;
		else
			return new String(ca1);
	}
	
	
	
	
	
	
	
	
	
	
	
	//TODO case-insensitivizing dictionaries :>   (one that simply lowers or uppers or title-cases all the things; one that preserves the first key case put() into the dict; and one that records all distinct patterns of case)    (..if you wanted to make those XD)
	
	
	
	
	//TODO CharBuffer!  (already included somewhat by virtue of CharSequence :> )
	
	public static interface CharArrayableCharSequence
	extends CharSequence
	{
		public char[] toCharArray();
		public void copyIntoCharArray(int offsetHere, char[] dest, int offsetInDest, int length); //Todo grandfathering for this :>
	}
	
	
	//	//Which does NOT implement CharSequence in my version at least! ><!
	//	public static final Class Softlinked_Class_Jython_PyString = null; //AngryReflectionUtility.forName("org.python.core.PyString");
	
	
	/**
	 * + note: returns <code>true</code> if passed <code>null</code>  :>
	 */
	public static boolean isTextthing(Object thing)
	{
		return
		thing == null ||
		thing instanceof CharSequence ||
		thing instanceof char[] ||
		thing instanceof Character
		//		(Softlinked_Class_Jython_PyString != null && Softlinked_Class_Jython_PyString.isInstance(thing))
		;
	}
	
	public static void checkValidTextthing(Object thing) throws ClassCastException
	{
		if (!isTextthing(thing))
			throw new ClassCastException(ObjectUtilities.getClassNameNT(thing));
	}
	
	public static Object normalizeTextthing(Object textthing)
	{
		//		if (Softlinked_Class_Jython_PyString != null && Softlinked_Class_Jython_PyString.isInstance(textthing))
		//			return textthing.toString(); //this is fast; just a field accessor ^_^   (in this version of Jython I'm looking at here, anyway)
		//		else
		return textthing;
	}
	
	public static char[] textthingToCharArray(Object textthing, boolean duplicate)
	{
		textthing = normalizeTextthing(textthing);
		
		if (textthing instanceof String) //important that this comes before CharSequence!!
		{
			return ((String)textthing).toCharArray();
		}
		else if (textthing instanceof char[])
		{
			return duplicate ? (char[]) ((char[])textthing).clone() : (char[])textthing;
		}
		else if (textthing instanceof StringBuilder) //important that this comes before CharSequence!!
		{
			char[] dest = new char[((StringBuilder)textthing).length()];
			((StringBuilder)textthing).getChars(0, ((StringBuilder)textthing).length(), dest, 0);
			return dest;
		}
		else if (textthing instanceof StringBuffer) //important that this comes before CharSequence!!
		{
			char[] dest = new char[((StringBuffer)textthing).length()];
			((StringBuffer)textthing).getChars(0, ((StringBuffer)textthing).length(), dest, 0);
			return dest;
		}
		else if (textthing instanceof CharArrayableCharSequence)
		{
			return ((CharArrayableCharSequence)textthing).toCharArray(); //better way :>
		}
		else if (textthing instanceof CharSequence)
		{
			return textthing.toString().toCharArray(); //horribly wasteful! ;_;!
		}
		else if (textthing instanceof Character)
		{
			char[] dest = new char[1];
			dest[0] = (Character)textthing;
			return dest;
		}
		else if (textthing == null)
		{
			return null;
		}
		else
		{
			throw new ClassCastException(ObjectUtilities.getClassNameNT(textthing));
		}
	}
	
	public static char[] textthingToPossiblyUnclonedCharArray(Object textthing)
	{
		return textthingToCharArray(textthing, false);
	}
	
	public static char[] textthingToNewCharArray(Object textthing)
	{
		return textthingToCharArray(textthing, true);
	}
	
	
	@Nullable
	public static String textthingToString(@Nullable Object textthing)
	{
		textthing = normalizeTextthing(textthing);
		
		if (textthing == null)
			return null;
		else if (textthing instanceof String)
			return (String)textthing;
		else if (textthing instanceof CharSequence)
			return textthing.toString();
		else if (textthing instanceof Character)
			return String.valueOf(textthing);
		else if (textthing instanceof char[])
			return String.valueOf((char[])textthing);
		else
			throw new ClassCastException(ObjectUtilities.getClassNameNT(textthing));
	}
	
	
	@Nullable
	public static CharSequence textthingToCharSequence(@Nullable Object textthing)
	{
		textthing = normalizeTextthing(textthing);
		
		if (textthing == null)
			return null;
		else if (textthing instanceof CharSequence)
			return (CharSequence)textthing;
		else if (textthing instanceof Character)
			return String.valueOf(textthing);
		else if (textthing instanceof char[])
			return String.valueOf((char[])textthing);
		else
			throw new ClassCastException(ObjectUtilities.getClassNameNT(textthing));
	}
	
	
	public static int getLength(@Nonnull Object textthing)
	{
		textthing = normalizeTextthing(textthing);
		
		if (textthing == null)
			throw new NullPointerException();
		else if (textthing instanceof CharSequence)
			return ((CharSequence)textthing).length();
		else if (textthing instanceof char[])
			return ((char[])textthing).length;
		else if (textthing instanceof Character)
			return 1;
		else
			throw new ClassCastException(ObjectUtilities.getClassNameNT(textthing));
	}
	
	
	public static char getSingleChar(Object textthing) throws IllegalArgumentException
	{
		textthing = normalizeTextthing(textthing);
		
		if (textthing instanceof CharSequence)
		{
			if (((CharSequence)textthing).length() != 1)
				throw new IllegalArgumentException("length != 1! 0_0 :   "+repr(textthing));
			return ((CharSequence)textthing).charAt(0);
		}
		else if (textthing instanceof char[])
		{
			if (((char[])textthing).length != 1)
				throw new IllegalArgumentException("length != 1! 0_0 :   "+repr(textthing));
			return ((char[])textthing)[0];
		}
		else if (textthing instanceof Character)
		{
			return (Character)textthing;
		}
		else if (textthing == null)
		{
			throw new NullPointerException();
		}
		else
		{
			throw new ClassCastException(ObjectUtilities.getClassNameNT(textthing));
		}
	}
	
	/**
	 * @return null if zero-lengthed or input was null :>
	 */
	public static Character getSingleCharacter(Object textthing) throws IllegalArgumentException
	{
		textthing = normalizeTextthing(textthing);
		
		if (textthing instanceof CharSequence)
		{
			if (((CharSequence)textthing).length() != 1)
				return null;
			else
				return ((CharSequence)textthing).charAt(0);
		}
		else if (textthing instanceof char[])
		{
			if (((CharSequence)textthing).length() != 1)
				return null;
			else
				return ((char[])textthing)[0];
		}
		else if (textthing instanceof Character)
		{
			return (Character)textthing;
		}
		else if (textthing == null)
		{
			return null;
		}
		else
		{
			throw new ClassCastException(ObjectUtilities.getClassNameNT(textthing));
		}
	}
	
	
	public static char charAt(Object textthing, int index) throws IndexOutOfBoundsException
	{
		textthing = normalizeTextthing(textthing);
		
		if (textthing instanceof CharSequence)
		{
			return ((CharSequence)textthing).charAt(index);
		}
		else if (textthing instanceof char[])
		{
			return ((char[])textthing)[index];
		}
		else if (textthing instanceof Character)
		{
			if (index < 0)
				throw new IndexOutOfBoundsException("negative index! :p  (charAt)");
			if (index > 0)
				throw new IndexOutOfBoundsException("too-large index! :p  (charAt)");
			
			return (Character)textthing;
		}
		else if (textthing == null)
		{
			throw new NullPointerException();
		}
		else
		{
			throw new ClassCastException(ObjectUtilities.getClassNameNT(textthing));
		}
	}
	
	
	
	public static void appendTextthing(Object recipient, Object thingToAppend)
	{
		recipient = normalizeTextthing(recipient);
		thingToAppend = normalizeTextthing(thingToAppend);
		
		if (recipient == null)
			throw new NullPointerException();
		
		if (thingToAppend == null)
			thingToAppend = "null";
		
		if (recipient instanceof StringBuilder)
		{
			if (thingToAppend instanceof CharSequence)
				((StringBuilder)recipient).append((CharSequence)thingToAppend); //handles subtypes ^_^
			else if (thingToAppend instanceof char[])
				((StringBuilder)recipient).append((char[])thingToAppend);
			else if (thingToAppend instanceof Character)
				((StringBuilder)recipient).append(((Character)thingToAppend).charValue());
			else
				throw new ClassCastException(ObjectUtilities.getClassNameNT(thingToAppend));
		}
		else if (recipient instanceof StringBuffer)
		{
			if (thingToAppend instanceof CharSequence)
				((StringBuffer)recipient).append((CharSequence)thingToAppend); //handles subtypes ^_^
			else if (thingToAppend instanceof char[])
				((StringBuffer)recipient).append((char[])thingToAppend);
			else if (thingToAppend instanceof Character)
				((StringBuffer)recipient).append(((Character)thingToAppend).charValue());
			else
				throw new ClassCastException(ObjectUtilities.getClassNameNT(thingToAppend));
		}
		else if (recipient instanceof Appendable)
		{
			try
			{
				if (thingToAppend instanceof CharSequence)
					((Appendable)recipient).append((CharSequence)thingToAppend); //handles subtypes ^_^
				else if (thingToAppend instanceof char[])
					((Appendable)recipient).append(new String((char[])thingToAppend));
				else if (thingToAppend instanceof Character)
					((Appendable)recipient).append(((Character)thingToAppend).charValue());
				else
					throw new ClassCastException(ObjectUtilities.getClassNameNT(thingToAppend));
			}
			catch (IOException exc)
			{
				//throw new IORuntimeException(exc);
				throw new WrappedThrowableRuntimeException(exc);
			}
		}
		else
		{
			throw new ClassCastException(ObjectUtilities.getClassNameNT(recipient));
		}
	}
	
	
	
	
	public static byte[] textthingEncode(Object textthing, Charset encoding)
	{
		//Todo Any better way?  (oh, well there's one for if textthing instanceof CharBuffer :> )
		return textthingToString(textthing).getBytes(encoding);
	}
	
	public static byte[] textthingEncode(Object textthing, String encoding)
	{
		return textthingEncode(textthing, Charset.forName(encoding));
	}
	
	public static byte[] textthingEncode(Object textthing)
	{
		return textthingEncode(textthing, Charset.defaultCharset());
	}
	
	
	
	
	
	@PossiblySnapshotPossiblyLiveValue  //Future-proofing ^^'
	public static Object universalNewlinesTextthings(Object original)
	{
		return universalNewlinesCharArrayOPC(textthingToCharArray(original, false));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static ByteBuffer encodeTextToByteBuffer(CharSequence text, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		CharsetEncoder encoder = encoding.newEncoder();
		encoder.onUnmappableCharacter(onUnmappableCharacter);
		encoder.onMalformedInput(onMalformedInput);
		return encoder.encode(text instanceof CharBuffer ? (CharBuffer)text : CharBuffer.wrap(text));
	}
	
	public static byte[] encodeTextToByteArray(CharSequence text, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		return NIOBufferUtilities.getArray(encodeTextToByteBuffer(text, encoding, onUnmappableCharacter, onMalformedInput));
	}
	
	public static byte[] encodeTextToByteArrayReporting(CharSequence s, Charset encoding) throws CharacterCodingException
	{
		return encodeTextToByteArray(s, encoding, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
	}
	
	
	
	
	public static CharSequence decodeTextToCharSequence(byte[] bytes, int offset, int length, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		CharsetDecoder decoder = encoding.newDecoder();
		decoder.onUnmappableCharacter(onUnmappableCharacter);
		decoder.onMalformedInput(onMalformedInput);
		return decoder.decode(ByteBuffer.wrap(bytes, offset, length)); //CharBuffers *are* CharSequences! Yay API overlaps! ^^!
	}
	
	public static CharSequence decodeTextToCharSequence(byte[] bytes, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		return decodeTextToCharSequence(bytes, 0, bytes.length, encoding, onUnmappableCharacter, onMalformedInput);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static CharSequence decodeTextToCharSequenceReporting(byte[] bytes, int offset, int length, Charset encoding) throws CharacterCodingException
	{
		return decodeTextToCharSequence(bytes, offset, length, encoding, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
	}
	
	public static CharSequence decodeTextToCharSequenceReporting(byte[] bytes, Charset encoding) throws CharacterCodingException
	{
		return decodeTextToCharSequence(bytes, encoding, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static String decodeTextToString(byte[] bytes, int offset, int length, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		return decodeTextToCharSequence(bytes, offset, length, encoding, onUnmappableCharacter, onMalformedInput).toString();
	}
	
	public static String decodeTextToStringReporting(byte[] bytes, int offset, int length, Charset encoding) throws CharacterCodingException
	{
		return decodeTextToString(bytes, offset, length, encoding, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
	}
	
	
	
	
	
	
	public static String decodeTextToString(byte[] bytes, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		return decodeTextToString(bytes, 0, bytes.length, encoding, onUnmappableCharacter, onMalformedInput);
	}
	
	public static String decodeTextToStringReporting(byte[] bytes, Charset encoding) throws CharacterCodingException
	{
		return decodeTextToString(bytes, encoding, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
	}
	
	
	
	public static String decodeTextToString(Slice<byte[]> bytes, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		return decodeTextToString(bytes.getUnderlying(), bytes.getOffset(), bytes.getLength(), encoding, onUnmappableCharacter, onMalformedInput);
	}
	
	public static String decodeTextToStringReporting(Slice<byte[]> bytes, Charset encoding) throws CharacterCodingException
	{
		return decodeTextToString(bytes, encoding, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
	}
	
	
	
	public static String decodeTextToString(ByteList bytes, Charset encoding, CodingErrorAction onUnmappableCharacter, CodingErrorAction onMalformedInput) throws CharacterCodingException
	{
		return decodeTextToString(bytes.toByteArray(), encoding, onUnmappableCharacter, onMalformedInput);
	}
	
	public static String decodeTextToStringReporting(ByteList bytes, Charset encoding) throws CharacterCodingException
	{
		return decodeTextToString(bytes, encoding, CodingErrorAction.REPORT, CodingErrorAction.REPORT);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////////// PARSING :D!! ////////////
	/**
	 * This is a very elementary number parsing utility, it is designed to be used to make more complicated syntaxes (like in script parsers).<br>
	 * <br>
	 * It reads integer digits in the given radix, moves along the given {@link Cursor}, and stops on the first invalid character or at the EOF (which will set the cursor to <code>length</code>).<br>
	 * The long it reads to is treated as a 64-bit <b>un</b>signed integer, which you can ensure fits in a signed one with {@link Unsigned#safeCastU64toS64(long)} :><br>
	 * <br>
	 * There are no safeties (like <code>data != null</code>), so don't give any invalid parameters.<br>
	 * If, while parsing, the number becomes to great, it will simply stop at 2<sup>64</sup>-1<br>
	 */
	@ActuallyUnsignedValue
	public static long parseBasicNumber(char[] data, int offset, int length, Cursor cursor, int radix)
	{
		long value = 0;
		int digit = 0;
		int curpos = 0;
		
		long max = Unsigned.divideU64(Unsigned.LONG_MAX_VALUE, radix);
		
		while (true)
		{
			curpos = cursor.getCursor();
			
			if (curpos >= length)
				//EOF
				return value;
			
			digit = Character.digit(data[offset+curpos], radix);
			
			if (digit == -1)
			{
				//Invalid digit (like '.')
				return value;
			}
			else
			{
				//Check for overflow
				if (Unsigned.greaterThanU64(value, max))
				{
					value = Unsigned.LONG_MAX_VALUE;
				}
				else
				{
					value *= radix;
					
					//Check for overflow
					if (Unsigned.greaterThanU64(value, Unsigned.LONG_MAX_VALUE - digit))
					{
						value = Unsigned.LONG_MAX_VALUE;
					}
					else
					{
						value += digit;
					}
				}
				
				cursor.setCursor(curpos+1);
			}
		}
	}
	
	
	
	/**
	 * This is a very elementary number parsing utility, it is designed to be used to make more complicated syntaxes (like in script parsers).<br>
	 * <br>
	 * It reads integer digits in the given radix, and stops on the first invalid character or at the EOF.<br>
	 * The long it reads to is treated as a 64-bit <b>un</b>signed integer, which you can ensure fits in a signed one with {@link Unsigned#safeCastU64toS64(long)} :><br>
	 * <br>
	 * There are no safeties (like <code>data != null</code>), so don't give any invalid parameters.<br>
	 * If, while parsing, the number becomes to great, it will simply stop at 2<sup>64</sup>-1<br>
	 */
	@ActuallyUnsignedValue
	public static long parseBasicNumber(char[] data, int offset, int length, int radix)
	{
		long value = 0;
		int digit = 0;
		int curpos = offset;
		
		long max = Unsigned.divideU64(Unsigned.LONG_MAX_VALUE, radix);
		
		while (true)
		{
			if (curpos >= length+offset)
				//EOF
				return value;
			
			digit = Character.digit(data[curpos], radix);
			
			if (digit == -1)
			{
				//Invalid digit (like '.')
				return value;
			}
			else
			{
				//Check for overflow
				if (Unsigned.greaterThanU64(value, max))
				{
					value = Unsigned.LONG_MAX_VALUE;
				}
				else
				{
					value *= radix;
					
					//Check for overflow
					if (Unsigned.greaterThanU64(value, Unsigned.LONG_MAX_VALUE - digit))
					{
						value = Unsigned.LONG_MAX_VALUE;
					}
					else
					{
						value += digit;
					}
				}
				
				curpos++;
			}
		}
	}
	
	
	/**
	 * Parses the fractional part of a number in decimal notation, of a given radix.
	 */
	public static double parseBasicDecimalPart(char[] data, int offset, int length, Cursor cursor, int radix)
	{
		double value = 0;
		int digit = 0;
		int curpos = 0;
		int startPos = cursor.getCursor();
		
		while (true)
		{
			curpos = cursor.getCursor();
			
			if (curpos >= length)
				//EOF
				return value;
			
			digit = Character.digit(data[offset+curpos], radix);
			
			if (digit == -1)
			{
				//Invalid digit (like '.')
				return value;
			}
			else
			{
				value += digit / (Math.pow(radix, (curpos - startPos + 1)));
				
				cursor.setCursor(curpos+1);
			}
		}
	}
	
	
	public static Integer parseIntegerLeniently(String data, int radix, Integer defaultValue)
	{
		if (data == null || data.length() == 0)
			return defaultValue;
		
		if (radix < Character.MIN_RADIX)
			radix = Character.MIN_RADIX;
		else if (radix > Character.MAX_RADIX)
			radix = Character.MAX_RADIX;
		
		
		boolean negative = data.charAt(0) == '-';
		
		
		if (negative && data.length() == 1)
			return defaultValue;
		
		int value = 0;
		int digit = 0;
		
		for (int i = (negative ? 1 : 0); i < data.length(); i++)
		{
			digit = Character.digit(data.charAt(i), radix);
			
			if (digit == -1)
			{
				if (i == (negative ? 1 : 0))
					return defaultValue;
				else
					break;
			}
			else
			{
				/*
				 * (value > max / radix)  implies  (value * radix > max)
				 * 
				 * Proof:
				 * 	Unavailable Integer property:
				 * 		Let i and a be integers
				 * 		i < a < i + 1 := False
				 * 			Because there is no integer between two consecutive integers
				 * 
				 * 	Integer division property:
				 * 		Let a and b be integers and a > b
				 * 		Let q = a / b
				 * 		a % b = 0 := q * b = a					A number divisible by another number loses nothing during integer division
				 * 		a % b ≠ 0 := q * b < a < (q+1) * b		A number not divisible by another number loses the remainder during integer division, and so when multiplied back, is smaller than it was originally. But the next integer after the quotient when multiplied, extends beyond the original number
				 * 
				 * 	Indivisibility:
				 * 		Let a, b, and c be integers
				 * 		If a % b ≠ 0
				 * 		Then c * b = a := False
				 * 			Because c does not exist.
				 * 			a % b ≠ 0 means a is not divisible by b.
				 * 			So there is no integer, when multiplied by b makes a.
				 * 
				 * 
				 * 
				 * let a = v*r
				 * let b = m/r*r
				 * 
				 * v > m/r			Given
				 * v*r > m/r*r		Integer multiplication
				 * a > b			Substitution
				 * 
				 * If m % r = 0		Meaning m is divisible by r
				 * 	b = m			If a number A is divisible by another number B,
				 * 	a > b
				 * 	a > m			Done.
				 * 
				 * If m % r > 0
				 *  b < m			Integer division property
				 *  Assume a = m
				 *   a = v * r
				 *   v * r = m
				 *   False			Indivisibility (v does not exist)
				 * 
				 *  Assume a < m
				 * 	 let q = m / r
				 *   b = q * r		Substitution
				 * 
				 *   m < (q+1)*r	Integer division property
				 * 
				 *   b < a < (q+1) * r
				 *   q * r < v * r < (q+1) * r
				 *   q < v < q+1
				 *   False			Unavailable integer
				 */
				
				if (!negative)
				{
					//Check for overflow
					if (value > Integer.MAX_VALUE / radix)
						return Integer.MAX_VALUE;
					
					value *= radix;
					
					//Check for overflow
					if (value > Integer.MAX_VALUE - digit)
						return Integer.MAX_VALUE;
					
					value += digit;
				}
				else
				{
					//Check for overflow
					if (value < Integer.MIN_VALUE / radix)
						return Integer.MIN_VALUE;
					
					value *= radix;
					
					//Check for overflow
					if (value < Integer.MIN_VALUE + digit)
						return Integer.MIN_VALUE;
					
					value -= digit;
				}
			}
		}
		
		return value;
	}
	
	
	
	public static Long parseLongLeniently(String data, int radix, Long defaultValue)
	{
		if (data == null || data.length() == 0)
			return defaultValue;
		
		if (radix < Character.MIN_RADIX)
			radix = Character.MIN_RADIX;
		else if (radix > Character.MAX_RADIX)
			radix = Character.MAX_RADIX;
		
		
		boolean negative = data.charAt(0) == '-';
		
		if (negative && data.length() == 1)
			return defaultValue;
		
		long value = 0;
		int digit = 0;
		
		for (int i = (negative ? 1 : 0); i < data.length(); i++)
		{
			digit = Character.digit(data.charAt(i), radix);
			
			if (digit == -1)
			{
				if (i == (negative ? 1 : 0))
					return defaultValue;
				else
					break;
			}
			else
			{
				if (!negative)
				{
					//Check for overflow
					if (value > Long.MAX_VALUE / radix)
						return Long.MAX_VALUE;
					
					value *= radix;
					
					//Check for overflow
					if (value > Long.MAX_VALUE - digit)
						return Long.MAX_VALUE;
					
					value += digit;
				}
				else
				{
					//Check for overflow
					if (value < Long.MIN_VALUE / radix)
						return Long.MIN_VALUE;
					
					value *= radix;
					
					//Check for overflow
					if (value < Long.MIN_VALUE + digit)
						return Long.MIN_VALUE;
					
					value -= digit;
				}
			}
		}
		
		return value;
	}
	
	
	
	
	
	
	
	public static int parseIntegerLeniently(String data, int radix)
	{
		return parseIntegerLeniently(data, radix, 0);
	}
	
	public static int parseIntegerLeniently(String data)
	{
		return parseIntegerLeniently(data, 10, 0);
	}
	
	
	public static long parseLongLeniently(String data, int radix)
	{
		return parseLongLeniently(data, radix, 0L);
	}
	
	public static long parseLongLeniently(String data)
	{
		return parseLongLeniently(data, 10, 0L);
	}
	
	
	
	public static Boolean parseBooleanLeniently(String data, Boolean def)
	{
		if (data == null || data.length() == 0)
			return def;
		
		if (
		data.equalsIgnoreCase("true") ||
		data.equalsIgnoreCase("t") ||
		data.equalsIgnoreCase("yes") ||
		data.equalsIgnoreCase("y") ||
		data.equalsIgnoreCase("on") ||
		data.equalsIgnoreCase("1")
		)
			return true;
		
		else if (
		data.equalsIgnoreCase("false") ||
		data.equalsIgnoreCase("f") ||
		data.equalsIgnoreCase("no") ||
		data.equalsIgnoreCase("n") ||
		data.equalsIgnoreCase("off") ||
		data.equalsIgnoreCase("0")
		)
			return false;
		
		else
			return def;
	}
	
	public static Boolean parseBooleanLeniently(String data)
	{
		return parseBooleanLeniently(data, null);
	}
	
	
	
	public static boolean parseBooleanStrictly(String data) throws TextSyntaxException
	{
		if (data.equals("false"))
			return true;
		else if (data.equals("true"))
			return true;
		else
			throw TextSyntaxException.inst("Not a 'true' or 'false'!: "+data);
	}
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * De-escapes occurrences of certain characters inside a main string and returns the results.  ^_^
	 * 
	 * The escape code dictionary is a dictionary of single characters to one or multi-length strings :>
	 * It's given in dictionary-interleaved format with escape codes first (eg, key0, value0, key1, value1, ...),
	 * and the keys and values can both be {@link Character}s, char[]s, or CharSequences,
	 * but the keys must represent, if not a {@link Character}, then a string of length 1 :>
	 * 
	 * @param unicodeEscapeCode if not null, then this is the escape code which corresponds to four-hex-char unicode escapes (eg, 'u' ^^ )
	 * @param unmatchedEscapedValidlyMatchEscapeCode if <code>true</code>, then it is not an error for an escape to be unmatched; it simply translates directly to the code (a la bash :> ); eg, so you don't have to include things like \" -> ", \\ -> \  ^_^
	 * @param source The original string containing things which are escaped
	 */
	public static String descape(Object source, char escapeChar, Character unicodeEscapeCode, boolean unmatchedEscapedValidlyMatchEscapeCode, boolean ignoreErrors, Object[] escapeDictionary) throws TextSyntaxException
	{
		int srclen = getLength(source);
		
		//Shortcuts :>
		if (source == null)
			return null;
		if (srclen == 0)
			return "";
		
		char[] src = textthingToPossiblyUnclonedCharArray(source);
		assert src.length == srclen;
		
		StringBuilder output = new StringBuilder(srclen);
		
		
		//Interpret dictionarythings :>
		boolean usingUnicodeEscapes = false;
		char unicodeEscapeCodeChar = 0;
		char[] escapeCodes = null;
		char[] escapeReplacementChars = null;
		char[][] escapeReplacementStrings = null;
		{
			usingUnicodeEscapes = unicodeEscapeCode != null;
			if (usingUnicodeEscapes)
				unicodeEscapeCodeChar = unicodeEscapeCode;
			
			if ((escapeDictionary.length % 2) != 0)
				throw new IllegalArgumentException("dictionary not in pairs! ;_;");
			
			escapeCodes = new char[escapeDictionary.length / 2];
			escapeReplacementChars = new char[escapeCodes.length];
			escapeReplacementStrings = new char[escapeCodes.length][];
			
			for (int i = 0; i < escapeCodes.length; i++)
			{
				escapeCodes[i] = getSingleChar(escapeDictionary[i*2+0]);
				
				Object v = escapeDictionary[i*2+1];
				
				int l = getLength(v);
				if (l == 0)
				{
					throw new IllegalArgumentException("empty escape replacement for code "+repr(escapeCodes[i])+" o_O");
				}
				else if (l == 1)
				{
					escapeReplacementStrings[i] = null;
					escapeReplacementChars[i] = getSingleChar(v);
				}
				else
				{
					escapeReplacementStrings[i] = textthingToPossiblyUnclonedCharArray(v);
				}
			}
		}
		
		
		char escapeCode = 0;
		int escapeDictionaryIndex = 0;
		boolean matchedEscapeCode = false;
		
		int safeSubstringStart = 0;
		
		for (int i = 0; i < srclen;)
		{
			if (src[i] == escapeChar)
			{
				//No escape code!; escape char at eof!
				if (i+1 >= srclen)
				{
					if (ignoreErrors)
					{
						//subsume it all as stuff to output unchanged by not touching safeSubstringStart ^_^
						i++;
					}
					else
					{
						throw TextSyntaxException.inst("dangling escape char at EOF ><");
					}
				}
				
				//There is an escape code! :D
				else
				{
					escapeCode = src[i+1];
					
					//Hex code escape sequence :>
					if (usingUnicodeEscapes && escapeCode == unicodeEscapeCodeChar)
					{
						if (i+2+4 > srclen)
						{
							if (ignoreErrors)
							{
								//subsume it all as stuff to output unchanged by not touching safeSubstringStart ^_^
								i = Math.min(i+2+4, srclen);
							}
							else
							{
								throw TextSyntaxException.inst("dangling unicode escape at EOF ><");
							}
						}
						
						else
						{
							int v0 = Character.digit(src[i+2+0], 16);
							int v1 = Character.digit(src[i+2+1], 16);
							int v2 = Character.digit(src[i+2+2], 16);
							int v3 = Character.digit(src[i+2+3], 16);
							
							if (v0 == -1 || v1 == -1 || v2 == -1 || v3 == -1)
							{
								if (ignoreErrors)
								{
									//subsume it all as stuff to output unchanged by not touching safeSubstringStart ^_^
									i = Math.min(i+2+4, srclen);
								}
								else
								{
									throw TextSyntaxException.inst("invalid hexadecimal unicode UTF-16BE (four hex char) escape! @ "+i+": "+new String(src, i, i+2+4));
								}
							}
							
							//It's good! :D
							else
							{
								char c = (char)((v0 << 12) | (v1 << 8) | (v2 << 4) | (v3 << 0));
								
								
								
								//Here's where you finally output safe string ^_^
								if (safeSubstringStart != i)
								{
									output.append(src, safeSubstringStart, i - safeSubstringStart);
								}
								
								
								
								output.append(c);
								
								
								
								i += 2 + 4;
								safeSubstringStart = i; //don't include the escape sequence in the safe output ^_^
							}
						}
					}
					
					//Normal escape sequence :>
					else
					{
						matchedEscapeCode = false;
						
						for (escapeDictionaryIndex = 0; escapeDictionaryIndex < escapeCodes.length; escapeDictionaryIndex++)
						{
							if (escapeCodes[escapeDictionaryIndex] == escapeCode)
							{
								matchedEscapeCode = true;
								
								
								
								//Here's where you finally output safe string ^_^
								if (safeSubstringStart != i)
								{
									output.append(src, safeSubstringStart, i - safeSubstringStart);
								}
								
								i += 2;
								safeSubstringStart = i; //don't include the escape sequence in the safe output ^_^
								
								
								
								if (escapeReplacementStrings[escapeDictionaryIndex] == null)
								{
									output.append(escapeReplacementChars[escapeDictionaryIndex]);
								}
								else
								{
									output.append(escapeReplacementStrings[escapeDictionaryIndex]);
								}
								
								break;
							}
						}
						
						
						if (!matchedEscapeCode)
						{
							if (unmatchedEscapedValidlyMatchEscapeCode)
							{
								//Here's where you finally output safestring ^_^
								if (safeSubstringStart != i)
								{
									output.append(src, safeSubstringStart, i - safeSubstringStart);
								}
								
								
								output.append(escapeCode); // ^_^
								
								
								i += 2;
								safeSubstringStart = i; //don't include the escape sequence in the safe output ^_^
							}
							else
							{
								if (ignoreErrors)
								{
									i += 2;
									//subsume it all as stuff to output unchanged by not touching safeSubstringStart ^_^
								}
								else
								{
									throw TextSyntaxException.inst("Unmatched escape code at "+(i+1)+": "+repr(escapeCode));
								}
							}
						}
					}
				}
			}
			
			
			else //if (c != escapeChar)
			{
				//handled via safeSubstringStart ^^
				i++;
			}
		}
		
		
		
		
		if (safeSubstringStart < srclen)
		{
			output.append(src, safeSubstringStart, srclen - safeSubstringStart);
			//and safeSubstringStart doesn't need to be reset; it's the end! :D
		}
		else if (safeSubstringStart == srclen)
		{
			//Then nothing to do; empty string! ^_^
		}
		else if (safeSubstringStart > srclen)
		{
			throw new ImpossibleException("src.length: "+src.length+", srclen: "+srclen+", safeSubstringStart: "+safeSubstringStart+", escapeDictionary="+repr(escapeDictionary)+", escapeChar="+repr(escapeChar)+", unicodeEscapeCode="+repr(unicodeEscapeCode)+", ignoreErrors="+ignoreErrors+", src="+repr(new String(src)));
		}
		else
			throw new UnreachableCodeException();
		
		
		
		return output.toString();
	}
	
	public static String descapev(Object source, char escapeChar, Character unicodeEscapeCode, boolean unmatchedEscapedValidlyMatchEscapeCode, boolean ignoreErrors, Object... escapeDictionary) throws TextSyntaxException
	{
		return descape(source, escapeChar, unicodeEscapeCode, unmatchedEscapedValidlyMatchEscapeCode, ignoreErrors, escapeDictionary);
	}
	
	
	public static String descape(Object source, char escapeChar, Character unicodeEscapeCode, boolean unmatchedEscapedValidlyMatchEscapeCode, boolean ignoreErrors, Map escapeDictionary)
	{
		Object[] asarray = escapeDictionary == null ? new Object[0] : new Object[escapeDictionary.size()*2];
		
		int i = 0;
		for (Object code : escapeDictionary.keySet())
		{
			if (code == null) continue;
			
			Object replacementB = escapeDictionary.get(code);
			
			if (replacementB == null) continue;
			
			asarray[i*2+0] = code;
			asarray[i*2+1] = replacementB;
		}
		
		return descape(source, escapeChar, unicodeEscapeCode, unmatchedEscapedValidlyMatchEscapeCode, ignoreErrors, asarray);
	}
	
	
	
	public static String descapeRPStandard(Object escaped, char escapeChar, char whichSurroundingQuotelikeCharIsUsed)
	{
		return descapev(escaped, escapeChar, 'u', true, true,
		//Variable escapes :>
		'e', escapeChar,
		'z', whichSurroundingQuotelikeCharIsUsed,
		
		//Things necessary for quotation delimiters :>
		'q', '\'', //Quote
		'd', '"',  //Double-quote
		'b', '`',  //Backtick :>
		'g', '«',  //opening Guillemet :>
		'G', '»',  //closing Guillemet :>
		'a', '<',  //opening Angle bracket :>
		'A', '>',  //closing Angle bracket :>
		
		//Things for some [programming language] comment syntaxes ;>
		'S', '*',
		'H', '#',
		
		//Things sometimes necessary for lexical restrictions on quoted strings :>
		'n', '\n', //Newline aka line feed
		'r', '\r', //carriage Return
		't', '\t', //Tab
		's', ' '   //Space
		);
	}
	
	/**
	 * Standardstandard:
	 * escapeChar = '\\'
	 * whichSurroundingQuotelikeCharIsUsed = '"'
	 * ^_^
	 * XD
	 */
	public static String descapeRPStandard(Object escaped)
	{
		return descapeRPStandard(escaped, '\\', '"');
	}
	
	
	
	public static String descapeJavaStandard(Object escaped)
	{
		return descapev(escaped, '\\', 'u', true, true,
		'n', '\n',
		'r', '\r',
		't', '\t'
		);
	}
	
	
	
	
	//Old things :>
	public static String deEscape(Object escaped, char escapeChar, char... escapes)
	{
		Object[] escapeDict = Primitives.box(escapes);
		
		//Swap escape codes and things, because it's backwards now (as of the revamp in 2013-10/11) XD
		ArrayUtilities.swapPairs(escapeDict);
		
		return descape(escaped, escapeChar, null, false, true, escapeDict);
	}
	
	//Old things :>
	public static String unicodeDescape(Object escaped)
	{
		return descapev(escaped, '\\', 'u', false, true);
	}
	
	
	
	
	public static int indexOfSkippingSimpleEscapes(Object textthing, UnaryFunctionCharToBoolean targetChar, UnaryFunctionCharToBoolean escapeChar)
	{
		int p = -1;
		
		int len = getLength(textthing);
		
		if (!(textthing instanceof char[]) && len < 24) //super properly determined performance optimization threshold; mhm. XD
		{
			boolean inEscape = false;
			char c = 0;
			for (int i = 0; i < len; i++)
			{
				c = charAt(textthing, i);
				
				if (!inEscape)
				{
					if (escapeChar.f(c))
					{
						inEscape = true;
					}
					else if (targetChar.f(c))
					{
						p = i;
						break;
					}
				}
				else
				{
					inEscape = false;
				}
			}
		}
		else
		{
			char[] s = textthingToPossiblyUnclonedCharArray(textthing);
			
			boolean inEscape = false;
			char c = 0;
			for (int i = 0; i < len; i++)
			{
				c = s[i];
				
				if (!inEscape)
				{
					if (escapeChar.f(c))
					{
						inEscape = true;
					}
					else if (targetChar.f(c))
					{
						p = i;
						break;
					}
				}
				else
				{
					inEscape = false;
				}
			}
		}
		
		return p;
	}
	
	
	
	
	
	public static int indexOfSkippingSimpleEscapes(Object textthing, char targetChar, char escapeChar)
	{
		int p = -1;
		
		int len = getLength(textthing);
		
		if (!(textthing instanceof char[]) && len < 24) //super properly determined performance optimization threshold; mhm. XD
		{
			boolean inEscape = false;
			char c = 0;
			for (int i = 0; i < len; i++)
			{
				c = charAt(textthing, i);
				
				if (!inEscape)
				{
					if (escapeChar == c)
					{
						inEscape = true;
					}
					else if (targetChar == c)
					{
						p = i;
						break;
					}
				}
				else
				{
					inEscape = false;
				}
			}
		}
		else
		{
			char[] s = textthingToPossiblyUnclonedCharArray(textthing);
			
			boolean inEscape = false;
			char c = 0;
			for (int i = 0; i < len; i++)
			{
				c = s[i];
				
				if (!inEscape)
				{
					if (escapeChar == c)
					{
						inEscape = true;
					}
					else if (targetChar == c)
					{
						p = i;
						break;
					}
				}
				else
				{
					inEscape = false;
				}
			}
		}
		
		return p;
	}
	
	
	/**
	 * Note: does NOT de-escape the tokens!!
	 * merely removes the quotes, and discards the whitespace!
	 * :)
	 * 
	 * + Also note: quotes can be started by anything the pattern returns true for, but must be closed/matched by the same character that started them, whatever that is
	 * 		:>
	 */
	public static Object[] parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes(Object source, UnaryFunctionCharToBoolean quotesPattern, UnaryFunctionCharToBoolean breakingSpacePattern, UnaryFunctionCharToBoolean escapeCharacterPattern, boolean skipEscapesInsideUnquoteds, boolean throwSyntaxExceptionOnEoffedQuotedTokens)
	{
		List<char[]> tokens = new ArrayList<char[]>();
		
		
		char[] sourceCA = textthingToPossiblyUnclonedCharArray(source);
		
		int currentTokenStart = 0;
		boolean inQuotes = false;
		char quoteChar = 0;
		
		char c = 0;
		for (int i = 0; i < sourceCA.length; i++)
		{
			c = sourceCA[i];
			
			if (inQuotes)
			{
				if (c == quoteChar)
				{
					inQuotes = false;
					tokens.add(ArrayUtilities.slice(sourceCA, currentTokenStart, i)); //add even if zero-length; one of the two or three usages/benefits of quoting! ^_^     (others are including breaking/white spaces, and possibly being able to use escapes (if not allowed outside quoted tokens!) )
					currentTokenStart = i+1;
				}
				else if (escapeCharacterPattern.f(c))
				{
					//skip over escape code; since delimiter-containing escape codes are (assumed here to be) always one-character long :)
					i++; //moving too far (beyond eof! :o ) is still caught by the loop condition ^_^
				}
				//else don't do anything; leave the character included in the current token by not changing currentTokenStart ^_^
			}
			else
			{
				if (breakingSpacePattern.f(c))
				{
					if (i - currentTokenStart > 0) //*don't* include zero-length tokens, because otherwise every spot between more than one consecutive breaking/white space would be an empty token! 0,0
					{
						tokens.add(ArrayUtilities.slice(sourceCA, currentTokenStart, i));
					}
					
					currentTokenStart = i+1;
				}
				else if (escapeCharacterPattern.f(c) && skipEscapesInsideUnquoteds)
				{
					//skip over escape code; since delimiter-containing escape codes are (assumed here to be) always one-character long :)
					i++; //moving too far (beyond eof! :o ) is still caught by the loop condition ^_^
				}
				else if (quotesPattern.f(c))
				{
					if (i - currentTokenStart > 0) //*don't* include zero-length tokens, because otherwise every spot between more than one consecutive breaking/white space would be an empty token! 0,0
					{
						tokens.add(ArrayUtilities.slice(sourceCA, currentTokenStart, i));
					}
					
					currentTokenStart = i+1;
					
					
					inQuotes = true;
					quoteChar = c;
				}
				//else don't do anything; leave the character included in the current token by not changing currentTokenStart ^_^
			}
		}
		
		
		
		
		if (inQuotes)
		{
			if (throwSyntaxExceptionOnEoffedQuotedTokens)
				throw TextSyntaxException.inst("(local) EOF inside quoted token! :[");
			else
				tokens.add(ArrayUtilities.slice(sourceCA, currentTokenStart, sourceCA.length)); //(still add even if zero-length X3)
		}
		else
		{
			if (sourceCA.length - currentTokenStart > 0) //*don't* include zero-length tokens, because otherwise every spot between more than one consecutive breaking/white space would be an empty token! 0,0
			{
				tokens.add(ArrayUtilities.slice(sourceCA, currentTokenStart, sourceCA.length));
			}
		}
		
		
		
		
		return tokens.toArray();
	}
	
	
	protected static final UnaryFunctionCharToBoolean ThreeQuotesPattern = new UnaryFunctionCharToBoolean()
	{
		@Override
		public boolean f(char input)
		{
			return input == '"' || input == '\'' || input == '`';
		}
	};
	
	protected static final UnaryFunctionCharToBoolean TwoQuotesPattern = new UnaryFunctionCharToBoolean()
	{
		@Override
		public boolean f(char input)
		{
			return input == '"' || input == '\'';
		}
	};
	
	
	protected static final UnaryFunctionCharToBoolean JustDoubleQuotesPattern = new SingletonCharEqualityPredicate('"');
	
	protected static final UnaryFunctionCharToBoolean BackslashPattern = new SingletonCharEqualityPredicate('\\');
	
	
	public static Object[] parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleSingleAndBacktickQuotes_WhitespaceBreakingSpace_BackslashEscapes(Object source, boolean skipEscapesInsideUnquoteds, boolean throwSyntaxExceptionOnEoffedQuotedTokens)
	{
		return parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes(source, ThreeQuotesPattern, WHITESPACE_PATTERN, BackslashPattern, skipEscapesInsideUnquoteds, throwSyntaxExceptionOnEoffedQuotedTokens);
	}
	public static Object[] parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleAndSingleQuotes_WhitespaceBreakingSpace_BackslashEscapes(Object source, boolean skipEscapesInsideUnquoteds, boolean throwSyntaxExceptionOnEoffedQuotedTokens)
	{
		return parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes(source, TwoQuotesPattern, WHITESPACE_PATTERN, BackslashPattern, skipEscapesInsideUnquoteds, throwSyntaxExceptionOnEoffedQuotedTokens);
	}
	public static Object[] parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleQuotes_WhitespaceBreakingSpace_BackslashEscapes(Object source, boolean skipEscapesInsideUnquoteds, boolean throwSyntaxExceptionOnEoffedQuotedTokens)
	{
		return parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes(source, JustDoubleQuotesPattern, WHITESPACE_PATTERN, BackslashPattern, skipEscapesInsideUnquoteds, throwSyntaxExceptionOnEoffedQuotedTokens);
	}
	
	
	public static Object[] parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleSingleAndBacktickQuotes_WhitespaceBreakingSpace_BackslashEscapes(Object source)
	{
		return parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleSingleAndBacktickQuotes_WhitespaceBreakingSpace_BackslashEscapes(source, true, false);
	}
	public static Object[] parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleAndSingleQuotes_WhitespaceBreakingSpace_BackslashEscapes(Object source)
	{
		return parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleAndSingleQuotes_WhitespaceBreakingSpace_BackslashEscapes(source, true, false);
	}
	public static Object[] parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleQuotes_WhitespaceBreakingSpace_BackslashEscapes(Object source)
	{
		return parsePossiblyFlatlyQuotedWhitespaceSepararedTokensSkippingSimpleEscapes_DoubleQuotes_WhitespaceBreakingSpace_BackslashEscapes(source, true, false);
	}
	
	
	
	
	
	/*
	 * Unicode escape format is the backslash u #### (where # are hexadecimal digits) subset of the Java escape syntax.
	 * /
	public static String unicodeDescape(String escaped)
	{
		StringBuilder buffer = new StringBuilder();
		char c = 0;
		MainLoop: for (int i = 0; i < escaped.length();)
		{
			c = escaped.charAt(i);
			i++;
			
			if (c == '\\') //first escape code char
			{
				if (i >= escaped.length()) //for dangling escape chars (eg, "abcabc"+"\" EOF)
				{
					//Pass through the invalid partial escape
					buffer.append('\\');
					break MainLoop;
				}
				else
				{
					c = escaped.charAt(i);
					i++;
					
					if (c == 'u') //second escape code char
					{
						if (i+4 > escaped.length()) //for dangling escape chars (eg, "abcabc"+"\"+"u" or "abcabc"+"\"+"uABC")
						{
							//Pass through the invalid partial escape
							buffer.append("\\u");
							break MainLoop;
						}
						else
						{
							String scode = escaped.substring(i, i+4);
							i += 4;
							
							try
							{
								int code = Integer.parseInt(scode, 16);
								//success:
								char escapedC = (char)code; //unicode translation
								
								buffer.append(escapedC);
							}
							catch (NumberFormatException exc)
							{
								//failure:
								//Pass through the invalid escape
								buffer.append("\\u");
							}
						}
					}
					else
					{
						//eg, "abcabc"+"\"+"n"
						
						//Pass through the not-real escape
						buffer.append('\\');
						buffer.append(c);
					}
				}
			}
			else
			{
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
	 */
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * ignore mode is swallow (since there's no way to leave it in) :>
	 * + case insensitivity could be done with a case-insensitivizing {@link Map} ^_^
	 */
	public static void parseBasicInfoDataLine(String sourceLine, Map<String, List<String>> results, boolean ignoreErroneousLines) throws TextSyntaxException
	{
		sourceLine = sourceLine.trim();
		
		if (sourceLine.isEmpty())
			return;
		
		
		String key = null;
		String value = null;
		
		if (sourceLine.charAt(0) == '\"' || sourceLine.charAt(0) == '\'')
		{
			char q = sourceLine.charAt(0);
			sourceLine = sourceLine.substring(1); //skip starting quote
			
			//int n = sourceLine.indexOf(q);  this only allowes alphacode escapes (but not \" or \' ._. )
			int n = indexOfSkippingSimpleEscapes(sourceLine, q, '\\');
			
			if (n == -1)
			{
				if (ignoreErroneousLines)
					return;
				else
					throw TextSyntaxException.inst("Opening key quote ("+q+") but no closing one on same line! ;_;");
			}
			
			key = sourceLine.substring(0, n);
			key = descapeRPStandard(key, '\\', q);
			
			sourceLine = sourceLine.substring(n+1).trim(); //skip ending quote
			
			if (sourceLine.charAt(0) != ':' && sourceLine.charAt(0) != '=')
			{
				if (ignoreErroneousLines)
					return;
				else
					throw TextSyntaxException.inst("Neither a ':' nor a '='! ;_;");
			}
			sourceLine = sourceLine.substring(1).trim(); //skip delimiter :>
		}
		else
		{
			int n0 = sourceLine.indexOf(':');
			int n1 = sourceLine.indexOf('=');
			
			int n = 0;
			{
				if (n0 == -1 && n1 == -1)
				{
					if (ignoreErroneousLines)
						return;
					else
						throw TextSyntaxException.inst("neither a ':' nor a '='! ;_;");
				}
				else if (n0 == -1) //&& n1 != -1
					n = n1;
				else if (n1 == -1) //&& n0 != -1
					n = n0;
				else //if (n0 != -1 && n1 != -1)
					n = Math.min(n0, n1);
			}
			
			key = sourceLine.substring(0, n).trim();
			
			sourceLine = sourceLine.substring(1).trim(); //skip delimiter :>
		}
		
		
		value = sourceLine;
		
		
		//Include in results ^_^
		{
			List<String> values = results.get(key);
			
			if (values == null)
			{
				values = new ArrayList<String>(1);
				results.put(key, values);
			}
			
			values.add(value);
		}
	}
	
	public static void parseBasicInfoData(String[] sourceLines, Map<String, List<String>> results, boolean ignoreErroneousLines) throws TextSyntaxException
	{
		for (String sourceLine : sourceLines)
			parseBasicInfoDataLine(sourceLine, results, ignoreErroneousLines);
	}
	
	public static void parseBasicInfoData(String source, Map<String, List<String>> results, boolean ignoreErroneousLines) throws TextSyntaxException
	{
		parseBasicInfoData(split(source, '\n'), results, ignoreErroneousLines);
	}
	
	
	public static Map<String, List<String>> parseBasicInfoData(String[] sourceLines, boolean ignoreErroneousLines) throws TextSyntaxException
	{
		Map<String, List<String>> results = new HashMap<String, List<String>>();
		parseBasicInfoData(sourceLines, results, ignoreErroneousLines);
		return results;
	}
	
	public static Map<String, List<String>> parseBasicInfoData(String source, boolean ignoreErroneousLines) throws TextSyntaxException
	{
		Map<String, List<String>> results = new HashMap<String, List<String>>();
		parseBasicInfoData(source, results, ignoreErroneousLines);
		return results;
	}
	
	
	
	
	
	/**
	 * @param ignoreSyntaxErroneousLines only applies to syntax errors; not non-singleton values on keys, {@link NonSingletonException} is always thrown for those ^_^
	 */
	public static Map<String, String> parseBasicSingletonValuedInfoData(String source, boolean ignoreSyntaxErroneousLines) throws TextSyntaxException, NonSingletonException
	{
		Map results = parseBasicInfoData(source, ignoreSyntaxErroneousLines);
		List<String> badKeys = null;
		
		for (String key : (Set<String>)results.keySet())
		{
			List<String> values = (List<String>)results.get(key);
			if (values.size() == 0)
				throw new ImpossibleException();
			else if (values.size() == 1)
				results.put(key, values.get(0));
			else
			{
				if (badKeys == null)
					badKeys = new ArrayList<String>();
				badKeys.add(key);
			}
		}
		
		
		if (badKeys == null || badKeys.isEmpty())
			return results;
		else
		{
			throw new NonSingletonException("Non singleton keys!: "+joinStrings(mapToNewList(e -> repr(e), badKeys), ", "));
		}
	}
	
	
	
	
	/**
	 * + empty args are passed (eg, "f(a,,c)"), except if there is only one (eg, "f()"), in which case it's removed and zero args are sent :>
	 * @return {functionthing name, first arg, second arg, third arg, ...}, or <code>null</code> if there is no "(", and so it's apparently not a function invocation/definition thing..
	 */
	public static String[] parseSimpleNonnestedFunctionInvocationExpression(String src)
	{
		src = src.trim();
		
		
		int p = src.indexOf('(');
		
		if (p == -1)
			return null;
		
		
		String functionName = src.substring(0, p).trim();
		src = src.substring(p+1);
		
		if (src.endsWith(")"))
			src = src.substring(0, src.length()-1);
		
		String[] args = split(src, ',');
		
		for (int i = 0; i < args.length; i++)
			args[i] = args[i].trim();
		
		if (args.length == 1 && args[0].isEmpty())
			args = new String[0];
		
		return ArrayUtilities.concat1WithArray(functionName, args);
	}
	
	
	
	
	public static List<Object> parseStandardPrefixnotationNestedFunctionInvocationExpression(String src) throws TextSyntaxCheckedException
	{
		try
		{
			return parseStandardPrefixnotationNestedFunctionInvocationExpression(new StringReader(src));
		}
		catch (IOException exc)
		{
			throw new ImpossibleException(exc);
		}
	}
	
	public static List<Object> parseStandardPrefixnotationNestedFunctionInvocationExpression(Reader r) throws IOException, TextSyntaxCheckedException
	{
		return parsePrefixnotationNestedFunctionInvocationExpression(r, '(', ')', ',', WHITESPACE_PATTERN);
	}
	
	
	public static List<Object> parsePrefixnotationNestedFunctionInvocationExpression(Reader r, char opener, char closer, char delimiter, UnaryFunctionCharToBoolean ignorableCharPredicate) throws IOException, TextSyntaxCheckedException
	{
		List<List<Object>> stack = new ArrayList<>();
		stack.add(new ArrayList<>());
		
		StringBuilder buff = new StringBuilder();
		
		
		final int State_FunctionName = 0;
		final int State_Arguments = 1;
		final int State_ArgumentsAfterNestedFunction = 2;
		
		int state = State_FunctionName;
		
		
		
		while (true)
		{
			int e = r.read();
			
			if (e == -1)
			{
				//EOF!
				
				if (state == State_FunctionName)
					throw TextSyntaxCheckedException.inst("Premature EOF!  Expected an opening '"+opener+"'");
				else
					throw TextSyntaxCheckedException.inst("Premature EOF!  Expected a closing '"+closer+"'");
			}
			else
			{
				char c = (char) e;
				
				if (state == State_FunctionName)
				{
					if (c == opener)
					{
						String functionName = trim(buff.toString(), ignorableCharPredicate);
						buff.setLength(0);
						
						List<Object> currentDepthList = stack.get(stack.size() - 1);
						currentDepthList.add(functionName);
						
						state = State_Arguments;
					}
					else if (c == closer)
					{
						throw TextSyntaxCheckedException.inst("Got a closing '"+closer+"' in the function name part where we expected an opening '"+opener+"'");
					}
					else if (c == delimiter)
					{
						throw TextSyntaxCheckedException.inst("Got a '"+delimiter+"' in the function name part where we expected an opening '"+opener+"'");
					}
					else
					{
						buff.append(c);
					}
				}
				
				
				else if (state == State_Arguments)
				{
					if (c == opener)
					{
						String functionName = trim(buff.toString(), ignorableCharPredicate);
						buff.setLength(0);
						
						List<Object> currentDepthList = stack.get(stack.size() - 1);
						
						List<Object> nextDepthList = new ArrayList<>();
						nextDepthList.add(functionName);
						
						currentDepthList.add(nextDepthList);
						stack.add(nextDepthList);
					}
					else if (c == closer || c == delimiter)
					{
						String leafArgument = trim(buff.toString(), ignorableCharPredicate);
						buff.setLength(0);
						
						List<Object> currentDepthList = stack.get(stack.size() - 1);
						
						boolean isEmptyArgumentList = c == closer && currentDepthList.size() == 1 && leafArgument.isEmpty();
						
						if (!isEmptyArgumentList)
							currentDepthList.add(leafArgument);
						
						
						
						
						if (c == closer)
						{
							int n = stack.size();
							
							if (n <= 0)
								throw new AssertionError();
							else if (n == 1)
								return stack.get(0);  //Don't continue parsing after the end!  This makes it amenable to nesting within other syntaxes!  8D
							else
							{
								stack.remove(n-1);
								state = State_ArgumentsAfterNestedFunction;
							}
						}
					}
					else
					{
						buff.append(c);
					}
				}
				
				
				else
				{
					assert state == State_ArgumentsAfterNestedFunction;
					
					if (c == opener)
					{
						throw TextSyntaxCheckedException.inst("Got a opening '"+opener+"' in an argument after a function expression where we expected an closing '"+closer+"', a delimiting '"+delimiter+"', or ignorable chars (eg, whitespace) and then one of those two.");
					}
					else if (c == closer)
					{
						int n = stack.size();
						
						if (n <= 0)
							throw new AssertionError();
						else if (n == 1)
							return stack.get(0);  //Don't continue parsing after the end!  This makes it amenable to nesting within other syntaxes!  8D
						else
						{
							stack.remove(n-1);
							state = State_ArgumentsAfterNestedFunction;
						}
					}
					else if (c == delimiter)
					{
						state = State_Arguments;
					}
					else
					{
						if (!ignorableCharPredicate.f(c))
							throw TextSyntaxCheckedException.inst("Got a non-ignorable character '"+c+"' in an argument after a function expression where we expected an closing '"+closer+"', a delimiting '"+delimiter+"', or ignorable chars (eg, whitespace) and then one of those two.");
					}
				}
			}
		}
	}
	
	
	
	/*
	public static List<Object> parseNestedFunctionInvocationExpression(String src)
	{
		src = src.trim();
		
		List<Object> list = new ArrayList<>();
		
	}
	
	
	
	public static List<Object> parseNestedBracketedThings(String src, char open, char close)
	{
		if (src.isEmpty())
			return emptyList();
		else
		{
			List<Object> list = new ArrayList<>();
			
			
			
			return list;
		}
	}
	 */
	
	
	
	
	
	
	
	
	
	
	
	/*
		TODO GRSON (full-Graph! raw/reinterpretable serialized object notation :D ): like JSON but with!!:
			
			+ Object graphs/references!!
				name = <expr>
				@name
			
			+ Fully-escapable, re-interpretable nonstrings!  (backticks if necessary :> )
			+ Better object notation (nonstrings as keys in dicts/objects allow things like "$class.java: org.pkg.foo", "$class.python: pkg.foo"! :D (XD) )
			
			+ Unordered sets!  (parentheses!)
			
			+ Arbitrarily-keyed maps!
			
			+ COMMENTS!! XD
				+ Nestable comments!!  (if you care to implement that ;> )
			
			+ RAW NEWLINES IN STRINGS!
			
			+ Techniques:
				+ Directives like namespacing imports can be done by making the outside element be an object, and using nonstrings to specify
	
	
	
	
	==
	+ Maps: "{}", ":", "," or "\n"
	+ Lists: "[]", ","
	+ Sets: "()", ","
	+ Any object can be wrapped in any number of nested "<>"['s] to flag it in some way.  The convention will be: mutable, as opposed to a default of immutable.  :>
	
	+ Reference/Pointer usage: "@"
		+ never has to refer to corresponding Assignment in any particular ordering (eg, after declaration/definition); but if it's never assigned when needed, the code using GRSON might see an undeclared reference object! 0,0
	
	+ Non-reference scalars are always textual, but typing can be encoded in the fact that unquoted scalars, and three types of quotes are supported :>
		<nothing>
		""
		''
		``
		
		All support linebreaks within.
		And all are escaped the same way:
			+ Backslash is escape character
			+ Escape code is one character long, except for unicode escapes
			+ Unicode escapes are like python:
				\ x NN
				\ u NNNN
				\ U NNNNNNNN
			+ Escape characters currently are:
				"n" - newline
				"r" - linefeed
				"t" - tab
				<else> - the character it is (used for escaping quotes, and for escaping escapes) :>
		
		Using code is free to interpret these as it wishes; eg, using unquoteds and backticks for integers and keywords and identifiers, double-quotes for (unicode) strings, and single quotes for characters
		Or perhaps single quotes for brief string values, and double-quotes for large bodies of for-human text :)
	
	
	
	
	+ Reference assignment comes after any object, with an "=" sign and then a backtick-quoted scalar (without "@" sign!) for the name :>
	
	
	
	+ Comments: "/*" * /, "//" "\n", "#" "\n"
		+ can appear anywhere except in quoteds; making comments-and-quotes the first level syntax of GRSON, like most other things ^_^
	==
	
	
	
	
	public static Object parseGRSON(Reader source, GRSONParserHelper helper)
	{
		
	}
	
	public static Object parseGRSON(Reader source)
	{
		return parseGRSON(source, RPStandardGRSONParserHelper);
	}
	
	
	public static Object parseGRSON(Object sourcetextthing, GRSONParserHelper helper)
	{
		return parseGRSON(new StringReader(textthingToString(sourcetextthing)), helper);
	}
	
	public static Object parseGRSON(Object sourcetextthing)
	{
		return parseGRSON(new StringReader(textthingToString(sourcetextthing)));
	}
	
	//TODO formatGRSON
	 */
	
	
	
	
	
	
	
	
	
	
	
	public static interface RPBasicNaiveParsingSyntaxElement
	{
		/**
		 * This is useful for eg, creating an embedded version of a syntax (eg, add a state transition to the python syntax of /"</" --> xml.initial to make an embeddable derivative! :D )
		 */
		public RPBasicNaiveParsingSyntaxElement deriveWithAddedTransitions(Map<NaiveCharacterSequencePattern, RPBasicNaiveParsingSyntaxElement> extraTransitions);
	}
	
	public static class RPBasicNaiveParsingSyntaxDescription<SyntaxIdType, StateIdType>
	implements RPBasicNaiveParsingSyntaxElement
	{
		public SyntaxIdType id;
		public RPBasicNaiveParsingSyntaxStateDescription<StateIdType> initialState;
		
		@Override
		public RPBasicNaiveParsingSyntaxDescription<SyntaxIdType, StateIdType> deriveWithAddedTransitions(Map<NaiveCharacterSequencePattern, RPBasicNaiveParsingSyntaxElement> extraTransitions)
		{
			RPBasicNaiveParsingSyntaxDescription<SyntaxIdType, StateIdType> derived = new RPBasicNaiveParsingSyntaxDescription();
			derived.id = this.id;
			derived.initialState = this.initialState.deriveWithAddedTransitions(extraTransitions);
			return derived;
		}
		
		public static class RPBasicNaiveParsingSyntaxStateDescription<StateIdType>
		implements RPBasicNaiveParsingSyntaxElement
		{
			public StateIdType id;
			
			/**
			 * Necessary trick: Use a state transition back to the current state to enact a 'skip' (eg, to skip over escapes ;D )
			 */
			public Map<NaiveCharacterSequencePattern, RPBasicNaiveParsingSyntaxElement> transitions;
			
			
			@Override
			public RPBasicNaiveParsingSyntaxStateDescription<StateIdType> deriveWithAddedTransitions(Map<NaiveCharacterSequencePattern, RPBasicNaiveParsingSyntaxElement> extraTransitions)
			{
				RPBasicNaiveParsingSyntaxStateDescription<StateIdType> derived = new RPBasicNaiveParsingSyntaxStateDescription<StateIdType>();
				derived.id = this.id;
				derived.transitions = ObjectUtilities.attemptClone(this.transitions);
				derived.transitions.putAll(extraTransitions);
				return derived;
			}
		}
	}
	
	
	public static abstract class RPBasicParsedElementElement
	{
		public char[] source;
		public int start, length;
		
		@Override
		public String toString()
		{
			return new String(this.source, this.start, this.length);
		}
		
		
		public static abstract class RPBasicParsedElementElement_Chunk
		{
			public RPBasicNaiveParsingSyntaxDescription syntax;
			public RPBasicNaiveParsingSyntaxStateDescription state;
		}
		
		public static abstract class RPBasicParsedElementElement_Embed
		{
			public RPBasicNaiveParsingSyntaxDescription syntax;
		}
	}
	
	
	public static RPBasicParsedElementElement[] parseRPBasicNaive(RPBasicNaiveParsingSyntaxDescription syntax, Object textthing)
	{
		//char[] src = textthingToPossiblyUnclonedCharArray(textthing);
		
		//TODO
		throw new NotYetImplementedException();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//////////// FORMATTING :D!! ////////////
	
	/**
	 * Append items in the sequence ITEM DEL ITEM DEL ... ITEM DEL ITEM   (ie, no terminating delimiter ^_^ )
	 * @param buff The string buffer to append to.  Note: This is included mainly for compatiblity between StringBuilder and StringBuffer, don't use anything which could throw an IOException
	 */
	public static void appendDelimitedList(Object[] items, Object delimiter, Appendable buff)
	{
		boolean first = true;
		for (Object item : items)
		{
			if (item == null)
				continue;
			
			if (first)
				first = false;
			else
				appendTextthing(buff, delimiter);
			
			appendTextthing(buff, item);
		}
	}
	
	public static void appendDelimitedList(Iterable<?> items, Object delimiter, Appendable buff)
	{
		boolean first = true;
		for (Object item : items)
		{
			if (item == null)
				continue;
			
			if (first)
				first = false;
			else
				appendTextthing(buff, delimiter);
			
			appendTextthing(buff, item);
		}
	}
	
	
	public static void appendDelimitedList(Object[] items, char delimiter, Appendable buff)
	{
		try
		{
			boolean first = true;
			for (Object item : items)
			{
				if (item == null)
					continue;
				
				if (first)
					first = false;
				else
					buff.append(delimiter);
				
				appendTextthing(buff, item);
			}
		}
		catch (IOException exc)
		{
			//throw new IORuntimeException(exc);
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static void appendDelimitedList(Iterable<?> items, char delimiter, Appendable buff)
	{
		try
		{
			boolean first = true;
			for (Object item : items)
			{
				if (item == null)
					continue;
				
				if (first)
					first = false;
				else
					buff.append(delimiter);
				
				appendTextthing(buff, item);
			}
		}
		catch (IOException exc)
		{
			//throw new IORuntimeException(exc);
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	/**
	 * aka getDelimitedList, reduceDelims, combineDelims, join  :>
	 */
	public static String joinStrings(Object[] items, CharSequence delimiter)
	{
		StringBuilder buff = new StringBuilder();
		appendDelimitedList(items, delimiter, buff);
		return buff.toString();
	}
	
	/**
	 * aka getDelimitedList, reduceDelims, combineDelims, join  :>
	 */
	public static String joinStrings(Iterable<?> items, CharSequence delimiter)
	{
		StringBuilder buff = new StringBuilder();
		appendDelimitedList(items, delimiter, buff);
		return buff.toString();
	}
	
	/**
	 * aka getDelimitedList, reduceDelims, combineDelims, join  :>
	 */
	public static String joinStrings(CharSequence delimiter, CharSequence... items)
	{
		StringBuilder buff = new StringBuilder();
		appendDelimitedList(items, delimiter, buff);
		return buff.toString();
	}
	
	
	/**
	 * aka getDelimitedList, reduceDelims, combineDelims, join  :>
	 */
	public static String joinStrings(Object[] items, char delimiter)
	{
		StringBuilder buff = new StringBuilder();
		appendDelimitedList(items, delimiter, buff);
		return buff.toString();
	}
	
	/**
	 * aka getDelimitedList, reduceDelims, combineDelims, join  :>
	 */
	public static String joinStrings(Iterable<?> items, char delimiter)
	{
		StringBuilder buff = new StringBuilder();
		appendDelimitedList(items, delimiter, buff);
		return buff.toString();
	}
	
	/**
	 * aka getDelimitedList, reduceDelims, combineDelims, join  :>
	 */
	public static String joinStrings(char delimiter, CharSequence... items)
	{
		StringBuilder buff = new StringBuilder();
		appendDelimitedList(items, delimiter, buff);
		return buff.toString();
	}
	
	
	
	
	
	
	
	
	
	
	//TODO make this as awesome as descape is now, in analogy :>
	
	/**
	 * Escapes occurrences of certain characters inside a main string and returns the results.<br>
	 * Note that the escape dictionary must include an escape for the escape character itself.<br>
	 * @param original The original string containing things which need escaping
	 * @param escapeChar In C-Style escapes, it is '\'
	 * @param escapes The characters which required escaping and their escape code in this format (which should include the escape character itself), example (basic C escapes): {'\n', 'n',   '\r', 'r',   '"', '"',   '	', 't',   '\\', '\\'}
	 */
	public static String escape(CharSequence original, char escapeChar, char... escapes)
	{
		StringBuilder buffer = new StringBuilder();
		char c = 0;
		MainLoop: for (int i = 0; i < original.length(); i++)
		{
			c = original.charAt(i);
			EscapeLoop: for (int e = 0; e + 1 < escapes.length; e += 2)
			{
				if (c == escapes[e])
				{
					buffer.append(escapeChar);
					buffer.append(escapes[e+1]);
					continue MainLoop;
				}
			}
			
			//this is never reached unless the escape loop doesn't find an escape (due to the continue in it)
			buffer.append(c);
		}
		return buffer.toString();
	}
	
	
	/**
	 * @see #unicodeDescape(Object)
	 */
	public static String unicodeEscape(String original, UnaryFunctionCharToBoolean escapedChars)
	{
		StringBuilder buffer = new StringBuilder();
		char c = 0;
		for (int i = 0; i < original.length(); i++)
		{
			c = original.charAt(i);
			if (c == '\\' || escapedChars.f(c)) //escape character always gets escaped
			{
				buffer.append(unicodeEscapeS(c));
			}
			else
			{
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
	
	public static String unicodeEscape(String original)
	{
		return unicodeEscape(original, GENERALLY_PRINTABLE_NONNEWLINE_CHARS_INVERSE);
	}
	
	
	
	public static char[] unicodeEscapeCA(char c)
	{
		char[] ca = new char[6];
		ca[0] = '\\';
		ca[1] = 'u';
		addFourBEHexCharsCA(c, ca, 2);
		return ca;
	}
	
	public static String unicodeEscapeS(char c)
	{
		return new String(unicodeEscapeCA(c));
	}
	
	
	public static void addFourBEHexChars(char c, Appendable out)
	{
		try
		{
			out.append(uppercaseGeneralAsciiDigitFor((c & 0xF000) >> 12));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x0F00) >> 8));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x00F0) >> 4));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x000F)));
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static void addFourBEHexCharsCA(char c, char[] ca, int offset)
	{
		ca[offset+0] = uppercaseGeneralAsciiDigitFor((c & 0xF000) >> 12);
		ca[offset+1] = uppercaseGeneralAsciiDigitFor((c & 0x0F00) >> 8);
		ca[offset+2] = uppercaseGeneralAsciiDigitFor((c & 0x00F0) >> 4);
		ca[offset+3] = uppercaseGeneralAsciiDigitFor((c & 0x000F));
	}
	
	public static char[] fourBEHexCharsCA(char c)
	{
		char[] ca = new char[4];
		addFourBEHexCharsCA(c, ca, 0);
		return ca;
	}
	
	public static String fourBEHexCharsS(char c)
	{
		return new String(fourBEHexCharsCA(c));
	}
	
	
	
	
	
	
	
	public static char[] bigUnicodeEscapeCA(int c)
	{
		char[] ca = new char[10];
		ca[0] = '\\';
		ca[1] = 'U';
		addEightBEHexCharsCA(c, ca, 2);
		return ca;
	}
	
	public static String bigUnicodeEscapeS(int c)
	{
		return new String(bigUnicodeEscapeCA(c));
	}
	
	
	public static void addEightBEHexChars(int c, Appendable out)
	{
		try
		{
			out.append(uppercaseGeneralAsciiDigitFor((c & 0xF0000000) >> 28));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x0F000000) >> 24));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x00F00000) >> 20));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x000F0000) >> 16));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x0000F000) >> 12));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x00000F00) >> 8));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x000000F0) >> 4));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x0000000F)));
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static void addEightBEHexCharsCA(int c, char[] ca, int offset)
	{
		ca[offset+0] = uppercaseGeneralAsciiDigitFor((c & 0xF0000000) >> 28);
		ca[offset+1] = uppercaseGeneralAsciiDigitFor((c & 0x0F000000) >> 24);
		ca[offset+2] = uppercaseGeneralAsciiDigitFor((c & 0x00F00000) >> 20);
		ca[offset+3] = uppercaseGeneralAsciiDigitFor((c & 0x000F0000) >> 16);
		ca[offset+4] = uppercaseGeneralAsciiDigitFor((c & 0x0000F000) >> 12);
		ca[offset+5] = uppercaseGeneralAsciiDigitFor((c & 0x00000F00) >> 8);
		ca[offset+6] = uppercaseGeneralAsciiDigitFor((c & 0x000000F0) >> 4);
		ca[offset+7] = uppercaseGeneralAsciiDigitFor((c & 0x0000000F));
	}
	
	public static char[] eightBEHexCharsCA(int c)
	{
		char[] ca = new char[4];
		addEightBEHexCharsCA(c, ca, 0);
		return ca;
	}
	
	public static String eightBEHexCharsS(int c)
	{
		return new String(eightBEHexCharsCA(c));
	}
	
	
	
	
	
	
	
	public static void addTwoBEHexChars(int c, Appendable out)
	{
		try
		{
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x00F0) >> 4));
			out.append(uppercaseGeneralAsciiDigitFor((c & 0x000F)));
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static void addTwoBEHexCharsCA(int c, char[] ca, int offset)
	{
		ca[offset+2] = uppercaseGeneralAsciiDigitFor((c & 0x00F0) >> 4);
		ca[offset+3] = uppercaseGeneralAsciiDigitFor((c & 0x000F));
	}
	
	public static char[] twoBEHexCharsCA(int c)
	{
		char[] ca = new char[2];
		addTwoBEHexCharsCA(c, ca, 0);
		return ca;
	}
	
	public static String twoBEHexCharsS(int c)
	{
		return new String(twoBEHexCharsCA(c));
	}
	
	
	
	
	
	
	
	
	
	public static String unicodeHexEscape(int ucs4CharacterCodePoint)
	{
		if (ucs4CharacterCodePoint > 0xFFFF)
			return bigUnicodeEscapeS(ucs4CharacterCodePoint);
		else
			return unicodeEscapeS((char)ucs4CharacterCodePoint);
	}
	
	
	
	
	
	
	
	
	
	public static char numeralAsciiDigitFor(int v)
	{
		if (v < 0 || v >= 10) throw new IllegalArgumentException(String.valueOf(v));
		return (char)('0'+v);
	}
	
	public static char uppercaseGeneralAsciiDigitFor(int v)
	{
		if (v < 0 || v >= 36) throw new IllegalArgumentException(String.valueOf(v));
		
		if (v < 10) return (char)('0'+v);
		else return (char)('A'+(v - 10));
	}
	
	public static char lowercaseGeneralAsciiDigitFor(int v)
	{
		if (v < 0 || v >= 36) throw new IllegalArgumentException(String.valueOf(v));
		
		if (v < 10) return (char)('0'+v);
		else return (char)('a'+(v - 10));
	}
	
	
	
	public static boolean is7bitAsciiCharacter(char c)
	{
		return c <= 127;
	}
	
	
	public static String escapeJavaStandard(String original, boolean escapeSingleQuotes, boolean escapeDoubleQuotes, boolean escapeNonAscii)
	{
		StringBuilder buffer = new StringBuilder();
		char c = 0;
		for (int i = 0; i < original.length(); i++)
		{
			c = original.charAt(i);
			if (c == '\\' || (escapeDoubleQuotes && c == '"') || (escapeSingleQuotes && c == '\'') || c == '\t' || GENERALLY_PRINTABLE_NONNEWLINE_CHARS_INVERSE.f(c) || (escapeNonAscii && !is7bitAsciiCharacter(c)))
			{
				buffer.append(javaEscape(c));
			}
			else
			{
				buffer.append(c);
			}
		}
		return buffer.toString();
	}
	
	public static String escapeJavaStandard(String original, boolean escapeSingleQuotes, boolean escapeDoubleQuotes)
	{
		return escapeJavaStandard(original, escapeSingleQuotes, escapeDoubleQuotes, false);
	}
	
	public static String escapeJavaStandard(String original)
	{
		return escapeJavaStandard(original, false, true);
	}
	
	
	public static String javaEscape(char c)
	{
		if (c == '\\')
			return "\\\\";
		else if (c == '"')
			return "\\\"";
		else if (c == '\'')
			return "\\\'";
		else if (c == '\n')
			return "\\n";
		else if (c == '\r')
			return "\\r";
		else if (c == '\t')
			return "\\t";
		
		//Uhh, a bit less common ones >,>  XD
		else if (c == '\f')
			return "\\f";
		else if (c == '\b')
			return "\\b";
		
		//Todo is that all?
		
		else
			return unicodeEscapeS(c);
	}
	
	
	/**
	 * This escapes a string so that non-space whitespace characters, quotes, and backspaces are escaped in the C-style (useful for debugging)
	 * Control chars are escaped in the \x<i>##</i> syntax, other escapes are the Java standard sequences (backslash, CR, LF, tab, backspace, form-feed, single-quote, double-quote)
	 */
	public static String cescape(String raw)
	{
		StringBuilder buffer = new StringBuilder();
		char c = 0;
		MainLoop: for (int i = 0; i < raw.length(); i++)
		{
			c = raw.charAt(i);
			
			String standardEscape = null;
			{
				if (c == '\\')
					standardEscape = "\\\\";
				else if (c == '\n')
					standardEscape = "\\n";
				else if (c == '\r')
					standardEscape = "\\r";
				else if (c == '\t')
					standardEscape = "\\t";
				else if (c == '\b')
					standardEscape = "\\b";
				else if (c == '\f')
					standardEscape = "\\f";
				else if (c == '\"')
					standardEscape = "\\\"";
				else if (c == '\'')
					standardEscape = "\\\'";
			}
			
			if (standardEscape != null)
			{
				buffer.append(standardEscape);
			}
			else
			{
				if (c < 32)
				{
					buffer.append("\\x");
					buffer.append(c & 0x00F0 >> 16);
					buffer.append(c & 0x000F >>  0);
				}
				else
				{
					buffer.append(c);
				}
			}
		}
		return buffer.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	@SignalType
	public static interface Reprable
	{
		//Guess what language I stole *this* from! XDDD
		public default String reprThis()
		{
			return toString();
		}
	}
	
	
	
	
	
	public static String repr(Object o)
	{
		return reprr(o, Collections.EMPTY_SET, null);
	}
	
	public static String repr(Object o, Class... classesToDeeplyBeanilyInspect)
	{
		return reprr(o, Arrays.asList(classesToDeeplyBeanilyInspect), null);
	}
	
	public static String repr(Object o, Collection<Class> classesToDeeplyBeanilyInspect)
	{
		return reprr(o, classesToDeeplyBeanilyInspect, null);
	}
	
	
	
	/*
	public static String reprBeanily(Object o)
	{
		return reprrBeanily(o, Collections.EMPTY_SET, null);
	}
	
	public static String reprBeanily(Object o, Class... classesToDeeplyBeanilyInspect)
	{
		return reprrBeanily(o, Arrays.asList(classesToDeeplyBeanilyInspect), null);
	}
	
	public static String reprBeanily(Object o, Collection<Class> classesToDeeplyBeanilyInspect)
	{
		return reprrBeanily(o, classesToDeeplyBeanilyInspect, null);
	}
	 */
	
	
	
	protected static final int SequenceChildmembersReprStringLengthThresholdForMultiline = 20; // > 20 chars makes things each get their own line :>
	
	
	//Todo optimize to just use one StringBuilder ._.
	protected static String reprr(Object o, final Collection<Class> classesToDeeplyBeanilyInspect, IdentityHashSet alreadyDoneM)
	{
		if (o == null)
			return String.valueOf(o);
		
		boolean scalar = Primitives.isPrimitiveWrapperClass(o.getClass()) ||
		o instanceof String || o instanceof StringBuffer || o instanceof StringBuilder ||
		o instanceof ImmutableByteArrayList ||
		o instanceof Class || //Todo more reflect things ._.
		o instanceof Enum;
		
		if (scalar)
		{
			if (o instanceof Byte)
			{
				return "(byte)"+o;
			}
			else if (o instanceof Short)
			{
				return "(short)"+o;
			}
			else if (o instanceof Long)
			{
				return o+"l";
			}
			else if (o instanceof Float)
			{
				return o+"f";
			}
			else if (o instanceof Double)
			{
				return o.toString();
			}
			else if (o instanceof Integer)
			{
				return o.toString();
			}
			else if (o instanceof Boolean)
			{
				return o.toString();
			}
			
			else if (o instanceof CharSequence)
				return "\""+escapeJavaStandard(o.toString())+"\"";
			else if (o instanceof Character)
				return "'"+escapeJavaStandard(o.toString())+"'";
			
			else if (o instanceof ImmutableByteArrayList)
				return UIDUtilities.formatString((ImmutableByteArrayList)o);
			
			else if (o instanceof Class)
			{
				return o.toString();
			}
			
			else if (o instanceof Enum)
			{
				//return o.toString(); //Use whatever code they define :3   (is distinguished from strings/text in that they would have double quotes ^_^ )
				return o.getClass().getName() + "." + ((Enum)o).name();  //they can implement the new Reprable interface to override this if they wants :333
			}
			
			else
				throw new ImpossibleException("you forgot to add a case for it silly me >,>");
		}
		else
		{
			final IdentityHashSet alreadyDone = alreadyDoneM == null ? new IdentityHashSet() : alreadyDoneM;
			
			if (alreadyDone.contains(o))
				//This keeps it from falling into infinite recursions for cyclic object graphs! ^w^
				return "<instance already represented>"; //todo lp: a mode that adds ids before each thing, and uses these here to identify which one it was!
			
			alreadyDone.add(o);
			
			
			if (o instanceof Collection || o.getClass().isArray() || o instanceof Slice)
			{
				boolean isArray = o.getClass().isArray();
				
				StringBuilder out = new StringBuilder();
				
				if (isArray)
				{
					out.append("new ");
					out.append(o.getClass().getCanonicalName());
					out.append("{");
				}
				else if (o instanceof Slice)
				{
					Slice s = (Slice) o;
					Object u = s.getUnderlying();
					
					
					out.append("Slice(");
					
					if (u.getClass().isArray())
						out.append(u.getClass().getCanonicalName());
					else
						out.append(u.getClass().getName());
					
					out.append(")[");
				}
				else
				{
					out.append("(");
					out.append(o.getClass().getName());
					out.append(")[");
				}
				
				reprListContents(PolymorphicCollectionUtilities.anyToIterable(o), c -> reprr(c, classesToDeeplyBeanilyInspect, alreadyDone), out);
				
				if (isArray)
					out.append('}');
				else
					out.append(']');
				
				return out.toString();
			}
			
			else if (o instanceof Map)
			{
				StringBuilder out = new StringBuilder("(");
				out.append(o.getClass().getName());
				out.append("){");
				reprMapContents((Map)o, c -> reprr(c, classesToDeeplyBeanilyInspect, alreadyDone), out);
				out.append("}");
				
				return out.toString();
			}
			
			else if (o instanceof Reprable)
			{
				return ((Reprable)o).reprThis();
			}
			
			else
			{
				//				if (classesToDeeplyBeanilyInspect != null && classesToDeeplyBeanilyInspect.contains(o.getClass()))
				//				{
				//					return reprrBeanily(o, classesToDeeplyBeanilyInspect, alreadyDone);
				//				}
				//				else
				//				{
				return "("+o.getClass().getName()+")"+repr(o.toString()); //doesn't need to be reprr because it's definitely a string! x>   (I guess it's better that it displayes it agains if the String instance haz already been stringserialized :> )
				//				}
			}
		}
	}
	
	
	
	public static String reprMapContents(Map o, UnaryFunction<Object, String> repr)
	{
		StringBuilder out = new StringBuilder();
		reprMapContents(o, repr, out);
		return out.toString();
	}
	
	
	
	
	protected static final Comparator<Entry<Object, Object>> ReprMapKeysBestEffortSortingComparator = new Comparator<Entry<Object, Object>>()
	{
		@Override
		public int compare(Entry<Object, Object> a, Entry<Object, Object> b)
		{
			Object ak = a.getKey();
			Object bk = b.getKey();
			
			if (ak instanceof Comparable)
			{
				if (bk instanceof Comparable)
					return cmp2(ak, bk);
				else
					return 1;
			}
			else
			{
				if (bk instanceof Comparable)
					return -1;
				else
					return 0;
			}
		}
	};
	
	
	public static void reprMapContents(Map o, UnaryFunction<Object, String> repr, StringBuilder out)
	{
		//Todo make this multi/single line detecting like list/array is, too! :D
		
		if (!o.isEmpty())
		{
			out.append("\n\t");
			
			boolean first = true;
			for (Object oEntry : sorted(((Map<Object, Object>)o).entrySet(), ReprMapKeysBestEffortSortingComparator))
			{
				if (!first) out.append(",\n\t"); first = false;
				
				Map.Entry entry = (Map.Entry)oEntry;
				
				out.append(repr.f(entry.getKey()).replace("\n", "\n\t"));
				out.append(": ");
				out.append(repr.f(entry.getValue()).replace("\n", "\n\t"));
			}
			
			out.append("\n");
		}
	}
	
	
	/**
	 * Note: this doesn't add brackets, so you might want to do something like, "{" + {@link #reprMapContents(Map)} + "}"  ^w^
	 */
	public static String reprMapContents(Map map)
	{
		StringBuilder out = new StringBuilder();
		reprMapContents(map, StringUtilities::repr, out);
		return out.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	public static String reprListContents(Iterable o, UnaryFunction<Object, String> repr)
	{
		StringBuilder out = new StringBuilder();
		reprListContents(o, repr, out);
		return out.toString();
	}
	
	public static void reprListContents(Iterable list, UnaryFunction<Object, String> repr, StringBuilder out)
	{
		if (!BasicCollectionUtilities.isEmptyIterable(list))
		{
			Iterable<String> childrenReprs = mapToNewCollection(input -> repr.f(input).replace("\n", "\n\t"), list);
			
			
			boolean multiline = false;
			{
				for (String childRepr : childrenReprs)
				{
					if (childRepr.contains("\n") || childRepr.length() > SequenceChildmembersReprStringLengthThresholdForMultiline)
					{
						multiline = true;
						break;
					}
				}
			}
			
			
			if (multiline)
				out.append("\n\t");
			
			boolean first = true;
			for (String childRepr : childrenReprs)
			{
				if (!first) out.append(multiline ? ",\n\t" : ", "); first = false;
				
				out.append(childRepr);
			}
			
			if (multiline)
				out.append("\n");
		}
	}
	
	
	/**
	 * Note: this doesn't add brackets, so you might want to do something like, "[" + {@link #reprListContents(Iterable)} + "]"  ^w^
	 */
	public static String reprListContents(Iterable list)
	{
		StringBuilder out = new StringBuilder();
		reprListContents(list, StringUtilities::repr, out);
		return out.toString();
	}
	public static String reprListContentsSingleLine(Iterable list)
	{
		return singleLineify(reprListContents(list));
	}
	
	/**
	 * Just does {@link PolymorphicCollectionUtilities#anyToIterable(Object)} then {@link #reprListContents(Iterable)} ^w^
	 */
	public static String reprListContents(Object list)
	{
		return reprListContents(PolymorphicCollectionUtilities.anyToIterable(list));
	}
	public static String reprListContentsSingleLine(Object list)
	{
		return singleLineify(reprListContents(list));
	}
	
	public static String reprListContentsV(Object... list)
	{
		return reprListContents(Arrays.asList(list));
	}
	public static String reprListContentsSingleLineV(Object... list)
	{
		return singleLineify(reprListContents(Arrays.asList(list)));
	}
	
	
	//	protected static String reprrBeanily(Object o, Collection<Class> classesToDeeplyBeanilyInspect, IdentityHashSet alreadyDone)
	//	{
	//		if (o == null)
	//			return String.valueOf(o);
	//
	//		StringBuilder out = new StringBuilder();
	//
	//		out.append("new ");
	//		out.append(o.getClass().getName());
	//		out.append("{");
	//
	//		Map<String, JavaBeanProperty> properties = JavaUtilities.inspect(o.getClass());
	//
	//		if (!properties.isEmpty())
	//			out.append("\n\t");
	//
	//		boolean first = true;
	//		for (String propertyName : CollectionUtilities.sorted(properties.keySet()))
	//		{
	//			if (!first) out.append(",\n\t"); first = false;
	//			out.append(propertyName);
	//			out.append(" = ");
	//			out.append(reprr(properties.get(propertyName).get(o), classesToDeeplyBeanilyInspect, alreadyDone).replace("\n", "\n\t"));
	//		}
	//
	//		out.append("\n");
	//
	//		out.append("}");
	//
	//
	//		return out.toString();
	//	}
	
	
	
	
	
	//Todo different formats for repr (using an enum?)  (eg, single-line vs. [indented] multi-line; valid java vs. java-ish vs. pretty)
	
	public static String singleLineify(String s)
	{
		return s.replace("\t", "").replace("\n", " ");
	}
	
	public static String reprSingleLine(Object o)
	{
		return singleLineify(repr(o));
	}
	
	public static String reprSingleLine(Object o, Class... classesToDeeplyBeanilyInspect)
	{
		return singleLineify(repr(o, classesToDeeplyBeanilyInspect));
	}
	
	public static String reprSingleLine(Object o, Collection<Class> classesToDeeplyBeanilyInspect)
	{
		return singleLineify(repr(o, classesToDeeplyBeanilyInspect));
	}
	
	/*
	public static String reprBeanilySingleLine(Object o)
	{
		return reprBeanily(o).replace("\t", "").replace("\n", " ");
	}
	 */
	
	
	/**
	 * Pads the integer with leading zeros out to the specified length, if needed.
	 * Note that it may be longer than the given length if the given integer is too big (integer >= Math.pow(radix, minDigits) )  :>
	 * Note: minDigits doesn't include the negative sign character.
	 * @param minDigits the minimum number of digits (which will only be exceeded if the provided integer is too large)
	 * @param radix the base (eg, 10 for decimal, 16 for hexadecimal)
	 */
	public static String getSimpleLeadingZeroPaddedIntegerRepresentation(long integer, int minDigits, int radix)
	{
		long positiveInteger = Math.abs(integer);
		String normal = Long.toString(positiveInteger, radix);
		int normalLength = normal.length();
		
		if (normalLength >= minDigits)
			return integer < 0 ? '-'+normal : normal;
		else
		{
			String lz = mul('0', minDigits - normalLength);
			return integer < 0 ? '-'+lz+normal : lz+normal;
		}
	}
	
	/**
	 * Just {@link #getSimpleLeadingZeroPaddedIntegerRepresentation(long, int, int)} for radix=10 (decimal).
	 */
	public static String getSimpleLeadingZeroPaddedIntegerRepresentation(long integer, int minDigits)
	{
		return getSimpleLeadingZeroPaddedIntegerRepresentation(integer, minDigits, 10);
	}
	
	
	
	
	
	public static String toFixedLengthBinaryString(boolean v)
	{
		return v ? "1" : "0";
	}
	
	public static String toFixedLengthBinaryString(byte v)
	{
		String s = Integer.toBinaryString(v & 0xFF);
		if (s.length() < 8)
			s = StringUtilities.mul('0', 8 - s.length()) + s;
		return s;
	}
	
	public static String toFixedLengthBinaryString(short v)
	{
		String s = Integer.toBinaryString(v & 0xFFFF);
		if (s.length() < 16)
			s = StringUtilities.mul('0', 16 - s.length()) + s;
		return s;
	}
	
	public static String toFixedLengthBinaryString(char v)
	{
		String s = Integer.toBinaryString(v & 0xFFFF);
		if (s.length() < 16)
			s = StringUtilities.mul('0', 16 - s.length()) + s;
		return s;
	}
	
	public static String toFixedLengthBinaryString(int v)
	{
		String s = Integer.toBinaryString(v);
		if (s.length() < 32)
			s = StringUtilities.mul('0', 32 - s.length()) + s;
		return s;
	}
	
	public static String toFixedLengthBinaryString(long v)
	{
		String s = Long.toBinaryString(v);
		if (s.length() < 64)
			s = StringUtilities.mul('0', 64 - s.length()) + s;
		return s;
	}
	
	
	
	
	public static String toFixedLengthHexStringLowercase(byte v)
	{
		int i = Unsigned.upcast(v);
		if (i <= 0x0F)
			return "0" + Integer.toHexString(i);
		else
			return Integer.toHexString(i);
	}
	
	public static String toFixedLengthHexStringLowercase(short v)
	{
		int i = Unsigned.upcast(v);
		if (i <= 0x000F)
			return "000" + Integer.toHexString(i);
		else if (i <= 0x00FF)
			return "00" + Integer.toHexString(i);
		else if (i <= 0x0FFF)
			return "0" + Integer.toHexString(i);
		else
			return Integer.toHexString(i);
	}
	
	public static String toFixedLengthHexStringLowercase(char v)
	{
		int i = v;  //char = u16, short = s16!
		if (i <= 0x000F)
			return "000" + Integer.toHexString(i);
		else if (i <= 0x00FF)
			return "00" + Integer.toHexString(i);
		else if (i <= 0x0FFF)
			return "0" + Integer.toHexString(i);
		else
			return Integer.toHexString(i);
	}
	
	public static String toFixedLengthHexStringLowercase(int v)
	{
		String s = Integer.toHexString(v);
		
		int l = s.length();
		
		if (l > 8) throw new AssertionError();
		
		if (l == 8)
			return s;
		
		char[] c = new char[8];
		
		for (int x = 0; x < 8-l; x++)
			c[x] = '0';
		for (int x = 8-l; x < 8; x++)
			c[x] = s.charAt(x - (8-l));
		
		return new String(c);
	}
	
	public static String toFixedLengthHexStringLowercase(long v)
	{
		String s = Long.toHexString(v);
		
		int l = s.length();
		
		if (l > 16) throw new AssertionError();
		
		if (l == 16)
			return s;
		
		char[] c = new char[16];
		
		for (int x = 0; x < 16-l; x++)
			c[x] = '0';
		for (int x = 16-l; x < 16; x++)
			c[x] = s.charAt(x - (16-l));
		
		return new String(c);
	}
	
	
	
	
	public static String reprBitfield(Object v)
	{
		if (v instanceof Long)
			return reprBitfield((long)(Long)v);
		else if (v instanceof Integer)
			return reprBitfield((int)(Integer)v);
		else if (v instanceof Character)
			return reprBitfield((char)(Character)v);
		else if (v instanceof Short)
			return reprBitfield((short)(Short)v);
		else if (v instanceof Byte)
			return reprBitfield((byte)(Byte)v);
		else if (v instanceof Boolean)
			return reprBitfield((boolean)(Boolean)v);
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(v);
	}
	
	
	
	
	
	
	
	
	
	
	public static String reprBitfield(long v)
	{
		return "0b"+toFixedLengthBinaryString(v)+"L";
	}
	
	public static String reprBitfield(int v)
	{
		return "0b"+toFixedLengthBinaryString(v);
	}
	
	public static String reprBitfield(char v)
	{
		return "(char)0b"+toFixedLengthBinaryString(v);
	}
	
	public static String reprBitfield(short v)
	{
		return "(short)0b"+toFixedLengthBinaryString(v);
	}
	
	public static String reprBitfield(byte v)
	{
		return "(byte)0b"+toFixedLengthBinaryString(v);
	}
	
	public static String reprBitfield(boolean v)
	{
		return Boolean.toString(v);
	}
	
	
	
	
	
	public static String toFixedLengthBinaryString(Object v)
	{
		if (v instanceof Long)
			return toFixedLengthBinaryString((long)(Long)v);
		else if (v instanceof Integer)
			return toFixedLengthBinaryString((int)(Integer)v);
		else if (v instanceof Character)
			return toFixedLengthBinaryString((char)(Character)v);
		else if (v instanceof Short)
			return toFixedLengthBinaryString((short)(Short)v);
		else if (v instanceof Byte)
			return toFixedLengthBinaryString((byte)(Byte)v);
		else if (v instanceof Boolean)
			return toFixedLengthBinaryString((boolean)(Boolean)v);
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(v);
	}
	
	
	
	
	
	
	/**
	 * Where "identifier" here means anything that can only contain ASCII uppercase, lowercase, digits, and underscore :3
	 * 
	 * (hint: it's underscore followed by four hex digits, encoding in UTF-16  X3  (including the literal underscores!) )
	 */
	public static String simpleIdentifierMunging(Object s)
	{
		char[] ca = textthingToPossiblyUnclonedCharArray(s);
		
		//Check if clean first!
		boolean clean = true;
		{
			for (char c : ca)
			{
				if (!(isAsciiLowercase(c) || isAsciiUppercase(c) || isAsciiDigit(c) || c == '_'))
				{
					clean = false;
					break;
				}
			}
		}
		
		
		if (clean)
			return textthingToString(s);
		
		
		//Actually munge!
		{
			StringBuilder b = new StringBuilder();
			
			int cleanRegionStart = 0;
			
			for (int i = 0; i < ca.length; i++)
			{
				char c = ca[i];
				
				if (!(isAsciiDigit(c) || isAsciiLowercase(c) || isAsciiUppercase(c)))
				{
					if (cleanRegionStart - i > 0)
						b.append(ca, cleanRegionStart, i - cleanRegionStart);
					
					b.append('_');
					addFourBEHexChars(c, b);
					
					cleanRegionStart = i+1;
				}
			}
			
			//eof ^^
			{
				if (cleanRegionStart - ca.length > 0)
					b.append(ca, cleanRegionStart, ca.length - cleanRegionStart);
			}
			
			return b.toString();
		}
	}
	
	
	public static String simpleIdentifierDeMunging(Object s)
	{
		char[] ca = textthingToPossiblyUnclonedCharArray(s);
		
		//Check if clean first!
		boolean clean = true;
		{
			for (char c : ca)
			{
				if (!(isAsciiLowercase(c) || isAsciiUppercase(c) || isAsciiDigit(c) || c == '_'))
				{
					clean = false;
					break;
				}
			}
		}
		
		
		if (clean)
			return textthingToString(s);
		
		
		//Actually munge!
		{
			StringBuilder b = new StringBuilder();
			
			int cleanRegionStart = 0;
			
			for (int i = 0; i < ca.length; i++)
			{
				char c = ca[i];
				
				if (c == '_')
				{
					if (cleanRegionStart - i > 0)
						b.append(ca, cleanRegionStart, i - cleanRegionStart);
					
					if (i+1+4 <= ca.length) //silent ignore encoding errorthings :3
					{
						//SPEED
						int v = Integer.parseInt(new String(ca, i+1, 4), 16);
						assert v >= 0 && v <= 0xFFFF;
						b.append((char)i);
					}
					
					cleanRegionStart = i+1+4;
				}
			}
			
			//eof ^^
			{
				if (cleanRegionStart - ca.length > 0)
					b.append(ca, cleanRegionStart, ca.length - cleanRegionStart);
			}
			
			return b.toString();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String reverse(String x)
	{
		if (x.length() < 2)
			return x;
		else
		{
			//Todo UCS-4 things? ;;
			char[] c = x.toCharArray();
			ArrayUtilities.reverse(c);
			return new String(c);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * @throws IOException on a UTF-16 decoding error!
	 */
	public static int[] UTF16ToUCS4(char[] utf16charsLikeNormalJava) throws IOException
	{
		UCS4Reader utf16Decoder = new UCS4ReaderFromNormalUTF16Reader(new CharArrayReader(utf16charsLikeNormalJava));
		return TextIOUtilities.readAll(utf16Decoder);
	}
	
	
	@Nonnull
	public static int[] toCodePointArray(@Nonnull String unicodeString)
	{
		//why can't there just be an int[] String.toCodepointArray()??  There's a new String(int[], ...)!!  X'D
		
		UCS4Reader utf16Decoder = new UCS4ReaderFromNormalUTF16Reader(new StringReader(unicodeString));
		
		try
		{
			return TextIOUtilities.readAll(utf16Decoder);
		}
		catch (IOException exc)
		{
			throw new ImpossibleException("java.lang.String's containing illegal UTF-16 encoded chars is best to consider a RuntimeException (bug), I think!    (but apparently that's what happened here x\"D )", exc);
		}
	}
	
	
	
	
	
	public static void defaultWriteStringToUCS4(CharSequence source, UCS4Writer dest) throws IOException
	{
		defaultWriteStringToUCS4(source.toString(), dest);
	}
	
	public static void defaultWriteStringToUCS4(String source, UCS4Writer dest) throws IOException
	{
		defaultWriteUTF16toUCS4(new StringReader(source), dest);
	}
	
	
	public static void defaultWriteUTF16toUCS4(Reader source, UCS4Writer dest) throws IOException
	{
		UCS4Reader utf16Decoder = new UCS4ReaderFromNormalUTF16Reader(source);
		
		TextIOUtilities.pump(utf16Decoder, dest);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	protected static final int ByteArrayMaxTostringLengthInBytes = 128;
	
	public static String byteArrayToString(byte[] data)
	{
		StringBuilder s = new StringBuilder();
		
		s.append('(');
		s.append(data.length);
		s.append(" immutable bytes) ");
		
		s.append(DataEncodingUtilities.encodeHex(data, 0, least(data.length, ByteArrayMaxTostringLengthInBytes), DataEncodingUtilities.HEX_UPPERCASE, (String)null));
		
		if (data.length > ByteArrayMaxTostringLengthInBytes)
			s.append("...");
		
		return s.toString();
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String toStringU64(@ActuallyUnsignedValue long value, int radix)
	{
		if (value == 0)
			return "0";
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
			radix = 10;
		
		//Declare
		char[] chars = new char[64]; //Note: Backwards
		int count = 0; //String size
		int digit = 0; //238= 8  3  2
		
		//Convert
		while (greaterThanU64(value, 0)) //value > 0
		{
			digit = (int)(modulusU64(value, radix)); //value % radix
			value -= digit;
			value = divideU64(value, radix); //value / radix
			
			count++;
			chars[64-count] = Character.forDigit(digit, radix);
		}
		
		//Store
		return new String(chars, 64-count, count);
	}
	
	
	public static String toStringU32(@ActuallyUnsignedValue int value, int radix)
	{
		if (value == 0)
			return "0";
		if (radix < Character.MIN_RADIX || radix > Character.MAX_RADIX)
			radix = 10;
		
		//Declare
		char[] chars = new char[32]; //Note: Backwards
		int count = 0; //String size
		int digit = 0; //238 = 8 3 2
		
		//Convert
		while (greaterThanU32(value, 0)) //value > 0
		{
			digit = modulusU32(value, radix); //value % radix
			value -= digit;
			value = divideU32(value, radix); //value / radix
			
			count++;
			chars[32-count] = Character.forDigit(digit, radix);
		}
		
		//Store
		return new String(chars, 32-count, count);
	}
	
	/**
	 * Important because it would otherwise be added as an actual unicode character!! :P
	 */
	public static String toStringU16(char value, int radix)
	{
		return toStringU32(value, radix);
	}
	
	public static String toStringU8(@ActuallyUnsignedValue byte value, int radix)
	{
		return toStringU32(upcast(value), radix);
	}
	
	
	
	public static String toStringU64(@ActuallyUnsignedValue long value)
	{
		return toStringU64(value, 10);
	}
	
	public static String toStringU32(@ActuallyUnsignedValue int value)
	{
		return toStringU32(value, 10);
	}
	
	/**
	 * Important because it would otherwise be added as an actual unicode character!! :P
	 */
	public static String toStringU16(char value)
	{
		return toStringU16(value, 10);
	}
	
	public static String toStringU8(@ActuallyUnsignedValue byte value)
	{
		return toStringU8(value, 10);
	}
	
	
	
	
	
	
	
	
	
	
	
	public static String arrayToString(Object array)
	{
		if (array == null)
			return String.valueOf(null);
		else if (array instanceof Object[])
			return Arrays.toString((Object[])array);
		
		/* <<<
primxp
		else if (array instanceof _$$prim$$_[])
			return Arrays.toString((_$$prim$$_[])array);
		 */
		else if (array instanceof boolean[])
			return Arrays.toString((boolean[])array);
		else if (array instanceof byte[])
			return Arrays.toString((byte[])array);
		else if (array instanceof char[])
			return Arrays.toString((char[])array);
		else if (array instanceof short[])
			return Arrays.toString((short[])array);
		else if (array instanceof float[])
			return Arrays.toString((float[])array);
		else if (array instanceof int[])
			return Arrays.toString((int[])array);
		else if (array instanceof double[])
			return Arrays.toString((double[])array);
		else if (array instanceof long[])
			return Arrays.toString((long[])array);
		
		// >>>
		
		else
			throw BasicExceptionUtilities.newClassCastExceptionOrNullPointerException(array);
	}
	
	
	
	
	/**
	 * Convert simple 7-bit US-ASCII text encoding into Unicode UCS2 encoding (Java's native encoding) ^,^
	 * ..ie, just copy the numerical values XDDD  (and validate that they're correct 7-bit ASCII!, hence the possible {@link MalformedInputException}!! /o/ )
	 */
	public static char[] simple7bitAsciiToUCS2Chars(byte[] asciiBytes) throws MalformedInputException
	{
		int n = asciiBytes.length;
		char[] c = new char[n];
		
		for (int i = 0; i < n; i++)
		{
			int b = asciiBytes[i] & 0xFF;
			
			//All 128 "codepoints" of Standard US-ASCII are numerically equivalent to their Unicode counterparts \:DDD/
			if (b >= 128)
				throw new MalformedInputException(i);
			
			c[i] = (char)b;
		}
		
		return c;
	}
	
	
	
	/**
	 * Convert simple 8-bit ISO-8859-1 text encoding into Unicode UCS2 encoding (Java's native encoding) ^,^
	 * ..ie, just copy the numerical values XDDD
	 */
	public static char[] simpleISO88591ToUCS2Chars(byte[] asciiBytes)
	{
		int n = asciiBytes.length;
		char[] c = new char[n];
		
		for (int i = 0; i < n; i++)
		{
			int b = asciiBytes[i] & 0xFF;
			
			//The only difference is that all 256 "codepoints" of ISO-8859-1 are numerically equivalent to their Unicode counterparts, not just the first 128 :333
			
			c[i] = (char)b;
		}
		
		return c;
	}
	
	
	
	
	
	
	
	
	public static String canonicalizeWhitespace(String s)
	{
		return charuniq(replaceWhitespace(s, ' '), c -> c == ' ');
	}
	
	
	
	
	@Nonnull
	public static String charuniq(@Nonnull String s)
	{
		return charuniq(s, c -> c == ' ');
	}
	
	
	/**
	 * replaces repetitions of any char matching the predicate with a single occurrence
	 * 
	 * (eg, to reduce multiple spaces into one space :>
	 *   though you may or may not want to call .replace('\t', ' ') or {@link #replaceWhitespace(String)} or etc. first if you're using it for that :> )
	 */
	@Nonnull
	public static String charuniq(@Nonnull String s, @Nullable UnaryFunctionCharToBoolean characterPredicate)
	{
		//Todo optimize to append whole substrings at a time??
		
		StringBuilder rv = new StringBuilder();
		
		char prev = 0;
		
		int n = s.length();
		for (int i = 0; i < n; i++)
		{
			char c = s.charAt(i);
			
			if (i == 0)
			{
				rv.append(c);
			}
			else
			{
				if (c != prev || (characterPredicate != null && !characterPredicate.f(c)))
				{
					rv.append(c);
				}
			}
			
			prev = c;
		}
		
		return rv.toString();
	}
	
	
	
	
	
	
	
	public static String replaceWhitespace(String s)
	{
		return replaceWhitespace(s, ' ');
	}
	
	public static String replaceWhitespace(String s, char replacement)
	{
		return replaceCharsByPattern(s, WHITESPACE_PATTERN, replacement);
	}
	
	public static String replaceCharsByPattern(String s, UnaryFunctionCharToBoolean charPattern, char replacement)
	{
		return new String(replaceByPatternToNew(s.toCharArray(), charPattern, replacement));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Todo mapString()! ^_~
	
	
	
	
	public static String filterString(UnaryFunctionCharToBoolean predicate, String s)
	{
		return new String(filterArray(predicate, s.toCharArray()));
	}
	
	public static String filterAwayWhitespace(String s)
	{
		return filterString(WHITESPACE_PATTERN_INVERSE, s);
	}
	
	
	
	
	
	
	
	
	
	
	//Todo these for other textthings! ^^
	
	public static String filterStringTwoPass(UnaryFunctionCharToBoolean filterPredicate, String input)
	{
		int length = input.length();
		int outputSize = 0;
		
		for (int i = 0; i < length; i++)
		{
			char c = input.charAt(i);
			
			if (filterPredicate.f(c))
			{
				outputSize++;
			}
		}
		
		
		
		
		if (outputSize == length)
			return input;
		
		else
		{
			char[] output = new char[outputSize];
			int e = 0;
			
			for (int i = 0; i < length; i++)
			{
				char c = input.charAt(i);
				
				if (filterPredicate.f(c))
				{
					output[e] = c;
					e++;
				}
			}
			
			return new String(output);
		}
		
		
		
		//Old single-pass, but extra-allocating and no unmodified-check version!
		//		char[] output = new char[input.length()];
		//		int s = 0;
		//
		//		for (int i = 0; i < output.length; i++)
		//		{
		//			char c = input.charAt(i);
		//
		//			if (filterPredicate.f(c))
		//			{
		//				output[s] = c;
		//				s++;
		//			}
		//		}
		//
		//		if (s != output.length)
		//		{
		//			char[] trimmed = new char[s];
		//			System.arraycopy(output, 0, trimmed, 0, s);
		//			output = trimmed;
		//		}
		//
		//		return new String(output);
	}
	
	
	
	
	
	
	
	//Todo support length-nonpreserving canonicalizations??
	/**
	 * NOTE: the canonicalization here MUST preserve length!!!
	 */
	public static String canonicalizeOnlyOccurrencesOfCertainString(String string, String substring, UnaryFunction<String, String> canonicalizer)
	{
		int n = string.length();
		int nSub = substring.length();
		
		if (nSub == 0)
			return string;
		
		//This is a super-quick check we can do ^www^
		if (nSub > n)
			return string;
		
		String stringC = canonicalizer.f(string);
		String substringC = canonicalizer.f(substring);
		assert stringC.length() == n;
		assert substringC.length() == nSub;
		
		boolean first = true;
		int prevLocation = -1;
		
		StringBuilder b = new StringBuilder();
		
		while (true)
		{
			int nextLocation = stringC.indexOf(substringC, prevLocation+1);
			
			if (nextLocation != -1)
			{
				if (first)
				{
					b.append(string, 0, nextLocation);
					first = false;
				}
				else
				{
					b.append(string, prevLocation+nSub, nextLocation);
				}
				
				assert eq(substringC, canonicalizer.f(string.substring(nextLocation, nextLocation+nSub)));
				b.append(substringC);
				
				prevLocation = nextLocation;
			}
			else
			{
				break;
			}
		}
		
		if (first)
		{
			//None found!
			return string;
		}
		else
		{
			b.append(string, prevLocation+nSub, n);
		}
		
		return b.toString();
	}
	
	
	
	
	
	/**
	 * Useful for, eg, GDSFieldCustomAcyclicDecoder and similar ^www^
	 */
	public static int parseIntBase10Unoverloaded(String s)
	{
		return Integer.parseInt(s);
	}
	
	/**
	 * Useful for, eg, GDSFieldCustomAcyclicDecoder and similar ^www^
	 */
	public static long parseLongBase10Unoverloaded(String s)
	{
		return Long.parseLong(s);
	}
	
	
	
	/**
	 * Useful for, eg, GDSFieldCustomAcyclicEncoder and similar ^www^
	 */
	public static String intToStringBase10Unoverloaded(int v)
	{
		return Integer.toString(v);
	}
	
	/**
	 * Useful for, eg, GDSFieldCustomAcyclicEncoder and similar ^www^
	 */
	public static String longToStringBase10Unoverloaded(long v)
	{
		return Long.toString(v);
	}
	
	
	
	public static enum CaseSensitivity
	{
		CaseSensitive,
		CaseINsensitive,
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static String toTwoDigitStringWithLeadingZeroPad(int v)
	{
		if (v < 10)
			return "0" + v;
		else
			return Integer.toString(v);
	}
	
	public static String toThreeDigitStringWithLeadingZeroPad(int v)
	{
		if (v < 10)
			return "00" + v;
		else if (v < 100)
			return "0" + v;
		else
			return Integer.toString(v);
	}
	
	
	public static String zeroPad(String s, int minDigits)
	{
		int n = minDigits - s.length();
		return n > 0 ? mul('0', n) + s : s;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static String convertIndentationToTabs(String s)
	{
		StringBuilder buff = new StringBuilder();
		
		boolean hasIndentation = false;
		int numberOfSpacesPerIndentLevel = 0;
		
		boolean first = true;
		for (String line : splitlines(s))
		{
			if (first)
			{
				first = false;
			}
			else
			{
				buff.append('\n');
			}
			
			
			if (!hasIndentation)
			{
				if (line.startsWith("\t"))
				{
					//It uses tabs already! XD :D
					return s;
				}
				else if (line.startsWith(" "))
				{
					//We (kind of have to) assume the first indentation is 1 level deep ^^'
					
					numberOfSpacesPerIndentLevel = countLeading(line, ' ');
					hasIndentation = true;
					
					buff.append('\t');
					buff.append(line.substring(numberOfSpacesPerIndentLevel));
				}
				else
				{
					buff.append(line);
				}
			}
			else
			{
				int numberOfSpacesInThisLine = countLeading(line, ' ');
				
				int indentLevel = numberOfSpacesInThisLine / numberOfSpacesPerIndentLevel;  //Flooring division is important in case there isn't an integer multiple number of spaces ^^'
				
				int i = indentLevel * numberOfSpacesPerIndentLevel;
				
				appendMultiple(buff, '\t', indentLevel);
				buff.append(line.substring(i));
			}
		}
		
		return buff.toString();
	}
	
	
	
	
	
	
	public static int countLeading(String s, char c)
	{
		return countLeadingMatching(s, cc -> cc == c);
	}
	
	public static int countLeadingMatching(String s, UnaryFunctionCharToBoolean predicate)
	{
		int i = 0;
		while (true)
		{
			if (i >= s.length())
			{
				break;
			}
			else if (!predicate.f(s.charAt(i)))
			{
				break;
			}
			
			i++;
		}
		
		return i;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static List<Object> parseLenientSemanticIndentationToNewCompletely(String lines) throws TextSyntaxCheckedException
	{
		return parseLenientSemanticIndentationToNew(asList(splitlines(lines)));
	}
	
	public static List<Object> parseLenientSemanticIndentationToNew(List<String> lines) throws TextSyntaxCheckedException
	{
		List<Object> blocksAndLines = new ArrayList<>();
		
		PLSIrv r = parseLenientSemanticIndentation(lines, blocksAndLines);
		
		if (r.lineIndexWeStoppedAt < lines.size())
		{
			if (!lines.get(r.lineIndexWeStoppedAt).startsWith(r.indentation))
				throw TextSyntaxCheckedException.inst("Illegal de-indent!");
			else
				throw new AssertionError();
		}
		else if (r.lineIndexWeStoppedAt < lines.size())
		{
			throw new AssertionError();
		}
		
		return blocksAndLines;
	}
	
	
	public static class PLSIrv { public int lineIndexWeStoppedAt; public String indentation; }
	
	public static PLSIrv parseLenientSemanticIndentation(List<String> lines, List<Object> output) throws TextSyntaxCheckedException
	{
		if (lines.isEmpty())
		{
			PLSIrv rv = new PLSIrv();
			rv.lineIndexWeStoppedAt = 0;
			rv.indentation = "";
			return rv;
		}
		else
		{
			String indentation;
			{
				String l0 = lines.get(0);
				int i = indexOf(l0, c -> !Character.isWhitespace(c));
				int end = i == -1 ? l0.length() : i;
				indentation = l0.substring(0, end);
			}
			
			
			int i = 0;
			
			String lastIndentation = null;
			
			while (i < lines.size())
			{
				String bareline = lines.get(i);
				
				if (!bareline.startsWith(indentation))
				{
					PLSIrv rv = new PLSIrv();
					rv.lineIndexWeStoppedAt = i;
					rv.indentation = indentation;
					return rv;
				}
				else
				{
					String line = bareline.substring(indentation.length());
					
					if (line.isEmpty() || !Character.isWhitespace(line.charAt(0)))
					{
						output.add(line);
						i++;
					}
					else
					{
						
						List<Object> o = new ArrayList<>();
						
						PLSIrv r = parseLenientSemanticIndentation(lines.subList(i, lines.size()), o);
						
						if (lastIndentation != null && !eq(lastIndentation, r.indentation))
							throw TextSyntaxCheckedException.inst("Illegal de-indent!");
						else
							assert output.isEmpty() || output.get(output.size()-1) instanceof String;
						
						i = i+r.lineIndexWeStoppedAt;
						lastIndentation = r.indentation;
						
						output.add(o);
					}
				}
			}
			
			PLSIrv rv = new PLSIrv();
			rv.lineIndexWeStoppedAt = lines.size();
			rv.indentation = indentation;
			return rv;
		}
	}
	
	
	
	
	
	
	
	public static String reconstituteSemanticIndentationBlocks(List<Object> blocksAndLines, String baseIndentation, String indent)
	{
		return joinlines(reconstituteSemanticIndentationBlocksToLines(blocksAndLines, baseIndentation, indent));
	}
	
	public static List<String> reconstituteSemanticIndentationBlocksToLines(List<Object> blocksAndLines, String baseIndentation, String indent)
	{
		List<String> lines = new ArrayList<>();
		reconstituteSemanticIndentationBlocksToLines(blocksAndLines, baseIndentation, indent, lines);
		return lines;
	}
	
	public static void reconstituteSemanticIndentationBlocksToLines(List<Object> blocksAndLines, String baseIndentation, String indent, @WritableValue List<String> output)
	{
		for (Object l : blocksAndLines)
		{
			if (l instanceof String)
				output.add(baseIndentation + l);
			else
				reconstituteSemanticIndentationBlocksToLines((List<Object>)l, baseIndentation+indent, indent, output);
		}
	}
	
	
	
	
	
	
	
	
	public static String getIndentation(String line)
	{
		int i = findFirstIndex(c -> !Character.isWhitespace(c), line);
		return line.substring(0, i);
	}
	
	public static String getIndentationFromFirstLine(String s)
	{
		int i = findFirstIndex(c -> !Character.isWhitespace(c) || c == '\n' || c == '\r', s);
		return s.substring(0, i);
	}
	
	
	
	public static String removeCommonLeadingIndentationAndSurroundingBlankLines(String s)
	{
		s = rtrim(s);
		
		StringBuilder b = new StringBuilder();
		
		String indent = null;
		
		for (String line : splitlines(s))
		{
			if (indent == null)
			{
				if (!line.trim().isEmpty())
				{
					int i = findFirstIndex(c -> !Character.isWhitespace(c), line);
					
					if (i == -1)
						i = 0;
					
					indent = line.substring(0, i);
					
					b.append(line.substring(i));
					b.append('\n');
				}
				//else, trim the line :>
			}
			else
			{
				//Since indent != null, we've already passed the first line, so leave this in even if it's empty / all-whitespace :3
				b.append(ltrimstr(line, indent));
				b.append('\n');
			}
		}
		
		return b.toString();
	}
}
