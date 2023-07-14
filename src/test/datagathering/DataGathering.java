package test.datagathering;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import model.Manager;
import model.process.coobservability.Incremental;
import test.help.AgentChicanery;
import test.help.EventSets;
import test.help.RandomGenStats;
import test.help.RandomGeneration;
import test.help.SystemGeneration;

/**
 * 
 * Make a test batch keep going until minimum number of true/false are met (25 of each?)
 * 
 * Make each test occur 3 times to average results, and do general analysis of variance
 * 
 * 
 * 
 * Integrate specific examples from papers, make sure run heuristics on them
 * 
 * 2 or 3 max transitions in the random generation? May want to define that differently based on size.
 * 
 * 
 * 
 * Heuristics incremental tests should also compare against 100 true-random heuristic examples averaged

 * MemoryMeasure should probably be a HashMap instead of synchronized lists; when writing to file map each key to an index to associate value properly; use dummy value to denote it's missing

 * Heuristic: Alternate choosing Plants or Specs

 * Possibility on skipping redoing a complete test to not note its result for comparison to other actively calculated results. Need to retrieve result somehow.
 *  - A logic error could slip through if a later test finished after a preliminary crash and prior test results aren't in memory
 *  
 * Retain some progress data for incremental by assigning memory measure object before test begins, then save output of that when marking unfinished?
 *  - Decide how much data we want; could track every sub-system along the way and not just the final one before resetting, but may be messy/hard to interpret.

 * @author aclevinger
 *
 */

public class DataGathering {
	
//---  Constants   ----------------------------------------------------------------------------

	private final String RESULTS_FILE = "output.txt";
	
	private final String ANALYSIS_FILE = "raw_num";
	
	private final String PROCESS_FILE = "analysis";
	
	private final String RAW_DATA_FILE = "raw_data";
	
	private final String TEXT_EXTENSION = ".txt";
	
	private final String TEST_NAME = "test";
	
	private final String VERIFY_COMPLETE_TEST = "!!~~Verified Complete and Done!~~!!";
	
	private final String DECLARE_MEMORY_ERROR = "!!~~Possible Memory Exception~~!!";
	
	private final String VERIFY_MEMORY_ERROR = "!!~~Verified Memory Exception~~!!";
	
	private final String VERIFY_COMPLETE_CHECKPOINT = "!!~~Verified Subtest Complete~~!!";
	
	private final int MINIMUM_TRUE_RESULTS = 25;
	
	private final int NUMBER_REPEAT_TEST = 3;
	
	private final int NUMBER_EXISTING_TEST_RUNS = 50;
	
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
	
	private final String[] ANALYSIS_TYPES = new String[] {ANALYSIS_COOBS, ANALYSIS_SB, ANALYSIS_INC_COOBS, ANALYSIS_INC_SB};
	
	private final String[] TEST_NAMES = new String[] {"/Test Batch Random Basic 1",
															 "/Test Batch Random Basic 2",
															 "/Test Batch Random Basic 3",
															 "/Test Batch Random Basic 4",
															 "/Test Batch Random Inc 1",
															 "/Test Batch Random Inc 2",
															 "/Test Batch Random Inc 3",
															 "/Test Batch Random Heuristic 1",
															 "/Test Batch Random Heuristic 2",
															 "/Test Batch Basic DTP",
															 "/Test Basic Batch HISC"};
	private final int[] TEST_SIZES = new int[] {	   150,
													   100,
													   100,
													   100,
													   150,
													   125,
													   75,
													   200,
													   200,
													   NUMBER_EXISTING_TEST_RUNS,
													   NUMBER_EXISTING_TEST_RUNS};
	
	
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private Manager model;
	
	private ArrayList<String> eventAtt;
	
	private String defaultWritePath;
	
	private String writePath;
	
	private String analysisSubtype;
	
	private TestReset clock;
	
	private boolean finished;
	
	private boolean heuristics;
	
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
		heuristics = false;
		
		boolean runThrough = false ;			//set true if you want to force it to go through and rewrite the analysis files/check for gaps
		
		//resetDataGathered(f);
		
		
		//Existing Tests
		
