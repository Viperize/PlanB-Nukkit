package planb;

import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.plugin.PluginBase;
import cn.nukkit.utils.Config;
import cn.nukkit.utils.TextFormat;
import cn.nukkit.Player;
import java.io.File;
import java.util.HashMap;

public class PlanB extends PluginBase{
    private Config backups;   
    @Override
    public void onEnable(){
        this.getDataFolder().mkdirs();
        this.backups = new Config(new File(this.getDataFolder(), "backups.txt"), Config.ENUM);
    }
    @Override
    public void onDisable(){
        this.backups.save();
    }   
    private void sendCommandHelp(CommandSender sender){
        HashMap<String, String> commands = new HashMap<>();
        commands.put("help", "Shows all PlanB commands");
        commands.put("restore", "Restores OP status of all online players listed in backups.txt");
        sender.sendMessage("PlanB commands:");
        for(String name : commands.keySet()){
            sender.sendMessage("/planb "+name+": "+commands.get(name));
        }
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(command.getName().equalsIgnoreCase("planb")){
            if(args.length != 0){
                switch(args[0].toLowerCase()){
                    case "help":
                        this.sendCommandHelp(sender);
                        break;
                    case "r":
                    case "restore":
                        if(sender instanceof Player){
                            if(this.backups.exists(sender.getName().toLowerCase(), true)){
                                sender.sendMessage(TextFormat.YELLOW+"Restoring statuses of OPs...");
                                for(Player player : this.getServer().getOnlinePlayers().values()){
                                    if(this.backups.exists(player.getName().toLowerCase(), true)){
                                        if(!player.isOp()){
                                            player.setOp(true);
                                            player.sendMessage(TextFormat.GREEN+"Your OP status has been restored.");
                                            sender.sendMessage(TextFormat.GREEN+"Restored "+player.getName()+"'s OP status.");
                                        }
                                    }
                                    else{
                                        if(player.isOp()){
                                            player.setOp(false);
                                            player.kick("Detected potential hacker");
                                            this.getServer().broadcastMessage(TextFormat.YELLOW+"Deopped and kicked potential hacker: "+player.getName());
                                        }
                                    }
                                }
                            }
                            else{
                                sender.sendMessage(TextFormat.RED+"You don't have permissions to restore OP statuses");
                            }
                        }
                        else{
                            sender.sendMessage(TextFormat.RED+"Please run this command in-game.");
                        }
                        break;
                    default:
                        sender.sendMessage("Usage: /planb <sub-command> [parameters]");
                        break;
                }
            }
            else{
                this.sendCommandHelp(sender);
            }
        }
        return true;
    }
}