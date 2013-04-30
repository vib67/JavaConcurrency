
/*This class is responsible for starting the  thread  and listenign for read */



package reactor;
import reactorapi.*;

public class EventProducer extends Thread{
	
	private EventHandler iEh;
	boolean iCanThreadRun;
	private Demultiplexer iDemulx;
	
	public EventProducer(Demultiplexer d,EventHandler h ) {
		iDemulx = d;
		this.iEh = h;
		iCanThreadRun = true;
	}

	public EventHandler getEventHandler(){
		return iEh;
	}
	
	public void run(){
		//first block for a message(event) on this thread
	//	System.err.println("Started Event Thread " +this );
		while(iCanThreadRun)
		{
			Handle handle = iEh.getHandle(); 
			Object message = handle.read();
			
			// now put up your result to the demuliplexer
			
			/*if(!iCanThreadRun)
			{
				
				return;
			}*/
			
			
			
			iDemulx.put(iEh,message);
			if(message == null)   //if message is null then loop should not repeat
				iCanThreadRun = false;
			
			
		
			
			
		}
	}
	
	public void StopReading(){
		iCanThreadRun = false;
	}
	 
	

	
}



