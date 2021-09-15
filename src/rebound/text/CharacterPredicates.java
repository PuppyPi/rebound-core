package rebound.text;

import static java.lang.Character.*;
import static rebound.text.StringUtilities.*;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import rebound.text.StringUtilities.TransparentArraySharingCharSequence;
import rebound.util.collections.ArrayUtilities;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionCharToBoolean;

public class CharacterPredicates
{
	//<Patterns and such
	/**
	 * Very naive -,- XD
	 * 
	 * @author Puppy Pie ^_^
	 */
	@FunctionalInterface
	public static interface NaiveCharacterSequencePattern
	{
		/**
		 * @param length may definitely be -1 indicating no restriction on the length to consider (except src.length of course xD)
		 * @return the length of the given string that matches, or -1 if no match :>
		 */
		public int matches(char[] src, int offset, int length);
	}
	
	
	
	
	public static final UnaryFunctionCharToBoolean DEFINED16_PATTERN = c -> Character.isDefined(c);
	
	public static final UnaryFunctionCharToBoolean SURROGATE_PATTERN = c -> Character.isSurrogate(c);
	
	public static final UnaryFunctionCharToBoolean NOT_SURROGATE_PATTERN = c -> !Character.isSurrogate(c);
	
	public static final UnaryFunctionCharToBoolean GENERALLY_PRINTABLE_CHARS = c ->
	c < 32 ? (c == '\t' || c == '\r' || c == '\n') :
		Character.isWhitespace(c) ||
		(
		Character.isDefined(c) &&
		!Character.isSurrogate(c) &&
		!Character.isISOControl(c)
		);
	
	public static final UnaryFunctionCharToBoolean GENERALLY_PRINTABLE_NONNEWLINE_CHARS = c -> c != '\n' && c != '\r' && GENERALLY_PRINTABLE_CHARS.f(c);
	
	
	
	
	public static final UnaryFunctionCharToBoolean BREAKING_WHITESPACE_PATTERN = c -> Character.isWhitespace(c);
	public static final UnaryFunctionCharToBoolean ALL_WHITESPACE_PATTERN = c -> Character.isWhitespace(c) || c == '\u00A0';  //Todo any other non-"whitespace" whitespaces? XD'
	
	public static final UnaryFunctionCharToBoolean NONNEWLINE_WHITESPACE_PATTERN = c -> Character.getType(c) != Character.LINE_SEPARATOR && Character.isWhitespace(c);
	
	public static final UnaryFunctionCharToBoolean SPACE_PATTERN = c -> Character.isSpaceChar(c);
	
	
	
	
	public static final UnaryFunctionCharToBoolean DIGIT_PATTERN = c -> Character.isDigit(c);
	
