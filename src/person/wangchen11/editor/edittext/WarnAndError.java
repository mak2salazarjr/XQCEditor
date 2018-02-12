package person.wangchen11.editor.edittext;

import android.graphics.Color;
import person.wangchen11.waps.Waps;
import person.wangchen11.xqceditor.R;

public class WarnAndError {
	public int mLevel;
	public int mColor;
	public int mLine;
	public String mMsg;
	public boolean mDrawUnderLine;
	public boolean mFullLine;
	public int mIndex;
	public int mLength;
	public static final int LEVEL_INFO  = 0;
	public static final int LEVEL_WARN  = 1;
	public static final int LEVEL_ERROR = 2;
	public static final int COLOR_INFO  = Color.BLUE;
	public static final int COLOR_WARN  = Color.rgb(0xf0, 0xb0, 0x20);
	public static final int COLOR_ERROR = Color.RED;
	public WarnAndError(int line,int level){
		this(line,level,"");
	}
	public WarnAndError(int line,int level,String msg){
		mLine  = line;
		mLevel = level;
		switch (mLevel) {
		case LEVEL_INFO:
			mColor = COLOR_INFO;
			break;
		case LEVEL_WARN:
			mColor = COLOR_WARN;
			break;
		case LEVEL_ERROR:
			mColor = COLOR_ERROR;
			break;

		default:
			break;
		}
		mMsg   = msg;
		mDrawUnderLine = true;
		mFullLine = true;
		
	}
	
	public int getTitle(){
		switch (mLevel) {
		case LEVEL_INFO:
			return R.string.tip;
		case LEVEL_WARN:
			return R.string.warn;
		case LEVEL_ERROR:
			return R.string.error;
		default:
			return R.string.tip;
		}
	}
	
	public boolean include(int line,int offset){
		if(mLine==line){
			if(mFullLine==true)
				return true;
			// TODO 
		}
		return false;
	}

	public static final String mReplaceStr[][] = {
		{"unused variable","û�õ��ı���"},
		{"too many arguments for format","format��Ӧ�Ĳ�������"},
		{"expects a matching","����ƥ�䵽"},
		{"first use in this function","��һ�������������ʹ��"},
		{"with no value","��Ҫһ��ֵ"},
		{"in function returning non-void","��������ķ������Ͳ���void"},
		{"data definition has no type or storage class","û��ָ�����͵����ݶ���"},
		{"two or more data types in declaration specifiers","�������������ϵ��������͵�����"},
		{"redeclared as different kind of symbol","��������Ϊ��ͬ�ķ���"},
		{"that defines no instances","����û�ж���ʵ��"},
		{"empty character constant","���ַ�����"},
		{"multi-character character constant","�����ַ�����"},
		{"conflicting types for","��ͻ������:"},
		{"initialization makes integer from pointer without a cast","ʹ��ָ���ʼ����������ȴû��ǿת"},
		{"pointer value used where a floating point value was expected",""},
		{"cast from pointer to integer of different size","ָ��ǿתʱ���ʹ�С��һ��"},
		{"return type defaults to","ûָ���������ͣ�Ĭ��Ϊ:"},
		{"control reaches end of non-void function","���ƴﵽ��void��������"},
		{"implicit declaration of function","��ʽ�����ĺ���:"},
		{"too few arguments to function","ȱ�ٲ���ȥ����:"},
		{"too many arguments to function","̫��Ĳ���ȥ����:"},
		{"array size missing in","ȱ�������С:"},
		{"lvalue required as left operand of assignment","��ֵ�޷�����ֵ"},
		{"No such file or directory","û���������ļ���Ŀ¼"},
		{"in program","�ڳ�����"},
		{"request for member","��ͼ���ʳ�Ա"},
		{"in something not a structure or union","��һ���ǽṹ���������������"},
		{"with no expression","����û�б��ʽ"},
		{"stray ","δ֪�ķ���(�����������ַ�):"},
		{"undeclared","δ����"},
		{"expected ","ȱ��"},
		{"expects ","������"},
		{"before ","����֮ǰ:"},
		{"format ","��ʽ"},
		{"arguments","����"},
		{"argument","����"},
		{"unnamed ","δ������"},
		{"expression ","���ʽ"},
		{"of type ","������"},
		{"has type ","��������"},
		{"but ","���� "},
		{"token",""},
	};
	
	public static String translateMsg(String msg){
		if(!Waps.isGoogle())
		for(int i=0;i<mReplaceStr.length;i++){
			msg = msg.replaceAll(mReplaceStr[i][0], mReplaceStr[i][1]);
		}
		return msg;
	}
}
