package model.process.coobservability;

public class StateSet{
	
//---  Instance Variables   -------------------------------------------------------------------
	
	private static int sizePlants;
	
	private static int sizeSpecs;
	
	private String[] plant;
	
	private String[] spec;
	
//---  Constructors   -------------------------------------------------------------------------
	
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
	
	public static void assignSizes(int sizePl, int sizeSp) {
		sizePlants = sizePl;
		sizeSpecs = sizeSp;
	}
	
//---  Getter Methods   -----------------------------------------------------------------------
	
	public String[] getPlantStates() {
		return plant;
	}
	
	public String[] getSpecStates() {
		return spec;
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
	
}