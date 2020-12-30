package controller;

import controller.convert.FormatConversion;
import filemeta.FileChooser;
import filemeta.config.Config;
import visual.composite.popout.PopoutAlert;

public class FiniteStateMachine {
	
	//-- Config  ----------------------------------------------
	
	private final static String OS = System.getProperty("os.name");
	public final static String DOT_ADDRESS_VAR = "dotAddress";
	public final static String ADDRESS_SETTINGS = "./Finite State Machine Model/settings/";
	public final static String ADDRESS_IMAGES = "./Finite State Machine Model/images/";
	public final static String ADDRESS_SOURCES = "./Finite State Machine Model/sources/";
	public final static String ADDRESS_CONFIG = ADDRESS_SETTINGS + "/config.txt";
	
	private final static String DEFAULT_CONFIG_COMMENT = "##############################################################\r\n" + 
			"#                       Configurations                       #\r\n" + 
			"##############################################################\r\n" + 
			"# Format as 'name = address', the \" = \" spacing is necessary\r\n" + 
			"# It's awkward but it makes the file reading easier and I'm telling you this directly";
	
	public FiniteStateMachine() {
		FormatConversion.assignPaths(ADDRESS_SOURCES, ADDRESS_CONFIG);
		fileConfiguration();
	}
	
	//-- File Configuration  ----------------------------------
	
	private void fileConfiguration() {
		Config c = new Config("", new UMLConfigValidation());
		c.addFilePath("Diagram");
		c.addFilePath("Diagram/settings");
		c.addFilePath("Diagram/images");
		c.addFilePath("Diagram/sources");
		c.addFile("Diagram/settings", "config.txt", DEFAULT_CONFIG_COMMENT);
		c.addFileEntry("Diagram/settings", "config.txt", DOT_ADDRESS_VAR, "Where is your dot program located? It will be called externally.", "?");
		
		c.softWriteConfig();
		
		while(!c.verifyConfig()) {
			switch(c.getErrorCode()) {
				case UMLConfigValidation.CODE_FAILURE_DOT_ADDRESS:
					PopoutAlert pA = new PopoutAlert(400, 250, "Please navigate to and select the path for your graphviz/bin/dot.exe file in the following navigation tool");
					c.setConfigFileEntry("Diagram/settings/config.txt", DOT_ADDRESS_VAR, FileChooser.promptSelectFile("C:/", true, true).getAbsolutePath());
					pA.dispose();
					break;
				case UMLConfigValidation.CODE_FAILURE_FILE_MISSING:
					c.initializeDefaultConfig();
					break;
				default:
					break;
			}
		}
	}
	
	public void handleInput(int code, String ref) {
		
	}
	
	
	
}
