package ui;

import java.io.File;

import filemeta.config.Config;
import filemeta.config.ValidateFiles;
import graphviz.GraphViz;

public class UMLConfigValidation implements ValidateFiles {

	public final static int CODE_FAILURE_DOT_ADDRESS = -1;
	public final static int CODE_FAILURE_FILE_MISSING = -2;
	
	@Override
	public int validateFile(Config c, File f) {
		switch(f.getName()) {
			case "config.txt":
				String entry = c.getConfigFileEntry(f.getAbsolutePath(), FSMUI.DOT_ADDRESS_VAR);
				return verifyDotAddress(entry);
		}
		return CODE_FAILURE_FILE_MISSING;
	}
	
	private int verifyDotAddress(String path) {
		return GraphViz.verifyDotPath(path) ? Config.CONFIG_VERIFY_SUCCESS : CODE_FAILURE_DOT_ADDRESS;
	}
	
}
