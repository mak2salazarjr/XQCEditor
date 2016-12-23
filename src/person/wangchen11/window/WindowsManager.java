package person.wangchen11.window;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import person.wangchen11.help.Help;
import person.wangchen11.window.ext.About;
import person.wangchen11.window.ext.Console;
import person.wangchen11.window.ext.FileBrowser;
import person.wangchen11.window.ext.NetAssist;
import person.wangchen11.window.ext.PHPServerConfig;
import person.wangchen11.window.ext.Setting;
import person.wangchen11.xqceditor.R;


import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;

public class WindowsManager implements View.OnClickListener, android.support.v7.widget.PopupMenu.OnMenuItemClickListener{
	protected static final String TAG="WindowsManager";
	public static int mSelectTitleColor=Color.rgb(0xbf, 0xff, 0x80);
	private Activity mContext;
	private LinkedList<WindowPointer> mWindowPointers=new LinkedList<WindowPointer>();
	private LinkedList<WindowsManagerLintener> mLinteners=new LinkedList<WindowsManagerLintener>();
	private LinearLayout mTitleListView;
	private HorizontalScrollView mScrollView;
	
	private WindowPointer mSelectWindow=null;
	public View getTitleListView(){
		return mScrollView;
	}
	
	public Context getContext(){
		return mContext;
	}
	
	public WindowsManager(Activity context) {
		mContext=context;
		mTitleListView=new LinearLayout(context);
		mScrollView=new HorizontalScrollView(context);
		mScrollView.addView(mTitleListView);
	}
	
	public WindowPointer getSelectWindow(){
		return mSelectWindow;
	}

	public WindowPointer getWindowPointer(int index){
		return mWindowPointers.get(index);
	}

	public WindowPointer getWindowPointer(Window window){
		Iterator< WindowPointer >iterator = mWindowPointers.iterator();
		while(iterator.hasNext())
		{
			WindowPointer pointer=iterator.next();
			if(pointer.mWindow==window)
				return pointer;
		}
		return null;
	}
	
	
	
	public boolean changeWondow(WindowPointer pointer){
		mSelectWindow=pointer;
		sendChangeWindow();
		applyAllTitleStyle();
		return true;
	}
	
	public boolean closeAllWindow(){
		boolean ret = true;
		LinkedList<WindowPointer> linkedList = new LinkedList<WindowPointer>();
		linkedList.addAll(mWindowPointers);
		
		Iterator<WindowPointer> iterator = linkedList.iterator();
		
		while(iterator.hasNext())
		{
			WindowPointer pointer = iterator.next();
			if(! closeWindow(pointer.mWindow))
			{
				ret = false;
			}
		}
		return ret;
	}
	
	public boolean closeWindow(Window window){
		WindowPointer pointer = getWindowPointer(window);
		return closeWindow(pointer);
	}

	public boolean closeWindow(WindowPointer pointer)
	{
		if(pointer!=null)
		{
			if(pointer.mWindow.onClose())
			{
				sendCloseWindow(pointer);
				if(pointer==getSelectWindow())
				{
					WindowPointer pointerTo=null;
					int index=mWindowPointers.indexOf(pointer);
					if(index==-1)
					{
						Log.i(TAG, "index==-1");
						pointerTo=null;
					}
					else
					{
						int pos=0;
						if(index==0)
							pos=1;
						else
						if(index>=1)
							pos=index-1;
						Log.i(TAG, "pos:"+pos);
						if(pos>=0&&pos<mWindowPointers.size())
							pointerTo=mWindowPointers.get(pos);
					}
					if(pointerTo==null && !mWindowPointers.isEmpty())
						pointerTo=mWindowPointers.getFirst();
					mWindowPointers.remove(pointer);
					changeWondow(pointerTo);
				}
				else
				{
					mWindowPointers.remove(pointer);
					applyAllTitleStyle();
				}
				if(mWindowPointers.size()<=0)
				{
					mContext.finish();
				}
				return true;
			}
		}
		return false;
	}
	
	public boolean closeSelectWindow() {
		WindowPointer pointer=getSelectWindow();
		if(pointer!=null)
		{
			return closeWindow(pointer);
		}
		return false;
	}
	