		initializeTestFolder(f, TEST_NAMES[9]);
		if(!testsCompletedNonRandom(TEST_NAMES[9], ANALYSIS_COOBS, TEST_SIZES[9]) || runThrough) {
			testBasicConfigDTP();
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		
		initializeTestFolder(f, TEST_NAMES[10]);
		if(!testsCompletedNonRandom(TEST_NAMES[10], ANALYSIS_COOBS, TEST_SIZES[9]) || runThrough) {
			testBasicConfigHISC();
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		
		//Coobs and SB
		int testNum = 0;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testBasicConfigOne(TEST_SIZES[testNum]);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		testNum = 1;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testBasicConfigTwo(TEST_SIZES[testNum]);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		testNum = 2;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testBasicConfigThree(TEST_SIZES[testNum]);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		testNum = 3;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testBasicConfigFour(TEST_SIZES[testNum]);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_COOBS);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		
		// SB and Inc
		testNum = 4;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testIncConfigOne(TEST_SIZES[testNum]);
			interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
			interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
			interpretTestBatchDataSimple(f.getAbsolutePath() + "/" + TEST_NAMES[testNum], ANALYSIS_SB);
		}
		testNum = 5;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testIncConfigTwo(TEST_SIZES[testNum]);
			interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
			interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		testNum = 6;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testIncConfigThree(TEST_SIZES[testNum]);
			interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_COOBS);
			interpretTestBatchDataIncremental(defaultWritePath, ANALYSIS_INC_SB);
			interpretTestBatchDataSimple(defaultWritePath, ANALYSIS_SB);
		}
		
		heuristics = true;
		//Heuristics Test (SB Inc and Coobs Inc)
		testNum = 7;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testHeuristicConfigOne(TEST_SIZES[testNum]);
		}
		testNum = 8;
		initializeTestFolder(f, TEST_NAMES[testNum]);
		if(!testsCompleted(TEST_NAMES[testNum], ANALYSIS_INC_COOBS, TEST_SIZES[testNum]) || runThrough) {
			testHeuristicConfigTwo(TEST_SIZES[testNum]);
		}
		
		
	}
	
	private void initializeTestFolder(File f, String in) {
		defaultWritePath = f.getAbsolutePath() + in;
		File g = new File(defaultWritePath);
		g.mkdir();
	}
	
	private boolean testsCompleted(String testBatch, String type, int maxSize) {
		InterpretData d = generateInterpretDataSimple(defaultWritePath, type);
		int totalTests = d.getTotalNumberTests();
		int trueResults = d.getNumberTrueResults();
		if(!(trueResults >= MINIMUM_TRUE_RESULTS)) {
			System.out.println(testBatch + " in progress at: " + trueResults + " true outcomes of " + totalTests + " total tests.");
			return false;
		}
		if(checkTestNumberVerifiedComplete(defaultWritePath + "/" + TEST_NAME + "_" + maxSize) != NUMBER_REPEAT_TEST) {
			System.out.println(testBatch + " in progress at: " + trueResults + " true outcomes of " + totalTests + " total tests.");
			return false;
		}
		System.out.println(testBatch + " already complete at: " + totalTests + " tests");
		System.out.println(testBatch + " satisfies minimum true results: " + trueResults);
		return true;
	}
	
	private boolean testsCompletedNonRandom(String testBatch, String type, int maxSize) {
		InterpretData d = generateInterpretDataSimple(defaultWritePath, type);
		int totalTests = d.getTotalNumberTests();
		if(checkTestNumberVerifiedComplete(defaultWritePath + "/" + TEST_NAME + "_" + maxSize) != NUMBER_REPEAT_TEST) {
			System.out.println(checkTestNumberVerifiedComplete(defaultWritePath + "/" + TEST_NAME + "_" + maxSize));
			System.out.println(defaultWritePath + "/" + TEST_NAME + "_" + maxSize);
			return false;
		}
		System.out.println(testBatch + " already complete at: " + totalTests + " tests");
		return true;
	}
	
