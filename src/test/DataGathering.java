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
 * 
 * Integrate specific examples from papers, make sure run heuristics on them
 * 
 * 2 or 3 max transitions in the random generation? May want to define that differently based on size.
 * 
 * 
 * 
 * Heuristics incremental tests should also compare against 100 true-random heuristic examples averaged
 * 
 * Need proper memory failure handling in heuristic tests
 * 
 * MemoryMeasure should probably be a HashMap instead of synchronized lists; when writing to file map each key to an index to associate value properly; use dummy value to denote it's missing
 * 
 * Incorporate DNF values into analysis
 * 
 * Heuristic: Alternate choosing Plants or Specs
 * 
 * @author aclevinger
 *
 */

public class DataGathering {
	
//---  Constants   ----------------------------------------------------------------------------

	private final String RESULTS_FILE = "output.txt";
	
	private final String ANALYSIS_FILE = "raw_num";
	
	private final String TEXT_EXTENSION = ".txt";
	
	private final String TEST_NAME = "test";
	
	private final String VERIFY_COMPLETE_TEST = "!!~~Verified Complete and Done!~~!!";
	
	private final String DECLARE_MEMORY_ERROR = "!!~~Possible Memory Exception~~!!";
	
	private final String VERIFY_MEMORY_ERROR = "!!~~Verified Memory Exception~~!!";
	
	private final int TEST_ALL = 0;
	private final int TEST_BASIC = 1;
	private final int TEST_INC = 2;
	private final int TEST_HEUR = 3;
	
	private final int TYPE_COOBS = 0;
	private final int TYPE_SB = 1;
	private final int TYPE_INC_COOBS = 2;
	private final int TYPE_INC_SB = 3;
	private final String ANALYSIS_COOBS = "_coobs";
	private final String ANALYSIS_SB = "_sb";
	private final String ANALYSIS_INC_COOBS = "_inc_coobs";
	private final String ANALYSIS_INC_SB = "_inc_sb";
	
	private final String[] TEST_NAMES = new String[] {"/Test Batch Random Basic 1",
															 "/Test Batch Random Basic 2",
															 "/Test Batch Random Basic 3",
															 "/Test Batch Random Basic 4",
															 "/Test Batch Random Inc 1",
															 "/Test Batch Random Inc 2",
															 "/Test Batch Random Inc 3",
															 "/Test Batch Random Heuristic 1",
															 "/Test Batch Random Heuristic 2"};
	private final int[] TEST_SIZES = new int[] {150,
													   75,
													   50,
													   40,
													   100,
													   75,
													   50,
													   200,
													   200};
	
	private boolean finished;
	
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private Manager model;
	
	private ArrayList<String> eventAtt;
	
	private String defaultWritePath;
	
	private String writePath;
	
	private String analysisSubtype;
	
	private TestReset clock;
	
//---  Constructors   -------------------------------------------------------------------------
	
	public DataGathering(TestReset inClock) {
		clock = inClock;
	}

	public void allInOneRunTests() throws Exception{
		finished = false;
		runTests(initializeDataGathering());
		finished = true;
	}
	
	public boolean getFinished() {
		return finished;
	}
	
	public File initializeDataGathering() {
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
		
		defaultWritePath = f.getAbsolutePath();
		
		return f;
	}
	
