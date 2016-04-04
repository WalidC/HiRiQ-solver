import java.util.Arrays;
import java.util.ArrayList;
import java.util.LinkedList;

public class pegSolitaireSolver {

	public static void main(String[] args) {

		class Node { //this is the node object that will be used
			public int depth; //useless for now but to be implemented for efficiency
			private boolean[] node; //the config itself in form of a boolean array
			private String identifier = ""; //the string that contains the substitutions (i.e. the history of the branch)
								
			public Node(boolean[] node, String identifier, int depth) { //constructor
				this.node = node;
				this.identifier = identifier;
				this.depth = depth;
			}

			public boolean[] getConfig() { //get method
				return this.node;
			}

			public String getIdentifier() { //get method
				return this.identifier;
			}
		}
		
		class Queue<E> { //Queue implementation using LinkedLists

			private LinkedList<E> list = new LinkedList<E>();
			
			public E dequeue() { //dequeuing a node
				E toRemove = list.poll();
				return toRemove;
			}

			public void enqueue(E input) { //queuing a node
				list.addLast(input);
			}

			public boolean isEmpty(){ //checking if the queue is empty (used for efficiency with dual queues later)
				return list.size()==0;
			}
		}
		class HiRiQ {
			// int is used to reduce storage to a minimum...
			public int config;
			public byte weight;
			private int[][] allTriplets = { { 0, 1, 2 }, { 3, 4, 5 }, { 16, 23, 28 }, { 6, 7, 8 }, { 7, 8, 9 }, { 8, 9, 10 },
					{ 9, 10, 11 }, { 10, 11, 12 }, { 13, 14, 15 }, { 14, 15, 16 }, { 15, 16, 17 }, { 16, 17, 18 },
					{ 17, 18, 19 }, { 20, 21, 22 }, { 21, 22, 23 }, { 22, 23, 24 }, { 23, 24, 25 }, { 24, 25, 26 },
					{ 27, 28, 29 }, { 30, 31, 32 }, { 12, 19, 26 }, { 11, 18, 25 }, { 2, 5, 10 }, { 5, 10, 17 },
					{ 10, 17, 24 }, { 17, 24, 29 }, { 24, 29, 32 }, { 1, 4, 9 }, { 4, 9, 16 }, { 9, 16, 23 },
					 { 23, 28, 31 }, { 0, 3, 8 }, { 3, 8, 15 }, { 8, 15, 22 }, { 15, 22, 27 },
					{ 22, 27, 30 }, { 7, 14, 21 }, { 6, 13, 20 } };

			ArrayList<boolean[]> occured = new ArrayList<boolean[]>();

			// initialize the configuration to one of 4 START setups n=0,1,2,3
			HiRiQ(byte n) {
				if (n==0)
				   {config=65536/2;weight=1;}
				  else
				    if (n==1)
				     {config=1626;weight=6;}
				    else
				      if (n==2)
				       {config=-1140868948; weight=10;}
				      else
				        if (n==3)
				         {config=-411153748; weight=13;}
				        else
				         {config=-2147450879; weight=32;}
			}

			// initialize the configuration to one of 4 START setups
			// n=0,10,20,30

			boolean IsSolved() {
				return ((config == 65536 / 2) && (weight == 1));
			}

			// transforms the array of 33 booleans to an (int) config and a
			// (byte) weight.
			public void store(boolean[] B) {
				int a = 1;
				config = 0;
				weight = (byte) 0;
				if (B[0]) {
					weight++;
				}
				for (int i = 1; i < 32; i++) {
					if (B[i]) {
						config = config + a;
						weight++;
					}
					a = 2 * a;
				}
				if (B[32]) {
					config = -config;
					weight++;
				}
			}

			// transform the int representation to an array of booleans.
			// the weight (byte) is necessary because only 32 bits are memorized
			// and so the 33rd is decided based on the fact that the config has
			// the
			// correct weight or not.
			public boolean[] load(boolean[] B) {
				byte count = 0;
				int fig = config;
				B[32] = fig < 0;
				if (B[32]) {
					fig = -fig;
					count++;
				}
				int a = 2;
				for (int i = 1; i < 32; i++) {
					B[i] = fig % a > 0;
					if (B[i]) {
						fig = fig - a / 2;
						count++;
					}
					a = 2 * a;
				}
				B[0] = count < weight;
				return (B);
			}

			// prints the int representation to an array of booleans.
			// the weight (byte) is necessary because only 32 bits are memorized
			// and so the 33rd is decided based on the fact that the config has
			// the
			// correct weight or not.
			public void printB(boolean Z) {
				if (Z) {
					System.out.print("[ ]");
				} else {
					System.out.print("[@]");
				}
			}

			public void print() {
				byte count = 0;
				int fig = config;
				boolean next, last = fig < 0;
				if (last) {
					fig = -fig;
					count++;
				}
				int a = 2;
				for (int i = 1; i < 32; i++) {
					next = fig % a > 0;
					if (next) {
						fig = fig - a / 2;
						count++;
					}
					a = 2 * a;
				}
				next = count < weight;

				count = 0;
				fig = config;
				if (last) {
					fig = -fig;
					count++;
				}
				a = 2;

				System.out.print("      ");
				printB(next);
				for (int i = 1; i < 32; i++) {
					next = fig % a > 0;
					if (next) {
						fig = fig - a / 2;
						count++;
					}
					a = 2 * a;
					printB(next);
					if (i == 2 || i == 5 || i == 12 || i == 19 || i == 26 || i == 29) {
						System.out.println();
					}
					if (i == 2 || i == 26 || i == 29) {
						System.out.print("      ");
					}
					;
				}
				printB(last);
				System.out.println();

			}
			//THE SOLVER METHOD takes the config as input and return the substitutions (also prints the number of iterations and displays the current board being processed)
			public String solve(boolean[] inputConfig) { 
				int level = 0; //integer used to refer to the depth of nodes
				Queue<Node> myQueue; //main queue which will contains the children that have more black tiles than their parents (thus getting closer to the solution)
				Queue<Node> secondaryQueue; //secondary queue that contains the rest of the children that have less black tiles than their parents
				myQueue = new Queue<Node>();
				secondaryQueue = new Queue<Node>();
				String finalString = ""; //substitutions string that will be returned
				ArrayList<boolean[]> occurences = new ArrayList<boolean[]>(); //arraylist that contains all visited nodes (to avoid visiting dupplicates)
				HiRiQ temp = new HiRiQ((byte) 0); //creating our focus HiRiQ object
				temp.store(inputConfig); //storing our input into the object
				temp.print(); //printing it for reference
				int weight = (int) temp.weight; //getting the weight of the inputConfig (i.e the number of white tiles) to be used to classify Nodes for faster processing
				Node startNode = new Node(inputConfig, "", 0); //initializing our root (starting node)
				myQueue.enqueue(startNode); //queueing it
				while (!temp.IsSolved()){ 
					boolean[] focusConfig = new boolean[33];
					Node parent; //creating parent node
					if(myQueue.isEmpty()){ //checking if the primary queue is empty and thus getting a new parent from the secondary queue
						parent = secondaryQueue.dequeue();
					}else{
						parent = myQueue.dequeue(); //otherwise dequeue a node from the primary queue and generate its children
					}
					HiRiQ parentObject = new HiRiQ ((byte) 0);
					parentObject.store(parent.getConfig()); //creating a parentObject to get its weight (for it's number of white tiles)
					String parentInfo = parent.getIdentifier(); //getting the history of a path from the parent Node to be passed to the Children
					focusConfig = parent.getConfig(); 
					for (int i = 0; i < allTriplets.length; i++) { //going through all possible triplet substitutions 
						boolean[] currentTriplet = new boolean[3];
						currentTriplet[0] = focusConfig[allTriplets[i][0]];
						currentTriplet[1] = focusConfig[allTriplets[i][1]];
						currentTriplet[2] = focusConfig[allTriplets[i][2]];
						if (canSubstitute(currentTriplet)) { //checking if they are eligible for substitution
							boolean[] newChild = new boolean[33];
							newChild = makeSubstitution(focusConfig, i);
							String subInfo = substitutionInfo(focusConfig, i); //getting the info of this current substitution
							if (!didOccur(occurences, newChild) || !parityTest(newChild)) { //we can now validate the child if it passes the parity Test and is not a duplicate
								System.out.println("");
								System.out.println(level++); 
								System.out.println("");
								Node currentNode = new Node(newChild, parentInfo + " " + subInfo, level); //creating the child node
								occurences.add(newChild); //adding it to the visited arrayList
								temp.store(newChild); //storing it to the parent object (it is now becoming a parent)
								temp.print();
								if(temp.weight<parentObject.weight){ //if child has more black tiles than parent, then pass it to the primary queue (which has more priority)
									myQueue.enqueue(currentNode);
								}else{
									secondaryQueue.enqueue(currentNode);
								}
								if(temp.IsSolved()){ //if the child is the solution
									finalString = currentNode.getIdentifier(); //get the history of that path
									break; //and break from the while loop
								}
							}
						}
					}
				}
				System.out.println("SOLVED !!!!"); //YAY
				return finalString; //returning the substitutions
			}

			public boolean didOccur(ArrayList<boolean[]> occurenceList, boolean[] focus) { //simple method that goes over an arrayList and checks if focus is a duplicate
				for (int i = 0; i < occurenceList.size(); i++) {
					if (Arrays.equals(occurenceList.get(i), focus)) {
						return true; //true if it has occured
					}
				}
				return false;
			}

			public boolean parityTest(boolean[] inputConfig) { //simple implementation of the Parity Test
				int blueCounter = 0; // white pixels counters
				int yellowCounter = 0;
				int redCounter = 0;
				//harcoding of the coordinates in the boolean array of the blue, yellow and red tiles
				int[] blueTiles = { 0, 5, 6, 9, 12, 15, 18, 21, 24, 28, 30 };
				int[] yellowTiles = { 1, 3, 7, 10, 13, 16, 19, 22, 25, 29, 31 };
				int[] redTiles = { 2, 4, 8, 11, 14, 17, 20, 23, 26, 27, 32 };

				for (int i = 0; i < blueTiles.length; i++) {
					if (inputConfig[blueTiles[i]]) {
						blueCounter++;
					}
					if (inputConfig[yellowTiles[i]]) {
						yellowCounter++;
					}
					if (inputConfig[redTiles[i]]) {
						redCounter++;
					}
				}

				if ((blueCounter % 2 == redCounter % 2) && (yellowCounter % 2 != redCounter % 2)) {
					return true; //true if it passes the parity test
				} else {
					return false;
				}
			}

			public boolean canSubstitute(boolean[] triplet) { //checking if the triplet is eligible for substitution

				if (triplet[0] == true && triplet[1] == true && triplet[2] == false) {
					return true;
				} else if (triplet[0] == true && triplet[1] == false && triplet[2] == false) {
					return true;
				} else if (triplet[0] == false && triplet[1] == false && triplet[2] == true) {
					return true;
				} else if (triplet[0] == false && triplet[1] == true && triplet[2] == true) {
					return true;
				}
				return false;
			}

			public boolean[] makeSubstitution(boolean[] currentFocus, int index) { //the actual substitution method
				boolean[] temp = Arrays.copyOf(currentFocus, currentFocus.length); //preparing the returning array
				int[] tripletIndex = allTriplets[index]; //getting the triplet that will be subject to substitution
				if (currentFocus[tripletIndex[0]] == true && currentFocus[tripletIndex[1]] == true
						&& currentFocus[tripletIndex[2]] == false) {
					temp[tripletIndex[0]] = false;
					temp[tripletIndex[1]] = false;
					temp[tripletIndex[2]] = true;
				} else if (currentFocus[tripletIndex[0]] == true && currentFocus[tripletIndex[1]] == false
						&& currentFocus[tripletIndex[2]] == false) {
					temp[tripletIndex[0]] = false;
					temp[tripletIndex[1]] = true;
					temp[tripletIndex[2]] = true;
				} else if (currentFocus[tripletIndex[0]] == false && currentFocus[tripletIndex[1]] == false
						&& currentFocus[tripletIndex[2]] == true) {
					temp[tripletIndex[0]] = true;
					temp[tripletIndex[1]] = true;
					temp[tripletIndex[2]] = false;
				} else if (currentFocus[tripletIndex[0]] == false && currentFocus[tripletIndex[1]] == true
						&& currentFocus[tripletIndex[2]] == true) {
					temp[tripletIndex[0]] = true;
					temp[tripletIndex[1]] = false;
					temp[tripletIndex[2]] = false;
				}
				return temp; //returning the config now with a new substitution in it
			}

			public String substitutionInfo(boolean[] currentFocus, int index) { //method that returns the detail of a single substitution
				int[] tripletIndex = allTriplets[index]; //grabs the triplet that the substitution index is refering to
				String info = ""; 
				if (currentFocus[tripletIndex[0]] == true && currentFocus[tripletIndex[1]] == true
						&& currentFocus[tripletIndex[2]] == false) {
					info += tripletIndex[0] + "W" + tripletIndex[2]; //WWB form
				} else if (currentFocus[tripletIndex[0]] == true && currentFocus[tripletIndex[1]] == false
						&& currentFocus[tripletIndex[2]] == false) {
					info += tripletIndex[2] + "B" + tripletIndex[0]; //WBB form
				} else if (currentFocus[tripletIndex[0]] == false && currentFocus[tripletIndex[1]] == false
						&& currentFocus[tripletIndex[2]] == true) {
					info += tripletIndex[0] + "B" + tripletIndex[2]; //BBW form
				} else if (currentFocus[tripletIndex[0]] == false && currentFocus[tripletIndex[1]] == true
						&& currentFocus[tripletIndex[2]] == true) {
					info += tripletIndex[2] + "W" + tripletIndex[0]; //BWW form
				}
				return info;
			}

		}

		boolean[] B = new boolean[33];
		HiRiQ Y = new HiRiQ((byte) 2); //n=3 and n=2 runs in about 15 seconds and about 33000 iterations, also n=1 is solved instantly (112 iterations) 
		System.out.println(Y.solve(Y.load(B)));
	}

}
