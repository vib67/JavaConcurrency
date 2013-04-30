package chat;

import tuplespaces.TupleSpace;

public class ChatListener {
	private TupleSpace ts;
	private String[] pattern;
	public ChatListener(TupleSpace ts, String[] pattern) {
	
		// TODO Auto-generated constructor stub
        this.ts= ts;		
        this.pattern= pattern;		
		
	}
    public String getNextMessage() {
	 return ts.get(pattern)[2];
	
	
    }

    public void closeConnection() {
    	String[] tuple = ts.get(new String[]{"_ChannelListeners_", pattern[0], null});  //get the channel listtners
    	String[] list = tuple[2].split(";");
    	String lst = "";
    	for (int i =0;i<list.length;i++){  //removing the id from the list
    		String s =list[i];
    		if (!s.equalsIgnoreCase(pattern[1])){ 
    			if (lst.length() != 0)
    				lst += ";";
    			lst += s;
    		}
    	}
    	tuple[2] = lst;
    	ts.put(tuple);
    }
}
