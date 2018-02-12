package person.wangchen11.filebrowser;

import java.io.File;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class Open {
	public static void openFile(Context context,File file){
		
		Intent intent = new Intent();
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(Intent.ACTION_VIEW);
		String type = Mime.getMime(file);
		intent.setDataAndType(Uri.fromFile(file), type);
		context.startActivity(intent);
	}

}
