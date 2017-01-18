package biosyndesign.core.ui;

import java.io.File;
import java.io.FilenameFilter;

public class FileFilter implements FilenameFilter {

	@Override
	public boolean accept(File directory, String fileName) {

		if (fileName.toLowerCase().endsWith(".vis")) {

			return true;

		}

		return false;

	}

}
