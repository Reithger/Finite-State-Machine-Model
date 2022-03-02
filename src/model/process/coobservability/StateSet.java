package model.process.coobservability;

public class StateSet{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	/** Number of plants*/
	private static int sizePlants;
	/** Number of specifications*/
	private static int sizeSpecs;
	/** List of our plants*/
	private String[] plant;
	/** List of our specifications*/
	private String[] spec;
	
//---  Constructors   -------------------------------------------------------------------------
	
	/**
	 * 
	 * After statically assigning the size of our plant and specification lists, take an input array
	 * of Strings representing State names mapping to each of our plant and specifications. Uses the
	 * number of plants and specifications to know when the Strings stop representing plant States
	 * and start representing specification States.
	 * 
	 * @param in - String[] containing the names of States in plants and specifications
	 */
	
	public StateSet(String[] in) {
		plant = new String[sizePlants];
		spec = new String[sizeSpecs];
		for(int i = 0; i < in.length; i++) {
			if(i < sizePlants) {
				plant[i] = in[i];
			}
			else {
				spec[i - sizePlants] = in[i];
			}
		}
	}
	
//---  Setter Methods   -----------------------------------------------------------------------
	
	/**
	 * 
	 * Static assignment function to inform the StateSet class of objects how many plants and
	 * specifications there are to expect State names from when representing a set of States.
	 * 
	 * @param sizePl
	 * @param sizeSp
	 */
	
	public static void assignSizes(int sizePl, int sizeSp) {
		sizePlants = sizePl;
		sizeSpecs = sizeSp;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String[] getPlantStates() {
		return plant;
	}
	
	public String getPlantState(int index) {
		return plant[index];
	}
	
	public String[] getSpecStates() {
		return spec;
	}
	
	public String getSpecState(int index) {
		return spec[index];
	}
	
	public String getState(int index) {
		boolean isPlant = index < sizePlants;
		return isPlant ? plant[index] : spec[index - sizePlants];
	}
	
	public String getPairName() {
		String out = "(";
		for(String s : plant) {
			out += s + ", ";
		}
		for(String s : spec) {
			out += s + ", ";
		}
		out = out.substring(0, out.length() - 2);
		return out + ")";
	}
	
//---  Mechanics   ----------------------------------------------------------------------------
	
	@Override
	public int hashCode() {
		return getPairName().hashCode();
	}
	
	@Override
	public boolean equals(Object o) {
		try {
			StateSet ot = (StateSet)o;
			return getPairName().equals(ot.getPairName());
		}
		catch(Exception e) {
			e.printStackTrace();
			return false;
		}
	}
	
}