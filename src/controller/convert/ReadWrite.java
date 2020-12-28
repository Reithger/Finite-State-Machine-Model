package controller.convert;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Scanner;

import model.fsm.event.Event;
import model.fsm.event.EventMap;
import model.fsm.state.State;
import model.fsm.state.StateMap;
import model.fsm.transition.Transition;
import model.fsm.transition.TransitionFunction;

/**
 * This class is used for conversions between FSM objects and their Text File representations for reading/writing.
 * 
 * This class is a part of the support package.
 * 
 * @author Ada Clevinger and Graeme Zinck 
 */

public class ReadWrite{

//---  Operations   ---------------------------------------------------------------------------
	
	/**
	 * This method converts the data structures configuring the provided FSM to a File format.
	 * 
	 * @param - String object representing the File Path to write the converted FSM to.
	 * @param - String object representing the pre-calculated special attributes that are written before any Transitions.
	 * @param - TransitionFunction<<r>T> object representing the Transitions that are written after the Special Attributes.
	 * @return - Returns a boolean value representing the result of this method's attempt to write to the File.
	 */
	
	public boolean writeToFile(String filePath, String special, TransitionFunction transF, String ext) {
		try {
			File f = new File(filePath + ext);
			f.delete();
			RandomAccessFile raf = new RandomAccessFile(f, "rw");
			try {
			  raf.writeBytes(special);
			  String build = "";
			  for(State state1 : transF.getStates()) {
				for(Transition trans : transF.getTransitions(state1)) {
					for(State state2 : trans.getTransitionStates()) {
						build += state1.getStateName() + " " + state2.getStateName() + " " + trans.getTransitionEvent().getEventName() + "\n";
				  }
				}
			  }
			  if(build.length() > 0)
			  	build = build.substring(0, build.length()-1);
			  raf.writeBytes(build);
			  raf.close();
			}
			catch(IOException e1) {
				e1.printStackTrace();
				try {
					raf.close();
				}
				catch(Exception e) {
					e.printStackTrace();
				}
				return false;
			}
			return true;
		}
		catch(FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * This method reads from the provided file information that is used to fill the sets of States,
	 * Events, and Transitions that are passed to this method, returning the additional information
	 * that each class handles separately. (Initial States, Marked States, etc.)
	 * 
	 * @param states - StateMap object that represents the empty set of States to be filled.
	 * @param events - EventMap object that represents the empty set of Events to be filled.
	 * @param transitions - TransitionFunction<<r>T> object that represents the empty set of Transitions to be filled.
	 * @param file - File object that holds the provided information instructing how to construct the FSM object.
	 * @return - Returns an ArrayList<<r>ArrayList<<r>String>> object that contains the additional information about this FSM object based on its type.
	 */
	
	public ArrayList<ArrayList<String>> readFromFile(StateMap states, EventMap events, TransitionFunction transitions, File file){
		Scanner sc = null;
		try {
			sc = new Scanner(file);
			int numSpec = sc.nextInt();
			ArrayList<ArrayList<String>> specialInfo = new ArrayList<ArrayList<String>>();
			for(int i = 0; i < numSpec; i++) {
				int numIndSpec = sc.nextInt(); sc.nextLine();
				ArrayList<String> oneBatch = new ArrayList<String>();
				for(int j = 0; j < numIndSpec; j++) {
					oneBatch.add(sc.nextLine());
				}
				specialInfo.add(oneBatch);
			}
			while(sc.hasNextLine()) {
				String[] in = sc.nextLine().split(" ");
				if(in.length < 3)
					break;
				State fromState = states.addState(in[0]);
				State toState = states.addState(in[1]);
				Event event = events.addEvent(in[2]);
				
				// See if there is already a transition with the event...
				ArrayList<Transition> thisTransitions = transitions.getTransitions(fromState);
				boolean foundTransition = false;
				if(thisTransitions != null) {
					Iterator<Transition> itr = thisTransitions.iterator();
					while(!foundTransition && itr.hasNext()) {
						Transition t = itr.next();
						if(t.getTransitionEvent().equals(event)) {
							t.setTransitionState(toState);
							foundTransition = true;
						} // if equal
					} // for every transition
				} // if not null
				if(!foundTransition) {
					Transition outbound = transitions.getEmptyTransition();
					outbound.setTransitionEvent(event);
					outbound.setTransitionState(toState);
					transitions.addTransition(fromState, outbound);
				} // if did not find transition
			} // while sc has next line
			sc.close();
			return specialInfo;
		}
		catch(Exception e) {
			e.printStackTrace();
			System.out.println("Failure during File Reading");
			try {
				sc.close();
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
			return null;
		}
		
	}
	
}
