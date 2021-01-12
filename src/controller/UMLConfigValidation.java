package controller;

import java.io.File;

import controller.convert.GraphViz;
import filemeta.config.Config;
import filemeta.config.ValidateFiles;

public class UMLConfigValidation implements ValidateFiles {

//---  Constants   ----------------------------------------------------------------------------
	
	public final static int CODE_FAILURE_DOT_ADDRESS = -1;
	public final static int CODE_FAILURE_FILE_MISSING = -2;
	
//---  Operations   ---------------------------------------------------------------------------
	
	@Override
	public int validateFile(Config c, File f) {
		switch(f.getName()) {
			case "config.txt":
				String entry = Config.getConfigFileEntry(f.getAbsolutePath(), FiniteStateMachine.DOT_ADDRESS_VAR);
				return verifyDotAddress(entry);
		}
		return CODE_FAILURE_FILE_MISSING;
	}
	
	private int verifyDotAddress(String path) {
		return GraphViz.verifyDotPath(path) ? Config.CONFIG_VERIFY_SUCCESS : CODE_FAILURE_DOT_ADDRESS;
	}
	
}
