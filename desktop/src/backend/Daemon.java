package backend;

import java.io.File;
import java.util.ArrayList;

import network.FileTransfer;
import network.Protocol;

import org.json.JSONObject;

/**
 * 
 *
 */
public class Daemon
{
	ArrayList<SyncDirectory> outDirs;
	ArrayList<SyncDirectory> inDirs;
	
	/**
	 * Daemon to run in background, main workhorse of project
	 */
	public Daemon(ArrayList<SyncDirectory> outDirs, ArrayList<SyncDirectory> inDirs)
	{
		this.outDirs = outDirs;
		this.inDirs = inDirs;
	}
	
	/**
	 * 
	 *
	 */
	private class StartSending implements Runnable
	{
		@Override
		public void run()
		{
			FileTransfer fileTransfer = FileTransfer.getFileTransfer();
			while(true)
			{
				ArrayList<File> newOutFiles;
				for(SyncDirectory sd : outDirs)
				{
					newOutFiles = checkForNewOutFiles(sd);
					try
					{
						for(File f : newOutFiles)
						{
							String json = sendFile(sd.getHost(), sd.getDestination(), f);
							System.out.println(json);
							fileTransfer.sendJson(json);
							while(!fileTransfer.isSendFinished())
							{
								Thread.sleep(1000);
							}
							System.out.println("json sent successfully");
							
							fileTransfer.sendFile(sd.getHost(), sd.getDestination(), f);
							//System.out.println("Sending to " + sd.getHost());
							while(!fileTransfer.isSendFinished())
							{
								Thread.sleep(1000);
							}
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				try
				{
					Thread.sleep(5000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 
	 *
	 */
	private class StartReceiving implements Runnable
	{
		@Override
		public void run()
		{
			FileTransfer fileTransfer = FileTransfer.getFileTransfer();
			while(true)
			{
				for(SyncDirectory sd : inDirs)
				{
					/**
					 * Try to get json, wait if socket not ready
					 */
					String jsonText = "";
					fileTransfer.resetJsonStr();
					while(jsonText.length() < 3 || 
								(jsonText.indexOf('{') == -1 || jsonText.indexOf('}') == -1))
					{
						
						fileTransfer.receiveJson(sd.getHost());
						try
						{
							Thread.sleep(100);
						}
						catch (InterruptedException e)
						{
							e.printStackTrace();
						}
						if(fileTransfer.isReceiveFinished())
						{
							jsonText = fileTransfer.getJsonStr();
						}
						else
						{
							fileTransfer.receiveThreadWait(5000);
							try
							{
								Thread.sleep(5000);
							}
							catch (InterruptedException e)
							{
								e.printStackTrace();
							}
							fileTransfer.notifyReceiveThread();
						}
					}
					
					
					try
					{
						//System.out.println("jsonText: " + jsonText);
						JSONObject jsonObj = new JSONObject(fileTransfer.getJsonStr());
						int filesize = new Integer((String) jsonObj.get(Protocol.FILESIZE));
						String filename = (String) jsonObj.get(Protocol.FILENAME);
						String destination = (String) jsonObj.get(Protocol.DIRECTORY);
						fileTransfer.receiveFile(destination, filename, filesize);
						while(!fileTransfer.isReceiveFinished())
						{
							Thread.sleep(1000);
						}
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				try
				{
					Thread.sleep(5000);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Send files
	 * @param outDirs
	 */
	public void startSending()
	{
		new Thread(new StartSending()).start();
	}
	
	/**
	 * 
	 */
	public void startReceiving()
	{
		new Thread(new StartReceiving()).start();
	}
	
	/**
	 * 
	 * @param host
	 * @param destination
	 * @param f
	 * @return
	 */
	private String sendFile(String host, String destination, File f)
	{
		Protocol p = new Protocol();
		p.addHost(host);
		p.addPlatform(p.DESKTOP);
		p.addAction(p.SEND);
		p.addFileSize(f.length());
		p.addFileName(f.getName());
		p.addDestination(destination);
		
		return p.finalizeJson();
	}
	
	/**
	 * Return all of the new files in a syncDir if syncDir.isOut == true
	 * @param sd
	 * @return
	 */
	public ArrayList<File> checkForNewOutFiles(SyncDirectory sd)
	{
		ArrayList<File> newOutFiles = new ArrayList<File>();
		if(sd.syncOut())
		{
			NewFiles nf = sd.getNewFiles();
			ArrayList<File> list = nf.refreshFileMap();
			if(list.size() != 0)
			{
				newOutFiles.addAll(list);
			}
		}
		return newOutFiles;
	}
	
	/**
	 * 
	 * @param files
	 */
	private void print(ArrayList<File> files)
	{
		for(File f : files)
		{
			System.out.println(f.getAbsolutePath());
		}
	}
}
