package test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import model.Manager;
import model.process.coobservability.Incremental;
import test.help.EventSets;
import test.help.RandomGeneration;
import test.help.SystemGeneration;

/**
 * 
 * Multithread so can bail on terminal tests; need some mechanism to let me ignore this running for 10 hours
 * 
 * Interface for setting up test configs, # of tests to run, overall data output
 * 
 * Incremental heuristics testing; small batch of random with all variations, all specific examples
 * 
 * Integrate specific examples from papers
 * 
 * Large single plant and specification examples
 * 
 * Denote when a test batch has fully finished so that incomplete tests are re-done (when memory exception, want to re-do that test fully)
 *  - May want to have it use existing system data to re-attempt? Should know how often they can't be done for a random sample size.
 * 
 * 2 or 3 max transitions in the random generation? May want to define that differently based on size.
 * 
 * 
 * 
 * Need to add multithreading, separate class that runs a thread of DataGathering where DataGathering pushes to the multithreader when
 * a test starts to start a counter; need internal way to pick back up a test and denote a DNF when severing thread/resetting memory.
 * 
 * 
 * 
 * @author aclevinger
 *
 */

public class DataGathering {
	
//---  Constants   ----------------------------------------------------------------------------

	private static final String RESULTS_FILE = "output.txt";
	
	private static final String ANALYSIS_FILE = "raw_num";
	
	private static final String TEXT_EXTENSION = ".txt";
	
	private static final String TEST_NAME = "test";
	
	private static final String VERIFY_COMPLETE_TEST = "!!~~Verified Complete and Done!~~!!";
	
	private static final String DECLARE_MEMORY_ERROR = "!!~~Possible Memory Exception~~!!";
	
	private static final String VERIFY_MEMORY_ERROR = "!!~~Verified Memory Exception~~!!";
	
	private static final int TEST_ALL = 0;
	private static final int TEST_BASIC = 1;
	private static final int TEST_INC = 2;
	private static final int TEST_HEUR = 3;
	
	private static final int TYPE_COOBS = 0;
	private static final int TYPE_SB = 1;
	private static final int TYPE_INC_COOBS = 2;
	private static final int TYPE_INC_SB = 3;
	private static final String ANALYSIS_COOBS = "_coobs";
	private static final String ANALYSIS_SB = "_sb";
	private static final String ANALYSIS_INC_COOBS = "_inc_coobs";
	private static final String ANALYSIS_INC_SB = "_inc_sb";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static Manager model;
	
	private static ArrayList<String> eventAtt;
	
	private static String defaultWritePath;
	
	private static String writePath;
	
	private static String analysisSubtype;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public static void main(String[] args) throws Exception{
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES, FiniteStateMachine.ADDRESS_CONFIG);
		FiniteStateMachine.fileConfiguration();
		model = new Manager();
		
		File f = new File(FiniteStateMachine.ADDRESS_IMAGES);
		f = f.getParentFile();
		
		f = new File(f.getAbsolutePath() + "/TestBatches/");
		f.mkdir();
		
		SystemGeneration.assignManager(model);
		
		eventAtt = new ArrayList<String>();
		for(String s : EventSets.EVENT_ATTR_LIST) {
			eventAtt.add(s);
		}
		

