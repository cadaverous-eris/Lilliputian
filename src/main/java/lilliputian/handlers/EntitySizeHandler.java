package lilliputian.handlers;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import lilliputian.Config;
import lilliputian.Lilliputian;
import lilliputian.ai.EntityAIHuntTinyCreatures;
import lilliputian.ai.EntityAINewOcelotFear;
import lilliputian.capabilities.DefaultSizeCapability;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeProvider;
import lilliputian.network.MessageSizeChange;
import lilliputian.network.PacketHandler;
import lilliputian.potions.PotionLilliputian;
import lilliputian.util.EntitySizeUtil;
import net.minecraft.block.BlockDoublePlant;
import net.minecraft.block.BlockDoublePlant.EnumBlockHalf;
import net.minecraft.block.BlockDoublePlant.EnumPlantType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.monster.EntityShulker;
import net.minecraft.entity.passive.EntityOcelot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayer.SleepResult;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityMountEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@EventBusSubscriber(modid = Lilliputian.MODID)
public class EntitySizeHandler {

	private static final Map<Entity, Tuple<Float, Float>> entitySizeCache = new HashMap<Entity, Tuple<Float, Float>>();
	private static final List<EntityPlayer> initializedPlayers = new ArrayList<EntityPlayer>();

	private static final Map<Entity, Integer> shrinkingAmps = new HashMap<Entity, Integer>();
	private static final Map<Entity, Integer> growingAmps = new HashMap<Entity, Integer>();

	public static final float defaultWidth = 0.6F;
	public static final float defaultHeight = 1.8F;

	public static final AttributeModifier SPEED_MODIFIER = new AttributeModifier(
			UUID.fromString("1E7E2380-2E87-45B6-A90C-869563A27FA3"), "size_speed_mod", 1 - 1, 1);
	public static final AttributeModifier ATTACK_SPEED_MODIFIER = new AttributeModifier(
			UUID.fromString("174DEEAF-A876-4666-BFEB-709960E09021"), "size_attack_speed_mod", 1 - 1, 1);

	@SubscribeEvent
	public static void onAddCapabilites(AttachCapabilitiesEvent event) {
		if ((event.getObject() instanceof EntityPlayer || event.getObject() instanceof EntityLivingBase)) {
			for (Class entityClass : Config.RESIZING_BLACKLIST) {
				if (entityClass == (event.getObject().getClass())) {
					return;
				}
			}
			EntityLivingBase entity = (EntityLivingBase) event.getObject();
			if (entity.isNonBoss() && !entity.hasCapability(SizeProvider.sizeCapability, null)) {
				float baseSize = 1F;
				if (Config.ENTITY_BASESIZES.containsKey(entity.getClass())) {
					baseSize = Config.ENTITY_BASESIZES.get(entity.getClass()).randomBaseSize(entity.getRNG());
				}
				if (entity instanceof EntityPlayer) {
					baseSize = Config.PLAYER_BASESIZE.randomBaseSize(entity.getRNG());
				}
				ISizeCapability cap = new DefaultSizeCapability(baseSize);
				event.addCapability(new ResourceLocation(Lilliputian.MODID, "size"), new SizeProvider(cap));
			}
		}
	}

