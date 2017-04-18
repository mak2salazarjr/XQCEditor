package person.wangchen11.plugins;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import person.wangchen11.xqceditor.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

public class PluginsManager {
	private static final String TAG = "PluginsManager";
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

	@SuppressLint("DefaultLocale") 
	public boolean installPlugin(File file){
		String fileName = file.getName();
		String lastName = ".qplug.zip";
		if(!fileName.toLowerCase().endsWith(lastName))
			return false;
		String name = fileName.substring(0, fileName.length() - lastName.length());
		if(name.length()<0)
			return false;
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(file);
			boolean ret = installPlugin(name,fileInputStream);
			return ret;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return false;
	}
	
	public boolean installPlugin(String name,final InputStream in){
		String path = getPluginsPath()+"/"+name;
		final File dir = new File(path);
		new WaitingProcess(mContext,R.string.installing){
			@Override
			public void run() {
				dir.mkdirs();
				try {
					addMsgLn(R.string.unziping);
					int number = freeZip(in, dir.getPath());
					if(number <=0 || dir.list()==null || dir.list().length==0){
						dir.delete();
						throw new Exception("Nothing is released!");
					}
					addMsgLn(R.string.installing);
					Plugin plugin = new Plugin(dir);
					String cmd = plugin.getInstallCmd();
					cmd+="\nexit\n";
					Process process = Runtime.getRuntime().exec("sh");
					process.getOutputStream().write(cmd.getBytes());
					BufferedInputStream bufferedInputStream = new BufferedInputStream(process.getInputStream());
					InputStreamReader reader=new InputStreamReader(bufferedInputStream,"UTF-8");
					BufferedReader bufferedReader=new BufferedReader(reader);
					String line = null;
					while( (line = bufferedReader.readLine())!=null )
					{
						addMsgLn(line);
					}
					if(process.waitFor()==0){
						addMsg(R.string.install_done);
						setTitle(R.string.install_done);
					} else {
						addMsg(R.string.install_done);
						setTitle(R.string.install_failed);
					}
				} catch (Exception e) {
					e.printStackTrace();
					addMsgLn(e.toString());
					addMsgLn(R.string.install_failed);
					setTitle(R.string.install_failed);
				}

				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				refreshPlugs();
				hideProcess();
			}
			public void onComplete() {
				getDialog().setCancelable(true);
			};
		}.start();
		return true;
	}


	public static int freeZip(InputStream in,String pathTo) throws Exception{
		int BUFFER = 1024;
		int number = 0;
		if(pathTo!=null)
			pathTo+=File.separatorChar;
		ZipInputStream zis = new ZipInputStream(in);
		BufferedOutputStream dest = null;
		ZipEntry entry = null; 
		String strEntry = null;
		byte data[] = new byte[BUFFER];
		while ((entry = zis.getNextEntry()) != null) {
			Log.i("Unzip: ", "" + entry);
			int count;
			strEntry = entry.getName();

			File entryFile = new File(pathTo + strEntry);
			File entryDir = new File(entryFile.getParent());
			entryDir.mkdirs();
			if (!entryDir.exists()) {
				Log.i(TAG, "mkdirs");
				if(!entryDir.mkdirs())
					Log.i(TAG, "mkdirs failed :"+entryDir.getAbsolutePath());;
			}
			if(entry.isDirectory())
			{
				entryFile.mkdirs();
			}else
			{
				FileOutputStream fos = new FileOutputStream(entryFile);
				dest = new BufferedOutputStream(fos, BUFFER);
				while ((count = zis.read(data, 0, BUFFER)) != -1) {
					dest.write(data, 0, count);
				}
				dest.flush();
				dest.close();
			}
			number++;
		}
		zis.close();
		return number;
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
		cmd+="\n";
		return cmd;
	}
	
}