	public void runTests(File f) throws Exception{
		
		//Coobs and SB
		int testNum = 0;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testBasicConfigOne(TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_SB, TEST_SIZES[testNum]);
		testNum = 1;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testBasicConfigTwo(TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_SB, TEST_SIZES[testNum]);
		testNum = 2;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testBasicConfigThree(TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_SB, TEST_SIZES[testNum]);
		testNum = 3;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testBasicConfigFour(TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]);
		interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_SB, TEST_SIZES[testNum]);
		
		// SB and Inc
		testNum = 4;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testIncConfigOne(TEST_SIZES[testNum]);
		testNum = 5;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testIncConfigTwo(TEST_SIZES[testNum]);
		testNum = 6;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testIncConfigThree(TEST_SIZES[testNum]);
		
		//Heuristics Test (SB Inc and Coobs Inc)
		testNum = 7;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testHeuristicConfigOne(TEST_SIZES[testNum]);
		testNum = 8;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		testHeuristicConfigTwo(TEST_SIZES[testNum]);
		
		
	}
	
	private void initializeTestFolder(File f, String in) {
		defaultWritePath = f.getAbsolutePath() + in;
		File g = new File(defaultWritePath);
		g.mkdir();
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- Test Data Interpretation  ----------------------------
	
	private void interpretTestBatchDataSimple(String path, String type, int size) {
		int counter = 1;
		
		InterpretData hold = new InterpretData();
		String[] attributes = null;
		File f = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + ANALYSIS_FILE + type + ".txt");

		try {
			RandomAccessFile raf;
			while(counter <= size+1) {
				if(f.exists()) {
					raf = new RandomAccessFile(f, "rw");
					boolean skip = false;
					if(attributes == null) {
						String line = raf.readLine();
						if(line != null)
							attributes = line.split(", ");
						else
							skip = true;
					}
					else {
						raf.readLine();
					}
					if(!skip) {
						String[] values = raf.readLine().split(", ");
						while(values != null) {
							for(int i = 0; i < values.length; i++) {
								values[i] = values[i].trim();
							}
							hold.addDataRow(values);
							
							String next = raf.readLine();
							if(next != null) {
								values = next.split(",");
							}
							else {
								values = null;
							}
						}
					}
					raf.close();
				}
				f = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + ANALYSIS_FILE + type + ".txt");
			}
		}
		catch(Exception e) {
			System.out.println(f.getAbsolutePath());
			e.printStackTrace();
		}
		
		f = new File(path + "/analysis" + type + ".txt");
		f.delete();
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			raf.writeBytes("\t\t\t\t\t\t");
			for(String s : attributes) {
				raf.writeBytes(s + ", ");
			}
			raf.writeBytes("\nAverage: \t\t\t\t");
			for(double v : hold.calculateAverages()) {
				raf.writeBytes(threeSig(v) + ", \t");
			}
			raf.writeBytes("\nMinimum: \t\t\t\t");
			for(double v : hold.calculateMinimums()) {
				raf.writeBytes(threeSig(v) + ", \t");
			}
			raf.writeBytes("\nMaximum: \t\t\t\t");
			for(double v : hold.calculateMaximums()) {
				raf.writeBytes(threeSig(v) + ", \t");
			}
			raf.writeBytes("\nMedian: \t\t\t\t");
			for(double v : hold.calculateMedians()) {
				raf.writeBytes(threeSig(v) + ", \t");
			}
			raf.writeBytes("\nInter Quart Range:\t\t");
			for(double v : hold.calculateInterquartileRange()) {
				raf.writeBytes(threeSig(v) + ", \t");
			}
			raf.writeBytes("\nNumber Valid Tests:\t\t");
			for(Integer v : hold.getColumnSizes()) {
				raf.writeBytes(v + ", \t");
			}
			raf.writeBytes("\n\nNumber True Results:\t" + hold.getNumberTrueResults());
			raf.writeBytes("\nNumber False Results:\t" + hold.getNumberFalseResults());
			raf.writeBytes("\nNumber of DNFs:\t\t\t" + hold.getNumberDNF(size));
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	
	//-- Random Tests  ----------------------------------------
	
	  //- Basic ---------------------------
	  
	private void testBasicConfigOne(int count) throws Exception{
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
			resetModel();
			counter++;
		}
	}
	
	private void testBasicConfigTwo(int count) throws Exception{
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
			resetModel();
			counter++;
		}
	}
	
	private void testBasicConfigThree(int count) throws Exception{
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
			resetModel();
			counter++;
		}
	}
	
	private void testBasicConfigFour(int count) throws Exception{
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
			resetModel();
			counter++;
		}
	}
	
	  //- Incremental ---------------------
	
	private void testIncConfigOne(int count) throws Exception{
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
			resetModel();
			counter++;
		}
	}
	
	private void testIncConfigTwo(int count) throws Exception{
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
			resetModel();
			counter++;
		}
	}
	
	private void testIncConfigThree(int count) throws Exception{
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
			resetModel();
			counter++;
		}
	}
	
	  //- Heuristics ----------------------
	
	private void testHeuristicConfigOne(int count) throws Exception{
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
			resetModel();
			counter++;
			
		}
	}
	
	private void testHeuristicConfigTwo(int count) throws Exception{
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
			resetModel();
			counter++;
			
		}
	}
	
  //-- Specific Tests  ----------------------------------------
	