		initializeTestFolder(f, "/Test Batch Random Basic 1");
		testBasicConfigOne(100);
		initializeTestFolder(f, "/Test Batch Random Basic 2");
		testBasicConfigTwo(30);
		initializeTestFolder(f, "/Test Batch Random Basic 3");
		testBasicConfigThree(50);
		initializeTestFolder(f, "/Test Batch Random Basic 4");
		testBasicConfigFour(40);
		initializeTestFolder(f, "/Test Batch Random Inc 1");
		testIncConfigOne(100);
		initializeTestFolder(f, "/Test Batch Random Inc 2");
		testIncConfigTwo(75);
		initializeTestFolder(f, "/Test Batch Random Inc 3");
		testIncConfigThree(50);
		initializeTestFolder(f, "/Test Batch Random Heuristic 1");
		testHeuristicConfigOne(200);
		initializeTestFolder(f, "/Test Batch Random Heuristic 2");
		testHeuristicConfigTwo(200);
	}
	
	private static void initializeTestFolder(File f, String in) {
		defaultWritePath = f.getAbsolutePath() + in;
		File g = new File(defaultWritePath);
		g.mkdir();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- Test Data Interpretation  ----------------------------
	
	private static void interpretTestBatchData(String path) {
		int counter = 0;
		/*
		 * Need to know total number of tests ahead of time
		 * Minimum, maximum, average, interquartile range -> box plots
		 * 
		 * Pull data first then analyze
		 * 
		 */
		ArrayList<ArrayList<Double>> rawData = new ArrayList<ArrayList<Double>>();
		String[] attributes;
		File f = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + ANALYSIS_FILE);
		if(f.exists()) {
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				attributes = raf.readLine().split(", ");
				raf.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		
		while(f.exists()) {
			
		}
	}
	
	//-- Random Tests  ----------------------------------------
	
	  //- Basic ---------------------------
	  
	private static void testBasicConfigOne(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 2;
		int numSpecs = 2;
		
		int numStates = 4;
		int numStateVar = 2;
		int numEvents = 3;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 2;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Basic Config One: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_BASIC);
			garbageCollect();
			counter++;
		}
	}
	
	private static void testBasicConfigTwo(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 3;
		int numSpecs = 3;
		
		int numStates = 4;
		int numStateVar = 2;
		int numEvents = 3;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 2;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Basic Config Two: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_BASIC);
			garbageCollect();
			counter++;
		}
	}
	
	private static void testBasicConfigThree(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 1;
		int numSpecs = 1;
		
		int numStates = 20;
		int numStateVar = 4;
		int numEvents = 7;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 3;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Basic Config Three: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_BASIC);
			garbageCollect();
			counter++;
		}
	}
	
	private static void testBasicConfigFour(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 1;
		int numSpecs = 1;
		
		int numStates = 50;
		int numStateVar = 15;
		int numEvents = 8;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 5;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Basic Config Four: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_BASIC);
			garbageCollect();
			counter++;
		}
	}
	
	  //- Incremental ---------------------
	
	private static void testIncConfigOne(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 2;
		int numSpecs = 2;
		
		int numStates = 4;
		int numStateVar = 2;
		int numEvents = 3;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 2;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Inc Config One: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_INC);
			garbageCollect();
			counter++;
		}
	}
	
	private static void testIncConfigTwo(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 3;
		int numSpecs = 3;
		
		int numStates = 4;
		int numStateVar = 2;
		int numEvents = 3;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 2;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Inc Config Two: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_INC);
			garbageCollect();
			counter++;
		}
	}
	
	private static void testIncConfigThree(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 5;
		int numSpecs = 5;
		
		int numStates = 4;
		int numStateVar = 2;
		int numEvents = 3;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 3;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Inc Config Three: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_INC);
			garbageCollect();
			counter++;
		}
	}
	
	  //- Heuristics ----------------------
	
	private static void testHeuristicConfigOne(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 2;
		int numSpecs = 2;
		
		int numStates = 4;
		int numStateVar = 2;
		int numEvents = 3;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 2;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Heuristic Config One: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_HEUR);
			garbageCollect();
			counter++;
			
		}
	}
	
	private static void testHeuristicConfigTwo(int count) throws Exception{
		Incremental.assignIncrementalOptions(0, 1, 1);
		
		int numPlants = 5;
		int numSpecs = 5;
		
		int numStates = 4;
		int numStateVar = 2;
		int numEvents = 3;
		int numEventsVar = 2;
		
		double eventShareRate = .4;
		
		int numControllers = 4;
		int numControllersVar = 0;
		double controllerObserveRate = .4;
		double controllerControlRate = .3;
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Heuristic Config Two: " + counter);
			autoTestRandomSystem(counter + 1, numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate, TEST_HEUR);
			garbageCollect();
			counter++;
			
		}
	}
	
  //-- Specific Tests  ----------------------------------------
	
