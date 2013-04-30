
/* This dispatches the call to handlers and starts the event producer threads. This thread in-turn
 * hangs on socket.read in event producer class and then it monitors user input.   
 * */
package reactor;
import java.util.ArrayList;

import reactorapi.*;

public class Dispatcher {
	private  ArrayList<EventProducer> EPthread;	
	private Demultiplexer iDemulx;

	public Dispatcher() {
		//System.err.println("Dispatcher::Dispatcher Instainaited ");
		EPthread = new ArrayList<EventProducer>();
		
		// create a demulitplexer 
		iDemulx = new Demultiplexer(); 
	}

	public void handleEvents() throws InterruptedException {
		while(EPthread.size() > 0)  
		{                                           //get the input from the user 
			EventConsumer eresult = iDemulx.select();
			Object message = eresult.getmessage();
			EventHandler ehandler = eresult.getEventHandler();
			for (EventProducer p : EPthread)
				if (p.getEventHandler() == ehandler){
					ehandler.handleEvent(message);
					break;
				}
			if (message == null)
				removeHandler(ehandler);
			
		}
	}

	public void addHandler(EventHandler h) {
		EventProducer ep = new EventProducer(iDemulx,h);
	//thread array  ,stating the event produce thread!!
		EPthread.add(ep);
		ep.start();
	}

	public void removeHandler(EventHandler h) {
		// check if the handler is a registered handler
		int i = 0;
		for (i = 0; i < EPthread.size(); i ++)
			if (EPthread.get(i).getEventHandler() == h){
				EPthread.get(i).StopReading();
				EPthread.remove(i);
				break;
			}
	}

	
}
