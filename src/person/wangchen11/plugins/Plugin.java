package person.wangchen11.plugins;

import java.io.File;

public class Plugin {
	private File   mFile;
	private String mName;
	private String mAlias;
	private String mVersion;
	public Plugin(File file) {
		mFile = file;
		mName = file.getName();
	}
	
	public String getInstallCmd(){
		return "";
	}
	
	public String getSourceCmd(){
		return "";
	}
}