	public boolean addWindow(Window window){
		Iterator<WindowPointer> iterator=mWindowPointers.iterator();
		while(iterator.hasNext())
		{
			WindowPointer pointer=iterator.next();
			if(pointer.mWindow==window)
				return false;
			if(!pointer.mWindow.canAddNewWindow(window))
			{
				changeWondow(pointer);
				return false;
			}
		}
		WindowPointer pointer=new WindowPointer();
		pointer.mWindow=window;
		pointer.mTitle=window.getTitle(mContext);
		TitleView button=new TitleView(mContext);
		button.setOnClickListener(this);
		button.setText(pointer.mTitle);
		pointer.mTitleView=button;
		boolean ret=mWindowPointers.add(pointer);
		if(ret){
			changeWondow(pointer);
			sendAddWindow(pointer);
		}
		return ret;
	}

	private void sendAddWindow(WindowPointer pointer){
		Iterator< WindowsManagerLintener> iterator=mLinteners.iterator();
		while(iterator.hasNext()){
			WindowsManagerLintener windowsManagerLintener=iterator.next();
			windowsManagerLintener.onAddWindow(this,pointer);
		}
	}
	
	private void sendCloseWindow(WindowPointer pointer){
		Iterator< WindowsManagerLintener> iterator=mLinteners.iterator();
		while(iterator.hasNext()){
			WindowsManagerLintener windowsManagerLintener=iterator.next();
			windowsManagerLintener.onCloseWindow(this,pointer);
		}
	}
	
	private void sendChangeWindow(){
		Iterator< WindowsManagerLintener> iterator=mLinteners.iterator();
		while(iterator.hasNext()){
			WindowsManagerLintener windowsManagerLintener=iterator.next();
			windowsManagerLintener.onChangeWindow(this);
		}
	}

	public void sendConfigChanged(){
		applyAllTitleStyle();
		mContext.onContentChanged();
	}
	
	public boolean addListener(WindowsManagerLintener lintener){
		if(lintener==null)
			return false;
		Iterator< WindowsManagerLintener> iterator=mLinteners.iterator();
		while(iterator.hasNext()){
			WindowsManagerLintener windowsManagerLintener=iterator.next();
			if(windowsManagerLintener==lintener)
				return false;
		}
		return mLinteners.add(lintener);
	}
	
	public boolean removeListener(WindowsManagerLintener lintener){
		return mLinteners.remove(lintener);
	}

	@Override
	public void onClick(View v) {
		
		Object obj=v.getTag();
		if(obj instanceof WindowPointer)
		{
			WindowPointer pointer=(WindowPointer) obj;
			WindowPointer pointerSelect=getSelectWindow();
			if(pointer.mTitleView.isInColseArea())
			{
				closeWindow(pointer);
			}else
			if(pointerSelect ==null || pointerSelect!=pointer)
			{
				changeWondow(pointer);
			}
			else{
				PopupMenu popupMenu=new PopupMenu(mContext, pointerSelect.mTitleView);
				popupMenu.setOnMenuItemClickListener(this);
				Menu menu=popupMenu.getMenu();
				menu.add(0,R.string.close,0, mContext.getResources().getText(R.string.close));
				menu.add(0,R.string.move_to_left,0, mContext.getResources().getText(R.string.move_to_left));
				menu.add(0,R.string.move_to_right,0, mContext.getResources().getText(R.string.move_to_right));
				popupMenu.show();
			}
		}
	}
	
	public boolean onBackPressed(){
		WindowPointer pointer= getSelectWindow();
		if(pointer==null)
			return false;
		return pointer.mWindow.onBackPressed();
	}
	
	public interface WindowsManagerLintener{
		public void onChangeWindow(WindowsManager manager);
		public void onAddWindow(WindowsManager manager,WindowPointer pointer);
		public void onCloseWindow(WindowsManager manager,WindowPointer pointer);
	}

	@Override
	public boolean onMenuItemClick(MenuItem arg0) {
		return onMenuItemClick(arg0.getItemId());
	}
	
