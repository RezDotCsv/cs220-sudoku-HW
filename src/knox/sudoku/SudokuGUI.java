package knox.sudoku;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.Scanner;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.WindowConstants;


/**
 * 
 * This is the GUI (Graphical User Interface) for Sudoku.
 * 
 * It extends JFrame, which means that it is a subclass of JFrame.
 * The JFrame is the main class, and owns the JMenuBar, which is 
 * the menu bar at the top of the screen with the File and Help 
 * and other menus.
 * 
 * 
 * One of the most important instance variables is a JCanvas, which is 
 * kind of like the canvas that we will paint all of the grid squared onto.
 * 
 * @author jaimespacco
 *
 */
public class SudokuGUI extends JFrame {
	
	private Sudoku sudoku;
	public boolean expertEnabled;
	private static final long serialVersionUID = 1L;
	
	// Sudoku boards have 9 rows and 9 columns
    private int numRows = 9;
    private int numCols = 9;
    
    // the current row and column we are potentially putting values into
    private int currentRow = -1;
    private int currentCol = -1;

    
    // figuring out how big to make each button
    // honestly not sure how much detail is needed here with margins
	protected final int MARGIN_SIZE = 5;
    protected final int DOUBLE_MARGIN_SIZE = MARGIN_SIZE*2;
    protected static int squareSize = 60;
    private int width = DOUBLE_MARGIN_SIZE + squareSize * numCols;    		
    private int height = DOUBLE_MARGIN_SIZE + squareSize * numRows;  
    
    // for lots of fun, too much fun really, try "Wingdings"
    private static Font FONT = new Font("Verdana", Font.BOLD, squareSize/2 );
    private static Color FONT_COLOR = Color.BLACK;
    private static Color BACKGROUND_COLOR = Color.GRAY;
    
    // the canvas is a panel that gets drawn on
    private JPanel panel;

    // this is the menu bar at the top that owns all of the buttons
    private JMenuBar menuBar;
    
    // 2D array of buttons; each sudoku square is a button
    private JButton[][] buttons = new JButton[numRows][numCols];
    
    private class MyKeyListener extends KeyAdapter {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	
    	MyKeyListener(int row, int col, Sudoku sudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    	}
    	List<Integer> possibleNums;
    	public void keyTyped(KeyEvent e) {
			char key = e.getKeyChar();
			//System.out.println(key);
			 possibleNums = sudoku.allPossibleAllowedForSquare(currentRow,currentCol);
			
			if(key == 'h') {	
				
				JOptionPane.showMessageDialog(null, "The following values are possible: " + possibleNums.toString() );
			}
			
			if (Character.isDigit(key)) {
				// use ascii values to convert chars to ints
				int digit = key - '0';
				System.out.println(key);
				if(!expertEnabled) {
					if (currentRow == row && currentCol == col) {
						//This is there the set is happening add check 
						if(sudoku.isLegal(row, col, digit)) {
							
							sudoku.set(row, col, digit);
						}else {
							JOptionPane.showMessageDialog(null, digit + " is not a legal move.");
						}
						
					}
				}else {
					if(sudoku.isBlank(row, col)) {
						sudoku.set(row, col, digit);
					}else {
						JOptionPane.showMessageDialog(null, "Can not place on an already filled square.");
					}
					
					
				}
				
				
				update();
			}
		}
    }
    
    private class ButtonListener implements ActionListener {
    	public final int row;
    	public final int col;
    	public final Sudoku sudoku;
    	ButtonListener(int row, int col, Sudoku sudoku){
    		this.sudoku = sudoku;
    		this.row = row;
    		this.col = col;
    	}
		@Override
		public void actionPerformed(ActionEvent e) {
			//System.out.printf("row %d, col %d, %s\n", row, col, e);
			JButton button = (JButton)e.getSource();
			
			if (row == currentRow && col == currentCol) {
				currentRow = -1;
				currentCol = -1;
			} else if (sudoku.isBlank(row, col)) {
				// we can try to enter a value in a 
				currentRow = row;
				currentCol = col;
				
				// TODO: figure out some way that users can enter values
				// A simple way to do this is to take keyboard input
				// or you can cycle through possible legal values with each click
				// or pop up a selector with only the legal valuess
				
			} else {
				// TODO: error dialog letting the user know that they cannot enter values
				// where a value has already been placed
				JOptionPane.showMessageDialog(null, "Can't enter a value here");
			}
			
			update();
		}
    }
    
