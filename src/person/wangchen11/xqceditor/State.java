package person.wangchen11.xqceditor;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.util.Log;

public class State {
	public static int VersionCodePro = 1;
	public static int VersionCodeNow = 1;
	public static String VersionNamePro = null;
	public static String VersionNameNow = null;
	private static final String ConfigName="State";
	private static final String TAG="State";
	public static void init(Context context)
	{
		SharedPreferences sharedPreferences=context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		VersionCodePro=sharedPreferences.getInt("VersionCodePro", 0);
		VersionNamePro=sharedPreferences.getString("VersionNamePro", null);
		PackageManager packageManager=context.getPackageManager();
		try {
			PackageInfo packageInfo=packageManager.getPackageInfo(context.getPackageName(), 0);
			VersionCodeNow=packageInfo.versionCode;
			VersionNameNow=packageInfo.versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		
		if(isUpdated())
		{
			showUpdateMsg(context);
		}
		
		Log.i(TAG, "VersionCodePro:"+VersionCodePro);
		Log.i(TAG, "VersionCodeNow:"+VersionCodeNow);
		Log.i(TAG, "VersionNamePro:"+VersionNamePro);
		Log.i(TAG, "VersionNameNow:"+VersionNameNow);
	}
	
	/**
	 * 是否为刚刚更新完应用 
	 * @return
	 */
	public static boolean isUpdated()
	{
		if(VersionCodePro!=VersionCodeNow)
			return true;
		if(VersionNameNow==null)
			return true;
		if(!VersionNameNow.equals(VersionNamePro))
			return true;
		return false;
	}
	
	public static void save(Context context)
	{
		SharedPreferences sharedPreferences = context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		Editor editor=sharedPreferences.edit();
		editor.putInt("VersionCodePro", VersionCodeNow);
		editor.putString("VersionNamePro", VersionNameNow);
		editor.commit();
	}
	
	public static String mUpdateMsg = 
			"2.1.2:\n" +
			"(1)加入控件。\n" +
			"(2)修复前一版本中的不稳定因素。\n" +
			"\n" +
			"2.1.1:\n" +
			"(1)优化include处理逻辑。\n" +
			"(2)修改GUI的绘图API。\n" +
			"(3)增加状态保存恢复功能。\n" +
			"(3)增加切换动画功能。\n" +
			"\n" +
			"2.1.0:\n" +
			"(1)增加实时查错功能。\n" +
			"(2)优化提示功能。\n" +
			"\n" +
			"2.0.9:\n" +
			"(1)修复makefile部分问题。\n" +
			"(2)添加物理键盘支持。\n" +
			"(3)可以从其它应用中通过快写代码打开代码。\n" +
			"(4)增加PHP代码提示功能。";
	public static void showUpdateMsg(Context context)
	{
		AlertDialog.Builder builder=new Builder(context);
		builder.setTitle(R.string._updatemsg);
		builder.setMessage(mUpdateMsg);
		builder.setCancelable(false);
		builder.setPositiveButton(android.R.string.ok, null);
		builder.create();
		builder.show();
	}
}












