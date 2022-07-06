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
import java.util.HashSet;
import java.util.Scanner;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import model.AttributeList;
import model.Manager;
import model.fsm.TransitionSystem;
import model.process.coobservability.Incremental;
import test.help.AgentChicanery;
import test.help.EventSets;
import test.help.RandomGeneration;
import test.help.SystemGeneration;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class TestFunctionality {
	
//---  Constants   ----------------------------------------------------------------------------

	private static final String RESULTS_FILE = "output.txt";
	
	private static final String ANALYSIS_FILE = "raw_num.txt";
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static Manager model;
	
	private static ArrayList<String> eventAtt;
	
	private static String defaultWritePath;
	
	private static String writePath;
	
	private static boolean terminalPrint;
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void main(String[] args) throws Exception {
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES, FiniteStateMachine.ADDRESS_CONFIG);
		FiniteStateMachine.fileConfiguration();
		model = new Manager();
		terminalPrint = true;
		
		File f = new File(FiniteStateMachine.ADDRESS_IMAGES);
		f = f.getParentFile();
		defaultWritePath = f.getAbsolutePath() + "/autogenerate";
		f = new File(defaultWritePath);
		f.mkdir();
		
		SystemGeneration.assignManager(model);
		
		eventAtt = new ArrayList<String>();
		for(String s : EventSets.EVENT_ATTR_LIST) {
			eventAtt.add(s);
		}
		//basicUStructCheck();
		//crushUStructCheck();
		//crushUStructCheck2();
		//crushUStructCheck3();
		//generateSystems();
		//runAllTests();
		
		//RandomGeneration.setupRandomFSMConditions(model, 1, 1, 1);
		//makeImageDisplay(RandomGeneration.generateRandomFSM("rand", model, 5, 3, 2, true), "Rand");
		
		//runAllCoobsTests();
		//runAllSBTests();
		
		//runAllTests();

		
		//runAllSBTests();
		//runAllIncrementalCoobsTests();
		
		//checkSystemDSBCoobservable();
		
		//runAllTests();
		
		/*
		SystemGeneration.generateSystemA("A");
		SystemGeneration.generateSystemB("B");
		ArrayList<String> use = new ArrayList<String>();
		use.add("A"); use.add("B");
		String nom = model.performParallelComposition(use);
		makeImageDisplay("A", "A");
		makeImageDisplay("B", "B");
		makeImageDisplay(nom, nom);
		*/
		
		//autoTestHeuristicsIncremental(4, 4, 5, 2, 4, 2, .35, 2, 0, .4, .3);
		//autoTestNewRandomSystem(3, 3, 6, 2, 4, 2, .5, 2, 0, .4, .3);
		//while(true)
		//	autoTestHeuristicsIncremental(2, 2, 5, 2, 4, 2, .4, 2, 0, .4, .3);
		
		Incremental.assignIncrementalOptions(1, 1, 1);
		
		Integer testCase = 774;
		
		if(testCase != null) {
			String prefix = "test_full_suite_";
			autoTestOldSystem(prefix + testCase);
		} else {
			Scanner sc = new Scanner(System.in);
			while(true) {
				autoTestNewRandomSystem(2, 2, 5, 2, 4, 2, .4, 2, 0, .4, .3);
				sc.nextLine();
			}
		}
		
		//basicUStructCheck();
		
		//generateSystems();
		
		//checkSystemUrvashiSBCoobservable();
		
		/*
		checkSystemLiuOneIncrementalCoobservable();
		model.flushFSMs();
		checkSystemLiuTwoIncrementalCoobservable();
		
		model.flushFSMs();
		
		checkSystemLiuOneCoobservable();
		model.flushFSMs();
		checkSystemLiuTwoCoobservable();
		*/
		
		//checkSystemBCoobservable();
		//checkSystemBAltCoobservable();
		//checkSystemFinnCoobservable();
	}
	
