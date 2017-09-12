package person.wangchen11.waps;

import java.util.Date;

import cn.waps.AppConnect;
import cn.waps.UpdatePointsListener;
import android.content.Context;
import android.widget.LinearLayout;

@SuppressWarnings("deprecation")
public class Waps {
	static final String TAG="Waps";
	private static String APP_ID="f29592b5daa7915e1048e659e7e930cf";
	//private static String APP_PID="qq";
	private static boolean mInited = false;
	private static String APP_PID="google";
	
	private static Date mDurTime;
	static{
		int year=2017;
		int month=6;
		int day=1;
		int hour=0;
		int minute=0;
		int second=0;
		mDurTime=new Date(year-1900, month-1, day, hour, minute, second);
	}
	
	public static void init(Context context)
	{
		try {
			try {
				//AppConnect.getInstance(context);
				AppConnect.getInstance(APP_ID, APP_PID, context);
				AppConnect.getInstance(context).initAdInfo();
				mInited = true;
			} catch (Error e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void showPop(Context context)
	{
		if(!isTimeToShow())
			return ;
		AppConnect.getInstance(context).showPopAd(context);
		AppConnect.getInstance(context).setPopAdBack(true);
	}
	
	public static void showBanner(Context context,LinearLayout linearLayout)
	{
		if(!isTimeToShow())
			return ;
		if(Key.hasRealKey(context))
			return ;
		try {
			try {
				AdLinearLayout adLinearLayout=new AdLinearLayout(context);
				linearLayout.addView(adLinearLayout);
				AppConnect.getInstance(context).showBannerAd(context, adLinearLayout);
			} catch (Error e) {
				e.printStackTrace();
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean isGoogle(){
		return APP_PID.equals("google");
	}
	
	public static boolean isTimeToShow()
	{
		if(!mInited)
			return false;
		
		if(isGoogle())
			return false;
		
		Date timeNow=new Date();
		if(timeNow.after(mDurTime))
			return true;
		return false;
	}

	public static void showOffers(Context context)
	{
		if(!isTimeToShow())
			return ;
		try{
			AppConnect.getInstance(context).showAppOffers(context);
		}catch(Error e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void updatePoints(Context context,UpdatePointsListener listener)
	{
		if(!isTimeToShow())
			return ;
		try{
			AppConnect.getInstance(context).getPoints(listener);
		}catch(Error e) {
			e.printStackTrace();
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
}
