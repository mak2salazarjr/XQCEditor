package person.wangchen11.filebrowser;

import java.io.File;
import person.wangchen11.xqceditor.R;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

public class Share {
	public static boolean ShareFile(Context context,File file)
	{
		String mime;
		mime=Mime.getMime(file);
		Log.i("fbr", mime);
		Intent intent;
		intent=new Intent(Intent.ACTION_SEND); 
		intent.setType("text/plain");
		intent.putExtra(Intent.EXTRA_SUBJECT,  context.getString(R.string.share)); 
		intent.putExtra(Intent.EXTRA_STREAM, Uri.fromFile(file) );  
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
		context.startActivity(Intent.createChooser(intent, context.getString(R.string.share)));
		return true;	
	}

}
