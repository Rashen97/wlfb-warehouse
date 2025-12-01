import java.net.*;
import java.io.*;

public class SharedActionState{

	private SharedActionState mySharedObj;
	private String myThreadName;
	private double mySharedVariable;
	private boolean accessing=false; // true a thread has a lock, false otherwise
	private int threadsWaiting=0; // number of waiting writers

	// Warehouse stock
	private int apples;
	private int oranges;

	int number;

	// Constructor	

	SharedActionState(int startingApples, int startingOranges) {
		apples = startingApples;
		oranges = startingOranges;
	}

	//Attempt to aquire a lock

	public synchronized void acquireLock() throws InterruptedException{
		Thread me = Thread.currentThread(); // get a ref to the current thread
		System.out.println(me.getName()+" is attempting to acquire a lock!");	
		++threadsWaiting;
		while (accessing) {  // while someone else is accessing or threadsWaiting > 0
			System.out.println(me.getName()+" waiting to get a lock as someone else is accessing...");
			//wait for the lock to be released - see releaseLock() below
			wait();
		}
		// nobody has got a lock so get one
		--threadsWaiting;
		accessing = true;
		System.out.println(me.getName()+" got a lock!"); 
	}

	// Releases a lock to when a thread is finished

	public synchronized void releaseLock() {
		//release the lock and tell everyone
		accessing = false;
		notifyAll();
		Thread me = Thread.currentThread(); // get a ref to the current thread
		System.out.println(me.getName()+" released a lock!");
	}


	/* The processInput method */

	public synchronized String processInput(String myThreadName, String theInput) {
		System.out.println(myThreadName + " received "+ theInput);
		String theOutput = null;


		// Split input command
		String[] part = theInput.trim().split("\\s+");
		if(part.length == 0) {
			theOutput = "Empty command!";
			System.out.println(theOutput);
			return theOutput;
		}

		String action = part[0].toUpperCase();
		
		String item;
		if (part.length > 1) {
		    item = part[1].toUpperCase();
		} else {
		    item = "";
		}

		try {
			if (action.equals("CHECK") && item.equals("STOCK")) {
				theOutput = "Stock Apples =  " + apples + "  Oranges =   " + oranges;
			}
			else if (action.equals("BUY") && item.equals("APPLES")) {
				if(part.length < 3) {
					theOutput = "Missing Quantity for BUY_APPLES";
				}else {
					int number = Integer.parseInt(part[2]);
					if (number < 0) {
						theOutput = "Quantity Must be Positive!";
					}else if (number > apples) {
						theOutput = "Error ! Not enough Apples in stock. Current = " + apples;
					}else {
						apples = apples - number;
						theOutput = "Bought " + number + " of Apples. Available in Stock =  " + apples + " Oranges = " + oranges;
					}
				}
			}
			else if (action.equals("BUY") && item.equals("ORANGES")) {
				if(part.length < 3) {
					theOutput = "Missing Quantity for BUY_ORANGES";
				}else {
					number = Integer.parseInt(part[2]);
					if (number < 0) {
						theOutput = "Quantity Must be Positive!";
					}else if (number > oranges) {
						theOutput = "Error ! Not enough Oranges in stock. Current = " + oranges;
					}else {
						oranges = oranges - number;
						theOutput = "Bought" + number + " of Oranges. Available in Stock = " + oranges + " Apples = " + apples;
					}
				}
			}
			else if (action.equals("ADD") && item.equals("APPLES") ) {
				if (part.length < 3) {
					theOutput = "Missing Quantity for ADD Apples!";
				}else {
					number = Integer.parseInt(part[2]);
					if (number < 0) {
						theOutput = "Quantity must be Positive!";
					}else {
						apples = apples + number;
						theOutput = "Added ! " + number + " of Apples. Available Stock Apples = " + apples + " Oranges = " + oranges;
					}

				}
			}
			else if (action.equals("ADD") && item.equals("ORANGES")) {
				if (part.length < 3) {
					theOutput = "Missing Quantity of Oranges!";
				}else {
					number = Integer.parseInt(part[2]);
					if (number < 0) {
						theOutput = "Quantity must be Positive!";
					}else {
						oranges = oranges + number;
						theOutput = "Added ! " + number + " of Oranges. Available Stock Oranges = " + oranges + " Apples = " + apples;
					}

				}
			}
			
			else {
				theOutput = "Error! Unkown command: " + theInput;
			}
		} catch(NumberFormatException e) {
			theOutput = "Error! Invalid Number in command: " + theInput;
		}
		
		System.out.println("State after " + myThreadName + " command: apples=" + apples + " oranges=" + oranges);
		//Return the output message to the ActionServer
		System.out.println(theOutput);
		System.out.println();
		System.out.println("-----------------------------------------------------");
		System.out.println();
		return theOutput;
	}	
}

