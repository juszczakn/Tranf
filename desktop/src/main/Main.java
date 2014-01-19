package main;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;

import tests.NewFilesTests;
import tests.SaveStateTests;
import backend.Daemon;
import backend.Preferences;
import backend.SaveState;
import backend.SyncDirectory;

public class Main
{
	public CommandLine cli;
	public HelpFormatter help;
	public SaveState saveState;
	public ArrayList<File> outFiles;

	public static void main(String[] args)
	{
		new Main(args);
	}

	public Main(String[] args)
	{
		ArrayList<String> userInput;
//		try
//		{
//			NewFiles nf = new NewFiles("./");
//			SaveState ss = new SaveState("./");
//			ss.writeFileListState(nf.getFiles());
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
		
		boolean parsed = parseOptions(args);
		if(parsed && cli.hasOption('t')) {runTests(); System.exit(0);}

		Preferences pref = new Preferences();
		ArrayList<SyncDirectory> outDirs = pref.getOutDirectories();
		ArrayList<SyncDirectory> inDirs = pref.getInDirectories();

		Daemon daemon = new Daemon(outDirs, inDirs);
		daemon.startSending();
		daemon.startReceiving();
	}

	/**
	 * Parse cli options
	 * @param args
	 * @return
	 */
	public boolean parseOptions(String[] args)
	{
		Options options = new Options();
		options.addOption("h", "help", false, "Print help");
		options.addOption("t", "tests", false, "Run tests");
		options.addOption("d", "directory", true, "Directory to be added");
		options.addOption("o", "out", false, "Sync out to other application");
		options.addOption("i", "in", false, "Sync in from other application");
		options.addOption("n", "nogui", false, "Run as daemon");

		CommandLineParser clp = new PosixParser();
		try
		{
			cli = clp.parse(options, args);
		}
		catch(ParseException e)
		{
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Run all tests
	 */
	public void runTests()
	{
		println("\n\tTest NewFiles.java: "
				+ (new NewFilesTests().testNewFiles()) + "\n");
		println("\n\tTest SaveState.java: "
				+ (new SaveStateTests().testSaveState() + "\n"));
	}

	private void println()
	{
		println("");
	}

	private void println(String s)
	{
		System.out.println(s);
	}
}
