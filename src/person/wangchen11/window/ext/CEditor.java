package person.wangchen11.window.ext;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.support.v4.app.Fragment;
import android.widget.Toast;
import person.wangchen11.cproject.CProject;
import person.wangchen11.filebrowser.FileWork;
import person.wangchen11.gnuccompiler.GNUCCompiler;
import person.wangchen11.gnuccompiler.GNUCCompiler2;
import person.wangchen11.packageapk.DebugApk;
import person.wangchen11.packageapk.PackageApk;
import person.wangchen11.phpconfig.PHPConfig;
import person.wangchen11.qeditor.EditorFregment;
import person.wangchen11.qeditor.OnRunButtonClickListener;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowPointer;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.window.WindowsManager.WindowsManagerLintener;
import person.wangchen11.xqceditor.R;

public class CEditor implements Window, EditorFregment.ChangeFlagChanged, OnClickListener ,WindowsManagerLintener, OnRunButtonClickListener{
	protected static final String TAG="CEditor";
	private EditorFregment mCEditorFregment;
	private WindowsManager mWindowsManager;
	private boolean mIsAlive=true;
	
	public CEditor(WindowsManager windowsManager,File file) {
		mWindowsManager=windowsManager;
		mCEditorFregment=new EditorFregment(file);
		mCEditorFregment.setChangeFlagChanged(this);
		mCEditorFregment.setOnRunButtonClickListener(this);
		mWindowsManager.addListener(this);
	}
	
	public CEditor(WindowsManager windowsManager) {
		mWindowsManager=windowsManager;
		mCEditorFregment=new EditorFregment();
		mCEditorFregment.setChangeFlagChanged(this);
		mCEditorFregment.setOnRunButtonClickListener(this);
		mWindowsManager.addListener(this);
	}

	public File getFile(){
		return mCEditorFregment.getFile();
	}
	@Override
	public Fragment getFragment() {
		return mCEditorFregment;
	}

