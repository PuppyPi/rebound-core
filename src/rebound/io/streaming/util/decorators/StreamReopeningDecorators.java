package rebound.io.streaming.util.decorators;

import static java.util.Objects.*;
import static rebound.util.ExceptionUtilities.*;
import java.io.EOFException;
import java.io.IOException;
import javax.annotation.Nonnull;
import rebound.io.streaming.api.ClosedStreamException;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.BooleanBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ByteBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.CharBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.DoubleBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.FloatBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.IntBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.LongBlockWriteStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockReadStream;
import rebound.io.streaming.api.StreamAPIs.ShortBlockWriteStream;

public class StreamReopeningDecorators
{
	public static interface StreamReopener<S>
	{
		@Nonnull
		public S reopen() throws IOException;
	}
	
	
	/**
	 * See DefaultReopeningStreamErrorHandler for a default implementation that properly rethrows fatal {@link Error}s and such things :>
	 */
	public static interface ReopeningStreamErrorHandler
	{
		public void caughtThrowableOpeningStream(Throwable t);
		public void caughtThrowableUsingStream(Throwable t);
		
		
		
		public static ReopeningStreamErrorHandler newFatalThrowingReopeningStreamErrorHandler(ReopeningStreamErrorHandler underlying)
		{
			return new ReopeningStreamErrorHandler()
			{
				@Override
				public void caughtThrowableOpeningStream(Throwable t)
				{
					throwIfFatalError(t);
					underlying.caughtThrowableOpeningStream(t);
				}
				
				@Override
				public void caughtThrowableUsingStream(Throwable t)
				{
					throwIfFatalError(t);
					underlying.caughtThrowableUsingStream(t);
				}
			};
		}
		
		
		public static final ReopeningStreamErrorHandler DefaultReopeningStreamErrorHandler = newFatalThrowingReopeningStreamErrorHandler(new ReopeningStreamErrorHandler()
		{
			@Override
			public void caughtThrowableOpeningStream(Throwable t)
			{
				t.printStackTrace();
			}
			
			@Override
			public void caughtThrowableUsingStream(Throwable t)
			{
				t.printStackTrace();
			}
		});
		
		
		public static final ReopeningStreamErrorHandler NullReopeningStreamErrorHandler = newFatalThrowingReopeningStreamErrorHandler(new ReopeningStreamErrorHandler()
		{
			@Override
			public void caughtThrowableOpeningStream(Throwable t)
			{
			}
			
			@Override
			public void caughtThrowableUsingStream(Throwable t)
			{
			}
		});
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/* <<<
	primxp
	
	
	
	
	
	
	
	
	
	public static class Reopening_$$Prim$$_BlockReadStream
	implements _$$Prim$$_BlockReadStream
	{
		protected StreamReopener<_$$Prim$$_BlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected _$$Prim$$_BlockReadStream current = null;
		
		
		public Reopening_$$Prim$$_BlockReadStream(@Nonnull StreamReopener<_$$Prim$$_BlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (closed)
				throw new ClosedStreamException();
			
			if (current == null)
			{
				while (true)
				{
					try
					{
						_$$Prim$$_BlockReadStream s = reopener.reopen();
						
						if (s != null)
						{
							current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		public int read(_$$prim$$_[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		public _$$prim$$_ read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (current != null)
					current.close();
				
				this.closed = true;
			}
		}
		
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class Reopening_$$Prim$$_BlockWriteStream
	implements _$$Prim$$_BlockWriteStream
	{
		protected StreamReopener<_$$Prim$$_BlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected _$$Prim$$_BlockWriteStream current = null;
		
		
		public Reopening_$$Prim$$_BlockWriteStream(@Nonnull StreamReopener<_$$Prim$$_BlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (closed)
				throw new ClosedStreamException();
			
			if (current == null)
			{
				while (true)
				{
					try
					{
						_$$Prim$$_BlockWriteStream s = reopener.reopen();
						
						if (s != null)
						{
							current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		public int write(_$$prim$$_[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		public void write(_$$prim$$_ unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (current != null)
					current.close();
				
				this.closed = true;
			}
		}
		
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	 */
	
	
	
	
	
	
	
	
	
