package test;

import java.util.ArrayList;
import java.util.HashMap;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import model.Manager;
import test.help.AgentChicanery;
import test.help.EventSets;
import test.help.RandomGeneration;
import test.help.SystemGeneration;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class TestFunctionality {

//---  Instance Variables   -------------------------------------------------------------------
	
	private static Manager model;
	
	private static ArrayList<String> eventAtt;
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void main(String[] args) throws Exception {
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES, FiniteStateMachine.ADDRESS_CONFIG);
		FiniteStateMachine.fileConfiguration();
		model = new Manager();
		
		model.assignCoobservabilityPrintOutState(false, false, true);
		
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
		
		autoTestNewRandomSystem("test", 2, 2, 5, 2, 4, 2, .35, 2, 0, .3, .3);

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
		System.out.println("\n\t\t\t\t~~~ Testing System A ~~~\n");
		checkSystemACoobservable(false);
		checkSystemASBCoobservable();

		System.out.println("\n\t\t\t\t~~~ Testing System B ~~~\n");
		checkSystemBCoobservable(false);
		checkSystemBSBCoobservable();

		System.out.println("\n\t\t\t\t~~~ Testing System C ~~~\n");
		checkSystemCCoobservable(false);
		checkSystemCSBCoobservable();

		System.out.println("\n\t\t\t\t~~~ Testing System D ~~~\n");
		checkSystemDCoobservable(false);
		checkSystemDSBCoobservable();

		System.out.println("\n\t\t\t\t~~~ Testing System E ~~~\n");
		checkSystemECoobservable(false);
		checkSystemESBCoobservable();

		System.out.println("\n\t\t\t\t~~~ Testing System Finn ~~~\n");
		checkSystemFinnCoobservable(false);
		
		System.out.println("\n\t\t\t\t~~~ Testing System Urvashi ~~~\n");
		checkSystemUrvashiSBCoobservable();

		System.out.println("\n\t\t\t\t~~~ Testing System Liu One ~~~\n");
		checkSystemLiuOneCoobservable(false);
		checkSystemLiuOneSBCoobservable();
		checkSystemLiuOneIncrementalCoobservable();

		System.out.println("\n\t\t\t\t~~~ Testing System Liu Two ~~~\n");
		checkSystemLiuTwoCoobservable(false);
		checkSystemLiuTwoSBCoobservable();
		checkSystemLiuTwoIncrementalCoobservable();
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
	
	private static void autoTestNewRandomSystem(String prefix, int numPlants, int numSpecs, int numStates, int numStateVar, int numEve, int numEveVar, double shareRate, int numAgents, int numAgentVar, double obsRate, double ctrRate) throws Exception {
		ArrayList<String> events = RandomGeneration.generateRandomSystemSet(prefix, model, numPlants, numSpecs, numStates, numStateVar, numEve, numEveVar, shareRate);
		ArrayList<String> names = RandomGeneration.getComponentNames(prefix, numPlants, numSpecs);
		
		for(String s : names) {
			makeImageDisplay(s, s);
		}
		
		ArrayList<HashMap<String, ArrayList<Boolean>>> agents = RandomGeneration.generateRandomAgents(events, numAgents, numAgentVar, obsRate, ctrRate);
		System.out.println(agents.toString().replace("},", "},\n"));
		
		autoTestSystemFull(prefix, RandomGeneration.getPlantNames(prefix, numPlants), RandomGeneration.getSpecNames(prefix, numSpecs), agents);

	}
	
	private static void autoTestSystemFull(String prefixNom, ArrayList<String> plantNames, ArrayList<String> specNames, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		printCoobsLabel(prefixNom, false);
		checkCoobservable(plantNames, specNames, agents, false);
		printSBCoobsLabel(prefixNom);
		checkSBCoobservable(plantNames, specNames, agents);
		printIncrementalLabel(prefixNom);
		checkIncrementalCoobservable(plantNames, specNames, agents);
		printCoobsLabel(prefixNom, true);
		checkCoobservable(plantNames, specNames, agents, true);
	}
	
	//-- Coobservable  ------------------------------------------------------------------------
	
	private static void checkCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isInferenceCoobservableUStruct(name, eventAtt, agents) : model.isCoobservableUStruct(name, eventAtt, agents);
		printTimeTook(t);
		printMemoryUsage(hold);
		System.out.println("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);
		garbageCollect();
	}
	
	private static void checkCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents, boolean inf) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = inf ? model.isInferenceCoobservableUStruct(plants, specs, eventAtt, agents) : model.isCoobservableUStruct(plants, specs, eventAtt, agents);
		printTimeTook(t);
		printMemoryUsage(hold);
		System.out.println("\t\t\t\t" + (inf ? "Inferencing " : "" ) + "Coobservable: " + result);
		garbageCollect();
	}

	private static void printCoobsLabel(String system, boolean type) {
		System.out.println(system + " " + (type ? "Inference Coobservability:" : "Coobservability:") + " \t");
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
	
	private static void checkSBCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = prepSoloSpecRunSB(name, agents);
		printTimeTook(t);
		printMemoryUsage(hold);
		System.out.println("\t\t\t\tSB-Coobservable: " + result);
		garbageCollect();
	}
	
	private static void checkSBCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents);
		printTimeTook(t);
		printMemoryUsage(hold);
		System.out.println("\t\t\t\tSB-Coobservable: " + result);
		garbageCollect();
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
		System.out.println(system + "SB Coobservability: \t");
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
	
	private static void checkIncrementalCoobservable(ArrayList<String> plants, ArrayList<String> specs, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		long hold = getCurrentMemoryUsage();
		boolean result = model.isIncrementalCoobservable(plants, specs, eventAtt, agents);
		printTimeTook(t);
		printMemoryUsage(hold);
		System.out.println("\t\t\t\tIncremental Coobservable: " + result);
		garbageCollect();
	}
	
	private static void printIncrementalLabel(String system) {
		System.out.println(system + " Incremental Coobservability: \t");
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
		printIncrementalLabel("System Liu One");
		checkIncrementalCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne());
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
		printIncrementalLabel("System Liu Two");
		checkIncrementalCoobservable(plant, spec, AgentChicanery.generateAgentsLiuOne());
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static void garbageCollect() {
		System.gc();
		Runtime.getRuntime().gc();
	}
	
	private static void printTimeTook(long t) {
		System.out.println("\t\t\t\tTook " + (System.currentTimeMillis() - t) + " ms");
	}
	
	private static void printMemoryUsage(long reduction) {
		System.out.println("\t\t\t\tUsing " + threeSig(inMB(getCurrentMemoryUsage() - reduction)) + " Mb");
		garbageCollect();
	}
	
	private static long getCurrentMemoryUsage() {
		Runtime r = Runtime.getRuntime();
		return ((r.totalMemory() - r.freeMemory()));
	}
	
	private static double inMB(long in) {
		return (double)in / 1000000;
	}
	
	private static String threeSig(double in) {
		String use = in+"000000000";
		int posit = use.indexOf(".") + 4;
		return use.substring(0, posit);
	}
	
	private static void generateSoloSpecPlant(String plant, String spec) {
		model.convertSoloPlantSpec(plant, spec);
	}

	private static void makeImageDisplay(String in, String nom) {
		String path = FormatConversion.createImgFromFSM(model.generateFSMDot(in), nom);
		System.out.println(path);
		WindowFrame fram = new WindowFrame(800, 800);
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
		System.out.println(path);
	}
	
}
