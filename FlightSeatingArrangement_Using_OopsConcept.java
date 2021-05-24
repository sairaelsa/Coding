import java.util.Scanner;

interface printInterface { //Abstraction - Interface
	void printSeats(int[][] plane, int[][] limit, int passengers);//Polymorphism
	void printSeats();//Polymorphism
}

abstract class ParamSetClass { //Abstraction - Abstract Class
	abstract void setParams(int[][] limit); //Abstract method
	public String msg() {
		return "Filling seats...";
	}
}

public class FlightSeatingArrangement implements printInterface { //Class that implements and interface
	
	public static FillSeatsLogic fillSeatsLogic = new FillSeatsLogic(); //Object creation of class
	
    public static void main(String[] args) {

        Scanner sc = new Scanner(System.in);
        System.out.print("Enter number of segments: "); //Number of divisions
        int segments = sc.nextInt();
        sc.nextLine();

        int[][] limit = new int[segments][2]; //2D array

        for(int seg = 0; seg < segments; seg++) {

            System.out.printf("Enter segment %d row and column (r,c): ", seg+1);
            String[] rowAndCol = sc.nextLine().split(",");

            limit[seg][0] = Integer.parseInt(rowAndCol[1]);
            limit[seg][1] = Integer.parseInt(rowAndCol[0]);
            // In the given problem, rows and columns are interchanged.
        }

        System.out.print("Enter number of Passengers: ");
        int passengers = sc.nextInt();
        sc.nextLine();

        sc.close();

        fillSeatsLogic.setLastRow(0); //Encapsulation setters
        fillSeatsLogic.setNumOfSeats(0);
        fillSeatsLogic.setTotalColumn(0);
        int[][] plane = fillSeatsLogic.fillSeats(segments, limit, passengers);
        
        FlightSeatingArrangement flightSeatingArrangement = new FlightSeatingArrangement(); //Object creation of class
        flightSeatingArrangement.printSeats();
        flightSeatingArrangement.printSeats(plane, limit, passengers);
    }
	
    public void printSeats(int[][] plane, int[][] limit, int passengers) { //Interface method body
        
        for(int row = -1; row < fillSeatsLogic.getLastRow(); row++) { //Encapsulation getters

            int segment = 0;
            int segStart = 0;
            
            for(int col = 0; col < fillSeatsLogic.getTotalColumn(); col++) {

                if(col == 0 || col == segStart + limit[segment][1]) {

                    if(row == -1) {
                        System.out.print("  ");
                    } else {
                        System.out.print("| ");
                    }

                    if(col > 0) {
                        segStart += limit[segment][1];
                        segment++;
                    }
                }

                if(row == -1) { // seat title row

                    int segEnd = segStart + limit[segment][1] - 1;

                    if(col == 0 || col == fillSeatsLogic.getTotalColumn()-1) {
                        System.out.print(" W ");
                    }else if(col == segStart || col == segEnd) {
                        System.out.print(" A ");
                    }else {
                        System.out.print(" M ");
                    }
                    continue;
                }

                if(row < limit[segment][0]) {

                    if(plane[row][col] > 0) {
                        System.out.print(plane[row][col] < 10 ? " "+plane[row][col]+" " :  plane[row][col]+" ");
                    }else {
                        System.out.print("__ ");  // empty seat.
                    }
                }else {
                    System.out.print("   ");  // This is not a seat.
                }
            }
            if(row == -1) {
                System.out.println();
            }else {
                System.out.println("|");
            }
        }
        if(passengers > fillSeatsLogic.getNumOfSeats()) {
            System.out.printf("Sorry! There are no seats available for %d passengers.", passengers - fillSeatsLogic.getNumOfSeats());
        }
    }
    
    public void printSeats() { //Interface method body

        System.out.println("\nW - Window seats");
        System.out.println("M - Middle seats");
        System.out.println("A - Aisle seats");
        System.out.println("_ - Empty seats");
        System.out.println("\nThe Plane looks like:\n");
    }
}

class FillSeatsLogic extends ParamSetClass { //Class + Inheritance

	//Encapsulation - private variables and public methods
	private int lastRow;
    private int totalColumn;
    private int numOfSeats;
    
	public int getLastRow() {
		return lastRow;
	}

	public void setLastRow(int lastRow) {
		this.lastRow = lastRow;
	}

	public int getTotalColumn() {
		return totalColumn;
	}

	public void setTotalColumn(int totalColumn) {
		this.totalColumn = totalColumn;
	}

	public int getNumOfSeats() {
		return numOfSeats;
	}

	public void setNumOfSeats(int numOfSeats) {
		this.numOfSeats = numOfSeats;
	}
	
	void setParams(int[][] limit) { //Abstract method body
	    for(int seg = 0; seg < limit.length; seg++) {
	        lastRow = Math.max(lastRow, limit[seg][0]);
	        totalColumn += limit[seg][1];
	        numOfSeats += limit[seg][0] * limit[seg][1];
	    }
	}

    public int[][] fillSeats(int segmnts, int[][] limit, int passengers) {

        setParams(limit);
        
        System.out.println("\n" + super.msg()); //Inheritance - reuse of parent method
        
        final int aisle = 1;
	    final int window = 2;
	    final int middle = 3;

        int[][] plane = new int[lastRow][totalColumn];
        int seatType = 1;   // initially aisle
        int passenger = 1;

        while (passenger <= passengers && passenger <= numOfSeats) {

            for(int row = 0; row < lastRow && passenger <= passengers; row++) {

                int segStart = 0;

                for(int seg = 0; seg < segmnts && passenger <= passengers; seg++) {

                    if(seg > 0) {
                        segStart += limit[seg-1][1];  // index where the segment starts
                    }
                    if(row < limit[seg][0]) {
                        int segEnd = segStart + limit[seg][1] - 1;  // index where the segment ends

                        switch (seatType) {

                            case aisle:

                                	if(seg == 0) {  // first segment has aisle on last column
                                		plane[row][limit[seg][1] - 1] = passenger++;
                                	}else if(seg == segmnts-1) {   // last segment has aisle on first column
                                		plane[row][segStart] = passenger++;
                                	}else {    // other segments have aisles on first and last columns
                                		plane[row][segStart] = passenger++;
                                		if(passenger <= passengers) {
                                			plane[row][segEnd] = passenger++;
                                		}
                                	}
                                	break;                                			

                            case window:

			                        if(seg == 0) {  // first segment has window on first column
			                            plane[row][0] = passenger++;
			                        }else if(seg == segmnts-1) {   // last segment has window on last column
			                            plane[row][segEnd] = passenger++;
			                        }
			                        break;

                            case middle:   // middle seats start from segStart+1 and ends at segEnd-1

			                        for(int col = segStart + 1; col < segEnd && passenger <= passengers; col++) {
			                            plane[row][col] = passenger++;
			                        }
                        }
                    }
                }
            }
            seatType++;
        }
        return plane;
    }
}