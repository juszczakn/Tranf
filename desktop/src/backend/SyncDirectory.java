package backend;

public class SyncDirectory
{
	private String host;
	private String directory;
	private String destination;
	private NewFiles newFiles;
	
	private boolean out;
	
	public SyncDirectory(String host, String directory, String destination, boolean out)
	{
		this.host = host;
		this.directory = directory;
		this.destination = destination;
		this.out = out;
	}
	
	public NewFiles getNewFiles()
	{
		if(!out)
			return null;
		if(newFiles != null)
			return newFiles;
		
		try
		{
			SaveState ss = new SaveState(directory);
			newFiles = new NewFiles(directory, ss.readFileListState());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return newFiles;
	}
	
	public String getHost()
	{
		return host;
	}
	
	public String getDestination()
	{
		return destination;
	}
		
	public void setSyncOut(boolean b)
	{
		out = b;
	}
	
	public void setDirectory(String dir)
	{
		directory = dir;
	}
	
	public boolean syncOut()
	{
		return out;
	}
	
	public boolean syncIn()
	{
		return !out;
	}
	
	public String getDirectory()
	{
		return directory;
	}
}
