package lilliputian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import lilliputian.util.EntitySizeUtil;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Config {
	
	private final static String CATEGORY_GENERAL = "all.general";
	
	private final static List<String> PROPERTY_ORDER_GENERAL = new ArrayList<String>();
	
	private static List<String> resizingBlacklistStrings;
	public static final List<Class> RESIZING_BLACKLIST = new ArrayList<Class>();
	private static List<String> entityBaseSizeStrings;
	public static final Map<Class, BaseSizeRange> ENTITY_BASESIZES = new HashMap<Class, BaseSizeRange>();
	public static BaseSizeRange PLAYER_BASESIZE;
	
	public static void readConfig() {
		Configuration cfg = Lilliputian.config;
		try {
			cfg.load();
			initGeneralConfig(cfg);
		} catch (Exception e1) {

		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}
	
	private static void initGeneralConfig(Configuration cfg) {
		cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General Options");
		
		resizingBlacklistStrings = Arrays.asList(cfg.getStringList("Entity Resizing Blacklist", CATEGORY_GENERAL, new String[] { "minecraft:giant", "minecraft:elder_guardian", "minecraft:shulker" }, "Entities on this list will not be resizeable. If you load a world with previously resized entities after blacklisting them, they will return to normal size."));
		entityBaseSizeStrings = Arrays.asList(cfg.getStringList("Entity Base Sizes", CATEGORY_GENERAL, new String[] { }, "The entries of this list will be parsed and used to determine the base size of newly spawned mobs. You can give a single value per entity, or give an entity a range from which a random base size will be selected.\nformat: <entity name>|<basesize> OR <entity name>|<min>-<max>"));
		String playerBaseSizeRangeString = cfg.getString("Player Base Size", CATEGORY_GENERAL, "1", "This string will be used to determine the base size of players when they spawn in a world. You can give a single value, or a range from which a random base size will be selected.\nformat: <basesize> OR <min>-<max>");
		PLAYER_BASESIZE = new BaseSizeRange(playerBaseSizeRangeString);
		
		PROPERTY_ORDER_GENERAL.add("Entity Resizing Blacklist");
		PROPERTY_ORDER_GENERAL.add("Player Base Size");
		PROPERTY_ORDER_GENERAL.add("Entity Base Sizes");
		
		cfg.setCategoryPropertyOrder(CATEGORY_GENERAL, PROPERTY_ORDER_GENERAL);
	}
	
	public static void postInit() {
		for (String entityName : resizingBlacklistStrings) {
			ResourceLocation key = new ResourceLocation(entityName);
			if (ForgeRegistries.ENTITIES.containsKey(key)) {
				EntityEntry e = ForgeRegistries.ENTITIES.getValue(key);
				RESIZING_BLACKLIST.add(e.getEntityClass());
			}
		}
		
		for (String listEntry : entityBaseSizeStrings) {
			int separator = listEntry.indexOf("|");
			if (separator < 1) {
				continue;
			}
			ResourceLocation key = new ResourceLocation(listEntry.substring(0, separator));
			if (ForgeRegistries.ENTITIES.containsKey(key)) {
				EntityEntry e = ForgeRegistries.ENTITIES.getValue(key);
				ENTITY_BASESIZES.put(e.getEntityClass(), new BaseSizeRange(listEntry.substring(separator + 1)));
			}
		}
	}
	
	public static class BaseSizeRange {
		
		float min, max;
		
		private BaseSizeRange(String configEntry) {
			int separator = configEntry.indexOf("-");
			if (separator > 0) {
				String minString = configEntry.substring(0, separator);
				String maxString = configEntry.substring(separator + 1);
				
				this.min = Math.max(Float.parseFloat(minString), EntitySizeUtil.HARD_MIN);
				this.max = Math.min(Float.parseFloat(maxString), EntitySizeUtil.HARD_MAX);
			} else {
				this.min = this.max = MathHelper.clamp(Float.parseFloat(configEntry), EntitySizeUtil.HARD_MIN, EntitySizeUtil.HARD_MAX);
			}
		}
		
		private BaseSizeRange(float min, float max) {
			this.min = Math.max(min, EntitySizeUtil.HARD_MIN);
			this.max = Math.min(max, EntitySizeUtil.HARD_MAX);
		}
		
		public float randomBaseSize(Random rand) {
			if (this.min == this.max) {
				return this.min;
			} else {
				return (rand.nextFloat() * (this.max - this.min)) + this.min;
			}
		}
		
	}

}
