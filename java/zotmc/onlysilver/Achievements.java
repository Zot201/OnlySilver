package zotmc.onlysilver;

import static zotmc.onlysilver.Contents.silverBow;
import net.minecraft.stats.Achievement;
import net.minecraft.stats.AchievementList;

public class Achievements {
	
	public static final Content<Achievement> silverBowAch = Content.absent();

	static void init() {
		silverBowAch.set(new Achievement("silverBowAch", "silverBowAch", 6, 5,
				silverBow.get(), AchievementList.acquireIron));
		silverBowAch.get().registerStat();
		
	}
	
}
