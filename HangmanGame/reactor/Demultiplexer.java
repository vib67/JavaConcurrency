/*
 * Demultiplexer : This class keeps tracks the evnt occured , if so then calls the consumer to consume the event.
 * 
 */



package reactor;
import reactorapi.*;

public class Demultiplexer {
  
      private EventHandler iEh;
     
      private boolean iEventOccured;
      private Object message;
      
      public Demultiplexer() {
      message = new Object();
      iEh = null;
      iEventOccured = false;
      }
  
      public synchronized void put(EventHandler handler,Object event){   //to riased the state that event has occurred.
          while ( iEventOccured == true)
            { //check for late events  
               try{
                  wait();
               }catch (InterruptedException e) { }
            }
         
                        // submit the event for the consumer !!            
          iEh= handler;
          message = event;
          iEventOccured = true;
          notifyAll();  //notifies all the blocked thread;
         
       }
       public synchronized EventConsumer select(){
           while ( iEventOccured == false)
              {   // the event has not occured yet, wait for the event
                 try{
                 wait();
                 }catch (InterruptedException e) { }
              }
            
           // preapere the consumer for eating !!
           EventConsumer eventtoconsume = new EventConsumer(message,iEh);          
            iEventOccured = false;
            notifyAll();    //same inefficient way of informing the thread but works  !! so kept there 
            return eventtoconsume;
       }
       
} // end of the class.

 