	public static class ReopeningBooleanBlockReadStream
	implements BooleanBlockReadStream
	{
		protected StreamReopener<BooleanBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected BooleanBlockReadStream current = null;
		
		
		public ReopeningBooleanBlockReadStream(@Nonnull StreamReopener<BooleanBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						BooleanBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(boolean[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public boolean read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningBooleanBlockWriteStream
	implements BooleanBlockWriteStream
	{
		protected StreamReopener<BooleanBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected BooleanBlockWriteStream current = null;
		
		
		public ReopeningBooleanBlockWriteStream(@Nonnull StreamReopener<BooleanBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						BooleanBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(boolean[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(boolean unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ReopeningByteBlockReadStream
	implements ByteBlockReadStream
	{
		protected StreamReopener<ByteBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected ByteBlockReadStream current = null;
		
		
		public ReopeningByteBlockReadStream(@Nonnull StreamReopener<ByteBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						ByteBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(byte[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public byte read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningByteBlockWriteStream
	implements ByteBlockWriteStream
	{
		protected StreamReopener<ByteBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected ByteBlockWriteStream current = null;
		
		
		public ReopeningByteBlockWriteStream(@Nonnull StreamReopener<ByteBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						ByteBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(byte[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(byte unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ReopeningCharBlockReadStream
	implements CharBlockReadStream
	{
		protected StreamReopener<CharBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected CharBlockReadStream current = null;
		
		
		public ReopeningCharBlockReadStream(@Nonnull StreamReopener<CharBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						CharBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(char[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public char read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningCharBlockWriteStream
	implements CharBlockWriteStream
	{
		protected StreamReopener<CharBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected CharBlockWriteStream current = null;
		
		
		public ReopeningCharBlockWriteStream(@Nonnull StreamReopener<CharBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						CharBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(char[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(char unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ReopeningShortBlockReadStream
	implements ShortBlockReadStream
	{
		protected StreamReopener<ShortBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected ShortBlockReadStream current = null;
		
		
		public ReopeningShortBlockReadStream(@Nonnull StreamReopener<ShortBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						ShortBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(short[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public short read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningShortBlockWriteStream
	implements ShortBlockWriteStream
	{
		protected StreamReopener<ShortBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected ShortBlockWriteStream current = null;
		
		
		public ReopeningShortBlockWriteStream(@Nonnull StreamReopener<ShortBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						ShortBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(short[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(short unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ReopeningFloatBlockReadStream
	implements FloatBlockReadStream
	{
		protected StreamReopener<FloatBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected FloatBlockReadStream current = null;
		
		
		public ReopeningFloatBlockReadStream(@Nonnull StreamReopener<FloatBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						FloatBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(float[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public float read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningFloatBlockWriteStream
	implements FloatBlockWriteStream
	{
		protected StreamReopener<FloatBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected FloatBlockWriteStream current = null;
		
		
		public ReopeningFloatBlockWriteStream(@Nonnull StreamReopener<FloatBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						FloatBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(float[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(float unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ReopeningIntBlockReadStream
	implements IntBlockReadStream
	{
		protected StreamReopener<IntBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected IntBlockReadStream current = null;
		
		
		public ReopeningIntBlockReadStream(@Nonnull StreamReopener<IntBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						IntBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(int[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningIntBlockWriteStream
	implements IntBlockWriteStream
	{
		protected StreamReopener<IntBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected IntBlockWriteStream current = null;
		
		
		public ReopeningIntBlockWriteStream(@Nonnull StreamReopener<IntBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						IntBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(int[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(int unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ReopeningDoubleBlockReadStream
	implements DoubleBlockReadStream
	{
		protected StreamReopener<DoubleBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected DoubleBlockReadStream current = null;
		
		
		public ReopeningDoubleBlockReadStream(@Nonnull StreamReopener<DoubleBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						DoubleBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(double[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public double read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningDoubleBlockWriteStream
	implements DoubleBlockWriteStream
	{
		protected StreamReopener<DoubleBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected DoubleBlockWriteStream current = null;
		
		
		public ReopeningDoubleBlockWriteStream(@Nonnull StreamReopener<DoubleBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						DoubleBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(double[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(double unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static class ReopeningLongBlockReadStream
	implements LongBlockReadStream
	{
		protected StreamReopener<LongBlockReadStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected LongBlockReadStream current = null;
		
		
		public ReopeningLongBlockReadStream(@Nonnull StreamReopener<LongBlockReadStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						LongBlockReadStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int read(long[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long read() throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.read();
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	public static class ReopeningLongBlockWriteStream
	implements LongBlockWriteStream
	{
		protected StreamReopener<LongBlockWriteStream> reopener;
		protected ReopeningStreamErrorHandler errorHandler;
		protected long sleepTimeBetweenReopenAttemptsMS;
		
		protected boolean closed = false;
		protected LongBlockWriteStream current = null;
		
		
		public ReopeningLongBlockWriteStream(@Nonnull StreamReopener<LongBlockWriteStream> reopener, @Nonnull ReopeningStreamErrorHandler errorHandler, long sleepTimeBetweenReopenAttemptsMS)
		{
			if (sleepTimeBetweenReopenAttemptsMS < 0)
				throw new IllegalArgumentException();
			
			this.reopener = requireNonNull(reopener);
			this.errorHandler = requireNonNull(errorHandler);
			this.sleepTimeBetweenReopenAttemptsMS = sleepTimeBetweenReopenAttemptsMS;
		}
		
		
		protected void getStream()
		{
			if (this.closed)
				throw new ClosedStreamException();
			
			if (this.current == null)
			{
				while (true)
				{
					try
					{
						LongBlockWriteStream s = this.reopener.reopen();
						
						if (s != null)
						{
							this.current = s;
							return;
						}
					}
					catch (Throwable t)
					{
						this.errorHandler.caughtThrowableOpeningStream(t);
					}
					
					
					if (this.sleepTimeBetweenReopenAttemptsMS > 0)
					{
						try
						{
							Thread.sleep(this.sleepTimeBetweenReopenAttemptsMS);
						}
						catch (InterruptedException exc)
						{
						}
					}
				}
			}
		}
		
		protected void errorEncounteredInUse(Throwable t)
		{
			this.errorHandler.caughtThrowableUsingStream(t);
			
			try
			{
				this.current.close();
			}
			catch (Exception | AssertionError exc)
			{
				t.addSuppressed(exc);
			}
			
			this.current = null;
		}
		
		
		
		@Override
		public boolean isEOF() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.isEOF();
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public int write(long[] buffer, int offset, int length) throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.write(buffer, offset, length);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void write(long unit) throws EOFException, ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.write(unit);
					return;
				}
				catch (Throwable t)
				{
					if (t instanceof EOFException)
						throw (EOFException)t;
					
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public long skip(long amount) throws ClosedStreamException, IllegalArgumentException
		{
			if (amount < 0)
				throw new IllegalArgumentException();
			
			
			while (true)
			{
				getStream();
				
				try
				{
					return this.current.skip(amount);
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		@Override
		public void flush() throws ClosedStreamException
		{
			while (true)
			{
				getStream();
				
				try
				{
					this.current.flush();
					return;
				}
				catch (Throwable t)
				{
					errorEncounteredInUse(t);
				}
			}
		}
		
		
		
		
		
		@Override
		public void close() throws IOException
		{
			if (!this.closed)
			{
				//Do this first so it won't get cleared and they can keep retrying close()  :3
				if (this.current != null)
					this.current.close();
				
				this.closed = true;
			}
		}
		
		@Override
		public boolean isClosed()
		{
			return this.closed;
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	//>>>
}
