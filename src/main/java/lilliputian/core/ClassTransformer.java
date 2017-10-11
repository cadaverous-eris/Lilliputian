package lilliputian.core;

import java.util.ArrayList;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldInsnNode;
import org.objectweb.asm.tree.FrameNode;
import org.objectweb.asm.tree.InsnList;
import org.objectweb.asm.tree.InsnNode;
import org.objectweb.asm.tree.LdcInsnNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;
import org.objectweb.asm.tree.VarInsnNode;

import net.minecraft.client.Minecraft;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClassTransformer implements IClassTransformer {

	@Override
	public byte[] transform(String name, String transformedName, byte[] basicClass) {
		if (transformedName.equals("net.minecraft.client.renderer.EntityRenderer")) {
			return patchEntityRendererASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.equals("net.minecraft.client.renderer.entity.Render")) {
			return patchAbstractRenderASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.equals("net.minecraft.entity.player.EntityPlayer")) {
			return patchEntityPlayerASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.equals("net.minecraft.entity.Entity")) {
			return patchEntityASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.equals("net.minecraft.entity.EntityLivingBase")) {
			return patchEntityLivingBaseASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.equals("net.minecraft.client.multiplayer.PlayerControllerMP")) {
			return patchPlayerControllerMPASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.equals("net.minecraft.server.management.PlayerInteractionManager")) {
			return patchPlayerInteractionManagerASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.equals("net.minecraft.block.BlockCactus")) {
			return patchBlockCactusASM(name, basicClass, !name.equals(transformedName));
		} else if (transformedName.contains("Entity") && !transformedName.contains("minecraftforge")) {
			return patchGenericEntityASM(name, basicClass, !name.equals(transformedName));
		}
		return basicClass;
	}

	@SideOnly(Side.CLIENT)
	public byte[] patchEntityRendererASM(String name, byte[] bytes, boolean obfuscated) {
		String setupCameraTransform = "";
		String renderHand = "";
		String renderWorldPass = "";
		String renderCloudsCheck = "";
		String applyBobbing = "";
		String orientCamera = "";
		String updateRenderer = "";
		String getMouseOver = "";

		String extendedReach = "";
		
		String entityName = "";

		if (obfuscated) {
			setupCameraTransform = "func_78479_a";
			renderHand = "func_78476_b";
			renderWorldPass = "func_175068_a";
			renderCloudsCheck = "func_180437_a";
			applyBobbing = "func_78475_f";
			orientCamera = "func_78467_g";
			updateRenderer = "func_78464_a";
			getMouseOver = "func_78473_a";

			extendedReach = "func_78749_i";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		} else {
			setupCameraTransform = "setupCameraTransform";
			renderHand = "renderHand";
			renderWorldPass = "renderWorldPass";
			renderCloudsCheck = "renderCloudsCheck";
			applyBobbing = "applyBobbing";
			orientCamera = "orientCamera";
			updateRenderer = "updateRenderer";
			getMouseOver = "getMouseOver";
			
			extendedReach = "extendedReach";

			entityName = "Lnet/minecraft/entity/Entity;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(setupCameraTransform)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getCameraNearPlane", "()F", false);
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof LdcInsnNode) {
						LdcInsnNode lin = (LdcInsnNode) code.get(i);
						if (lin.cst instanceof Float && ((Float) lin.cst).floatValue() == 0.05F) {
							code.set(lin, method);
						}
					}
				}
				//code.set(code.get(86), method);
			} else if (m.name.equals(renderHand)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getCameraNearPlane", "()F", false);
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof LdcInsnNode) {
						LdcInsnNode lin = (LdcInsnNode) code.get(i);
						if (lin.cst instanceof Float && ((Float) lin.cst).floatValue() == 0.05F) {
							code.set(lin, method);
						}
					}
				}
				//code.set(code.get(53), method);
			} else if (m.name.equals(renderWorldPass)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getCameraNearPlane", "()F", false);
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof LdcInsnNode) {
						LdcInsnNode lin = (LdcInsnNode) code.get(i);
						if (lin.cst instanceof Float && ((Float) lin.cst).floatValue() == 0.05F) {
							code.set(lin, method);
						}
					}
				}
				//code.set(code.get(201), method);
				//code.set(code.get(239), method2);
			} else if (m.name.equals(renderCloudsCheck)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getCameraNearPlane", "()F", false);
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof LdcInsnNode) {
						LdcInsnNode lin = (LdcInsnNode) code.get(i);
						if (lin.cst instanceof Float && ((Float) lin.cst).floatValue() == 0.05F) {
							code.set(lin, method);
						}
					}
				}
				//code.set(code.get(36), method);
				//code.set(code.get(92), method2);
			} else if (m.name.equals(applyBobbing)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getViewEntityScale", "()F", false);
				MethodInsnNode method2 = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getViewEntityScale", "()F", false);
				MethodInsnNode method3 = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getViewEntityScaleRoot", "()F", false);

				code.insert(code.get(73), method);
				code.insert(code.get(74), new InsnNode(Opcodes.FMUL));
				code.insert(code.get(67), method2);
				code.insert(code.get(68), new InsnNode(Opcodes.FMUL));
				
				code.insert(code.get(29), method3);
				code.insert(code.get(30), new InsnNode(Opcodes.FDIV));
			} else if (m.name.equals(orientCamera)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getViewEntityScale", "()F", false);
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof LdcInsnNode) {
						LdcInsnNode lin = (LdcInsnNode) code.get(i);
						if (lin.cst instanceof Float && ((Float) lin.cst).floatValue() == 4.0F) {
							code.insert(code.get(i), method);
							code.insert(code.get(i + 1), new InsnNode(Opcodes.FMUL));
						}
					}
				}
				//code.insert(code.get(159), method);
				//code.insert(code.get(160), new InsnNode(Opcodes.FMUL));
			} else if (m.name.equals(updateRenderer)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getViewEntityScale", "()F", false);

				code.insert(code.get(27), method);
				code.insert(code.get(28), new InsnNode(Opcodes.FMUL));
			}  else if (m.name.equals(getMouseOver)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getMaxReach", "()D", false);
				MethodInsnNode method2 = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getExtendedReach", "()D", false);
				boolean notFirst = false;
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof LdcInsnNode) {
						LdcInsnNode lin = (LdcInsnNode) code.get(i);
						if (lin.cst instanceof Double && ((Double) lin.cst).doubleValue() == 3.0D) {
							if (notFirst) {
								code.set(lin, method);
							} else {
								notFirst = true;
							}
						} else if (lin.cst instanceof Double && ((Double) lin.cst).doubleValue() == 6.0D) {
							code.set(lin, method2);
						}
					}
				}
				//code.insert(code.get(330), method2);
				//code.insert(code.get(331), new InsnNode(Opcodes.DMUL));
				//code.insert(code.get(73), method);
				//code.insert(code.get(74), new InsnNode(Opcodes.DMUL));
			}
		}

		ClassWriter writer = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	@SideOnly(Side.CLIENT)
	public byte[] patchAbstractRenderASM(String name, byte[] bytes, boolean obfuscated) {
		String renderShadow = "";
		
		String shadowSize = "";

		String entityName = "";
		
		if (obfuscated) {
			renderShadow = "func_76975_c";
			
			shadowSize = "field_76989_e";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		} else {
			renderShadow = "renderShadow";
			
			shadowSize = "shadowSize";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(renderShadow)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScale", "(" + entityName + ")F", false);
				/*int i = 0;
				for (AbstractInsnNode n : code.toArray()) {
					System.out.println(i + " -> " + n.getOpcode() + " " + n.getType());
					i++;
				}*/
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof FieldInsnNode) {
						FieldInsnNode fin = (FieldInsnNode) code.get(i);
						if (fin.getOpcode() == Opcodes.GETFIELD && fin.name.equals(shadowSize) && fin.desc.equals("F")) {
							code.insert(code.get(i), new VarInsnNode(Opcodes.ALOAD, 1));
							code.insert(code.get(i + 1), method);
							code.insert(code.get(i + 2), new InsnNode(Opcodes.FMUL));
						}
					}
				}
				//code.insertBefore(code.get(26), new VarInsnNode(Opcodes.ALOAD, 1));
				//code.insertBefore(code.get(27), method);
				//code.insert(code.get(29), new InsnNode(Opcodes.FMUL));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	public byte[] patchEntityPlayerASM(String name, byte[] bytes, boolean obfuscated) {
		String getEyeHeight = "";
		String updateSize = "";
		String getYOffset = "";
		
		String entityName = "";
		
		if (obfuscated) {
			getEyeHeight = "func_70047_e";
			updateSize = "func_184808_cD";
			getYOffset = "func_70033_W";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		} else {
			getEyeHeight = "getEyeHeight";
			updateSize = "updateSize";
			getYOffset = "getYOffset";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(getEyeHeight)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScale", "(" + entityName + ")F", false);

				code.insertBefore(code.get(53), new VarInsnNode(Opcodes.ALOAD, 0));
				code.insertBefore(code.get(54), method);
				code.insertBefore(code.get(55), new InsnNode(Opcodes.FMUL));
			} else if (m.name.equals(updateSize)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScale", "(" + entityName + ")F", false);
				MethodInsnNode method2 = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScale", "(" + entityName + ")F", false);

				code.insert(code.get(58), new VarInsnNode(Opcodes.ALOAD, 0));
				code.insert(code.get(59), method);
				code.insert(code.get(60), new VarInsnNode(Opcodes.FLOAD, 1));
				code.insert(code.get(61), new InsnNode(Opcodes.FMUL));
				code.insert(code.get(62), new VarInsnNode(Opcodes.FSTORE, 1));
				
				code.insert(code.get(63), new VarInsnNode(Opcodes.ALOAD, 0));
				code.insert(code.get(64), method2);
				code.insert(code.get(65), new VarInsnNode(Opcodes.FLOAD, 2));
				code.insert(code.get(66), new InsnNode(Opcodes.FMUL));
				code.insert(code.get(67), new VarInsnNode(Opcodes.FSTORE, 2));
			} else if (m.name.equals(getYOffset)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityYOffset", "(D" + entityName + ")D", false);
				
				for (int i = code.size() - 1; i > 0; i--) {
					if (code.get(i).getOpcode() == Opcodes.DRETURN) {
						code.insertBefore(code.get(i), new VarInsnNode(Opcodes.ALOAD, 0));
						code.insertBefore(code.get(i + 1), method);
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	public byte[] patchEntityASM(String name, byte[] bytes, boolean obfuscated) {
		String isEntityInsideOpaqueBlock = "";
		String getMountedYOffset = "";
		
		String entityName = "";
		
		if (obfuscated) {
			isEntityInsideOpaqueBlock = "func_70094_T";
			getMountedYOffset = "func_70042_X";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		} else {
			isEntityInsideOpaqueBlock = "isEntityInsideOpaqueBlock";
			getMountedYOffset = "getMountedYOffset";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(isEntityInsideOpaqueBlock)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScale", "(" + entityName + ")F", false);
				
				code.insert(code.get(36), new VarInsnNode(Opcodes.ALOAD, 0));
				code.insert(code.get(37), method);
				code.insert(code.get(38), new InsnNode(Opcodes.FMUL));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	public byte[] patchEntityLivingBaseASM(String name, byte[] bytes, boolean obfuscated) {
		String isOnLadder = "";
		
		String entityName = "";
		
		if (obfuscated) {
			isOnLadder = "func_70617_f_";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		} else {
			isOnLadder = "isOnLadder";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(isOnLadder)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"isOnLadder", "(" + entityName + ")Z", false);

				code.insertBefore(code.get(67), new VarInsnNode(Opcodes.ALOAD, 0));
				code.insertBefore(code.get(68), method);
				code.insertBefore(code.get(69), new InsnNode(Opcodes.IOR));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	@SideOnly(Side.CLIENT)
	public byte[] patchPlayerControllerMPASM(String name, byte[] bytes, boolean obfuscated) {
		String getBlockReachDistance = "";
		
		String mc = "";
		String player = "";
		
		String entityName = "";
		String minecraftName = "";
		String playerName = "";
		
		if (obfuscated) {
			getBlockReachDistance = "func_78757_d";
			
			mc = "field_78776_a";
			player = "field_71439_g";
			
			entityName = "Lnet/minecraft/entity/Entity;";
			minecraftName = "Lnet/minecraft/client/Minecraft;";
			playerName = "Lnet/minecraft/client/entity/EntityPlayerSP;";
		} else {
			getBlockReachDistance = "getBlockReachDistance";
			
			mc = "mc";
			player = "player";
			
			entityName = "Lnet/minecraft/entity/Entity;";
			minecraftName = "Lnet/minecraft/client/Minecraft;";
			playerName = "Lnet/minecraft/client/entity/EntityPlayerSP;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(getBlockReachDistance)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScaleRoot", "(" + entityName + ")F", false);
				/*int i = 0;
				for (AbstractInsnNode n : code.toArray()) {
					System.out.println(i + " -> " + n.getOpcode() + " " + n.getType());
					i++;
				}*/
				code.insertBefore(code.get(13), new VarInsnNode(Opcodes.ALOAD, 0));
				code.insertBefore(code.get(14), new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/multiplayer/PlayerControllerMP", mc, minecraftName));
				code.insertBefore(code.get(15), new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/client/Minecraft", player, playerName));
				code.insertBefore(code.get(16), method);
				code.insertBefore(code.get(17), new InsnNode(Opcodes.FMUL));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	public byte[] patchPlayerInteractionManagerASM(String name, byte[] bytes, boolean obfuscated) {
		String getBlockReachDistance = "";
		
		String player = "";
		
		String entityName = "";
		String playerName = "";
		
		if (obfuscated) {
			getBlockReachDistance = "getBlockReachDistance";
			
			player = "field_73090_b";
			
			entityName = "Lnet/minecraft/entity/Entity;";
			playerName = "Lnet/minecraft/entity/player/EntityPlayerMP;";
		} else {
			getBlockReachDistance = "getBlockReachDistance";
			
			player = "player";
			
			entityName = "Lnet/minecraft/entity/Entity;";
			playerName = "Lnet/minecraft/entity/player/EntityPlayerMP;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(getBlockReachDistance)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScaleRootDouble", "(" + entityName + ")D", false);
				/*int i = 0;
				for (AbstractInsnNode n : code.toArray()) {
					System.out.println(i + " -> " + n.getOpcode() + " " + n.getType());
					i++;
				}*/
				code.insertBefore(code.get(4), new VarInsnNode(Opcodes.ALOAD, 0));
				code.insertBefore(code.get(5), new FieldInsnNode(Opcodes.GETFIELD, "net/minecraft/server/management/PlayerInteractionManager", player, playerName));
				code.insertBefore(code.get(6), method);
				code.insertBefore(code.get(7), new InsnNode(Opcodes.DMUL));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	public byte[] patchGenericEntityASM(String name, byte[] bytes, boolean obfuscated) {
		String getEyeHeight = "";
		String getMountedYOffset = "";
		String getYOffset = "";
		
		String height = "";
		
		String entityName = "";
		
		if (obfuscated) {
			getEyeHeight = "func_70047_e";
			getMountedYOffset = "func_70042_X";
			getYOffset = "func_70033_W";
			
			height = "field_70131_O";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		} else {
			getEyeHeight = "getEyeHeight";
			getMountedYOffset = "getMountedYOffset";
			getYOffset = "getYOffset";
			
			height = "height";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(getEyeHeight)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityScale", "(" + entityName + ")F", false);
				boolean referencesHeight = false;
				for (int i = 0; i < code.size(); i++) {
					if (code.get(i) instanceof FieldInsnNode) {
						FieldInsnNode fin = (FieldInsnNode) code.get(i);
						if (fin.getOpcode() == Opcodes.GETFIELD && fin.desc.equals("F") && fin.name.equals(height)) {
							referencesHeight = true;
							break;
						}
					}
				}
				if (!referencesHeight) {
					for (int i = code.size() - 1; i >= 0; i--) {
						if (code.get(i).getOpcode() == Opcodes.FRETURN) {
							code.insertBefore(code.get(i), new VarInsnNode(Opcodes.ALOAD, 0));
							code.insertBefore(code.get(i + 1), method);
							code.insertBefore(code.get(i + 2), new InsnNode(Opcodes.FMUL));
						}
					}
				}
			} else if (m.name.equals(getYOffset)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"getEntityYOffset", "(D" + entityName + ")D", false);
				
				for (int i = code.size() - 1; i > 0; i--) {
					if (code.get(i).getOpcode() == Opcodes.DRETURN) {
						code.insertBefore(code.get(i), new VarInsnNode(Opcodes.ALOAD, 0));
						code.insertBefore(code.get(i + 1), method);
					}
				}
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}
	
	public byte[] patchBlockCactusASM(String name, byte[] bytes, boolean obfuscated) {
		String onEntityCollidedWithBlock = "";
		
		String entityName = "";
		
		if (obfuscated) {
			onEntityCollidedWithBlock = "func_180634_a";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		} else {
			onEntityCollidedWithBlock = "onEntityCollidedWithBlock";
			
			entityName = "Lnet/minecraft/entity/Entity;";
		}

		ClassNode classNode = new ClassNode();
		ClassReader classReader = new ClassReader(bytes);
		classReader.accept(classNode, 0);

		List<MethodNode> methods = classNode.methods;

		for (MethodNode m : methods) {
			if (m.name.equals(onEntityCollidedWithBlock)) {
				System.out.println("Found method " + name + "." + m.name + "" + m.desc);
				InsnList code = m.instructions;
				MethodInsnNode method = new MethodInsnNode(Opcodes.INVOKESTATIC, "lilliputian/util/EntitySizeUtil",
						"attemptCactusDamage", "(" + entityName + ")V", false);
				/*int i = 0;
				for (AbstractInsnNode n : code.toArray()) {
					System.out.println(i + " -> " + n.getOpcode() + " " + n.getType());
					i++;
				}*/
				code.remove(code.get(6));
				code.set(code.get(5), method);
				code.remove(code.get(4));
				code.remove(code.get(3));
			}
		}

		ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		classNode.accept(writer);

		return writer.toByteArray();
	}

}
