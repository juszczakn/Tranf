package network;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Wrapper class for JSONObject for two purposes.
 *
 * 1. Create with a received JSON string. Contains getters
 * to extract important values from JSON
 *
 * 2. Use to create a new JSON string to be sent.
 */
public class Protocol
{
	/**
	 * Keys for JSON
	 */
	public static final String HOST = "host";
	public static final String PLATFORM = "platform";
	public static final String ACTION = "action";
	public static final String DIRECTORY = "directory";
	public static final String FILENAME = "filename";

	/**
	 * Standard strings to be returned from keys
	 */
	// PLATFORM items
	public static final String DESKTOP = "desktop";
	public static final String ANDROID = "android";
	// ACTION items
	public static final String SEND = "send";
	public static final String RECEIVE = "receive";

	//FILE META
	public static final String FILESIZE = "filesize";

	public static final String HASH = " : ";
	public static final String SEPERATOR = ", ";

	private JSONObject json;
	private String jsonString;

	/**
	 * Protocol object. Use to create a new JSON String.
	 */
	public Protocol()
	{
		json = new JSONObject();
		jsonString = "";
	}

	/**
	 * Protocol object. Feed a JSON String for extraction
	 */
	public Protocol(String s)
	{
		json = new JSONObject(s);
	}

	/**
	 * return directory to be synced
	 */
	public String getDirectory()
	{
		return (String) json.get(DIRECTORY);
	}

	/**
	 * return file to be transferred
	 */
	public String getFile()
	{
		return (String) json.get(FILENAME);
	}

	/**
	 * return desktop or android
	 */
	public String getPlatform()
	{
		return (String) json.get(PLATFORM);
	}

	/**
	 * return receive or send
	 */
	public String getAction()
	{
		return (String) json.get(ACTION);
	}

	/**
	 * JSON creation methods below.
	 *
	 * Standard:
	 * - New additions to string end with ','
	 * and a space.
	 * - No hardcoded strings
	 */

	public String finalizeJson()
	{
		if(jsonString.endsWith(SEPERATOR))
		{
			jsonString = jsonString.substring(0, jsonString.length() - 2);
		}
		jsonString = "{ " + jsonString + " }";
		return jsonString;
	}

	public void addFileName(String fname)
	{
		jsonString += "\"" + FILENAME + "\""  + HASH + "\"" + fname + "\""  + SEPERATOR;
	}

	public void addHost(String host)
	{
		jsonString += "\"" + HOST + "\"" + HASH + "\"" + host + "\"" + SEPERATOR;
	}

	public void addPlatform(String platform)
	{
		jsonString += "\"" + PLATFORM + "\"" + HASH + "\"" + platform + "\"" + SEPERATOR;
	}

	public void addDestination(String dest)
	{
		jsonString += "\"" + DIRECTORY + "\"" + HASH + "\"" + dest + "\"" + SEPERATOR;
	}

	public void addAction(String action)
	{
		jsonString += "\"" + ACTION + "\"" + HASH + "\"" + action + "\"" + SEPERATOR;
	}

	public void addFileSize(long size)
	{
		jsonString += "\"" + FILESIZE + "\"" + HASH + "\"" + size + "\"" + SEPERATOR;
	}
}