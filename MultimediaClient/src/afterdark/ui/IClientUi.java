package afterdark.ui;

import java.awt.event.ActionListener;

//this interface, will be implemented by the swing window and will be used so that the client connector can inform the ui about state
public interface IClientUi {
	void start();
	
	void signalClose();
	
	void connectedOk();
	
	void speedTestDone();
	
	void setSpeedTestProgress(String prog,String octo,String bit);
}
