package lilliputian.commands;

import java.util.Collections;
import java.util.List;

import javax.annotation.Nullable;

import lilliputian.Lilliputian;
import lilliputian.capabilities.ISizeCapability;
import lilliputian.capabilities.SizeProvider;
import lilliputian.network.MessageSizeChange;
import lilliputian.network.PacketHandler;
import lilliputian.util.EntitySizeUtil;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.WrongUsageException;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class CommandSetBaseSize extends CommandBase {

	@Override
	public String getName() {
		return "setbasesize";
	}
	
	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

	@Override
	public String getUsage(ICommandSender sender) {
		return Lilliputian.MODID + ".commands.setbasesize.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length < 1 || args.length > 2) {
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        } else {
        	int i = 0;
            Entity entity;
            
            if (args.length == 1) {
                entity = getCommandSenderAsPlayer(sender);
            } else {
                entity = getEntity(server, sender, args[i]);
                i = 1;
            }
            
            if (entity.hasCapability(SizeProvider.sizeCapability, null)) {
            	ISizeCapability cap = entity.getCapability(SizeProvider.sizeCapability, null);
            	
            	if (args.length != i + 1) {
            		throw new WrongUsageException(getUsage(sender), new Object[0]);
            	}
            
            	float newBaseSize = (float) parseDouble(args[i], EntitySizeUtil.HARD_MIN, EntitySizeUtil.HARD_MAX);
            	if (!entity.world.isRemote) {
            		cap.setBaseSize(newBaseSize);
            		PacketHandler.INSTANCE.sendToAll(new MessageSizeChange(cap.getBaseSize(), cap.getScale(), entity.getEntityId()));
            	}
            } else {
            	// notifyCommandListener(sender, this, Elastic.MODID + ".commands.setbasesize.failure.capability");
            }
        }
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
        return args.length != 1 ? Collections.emptyList() : getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
    }
	
	@Override
	public boolean isUsernameIndex(String[] args, int index) {
        return index == 0;
    }

}
