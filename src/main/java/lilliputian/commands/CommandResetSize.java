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

public class CommandResetSize extends CommandBase {

	@Override
	public String getName() {
		return "resetsize";
	}
	
	@Override
    public int getRequiredPermissionLevel() {
        return 2;
    }

	@Override
	public String getUsage(ICommandSender sender) {
		return Lilliputian.MODID + ".commands.resetsize.usage";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if (args.length > 1) {
            throw new WrongUsageException(getUsage(sender), new Object[0]);
        } else {
            Entity entity;
            
            if (args.length == 0) {
                entity = getCommandSenderAsPlayer(sender);
            } else {
                entity = getEntity(server, sender, args[0]);
            }
            
            if (entity.hasCapability(SizeProvider.sizeCapability, null)) {
            	ISizeCapability cap = entity.getCapability(SizeProvider.sizeCapability, null);

            	if (!entity.world.isRemote) {
            		cap.setBaseSize(1F);
            		cap.setScale(1F);
            		cap.setActualScale(1F);
            		PacketHandler.INSTANCE.sendToAll(new MessageSizeChange(cap.getBaseSize(), cap.getScale(), entity.getEntityId(), false));
            	}
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