	public static final UnaryFunctionCharToBoolean ASCII_DECIMAL_DIGIT_PATTERN = c -> c >= '0' && c <= '9';
	public static final UnaryFunctionCharToBoolean ASCII_DECIMAL_DIGIT_AND_NUMERIC_PUNCTUATION_PATTERN = c -> (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+';
	public static final UnaryFunctionCharToBoolean ASCII_DECIMAL_DIGIT_AND_NUMERIC_PUNCTUATION_PATTERN_AND_BASIC_FLOATING_POINT_SCIENTIFIC_NOTATION_SYNTAX = c -> (c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+' || c == 'e' || c == 'E';
	
	public static final UnaryFunctionCharToBoolean ASCII_HEXADECIMAL_DIGIT_PATTERN = c -> (c >= '0' && c <= '9') || (c >= 'A' && c <= 'F') || (c >= 'a' && c <= 'f');
	
	public static final UnaryFunctionCharToBoolean ALPHABETIC_PATTERN = c -> Character.isAlphabetic(c);
	
	public static final UnaryFunctionCharToBoolean ISOCONTROL_PATTERN = c -> Character.isISOControl(c);
	
	public static final UnaryFunctionCharToBoolean IDEOGRAPHIC_PATTERN = c -> Character.isIdeographic(c);
	
	public static final UnaryFunctionCharToBoolean LETTER_PATTERN = c -> Character.isLetter(c);
	
	public static final UnaryFunctionCharToBoolean LOWERCASE_PATTERN = c -> Character.isLowerCase(c);
	
	public static final UnaryFunctionCharToBoolean UPPERCASE_PATTERN = c -> Character.isUpperCase(c);
	
	public static final UnaryFunctionCharToBoolean TITLECASE_PATTERN = c -> Character.isTitleCase(c);
	
	public static final UnaryFunctionCharToBoolean COMBINING_CHARACTER = c ->
	{
		int t = Character.getType(c);
		return t == COMBINING_SPACING_MARK || t == ENCLOSING_MARK|| t == NON_SPACING_MARK;
	};
	
	/**
	 * The characters you'd prefer not to make a word-wrap break on, but might have to.
	 * + Includes nbsp!
	 */
	public static final UnaryFunctionCharToBoolean WORD_WRAP_BREAK_CHARACTERS_OTHER_THAN_WHITESPACE = c -> !Character.isLetter(c) && !Character.isIdeographic(c) && !COMBINING_CHARACTER.f(c) && !Character.isSurrogate(c);
	
	
	
	public static final UnaryFunctionCharToBoolean DEFINED16_PATTERN_INVERSE = new InverseCharacterPattern(DEFINED16_PATTERN);
	public static final UnaryFunctionCharToBoolean SURROGATE_PATTERN_INVERSE = new InverseCharacterPattern(SURROGATE_PATTERN);
	public static final UnaryFunctionCharToBoolean NOT_SURROGATE_PATTERN_INVERSE = new InverseCharacterPattern(NOT_SURROGATE_PATTERN);
	public static final UnaryFunctionCharToBoolean GENERALLY_PRINTABLE_CHARS_INVERSE = new InverseCharacterPattern(GENERALLY_PRINTABLE_CHARS);
	public static final UnaryFunctionCharToBoolean GENERALLY_PRINTABLE_NONNEWLINE_CHARS_INVERSE = new InverseCharacterPattern(GENERALLY_PRINTABLE_NONNEWLINE_CHARS);
	public static final UnaryFunctionCharToBoolean BREAKING_WHITESPACE_PATTERN_INVERSE = new InverseCharacterPattern(BREAKING_WHITESPACE_PATTERN);
	public static final UnaryFunctionCharToBoolean ALL_WHITESPACE_PATTERN_INVERSE = new InverseCharacterPattern(ALL_WHITESPACE_PATTERN);
	public static final UnaryFunctionCharToBoolean NONNEWLINE_WHITESPACE_PATTERN_INVERSE = new InverseCharacterPattern(NONNEWLINE_WHITESPACE_PATTERN);
	public static final UnaryFunctionCharToBoolean SPACE_PATTERN_INVERSE = new InverseCharacterPattern(SPACE_PATTERN);
	public static final UnaryFunctionCharToBoolean DIGIT_PATTERN_INVERSE = new InverseCharacterPattern(DIGIT_PATTERN);
	public static final UnaryFunctionCharToBoolean ASCII_DECIMAL_DIGIT_PATTERN_INVERSE = new InverseCharacterPattern(ASCII_DECIMAL_DIGIT_PATTERN);
	public static final UnaryFunctionCharToBoolean ASCII_DECIMAL_DIGIT_AND_NUMERIC_PUNCTUATION_PATTERN_INVERSE = new InverseCharacterPattern(ASCII_DECIMAL_DIGIT_AND_NUMERIC_PUNCTUATION_PATTERN);
	public static final UnaryFunctionCharToBoolean ASCII_DECIMAL_DIGIT_AND_NUMERIC_PUNCTUATION_PATTERN_AND_BASIC_FLOATING_POINT_SCIENTIFIC_NOTATION_SYNTAX_INVERSE = new InverseCharacterPattern(ASCII_DECIMAL_DIGIT_AND_NUMERIC_PUNCTUATION_PATTERN_AND_BASIC_FLOATING_POINT_SCIENTIFIC_NOTATION_SYNTAX);
	public static final UnaryFunctionCharToBoolean ASCII_HEXADECIMAL_DIGIT_PATTERN_INVERSE = new InverseCharacterPattern(ASCII_HEXADECIMAL_DIGIT_PATTERN);
	public static final UnaryFunctionCharToBoolean ALPHABETIC_PATTERN_INVERSE = new InverseCharacterPattern(ALPHABETIC_PATTERN);
	public static final UnaryFunctionCharToBoolean ISOCONTROL_PATTERN_INVERSE = new InverseCharacterPattern(ISOCONTROL_PATTERN);
	public static final UnaryFunctionCharToBoolean IDEOGRAPHIC_PATTERN_INVERSE = new InverseCharacterPattern(IDEOGRAPHIC_PATTERN);
	public static final UnaryFunctionCharToBoolean LETTER_PATTERN_INVERSE = new InverseCharacterPattern(LETTER_PATTERN);
	public static final UnaryFunctionCharToBoolean LOWERCASE_PATTERN_INVERSE = new InverseCharacterPattern(LOWERCASE_PATTERN);
	public static final UnaryFunctionCharToBoolean UPPERCASE_PATTERN_INVERSE = new InverseCharacterPattern(UPPERCASE_PATTERN);
	public static final UnaryFunctionCharToBoolean TITLECASE_PATTERN_INVERSE = new InverseCharacterPattern(TITLECASE_PATTERN);
	
	
	
	public static class CaseInsensitiveCharacterPattern
	implements UnaryFunctionCharToBoolean
	{
		protected UnaryFunctionCharToBoolean pattern;
		
		public CaseInsensitiveCharacterPattern()
		{
			super();
		}
		
		public CaseInsensitiveCharacterPattern(UnaryFunctionCharToBoolean pattern)
		{
			super();
			this.pattern = pattern;
		}
		
		public UnaryFunctionCharToBoolean getCharacterPattern()
		{
			return this.pattern;
		}
		
		public void setCharacterPattern(UnaryFunctionCharToBoolean pattern)
		{
			this.pattern = pattern;
		}
		
		
		@Override
		public boolean f(char c)
		{
			return this.pattern.f(Character.toLowerCase(c)) || this.pattern.f(Character.toUpperCase(c));
		}
	}
	
	public static class InverseCharacterPattern
	implements UnaryFunctionCharToBoolean
	{
		protected UnaryFunctionCharToBoolean pattern;
		
		public InverseCharacterPattern()
		{
			super();
		}
		
		public InverseCharacterPattern(UnaryFunctionCharToBoolean pattern)
		{
			super();
			setPattern(pattern);
		}
		
		
		@Override
		public boolean f(char c)
		{
			return !this.pattern.f(c);
		}
		
		public UnaryFunctionCharToBoolean getPattern()
		{
			return this.pattern;
		}
		
		public void setPattern(UnaryFunctionCharToBoolean pattern)
		{
			this.pattern = pattern;
		}
	}
	
	
	
	
	
	
	public static NaiveCharacterSequencePattern newStaticNaiveCharacterSequencePattern(Object textthing)
	{
		final char[] target = textthingToPossiblyUnclonedCharArray(textthing);
		
		return (src, offset, length) ->
		{
			if (offset < 0)
				throw new IndexOutOfBoundsException();
			
			if (length == -1)
				length = src.length - offset;
			
			if (length != target.length)
				return -1;
			
			if (ArrayUtilities.arrayMatches(src, offset, target, 0, target.length))
				return target.length;
			else
				return -1;
		};
	}
	
	public static NaiveCharacterSequencePattern newCaseInsensitiveStaticNaiveCharacterSequencePattern(Object textthing)
	{
		final char[] target = textthingToPossiblyUnclonedCharArray(textthing);
		
		return (src, offset, length) ->
		{
			if (offset < 0)
				throw new IndexOutOfBoundsException();
			
			if (length == -1)
				length = src.length - offset;
			
			if (length != target.length)
				return -1;
			
			if (arrayMatchesCaseInsensitive(src, offset, target, 0, target.length))
				return target.length;
			else
				return -1;
		};
	}
	
	/**
	 * @param patternthing can be either a pre-compiled {@link Pattern}, or a textthing :>
	 */
	public static NaiveCharacterSequencePattern newRegexNaiveCharacterSequencePattern(Object patternthing, int regexFlags)
	{
		final Pattern pattern = patternthing instanceof Pattern ? (Pattern)patternthing : Pattern.compile(textthingToString(patternthing));
		
		return (src, offset, length) ->
		{
			if (offset < 0)
				throw new IndexOutOfBoundsException();
			
			if (length == -1)
				length = src.length - offset;
			
			Matcher matcher = pattern.matcher(new TransparentArraySharingCharSequence(src, offset, length));
			
			if (!matcher.lookingAt())
				return -1;
			
			//since the provided CharSequence was already started at 'offset', matcher.start() will == 0   ^_^
			return matcher.end();// - matcher.start();
		};
	}
	
	public static NaiveCharacterSequencePattern newRegexNaiveCharacterSequencePattern(Object compiledPatternOrPatternStringToCompile)
	{
		return newRegexNaiveCharacterSequencePattern(compiledPatternOrPatternStringToCompile, 0);
	}
	
	
	public static NaiveCharacterSequencePattern newLengthClobberingNaiveCharacterSequencePattern(final NaiveCharacterSequencePattern underlying, final int valueToReturnOnSuccess)
	{
		return (src, offset, length) -> underlying.matches(src, offset, length) != -1 ? valueToReturnOnSuccess : -1;
	}
	
	public static NaiveCharacterSequencePattern newLengthClobberingNaiveCharacterSequencePattern(final NaiveCharacterSequencePattern underlying)
	{
		return newLengthClobberingNaiveCharacterSequencePattern(underlying, 0);
	}
	
	
	
	public static boolean regexMatchesAtStartButNotNecessarilyEnd(Object patternthing, CharSequence text)
	{
		Pattern p = patternthing instanceof Pattern ? (Pattern)patternthing : Pattern.compile(textthingToString(patternthing));
		return p.matcher(text).lookingAt();
	}
	
	
	
	/**
	 * Tests if a character is a digit in a given radix.<br>
	 */
	public static boolean isDigit(char c, int radix)
	{
		return Character.digit(c, radix) != -1;
	}
	
	
	
	
	public static boolean isAsciiLowercaseLetter(char c)
	{
		return c >= 'a' && c <= 'z';
	}
	
	public static boolean isAsciiUppercaseLetter(char c)
	{
		return c >= 'A' && c <= 'Z';
	}
	
	public static boolean isAsciiDigit(char c)
	{
		return c >= '0' && c <= '9';
	}
	
	public static boolean isAsciiLetter(char c)
	{
		return isAsciiLowercaseLetter(c) || isAsciiUppercaseLetter(c);
	}
	
	public static boolean isAsciiAlphanumeric(char c)
	{
		return isAsciiLetter(c) || isAsciiDigit(c);
	}
	
	
	
	
	
	public static final Predicate StringPattern_IsEmpty = input -> getLength(input) == 0;
	
	public static final Predicate StringPattern_IsNotEmpty = input -> getLength(input) != 0;
	//Patterns and such>
}
