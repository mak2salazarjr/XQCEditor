package person.wangchen11.gnuccompiler;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.cproject.CProject;
import person.wangchen11.xqceditor.R;

import android.content.Context;
import android.os.Environment;

public class GNUCCompiler2 {
	static final String TAG = "GNUCCompiler2";
	public static String getRunablePath(Context context){
		return context.getFilesDir().getAbsolutePath()+File.separatorChar;
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
		return " -Wall -I\""+getIncludeDir()+"\" -I\""+getIncludeDirEx()+"\" -lm -lstdc++ -lsupc++ -lgnustl_static ";
	}
	
	public static String getExportEnvPathCmd(Context context)
	{
		String cmd="";
		cmd+="chmod 777 "+getGccPath(context)+"/*\n";
		cmd+="chmod 777 "+getCcPath(context)+"/*\n";
		cmd+="chmod 777 "+getAbiPath(context)+"/*\n";
		cmd+="export PATH=$PATH:"+getGccPath(context)+"\n";
		cmd+="export PATH=$PATH:"+getCcPath(context)+"\n";
		cmd+="export PATH=$PATH:"+getAbiPath(context)+"\n";
		cmd+="export INCLUDE_C=\""+getIncludeDir()+"\"\n";
		cmd+="export INCLUDE_CPP=\""+getIncludeDirEx()+"\"\n";
		return cmd;
	}

	private static String getFilesString(List <File> files){
		Iterator<File> iterator=files.iterator();
		StringBuilder cmdBuilder = new StringBuilder();
		while(iterator.hasNext()){
			cmdBuilder.append(" \""+iterator.next().getPath()+"\" ");
		}
		return cmdBuilder.toString();
	}
	
	private static String getRelativePath(File file,File dir)
	{
		try {
			String dirPath=dir.getCanonicalPath();
			String filepath=file.getCanonicalPath();
			if(filepath.startsWith(dirPath))
			{
				return filepath.substring(dirPath.length());
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private static List<File> getObjFiles(List <File> files,File objPath,File srcPath) throws Exception
	{
		List<File> retFiles=new LinkedList<File>();
		Iterator<File > iterator = files.iterator();
		while(iterator.hasNext())
		{
			File file = iterator.next();
			String relPath = getRelativePath(file,srcPath);
			if(relPath==null)
			{
				throw new Exception("source file not in src path:"+file.getAbsolutePath());
			}
			File objFile = new File(objPath.getAbsolutePath()+"/"+relPath+".o");
			retFiles.add(objFile);
		}
		return retFiles;
	}
	
	
	public static String getCompilerOnlyCmd(File file,File objFile,String compileOption)
	{
		String cmd = ""
				+"gcc -c "
				+" \""+file.getAbsolutePath()+"\" "
				+" -o \""+objFile.getAbsolutePath()+"\" "
				+getNeedOption()
				+" "+(compileOption!=null?compileOption:"")
				+"\n";
		return cmd;
	}
	
	private static String getCompilerToObjCmd(List <File> files,File objPath,File srcPath,String compileOption) throws Exception
	{
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append("compiler_to_obj_success=1\n");
		
		Iterator<File > iterator = files.iterator();
		while(iterator.hasNext())
		{
			File file = iterator.next();
			String relPath = getRelativePath(file,srcPath);
			if(relPath==null)
			{
				throw new Exception("source file not in src path!");
			}
			File objFile = new File(objPath.getAbsolutePath()+"/"+relPath+".o");
			if(!objFile.isFile() || objFile.lastModified() <= file.lastModified() )
			{
				objFile.getParentFile().mkdirs();
				objFile.delete();

				cmdBuilder.append(
						"echo \""+file.getName()+"\t-->\t"+objFile.getName()+"\"\n"
						+"gcc -c "
						+" \""+file.getAbsolutePath()+"\" "
						+" -o \""+objFile.getAbsolutePath()+"\" "
						+getNeedOption()
						+" "+(compileOption!=null?compileOption:"")
						+"\n"
						+"if [ ! -f \""+objFile.getPath()+"\" ]; then \n"
				      	+"compiler_to_obj_success=0\n"
						+"fi\n");
			}
		}
		cmdBuilder.append("\n");
		return cmdBuilder.toString();
	}
	
	
	
	public static String getCompilerCmd(Context context,CProject project,boolean toSo){
		File outFile = null;
		if(toSo)
			outFile = new File(project.getSoFilePath());
		else
			outFile = new File(project.getBinFilePath());
		outFile.delete();
		outFile.getParentFile().mkdirs();
		File objPath = new File(project.getObjPath());
		File srcPath = new File(project.getSrcPath());
		String otherOption = project.getOtherOption();
		StringBuilder cmdBuilder = new StringBuilder();
		cmdBuilder.append("cd \""+project.getProjectPath()+"\"\n");
		//cmdBuilder.append(getExportEnvPathCmd(context));
		try {
			cmdBuilder.append(getCompilerToObjCmd(project.getAllCFiles(), objPath, srcPath,project.getCompileOption()));
		} catch (Exception e) {
			e.printStackTrace();
			return "echo \"Exception:"+e.getMessage()+"\"\n";
		}
		cmdBuilder.append("if [ \"$compiler_to_obj_success\" = \"1\" ] \n");
		cmdBuilder.append("then\n");
		cmdBuilder.append("echo linking...\n");
		cmdBuilder.append("gcc ");
		try {
			cmdBuilder.append( getFilesString(getObjFiles(project.getAllCFiles(), objPath, srcPath)));
		} catch (Exception e) {
			e.printStackTrace();
			return "echo \"Exception:"+e.getMessage()+"\"\n";
		}
		cmdBuilder.append(" \""+GNUCCompiler.getFixCppObj(context)+"\" ");
		cmdBuilder.append(getNeedOption());
		cmdBuilder.append(" -o \""+outFile.getAbsolutePath()+"\" ");
		if(toSo)
			cmdBuilder.append(" -llog -landroid -lEGL -shared ");
		else
			cmdBuilder.append(" -static ");
		cmdBuilder.append(" "+ (otherOption!=null?otherOption:""));
		cmdBuilder.append("\n");

		cmdBuilder.append("if [ ! -f \""+outFile.getPath()+"\" ]; then \n");
		cmdBuilder.append("echo \""+context.getText(R.string.compilation_fails)+"\"\n");
		cmdBuilder.append("else\n");
		cmdBuilder.append("echo \""+context.getText(R.string.successfully_compiled)+"\"\n");
		cmdBuilder.append("fi\n");
		
		cmdBuilder.append("else\n");
		cmdBuilder.append( "echo \""+context.getText(R.string.compilation_fails)+"\"\n");
		cmdBuilder.append("fi\n");
		return cmdBuilder.toString();
	}
	
}
