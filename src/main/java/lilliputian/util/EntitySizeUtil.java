package lilliputian.util;

import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeProvider;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySizeUtil {
	
	public static final float HARD_MIN = 0.25F;
	public static final float HARD_MAX = 4F;
	
	public static float getEntityScale(Entity entity) {
		if (entity.hasCapability(SizeProvider.sizeCapability, null)) {
			ISizeCapability size = entity.getCapability(SizeProvider.sizeCapability, null);

			return size.getScale();
		}
		return 1F;
	}

}
