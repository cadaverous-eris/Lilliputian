package lilliputian;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import com.google.common.collect.Lists;

import lilliputian.util.EntitySizeUtil;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.common.config.ConfigCategory;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

public class Config {
	
private final static String CATEGORY_GENERAL = "all.general";
	
	private final static List<String> PROPERTY_ORDER_GENERAL = new ArrayList<String>();
	
	private static List<String> resizingBlacklistStrings;
	public static final List<Class> RESIZING_BLACKLIST = new ArrayList<Class>();
	private static List<String> entityBaseSizeStrings;
	public static final Map<Class, SizeRange> ENTITY_SIZES = new HashMap<Class, SizeRange>();
	
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
		
		resizingBlacklistStrings = Lists.newArrayList(cfg.getStringList("Entity Resizing Blacklist", CATEGORY_GENERAL, new String[] { "minecraft:giant", "minecraft:elder_guardian", "minecraft:shulker" }, "Entities on this list will not be resizeable. If you load a world with previously resized entities after blacklisting them, they will return to normal size."));
		entityBaseSizeStrings = Lists.newArrayList(cfg.getStringList("Entity Base Sizes", CATEGORY_GENERAL, new String[] { }, "The entries of this list will be parsed and used to determine the size of newly spawned mobs. You can give a single value per entity, or give an entity a range from which a random base size will be selected.\nformat: <entity name>|<basesize> OR <entity name>|<min>-<max>"));
		
		PROPERTY_ORDER_GENERAL.add("Entity Resizing Blacklist");
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
				ENTITY_SIZES.put(e.getEntityClass(), new SizeRange(listEntry.substring(separator + 1)));
			}
		}
		
		for (Entry<ResourceLocation, EntityEntry> entry : ForgeRegistries.ENTITIES.getEntries()) {
			if (!RESIZING_BLACKLIST.contains(entry.getValue().getEntityClass()) && EntityLiving.class.isAssignableFrom(entry.getValue().getEntityClass())) {
				if (!ENTITY_SIZES.containsKey(entry.getValue().getEntityClass())) {
					entityBaseSizeStrings.add(entry.getKey().toString() + "|1.0");
				}
			}
		}
		ConfigCategory general = Lilliputian.config.getCategory(CATEGORY_GENERAL);
		Property sizes = general.get("Entity Base Sizes");
		sizes.set(entityBaseSizeStrings.toArray(new String[entityBaseSizeStrings.size()]));
	}
	
	public static class SizeRange {
		
		float min, max;
		
		private SizeRange(String configEntry) {
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
		
		private SizeRange(float min, float max) {
			this.min = Math.max(min, EntitySizeUtil.HARD_MIN);
			this.max = Math.min(max, EntitySizeUtil.HARD_MAX);
		}
		
		public float randomSize(Random rand) {
			if (this.min == this.max) {
				return this.min;
			} else {
				return (rand.nextFloat() * (this.max - this.min)) + this.min;
			}
		}
		
	}

}
