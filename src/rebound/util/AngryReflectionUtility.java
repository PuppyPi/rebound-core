/*
 * Created on Jun 5, 2007
 * 	by the great Eclipse(c)
 */
package rebound.util;

import static java.util.Objects.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import java.lang.annotation.Annotation;
import java.lang.reflect.AccessibleObject;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.semantic.reachability.ThrowAwayValue;
import rebound.exceptions.ClassNotFoundRuntimeException;
import rebound.exceptions.ImPrettySureThisNeverActuallyHappensRuntimeException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NameConflictException;
import rebound.exceptions.NotFoundException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.NullEnumValueIllegalArgumentException;
import rebound.exceptions.TooManyException;
import rebound.exceptions.UnexpectedHardcodedEnumValueException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.text.StringUtilities;
import rebound.util.collections.ArrayUtilities;
import rebound.util.objectutil.BasicObjectUtilities;
import rebound.util.objectutil.JavaNamespace;

//[Some of] these methods are angry because they are not in java.lang.Class  (XD)
public class AngryReflectionUtility
implements JavaNamespace
{
	public static Class arrayClassOf(Class componentType)
	{
		return Array.newInstance(componentType, 0).getClass();
	}
	
	
	/**
	 * A wrapper around {@link Class#forName(String)} which returns <code>null</code> rather than throw a {@link ClassNotFoundException},
	 * and also observes the primitives' names.  :>
	 */
	@Nullable
	public static Class forName(String name)
	{
		try
		{
			return Class.forName(name);
		}
		catch (ClassNotFoundException exc)
		{
			return forPrimitiveName(name);
		}
	}
	
	public static Class forPrimitiveName(String name)
	{
		if (name.equals("int")) return int.class;
		else if (name.equals("double")) return double.class;
		else if (name.equals("long")) return long.class;
		else if (name.equals("boolean")) return boolean.class;
		else if (name.equals("byte")) return byte.class;
		else if (name.equals("char")) return char.class;
		else if (name.equals("float")) return float.class;
		else if (name.equals("short")) return short.class;
		else if (name.equals("void")) return void.class;
		else return null;
	}
	
	public static boolean isPrimitiveName(String name)
	{
		return forPrimitiveName(name) != null;
	}
	
	public static boolean isArrayName(String name)
	{
		return name.indexOf('[') != -1;
	}
	
	
	public static boolean isInstanceNormalizingPrimitivesAndWrappers(Class c, Object instanceCandidate)
	{
		if (c.isPrimitive())
			c = Primitives.getWrapperClassFromPrimitiveStrict(c);
		
		return c.isInstance(instanceCandidate);
	}
	
	
	
	/**
	 * Like {@link #forName(String)}, but throws a <i>Runtime</i> exception if not found.  ^_^
	 */
	public static Class forNameMandatory(String name) throws ClassNotFoundRuntimeException
	{
		Class c = forName(name);
		if (c == null)
			throw new ClassNotFoundRuntimeException(name);
		return c;
	}
	
	
	/**
	 *   /just personally understands this name better than 'isAssignableFrom' :>
	 */
	public static boolean isSuperclass(Class candidateSuperclass, Class candidateSubclass)
	{
		return candidateSuperclass.isAssignableFrom(candidateSubclass);
	}
	
	/**
	 * a la Python ;3
	 */
	public static boolean isSubclass(Class candidateSubclass, Class candidateSuperclass)
	{
		return candidateSuperclass.isAssignableFrom(candidateSubclass);
	}
	
	
	
	public static boolean isParameterAnnotationPresent(Method m, int parameterIndex, Class<? extends Annotation> annotationClass)
	{
		if (parameterIndex < 0) throw new IndexOutOfBoundsException("negative index! D:");
		
		Annotation[][] parameterAnnotations = m.getParameterAnnotations();
		
		if (parameterIndex >= parameterAnnotations.length) throw new IndexOutOfBoundsException("["+parameterIndex+"] >= "+parameterAnnotations.length);
		
		for (Annotation a : parameterAnnotations[parameterIndex])
			if (annotationClass.isInstance(a))
				return true;
		
		return false;
	}
	
	public static boolean isParameterAnnotationPresent(Constructor m, int parameterIndex, Class<? extends Annotation> annotationClass)
	{
		if (parameterIndex < 0) throw new IndexOutOfBoundsException("negative index! D:");
		
		Annotation[][] parameterAnnotations = m.getParameterAnnotations();
		
		if (parameterIndex >= parameterAnnotations.length) throw new IndexOutOfBoundsException("["+parameterIndex+"] >= "+parameterAnnotations.length);
		
		for (Annotation a : parameterAnnotations[parameterIndex])
			if (annotationClass.isInstance(a))
				return true;
		
		return false;
	}
	
	
	/**
	 * + Order will be in subclass first, then superclass; it will always end in Object.class :>   (unless iff the given class is <code>null</code>, primitive, void, or an interface :> )
	 * Note: won't contain interfaces, unless the given class is an interface class (then will be {c, Object.class} xD)
	 */
	public static Class[] getSuperclassesOfIncludingSelf(Class c)
	{
		if (c == null)
			return new Class[0];
		
		List<Class> supers = new ArrayList<>();
		while (c != null)
		{
			supers.add(c);
			c = c.getSuperclass();
		}
		
		return supers.toArray(new Class[supers.size()]);
	}
	
	/**
	 * + Order will be in subclass first, then superclass; it will always end in Object.class, unless iff the given class IS Object.class! XD     (or <code>null</code>, primitive, void, or an interface :> )
	 * Note: will never contain interfaces, even if the given class is an interface class! XD
	 */
	public static Class[] getSuperclassesOfNotIncludingSelf(Class c)
	{
		if (c == null)
			return new Class[0];
		
		c = c.getSuperclass();
		
		if (c == null)
			return new Class[0];
		
		
		List<Class> supers = new ArrayList<>();
		while (c != null)
		{
			supers.add(c);
			c = c.getSuperclass();
		}
		
		return supers.toArray(new Class[supers.size()]);
	}
	
	
	
	/**
	 *  Get all interfaces implemented/extended by this class/interface,
	 *  *and by all superclasses*
	 *  *and by all implemented/extended interfaces*!!
	 */
	@ThrowAwayValue
	public static Class[] getAllInterfacesArray(Class c)
	{
		if (c == null || c.isPrimitive() || c == void.class)
			return new Class[0];
		
		Set<Class> set = getAllInterfaces(c);
		return set.toArray(new Class[set.size()]);
	}
	
	@ThrowAwayValue
	public static Set<Class> getAllInterfaces(Class c)
	{
		if (c == null || c.isPrimitive() || c == void.class)
			return Collections.emptySet();
		
		Set<Class> rv = new HashSet<Class>();
		getAllInterfaces(c, rv);
		return rv;
	}
	
	
	public static void getAllInterfaces(Class c, Set<Class> interfacesOUT)
	{
		if (c == null || c.isPrimitive() || c == void.class)
			return;
		
		getAllInterfaces(c.getSuperclass(), interfacesOUT);
		
		for (Class i : c.getInterfaces())
		{
			if (!interfacesOUT.contains(i))
			{
				interfacesOUT.add(i);
				getAllInterfaces(i, interfacesOUT);
			}
		}
	}
	
	
	/*
	public static boolean areSignaturesTheSame(Member a, Member b)
	{
		if (a == b) return true;
		if (a == null || b == null) return false;
		
		if (!a.getName().equals(b.getName()))
			return false;
		
		if (a instanceof Field)
		{
			if (!(b instanceof Field))
				return false;
			
			return ((Field)a).getType() == ((Field)b).getType();
		}
		else if (a instanceof Method)
		{
			if (!(b instanceof Method))
				return false;
			
			return ((Method)a).getReturnType() == ((Method)b).getReturnType() && Arrays.equals(((Method)a).getParameterTypes(), ((Method)b).getParameterTypes());
		}
		else if (a instanceof Constructor)
		{
			if (!(b instanceof Constructor))
				return false;
			
			return Arrays.equals(((Constructor)a).getParameterTypes(), ((Constructor)b).getParameterTypes());
		}
		
		else
			throw ExceptionUtilities.newClassCastExceptionOrNullPointerException(a);
	}
	 */
	
	
	
	
	
	
	
	public static enum JavaVisibility
	{
		PUBLIC ("public"),
		PROTECTED ("protected"),
		PACKAGE_PRIVATE (""),
		PRIVATE ("private"),
		;
		
		protected String codeManifestation;
		
		private JavaVisibility(String codeManifestation)
		{
			this.codeManifestation = codeManifestation;
		}
		
		/**
		 * "public" for {@link #PUBLIC},
		 * "protected" for {@link #PROTECTED},
		 * "" for {@link #PACKAGE_PRIVATE}
		 * "private" for {@link #PRIVATE}
		 * 
		 * ^_^
		 */
		public String getManifestationInCode()
		{
			return this.codeManifestation;
		}
		
		/**
		 * "public" for {@link #PUBLIC},
		 * "protected" for {@link #PROTECTED},
		 * "<package private>" for {@link #PACKAGE_PRIVATE}
		 * "private" for {@link #PRIVATE}
		 * 
		 * ^_^
		 */
		@Override
		public String toString()
		{
			return this == PACKAGE_PRIVATE ? "<package private>" : getManifestationInCode();
		}
		
		
		
		
		public JavaVisibility fromString(String str)
		{
			str = str.trim();
			
			if (BasicObjectUtilities.eq(str, "public"))
				return PUBLIC;
			else if (BasicObjectUtilities.eq(str, "protected"))
				return PROTECTED;
			else if (BasicObjectUtilities.eq(str, "private"))
				return PRIVATE;
			else if (str == null || str.isEmpty() || BasicObjectUtilities.eq(str, "package") || BasicObjectUtilities.eq(str, "<package private>"))
				return PACKAGE_PRIVATE;
			else
				throw new IllegalArgumentException("Invalid Java Visibility: "+str);
		}
	}
	
	
	
	
	
	/**
	 * "Overridability" is a good word for methods,
	 * "Subclassability" is a good word for classes/types.
	 * @author RProgrammer
	 */
	public static enum JavaOverrideOrSubclassAbility
	{
		/**
		 * override/subclass required
		 */
		ABSTRACT,
		
		/**
		 * override/subclass optional
		 */
		DEFAULT,
		
		/**
		 * override/subclass illegal
		 * 
		 * (VERY different from field/variable/parameter "final", which means simply "readonly" X3 )
		 */
		FINAL,
	}
	
	
	public static enum JavaModifierParametersForClasses
	{
		VISBILITY,
		SUBCLASSABILITY,
		STATIC,
		
		STRICTFP,
	}
	
	public static enum JavaModifierParametersForMethods
	{
		VISBILITY,
		SUBCLASSABILITY,
		STATIC,
		
		NATIVE,
		STRICTFP,
		SYNCHRONIZED, //note that this one's just syntactic sugar ;>
	}
	
	public static enum JavaModifierParametersForFields
	{
		VISBILITY,
		STATIC,
		
		/**
		 * meaning 'constant'; a different meaning than 'final' for methods and classes (having to do with inheritance)
		 */
		FINAL,
		TRANSIENT,
		VOLATILE,
	}
	
	
	
	public static final int VISIBILITY_MODIFIER_MASK = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.PRIVATE;
	
	public static JavaVisibility getVisibility(int modifiers)
	{
		if (Integer.bitCount(modifiers & VISIBILITY_MODIFIER_MASK) > 1)
			throw new IllegalArgumentException("Invalid Java modifiers bitfield: "+modifiers);
		
		if (Modifier.isPublic(modifiers))
			return JavaVisibility.PUBLIC;
		else if (Modifier.isProtected(modifiers))
			return JavaVisibility.PROTECTED;
		else if (Modifier.isPrivate(modifiers))
			return JavaVisibility.PRIVATE;
		else
			return JavaVisibility.PACKAGE_PRIVATE;
	}
	
	public static JavaVisibility getVisibility(Class thing)
	{
		return getVisibility(thing.getModifiers());
	}
	
	public static JavaVisibility getVisibility(Method thing)
	{
		return getVisibility(thing.getModifiers());
	}
	
	public static JavaVisibility getVisibility(Constructor thing)
	{
		return getVisibility(thing.getModifiers());
	}
	
	public static JavaVisibility getVisibility(Field thing)
	{
		return getVisibility(thing.getModifiers());
	}
	
	public static JavaVisibility getVisibility(Object thing)
	{
		if (thing instanceof Class)
			return getVisibility((Class)thing);
		else if (thing instanceof Method)
			return getVisibility((Method)thing);
		else if (thing instanceof Field)
			return getVisibility((Field)thing);
		else if (thing instanceof Constructor)
			return getVisibility((Constructor)thing);
		else
			throw new IllegalArgumentException(thing != null ? new ClassCastException(thing.getClass().toString()) : new NullPointerException());
	}
	
	
	
	
	public static boolean getBooleanModifierValue(Class c, JavaModifierParametersForClasses modifier)
	{
		if (modifier == JavaModifierParametersForClasses.STATIC)
			return Modifier.isStatic(c.getModifiers());
		else if (modifier == JavaModifierParametersForClasses.STRICTFP)
			return Modifier.isStrict(c.getModifiers());
		
		else if (modifier == JavaModifierParametersForClasses.VISBILITY)
			throw new IllegalArgumentException("not a boolean property ._.");
		else if (modifier == JavaModifierParametersForClasses.SUBCLASSABILITY)
			throw new IllegalArgumentException("not a boolean property ._.");
		
		else if (modifier == null)
			throw new NullEnumValueIllegalArgumentException();
		else
			throw new UnexpectedHardcodedEnumValueException();
	}
	
	public static boolean getBooleanModifierValue(Method m, JavaModifierParametersForMethods modifier)
	{
		if (modifier == JavaModifierParametersForMethods.STATIC)
			return Modifier.isStatic(m.getModifiers());
		else if (modifier == JavaModifierParametersForMethods.NATIVE)
			return Modifier.isNative(m.getModifiers());
		else if (modifier == JavaModifierParametersForMethods.STRICTFP)
			return Modifier.isStrict(m.getModifiers());
		else if (modifier == JavaModifierParametersForMethods.SYNCHRONIZED)
			return Modifier.isSynchronized(m.getModifiers());
		
		else if (modifier == JavaModifierParametersForMethods.VISBILITY)
			throw new IllegalArgumentException("not a boolean property ._.");
		else if (modifier == JavaModifierParametersForMethods.SUBCLASSABILITY)
			throw new IllegalArgumentException("not a boolean property ._.");
		
		else if (modifier == null)
			throw new NullEnumValueIllegalArgumentException();
		else
			throw new UnexpectedHardcodedEnumValueException();
	}
	
	public static boolean getBooleanModifierValue(Field f, JavaModifierParametersForFields modifier)
	{
		if (modifier == JavaModifierParametersForFields.STATIC)
			return Modifier.isStatic(f.getModifiers());
		else if (modifier == JavaModifierParametersForFields.FINAL)
			return Modifier.isFinal(f.getModifiers());
		else if (modifier == JavaModifierParametersForFields.TRANSIENT)
			return Modifier.isTransient(f.getModifiers());
		else if (modifier == JavaModifierParametersForFields.VOLATILE)
			return Modifier.isVolatile(f.getModifiers());
		
		else if (modifier == JavaModifierParametersForFields.VISBILITY)
			throw new IllegalArgumentException("not a boolean property ._.");
		
		else if (modifier == null)
			throw new NullEnumValueIllegalArgumentException();
		else
			throw new UnexpectedHardcodedEnumValueException();
	}
	
	
	
	/**
	 * For Methods and Fields this tells if it's static or instance :3
	 * 
	 * For Classes this tells if it's just purely a "member class" organizationally (static member class), or a true inner class that actually maintains a special hidden context field to an instance of the enclosing class type! (nonstatic member class) \o/
	 * 
	 * For Constructors this always returns true, as all constructors are essentially special static methods with the name "<init>" :3
	 */
	public static boolean isStatic(Object classOrMethodOrField)
	{
		if (classOrMethodOrField instanceof Method)
			return Modifier.isStatic(((Method)classOrMethodOrField).getModifiers());
		else if (classOrMethodOrField instanceof Field)
			return Modifier.isStatic(((Field)classOrMethodOrField).getModifiers());
		
		else if (classOrMethodOrField instanceof Constructor)
			return true;
		
		else if (classOrMethodOrField instanceof Class)
			return Modifier.isStatic(((Class)classOrMethodOrField).getModifiers());
		
		else
			throw new IllegalArgumentException(classOrMethodOrField != null ? new ClassCastException(classOrMethodOrField.getClass().toString()) : new NullPointerException());
	}
	
	
	//Todo the ones for subclassability ._.
	
	
	
	
	
	
	
	//Todo method for getting all the Methods of a name; and method for getting the first; and method for getting the only one and throwing an error if there is not only one :>
	
	
	/**
	 * Finds the specified method in the given class or returns <code>null</code> if one could not be found matching the criteria.
	 * + Note: argumentTypes is not optional, since that could identify more than one method
	 * @param optionalVisibilityRequirement a criterion that the visbility of the member be this; a value of <code>null</code> disables this criterion
	 * @param optionalReturnTypeRequirement a criterion that the return type of the member be this; a value of <code>null</code> disables this criterion  (use {@link Void}.class to mandate a return type of void ;> )
	 * @param optionalModifierStaticRequirement a criterion that the method be static or instance; a value of <code>null</code> disables this criterion  (use False to mandate non-static ;> )
	 * @param scanSuperclasses if <code>false</code>, only scan for methods declared in this class
	 */
	@Nullable
	public static Method getMethod(Class c, String methodName, Class[] argumentTypes, JavaVisibility optionalVisibilityRequirement, Class optionalReturnTypeRequirement, Boolean optionalModifierStaticRequirement, boolean scanSuperclasses)
	{
		if (c == null || methodName == null || argumentTypes == null)
			throw new NullPointerException();
		
		Class cls = c;
		while (cls != null)
		{
			Method[] members = cls.getDeclaredMethods();
			
			for (Method member : members)
			{
				//Check if it satisfies the criteria
				if (member.getName().equals(methodName))
				{
					if (Arrays.equals(member.getParameterTypes(), argumentTypes))
					{
						if (optionalReturnTypeRequirement == null || member.getReturnType() == optionalReturnTypeRequirement)
						{
							if (optionalVisibilityRequirement == null || getVisibility(member) == optionalVisibilityRequirement)
							{
								if (optionalVisibilityRequirement == null || Modifier.isStatic(member.getModifiers()) == optionalModifierStaticRequirement)
								{
									//We have a match! :D
									return member;
								}
							}
						}
					}
				}
			}
			
			if (!scanSuperclasses)
				break;
			
			cls = cls.getSuperclass();
		}
		
		return null;
	}
	
	public static Method getMethod(Class c, String methodName, Class[] argumentTypes, boolean scanSuperclasses)
	{
		return getMethod(c, methodName, argumentTypes, null, null, null, scanSuperclasses);
	}
	
	
	public static Method getMethod(Class c, String methodName, Class[] argumentTypes)
	{
		return getMethod(c, methodName, argumentTypes, true);
	}
	
	
	
	
	
	
	
	@Nonnull
	public static Method getMethodByNameCheckingExactlyOneNotCountingInherited(Class c, String methodName) throws TooManyException, NotFoundException
	{
		return getMethodByNameCheckingExactlyOneNotCountingInherited(c, methodName, null);
	}
	
	
	/**
	 * Since there technically could be more than one with the same name since JVM/Java allows overloading! (distinguishing them by different parameter types)
	 */
	@Nonnull
	public static Method getMethodByNameCheckingExactlyOneNotCountingInherited(Class c, String methodName, @Nullable Boolean optionalStaticRequirement) throws TooManyException, NotFoundException
	{
		Method ourMethod = null;
		boolean has = false;
		
		for (Method m : c.getDeclaredMethods())
		{
			if (m.getName().equals(methodName))
			{
				if (optionalStaticRequirement == null || Modifier.isStatic(m.getModifiers()) == optionalStaticRequirement.booleanValue())
				{
					if (has)
					{
						throw new TooManyException("Class "+c.getName()+" has multiple methods with the name "+methodName+"!!  (not counting inherited!)");
					}
					else
					{
						ourMethod = m;
						has = true;
					}
				}
			}
		}
		
		if (has)
		{
			return ourMethod;
		}
		else
		{
			throw new NotFoundException("No method found in class "+c.getName()+" with the name "+methodName+"  (not counting inherited ones)");
		}
	}
	
	
	
	@ThrowAwayValue
	@Nonnull
	public static Set<Method> getMethodsByNameNotCountingInherited(Class c, String methodName)
	{
		Set<Method> ourMethods = new HashSet<>();
		
		for (Method m : c.getDeclaredMethods())
		{
			if (m.getName().equals(methodName))
			{
				ourMethods.add(m);
			}
		}
		
		return ourMethods;
	}
	
	
	
	//Todo write doc ._.
	public static <T> Constructor<T> getConstructor(Class<T> c, Class[] argumentTypes, JavaVisibility optionalVisibilityRequirement)
	{
		if (c == null || argumentTypes == null)
			throw new NullPointerException();
		
		Constructor<T> member = null;
		{
			try
			{
				member = c.getDeclaredConstructor(argumentTypes); //we can use the normal way here because we are not angries.  (XD)
			}
			catch (NoSuchMethodException exc)
			{
				member = null;
			}
		}
		
		if (member != null)
		{
			//Check if it satisfies the criteria
			if (optionalVisibilityRequirement == null || getVisibility(member) == optionalVisibilityRequirement)
			{
				//We have a match! :D
				return member;
			}
		}
		
		return null;
	}
	
	
	
	//Todo write doc ._.
	public static Field getField(Class c, String fieldName, JavaVisibility optionalVisibilityRequirement, Class optionalFieldTypeRequirement, Boolean optionalModifierStaticRequirement, boolean scanSuperclasses)
	{
		if (c == null || fieldName == null)
			throw new NullPointerException();
		
		Class cls = c;
		while (cls != null)
		{
			Field[] members = cls.getDeclaredFields();
			for (Field member : members)
			{
				//Check if it satisfies the criteria
				if (member.getName().equals(fieldName))
				{
					if (optionalFieldTypeRequirement == null || member.getType() == optionalFieldTypeRequirement)
					{
						if (optionalVisibilityRequirement == null || getVisibility(member) == optionalVisibilityRequirement)
						{
							if (optionalVisibilityRequirement == null || Modifier.isStatic(member.getModifiers()) == optionalModifierStaticRequirement)
							{
								//We have a match! :D
								return member;
							}
						}
					}
				}
			}
			
			if (!scanSuperclasses)
				break;
			
			cls = cls.getSuperclass();
		}
		
		return null;
	}
	
	public static Field getField(Class c, String fieldName)
	{
		return getField(c, fieldName, null, null, null, false);
	}
	
	
	
	public static <T> Constructor<T> getPublicNoArgsConstructor(Class<T> c)
	{
		return getConstructor(c, new Class[]{}, JavaVisibility.PUBLIC);
	}
	
	
	
	
	
	
	
	public static Field[] getAllFields(Class c)
	{
		if (c == null)
			throw new NullPointerException();
		
		ArrayList<Field> fields = new ArrayList<Field>();
		Class cls = c;
		while (cls != null)
		{
			Field[] fs = cls.getDeclaredFields();
			for (Field f : fs)
				fields.add(f);
			cls = cls.getSuperclass();
		}
		return fields.toArray(new Field[fields.size()]);
	}
	
	//Superclass methods will be returned last in the list/array
	public static Method[] getAllMethods(Class c)
	{
		if (c == null)
			throw new NullPointerException();
		
		ArrayList<Method> methods = new ArrayList<Method>();
		Class cls = c;
		while (cls != null)
		{
			Method[] fs = cls.getDeclaredMethods();
			for (Method f : fs)
				methods.add(f);
			cls = cls.getSuperclass();
		}
		return methods.toArray(new Method[methods.size()]);
	}
	
	
	/**
	 * 'recursively' scans this class and superclasses :>
	 */
	public static boolean areAllInstanceFieldsFinal(Class c)
	{
		if (c == null)
			throw new NullPointerException();
		
		Class cls = c;
		while (cls != null)
		{
			Field[] fs = cls.getDeclaredFields();
			for (Field f : fs)
			{
				if (!Modifier.isStatic(f.getModifiers()))
				{
					if (!Modifier.isFinal(f.getModifiers()))
						return false;
				}
			}
			cls = cls.getSuperclass();
		}
		
		return true;
	}
	
	
	
	public static boolean isInheritedAnnotationPresent(Class owner, Class<? extends Annotation> annotationClass)
	{
		Class c = owner;
		
		while (c != null)
		{
			if (c.isAnnotationPresent(annotationClass))
				return true;
			else
				c = c.getSuperclass();
		}
		
		return false;
	}
	
	public static <A extends Annotation> A getInheritedAnnotation(Class owner, Class<A> annotationClass)
	{
		Class c = owner;
		A a = null;
		
		while (c != null)
		{
			a = (A)c.getAnnotation(annotationClass); //Sigh..
			
			if (a != null)
				return a;
			else
				c = c.getSuperclass();
		}
		
		return null;
	}
	
	
	
	
	//I can understand these not being in Class
	public static <E> E cast(Object o, Class<E> dest) throws ClassCastException
	{
		if (o == null)
			return null;
		
		if (dest != null)
		{
			if (isInstanceNormalizingPrimitivesAndWrappers(dest, o))
				return (E)o;
			else
			{
				if (Number.class.isAssignableFrom(dest) || (dest.isPrimitive() && dest != boolean.class && dest != char.class))
				{
					if (o instanceof Character)
					{
						o = (int)(Character)o;
					}
					else if (o instanceof CharSequence)
					{
						//TODO Remove the String parsing part (not really part of Cast)
						if (((CharSequence)o).length() == 0)
						{
							o = 0;
						}
						else
						{
							try
							{
								o = Long.parseLong(o.toString());
							}
							catch (NumberFormatException exc)
							{
								try
								{
									o = Double.parseDouble(o.toString());
								}
								catch (NumberFormatException exc1)
								{
									o = 0;
								}
							}
						}
					}
					
					
					if (o instanceof Number)
					{
						Number n = (Number)o;
						if (dest == Byte.class || dest == byte.class)
							return (E)(Byte)n.byteValue();
						else if (dest == Short.class || dest == short.class)
							return (E)(Short)n.shortValue();
						else if (dest == Integer.class || dest == int.class)
							return (E)(Integer)n.intValue();
						else if (dest == Long.class || dest == long.class)
							return (E)(Long)n.longValue();
						else if (dest == Float.class || dest == float.class)
							return (E)(Float)n.floatValue();
						else if (dest == Double.class || dest == double.class)
							return (E)(Double)n.doubleValue();
					}
				}
				
				else if (dest == Boolean.class || dest == boolean.class)
				{
					if (o instanceof Boolean)
						return (E)(Boolean)o;
					else if (o instanceof Number)
						return (E)(Boolean)(((Number)o).longValue() != 0);
				}
				
				else if (dest == Character.class || dest == char.class)
				{
					if (o instanceof Character)
						return (E)(Character)o;
					else if (o instanceof Number)
						return (E)(Character)(char)((Number)o).shortValue();
				}
				
				else if (dest == String.class)
				{
					return (E)new String(o.toString());
				}
			}
		}
		
		throw new ClassCastException("Cannot cast a "+o.getClass().getName()+" to a "+dest.getName());
	}
	
	
	public static boolean canCast(Class source, Class dest)
	{
		if (source == null)
			return true; //null casts to anything
		
		if (dest == null)
			return false; //can't cast *to* null!
		
		if (source == dest)
			return true; //identity
		
		if (dest == Object.class)
			return true; //autoboxing / widening
		
		if (dest.isAssignableFrom(source))
			return true; //widening conversion
		
		if ((source == boolean.class || source == Boolean.class) && (dest == boolean.class || dest == Boolean.class))
			return true; //boolean wrapping
		
		if
		(
		(
		(source != boolean.class && source.isPrimitive())
		||
		source == Character.class
		||
		Number.class.isAssignableFrom(source)
		)
		&&
		(
		(dest != boolean.class && dest.isPrimitive())
		||
		dest == Character.class
		||
		dest == Number.class
		||
		(
		dest == Byte.class ||
		dest == Short.class ||
		dest == Integer.class ||
		dest == Long.class ||
		dest == Float.class ||
		dest == Double.class
		)
		)
		)
			return true;
		
		return false;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Map<String, Class> getSimpleNameReverserMap(Class[] classes) throws NameConflictException
	{
		HashMap<String, Class> map = new HashMap<String, Class>();
		
		for (Class c : classes)
		{
			String simpleName = c.getSimpleName();
			
			if (map.containsKey(simpleName))
				throw new NameConflictException();
			
			map.put(simpleName, c);
		}
		
		return map;
	}
	
	
	public static Map<String, Class> getSimpleNameReverserMap_besteffort(Class[] classes)
	{
		HashMap<String, Class> map = new HashMap<String, Class>();
		
		for (Class c : classes)
		{
			String simpleName = c.getSimpleName();
			
			if (map.containsKey(simpleName))
			{
				//Do nothing; leave the class that comes first (by classes array) in the reverser map
			}
			else
			{
				map.put(simpleName, c);
			}
		}
		
		return map;
	}
	
	
	public static Map<String, List<Class>> getSimpleNameConflicts(Class[] classes)
	{
		HashMap<String, Class> map = new HashMap<String, Class>();
		HashMap<String, List<Class>> conflictMap = new HashMap<String, List<Class>>();
		
		for (Class c : classes)
		{
			String simpleName = c.getSimpleName();
			
			if (map.containsKey(simpleName))
			{
				if (!conflictMap.containsKey(simpleName))
				{
					//On first time add both old one from map and new one 'c'
					List<Class> list = new ArrayList<Class>();
					list.add(map.get(simpleName));
					list.add(c);
					conflictMap.put(simpleName, list);
				}
				else
				{
					conflictMap.get(simpleName).add(c);
				}
			}
			else
			{
				map.put(simpleName, c);
			}
		}
		
		return conflictMap;
	}
	
	
	
	
	
	
	
	/**
	 * Some method invoked you.
	 * You invoked this.
	 * This invokes things.
	 * The class of that first "some method" is what this returns.
	 */
	public static String getInvokingClassName()
	{
		StackTraceElement[] stack = Thread.currentThread().getStackTrace();
		
		/*
		 * Trace:
		 * 		java.lang.Thread: getStackTrace()
		 * 		AngryReflectionUtility: getInvokingClassName()
		 * 		Your class: your method
		 * 		Higher class: higher method
		 */
		
		return stack[3].getClassName();
	}
	
	
	
	
	
	/**
	 * Example: getMethod(AngryReflectionUtility.class, "getMethod(java.lang.Class, java.lang.String, java.lang.reflect.Method)", Method.class)
	 * @param returnType if this is not null, we'll filter by return type (which can be necessary to uniquely disambiguate a method in JVM (though not JLS, see {@link Class#getMethod(String, Class...)}))
	 * @return the first matching method (superclasses last), or <code>null</code> if method is not found
	 */
	@Nullable
	public static Method getMethod(Class cls, String methodDescriptor, Class returnType)
	{
		String methodName = null;
		String[] methodArgTypeNames = null;
		{
			String[] pp = StringUtilities.split(methodDescriptor, '(');
			if (pp.length != 2)
				throw new IllegalArgumentException("Invalid syntax: '"+methodDescriptor+"'");
			methodName = pp[0];
			
			String args = pp[1];
			if (!args.endsWith(")"))
				throw new IllegalArgumentException("Invalid syntax: '"+methodDescriptor+"'");
			args = args.substring(0, args.length()-1);
			
			methodArgTypeNames = StringUtilities.split(args, ',');
			for (int i = 0; i < methodArgTypeNames.length; i++)
			{
				methodArgTypeNames[i] = methodArgTypeNames[i].trim();
			}
		}
		
		Class[] methodArgTypeClasses = null;
		{
			methodArgTypeClasses = new Class[methodArgTypeNames.length];
			for (int i = 0; i < methodArgTypeClasses.length; i++)
			{
				methodArgTypeClasses[i] = forName(methodArgTypeNames[i]);
				if (methodArgTypeClasses[i] == null)
					throw new RuntimeException(new ClassNotFoundException(methodArgTypeNames[i]));
			}
		}
		
		Method[] allMethods = getAllMethods(cls);
		
		for (Method m : allMethods)
		{
			if (m.getName().equals(methodName))
			{
				if (Arrays.equals(m.getParameterTypes(), methodArgTypeClasses))
				{
					if (returnType == null || returnType.equals(m.getReturnType()))
					{
						return m;
					}
				}
			}
		}
		
		return null;
	}
	
	@Nullable
	public static Method getMethod(Class cls, String methodDescriptor)
	{
		return getMethod(cls, methodDescriptor, (Class)null);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean lazyprogrammersPublicFieldbasedEquals(Object a, Object b) throws WrappedThrowableRuntimeException
	{
		if (a == b) //(null == null) == true
			return true;
		if (a == null ^ b == null)
			return false;
		if (a.getClass() != b.getClass())
			return false;
		
		Class c = a.getClass(); //the arbitraries; blah xP
		
		Field[] fieldses = c.getFields();
		
		for (Field field : fieldses)
		{
			Object vA = null;
			Object vB = null;
			{
				try
				{
					vA = field.get(a);
					vB = field.get(a);
				}
				catch (IllegalArgumentException exc)
				{
					throw new WrappedThrowableRuntimeException(exc);
				}
				catch (IllegalAccessException exc)
				{
					throw new WrappedThrowableRuntimeException(exc);
				}
			}
			
			if (!BasicObjectUtilities.eq(vA, vB))
				return false;
		}
		
		return true;
	}
	
	
	
	
	
	
	public static int getPrimitiveBitLength(Class primitiveClass)
	{
		return getPrimitiveBitLength(primitiveClass, 8);
	}
	
	public static int getPackedPrimitiveBitLength(Class primitiveClass)
	{
		return getPrimitiveBitLength(primitiveClass, 1);
	}
	
	public static int getPrimitiveBitLength(Class primitiveClass, int booleanBitLength)
	{
		if (primitiveClass == boolean.class)
			return booleanBitLength;
		else if (primitiveClass == byte.class)
			return 8;
		else if (primitiveClass == short.class)
			return 16;
		else if (primitiveClass == char.class)
			return 16;
		else if (primitiveClass == int.class)
			return 32;
		else if (primitiveClass == float.class)
			return 32;
		else if (primitiveClass == long.class)
			return 64;
		else if (primitiveClass == double.class)
			return 64;
		else
			throw new IllegalArgumentException("Invalid primitive type: "+primitiveClass.getName());
	}
	
	
	
	
	
	
	
	
	/**
	 * Ambiguous return value: null can mean no-common-supertype, or it can mean all-provided-classes-were-null (ie, NoneType's, which conceptually extend Object)
	 */
	public static Class getSubestCommonSuperclass(Class... classes)
	{
		if (classes == null || classes.length == 0)
			return null;
		
		else if (classes[0].isPrimitive() || classes[0] == Void.class) //if any is this the only possibilities is that they all are it (return it) or they share no commonality (return null), so SUPAH FAST MODE (and clarity?)  :>
		{
			for (int i = 1; i < classes.length; i++)
			{
				Class c = classes[i];
				
				if (c != classes[0])
					return null; //no commonality at all!
			}
			
			return classes[0];
		}
		
		else
		{
			Class[][] hierarchies = null;
			{
				hierarchies = new Class[classes.length][];
				for (int i = 0; i < classes.length; i++)
				{
					hierarchies[i] = getClassInheritanceHierarchy(classes[i]);
				}
			}
			
			int minLength = 0;
			{
				boolean has = false;
				for (Class[] hierarchy : hierarchies)
				{
					if (!has || hierarchy.length < minLength)
						minLength = hierarchy.length;
				}
			}
			
			
			Class lastEqual = null;
			
			for (int i = 0; i < minLength; i++)
			{
				boolean allEqualHere = false;
				{
					allEqualHere = true;
					for (int e = 1; e < hierarchies.length; e++)
					{
						if (hierarchies[e][i] != hierarchies[0][i])
						{
							allEqualHere = false;
							break;
						}
					}
				}
				
				if (allEqualHere)
					lastEqual = hierarchies[0][i];//token value
				else
					return lastEqual;
			}
			
			return lastEqual;
		}
	}
	
	
	/**
	 * For primitives and Void.class, returns singleton array with that input value.
	 * For null (as in conceptual null.getClass()), returns new Class[]{Object.class}  (which makes sense if you think about it! (what's the superest type a variable has to have to hold a null? ;> ) )
	 * For all others, returns a super-first inheritance hierarchy (ie, always starts with Object.class)
	 */
	public static Class[] getClassInheritanceHierarchy(Class c)
	{
		if (c == null)
			return new Class[]{Object.class};
		else if (c.isPrimitive() || c == Void.class)
			return new Class[]{c};
		else
		{
			//Yeah, we defined it opposite how it naturally comes, BUT IT MAKES MORE SENSE THAT WAY 8|  XD
			Class[] h = getClassInheritanceHierarchyReversed(c);
			ArrayUtilities.reverse(h);
			return h;
		}
	}
	
	
	public static Class[] getClassInheritanceHierarchyReversed(Class c)
	{
		if (c == null)
			return new Class[]{Object.class};
		else if (c.isPrimitive() || c == Void.class)
			return new Class[]{c};
		else
		{
			List<Class> l = new ArrayList<Class>();
			
			Class s = c;
			while (s != null)
			{
				l.add(s);
				s = s.getSuperclass();
			}
			
			return l.toArray(new Class[l.size()]);
		}
	}
	
	
	
	public static Class[] getClassesOf(Object... things)
	{
		if (things == null)
			return null;
		else
		{
			Class[] classes = new Class[things.length];
			for (int i = 0; i < things.length; i++)
				classes[i] = BasicObjectUtilities.getClassNT(things[i]);
			return classes;
		}
	}
	
	
	
	
	
	
	
	public static void setAccessibleIfNotPublic(Member thing) throws SecurityException
	{
		if (Modifier.isPublic(thing.getModifiers()))
			return;
		
		((AccessibleObject)thing).setAccessible(true);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//<Dynamic object things
	public static Map<Field, Object> createObjectAsMapViewByFieldsThroughReflection(final Object object, final boolean includeNonpublicMembersThroughAccessibilityOverrideAttempts, final boolean includeSupertypeMembers)
	{
		//TODO
		throw new NotYetImplementedException();
		
		//if (object == null)
		//	throw new NullPointerException();
		//
		////Todo you could do this more efficiently; ie, narrowquery vs. broadquery-filter xD'
		//Field[] fields = includeSupertypeMembers ? AngryReflectionUtility.getAllFields(object.getClass()) : object.getClass().getDeclaredFields();
		//
		//fields = filter(new UnaryFunctionObjectToBoolean<Field>
		//()
		//{
		//	public boolean f(Field f)
		//	{
		//		return (!includeNonpublicMembersThroughAccessibilityOverrideAttempts || Modifier.isPublic(f.getModifiers()));
		//	}
		//}, fields);
		//
		//
		//if (includeNonpublicMembersThroughAccessibilityOverrideAttempts)
		//{
		//	for (Field f : fields)
		//		AngryReflectionUtility.setAccessibleIfNotPublic(f);
		//}
		//
		//
		//Map.Entry<Field, Object>[] entries = new Map.Entry[fields.length];
		//
		//Set<Map.Entry<Field, Object>> entrySet = Collections.unmodifiableSet(new IdentityArraySet<Map.Entry<Field,Object>>(entries));
		//
		//return new AbstractMap<Field, Object>()
		//	{
		//	@Override
		//	public Set<Map.Entry<Field, Object>> entrySet()
		//	{
		//		//TODO @@@ ._.
		//	}
		//	};
	}
	
	public static Map<Field, Object> createObjectAsMapViewByFieldsThroughReflection(Object object)
	{
		return createObjectAsMapViewByFieldsThroughReflection(object, false, true);
	}
	
	
	
	
	
	
	//TODO method-based property things :>
	//TODO all kinds of happy annotations for overriding javabean properties determinations :3 :D
	//	+ Note: isn't this categorized in JavaUtilities? :>
	
	
	public static Set newPropertySingletonByFieldThroughReflection(Field field, Object instance)
	{
		//TODO
		throw new NotYetImplementedException();
		//return new DelegatingSingletonCollection(getter, setter);
	}
	
	public static Set newStaticPropertySingletonByFieldThroughReflection(Field staticField)
	{
		//TODO
		throw new NotYetImplementedException();
		//if (!isStatic(staticField))
		//	throw new IllegalArgumentException();
	}
	//Dynamic object things>
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Word "Java" is clarified here since this is a *Java* thing, not a *JVM* thing ;D
	
	public static boolean isJavaToplevelClass(Class c)
	{
		return c.getEnclosingClass() == null;
	}
	
	public static boolean isJavaNestedClass(Class c)
	{
		return c.isMemberClass() && Modifier.isStatic(c.getModifiers());
	}
	
	public static boolean isJavaInnerClass(Class c)
	{
		return c.isMemberClass() && !Modifier.isStatic(c.getModifiers());
	}
	
	public static boolean isJavaLocalClass(Class c)
	{
		return c.isLocalClass();
	}
	
	public static boolean isJavaAnonymousClass(Class c)
	{
		return c.isAnonymousClass();
	}
	
	
	
	
	public static boolean isJavaNestedOrInnerClass(Class c)
	{
		return c.isMemberClass();
	}
	
	public static boolean isJavaLocalOrAnonymousClass(Class c)
	{
		//Also c.getCanonicalName() == null  ^_^
		return c.isLocalClass() || c.isAnonymousClass();
	}
	
	public static boolean isJavaClassThatCouldHaveOuterClassContextField(Class c)
	{
		return isJavaInnerClass(c) || c.isLocalClass() || c.isAnonymousClass();
	}
	
	
	
	public static void _isJavaSelfCheck(Class c)
	{
		if (c == null)
			return;
		
		int n = 0;
		if (isJavaToplevelClass(c)) n++;
		if (isJavaNestedClass(c)) n++;
		if (isJavaInnerClass(c)) n++;
		if (isJavaLocalClass(c)) n++;
		if (isJavaAnonymousClass(c)) n++;
		
		if (n != 1)
			throw new AssertionError("isJavaXYZ checks failed; "+n+" leafset predicates return true! ;_;");
	}
	
	
	
	
	
	
	
	public static Class getToplevelClass(Class c)
	{
		Class x = c;
		
		while (true)
		{
			Class y = x.getEnclosingClass();
			
			if (y == null)
				return x;
		}
	}
	
	
	
	
	
	
	@Nonnull
	public static Field getEnumField(Enum singleton)
	{
		requireNonNull(singleton);
		
		Class c = singleton.getDeclaringClass();  //NOT GETCLASS, BECAUSE ANONYMOUS ENUM TYPES  (ie, when you add the bodies you know, like in Direction2D! ^^" )
		
		Field f;
		{
			try
			{
				f = c.getDeclaredField(singleton.name());
			}
			catch (NoSuchFieldException exc)
			{
				throw new ImPrettySureThisNeverActuallyHappensRuntimeException(exc);
			}
		}
		
		if (f == null)
			throw new ImPrettySureThisNeverActuallyHappensRuntimeException();
		
		return f;
	}
	
	
	
	
	
	
	
	/**
	 * "Code name" is something I made up right here, to be sure ^^'
	 * But it's unambiguous like binary and JVM names and yet readable like the canonical/source name for arrays! :>
	 * 
	 * 
	 * 
	 * JVM name		Inside .class files! :D				java/util/Map$Entry		[[Ljava/util/Map$Entry;		I		[I
	 * Binary name	{@link Class#getName()}				java.util.Map$Entry		[[Ljava.util.Map$Entry;		int		[I
	 * "Code name"	{@link #getCodeName(Class)}			java.util.Map$Entry		java.util.Map$Entry[][]		int		int[]
	 * Source name	{@link Class#getCanonicalName()}	java.util.Map.Entry		java.util.Map.Entry[][]		int		int[]
	 */
	public static String getCodeName(Class c)
	{
		if (c.isArray())
		{
			return getCodeName(c.getComponentType())+"[]";
		}
		else
		{
			return c.getName();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	//TODO Revamp this to run in one pass and also figure out how non-public annotations should work (optional parameter?), and *inherited* annotations!!
	@Nonnull
	@ThrowAwayValue
	public static Map<Class<? extends Annotation>, List<? extends Annotation>> getPublicUninheritedAnnotations(AnnotatedElement o)
	{
		Map<Class<? extends Annotation>, List<? extends Annotation>> annotationsMap = new HashMap<>();
		
		for (Annotation a : o.getAnnotations())
		{
			Class<? extends Annotation> c = a.annotationType();
			if (!annotationsMap.containsKey(c))
				annotationsMap.put(c, asList(o.getAnnotationsByType(c)));
		}
		
		return annotationsMap;
	}
	
	
	
	
	
	@Nullable
	public static Method getUninheritedMethodByAnnotationPresence(Class c, Class<? extends Annotation> annotationClass)
	{
		Method found = null;
		
		for (Method m : c.getMethods())
		{
			if (m.isAnnotationPresent(annotationClass))
			{
				if (found != null)
					throw new ImpossibleException("Misconfigured class!!  Multiple methods with the @"+annotationClass.getName()+" annotation!!");
				else
					found = m;
			}
		}
		
		return found;
	}
	
	
	
	
	
	
	@Nonnull
	public static Class getRawType(@Nonnull Type type)
	{
		return type instanceof Class ? (Class)type : (Class)((ParameterizedType)type).getRawType();
	}
	
	
	
	
	
	
	
	
	
	
	public static <T extends AccessibleObject> T makeAccessible(T member)
	{
		member.setAccessible(true);
		return member;
	}
}
