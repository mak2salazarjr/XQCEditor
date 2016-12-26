package person.wangchen11.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileUtil {
	public static String getAll(String fileName,String charSet){
		File file=new File(fileName);
		try {
			FileInputStream fileInputStream=new FileInputStream(file);
			try {
				byte data[]=new byte[(int) file.length()];
				try {
					int readLen=fileInputStream.read(data);
					if(charSet!=null)
						return new String(data,0,readLen,charSet);
					return new String(data,0,readLen);
				} catch (IOException e) {
					e.printStackTrace();
				}
			} catch (Error e) {
			}
			finally{
					try {
						fileInputStream.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static boolean setFileExecutable(File file)
	{
		return file.setExecutable(true, true);
	}
	
	public static void setFileAllChildsExecutable(File file)
	{
		if(file.isFile())
		{
			setFileExecutable(file);
			return ;
		}
		if(file.isDirectory())
		{
			File []files = file.listFiles();
			if(files!=null)
			{
				for(File item : files)
				{
					setFileAllChildsExecutable(item);
				}
			}
		}
	}
}
