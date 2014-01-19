package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.Enumeration;

public class Preferences
{
	final private String rootDir = "./conf/";
	final private String inPref = rootDir + "in.txt";
	final private String outPref = rootDir + "out.txt";
	final private String separator = "\t";
	
	public Preferences()
	{
		
	}

	/**
	 * Return directories to be synced
	 * @return
	 */
	public ArrayList<SyncDirectory> getOutDirectories()
	{
		ArrayList<SyncDirectory> out = new ArrayList<SyncDirectory>();
		BufferedReader br;
		try
		{
			//String localhost = InetAddress.getLocalHost().toString();
			//localhost = localhost.split("/")[1];
			String localhost = "192.168.1.112";
			//System.out.println(localhost);
			br = new BufferedReader(new FileReader(outPref));
			String line = null;
			while(null != (line = br.readLine()))
			{
				if(!line.trim().equalsIgnoreCase(""))
				{
					String[] dirs = line.trim().split(separator);
					if(dirs.length == 2)
					{
						// we want host, src dir, and destination of file
						out.add(new SyncDirectory(localhost, dirs[0], dirs[1], true));
					}
					else
					{
						System.out.println("Malformed conf file, line: " + line);
						System.out.println("Correct format: host<tab>Dir<tab>Dest");
					}
				}
			}
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return out;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<SyncDirectory> getInDirectories()
	{
		ArrayList<SyncDirectory> in = new ArrayList<SyncDirectory>();
		BufferedReader br;
		try
		{
			br = new BufferedReader(new FileReader(inPref));
			String line = null;
			while(null != (line = br.readLine()))
			{
				if(!line.trim().equalsIgnoreCase(""))
				{
					String[] dirs = line.trim().split(separator);
					if(dirs.length == 2)
					{
						// we want host and destination of file
						in.add(new SyncDirectory(dirs[0], null, dirs[1], false));
					}
					else
					{
						System.out.println("Malformed conf file, line: " + line);
						System.out.println("Correct format: host<tab>Dir<tab>Dest");
					}
				}
			}
			br.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
		return in;
	}
	
	/**
	 * Add directories to be synchronized to their respective
	 * preference files to be kept
	 * @param sd
	 */
	public void addSyncDirectories(ArrayList<SyncDirectory> sd)
	{
		createRootDir();
		BufferedWriter ibw;
		BufferedWriter obw;
		try
		{
			ibw = new BufferedWriter(new PrintWriter(inPref));
			obw = new BufferedWriter(new PrintWriter(outPref));
			
			for(SyncDirectory s : sd)
			{
				if(s.syncIn())
					ibw.append(s.getDirectory());
				else
					obw.append(s.getDirectory());
			}
			ibw.close();
			obw.close();
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Creates the root dir for preference files if needed
	 * @return if root exists or not
	 */
	private boolean createRootDir()
	{
		File root = new File(rootDir);
		if(!root.exists())
			root.mkdir();
		return root.exists();
	}
}
