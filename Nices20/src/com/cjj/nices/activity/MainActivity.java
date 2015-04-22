package com.cjj.nices.activity;

import static com.norbsoft.typefacehelper.TypefaceHelper.typeface;

import org.litepal.crud.DataSupport;

import uk.me.lewisdeane.ldialogs.CustomDialog;
import net.youmi.android.AdManager;
import net.youmi.android.diy.DiyManager;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;

import com.cjj.nices.callback.NavigationDrawerCallbacks;
import com.cjj.nices.db.FavoriteTableModel;
import com.cjj.nices.fragment.FavoriteFragment;
import com.cjj.nices.fragment.HomeFragment;
import com.cjj.nices.fragment.MessageFragment;
import com.cjj.nices.fragment.NavigationDrawerFragment;
import com.cjj.nices.fragment.SupportFragment;
import com.cjj.nices.model.NicesApplication;
import com.norbsoft.typefacehelper.ActionBarHelper;
import com.umeng.analytics.MobclickAgent;

/**
 * 
 * @author cjj
 *
 */
@TargetApi(Build.VERSION_CODES.HONEYCOMB) @SuppressLint("NewApi")
public class MainActivity extends ActionBarActivity implements NavigationDrawerCallbacks {

    private Toolbar mToolbar;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    public static MainActivity instance;
    private boolean titleFlag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        instance = this;
        mToolbar = (Toolbar) findViewById(R.id.toolbar_actionbar);
        setSupportActionBar(mToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        ActionBarHelper.setTitle(getSupportActionBar(), typeface(this, R.string.app_name,NicesApplication.nicesApplication.getHuayunTypeface(),Typeface.NORMAL));
//        typeface(this);
        mNavigationDrawerFragment = (NavigationDrawerFragment) getFragmentManager().findFragmentById(R.id.fragment_drawer);
        mNavigationDrawerFragment.setup(R.id.fragment_drawer, (DrawerLayout) findViewById(R.id.drawer), mToolbar);
    
        AdManager.getInstance(this).init("2b293550678dfc41", "d0412a6df86290bc", false);
        
        ShareSDK.initSDK(this);
    }
    
    
	/*退出的间隔时间 */
	private static final long EXIT_INTERVAL_TIME = 2000;
	private long touchTime = 0;

	/**
	 * On key up.
	 * 
	 * @param keyCode
	 *            the key code
	 * @param event
	 *            the event
	 * @return true, if successful
	 */
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK
			) {
			long currentTime = System.currentTimeMillis();

			if ((currentTime - touchTime) >= EXIT_INTERVAL_TIME) {
				Toast.makeText(MainActivity.this, "在按一次退出程序", 1).show();
				touchTime = currentTime;
			} else {
				
				show_existDialog();
			}

			return false;
		} else {
			return true;
		}
	}
	
	
	
	

    
    private void show_existDialog() {
    	CustomDialog.Builder builder = new CustomDialog.Builder(this, "支持", "确定");
		// Now we can any of the following methods.
		builder.content("您的支持，我的感谢^_^");
		builder.negativeText("取消");
		// Now we can build the dialog.
		CustomDialog customDialog = builder.build();
//		// Show the dialog.
		customDialog.setClickListener(new CustomDialog.ClickListener() {
            @Override
            public void onConfirmClick() {
            	DiyManager.showRecommendWall(MainActivity.this );
            }

            @Override
            public void onCancelClick() {
            	MobclickAgent.onKillProcess( MainActivity.this );
				finish();
				System.exit(0);
            }
        });
		customDialog.show();		
	}






	public void showShare(String str) {
    	
    	 OnekeyShare oks = new OnekeyShare();
    	 //关闭sso授权
    	 oks.disableSSOWhenAuthorize(); 

    	// 分享时Notification的图标和文字
    	 oks.setNotification(R.drawable.app_icon, getString(R.string.app_name));
    	 // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
//    	 oks.setTitle(getString(R.string.share));
    	 // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
//    	 oks.setTitleUrl("http://www.apkbus.com/home.php?mod=space&uid=192812");
    	 // text是分享文本，所有平台都需要这个字段
    	 oks.setText("[NiceS]"+str);
    	 // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
//    	 oks.setImagePath("/sdcard/test.jpg");//确保SDcard下面存在此张图片
    	 // url仅在微信（包括好友和朋友圈）中使用
//    	 oks.setUrl("http://sharesdk.cn");
    	 // comment是我对这条分享的评论，仅在人人网和QQ空间使用
//    	 oks.setComment("我是测试评论文本");
    	 // site是分享此内容的网站名称，仅在QQ空间使用
//    	 oks.setSite(getString(R.string.app_name));
    	 // siteUrl是分享此内容的网站地址，仅在QQ空间使用
//    	 oks.setSiteUrl("http://sharesdk.cn");
    	// 启动分享GUI
    	 oks.show(this);
    	 }
    
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return super.onCreateOptionsMenu(menu);
//    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
    	selectFragment(position);
    }
    
	public void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

	public void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}

    /**
     * 选择fragment
     */
    public void selectFragment(int postion)
    {
    	Fragment fragment = null;
    	FragmentManager fragmentManager = getSupportFragmentManager();
    	
    	switch (postion) {
		case 0:
			
			fragment = HomeFragment.newInstance();
			if(titleFlag)
			{
				  ActionBarHelper.setTitle(getSupportActionBar(), typeface(MainActivity.this, R.string.home,NicesApplication.nicesApplication.getHuayunTypeface(),Typeface.NORMAL));
			}else
			{
				titleFlag = true;
			}
			break;
			
		case 1:
			
			fragment = FavoriteFragment.newInstance();
			 ActionBarHelper.setTitle(getSupportActionBar(), typeface(this, R.string.favorite,NicesApplication.nicesApplication.getHuayunTypeface(),Typeface.NORMAL));
				
			break;
			
		case 2:
			
			fragment = MessageFragment.newInstance();
			ActionBarHelper.setTitle(getSupportActionBar(), typeface(this, R.string.messages,NicesApplication.nicesApplication.getHuayunTypeface(),Typeface.NORMAL));
			
			
			break;
			
		case 3:
			
			fragment = SupportFragment.newInstance();
			ActionBarHelper.setTitle(getSupportActionBar(), typeface(this, R.string.support,NicesApplication.nicesApplication.getHuayunTypeface(),Typeface.NORMAL));
			break;

		default:
			break;
		}
    	
    	fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
    }
    
    
    
}
