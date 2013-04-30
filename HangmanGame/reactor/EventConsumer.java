package reactor;
import reactorapi.*;

/* 
 *  Thsi class consumes the event snd psses the sme to the hangman server!!
 */


 public class EventConsumer {
	 private EventHandler iEvenhandler;
          private Object iEventtoCons;
          
          EventConsumer( Object aEventtocons, EventHandler aEvenhandler)
            {
        	  iEventtoCons = aEventtocons;
                this.iEvenhandler= aEvenhandler;
             }
          
          
           public Object getmessage(){ 
              return iEventtoCons;
           }
           
           
           public EventHandler getEventHandler(){
               return iEvenhandler;
           }
}