package person.wangchen11.gnuccompiler;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;

import android.content.Context;
import android.util.Log;

public class GNUCCodeCheck {
	final static String TAG="GNUCCodeCheck";
	private String mCmd ="";
	private boolean mIsChecked = false;
	private LinkedList<CheckInfo> mCheckInfos = new LinkedList<CheckInfo>();
	private boolean mStopFlag = false;
	
	public GNUCCodeCheck(Context context,File file) {
		mCmd=GNUCCompiler2.getExportEnvPathCmd(context);
		mCmd+="cd \""+context.getFilesDir().getAbsolutePath()+"\"\n";
		File tempFile = new File(file.getAbsolutePath()+".o");
		try {
			tempFile = File.createTempFile("qeditor", null);
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		tempFile.deleteOnExit();
		mCmd+=GNUCCompiler2.getCompilerOnlyCmd(file,tempFile,null);
		mCmd+="\nexit\n";
	}
	
	public void stop(){
		mStopFlag = true;
	}
	
	public boolean isChencked()
	{
		return mIsChecked;
	}
	
	public LinkedList<CheckInfo> start()
	{
		mIsChecked = false;
		mCheckInfos.clear();
		Log.i(TAG, "start");
		try {
			startEx();
		} catch (Exception e) {
			e.printStackTrace();
		} catch (Error e) {
			e.printStackTrace();
		}
		Log.i(TAG, "mCheckInfos:"+mCheckInfos);
		mIsChecked = true;
		return mCheckInfos;
	}
	
	private void startEx() 
	{
		try {
			StringBuilder stringBuilder = new StringBuilder();
			Process process = Runtime.getRuntime().exec("sh");
			InputStream inputStream = process.getErrorStream();
			OutputStream outputStream = process.getOutputStream();
			outputStream.write(mCmd.getBytes());
			outputStream.flush();
			int proLength=0;
			byte[] buffer=new byte[100];
			while(!mStopFlag){
				try {
					Thread.sleep(10);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				int readLen = inputStream.read(buffer,proLength,buffer.length-proLength);
				if(readLen>0)
				{
					int nowLength=proLength+readLen;
					int utf8len=getUtf8Length(buffer,nowLength);
					if(nowLength==utf8len)
					{
						stringBuilder.append(new String(buffer,0,utf8len));
						proLength=0;
					}
					else
					if(nowLength>utf8len)
					{
						stringBuilder.append(new String(buffer,0,utf8len));
						proLength=nowLength-utf8len;
						System.arraycopy(buffer, utf8len, buffer, 0, proLength);
					}
					else
					{
						proLength=0;
					}
				}
				else
					break;
			}
			if(!mStopFlag)
				dexErrorPutMsg(stringBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	private void dexErrorPutMsg(String msg)
	{
		mCheckInfos.clear();
		String []lines = msg.split("\n");
		for (int i = 0; i < lines.length; i++) {
			String line = lines[i];
			String[] items = line.split(":");
			if(items == null||items.length<5)
				continue;
			else
			{
				try {
					int lineAt = Integer.valueOf(items[1]);
					int charAt = Integer.valueOf(items[2]);
					int type = CheckInfo.TYPE_WARN;
					if(items[3].contains("error"))
					{
						type = CheckInfo.TYPE_ERROR;
					}
					else
					if(items[3].contains("warning"))
					{
						type = CheckInfo.TYPE_WARN;
					}
					else
						continue;
					String wmsg = items[4];
					for(int j=5;j<items.length;j++)
					{
						wmsg+=":"+items[j];
					}
					String transMsg = translateMsg(wmsg);
					if(!wmsg.equals(transMsg))
						wmsg+="\n\n"+transMsg;
					mCheckInfos.add(new CheckInfo(items[0], wmsg, type, lineAt, charAt));
					
				}catch (Exception e) {
				}
			}
			
		}
	}

	private static int getUtf8Length(byte data[],int length)
	{
		int offset=0;
		while(true)
		{
			if(offset>=length)
				break;
			int bytes=getUtf8ByteLength(data[offset]);
			int nextOffset=offset+bytes;
			if(nextOffset==length)
			{
				offset=length;
				break;
			}
			if(nextOffset>length)
			{
				break;
			}
			offset=nextOffset;
		}
		return offset;
	}
	
	private static int getUtf8ByteLength(byte b)
	{
		int data=((int) b)&0xff;
		if( (data&0b10000000) == 0b00000000)
			return 1;
		if( (data&0b11100000) == 0b11000000)
			return 2;
		if( (data&0b11110000) == 0b11100000)
			return 3;
		if( (data&0b11111000) == 0b11110000)
			return 4;
		if( (data&0b11111100) == 0b11111000)
			return 5;
		if( (data&0b11111110) == 0b11111100)
			return 6;
		return 1;
	}
	
	public static final String mReplaceStr[][] = {
		{"unused variable","没用到的变量"},
		{"too many arguments for format","format对应的参数过多"},
		{"expects a matching","期望匹配到"},
		{"first use in this function","第一次在这个函数里使用"},
		{"with no value","需要一个值"},
		{"in function returning non-void","这个函数的返回类型不是void"},
		{"data definition has no type or storage class","没有指明类型的数据定义"},
		{"two or more data types in declaration specifiers","两个或两个以上的数据类型的声明"},
		{"redeclared as different kind of symbol","重新声明为不同的符号"},
		{"that defines no instances","并且没有定义实例"},
		{"empty character constant","空字符常数"},
		{"multi-character character constant","多字字符常数"},
		{"conflicting types for","冲突的类型:"},
		{"initialization makes integer from pointer without a cast","使用指针初始化整型数据却没有强转"},
		{"pointer value used where a floating point value was expected",""},
		{"cast from pointer to integer of different size","指针强转时类型大小不一致"},
		{"return type defaults to","没指定返回类型，默认为:"},
		{"control reaches end of non-void function","控制达到非void函数结束"},
		{"implicit declaration of function","隐式声明的函数:"},
		{"too few arguments to function","缺少参数去调用:"},
		{"too many arguments to function","太多的参数去调用:"},
		{"array size missing in","缺少数组大小:"},
		{"lvalue required as left operand of assignment","左值无法被赋值"},
		{"No such file or directory","没有这样的文件或目录"},
		{"in program","在程序里"},
		{"stray ","未知的符号(可能是中文字符):"},
		{"undeclared","未声明"},
		{"expected ","缺少"},
		{"expects ","期望的"},
		{"before ","在这之前:"},
		{"format ","格式"},
		{"arguments","参数"},
		{"argument","参数"},
		{"unnamed ","未命名的"},
		{"expression ","表达式"},
		{"of type ","类型是"},
		{"has type ","的类型是"},
		{"but ","但是 "},
		{"token",""},
	};
	private String translateMsg(String msg){
		for(int i=0;i<mReplaceStr.length;i++){
			msg = msg.replaceAll(mReplaceStr[i][0], mReplaceStr[i][1]);
		}
		return msg;
	}
}
