package afterdark;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Consumer;
import java.util.function.Function;

import afterdark.ClientConnect.SpeedTestCallbacks;
import fr.bmartel.speedtest.SpeedTestReport;
import fr.bmartel.speedtest.SpeedTestSocket;
import fr.bmartel.speedtest.inter.ISpeedTestListener;
import fr.bmartel.speedtest.model.SpeedTestError;

public class SpeedTester {
	SpeedTestSocket speedTestSocket = new SpeedTestSocket();
	int progress = 0;
	
	public void setUp(SpeedTestCallbacks callbacks) {
		// add a listener to wait for speedtest completion and progress
		speedTestSocket.addSpeedTestListener(new ISpeedTestListener() {

			@Override
		    public void onCompletion(SpeedTestReport report) {
		        // called when download/upload is complete
		        
		        System.out.println("[COMPLETED] Kbps   : " + report.getTransferRateOctet().divide(new BigDecimal(1000.0),1,RoundingMode.HALF_DOWN).toString());
		        callbacks.completed(report.getTransferRateOctet().divide(new BigDecimal(1000.0),1,RoundingMode.HALF_DOWN).toString());
		    }

		    @Override
		    public void onError(SpeedTestError speedTestError, String errorMessage) {
		         // called when a download/upload error occur
		    }

		    @Override
		    public void onProgress(float percent, SpeedTestReport report) {
		        // called to notify download/upload progress
		    	progress+=1;
		        
		        callbacks.progress("[PROGRESS] progress "+progress+"/5", 
		        		report.getTransferRateOctet().divide(new BigDecimal(1000000.0),1,RoundingMode.HALF_DOWN).toString(), 
		        		report.getTransferRateBit().divide(new BigDecimal(1000000.0),1,RoundingMode.HALF_DOWN).toString()
		        );
		        
		    }
		});
	}
	
	public void downlinkTest() {
		speedTestSocket.startFixedDownload("http://ipv4.scaleway.testdebit.info/1G.iso", 5000,1000);
	}
}
