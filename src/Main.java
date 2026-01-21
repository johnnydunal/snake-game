import javax.swing.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class Main {
	
	/*
	 * Welcome to Snake Game!
	 * 
	 * This whole game is built in a single file! (because why not)
	 */
	
	static JFrame frame = new JFrame("Snake Game");
	
	static final boolean RUNNING = true;
	
	static final int MAP_WIDTH = 23;
    static final int MAP_HEIGHT = 19;
    
    static final int TILE_SIZE = 40;
    
    static final int SNAKE_SPEED = 3; // amount of tiles that the snake moves in a second
    
    static final Color DARK_GREEN = new Color(0, 150, 0);
    static final Color LIGHT_GREEN = new Color(0, 130, 0);
    static final Color SNAKE_BLUE = new Color(50, 85, 180);
    static final Color APPLE_RED = new Color(125, 10, 0);
    
    static final String[][] stringMap = initStringMap();
    static final JPanel[][] panelMap = initJPanelMap();
    
    static String SnakeDirection = "L";

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
        while(RUNNING) {
        	
        	update();
        	
        	redraw();
        	
        	//wait
        	Thread.sleep(1000 / SNAKE_SPEED);
        	
        }

    }
    
    static private void update() {
    	
    	switch(SnakeDirection) {
		case "U":
			panelMap[0][1].setBackground(Color.BLACK);
			break;
		case "D":
			panelMap[2][1].setBackground(Color.BLACK);
			break;
		case "L":
			panelMap[1][0].setBackground(Color.BLACK);
			break;
		case "R":
			panelMap[1][2].setBackground(Color.BLACK);
			break;
    	}
    }
    
    static private void redraw() {
    	
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
    
    static private void gameOver() {
    	
    }
    
    static private String[][] initStringMap(){
    	
    	String[][] map = new String[MAP_HEIGHT][MAP_WIDTH];
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
        		map[i][j] = "N";
        	}
    	}
    	
    	//Create snake and apple:
    	map[(int)Math.floor(MAP_HEIGHT / 2)][4] = "S";
    	map[(int)Math.floor(MAP_HEIGHT / 2)][5] = "S";
    	map[(int)Math.floor(MAP_HEIGHT / 2)][6] = "S";
    	map[(int)Math.floor(MAP_HEIGHT / 2)][MAP_WIDTH - 6] = "A";
    	
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
            	SnakeDirection = "U";
            } else if (keyCode == KeyEvent.VK_DOWN) {
            	SnakeDirection = "D";
            } else if (keyCode == KeyEvent.VK_LEFT) {
            	SnakeDirection = "L";
            } else if (keyCode == KeyEvent.VK_RIGHT) {
            	SnakeDirection = "R";
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
