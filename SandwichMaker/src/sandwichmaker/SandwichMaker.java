package sandwichmaker;

/**
 * SYSC 3303 Assignment 1
 * SandwichMaker.java
 * Purpose: Makes predetermined amount of sandwiches using threads.
 * 
 * @author Valerie Figuracion
 * @since January 16, 2020
 */

public class SandwichMaker{

	private static int eatenSandwiches = 20;

	/**
	 * Main method for the SandwichMaker program
	 * 
	 * @param args Not used
	 */
	public static void main(String args[]) {
		System.out.println("Welcome to the sandwich maker! \n");
		
		//Ensures that there's at least x amount of sandwiches made.
		for (int i = 0; i <eatenSandwiches; i++) {
			System.out.println("Making sandwich #" + (i+1)); //Keeps track of how many sandwiches are being made.
			
			try {
				Thread agent = new Agent();
				agent.start();
				Thread.sleep(1750); //To allow the agent class to finish their run method before notifyAll()
				
				//Conditions to choose which Chef class will use their ingredient to make their sandwich.
				if (((Agent) agent).getChosenIngredients().contains("Peanut Butter") && ((Agent) agent).getChosenIngredients().contains("Jam")){
					Thread breadChef = new Thread(new Chef("Bread Chef"));
					breadChef.start();
				}
				if (((Agent) agent).getChosenIngredients().contains("Peanut Butter") && ((Agent) agent).getChosenIngredients().contains("Bread")){
					Thread jamChef = new Thread(new Chef("Jam Chef"));
					jamChef.start();
				}
				if (((Agent) agent).getChosenIngredients().contains("Jam") && ((Agent) agent).getChosenIngredients().contains("Bread")){
					Thread peanutChef = new Thread(new Chef("Peanut Chef"));
					peanutChef.start();
				}
				
				Thread.sleep(1250);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("All done! Program shutting down... :3");
		
		//Exits the program
		System.exit(0);
	}
}
