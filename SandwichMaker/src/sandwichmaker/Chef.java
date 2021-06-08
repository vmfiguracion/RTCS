package sandwichmaker;

/**
 * SYSC 3303 Assignment 1
 * Chef.java
 * Purpose: Thread class for chefs that have only one ingredient.
 * 
 * @author Valerie Figuracion
 * @since January 16, 2020
 */

public class Chef extends Thread{
		
	private String name;
	
	/**
	 * @param name: The name of the chef to make the sandwich
	 */
	public Chef(String name) {
		this.name = name;
	}

	@Override
	public synchronized void run() {
		try {
			System.out.println(name + " is making the sandwich. \n");
			
			notifyAll();
			wait();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
}
