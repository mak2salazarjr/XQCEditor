package person.wangchen11.busybox;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import person.wangchen11.xqceditor.State;

import android.content.Context;
import android.util.Log;

public class Busybox {
	private final static int BUFFER=4096; 
	static final String TAG="Busybox";
	
	
	public static void freeResourceIfNeed(final Context context){
		if( State.isUpdated() || !new File(getWorkDir(context)).isDirectory() )
		{
			new File(getWorkDir(context)).mkdirs();
			freeZip(context, "busybox.zip", getRunnablePath(context) );
		}
	}
	
	public static boolean freeZip(Context context,String assetsName,String pathTo){
		if(pathTo!=null)
			pathTo+=File.separatorChar;
		try {
			ZipInputStream zis = new ZipInputStream(context.getAssets().open(
					assetsName));
			BufferedOutputStream dest = null;
			ZipEntry entry = null; 
			String strEntry = null;
			byte data[] = new byte[BUFFER];
			while ((entry = zis.getNextEntry()) != null) {
				try {
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
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
			return false;
		}
		return true;
	}

	public static String getRunnablePath(Context context)
	{
		return context.getFilesDir().getAbsolutePath();
	}
	
	public static String getWorkDir(Context context)
	{
		return getRunnablePath(context)+"/busybox/";
	}
	
	public static String getCmd(Context context)
	{
		String cmd="\n";
		cmd+="export PATH=$PATH:"+getWorkDir(context)+"\n";
		cmd+="cd "+getWorkDir(context)+"\n";
		cmd+="chmod 777 *\n";
		cmd+="cd /\n";
		return cmd;
	}
}