//---  System Testing   -----------------------------------------------------------------------
	
	//TODO: Function that lets multithread wrapper tell newest test file to print DECLARE_MEMORY_ERROR and VERIFY_MEMORY_ERROR
	
	public void markTestUnfinished() {
		if(checkTestDeclaredMemoryError(writePath) && !checkTestVerifiedMemoryError(writePath)) {
			printOut(VERIFY_MEMORY_ERROR);
		}
		else{
			printOut(DECLARE_MEMORY_ERROR);
		}
	}
	
	private boolean checkTestVerifiedComplete(String path) {
		return checkForTerm(path + "/" + RESULTS_FILE, VERIFY_COMPLETE_TEST);
	}
	
	private boolean checkTestDeclaredMemoryError(String path) {
		return checkForTerm(path + "/" + RESULTS_FILE, DECLARE_MEMORY_ERROR);
	}
	
	private boolean checkTestVerifiedMemoryError(String path) {
		return checkForTerm(path + "/" + RESULTS_FILE, VERIFY_MEMORY_ERROR);
	}
	
	private void autoTestRandomSystem(int count, int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate, int testChoice) throws Exception {
		String testName = TEST_NAME + "_" +  count;
		File f;
		f = new File(defaultWritePath + "/" + testName);

		writePath = defaultWritePath + "/" + testName;

		boolean inMem = false;
		
		if(!f.exists()) {
			autoGenerateNewRandomSystem(count, numPlants, numSpecs, numStates, numStateVar, numEve, numEveVar, shareRate, numAgents, numAgentVar, obsRate, ctrRate);
			inMem = true;
		}
		
		boolean finished = checkTestVerifiedComplete(writePath);

		if(finished)
			return;
		
		boolean memoryError = checkTestVerifiedMemoryError(writePath);

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
	
	private void readInOldSystem(String prefixNom) throws FileNotFoundException {
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

	private void autoGenerateNewRandomSystem(int count, int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate) throws Exception {
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
	
	private void autoTestSystemFull(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
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
	
	private void autoTestSystemCoobsSB(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean memoryError) throws Exception{
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

	private void autoTestSystemIncr(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean memoryError) throws Exception{
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
	
	private void autoTestHeuristics(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean memoryError) throws Exception{
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

	private boolean checkCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf)throws Exception {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isInferenceCoobservableUStruct(plants, specs, eventAtt, agents) : model.isCoobservableUStruct(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_COOBS);
		handleOutData(t, hold);
		printOut("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private void printCoobsLabel(String system, boolean type) {
		printOut(system + " " + (type ? "Inference Coobservability:" : "Coobservability:") + " \t");
	}
	
//---  SB Coobservability Testing   -----------------------------------------------------------

	private boolean checkSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_SB);
		handleOutData(t, hold);
		printOut("\t\t\t\tSB-Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private void printSBCoobsLabel(String system) {
		printOut(system + " SB Coobservability: \t");
	}

//---  Incremental Testing   ------------------------------------------------------------------
	
	private boolean checkIncrementalCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) throws Exception{
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isIncrementalInferenceCoobservable(plants, specs, eventAtt, agents) : model.isIncrementalCoobservable(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_INC_COOBS);
		handleOutData(t, hold);
		printOut("\t\t\t\tIncremental" + (inf ? " Inference" : "") + " Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private boolean checkIncrementalSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isIncrementalSBCoobservable(plants, specs, eventAtt, agents);
		assignAnalysisSubtype(TYPE_INC_SB);
		handleOutData(t, hold);
		printOut("\t\t\t\tIncremental SB Coobservable: " + result);
		garbageCollect();
		return result;
	}
	
	private void printIncrementalLabel(String system, boolean inf) {
		printOut(system + " Incremental" + (inf ? " Inference" : "") + " Coobservability: \t");
	}

	private void printIncrementalSBLabel(String system) {
		printOut(system + " Incremental SB Coobservability: \t");
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private void assignAnalysisSubtype(int in) {
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
	
	private void handleOutData(long t, long hold) {
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

	private void printTimeTook(long t) {
		printOut("\t\t\t\tTook " + t + " ms");
	}
	
	private void printMemoryUsage(double reduction) {
		printOut("\t\t\t\tUsing " + threeSig(reduction) + " Mb");
		garbageCollect();
	}
	
	private long getCurrentMemoryUsage() {
		Runtime r = Runtime.getRuntime();
		return ((r.totalMemory() - r.freeMemory()));
	}
	
	private double inMB(long in) {
		return (double)in / 1000000;
	}
	
	private Double threeSig(double in) {
		String use = (in < 0 ? 0 : in)+"0000";
		int posit = use.indexOf(".") + 4;
		return Double.parseDouble(use.substring(0, posit));
	}

	private void confirmComplete() {
		printOut("\n" + VERIFY_COMPLETE_TEST + "\n");
		clock.resetClock();
	}
	
	//-- File Output  -----------------------------------------
	
	private void printOut(String text) {
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

	private void printEquivalentResults(ArrayList<String> guide, long time, double overallMem, ArrayList<Double> vals) {
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
				if(d != null)
					raf.writeBytes(threeSig(d) + ", \t");
				else {
					raf.writeBytes("\n,,\t\t\t");
				}
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
	
	private String pullSourceData(String path) throws FileNotFoundException {
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

	private boolean checkForTerm(String path, String phrase) {
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
	
	private ArrayList<String> getPlants(String prefix) throws Exception{
		ArrayList<String> plants = new ArrayList<String>();
		int counter = 0;	
		String hold = pullSourceData(writePath + "/" + prefix + "_p_" + counter++ + ".txt");
		while(hold != null) {
			plants.add(model.readInFSM(hold));
			hold = pullSourceData(writePath + "/" + prefix + "_p_" + counter++ + ".txt");
		}
		return plants;
	}
	
	private ArrayList<String> getSpecs(String prefix) throws Exception{
		ArrayList<String> plants = new ArrayList<String>();
		int counter = 0;	
		String hold = pullSourceData(writePath + "/" + prefix + "_s_" + counter++ + ".txt");
		while(hold != null) {
			plants.add(model.readInFSM(hold));
			hold = pullSourceData(writePath + "/" + prefix + "_s_" + counter++ + ".txt");
		}
		return plants;
	}
	
	private ArrayList<HashMap<String, ArrayList<Boolean>>> getAgents(String prefix) throws Exception{
		String hold = pullSourceData(writePath + "/" + prefix + "_agents.txt");
		return model.readInAgents(hold);
	}

	//-- Model Management  ------------------------------------
	
	private void garbageCollect() {
		System.gc();
		Runtime.getRuntime().gc();
	}
	
	private void resetModel() {
		model.flushFSMs();
		garbageCollect();
	}

}
