package network;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.ServerSocket;
import java.net.Socket;

public class FileTransfer
{
	static FileTransfer fileTransfer;
	static final int SOCKET_SEND_NUMBER = 8312;
	static final int SOCKET_RECEIVE_NUMBER = 8313;
	//Needed now for POC
	static File file;
	static int fileLength = 0;
	static String fileName = "";
	static String destination = "";
	static String receivedJsonStr = "";
	static String host = "";
	
	static boolean killReceiveThread = false;
	
	
	Thread sendThread;
	Thread receiveThread;
	
	//ServerSocket serverSendSocket = null;
	ServerSocket serverReceiveSocket = null;
	
	private FileTransfer()
	{
		init();
	}
	
	private void init()
	{
		try
		{
			serverReceiveSocket = new ServerSocket(SOCKET_SEND_NUMBER);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Accessor for singleton
	 * @return
	 */
	public synchronized static FileTransfer getFileTransfer()
	{
		if(fileTransfer == null)
			fileTransfer = new FileTransfer();

		return fileTransfer;
	}
	
	public void sendJson(final String json)
	{
		try
		{
			if(sendThread != null && sendThread.isAlive())
				sendThread.join();
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		sendThread = new Thread()
		{
			public void run()
			{
				try
				{
					if(serverReceiveSocket == null)
						serverReceiveSocket = new ServerSocket(SOCKET_SEND_NUMBER);
					Socket socket = serverReceiveSocket.accept();
					System.out.println("Socket accepted");
					BufferedOutputStream bos = new BufferedOutputStream(socket.getOutputStream());
					bos.write(json.getBytes());
					bos.flush();
					bos.close();
					socket.close();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		sendThread.start();
	}
	
	public void receiveJson(final String hostname)
	{
		try
		{
			if(receiveThread != null && receiveThread.isAlive())
				return;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			return;
		}
		
		receiveThread = new Thread()
		{
			public void run()
			{
				try
				{
					Socket socket = new Socket(hostname, SOCKET_SEND_NUMBER);
					BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					
					if(!receivedJsonStr.trim().equals(""))
					{
						receivedJsonStr += in.readLine();
					}
					else
					{
						receivedJsonStr = in.readLine();
					}
				}
				catch(ConnectException e)
				{
					//e.printStackTrace();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};
		receiveThread.start();
	}
	
	/**
	 * Have receive thread wait
	 * @param wait
	 */
	public void receiveThreadWait(int wait)
	{
		try
		{
			receiveThread.wait(wait);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * Start the receive thread back up
	 */
	public void notifyReceiveThread()
	{
		receiveThread.notify();
	}

	public void sendFile(String host, String destination, File f)
	{
		this.host = host;
		file = f;
		fileLength = (int) f.length();
		fileName = f.getName();
		sendThread = new Thread(new SendFile());
		sendThread.start();
	}
	
	public void receiveFile(String dest, String fname, int flength)
	{
		destination = dest;
		fileLength = flength;
		fileName = fname;
		receiveThread = new Thread(new ReceiveFile());
		receiveThread.start();
	}
	
	public boolean isSendFinished()
	{
		if(sendThread == null)
			return false;
		else if(!sendThread.isAlive())
			return true;
		return false;
	}
	
	public boolean isReceiveFinished()
	{
		if(receiveThread == null)
			return false;
		else if(!receiveThread.isAlive())
			return true;
		return false;
	}	
	
	public boolean isTransferFinished()
	{
		boolean finished = true;
		if(receiveThread != null && receiveThread.isAlive())
			finished = false;
		if(sendThread != null && sendThread.isAlive())
			finished = false;
		return finished;
	}
	
	public String getJsonStr()
	{
		return receivedJsonStr;
	}
	
	public void resetJsonStr()
	{
		receivedJsonStr = "";
	}
	
	private static class ReceiveFile implements Runnable
	{
		/**
		 * Receive a file over the network
		 * @return
		 */
		public void run()
		{
			Socket socket;
			byte[] arr = new byte[fileLength + 1];
			InputStream is;
			FileOutputStream fos;
			BufferedOutputStream bos;
			try
			{
				socket = new Socket(host, SOCKET_SEND_NUMBER);
				is = socket.getInputStream();
				System.out.println(destination + fileName);
				fos = new FileOutputStream(destination + fileName);
				bos = new BufferedOutputStream(fos);
				/*
				 * Attempt to read in as much of the file as possible.
				 * is.read returns the # of bytes read in so far. Keep
				 * reading in as long as the file is not read in all the
				 * way. 
				 */
				//System.out.println("Starting to read File from stream");
				int bytesRead = is.read(arr, 0, fileLength + 1);
				int current = bytesRead;
				//System.out.println("Entering receive loop");
				int counter = 0;
				do
				{
					bytesRead = is.read(arr, current, fileLength - current + 1);
					if(bytesRead >= 0)
						current += bytesRead;
					counter++;
					if(counter % 10000 == 0)
						System.out.println(bytesRead);
				} while(bytesRead > -1);

				//System.out.println("Finished reading File from stream");
				bos.write(arr, 0, fileLength);
				bos.flush();
				bos.close();
				socket.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	

	private class SendFile implements Runnable
	{
		/**
		 * Send a given file over the network.
		 * @param file
		 */
		public void run()
		{
			fileLength = (int) file.length();
			fileName = file.getName();

			Socket socket;
			//TODO change to long?
			byte[] arr = new byte[fileLength];
			FileInputStream fis;
			BufferedInputStream bis;
			OutputStream os;
			try
			{
				if(serverReceiveSocket == null)
					serverReceiveSocket = new ServerSocket(SOCKET_SEND_NUMBER);

				socket = serverReceiveSocket.accept();
				fis = new FileInputStream(file);
				bis = new BufferedInputStream(fis);
				bis.read(arr, 0, arr.length);
				os = socket.getOutputStream();
				os.write(arr, 0, arr.length);
				os.flush();
				socket.close();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
}
