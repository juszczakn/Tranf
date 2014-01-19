package backend;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;

public class PropertiesFile
{
	public File file;
	
	public PropertiesFile(String f)
	{
		file = new File(f);
	}

	public void addLine(String line)
	{
		FileWriter fw;
		BufferedWriter bw;
		try
		{
			fw = new FileWriter(file);
			bw = new BufferedWriter(fw);
			
			bw.append(line);
			bw.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