//---  System Testing   -----------------------------------------------------------------------
	
	//TODO: Function that lets multithread wrapper tell newest test file to print DECLARE_MEMORY_ERROR and VERIFY_MEMORY_ERROR
	
	private static void autoTestRandomSystem(int count, int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate, int testChoice) throws Exception {
		String testName = TEST_NAME + "_" +  count;
		File f;
		f = new File(defaultWritePath + "/" + testName);

		writePath = defaultWritePath + "/" + testName;
		
		boolean inMem = false;
		
		if(!f.exists()) {
			autoGenerateNewRandomSystem(count, numPlants, numSpecs, numStates, numStateVar, numEve, numEveVar, shareRate, numAgents, numAgentVar, obsRate, ctrRate);
			inMem = true;
		}
		
		boolean finished = checkForTerm(writePath + "/" + RESULTS_FILE, VERIFY_COMPLETE_TEST);

		if(finished)
			return;
		
		boolean memoryError = checkForTerm(writePath + "/" + RESULTS_FILE, VERIFY_MEMORY_ERROR);
		
		if(!memoryError && checkForTerm(writePath + "/" + RESULTS_FILE, DECLARE_MEMORY_ERROR)) {
			//TODO: Implies an actual crash ocurred, restart testing fresh delete output.txt
		}
		
		if(!inMem)
			readInOldSystem(testName);

		ArrayList<String> plants = getPlants(testName);
		ArrayList<String> specs = getSpecs(testName);
		ArrayList<HashMap<String, ArrayList<Boolean>>> agents = getAgents(testName);
		
		switch(testChoice) {
			case TEST_ALL:
				autoTestSystemFull(testName, plants, specs, agents);
				break;
			case TEST_BASIC:
				autoTestSystemCoobsSB(testName, plants, specs, agents, memoryError);
				break;
			case TEST_INC:
				autoTestSystemIncr(testName, plants, specs, agents, memoryError);
				break;
			case TEST_HEUR:
				autoTestHeuristics(testName, plants, specs, agents, memoryError);
				break;
			default:
				break;
		}
		confirmComplete();
		
	}
	
	private static void readInOldSystem(String prefixNom) throws FileNotFoundException {
		String path = writePath;
		ArrayList<String> plants = new ArrayList<String>();
		int counter = 0;	
		String hold = pullSourceData(path + "/" + prefixNom + "_p_" + counter++ + ".txt");
		while(hold != null) {
			plants.add(model.readInFSM(hold));
			hold = pullSourceData(path + "/" + prefixNom + "_p_" + counter++ + ".txt");
		}

		ArrayList<String> specs = new ArrayList<String>();
		counter = 0;
		hold = pullSourceData(path + "/" + prefixNom + "_s_" + counter++ + ".txt");
		while(hold != null) {
			specs.add(model.readInFSM(hold));
			hold = pullSourceData(path + "/" + prefixNom + "_s_" + counter++ + ".txt");
		}
	}

	private static void autoGenerateNewRandomSystem(int count, int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate) throws Exception {
		String testName = TEST_NAME + "_" +  count;
		File f;
		f = new File(defaultWritePath + "/" + testName);

		writePath = defaultWritePath + "/" + testName;

		f.mkdir();
		
		//System.out.println("This test: " + testName);
		printOut(testName + ", " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n---------------------------------------------\n");
		
		
		printOut("Test Configuration: Full Suite");
		printOut("Randomizer Parameters:");
		printOut(" Plants: " + numPlants + ", Specs: " + numSpecs + ", # States Average: " + numStates + ", State Variance: " + numStateVar + ", # Events Average: " + numEve + ", Event Variance: " + numEveVar + 
				", Event Share Rate: " + shareRate + ", # Agents: " + numAgents + ", Agent Variance: " + numAgentVar + ", Agent Obs. Event Rate: " + obsRate + ", Agent Ctr. Event Rate: " + ctrRate);
		printOut(" " + numPlants + ", " + numSpecs + ", " + numStates + ", " + numStateVar + ", " + numEve + ", " + numEveVar + ", " + shareRate + ", " + numAgents + ", " + numAgentVar + ", " + obsRate + ", " + ctrRate + "\n");
		printOut("---------------------------------------------\n");
		
		ArrayList<String> events = RandomGeneration.generateRandomSystemSet(testName, model, numPlants, numSpecs, numStates, numStateVar, numEve, numEveVar, shareRate);
		ArrayList<String> names = RandomGeneration.getComponentNames(testName, numPlants, numSpecs);

		ArrayList<HashMap<String, ArrayList<Boolean>>> agents = RandomGeneration.generateRandomAgents(events, numAgents, numAgentVar, obsRate, ctrRate);
		
		f = new File(writePath + "/" + (testName + "_agents.txt"));
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.writeBytes(model.exportAgents(testName + "_agents", agents, eventAtt));
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		printOut("Agent Information: \n" + agents.toString().replace("},", "},\n").replaceAll("[\\[\\]]", " "));
		printOut("\n---------------------------------------------\n");

		for(String s : names) {
			//makeImageDisplay(s, s);
			f = new File(writePath + "/" + s + ".txt");
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				raf.writeBytes(model.exportFSM(s));
				raf.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
			Files.move(new File(FormatConversion.createImgFromFSM(model.generateFSMDot(s), s)).toPath(), new File(writePath + "/" + s + ".png").toPath(), StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	private static void autoTestSystemFull(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		printCoobsLabel(prefixNom, false);
		boolean coobs = checkCoobservable(plantNames, specNames, agents, false);

		printIncrementalLabel(prefixNom, false);
		boolean icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
		
		printSBCoobsLabel(prefixNom);
		boolean sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
		printIncrementalSBLabel(prefixNom);
		boolean icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);

		if(coobs && !sbCoobs) {
			printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
		}
		
		boolean error = false;
		
		if(coobs != icCoobs) {
			printOut("~~~\nError!!! : Incremental Algo. did not return same as Coobs. Algo.\n~~~");
			error = true;
		}
		if(sbCoobs != icSbCoobs) {
			printOut("~~~\nError!!! : Incremental SB Algo. did not return same as SB Algo.\n~~~");
			error = true;
		}

		if(sbCoobs && !coobs) {
			printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
			error = true;
		}

		if(error) {
			throw new Exception("Logic Conflict in Data Output");
		}
		resetModel();
	}
	
	private static void autoTestSystemCoobsSB(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean memoryError) throws Exception{
		boolean coobs = false;
		if(!memoryError) {
			printCoobsLabel(prefixNom, false);
			coobs = checkCoobservable(plantNames, specNames, agents, false);
		}

		printSBCoobsLabel(prefixNom);
		boolean sbCoobs = checkSBCoobservable(plantNames, specNames, agents);

		
		if(!memoryError && (coobs && !sbCoobs)) {
			printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
		}
		
		boolean error = false;
		
		if(!memoryError && (sbCoobs && !coobs)) {
			printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
			error = true;
		}
		if(error) {
			throw new Exception("Logic Conflict in Data Output");
		}
		resetModel();
	}

	private static void autoTestSystemIncr(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean memoryError) throws Exception{
		boolean icCoobs = false;
		if(!memoryError) {
			printIncrementalLabel(prefixNom, false);
			icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
		}
		
		printSBCoobsLabel(prefixNom);
		boolean sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
		printIncrementalSBLabel(prefixNom);
		boolean icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);

		if(icCoobs && !sbCoobs) {
			printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
		}
		
		boolean error = false;
		
		if(sbCoobs != icSbCoobs) {
			printOut("~~~\nError!!! : Incremental SB Algo. did not return same as SB Algo.\n~~~");
			error = true;
		}
		if(!memoryError && (sbCoobs && !icCoobs)) {
			printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
			error = true;
		}
		if(error) {
			throw new Exception("Logic Conflict in Data Output");
		}
		resetModel();
	}
	
	private static void autoTestHeuristics(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean memoryError) throws Exception{
		Boolean expected = null;
		
		for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
			for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
				for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
					Incremental.assignIncrementalOptions(i, j, k);
					
					Boolean icCoobs = null;
					if(!memoryError) {
						printIncrementalLabel(prefixNom + " " + i + " " + j + " " + k, false);
						icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
					}
					
					if(expected == null) {
						expected = icCoobs;
					}
					
					printIncrementalSBLabel(prefixNom + " " + i + " " + j + " " + k);
					boolean icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);

					if(expected == null) {
						expected = icSbCoobs;
					}
					
					if((icCoobs != null && expected != icCoobs) || expected != icSbCoobs) {
						throw new Exception("Change in Heuristics caused difference result");
					}
				}
			}
		}
		resetModel();
	}

