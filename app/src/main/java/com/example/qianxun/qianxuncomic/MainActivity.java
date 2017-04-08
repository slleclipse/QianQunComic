package com.example.qianxun.qianxuncomic;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.ShareActionProvider;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.qianxun.qianxuncomic.constant.Constant;
import com.example.qianxun.qianxuncomic.fragment.CategoryFragment;
import com.example.qianxun.qianxuncomic.fragment.HotFragment;
import com.example.qianxun.qianxuncomic.fragment.IndexFragment;
import com.example.qianxun.qianxuncomic.utils.DatabaseHelper;
import com.example.qianxun.qianxuncomic.utils.Infor_communication;
import com.example.qianxun.qianxuncomic.widget.PagerSlidingTabStrip;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
@ContentView(R.layout.activity_main)
public class MainActivity extends ActionBarActivity {
	private DrawerLayout mDrawerLayout;
	private ActionBarDrawerToggle mDrawerToggle;
	private ShareActionProvider mShareActionProvider;
	private PagerSlidingTabStrip mPagerSlidingTabStrip;
	private ViewPager mViewPager;
	private Toolbar mToolbar;
	private ListView drawerList;
	private SearchView searchView;
	private TextView user_name;

	//判断是否登录
	private boolean flag = false;
	//private ImageUtils imageUtils = null;
/*相机的参数*/
	protected static final int CHOOSE_PICTURE = 0;
	protected static final int TAKE_PICTURE = 1;
	private ImageView ivHead;//头像显示
	/*    private Button btnTakephoto;//拍照
        private Button btnPhotos;//相册*/
	private Bitmap head;//头像Bitmap
	private static String path = "/sdcard/DemoHead/";//sd路径

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.activity_main);
		x.view().inject(this);
		initViews();
		initListeners();
	}


	private void initListeners(){
		ivHead.setOnClickListener(new View.OnClickListener(){
			@Override
			public void onClick(View v){
				showChoosePicDialog();
			}
		});
	}


	public void showChoosePicDialog(){
		AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
		builder.setTitle("添加图片");
		String[] items = {"选择本地图片","拍照"};
		builder.setNegativeButton("取消",null);
		builder.setItems(items, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
					case CHOOSE_PICTURE://从相册里面取照片
						Intent intent1 = new Intent(Intent.ACTION_PICK, null);//返回被选中项的URI
						intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");//得到所有图片的URI
//                System.out.println("MediaStore.Images.Media.EXTERNAL_CONTENT_URI  ------------>   "
//                        + MediaStore.Images.Media.EXTERNAL_CONTENT_URI);//   content://media/external/images/media
						startActivityForResult(intent1, 1);
						break;
					case TAKE_PICTURE://调用相机拍照
						//最好用try/catch包裹一下，防止因为用户未给应用程序开启相机权限，而使程序崩溃
						try {
							Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//开启相机应用程序获取并返回图片（capture：俘获）
							intent2.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
									"head.jpg")));//指明存储图片或视频的地址URI
							startActivityForResult(intent2, 2);//采用ForResult打开
						} catch (Exception e) {
							Toast.makeText(MainActivity.this, "相机无法启动，请先开启相机权限", Toast.LENGTH_LONG).show();
						}
						break;
					default:
						break;
				}
			}
		});
		builder.show();
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
			//从相册里面取相片的返回结果
			case 1:
				if (resultCode == RESULT_OK) {
					cropPhoto(data.getData());//裁剪图片
				}

				break;
			//相机拍照后的返回结果
			case 2:
				if (resultCode == RESULT_OK) {
					File temp = new File(Environment.getExternalStorageDirectory()
							+ "/head.jpg");
					cropPhoto(Uri.fromFile(temp));//裁剪图片
				}

				break;
			//调用系统裁剪图片后
			case 3:
				if (data != null) {
					Bundle extras = data.getExtras();
					head = extras.getParcelable("data");
					if (head != null) {
						/**
						 * 上传服务器代码
						 */
						setPicToView(head);//保存在SD卡中
						ivHead.setImageBitmap(head);//用ImageView显示出来
					}
				}
				break;
			default:
				break;

		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	;

	/**
	 * 调用系统的裁剪
	 *
	 * @param uri
	 */
	public void cropPhoto(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		//找到指定URI对应的资源图片
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", "true");
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		// outputX outputY 是裁剪图片宽高
		intent.putExtra("outputX", 150);
		intent.putExtra("outputY", 150);
		intent.putExtra("return-data", true);
		//进入系统裁剪图片的界面
		startActivityForResult(intent, 3);
	}

	private void setPicToView(Bitmap mBitmap) {
		String sdStatus = Environment.getExternalStorageState();
		if (!sdStatus.equals(Environment.MEDIA_MOUNTED)) { // 检测sd卡是否可用
			return;
		}
		FileOutputStream b = null;
		File file = new File(path);
		file.mkdirs();// 创建以此File对象为名（path）的文件夹
		String fileName = path + "head.jpg";//图片名字
		try {
			b = new FileOutputStream(fileName);
			mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, b);// 把数据写入文件（compress：压缩）

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				//关闭流
				b.flush();
				b.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	private void initViews() {
		user_name = (TextView)findViewById(R.id.user_id);
		ivHead = (ImageView) findViewById(R.id.user_image);
		Bitmap bt = BitmapFactory.decodeFile(path + "head.jpg");//从Sd中找头像，转换成Bitmap
		if (bt != null) {
			//如果本地有头像图片的话
			ivHead.setImageBitmap(bt);
		} else {
			//如果本地没有头像图片则从服务器取头像，然后保存在SD卡中，本Demo的网络请求头像部分忽略

		}

		mToolbar = (Toolbar) findViewById(R.id.toolbar);
		// toolbar.setLogo(R.drawable.ic_launcher);
		mToolbar.setTitle("千寻漫画");// 标题的文字需在setSupportActionBar之前，不然会无效
		// toolbar.setSubtitle("副标题");
		setSupportActionBar(mToolbar);
		/* 这些通过ActionBar来设置也是一样的，注意要在setSupportActionBar(toolbar);之后，不然就报错了 */
		// getSupportActionBar().setTitle("标题");
		// getSupportActionBar().setSubtitle("副标题");
		// getSupportActionBar().setLogo(R.drawable.ic_launcher);


		getSupportActionBar().setDisplayHomeAsUpEnabled(true);


		/* findView */
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);
		drawerList = (ListView) findViewById(R.id.drawerlist);
		final String[] values = new String[4];

		//获取用户登录信息
		final SharedPreferences sharedPreferences = getSharedPreferences("user", Context.MODE_PRIVATE);
		String count = sharedPreferences.getString("count","null");
		if(!count.equals("null")){
			user_name.setText(count);
			values[0]="退出登录";
			values[1]="我的收藏";
			values[2]="本地下载";
			values[3]="观看记录";
			flag = true;
		}else{
			values[0]="登录/注册";
			values[1]="我的收藏";
			values[2]="本地下载";
			values[3]="观看记录";
		}


		ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
				android.R.layout.simple_list_item_1, android.R.id.text1, values);
		drawerList.setAdapter(adapter);
		drawerList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
									int position, long id) {
				switch (position) {
					case 0:
						if(!flag){			//未登录
							Intent loginIntent = new Intent(MainActivity.this,LoginActivity.class);
							startActivity(loginIntent);
							break;
						}
						else{				//已登录,点击退出时应清除登录信息
							SharedPreferences.Editor editor = sharedPreferences.edit();
							editor.remove("count");
							editor.clear();
							editor.commit();
							user_name.setText("");
							flag  = false;
							values[0] ="登录/注册";
							Intent MainIntent = new Intent(MainActivity.this,MainActivity.class);
							startActivity(MainIntent);
							break;
						}
					case 1:
						Intent collectionIntent = new Intent(MainActivity.this,CollectionActivity.class);
						startActivity(collectionIntent);
						break;
					case 2:
						Intent downLoadIntent = new Intent(MainActivity.this,UserDownLoadActivity.class);
						startActivity(downLoadIntent);
						break;
					case 3:
						Intent historyIntent = new Intent(MainActivity.this,HistoryActivity.class);
						startActivity(historyIntent);
						break;
				}

			}
		});

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, R.string.drawer_open,
				R.string.drawer_close);
		mDrawerToggle.syncState();
		mDrawerLayout.setDrawerListener(mDrawerToggle);

		mPagerSlidingTabStrip = (PagerSlidingTabStrip) findViewById(R.id.tabs);
		mViewPager = (ViewPager) findViewById(R.id.pager);
		mViewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		mPagerSlidingTabStrip.setViewPager(mViewPager);
		mPagerSlidingTabStrip.setOnPageChangeListener(new OnPageChangeListener() {
			@Override
			public void onPageSelected(int arg0) {
				//colorChange(arg0);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});
		initTabsValue();
	}

	/**
	 * mPagerSlidingTabStrip默认值配置
	 * 
	 */
	private void initTabsValue() {
		// 底部游标颜色
		mPagerSlidingTabStrip.setIndicatorColor(Color.BLUE);
		// tab的分割线颜色
		mPagerSlidingTabStrip.setDividerColor(Color.TRANSPARENT);
		// tab背景
		mPagerSlidingTabStrip.setBackgroundColor(Color.parseColor("#4876FF"));
		// tab底线高度
		mPagerSlidingTabStrip.setUnderlineHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				1, getResources().getDisplayMetrics()));
		// 游标高度
		mPagerSlidingTabStrip.setIndicatorHeight((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				5, getResources().getDisplayMetrics()));
		// 选中的文字颜色
		mPagerSlidingTabStrip.setSelectedTextColor(Color.WHITE);
		// 正常文字颜色
		mPagerSlidingTabStrip.setTextColor(Color.BLACK);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);

		final MenuItem item = menu.findItem(R.id.action_search);
		searchView = (SearchView) MenuItemCompat.getActionView(item);
		final SearchView.SearchAutoComplete textView = ( SearchView.SearchAutoComplete) searchView.findViewById(R.id.search_src_text);
		textView.setTextColor(Color.WHITE);
		textView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				JSONObject jsonObject = new JSONObject();
				try {
					jsonObject.put("searchContent",textView.getText().toString());
					Log.i("searchContent","http://m.tuku.cc/comic/search?word="+textView.getText().toString());
					Infor_communication infor_communication = new Infor_communication("http://"+ Constant.ip+"/qianxun_background/cartoon/search",jsonObject);
					JSONArray jsonArray = infor_communication.getResult();
					Intent searchIntent = new Intent(MainActivity.this,ResultActivity.class);
					Log.i("searchResult",jsonArray.toString());
					searchIntent.putExtra("searchResult",jsonArray.toString());
					startActivity(searchIntent);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		return super.onOptionsItemSelected(item);
	}

	/* ***************FragmentPagerAdapter***************** */
	public class MyPagerAdapter extends FragmentPagerAdapter {

		private final String[] TITLES = { "主页", "分类", "热门推荐", "热门收藏"};

		private final Fragment[] fragmentpages = {
				new IndexFragment(),
				new CategoryFragment(),
				new HotFragment(),
				new HotFragment(),
		};

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public CharSequence getPageTitle(int position) {
			return TITLES[position];
		}

		@Override
		public int getCount() {
			return TITLES.length;
		}

		@Override
		public Fragment getItem(int position) {
			return fragmentpages[position];
		}

	}

}
