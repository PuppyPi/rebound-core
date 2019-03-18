/*
 * Created on May 29, 2013
 * 	with the GREAT AND POWERFUL Eclipse(c)
 */
package rebound.util.container;

import static rebound.util.Primitives.*;
import rebound.concurrency.immutability.JavaImmutability;
import rebound.util.container.ContainerInterfaces.BooleanContainer;
import rebound.util.container.ContainerInterfaces.ByteContainer;
import rebound.util.container.ContainerInterfaces.CharacterContainer;
import rebound.util.container.ContainerInterfaces.DoubleContainer;
import rebound.util.container.ContainerInterfaces.FloatContainer;
import rebound.util.container.ContainerInterfaces.IntegerContainer;
import rebound.util.container.ContainerInterfaces.LongContainer;
import rebound.util.container.ContainerInterfaces.ObjectContainer;
import rebound.util.container.ContainerInterfaces.ShortContainer;
import rebound.util.objectutil.BasicObjectUtilities;
import rebound.util.objectutil.RuntimeImmutability;

public class SimpleContainers
{
	public static class SimpleObjectContainer<T>
	implements ProperContainer<SimpleObjectContainer<T>>, ObjectContainer<T>, RuntimeImmutability
	{
		protected T value;
		
		public SimpleObjectContainer()
		{
			this.value = null;
		}
		
		public SimpleObjectContainer(T value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public T get()
		{
			return this.value;
		}
		
		@Override
		public void set(T value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof ObjectContainer)
				this.set((T)((ObjectContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleObjectContainer<T> clone()
		{
			return new SimpleObjectContainer<T>(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof ObjectContainer)
				return ((ObjectContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return BasicObjectUtilities.hashNT(this.value);
		}
		
		@Override
		public SimpleObjectContainer<T> newInstance()
		{
			return new SimpleObjectContainer<T>();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	
	
	
	/* <<<
	primxp
	public static class Simple_$$Primitive$$_Container
	implements ProperContainer<Simple_$$Primitive$$_Container>, _$$Primitive$$_Container, RuntimeImmutability
	{
		protected _$$prim$$_ value;
		
		public Simple_$$Primitive$$_Container()
		{
			this.value = _$$primdef$$_;
		}
		
		public Simple_$$Primitive$$_Container(_$$prim$$_ value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public _$$prim$$_ get()
		{
			return this.value;
		}
		
		@Override
		public void set(_$$prim$$_ value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof _$$Primitive$$_Container)
				this.set(((_$$Primitive$$_Container)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public Simple_$$Primitive$$_Container clone()
		{
			return new Simple_$$Primitive$$_Container(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof _$$Primitive$$_Container)
				return ((_$$Primitive$$_Container)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public Simple_$$Primitive$$_Container newInstance()
		{
			return new Simple_$$Primitive$$_Container();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	 */
	public static class SimpleBooleanContainer
	implements ProperContainer<SimpleBooleanContainer>, BooleanContainer, RuntimeImmutability
	{
		protected boolean value;
		
		public SimpleBooleanContainer()
		{
			this.value = false;
		}
		
		public SimpleBooleanContainer(boolean value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public boolean get()
		{
			return this.value;
		}
		
		@Override
		public void set(boolean value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof BooleanContainer)
				set(((BooleanContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleBooleanContainer clone()
		{
			return new SimpleBooleanContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof BooleanContainer)
				return ((BooleanContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleBooleanContainer newInstance()
		{
			return new SimpleBooleanContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	public static class SimpleByteContainer
	implements ProperContainer<SimpleByteContainer>, ByteContainer, RuntimeImmutability
	{
		protected byte value;
		
		public SimpleByteContainer()
		{
			this.value = ((byte)0);
		}
		
		public SimpleByteContainer(byte value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public byte get()
		{
			return this.value;
		}
		
		@Override
		public void set(byte value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof ByteContainer)
				set(((ByteContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleByteContainer clone()
		{
			return new SimpleByteContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof ByteContainer)
				return ((ByteContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleByteContainer newInstance()
		{
			return new SimpleByteContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	public static class SimpleCharacterContainer
	implements ProperContainer<SimpleCharacterContainer>, CharacterContainer, RuntimeImmutability
	{
		protected char value;
		
		public SimpleCharacterContainer()
		{
			this.value = ((char)0);
		}
		
		public SimpleCharacterContainer(char value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public char get()
		{
			return this.value;
		}
		
		@Override
		public void set(char value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof CharacterContainer)
				set(((CharacterContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleCharacterContainer clone()
		{
			return new SimpleCharacterContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof CharacterContainer)
				return ((CharacterContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleCharacterContainer newInstance()
		{
			return new SimpleCharacterContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	public static class SimpleShortContainer
	implements ProperContainer<SimpleShortContainer>, ShortContainer, RuntimeImmutability
	{
		protected short value;
		
		public SimpleShortContainer()
		{
			this.value = ((short)0);
		}
		
		public SimpleShortContainer(short value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public short get()
		{
			return this.value;
		}
		
		@Override
		public void set(short value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof ShortContainer)
				set(((ShortContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleShortContainer clone()
		{
			return new SimpleShortContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof ShortContainer)
				return ((ShortContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleShortContainer newInstance()
		{
			return new SimpleShortContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	public static class SimpleFloatContainer
	implements ProperContainer<SimpleFloatContainer>, FloatContainer, RuntimeImmutability
	{
		protected float value;
		
		public SimpleFloatContainer()
		{
			this.value = 0.0f;
		}
		
		public SimpleFloatContainer(float value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public float get()
		{
			return this.value;
		}
		
		@Override
		public void set(float value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof FloatContainer)
				set(((FloatContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleFloatContainer clone()
		{
			return new SimpleFloatContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof FloatContainer)
				return ((FloatContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleFloatContainer newInstance()
		{
			return new SimpleFloatContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	public static class SimpleIntegerContainer
	implements ProperContainer<SimpleIntegerContainer>, IntegerContainer, RuntimeImmutability
	{
		protected int value;
		
		public SimpleIntegerContainer()
		{
			this.value = 0;
		}
		
		public SimpleIntegerContainer(int value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public int get()
		{
			return this.value;
		}
		
		@Override
		public void set(int value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof IntegerContainer)
				set(((IntegerContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleIntegerContainer clone()
		{
			return new SimpleIntegerContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof IntegerContainer)
				return ((IntegerContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleIntegerContainer newInstance()
		{
			return new SimpleIntegerContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	public static class SimpleDoubleContainer
	implements ProperContainer<SimpleDoubleContainer>, DoubleContainer, RuntimeImmutability
	{
		protected double value;
		
		public SimpleDoubleContainer()
		{
			this.value = 0.0d;
		}
		
		public SimpleDoubleContainer(double value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public double get()
		{
			return this.value;
		}
		
		@Override
		public void set(double value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof DoubleContainer)
				set(((DoubleContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleDoubleContainer clone()
		{
			return new SimpleDoubleContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof DoubleContainer)
				return ((DoubleContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleDoubleContainer newInstance()
		{
			return new SimpleDoubleContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	public static class SimpleLongContainer
	implements ProperContainer<SimpleLongContainer>, LongContainer, RuntimeImmutability
	{
		protected long value;
		
		public SimpleLongContainer()
		{
			this.value = 0l;
		}
		
		public SimpleLongContainer(long value)
		{
			this.value = value;
		}
		
		@Override
		public JavaImmutability isImmutable() //for in case subclasses wants to override :>
		{
			return JavaImmutability.Mutable;
		}
		
		
		@Override
		public long get()
		{
			return this.value;
		}
		
		@Override
		public void set(long value)
		{
			this.value = value;
		}
		
		
		@Override
		public void setFrom(Object source)
		{
			if (source instanceof LongContainer)
				set(((LongContainer)source).get());
			else if (source == null)
				throw new NullPointerException();
			else
				throw new ClassCastException(source.getClass().getName());
		}
		
		
		@Override
		public SimpleLongContainer clone()
		{
			return new SimpleLongContainer(this.value);
		}
		
		@Override
		public boolean equals(Object obj)
		{
			if (obj == this)
				return true;
			
			if (obj instanceof LongContainer)
				return ((LongContainer)obj).get() == this.value;
			else
				return false;
		}
		
		@Override
		public int hashCode()
		{
			return hashprim(this.value);
		}
		
		@Override
		public SimpleLongContainer newInstance()
		{
			return new SimpleLongContainer();
		}
		
		@Override
		public String toString()
		{
			return this.getClass().getName()+":("+this.value+")";
		}
	}
	
	
	// >>>
}
