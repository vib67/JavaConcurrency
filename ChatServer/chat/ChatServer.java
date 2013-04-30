package chat;

import java.util.ArrayList;

import tuplespaces.TupleSpace;

public class ChatServer {
	private String[] chname;
	private ArrayList messages = new ArrayList(); 
	private int chid=0;
	TupleSpace ts;
	private int rows;
	private int ChannelCount; 

	public ChatServer(TupleSpace t, int rows, String[] channelNames) {



		this.chname = channelNames;
		ts = t;
		this.rows = rows;
		ts.put(new String[]{"_RowNO_" ,String.valueOf(rows)});  

		ChannelCount =channelNames.length ;   //push the channel count to the tuple space
		ts.put(new String[]{"_ChannelCount_" ,String.valueOf(ChannelCount)});

		for( int i=0; i < channelNames.length; i++)   //push the channelname ,channel count ,channel listeners count   to the tuple space
		{
			String[] ChannelNamePattern = {"_ChannelName_",channelNames[i]};
			ts.put(ChannelNamePattern);
			ts.put(new String[]{"_ChannelListenersCounter_", channelNames[i], "0"});
			ts.put(new String[]{"_ChannelListeners_", channelNames[i], ""});
			ts.put(new String[]{"_ChannelMessageMinNumber_", channelNames[i], "0"});  // push the first of the interval
			ts.put(new String[]{"_ChannelMessageCurrentNumber_", channelNames[i], "0"}); //push the last count of the interval
		}


	}




	public ChatServer(TupleSpace t) {   //constructor for the remote machines
		ts = t;

		String[] getPattern =ts.get(new String[]{"_RowNO_",null});
		rows=  Integer.parseInt(getPattern[1]);   //populate the row no
		ts.put(getPattern);

		getPattern =ts.get(new String[]{"_ChannelCount_",null});
		ChannelCount =Integer.parseInt(getPattern[1]);
		chname = new String[ChannelCount];
		
		for( int i=0; i < ChannelCount; i++)      // get the channel names
		{
			getPattern = new String[] {"_ChannelName_",null};
			chname[i]= ts.get(getPattern)[1];

		}
		
		for( int i=0; i < ChannelCount; i++)   //populate the channel names
		{
			getPattern = new String[] {"_ChannelName_",chname[i]};
			ts.put(getPattern);  //put the channel name back

		}



		ts.put(new String[]{"_ChannelCount_",String.valueOf(ChannelCount)});
	}

	public String[] getChannels() {
		//throw new UnsupportedOperationException();
		return chname;
	}

	public void writeMessage(String channel, String message) {

		String[] pattern = ts.get(new String[]{"_ChannelListeners_", channel, null});
		String[] list = pattern[2].split(";");
		for (int i=0;i<list.length;i++){   //get all the channel listners
			String s = list[i];
			ts.put(new String[]{channel, s, message});  //put the message to all the listners
		}
		ts.put(pattern); // put the listners back to tuple space

		int first = Integer.parseInt(ts.get(new String[]{"_ChannelMessageMinNumber_", channel, null})[2]);  //get the position number of the first message of the channel
		int last = Integer.parseInt(ts.get(new String[]{"_ChannelMessageCurrentNumber_", channel, null})[2])+1; //get the last  position

		if (first == 0)
			first++;
		if (last - first >= rows){   //it exceecds the number of rows
			ts.get(new String[]{"_ChannelMessages_", channel, "message" + first, null});  //remove the first one 
			first++;                                                                       // and increment the counter 
		}
		ts.put(new String[]{"_ChannelMessages_", channel, "message" + last, message});  //put the last message
		
		ts.put(new String[]{"_ChannelMessageCurrentNumber_", channel, String.valueOf(last)});
		ts.put(new String[]{"_ChannelMessageMinNumber_", channel, String.valueOf(first)});

	}

	public ChatListener openConnection(String channel) {
		//throw new UnsupportedOperationException();

		String id = ts.get(new String[]{"_ChannelListenersCounter_", channel, null})[2];
		ChatListener chlistener =  new ChatListener( ts, new String [] {channel, id, null} );  //new chat listner object

		//for(int i=0;i<messages.size();i++)
			//ts.put(new String []{channel, String.valueOf(chid) , (String)messages.get(i)});
		ts.put(new String[]{"_ChannelListenersCounter_", channel, String.valueOf(Integer.parseInt(id) + 1)});   //increment the lsitner id 
		String lst = ts.get(new String[]{"_ChannelListeners_", channel, null})[2]; 
		if (lst.length() != 0)
			lst += ";";
		lst += id;    //put the id into the list
		ts.put(new String[]{"_ChannelListeners_", channel, lst});
		
		int first= Integer.parseInt(ts.get(new String[]{"_ChannelMessageMinNumber_", channel, null})[2]);  //get the first message posiotn
		int last = Integer.parseInt(ts.get(new String[]{"_ChannelMessageCurrentNumber_", channel, null})[2]); //get the last message posiiton 
		
		String[] pattern;
		if (first != 0)
			for (int i = first; i <= last; i++){  //from fist message to the last put it into the new window
				pattern = ts.get(new String[]{"_ChannelMessages_", channel, "message" + i, null});
				ts.put(pattern);
				ts.put(new String[]{channel, id, pattern[3]});
			}

		ts.put(new String[]{"_ChannelMessageCurrentNumber_", channel, String.valueOf(last)});  //put the values back to the tuple spaces
		ts.put(new String[]{"_ChannelMessageMinNumber_", channel, String.valueOf(first)});
		

		return chlistener;
	}
}
