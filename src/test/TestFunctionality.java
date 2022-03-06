package test;

import java.util.ArrayList;
import java.util.HashMap;

import controller.FiniteStateMachine;
import controller.convert.FormatConversion;
import model.Manager;
import test.help.AgentChicanery;
import test.help.EventSets;
import test.help.SystemGeneration;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class TestFunctionality {

//---  Instance Variables   -------------------------------------------------------------------
	
	private static Manager model;
	
	private static ArrayList<String> eventAtt;
	
//---  Operations   ---------------------------------------------------------------------------
	
	public static void main(String[] args) {
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_IMAGES, FiniteStateMachine.ADDRESS_CONFIG);
		FiniteStateMachine.fileConfiguration();
		model = new Manager();
		
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
		runAllTests();
	}
	
//---  Automated Testing   --------------------------------------------------------------------
	
	private static void runAllTests() {
		System.out.print("System A Coobservability: \t");
		checkSystemACoobservable();
		System.out.print("System A SB Coobservability: \t");
		checkSystemASBCoobservable();
		System.out.print("System B Coobservability: \t");
		checkSystemBCoobservable();
		System.out.print("System B SB Coobservability: \t");
		checkSystemBSBCoobservable();
		System.out.print("System C Coobservability: \t");
		checkSystemCCoobservable();
		System.out.print("System C SB Coobservability: \t");
		checkSystemCSBCoobservable();
		System.out.print("System D Coobservability: \t");
		checkSystemDCoobservable();
		System.out.print("System D SB Coobservability: \t");
		checkSystemDSBCoobservable();
		System.out.print("System E Coobservability: \t");
		checkSystemECoobservable();
		System.out.print("System E SB Coobservability: \t");
		checkSystemESBCoobservable();
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
	
	//-- Coobservable  ------------------------------------------------------------------------
	
	private static void checkCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		long t = System.currentTimeMillis();
		System.out.println(model.isCoobservableUStruct(name, eventAtt, agents));
		System.out.println("\t\t\t\tTook " + (System.currentTimeMillis() - t) + " ms\n");
	}
	
	private static void checkSystemACoobservable() {
		String SystemA = "Example 1";
		SystemGeneration.generateSystemA(SystemA);
		checkCoobservable(SystemA, AgentChicanery.generateAgentsA());
	}
	
	private static void checkSystemBCoobservable() {
		String SystemB = "Example B 1";
		SystemGeneration.generateSystemB(SystemB);
		checkCoobservable(SystemB, AgentChicanery.generateAgentsB());
	}
	
	private static void checkSystemCCoobservable() {
		String SystemC = "Example C 1";
		SystemGeneration.generateSystemC(SystemC);
		checkCoobservable(SystemC, AgentChicanery.generateAgentsC());
	}
	
	private static void checkSystemDCoobservable() {
		String SystemD = "Example D 1";
		SystemGeneration.generateSystemD(SystemD);
		checkCoobservable(SystemD, AgentChicanery.generateAgentsD());
	}	
	
	private static void checkSystemECoobservable() {
		String SystemE = "Example E 1";
		SystemGeneration.generateSystemE(SystemE);
		checkCoobservable(SystemE, AgentChicanery.generateAgentsE());
	}
	
	//-- SBCoobservable  ----------------------------------------------------------------------
	
	private static void checkSBCoobservable(String name, ArrayList<HashMap<String, ArrayList<Boolean>>> agents) {
		String b = name + "_spec";
		generateSoloSpecPlant(name, b);
		ArrayList<String> plants = new ArrayList<String>();
		ArrayList<String> specs = new ArrayList<String>();
		plants.add(name);
		specs.add(b);
		long t = System.currentTimeMillis();
		System.out.println(model.isSBCoobservableUrvashi(plants, specs, eventAtt, agents));
		System.out.println("\t\t\t\tTook " + (System.currentTimeMillis() - t) + " ms\n");
	}

	private static void checkSystemASBCoobservable() {
		String a = "Ex1";
		SystemGeneration.generateSystemA(a);
		checkSBCoobservable(a, AgentChicanery.generateAgentsA());
	}

	private static void checkSystemBSBCoobservable() {
		String ex1 = "Ex B 1";
		SystemGeneration.generateSystemB(ex1);
		checkSBCoobservable(ex1, AgentChicanery.generateAgentsB());
	}

	private static void checkSystemCSBCoobservable() {
		String ex1 = "Ex C 1";
		SystemGeneration.generateSystemC(ex1);
		checkSBCoobservable(ex1, AgentChicanery.generateAgentsC());
	}

	private static void checkSystemDSBCoobservable() {
		String ex1 = "Ex D 1";
		SystemGeneration.generateSystemD(ex1);
		checkSBCoobservable(ex1, AgentChicanery.generateAgentsD());
	}
	
	private static void checkSystemESBCoobservable() {
		String SystemE = "Example E 1";
		SystemGeneration.generateSystemE(SystemE);
		checkSBCoobservable(SystemE, AgentChicanery.generateAgentsE());
	}
	
//---  Support Methods   ----------------------------------------------------------------------
	
	private static void generateSoloSpecPlant(String plant, String spec) {
		model.convertSoloPlantSpec(plant, spec);
	}
	
	private static String generateRandomFSM(String nom, Manager model, int numStates, int numEvents, int numTransition, boolean det, ArrayList<String> strAtt, ArrayList<String> eveAtt, ArrayList<String> tranAtt, ArrayList<Integer> numbers) {
		return model.readInFSM(model.generateRandomFSM(nom, numStates, numEvents, numTransition, det, strAtt, eveAtt, tranAtt, numbers));
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
