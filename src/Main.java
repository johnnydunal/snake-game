import javax.swing.*;

import java.awt.*;

public class Main {
	
	static JFrame frame = new JFrame("Snake Game");
	
	static final int MAP_WIDTH = 22;
    static final int MAP_HEIGHT = 18;

    @SuppressWarnings("unused")
	public static void main(String[] args) {

        //frame.setSize(600, 450);
        frame.setSize(MAP_WIDTH*40, MAP_HEIGHT*40);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        frame.getContentPane().setBackground(Color.BLACK);

        frame.setLayout(new GridLayout(MAP_HEIGHT, MAP_WIDTH));
        
        String[][] stringMap = initStringMap();
        JPanel[][] panelMap = initJPanelMap();

        frame.setVisible(true);

    }
    
    static private String[][] initStringMap(){
    	
    	String[][] map = new String[MAP_HEIGHT][MAP_WIDTH];
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
        		map[i][j] = "N";
        	}
    	}
    	
    	return map;
    }
    
    static private JPanel[][] initJPanelMap(){
    	
    	JPanel[][] map = new JPanel[MAP_HEIGHT][MAP_WIDTH];
    	
    	for(int i = 0;i < MAP_HEIGHT;i++) {
    		for(int j = 0;j < MAP_WIDTH;j++) {
        		map[i][j] = new JPanel();
        		if((i % 2 != 0 && j % 2 == 0) || (i % 2 == 0 && j % 2 != 0)) {
        			map[i][j].setBackground(new Color(0, 150, 0));
        		}else {
        			map[i][j].setBackground(new Color(0, 130, 0));
        		}
        		frame.add(map[i][j]);
        	}
    	}
    	
    	return map;
    	
    }
}

