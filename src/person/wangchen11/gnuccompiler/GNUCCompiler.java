package person.wangchen11.gnuccompiler;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import person.wangchen11.util.FileUtil;
import person.wangchen11.xqceditor.R;
import person.wangchen11.xqceditor.State;

import android.content.Context;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.util.Log;

public class GNUCCompiler {
	protected final static String TAG="GNUCCompiler"; 
	private final static int BUFFER=4096; 

	public static void freeResourceIfNeed(final Context context){
		if( State.isUpdated() || !new File(getWorkSpaceDir()).isDirectory() )
		{
			freeZip(context, "workspace.zip", getSystemDir() );
			
		}
		new Thread(new Runnable() {
			@Override
			public void run() {
				if( State.isUpdated() || !new File(getGccPath(context)).isDirectory() )
				{
					freeZip(context, "gcc.zip", getRunablePath(context));
				}
				if( State.isUpdated() || !new File(getIncludeDir()).isDirectory() )
				{
					freeZip(context, "gcc include.zip", getSystemDir() );
				}
				if( State.isUpdated() || !new File(getIncludeDirEx()).isDirectory() )
				{
					freeZip(context, "g++ include.zip", getSystemDir() );
				}
				
				if( State.isUpdated() || !new File(getFixCppObj(context)).isFile() )
				{
					freeFile(context, "fix.cpp.o", getFixCppObj(context));
				}
				
				FileUtil.setFileAllChildsExecutable(new File(getGccPath(context)));
				FileUtil.setFileAllChildsExecutable(new File(getCcPath(context)));
				FileUtil.setFileAllChildsExecutable(new File(getAbiPath(context)));
				State.save(context);
			}
		}).start();
	}

