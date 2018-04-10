package lilliputian.handlers;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import lilliputian.Config;
import lilliputian.Lilliputian;
import lilliputian.capabilities.DefaultSizeCapability;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeProvider;
import lilliputian.network.MessageSizeChange;
import lilliputian.network.PacketHandler;
import lilliputian.util.EntitySizeUtil;

@EventBusSubscriber(modid = Lilliputian.MODID)
public class EntitySizeHandler {
	
	private static final Map<Entity, Tuple<Float, Float>> entitySizeCache = new HashMap<Entity, Tuple<Float, Float>>();
	
	public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
			UUID.fromString("1E7E2380-2E87-45B6-A90C-869563A27FA3"), "size_speed_mod", 1 - 1, 1);
	public static final AttributeModifier ATTACK_DAMAGE_MODIFIER = new AttributeModifier(
			UUID.fromString("174DEEAF-A876-4666-BFEB-709960E09021"), "size_attack_damage_mod", 1 - 1, 1);
	
	@SubscribeEvent
	public static void onAddCapabilites(AttachCapabilitiesEvent event) {
		if (event.getObject() instanceof EntityLiving) {
			for (Class entityClass : Config.RESIZING_BLACKLIST) {
				if (entityClass == (event.getObject().getClass())) {
					return;
				}
			}
			EntityLiving entity = (EntityLiving) event.getObject();
			if (entity.isNonBoss() && !entity.hasCapability(SizeProvider.sizeCapability, null)) {
				float scale = 1F;
				if (Config.ENTITY_SIZES.containsKey(entity.getClass())) {
					scale = Config.ENTITY_SIZES.get(entity.getClass()).randomSize(entity.getRNG());
				}
				ISizeCapability cap = new DefaultSizeCapability(scale);
				event.addCapability(new ResourceLocation(Lilliputian.MODID, "size"), new SizeProvider(cap));
			}
		}
	}
	
	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null && event.getEntityLiving() instanceof EntityLiving) {
			EntityLiving entity = (EntityLiving) event.getEntityLiving();
			if (entity.hasCapability(SizeProvider.sizeCapability, null)) {
				ISizeCapability size = entity.getCapability(SizeProvider.sizeCapability, null);
				
				// Send a size update packet to all clients
				if (!entity.world.isRemote) {
					if (!entitySizeCache.containsKey(entity) || entity.ticksExisted % 200 == 2) {
						PacketHandler.INSTANCE.sendToAll(new MessageSizeChange(size.getScale(), entity.getEntityId()));
					}
				}
				
				// Cache initial entity size
				if (!entitySizeCache.containsKey(entity)) {
					if (entity.isChild()) {
						entitySizeCache.put(entity, new Tuple(entity.width * 2, entity.height * 2));
					} else {
						entitySizeCache.put(entity, new Tuple(entity.width, entity.height));
					}
					Tuple<Float, Float> dims = entitySizeCache.get(entity);
					float width = dims.getFirst() * size.getScale();
					float height = dims.getSecond() * size.getScale();
					setEntitySize(entity, width, height);
				}
				
				if (entity.ticksExisted % 200 == 2) {
					// Update the entity's hitbox
					Tuple<Float, Float> dims = entitySizeCache.get(entity);
					float width = dims.getFirst() * size.getScale();
					float height = dims.getSecond() * size.getScale();
					setEntitySize(entity, width, height);
					
					// Attribute Modifiers
					if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null) {
						IAttributeInstance speedAttribute = entity
								.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
						double speedMod = Math.pow(size.getScale(), 0.25) - 1;
						speedAttribute.removeModifier(SPEED_MODIFIER.getID());
						speedAttribute.applyModifier(new AttributeModifier(SPEED_MODIFIER.getID(), SPEED_MODIFIER.getName(),
								speedMod, SPEED_MODIFIER.getOperation()).setSaved(false));
					}
					if (entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE) != null) {
						IAttributeInstance damageAttribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE);
						double damageMod = (Math.pow(size.getScale(), 0.25)) - 1;
						damageAttribute.removeModifier(ATTACK_DAMAGE_MODIFIER.getID());
						damageAttribute.applyModifier(
								new AttributeModifier(ATTACK_DAMAGE_MODIFIER.getID(), ATTACK_DAMAGE_MODIFIER.getName(),
										damageMod, ATTACK_DAMAGE_MODIFIER.getOperation()).setSaved(false));
					}
				}
			}
		}
	}
	
	@SubscribeEvent
	public static void entityJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity().world != null && !event.getEntity().world.isRemote
				&& event.getEntity().hasCapability(SizeProvider.sizeCapability, null)) {
			ISizeCapability size = event.getEntity().getCapability(SizeProvider.sizeCapability, null);
			PacketHandler.INSTANCE.sendToAll(
					new MessageSizeChange(size.getScale(), event.getEntity().getEntityId()));
		}
	}
	
	@SubscribeEvent
	public static void worldUnload(WorldEvent.Unload event) {
		Iterator<Entity> eIter = entitySizeCache.keySet().iterator();
		while (eIter.hasNext()) {
			Entity e = eIter.next();
			if (e == null || e.world.equals(event.getWorld())) {
				eIter.remove();
			}
		}
	}
	
	@SubscribeEvent
	public static void entityMount(EntityMountEvent event) {
		if (event.getEntityMounting() != null && event.getEntityBeingMounted() != null && event.isMounting()) {
			Entity entityMounted = event.getEntityBeingMounted();

			if (EntitySizeUtil.getEntityScale(entityMounted) < 0.8) {
				event.setCanceled(true);
			}
		}
	}
	
	@SubscribeEvent
	public static void playSoundAtEntity(PlaySoundAtEntityEvent event) {
		if (event.getEntity() != null && event.getEntity().hasCapability(SizeProvider.sizeCapability, null)) {
			Entity entity = event.getEntity();
			float entitySize = EntitySizeUtil.getEntityScale(entity);

			event.setVolume(event.getVolume() * MathHelper.sqrt(entitySize));
			event.setPitch(event.getPitch() / entitySize);
		}
	}
	
	private static void setEntitySize(EntityLiving entity, float width, float height) {
		if (width != entity.width || height != entity.height) {
			float f = entity.width;
			float f1 = entity.height;
			if (entity.isChild()) {
				width *= 0.5f;
				height *= 0.5f;
			}
			entity.width = width;
			entity.height = height;

			if (entity.width < f) {
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
				if (!entity.world.isRemote) {
					entity.posY += (f1 - entity.height) / 2F;
				}
			}

			double d0 = (double) width / 2.0D;
			entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0,
					entity.posX + d0, entity.posY + (double) entity.height, entity.posZ + d0));
		}
	}

}
