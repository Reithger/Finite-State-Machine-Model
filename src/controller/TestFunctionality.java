package controller;

import java.util.ArrayList;

import controller.convert.FormatConversion;
import input.CustomEventReceiver;
import model.AttributeList;
import model.Manager;
import ui.popups.PopoutAgentSelection;
import visual.composite.ImageDisplay;
import visual.frame.WindowFrame;
import visual.panel.ElementPanel;

public class TestFunctionality {

	public static void main(String[] args) {
		
		ArrayList<String> a = new ArrayList<String>();
		a.add("a");
		a.add("b");
		a.add("c");
		ArrayList<String> b = new ArrayList<String>();
		b.add("Observability");
		b.add("Controlability");
		
		PopoutAgentSelection.assignSymbols("---", "o", "x");
		PopoutAgentSelection pAS = new PopoutAgentSelection(new ArrayList<String>(), a, b);
		System.out.println(pAS.getResult());
		/*
		Manager model = new Manager();
		FormatConversion.assignPaths(FiniteStateMachine.ADDRESS_SOURCES, FiniteStateMachine.ADDRESS_CONFIG);
		ArrayList<String> strAtt = new ArrayList<String>();
		strAtt.add(AttributeList.ATTRIBUTE_INITIAL);
		strAtt.add(AttributeList.ATTRIBUTE_MARKED);
		ArrayList<String> eveAtt = new ArrayList<String>();
		eveAtt.add(AttributeList.ATTRIBUTE_OBSERVABLE);
		ArrayList<String> tranAtt = new ArrayList<String>();
		ArrayList<Integer> numbers = new ArrayList<Integer>();
		numbers.add(1);
		numbers.add(1);
		numbers.add(2);
		String nom = generateRandomFSM("test", model, 5, 3, 2, true, strAtt, eveAtt, tranAtt, numbers);
		String nom2 = generateRandomFSM("test2", model, 5, 3, 2, true, strAtt, eveAtt, tranAtt, numbers);
		String nom3 = generateRandomFSM("test3", model, 5, 3, 2, true, strAtt, eveAtt, tranAtt, numbers);

		makeImageDisplay(nom, model, "Root 1");
		makeImageDisplay(nom2, model, "Root 2");
		makeImageDisplay(nom3, model, "Root 3");
		
		ArrayList<String> names = new ArrayList<String>();
		names.add(nom);
		names.add(nom2);
		
		String trim = model.performParallelComposition(names);
		
		makeImageDisplay(trim, model, "Product");
		makeImageDisplay(model.makeAccessible(trim), model, "Product");
		*/
	}
	
	private static String generateRandomFSM(String nom, Manager model, int a, int b, int c, boolean det, ArrayList<String> strAtt, ArrayList<String> eveAtt, ArrayList<String> tranAtt, ArrayList<Integer> numbers) {
		return model.readInFSM(model.generateRandomFSM(nom, a, b, c, det, strAtt, eveAtt, tranAtt, numbers));
	}
	
	private static void makeImageDisplay(String in, Manager model, String nom) {
		String path = FormatConversion.createImgFromFSM(model.generateFSMDot(in), nom);
		WindowFrame fram = new WindowFrame(800, 800);
		fram.reserveWindow("Main");
		fram.setName("Test Functionality: " + nom);
		fram.showActiveWindow("Main");
		ElementPanel p = new ElementPanel(0, 0, 800, 800);
		ImageDisplay iD = new ImageDisplay(path, p);
		p.setEventReceiver(new CustomEventReceiver() {
			@Override
			public void clickEvent(int code, int x, int y, int mouseType) {
				iD.processClickInput(code);
			}
			@Override
			public void dragEvent(int code, int x, int y, int mouseType) {
				iD.processDragInput(code, x, y);
			}
			@Override
			public void mouseWheelEvent(int rot) {
				iD.processMouseWheelInput(rot);
			}
			@Override
			public void clickPressEvent(int code, int x, int y, int mouseType) {
				iD.processPressInput(code, x, y);
			}
			@Override
			public void clickReleaseEvent(int code, int x, int y, int mouseType) {
				iD.processReleaseInput(code, x, y);
			}
		});
		fram.addPanelToWindow("Main", "pan", p);
	}
	
}
