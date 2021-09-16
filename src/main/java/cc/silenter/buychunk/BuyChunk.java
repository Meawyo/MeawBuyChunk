package cc.silenter.buychunk;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.BlockArrayClipboard;
import com.sk89q.worldedit.function.operation.ForwardExtentCopy;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public final class BuyChunk extends JavaPlugin {
    public static Plugin instance;
    @Override
    public void onEnable() {
        // Plugin startup logic
        instance=this;
        getCommand("buychunk").setExecutor(this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(cmd.getName().equalsIgnoreCase("buychunk")){ // your command name
            if(args.length != 1 ){
                sender.sendMessage("Syntax Error");
                return true;
            }
            if (args[0].equals("go")) {
                int x = (int) Math.ceil(Math.random()*10000-5000);
                int z = (int) Math.ceil(Math.random()*10000-5000);
                x = x/16*16;
                z = z/16*16;
                CuboidRegion region = new CuboidRegion(BukkitAdapter.adapt(Bukkit.getWorld("1")), BlockVector3.at(x,0,z),BlockVector3.at(x+16,255,z+16));
                BlockArrayClipboard clipboard = new BlockArrayClipboard(region);

                try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(Bukkit.getWorld("1")))) {
                    ForwardExtentCopy forwardExtentCopy = new ForwardExtentCopy(
                            editSession, region, clipboard, region.getMinimumPoint()
                    );
                    forwardExtentCopy.setCopyingBiomes(true);
                    forwardExtentCopy.setCopyingEntities(true);
                    // configure here
                    try {
                        Operations.complete(forwardExtentCopy);
                    } catch (WorldEditException e) {
                        e.printStackTrace();
                    }
                }


                Player player = (Player)sender;
                try (EditSession editSession = WorldEdit.getInstance().newEditSession(BukkitAdapter.adapt(player.getLocation().getWorld()))) {
                    Operation operation = new ClipboardHolder(clipboard).createPaste(editSession).to(BlockVector3.at(((int)player.getLocation().getX())/16*16-16, 0, ((int)player.getLocation().getZ())/16*16))
                            .copyBiomes(true)
                            .copyEntities(true)
                            .build();
                    try {
                        Operations.complete(operation);
                    } catch (WorldEditException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            }


        }
        sender.sendMessage("No such command");
        return true;
    }
}
