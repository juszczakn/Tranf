package tests;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;

import backend.NewFiles;

public class NewFilesTests {
	public NewFilesTests() {
	}

	/**
	 * Test NewFiles.java TODO: expand
	 */
	public boolean testNewFiles() {
		println("Testing NewFiles.java\n");
		try {
			new NewFiles("./").printFileMap();
		} catch (Exception e) {
			return false;
		}
		try {
			new NewFiles("").printFileMap();
			return false;
		} catch (Exception e) {
		}
		try {
			new NewFiles("files.java").printFileMap();
			return false;
		} catch (Exception e) {
		}
		try {
			new NewFiles("./bin/NewFiles.class").printFileMap();
			return false;
		} catch (Exception e) {
		}
		try {
			println(new NewFiles("./").getRootDir());
		} catch (Exception e) {
			return false;
		}

		try {
			NewFiles n = new NewFiles("./");
			File f = new File("./test~");
			if (!f.exists())
				new FileOutputStream(f).close();
			f.setLastModified(f.lastModified() + 1);
			if (!n.areFilesUpdated())
				return false;
			ArrayList<File> flist = n.refreshFileMap();
			if (flist.size() != 1)
				return false;
			for (File fs : flist)
				println("Modified: " + fs.toString());
			f.delete();
		} catch (Exception e) {
			return false;
		}

		return true;
	}

	private void println() {
		println("");
	}

	private void println(String s) {
		System.out.println(s);
	}

}