//---  Coobservability Testing   --------------------------------------------------------------

	private static boolean checkCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isInferenceCoobservableUStruct(plants, specs, eventAtt, agents) : model.isCoobservableUStruct(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_COOBS);
		handleOutData(t, hold);
		printOut("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private static void printCoobsLabel(String system, boolean type) {
		printOut(system + " " + (type ? "Inference Coobservability:" : "Coobservability:") + " \t");
	}
	
//---  SB Coobservability Testing   -----------------------------------------------------------

	private static boolean checkSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_SB);
		handleOutData(t, hold);
		printOut("\t\t\t\tSB-Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private static void printSBCoobsLabel(String system) {
		printOut(system + " SB Coobservability: \t");
	}

//---  Incremental Testing   ------------------------------------------------------------------
	
	private static boolean checkIncrementalCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isIncrementalInferenceCoobservable(plants, specs, eventAtt, agents) : model.isIncrementalCoobservable(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_INC_COOBS);
		handleOutData(t, hold);
		printOut("\t\t\t\tIncremental" + (inf ? " Inference" : "") + " Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private static boolean checkIncrementalSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isIncrementalSBCoobservable(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_INC_SB);
		handleOutData(t, hold);
		printOut("\t\t\t\tIncremental SB Coobservable: " + result);
		garbageCollect();
		return result;
	}
	
	private static void printIncrementalLabel(String system, boolean inf) {
		printOut(system + " Incremental" + (inf ? " Inference" : "") + " Coobservability: \t");
	}

	private static void printIncrementalSBLabel(String system) {
		printOut(system + " Incremental SB Coobservability: \t");
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static void assignAnalysisSubtype(int in) {
		switch(in) {
			case TYPE_COOBS:
				analysisSubtype = ANALYSIS_COOBS;
				break;
			case TYPE_SB:
				analysisSubtype = ANALYSIS_SB;
				break;
			case TYPE_INC_COOBS:
				analysisSubtype = ANALYSIS_INC_COOBS;
				break;
			case TYPE_INC_SB:
				analysisSubtype = ANALYSIS_INC_SB;
				break;
			default:
				analysisSubtype = "";
		}
	}
	
	//-- Data Output Gathering  -------------------------------
	
	private static void handleOutData(long t, long hold) {
		printOut(model.getLastProcessData().produceOutputLog());
		long res = (System.currentTimeMillis() - t);
		printTimeTook(res);
		double val = inMB(getCurrentMemoryUsage() - hold);
		val = val < 0 ? 0 : val;
		printMemoryUsage(val);
		ArrayList<Double> data = model.getLastProcessData().getStoredData();
		ArrayList<String> use = model.getLastProcessData().getOutputGuide();
		
		use.add(0, "Total Time (ms)");
		use.add(1, "Overall Memory Usage (Mb)");
		
		printEquivalentResults(use, res, val, data);
	}

	private static void printTimeTook(long t) {
		printOut("\t\t\t\tTook " + t + " ms");
	}
	
	private static void printMemoryUsage(double reduction) {
		printOut("\t\t\t\tUsing " + threeSig(reduction) + " Mb");
		garbageCollect();
	}
	
	private static long getCurrentMemoryUsage() {
		Runtime r = Runtime.getRuntime();
		return ((r.totalMemory() - r.freeMemory()));
	}
	
	private static double inMB(long in) {
		return (double)in / 1000000;
	}
	
	private static Double threeSig(double in) {
		String use = in+"0000";
		int posit = use.indexOf(".") + 4;
		return Double.parseDouble(use.substring(0, posit));
	}

	private static void confirmComplete() {
		printOut("\n" + VERIFY_COMPLETE_TEST + "\n");
	}
	
	//-- File Output  -----------------------------------------
	
	private static void printOut(String text) {
		if(writePath != null) {
			File f = new File(writePath + "/" + RESULTS_FILE);
			try {
				RandomAccessFile raf = new RandomAccessFile(f, "rw");
				raf.seek(raf.length());
				raf.writeBytes(text + "\n");
				raf.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	private static void printEquivalentResults(ArrayList<String> guide, long time, double overallMem, ArrayList<Double> vals) {
		if(writePath != null) {
			File f = new File(writePath + "/" + ANALYSIS_FILE + analysisSubtype + TEXT_EXTENSION);
			try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.seek(raf.length());
			if(raf.length() == 0) {
				for(String s : guide)
					raf.writeBytes(s + ", ");
				raf.writeBytes("\n");
			}
			raf.writeBytes(time + ", \t" + threeSig(overallMem) + ", \t");
			for(Double d : vals) {
				raf.writeBytes(threeSig(d) + ", \t");
			}
			raf.writeBytes("\n");
			raf.close();
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
	}

	//-- Data Input  ------------------------------------------
	
	private static String pullSourceData(String path) throws FileNotFoundException {
		File f = new File(path);
		if(f.exists()) {
			StringBuilder sb = new StringBuilder();
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				sb.append(sc.nextLine() + "\n");
			}
			sc.close();
			return sb.toString();
		}
		return null;
	}

	private static boolean checkForTerm(String path, String phrase) {
		File g = new File(path);
		try {
			RandomAccessFile raf = new RandomAccessFile(g, "r");
			String line = raf.readLine();
			while(line != null && !line.equals(phrase)) {
				line = raf.readLine();
			}
			raf.close();
			return line != null && line.equals(phrase);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return false;
	}
	
	private static ArrayList<String> getPlants(String prefix) throws Exception{
		ArrayList<String> plants = new ArrayList<String>();
		int counter = 0;	
		String hold = pullSourceData(writePath + "/" + prefix + "_p_" + counter++ + ".txt");
		while(hold != null) {
			plants.add(model.readInFSM(hold));
			hold = pullSourceData(writePath + "/" + prefix + "_p_" + counter++ + ".txt");
		}
		return plants;
	}
	
	private static ArrayList<String> getSpecs(String prefix) throws Exception{
		ArrayList<String> plants = new ArrayList<String>();
		int counter = 0;	
		String hold = pullSourceData(writePath + "/" + prefix + "_s_" + counter++ + ".txt");
		while(hold != null) {
			plants.add(model.readInFSM(hold));
			hold = pullSourceData(writePath + "/" + prefix + "_s_" + counter++ + ".txt");
		}
		return plants;
	}
	
	private static ArrayList<HashMap<String, ArrayList<Boolean>>> getAgents(String prefix) throws Exception{
		String hold = pullSourceData(writePath + "/" + prefix + "_agents.txt");
		return model.readInAgents(hold);
	}

	//-- Model Management  ------------------------------------
	
	private static void garbageCollect() {
		System.gc();
		Runtime.getRuntime().gc();
	}
	
	private static void resetModel() {
		model.flushFSMs();
		garbageCollect();
	}

}
