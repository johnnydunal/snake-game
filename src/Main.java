import javax.swing.*;
import java.util.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {
	
	/*
	 * Welcome to Snake Game!
	 */
	
	static JFrame frame = new JFrame("Snake Game");
	
	static int gameChosen = 0; // 0 = none chosen yet, 1 = normal snake
	static boolean running = false;
	static boolean paused = false;
	
	static boolean speedBoosted = false;
	static int ticksSpentWithSpeedBoost = 0;
	
	static final int MAP_WIDTH = 23;
    static final int MAP_HEIGHT = 19;
    
    static final int TILE_SIZE = 40;
    
    static int score = 0;
    
    static int DEFAULT_SNAKE_SPEED = 8;
    static int FAST_SNAKE_SPEED = (int)(DEFAULT_SNAKE_SPEED * 2.2);
    static int SNAKE_SPEED = DEFAULT_SNAKE_SPEED; // amount of tiles that the snake moves in a second
    static int BlueSnakeLength = 3;
    static String SnakeDirection = "N";
    static String SnakeCurrentDirection = "R";
    
    static int[] snakeHeadCords = {(int)Math.floor(MAP_HEIGHT / 2), 6}; // keep track of the snake's head's cords (for collision detection)
    // NOTE: snakeHeadCords[0] gives row number, snakeHeadCords[1] gives column number
    
    static ArrayList<int[]> snake = new ArrayList<int[]>(); // to keep track of coordinates of each part of the snake's body
    
    static final Color DARK_GREEN = new Color(0, 150, 0);
    static final Color LIGHT_GREEN = new Color(0, 130, 0);
    static final Color SNAKE_BLUE = new Color(50, 85, 180);
    static final Color APPLE_RED = new Color(125, 10, 0);
    static final Color APPLE_GOLD = new Color(255, 215, 0);
    
    static String[][] stringMap;
    static JPanel[][] panelMap;
    
    // Special variables for the CHAOTIC GAME version:
    static int chaos = 5; // ADAPTIVE DIFFICULTY! Value between 0-10. Higher chaos makes the game harder.
    static int totalNumOfApples = 0;
    static int numOfWalls = 0;
    static final int maxNumOfApples = 8;
    static boolean readyToAddWall = true;
    static int wallsToAddPerWallAttack = 2;
    static int[] portals; // holds coordinates of portals
    
    // Special variables for the 2-player version:
    static int winner = 0; // 0 = no winner yet, 1 = blue, 2 = cyan
    static ArrayList<int[]> snake2 = new ArrayList<int[]>(); // cyan snake
    static int[] snakeHeadCords2 = {0, 0};
    static int CyanSnakeLength = 3;
    static String SnakeDirection2 = "N";
    static String SnakeCurrentDirection2 = "R";

	public static void main(String[] args) throws InterruptedException{
		
		// Create Welcome Window / Start Menu:
		
		frame.setSize(MAP_WIDTH * TILE_SIZE, MAP_HEIGHT * TILE_SIZE);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
		
		JPanel welcomePanel = new JPanel();
		welcomePanel.setLayout(new GridLayout(4, 1));
		welcomePanel.setBackground(Color.WHITE);
    	
        JLabel title = new JLabel("Welcome to Snake Game!", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, TILE_SIZE));
        title.setForeground(LIGHT_GREEN);
        
        JLabel playLabel = new JLabel("Press 1 to play Standard Game", SwingConstants.CENTER);
        playLabel.setFont(new Font("Arial", Font.BOLD, TILE_SIZE));
        playLabel.setBackground(Color.WHITE);
        playLabel.setForeground(Color.BLACK);
        
        JLabel playLabel2 = new JLabel("Press 2 to play Chaotic Game", SwingConstants.CENTER);
        playLabel2.setFont(new Font("Arial", Font.BOLD, TILE_SIZE));
        playLabel2.setBackground(Color.WHITE);
        playLabel2.setForeground(Color.BLACK);
        
        JLabel playLabel3 = new JLabel("Press 3 to play 2-player Chaotic Game", SwingConstants.CENTER);
        playLabel3.setFont(new Font("Arial", Font.BOLD, TILE_SIZE));
        playLabel3.setBackground(Color.WHITE);
        playLabel3.setForeground(Color.BLACK);
        
        welcomePanel.add(title);
        welcomePanel.add(playLabel);
        welcomePanel.add(playLabel2);
        welcomePanel.add(playLabel3);
        
        frame.setLayout(new GridLayout(1, 1));
        frame.getContentPane().add(welcomePanel);
        
        KeyInput keyInput = new KeyInput();
        frame.addKeyListener(keyInput);
        
        frame.setVisible(true);
        
        // wait until a game is chosen from start menu
        while(gameChosen == 0) {
        	Thread.sleep(50);
        }
		
        // play the selected game
        switch(gameChosen) {
        	case 1:
        		playNormalGame();
        		break;
        	case 2:
        		String introMessage = "Chaos is coming! Apples multiply, golden apples shine, walls and portals appear at random, and the world has no edges. Good Luck!";
        		JOptionPane.showMessageDialog(frame, introMessage, "Chaos Ensues", JOptionPane.INFORMATION_MESSAGE);
        		playChaoticGame();
        		break;
        	case 3:
        		String introMessage2 = "Chaos is coming! Apples multiply, walls and portals appear at random, the world has no edges, and there are 2 snakes present.\nOrange snake uses WASD. Blue snake uses arrow keys. Good luck!";
        		JOptionPane.showMessageDialog(frame, introMessage2, "Chaos Ensues - 2 Player", JOptionPane.INFORMATION_MESSAGE);
        		playTwoPlayerChaoticGame();
        		break;
        	default:
        		playNormalGame();
        }
        
    }
	
	static private void playNormalGame() throws InterruptedException{
		
		// This method contains all the code to set up / run the game
		
		frame.getContentPane().removeAll(); // remove all stuff from previous instances
		
		// Reset global variables in case this method is rerun:
		gameChosen = 1;
		running = true;
		paused = false;
		speedBoosted = false;
		ticksSpentWithSpeedBoost = 0;
		score = 0;
		BlueSnakeLength = 3;
	    SnakeDirection = "N";
	    SnakeCurrentDirection = "R";
	    snakeHeadCords[0] = (int)Math.floor(MAP_HEIGHT / 2);
	    snakeHeadCords[1] = 6;
	    snake = new ArrayList<int[]>();
	    stringMap = initStringMap();
	    panelMap = initJPanelMap();
	    
		
		SoundPlayer.playSound("true-short-silence.wav"); // empty sound file to prepare SoundPlayer
        
        frame.getContentPane().setBackground(Color.BLACK);
        
        frame.setLayout(new GridLayout(MAP_HEIGHT, MAP_WIDTH));
        
        redraw(); // to make sure that the snake and apple are on the map
        
        frame.setVisible(true);
        
        // GAME LOOP:
        while(running) {
        	
        	if(paused) {
        		Thread.sleep(100);
        		continue;
        	}
        	
        	update();
        	
        	redraw();
        	
        	//wait
        	Thread.sleep(1000 / SNAKE_SPEED);
        	
        }
	}
	
	static private void playChaoticGame() throws InterruptedException{
		
		/*
		 * CHAOTIC GAME VERSION!!
		 * 
		 * Includes:
		 *  - More Apples
		 *  - More Golden Apples
		 *  - Extra Walls
		 *  - Wrap-around / Teleportation
		 *  - And More to come!
		 * 
		 */
		
		// This method contains all the code to set up / run the game
		
		frame.getContentPane().removeAll(); // remove all stuff from previous instances
		
		// Reset global variables in case this method is rerun:
		gameChosen = 2;
		running = true;
		paused = false;
		speedBoosted = false;
		ticksSpentWithSpeedBoost = 0;
		score = 0;
		BlueSnakeLength = 3;
	    SnakeDirection = "N";
	    SnakeCurrentDirection = "R";
	    snakeHeadCords[0] = (int)Math.floor(MAP_HEIGHT / 2);
	    snakeHeadCords[1] = 6;
	    snake = new ArrayList<int[]>();
	    stringMap = initStringMap();
	    panelMap = initJPanelMap();
	    
		
		SoundPlayer.playSound("true-short-silence.wav"); // empty sound file to prepare SoundPlayer
        
        frame.getContentPane().setBackground(Color.BLACK);
        
        frame.setLayout(new GridLayout(MAP_HEIGHT, MAP_WIDTH));
        
        redraw(); // to make sure that the snake and apple are on the map
        
        // Filling in all necessary apples:
        int counter = 0; // to make sure an infinite loop doesn't happen
        while(totalNumOfApples <= maxNumOfApples) {
        	appleEaten(false);
        	counter++;
        	if(counter > maxNumOfApples) {break;}
        }
        
        frame.setVisible(true);
        
        // GAME LOOP:
        while(running) {
        	
        	if(paused) {
        		Thread.sleep(100);
        		continue;
        	}
        	
        	// Check Whether a Wall should be spawned:
        	if(BlueSnakeLength % 9 == 0 && readyToAddWall) {
        		addWall(true, wallsToAddPerWallAttack);
        		readyToAddWall = false;
        	}
        	
        	update();
        	
        	redraw();
        	
        	//wait
        	Thread.sleep(1000 / SNAKE_SPEED);
        	
        }
	}
	
	static private void playTwoPlayerChaoticGame() throws InterruptedException{
		
		/*
		 * CHAOTIC GAME Two-Player VERSION!!
		 * 
		 * Includes:
		 *  - More Apples
		 *  - More Golden Apples
		 *  - Extra Walls
		 *  - Wrap-around / Teleportation
		 *  - And More to come!
		 * 
		 */
		
		// This method contains all the code to set up / run the game
		
		frame.getContentPane().removeAll(); // remove all stuff from previous instances
		
		// Reset global variables in case this method is rerun:
		gameChosen = 3;
		running = true;
		paused = false;
		speedBoosted = false;
		ticksSpentWithSpeedBoost = 0;
		score = 0;
		BlueSnakeLength = 3;
		CyanSnakeLength = 3;
	    SnakeDirection = "L";
	    SnakeCurrentDirection = "L";
	    SnakeDirection2 = "R";
	    SnakeCurrentDirection2 = "R";
	    snakeHeadCords[0] = MAP_HEIGHT - 2;
	    snakeHeadCords[1] = MAP_WIDTH - 5;
	    snakeHeadCords2[0] = 1;
	    snakeHeadCords2[1] = 4;
	    snake = new ArrayList<int[]>();
	    stringMap = initStringMap();
	    panelMap = initJPanelMap();
	    winner = 0;
		
		SoundPlayer.playSound("true-short-silence.wav"); // empty sound file to prepare SoundPlayer
        
        frame.getContentPane().setBackground(Color.BLACK);
        
        frame.setLayout(new GridLayout(MAP_HEIGHT, MAP_WIDTH));
        
        redraw(); // to make sure that the snake and apple are on the map
        
        // Filling in all necessary apples:
        int counter = 0; // to make sure an infinite loop doesn't happen
        while(totalNumOfApples <= maxNumOfApples) {
        	appleEaten(false);
        	counter++;
        	if(counter > maxNumOfApples) {break;}
        }
        
        frame.setVisible(true);
        
        // GAME LOOP:
        while(running) {
        	
        	if(paused) {
        		Thread.sleep(100);
        		continue;
        	}
        	
        	// Check Whether a Wall should be spawned:
        	if((BlueSnakeLength % 9 == 0 || CyanSnakeLength % 9 == 0) && readyToAddWall) {
        		addWall(true, 1);
        		readyToAddWall = false;
        	}
        	
        	update();
        	update2(); // second snake
        	
        	redraw();
        	
        	//wait
        	Thread.sleep(1000 / SNAKE_SPEED);
        	
        }
	}
    
    static private void update() {
    	
    	// 1) Check for wall collision
    	if(gameChosen == 1 && ((snakeHeadCords[0] == 0 && SnakeDirection.equals("U")) || (snakeHeadCords[0] == MAP_HEIGHT - 1 && SnakeDirection.equals("D")) || (snakeHeadCords[1] == 0 && SnakeDirection.equals("L")) || (snakeHeadCords[1] == MAP_WIDTH - 1 && SnakeDirection.equals("R")))) {
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
    	
    	// wrap snake around edges IF IN CHAOTIC MODE!
    	if(gameChosen == 2 || gameChosen == 3) {
    		snakeHeadCords[0] = (snakeHeadCords[0] + MAP_HEIGHT) % MAP_HEIGHT;
    		snakeHeadCords[1] = (snakeHeadCords[1] + MAP_WIDTH) % MAP_WIDTH;
    	}
    	
    	// 3) Check for snake collision
    	for(int i = 0;i < snake.size();i++) {
    		if(snake.get(i)[0] == snakeHeadCords[0] && snake.get(i)[1] == snakeHeadCords[1]) {
    			winner = 2;
    			gameOver();
        		return;
    		}
    	}
    	
    	// 4) Check for apple eaten & Fully update snake body
    	//		- If yes, increase snake length
    	//		- If no, snake keeps moving
    	String tileReached = stringMap[snakeHeadCords[0]][snakeHeadCords[1]];
    	
    	if(tileReached.equals("A")) { // Apple
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
    		int[] a = {snakeHeadCords[0], snakeHeadCords[1]};
    		snake.add(a);
    		
    		BlueSnakeLength++;
    		score++;
    		totalNumOfApples--;
    		readyToAddWall = true; // for chaotic mode
    		
    		checkSpeed();
    		appleEaten(true);
    		
    	} else if(tileReached.equals("AG")) { // Golden Apple
    		
    		// Golden Apple Eaten!
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
    		int[] a = {snakeHeadCords[0], snakeHeadCords[1]};
    		snake.add(a);
    		
    		BlueSnakeLength++;
    		score++;
    		totalNumOfApples--;
    		readyToAddWall = true; // for chaotic mode
    		
    		ticksSpentWithSpeedBoost = 0;
    		speedBoosted = true;
    		
    		checkSpeed();
    		appleEaten(true);
    		
    	} else if(tileReached.equals("W")){ // Wall
    		
    		winner = 2;
    		gameOver();
    		return;
    	} else if(tileReached.equals("SC")){ // Cyan Snake
    		
    		winner = 2;
    		gameOver();
    		return;
    	} else if(tileReached.equals("P")){ // Portal
    		
    		SoundPlayer.playSound("portal-sound.wav");
    		
    		if(snakeHeadCords[0] == portals[0] && snakeHeadCords[1] == portals[1]) {
    			
    			// portal 1 -> portal 2
    			snakeHeadCords[0] = portals[2];
        		snakeHeadCords[1] = portals[3];
        		
        		// remove portal
        		stringMap[portals[0]][portals[1]] = "N";
        		portals = new int[]{-1, 0, 0, 0};
        		
        		// Update map to show new snake head
        		stringMap[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
        		int[] a = {snakeHeadCords[0], snakeHeadCords[1]};
        		snake.add(a);
        		
        		// Update map to remove snake tail
        		int[] a1 = {snake.get(0)[0], snake.get(0)[1]};
        		stringMap[a1[0]][a1[1]] = "N";
        		snake.remove(0);
    			
    		}else {
    			
    			// portal 2 -> portal 1
    			snakeHeadCords[0] = portals[0];
        		snakeHeadCords[1] = portals[1];
        		
        		// remove portal
        		stringMap[portals[2]][portals[3]] = "N";
        		portals = new int[]{-1, 0, 0, 0};
        		
        		// Update map to show new snake head
        		stringMap[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
        		int[] a = {snakeHeadCords[0], snakeHeadCords[1]};
        		snake.add(a);
        		
        		// Update map to remove snake tail
        		int[] a1 = {snake.get(0)[0], snake.get(0)[1]};
        		stringMap[a1[0]][a1[1]] = "N";
        		snake.remove(0);
    			
    		}
    		
    		if(chaos > 4) {
    			addPortal(); // add new portal
    		}
    		
    	} else { // apple not eaten
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords[0]][snakeHeadCords[1]] = "S";
    		int[] a = {snakeHeadCords[0], snakeHeadCords[1]};
    		snake.add(a);
    		
    		// Update map to remove snake tail
    		int[] a1 = {snake.get(0)[0], snake.get(0)[1]};
    		stringMap[a1[0]][a1[1]] = "N";
    		snake.remove(0);
    		
    	}
    	
    }
    
    static private void update2() { // update cyan snake for 2-player version
    	
    	// 2) Move/Update Snake HEAD
    	switch(SnakeDirection2) {
			case "U":
				SnakeCurrentDirection2 = "U";
				snakeHeadCords2[0]--;
				break;
			case "D":
				SnakeCurrentDirection2 = "D";
				snakeHeadCords2[0]++;
				break;
			case "L":
				SnakeCurrentDirection2 = "L";
				snakeHeadCords2[1]--;
				break;
			case "R":
				SnakeCurrentDirection2 = "R";
				snakeHeadCords2[1]++;
				break;
			default:
				return; // user has not moved yet
    	}
    	
    	// wrap snake around edges
    	snakeHeadCords2[0] = (snakeHeadCords2[0] + MAP_HEIGHT) % MAP_HEIGHT;
    	snakeHeadCords2[1] = (snakeHeadCords2[1] + MAP_WIDTH) % MAP_WIDTH;
    	
    	// 3) Check for snake collision
    	for(int i = 0;i < snake2.size();i++) {
    		if(snake2.get(i)[0] == snakeHeadCords2[0] && snake2.get(i)[1] == snakeHeadCords2[1]) {
    			winner = 1;
    			gameOver();
        		return;
    		}
    	}
    	
    	// 4) Check for apple eaten & Fully update snake body
    	//		- If yes, increase snake length
    	//		- If no, snake keeps moving
    	String tileReached = stringMap[snakeHeadCords2[0]][snakeHeadCords2[1]];
    	
    	if(tileReached.equals("A")) {
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords2[0]][snakeHeadCords2[1]] = "SC";
    		int[] a = {snakeHeadCords2[0], snakeHeadCords2[1]};
    		snake2.add(a);
    		
    		CyanSnakeLength++;
    		totalNumOfApples--;
    		readyToAddWall = true; // for chaotic mode
    		
    		checkSpeed();
    		appleEaten(true);
    		
    	} else if(tileReached.equals("AG")) {
    		
    		// Golden Apple Eaten!
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords2[0]][snakeHeadCords2[1]] = "SC";
    		int[] a = {snakeHeadCords2[0], snakeHeadCords2[1]};
    		snake2.add(a);
    		
    		CyanSnakeLength++;
    		score++;
    		totalNumOfApples--;
    		readyToAddWall = true; // for chaotic mode
    		
    		ticksSpentWithSpeedBoost = 0;
    		speedBoosted = true;
    		
    		checkSpeed();
    		appleEaten(true);
    		
    	} else if(tileReached.equals("W")){
    		
    		winner = 1;
    		gameOver();
    		return;
    	} else if(tileReached.equals("S")) {
    		
    		winner = 1;
    		gameOver();
    		return;
    	} else if(tileReached.equals("P")) {
    		
    		SoundPlayer.playSound("portal-sound.wav");
    		
    		if(snakeHeadCords2[0] == portals[0] && snakeHeadCords2[1] == portals[1]) {
    			
    			// portal 1 -> portal 2
    			snakeHeadCords2[0] = portals[2];
        		snakeHeadCords2[1] = portals[3];
        		
        		// remove portal
        		stringMap[portals[0]][portals[1]] = "N";
        		portals = new int[]{-1, 0, 0, 0};
        		
        		// Update map to show new snake head
        		stringMap[snakeHeadCords2[0]][snakeHeadCords2[1]] = "SC";
        		int[] a = {snakeHeadCords2[0], snakeHeadCords2[1]};
        		snake2.add(a);
        		
        		// Update map to remove snake tail
        		int[] a1 = {snake2.get(0)[0], snake2.get(0)[1]};
        		stringMap[a1[0]][a1[1]] = "N";
        		snake2.remove(0);
    			
    		}else {
    			
    			// portal 2 -> portal 1
    			snakeHeadCords2[0] = portals[0];
        		snakeHeadCords2[1] = portals[1];
        		
        		// remove portal
        		stringMap[portals[2]][portals[3]] = "N";
        		portals = new int[]{-1, 0, 0, 0};
        		
        		// Update map to show new snake head
        		stringMap[snakeHeadCords2[0]][snakeHeadCords2[1]] = "S";
        		int[] a = {snakeHeadCords2[0], snakeHeadCords2[1]};
        		snake2.add(a);
        		
        		// Update map to remove snake tail
        		int[] a1 = {snake2.get(0)[0], snake2.get(0)[1]};
        		stringMap[a1[0]][a1[1]] = "N";
        		snake2.remove(0);
    			
    		}
    		
    		if(chaos > 4) {
    			addPortal(); // add new portal
    		}
    		
    	} else { // apple not eaten
    		
    		// Update map to show new snake head
    		stringMap[snakeHeadCords2[0]][snakeHeadCords2[1]] = "SC";
    		int[] a = {snakeHeadCords2[0], snakeHeadCords2[1]};
    		snake2.add(a);
    		
    		// Update map to remove snake tail
    		int[] a1 = {snake2.get(0)[0], snake2.get(0)[1]};
    		stringMap[a1[0]][a1[1]] = "N";
    		snake2.remove(0);
    		
    	}
    	
    }
    
    static private void redraw() {
    	
    	if(!running) {
    		return;
    	}
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
    			
        		switch(stringMap[i][j]) {
        			case "S": // Snake
        				panelMap[i][j].setBackground(SNAKE_BLUE);
        				break;
        			case "SC": // Snake
        				panelMap[i][j].setBackground(Color.CYAN);
        				break;
        			case "A": // Apple
        				panelMap[i][j].setBackground(APPLE_RED);
        				break;
        			case "AG": // Golden Apple
        				panelMap[i][j].setBackground(APPLE_GOLD);
        				break;
        			case "W": // Wall
        				panelMap[i][j].setBackground(Color.BLACK);
        				break;
        			case "P": // Wall
        				panelMap[i][j].setBackground(Color.MAGENTA);
        				break;
        			default: // normal checkered background
        				if((i % 2 != 0 && j % 2 == 0) || (i % 2 == 0 && j % 2 != 0)) {
                			panelMap[i][j].setBackground(DARK_GREEN);
                		}else {
                			panelMap[i][j].setBackground(LIGHT_GREEN);
                		}
        				break;
        		}
        	}
    	}
    	
    	checkSpeed(); // checks whether the snake's speed is boosted
    }
    
    static private boolean appleEaten(boolean playSound) {
    	
    	// This method returns true if an apple was successfully created, false otherwise
    	
    	if(playSound) {
    		SoundPlayer.playSound("apple-crunch.wav");
    	}
    	
    	int emptySpots = (MAP_HEIGHT * MAP_WIDTH) - BlueSnakeLength - CyanSnakeLength - totalNumOfApples - numOfWalls - 5;
    	
    	String nextAppleType = "A";
    	
    	// Try for golden apple:
    	if(score > 8) {
    		Random r = new Random();
    		int goldenApplePossibility = r.nextInt(20);
    		if(goldenApplePossibility == 0) {
    			// GOLDEN APPLE!
    			nextAppleType = "AG";
    		}
    	}
    	
    	if(gameChosen == 3) {
    		nextAppleType = "A"; // no golden apples in 2-player
    	}
    	
    	// generate empty spot for apple
    	
    	Random r = new Random();
    	
    	int chosenSpot = r.nextInt(emptySpots);
    	int currentSpot = 0;
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
    			
        		if(stringMap[i][j].equals("N")) {
        			if(chosenSpot == currentSpot) {
        				stringMap[i][j] = nextAppleType;
        				totalNumOfApples++;
        				return true;
        			}
        			currentSpot++;
        		}
        	}
    	}
    	
    	return false;
    }
    
    static private void addWall(boolean playSound, int amountOfWalls) {
    	
    	if(playSound) {
    		SoundPlayer.playSound("wall-spawns.wav");
    	}
    	
    	int emptySpots = (MAP_HEIGHT * MAP_WIDTH) - BlueSnakeLength - CyanSnakeLength - totalNumOfApples - numOfWalls - 5;
    	
    	String nextWallType = "W";
    	
    	// generate empty spots for wall and fill them in
    	
    	for(int w = 0;w < amountOfWalls;w++) {
    		
    		Random r = new Random();
        	
        	int chosenSpot = r.nextInt(emptySpots);
        	int currentSpot = 0;
        	
        	for(int i = 0;i < MAP_HEIGHT;i++) {
        		for(int j = 0;j < MAP_WIDTH;j++) {
        			
            		if(stringMap[i][j].equals("N")) {
            			if(chosenSpot == currentSpot) {
            				stringMap[i][j] = nextWallType;
            				numOfWalls++;
            			}
            			currentSpot++;
            		}
            	}
        	}
    		
    	}
		
	}
    
    static private void addPortal() {
    	
    	int emptySpots = (MAP_HEIGHT * MAP_WIDTH) - BlueSnakeLength - CyanSnakeLength - totalNumOfApples - numOfWalls - 5;
    	
    	for(int p = 0;p < 3;p+=2) {
    		
    		Random r = new Random();
        	
        	int chosenSpot = r.nextInt(emptySpots);
        	int currentSpot = 0;
        	
        	for(int i = 0;i < MAP_HEIGHT;i++) {
        		for(int j = 0;j < MAP_WIDTH;j++) {
        			
            		if(stringMap[i][j].equals("N")) {
            			if(chosenSpot == currentSpot) {
            				stringMap[i][j] = "P";
            				portals[p] = i;
            				portals[p + 1] = j;
            				numOfWalls++;
            			}
            			currentSpot++;
            		}
            	}
        	}
    		
    	}
    }
    
    static private void checkSpeed() {
    	
    	// adjusts the snake's speed based off of the snake's length or boosted status
    	
    	if(speedBoosted) {
    		
    		SNAKE_SPEED = FAST_SNAKE_SPEED;
    		
    		if(ticksSpentWithSpeedBoost > 120) {
    			speedBoosted = !speedBoosted; // turn off speed boost
    			ticksSpentWithSpeedBoost = 0; // reset speed boost timer
    		}else {
    			ticksSpentWithSpeedBoost++;
    		}
    	} else {
    		SNAKE_SPEED = DEFAULT_SNAKE_SPEED;
    	}
    }
    
    static private void gameOver() {
    	
    	running = false;
    	
    	SoundPlayer.playSound("musical-game-over.wav");
    	
    	JPanel gameOverPanel = new JPanel();
    	gameOverPanel.setLayout(new GridLayout(3, 1));
    	gameOverPanel.setBackground(Color.WHITE);
    	
        JLabel gameOverMessage = new JLabel("GAME OVER", SwingConstants.CENTER);
        gameOverMessage.setFont(new Font("Arial", Font.BOLD, 52));
        gameOverMessage.setForeground(Color.RED);
        
        JLabel scoreMessage = new JLabel("Score: " + score, SwingConstants.CENTER);
        scoreMessage.setFont(new Font("Arial", Font.BOLD, 42));
        scoreMessage.setForeground(Color.BLACK);
        if(gameChosen == 3) {
        	if(winner == 1) {
        		scoreMessage.setText("Dark Blue Wins!");
        		scoreMessage.setForeground(SNAKE_BLUE);
        	}else if(winner == 2) {
        		scoreMessage.setText("Cyan Wins!");
        		scoreMessage.setForeground(Color.CYAN);
        	}
        }
        
        JButton exitButton = new JButton("Exit");
        exitButton.setFont(new Font("Arial", Font.BOLD, 42));
        exitButton.setBackground(Color.WHITE);
        exitButton.setForeground(Color.BLACK);
        exitButton.addActionListener(e -> {
            System.exit(0);
        });
        
        gameOverPanel.add(gameOverMessage);
        gameOverPanel.add(scoreMessage);
        gameOverPanel.add(exitButton);
        
        frame.getContentPane().removeAll();
        frame.setLayout(new GridLayout(1, 1));
        frame.getContentPane().add(gameOverPanel);
        
        frame.revalidate();
        frame.repaint();
        
    }
    
    
    static private String[][] initStringMap(){
    	
    	String[][] map = new String[MAP_HEIGHT][MAP_WIDTH];
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
        		map[i][j] = "N";
        	}
    	}
    	
    	if(gameChosen != 1) { // setup portals for chaotic mode
    		portals = new int[] {6, MAP_WIDTH / 2, 12, MAP_WIDTH / 2};
    		map[portals[0]][portals[1]] = "P";
    		map[portals[2]][portals[3]] = "P";
    	}
    	
    	if(gameChosen == 3) { // check for two-player version
    		
    		// Blue Snake
        	map[MAP_HEIGHT - 2][MAP_WIDTH - 3] = "S";
        	int[] a = {MAP_HEIGHT - 2, MAP_WIDTH - 3};
        	snake.add(a);
        	
        	map[MAP_HEIGHT - 2][MAP_WIDTH - 4] = "S";
        	int[] a1 = {MAP_HEIGHT - 2, MAP_WIDTH - 4};
        	snake.add(a1);
        	
        	map[MAP_HEIGHT - 2][MAP_WIDTH - 5] = "S";
        	int[] a2 = {MAP_HEIGHT - 2, MAP_WIDTH - 5};
        	snake.add(a2);
        	
        	// Cyan Snake
        	map[1][2] = "SC";
        	int[] a3 = {1, 2};
        	snake2.add(a3);
        	
        	map[1][3] = "SC";
        	int[] a4 = {1, 3};
        	snake2.add(a4);
        	
        	map[1][4] = "SC";
        	int[] a5 = {1, 4};
        	snake2.add(a5);
        	
        	return map;
    		
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
    	
    	map[snakeHeadCords[0]][MAP_WIDTH - 6] = "A";
    	totalNumOfApples++;
    	
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
        		frame.getContentPane().add(map[i][j]);
        	}
    	}
    	
    	return map;
    	
    }
    
    
    
    static private class KeyInput implements KeyListener {
    	
    	@Override
        public void keyPressed(KeyEvent e) {
            
            int keyCode = e.getKeyCode();
            
            if (keyCode == KeyEvent.VK_UP && gameChosen != 0) {
            	
            	if(!SnakeCurrentDirection.equals("D")) {
            		SnakeDirection = "U";
            	}
            } else if (keyCode == KeyEvent.VK_DOWN && gameChosen != 0) {
            	
            	if(!SnakeCurrentDirection.equals("U")) {
            		SnakeDirection = "D";
            	}
            } else if (keyCode == KeyEvent.VK_LEFT && gameChosen != 0) {
            	
            	if(!SnakeCurrentDirection.equals("R")) {
            		SnakeDirection = "L";
            	}
            } else if (keyCode == KeyEvent.VK_RIGHT && gameChosen != 0) {
            	
            	if(!SnakeCurrentDirection.equals("L")) {
            		SnakeDirection = "R";
            	}
            } else if ((keyCode == KeyEvent.VK_P || keyCode == KeyEvent.VK_ESCAPE) && gameChosen != 0) {
            	
            	paused = !paused;
            } else if (keyCode == KeyEvent.VK_1 && gameChosen == 0) {
            	
            	gameChosen = 1;
            } else if (keyCode == KeyEvent.VK_2 && gameChosen == 0) {
            	
            	gameChosen = 2;
            } else if (keyCode == KeyEvent.VK_3 && gameChosen == 0) {
            	
            	gameChosen = 3;
            }
            
            if(gameChosen == 3) {
            	if (keyCode == KeyEvent.VK_W && gameChosen != 0) {
                	
                	if(!SnakeCurrentDirection2.equals("D")) {
                		SnakeDirection2 = "U";
                	}
                } else if (keyCode == KeyEvent.VK_S && gameChosen != 0) {
                	
                	if(!SnakeCurrentDirection2.equals("U")) {
                		SnakeDirection2 = "D";
                	}
                } else if (keyCode == KeyEvent.VK_A && gameChosen != 0) {
                	
                	if(!SnakeCurrentDirection2.equals("R")) {
                		SnakeDirection2 = "L";
                	}
                } else if (keyCode == KeyEvent.VK_D && gameChosen != 0) {
                	
                	if(!SnakeCurrentDirection2.equals("L")) {
                		SnakeDirection2 = "R";
                	}
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