    /**
     * Put text into the given JButton
     * 
     * @param row
     * @param col
     * @param text
     */
    private void setText(int row, int col, String text) {
    	buttons[row][col].setText(text);
    }
    
    /**
     * This is a private helper method that updates the GUI/view
     * to match any changes to the model
     */
    
    private void update() {
    	
    	for (int row=0; row<numRows; row++) {
    		for (int col=0; col<numCols; col++) {
    			
    			if (row == currentRow && col == currentCol && sudoku.isBlank(row, col)) {
    				// draw this grid square special!
    				// this is the grid square we are trying to enter value into
    				buttons[row][col].setForeground(Color.RED);
    				// I can't figure out how to change the background color of a grid square, ugh
    				// Maybe I should have used JLabel instead of JButton?
    				buttons[row][col].setBackground(Color.CYAN);
    				setText(row, col, "_");
    			} else {
    				buttons[row][col].setForeground(FONT_COLOR);
    				buttons[row][col].setBackground(BACKGROUND_COLOR);
	    			int val = sudoku.get(row, col);
	    			if (val == 0) {
	    				setText(row, col, "");
	    			} else {
	    				setText(row, col, val+"");
	    			}
    			}
    		}
    	}
    	repaint();
    	if(!sudoku.blankExists()) {
    		if(sudoku.onlySeenOnce()) {
        		JOptionPane.showMessageDialog(null, "Congratulations!");
        	}else {
        		JOptionPane.showMessageDialog(null, "Oh you lost...well...better luck next time? It's not really a luck game.");
        	}
    	}
    	
    }
    
	
    private void createMenuBar() {
    	menuBar = new JMenuBar();
        
    	//
    	// File menu
    	//
    	JMenu file = new JMenu("File");
        menuBar.add(file);
        
        // anonymous inner class
        // basically, immediately create a class that implements actionlistener
        // and then give it the given actionPerformed method.
        addToMenu(file, "New Game", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sudoku.load("easy1.txt");
                update();
            }
        });
        
        JMenu make = new JMenu("Maker Mode");
        menuBar.add(make);
        
        // wanted to add an method to also make a random game, but I don't know enough about the game theory
        addToMenu(make, "Make a game", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	JOptionPane.showMessageDialog(null, "When finished making your game, hit save so that you can load it.");
            	JOptionPane.showMessageDialog(null, "Pressing the new game option will set it to the default game", "", JOptionPane.WARNING_MESSAGE);
                for(int i= 0;i<9;i++) {
                	for(int x = 0;x<9;x++) {
                		sudoku.set(i, x, 0);
                	}
                }
                update();
            }
        });
        addToMenu(file, "Set As Default", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	
            	
            	
            	try {
        			
        			
        			FileWriter fw = new FileWriter("easy1.txt"); //this erases previous content; tbh this stuff is still kinda magic
        			fw = new FileWriter("easy1.txt", true); //this reopens file for appending 
        			BufferedWriter bw = new BufferedWriter(fw);
        			PrintWriter pw = new PrintWriter(bw);
        			
        			
        			for(int x = 0; x<9;x++) {
        				
                		
                		for(int y =0 ; y<9;y++) {
                			pw.print(sudoku.board[x][y] +"\t") ;
                		}
                		
                		pw.print("\n") ;
                		
                	}
        			pw.close();
        		} catch (IOException e1) {
        			throw new RuntimeException(e1);
        		}
            	// TODO: save the current game to a file!
            	// HINT: Check the Util.java class for helpful methods
            	// HINT: check out JFileChooser
            	// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
            	
            
                update();
            }
        });
        
        
        addToMenu(file, "Save", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	
            	
            	
            	
            	try {
        			
        			
        			FileWriter fw = new FileWriter("easy2.txt"); //this erases previous content; tbh this stuff is still kinda magic
        			fw = new FileWriter("easy2.txt", true); //this reopens file for appending 
        			BufferedWriter bw = new BufferedWriter(fw);
        			PrintWriter pw = new PrintWriter(bw);
        			
        			
        			for(int x = 0; x<9;x++) {
        				
                		
                		for(int y =0 ; y<9;y++) {
                			pw.print(sudoku.board[x][y] +"\t") ;
                		}
                		
                		pw.print("\n") ;
                		
                	}
        			pw.close();
        			JOptionPane.showMessageDialog(null,
                		    "Current game has been saved");
        		} catch (IOException e1) {
        			throw new RuntimeException(e1);
        		}
            	// TODO: save the current game to a file!
            	// HINT: Check the Util.java class for helpful methods
            	// HINT: check out JFileChooser
            	// https://docs.oracle.com/javase/tutorial/uiswing/components/filechooser.html
            	
            
                update();
            }
        });
        
        addToMenu(file, "Load", new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
            	 sudoku.load("easy2.txt");
            	
                update();
            }
        });
        
        //
        // Help menu
        //
        JMenu tips = new JMenu("Tips");
        menuBar.add(tips);
        
        addToMenu(tips, "Minor Cheat", new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			JOptionPane.showMessageDialog(null, "You can press the \"H\" key after clicking a square "
    					+ "to get all numbers that could go in that square");
    			
    		}
    	});
        
        
       
        JMenu help = new JMenu("Help");
        menuBar.add(help);
        
        addToMenu(help, "Hint", new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JOptionPane.showMessageDialog(null, "Give the user a hint! Highlight the most constrained square\n" + 
						"which is the square where the fewest posssible values can go");
				String hintsWanted;
				int k;
				 hintsWanted = JOptionPane.showInputDialog(null,"How many maximum hints do you want?");
				 k = Integer.parseInt(hintsWanted);
				List<Integer> show= sudoku.showlegalMoves(false);
				
				for(int i = 0; i<2*k;i+=2) {
						if(i>show.size()-1) {
							break;
						}else {
							buttons[show.get(i)][show.get(i+1)].setBackground(Color.GREEN);
						}
						
					
				}
			}
		});
        addToMenu(help, "I'm Lazy So Please Play For Me", new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			
    			while(sudoku.blankExists()) {
    				List<Integer> show= sudoku.showlegalMoves(true);
    				sudoku.set(show.get(0), show.get(1), show.get(2));
    				
    			}
    			JOptionPane.showMessageDialog(null, "Awesome Job YOU WON while DOING NOTHING!!!");
    			update();
    		}
    	});
        addToMenu(help, "Enable/Disable Square Input Allowed", new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			if(expertEnabled) {
    				JOptionPane.showMessageDialog(null, "Yeah, not so easy huh. Now playing with assist");
    			}else {
    				JOptionPane.showMessageDialog(null, "Feeling confident are we? Now playing with no assist");
    			}
    			expertEnabled = !expertEnabled;
    			
    			
    			update();
    		}
    	});
        
        
        addToMenu(help, "I'm Lazy So Please Play For Me, but this time I'm bad", new ActionListener() {
    		@Override
    		public void actionPerformed(ActionEvent e) {
    			
    			while(sudoku.blankExists()) {
    				for(int i = 0;i<9;i++) {
    					for(int n = 0;n<9;n++) {
        					if(sudoku.isBlank(i, n)) {
        						sudoku.set(i, n, (int) (Math.random()*(9-1+1)+1));
        					}
        				}	
    				}
    				
    				
    			}
    			JOptionPane.showMessageDialog(null, "No clue how this went, probably poorly, give me a sec");
    			update();
    		}
    	});
        
        this.setJMenuBar(menuBar);
    }
    
    
    
    
    
    
    
    
    /**
     * Private helper method to put 
     * 
     * @param menu
     * @param title
     * @param listener
     */
    private void addToMenu(JMenu menu, String title, ActionListener listener) {
    	JMenuItem menuItem = new JMenuItem(title);
    	menu.add(menuItem);
    	menuItem.addActionListener(listener);
    }
    
    private void createMouseHandler() {
    	MouseAdapter a = new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.printf("%s\n", e.getButton());
			}
    		
    	};
        this.addMouseMotionListener(a);
        this.addMouseListener(a);
    }
    
    
    private void createKeyboardHandlers() {
    	for (int r=0; r<buttons.length; r++) {
    		for (int c=0; c<buttons[r].length; c++) {
    			buttons[r][c].addKeyListener(new MyKeyListener(r, c, sudoku));
    		}
    	}
    }
    
    public SudokuGUI() {
        sudoku = new Sudoku();
        // load a puzzle from a text file
        // right now we only have 1 puzzle, but we could always add more!
        sudoku.load("easy1.txt");
       int result = JOptionPane.showConfirmDialog(null,"Do you want to play on expert? \nYou will not get notifications that an object can not appear in the square. Can be changed later under the help option","Skill", JOptionPane.YES_NO_OPTION,JOptionPane.PLAIN_MESSAGE);
    	   //0 is a yes 1 is no, perfect for a boolean
       if(result == 0) {
    	   expertEnabled = true;
       }
       
        setTitle("Sudoku!");

        this.setSize(width, height);
        
        // the JPanel where everything gets painted
        panel = new JPanel();
        // set up a 9x9 grid layout, since sudoku boards are 9x9
        panel.setLayout(new GridLayout(9, 9));
        // set the preferred size
        // If we don't do this, often the window will be minimized
        // This is a weird quirk of Java GUIs
        panel.setPreferredSize(new Dimension(width, height));
        
        // This sets up 81 JButtons (9 rows * 9 columns)
        for (int r=0; r<numRows; r++) {
        	for (int c=0; c<numCols; c++) {
        		JButton b = new JButton();
        		b.setPreferredSize(new Dimension(squareSize, squareSize));
        		
        		b.setFont(FONT);
        		b.setForeground(FONT_COLOR);
        		b.setBackground(BACKGROUND_COLOR);
        		b.setOpaque(true);
        		buttons[r][c] = b;
        		// add the button to the canvas
        		// the layout manager (the 9x9 GridLayout from a few lines earlier)
        		// will make sure we get a 9x9 grid of these buttons
        		panel.add(b);

        		// thicker borders in some places
        		// sudoku boards use 3x3 sub-grids
        		int top = 1;
        		int left = 1;
        		int right = 1;
        		int bottom = 1;
        		if (r % 3 == 2) {
        			bottom = 5;
        		}
        		if (c % 3 == 2) {
        			right = 5;
        		}
        		if (r == 0) {
        			top = 5;
        		}
        		if (c == 9) {
        			bottom = 5;
        		}
        		b.setBorder(BorderFactory.createMatteBorder(top, left, bottom, right, Color.black));
        		
        		//
        		// button handlers!
        		//
        		// check the ButtonListener class to see what this does
        		//
        		b.addActionListener(new ButtonListener(r, c, sudoku));
        	}
        }
        
        this.getContentPane().add(panel, BorderLayout.CENTER);
        this.setPreferredSize(new Dimension(width, height));
        this.setResizable(false);
        this.pack();
        this.setLocation(100,100);
        this.setFocusable(true);
        
        createMenuBar();
        createKeyboardHandlers();
        createMouseHandler();
        
        // close the GUI application when people click the X to close the window
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        
        update();
        repaint();
    }
    
    public static void main(String[] args) {
        SudokuGUI g = new SudokuGUI();
        
        g.setVisible(true);
    }

}
