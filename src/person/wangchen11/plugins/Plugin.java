package person.wangchen11.plugins;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class Plugin {
	private File   mFile;
	private String mName;
	private String mAlias;
	private String mVersion;
	
	public String getName(){
		return mName;
	}
	
	public String getAlias(){
		return mAlias;
	}
	
	public String getVersion(){
		return mVersion;
	}
	
	public Plugin(File file) {
		mFile = file;
		mName = file.getName();
	}
	
	public String getExportPathCmd(){
		String cmd = "export PLUGHOME=\'"+mFile.getPath()+"/\'\n";
		return cmd;
	}
	
	public String getInstallCmd(){
		try {
			String all = readAll(new File(mFile, "install.sh"));
			if(all == null)
				return "";
			return getExportPathCmd()+all;
		} catch (Exception e) {
			return "";
		}
	}
	
	public String getSourceCmd(){
		try {
			String all = readAll(new File(mFile, "source.sh"));
			if(all == null)
				return "";
			return getExportPathCmd()+all;
		} catch (Exception e) {
			return "";
		}
	}
	
	private String readAll(File file){ 
        Long filelength = file.length();  
        byte[] filecontent = new byte[filelength.intValue()];  
        try {  
            FileInputStream in = new FileInputStream(file);  
            in.read(filecontent);  
            in.close();  
        } catch (FileNotFoundException e) {  
            e.printStackTrace();  
        } catch (IOException e) {  
            e.printStackTrace();  
        }
        String encoding = guessEncoding(filecontent,0,filecontent.length);
        if(encoding == null)
        	encoding = "UTF-8";
        try {  
            return new String(filecontent, encoding);  
        } catch (UnsupportedEncodingException e) {  
            System.err.println("The OS does not support " + encoding);  
            e.printStackTrace();  
            return null;  
        }  
    }

	private static String guessEncoding(byte[] bytes,int offset,int length) {
	    org.mozilla.universalchardet.UniversalDetector detector =  
	        new org.mozilla.universalchardet.UniversalDetector(null);  
	    detector.handleData(bytes, offset, length);  
	    detector.dataEnd();  
	    String encoding = detector.getDetectedCharset();  
	    detector.reset();
	    return encoding;  
	}
	
}
