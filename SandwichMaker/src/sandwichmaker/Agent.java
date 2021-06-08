package sandwichmaker;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * SYSC 3303 Assignment 1
 * Agent.java
 * Purpose: Thread class for chef that has multiple ingredients.
 * 
 * @author Valerie Figuracion
 * @since January 16, 2020
 */

public class Agent extends Thread{
	private List<String> chosenIngredients;

	//The initial list of ingredients
	List<String> ingredients = Collections.synchronizedList(new LinkedList<String>());{
		synchronized(ingredients) {
			ingredients.add("Bread");
			ingredients.add("Peanut Butter");
			ingredients.add("Jam");
		}
	}  

	@Override
	public synchronized void run() {
		try {
			System.out.println("Agent Chef is choosing ingredients...");
			
			//Ingredients are chosen randomly using getIngredients().
			chosenIngredients = getIngredients(); 
			Thread.sleep(1000);
			
			System.out.println("Ingredients are " + chosenIngredients);
			Thread.sleep(100);
			
			notifyAll();
			wait();
		} catch (InterruptedException e) {}
	}
	
	/**
	 * @param list - A list of strings with the desired ingredients
	 * @return A two element array list of ingredients at random
	 */
	public List<String> randomIngredients(List<String> list){
		
		//A copy of the list is made before shuffling and choosing a 
		//sublist of ingredients from the initial list.
		List<String> copyList = new LinkedList<String>(list);
		Collections.shuffle(copyList);
		return copyList.subList(0, 2);
	}
	
	/**
	 * @return The sublist of ingredients chosen by randomIngredients.
	 */
	public List<String> getIngredients() {
		List<String>randIng = randomIngredients(ingredients);
		return randIng;
	}
	
	/**
	 * @return Returns the already randomized list of ingredients.
	 */
	public List<String> getChosenIngredients(){
		return chosenIngredients;
	}
}