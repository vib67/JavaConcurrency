package hangman;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.*;
import java.util.ArrayList;

import reactor.*;
import reactorapi.*;

public class HangmanServer {
	Dispatcher d=new Dispatcher();
	String iWord;
	String iCurrentStatus;
	
	int iCurrentAttemptsLeft;
	AcceptEventHandler ahandler;
	
	int PlayerCount;
	private ArrayList<PlayerHandler> player_list;

	public static void main( String[] args) {
		HangmanServer hangmanserver =  new HangmanServer(args[0],Integer.parseInt(args[1]));
		hangmanserver.execute();
	}

	public void execute() {
		try {
			d.handleEvents();
		} catch(InterruptedException ie) {
			return;
		}
	}


	public HangmanServer(String word,int attempt){
		iWord = word;   //word to be found out 
		iCurrentAttemptsLeft = attempt; //left attempt
		iCurrentStatus = "";
		for( int i = 0;i < word.length();i++)
			iCurrentStatus += '-';

		//create an empty PlayerHandlerList
		player_list = new ArrayList<PlayerHandler>();
		
		//create a handler and register it with dispatcher
		ahandler = new AcceptEventHandler();
		d.addHandler(ahandler);  //add handler to the dispatcher
	}



	public class AcceptEventHandler implements EventHandler<Socket> {
		AcceptHandle ahandle; 
		public AcceptEventHandler(){
			ahandle = new AcceptHandle();
		}

		public Handle getHandle(){
			return ahandle;
		}
		public void handleEvent(Socket s){
			//Socket playersocket = (Socket)s;
			PlayerHandler phandler = new PlayerHandler(s);
			d.addHandler(phandler);
			player_list.add(phandler);
			
		}
	}
	public class PlayerHandler implements EventHandler {
		PlayerHandle phandle; 
		String PlayerName;
		public PlayerHandler(Socket s){
			phandle = new PlayerHandle(s);
			PlayerName = "";
		}
		public Handle getHandle(){
			return phandle;
		}
		public void handleEvent(Object s){
			String message = (String)s;
			//is the message a null message?
			if ((message== null) || (message.length()==0))
			{
				//RemoveFromPlayerList(this);
				player_list.remove(this);
				//remove this handler from the player list
				d.removeHandler(this);
				PlayerHandle handle = (PlayerHandle)getHandle();     
				handle.close();
				return;
			}
			//is the message a valid message?
			else if((message.charAt(0)< 'A')||(message.charAt(0)>'z'))
			{
				PlayerHandle handle = (PlayerHandle)getHandle();
				handle.write("Write a letter");
				return;
			}

			//Is this the first message of this player ?
			if (PlayerName == "")
			{ 
				//store this message as player name
				PlayerName = message;
				String SingleResponse = iCurrentStatus + " " + iCurrentAttemptsLeft;
				((PlayerHandle)getHandle()).write(SingleResponse);
			}
			else{
				//must be a guess response
				String ResponseAll = ResponsetoUSer(message.charAt(0)); //alter variables and respond
				//send the message to all
				for (PlayerHandler p : player_list)
					((PlayerHandle)p.getHandle()).write(ResponseAll);			

				//did players win or loose?
				if((iWord.compareTo(iCurrentStatus)==0)||(iCurrentAttemptsLeft == 0))
				{
					//cleanup everything and exit
					for (PlayerHandler p : player_list)
						((PlayerHandle)p.getHandle()).close();
					
					//remove the AccepterHandler too
					d.removeHandler(ahandler);
					AcceptHandle aehandle = (AcceptHandle)ahandler.getHandle();
					aehandle.close();
				}
			}
		} //handleEvent definition over

		public String ResponsetoUSer(char userip){
			boolean match = false;
			String userop = "";
			char []CurrentStatusChar = iCurrentStatus.toCharArray();
			for( int i = 0; i < iWord.length();i++)
			{
				if(userip == iWord.charAt(i))
				{
					match = true;
					CurrentStatusChar[i] = userip;
				}
			}
			iCurrentStatus = new String(CurrentStatusChar);
			if(!match)
				iCurrentAttemptsLeft--;
			//Current word status is ready, append other info
			userop = userip + " " + iCurrentStatus + " " + iCurrentAttemptsLeft + " " + PlayerName ;
			return userop;
		}  

	

	}
	class PlayerHandle implements Handle {
	    Socket socket;
	    BufferedReader in;
	    PrintStream out;

	    public PlayerHandle(Socket s) {
		socket=s;
		
		try {
		    in=new BufferedReader(new InputStreamReader(
					      socket.getInputStream()));
		    out=new PrintStream(socket.getOutputStream());
		} catch(Exception e) {
		    throw new RuntimeException("Internal socket error");
		}
	    }

	    public void write(Object s) {
		out.println(s);
	    }

	    public Object read() {
		try {
		    return in.readLine();
		} catch(Exception e) {
		    return null;
		}
	    }

	    public void close() {
		try {
		    socket.close();
		} catch(Exception e) {
		    /* It's already dead. */
		}
	   }
	}
}  