//---  Automated Testing   --------------------------------------------------------------------
	
	private static void runAllTests() {
		String hold = writePath;
		writePath = null;
		printOut("\n\t\t\t\t~~~ Testing System A ~~~\n");
		checkSystemACoobservable(false);
		checkSystemACoobservable(true);
		checkSystemASBCoobservable();

		printOut("\n\t\t\t\t~~~ Testing System B ~~~\n");
		checkSystemBCoobservable(false);
		checkSystemBCoobservable(true);
		checkSystemBSBCoobservable();

		printOut("\n\t\t\t\t~~~ Testing System C ~~~\n");
		checkSystemCCoobservable(false);
		checkSystemCCoobservable(true);
		checkSystemCSBCoobservable();

		printOut("\n\t\t\t\t~~~ Testing System D ~~~\n");
		checkSystemDCoobservable(false);
		checkSystemDCoobservable(true);
		checkSystemDSBCoobservable();

		printOut("\n\t\t\t\t~~~ Testing System E ~~~\n");
		checkSystemECoobservable(false);
		checkSystemECoobservable(true);
		checkSystemESBCoobservable();

		printOut("\n\t\t\t\t~~~ Testing System Finn ~~~\n");
		checkSystemFinnCoobservable(false);
		checkSystemFinnCoobservable(true);
		
		printOut("\n\t\t\t\t~~~ Testing System Urvashi ~~~\n");
		checkSystemUrvashiSBCoobservable();

		printOut("\n\t\t\t\t~~~ Testing System Liu One ~~~\n");
		checkSystemLiuOneCoobservable(false);
		checkSystemLiuOneCoobservable(true);
		checkSystemLiuOneSBCoobservable();
		checkSystemLiuOneIncrementalCoobservable();

		printOut("\n\t\t\t\t~~~ Testing System Liu Two ~~~\n");
		checkSystemLiuTwoCoobservable(false);
		checkSystemLiuTwoCoobservable(true);
		checkSystemLiuTwoSBCoobservable();
		checkSystemLiuTwoIncrementalCoobservable();
		writePath = hold;
	}
	
	private static void runAllCoobsTests() {
		checkSystemACoobservable(false);
		checkSystemBCoobservable(false);
		checkSystemCCoobservable(false);
		checkSystemDCoobservable(false);
		checkSystemECoobservable(false);
		checkSystemFinnCoobservable(false);
		checkSystemLiuOneCoobservable(false);
		checkSystemLiuTwoCoobservable(false);
	}
	
	private static void runAllInfCoobsTests() {
		checkSystemACoobservable(true);
		checkSystemBCoobservable(true);
		checkSystemCCoobservable(true);
		checkSystemDCoobservable(true);
		checkSystemECoobservable(true);
		checkSystemFinnCoobservable(true);
		checkSystemLiuOneCoobservable(true);
		checkSystemLiuTwoCoobservable(true);
	}
	
	private static void runAllSBTests() {
		checkSystemASBCoobservable();
		checkSystemBSBCoobservable();
		checkSystemCSBCoobservable();
		checkSystemDSBCoobservable();
		checkSystemESBCoobservable();
		checkSystemUrvashiSBCoobservable();
		checkSystemLiuOneSBCoobservable();
		checkSystemLiuTwoSBCoobservable();
	}
	
	private static void runAllIncrementalCoobsTests() {
		checkSystemLiuOneIncrementalCoobservable();
		checkSystemLiuTwoIncrementalCoobservable();
	}
	
	private static void generateSystems() {
		String SystemA = "Example 1";
		SystemGeneration.generateSystemA(SystemA);
		makeImageDisplay(SystemA, "Example 1");
		
		String SystemB = "Example 2";
		SystemGeneration.generateSystemB(SystemB);
		makeImageDisplay(SystemB, "Example 2");
		
		String SystemC = "Example 3";
		SystemGeneration.generateSystemC(SystemC);
		makeImageDisplay(SystemC, "Example 3");
		
		String SystemD = "Example 4";
		SystemGeneration.generateSystemD(SystemD);
		makeImageDisplay(SystemD, "Example 4");
		
		String SystemE = "Example 5";
		SystemGeneration.generateSystemE(SystemE);
		makeImageDisplay(SystemE, SystemE);
	}
	
	private static void basicUStructCheck() {
		String SystemA = "Example 1";
		SystemGeneration.generateSystemA(SystemA);
		makeImageDisplay(SystemA, "Example 1");

		String ustruct = model.buildUStructure(SystemA, eventAtt, AgentChicanery.generateAgentsA());
		System.out.println(model.getLastProcessData().produceOutputLog());
		for(String s : model.getFSMStateList(ustruct)) {
			System.out.println(s);
		}
		for(String s : model.getFSMEventList(ustruct)) {
			System.out.println(s);
		}
		for(String s : model.getFSMTransitionList(ustruct)) {
			System.out.println(s);
		}
		makeImageDisplay(ustruct, "Example 1 UStruct");
	}
	
	private static void crushUStructCheck() {
		String SystemA = "Example 1";
		SystemGeneration.generateSystemA(SystemA);
		makeImageDisplay(SystemA, "Example 1");

		ArrayList<String> ustruct = model.buildUStructureCrush(SystemA, eventAtt, AgentChicanery.generateAgentsA());
		for(String s : ustruct)
			makeImageDisplay(s, s);
	}
	
	private static void crushUStructCheck2() {
		String SystemB = "Example 2";
		SystemGeneration.generateSystemB(SystemB);
		makeImageDisplay(SystemB, "Example 2");

		ArrayList<String> ustruct = model.buildUStructureCrush(SystemB, eventAtt, AgentChicanery.generateAgentsB2());
		for(String s : ustruct) {
			makeImageDisplay(s, s);
			model.exportFSM(s);
		}
	}
	
	private static void crushUStructCheck3() {
		String SystemE = "Example 5";
		SystemGeneration.generateSystemE(SystemE);
		makeImageDisplay(SystemE, "Example 5");

		ArrayList<String> ustruct = model.buildUStructureCrush(SystemE, eventAtt, AgentChicanery.generateAgentsE());
		for(String s : ustruct) {
			makeSVGImage(s, s);
			model.exportFSM(s);
		}
	}
	
	private static void crushUStructCheckFinn() {
		String SystemFinn = "Example Finn";
		SystemGeneration.generateSystemFinn(SystemFinn);
		makeImageDisplay(SystemFinn, SystemFinn);
		ArrayList<String> ustruct = model.buildUStructureCrush(SystemFinn, eventAtt, AgentChicanery.generateAgentsFinn5());
		for(String s : ustruct) {
			makeImageDisplay(s, s);
			model.exportFSM(s);
		}
	}
	
	private static void autoTestNewRandomSystem(int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate) throws Exception {
		String testName = "test_full_suite_";
		int count = -1;
		File f;
		do {
			f = new File(defaultWritePath + "/" + testName + ++count);
		}while(f.exists());
		
		f.mkdir();
		testName += count+"";
		writePath = defaultWritePath + "/" + testName;
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
		
		autoTestSystemFull(testName, RandomGeneration.getPlantNames(testName, numPlants), RandomGeneration.getSpecNames(testName, numSpecs), agents, false);
	}

	private static void autoTestOldSystem(String prefixNom) throws FileNotFoundException {
		String path = defaultWritePath + "/" + prefixNom;
		ArrayList<String> plants = new ArrayList<String>();
		int counter = 0;
		File f = new File(path + "/" + prefixNom + "_p_" + counter++ + ".txt");
		while(f.exists()) {
			StringBuilder sb = new StringBuilder();
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				sb.append(sc.nextLine() + "\n");
			}
			sc.close();
			plants.add(model.readInFSM(sb.toString()));
			f = new File(path + "/" + prefixNom + "_p_" + counter++ + ".txt");
		}

		ArrayList<String> specs = new ArrayList<String>();
		counter = 0;
		f = new File(path + "/" + prefixNom + "_s_" + counter++ + ".txt");
		while(f.exists()) {
			StringBuilder sb = new StringBuilder();
			Scanner sc = new Scanner(f);
			while(sc.hasNextLine()) {
				sb.append(sc.nextLine() + "\n");
			}
			sc.close();
			specs.add(model.readInFSM(sb.toString()));
			f = new File(path + "/" + prefixNom + "_s_" + counter++ + ".txt");
		}
		
		f = new File(path + "/" + prefixNom + "_agents.txt");
		StringBuilder sb = new StringBuilder();
		Scanner sc = new Scanner(f);
		while(sc.hasNextLine()) {
			sb.append(sc.nextLine() + "\n");
		}
		sc.close();
		ArrayList<HashMap<String, ArrayList<Boolean>>> agents = model.readInAgents(sb.toString());
		
		printOut("Agent Information: \n" + agents.toString().replace("},", "},\n").replaceAll("[\\[\\]]", " "));
		printOut("\n---------------------------------------------\n");

		String hold = writePath;
		writePath = null;
		
		autoTestSystemFull(prefixNom, plants, specs, agents, true);
		writePath = hold;
	}
	
	private static void pullReserveDisplay() {
		String nom = model.storeProcessHoldSystem();
		if(nom != null) {
			makeImageDisplay(nom, nom + "_coobs");
		}
	}
	
	private static void autoTestSystemFull(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean displays) {
		printCoobsLabel(prefixNom, false);
		boolean coobs = checkCoobservable(plantNames, specNames, agents, false);

		if(displays)
			pullReserveDisplay();
		
		printIncrementalLabel(prefixNom, false);
		boolean icCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, false);
		
		if(displays)
			pullReserveDisplay();
		
		printSBCoobsLabel(prefixNom);
		boolean sbCoobs = checkSBCoobservable(plantNames, specNames, agents);
		printIncrementalSBLabel(prefixNom);
		boolean icSbCoobs = checkIncrementalSBCoobservable(plantNames, specNames, agents);
	/*	printCoobsLabel(prefixNom, true);
		boolean infCoobs = checkCoobservable(plantNames, specNames, agents, true);
		printIncrementalLabel(prefixNom, true);
		boolean icIfCoobs = checkIncrementalCoobservable(plantNames, specNames, agents, true);
*/
		if(coobs && !sbCoobs) {
			printOut("---\nOf note, State Based Algo. returned False while Coobs. Algo. returned True\n---");
		}
		
		if(coobs != icCoobs) {
			printOut("~~~\nError!!! : Incremental Algo. did not return same as Coobs. Algo.\n~~~");
		}
		if(sbCoobs != icSbCoobs) {
			printOut("~~~\nError!!! : Incremental SB Algo. did not return same as SB Algo.\n~~~");
		}