	public static boolean freeZip(Context context,String assetsName,String pathTo){
		if(pathTo!=null)
			pathTo+=File.separatorChar;
		try {
			ZipInputStream zis = new ZipInputStream(context.getAssets().open(
					assetsName));
			BufferedOutputStream dest = null;
			ZipEntry entry = null; 
			String strEntry = null;
			byte data[] = new byte[BUFFER];
			while ((entry = zis.getNextEntry()) != null) {
				try {
					Log.i("Unzip: ", "" + entry);
					int count;
					strEntry = entry.getName();

					File entryFile = new File(pathTo + strEntry);
					File entryDir = new File(entryFile.getParent());
					entryDir.mkdirs();
					if (!entryDir.exists()) {
						Log.i(TAG, "mkdirs");
						if(!entryDir.mkdirs())
							Log.i(TAG, "mkdirs failed :"+entryDir.getAbsolutePath());;
					}
					if(entry.isDirectory())
					{
						entryFile.mkdirs();
					}else
					{
						FileOutputStream fos = new FileOutputStream(entryFile);
						dest = new BufferedOutputStream(fos, BUFFER);
						while ((count = zis.read(data, 0, BUFFER)) != -1) {
							dest.write(data, 0, count);
						}
						dest.flush();
						dest.close();
					}
				} catch (Exception ex) {
					ex.printStackTrace();
					return false;
				}
			}
			zis.close();
		} catch (Exception cwj) {
			cwj.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean freeFile(Context context,String assetsName,String fileTo)
	{
		try {
			OutputStream outputStream=new FileOutputStream(new File(fileTo));
			try {
				InputStream inputStream=context.getAssets().open(assetsName);
				byte data[] = new byte[4096];
				int readLen=0;
				while( ( readLen=inputStream.read(data))>0 )
				{
					outputStream.write(data,0,readLen);
				}
				inputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				outputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static String getGccPath(Context context){
		return getRunablePath(context)+"/gcc/arm-linux-androideabi/bin/";
	}

	public static String getCcPath(Context context){
		return getRunablePath(context)+"/gcc/libexec/gcc/arm-linux-androideabi/6.1.0/";
	}

	public static String getAbiPath(Context context){
		return getRunablePath(context)+"/gcc/bin/";
	}
	
	public static String getFixCppObj(Context context)
	{
		return getRunablePath(context)+"/fix.cpp.o";
	}
	
	public static String getRunablePath(Context context){
		return context.getFilesDir().getAbsolutePath()+File.separatorChar;
	}
	static int mNameStartNumber=0;
	public static String getTempFilePath(Context context){
		String path="";
		for(int i=0;i<1000;i++)
		{
			path = getRunablePath(context)+mNameStartNumber+".tmp";
			mNameStartNumber++;
			if( !(new File(path).isFile()) )
				break;
		}
		return path;
	}
	
	public static String getSystemDir(){
		return Environment.getExternalStorageDirectory().getAbsolutePath()+File.separatorChar+"qeditor"+File.separatorChar;
	}

	public static String getIncludeDir(){
		return getSystemDir()+"/gcc include/";
	}

	public static String getIncludeDirEx(){
		return getSystemDir()+"/g++ include/";
	}
	
	public static String getWorkSpaceDir(){
		return getSystemDir()+"/workspace/";
	}
	
	public static String getNeedOption()
	{
		return " -I\""+getIncludeDir()+"\" -I\""+getIncludeDirEx()+"\" -lm -lstdc++ -lsupc++ -lgnustl_static ";
	}
	
	public static String getFilesString(List <File> files){
		Iterator<File> iterator=files.iterator();
		String cmd="";
		while(iterator.hasNext()){
			cmd+=" \""+iterator.next().getPath()+"\" ";
		}
		return cmd;
	}
	
	public static String getCompilerCmd(Context context,List <File> files,File outFile,String otherOption){
		outFile.delete();
		outFile.getParentFile().mkdirs();
		String cmd="";
		/*
		cmd+="cd \""+getGccPath(context)+"\"\n";
		cmd+="chmod 777 *\n";
		cmd+="cd \""+getCcPath(context)+"\"\n";
		cmd+="chmod 777 *\n";
		cmd+="cd \""+getAbiPath(context)+"\"\n";
		cmd+="chmod 777 *\n";
		cmd+="if [ -f \""+outFile.getPath()+"\" ]; then \n";
		cmd+="rm \""+outFile.getPath()+"\"\n";
		cmd+="fi\n";
		cmd+="export PATH=$PATH:"+getGccPath(context)+"\n";
		cmd+="export PATH=$PATH:"+getCcPath(context)+"\n";
		cmd+="export PATH=$PATH:"+getAbiPath(context)+"\n";
		*/
		cmd+="echo \""+context.getText(R.string.compiling)+"\"\n";
		cmd+="cd \""+outFile.getParent()+"\"\n";
		cmd+="gcc ";
		cmd+=getFilesString(files)+" ";
		cmd+=" \""+getFixCppObj(context)+"\" ";
		cmd+=getNeedOption();
		cmd+=" -o \""+outFile.getPath()+"\" ";
		cmd+="-Wall ";
		cmd+=(otherOption!=null?otherOption:"");
		cmd+="\n";
		cmd+="if [ ! -f \""+outFile.getPath()+"\" ]; then \n";
		cmd+="echo \""+context.getText(R.string.compilation_fails)+"\"\n";
		cmd+="else\n";
		cmd+="echo \""+context.getText(R.string.successfully_compiled)+"\"\n";
		cmd+="fi\n";
		return cmd;
	}
	
	
	static String mProPocesssName=null;
	public static String getRunCmd(Context context,File executeFile){
		return getRunCmd(context,executeFile,null);
	}
	
	public static String getRunCmd(Context context,File executeFile,String otherOption){
		try {
			//ÈÃrm²»±¨¾¯¸æ 
			new File(getRunablePath(context)+"asddsatemp.tmp").createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
		String cmd="";
		//cmd+=Busybox.getCmd(context);
		executeFile=new File(executeFile.getPath());
		cmd+="if [ ! -f \""+executeFile.getPath()+"\" ]; then \n";
		cmd+="echo \""+context.getText(R.string.no_elf_file)+"\"\n";
		cmd+="else\n";
		cmd+="cd \""+getRunablePath(context)+"\"\n";
		cmd+="rm *.tmp\n";
		cmd+="cd \""+executeFile.getParent()+"\"\n";// TODO
		String tempPath=getTempFilePath(context);
		cmd+="mycp \""+executeFile.getPath()+"\" \""+tempPath+"\"\n";
		cmd+="chmod 777 \""+tempPath+"\"\n";
		cmd+=tempPath+(otherOption!=null?(" "+otherOption):"")+"\n";
		cmd+="echo \n";
		//cmd+="rm \""+tempPath+"\"\n";
		//cmd+="echo \""+context.getText(R.string.program_end)+"\"\n";
		cmd+="if [ -f \""+tempPath+"\" ]; then \n";
		cmd+="rm \""+tempPath+"\"\n";
		cmd+="fi\n";
		cmd+="fi\n";
		mProPocesssName=tempPath;
		return cmd;
	}
	
	public static String getRunCmdProcessName()
	{
		return mProPocesssName;
	}

	public static String getProjectCompilerCmd(Context context,List <File> files,File outFile,@Nullable String otherOption){
		String cmd="";
		File elfFile=new File(outFile.getPath());
		cmd=getCompilerCmd(context,files,elfFile," "+(otherOption!=null?otherOption:""));
		return cmd;
	}

	public static String getProjectCompilerAndRunCmd(Context context,List <File> files,File outFile,@Nullable String otherOption){
		String cmd="";
		File elfFile=new File(outFile.getPath());
		cmd=getCompilerCmd(context,files,elfFile," -static "+(otherOption!=null?otherOption:""));
		cmd+=getRunCmd(context, elfFile);
		return cmd;
	}
	
	public static String getCompilerAndRunCmd(Context context,File file,@Nullable String otherOption){
		LinkedList<File> files=new LinkedList<File>();
		files.add(file);
		String cmd="";
		File elfFile=new File(file.getPath()+".elf");
		cmd=getCompilerCmd(context,files,elfFile," -static "+(otherOption!=null?otherOption:""));
		cmd+=getRunCmd(context, elfFile);
		return cmd;
	}

	public static String getProjectCompilerSoCmd(Context context,List <File> files,File outFile,@Nullable String otherOption){
		String cmd="";
		File elfFile=new File(outFile.getPath());
		cmd=getCompilerCmd(context,files,elfFile," -llog -landroid -lEGL -shared "+(otherOption!=null?otherOption:""));
		return cmd;
	}
	
	public static String getCompilerSoCmd(Context context,File file,@Nullable String otherOption){
		LinkedList<File> files=new LinkedList<File>();
		files.add(file);
		String cmd="";
		File elfFile=new File(file.getPath()+".so");
		// -fuse-ld=bfd 
	/*	cmd=getCompilerCmd(context,files,elfFile," -llog -landroid -lEGL -shared "+(otherOption!=null?otherOption:""));
*/		
/**** androids7修改，使得可以编译jni
cmd=getCompilerCmd(context,files,elfFile,"  -shared "+(otherOption!=null?otherOption:""))
return cmd;
	}
	
	public static String getCompilerSCmd(Context context,File file,File fileTo,@Nullable String otherOption)
	{
		LinkedList<File> files=new LinkedList<File>();
		files.add(file);
		String cmd="";
		cmd=getCompilerCmd(context,files,fileTo," -S "+(otherOption!=null?otherOption:""));
		return cmd;
	}
	
}
