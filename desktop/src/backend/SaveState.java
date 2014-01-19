package backend;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

/**
 * Used to save list of files that will
 * be read in on next startup
 */
public class SaveState
{
	File saveFile;
	final String OBJ_FILENAME = "/.syph";

	public SaveState(String directory) throws IOException
	{
		saveFile = new File(directory + OBJ_FILENAME);
		if(saveFile.isDirectory())
		{
			throw new IOException("File \"" + directory
					+ "\" is a directory");
		}
		else if(!saveFile.exists())
		{
			writeFileListState(new ArrayList<File>());
		}
	}

	/**
	 * Write list of files out to a given file
	 * returns true on success
	 */
	public boolean writeFileListState(ArrayList<File> flist)
	{
		try
		{
			FileOutputStream fout = new FileOutputStream(saveFile);
			ObjectOutputStream objOut = new ObjectOutputStream(fout);
			objOut.writeObject(flist);
			objOut.flush();
			objOut.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Reads in a ArrayList<File> from save file
	 * returns file list of saved state
	 */
	@SuppressWarnings("unchecked")
	public ArrayList<File> readFileListState() throws IOException
	{
		try
		{
			FileInputStream fin = new FileInputStream(saveFile);
			ObjectInputStream objIn = new ObjectInputStream(fin);
			Object obj = objIn.readObject();
			objIn.close();

			// Might be worth trying to get rid
			// of cast warning
			if(obj instanceof ArrayList)
			{
				return (ArrayList<File>) obj;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		throw new IOException("Unable to read ArrayList<File> from "
				+ saveFile);
	}
}