	@SubscribeEvent
	public static void onLivingUpdate(LivingUpdateEvent event) {
		if (event.getEntityLiving() != null) {
			EntityLivingBase entity = event.getEntityLiving();
			if (entity.hasCapability(SizeProvider.sizeCapability, null)) {
				ISizeCapability size = entity.getCapability(SizeProvider.sizeCapability, null);

				if (!entity.world.isRemote) {
					if ((entity instanceof EntityPlayer && !initializedPlayers.contains(entity))
							|| (!(entity instanceof EntityPlayer) && !entitySizeCache.containsKey(entity))) {
						PacketHandler.INSTANCE.sendToAll(new MessageSizeChange(size.getBaseSize(), size.getScale(),
								entity.getEntityId(), false));
					} else if (entity.ticksExisted % 40 == 0) {
						PacketHandler.INSTANCE.sendToAll(
								new MessageSizeChange(size.getBaseSize(), size.getScale(), entity.getEntityId()));
					}
					updateSizePotionEffects(entity, size);
				}

				if (size.getMorphTime() > 0 && size.getMaxMorphTime() > 0) {
					if (size.getActualScaleNoClamp() != size.getScale()) {
						int t = size.getMorphTime();
						size.incrementMorphTime();
						int m = size.getMaxMorphTime();
						float d = (size.getScale() - size.getActualScale()) / (t);

						size.setActualScale(size.getActualScale() + (d / (1)));
					}
				} else if (size.getActualScaleNoClamp() != size.getScale()) {
					size.setActualScale(size.getScale());
				}

				if (!(entity instanceof EntityPlayer)) {
					if (!entitySizeCache.containsKey(entity)) {
						entitySizeCache.put(entity, new Tuple(entity.width, entity.height));
					}
					Tuple<Float, Float> dims = entitySizeCache.get(entity);
					float width = dims.getFirst() * size.getActualSize();
					float height = dims.getSecond() * size.getActualSize();
					setEntitySize(entity, width, height);
				} else {
					if (!initializedPlayers.contains(entity)) {
						initializedPlayers.add((EntityPlayer) entity);
					}
					if (entity.getRidingEntity() != null) {
						setEntitySize(entity, 0.6F * size.getActualSize(), 1.8F * size.getActualSize());

						Entity mount = entity.getRidingEntity();
						if (EntitySizeUtil.getEntityScale(mount) < size.getActualSize()) {
							entity.dismountRidingEntity();
						}
					}
				}

				if (entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null) {
					IAttributeInstance speedAttribute = entity
							.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
					double speedMod = Math.pow(size.getActualSize(), 0.25) - 1;
					speedAttribute.removeModifier(SPEED_MODIFIER.getID());
					speedAttribute.applyModifier(new AttributeModifier(SPEED_MODIFIER.getID(), SPEED_MODIFIER.getName(),
							speedMod, SPEED_MODIFIER.getOperation()).setSaved(false));
				}
				if (entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED) != null) {
					IAttributeInstance speedAttribute = entity.getEntityAttribute(SharedMonsterAttributes.ATTACK_SPEED);
					double speedMod = (Math.pow(size.getActualSize(), -0.25)) - 1;
					speedAttribute.removeModifier(ATTACK_SPEED_MODIFIER.getID());
					speedAttribute.applyModifier(
							new AttributeModifier(ATTACK_SPEED_MODIFIER.getID(), ATTACK_SPEED_MODIFIER.getName(),
									speedMod, ATTACK_SPEED_MODIFIER.getOperation()).setSaved(false));
				}

				if (entity instanceof EntityPlayer) {
					float actualSize = size.getActualSize();
					entity.stepHeight = (actualSize < 0.6F) ? actualSize : (actualSize > 1F) ? actualSize * 0.6F : 0.6F;
				}

				if (entity.isElytraFlying() && (entity.rotationPitch > 45 && entity.rotationPitch < 10)
						&& size.getActualSize() <= EntitySizeUtil.TINY_THRESHOLD) {
					entity.motionY += 0.1F;
				}

				if (!entity.world.isRemote && (size.getActualSize() <= EntitySizeUtil.TINY_THRESHOLD
						|| size.getActualSize() >= EntitySizeUtil.HUGE_THRESHOLD)) {
					if (entity.getArmorInventoryList() instanceof NonNullList) {
						NonNullList<ItemStack> armorInv = (NonNullList) entity.getArmorInventoryList();
						for (int i = 0; i < armorInv.size(); i++) {
							if (!armorInv.get(i).isEmpty()) {
								entity.entityDropItem(armorInv.get(i).copy(),
										(entity.height * 0.125F) + (entity.height * 0.25F));
								armorInv.set(i, ItemStack.EMPTY);
							}
						}
					}
				}

				if (size.getActualSize() <= EntitySizeUtil.TINY_THRESHOLD) {
					AxisAlignedBB aabb = entity.getEntityBoundingBox();
					if (aabb != null) {
						int minX = MathHelper.floor(aabb.minX);
						int maxX = MathHelper.ceil(aabb.maxX);
						int minY = MathHelper.floor(aabb.minY);
						int maxY = MathHelper.ceil(aabb.maxY);
						int minZ = MathHelper.floor(aabb.minZ);
						int maxZ = MathHelper.ceil(aabb.maxZ);

						for (int x = minX; x < maxX; x++) {
							for (int y = minY; y < maxY && y >= 0 && y < 256; y++) {
								for (int z = minZ; z < maxZ; z++) {
									BlockPos pos = new BlockPos(x, y, z);
									IBlockState state = entity.world.getBlockState(pos);
									if (state.getBlock() == Blocks.DOUBLE_PLANT
											&& (state.getValue(BlockDoublePlant.VARIANT) == EnumPlantType.ROSE
													|| (state.getValue(BlockDoublePlant.HALF) == EnumBlockHalf.UPPER
															&& y > 0
															&& entity.world.getBlockState(pos.down())
																	.getBlock() == Blocks.DOUBLE_PLANT
															&& entity.world.getBlockState(pos.down()).getValue(
																	BlockDoublePlant.VARIANT) == EnumPlantType.ROSE))) {
										entity.attackEntityFrom(new DamageSource("rose"), 1.0F);
										break;
									}
								}
							}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void playerJoinWorld(EntityJoinWorldEvent event) {
		if (event.getEntity().world != null && !event.getEntity().world.isRemote
				&& event.getEntity().hasCapability(SizeProvider.sizeCapability, null)) {
			ISizeCapability size = event.getEntity().getCapability(SizeProvider.sizeCapability, null);
			PacketHandler.INSTANCE.sendToAll(
					new MessageSizeChange(size.getBaseSize(), size.getScale(), event.getEntity().getEntityId(), false));
		}
	}

	@SubscribeEvent
	public static void ocelotInit(EntityJoinWorldEvent event) {
		if (event.getEntity() instanceof EntityOcelot) {
			EntityOcelot ocelot = (EntityOcelot) event.getEntity();
			Field avoidAiField = ReflectionHelper.findField(EntityOcelot.class,
					new String[] { "avoidEntity", "field_175545_bm" });

			try {
				ocelot.tasks.removeTask((EntityAIBase) avoidAiField.get(ocelot));
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

			ocelot.tasks.addTask(4, new EntityAINewOcelotFear(ocelot, EntityPlayer.class, 16.0F, 0.8D, 1.33D));
			ocelot.targetTasks.addTask(2, new EntityAIHuntTinyCreatures(ocelot));
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

		Iterator<EntityPlayer> pIter = initializedPlayers.iterator();
		while (pIter.hasNext()) {
			EntityPlayer p = pIter.next();
			if (p == null || p.world.equals(event.getWorld())) {
				pIter.remove();
			}
		}

		Iterator<Entity> sIter = shrinkingAmps.keySet().iterator();
		while (sIter.hasNext()) {
			Entity e = sIter.next();
			if (e == null || e.world.equals(event.getWorld())) {
				sIter.remove();
			}
		}
		Iterator<Entity> gIter = growingAmps.keySet().iterator();
		while (gIter.hasNext()) {
			Entity e = gIter.next();
			if (e == null || e.world.equals(event.getWorld())) {
				gIter.remove();
			}
		}
	}

	@SubscribeEvent
	public static void playerClone(PlayerEvent.Clone event) {
		if (event.isWasDeath() && event.getOriginal().hasCapability(SizeProvider.sizeCapability, null)
				&& event.getEntityPlayer().hasCapability(SizeProvider.sizeCapability, null)) {
			ISizeCapability originalSize = event.getOriginal().getCapability(SizeProvider.sizeCapability, null);
			ISizeCapability size = event.getEntityPlayer().getCapability(SizeProvider.sizeCapability, null);

			size.setBaseSize(originalSize.getBaseSize());
			initializedPlayers.remove(event.getOriginal());
			initializedPlayers.remove(event.getEntityPlayer());
		}
	}

	@SubscribeEvent
	public static void onEntityJump(LivingEvent.LivingJumpEvent event) {
		if (!event.getEntityLiving().isSneaking() && EntitySizeUtil.getEntityScale(event.getEntityLiving()) > 1) {
			event.getEntityLiving().motionY *= MathHelper
					.sqrt(Math.max(EntitySizeUtil.getEntityScale(event.getEntityLiving()), 1));
			event.getEntityLiving().velocityChanged = true;
		}
	}

	@SubscribeEvent
	public static void breakSpeed(PlayerEvent.BreakSpeed event) {
		event.setNewSpeed(
				event.getNewSpeed() * MathHelper.sqrt(EntitySizeUtil.getEntityScale(event.getEntityPlayer())));
	}

	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public static void pushOutOfBlock(PlayerSPPushOutOfBlocksEvent event) {
		if (EntitySizeUtil.getEntityScale(event.getEntityPlayer()) < 1) {
			event.setCanceled(true);
			EntityPlayer player = event.getEntityPlayer();
			AxisAlignedBB axisalignedbb = event.getEntityBoundingBox();
			
			pushPlayerSPOutOfBlocks(player, player.posX - (double) player.width * 0.35D, axisalignedbb.minY + Math.max(0.125D, (0.5D * EntitySizeUtil.getEntityScaleDoubleMin1(player))), player.posZ + (double) player.width * 0.35D);
			pushPlayerSPOutOfBlocks(player, player.posX - (double) player.width * 0.35D, axisalignedbb.minY + Math.max(0.125D, (0.5D * EntitySizeUtil.getEntityScaleDoubleMin1(player))), player.posZ - (double) player.width * 0.35D);
			pushPlayerSPOutOfBlocks(player, player.posX + (double) player.width * 0.35D, axisalignedbb.minY + Math.max(0.125D, (0.5D * EntitySizeUtil.getEntityScaleDoubleMin1(player))), player.posZ - (double) player.width * 0.35D);
			pushPlayerSPOutOfBlocks(player, player.posX + (double) player.width * 0.35D, axisalignedbb.minY + Math.max(0.125D, (0.5D * EntitySizeUtil.getEntityScaleDoubleMin1(player))), player.posZ + (double) player.width * 0.35D);
		}
	}

	/*
	 * @SubscribeEvent public static void
	 * playerVisibility(PlayerEvent.Visibility event) {
	 * event.modifyVisibility(EntitySizeUtil.getEntityScaleRootDouble(event.
	 * getEntityPlayer())); }
	 */

	@SubscribeEvent
	public static void trySleep(PlayerSleepInBedEvent event) {
		if (event.getEntityPlayer() != null) {
			if (EntitySizeUtil.getEntityScale(event.getEntityPlayer()) > 1.25F) {
				event.setResult(SleepResult.OTHER_PROBLEM);
				event.getEntityPlayer().sendStatusMessage(new TextComponentTranslation("tile.bed.tooBig"), true);
			}
		}
	}

	@SubscribeEvent
	public static void onEntityHurt(LivingHurtEvent event) {
		EntityLivingBase entity = event.getEntityLiving();
		float size = EntitySizeUtil.getEntityScale(entity);

		if (event.getSource() == DamageSource.FALL) {
			event.setAmount(event.getAmount() / Math.min(MathHelper.sqrt(size), size));
		} else {
			event.setAmount(event.getAmount() / MathHelper.sqrt(size));
		}

		if (event.getSource().getImmediateSource() != null) {
			float attackerSize = EntitySizeUtil.getEntityScale(event.getSource().getImmediateSource());

			event.setAmount(event.getAmount() / MathHelper.sqrt(size));
		}
	}

	@SubscribeEvent
	public static void entityInteract(PlayerInteractEvent.EntityInteract event) {
		if (event.getTarget() != null
				&& EntitySizeUtil.getEntityScale(event.getEntityPlayer()) <= EntitySizeUtil.TINY_THRESHOLD) {
			EntityPlayer player = event.getEntityPlayer();
			Entity target = event.getTarget();

			if (target.isNonBoss() && !player.getHeldItem(event.getHand()).isEmpty()
					&& player.getHeldItem(event.getHand()).getItem() == Items.STRING) {
				if (player.startRiding(target)) {
					event.setCancellationResult(EnumActionResult.SUCCESS);
				}
			}
		}
	}

	@SubscribeEvent
	public static void entityMount(EntityMountEvent event) {
		if (event.getEntityMounting() != null && event.getEntityBeingMounted() != null && event.isMounting()) {
			Entity entityMounting = event.getEntityMounting();
			float mountingSize = EntitySizeUtil.getEntityScale(entityMounting);
			Entity entityMounted = event.getEntityBeingMounted();
			float mountedSize = EntitySizeUtil.getEntityScale(entityMounted);

			if (mountingSize > mountedSize) {
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

	private static void setEntitySize(Entity entity, float width, float height) {
		if (width != entity.width || height != entity.height) {
			float f = entity.width;
			float f1 = entity.height;
			entity.width = width;
			entity.height = height;

			if (entity.width < f) {
				AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
				if (!entity.world.isRemote) {
					entity.posY += (f1 - entity.height) / 2F;
				}
				double d0 = (double) width / 2.0D;
				entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0,
						entity.posX + d0, entity.posY + (double) entity.height, entity.posZ + d0));
				return;
			}

			AxisAlignedBB axisalignedbb = entity.getEntityBoundingBox();
			// entity.setEntityBoundingBox(new AxisAlignedBB(axisalignedbb.minX,
			// axisalignedbb.minY, axisalignedbb.minZ, axisalignedbb.minX +
			// (double) entity.width, axisalignedbb.minY + (double)
			// entity.height, axisalignedbb.minZ + (double) entity.width));
			double d0 = (double) width / 2.0D;
			entity.setEntityBoundingBox(new AxisAlignedBB(entity.posX - d0, entity.posY, entity.posZ - d0,
					entity.posX + d0, entity.posY + (double) entity.height, entity.posZ + d0));
		}
	}

	private static void updateSizePotionEffects(EntityLivingBase entity, ISizeCapability size) {
		int prevShrinkingAmp = shrinkingAmps.containsKey(entity) ? shrinkingAmps.get(entity)
				: (entity.getActivePotionEffect(PotionLilliputian.SHRINKING_POTION) != null)
						? entity.getActivePotionEffect(PotionLilliputian.SHRINKING_POTION).getAmplifier() : -1;
		int prevGrowingAmp = growingAmps.containsKey(entity) ? growingAmps.get(entity)
				: (entity.getActivePotionEffect(PotionLilliputian.GROWING_POTION) != null)
						? entity.getActivePotionEffect(PotionLilliputian.GROWING_POTION).getAmplifier() : -1;
		int shrinkingAmp = (entity.getActivePotionEffect(PotionLilliputian.SHRINKING_POTION) != null)
				? entity.getActivePotionEffect(PotionLilliputian.SHRINKING_POTION).getAmplifier() : -1;
		int growingAmp = (entity.getActivePotionEffect(PotionLilliputian.GROWING_POTION) != null)
				? entity.getActivePotionEffect(PotionLilliputian.GROWING_POTION).getAmplifier() : -1;

		if (shrinkingAmp > -1 && growingAmp > -1
				&& (prevShrinkingAmp != shrinkingAmp || prevGrowingAmp != growingAmp)) {
			if (shrinkingAmp == growingAmp) {
				entity.removePotionEffect(PotionLilliputian.GROWING_POTION);
				entity.removePotionEffect(PotionLilliputian.SHRINKING_POTION);
			} else if (shrinkingAmp > growingAmp) {
				entity.removePotionEffect(PotionLilliputian.GROWING_POTION);
				PotionEffect shrinking = entity.getActivePotionEffect(PotionLilliputian.SHRINKING_POTION);
				int duration = shrinking.getDuration();
				boolean ambient = shrinking.getIsAmbient();
				boolean particles = shrinking.doesShowParticles();
				entity.removePotionEffect(PotionLilliputian.SHRINKING_POTION);
				entity.addPotionEffect(new PotionEffect(PotionLilliputian.SHRINKING_POTION, duration,
						(shrinkingAmp + 1) - (growingAmp + 1) - 1, ambient, particles));
			} else if (shrinkingAmp < growingAmp) {
				entity.removePotionEffect(PotionLilliputian.SHRINKING_POTION);
				PotionEffect growing = entity.getActivePotionEffect(PotionLilliputian.GROWING_POTION);
				int duration = growing.getDuration();
				boolean ambient = growing.getIsAmbient();
				boolean particles = growing.doesShowParticles();
				entity.removePotionEffect(PotionLilliputian.GROWING_POTION);
				entity.addPotionEffect(new PotionEffect(PotionLilliputian.GROWING_POTION, duration,
						(growingAmp + 1) - (shrinkingAmp + 1) - 1, ambient, particles));
			}
		}
		shrinkingAmp = (entity.getActivePotionEffect(PotionLilliputian.SHRINKING_POTION) != null)
				? entity.getActivePotionEffect(PotionLilliputian.SHRINKING_POTION).getAmplifier() : -1;
		growingAmp = (entity.getActivePotionEffect(PotionLilliputian.GROWING_POTION) != null)
				? entity.getActivePotionEffect(PotionLilliputian.GROWING_POTION).getAmplifier() : -1;

		if (prevShrinkingAmp != shrinkingAmp || prevGrowingAmp != growingAmp) {
			float potionScaling = 1F;
			float prevPotionScaling = 1F;
			if (shrinkingAmp > -1) {
				potionScaling /= Math.max((shrinkingAmp + 1) * 2, EntitySizeUtil.HARD_MIN / size.getBaseSize());
			}
			if (growingAmp > -1) {
				potionScaling *= Math.min((growingAmp + 1) * 2, EntitySizeUtil.HARD_MAX / size.getBaseSize());
			}
			if (prevShrinkingAmp > -1) {
				prevPotionScaling /= Math.max((prevShrinkingAmp + 1) * 2, EntitySizeUtil.HARD_MIN / size.getBaseSize());
			}
			if (prevGrowingAmp > -1) {
				prevPotionScaling *= Math.min((prevGrowingAmp + 1) * 2, EntitySizeUtil.HARD_MAX / size.getBaseSize());
			}

			// System.out.println((potionScaling + " / " + prevPotionScaling));
			size.setScale(size.getScale() * (potionScaling / prevPotionScaling));
			PacketHandler.INSTANCE
					.sendToAll(new MessageSizeChange(size.getBaseSize(), size.getScale(), entity.getEntityId()));
		}

		if (entity.isDead) {
			shrinkingAmps.put(entity, -1);
			growingAmps.put(entity, -1);
		} else {
			shrinkingAmps.put(entity, shrinkingAmp);
			growingAmps.put(entity, growingAmp);
		}
	}

	private static void pushPlayerSPOutOfBlocks(EntityPlayer player, double x, double y, double z) {
		if (player.noClip) {
			return;
		} else {
			BlockPos blockpos = new BlockPos(x, y, z);
			double d0 = x - (double) blockpos.getX();
			double d1 = z - (double) blockpos.getZ();

			int entHeight = Math.max((int) Math.ceil(player.height), 1);

			boolean inTranslucentBlock = !isHeadspaceFree(player.world, blockpos, entHeight);

			if (inTranslucentBlock) {
				int i = -1;
				double d2 = 9999.0D;

				if (isHeadspaceFree(player.world, blockpos.west(), entHeight) && d0 < d2) {
					d2 = d0;
					i = 0;
				}

				if (isHeadspaceFree(player.world, blockpos.east(), entHeight) && 1.0D - d0 < d2) {
					d2 = 1.0D - d0;
					i = 1;
				}

				if (isHeadspaceFree(player.world, blockpos.north(), entHeight) && d1 < d2) {
					d2 = d1;
					i = 4;
				}

				if (isHeadspaceFree(player.world, blockpos.south(), entHeight) && 1.0D - d1 < d2) {
					d2 = 1.0D - d1;
					i = 5;
				}

				float f = 0.1F;

				if (i == 0) {
					player.motionX = -0.10000000149011612D;
				}

				if (i == 1) {
					player.motionX = 0.10000000149011612D;
				}

				if (i == 4) {
					player.motionZ = -0.10000000149011612D;
				}

				if (i == 5) {
					player.motionZ = 0.10000000149011612D;
				}
			}
		}
	}

	private static boolean isHeadspaceFree(World world, BlockPos pos, int height) {
		for (int y = 0; y < height; y++) {
			if (!isOpenBlockSpace(world, pos.add(0, y, 0))) {
				return false;
			}
		}
		return true;
	}

	private static boolean isOpenBlockSpace(World world, BlockPos pos) {
		IBlockState iblockstate = world.getBlockState(pos);
		return !iblockstate.getBlock().isNormalCube(iblockstate, world, pos);
	}

}
