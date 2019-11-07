/*
 * Created on Nov 17, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.file;

import static java.util.Collections.*;
import static java.util.Objects.*;
import static rebound.GlobalCodeMetastuffContext.*;
import static rebound.io.util.BasicIOUtilities.*;
import static rebound.io.util.JRECompatIOUtilities.*;
import static rebound.math.SmallIntegerMathUtilities.*;
import static rebound.testing.WidespreadTestingUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.ArrayUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
import static rebound.util.objectutil.ObjectUtilities.*;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.channels.FileChannel.MapMode;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.nio.file.attribute.PosixFilePermission;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.hints.ImplementationTransparency;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.exceptions.ImPrettySureThisNeverActuallyHappensRuntimeException;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NoFreeResourceFoundException;
import rebound.exceptions.NotFoundException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.OverflowException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.io.util.FSIOUtilities;
import rebound.text.StringUtilities;
import rebound.text.StringUtilities.WhatToDoWithEmpties;
import rebound.util.collections.ArrayUtilities;
import rebound.util.collections.CollectionUtilities;
import rebound.util.container.ContainerInterfaces.IntegerContainer;
import rebound.util.container.SimpleContainers.SimpleIntegerContainer;
import rebound.util.functional.FunctionInterfaces.BinaryProcedure;
import rebound.util.functional.FunctionInterfaces.UnaryFunction;
import rebound.util.functional.FunctionInterfaces.UnaryFunctionIntToObject;
import rebound.util.functional.FunctionInterfaces.UnaryProcedure;
import rebound.util.functional.throwing.FunctionalInterfacesThrowingCheckedExceptionsStandard.RunnableThrowingIOException;
import rebound.util.objectutil.JavaNamespace;

//Todo should joinPathsStrict use a different definition of strictness? (the one used by the isStrictPath()s ?) XD
//         + should we have three levels? XD

/**
 * 
 * 
 * ====
 * Note on terminologies ^_^
 * 
 * <ul>
 * 	<li>path = join(dirname, basename)
 * 	<li>basename = stem + suffix
 * 	<li>suffix = extensionPresent ? "."+extension : ""
 * 	<li>(extension == null if !extensionPresent :> )
 * </ul>
 * ====
 * 
 * @author Puppy Pie ^_^
 */
