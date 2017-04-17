package person.wangchen11.plugins;

import java.io.File;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;

import android.content.Context;

public class PluginsManager {
	private Context mContext; 
	private LinkedList<Plugin> mPlugins = new LinkedList<Plugin>();
	
	private static final String PATH = "plugins";
	private static PluginsManager mPluginsManager = null;
	private PluginsManager(Context context){
		mContext = context;
		refreshPlugs();
	}
	
	public static void init(Context context){
		mPluginsManager = new PluginsManager(context);
	}
	
	public static PluginsManager getInstance(){
		return mPluginsManager;
	}
	
	private void refreshPlugs(){
		mPlugins.clear();
		File dir = new File(getPluginsPath());
		File [] files = dir.listFiles();
		if(files!=null){
			for (File file : files) {
				if(file.isDirectory()){
					mPlugins.add(new Plugin(file));
				}
			}
		}
	}
	
	public String getSourceCmd(){
		Iterator<Plugin> iterator = mPlugins.iterator();
		StringBuilder stringBuilder = new StringBuilder();
		while(iterator.hasNext())
		{
			Plugin plugin = iterator.next();
			stringBuilder.append( plugin.getSourceCmd() );
			stringBuilder.append( "\n" );
		}
		return stringBuilder.toString();
	}
	
	public String getRunnablePath()
	{
		return getRunnablePath(mContext);
	}
	
	public String getPluginsPath(){
		return getPluginsPath(mContext);
	}

	public void installPlugin(File file){
		
	}
	
	public void installPlugin(InputStream in){
		
	}

	public static String getRunnablePath(Context context)
	{
		return context.getFilesDir().getAbsolutePath();
	}
	
	public static String getPluginsPath(Context context){
		return getRunnablePath(context)+"/"+PATH;
	}
	
	public static String getCmd(Context context){
		String cmd="\n";
		cmd+="export PLUGINS="+getPluginsPath(context)+"\n";
		cmd+="\n";
		cmd+=getInstance().getSourceCmd();
		return cmd;
	}
	
}
