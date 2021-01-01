package controller;

public interface InputReceiver {

//---  Operations   ---------------------------------------------------------------------------
	
	public abstract void receiveCode(int code, String ref, int mouseType);

	public abstract void receiveKeyInput(char code, String ref, int keyType);
	
}