//---  Operations   ---------------------------------------------------------------------------
	
	//-- Test Data Interpretation  ----------------------------
	
	private void interpretTestBatchDataSimple(String path, String type) {
		generateRawDataFileSimple(path, type);
		outputInterpretDataSimple(generateInterpretDataSimple(path, type), path, type);
	}
	
	private int getTotalNumberTests(String path) {
		int validTests = 0;
		int counter = 0;

		int size = new File(path).list().length;
		while(counter <= size + 1) {
			File g = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + RESULTS_FILE);
			if(g.exists()) {
				validTests++;
			}
		}
		
		return validTests;
	}
		
	private void generateRawDataFileSimple(String path, String type) {
		int counter = 0;

		int size = new File(path).list().length;
		String[] attributes = null;
		
		File f = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + ANALYSIS_FILE + type + ".txt");
		File g = new File(path + "/" + RAW_DATA_FILE + type + ".txt");

		
		try {
			RandomAccessFile raf;
			RandomAccessFile rag = new RandomAccessFile(g, "rw");
			while(counter <= size+1) {

				if(f.exists()) {
					raf = new RandomAccessFile(f, "rw");
					boolean skip = false;
					if(attributes == null) {
						String line = raf.readLine();
						if(line != null) {
							attributes = line.split(", ");
							rag.writeBytes(line + "\n");
						}
						else
							skip = true;
					}
					else {
						raf.readLine();
					}
					InterpretData hold = new InterpretData();
					//TODO: Need to create the raw file that averages the numbers then read the raw file into this
					if(!skip) {
						String line = raf.readLine();
						String[] values = line.split(", ");
						if(values[values.length - 1].equals("")) {
							values = Arrays.copyOf(values, values.length - 2);
						}
						while(values != null) {
							for(int i = 0; i < values.length; i++) {
								values[i] = values[i].trim();
							}
							hold.addDataRow(values);
							String next = raf.readLine();
							values = next == null ? null : next.split(", ");
						}
						ArrayList<Double> aver = hold.calculateAverages();
						//Should have some way to output general variance using interquartile range; or, for 3 averaged numbers, something else?
						//System.out.println(hold.calculateInterquartileRange() + " " + hold.calculateFirstQuartile() + " " + hold.calculateThirdQuartile());
						for(int i = 0; i < aver.size(); i++) {
							rag.writeBytes((threeSig(aver.get(i))+"") + (i + 1 == aver.size() ? "" : ", "));
						}
						rag.writeBytes("\n");
					}
					raf.close();
				}
				else {
					System.out.println(type + " " + counter);
				}
				f = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + ANALYSIS_FILE + type + ".txt");
			}
			rag.close();
		}
		catch(Exception e) {
			System.out.println(f.getAbsolutePath());
			e.printStackTrace();
		}
	}
	
	private InterpretData generateInterpretDataSimple(String path, String type) {
		int counter = 0;

		int size = new File(path).list().length;
		
		InterpretData hold = new InterpretData();
		hold.assignTotalNumberTests(getTotalNumberTests(path));
		
		String[] attributes = null;
		File f = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + RAW_DATA_FILE + type + ".txt");

		try {
			RandomAccessFile raf;
			while(counter <= size+1) {
				if(f.exists()) {
					raf = new RandomAccessFile(f, "rw");
					boolean skip = false;
					if(attributes == null) {
						String line = raf.readLine();
						if(line != null) {
							attributes = line.split(", ");
							hold.assignAttributes(attributes);
						}
						else
							skip = true;
					}
					else {
						raf.readLine();
					}
					if(!skip) {
						String line = raf.readLine();
						String[] values = line.split(", ");
						for(int i = 0; i < values.length; i++) {
							values[i] = values[i].trim();
						}
						hold.addDataRow(values);
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
		return hold;
	}
		
	private void outputInterpretDataSimple(InterpretData hold, String path, String type) {
		String[] attributes = hold.getAttributes();

		File f = new File(path + "/" + PROCESS_FILE + type + ".txt");
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
			raf.writeBytes("\nNumber of DNFs:\t\t\t" + hold.getNumberDNF());
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		System.out.println("Done");
	}
	
	private void interpretTestBatchDataIncremental(String path, String type) {
		int counter = 1;
		
		int size = new File(path).list().length;
		
		InterpretDataNested hold = new InterpretDataNested();
		String[] attributes = null;
		File f = new File(path + "/" + TEST_NAME + "_" + counter++ + "/" + ANALYSIS_FILE + type + ".txt");
		File g = new File(path + "/" + RAW_DATA_FILE + type + ".txt");

		try {
			RandomAccessFile raf;
			RandomAccessFile rag = new RandomAccessFile(g, "rw");
			while(counter <= size+1) {
				if(f.exists()) {
					raf = new RandomAccessFile(f, "rw");
					boolean skip = false;
					
					//First get ahold of our attribute list if not done so yet; perform check for if it's a dead file
					
					if(attributes == null) {
						String line = raf.readLine();
						if(line != null) {
							attributes = line.split(", ");
							rag.writeBytes(line + "\n");
						}
						else
							skip = true;
					}
					else if(raf.readLine() == null){
						skip = true;
					}
					
					//If file exists with information, start pulling data from it; here, first line is regular InterpretData, following lines go in nested
					
					if(!skip) {
						String line = raf.readLine();
						String[] values = line.split(", ");
						rag.writeBytes(line + "\n");
						boolean proceeding = false;
						while(values != null) {
							for(int i = 0; i < values.length; i++) {
								values[i] = values[i].trim();
							}
							if(!proceeding) {
								hold.addDataRow(values);
								proceeding = true;
							}
							else {
								hold.addAssociatedDataRow(values);
							}
							
							String next = raf.readLine();
							if(next != null) {
								values = next.split(",");
								rag.writeBytes(next + "\n");
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
			rag.close();
		}
		catch(Exception e) {
			System.out.println(f.getAbsolutePath());
			e.printStackTrace();
		}
		
		//Now we use the gathered data in InterpretData to do some varying analysis of it for the overall incremental performances
		
		f = new File(path + "/" + PROCESS_FILE + type + ".txt");
		f.delete();
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			fileWriteInterpretDataGeneral(hold, raf, attributes);
			raf.writeBytes("\nNumber of DNFs:\t\t\t" + hold.getNumberDNF());
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		InterpretData other = hold.getAssociatedData();
		
		//Now we use the subsystem record data in InterpretDataNested to do general and specific analysis
		
		f = new File(path + "/" + PROCESS_FILE + type + "_subsystems.txt");
		f.delete();
		try {
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			fileWriteInterpretDataGeneral(other, raf, attributes);
			raf.close();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("Done");
	}
	
	private void fileWriteInterpretDataGeneral(InterpretData hold, RandomAccessFile raf, String[] attributes) throws Exception {
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
	}
	
	private void interpretTestBatchDataHeuristics(String path, String type, int size) {
		
	}
	
	private void resetDataGathered(File f) {
		//Deletes all the result files but keeps the generated systems in place for a re-run
		
		for(String s : TEST_NAMES) {
			int counter = 0;
			
			String path = f.getAbsolutePath() + "/" + s + "/";
			System.out.println(path);
			
			if(!(new File(path).exists())) {
				continue;
			}
			
			int size = new File(path).list().length;
			
			while(counter < size + 1) {
				
				String outputFile = path + TEST_NAME + "_" + counter + "/" + RESULTS_FILE;
				new File(outputFile).delete();
				System.out.println(outputFile);
				
				for(String t : ANALYSIS_TYPES) {
					outputFile = path + TEST_NAME + "_" + counter + "/" + ANALYSIS_FILE + t + ".txt";
					new File(outputFile).delete();
				}
				
				counter++;
			}
			
		}
		
	}
	
	//-- Existing Tests  --------------------------------------
	
	private void testBasicConfigDTP() throws Exception {

		ArrayList<String> names = SystemGeneration.generateSystemSetDTP();

		ArrayList<String> plant = new ArrayList<String>(names.subList(0, 3));
		ArrayList<String> spec = new ArrayList<String>(names.subList(3, 6));
		
		testExistingSystem("Test Basic Config DTP", plant, spec, AgentChicanery.generateAgentsDTP(), new BatchSetup(){
			public void setUpSystem() {
				SystemGeneration.generateSystemSetDTP();
			
		}
		});
	}
	
	private void testBasicConfigHISC() throws Exception {
		
		ArrayList<ArrayList<String>> names = SystemGeneration.generateSystemSetHISC();
		
		testExistingSystem("Test Basic Config HISC", names.get(0), names.get(1), AgentChicanery.generateAgentsHISC(), new BatchSetup() {
			
			public void setUpSystem() {
				SystemGeneration.generateSystemSetHISC();
			}
			
		});
		
	}
	
	abstract class BatchSetup {
		
		public abstract void setUpSystem();
		
	}
	
	private void testExistingSystem(String testBatchIn, ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, BatchSetup systemStart) throws Exception {
		int counter = 0;

		String testBatch = testBatchIn;
		
		
		while(counter <= NUMBER_EXISTING_TEST_RUNS) {
			System.out.println(testBatch + ": " + counter);
			
			String testName = TEST_NAME + "_" + counter;
			
			File f;
			f = new File(defaultWritePath + "/" + testName);
			f.mkdir();

			writePath = defaultWritePath + "/" + testName;

			int finished = checkTestNumberVerifiedComplete(writePath);
			
			while(finished < NUMBER_REPEAT_TEST) {
				systemStart.setUpSystem();
				autoTestSystemCoobsSB(testBatch + "_" + counter, plants, specs, agents);
				resetModel();
	
				finished = checkTestNumberVerifiedComplete(writePath);
			}

			counter++;
		}
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
		
		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

		runBasicTest(info, count, "Test Basic Config One");
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
		
		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);
		
		runBasicTest(info, count, "Test Basic Config Two");
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

		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);
		
		runBasicTest(info, count, "Test Basic Config Three");
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

		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);
		
		runBasicTest(info, count, "Test Basic Config Four");
	}
	
	private void runBasicTest(RandomGenStats info, int count, String testBatch) throws Exception{
		int trueResults = generateInterpretDataSimple(defaultWritePath, ANALYSIS_COOBS).getNumberTrueResults();
		
		int counter = 0;
		
		while(counter < count || trueResults < MINIMUM_TRUE_RESULTS) {
			System.out.println(testBatch + ": " + counter + ", " + trueResults + "/" + MINIMUM_TRUE_RESULTS);
			if(autoTestRandomSystem(counter + 1, info, TEST_BASIC))
				trueResults++;
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
		
		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);
		
		runIncTest(info, count, "Test Inc Config One");
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
		
		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

		runIncTest(info, count, "Test Inc Config Two");
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
		
		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);

		runIncTest(info, count, "Test Inc Config Three");
	}
	
	private void runIncTest(RandomGenStats info, int count, String testBatch) throws Exception{
		int trueResults = generateInterpretDataSimple(defaultWritePath, ANALYSIS_COOBS).getNumberTrueResults();
		
		int counter = 0;
		
		while(counter < count || trueResults < MINIMUM_TRUE_RESULTS) {
			System.out.println(testBatch + ": " + counter);
			if(autoTestRandomSystem(counter + 1, info, TEST_INC))
				trueResults++;
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
		
		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Heuristic Config One: " + counter);
			autoTestRandomSystem(counter + 1, info, TEST_HEUR);
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
		
		RandomGenStats info = new RandomGenStats(numPlants, numSpecs, numStates, numStateVar, numEvents, numEventsVar, eventShareRate, numControllers, numControllersVar, controllerObserveRate, controllerControlRate);
		
		int counter = 0;
		
		while(counter < count) {
			System.out.println("Test Heuristic Config Two: " + counter);
			autoTestRandomSystem(counter + 1, info, TEST_HEUR);
			resetModel();
			counter++;
			
		}
	}

  //-- Specific Tests  ----------------------------------------
	
//---  System Testing   -----------------------------------------------------------------------

	public void markTestUnfinished() {
		if(!heuristics) {
			if(checkTestDeclaredTypeMemoryError(writePath)) {
				printOut(VERIFY_MEMORY_ERROR + analysisSubtype);
			}
			else{
				printOut(DECLARE_MEMORY_ERROR + analysisSubtype);
			}

			if(model != null && model.getLastProcessData() != null) {
				printOut(model.getLastProcessData().produceOutputLog());
			}
		}
		else {
			printOut(VERIFY_MEMORY_ERROR + analysisSubtype + retrieveHeuristicsPostscript()); 
		}
	}

	private boolean autoTestRandomSystem(int count, RandomGenStats info, int testChoice) throws Exception {
		String testName = TEST_NAME + "_" +  count;
		File f;
		f = new File(defaultWritePath + "/" + testName);

		writePath = defaultWritePath + "/" + testName;

		boolean inMem = false;
		
		if(!f.exists()) {
			autoGenerateNewRandomSystem(count, info);
			inMem = true;
		}

		Boolean out = null;
		
		
		
		int finished = checkTestNumberVerifiedComplete(writePath);

		while(finished < (heuristics ? 1 : NUMBER_REPEAT_TEST)) {

			ArrayList<String> completeTests = checkTestsCompleted(writePath);
			ArrayList<String> memoryError = checkTestTypesVerifiedMemoryError(writePath);
	
			if(!inMem)
				readInOldSystem(testName);
	
			ArrayList<String> plants = getPlants(testName);
			ArrayList<String> specs = getSpecs(testName);
			ArrayList<HashMap<String, ArrayList<Boolean>>> agents = getAgents(testName);
			
			
			switch(testChoice) {
				case TEST_ALL:
					out = autoTestSystemFull(testName, plants, specs, agents);
					break;
				case TEST_BASIC:
					out = autoTestSystemCoobsSB(testName, plants, specs, agents, finished, completeTests, memoryError);
					break;
				case TEST_INC:
					out = autoTestSystemIncr(testName, plants, specs, agents, finished, completeTests, memoryError);
					break;
				case TEST_HEUR:
					out = autoTestHeuristics(testName, plants, specs, agents, completeTests, memoryError);
					break;
				default:
					break;
			}
			confirmComplete();			
			finished = checkTestNumberVerifiedComplete(writePath);
		}

		return out == null ? false : out;
	}
	
	private int contains(ArrayList<String> list, String find) {
		int out = 0;
		for(String s : list) {
			out += (s.equals(find) ? 1 : 0);
		}
		return out;
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

	private void autoGenerateNewRandomSystem(int count, RandomGenStats info) throws Exception {
		String testName = TEST_NAME + "_" +  count;
		File f;
		f = new File(defaultWritePath + "/" + testName);

		writePath = defaultWritePath + "/" + testName;

		f.mkdir();
		
		//System.out.println("This test: " + testName);
		printOut(testName + ", " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n---------------------------------------------\n");
		
		
		printOut("Test Configuration: Full Suite");
		printOut("Randomizer Parameters: ");
		printOut(" " + info.toString());
		printOut(" " + info.shortToString() + "\n");
		printOut("---------------------------------------------\n");
		
		ArrayList<String> events = RandomGeneration.generateRandomSystemSet(testName, model, info);
		ArrayList<String> names = RandomGeneration.getComponentNames(testName, info.getNumPlants(), info.getNumSpecs());

		ArrayList<HashMap<String, ArrayList<Boolean>>> agents = RandomGeneration.generateRandomAgents(events, info);
		
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
	
	private boolean autoTestSystemFull(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
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
		return coobs;
	}
	
	private void autoTestSystemCoobsSB(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception {

		printCoobsLabel(prefixNom, false);
		boolean coobs = checkCoobservable(plantNames, specNames, agents, false);
		
		printSBCoobsLabel(prefixNom);
		boolean sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
		
		if(coobs && !sbCoobs) {
			printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
		}
		
		boolean error = false;
		
		if(sbCoobs && !coobs) {
			printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
			error = true;
		}
		if(error) {
			throw new Exception("Logic Conflict in Data Output: " + prefixNom);
		}
		resetModel();
		
		confirmComplete();

	}
	
	private boolean autoTestSystemCoobsSB(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, int finishCount, ArrayList<String> completedTests, ArrayList<String> memoryError) throws Exception{
		boolean coobs = false;
		if(contains(completedTests, ANALYSIS_COOBS) == finishCount && !memoryError.contains(ANALYSIS_COOBS)) {
			printCoobsLabel(prefixNom, false);
			coobs = checkCoobservable(plantNames, specNames, agents, false);
		}

		boolean sbCoobs = false;
		if(contains(completedTests, ANALYSIS_SB) == finishCount && !memoryError.contains(ANALYSIS_SB)) {
			printSBCoobsLabel(prefixNom);
			sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
		}
		
		if(memoryError.isEmpty() && (coobs && !sbCoobs)) {
			printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
		}
		
		boolean error = false;
		
		if(memoryError.isEmpty() && (sbCoobs && !coobs)) {
			printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
			error = true;
		}
		if(error) {
			throw new Exception("Logic Conflict in Data Output: " + prefixNom);
		}
		resetModel();
		return coobs;
	}

	private boolean autoTestSystemIncr(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, int finishCount, ArrayList<String> completedTests, ArrayList<String> memoryError) throws Exception{
		Boolean icCoobs = null;
		if(contains(completedTests, ANALYSIS_INC_COOBS) == finishCount && !memoryError.contains(ANALYSIS_INC_COOBS)) {
			printIncrementalLabel(prefixNom, false);
			icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
		}
		
		Boolean sbCoobs = null;
		if(contains(completedTests, ANALYSIS_SB) == finishCount && !memoryError.contains(ANALYSIS_SB)) {
			printSBCoobsLabel(prefixNom);
			sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
		}
		
		Boolean icSbCoobs = null;
		if(contains(completedTests, ANALYSIS_INC_SB) == finishCount && !memoryError.contains(ANALYSIS_INC_SB)) {
			printIncrementalSBLabel(prefixNom);
			icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);
		}

		if((icCoobs != null && sbCoobs != null) && icCoobs && !sbCoobs) {
			printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
		}
		
		boolean error = false;
		
		if((sbCoobs != null && icSbCoobs != null) && sbCoobs != icSbCoobs) {
			printOut("~~~\nError!!! : Incremental SB Algo. did not return same as SB Algo.\n~~~");
			error = true;
		}
		if((sbCoobs != null && icCoobs != null) && (sbCoobs && !icCoobs)) {
			printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
			error = true;
		}
		if(error) {
			throw new Exception("Logic Conflict in Data Output: " + prefixNom);
		}
		resetModel();
		return icCoobs == null ? (sbCoobs == null ? (icSbCoobs == null ? false : icSbCoobs) : sbCoobs) : icCoobs;
	}
	
	private boolean autoTestHeuristics(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, ArrayList<String> completedTests, ArrayList<String> memoryError) throws Exception{
		Boolean expected = null;
		
		for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
			for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
				for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
					Incremental.assignIncrementalOptions(i, j, k);
					String post = generateHeuristicsPostscript(i, j, k);
					Boolean icCoobs = null;
					if(!completedTests.contains(ANALYSIS_INC_COOBS + post) && !memoryError.contains(ANALYSIS_INC_COOBS + post)) {
						printIncrementalLabel(prefixNom + post, false);
						icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
					}
					
					if(expected == null) {
						expected = icCoobs;
					}

					Boolean icSbCoobs = null;
					if(k == 0 && indexOf(Incremental.INCREMENTAL_B_NO_REJECT, j) != -1 && (!completedTests.contains(ANALYSIS_INC_SB + post) && !memoryError.contains(ANALYSIS_INC_SB + post))) {
						printIncrementalSBLabel(prefixNom + post);
						icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);
					}
					
					if(expected == null) {
						expected = icSbCoobs;
					}
					
					if((expected != null) && ((icCoobs != null && expected != icCoobs) || (icSbCoobs != null && expected != icSbCoobs))) {
						throw new Exception("Change in Heuristics caused difference result: " + post);
					}
				}
			}
		}
		resetModel();
		return expected;
	}
	
	private int indexOf(int[] arr, int key) {
		for(int i = 0; i < arr.length; i++) {
			if(arr[i] == key) {
				return i;
			}
		}
		return -1;
	}

//---  Coobservability Testing   --------------------------------------------------------------

	private boolean checkCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf)throws Exception {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		assignAnalysisSubtype(TYPE_COOBS);
		Boolean result = inf ? model.isInferenceCoobservableUStruct(plants, specs, eventAtt, agents) : model.isCoobservableUStruct(plants, specs, eventAtt, agents);
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
		assignAnalysisSubtype(TYPE_SB);
		boolean result = model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
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
		assignAnalysisSubtype(TYPE_INC_COOBS);
		boolean result = inf ? model.isIncrementalInferenceCoobservable(plants, specs, eventAtt, agents) : model.isIncrementalCoobservable(plants, specs, eventAtt, agents);
		handleOutData(t, hold);
		printOut("\t\t\t\tIncremental" + (inf ? " Inference" : "") + " Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private boolean checkIncrementalSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) throws Exception{
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		assignAnalysisSubtype(TYPE_INC_SB);
		boolean result = model.isIncrementalSBCoobservable(plants, specs, eventAtt, agents);
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
	
	private String retrieveHeuristicsPostscript() {
		int[] hold = Incremental.retrieveIncrementalOptions();
		return "_" + hold[0] + "_" + hold[1] + "_" + hold[2];
	}
	
	private String generateHeuristicsPostscript(int a, int b, int c) {
		return "_" + a + "_" + b + "_" + c;
	}
	
	private boolean checkTestVerifiedComplete(String path) {
		return checkForTerm(path + "/" + RESULTS_FILE, VERIFY_COMPLETE_TEST);
	}
	
	private int checkTestNumberVerifiedComplete(String path) {
		return checkForTermLinePositions(path + "/" + RESULTS_FILE, VERIFY_COMPLETE_TEST).size();
	}
	
	private boolean checkTestDeclaredMemoryError(String path) {
		return checkForTerm(path + "/" + RESULTS_FILE, DECLARE_MEMORY_ERROR);
	}
	
	private boolean checkTestDeclaredTypeMemoryError(String path) {
		return checkForTerm(path + "/" + RESULTS_FILE, DECLARE_MEMORY_ERROR + analysisSubtype);
	}
	
	private int checkTestNumberDeclaredMemoryError(String path) {
		return checkForTermLinePositions(path + "/" + RESULTS_FILE, DECLARE_MEMORY_ERROR).size();
	}
	
	private boolean checkTestVerifiedMemoryError(String path) {
		return checkForTerm(path + "/" + RESULTS_FILE, VERIFY_MEMORY_ERROR);
	}
	
	private int checkTestNumberVerifiedMemoryError(String path) {
		return checkForTermLinePositions(path + "/" + RESULTS_FILE, VERIFY_MEMORY_ERROR).size();
	}

	private ArrayList<String> checkTestTypesVerifiedMemoryError(String path){
		ArrayList<String> out = new ArrayList<String>();
		if(!heuristics) {
			for(String s : ANALYSIS_TYPES) {
				if(checkForTerm(path + "/" + RESULTS_FILE, VERIFY_MEMORY_ERROR + s)) {
					out.add(s);
				}
			}
		}
		else {
			for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
				for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
					for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
						String post = generateHeuristicsPostscript(i, j, k);
						if(checkForTerm(path + "/" + RESULTS_FILE, VERIFY_MEMORY_ERROR + ANALYSIS_INC_SB + post)) {
							out.add(ANALYSIS_INC_SB + post);
						}
						if(checkForTerm(path + "/" + RESULTS_FILE, VERIFY_MEMORY_ERROR + ANALYSIS_INC_COOBS + post)) {
							out.add(ANALYSIS_INC_COOBS + post);
						}
					}
				}
			}
		}
		return out;
	}
	
	private ArrayList<String> checkTestsCompleted(String path){
		ArrayList<String> out = new ArrayList<String>();
		if(!heuristics) {
			for(String s : ANALYSIS_TYPES) {
				if(checkForTerm(path + "/" + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + s)) {
					for(int i = 0; i < checkForTermLinePositions(path + "/" + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + s).size(); i++)
						out.add(s);
				}
			}
		}
		else {
			for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
				for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
					for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
						String post = generateHeuristicsPostscript(i, j, k);
						if(checkForTerm(path + "/" + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + ANALYSIS_INC_SB + post)) {
							out.add(ANALYSIS_INC_SB + post);
						}
						if(checkForTerm(path + "/" + RESULTS_FILE, VERIFY_COMPLETE_CHECKPOINT + ANALYSIS_INC_COOBS + post)) {
							out.add(ANALYSIS_INC_COOBS + post);
						}
					}
				}
			}
		}
		return out;
	}
	
	//-- Data Output Gathering  -------------------------------
	
	private void handleOutData(long t, long hold) {
		printOut(VERIFY_COMPLETE_CHECKPOINT + analysisSubtype + (heuristics ? retrieveHeuristicsPostscript() : ""));
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
			for(int i = 0; i < vals.size(); i++){
				Double d = vals.get(i);
				if(d != null)
					raf.writeBytes(threeSig(d) + (i + 1 < vals.size() ? ", \t" : ""));
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
		if(!g.exists()) {
			return false;
		}
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
	
	private ArrayList<Integer> checkForTermLinePositions(String path, String phrase){
		ArrayList<Integer> out = new ArrayList<Integer>();
		File g = new File(path);
		if(!g.exists()) {
			return out;
		}
		try {
			RandomAccessFile raf = new RandomAccessFile(g, "r");
			String line = raf.readLine();
			int counter = 1;
			while(line != null) {
				if(line.equals(phrase)) {
					out.add(counter);
				}
				counter++;
				line = raf.readLine();
			}
			raf.close();
			return out;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<Integer>();
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