	public boolean onMenuItemClick(int id){
		switch(id){
		case R.string.console:
			addWindow(new Console(this));
			break;
		case R.string.open:
			addWindow(new FileBrowser(this));
			break;
		case R.string.php_config:
			addWindow(new PHPServerConfig());
			break;
		case R.string.net_assist:
			addWindow(new NetAssist());
			break;
		case R.string.close:
			closeSelectWindow();
			break;
		case R.string.about:
			addWindow(new About());
			break;
		case R.string.help:
			addWindow(new Help());
			break;
		case R.string.setting:
			addWindow(new Setting(this));
			break;
		case R.string.exit:
			mTitleListView.getHandler().post(new Runnable() {
				@Override
				public void run() {
					if(closeAllWindow())
						mContext.finish();
				}
			});
			break;
		case R.string.move_to_left:
			int index=mWindowPointers.indexOf(getSelectWindow());
			if(index<1)
				break;
			WindowPointer now=getSelectWindow();
			WindowPointer left=mWindowPointers.get(index-1);
			now.changeData(left);
			changeWondow(left);
			break;
		case R.string.move_to_right:
			index=mWindowPointers.indexOf(getSelectWindow());
			if(index+1>=mWindowPointers.size())
				break;
			now=getSelectWindow();
			WindowPointer right=mWindowPointers.get(index+1);
			now.changeData(right);
			changeWondow(right);
			break;
		default:
			if(getSelectWindow()==null)
				return false;
			return getSelectWindow().mWindow.onMenuItemClick(id);
		}
		return true;
	}
	
	public List<MenuTag> getMenuTags(){
		List <MenuTag> menuTags=new LinkedList<MenuTag>();
		WindowPointer pointer= getSelectWindow();
		if(pointer!=null)
		{
			List<MenuTag> childMenuTag=pointer.mWindow.getMenuTags();
			if(childMenuTag!=null)
				menuTags.addAll(childMenuTag);
		}
		menuTags.add(new MenuTag(R.string.console, mContext.getResources().getText(R.string.console)));
		menuTags.add(new MenuTag(R.string.open, mContext.getResources().getText(R.string.open)));
		menuTags.add(new MenuTag(R.string.php_config, mContext.getResources().getText(R.string.php_config)));
		menuTags.add(new MenuTag(R.string.net_assist, mContext.getResources().getText(R.string.net_assist)));
		menuTags.add(new MenuTag(R.string.setting, mContext.getResources().getText(R.string.setting)));
		menuTags.add(new MenuTag(R.string.help, mContext.getResources().getText(R.string.help)));
		menuTags.add(new MenuTag(R.string.about, mContext.getResources().getText(R.string.about)));
		menuTags.add(new MenuTag(R.string.exit, mContext.getResources().getText(R.string.exit)));
		return menuTags;
	}
	
	public void onTitleChanged(Window window){
		Iterator<WindowPointer> iterator=mWindowPointers.iterator();
		while(iterator.hasNext()){
			WindowPointer pointer=iterator.next();
			if(pointer.mWindow==window){
				pointer.mTitleView.setText(window.getTitle(mContext));
			}
		}
	}
	
	private void applyAllTitleStyle(){
		mTitleListView.removeAllViews();
		Iterator<WindowPointer> iterator=mWindowPointers.iterator();
		mTitleListView.setBackgroundColor(Color.TRANSPARENT);
		float densityDpi=mContext.getResources().getDisplayMetrics().density;
		while(iterator.hasNext()){
			WindowPointer pointer=iterator.next();
			pointer.mTitleView.setPadding((int)(densityDpi*16),(int)( densityDpi*4), (int)(densityDpi*16),(int)( densityDpi*4));
			if(pointer.mTitleView instanceof TitleView)
				((TitleView)pointer.mTitleView).setGravity(Gravity.CENTER_VERTICAL);
			pointer.mTitleView.setTag(pointer);
			if(mSelectWindow == pointer){
				pointer.mTitleView.setBackgroundColor(mSelectTitleColor);
			}else{
				pointer.mTitleView.setBackgroundColor(Color.TRANSPARENT);
			}
			LayoutParams layoutParams=new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
			mTitleListView.addView(pointer.mTitleView,layoutParams);//,LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT);
		}
		Setting.applySettingConfigToAllView(mTitleListView);
	}
}


