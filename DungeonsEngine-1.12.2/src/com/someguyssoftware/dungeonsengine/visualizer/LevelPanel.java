/**
 * 
 */
package com.someguyssoftware.dungeonsengine.visualizer;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import javax.swing.JPanel;

import com.someguyssoftware.dungeonsengine.model.Level;
import com.someguyssoftware.dungeonsengine.model.Room;

/**
 * @author Mark Gottschling on Sep 17, 2018
 *
 */
public class LevelPanel extends JPanel {
	private Level level;
	public static int CANVAS_WIDTH = 850;
	public static int CANVAS_HEIGHT = 650;
	public static int CANVAS_START_X = 250;
	public static int CANVAS_START_Y = 30;
	
	/**
	 * 
	 */
	public LevelPanel(Level level) {
		super();
		this.level = level;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		// create 2D graphics
		Graphics2D g2d = (Graphics2D)g.create();

		// setup the rendering hints
        RenderingHints rh =
            new RenderingHints(RenderingHints.KEY_ANTIALIASING, 
            RenderingHints.VALUE_ANTIALIAS_ON);

        rh.put(RenderingHints.KEY_RENDERING,
               RenderingHints.VALUE_RENDER_QUALITY);

        g2d.setRenderingHints(rh);
        
        // setup the title 
        g2d.setFont(new Font("Verdana", Font.BOLD, 14));
        g2d.setColor(Color.BLACK);
        String title = "Dungeons2! Level Visualizer 2";
        g2d.drawString(title, 200, 15);

        // draw map border area
        g2d.setColor(Color.BLACK);		
        g2d.fillRoundRect(CANVAS_START_X, CANVAS_START_Y, CANVAS_WIDTH, CANVAS_HEIGHT, 3, 3);
        
        // normalize field
        // find how many times level.z fits into rect.y
        int width = (int) (level.getField().maxX - level.getField().minX);
        int depth = (int) (level.getField().maxZ - level.getField().minZ);
        double sizeMultiplier = Math.floor((CANVAS_HEIGHT-20) / depth);
        System.out.println("depth x:" + sizeMultiplier);
        
        // center field in canvas
        int fieldWidth =  (int)(width * sizeMultiplier);
        int fieldDepth =  (int)(depth*sizeMultiplier);
        int fieldStartX = CANVAS_START_X + (CANVAS_WIDTH/2) - (fieldWidth/2);
        int fieldStartY = CANVAS_START_Y + (CANVAS_HEIGHT/2) - (fieldDepth/2);
        g2d.setColor(new Color(0, 128, 0));	
        g2d.fillRect(fieldStartX, fieldStartY, 	fieldWidth, fieldDepth);

        // setup the font
        g2d.setFont(new Font("Verdana", Font.PLAIN, 9));
        
        // normalize rooms
        for (Room room : level.getRooms()) {
        	System.out.println(room.getId() + ") " + room.printDimensions());
        	System.out.println(room.getId() + ")" + room.printCenter());
        	if (room.isStart()) System.out.println("Start");
        	if (room.isEnd()) System.out.println("End");
        	
        	/*
        	 *  draw the room rec
        	 */
        	
        	// check if the room is start, end or normal.
			if (room.isStart()) {
				g2d.setColor(Color.GREEN);
			}
			else if (room.isEnd()) {
				g2d.setColor(Color.RED);					
			}
			else {
				g2d.setColor(new Color(130, 100, 84));
			}
			
        	g2d.fillRoundRect((int) (fieldStartX + (room.getCoords().getX() * sizeMultiplier)),
        			(int) (fieldStartY + (room.getCoords().getZ() * sizeMultiplier)),
        			(int)(room.getWidth() * sizeMultiplier), (int) (room.getDepth() * sizeMultiplier), 3, 3);
        	
        	// draw the room border
        	g2d.setColor(Color.BLACK);
        	g2d.drawRoundRect((int) (fieldStartX + (room.getCoords().getX() * sizeMultiplier)),
        			(int) (fieldStartY + (room.getCoords().getZ() * sizeMultiplier)),
        			(int)(room.getWidth() * sizeMultiplier), (int) (room.getDepth() * sizeMultiplier), 3, 3);
        	
			// draw the room #
			g2d.setColor(Color.BLACK);
			g2d.drawString(String.valueOf(room.getId()),
					fieldStartX + (int)(room.getCoords().getX()* sizeMultiplier)+3, 
					fieldStartY + (int)(room.getCoords().getZ() * sizeMultiplier)+11);
			g2d.setColor(Color.WHITE);
			g2d.drawString(String.valueOf(room.getId()), 
					fieldStartX + (int)(room.getCoords().getX()*sizeMultiplier)+2, 
					fieldStartY + (int)(room.getCoords().getZ()*sizeMultiplier)+10);
        }
	}
}