/*		if(infCoobs != icIfCoobs) {
			printOut("~~~\nError!!! : Incremental Inferencing Algo. did not return same as Inferencing Algo.\n~~~");
		}*/
		if(sbCoobs && !coobs) {
			printOut("~~~\nError!!! : State Based Algo. claimed True while Coobs. Algo. claimed False\n~~~");
		}
/*		if(coobs && !infCoobs) {
			printOut("~~~\nError!!! : Coobs. Algo. claimed True while Infer. Coobs. Algo. claimed False\n~~~");
		}*/
		resetModel();
	}
	
	private static void autoTestHeuristicsIncremental(int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate) throws Exception{
		String testName = "test_heu_inc_";
		int count = -1;
		File f;
		do {
			f = new File(defaultWritePath + "/" + testName + ++count);
		}while(f.exists());
		
		f.mkdir();
		testName += count+"";
		writePath = defaultWritePath + "/" + testName;
		System.out.println("This test: " + testName);
		printOut(testName + ", " + DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss").format(LocalDateTime.now()) + "\n---------------------------------------------\n");
		
		
		printOut("Test Configuration: Incremental Heuristics");
		printOut("Randomizer Parameters:");
		printOut(" Plants: " + numPlants + ", Specs: " + numSpecs + ", # States Average: " + numStates + ", State Variance: " + numStateVar + ", # Events Average: " + numEve + ", Event Variance: " + numEveVar + 
				", Event Share Rate: " + shareRate + ", # Agents: " + numAgents + ", Agent Variance: " + numAgentVar + ", Agent Obs. Event Rate: " + obsRate + ", Agent Ctr. Event Rate: " + ctrRate);
		printOut(" " + numPlants + ", " + numSpecs + ", " + numStates + ", " + numStateVar + ", " + numEve + ", " + numEveVar + ", " + shareRate + ", " + numAgents + ", " + numAgentVar + ", " + obsRate + ", " + ctrRate + "\n");
		printOut("---------------------------------------------\n");
		
		ArrayList<String> events = RandomGeneration.generateRandomSystemSet(testName, model, numPlants, numSpecs, numStates, numStateVar, numEve, numEveVar, shareRate);
		ArrayList<String> names = RandomGeneration.getComponentNames(testName, numPlants, numSpecs);
		
		ArrayList<HashMap<String, ArrayList<Boolean>>> agents = RandomGeneration.generateRandomAgents(events, numAgents, numAgentVar, obsRate, ctrRate);
		
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

		printIncrementalLabel(testName, false);
		Boolean result = null;
		for(int i = 0; i < Incremental.NUM_A_HEURISTICS; i++) {
			for(int j = 0; j < Incremental.NUM_B_HEURISTICS; j++) {
				for(int k = 0; k < Incremental.NUM_C_HEURISTICS; k++) {
					Incremental.assignIncrementalOptions(i, j, k);
					printOut("\nTest Config: " + i + ", " + j + ", " + k);
					garbageCollect();
					boolean hold = checkIncrementalCoobservable(RandomGeneration.getPlantNames(testName, numPlants), RandomGeneration.getSpecNames(testName, numSpecs), agents, false);
					garbageCollect();
					if(result == null) {
						result = hold;
					}
					if(hold != result) {
						printOut("~~~ERROR!!!~~~\n - This heuristic configuration caused an improper result.");
					}
				}
			}
		}
		resetModel();
	}
	
	//-- Coobservable  ------------------------------------------------------------------------
	
	private static boolean checkCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isInferenceCoobservableUStruct(name, eventAtt, agents) : model.isCoobservableUStruct(name, eventAtt, agents);
		handleOutData(t, hold);
		printOut("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);
		garbageCollect();
		return result;
	}
	
	private static boolean checkCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isInferenceCoobservableUStruct(plants, specs, eventAtt, agents) : model.isCoobservableUStruct(plants, specs, eventAtt, agents);
		handleOutData(t, hold);
		printOut("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private static void printCoobsLabel(String system, boolean type) {
		printOut(system + " " + (type ? "Inference Coobservability:" : "Coobservability:") + " \t");
	}
	
	private static void checkSystemACoobservable(boolean inf) {
		String SystemA = "Example 1";
		SystemGeneration.generateSystemA(SystemA);
		printCoobsLabel(SystemA, inf);
		checkCoobservable(SystemA, AgentChicanery.generateAgentsA(), inf);
	}
	
	private static void checkSystemBCoobservable(boolean inf) {
		String SystemB = "Example B 1";
		SystemGeneration.generateSystemB(SystemB);
		printCoobsLabel(SystemB, inf);
		checkCoobservable(SystemB, AgentChicanery.generateAgentsB(), inf);
	}
	
	private static void checkSystemBAltCoobservable(boolean inf) {
		String SystemB = "Example B Alt";
		SystemGeneration.generateSystemBAlt(SystemB);
		makeImageDisplay(SystemB, SystemB);
		printCoobsLabel(SystemB, inf);
		checkCoobservable(SystemB, AgentChicanery.generateAgentsB(), inf);
		ArrayList<String> ustruct = model.buildUStructureCrush(SystemB, eventAtt, AgentChicanery.generateAgentsB());
		for(String s : ustruct) {
			makeImageDisplay(s, s);
			model.exportFSM(s);
		}
	}
	
	private static void checkSystemCCoobservable(boolean inf) {
		String SystemC = "Example C";
		SystemGeneration.generateSystemC(SystemC);
		printCoobsLabel(SystemC, inf);
		checkCoobservable(SystemC, AgentChicanery.generateAgentsC(), inf);
	}
	
	private static void checkSystemDCoobservable(boolean inf) {
		String SystemD = "Example D 1";
		SystemGeneration.generateSystemD(SystemD);
		printCoobsLabel(SystemD, inf);
		checkCoobservable(SystemD, AgentChicanery.generateAgentsD(), inf);
	}	
	
	private static void checkSystemECoobservable(boolean inf) {
		String SystemE = "Example E";
		SystemGeneration.generateSystemE(SystemE);
		printCoobsLabel(SystemE, inf);
		checkCoobservable(SystemE, AgentChicanery.generateAgentsE(), inf);
	}
	
	private static void checkSystemFinnCoobservable(boolean inf) {
		String SystemFinn = "System Example Finn";
		SystemGeneration.generateSystemFinn(SystemFinn);
		printCoobsLabel(SystemFinn, inf);
		checkCoobservable(SystemFinn, AgentChicanery.generateAgentsFinn5(), inf);
	}
	
	private static void checkSystemLiuOneCoobservable(boolean inf) {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> plant = new ArrayList<String>();
		ArrayList<String> spec = new ArrayList<String>();
		names.add("G1");
		plant.add("G1");
		names.add("H1");
		spec.add("H1");
		SystemGeneration.generateSystemSetA(names);
		printCoobsLabel("System Liu One", inf);
		checkCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), inf);
	}
	
	private static void checkSystemLiuTwoCoobservable(boolean inf) {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> plant = new ArrayList<String>();
		ArrayList<String> spec = new ArrayList<String>();
		names.add("G3");
		plant.add("G3");
		names.add("G4");
		plant.add("G4");
		names.add("H1");
		spec.add("H1");
		SystemGeneration.generateSystemSetB(names);
		printCoobsLabel("System Liu Two", inf);
		checkCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), inf);
	}
	
	//-- SBCoobservable  ----------------------------------------------------------------------
	
	private static boolean checkSBCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = prepSoloSpecRunSB(name, agents);
		handleOutData(t, hold);
		printOut("\t\t\t\tSB-Coobservable: " + result);
		garbageCollect();
		return result;
	}
	
	private static boolean checkSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
		handleOutData(t, hold);
		printOut("\t\t\t\tSB-Coobservable: " + result);
		garbageCollect();
		return result;
	}
	
	private static boolean prepSoloSpecRunSB(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		String b = name + "_spec";
		generateSoloSpecPlant(name, b);
		ArrayList<String> plants = new ArrayList<String>();
		ArrayList<String> specs = new ArrayList<String>();
		plants.add(name);
		specs.add(b);
		return model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
	}

	private static void printSBCoobsLabel(String system) {
		printOut(system + " SB Coobservability: \t");
	}
	
	private static void checkSystemASBCoobservable() {
		String a = "Ex1";
		SystemGeneration.generateSystemA(a);
		printSBCoobsLabel(a);
		checkSBCoobservable(a, AgentChicanery.generateAgentsA());
	}

	private static void checkSystemBSBCoobservable() {
		String ex1 = "Ex B 1";
		SystemGeneration.generateSystemB(ex1);
		printSBCoobsLabel(ex1);
		checkSBCoobservable(ex1, AgentChicanery.generateAgentsB());
	}

	private static void checkSystemCSBCoobservable() {
		String ex1 = "Ex C 1";
		SystemGeneration.generateSystemC(ex1);
		printSBCoobsLabel(ex1);
		checkSBCoobservable(ex1, AgentChicanery.generateAgentsC());
	}

	private static void checkSystemDSBCoobservable() {
		String ex1 = "Ex D 1";
		SystemGeneration.generateSystemD(ex1);
		printSBCoobsLabel(ex1);
		checkSBCoobservable(ex1, AgentChicanery.generateAgentsD());
	}
	
	private static void checkSystemESBCoobservable() {
		String SystemE = "Example E 1";
		SystemGeneration.generateSystemE(SystemE);
		printSBCoobsLabel(SystemE);
		checkSBCoobservable(SystemE, AgentChicanery.generateAgentsE());
	}
	
	private static void checkSystemUrvashiSBCoobservable() {
		ArrayList<String> plant = new ArrayList<String>();
		plant.add("plant");
		ArrayList<String> spec = new ArrayList<String>();
		spec.add("spec");
		SystemGeneration.generateSystemSetUrvashi(plant.get(0), spec.get(0));
		printSBCoobsLabel("System Urvashi");
		checkSBCoobservable(plant, spec, AgentChicanery.generateAgentsUrvashi());
	}
	
	private static void checkSystemLiuOneSBCoobservable() {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> plant = new ArrayList<String>();
		ArrayList<String> spec = new ArrayList<String>();
		names.add("G1");
		plant.add("G1");
		names.add("H1");
		spec.add("H1");
		SystemGeneration.generateSystemSetA(names);
		printSBCoobsLabel("System Liu One");
		checkSBCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne());
	}
	
	private static void checkSystemLiuTwoSBCoobservable() {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> plant = new ArrayList<String>();
		ArrayList<String> spec = new ArrayList<String>();
		names.add("G3");
		plant.add("G3");
		names.add("G4");
		plant.add("G4");
		names.add("H1");
		spec.add("H1");
		SystemGeneration.generateSystemSetB(names);
		printSBCoobsLabel("System Liu Two");
		checkSBCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne());
	}
	
	//-- Incremental Coobservable  ------------------------------------------------------------
	
	private static boolean checkIncrementalCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isIncrementalInferenceCoobservable(plants, specs, eventAtt, agents) : model.isIncrementalCoobservable(plants, specs, eventAtt, agents);
		handleOutData(t, hold);
		printOut("\t\t\t\tIncremental" + (inf ? " Inference" : "") + " Coobservable: " + result);
		garbageCollect();
		return result;
	}

	private static boolean checkIncrementalSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isIncrementalSBCoobservable(plants, specs, eventAtt, agents);
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
	
	private static void checkSystemLiuOneIncrementalCoobservable() {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> plant = new ArrayList<String>();
		ArrayList<String> spec = new ArrayList<String>();
		names.add("G1");
		plant.add("G1");
		names.add("H1");
		spec.add("H1");
		SystemGeneration.generateSystemSetA(names);
		printIncrementalLabel("System Liu One", false);
		checkIncrementalCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), false);
	}
	
	private static void checkSystemLiuTwoIncrementalCoobservable() {
		ArrayList<String> names = new ArrayList<String>();
		ArrayList<String> plant = new ArrayList<String>();
		ArrayList<String> spec = new ArrayList<String>();
		names.add("G3");
		plant.add("G3");
		names.add("G4");
		plant.add("G4");
		names.add("H1");
		spec.add("H1");
		SystemGeneration.generateSystemSetB(names);
		printIncrementalLabel("System Liu Two", false);
		checkIncrementalCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne(), false);
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static void handleOutData(long t, long hold) {
		printOut(model.getLastProcessData().produceOutputLog());
		long res = (System.currentTimeMillis() - t);
		printTimeTook(res);
		double val = inMB(getCurrentMemoryUsage() - hold);
		val = val < 0 ? 0 : val;
		printMemoryUsage(val);
		ArrayList<Double> data = model.getLastProcessData().getStoredData();
		ArrayList<String> use = model.getLastProcessData().getOutputGuide();
		
		use.add(0, "Total Time");
		use.add(1, "Overall Memory Usage");
		
		printEquivalentResults(use, res, val, data);
	}
	
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
		if(terminalPrint)
			System.out.println(text);
	}
	
	private static void printEquivalentResults(ArrayList<String> guide, long time, double overallMem, ArrayList<Double> vals) {
		if(writePath != null) {
			File f = new File(writePath + "/" + ANALYSIS_FILE);
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
	
	private static void garbageCollect() {
		System.gc();
		Runtime.getRuntime().gc();
	}
	
	private static void resetModel() {
		model.flushFSMs();
		garbageCollect();
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
	
	private static void generateSoloSpecPlant(String plant, String spec) {
		model.convertSoloPlantSpec(plant, spec);
	}

	private static void makeImageDisplay(String in, String nom) {
		String path = FormatConversion.createImgFromFSM(model.generateFSMDot(in), nom);
		//System.out.println(path);
		WindowFrame fram = new WindowFrame(800, 800) {
			
			@Override
			public void reactToResize() {
				
			}
			
			
			
		};
		fram.reserveWindow("Main");
		fram.setName("Test Functionality: " + nom);
		fram.showActiveWindow("Main");
		ElementPanel p = new ElementPanel(0, 0, 800, 800);
		ImageDisplay iD = new ImageDisplay(path, p);
		p.setEventReceiver(iD.generateEventReceiver());
		fram.addPanelToWindow("Main", "pan", p);
		iD.refresh();
	}
	
	private static void makeSVGImage(String in, String nom) {
		String path = FormatConversion.createSVGFromFSM(model.generateFSMDot(in), nom);
		printOut(path);
	}
	
}
