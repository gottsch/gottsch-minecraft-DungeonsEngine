package com.someguyssoftware.dungeonsengine.printer;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.someguyssoftware.dungeonsengine.model.IDungeon;
import com.someguyssoftware.dungeonsengine.model.ILevel;

/**
 * 
 * @author Mark Gottschling on Aug 26, 2017
 *
 */
public class DungeonPrettyPrinter implements IPrettyPrinter {
	public static Logger logger = LogManager.getLogger("DungeonsEngine");
	private static final String div;
	
	static {
		// setup a divider line
		char[] chars = new char[75];
		Arrays.fill(chars, '*');
		div = new String(chars) + "\n";	
	}
	
	public DungeonPrettyPrinter() {}
	
	public String print(Object dungeon) {
		return print((IDungeon)dungeon, "Dungeon");
	}
	
	/**
	 * Print all the properties of a dungeon in a prettified format out to a file.
	 * @param dungeon
	 * @param path
	 */
	public String print(IDungeon dungeon, Path filePath) {
		String s = print(dungeon);
//		Path path = Paths.get(filePath).toAbsolutePath();
		try {
			Files.write(filePath, s.getBytes());
		} catch (IOException e) {
			logger.error("Error writing Dungeon to dump file", e);
		}
		return s;
	}
	
	/**
	 * Print all the properties of a dungeon in a prettified format out to a String
	 * @param dungeon
	 * @return
	 */
	public String print(IDungeon dungeon, String title) {
		StringBuilder sb = new StringBuilder();
		try {
			String format = "**    %1$-33s: %2$-30s  **\n";
			String heading = "**  %1$-67s  **\n";
			sb
			.append(div)
			.append(String.format("**  %-67s  **\n", title))
			.append(div)
			.append(String.format(heading, "[Config]"))
			.append(String.format(format, "Min. Y Position", dungeon.getConfig().getYBottom()))
			.append(String.format(format, "Max. Y Position", dungeon.getConfig().getYTop()))
			.append(String.format(format, "Surface Buffer", dungeon.getConfig().getSurfaceBuffer()))
			.append(String.format(heading, "[Properties]"))
			.append(String.format(format, "Location", dungeon.getEntrance().getBottomCenter().toShortString()))
			.append(String.format(format, "# of Levels", dungeon.getLevels().size()))
//			.append(String.format(format, "X Range", String.format("%s <--> %s", dungeon.getMinX(), dungeon.getMaxX())))
//			.append(String.format(format, "Y Range", String.format("%s <--> %s", dungeon.getMinY(), dungeon.getMaxY())))
//			.append(String.format(format, "Z Range", String.format("%s <--> %s", dungeon.getMinZ(), dungeon.getMaxZ())))
			.append(div)
			.append("\n");
			
			// entrance room
			RoomPrettyPrinter roomPrinter = new RoomPrettyPrinter();
			String room = roomPrinter.print(dungeon.getEntrance(), "Entrance Room");
			sb.append(room).append("\n");
			
			// levels
			LevelPrettyPrinter levelPrinter = new LevelPrettyPrinter();
			
			int levelIndex = 1;
			for (ILevel l : dungeon.getLevels()) {
				String level = levelPrinter.print(l, "> LEVEL" + levelIndex);
				sb.append(level).append("\n");
//				sb
//				.append(div)
//				.append(String.format("**  %-67s  **\n", String.format("> LEVEL %d", levelIndex)))	
//				.append(div);
				levelIndex++;
			}
			
		}
		catch(Exception e) {
			return e.getMessage();
		}
		return sb.toString();
	}
}
