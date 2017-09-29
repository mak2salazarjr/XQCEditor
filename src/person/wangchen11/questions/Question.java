package person.wangchen11.questions;

import java.io.IOException;
import java.util.ArrayList;
import person.wangchen11.myscanner.MyScanner;

import android.content.Context;

public class Question {
	public String mKey = null;
	public String mAssetsPath = null;
	public String mTitle = null;
	public int    mPoint = 0;
	public int    mDifficulty = 0;
	public ArrayList<String> mImages = new ArrayList<String>();
	public int mScore = 0;
	
	public String getQuestion(Context context){
		return "";
	}
	
	
	public Question(Context context,String assrtsPath,String queskey) {
		mAssetsPath = assrtsPath;
		mKey = queskey;
		try {
			MyScanner scanner = new MyScanner(context.getAssets().open(assrtsPath+"_base.txt"));
			while(scanner.hasNextLine()){
				String line = scanner.nextLine();
				int index = line.indexOf(":");
				if(index<0)
					continue;
				String key = line.substring(0,index);
				String value = line.substring(index+1,line.length());
				if(key.equals("mTitle")){
					mTitle = value;
				}
				if(key.equals("mPoint")){
					try {
						mPoint = Integer.parseInt(value);
					} catch (Exception e) {
					}
				}
				if(key.equals("mDifficulty")){
					try {
						mDifficulty = Integer.parseInt(value);
					} catch (Exception e) {
					}
				}
				if(key.equals("mImages")){
					String[] images = value.split(",");
					if(images!=null){
						for(String image:mImages){
							mImages.add(image);
						}
					}
				}
			}
			scanner.close();
		} catch (IOException e) {
			e.printStackTrace();
		} 
	}
	
}
