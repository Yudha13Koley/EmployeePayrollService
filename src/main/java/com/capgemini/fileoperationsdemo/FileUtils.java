package com.capgemini.fileoperationsdemo;

import java.io.File;

public class FileUtils {
	public static boolean deleteFiles(File f) {
		File[] all = f.listFiles();
		if (all != null) {
			for (File file : all) {
				deleteFiles(file);
			}
		}
		return f.delete();
	}

}
