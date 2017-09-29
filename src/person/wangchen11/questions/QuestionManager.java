package person.wangchen11.questions;

import java.util.ArrayList;

import android.content.Context;

public class QuestionManager {
	private static QuestionManager mQuestionManager = null;
	
	private static final String ASSETS_PATH = "questions/";
	
	private Context mContext = null;
	private ArrayList<Question> mLevel1Questions = null;
	
	private QuestionManager(Context context) {
		mContext = context;
		mLevel1Questions = createQuestions(context,ASSETS_PATH,"1.",4);
	}
	public static void init(Context context){
		if(mQuestionManager==null)
			mQuestionManager = new QuestionManager(context);
	}
	
	public static QuestionManager instance(){
		return mQuestionManager;
	}
	
	private ArrayList<Question> createQuestions(Context context,String assetsPath,String prefix,int number){
		ArrayList<Question> questions = new ArrayList<Question>();
		for(int i=1;i<=4;i++){
			questions.add(new Question(context, assetsPath+prefix+i+"/", prefix+i));
		}
		return questions;
	}
	
	public ArrayList<Question> getLevel1Questions(){
		return mLevel1Questions;
	}
}
