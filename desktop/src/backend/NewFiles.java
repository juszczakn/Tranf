package backend;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Return all new files in a given root directory.
 * Empty directories are not registered, by design.
 */
public class NewFiles
{
	private File root;
	private HashMap<String, File> filemap;

	public NewFiles(String dir) throws Exception
	{
		root = new File(dir);
		if(!root.isDirectory())
		{
			root = null;
			filemap = null;

			throw new Exception(dir + " is not a directory.");
		}

		filemap = new HashMap<String, File>();
		initFileMap(root);
	}
	
	public NewFiles(String directory, ArrayList<File> files) throws Exception
	{
		initFileMap(directory, files);
	}

	public ArrayList<File> initFileMap()
	{
		return initFileMap(root);
	}

	/**
	 * Init file map of Filename -> FileObj
	 * Assumes file map is empty
	 * return list of new files added
	 */
	public ArrayList<File> initFileMap(File r)
	{
		ArrayList<File> newFiles = new ArrayList<File>();
		File[] files = r.listFiles();

		for(File file : files)
		{
			if(file.isDirectory())
			{
				newFiles.addAll(initFileMap(file));
			}
			else
			{
				filemap.put(file.getName(), file);
				newFiles.add(file);
			}
		}
		return newFiles;
	}
	
	/**
	 * Initialize the map with an arraylist, most likely
	 * from reading in a saved state
	 * @param files
	 * @throws Exception 
	 */
	public void initFileMap(String dir, ArrayList<File> files) throws Exception
	{
		root = new File(dir);
		if(!root.isDirectory())
		{
			root = null;
			filemap = null;

			throw new Exception(dir + " is not a directory.");
		}
		
		filemap = new HashMap<String, File>();
		if(files != null)
		{
			for(File file : files)
			{
				filemap.put(file.getName(), file);
			}
		}
	}

	/**
	 * Refresh the filemap with files that have been
	 * updated. Return 
	 * @return list of new files added
	 */
	public ArrayList<File> refreshFileMap()
	{
		return refreshFileMap(root);
	}

	/**
	 * Refresh the filemap with files that have been
	 * updated.
	 * return list of new files added
	 */
	public ArrayList<File> refreshFileMap(File r)
	{
		ArrayList<File> newFiles = new ArrayList<File>();
		File[] tmpFiles = r.listFiles();

		for(File file : tmpFiles)
		{
			if(file.isDirectory())
			{
				newFiles.addAll(refreshFileMap(file));				
			}
			else
			{
				File f = filemap.get(file.getName());
				// if a newly created file or recently modified
				if(null == f || file.lastModified() > f.lastModified())
				{
					filemap.put(file.getName(), file);
					newFiles.add(file);
				}
			}
		}
		return newFiles;
	}

	public boolean areFilesUpdated()
	{
		return areFilesUpdated(root);
	}

	/**
	 * return true if files have been updated in root
	 */
	public boolean areFilesUpdated(File r)
	{
		File[] tmpFiles = r.listFiles();

		for(File file : tmpFiles)
		{
			if(file.isDirectory())
			{
				if(areFilesUpdated(file)) return true;;
			}
			else
			{
				File f = filemap.get(file.getName());
				// if a newly created file or recently modified
				if(null == f || file.lastModified() > f.lastModified())
				{
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Print the current file map
	 */
	public void printFileMap()
	{
		Set<String> set = filemap.keySet();
		for(String s : set)
		{
			System.out.println(filemap.get(s));
		}
	}

	/**
	 * return files in root dir
	 */
	public ArrayList<File> getFiles()
	{
		ArrayList<File> flist = new ArrayList<File>();
		Set<String> set = filemap.keySet();
		for(String s : set)
		{
			flist.add(filemap.get(s));
		}

		return flist;
	}

	/**
	 * return absolute filepath of root dir
	 */
	public String getRootDir()
	{
		return root.getAbsolutePath();
	}
}
