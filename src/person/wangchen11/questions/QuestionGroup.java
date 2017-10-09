package person.wangchen11.questions;

import java.util.ArrayList;

import android.content.Context;

public class QuestionGroup {
	private ArrayList<Question> mQuestions = new ArrayList<Question>();
	private String mName = "";
	
	public QuestionGroup(Context context,String assetsPath,String prefix,int number,String name){
		for(int i=1;i<=4;i++){
			mQuestions.add(new Question(context, assetsPath+prefix+i+"/", prefix+i));
		}
		mName = name;
	}
	
	public ArrayList<Question> getQuestions(){
		return mQuestions;
	}
	
	public String getName(){
		return mName;
	}
	
	public int getQuestionIndex(Question question){
		return mQuestions.indexOf(question);
	}
}
