package test.datagathering;

public class InterpretDataNested extends InterpretData{

	private InterpretData subAssociate;
	
	public void addAssociatedDataRow(String[] data) {
		if(subAssociate == null) {
			subAssociate = new InterpretData();
		}
		subAssociate.addDataRow(data);
	}
	
	public InterpretData getAssociatedData() {
		return subAssociate;
	}
	
}
