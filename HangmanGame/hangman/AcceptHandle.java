package hangman;

import java.net.*;
import reactor.*;
import reactorapi.*;

class AcceptHandle implements Handle {
    ServerSocket socket;

    public AcceptHandle() {
	try {
	    socket=new ServerSocket(0);
	    System.out.println(""+socket.getLocalPort());
	} catch(Exception ioe) {
	    /* No free ports. Die noisily. */
	    throw new RuntimeException(ioe);
	}
    }

    public Object read() {
	try {
	    Socket s=socket.accept();
            
	    return s;
	} catch(Exception e) {
	    /* Socket error. Assume connection lost. */
	    return null;
	}
    }

    public void close() {
	try {
	    socket.close();
	} catch(Exception e) {
	   
	}
    }
}