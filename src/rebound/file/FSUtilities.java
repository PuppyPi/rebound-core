/*
 * Created on Nov 17, 2005
 * 	by the wonderful Eclipse(c)
 */
package rebound.file;

import static java.util.Collections.*;
import static java.util.Objects.*;
import static rebound.GlobalCodeMetastuffContext.*;
import static rebound.io.BasicIOUtilities.*;
import static rebound.io.JRECompatIOUtilities.*;
import static rebound.text.StringUtilities.*;
import static rebound.util.collections.CollectionUtilities.*;
import static rebound.util.objectutil.BasicObjectUtilities.*;
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
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Stack;
import java.util.Vector;
import java.util.function.Predicate;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import rebound.annotations.semantic.allowedoperations.WritableValue;
import rebound.exceptions.ImpossibleException;
import rebound.exceptions.NoFreeResourceFoundException;
import rebound.exceptions.NotFoundException;
import rebound.exceptions.NotYetImplementedException;
import rebound.exceptions.WrappedThrowableRuntimeException;
import rebound.io.FSIOUtilities;
import rebound.io.JRECompatIOUtilities;
import rebound.text.StringUtilities;
import rebound.text.StringUtilities.WhatToDoWithEmpties;
import rebound.util.collections.ArrayUtilities;
import rebound.util.container.ContainerInterfaces.IntegerContainer;
import rebound.util.container.SimpleContainers.SimpleIntegerContainer;
import rebound.util.functional.FunctionalInterfaces.BinaryProcedure;
import rebound.util.functional.FunctionalInterfaces.UnaryProcedure;
import rebound.util.objectutil.JavaNamespace;

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
		
		
		protected static final NonExistantFile INST = new NonExistantFile();
		
		public static NonExistantFile inst()
		{
			return INST;
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
		//Note: this is used by the archive (edit: you mean ARC1?? --er--I mean arc1? XD )
		//NOTE: THIS IS USED IN SECURITY BREACH DETECTING CODE IN FTPD AND SUCH!!!!
		
		
		
		if (f.isEmpty())
			return "";
		
		
		boolean absolute = f.charAt(0) == separator;
		
		if (absolute)
			f = f.substring(1);
		
		
		f = rtrim(f, separator);
		f = ltrimstr(f, "./");
		
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
	 * Gets a file's path relative to a given ancestor such that {@link #joinPaths(Object...) join}(<code>base</code>, <i>returnValue</i>) == <code>f</code>.
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
			String baseplussep = base+separator;
			
			if (f.startsWith(baseplussep))
				return f.substring(baseplussep.length());
			else
				return null;
		}
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
		int i = path.indexOf(sep);
		return i == -1 ? path : path.substring(0, i);
	}
	
	public static String getAllButFirstPathElement(String path, char sep)
	{
		path = StringUtilities.trim(path, sep);
		int i = path.indexOf(sep);
		return i == -1 ? path : path.substring(i+1);
	}
	
	public static String[] splitPathAtFirstOrNullIfNone(String path, char sep)
	{
		path = StringUtilities.trim(path, sep);
		return splitonceOrNull(path, sep);
	}
	
	
	/**
	 * Ie, basically <code>concat({@link StringUtilities#split(String, char)}(path, delimiter)[0:-1])</code>, but faster :D
	 */
	@Nullable
	public static String getUptoLastPathElementOrNull(@Nonnull String path, char delimiter)
	{
		int dotpos = path.lastIndexOf(delimiter);
		if (dotpos == -1)
			return null;
		else
			return path.substring(0, dotpos);
	}
	
	
	/**
	 * Ie, <code>{@link StringUtilities#split(String, char)}(path, delimiter)[.length-1]</code>, but faster :D
	 */
	@Nonnull
	public static String getLastPathElement(@Nonnull String path, char delimiter)
	{
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
		
		File p = new File(pathname);
		return p.isAbsolute() ? p : new File(currentDirectory, pathname);
	}
	
	public static File resolvePath(File currentDirectory, File pathname)
	{
		return resolvePath(currentDirectory, pathname.getPath());
	}
	
	
	
	
	
	//TODO Unit tests
	/**
	 * Joins path elements together adding {@link File#separatorChar} 's as needed.
	 * @param pathElements is an array of <code>File</code>s and <code>String</code>s
	 */
	public static File joinPaths(Object... pathElements)
	{
		File f = null;
		
		for (Object e : pathElements)
		{
			if (e == null || (e instanceof String && ((String)e).isEmpty()))
				continue;
			
			if (f == null)
			{
				f = e instanceof File ? (File)e : new File((String)e);
			}
			else
			{
				f = e instanceof File ? new File(f, ((File)e).getName()) : new File(f, (String)e);
			}
		}
		
		return f == null ? new File("") : f;
	}
	
	public static File joinPathsI(Iterable<?> pathElements)
	{
		File f = null;
		
		for (Object e : pathElements)
		{
			if (e == null || (e instanceof String && ((String)e).isEmpty()))
				continue;
			
			if (f == null)
			{
				f = e instanceof File ? (File)e : new File((String)e);
			}
			else
			{
				f = e instanceof File ? new File(f, ((File)e).getName()) : new File(f, (String)e);
			}
		}
		
		return f == null ? new File("") : f;
	}
	
	
	/**
	 * Joins path elements together adding and removing extraneous '/'s as needed.
	 * This is hard-coded to use the posix path name syntax, and so is suitable for URL's, etc.
	 */
	public static String joinPathsPosix(String... pathElements)
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
		for (String p : pathElements)
		{
			boolean root = first && !p.isEmpty() && forAll('/', p);
			
			if (root)  //The root directory! :D
			{
				p = "/";
			}
			else
			{
				p = trim(p, '/');
				
				p = ltrimstr(p, "./");
			}
			
			
			if (!p.isEmpty() && !p.equals("."))
			{
				if (first)
				{
					if (!root)
					{
						first = false;
					}
				}
				else
				{
					b.append('/');
				}
				
				b.append(p);
			}
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
	public static boolean delete_r(File f, boolean tryAll)
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
				success = delete_r(c, tryAll);
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
	
	public static void delete_rMandatory(File f, boolean tryAll) throws UncheckedIOException
	{
		boolean success = delete_r(f, tryAll);
		if (!success)
			throw new UncheckedIOException(new IOException("Could not delete file: "+f.getAbsolutePath()));
	}
	
	
	
	
	/**
	 * This tries to move source to dest (move != copy) first by renaming, then by copying and deleting source if that fails.
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
			File content = readlink(source);
			
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
			// NOTE: If we change this, remove the preemptive NYI in the UIDSFS dependent of this function :D    (perhaps do a reverse-search to find all the other dependents of this function!!)
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
					number = 0;
					first = false;
				}
				else
				{
					if (randTries > 256)
					{
						if (addTries > 2048)
							//I give up!
							return null;
						
						number = counter;
						counter++;
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
	
	
	
	
	public static File ensureExtension(File root, String desiredExtension)
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
		
		if (f instanceof NonExistantFile)
			return false;
		
		return !f.isFile() && !f.isDirectory() && f.exists();  //notice I use f.exists() not lexists(f) since the latter would include broken symlinks but the former excludes it! :>
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
		
		if (f instanceof NonExistantFile)
			return false;
		
		return !f.isDirectory() && f.exists();
	}
	
	
	
	
	
	
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
	
	
	
	public static File readlinkRE(File f) throws WrappedThrowableRuntimeException
	{
		requireNonNull(f);
		
		try
		{
			return readlink(f);
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static File readlink(File f) throws IOException
	{
		requireNonNull(f);
		
		return Files.readSymbolicLink(f.toPath()).toFile();
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
			if (isSymlink(pathForSymlink) && eq(readlink(pathForSymlink), immediateTarget))
				return false;  //Already what we would make :>
			else
				throw new IOException("Destination for symlink already exists: "+repr(pathForSymlink.getAbsolutePath()));
		}
		
		Files.createSymbolicLink(pathForSymlink.toPath(), immediateTarget.toPath());
		return true;
	}
	
	
	public static void remakelinkSymbolic(File immediateTarget, File pathForSymlink) throws IOException
	{
		requireNonNull(immediateTarget);
		requireNonNull(pathForSymlink);
		
		File oldTarget = null;
		
		if (isSymlink(pathForSymlink))
		{
			oldTarget = readlink(pathForSymlink);
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
	
	
	
	
	//Todo postParents option, like python version :>
	/**
	 * See docs on (better) Python version xD'
	 * @param descendRecurseFilter If this returns false, then we won't even descend into it recursively!  Much less will 'inclusion' filters in the observer have any affect on it! XP  :>
	 */
	public static void recurse(UnaryProcedure<File> observer, Predicate<File> descendRecurseFilter, Iterable<File> targets)
	{
		Collection<File> done = new HashSet<File>();
		Stack<File> rstack = new Stack<File>();
		
		for (File target : targets)
		{
			observer.f(target);
			done.add(realpath(target));
			
			if (target.isDirectory())
			{
				if (descendRecurseFilter == null || descendRecurseFilter.test(target))  //null filter means RECURSE_FILTER_ALWAYS  >,>
				{
					rstack.push(target);
				}
			}
			
			while (true)
			{
				if (rstack.isEmpty())
					break;
				
				File dir = rstack.pop();
				
				File[] files = dir.listFiles();
				
				if (files == null)
					throw new WrappedThrowableRuntimeException(new IOException("Listing directory failed for: "+repr(dir.getAbsolutePath())));
				
				for (File f : files)
				{
					if (!done.contains(realpath(f)))
					{
						observer.f(f);
						done.add(realpath(f));
						
						if (f.isDirectory())
						{
							if (descendRecurseFilter == null || descendRecurseFilter.test(f))  //null filter means RECURSE_FILTER_ALWAYS  >,>
							{
								rstack.push(f);
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
	
	
	
	
	
	
	
	
	//Todo resolve duplicates?  XD''
	
	public static File realpath(File f)
	{
		try
		{
			return f.getCanonicalFile();
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static File getCanonicalFileRE(File f) throws WrappedThrowableRuntimeException
	{
		try
		{
			return f.getCanonicalFile();
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static String getCanonicalPathRE(File f) throws WrappedThrowableRuntimeException
	{
		try
		{
			return f.getCanonicalPath();
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	
	
	public static File getCanonicalFileRE(String f) throws WrappedThrowableRuntimeException
	{
		try
		{
			return new File(f).getCanonicalFile();
		}
		catch (IOException exc)
		{
			throw new WrappedThrowableRuntimeException(exc);
		}
	}
	
	public static String getCanonicalPathRE(String f) throws WrappedThrowableRuntimeException
	{
		try
		{
			return new File(f).getCanonicalPath();
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
		return getCanonicalSetPathnames(Arrays.asList(files));
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
	
	
	//Todo recreated the old faster but more destructive one!
	
	
	public static void performSafeFileSystemWriteTwoStageAndCopy(File dest, WriterProcedure write) throws IOException
	{
		if (dest.isDirectory() || isBrokenSymlink(dest))
			throw new IOException("Tried to write into an invalid destination!: "+repr(dest.getAbsolutePath()));
		
		
		/*
		 * This technique doesn't reset file permissions!
		 */
		
		File temporary = getUniqueFileOrNull(dest.getParentFile(), dest.getName(), ".tmp");
		
		if (!lexists(temporary))
			temporary.createNewFile();
		
		if (!temporary.canWrite())
		{
			temporary = File.createTempFile(dest.getName(), ".tmp");
			
			if (!temporary.canWrite())
				logBug();
		}
		
		
		try (OutputStream out = new FileOutputStream(temporary))
		{
			write.write(out);
		}
		
		
		//We won't reach this part if writing failed!
		
		//Transfer it into the final location! :D
		{
			if (dest.isFile() && !isSymlink(dest))
				dest.delete();
			
			if (!lexists(dest))
			{
				if (temporary.renameTo(dest))
					return;
			}
			
			try (OutputStream out = new FileOutputStream(dest))
			{
				try (InputStream in = new FileInputStream(temporary))
				{
					JRECompatIOUtilities.pump(in, out);
				}
			}
			
			
			//We won't reach this part if transfer failed!
			
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
	
	
	
	
	
	
	public static void doLockedOnFileBlocking(File f, Runnable r) throws IOException
	{
		try (SimpleFileLock l = lockFileBlocking(f))
		{
			r.run();
		}
	}
	
	
	/**
	 * If this returns false, r.run() will have never been called :>
	 * @return if the operation was performed or not.  false is only returned if the file is already locked by another process :3
	 */
	public static boolean doLockedOnFileNonblocking(File f, Runnable r) throws IOException
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
	 */
	public static String fsescape(String s)
	{
		return s.replace("\\", "\\e").replace("\u2044", "\\s").replace("/", "\u2044");
	}
	
	/**
	 * (De!)Escapes forward slashes completely, using an escape syntax based on the backslash which normally needn't be used :3
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
	
	
	
	
	
	public static void ensureDirThrow(File d) throws IOException
	{
		if (!d.isDirectory())
		{
			d.mkdirs();
			if (!d.isDirectory())
				throw new IOException("Couldn't create dir: "+repr(d.getAbsolutePath()));
		}
	}
	
	
	
	
	
	
	
	
	
	
	public static File createTempFileRTExc(String prefix, String suffix) throws WrappedThrowableRuntimeException
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
			try
			{
				f.createNewFile();
			}
			catch (IOException exc)
			{
			}
			
			if (lexists(f))
			{
				f.delete();
				return true;
			}
			else
			{
				return false;
			}
		}
	}
}
