package lilliputian.handlers;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.fml.relauncher.Side;

import lilliputian.Lilliputian;
import lilliputian.util.EntitySizeUtil;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT, modid = Lilliputian.MODID)
public class RenderEntityHandler {
	
	@SubscribeEvent
	public static void renderEntityPre(RenderLivingEvent.Pre event) {
		if (event.isCanceled()) {
			return;
		}

		float scale = EntitySizeUtil.getEntityScale(event.getEntity());

		GlStateManager.pushMatrix();

		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate((event.getX() / scale) - event.getX(), (event.getY() / scale) - event.getY(),
				(event.getZ() / scale) - event.getZ());
		if (event.getEntity().isSneaking()) {
			GlStateManager.translate(0, 0.125F / scale, 0);
			GlStateManager.translate(0, -0.125F, 0);
		}
	}

	@SubscribeEvent
	public static void renderEntityPost(RenderLivingEvent.Post event) {
		GlStateManager.popMatrix();
	}

	@SubscribeEvent
	public static void renderEntityNamePre(RenderLivingEvent.Specials.Pre event) {
		if (event.isCanceled()) {
			return;
		}

		float scale = EntitySizeUtil.getEntityScale(event.getEntity());

		GlStateManager.pushMatrix();

		boolean flag = event.getEntity().isSneaking();
		float vanillaOffset = event.getEntity().height + 0.5F - (flag ? 0.25F : 0.0F);

		GlStateManager.translate(0, -vanillaOffset, 0);

		float adjustedOffset = (event.getEntity().height / scale) + (0.5F) - (flag ? 0.25F : 0F);

		GlStateManager.translate(0, adjustedOffset, 0);
	}

	@SubscribeEvent
	public static void renderEntityNamePost(RenderLivingEvent.Specials.Post event) {
		GlStateManager.popMatrix();
	}

}
