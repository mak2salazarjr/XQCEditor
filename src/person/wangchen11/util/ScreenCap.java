package person.wangchen11.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;

public class ScreenCap {

	public static Bitmap viewShot(View view) {
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        return view.getDrawingCache();
    }
	
	public static Bitmap activityShot(Activity activity) {
        /*��ȡwindows������view*/
        View view = activity.getWindow().getDecorView();

        //����ǰ���ڱ��滺����Ϣ
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();

        //��ȡ״̬���߶�
        Rect rect = new Rect();
        view.getWindowVisibleDisplayFrame(rect);
        int statusBarHeight = rect.top;

        WindowManager windowManager = activity.getWindowManager();

        //��ȡ��Ļ��͸�
        DisplayMetrics outMetrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(outMetrics);
        int width = outMetrics.widthPixels;
        int height = outMetrics.heightPixels;

        //ȥ��״̬��
        Bitmap bitmap = Bitmap.createBitmap(view.getDrawingCache(), 0, statusBarHeight, width,
                height-statusBarHeight);

        //���ٻ�����Ϣ
        view.destroyDrawingCache();
        view.setDrawingCacheEnabled(false);

        return bitmap;
    }
	
	public static boolean saveTo(Bitmap bmp,File file) {
		file.getParentFile().mkdirs();
    	FileOutputStream fos = null;
        try {
        	fos = new FileOutputStream(file);
            if (fos != null) {
                //��һ������ͼƬ��ʽ���ڶ�������ͼƬ���������������������
                bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.flush();
                return true;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return false;
    }
}
