package lilliputian.util;

import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeProvider;
import lilliputian.handlers.EntitySizeHandler;
import lilliputian.handlers.RenderEntityHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySizeUtil {
	
	public static final float HARD_MIN = 0.125F;
	public static final float HARD_MAX = 8F;
	
	public static final float TINY_THRESHOLD = 0.33333F;
	public static final float HUGE_THRESHOLD = 3F;
	
	public static float getEntityScale(Entity entity) {
		if (entity.hasCapability(SizeProvider.sizeCapability, null)) {
			ISizeCapability size = entity.getCapability(SizeProvider.sizeCapability, null);
				
			return size.getActualSize();
		}
		return 1F;
	}
	
	public static float getEntityScaleRoot(Entity entity) {
		return MathHelper.sqrt(getEntityScale(entity));
	}
	
	public static double getEntityScaleDouble(Entity entity) {
		return (double) getEntityScale(entity);
	}
	
	public static double getEntityScaleRootDouble(Entity entity) {
		return MathHelper.sqrt((double) getEntityScale(entity));
	}
	
	public static double getEntityScaleDoubleMin1(Entity entity) {
		return Math.min((double) getEntityScale(entity), 1D);
	}
	
	public static double getEntityScaleDoubleMax1(Entity entity) {
		return Math.max((double) getEntityScale(entity), 1D);
	}
	
	public static double getInverse(double d) {
		return 1D / d;
	}
	
	public static double getEntityYOffset(double origOffset, Entity entity) {
		double scale = getEntityScaleDouble(entity);
		double riddenScale = (entity.getRidingEntity() != null) ? getEntityScale(entity.getRidingEntity()) : 1D;
		//return (origOffset) + (((0.2625D * (riddenScale)) - (origOffset)) * (1 - (scale))); // maths
		//return (origOffset) + ((0.5 * entity.height) / scale) - (0.5 * entity.height); // make middle of bounding box constant
		double d = ((0.23125D * riddenScale) - (origOffset));
		return (origOffset) + (((1 / scale) - (1)) * (d * (entity.height / 2))) + (((riddenScale - 1) * (0.225D)) * scale);
	}
	
	@SideOnly(Side.CLIENT)
	public static float getCameraNearPlane() {
		return 0.05F * Math.min(getEntityScale(Minecraft.getMinecraft().getRenderViewEntity()), 1);
	}
	
	@SideOnly(Side.CLIENT)
	public static float getViewEntityScale() {
		return getEntityScale(Minecraft.getMinecraft().getRenderViewEntity());
	}
	
	@SideOnly(Side.CLIENT)
	public static float getViewEntityScaleRoot() {
		return MathHelper.sqrt(getEntityScale(Minecraft.getMinecraft().getRenderViewEntity()));
	}
	
	@SideOnly(Side.CLIENT)
	public static double getViewEntityScaleRootDouble() {
		return MathHelper.sqrt((double) getEntityScale(Minecraft.getMinecraft().getRenderViewEntity()));
	}
	
	@SideOnly(Side.CLIENT)
	public static double getMaxReach() {
		return 3.0D * MathHelper.sqrt((double) getEntityScale(Minecraft.getMinecraft().getRenderViewEntity()));
	}
	
	@SideOnly(Side.CLIENT)
	public static double getExtendedReach() {
		return 6.0D * MathHelper.sqrt((double) getEntityScale(Minecraft.getMinecraft().getRenderViewEntity()));
	}
	
	public static void attemptCactusDamage(Entity entity) {
		if (getEntityScale(entity) > TINY_THRESHOLD && getEntityScale(entity) < HUGE_THRESHOLD) {
			entity.attackEntityFrom(DamageSource.CACTUS, 1.0F);
		}
	}

	@SideOnly(Side.CLIENT)
	public static void doRenderEntity(Entity entityIn, int x, int y, int z, float yaw, float partialTicks, boolean p_188391_10_){
		RenderManager rendermanager = Minecraft.getMinecraft().getRenderManager();
		float scale = 1/EntitySizeUtil.getEntityScale(entityIn);
		GlStateManager.scale(scale, scale, scale);
		rendermanager.renderEntity(entityIn,0.0,0.0,0.0,yaw,partialTicks,p_188391_10_);
		RenderEntityHandler.registerMultiplier(x,y,"x"+scale,z);
	}
	
	public static boolean isOnLadder(Entity entity) {
		if (entity instanceof EntityPlayer && getEntityScale(entity) <= TINY_THRESHOLD && entity.collidedHorizontally) {
			EntityPlayer player = (EntityPlayer) entity;
			return (player.getHeldItemMainhand().getItem() == Items.SLIME_BALL && player.getHeldItemOffhand().getItem() == Items.SLIME_BALL);
		}
		return false;
	}

}