	@Override
	public CharSequence getTitle(Context context) {
		if(mCEditorFregment.getFile()==null)
			return context.getText(R.string.no_title);
		return (mCEditorFregment.isChanged()?"*":"")+mCEditorFregment.getFile().getName();
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}
	
	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof CEditor )
		{
			CEditor editor=(CEditor) window;
			if(getFile()!=null && editor.getFile()!=null && getFile().equals(editor.getFile()))
				return false;
		}
		return true;
	}

	@Override
	public boolean onClose() {
		mWindowsManager.removeListener(this);
		mCEditorFregment.closeInputMethod();
		if(mIsAlive)
		if(mCEditorFregment.isChanged() && mCEditorFregment.getFile()!=null){
			Builder alertDialog=new AlertDialog.Builder(mWindowsManager.getContext());
			alertDialog.setCancelable(false);
			alertDialog.setTitle(R.string.file_not_save);
			alertDialog.setMessage( 
					mWindowsManager.getContext().getText(R.string.file)+
					mCEditorFregment.getFile().getName()+
					mWindowsManager.getContext().getText(R.string.unsaved)+"\n"+
					mCEditorFregment.getFile().getPath());
			alertDialog.setNegativeButton(
					mWindowsManager.getContext().getText(R.string.cancel)
					, this);
			alertDialog.setNeutralButton(
					mWindowsManager.getContext().getText(R.string.save_and_quit)
					, this );
			alertDialog.setPositiveButton(
					mWindowsManager.getContext().getText(R.string.force_close)
					, this);
			alertDialog.create();
			alertDialog.show();
			return false;
		}
		return true;
	}

	@SuppressLint("DefaultLocale")
	@Override
	public List<MenuTag> getMenuTags() {
		LinkedList<MenuTag> menuTags=new LinkedList<MenuTag>();
		File file=mCEditorFregment.getFile();
		if(file!=null)
		{
			String name = file.getName().toLowerCase();
			if(name.equals("makefile"))
			{
				menuTags.add(new MenuTag( R.string.make_j8,mCEditorFregment.getText(R.string.make_j8)));
				menuTags.add(new MenuTag( R.string.make_b_j8,mCEditorFregment.getText(R.string.make_b_j8)));
				menuTags.add(new MenuTag( R.string.make_clean,mCEditorFregment.getText(R.string.make_clean)));
				menuTags.add(new MenuTag( R.string.make_option,mCEditorFregment.getText(R.string.make_option)));
				return menuTags;
			}
			
			if(name.endsWith(".lua"))
			{
				menuTags.add(new MenuTag( R.string.run_lua,mCEditorFregment.getText(R.string.run_lua)));
				return menuTags;
			}
			if(name.endsWith(".sh"))
			{
				menuTags.add(new MenuTag( R.string.run_shell, mCEditorFregment.getText(R.string.run_shell) ));
				menuTags.add(new MenuTag( R.string.run_shell_as_root, mCEditorFregment.getText(R.string.run_shell_as_root )));
				return menuTags;
			}
			
			if( name.endsWith(".php")||
					name.endsWith(".html")||
					name.endsWith(".htm") )
			{
				menuTags.add(new MenuTag(R.string.remote_browsing,mCEditorFregment.getText(R.string.remote_browsing)));
				menuTags.add(new MenuTag(R.string.local_browsing,mCEditorFregment.getText(R.string.local_browsing)));
			}
			
			if( name.endsWith(".c")||
					name.endsWith(".cpp")||
					name.endsWith(".s")||
					name.endsWith(".h")||
					name.endsWith(".xml")||
					name.endsWith(".project") )
			{
				CProject cProject=CProject.findCProjectByFile(mCEditorFregment.getFile());
				if(cProject!=null)
				{
					if(cProject.isGuiProject()&&(cProject.getDebugType()!=null&&cProject.getDebugType().length()>0))
					{
					}
					else
					{
						menuTags.add(new MenuTag(R.string.build_and_run,mCEditorFregment.getActivity().getResources().getText(R.string.build_and_run) ));
					}
					
					menuTags.add(new MenuTag( R.string.pack_and_run, mCEditorFregment.getActivity().getResources().getText(R.string.pack_and_run) ));
				}
				else
				{
					menuTags.add(new MenuTag(R.string.build_and_run,mCEditorFregment.getActivity().getResources().getText(R.string.build_and_run) ));
				}
				menuTags.add(new MenuTag(R.string.build_so,mCEditorFregment.getActivity().getResources().getText(R.string.build_so) ));
				
				if(name.endsWith(".c")||
						name.endsWith(".cpp"))
				{
					menuTags.add(new MenuTag(R.string.complie_to_s,mCEditorFregment.getActivity().getResources().getText(R.string.complie_to_s) ));
					menuTags.add(new MenuTag(R.string.code_format, mCEditorFregment.getActivity().getResources().getText(R.string.code_format)));
				}
				if(cProject!=null)
				{
					menuTags.add(new MenuTag(R.string.clean_objs, mCEditorFregment.getActivity().getResources().getText(R.string.clean_objs)));
				}
			}
		}
		
		return menuTags;
	}
	
	@Override
	public boolean onMenuItemClick(int id) {
		switch (id) {
		case R.string.make_j8:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n" +
						"make -j8\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		case R.string.make_b_j8:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n" +
						"make -B -j8\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		case R.string.make_clean:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n" +
						"make clean\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		case R.string.make_option:
			{
				String cmd = "";
				cmd = "cd \"" + getFile().getParent() + "\"\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		case R.string.remote_browsing:
		case R.string.local_browsing:
		case R.string.pack_and_run:
		case R.string.build_and_run:
		case R.string.run_shell:
		case R.string.run_shell_as_root:
		case R.string.complie_to_s:
		case R.string.build_so:
			if(mCEditorFregment.isChanged())
			if(!mCEditorFregment.save()){
				Toast.makeText(mWindowsManager.getContext(), R.string.save_fail, Toast.LENGTH_SHORT).show();
			}
			break;
		default:
			break;
		}
		
		CProject cProject;
		File file = mCEditorFregment.getFile();
		switch(id){
		case R.string.remote_browsing:
			if(file!=null)
			{
				PHPConfig config=PHPConfig.load(mWindowsManager.getContext());
				String url=config.getUrl(file);
				if(url==null)
					Toast.makeText(mWindowsManager.getContext(), "该文件不在网站目录内!", Toast.LENGTH_SHORT).show();
				else
					mWindowsManager.addWindow(new BrowserWindow(mWindowsManager, "http://127.0.0.1:"+config.HTTPD_PORT+"/"+url, ""+config.HTTPD_PORT+":"+file.getName() ));
			}
			break;
		case R.string.local_browsing:
			if(file!=null)
			{
				mWindowsManager.addWindow(new BrowserWindow(mWindowsManager, "file://"+mCEditorFregment.getFile().getAbsolutePath(), "file:"+file.getName() ));
			}
			break;
		case R.string.pack_and_run:
			cProject=CProject.findCProjectByFile(mCEditorFregment.getFile());
			if(cProject!=null )
			{
				new PackageApk(mWindowsManager.getContext(), cProject).start();
			}
			break;
		case R.string.build_and_run:
			if(mCEditorFregment.getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(mCEditorFregment.getFile());
				String cmd="";
				String processName=null;
				if(cProject!=null)
				{
					if(cProject.isGuiProject())
					{
						new DebugApk(mWindowsManager.getContext(), cProject).start();
						break;
					}
					else
					{
						List<File > files=cProject.getAllCFiles();
						if(files.size()>0)
						{

							cmd+=GNUCCompiler2.getCompilerCmd( mWindowsManager.getContext(),cProject,false);
							cmd+=GNUCCompiler.getRunCmd(mWindowsManager.getContext(), new File(cProject.getBinFilePath()));
							//cmd=GNUCCompiler.getProjectCompilerAndRunCmd(mWindowsManager.getContext(), files, new File(cProject.getBinFilePath()),  cProject.getOtherOption() );
							processName=GNUCCompiler.getRunCmdProcessName();
						}
						else
							cmd="echo '"+
									mWindowsManager.getContext().getText(R.string.c_file_not_found)+
									"'\n";
					}
				}
				else{
					//not a project 
					cmd = GNUCCompiler.getCompilerAndRunCmd(mWindowsManager.getContext(), mCEditorFregment.getFile(),null);
					processName=GNUCCompiler.getRunCmdProcessName();
					//cmd=TinyCCompiler.getCompilerAndRunCmd(mWindowsManager.getContext(), mCEditorFregment.getFile(),null);
				}
				Console console=new Console(mWindowsManager,cmd,true,getFile().getParent());
				console.setKillProcessName(processName);
				mWindowsManager.addWindow(console);
			}
			break;
		case R.string.run_shell:
			if(mCEditorFregment.getFile()!=null)
			{
				String cmd="";
				cmd+=GNUCCompiler.getRunCmd(mCEditorFregment.getActivity(), mCEditorFregment.getFile());
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		case R.string.run_shell_as_root:
			if(mCEditorFregment.getFile()!=null)
			{
				String cmd="";
				//cmd+="export APP_PATH=\""+mWindowsManager.getContext().getFilesDir().getAbsolutePath()+"\"\n";
				cmd+=GNUCCompiler.getRunCmd(mCEditorFregment.getActivity(), mCEditorFregment.getFile());
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,true,getFile().getParent()));
			}
			break;
		case R.string.complie_to_s:
			if(mCEditorFregment.getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(mCEditorFregment.getFile());
				String cmd="";
				if(cProject!=null)
				{
					List<File > files=cProject.getAllCFiles();
					if(files.size()>0)
					{
						file = mCEditorFregment.getFile();
						File fileTo = new File(cProject.getBinPath()+"/"+file.getName()+".s");
						cmd = GNUCCompiler.getCompilerSCmd(mWindowsManager.getContext(),file ,fileTo,null);
					}
					else
						cmd="echo '"+
								mWindowsManager.getContext().getText(R.string.c_file_not_found)+
								"'\n";
				}else
				{
					file = mCEditorFregment.getFile();
					File fileTo = new File(file.getAbsolutePath()+".s");
					cmd = GNUCCompiler.getCompilerSCmd(mWindowsManager.getContext(),file ,fileTo,null);
				}
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		case R.string.build_so:
			if(mCEditorFregment.getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(mCEditorFregment.getFile());
				String cmd="";
				if(cProject!=null)
				{
					List<File > files=cProject.getAllCFiles();
					if(files.size()>0)
					{
						cmd+=GNUCCompiler2.getCompilerCmd( mWindowsManager.getContext(),cProject,true);
						//cmd=GNUCCompiler.getProjectCompilerSoCmd(mWindowsManager.getContext(), files, new File(cProject.getSoFilePath()), cProject.getOtherOption() );
					}
					else
						cmd="echo '"+
								mWindowsManager.getContext().getText(R.string.c_file_not_found)+
								"'\n";
				}else
				{
					cmd = GNUCCompiler.getCompilerSoCmd(mWindowsManager.getContext(), mCEditorFregment.getFile(),null);
				}
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		case R.string.code_format:
			mCEditorFregment.codeFormat();
			break;
		case R.string.clean_objs:
			if(mCEditorFregment.getFile()!=null)
			{
				cProject=CProject.findCProjectByFile(mCEditorFregment.getFile());
				if(cProject!=null)
				{
					FileWork.deleteFile(new File(cProject.getObjPath()));
				}
			}
			break;
		case R.string.run_lua:
			if(file!=null)
			{
				String cmd="cd \""+file.getParent()+"\"\n";
				cmd+="lua \""+file.getAbsolutePath()+"\"\n";
				mWindowsManager.addWindow(new Console(mWindowsManager,cmd,true,getFile().getParent()));
			}
			break;
		}
		return true;
	}
	
	@Override
	public void onChangeFlagChanged() {
		mWindowsManager.onTitleChanged(this);
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEGATIVE://cancel 
			
			break;
		case DialogInterface.BUTTON_NEUTRAL://quit & save
			if(mCEditorFregment.save()){
				mIsAlive=false;
				mWindowsManager.closeWindow(this);
			}else{
				Toast.makeText(mWindowsManager.getContext(), R.string.save_fail, Toast.LENGTH_SHORT).show();
			}
			break;
		case DialogInterface.BUTTON_POSITIVE://focre close 
			mIsAlive=false;
			mWindowsManager.closeWindow(this);
			break;

		default:
			break;
		}
	}

	@Override
	public void onChangeWindow(WindowsManager manager) {
		if(mCEditorFregment!=null)
			mCEditorFregment.closeInputMethod();
	}

	@Override
	public void onAddWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@Override
	public void onCloseWindow(WindowsManager manager, WindowPointer pointer) {
	}

	@SuppressLint("DefaultLocale")
	@Override
	public boolean onRunButtonClick() {
		
		File file=mCEditorFregment.getFile();
		if(file!=null)
		{
			String name = file.getName().toLowerCase();
			

			if(name.equals("makefile"))
			{
				this.onMenuItemClick(R.string.make_j8);
				return true;
			}
			
			if(name.endsWith(".lua"))
			{
				this.onMenuItemClick(R.string.run_lua);
				return true;
			}
			
			if(name.endsWith(".sh"))
			{
				this.onMenuItemClick(R.string.run_shell);
				return true;
			}
			
			if( name.endsWith(".php")||
					name.endsWith(".html")||
					name.endsWith(".htm") )
			{
				this.onMenuItemClick(R.string.remote_browsing);
				return true;
			}
			
			if( name.endsWith(".c")||
					name.endsWith(".cpp")||
					name.endsWith(".s")||
					name.endsWith(".h")||
					name.endsWith(".xml")||
					name.endsWith(".project") )
			{
				CProject cProject=CProject.findCProjectByFile(mCEditorFregment.getFile());
				if(cProject!=null)
				{
					if(cProject.isGuiProject()&&(cProject.getDebugType()!=null&&cProject.getDebugType().length()>0))
					{
						this.onMenuItemClick(R.string.pack_and_run);
					}
					else
						this.onMenuItemClick(R.string.build_and_run);
					return true;
				}
				else
				{
					this.onMenuItemClick(R.string.build_and_run);
					return true;
				}
			}
		}
		
		return false;
	}

	@Override
	public String[] getResumeCmd() {
		String []cmd = new String[2];
		cmd[0] = getFile()!=null?getFile().getPath():null;
		cmd[1] = null;
		return cmd;
	}

	@Override
	public void resumeByCmd(String []cmd) {
		if(cmd==null)
			return;
		if(cmd.length!=2)
			return;
		mCEditorFregment = new EditorFregment(new File(cmd[0]));
	}

}
