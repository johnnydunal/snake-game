import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {
	
	/*
	 * Welcome to Snake Game!
	 * 
	 * This whole game is built in a single file! (because why not)
	 */
	
	/*
	 * TODO:
	 * 1) Display game over message in gameOver()
	 * 2) Ensure snake does not collide with himself
	 */
	
	static JFrame frame = new JFrame("Snake Game");
	
	static boolean running = true;
	
	static final int MAP_WIDTH = 23;
    static final int MAP_HEIGHT = 19;
    
    static final int TILE_SIZE = 40;
    
    static int SNAKE_SPEED = 8; // amount of tiles that the snake moves in a second
    static String SnakeDirection = "N";
    static String SnakeCurrentDirection = "N";
    
    static int[] snakeHeadCords = {(int)Math.floor(MAP_HEIGHT / 2), 6}; // keep track of the snake's head's cords (for collision detection)
    // NOTE: snakeHeadCords[0] gives row number, snakeHeadCords[1] gives column number
    
    static ArrayList<int[]> snake = new ArrayList<int[]>(); // to keep track of coordinates of each part of the snake's body
    
    static final Color DARK_GREEN = new Color(0, 150, 0);
    static final Color LIGHT_GREEN = new Color(0, 130, 0);
    static final Color SNAKE_BLUE = new Color(50, 85, 180);
    static final Color APPLE_RED = new Color(125, 10, 0);
    
    static final String[][] stringMap = initStringMap();
    static final JPanel[][] panelMap = initJPanelMap();

	public static void main(String[] args) throws InterruptedException{

        //frame.setSize(600, 450);
        frame.setSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        frame.getContentPane().setBackground(Color.BLACK);

        frame.setLayout(new GridLayout(MAP_HEIGHT, MAP_WIDTH));
        
        redraw(); // to make sure that snake and apple are on the map
        
        KeyInput keyInput = new KeyInput();
        frame.addKeyListener(keyInput);
        
        frame.setVisible(true);
        
        // GAME LOOP:
        while(running) {
        	
        	update();
        	
        	redraw();
        	
        	//wait
        	Thread.sleep(1000 / SNAKE_SPEED);
        	
        }

    }
    
    static private void update() {
    	
    	// 1) Check for wall collision
    	if((snakeHeadCords[0] == 0 && SnakeDirection.equals("U")) || (snakeHeadCords[0] == MAP_HEIGHT - 1 && SnakeDirection.equals("D")) || (snakeHeadCords[1] == 0 && SnakeDirection.equals("L")) || (snakeHeadCords[1] == MAP_WIDTH - 1 && SnakeDirection.equals("R"))) {
    		gameOver();
    		return;
    	}
    	
    	// 2) Move/Update Snake HEAD
    	switch(SnakeDirection) {
			case "U":
				SnakeCurrentDirection = "U";
				snakeHeadCords[0]--;
				break;
			case "D":
				SnakeCurrentDirection = "D";
				snakeHeadCords[0]++;
				break;
			case "L":
				SnakeCurrentDirection = "L";
				snakeHeadCords[1]--;
				break;
			case "R":
				SnakeCurrentDirection = "R";
				snakeHeadCords[1]++;
				break;
			default:
				return; // user has not moved yet
    	}
    	
    	// 3) Check for snake collision
    	if(snake.contains(snakeHeadCords)) {
    		gameOver();
    		return;
    	}
    	
    	// 4) Check for apple eaten & Fully update snake body
    	//		- If yes, increase snake length
    	//		- If no, snake keeps moving
    	if(stringMap[snakeHeadCords[0]][snakeHeadCords[1]].equals("A")) {
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
    		int[] a = {snakeHeadCords[0], snakeHeadCords[1]};
    		snake.add(a);
    		
    		appleEaten();
    	}else { // apple not eaten
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
    		int[] a = {snakeHeadCords[0], snakeHeadCords[1]};
    		snake.add(a);
    		
    		// Update map to remove snake head
    		int[] a1 = {snake.get(0)[0], snake.get(0)[1]};
    		stringMap[a1[0]][a1[1]] = "N";
    		snake.remove(0);
    		
    	}
    	
    }
    
    static private void redraw() {
    	
    	if(!running) {
    		return;
    	}
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
    			
        		switch(stringMap[i][j]) {
        			case "S":
        				panelMap[i][j].setBackground(SNAKE_BLUE);
        				break;
        			case "A":
        				panelMap[i][j].setBackground(APPLE_RED);
        				break;
        			default:
        				if((i % 2 != 0 && j % 2 == 0) || (i % 2 == 0 && j % 2 != 0)) {
                			panelMap[i][j].setBackground(DARK_GREEN);
                		}else {
                			panelMap[i][j].setBackground(LIGHT_GREEN);
                		}
        				break;
        		}
        	}
    	}
    }
    
    static private void appleEaten() {
    	
    	// TODO: Finish This
    }
    
    static private void gameOver() {
    	
    	running = false;
    	panelMap[0][0].setBackground(Color.BLACK); // TEMPORARY
    	//TODO: Display game over message
    }
    
    static private String[][] initStringMap(){
    	
    	String[][] map = new String[MAP_HEIGHT][MAP_WIDTH];
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
        		map[i][j] = "N";
        	}
    	}
    	
    	//Create snake and apple, adding snake's body cords to the snake ArrayList:
    	map[snakeHeadCords[0]][snakeHeadCords[1] - 2] = "S";
    	int[] a = {snakeHeadCords[0], snakeHeadCords[1] - 2};
    	snake.add(a);
    	
    	map[snakeHeadCords[0]][snakeHeadCords[1] - 1] = "S";
    	int[] a1 = {snakeHeadCords[0], snakeHeadCords[1] - 1};
    	snake.add(a1);
    	
    	map[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
    	int[] a2 = {snakeHeadCords[0], snakeHeadCords[1]};
    	snake.add(a2);
    	
    	map[snakeHeadCords[0]][MAP_WIDTH - 8] = "A";
    	map[snakeHeadCords[0]][MAP_WIDTH - 7] = "A";
    	map[snakeHeadCords[0]][MAP_WIDTH - 6] = "A";
    	map[snakeHeadCords[0]][MAP_WIDTH - 5] = "A";
    	map[snakeHeadCords[0]][MAP_WIDTH - 4] = "A";
    	map[snakeHeadCords[0]][MAP_WIDTH - 3] = "A";
    	map[snakeHeadCords[0]][MAP_WIDTH - 2] = "A";
    	map[snakeHeadCords[0]][MAP_WIDTH - 1] = "A";
    	
    	return map;
    }
    
    static private JPanel[][] initJPanelMap(){
    	
    	JPanel[][] map = new JPanel[MAP_HEIGHT][MAP_WIDTH];
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
        		map[i][j] = new JPanel();
        		if((i % 2 != 0 && j % 2 == 0) || (i % 2 == 0 && j % 2 != 0)) {
        			map[i][j].setBackground(DARK_GREEN);
        		}else {
        			map[i][j].setBackground(LIGHT_GREEN);
        		}
        		frame.add(map[i][j]);
        	}
    	}
    	
    	return map;
    	
    }
    
    static private class KeyInput implements KeyListener {
    	
    	@Override
        public void keyPressed(KeyEvent e) {
            
            int keyCode = e.getKeyCode();
            
            if (keyCode == KeyEvent.VK_UP) {
            	
            	if(!SnakeCurrentDirection.equals("D")) {
            		SnakeDirection = "U";	
            	}
            } else if (keyCode == KeyEvent.VK_DOWN) {
            	
            	if(!SnakeCurrentDirection.equals("U")) {
            		SnakeDirection = "D";
            	}
            } else if (keyCode == KeyEvent.VK_LEFT) {
            	
            	if(!SnakeCurrentDirection.equals("R")) {
            		SnakeDirection = "L";
            	}
            } else if (keyCode == KeyEvent.VK_RIGHT) {
            	
            	if(!SnakeCurrentDirection.equals("L")) {
            		SnakeDirection = "R";
            	}
            }
        }
    	
    	@Override
        public void keyReleased(KeyEvent e) {
            // Not needed
        }
    	@Override
        public void keyTyped(KeyEvent e) {
            // Not needed
        }
    	
    }
}
