/**
 * 
 */
package com.someguyssoftware.dungeonsengine.visualizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.builder.LevelBuilder;
import com.someguyssoftware.dungeonsengine.builder.RoomBuilder;
import com.someguyssoftware.dungeonsengine.config.LevelConfig;
import com.someguyssoftware.dungeonsengine.model.Level;
import com.someguyssoftware.dungeonsengine.model.Room;
import com.someguyssoftware.gottschcore.Quantity;
import com.someguyssoftware.gottschcore.positional.Coords;
import com.someguyssoftware.gottschcore.positional.ICoords;

import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;

/**
 * @author Mark Gottschling on Sep 17, 2018
 *
 */
public class LevelVisualizer {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");

	/**
	 * 
	 */
	public LevelVisualizer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// set up the builder
//		LevelBuilder builder = new LevelBuilder();
//		LevelConfig config = new LevelConfig();
		
//		ICoords startPoint = new Coords(500, 100, 500);
		
		// default seed for random
		long seed = System.currentTimeMillis();
		
		if (args.length ==1) seed = Long.valueOf(args[0]);
		Random random = new Random(seed);
		
		// build a level
		LevelConfig config = new LevelConfig();
		config.setNumberOfRooms(new Quantity(25, 50)); // VAST = 25-50
		double factor = 3.2;
		config.setWidth(new Quantity(5, 15));
		config.setDepth(new Quantity(5, 15));
		config.setHeight(new Quantity(5, 10));
		config.setDegrees(new Quantity(2, 4));
		config.setXDistance(new Quantity(-(30*factor), (30*factor)));
		config.setZDistance(new Quantity(-30*factor, 30*factor));
		config.setYVariance(new Quantity(0, 0));
		config.setMinecraftConstraintsOn(false);
		config.setSupportOn(false);
		
		/*
		 * setup values for builders
		 */
		ICoords startPoint = new Coords(100, 0, 100);
		AxisAlignedBB levelField = new AxisAlignedBB(new BlockPos(0,0,0), new BlockPos(200, 0, 200));
		AxisAlignedBB roomField = new AxisAlignedBB(new BlockPos(60,0,60), new BlockPos(140, 0, 140));
		int w = (int) Math.abs(roomField.maxX - roomField.minX);
		int d = (int) Math.abs(roomField.maxZ - roomField.minZ);
		AxisAlignedBB endField = new AxisAlignedBB(
				new BlockPos(Math.max(roomField.minX-(w/2), levelField.minX), 0,
						Math.max(roomField.minZ-(d/2), levelField.minZ)),
				new BlockPos(Math.min(roomField.maxX+(w/2), levelField.maxX), 0, 
						Math.min(roomField.maxZ+(d/2), levelField.maxZ)));
		
		
		List<Room> plannedRooms = new ArrayList<>();
		
		RoomBuilder roomBuilder = new RoomBuilder(random, roomField, startPoint, config);		
		RoomBuilder endRoomBuilder = new RoomBuilder(random, endField, startPoint, config);	
		LevelBuilder builder = new LevelBuilder(null, random); // TODO require room builder in constructor
		builder.setRoomBuilder(roomBuilder);
		
		Room startRoom = roomBuilder.buildStartRoom();
		plannedRooms.add(startRoom);
		Room endRoom =endRoomBuilder.buildEndRoom(plannedRooms);//.setAnchor(false);

		Level level = builder
			.withStartPoint(startPoint)		// TODO optional - get from room builder
			.withConfig(config)						// TODO optional - get from room builder
			.withField(levelField)					// TODO optional - get from room builder
			.withStartRoom(startRoom)
			.withEndRoom(endRoom)
			.build();
		
		System.out.println(level);
		System.out.println(level.getField());
		logger.debug(level);
		
		// visualize the level
		// draw out rectangles
		JFrame window = new JFrame();
		JPanel panel = new LevelPanel(level, builder);
		window.setTitle("Dungeons2! Level Visualizer 2");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setBounds(0, 0, 1400, 750);
		window.add(panel);
		window.setVisible(true);
	}

}
