package knox.sudoku;
import java.awt.Color;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * This is the MODEL class. This class knows all about the
 * underlying state of the Sudoku game. We can VIEW the data
 * stored in this class in a variety of ways, for example,
 * using a simple toString() method, or using a more complex
 * GUI (Graphical User Interface) such as the SudokuGUI 
 * class that is included.
 * 
 * @author jaimespacco
 *
 */
public class Sudoku {
	int[][] board = new int[9][9];
	
	public int get(int row, int col) {
		// TODO: check for out of bounds
		return board[row][col];
	}
	
	public void set(int row, int col, int val) {
		// TODO: make sure val is legal
		
			board[row][col] = val;
		
		
	}
	public int[] findSector(int row, int col){
		int[] sector = new int[2];
		sector[0] = row/3;
		sector[1] = col/3;
		
		
		return sector;
	}
	
	public boolean searchSector(int[] sector, int x) {
		int count1 =0;
		int count2 = 0;
		
		for(int r = 0+(sector[0]*3);count1<3;r++) {
			count2 = 0;
			for(int c = 0+(sector[1]*3);count2<3;c++) {
				if(board[r][c]== x ) {
					return false;
				}
				count2++;
			}
			count1++;
		}
		return true;
	}
	public boolean onlySeenOnce() {
		int seen;
		
		for(int i=1;i<10;i++) {
			seen=0;
			for(int x= 0;x<9;x++) {
				for(int y = 0; y<9;y++) {
					if(!isLegal(x,y,i)&& !searchSector(findSector(x,y),i)) {
						seen++;
						
					}
				}
			}
			if(seen != 81) {
				return false;
			}
		}
		return true;
		
		
	}
	public boolean isLegal(int row, int col, int val) {
		// TODO: check if it's legal to put val at row, col
		if(val == 0) {
			return true;
		}
		for(int x = 0;x<board.length;x++) {
			if(board[row][x] == val ) {
				return false;
			}
		}
		for(int y = 0; y<board.length;y++) {
			if(board[y][col] == val) {
				return false;
			}
			
			
		}
		
		return searchSector(findSector(row,col),val);
	}
	
	public List<Integer> showlegalMoves(boolean keepVal) {
		//Format of [row] [col]
		List<Integer> onlyOption = new ArrayList<Integer>();
		
		//how many hints will be shown at a time
		
		for(int x=0;x<9;x++) {//Max num of rows
			for(int y = 0;y<9;y++ ) {//Max num of cols
				if(isBlank(x, y)) {
					List<Integer> t = allPossibleAllowedForSquare(x,y);
					if(t.size()==1 && !keepVal) {
						onlyOption.add(x);
						onlyOption.add(y);
					}else if(t.size()==1 && keepVal) {
						onlyOption.add(x);
						onlyOption.add(y);
						onlyOption.add(t.get(0));//getting the one value that exists in t
						
					}
					
				
				}
			}
		}
		return onlyOption;
	}
	public boolean searchAllLegal() {
		for(int i =0; i<9;i++) {
			for(int n = 0;n< 9;n++) {
				//TO DO add a toggle so that this is actually useful
				//Right now only legal moves can be made 
				//This will check if someone is actually able to do it without assistance
				//Currently overkill 
				if(isLegal(i,n,board[i][n])) {
					return false;
				}
			}
		}
		return true;
	}
	public boolean victory() {
		if(searchAllLegal())return true;
		
		return false;
	}
	//Idea maybe add a map that takes in two cords and will show all the things that could go there?
	@SuppressWarnings("null")
	public List<Integer> allPossibleAllowedForSquare(int row, int col) {
		
		List<Integer> s = new ArrayList<Integer>();
		
		for(int i = 1;i<10;i++) {
			if(isLegal(row,col, i)) {
				
				s.add(i);
			}
		}
		
		return  s;
	}
	public Collection<Integer> getLegalValues(int row, int col) {
		// TODO: return only the legal values that can be stored at the given row, col
		return new LinkedList<>();
	}
	
/**

_ _ _ 3 _ 4 _ 8 9
1 _ 3 2 _ _ _ _ _
etc


0 0 0 3 0 4 0 8 9

 */
	public void load(String filename) {
		try {
			Scanner scan = new Scanner(new FileInputStream(filename));
			// read the file
			for (int r=0; r<9; r++) {
				for (int c=0; c<9; c++) {
					int val = scan.nextInt();
					board[r][c] = val;
				}
			}
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * Return which 3x3 grid this row is contained in.
	 * 
	 * @param row
	 * @return
	 */
	public int get3x3row(int row) {
		return row / 3;
	}
	
	/**
	 * Convert this Sudoku board into a String
	 */
	public String toString() {
		String result = "";
		for (int r=0; r<9; r++) {
			for (int c=0; c<9; c++) {
				int val = get(r, c);
				if (val == 0) {
					result += "_ ";
				} else {
					result += val + " ";
				}
			}
			result += "\n";
		}
		return result;
	}
	//Ever just want to just watch games be played for you?
	//This lets you do that
	public static void playForMe() {
		
	}
	public static void main(String[] args) {
		Sudoku sudoku = new Sudoku();
		sudoku.load("easy1.txt");
		System.out.println(sudoku);
		
		Scanner scan = new Scanner(System.in);
		while (!sudoku.gameOver()) {
			System.out.println("enter value r, c, v :");
			int r = scan.nextInt();
			int c = scan.nextInt();
			int v = scan.nextInt();
			if(sudoku.isLegal(r,c,v)) {
				sudoku.set(r, c, v);
			}else {
				System.out.println("Value already in play");
			}
			

			System.out.println(sudoku);
		}
	}

	public boolean gameOver() {
		// TODO check that there are still open spots
		return false;
	}

	public boolean blankExists() {
		for(int i= 0; i<9;i++) {
			for(int x= 0; x<9;x++) {
				if(isBlank(i,x)) {
					return true;
				}
			}
		}
		return false;
	}
	public boolean isBlank(int row, int col) {
		return board[row][col] == 0;
	}

}
