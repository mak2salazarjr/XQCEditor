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
	 * �Ƿ�Ϊ�ոո�����Ӧ�� 
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
			"(1)����ؼ���\n" +
			"(2)�޸�ǰһ�汾�еĲ��ȶ����ء�\n" +
			"\n" +
			"2.1.1:\n" +
			"(1)�Ż�include�����߼���\n" +
			"(2)�޸�GUI�Ļ�ͼAPI��\n" +
			"(3)����״̬����ָ����ܡ�\n" +
			"(3)�����л��������ܡ�\n" +
			"\n" +
			"2.1.0:\n" +
			"(1)����ʵʱ����ܡ�\n" +
			"(2)�Ż���ʾ���ܡ�\n" +
			"\n" +
			"2.0.9:\n" +
			"(1)�޸�makefile�������⡣\n" +
			"(2)����������֧�֡�\n" +
			"(3)���Դ�����Ӧ����ͨ����д����򿪴��롣\n" +
			"(4)����PHP������ʾ���ܡ�";
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












