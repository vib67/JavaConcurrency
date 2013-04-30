package tuplespaces;

import java.util.ArrayList;

public class LocalTupleSpace implements TupleSpace {

	private ArrayList  putget ;  //arraylist  to store the patterns (tuples)

	public LocalTupleSpace() {

		putget = new ArrayList();

		// TODO Auto-generated constructor stub
	}

	private void printTuple(String[] tuple){
		for (String s : tuple)
			System.out.print(s + " ");
		System.out.println();
	}

	public  synchronized String[] get(String[] pattern) {
		//throw new UnsupportedOperationException();
		String[] s;
		int count = 0;
		while(true)
		{	
			for (int i=0;i<putget.size();i++){
				s = (String[])putget.get(i);  // take each pattern from the array list

				if(s.length == pattern.length)
				{	count = 0;
				for(int j=0;j<s.length;j++)  
					if(pattern[j]== null || s[j].equals(pattern[j])) //compare with the input pattern
					{
						count++;   




					}

				if(count == s.length )  //if length is equal to the string array
				{
					putget.remove(s);  
					

					return s;
				}
				}	



			}   

			try {
				wait();   //if pattern not found then wait .
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}

	public synchronized void put(String[] tuple) {

		putget.add(tuple.clone());		// write the patten to the array
		notifyAll();  //notify the waiting thread
		

	}
}