public class FSUtilities
implements JavaNamespace
{
	public static final File UserUUIDBase = new File(System.getProperty("user.home"), ".uuid");
	
	
	
	
	
	
	
	public static File fnull(String path)
	{
		return path == null ? null : new File(path);
	}
	
	public static File fnex(String path)
	{
		return path == null ? NonExistantFile.inst() : new File(path);
	}
	
	public static class NonExistantFile
	extends File
	{
		private static final long serialVersionUID = 1L;
		
		
		protected static final NonExistantFile I = new NonExistantFile();
		
		public static NonExistantFile inst()
		{
			return I;
		}
		
		protected NonExistantFile()
		{
			super("");
		}
		
		@Override
		public String getName()
		{
			return "<non-existant>";
		}
		
		@Override
		public String getParent()
		{
			return null;
		}
		
		@Override
		public File getParentFile()
		{
			return null;
		}
		
		@Override
		public String getPath()
		{
			return "<non-existant>";
		}
		
		@Override
		public boolean isAbsolute()
		{
			return true;
		}
		
		@Override
		public String getAbsolutePath()
		{
			return getAbsoluteFile().getPath();
		}
		
		@Override
		public File getAbsoluteFile()
		{
			return this;
		}
		
		@Override
		public String getCanonicalPath() throws IOException
		{
			return getCanonicalFile().getPath();
		}
		
		@Override
		public File getCanonicalFile() throws IOException
		{
			return this;
		}
		
		@Deprecated
		@Override
		public URL toURL() throws MalformedURLException
		{
			return null; //if this causes problems, then worry about it then
		}
		
		@Override
		public URI toURI()
		{
			return null; //if this causes problems, then worry about it then
		}
		
		@Override
		public boolean canRead()
		{
			return false;
		}
		
		@Override
		public boolean canWrite()
		{
			return false;
		}
		
		@Override
		public boolean exists()
		{
			return false;
		}
		
		@Override
		public boolean isDirectory()
		{
			return false;
		}
		
		@Override
		public boolean isFile()
		{
			return false;
		}
		
		@Override
		public boolean isHidden()
		{
			return false; //dummy value
		}
		
		@Override
		public long lastModified()
		{
			return 0; //dummy value
		}
		
		@Override
		public long length()
		{
			return 0; //if 404, return 0, which isn't really a good behavior; dummy value
		}
		
		@Override
		public boolean createNewFile() throws IOException
		{
			throw new IOException("no such file or directory: "+this);
		}
		
		@Override
		public boolean delete()
		{
			return false; //fail
		}
		
		@Override
		public void deleteOnExit()
		{
			//pass;
		}
		
		@Override
		public String[] list()
		{
			return null; //not-a-directory
		}
		
		@Override
		public String[] list(FilenameFilter filter)
		{
			return null; //not-a-directory
		}
		
		@Override
		public File[] listFiles()
		{
			return null; //not-a-directory
		}
		
		@Override
		public File[] listFiles(FilenameFilter filter)
		{
			return null; //not-a-directory
		}
		
		@Override
		public File[] listFiles(FileFilter filter)
		{
			return null; //not-a-directory
		}
		
		@Override
		public boolean mkdir()
		{
			return false; //fail
		}
		
		@Override
		public boolean mkdirs()
		{
			return false; //fail
		}
		
		@Override
		public boolean renameTo(File dest)
		{
			return false; //fail
		}
		
		@Override
		public boolean setLastModified(long time)
		{
			return false; //fail
		}
		
		@Override
		public boolean setReadOnly()
		{
			return false; //fail
		}
		
		@Override
		public boolean setWritable(boolean writable, boolean ownerOnly)
		{
			return false; //fail
		}
		
		@Override
		public boolean setWritable(boolean writable)
		{
			return false; //fail
		}
		
		@Override
		public boolean setReadable(boolean readable, boolean ownerOnly)
		{
			return false; //fail
		}
		
		@Override
		public boolean setReadable(boolean readable)
		{
			return false; //fail
		}
		
		@Override
		public boolean setExecutable(boolean executable, boolean ownerOnly)
		{
			return false; //fail
		}
		
		@Override
		public boolean setExecutable(boolean executable)
		{
			return false; //fail
		}
		
		@Override
		public boolean canExecute()
		{
			return false;
		}
		
		@Override
		public long getTotalSpace()
		{
			return 0;
		}
		
		@Override
		public long getFreeSpace()
		{
			return 0;
		}
		
		@Override
		public long getUsableSpace()
		{
			return 0;
		}
		
		@Override
		public int compareTo(File pathname)
		{
			if (equals(pathname))
				return 0;
			else
				return -1; //non-existant < extant
		}
		
		@Override
		public boolean equals(Object obj)
		{
			return obj.getClass() == this.getClass();
		}
		
		@Override
		public int hashCode()
		{
			return toString().hashCode();
		}
		
		@Override
		public String toString()
		{
			return "<nonexistant file>";
		}
	}
	
	
	
	//Todo unit test these
	
	/**
	 * Normalize a path by making it absolute and removing '..'s and '.'s, and duplicate and trailing '/'s
	 */
	public static File normpath(File f)
	{
		return new File(normpathPosix(f.getAbsolutePath(), File.separatorChar));
	}
	
	/**
	 * Like {@link #normpath(File)} but doesn't make it absolute :3
	 * NOTE: this does NOT do anything with ".."'s that go above the implicit base directory!  (whatever it's relative to; eg, the one that would be referred to if the entire path was just "."   so if the entire path is eg "..", we'll just leave it as that X3 )
	 */
	public static File normrelpath(File f)
	{
		return new File(normpathPosix(f.getPath(), File.separatorChar));
	}
	
	
	
	
	public static boolean isStrictPath(File f)
	{
		return isRoot(f) || isStrictPathPosix(f.getPath(), File.separatorChar);
	}
	
	public static boolean isStrictPathPosix(String s)
	{
		return isStrictPathPosix(s, '/');
	}
	
	/**
	 * Note that "" is okay while "." is not!
	 */
	public static boolean isStrictPathPosix(String s, char c)
	{
		if (eq(s, new String(new char[]{c})))  //root (the only time something coming after a slash can be an empty element) :3
		{
			return true;
		}
		else
		{
			List<String> es = asList(split(s, c, -1, WhatToDoWithEmpties.LeaveInEmpties));
			
			String e0 = es.get(0);
			List<String> r = es.subList(1, es.size());
			
			Predicate<String> bad0 = e -> eq(e, ".") || eq(e, "..");
			Predicate<String> badR = e -> eq(e, ".") || eq(e, "..") || e.isEmpty();
			
			return !(bad0.test(e0) || forAny(badR, r));
		}
	}
	
	
	/**
	 * On windows, "C:\\" (C:\) isn't considered a relative path, so this returns false (and also for "C:", since a trailing separator always invalidates any concept of "strict" X3 )
	 */
	public static boolean isStrictNonemptyRelativePath(File f)
	{
		return !isRoot(f) && isStrictNonemptyRelativePathPosix(f.getPath(), File.separatorChar);
	}
	
	public static boolean isStrictNonemptyRelativePathPosix(String s)
	{
		return isStrictNonemptyRelativePathPosix(s, '/');
	}
	
	/**
	 * Note that neither "" nor "." is okay!
	 */
	public static boolean isStrictNonemptyRelativePathPosix(String s, char c)
	{
		List<String> es = asList(split(s, c, -1, WhatToDoWithEmpties.LeaveInEmpties));
		Predicate<String> bad = e -> eq(e, ".") || eq(e, "..") || e.isEmpty();
		return !forAny(bad, es);
	}
	
	
	
	
	
	
	
	public static String normpathPosix(String f)
	{
		return normpathPosix(f, '/');
	}
	
	/**
	 * Normalize an absolute path by removing '..'s and '.'s, and trailing slashes.
	 * (no system calls used, but implicitly assumes POSIX style root directory and absolute pathnames beginning with the separator char)
	 */
	public static String normpathPosix(String f, char separator)
	{
		//Note: this is used by the archive (edit: you mean ARC1??)
		//NOTE: THIS IS USED IN SECURITY BREACH DETECTING CODE IN FTPD AND SUCH!!!!
		
		
		
		if (f.isEmpty())
			return "";
		
		
		boolean absolute = f.charAt(0) == separator;
		
		if (absolute)
			f = f.substring(1);
		
		
		f = rtrim(f, separator);
		f = ltrimstr(f, "."+separator);
		
		if (eq(f, "."))
			return "";
		
		
		@WritableValue List<String> elements = new ArrayList<>(Arrays.asList(StringUtilities.split(f, separator, -1, WhatToDoWithEmpties.LeaveOutEmpties)));
		
		if (elements.size() == 1 && elements.get(0).isEmpty())
			elements = emptyList();
		else
		{
			int i = 0;
			while (i < elements.size())
			{
				String current = elements.get(i);
				
				if (current.isEmpty())
				{
					throw new AssertionError();
				}
				else if (eq(current, ".."))
				{
					if (i != 0 && !elements.get(i-1).equals(".."))
					{
						elements.remove(i);
						elements.remove(i-1);
						i--;
					}
					else
					{
						if (absolute)
						{
							//Todo configurable as to whether we throw an exception here or (as we currently do and the posix API does), merely truncate it!  :3
							elements.remove(i);
						}
						else
						{
							i++;
						}
					}
				}
				else
				{
					i++;
				}
			}
		}
		
		String mainpath = joinStrings(elements, separator);
		
		return absolute ? separator + mainpath : mainpath;
		
		
		
		
		
		
		
		//		String path = f;
		//
		//		//Lexically remove all occurrences of '.' or '..' as a directory
		//
		//		//TO DO actually remove all occurrences of '..'
		//		//TO DO actually remove all occurrences of '.'
		//		//TO DO respect generalized separator char
		//
		//
		//		//SPE ED way faster ._.
		//
		//		//Remove '.'s
		//		{
		//			String start = "."+separator;
		//			String end = separator+".";
		//			String interstitial = separator+"."+separator;
		//
		//			path = path.replace(interstitial, "");
		//
		//			while (true)
		//			{
		//				if (path.endsWith(end))
		//				{
		//					path = path.substring(0, path.length() - end.length());
		//					continue;
		//				}
		//
		//				if (path.startsWith(start))
		//				{
		//					path = path.substring(start.length());
		//					continue;
		//				}
		//
		//				break;
		//			}
		//		}
		//
		//
		//		if (path.contains("/./") || path.startsWith("./") || path.endsWith("/."))
		//			throw new NotYetImplementedException("Unsupported non-normalized path containing '.' directory: "+path);
		//
		//		if (path.contains("/../") || path.startsWith("../") || path.endsWith("/.."))
		//			throw new NotYetImplementedException("Unsupported non-normalized path containing '..' directory: "+path);
		//
		//		String newpath = path;
		//
		//		return newpath;
		
		/*
		String[] elements = StringUtilities.split(path, separator);
		
		//newElements is in reverse order (basename first)
		List<String> newElements = new ArrayList<String>(elements.length);
		
		/*
		 * a/b/c/../d/../../e.f	0
		 * a/b/c/../d/../e.f	1
		 * a/b/c/../d/e.f		2
		 * a/b/c/../e.f			1
		 * a/b/c/e.f			2
		 * a/b/e.f				1
		 * a/e.f				0
		 * 
		 * correct: a/e.f
		 */
		
		/*
		int dotdotCount = 0;
		for (int i = elements.length-1; i >= 0; i--)
		{
			String element = elements[i];
			
			//Skip "//" and "/." "./" "/./"
			if (element.isEmpty() || element.equals("."))
			{
				continue;
			}
			
			//Last (non-empty) one is the basename
			if (element.equals(".."))
			{
				//".." just increments the ddcount
				dotdotCount++;
			}
			else
			{
				if (dotdotCount > 0)
				{
					//just mark it by decrementing the ddcount, but skip it (since the ".." that followed it somewhere along the line consumed it)
					dotdotCount--;
				}
				else
				{
					//this time, add it
					newElements.add(element);
				}
				
				//note that a remaining ddcount once the root directory is reached is properly ignored
			}
		}
		
		String newpath = null;
		{
			if (newElements.isEmpty())
			{
				//Root
				newpath = String.valueOf(separator);
			}
			else
			{
				StringBuilder buff = new StringBuilder();
				
				//newElements is in reverse order (basename first)
				for (int i = newElements.size()-1; i >= 0; i--)
				{
					buff.append('/');
					buff.append(newElements.get(i));
					//notice no trailing slashes
				}
				
				newpath = buff.toString();
			}
		}
		
		return newpath;
		 */
	}
	
	/**
	 * Gets a file's path relative to a given ancestor such that {@link #joinPathsStrict(Object...) join}(<code>base</code>, <i>returnValue</i>) == <code>f</code>.
	 * similar to relpath(), contrast with abspath() ({@link File#getAbsolutePath()}) ^_^
	 * Except that the base is specified explicitly, rather than the Current Working Directory of the process :>
	 * Also, since this is the case (being system context independent), cwd-relative and absolute pathnames can't be mixed and matched! (sorries ._. )
	 * Use {@link #getRelativePath(File, File)} for that if you want :>  (since relative {@link File}'s are always relative to the system process cwd! ^_^, and easily abspath'ed ^^ )
	 * 
	 * join(relativeBase, [return-value]) == f
	 * 
	 * Note: canonicalization is NOT performed, but absolute..ification xD and normalization IS!
	 * 
	 * @return the relative file path or <code>null</code> if <code>base</code> is not an ancestor of <code>f</code> or <code>base == f</code> (soft equals).
	 */
	@Nullable
	public static String getRelativePath(File f, File relativeBase)
	{
		return getRelativePath(f.getAbsolutePath(), relativeBase.getAbsolutePath(), File.separatorChar);
	}
	
	
	@Nonnull
	public static String getRelativePathRequiring(File f, File relativeBase) throws NotFoundException
	{
		String rp = getRelativePath(f, relativeBase);
		
		if (rp == null)
			throw new NotFoundException(repr(f.getPath())+" is not inside "+repr(relativeBase.getPath()));
		else
			return rp;
	}
	
	
	
	
	public static boolean containsRealPath(File larger, File smaller)
	{
		return containsPath(realpath(larger), realpath(smaller));
	}
	
	public static boolean containsPath(File larger, File smaller)
	{
		//Todo optimize ^^'
		return getRelativePath(larger, smaller) != null;
	}
	
	
	@Nullable
	public static String getRelativePathInPosixSyntax(File f, File relativeBase)
	{
		String rp = getRelativePath(f, relativeBase);
		
		if (rp == null)
			return null;
		
		
		
		char s = File.separatorChar;
		
		if (s == '/')
		{
			return rp;
		}
		else
		{
			if (rp.indexOf('/') != -1)
				throw new ImpossibleException("The pathname on this OS legitimately contains forward-slashes!!!");
			
			return rp.replace(s, '/');
		}
	}
	
	/**
	 * @see #getRelativePath(File, File)
	 */
	@Nullable
	public static String getRelativePath(String f, String base, char separator)
	{
		if (f == null)
			return null; //parent-of-root is relative to no file
		else if (base == null)
			return f; //even root is itself relative to parent-of-root
		
		if (f.equals(base))
			return ".";
		
		f = normpathPosix(f, separator);
		base = normpathPosix(base, separator);
		
		if (f.equals(base))
		{
			return ".";
		}
		else
		{
			String baseplussep = isNormalizedRootPath(base, separator) ? base : base+separator;
			
			if (f.startsWith(baseplussep))
				return f.substring(baseplussep.length());
			else
				return null;
		}
	}
	
	public static boolean isNormalizedRootPath(String path, char sep)
	{
		return path.length() == 1 && path.charAt(0) == sep;
	}
	
	
	
	public static String dirnamePosix(String path)
	{
		//TODO do it properly X'D
		if (File.separatorChar != '/')
			throw new NotYetImplementedException();
		
		return new File(path).getParent();
	}
	
	
	
	public static String getFirstPathElement(String path, char sep)
	{
		//Todo use a splitonce..()?
		int i = path.indexOf(sep);
		return i == -1 ? path : path.substring(0, i);
	}
	
	public static String getAllButFirstPathElement(String path, char sep)
	{
		//Todo use a splitonce..()?
		path = trim(path, sep);
		int i = path.indexOf(sep);
		return i == -1 ? path : path.substring(i+1);
	}
	
	public static String[] splitPathAtFirstOrNullIfNone(String path, char sep)
	{
		path = trim(path, sep);
		return splitonceOrNull(path, sep);
	}
	
	public static String[] splitPathAtLastOrNullIfNone(String path, char sep)
	{
		path = trim(path, sep);
		return rsplitonceOrNull(path, sep);
	}
	
	
	/**
	 * Ie, basically <code>concat({@link StringUtilities#split(String, char)}(path, delimiter)[0:-1])</code>, but faster :D
	 */
	@Nullable
	public static String getAllButLastPathElementOrNull(@Nonnull String path, char sep)
	{
		path = trim(path, sep);
		return rsplitonceReturnPrecedingOrNull(path, sep);
	}
	
	public static String getAllButLastPathElementOrEmpty(String path, char sep)
	{
		path = trim(path, sep);
		String r = getAllButLastPathElementOrNull(path, sep);
		return r == null ? "" : r;
	}
	
	
	/**
	 * Ie, <code>{@link StringUtilities#split(String, char)}(path, delimiter)[.length-1]</code>, but faster :D
	 */
	@Nonnull
	public static String getLastPathElement(@Nonnull String path, char delimiter)
	{
		//Todo use a splitonce..()?
		int dotpos = path.lastIndexOf(delimiter);
		if (dotpos == -1)
			return path;
		else
			return path.substring(dotpos+1);
	}
	
	
	
	
	
	
	
	public static String getFirstPathElement(String path)
	{
		return getFirstPathElement(path, File.separatorChar);
	}
	
	public static String getAllButFirstPathElement(String path)
	{
		return getAllButFirstPathElement(path, File.separatorChar);
	}
	
	public static String[] splitPathAtFirstOrNullIfNone(String path)
	{
		return splitPathAtFirstOrNullIfNone(path, File.separatorChar);
	}
	
	
	
	public static String getFirstPathElementPosix(String path)
	{
		return getFirstPathElement(path, '/');
	}
	
	public static String getAllButFirstPathElementPosix(String path)
	{
		return getAllButFirstPathElement(path, '/');
	}
	
	public static String[] splitPathAtFirstOrNullIfNonePoxis(String path)
	{
		return splitPathAtFirstOrNullIfNone(path, '/');
	}
	
	
	
	
	
	
	public static String getLastPathElement(String path)
	{
		return getLastPathElement(path, File.separatorChar);
	}
	
	public static String getAllButLastPathElementOrEmpty(String path)
	{
		return getAllButLastPathElementOrEmpty(path, File.separatorChar);
	}
	
	public static String[] splitPathAtLastOrNullIfNone(String path)
	{
		return splitPathAtLastOrNullIfNone(path, File.separatorChar);
	}
	
	
	
	public static String getLastPathElementPosix(String path)
	{
		return getLastPathElement(path, '/');
	}
	
	public static String getAllButLastPathElementOrEmptyPosix(String path)
	{
		return getAllButLastPathElementOrEmpty(path, '/');
	}
	
	public static String[] splitPathAtLastOrNullIfNonePoxis(String path)
	{
		return splitPathAtLastOrNullIfNone(path, '/');
	}
	
	
	
	
	
	/**
	 * Also see JavaUtilities.getPrincipalSourceClassnameByRelativePath(String) for getting classnames from java source file names!! :DD
	 * 
	 * @return The intenal pathnames or <code>null</code> iff it's not inside one of the given directories!!
	 */
	@Nullable
	public static ContainingFolderAndRelativePathWithinIt getRelativePathFromMultipleBases(File f, Iterable<File> relativeBases)
	{
		for (File relativeBase : relativeBases)
		{
			String relativePath = getRelativePath(f, relativeBase);
			
			if (relativePath != null)
				return new ContainingFolderAndRelativePathWithinIt(relativeBase, relativePath);
		}
		
		return null;
	}
	
	@Nullable
	public static ContainingFolderAndRelativePathWithinIt getRelativePathFromMultipleBases(File f, File... relativeBases)
	{
		return getRelativePathFromMultipleBases(f, Arrays.asList(relativeBases));
	}
	
	
	
	
	
	
	
	
	
	
	
	@Nonnull
	public static String getFilenameStem(@Nonnull File file)
	{
		return getFilenameStemFromBasename(file.getName());
	}
	
	@Nonnull
	public static String getFilenameStem(@Nonnull String path, char separatorChar)
	{
		requireNonNull(path);
		
		int dotPos = path.lastIndexOf('.');
		int slashPos = path.lastIndexOf(separatorChar);
		
		int beginningPos = slashPos == -1 ? 0 : slashPos;
		int endPos = dotPos != -1 && (slashPos == -1 || dotPos > slashPos) ? dotPos : path.length();
		
		return path.substring(beginningPos, endPos);  //note: it shortcuts inside String.substring() to 'this' if appropriate ^_^
	}
	
	@Nonnull
	public static String getFilenameStem(@Nonnull String path)
	{
		return getFilenameStem(path, File.separatorChar);
	}
	
	@Nonnull
	public static String getFilenameStemFromBasename(@Nonnull String basename)
	{
		requireNonNull(basename);
		
		int dotPos = basename.lastIndexOf('.');
		
		if (dotPos == -1)
			return basename;
		else
			return basename.substring(0, dotPos);
	}
	
	
	
	
	public static String getPathWithoutSuffix(File file)
	{
		return getPathWithoutSuffix(file.getPath());
	}
	
	/**
	 * Basically, join(dirname, stem) :>
	 */
	public static String getPathWithoutSuffix(String path, char separatorChar)
	{
		if (path == null)
			return null;
		
		int dotPos = path.lastIndexOf('.');
		
		if (dotPos == -1)
			return path;
		else
		{
			int slashPos = path.lastIndexOf(separatorChar);
			
			if (slashPos == -1)
				return path.substring(0, dotPos);
			else
			{
				if (dotPos < slashPos) //then it's a dot in some directory's name!  Don't count it!!  0,0   (:>)
					return path;
				else
					return path.substring(0, dotPos);
			}
		}
	}
	public static String getPathWithoutSuffix(String path)
	{
		return getPathWithoutSuffix(path, File.separatorChar);
	}
	
	
	
	
	
	@Nullable
	public static String getFilenameExtension(File file)
	{
		return getFilenameExtension(file.getName()); //fasters to just right use the basename! ^_^
	}
	
	/**
	 * Doesn't include the '.'
	 * null if not present
	 */
	@Nullable
	public static String getFilenameExtension(String path, char separatorChar)
	{
		if (path == null)
			return null;
		
		int dotPos = path.lastIndexOf('.');
		
		if (dotPos == -1)
			return null;
		else
		{
			int slashPos = path.lastIndexOf(separatorChar);
			if (slashPos != -1 && dotPos < slashPos)
			{
				//dotPos = -1;
				return null;
			}
			else
			{
				return path.substring(dotPos+1);
			}
		}
	}
	
	@Nullable
	public static String getFilenameExtension(String path)
	{
		return getFilenameExtension(path, File.separatorChar);
	}
	
	
	
	
	
	
	@Nonnull
	public static String getFilenameSuffix(File file)
	{
		return getFilenameSuffixFromBasename(file.getName()); //faster to just right use the basename! ^_^
	}
	
	@Nonnull
	public static String getFilenameSuffix(String path)
	{
		return getFilenameSuffix(path, File.separatorChar);
	}
	
	/**
	 * Does include the '.'
	 * "" if not present
	 */
	@Nonnull
	public static String getFilenameSuffix(String path, char separatorChar)
	{
		if (path == null)
			return null;
		
		int dotPos = path.lastIndexOf('.');
		
		if (dotPos == -1)
			return "";
		else
		{
			int slashPos = path.lastIndexOf(separatorChar);
			if (slashPos != -1 && dotPos < slashPos)
			{
				//dotPos = -1;
				return "";
			}
			else
			{
				return path.substring(dotPos);
			}
		}
	}
	
	@Nonnull
	public static String getFilenameSuffixFromBasename(String path)
	{
		if (path == null)
			return null;
		
		int dotPos = path.lastIndexOf('.');
		
		if (dotPos == -1)
			return "";
		else
		{
			return path.substring(dotPos);
		}
	}
	
	
	
	
	public static boolean isPathAbsolute(String path)
	{
		return new File(path).isAbsolute();
	}
	
	
	
	
	
	
	
	public static File resolvePath(String pathname)  //For completeness :33'
	{
		return new File(pathname).getAbsoluteFile();
	}
	
	public static File resolvePath(File currentDirectory, String pathname)
	{
		//return resolvePath(currentDirectory, new File(pathname));   //java.io.File is safe to use with relative pathnames here, even though technically it uses the global CurrentDirectory ^^'
		
		return resolvePath(currentDirectory, new File(pathname));
	}
	
	/**
	 * Eg, for getting absolute paths from symlink targets (like you get from {@link #readlinkRaw(File)}) :>
	 * Since {@link File#getAbsoluteFile()} would resolve them relative to whatever our entire JVM process' Current Working Directory arbitrarily happened to be instead of relative to that specific symlink!
	 */
	public static File resolvePath(File currentDirectory, File pathname)
	{
		return pathname.isAbsolute() ? pathname : new File(currentDirectory, pathname.getPath());
	}
	
	
	
	
	
	
	
	//TODO Unit tests explicitly for these isLegalNonspecialPath..Element..'s
	
	public static boolean isLegalNonspecialPathStartElement(String n)
	{
		//Todo include extra unnecessarily-illegal chars that windows requires (if we're running on windows!)
		if (isLegalNonspecialPathStartElementPosixlike(n, File.separatorChar))
			return true;
		
		//absolute paths can have separators if they're the first element!
		//this accomodates non-posix platforms ^^'
		if (new File(n).isAbsolute())
			return true;
		
		return false;
	}
	
	public static boolean isLegalNonspecialPathElement(String n)
	{
		//Todo include extra unnecessarily-illegal chars that windows requires (if we're running on windows!)
		return isLegalNonspecialPathElementPosixlike(n, File.separatorChar);
	}
	
	
	
	public static boolean isLegalNonspecialPathStartElementPosix(String n)
	{
		return isLegalNonspecialPathStartElementPosixlike(n, '/');
	}
	
	public static boolean isLegalNonspecialPathElementPosix(String n)
	{
		return isLegalNonspecialPathElementPosixlike(n, '/');
	}
	
	
	
	public static boolean isLegalNonspecialPathStartElementPosixlike(String n, char sep)
	{
		//return isLegalNonspecialPathElementPosixlike(ltrim(n, sep), sep);
		
		if (n.isEmpty() || eq(n, ".") || eq(n, ".."))
			return false;
		
		n = trim(n, sep);  // "somedir/" is okay and "/somedir" is okay for a start-element! (hence the trim() not rtrim()!)
		
		if (eq(n, ".") || eq(n, ".."))  //and empty here is okay for the start element (ie, it's all slashes, the root directory!)
			return false;
		
		return true;
	}
	
	public static boolean isLegalNonspecialPathElementPosixlike(String n, char sep)
	{
		if (n.isEmpty() || eq(n, ".") || eq(n, ".."))
			return false;
		
		n = rtrim(n, sep);  // "somedir/" is okay
		
		if (n.isEmpty() || eq(n, ".") || eq(n, ".."))
			return false;
		
		if (n.indexOf(sep) != -1)
			return false;
		
		return true;
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Strict means "../somedir", empty elements, absolute elements other than the first one, etc. aren't allowed.
	 * And a path separator (eg, "/") is only allowed on the first element!
	 * 
	 * @see #isLegalNonspecialPathElement(String)
	 */
	public static File joinPathsStrict(Object... pathElements)
	{
		return joinPathsStrictA(pathElements);
	}
	
	/**
	 * Lenient means "../somedir", empty elements, and etc. *are* allowed!
	 */
	public static File joinPathsLenient(Object... pathElements)
	{
		return joinPathsLenientA(pathElements);
	}
	
	
	
	public static File joinPathsStrictA(Object[] pathElements)
	{
		return joinPathsStrictC(asList(pathElements));
	}
	
	public static File joinPathsLenientA(Object[] pathElements)
	{
		return joinPathsLenientC(asList(pathElements));
	}
	
	
	public static File joinPathsStrictC(Iterable<?> pathElements)
	{
		return joinPathsC(true, pathElements);
	}
	
	public static File joinPathsLenientC(Iterable<?> pathElements)
	{
		return joinPathsC(false, pathElements);
	}
	
	
	
	
	/**
	 * Joins path elements together adding {@link File#separatorChar} 's as needed.
	 * @param pathElements is an array of <code>File</code>s and <code>String</code>s
	 */
	public static File joinPathsC(boolean strict, Iterable<?> pathElements)
	{
		File f = null;
		
		boolean first = true;
		for (Object e : pathElements)
		{
			String n = e instanceof File ? ((File)e).getPath() : (String)e;
			
			if (!strict && (n == null || n.isEmpty()))
				continue;
			
			if (strict)
			{
				// :>
				{
					if (first)
					{
						if (!isLegalNonspecialPathStartElement(n))
							throw new IllegalArgumentException("Illegal first path element in strict mode: "+repr(n));
					}
					else
					{
						if (!isLegalNonspecialPathElement(n))
							throw new IllegalArgumentException("Illegal non-first path element in strict mode: "+repr(n));
					}
				}
			}
			
			
			if (f == null)
			{
				f = e instanceof File ? (File)e : new File(n);  //no need remaking the File if 'n' came from one! X3
			}
			else
			{
				if (!strict)
				{
					if (forAll(c -> c == File.separatorChar, n))  //new File() won't trim slashes if n is *entirely slashes!* XD    (it will trim them down to one slash, but no more than that!)
						n = "";
				}
				
				f = new File(f, n);
			}
			
			first = false;
		}
		
		return f == null ? new File("") : f;
	}
	
	
	
	
	
	
	/**
	 * Joins path elements together adding and removing extraneous '/'s as needed.
	 * This is hard-coded to use the posix path name syntax, and so is suitable for URL's, etc.
	 */
	public static String joinPathsPosixStrict(String... pathElements)
	{
		return joinPathsPosix(true, pathElements);
	}
	
	public static String joinPathsPosixLenient(String... pathElements)
	{
		return joinPathsPosix(false, pathElements);
	}
	
	public static String joinPathsPosix(boolean strict, String... pathElements)
	{
		StringBuilder b = new StringBuilder();
		
		//This can't look ahead to see if we're really the last *nonempty* one!!
		//		int n = pathElements.length;
		//		for (int i = 0; i < n; i++)
		//		{
		//			String p = pathElements[i];
		//
		//			p = trim(p, '/');
		//
		//			if (!p.isEmpty())
		//			{
		//				b.append(p);
		//				if (i < n-1)
		//					b.append('/');
		//			}
		//		}
		
		boolean first = true;
		for (String n : pathElements)
		{
			if (n.isEmpty() || n.equals("."))
			{
				first = false;
				continue;
			}
			
			
			
			if (strict)
			{
				if (first)
				{
					if (!isLegalNonspecialPathStartElementPosix(n))
						throw new IllegalArgumentException("Illegal first path element in strict mode: "+repr(n));
				}
				else
				{
					if (!isLegalNonspecialPathElementPosix(n))
						throw new IllegalArgumentException("Illegal non-first path element in strict mode: "+repr(n));
				}
			}
			
			
			
			
			
			
			//Trim slashes
			{
				String nn = ltrimstrOrNull(n, "./");
				
				if (nn == null)
				{
					if (first)
						n = n.startsWith("/") ? '/' + trim(n, '/') : trim(n, '/');
						else
							n = trim(n, '/');
				}
				else
				{
					n = trim(nn, '/');
				}
			}
			
			
			
			//Actually append it! :D
			{
				if (n.isEmpty() || n.equals("."))
				{
					first = false;
					continue;
				}
				
				if (!first)
				{
					if (b.length() != 0 && !(b.length() == 1 && b.charAt(0) == '/'))
						b.append('/');
				}
				
				b.append(n);
			}
			
			first = false;
		}
		
		return b.toString();
	}
	
	
	
	
	public static String[] splitPath(File path)
	{
		String[] nominal = StringUtilities.split(path.getPath(), File.separatorChar, -1, WhatToDoWithEmpties.LeaveOutEmpties);
		
		//Todo deal with filesystem roots properly, not just by hardcoding in the most common ones ^^'
		if (path.getPath().startsWith("/"))
			return ArrayUtilities.concat1WithArray("/", nominal);
		if (path.getPath().startsWith("\\\\"))  //Windows filesharing root iirc! ^^
			return ArrayUtilities.concat1WithArray("\\\\", nominal);
		else
			return nominal;
	}
	
	public static String[] splitPath(String path, char pathSep)
	{
		String[] nominal = StringUtilities.split(path, pathSep, -1, WhatToDoWithEmpties.LeaveOutEmpties);
		
		if (path.startsWith("/"))
			return ArrayUtilities.concat1WithArray(String.valueOf(pathSep), nominal);
		else
			return nominal;
	}
	
	public static String[] splitPathPosix(String path)
	{
		return splitPath(path, '/');
	}
	
	
	
	
	
	public static String[] listNamesSorted(File dir)
	{
		String[] children = dir.list();
		Arrays.sort(children);
		return children;
	}
	
	public static File[] listSorted(File dir)
	{
		String[] childrenNames = listNamesSorted(dir);
		File[] children = new File[childrenNames.length];
		for (int i = 0; i < children.length; i++)
			children[i] = new File(dir, childrenNames[i]);
		return children;
	}
	
	
	
	
	
	/**
	 * Copies data from <code>src</code> into <code>dest</code>.
	 */
	public static void copyFile(File source, File dest) throws FileNotFoundException, IOException
	{
		FileInputStream in = null;
		FileOutputStream out = null;
		
		try
		{
			in = new FileInputStream(source);
			out = new FileOutputStream(dest);
			pump(in, out);
			out.close();
			in.close();
		}
		finally
		{
			if (in != null) closeWithoutError(in);
			if (out != null) closeWithoutError(out);
		}
		
		dest.setLastModified(source.lastModified());
	}
	
	public static void dumpToNewFile(InputStream in, File dest) throws IOException
	{
		if (lexists(dest))
			throw new IOException("File "+dest+" already exists.");
		
		FileOutputStream out = null;
		try
		{
			out = new FileOutputStream(dest);
			pump(in, out);
		}
		catch (IOException exc)
		{
			closeWithoutError(out);
			throw exc;
		}
		out.close();
	}
	
	/**
	 * Gives all classes inside <code>cp</code>. Example:<br>
	 * <code>rebound.util.Utils<br></code>
	 * <code>rebound.util.Sorter<br></code>
	 * <code>rebound.net.ServerThread<br></code>
	 * <code>rebound.net.event.ServerAcceptEvent<br></code>
	 */
	public static List<String> getClasses(ZipFile cp)
	{
		ArrayList<String> classes = new ArrayList<String>();
		Enumeration<? extends ZipEntry> e = cp.entries();
		while (e.hasMoreElements())
		{
			ZipEntry entry = e.nextElement();
			//Must be a file!
			if (!entry.isDirectory())
			{
				String name = entry.getName();
				
				if (name.endsWith(".class")) //Must be a class file
				{
					//Format...
					// '/' --> '.'
					name = name.replace('/', '.');
					// ".rebound.util.Utils.class" --> "rebound.util.Utils"
					name = name.substring(1, name.length() - 6); //6 = ".class".length()
					
					//...And add to return vector
					classes.add(name);
				}
			}
		}
		
		classes.trimToSize();
		return classes;
	}
	
	/**
	 * Gives all classes in <code>cp</code> example:<br>
	 * rebound.util.Utils<br>
	 * rebound.util.Sorter<br>
	 * rebound.net.ServerThread<br>
	 * rebound.net.event.ServerAcceptEvent<br>
	 */
	public static String[] getClasses(String cp, boolean recursive)
	{
		Vector rv = new Vector(8, 16);
		File CP = new File(cp);
		FilenameFilter cf = new FilenameFilter()
		{
			@Override
			public boolean accept(File dir, String name)
			{
				if (new File(dir, name).isDirectory())
					return false;
				int e = name.lastIndexOf(".");
				if (e == -1)
					return false;
				String ext = name.substring(e);
				return ext.equalsIgnoreCase(".class");
			}
		};
		
		String[] cfs = CP.list(cf);
		int i = 0;
		while (i < cfs.length)
		{
			cfs[i] = cfs[i].substring(0, cfs[i].length()-".class".length());
			i++;
		}
		
		rv.addAll(Arrays.asList(cfs));
		
		if (recursive)
		{
			FileFilter df = new FileFilter()
			{
				@Override
				public boolean accept(File pathname)
				{
					return pathname.isDirectory();
				}
			};
			File[] dirs = CP.listFiles(df);
			String[] currrv;
			i = 0;
			int e;
			while (i < dirs.length)
			{
				currrv = getClasses(dirs[i].getPath(), true);
				e = 0;
				while (e < currrv.length)
				{
					currrv[e] = dirs[i].getName()+"."+currrv[e];
					e++;
				}
				rv.addAll(Arrays.asList(currrv));
				i++;
			}
		}
		
		rv.trimToSize();
		return (String[])rv.toArray(new String[0]);
	}
	
	/**
	 * {@link FSUtilities#getClasses(String, boolean)} with recursive = true;
	 */
	public static String[] getClasses(String cp)
	{
		return getClasses(cp, true);
	}
	
	
	
	/**
	 * This method recursively deletes <code>f</code> if it is a directory otherwise this is identical to <code>{@link java.io.File#delete}</code>.
	 * @param f The <code>File</code> to delete
	 * @param tryAll If this is set, then even if a <code>File</code> is encountered that cannot be deleted it will skip to the next one. As opposed to failing upon the first failed deletion.
	 * @return <code>true</code> if all sub<code>File</code>s are deleted, otherwise <code>false</code>
	 * @see java.io.File
	 */
	public static boolean deleteRecursively(File f, boolean tryAll)
	{
		if (isSymlink(f))
		{
			return f.delete();
		}
		else if (f.isDirectory())
		{
			boolean allSuccess = true;
			
			File[] children = f.listFiles();
			boolean success = false;
			for (File c : children)
			{
				success = deleteRecursively(c, tryAll);
				if (!success && !tryAll)
					return false;
				allSuccess &= success;
			}
			
			if (allSuccess)
				allSuccess &= f.delete();
			
			return allSuccess;
		}
		else if (f.exists())
		{
			return f.delete();
		}
		else
		{
			//broken symlinks appear as non-existant
			f.delete();
			return true;
		}
	}
	
	
	
	public static boolean deleteAndCheck(File f)
	{
		boolean reportedSuccess = f.delete();
		return !reportedSuccess || !lexists(f);
	}
	
	public static void deleteMandatory(File f) throws UncheckedIOException
	{
		boolean success = deleteAndCheck(f);
		if (!success)
			throw new UncheckedIOException(new IOException("Could not delete file: "+f.getAbsolutePath()));
	}
	
	public static void deleteMandatoryIfExists(File f) throws UncheckedIOException
	{
		if (!lexists(f))
			return;
		deleteMandatory(f);
	}
	
	public static void deleteRecursivelyMandatory(File f, boolean tryAll) throws UncheckedIOException
	{
		boolean success = deleteRecursively(f, tryAll);
		if (!success)
			throw new UncheckedIOException(new IOException("Could not delete file: "+f.getAbsolutePath()));
	}
	
	
	
	
	/**
	 * This tries to move source to dest (move != copy) first by renaming, then by copying and deleting source if that fails.
	 * + Guaranteed to preserve symlinks exactly if source is (itself) a symlink (broken or not!).  Dest will be too and no transformation on target pathname will be done :3
	 * 		+ Note that this may break the symlink if it's a relative one X'D
	 * 
	 * Note: this will delete the destination if it exists (and throw an IOException if it can't).
	 * And thus if the move fails, the source might not have been fully moved but the dest was deleted.
	 * However, *source* will definitely not be deleted until after a completely successful move :)
	 */
	public static void move(File source, File dest) throws IOException
	{
		if (lexists(dest))
			dest.delete();
		
		if (lexists(dest))
			throw new IOException("Could not delete pre-existing destination: "+repr(dest.getAbsolutePath()));
		
		
		
		
		if (source.renameTo(dest))
			return;
		
		
		
		if (isSymlink(source))
		{
			File content = readlinkRaw(source);
			
			makelinkSymbolic(content, dest);
			
			dest.setLastModified(source.lastModified());
			
			//Do this last!!  So that we don't delete source if an exception occurred!!
			source.delete();
		}
		else if (source.isFile())
		{
			//Now resort to copy-delete
			dest.createNewFile();
			
			//Set length ahead of time so that if it fails, it fails quickly (from unavailable disk space), and for performance reasons
			{
				RandomAccessFile raf = new RandomAccessFile(dest, "rw");
				raf.setLength(source.length());
				raf.close();
			}
			
			FileInputStream in = null;
			FileOutputStream out = null;
			try
			{
				in = new FileInputStream(source);
				out = new FileOutputStream(dest);
				pump(in, out);
			}
			finally
			{
				//Always try to close the streams; and throw the main error, not a close() error.
				if (in != null) closeWithoutError(in);
				if (out != null) closeWithoutError(out);
			}
			
			dest.setLastModified(source.lastModified());  //Do this AFTER writing to it! XD!
			
			//Do this last!!  So that we don't delete source if an exception occurred!!
			source.delete();
		}
		else if (source.isDirectory())
		{
			//TODO!!!
			// NOTE: If we change this, remove the preemptive NYI in the UIDSFS-Complete dependent of this function :D    (perhaps do a reverse-search to find all the other dependents of this function!!)
			throw new NotYetImplementedException("NYI: Recursively copying directories across filesystems ^^''    (Source="+repr(source.getAbsolutePath())+", Dest="+repr(dest.getAbsolutePath())+")");
		}
		else if (source.exists())
		{
			throw new NotYetImplementedException("NYI: Moving special files (eg, devices and fifos) across filesystems ^^''    (Source="+repr(source.getAbsolutePath())+", Dest="+repr(dest.getAbsolutePath())+")");
		}
		else
		{
			throw new IOException("Tried to move a nonexistant file:   "+repr(source.getAbsolutePath()));
		}
	}
	
	
	
	/**
	 * + Guaranteed to try prefix+suffix first before resorting to mangling the name! XD  :D
	 * @return null if it can't find one!  Otherwise one that doesn't already {@link #lexists(File) exist}! :D   (or at least didn't right before we returned!  unix isn't deterministic within a single process you know! x'D )
	 */
	@Nullable
	public static File getUniqueFileOrNull(@Nonnull File dir, @Nullable String prefix, @Nullable String suffix)
	{
		boolean hasPrefix = prefix != null && prefix.length() > 0;
		boolean hasSuffix = suffix != null && suffix.length() > 0;
		
		int randTries = 0, addTries = 0, counter = 0;
		File uniq = null;
		StringBuilder nameBuff = new StringBuilder();
		Random random = new Random(System.nanoTime());
		
		boolean first = true;
		while (true)
		{
			//Get next number
			int number;
			{
				if (first)
				{
					number = random.nextInt();
					first = false;
				}
				else
				{
					if (randTries > 1024)
					{
						if (addTries > 2048)
							//I give up!
							return null;
						
						if (addTries == 0)
							counter = random.nextInt();  //start off with something random X3
						
						number = counter;
						counter++;  //overflow intended here!!
						addTries++;
					}
					else
					{
						number = random.nextInt();
						randTries++;
					}
				}
			}
			
			
			//Calculate name
			{
				nameBuff.setLength(0);
				
				if (hasPrefix)
					nameBuff.append(prefix);
				
				if (number != 0)
				{
					String hex = Integer.toHexString(number).toUpperCase();
					for (int i = 0; i < (8-hex.length()); i++)
						nameBuff.append('0');
					nameBuff.append(hex);
				}
				
				if (hasSuffix)
					nameBuff.append(suffix);
			}
			
			uniq = new File(dir, nameBuff.toString());
			
			if (!lexists(uniq))
			{
				return uniq;
			}
		}
	}
	
	/**
	 * @see #getUniqueFileOrNull(File, String, String)
	 */
	@Nonnull
	public static File getUniqueFileOrThrow(@Nonnull File dir, @Nullable String prefix, @Nullable String suffix) throws NoFreeResourceFoundException
	{
		File f = getUniqueFileOrNull(dir, prefix, suffix);
		
		if (f != null)
			return f;
		else
			throw new NoFreeResourceFoundException();  //Todo give details in the exc :3'
	}
	
	
	
	
	public static File requireExtension(File root, String desiredExtension)
	{
		if (root.getName().endsWith("."+desiredExtension))
			return root;
		else
			return new File(root.getParent(), root.getName()+"."+desiredExtension);
	}
	
	
	
	public static boolean compare_r(File a, File b) throws IOException
	{
		if (a.isFile())
		{
			if (!b.isFile())
				return false;
			
			return FSIOUtilities.compare(a, b);
		}
		else if (a.isDirectory())
		{
			if (!b.isDirectory())
				return false;
			
			//<Main logic
			String[] alist = a.list();
			String[] blist = b.list();
			Arrays.sort(alist);
			Arrays.sort(blist);
			if (!Arrays.equals(alist, blist))
				return false;
			
			//They must be equivalent at this point
			for (String c : alist)
			{
				if (!FSIOUtilities.compare(new File(a, c), new File(b, c)))
					return false;
			}
			
			//Iff none are mismatching, then all are matching
			return true;
			//Main logic>
		}
		else
		{
			if (b.isFile() || b.isDirectory())
				return false;
			
			return true;
		}
	}
	
	
	
	
	
	public static boolean isAncestor(File ancestorCandidate, File file)
	{
		File ca = ancestorCandidate.getAbsoluteFile();
		File cf = file.getAbsoluteFile();
		
		File p = cf;
		while (p != null)
		{
			if (p.equals(ca))
				return true;
			p = p.getParentFile();
		}
		return false;
	}
	
	
	
	
	
	
	
	
	//<String/File type conversion
	public static File[] getFileArray(String... filenames)
	{
		File[] files = new File[filenames.length];
		for (int i = 0; i < files.length; i++)
			files[i] = new File(filenames[i]);
		return files;
	}
	
	public static File[] getFileArray(File dir, String... basenames)
	{
		File[] files = new File[basenames.length];
		for (int i = 0; i < files.length; i++)
			files[i] = new File(dir, basenames[i]);
		return files;
	}
	
	/**
	 * Gets an array of file paths.<br>
	 */
	public static String[] getFilenameArray(File... files)
	{
		String[] filenames = new String[files.length];
		for (int i = 0; i < files.length; i++)
			filenames[i] = files[i].getPath();
		return filenames;
	}
	
	/**
	 * Gets an array of file basenames (eg /usr/bin/nmap --> nmap, /usr/bin/ --> bin)
	 */
	public static String[] getBasenameArray(File... files)
	{
		String[] filenames = new String[files.length];
		for (int i = 0; i < files.length; i++)
			filenames[i] = files[i].getName();
		return filenames;
	}
	
	
	
	
	
	
	
	public static File[] getFileArray(List<String> pathnames)
	{
		File[] files = new File[pathnames.size()];
		int i = 0;
		for (String pathname : pathnames)
			files[i++] = new File(pathname);
		return files;
	}
	
	public static List<File> getFileList(String[] pathnames)
	{
		return Arrays.asList(getFileArray(pathnames));
	}
	
	public static List<File> getFileList(List<String> pathnames)
	{
		return Arrays.asList(getFileArray(pathnames));
	}
	
	
	
	
	
	public static String[] getPathnameArray(File[] files)
	{
		String[] pathnames = new String[files.length];
		int i = 0;
		for (File file : files)
			pathnames[i++] = file.getPath();
		return pathnames;
	}
	
	public static String[] getPathnameArray(List<File> files)
	{
		String[] pathnames = new String[files.size()];
		int i = 0;
		for (File file : files)
			pathnames[i++] = file.getPath();
		return pathnames;
	}
	
	public static List<String> getPathnameList(File[] files)
	{
		return Arrays.asList(getPathnameArray(files));
	}
	
	public static List<String> getPathnameList(List<File> files)
	{
		return Arrays.asList(getPathnameArray(files));
	}
	//String/File type conversion>
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static enum FileTypeA
	{
		Absent,
		Directory,
		NormalOrSpecialFile,
	}
	
	public static enum FileTypeB
	{
		Absent,
		Directory,
		SpecialFile,
		NormalFile,
	}
	
	public static enum FileTypeC
	{
		Absent,
		Symlink,
		Directory,
		SpecialFile,
		NormalFile,
	}
	
	public static enum FileTypeD
	{
		Absent,
		Directory,
		SpecialFile,
		NormalFile,
		
		SymlinkBroken,
		SymlinkDirectory,
		SymlinkSpecialFile,
		SymlinkNormalFile,
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Just like isFile() and isDirectory(), this dereferences symbolic links, so it will return true for symlinks to special files as well :3
	 * + {@link File#isFile()} is really "isNormalFile()" by our naming convention here :3
	 * 
	 * (Btw, if you don't know, "special files" are commonly interacted with on Linux/POSIX/Unix/BSD systems where they can do all kinds of nifty things, like be FIFO's and Device Files and such! :D )
	 * (I've tested this on FIFO's, block devices, and character devices and they each work the same! :3 )
	 * 
	 * (And for the record, broken symlinks do NOT count as "Special File"'s here!)
	 */
	public static boolean isSpecialFile(File f)
	{
		requireNonNull(f);
		
		return f.exists() && !f.isFile() && !f.isDirectory();  //notice I use f.exists() not lexists(f) since the latter would include broken symlinks but the former excludes it! :>
	}
	
	
	
	/**
	 * This is simply a delegate for {@link File#isFile()} but serves the self-documenting-code
	 * purpose of helping to show the author really did mean to exclude special files!
	 */
	public static boolean isNormalFile(File f)
	{
		requireNonNull(f);
		
		return f.isFile();
	}
	
	
	
	/**
	 * Just like isFile() and isDirectory(), this dereferences symbolic links, so it will return true for symlinks to special files as well :3
	 * + {@link File#isFile()} is really "isNormalFile()" by our naming convention here :3
	 */
	public static boolean isSpecialOrNormalFile(File f)
	{
		requireNonNull(f);
		
		return f.exists() && !f.isDirectory();
	}
	
	
	
	
	
	
	
	//<Hardlinks! :D
	public static boolean samefile(File a, File b)
	{
		try
		{
			return Files.isSameFile(a.toPath(), b.toPath());
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static void makelinkHard(File immediateTarget, File pathForHardlink) throws IOException
	{
		if (!immediateTarget.isFile())
			throw new IOException();
		
		if (lexists(pathForHardlink))
			throw new IOException();
		
		//executeCheckingExitCode("ln", immediateTarget.getAbsolutePath(), pathForHardlink.getAbsolutePath());
		Files.createLink(pathForHardlink.toPath(), immediateTarget.toPath());
	}
	
	public static void makelinkHardOrCopy(File immediateTarget, File pathForHardlink) throws IOException
	{
		if (!immediateTarget.isFile())
			throw new IOException();
		
		if (lexists(pathForHardlink))
			throw new IOException();
		
		if (!pathForHardlink.getParentFile().isDirectory())
			throw new IOException();
		
		//		try
		//		{
		//			executeCheckingExitCode("ln", immediateTarget.getAbsolutePath(), pathForHardlink.getAbsolutePath());
		//		}
		//		catch (IOException exc)
		//		{
		//			if (pathForHardlink.isFile())
		//				throw exc;
		//			else
		//			{
		//				copy(immediateTarget, pathForHardlink);
		//			}
		//		}
		
		if (arePathsOnSameFileStore(immediateTarget, pathForHardlink.getParentFile()))
		{
			Files.createLink(pathForHardlink.toPath(), immediateTarget.toPath());
		}
		else
		{
			copy(immediateTarget, pathForHardlink);
		}
	}
	//Hardlinks!>
	
	
	
	
	//<Symlinks
	public static boolean isSymlink(File f)
	{
		requireNonNull(f);
		
		if (f instanceof NonExistantFile)
			return false;
		
		return Files.isSymbolicLink(f.toPath());
	}
	
	public static boolean lexists(File f)
	{
		requireNonNull(f);
		
		if (f instanceof NonExistantFile)
			return false;
		
		//f.exists() catches real nodes, extant symlinks, but not broken; isInParentDirectoryListing() catches all those plus broken symlinks, but is slower
		return f.exists() || isSymlink(f);
	}
	
	public static boolean isBrokenSymlink(File f)
	{
		requireNonNull(f);
		
		if (f instanceof NonExistantFile)
			return false;
		
		//broken links show up in directory listings, but not in exists()
		return !f.exists() && isSymlink(f);
	}
	
	public static boolean isExtantSymlink(File f)
	{
		requireNonNull(f);
		
		if (f instanceof NonExistantFile)
			return false;
		
		//broken links show up in directory listings, but not in exists()
		return f.exists() && isSymlink(f);
	}
	
	
	
	
	
	
	
	
	
	
	/**
	 * Note that the target relative symlinks will be returned exactly, not resolved relative to the containing directory!
	 * And so if you attempt to use that {@link File} as an actual {@link File}, then it will be resolved relative to *the JVM's current working directory and probably be wrong*!!
	 * So use {@link #readlinkAbsoluteRE(File)} if you want the logical *target* instead of the literal *contents* of a symbolic link :3
	 */
	public static File readlinkRawRE(File f) throws WrappedThrowableRuntimeException
	{
		requireNonNull(f);
		
		try
		{
			return readlinkRaw(f);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	/**
	 * Note that the target relative symlinks will be returned exactly, not resolved relative to the containing directory!
	 * And so if you attempt to use that {@link File} as an actual {@link File}, then it will be resolved relative to *the JVM's current working directory and probably be wrong*!!
	 * So use {@link #readlinkAbsolute(File)} if you want the logical *target* instead of the literal *contents* of a symbolic link :3
	 */
	public static File readlinkRaw(File f) throws IOException
	{
		requireNonNull(f);
		
		return Files.readSymbolicLink(f.toPath()).toFile();
	}
	
	
	public static File resolveSymlinkTarget(File symlinksTarget, File symlink)
	{
		if (symlinksTarget.isAbsolute())
		{
			return symlinksTarget;
		}
		else
		{
			File parent;
			{
				File root = new File("/");
				
				if (eq(symlink, root))
				{
					parent = root;
				}
				else
				{
					parent = symlink.getParentFile();
					
					if (parent == null)
						throw new ImPrettySureThisNeverActuallyHappensRuntimeException("Parent == null for this File: "+repr(symlink.getPath()));
				}
			}
			
			return joinPathsLenient(realpath(parent), symlinksTarget);
		}
	}
	
	
	
	
	
	public static File readlinkAbsoluteRE(File f) throws WrappedThrowableRuntimeException
	{
		requireNonNull(f);
		
		try
		{
			return readlinkAbsolute(f);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static File readlinkAbsolute(File f) throws IOException
	{
		requireNonNull(f);
		
		return resolveSymlinkTarget(readlinkRaw(f), f);
	}
	
	
	
	
	
	
	
	/**
	 * ln -s immediateTarget pathForSymlink
	 * :>
	 */
	public static void makelinkSymbolic(File immediateTarget, File pathForSymlink) throws IOException
	{
		requireNonNull(immediateTarget);
		requireNonNull(pathForSymlink);
		
		if (lexists(pathForSymlink))
			throw new IOException("Destination for symlink already exists: "+repr(pathForSymlink.getAbsolutePath()));
		
		Files.createSymbolicLink(pathForSymlink.toPath(), immediateTarget.toPath());
	}
	
	/**
	 * @return if we did anything (ie, created the link; false if it was already what we would make it) :3
	 */
	public static boolean makelinkSymbolicIfNotAlready(File immediateTarget, File pathForSymlink) throws IOException
	{
		requireNonNull(immediateTarget);
		requireNonNull(pathForSymlink);
		
		if (lexists(pathForSymlink))
		{
			if (isThisSymlinkPresent(immediateTarget, pathForSymlink))
				return false;  //Already what we would make :>
			else
				throw new IOException("Destination for symlink already exists: "+repr(pathForSymlink.getAbsolutePath()));
		}
		
		Files.createSymbolicLink(pathForSymlink.toPath(), immediateTarget.toPath());
		return true;
	}
	
	public static boolean isThisSymlinkPresent(File immediateTarget, File pathForSymlink) throws IOException
	{
		return isSymlink(pathForSymlink) && eq(readlinkRaw(pathForSymlink), immediateTarget);
	}
	
	public static boolean isRealpathEquivalentSymlinkPresent(File immediateTarget, File pathForSymlink) throws IOException
	{
		File immediateTargetAbsolute = resolveSymlinkTarget(immediateTarget, pathForSymlink);
		return isSymlink(pathForSymlink) && realpathEq(pathForSymlink, immediateTargetAbsolute);
	}
	
	
	public static void remakelinkSymbolic(File immediateTarget, File pathForSymlink) throws IOException
	{
		requireNonNull(immediateTarget);
		requireNonNull(pathForSymlink);
		
		File oldTarget = null;
		
		if (isSymlink(pathForSymlink))
		{
			oldTarget = readlinkRaw(pathForSymlink);
			pathForSymlink.delete();
			
			if (lexists(pathForSymlink))
				throw new IOException("Destination already exists and we couldn't get rid of it!  ("+repr(pathForSymlink.getAbsolutePath())+")");
		}
		else if (lexists(pathForSymlink))
		{
			throw new IOException("Destination already exists but isn't a symlink!  ("+repr(pathForSymlink.getAbsolutePath())+")");
		}
		
		
		
		
		pathForSymlink.delete();
		
		boolean success = false;
		try
		{
			makelinkSymbolic(immediateTarget, pathForSymlink);
			
			success = true;
		}
		finally
		{
			if (!success)
			{
				if (oldTarget != null)
				{
					makelinkSymbolic(oldTarget, pathForSymlink);
				}
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	public static boolean lexists_old(File f)
	{
		if (f instanceof NonExistantFile)
			return false;
		
		//f.exists() catches real nodes, extant symlinks, but not broken; isInParentDirectoryListing() catches all those plus broken symlinks, but is slower
		return f.exists() || isInParentDirectoryListing(f);
	}
	
	public static boolean isSymlink_old(File f)
	{
		if (f instanceof NonExistantFile)
			return false;
		
		return isBrokenSymlink(f) || isExtantSymlink(f);
	}
	
	public static boolean isBrokenSymlink_old(File f)
	{
		if (f instanceof NonExistantFile)
			return false;
		
		//broken links show up in directory listings, but not in exists()
		return !f.exists() && isInParentDirectoryListing(f);
	}
	
	public static boolean isExtantSymlink_old(File f)
	{
		if (f instanceof NonExistantFile)
			return false;
		
		f = normpath(f); //resolve "./hi" and "/hi/."
		String basename = f.getName();
		File namedParent = f.getParentFile();
		
		if (namedParent == null)
			return false;  //The/A root directory is certainly not a symlink! XDD
		
		File canonicalParent = realpath(namedParent);
		
		File fileInCanonicalParent = new File(canonicalParent, basename);
		File canonicalFile = realpath(fileInCanonicalParent); //note that I also could have used f.getCanonicalFile(), but I didn't.  Yep, that's the logic behind that.. I didn't.   (I don't think it matters)
		return !fileInCanonicalParent.equals(canonicalFile);
	}
	
	
	
	
	
	
	
	public static boolean isInParentDirectoryListing(File f)
	{
		if (f instanceof NonExistantFile)
			return false;
		
		f = f.getAbsoluteFile(); //otherwise the parent might not be computed correctly
		
		File parent = f.getParentFile();
		
		if (!parent.isDirectory())
			return false;
		else
		{
			String[] siblings = parent.list();
			
			if (siblings == null)
				throw new WrappedThrowableRuntimeException(new IOException("null directory listing, for file: "+StringUtilities.repr(f.getPath())+"     This usually means Permission Denied, I've found"));
			
			for (String s : siblings)
				if (s.equals(f.getName()))
					return true;
			return false;
		}
	}
	//Symlinks>
	
	
	
	
	
	
	
	
	//<Recursing
	
	
	public static final Predicate<File> DescendRecurse_Always = new Predicate<File>
	()
	{
		@Override
		public boolean test(File dir)
		{
			return true;
		}
	};
	
	//Not sure what the use of this one is, but it's here for completeness! XD
	public static final Predicate<File> DescendRecurse_Never = new Predicate<File>
	()
	{
		@Override
		public boolean test(File dir)
		{
			return false;
		}
	};
	
	public static final Predicate<File> DescendRecurse_IfReadable = new Predicate<File>
	()
	{
		@Override
		public boolean test(File dir)
		{
			return dir.canRead();
		}
	};
	
	public static final Predicate<File> DescendRecurse_NoSymlinks = new Predicate<File>
	()
	{
		@Override
		public boolean test(File dir)
		{
			return !isSymlink(dir);
		}
	};
	
	public static final Predicate<File> DescendRecurse_IfReadableAndNotSymlink = new Predicate<File>
	()
	{
		@Override
		public boolean test(File dir)
		{
			return dir.canRead() && !isSymlink(dir);
		}
	};
	
	
	
	
	//Todo postParents option, like the python version :>
	/**
	 * See docs on (better) Python version xD'
	 * @param descendRecurseFilter If this returns false, then we won't even descend into it recursively!  Much less will 'inclusion' filters in the observer have any affect on it! XP  :>
	 */
	public static void recurse(UnaryProcedure<File> observer, Predicate<File> descendRecurseFilter, Iterable<File> targets)
	{
		Collection<File> done = new HashSet<File>();
		Stack<File> todo = new Stack<File>();
		
		for (File target : targets)
		{
			observer.f(target);
			
			if (target.isDirectory())
			{
				done.add(realpath(target));
				
				if (descendRecurseFilter == null || descendRecurseFilter.test(target))  //null filter means RECURSE_FILTER_ALWAYS  >,>
				{
					todo.push(target);
				}
			}
			
			while (true)
			{
				if (todo.isEmpty())
					break;
				
				final File dir = todo.pop();
				
				final File[] files = dir.listFiles();
				
				if (files == null)
					throw new WrappedThrowableRuntimeException(new IOException("Listing directory failed for: "+repr(dir.getAbsolutePath())));
				
				for (final File f : sorted(files))
				{
					observer.f(f);  //Don't put this after "if (!done.contains(realpath(f)))" because they might *want* to see multiple symlinks to the same target! :D
					
					if (f.isDirectory())
					{
						final File fRP = realpath(f);
						
						if (!done.contains(fRP))  //This prevents us from falling into infinite recursion on encountering directory symlink cycles :3
						{
							if (descendRecurseFilter == null || descendRecurseFilter.test(f))  //null filter means RECURSE_FILTER_ALWAYS  >,>
							{
								done.add(fRP);  //MUST COME INSIDE BOTH IF BODIES!!
								todo.push(f);
							}
						}
					}
				}
			}
		}
	}
	public static void recurse(UnaryProcedure<File> observer, Iterable<File> targets)
	{
		recurse(observer, DescendRecurse_Always, targets);
	}
	
	
	public static void recurse(UnaryProcedure<File> observer, Predicate<File> descendRecurseFilter, File... targets)
	{
		recurse(observer, descendRecurseFilter, Arrays.asList(targets));
	}
	
	public static void recurse(UnaryProcedure<File> observer, File... targets)
	{
		recurse(observer, Arrays.asList(targets));
	}
	
	
	
	
	
	
	
	
	
	//(OLD) T O D O:  Handle cyclic trees of symlinks
	//
	//		for (File f : targets)
	//		{
	//			recurse(observer, filter, f, postParents);
	//		}
	//
	//	public static void recurse(RecurseObserver observer, UnaryFunctionObjectToBoolean<File> filter, File f, boolean postParents) throws IOException
	//	{
	//		if (f.isDirectory())
	//		{
	//			boolean recurse = filter.testRecurse(f);
	//
	//			if (recurse)
	//			{
	//				if (!postParents)
	//					observer.observe(f);
	//
	//				for (File c : f.listFiles())
	//					recurse(observer, filter, c, postParents);
	//
	//				if (postParents)
	//					observer.observe(f);
	//			}
	//			else
	//			{
	//				observer.observe(f);
	//			}
	//		}
	//		else
	//		{
	//			observer.observe(f);
	//		}
	//	}
	
	
	
	public static class AccumulatingObserver<C extends Collection<File>>
	implements UnaryProcedure<File>
	{
		protected Predicate<File> matcher;
		protected C collection;
		
		public AccumulatingObserver()
		{
		}
		
		public AccumulatingObserver(C collection)
		{
			this.setCollection(collection);
		}
		
		public AccumulatingObserver(Predicate<File> matcher, C collection)
		{
			this.matcher = matcher;
			this.collection = collection;
		}
		
		@Override
		public void f(File f)
		{
			if (this.matcher == null || this.matcher.test(f))  //Null matcher means Include_Always ^_^
				this.collection.add(f);
		}
		
		public C getCollection()
		{
			return this.collection;
		}
		
		public void setCollection(C collection)
		{
			this.collection = collection;
		}
		
		public Predicate<File> getMatcher()
		{
			return this.matcher;
		}
		
		public void setMatcher(Predicate<File> matcher)
		{
			this.matcher = matcher;
		}
	}
	
	
	
	//	public static final UnaryFunctionObjectToBoolean<File> Include_Always = (f) -> true;
	//
	//	//Not sure what the use of this one is, but it's here for completeness! XD
	//	public static final UnaryFunctionObjectToBoolean<File> Include_Never = (f) -> false;
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_NoSymlinks = f -> !isSymlink(f);
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyFiles = f -> f.isFile();
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyReadableFiles = f -> f.isFile() && f.canRead();
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyWritableFiles = f -> f.isFile() && f.canWrite();
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyReadableWritableFiles = f -> f.isFile() && f.canRead() && f.canWrite();
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyDirectories = f -> f.isDirectory();
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyReadableDirectories = f -> f.isDirectory() && f.canRead();
	//
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyFilesAndBrokenSymlinks = f -> f.isFile() && isBrokenSymlink(f);
	//
	//	public static final UnaryFunctionObjectToBoolean<File> Include_OnlyDirectoriesAndBrokenSymlinks = f -> f.isDirectory() && isBrokenSymlink(f);
	
	
	
	public static List<File> accumulateFiles(Predicate<File> descendRecurseFilter, Predicate<File> inclusionFilter, Iterable<File> targets)
	{
		AccumulatingObserver<List<File>> accumulator = new AccumulatingObserver<List<File>>(inclusionFilter, new ArrayList<File>());
		recurse(accumulator, descendRecurseFilter, targets);
		return accumulator.getCollection();
	}
	public static File[] accumulateFilesA(Predicate<File> descendRecurseFilter, Predicate<File> inclusionFilter, Iterable<File> targets)
	{
		List<File> l = accumulateFiles(descendRecurseFilter, inclusionFilter, targets);
		return l.toArray(new File[l.size()]);
	}
	
	
	public static List<File> accumulateFiles(Predicate<File> descendRecurseFilter, Predicate<File> inclusionFilter, File... targets)
	{
		AccumulatingObserver<List<File>> accumulator = new AccumulatingObserver<List<File>>(inclusionFilter, new ArrayList<File>());
		recurse(accumulator, descendRecurseFilter, targets);
		return accumulator.getCollection();
	}
	public static File[] accumulateFilesA(Predicate<File> descendRecurseFilter, Predicate<File> inclusionFilter, File... targets)
	{
		List<File> l = accumulateFiles(descendRecurseFilter, inclusionFilter, targets);
		return l.toArray(new File[l.size()]);
	}
	
	
	
	public static List<File> accumulateFiles(Iterable<File> targets)
	{
		AccumulatingObserver<List<File>> accumulator = new AccumulatingObserver<List<File>>(new ArrayList<File>());
		recurse(accumulator, targets);
		return accumulator.getCollection();
	}
	public static File[] accumulateFilesA(Iterable<File> targets)
	{
		List<File> l = accumulateFiles(targets);
		return l.toArray(new File[l.size()]);
	}
	
	
	public static List<File> accumulateFiles(File... targets)
	{
		AccumulatingObserver<List<File>> accumulator = new AccumulatingObserver<List<File>>(new ArrayList<File>());
		recurse(accumulator, targets);
		return accumulator.getCollection();
	}
	public static File[] accumulateFilesA(File... targets)
	{
		List<File> l = accumulateFiles(targets);
		return l.toArray(new File[l.size()]);
	}
	
	
	
	
	
	public static List<File> recurseAndExpandFoldersIntoJustFiles(Iterable<File> targets)
	{
		return accumulateFiles(DescendRecurse_Always, File::isFile, targets);
	}
	
	
	
	/**
	 * Remove all empty directories within 'target' ^_^
	 * @return number of empty directories removed :>
	 */
	public static int rmdirs(boolean followSymlinks, File... targets)
	{
		//TODO! Get the post-parents option in the Java version, and make this freakishly less wasteful!! 0,0
		
		final IntegerContainer totalEmptyDirsRemoved = new SimpleIntegerContainer();
		
		while (true)
		{
			int before = totalEmptyDirsRemoved.get();
			recurse(new UnaryProcedure<File>
			()
			{
				@Override
				public void f(File input)
				{
					if (input.isDirectory())
					{
						if (input.list().length == 0)
						{
							input.delete();
							totalEmptyDirsRemoved.set(totalEmptyDirsRemoved.get());
						}
					}
				}
			}, followSymlinks ? DescendRecurse_Always : DescendRecurse_NoSymlinks, targets);
			int after = totalEmptyDirsRemoved.get();
			
			if (after == before)
				break;
		}
		
		return totalEmptyDirsRemoved.get();
	}
	//Recursing>
	
	
	
	
	
	
	
	
	public static @Nonnull File realpathThrowing(@Nonnull File f) throws IOException
	{
		if (f.exists())
		{
			File r = f.getCanonicalFile();  //this. is very inconsistent in its behavior X'D
			
			if (r.exists() && !isSymlink(r))
			{
				File o = _realpathThrowing_ourImpl(f);
				if (!eq(r, o))
					throw new AssertionError("realpath("+repr(f.getPath())+"): "+repr(r.getPath())+" != "+repr(o.getPath()));
				
				return r;  //it was already a realpath! XD
			}
		}
		
		return _realpathThrowing_ourImpl(f);
	}
	
	@ImplementationTransparency
	public static File _realpathThrowing_ourImpl(File f) throws IOException
	{
		return _realpathThrowing_ourImpl(f, (l, t) -> resolveSymlinkTarget(t, l));
	}
	
	@ImplementationTransparency
	public static File _realpathThrowing_ourImpl(File f, LinkDereferencer linkDereferencingPredicate) throws IOException
	{
		Stack<File> s = new Stack<>();
		File r = _realpathThrowing_ourImpl(f, s, linkDereferencingPredicate);
		asrt(s.isEmpty());
		return r;
	}
	
	protected static File _realpathThrowing_ourImpl(File f, Stack<File> stack, LinkDereferencer linkDereferencingPredicate) throws IOException
	{
		f = normpath(f);
		
		File p = f.getParentFile();
		
		if (p == null) //it's the root directory!
		{
			return f;
		}
		else
		{
			File d = _realpathThrowing_ourImpl(p, stack, linkDereferencingPredicate);
			String n = f.getName();
			f = eq(n, ".") ? d : new File(d, n);
			
			if (isSymlink(f))
			{
				File it = readlinkRaw(f);
				
				File t = linkDereferencingPredicate.dereference(f, it);
				
				if (t != null)
				{
					if (stack.contains(f))  //we don't support case-insensitivity and symbolic links on the same filesystem ^^'
						throw new SymlinkCycleIOException("Symbolic link cycle detected!!: "+repr(f.getPath()));
					stack.add(f);  //do after checking contains of course! XD
					
					File r = _realpathThrowing_ourImpl(t, stack, linkDereferencingPredicate);
					
					asrt(stack.pop() == f);
					
					return r;
				}
				else
				{
					return f;
				}
			}
			else
			{
				return f;
			}
		}
	}
	
	
	public static interface LinkDereferencer
	{
		public @Nullable File dereference(@Nonnull File link, @Nonnull File immediateRawTarget) throws IOException;
	}
	
	
	
	
	/**
	 * On symlink cycles, this is eq(readlinkAbsolute(a), readlinkAbsolute(b)) if both are symlinks (if not it's just false XD )
	 * When neither have a symlink cycle, it's eq(realpath(a), realpath(b))
	 * :>
	 */
	public static boolean realpathEq(File a, File b)
	{
		File arp;
		File brp;
		
		try
		{
			try
			{
				arp = realpathThrowing(a);
				brp = realpathThrowing(b);
			}
			catch (SymlinkCycleIOException exc)
			{
				if (isSymlink(a))
				{
					if (isSymlink(b))
					{
						return eq(readlinkAbsolute(a), readlinkAbsolute(b));
					}
					else
					{
						return false;
					}
				}
				else
				{
					if (isSymlink(b))
					{
						return false;
					}
					else
					{
						return eq(a, b);
					}
				}
			}
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
		
		return eq(arp, brp);
	}
	
	public static @Nonnull File realpath(@Nonnull File f) throws WrappedThrowableRuntimeException
	{
		try
		{
			return realpathThrowing(f);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static Set<File> getCanonicalSetPathnames(Iterable<File> files)
	{
		HashSet<File> canonicalSet = new HashSet<File>();
		for (File f : files)
			canonicalSet.add(realpath(f));
		return canonicalSet;
	}
	
	public static Set<File> getCanonicalSetPathnames(File... files)
	{
		return getCanonicalSetPathnames(Arrays.asList(files));
	}
	
	
	
	public static Set<File> getAbsoluteSetPathnames(Iterable<File> files)
	{
		HashSet<File> canonicalSet = new HashSet<File>();
		for (File f : files)
			canonicalSet.add(abspath(f));
		return canonicalSet;
	}
	
	public static Set<File> getAbsoluteSetPathnames(File... files)
	{
		return getAbsoluteSetPathnames(Arrays.asList(files));
	}
	
	
	
	
	public static boolean realpathsEqual(File a, File b)
	{
		return eq(realpath(a), realpath(b));
	}
	
	
	
	
	
	
	
	public static File[] boxFiles(String... pathnames)
	{
		if (pathnames == null)
			return null;
		
		File[] boxed = new File[pathnames.length];
		for (int i = 0; i < pathnames.length; i++)
			boxed[i] = new File(pathnames[i]);
		return boxed;
	}
	
	public static List<File> boxFiles(Iterable<String> pathnames)
	{
		if (pathnames == null)
			return null;
		
		List<File> boxed = new ArrayList<File>();
		for (String pathname : pathnames)
			boxed.add(new File(pathname));
		return boxed;
	}
	
	
	public static String[] unboxFiles(File... files)
	{
		if (files == null)
			return null;
		
		String[] unboxed = new String[files.length];
		for (int i = 0; i < files.length; i++)
			unboxed[i] = files[i].getPath();
		return unboxed;
	}
	
	public static List<String> unboxFiles(Iterable<File> files)
	{
		if (files == null)
			return null;
		
		List<String> unboxed = new ArrayList<String>();
		for (File file : files)
			unboxed.add(file.getPath());
		return unboxed;
	}
	
	
	
	
	
	public static boolean isPathnameContainingPathSeparator(String path)
	{
		return path.contains(File.pathSeparator);
	}
	
	public static boolean isPathContainingPathSeparator(File path)
	{
		return path.getPath().contains(File.pathSeparator);
	}
	
	public static boolean isAnyPathnameContainingPathSeparator(String... paths)
	{
		for (String path : paths)
			if (path.contains(File.pathSeparator))
				return true;
		return false;
	}
	
	public static boolean isAnyPathContainingPathSeparator(File... paths)
	{
		for (File path : paths)
			if (path.getPath().contains(File.pathSeparator))
				return true;
		return false;
	}
	
	public static boolean isAnyPathnameContainingPathSeparator(Iterable<String> paths)
	{
		for (String path : paths)
			if (path.contains(File.pathSeparator))
				return true;
		return false;
	}
	
	public static boolean isAnyPathContainingPathSeparator(Iterable<File> paths)
	{
		for (File path : paths)
			if (path.getPath().contains(File.pathSeparator))
				return true;
		return false;
	}
	
	
	
	
	
	public static class PathnameContainsPathSeparatorException
	extends IllegalArgumentException
	{
		private static final long serialVersionUID = 1L;
		
		
		public PathnameContainsPathSeparatorException()
		{
			super("A pathname contains a path separator ><!");
		}
		
		public PathnameContainsPathSeparatorException(File THEHORRIBLYOFFENSIVEPATH)
		{
			super("Pathname contains a path separator ><!  "+StringUtilities.repr(THEHORRIBLYOFFENSIVEPATH.getPath()));
		}
		
		protected PathnameContainsPathSeparatorException(String THEHORRIBLYOFFENSIVEPATH)
		{
			super("Pathname contains a path separator ><!  "+StringUtilities.repr(THEHORRIBLYOFFENSIVEPATH));
		}
		
		
		public static PathnameContainsPathSeparatorException instForAGivenPathname(File THEHORRIBLYOFFENSIVEPATH)
		{
			return new PathnameContainsPathSeparatorException(THEHORRIBLYOFFENSIVEPATH);
		}
		
		public static PathnameContainsPathSeparatorException instForAGivenPathname(String THEHORRIBLYOFFENSIVEPATH)
		{
			return new PathnameContainsPathSeparatorException(THEHORRIBLYOFFENSIVEPATH);
		}
	}
	
	
	
	public static void checkPathnameDoesNotContainPathSeparator(String path) throws PathnameContainsPathSeparatorException
	{
		if (path.contains(File.pathSeparator))
			throw PathnameContainsPathSeparatorException.instForAGivenPathname(path);
	}
	
	public static void checkPathDoesNotContainPathSeparator(File path) throws PathnameContainsPathSeparatorException
	{
		if (!path.getPath().contains(File.pathSeparator))
			throw PathnameContainsPathSeparatorException.instForAGivenPathname(path);
	}
	
	public static void checkNoPathnameContainsPathSeparator(String... paths) throws PathnameContainsPathSeparatorException
	{
		for (String path : paths)
			if (path.contains(File.pathSeparator))
				throw PathnameContainsPathSeparatorException.instForAGivenPathname(path);
	}
	
	public static void checkNoPathContainsPathSeparator(File... paths) throws PathnameContainsPathSeparatorException
	{
		for (File path : paths)
			if (path.getPath().contains(File.pathSeparator))
				throw PathnameContainsPathSeparatorException.instForAGivenPathname(path);
	}
	
	public static void checkNoPathnameContainsPathSeparator(Iterable<String> paths) throws PathnameContainsPathSeparatorException
	{
		for (String path : paths)
			if (path.contains(File.pathSeparator))
				throw PathnameContainsPathSeparatorException.instForAGivenPathname(path);
	}
	
	public static void checkNoPathContainsPathSeparator(Iterable<File> paths) throws PathnameContainsPathSeparatorException
	{
		for (File path : paths)
			if (path.getPath().contains(File.pathSeparator))
				throw PathnameContainsPathSeparatorException.instForAGivenPathname(path);
	}
	
	
	
	
	
	
	/**
	 * Because apparently File.getAbsoluteFile() *always* creates a new instance ><! xD
	 */
	public static File abspath(File f)
	{
		return f.isAbsolute() ? f : f.getAbsoluteFile();
	}
	
	
	
	
	//Todo samefile!
	
	
	
	
	
	
	
	
	public static String[] split3(String f)
	{
		return split3(new File(f));
	}
	
	public static String[] split3(File f)
	{
		return new String[]{f.getParent(), getFilenameStem(f), getFilenameSuffix(f)};
	}
	
	
	
	
	public static String[] split2posix(String f)
	{
		int i = f.indexOf('/');
		return i == -1 ? new String[]{".", f} : new String[]{f.substring(0, i), f.substring(i+1)};
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	public static Set<File> findFilesWithCaseSensitivelyConflictingBasenames(Set<File> files)
	{
		List<String> basenames = mapToNewList(File::getName, files);
		
		Map<String, Integer> c = getCounts(basenames);
		
		
		Set<File> conflictings = new HashSet<>();
		
		for (File f : files)
			if (c.get(f.getName()) > 1)
				conflictings.add(f);
		
		return conflictings;
	}
	
	
	
	public static Set<File> findFilesWithCaseInsensitivelyConflictingBasenames(Set<File> files)
	{
		List<String> basenames = mapToNewList(f -> f.getName().toLowerCase(), files);
		
		Map<String, Integer> c = getCounts(basenames);
		
		
		Set<File> conflictings = new HashSet<>();
		
		for (File f : files)
			if (c.get(f.getName().toLowerCase()) > 1)
				conflictings.add(f);
		
		return conflictings;
	}
	
	
	
	
	
	public static Set<File> realpathAndUniquify(Iterable<File> paths)
	{
		Set<File> r = new HashSet<>();
		
		for (File p : paths)
		{
			r.add(realpath(p));
		}
		
		return r;
	}
	
	public static Set<File> abspathAndUniquify(Iterable<File> paths)
	{
		Set<File> r = new HashSet<>();
		
		for (File p : paths)
		{
			r.add(p.getAbsoluteFile());
		}
		
		return r;
	}
	
	
	
	
	
	
	
	
	
	
	
	/*
	 * Note that these do NOT consider equal paths to be "overlaps"!
	 *  (where the definition of "equal" depends on whether it canonicalizes or not ^^' )
	 */
	
	
	public static boolean anyOverlapsRealpathed(Collection<File> paths)
	{
		return anyOverlapsRealpathed(paths, null);
	}
	
	public static boolean anyOverlapsRealpathed(Collection<File> paths, @Nullable BinaryProcedure<File, File> overlapObserver)
	{
		paths = FSUtilities.realpathAndUniquify(paths);
		return anyOverlapsRaw(paths, overlapObserver);
	}
	
	
	
	
	public static boolean anyOverlapsRaw(Collection<File> paths)
	{
		return anyOverlapsRaw(paths, null);
	}
	
	public static boolean anyOverlapsRaw(Collection<File> paths, @Nullable BinaryProcedure<File, File> overlapObserver)
	{
		boolean has = false;
		
		for (File a : paths)
		{
			a = a.getAbsoluteFile();
			List<String> aSplit = asList(splitPath(a));
			
			for (File b : paths)
			{
				b = b.getAbsoluteFile();
				
				if (!eq(a, b))
				{
					List<String> bSplit = asList(splitPath(b));
					
					//The path splitting is necessary otherwise "folder/doc.pdf" would be considered to overlap with "folder/doc (2).pdf"  X'D
					//Only "/folder/" or "/folder" should overlap with "/folder/doc.pdf"! ;D
					if (startsWithLists(aSplit, bSplit))
					{
						if (overlapObserver != null)
							overlapObserver.f(a, b);
						
						has = true;
					}
				}
			}
		}
		
		return has;
	}
	
	
	
	
	
	
	
	public static boolean anyOverlapsRawPosix(Collection<String> paths)
	{
		return anyOverlapsRawPosix(paths, null);
	}
	
	public static boolean anyOverlapsRawPosix(Collection<String> paths, @Nullable BinaryProcedure<String, String> overlapObserver)
	{
		boolean has = false;
		
		for (String a : paths)
		{
			List<String> aSplit = asList(splitPathPosix(normpathPosix(a)));
			
			for (String b : paths)
			{
				if (!eq(a, b))
				{
					List<String> bSplit = asList(splitPathPosix(normpathPosix(b)));
					
					//The path splitting is necessary otherwise "folder/doc.pdf" would be considered to overlap with "folder/doc (2).pdf"  X'D
					//Only "/folder/" or "/folder" should overlap with "/folder/doc.pdf"! ;D
					if (startsWithLists(aSplit, bSplit))
					{
						if (overlapObserver != null)
							overlapObserver.f(a, b);
						
						has = true;
					}
				}
			}
		}
		
		return has;
	}
	
	
	
	
	
	
	
	public static interface WriterProcedure
	{
		public void write(OutputStream out) throws IOException;
	}
	
	
	public static void performSafeFileSystemWriteTwoStageAndCopy(File dest, WriterProcedure write) throws IOException
	{
		if (dest.isDirectory() || isBrokenSymlink(dest))
			throw new IOException("Tried to write into an invalid destination!: "+repr(dest.getAbsolutePath()));
		
		dest = realpath(dest);
		
		
		File temporary = getUniqueFileOrNull(dest.getParentFile(), dest.getName(), ".tmp");
		if (lexists(temporary))
			throw new IOException("Strange error while making temp file: "+repr(temporary.getAbsolutePath()));
		ensureEmptyFileThrowing(temporary);
		
		if (!temporary.canWrite())
		{
			temporary = File.createTempFile(dest.getName(), ".tmp");
			
			if (!temporary.canWrite())
				logBug();
		}
		
		
		try
		{
			try (OutputStream out = new FileOutputStream(temporary))
			{
				write.write(out);
			}
			
			
			//We won't reach this part if writing failed!
			
			//Transfer it into the final location! :D
			{
				deleteMandatoryIfExists(dest);
				renameMandatory(temporary, dest);
			}
		}
		finally
		{
			temporary.delete();
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	// NIO!! :DD
	
	//So it turns out they added something to java.io.File as well!  toPath()!  XDDDDDDD       Well that makes things easier!! 8D
	//	public static Path pathNIO(File file)
	//	{
	//		return FileSystems.getDefault().provider().getPath(file.toURI());
	//	}
	
	public static FileChannel openReadonlyNIO(File file) throws IOException
	{
		return FileSystems.getDefault().provider().newFileChannel(file.toPath(), singleton(StandardOpenOption.READ));
	}
	
	public static FileChannel openReadwriteNIO(File file) throws IOException
	{
		//These are the standard options just like new FileOutputStream(File) ^ww^
		return FileSystems.getDefault().provider().newFileChannel(file.toPath(), EnumSet.of(StandardOpenOption.READ, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING));
	}
	
	
	
	
	
	
	//Todo is there a better way than classhacking to get at UnixFileStore.entry.dir ??   X'D
	
	public static boolean arePathsOnSameFileStore(File a, File b)
	{
		return arePathsOnSameFileStore(a.toPath(), b.toPath());
	}
	
	public static boolean arePathsOnSameFileStore(Path a, Path b)
	{
		try
		{
			//return Files.getFileStore(a) == Files.getFileStore(b);
			return eq(Files.getFileStore(a), Files.getFileStore(b));
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	
	
	public static interface SimpleFileLock
	extends Closeable
	{
	}
	
	
	
	
	public static SimpleFileLock lockFile(File f, boolean blocking) throws IOException
	{
		return blocking ? lockFileBlocking(f) : lockFileNonblocking(f);
	}
	
	
	
	@Nonnull
	public static SimpleFileLock lockFileBlocking(File f) throws IOException
	{
		FileChannel c = FileChannel.open(f.toPath(), StandardOpenOption.WRITE);
		
		FileLock l = c.lock();
		
		requireNonNull(l);
		
		return new SimpleFileLock()
		{
			boolean closed = false;
			
			@Override
			public void close() throws IOException
			{
				if (!this.closed)
				{
					l.release();
					c.close();
					
					this.closed = true;  //do this after so they can keep trying to invoke it until it works  (is that a better way? idk ^^' )
				}
			}
		};
	}
	
	
	@Nullable
	public static SimpleFileLock lockFileNonblocking(File f) throws IOException
	{
		FileChannel c = FileChannel.open(f.toPath(), StandardOpenOption.WRITE);
		
		FileLock l = c.tryLock();
		
		return l == null ? null : new SimpleFileLock()
		{
			boolean closed = false;
			
			@Override
			public void close() throws IOException
			{
				if (!this.closed)
				{
					l.release();
					c.close();
					
					this.closed = true;  //do this after so they can keep trying to invoke it until it works  (is that a better way? idk ^^' )
				}
			}
		};
	}
	
	
	
	
	
	
	public static void doLockedOnFileBlockingOrNotIfNull(@Nullable File f, RunnableThrowingIOException r) throws IOException
	{
		if (f == null)
			r.run();
		else
			doLockedOnFileBlocking(f, r);
	}
	
	public static void doLockedOnFileBlocking(@Nonnull File f, RunnableThrowingIOException r) throws IOException
	{
		requireNonNull(f);
		try (SimpleFileLock l = lockFileBlocking(f))
		{
			r.run();
		}
	}
	
	
	/**
	 * If this returns false, r.run() will have never been called :>
	 * @return if the operation was performed or not.  false is only returned if the file is already locked by another process :3
	 */
	public static boolean doLockedOnFileNonblocking(File f, RunnableThrowingIOException r) throws IOException
	{
		try (SimpleFileLock l = lockFileNonblocking(f))
		{
			if (l == null)
			{
				return false;
			}
			else
			{
				r.run();
				return true;
			}
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Escapes forward slashes completely, using an escape syntax based on the backslash which normally needn't be used :3
	 * 
	 * NOTE: DO NOT CHANGE THIS SYNTAX, MAKE A NEW METHOD INSTEAD!!!
	 */
	public static String fsescape(String s)
	{
		return s.replace("\\", "\\e").replace("\u2044", "\\s").replace("/", "\u2044");
	}
	
	/**
	 * (De!)Escapes forward slashes completely, using an escape syntax based on the backslash which normally needn't be used :3
	 * 
	 * NOTE: DO NOT CHANGE THIS SYNTAX, MAKE A NEW METHOD INSTEAD!!!
	 */
	public static String fsdescape(String s)
	{
		return s.replace("\u2044", "/").replace("\\s", "\u2044").replace("\\e", "\\");
	}
	
	
	
	
	
	
	
	
	public static boolean doesPathContainStandardUpwardTraversalMetaElement(String path)
	{
		return path.equals("..") || path.contains("/../") || path.startsWith("../") || path.endsWith("/..");
	}
	
	
	
	
	
	
	
	
	
	
	public static boolean isInSetByRealpaths(Iterable<File> set, File f)
	{
		for (File c : set)
			if (realpathsEqual(f, c))
				return true;
		return false;
	}
	
	
	
	
	
	
	
	public static void ensureDirLeafThrowing(File d) throws IOException
	{
		if (d.isDirectory())  //it's important to dereference symlinks here :>
			return;
		else if (lexists(d))
			throw new IOException("Monkey wrench: We tried to make this a directory but it's already something else: "+repr(d.getAbsolutePath()));
		else
		{
			d.mkdir();
			
			if (d.isDirectory())
				return;
			else if (lexists(d))
				throw new IOException("Monkey wrench: We tried to make this a directory but it's already something else: "+repr(d.getAbsolutePath()));
			else
				throw new IOException("We tried to make this a directory but failed: "+repr(d.getAbsolutePath()));
		}
	}
	
	public static void ensureDirsWholePathThrowing(File d) throws IOException
	{
		if (d.isDirectory())  //it's important to dereference symlinks here :>
			return;
		else if (lexists(d))
			throw new IOException("Monkey wrench: We tried to make this a directory but it's already something else: "+repr(d.getAbsolutePath()));
		else
		{
			d.mkdirs();
			
			if (d.isDirectory())
				return;
			else if (lexists(d))
				throw new IOException("Monkey wrench: We tried to make this a directory but it's already something else: "+repr(d.getAbsolutePath()));
			else
				throw new IOException("We tried to make this a directory but failed: "+repr(d.getAbsolutePath()));
		}
	}
	
	
	public static void ensureRenameThrowing(File oldPath, File newPath) throws IOException
	{
		if (!lexists(oldPath))
		{
			throw new IOException("Couldn't rename: "+repr(oldPath.getAbsolutePath())+" to: "+repr(newPath.getAbsolutePath())+" because the source didn't exist!");
		}
		
		if (lexists(newPath))
		{
			throw new IOException("Couldn't rename: "+repr(oldPath.getAbsolutePath())+" to: "+repr(newPath.getAbsolutePath())+" because the destination already existed!");
		}
		
		if (!oldPath.renameTo(newPath))
		{
			throw new IOException("Couldn't rename: "+repr(oldPath.getAbsolutePath())+" to: "+repr(newPath.getAbsolutePath()));
		}
	}
	
	
	public static void ensureEmptyFileThrowing(File f) throws IOException
	{
		if (f.isFile() && f.length() == 0)
		{
			return;
		}
		else if (lexists(f))
		{
			throw new IOException("Monkey wrench: We tried to make this an empty file but it's already something else: "+repr(f.getAbsolutePath()));
		}
		else
		{
			f.createNewFile();
			
			if (f.isFile())
			{
				if (f.length() != 0)
					throw new IOException("Monkey wrench: We tried to make this an empty file but (unless the JRE or OS is breaking API standards), it seems like something started writing to it *as soon as we made it!!* X'D  : "+repr(f.getAbsolutePath()));
				
				return;
			}
			else if (lexists(f))
			{
				throw new IOException("Monkey wrench: We tried to make this an empty file but it's already something else: "+repr(f.getAbsolutePath()));
			}
			else
			{
				throw new IOException("We tried to make this an empty file but failed: "+repr(f.getAbsolutePath()));
			}
		}
	}
	
	public static void ensureFileThrowing(File f) throws IOException
	{
		if (f.isFile())
			return;
		else if (lexists(f))
			throw new IOException("Monkey wrench: We tried to make this an empty file but it's already something else: "+repr(f.getAbsolutePath()));
		else
		{
			f.createNewFile();
			
			if (f.isFile())
				return;
			else if (lexists(f))
				throw new IOException("Monkey wrench: We tried to make this an empty file but it's already something else: "+repr(f.getAbsolutePath()));
			else
				throw new IOException("We tried to make this an empty file but failed: "+repr(f.getAbsolutePath()));
		}
	}
	
	
	
	
	
	
	
	
	public static File createTempFileUnchecked(String prefix, String suffix) throws WrappedThrowableRuntimeException
	{
		try
		{
			return File.createTempFile(prefix, suffix);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	
	
	
	
	public static boolean canFileExist(File f)
	{
		if (lexists(f))
		{
			return true;  //It already does! XD
		}
		else
		{
			boolean lexistsAfter;
			
			try
			{
				f.createNewFile();
			}
			catch (IOException exc)
			{
				return false;
			}
			finally
			{
				lexistsAfter = lexists(f);
				
				if (lexistsAfter)
				{
					f.delete();
				}
			}
			
			return lexistsAfter;
		}
	}
	
	
	
	
	
	
	
	
	/**
	 * @param nameMaker 'f' will become whatever this gives for 0 and the rest will be shifted up :>
	 */
	public static void shiftFile(File f, UnaryFunctionIntToObject<String> nameMaker)
	{
		File d = f.getParentFile();
		
		int pastLast;
		{
			int i = 0;
			while (lexists(new File(d, nameMaker.f(i))))
				i++;
			pastLast = i;
		}
		
		for (int i = pastLast-1; i >= 0; i++)
		{
			String currentName = nameMaker.f(i);
			String nextName = nameMaker.f(i+1);
			
			File current = new File(d, currentName);
			File next = new File(d, nextName);
			
			renameMandatoryRE(current, next);
		}
		
		String nextName = nameMaker.f(0);
		File next = new File(d, nextName);
		renameMandatoryRE(f, next);
	}
	
	
	
	
	
	
	
	
	public static void renameMandatory(File file, File newPath) throws IOException
	{
		if (lexists(newPath))
			throw new IOException("Destination already exists!: Renaming "+repr(file.getAbsolutePath())+" -> "+repr(newPath.getAbsolutePath()));
		
		if (!file.renameTo(newPath))
			throw new IOException("Renaming "+repr(file.getAbsolutePath())+" -> "+repr(newPath.getAbsolutePath()));
	}
	
	public static void renameMandatoryRE(File file, File newPath) throws RuntimeException
	{
		try
		{
			renameMandatory(file, newPath);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	/**
	 * @param symlinks map from basenames of the symlinks to their targets :>
	 */
	public static void repopulateDirectoryWithSymlinksDeletingAllExtantSymlinksButFailingIfAnythingElseInside(File dir, Map<String, File> symlinks) throws IOException
	{
		if (lexists(dir))
		{
			ensureDirsWholePathThrowing(dir);
			clearDirectoryOfSymlinks(dir);
		}
		else
		{
			ensureDirsWholePathThrowing(dir);
		}
		
		for (Entry<String, File> e : symlinks.entrySet())
		{
			if (doesPathContainStandardUpwardTraversalMetaElement(e.getKey()))
				throw new IllegalArgumentException(e.getKey());
			
			File pathForSymlink = new File(dir, e.getKey());
			File immediateTarget = e.getValue();
			
			makelinkSymbolic(immediateTarget, pathForSymlink);
		}
	}
	
	
	
	public static void clearDirectoryOfSymlinks(File dir) throws IOException
	{
		clearDirectoryOfMatchingEmpties(dir, f -> isSymlink(f));
	}
	
	
	
	public static void clearDirectoryOfMatchingEmpties(File dir, Predicate<File> pattern) throws IOException
	{
		//Check first :>
		for (File c : dir.listFiles())
		{
			if (!pattern.test(c))
			{
				throw new IOException("Directory has non-matching file in it!!: "+repr(c.getAbsolutePath()));
			}
		}
		
		//Then actually do it! :D
		for (File c : dir.listFiles())
		{
			if (!pattern.test(c))  //But it doesn't hurt to re-check in case their computer (namely filesystem I/O) is being really slow XD'
			{
				throw new IOException("Directory has non-matching file in it!!: "+repr(c.getAbsolutePath()));
			}
			else
			{
				c.delete();
			}
		}
	}
	
	public static void deleteMatching(File dir, Predicate<File> pattern) throws IOException
	{
		for (File c : dir.listFiles())
		{
			if (pattern.test(c))
				deleteMandatory(c);
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static void createNewFileUnchecked(File f)
	{
		try
		{
			f.createNewFile();
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	
	
	
	
	
	
	/**
	 * Eg, if "/dir/something.pdf" exists, returns "/dir/something (2).pdf"  :3
	 */
	public static File getNextFreePath(File f)
	{
		if (!lexists(f))
		{
			return f;
		}
		else
		{
			int i = 2;
			
			if (isRoot(f))
				throw new IllegalArgumentException(repr(f.getAbsolutePath()));
			
			File d = f.getAbsoluteFile().getParentFile();
			requireNonNull(d);
			
			String n = f.getName();
			String stem = splitonceReturnPrecedingOrWhole(n, '.');
			String ext = splitonceReturnSucceedingOrNull(n, '.');
			
			while (true)
			{
				String nn = stem+" ("+i+")";
				if (ext != null)
					nn += '.' + ext;
				
				File t = new File(d, nn);
				
				if (!lexists(t))
					return t;
				else
					i++;
				
				if (i == 0)
					throw new OverflowException();
			}
		}
	}
	
	
	
	
	public static boolean isRoot(File f)
	{
		return forAny(r -> eq(f, r), File.listRoots());
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Truncate the path to as much as will fit (given pathnames and filenames both have maximum limits in most filesystems!)
	 * + Adds an ellipsis on the end of truncated names :3
	 */
	public static File getNextFreeMaxPath(final String sOrig, File dir)
	{
		return getMaxPath(sOrig, dir, s -> getNextFreePath(new File(dir, s)).getName(), s -> getNextFreePath(new File(dir, s+'…')).getName());
	}
	
	/**
	 * Truncate the path to as much as will fit (given pathnames and filenames both have maximum limits in most filesystems!)
	 * + Adds an ellipsis on the end of truncated names :3
	 */
	public static File getMaxPath(final String sOrig, File dir)
	{
		return getMaxPath(sOrig, dir, s -> s, s -> s+'…');
	}
	
	/**
	 * Truncate the path to as much as will fit (given pathnames and filenames both have maximum limits in most filesystems!)
	 */
	public static File getMaxPath(final String sOrig, File dir, UnaryFunction<String, String> originalPathnameModifier, UnaryFunction<String, String> truncatedPathnameModifier)
	{
		String s = sOrig;
		
		//Then try the whole URL XD
		File lp = new File(dir, originalPathnameModifier.f(s));
		
		//Then try the url truncated ^^'
		while (!canFileExist(lp) && !s.isEmpty())
		{
			s = s.substring(0, s.length() - 1);
			lp = new File(dir, truncatedPathnameModifier.f(s));
		}
		
		if (s.isEmpty())
			throw new WrappedThrowableRuntimeException(new IOException("The first character must have made it illegal!: "+repr(sOrig)));
		
		return lp;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Only normal files (and symlinks to normal files) are entered into the map, not directories, special files, symlinks to dirs or specials, nor broken/cyclical symlinks :3
	 * Symlinks to directories will be recurse like normal directories, though!
	 */
	public static Map<String, byte[]> readEntireDirectoryTreeFiles(File d, Predicate<File> p, int entryCountLimit, long fileSizeLimit)
	{
		Map<String, byte[]> rv = new HashMap<>();
		
		recurse(f ->
		{
			
			if (f.isFile() && p.test(f))
			{
				int n = rv.size();
				
				if (n >= entryCountLimit)
					throw new WrappedThrowableRuntimeException(new IOException("Number of files ("+n+") exceeded limit ("+entryCountLimit+") inside "+repr(d.getAbsolutePath())));
				
				String relpath = getRelativePath(f, d);
				
				if (relpath == null)
					throw new WrappedThrowableRuntimeException(new IOException("getRelativePath("+repr(f)+", "+repr(d)+") == null"));
				
				long l = f.length();
				if (l > fileSizeLimit)
					throw new WrappedThrowableRuntimeException(new IOException("File size ("+l+" bytes) exceeded limit ("+fileSizeLimit+" bytes) for "+repr(f.getAbsolutePath())));
				
				byte[] c;
				try
				{
					c = FSIOUtilities.readAll(f);
				}
				catch (IOException exc)
				{
					throw new WrappedThrowableRuntimeException(exc);
				}
				
				putNewMandatory(rv, relpath, c);
			}
			
		}, DescendRecurse_Always, d);
		
		return rv;
	}
	
	public static Map<String, byte[]> readEntireDirectoryTreeFiles(File d, int entryCountLimit, long fileSizeLimit)
	{
		return readEntireDirectoryTreeFiles(d, f -> true, entryCountLimit, fileSizeLimit);
	}
	
	
	
	
	
	
	public static void storeEntireDirectoryTreeFilesIntoEmptyParent(File parent, Map<String, byte[]> tree)
	{
		if (!parent.isDirectory())
			throw new WrappedThrowableRuntimeException(new IOException("Not a directory: "+repr(parent.getAbsolutePath())));
		
		if (parent.list().length != 0)
			throw new WrappedThrowableRuntimeException(new IOException("Not an empty directory: "+repr(parent.getAbsolutePath())));
		
		for (Entry<String, byte[]> e : tree.entrySet())
		{
			String r = e.getKey();
			
			if (r.contains(File.separator+".."+File.separator))  //this is not a security check!!  windows permits both forward and back slashes, so if this only checks for one, it's not being done properly!  (we'd really need joinPaths() somewhere in between lenient and strict ^^' )
				throw new WrappedThrowableRuntimeException(new IOException("Bad relative path; contains directory ascensions: "+repr(r)));
			
			File f = joinPathsLenient(parent, r);
			
			try
			{
				FSIOUtilities.writeAll(f, e.getValue());
			}
			catch (IOException exc)
			{
				throw new WrappedThrowableRuntimeException(exc);
			}
		}
	}
	
	
	public static void storeEntireDirectoryTreeFilesIntoParentThatIsEmptySaveForTheseFilenames(File parent, Map<String, byte[]> tree)
	{
		for (String k : reversed(sorted(tree.keySet())))  //reverse the sort so that children come before parents! :D
			new File(parent, k).delete();
		
		storeEntireDirectoryTreeFilesIntoEmptyParent(parent, tree);
	}
	
	
	
	
	public static boolean eqDirTrees(Map<String, byte[]> a, Map<String, byte[]> b)
	{
		return CollectionUtilities.defaultMapsEquivalent(a, b, (byte[] av, byte[] bv) -> Arrays.equals(av, bv));
	}
	
	
	
	
	
	
	
	
	
	public static MappedByteBuffer mmap(File f, long startInBytes, int sizeInBytes, boolean writeable) throws IOException
	{
		return mmap(f.toPath(), startInBytes, sizeInBytes, writeable);
	}
	
	public static MappedByteBuffer mmap(Path p, long startInBytes, int sizeInBytes, boolean writeable) throws IOException
	{
		requireNonNegative(sizeInBytes);
		requireNonNegative(sizeInBytes);
		
		/*
		 * From FileChannel.map():
		 * 
		 * <p> A mapping, once established, is not dependent upon the file channel
		 * that was used to create it.  Closing the channel, in particular, has no
		 * effect upon the validity of the mapping.
		 */
		
		try (FileChannel c = writeable ? FileChannel.open(p, StandardOpenOption.READ, StandardOpenOption.WRITE) : FileChannel.open(p, StandardOpenOption.READ))
		{
			return c.map(writeable ? MapMode.READ_WRITE : MapMode.READ_ONLY, startInBytes, sizeInBytes);
		}
	}
	
	
	
	
	
	
	
	public static void chx(File f) throws IOException
	{
		chmod(f, union(getmod(f), setof(
		PosixFilePermission.OWNER_EXECUTE,
		PosixFilePermission.GROUP_EXECUTE,
		PosixFilePermission.OTHERS_EXECUTE
		)));
	}
	
	public static void normperms(File f) throws IOException
	{
		if (f.isDirectory())
			setPermsToStandardX(f);
		else
			setPermsToStandardNX(f);
	}
	
	
	/**
	 * Most common settings for non-executable files.
	 */
	public static void setPermsToStandardNX(File f) throws IOException
	{
		chmod(f, setof(
		PosixFilePermission.OWNER_READ,
		PosixFilePermission.OWNER_WRITE,
		PosixFilePermission.GROUP_READ,
		PosixFilePermission.OTHERS_READ
		));
	}
	
	/**
	 * Most common settings for directories and executable files.
	 */
	public static void setPermsToStandardX(File f) throws IOException
	{
		chmod(f, setof(
		PosixFilePermission.OWNER_READ,
		PosixFilePermission.OWNER_WRITE,
		PosixFilePermission.OWNER_EXECUTE,
		PosixFilePermission.GROUP_READ,
		PosixFilePermission.GROUP_EXECUTE,
		PosixFilePermission.OTHERS_READ,
		PosixFilePermission.OTHERS_EXECUTE
		));
	}
	
	
	
	public static Set<PosixFilePermission> getmod(File f) throws IOException
	{
		return Files.getPosixFilePermissions(f.toPath());
	}
	
	public static void chmod(File f, Set<PosixFilePermission> mode) throws IOException
	{
		Files.setPosixFilePermissions(f.toPath(), mode);
	}
}
