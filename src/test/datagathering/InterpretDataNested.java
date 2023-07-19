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
	
	public void assignTotalNumberTestsNested(int in) {
		subAssociate.assignTotalNumberTests(in);
	}
	
	@Override
	public void setColumnFilter(Integer in) {
		super.setColumnFilter(in);
		if(subAssociate != null) {
			subAssociate.setColumnFilter(in);
		}
	}
	
	@Override
	public void setFilterValue(Double in) {
		super.setFilterValue(in);
		if(subAssociate != null) {
			subAssociate.setFilterValue(in);
		}
	}
	
}
