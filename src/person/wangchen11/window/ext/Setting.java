package person.wangchen11.window.ext;

import jackpal.androidterm.TermView;
import jackpal.androidterm.emulatorview.ColorScheme;

import java.util.List;

import cn.waps.UpdatePointsListener;

import person.wangchen11.editor.codeedittext.CodeStyleAdapter;
import person.wangchen11.editor.edittext.EditableWithLayout;
import person.wangchen11.editor.edittext.MyEditText;
import person.wangchen11.qeditor.EditorFregment;
import person.wangchen11.waps.Key;
import person.wangchen11.waps.Waps;
import person.wangchen11.window.MenuTag;
import person.wangchen11.window.TitleView;
import person.wangchen11.window.Window;
import person.wangchen11.window.WindowsManager;
import person.wangchen11.xqceditor.R;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.SwitchCompat;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Setting extends Fragment implements Window, TextWatcher, OnClickListener, OnCheckedChangeListener,UpdatePointsListener{
	public static final String ConfigName="ceditor_config";
	RelativeLayout mRelativeLayout;
	public static Config mConfig = new Config();
	private WindowsManager mWindowsManager;
	public Setting(WindowsManager windowsManager)
	{
		mWindowsManager = windowsManager;
	}
	
	@SuppressLint("InflateParams")
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		mRelativeLayout=(RelativeLayout) inflater.inflate(R.layout.fragment_setting, null);
		Waps.updatePoints(mRelativeLayout.getContext(), this);
		mConfig=loadConfig(getActivity());
		refEditView();
		refColorView();
		refSwitchView();
		((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_scl))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_lh_scl))).addTextChangedListener(this);
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_kw_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_pk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_wd_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_cs_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_cm_col))).addTextChangedListener(this);
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_qk_ipt))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_til_bk_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_til_sl_col))).addTextChangedListener(this);
		((EditText)(mRelativeLayout.findViewById(R.id.et_close_col))).addTextChangedListener(this);

		((Button)(mRelativeLayout.findViewById(R.id.button_close_ad))).setOnClickListener(this);
		((Button)(mRelativeLayout.findViewById(R.id.button_show_ad))).setOnClickListener(this);
		((Button)(mRelativeLayout.findViewById(R.id.button_ok))).setOnClickListener(this);
		((Button)(mRelativeLayout.findViewById(R.id.button_to_default))).setOnClickListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.high_light_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.use_nice_font_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.quick_close_window_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.use_new_console_switch))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.title_at_head))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.ctrl_at_head))).setOnCheckedChangeListener(this);
		((SwitchCompat)(mRelativeLayout.findViewById(R.id.animation))).setOnCheckedChangeListener(this);
		
		return mRelativeLayout;
	}
	
	public void refEditView(){
		((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col))).setText(String.format("%08x", mConfig.mEditorConfig.mBackGroundColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_col))).setText(String.format("%08x", mConfig.mEditorConfig.mBaseFontColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_ft_scl))).setText(String.format("%.2f", mConfig.mEditorConfig.mFontScale ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_lh_scl))).setText(String.format("%.2f", mConfig.mEditorConfig.mLineScale ));
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_kw_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mKeywordsColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_pk_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mProKeywordsColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_wd_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mWordsColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_cs_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mConstantColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_cm_col))).setText(String.format("%08x", mConfig.mCEditorConfig.mCommentsColor ));
		
		((EditText)(mRelativeLayout.findViewById(R.id.et_qk_ipt))).setText(mConfig.mOtherConfig.mQuickInput);

		((EditText)(mRelativeLayout.findViewById(R.id.et_til_bk_col))).setText(String.format("%08x", mConfig.mOtherConfig.mTitleBarColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_til_sl_col))).setText(String.format("%08x", mConfig.mOtherConfig.mSelectTitleColor ));
		((EditText)(mRelativeLayout.findViewById(R.id.et_close_col))).setText(String.format("%08x", mConfig.mOtherConfig.mQuickCloseColor ));
	}
	
	public void loadEditView(){
		EditText editText;
		String str;
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_bk_col)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mBackGroundColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_ft_col)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mBaseFontColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_ft_scl)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mFontScale=StrToFloatWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_lh_scl)));
		str=editText.getText().toString();
		mConfig.mEditorConfig.mLineScale=StrToFloatWithTry(str);
		
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_kw_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mKeywordsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_pk_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mProKeywordsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_wd_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mWordsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_cs_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mConstantColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_cm_col)));
		str=editText.getText().toString();
		mConfig.mCEditorConfig.mCommentsColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_qk_ipt)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mQuickInput=str;
		

		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_til_bk_col)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mTitleBarColor=HexStrToIntWithTry(str);
		
		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_til_sl_col)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mSelectTitleColor=HexStrToIntWithTry(str);

		editText=((EditText)(mRelativeLayout.findViewById(R.id.et_close_col)));
		str=editText.getText().toString();
		mConfig.mOtherConfig.mQuickCloseColor=HexStrToIntWithTry(str);
	}
	
	public static int HexStrToIntWithTry(String str){
		try {
			return (int) Long.parseLong(str, 16);
			
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}

	public static float StrToFloatWithTry(String str){
		try {
			return Float.parseFloat(str);
		} catch (Exception e) {
			e.printStackTrace();
			return 0;
		}
	}
	
	public void refColorView(){
		mRelativeLayout.findViewById(R.id.bk_col).setBackgroundColor(mConfig.mEditorConfig.mBackGroundColor);
		mRelativeLayout.findViewById(R.id.ft_col).setBackgroundColor(mConfig.mEditorConfig.mBaseFontColor);

		mRelativeLayout.findViewById(R.id.kw_col).setBackgroundColor(mConfig.mCEditorConfig.mKeywordsColor);
		mRelativeLayout.findViewById(R.id.pk_col).setBackgroundColor(mConfig.mCEditorConfig.mProKeywordsColor);
		mRelativeLayout.findViewById(R.id.wd_col).setBackgroundColor(mConfig.mCEditorConfig.mWordsColor);
		mRelativeLayout.findViewById(R.id.cs_col).setBackgroundColor(mConfig.mCEditorConfig.mConstantColor);
		mRelativeLayout.findViewById(R.id.cm_col).setBackgroundColor(mConfig.mCEditorConfig.mCommentsColor);
		
		mRelativeLayout.findViewById(R.id.til_bk_col).setBackgroundColor(mConfig.mOtherConfig.mTitleBarColor);
		mRelativeLayout.findViewById(R.id.til_sl_col).setBackgroundColor(mConfig.mOtherConfig.mSelectTitleColor);
		mRelativeLayout.findViewById(R.id.close_col).setBackgroundColor(mConfig.mOtherConfig.mQuickCloseColor);
	}
	
	public void loadSwitchView()
	{
		mConfig.mCEditorConfig.mEnableHighLight=((SwitchCompat)mRelativeLayout.findViewById(R.id.high_light_switch)).isChecked();
		mConfig.mEditorConfig.mUseNiceFont=((SwitchCompat)mRelativeLayout.findViewById(R.id.use_nice_font_switch)).isChecked();
		mConfig.mOtherConfig.mQuickCloseEnable=((SwitchCompat)mRelativeLayout.findViewById(R.id.quick_close_window_switch)).isChecked();
		mConfig.mOtherConfig.mNewConsoleEnable=((SwitchCompat)mRelativeLayout.findViewById(R.id.use_new_console_switch)).isChecked();
		mConfig.mOtherConfig.mTitleAtHead=((SwitchCompat)mRelativeLayout.findViewById(R.id.title_at_head)).isChecked();
		mConfig.mOtherConfig.mCtrlAtHead=((SwitchCompat)mRelativeLayout.findViewById(R.id.ctrl_at_head)).isChecked();
		mConfig.mOtherConfig.mAnimation=((SwitchCompat)mRelativeLayout.findViewById(R.id.animation)).isChecked();
	}
	
	public void refSwitchView(){
		((SwitchCompat)mRelativeLayout.findViewById(R.id.high_light_switch)).setChecked(mConfig.mCEditorConfig.mEnableHighLight);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.use_nice_font_switch)).setChecked(mConfig.mEditorConfig.mUseNiceFont);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.quick_close_window_switch)).setChecked(mConfig.mOtherConfig.mQuickCloseEnable);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.use_new_console_switch)).setChecked(mConfig.mOtherConfig.mNewConsoleEnable);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.title_at_head)).setChecked(mConfig.mOtherConfig.mTitleAtHead);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.ctrl_at_head)).setChecked(mConfig.mOtherConfig.mCtrlAtHead);
		((SwitchCompat)mRelativeLayout.findViewById(R.id.animation)).setChecked(mConfig.mOtherConfig.mAnimation);
	}
	
	@Override
	public Fragment getFragment() {
		return this;
	}

	@Override
	public CharSequence getTitle(Context context) {
		return context.getText(R.string.setting);//"设置";
	}

	@Override
	public boolean onBackPressed() {
		return false;
	}

	@Override
	public boolean canAddNewWindow(Window window) {
		if(window instanceof Setting)
			return false;
		return true;
	}

	@Override
	public boolean onClose() {
		return true;
	}

	@Override
	public List<MenuTag> getMenuTags() {
		return null;
	}

	@Override
	public boolean onMenuItemClick(int id) {
		return false;
	}
	
	public static Config loadConfig(Context context){
		SharedPreferences sharedPreferences=context.getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		return Config.load(sharedPreferences);
	}

	public static void applyChangeDefault(Config config){
		CodeStyleAdapter.mCommentsColor=config.mCEditorConfig.mCommentsColor;
		CodeStyleAdapter.mConstantColor=config.mCEditorConfig.mConstantColor;
		CodeStyleAdapter.mKeywordsColor=config.mCEditorConfig.mKeywordsColor;
		CodeStyleAdapter.mProKeywordsColor=config.mCEditorConfig.mProKeywordsColor;
		CodeStyleAdapter.mWordsColor=config.mCEditorConfig.mWordsColor;
		MyEditText.mFontScale=config.mEditorConfig.mFontScale;
		MyEditText.mLineScale=config.mEditorConfig.mLineScale;
		CodeStyleAdapter.refColorSpan();
		EditorFregment.mQuickInput=config.mOtherConfig.mQuickInput;
		WindowsManager.mSelectTitleColor=config.mOtherConfig.mSelectTitleColor;
		EditableWithLayout.mEnableHightLight=config.mCEditorConfig.mEnableHighLight;
		mConfig = config;
	}
	
	public void save(){
		SharedPreferences sharedPreferences = getActivity().getSharedPreferences(ConfigName, Context.MODE_PRIVATE);
		Editor editor=sharedPreferences.edit();
		mConfig.save(editor);
		editor.commit();
	}
	
	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
	}

	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
	}

	@Override
	public void afterTextChanged(Editable s) {
		mRelativeLayout.getHandler().post(new Runnable() {
			@Override
			public void run() {
				loadEditView();
				refColorView();
				refSwitchView();
				mWindowsManager.sendConfigChanged();
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button_close_ad:
			if(Key.hasRealKey(mRelativeLayout.getContext()))
			{
				showToast( "您已经关闭广告无需再次关闭。\n关闭广告后重启应用生效！");
			}
			else
			{
				if(mPoints<200)
				{
					showToast("需要达到200积分才能关闭广告！\n请先获取积分！");
				}
				else
				{
					if(Key.createKey(mRelativeLayout.getContext()))
					{
						showToast("关闭广告成功！\n重启应用生效！");
					}
					else
					{
						showToast("关闭广告失败！");
					}
				}
			}
			Waps.updatePoints(mRelativeLayout.getContext(), null);
			break;
		case R.id.button_show_ad:
			Waps.showOffers(mRelativeLayout.getContext());
			Waps.updatePoints(mRelativeLayout.getContext(), null);
			break;
		case R.id.button_ok:
			applyChangeDefault(mConfig);
			save();
			mWindowsManager.sendConfigChanged();
			showToast("保存成功！\n部分设置需要重启软件才能生效！");
			break;
		case R.id.button_to_default:
			mConfig=Config.load(getActivity().getSharedPreferences("default", Context.MODE_PRIVATE));
			Log.i("Setting", "button_to_default:"+mConfig.mEditorConfig.mFontScale );
			refEditView();
			refColorView();
			refSwitchView();
			break;
		default:
			break;
		}
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
		loadSwitchView();
		mWindowsManager.sendConfigChanged();
	}
	
	private static int mPoints=0;
	
	@Override
	public void getUpdatePoints(String arg0, int arg1) {
		mPoints=arg1;
		mRelativeLayout.post(new Runnable() {
			@Override
			public void run() {
				((TextView)(mRelativeLayout.findViewById(R.id.points))).setText("积分:"+mPoints);
			}
		});
	}

	@Override
	public void getUpdatePointsFailed(String arg0) {
	}
	
	private Toast mToast=null; 
	private void showToast(String str)
	{
		if(mToast!=null)
			mToast.cancel();
		mToast=Toast.makeText(mRelativeLayout.getContext(), str, Toast.LENGTH_LONG);
		mToast.show();
	}
	
	public static void  applySettingConfigToAllView(View view)
	{
		Config config = mConfig;
		if(config==null)
			return ; 
		if( view instanceof TextView && !(view instanceof Button) )
		{
			TextView textView = (TextView) view;
			int color = textView.getTextColors().getDefaultColor();
			textView.setTextColor( ((config.mEditorConfig.mBaseFontColor &0xffffff)) | (0xff000000&color) );
			TextPaint textPaint = textView.getPaint();
			boolean useNiceFont=config.mEditorConfig.mUseNiceFont;
			textPaint.setAntiAlias(useNiceFont);
			textPaint.setDither(useNiceFont);
			textPaint.setFakeBoldText(useNiceFont);
			textPaint.setSubpixelText(useNiceFont);
			textView.postInvalidate();
		} 
		if( view instanceof MyEditText )
		{
			boolean useNiceFont=config.mEditorConfig.mUseNiceFont;
			MyEditText myEditText = (MyEditText) view;
			TextPaint textPaint = myEditText.getPaint();
			textPaint.setColor(config.mEditorConfig.mBaseFontColor);
			textPaint.setTypeface(Typeface.MONOSPACE);
			textPaint.setAntiAlias(true);
			textPaint.setDither(useNiceFont);
			textPaint.setFakeBoldText(useNiceFont);
			textPaint.setSubpixelText(useNiceFont);
			myEditText.setTextSize(myEditText.getTextSize());
			myEditText.postInvalidate();
		}  
		if(view instanceof TitleView)
		{
			TitleView titleView = (TitleView) view;
			titleView.setQuickCloseEnable(config.mOtherConfig.mQuickCloseEnable);
			titleView.setQuickColseColor(config.mOtherConfig.mQuickCloseColor);
		} 
		if(view instanceof ViewGroup)
		{
			ViewGroup viewGroup = (ViewGroup) view ;
			int count = viewGroup.getChildCount();
			if(count>0)
			{
				for(int i=0;i<count;i++)
				{
					View child = viewGroup.getChildAt(i);
					applySettingConfigToAllView(child);
				}
			}
		}
		if(view instanceof TermView)
		{
			TermView termView = (TermView) view;
			termView.setColorScheme(new ColorScheme(config.mEditorConfig.mBaseFontColor, 0x00000000));
		}
	}
	

	public static class Config {
		public EditorConfig mEditorConfig = new EditorConfig();
		public CEditorConfig mCEditorConfig = new CEditorConfig();
		public OtherConfig mOtherConfig = new OtherConfig();
		public static Config load(SharedPreferences sharedPreferences){
			Config config=new Config();
			config.mEditorConfig=EditorConfig.load(sharedPreferences);
			config.mCEditorConfig=CEditorConfig.load(sharedPreferences);
			config.mOtherConfig=OtherConfig.load(sharedPreferences);
			return config;
		}
		
		public void save(Editor editor){
			mEditorConfig.save(editor);
			mCEditorConfig.save(editor);
			mOtherConfig.save(editor);
		}
		
	}
	
	public static class EditorConfig{
		public boolean mUseNiceFont = true ; 
		public int mBackGroundColor = Color.TRANSPARENT ;
		public int mBaseFontColor = Color.BLACK ;
		public float mFontScale = 1.0f ;
		public float mLineScale = 1.0f ;
		public static EditorConfig load(SharedPreferences sharedPreferences){
			EditorConfig editorConfig=new EditorConfig();
			editorConfig.mUseNiceFont=sharedPreferences.getBoolean("mUseNiceFont",true);
			editorConfig.mBackGroundColor=sharedPreferences.getInt("mBackGroundColor", Color.TRANSPARENT);
			editorConfig.mBaseFontColor=sharedPreferences.getInt("mBaseFontColor", Color.BLACK);
			editorConfig.mFontScale=sharedPreferences.getFloat("mFontScale", 1.0f);
			editorConfig.mLineScale=sharedPreferences.getFloat("mLineScale", 1.0f);
			return editorConfig;
		}
		public void save(Editor editor){
			editor.putBoolean("mUseNiceFont",mUseNiceFont );
			editor.putInt("mBackGroundColor",mBackGroundColor );
			editor.putInt("mBaseFontColor",mBaseFontColor );
			editor.putFloat("mFontScale",mFontScale );
			editor.putFloat("mLineScale",mLineScale );
		}
	}
	
	public static class CEditorConfig{
		public boolean mEnableHighLight = true;
		public int mCommentsColor = Color.rgb( 0x60, 0xa0, 0x60);
		public int mConstantColor = Color.rgb( 0xff, 0x80, 0x80) ;
		public int mKeywordsColor = Color.rgb( 0x80, 0x80, 0xff) ;
		public int mProKeywordsColor = Color.rgb( 0x80, 0x80, 0xff) ;
		public int mWordsColor = Color.rgb( 0x80, 0x80, 0x80) ;
		public static CEditorConfig load(SharedPreferences sharedPreferences){
			CEditorConfig editorConfig=new CEditorConfig();
			editorConfig.mCommentsColor=sharedPreferences.getInt("mCommentsColor", Color.rgb( 0x60, 0xa0, 0x60));
			editorConfig.mConstantColor=sharedPreferences.getInt("mConstantColor", Color.rgb( 0xff, 0x80, 0x80));
			editorConfig.mKeywordsColor=sharedPreferences.getInt("mKeywordsColor", Color.rgb( 0x80, 0x80, 0xff));
			editorConfig.mProKeywordsColor=sharedPreferences.getInt("mProKeywordsColor", Color.rgb( 0x80, 0x80, 0xff));
			editorConfig.mWordsColor=sharedPreferences.getInt("mWordsColor", Color.rgb( 0x80, 0x80, 0x80));
			editorConfig.mEnableHighLight=sharedPreferences.getBoolean("mEnableHighLight", true );
			return editorConfig;
		}

		public void save(Editor editor){
			editor.putInt("mCommentsColor",mCommentsColor );
			editor.putInt("mConstantColor",mConstantColor );
			editor.putInt("mKeywordsColor",mKeywordsColor );
			editor.putInt("mProKeywordsColor",mProKeywordsColor );
			editor.putInt("mWordsColor",mWordsColor );
			editor.putBoolean("mEnableHighLight",mEnableHighLight );
		}
		
	}
	

	public static class OtherConfig{
		public String mQuickInput = "\t'\"`$[]{}<>()+-*%=&|!^~,;?:_\\";
		public int mTitleBarColor = Color.rgb(0xa0, 0xa0, 0xf0);
		public int mSelectTitleColor = Color.rgb(0xbf, 0xff, 0x80) ;
		public boolean mQuickCloseEnable = true;
		public int mQuickCloseColor = Color.argb(0xff, 0xff, 0x6f, 0x00);
		public boolean mNewConsoleEnable = true;
		public boolean mTitleAtHead = true;
		public boolean mCtrlAtHead = true;
		public boolean mAnimation = true;
		public static OtherConfig load(SharedPreferences sharedPreferences){
			OtherConfig config=new OtherConfig();
			config.mQuickInput=sharedPreferences.getString("mQuickInput", "\t'\"`$[]{}<>()+-*%=&|!^~,;?:_\\");
			config.mTitleBarColor=sharedPreferences.getInt("mTitleBarColor",Color.rgb(0xa0, 0xa0, 0xf0));
			config.mSelectTitleColor=sharedPreferences.getInt("mSelectTitleColor",Color.rgb(0xbf, 0xff, 0x80));
			config.mQuickCloseEnable=sharedPreferences.getBoolean("mQuickCloseEnable",true);
			config.mQuickCloseColor=sharedPreferences.getInt("mQuickCloseColor",Color.rgb( 0xff, 0x6f, 0x00));
			config.mNewConsoleEnable=sharedPreferences.getBoolean("mNewConsoleEnable",true);
			config.mTitleAtHead=sharedPreferences.getBoolean("mTitleAtHead",true);
			config.mCtrlAtHead=sharedPreferences.getBoolean("mCtrlAtHead",true);
			config.mAnimation=sharedPreferences.getBoolean("mAnimation",false);
			return config;
		}

		public void save(Editor editor){
			editor.putString("mQuickInput",mQuickInput );
			editor.putInt("mTitleBarColor", mTitleBarColor);
			editor.putInt("mSelectTitleColor", mSelectTitleColor);
			editor.putBoolean("mQuickCloseEnable", mQuickCloseEnable);
			editor.putInt("mQuickCloseColor", mQuickCloseColor);
			editor.putBoolean("mNewConsoleEnable", mNewConsoleEnable);
			editor.putBoolean("mTitleAtHead", mTitleAtHead);
			editor.putBoolean("mCtrlAtHead", mCtrlAtHead);
			editor.putBoolean("mAnimation", mAnimation);
		}
	}


	@Override
	public String[] getResumeCmd() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void resumeByCmd(String []cmd) {
		// TODO Auto-generated method stub
		
	}

}
