package lilliputian.handlers;

import lilliputian.Lilliputian;
import lilliputian.util.EntitySizeUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderPlayerEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;

@SideOnly(Side.CLIENT)
@EventBusSubscriber(value = Side.CLIENT, modid = Lilliputian.MODID)
public class RenderEntityHandler {
	public static ArrayList<Multiplier> cache = new ArrayList<>();

	@SubscribeEvent
	public static void renderEntityPre(RenderLivingEvent.Pre event) {
		float scale = EntitySizeUtil.getEntityScale(event.getEntity());

		GlStateManager.pushMatrix();

		GlStateManager.scale(scale, scale, scale);
		GlStateManager.translate((event.getX() / scale) - event.getX(), (event.getY() / scale) - event.getY(), (event.getZ() / scale) - event.getZ());
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

	@SubscribeEvent
	public static void setupCamera(EntityViewRenderEvent.CameraSetup event) {
		float scale = EntitySizeUtil.getEntityScale(event.getEntity());

		if (!(event.getEntity() instanceof EntityLivingBase
				&& ((EntityLivingBase) event.getEntity()).isPlayerSleeping())
				&& Minecraft.getMinecraft().gameSettings.thirdPersonView == 0) {
			GlStateManager.translate(0, 0, -0.05F);
			GlStateManager.translate(0, 0, (scale * 0.05F));
		}
	}

	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public static void renderGui(GuiScreenEvent.DrawScreenEvent.Post event) {
		GlStateManager.disableBlend();
		for(Multiplier m : cache){
			GlStateManager.pushMatrix();
			GlStateManager.scale(0.5,0.5,1);
			GlStateManager.translate(event.getGui().width/(50-m.scale),-(event.getGui().height/m.scale),0);
			Minecraft.getMinecraft().getRenderManager().getFontRenderer().drawString(m.string,m.x*2,m.y*2,553648127);
			GlStateManager.popMatrix();
		}
		cache.clear();
	}

	@SideOnly(Side.CLIENT)
	public static void registerMultiplier(int x, int y, String string,int scale){
		cache.add(new Multiplier(x,y,string,scale));
	}

	private static class Multiplier {
		public int x;
		public int y;
		public String string;
		public int scale;

		public Multiplier(int x, int y, String string,int scale) {
			this.x=x;
			this.y=y;
			this.string=string;
			this.scale=scale;
		}
	}

}
