package tests;

import java.io.File;
import java.util.ArrayList;

import backend.NewFiles;
import backend.SaveState;

public class SaveStateTests {
	String fname = "./savestate.txt~";

	public SaveStateTests() {

	}

	public boolean testSaveState() {
		System.out.println("Testing SaveState.java");

		try {
			new SaveState(fname);
		} catch (Exception e) {
			return false;
		}
		try {
			new SaveState("./bin");
			return false;
		} catch (Exception e) {
		}
		try {
			SaveState ss = new SaveState(fname);
			ss.writeFileListState(new NewFiles("./").initFileMap());
			ArrayList<File> flist = ss.readFileListState();
			System.out.println(flist.size());
			for (File f : flist) {
				System.out.println(f.toString());
			}
		} catch (Exception e) {
			return false;
		}
		return true;
	}

}
