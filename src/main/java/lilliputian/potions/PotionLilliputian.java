package lilliputian.potions;

import lilliputian.Lilliputian;
import net.minecraft.client.Minecraft;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class PotionLilliputian extends Potion {

	public static ResourceLocation POTION_ICONS = new ResourceLocation(Lilliputian.MODID, "textures/gui/potion_icons.png");
	
	public PotionLilliputian(boolean isBadEffect, int color, String name) {
		super(isBadEffect, color);
		setPotionName("effect." + name);
		if (!isBadEffect) {
			setBeneficial();
		}
	}
	
	public PotionLilliputian setIconIndex(int x, int y) {
		super.setIconIndex(x, y);
		return this;
	}
	
	@SideOnly(Side.CLIENT)
	@Override
	public int getStatusIconIndex() {
		Minecraft.getMinecraft().getTextureManager().bindTexture(POTION_ICONS);
		return super.getStatusIconIndex();
	}
	
	public static final Potion SHRINKING_POTION = new PotionLilliputian(false, 0xc300ff, "shrinking").setIconIndex(0, 0).setRegistryName(new ResourceLocation(Lilliputian.MODID, "shrinking"));
	public static final Potion GROWING_POTION = new PotionLilliputian(false, 0x00ff87, "growing").setIconIndex(1, 0).setRegistryName(new ResourceLocation(Lilliputian.MODID, "growing"));
	
	public static final PotionType SHRINKING = new PotionType("shrinking", new PotionEffect[] { new PotionEffect(SHRINKING_POTION, 3600, 0) }).setRegistryName(new ResourceLocation(Lilliputian.MODID, "shrinking"));
	public static final PotionType LONG_SHRINKING = new PotionType("shrinking", new PotionEffect[] { new PotionEffect(SHRINKING_POTION, 9600, 0) }).setRegistryName(new ResourceLocation(Lilliputian.MODID, "long_shrinking"));
	public static final PotionType STRONG_SHRINKING = new PotionType("shrinking", new PotionEffect[] { new PotionEffect(SHRINKING_POTION, 1800, 1) }).setRegistryName(new ResourceLocation(Lilliputian.MODID, "strong_shrinking"));
	
	public static final PotionType GROWING = new PotionType("growing", new PotionEffect[] { new PotionEffect(GROWING_POTION, 3600, 0) }).setRegistryName(new ResourceLocation(Lilliputian.MODID, "growing"));
	public static final PotionType LONG_GROWING = new PotionType("growing", new PotionEffect[] { new PotionEffect(GROWING_POTION, 9600, 0) }).setRegistryName(new ResourceLocation(Lilliputian.MODID, "long_growing"));
	public static final PotionType STRONG_GROWING = new PotionType("growing", new PotionEffect[] { new PotionEffect(GROWING_POTION, 1800, 1) }).setRegistryName(new ResourceLocation(Lilliputian.MODID, "strong_growing"));

}
