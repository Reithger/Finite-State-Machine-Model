package controller.convert;

import java.io.File;

/**
 * This class handles the transfer of an object from the fsm package to a .dot format
 * so that it may be visually represented as a graph via graphviz, producing a .jpg
 * image at a hard-coded location using a name given at the time of creation.
 * 
 * In addition, it also serves to create an .svg file type as well as convert that
 * file into a .tikz file for use in LaTEX or other programs that read through that.
 * 
 * TODO: Config File is referenced straight from the package, definite location, do not need file nameing.
 * 
 * This class is a part of the graphviz package.
 * 
 * @author Ada Clevinger and Graeme Zinck
 *
 */

public class FormatConversion {
	
//---  Constant Values   ----------------------------------------------------------------------
	
	private final static String TYPE_SVG = "svg";
	private final static String TYPE_JPG = "jpg";
	private final static int DPI_INCREASE = 8;
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static String CONFIG_PATH;
	private static String WORKING_PATH;
	private static boolean initialized;
	
//---  Initialization   -----------------------------------------------------------------------
	
	public static void assignPaths(String workingPath, String configPath) {
		WORKING_PATH = workingPath;
		CONFIG_PATH = configPath;
		initialized = true;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method takes any object extending the TransitionSystem class and converts it to the 
	 * dot-String-format, using that to generates a .jpg image from it using the GraphViz library.
	 * 
	 * @param fsm - A generic TransitionSystem object that will be converted into a .jpg image.
	 * @param name - A String object denoting the name to which the file should be saved, including its name.
	 * @param workingPath - A String object denoting the name to the GraphViz working directory.
	 * @param FSMUI.ADDRESS_CONFIG - A String object denoting the name to the GraphViz config file.
	 */
	
	public static String createImgFromFSM(String fsm, String name){
		if(initializeCheck()) {
		    return generateDotFile(fsm, name, TYPE_JPG).getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * This method creates a file representing the provided TransitionSystem object in the .svg format,
	 * saving it to a location as defined by the caller. (.svg format is a graphical view of the graph,
	 * but is composed of a series of instructions on how to draw the graph to be interpreted by another
	 * program. This permits its conversion in another method in this class.)
	 * 
	 * @param fsm - A TransitionSystem extending object that will be converted into .svg format.
	 * @param name - A String object denoting the name to which the file should be saved, including its name.
	 * @param workingPath - A String object denoting the name to the GraphViz working directory.
	 * @param FSMUI.ADDRESS_CONFIG - A String object denoting the name to the GraphViz config file.
	 */
	
	public static String createSVGFromFSM(String fsm, String name){
		if(initializeCheck()) {
			return generateDotFile(fsm, name, TYPE_SVG).getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * This method converts a provided file in the .svg format into a file in the .tikz
	 * format for use with LaTEX programs. It calls a support class that can be used disjoint
	 * from this project to perform the same feat.
	 * 
	 * @param svgFile - A File object containing a TransitionSystem described in the .svg format.
	 * @param name - A String object representing the file name to save the new file to.
	 */
	
	public static String createTikZFromSVG(String fsm, String name) {
		if(initializeCheck()) {
			return SVGtoTikZ.convertSVGToTikZ(generateDotFile(fsm, name, TYPE_SVG), WORKING_PATH + "//" + name).getAbsolutePath();
		}
		return null;
	}
	
	/**
	 * This method takes in a TransitionSystem object and converts it into a representative file
	 * of the .tikz format; it firts converts the object to .svg from which it is converted to .tikz
	 * via the createTikZFromSVG() method included in this class. (Deleting the interim file.)
	 * 
	 * @param fsm - A TransitionSystem extending object that will be converted into .tikz format.
	 * @param name - A String object denoting the name to which the file should be saved, including its name.
	 * @param workingPath - A String object denoting the name to the GraphViz working directory.
	 * @param FSMUI.ADDRESS_CONFIG - A String object denoting the name to the GraphViz config file.
	 */

	public static String createTikZFromFSM(String fsm, String name) {
		if(initializeCheck()) {
		    File out = generateDotFile(fsm, "DEMOLISH", TYPE_SVG);
		    String ret = SVGtoTikZ.convertSVGToTikZ(out, WORKING_PATH + "//" + name).getAbsolutePath();
		    return ret;
		}
		return null;
	}
	
//---  Support Functions   --------------------------------------------------------------------
	
	public static File generateDotFile(String fsm, String name, String type) {
	    GraphViz gv = new GraphViz(WORKING_PATH, CONFIG_PATH);
	    gv.addln(gv.start_graph());
	    gv.add(fsm);
	    gv.addln(gv.end_graph());
	    for(int i = 0; i < DPI_INCREASE; i++) {
	    	gv.increaseDpi();
	    }
	    File out = new File(WORKING_PATH + "//" + name + "." + type);
	    gv.writeGraphToFile(gv.getGraph(gv.getDotSource(), type), out);
	    return out;
	}
	
	private static boolean initializeCheck() {
		if(initialized) {
			return true;
		}
		else {
			return false;
		}
	}
	
}