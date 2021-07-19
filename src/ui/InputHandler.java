package ui;

public interface InputHandler {

//---  Operations   ---------------------------------------------------------------------------
	
	public abstract void receiveCode(int code, int mouseType);

	public abstract void receiveKeyInput(char code, int keyType);
	
}