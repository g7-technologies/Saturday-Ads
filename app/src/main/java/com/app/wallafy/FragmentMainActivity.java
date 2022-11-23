package com.app.wallafy;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.etsy.android.grid.StaggeredGridView;
import com.app.external.GPSTracker;
import com.app.external.HorizontalListView;
import com.app.helper.ItemAdapter;
import com.app.helper.Model;
import com.app.utils.Constants;
import com.app.utils.GetSet;
import com.app.utils.ItemsParsing;
import com.app.utils.SOAPParsing;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.ksoap2.serialization.SoapObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutionException;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;


public class FragmentMainActivity extends AppCompatActivity implements OnClickListener, AbsListView.OnScrollListener, SwipeRefreshLayout.OnRefreshListener, AdapterView.OnItemClickListener {

	public static ListView listView;
	ImageView titleImage, menu_btn, filter_btn, search_btn, floatingBtn;
	public static ItemAdapter adapter;
	public static int screenWidth, screenHeight, screenHalf, currentPage=0;
	int visibleThreshold=0, previousTotal=0, mDrawerPosition=0;
	boolean loading=true, pulldown=false, mDrawerItemClicked = false;
	boolean networkEnable = false;
	DrawerLayout drawer;
	ActionBarDrawerToggle toggle;
	LinearLayout profheader, proflogin, nullLay;
	RelativeLayout locationLay, headerLay;
	TextView login, userid;
	public static TextView username, locationTxt;
	public static ImageView userImage;
	Toolbar toolbar;
	StaggeredGridView gridView;
	SwipeRefreshLayout swipeLayout;
	AlertDialog alertDialog;
	public static HomeAdapter homeAdapter;
	AVLoadingIndicatorView progress;
	HorizontalListView filterList;
	public static FilterAdapter filterAdapter;
	View filterView;
	GPSTracker gps;
	private Geocoder geocoder;
	private List<Address> addresses;
	public static ArrayList<HashMap<String,String>> filterAry = new ArrayList<HashMap<String,String>>();
	public static ArrayList<HashMap<String,String>> HomeItems=new ArrayList<HashMap<String,String>>();

	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_frame);

		// For Getting Side Menu Items
		Model.LoadModel(FragmentMainActivity.this);
		String[] ids = new String[Model.Items.size()];
		for (int i = 0; i < ids.length; i++) {
			ids[i] = Integer.toString(i + 1);
		}

		toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Elements initialisation
		drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
		menu_btn = (ImageView) findViewById(R.id.menubtn);
		filter_btn=(ImageView) findViewById(R.id.filterbtn);
		search_btn = (ImageView) findViewById(R.id.searchbtn);
		listView = (ListView) findViewById(R.id.list);
		profheader = (LinearLayout)findViewById(R.id.profile_header);
		proflogin = (LinearLayout)findViewById(R.id.profile_login);
		headerLay = (RelativeLayout) findViewById(R.id.header);
		login = (TextView)findViewById(R.id.login);
		username = (TextView)findViewById(R.id.userName);
		userid = (TextView)findViewById(R.id.userId);
		userImage = (ImageView)findViewById(R.id.userImage);
		titleImage = (ImageView)findViewById(R.id.titleimg);
		gridView = (StaggeredGridView) findViewById(R.id.gridView);
		swipeLayout = (SwipeRefreshLayout) findViewById(R.id.swipeLayout);
		progress = (AVLoadingIndicatorView) findViewById(R.id.progress);
		nullLay = (LinearLayout) findViewById(R.id.nullLay);
		floatingBtn = (ImageView) findViewById(R.id.floatingBtn);
		locationLay = (RelativeLayout) findViewById(R.id.locationLay);
		locationTxt = (TextView) findViewById(R.id.locationTxt);
		filterList = (HorizontalListView) findViewById(R.id.filterList);
		filterView= (View) findViewById(R.id.filterView);

		swipeLayout.setProgressViewOffset(false, 0, wallafyApplication.dpToPx(FragmentMainActivity.this, 70));

		// Elements Visibility
		filter_btn.setVisibility(View.VISIBLE);
		search_btn.setVisibility(View.VISIBLE);
		titleImage.setVisibility(View.VISIBLE);
		nullLay.setVisibility(View.GONE);
		progress.setVisibility(View.GONE);

		// Adapter for side menu
		adapter = new ItemAdapter(FragmentMainActivity.this, R.layout.menu_list_item, ids);
		listView.setAdapter(adapter);

		toggle =  new ActionBarDrawerToggle(this, drawer,null, R.string.open, R.string.close) {

			@Override
			public void onDrawerOpened(View drawerView) {
				super.onDrawerOpened(drawerView);
				Log.v("Drawer", "Drawer Opened");
			}

			@Override
			public void onDrawerClosed(View drawerView) {
				super.onDrawerClosed(drawerView);
				Log.v("Drawer", "Drawer Closed");
				if (mDrawerItemClicked){
					mDrawerItemClicked = false;
					openActivity(mDrawerPosition);
				}
			}
		};

		Constants.filpref = getApplicationContext().getSharedPreferences("FilterPref",
				MODE_PRIVATE);
		Constants.fileditor = Constants.filpref.edit();

		LocationActivity.location = Constants.filpref.getString("location", getString(R.string.world_wide));
		LocationActivity.lat = Double.parseDouble(Constants.filpref.getString("lat", "0"));
		LocationActivity.lon = Double.parseDouble(Constants.filpref.getString("lon", "0"));
		LocationActivity.locationRemoved = Constants.filpref.getBoolean("locationRemoved", false);

		if (SearchAdvance.categoryId.size() > 0 || !SearchAdvance.distance.equals("0") || !SearchAdvance.sortBy.equals("1")
				|| !SearchAdvance.postedWithin.equals("")) {
			filterAry.clear();
			getFilterAry();
			Log.v("filterAry", "filterAry=" + filterAry);
			setFilterAdapter();
			filterList.setVisibility(View.VISIBLE);
			filterView.setVisibility(View.VISIBLE);
			filter_btn.setColorFilter(getResources().getColor(R.color.colorPrimary));

		} else {
			filterAry.clear();
			filterList.setVisibility(View.GONE);
			filterView.setVisibility(View.GONE);
			filter_btn.setColorFilter(getResources().getColor(R.color.colorAccent));
		}

		//drawer.setDescendantFocusability(ViewGroup.FOCUS_AFTER_DESCENDANTS);
		drawer.setDrawerListener(toggle);
		drawer.post(new Runnable() {
			@Override
			public void run() {
				toggle.syncState();
			}
		});

		// Elements Listener
		listView.setOnItemClickListener(new DrawerItemClickListener());
		gridView.setOnItemClickListener(new ItemClickListener());
		login.setOnClickListener(this);
		filter_btn.setOnClickListener(this);
		search_btn.setOnClickListener(this);
		menu_btn.setOnClickListener(this);
		gridView.setOnScrollListener(this);
		swipeLayout.setOnRefreshListener(this);
		locationLay.setOnClickListener(this);
		login.setOnClickListener(this);
		floatingBtn.setOnClickListener(this);
		headerLay.setOnClickListener(this);

		// For Set Login & Logout State
        Constants.pref = getApplicationContext().getSharedPreferences("wallafyPref",
				MODE_PRIVATE);
		Constants.editor = Constants.pref.edit();
		if (Constants.pref.getBoolean("isLogged", false)) {
			GetSet.setLogged(true);
			GetSet.setUserId(Constants.pref.getString("userId", null));
			GetSet.setUserName(Constants.pref.getString("userName", null));
			GetSet.setEmail(Constants.pref.getString("Email", null));
			GetSet.setPassword(Constants.pref.getString("Password", null));
			GetSet.setFullName(Constants.pref.getString("fullName", null));
			GetSet.setImageUrl(Constants.pref.getString("photo", null));
			profheader.setVisibility(View.VISIBLE);
			proflogin.setVisibility(View.GONE);
		}
		else{
			profheader.setVisibility(View.GONE);
			proflogin.setVisibility(View.VISIBLE);
		}

		if (GetSet.isLogged()) {
			profheader.setVisibility(View.VISIBLE);
			proflogin.setVisibility(View.GONE);
			username.setText(GetSet.getFullName());
			userid.setText(GetSet.getUserName());
			if (GetSet.getImageUrl() != null && !GetSet.getImageUrl().equals("")){
				Log.v("getImageurl", "getImageurl="+GetSet.getImageUrl());
				Picasso.with(FragmentMainActivity.this).load(GetSet.getImageUrl()).into(userImage);
			}
		}else{
			profheader.setVisibility(View.GONE);
			proflogin.setVisibility(View.VISIBLE);
		}

		swipeLayout.setColorSchemeColors(getResources().getColor(R.color.swipeColor));
		Display display = this.getWindowManager().getDefaultDisplay();
		screenWidth= display.getWidth();
		screenHeight= display.getHeight();
		screenHalf= screenWidth/2;

		// Setting Dialog Title
		alertDialog = new AlertDialog.Builder(FragmentMainActivity.this).create();
		alertDialog.setTitle(getString(R.string.gps_settings));
		alertDialog.setMessage(getString(R.string.gps_notenabled));
		alertDialog.setButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				networkEnable = true;
				Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
				startActivityForResult(intent, 3);
			}
		});
		alertDialog.setButton2(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		alertDialog.setCancelable(false);

		gps = new GPSTracker(FragmentMainActivity.this);

		setLocationTxt();
		loadData();
	}

	private void loadData(){
		if(HomeItems.size()==0){
			new homeLoadItems().execute(0);
			homeAdapter = new HomeAdapter(FragmentMainActivity.this, HomeItems);
			gridView.setAdapter(homeAdapter);
		} else {
			homeAdapter = new HomeAdapter(FragmentMainActivity.this, HomeItems);
			gridView.setAdapter(homeAdapter);
		}
	}

	private void swipeRefresh(){
		swipeLayout.post(new Runnable() {
			@Override
			public void run() {
				swipeLayout.setRefreshing(true);
			}
		});
	}

	/** function for get the location from gps **/
	private void setLocationTxt(){
		gps = new GPSTracker(FragmentMainActivity.this);
		if (LocationActivity.locationRemoved){
			locationTxt.setText(getString(R.string.world_wide));
		} else {
			if (LocationActivity.lat == 0  && LocationActivity.lon == 0){
				if (gps.canGetLocation()) {
					if (wallafyApplication.isNetworkAvailable(FragmentMainActivity.this)) {
						LocationActivity.lat = gps.getLatitude();
						LocationActivity.lon = gps.getLongitude();
						Log.v("lati", "lat" + LocationActivity.lat);
						Log.v("longi", "longi" + LocationActivity.lon);
						new GetLocationAsync(LocationActivity.lat, LocationActivity.lon).execute();
					}
				} else {
					if (ActivityCompat.checkSelfPermission(this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(FragmentMainActivity.this, new String[]{ ACCESS_FINE_LOCATION,  ACCESS_COARSE_LOCATION}, 102);
					} else {
						if (!alertDialog.isShowing()){
							alertDialog.show();
						}
					}
				}
			} else if(!LocationActivity.location.equals(getString(R.string.world_wide))){
				locationTxt.setText(LocationActivity.location);
			}
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

	}

	// home items //
	class homeLoadItems extends AsyncTask<Integer, Void, Void> {

		@Override
		protected Void doInBackground(Integer... params) {
			int offset = (params[0] * 20);
			String sortid = "1";

			String SOAP_ACTION = Constants.NAMESPACE + Constants.API_HOME;

			SoapObject req = new SoapObject(Constants.NAMESPACE, Constants.API_HOME);
			req.addProperty(Constants.SOAP_USERNAME, Constants.SOAP_USERNAME_VALUE);
			req.addProperty(Constants.SOAP_PASSWORD, Constants.SOAP_PASSWORD_VALUE);
			req.addProperty("type", "search");
			Log.v("LocationActivity.lat", "LocationActivity.lat=" + LocationActivity.lat);
			Log.v("LocationActivity.lon", "LocationActivity.lon=" + LocationActivity.lon);
			if (!Double.toString(LocationActivity.lat).equals("0.0")) {
				req.addProperty("lat", Double.toString(LocationActivity.lat));
			}
			if (!Double.toString(LocationActivity.lon).equals("0.0")) {
				req.addProperty("lon", Double.toString(LocationActivity.lon));
			}
			if (!SearchAdvance.distance.equals("0")) {
				req.addProperty("distance", SearchAdvance.distance);
			}
			if (!SearchAdvance.sortBy.equals("")) {
				req.addProperty("sorting_id", SearchAdvance.sortBy);
			}
			if (!SearchAdvance.postedWithin.equals("") && !SearchAdvance.postedWithin.equals("all")) {
				req.addProperty("posted_within", SearchAdvance.postedWithin);
			}
			if (SearchAdvance.categoryId.size() > 0) {
				ArrayList<String> main = new ArrayList<String>();
				ArrayList<String> sub = new ArrayList<String>();
				for (int i = 0; i < SearchAdvance.categoryId.size(); i++){
					String subc = SearchAdvance.subcategoryId.get(SearchAdvance.categoryId.get(i));
					if (subc == null || subc.equals("") || subc.equals("all")){
						main.add(SearchAdvance.categoryId.get(i));
					} else {
						sub.add(subc);
					}
				}
				if (main.size() > 0){
					req.addProperty("category_id", main.toString().replaceAll("[\\[\\]]|(?<=,)\\s+", ""));
				}

				if (sub.size() > 0){
					req.addProperty("subcategory_id", sub.toString().replaceAll("[\\[\\]]|(?<=,)\\s+", ""));
				}
			}
			req.addProperty("offset", Integer.toString(offset));
			req.addProperty("limit", "20");
			if (GetSet.isLogged()) {
				req.addProperty("user_id", GetSet.getUserId());
			}

			SOAPParsing soap = new SOAPParsing();
			final String json = soap.getJSONFromUrl(SOAP_ACTION, req);

			if (pulldown){
				HomeItems.clear();
			}
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					ArrayList<HashMap<String,String>> temp=new ArrayList<HashMap<String,String>>();
					ItemsParsing parse = new ItemsParsing(FragmentMainActivity.this);
					temp.addAll(parse.parsing(json));
					if (!HomeItems.contains(temp)){
						HomeItems.addAll(temp);
					}
					Log.v("HomeItems","HomeItems"+HomeItems);
				}
			});
			return null;
		}

		@Override
		protected void onPreExecute() {
			nullLay.setVisibility(View.INVISIBLE);
			if (pulldown) {
				gridView.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
			} else if (HomeItems.size() > 0) {
				gridView.setVisibility(View.VISIBLE);
				progress.setVisibility(View.GONE);
				swipeRefresh();
			} else {
				gridView.setVisibility(View.INVISIBLE);
				progress.setVisibility(View.VISIBLE);
			}
		}

		@Override
		protected void onPostExecute(Void unused) {
			if(pulldown){
				pulldown=false;
				loading = true;
			}
			gridView.setVisibility(View.VISIBLE);
			swipeLayout.setRefreshing(false);
			progress.setVisibility(View.GONE);
			homeAdapter.notifyDataSetChanged();
			if(HomeItems.size() == 0){
				nullLay.setVisibility(View.VISIBLE);
			}else{
				nullLay.setVisibility(View.INVISIBLE);
			}
		}

	}

	public class HomeAdapter extends BaseAdapter {
		ArrayList<HashMap<String, String>> Items;
		private Context mContext;
		ViewHolder holder = null;

		public HomeAdapter(Context ctx, ArrayList<HashMap<String, String>> data) {
			mContext = ctx;
			Items = data;
		}

		@Override
		public int getCount() {

			return Items.size();
		}

		@Override
		public Object getItem(int position) {

			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		private class ViewHolder {
			ImageView singleImage;
			TextView itemPrice, itemName, location, postedTime, productType;
			RelativeLayout imageLay;
		}

		@Override
		public View getView(final int position, View convertView,
							ViewGroup parent) {

			if (convertView == null) {
				LayoutInflater inflater = (LayoutInflater) mContext
						.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = inflater.inflate(R.layout.home_list_items,
						parent, false);// layout
				holder = new ViewHolder();
				holder.singleImage = (ImageView) convertView.findViewById(R.id.singleImage);
				holder.itemPrice = (TextView) convertView.findViewById(R.id.priceText);
				holder.itemName = (TextView) convertView.findViewById(R.id.itemName);
				holder.productType = (TextView) convertView.findViewById(R.id.productType);
				holder.location = (TextView) convertView.findViewById(R.id.location);
				holder.postedTime = (TextView) convertView.findViewById(R.id.postedTime);
				holder.imageLay = (RelativeLayout) convertView.findViewById(R.id.imageLay);

				holder.singleImage.getLayoutParams().height=screenHalf;
				holder.imageLay.getLayoutParams().height=screenHalf;

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			try {
				final HashMap<String, String> tempMap = Items.get(position);

				//	holder.singleImage.setBackgroundColor(Integer.parseInt(tempMap.get(Constants.TAG_COLOR)));
				Picasso.with(FragmentMainActivity.this).load(tempMap.get(Constants.TAG_ITEM_URL_350)).into(holder.singleImage);
				holder.itemName.setText(tempMap.get(Constants.TAG_TITLE));
				holder.itemPrice.setText(tempMap
						.get(Constants.TAG_CURRENCY_CODE)
						+ tempMap.get(Constants.TAG_PRICE));
				holder.location.setText(tempMap.get(Constants.TAG_LOCATION));
				holder.postedTime.setText(tempMap.get(Constants.TAG_POSTED_TIME).toUpperCase());

				if (tempMap.get(Constants.TAG_ITEM_STATUS).equalsIgnoreCase("sold")){
					holder.productType.setVisibility(View.VISIBLE);
					holder.productType.setText("Sold");
					holder.productType.setBackgroundDrawable(getResources().getDrawable(R.drawable.soldbg));
				} else {
					if(tempMap.get(Constants.TAG_PROMOTION_TYPE).equalsIgnoreCase("Premium")) {
						holder.productType.setVisibility(View.VISIBLE);
						holder.productType.setText("Premium");
						holder.productType.setBackgroundDrawable(getResources().getDrawable(R.drawable.adbg));
					} else if(tempMap.get(Constants.TAG_PROMOTION_TYPE).equalsIgnoreCase("Must Sell")) {
						holder.productType.setVisibility(View.VISIBLE);
						holder.productType.setText("Must Sell");
						holder.productType.setBackgroundDrawable(getResources().getDrawable(R.drawable.urgentbg));
					} else {
						holder.productType.setVisibility(View.GONE);
					}
				}

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
			return convertView;
		}

	}

	/** adapter for showing the applied filters **/
	public class FilterAdapter extends BaseAdapter {

		private Context mContext;
		ArrayList<HashMap<String, String>> datas;
		ViewHolder holder = null;

		public FilterAdapter(Context ctx, ArrayList<HashMap<String, String>> data) {
			mContext = ctx;
			datas = data;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {

			return position;
		}

		private class ViewHolder {
			ImageView crossIcon;
			TextView name;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			LayoutInflater inflater = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			if (convertView == null) {
				convertView = inflater.inflate(R.layout.home_filter_item, parent, false);//layout
				holder = new ViewHolder();

				holder.name = (TextView) convertView.findViewById(R.id.name);
				holder.crossIcon = (ImageView) convertView.findViewById(R.id.cross_icon);

				convertView.setTag(holder);
			} else {
				holder = (ViewHolder) convertView.getTag();
			}

			try {
				final HashMap<String, String> map = datas.get(position);

				holder.name.setText(map.get("name"));

				holder.crossIcon.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						switch (map.get("type")){
							case "category":
								if (SearchAdvance.categoryId.size()>0){
									SearchAdvance.categoryId.remove(map.get("categoryId"));
									SearchAdvance.categoryName.remove(map.get("name"));
									SearchAdvance.subcategoryId.remove(map.get("categoryId"));
								}
								break;
							case "distance":
								SearchAdvance.distance = "0";
								SearchAdvance.distanceX = 0;
								break;
							case "postedWithin":
								SearchAdvance.postedWithin = "";
								break;
							case "sortBy":
								SearchAdvance.sortBy = "1";
								break;
						}
						filterAry.remove(position);
						filterAdapter.notifyDataSetChanged();
						swipeRefresh();
						currentPage = 0;
						previousTotal = 0;
						pulldown = true;
						if (wallafyApplication.isNetworkAvailable(FragmentMainActivity.this)) {
							new homeLoadItems().execute(0);
						}
						if (filterAry.size() == 0) {
							filterList.setVisibility(View.GONE);
							filterView.setVisibility(View.GONE);
							filter_btn.setColorFilter(getResources().getColor(R.color.primaryText));
						}
						Log.v("filterAry", "filterAry"+ filterAry);
						Log.v("categoryId", "categoryId"+ SearchAdvance.categoryId);
						Log.v("categoryName", "categoryName"+ SearchAdvance.categoryName);
						Log.v("subcategoryId", "subcategoryId"+ SearchAdvance.subcategoryId);
					}
				});

			} catch (NullPointerException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}

			return convertView;
		}
	}

	/** function for get the applied filters to Ary **/
	private void getFilterAry(){
		if (SearchAdvance.categoryId.size() > 0) {
			for (int i = 0; i < SearchAdvance.categoryId.size(); i++){
				HashMap<String, String> map = new HashMap<String, String>();
				map.put("type", "category");
				map.put("name", SearchAdvance.categoryName.get(i));
				map.put("categoryId", SearchAdvance.categoryId.get(i));

				filterAry.add(map);
			}
		}

		if (!SearchAdvance.distance.equals("0")){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", "distance");
			map.put("name", "Within "+SearchAdvance.distance + " Miles");
			filterAry.add(map);
		}
		if (!SearchAdvance.postedWithin.equals("")){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", "postedWithin");
			map.put("name", SearchAdvance.postedTxt);
			filterAry.add(map);
		}
		if (!SearchAdvance.sortBy.equals("1")){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("type", "sortBy");
			if(SearchAdvance.sortTxt.equals("Urgent"))
				map.put("name", "Must Sell");
			else
				map.put("name", SearchAdvance.sortTxt);
			filterAry.add(map);
		}
	}

	private void setFilterAdapter(){
		filterAdapter = new FilterAdapter(FragmentMainActivity.this, filterAry);
		filterList.setAdapter(filterAdapter);
	}

	private class DrawerItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			drawer.closeDrawers();
			mDrawerItemClicked = true;
			mDrawerPosition = position;
		}
	}

	private class ItemClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView parent, View view, int position, long id) {
			if (HomeItems.size() > 0){
				Intent i = new Intent(FragmentMainActivity.this,
						DetailActivity.class);
				i.putExtra("data", HomeItems.get(position));
				i.putExtra("position", position);
				i.putExtra("from", "home");
				startActivity(i);
			}
		}
	}

	/** function for open the corresponding activity from sliding menu **/
	private void openActivity(int position) {
		switch (position) {
			case 0:
				if (GetSet.isLogged()) {
					Intent m = new Intent(FragmentMainActivity.this, CameraActivity.class);
					m.putExtra("from", "camera");
					startActivity(m);
				} else {
					Intent i = new Intent(FragmentMainActivity.this, WelcomeActivity.class);
					startActivity(i);
				}
				break;
			case 1:
				if (GetSet.isLogged()) {
					Intent i = new Intent(FragmentMainActivity.this, MessageActivity.class);
					startActivity(i);
				} else {
					Intent i = new Intent(FragmentMainActivity.this, WelcomeActivity.class);
					startActivity(i);
				}
				break;
			case 2:
				Intent c = new Intent(FragmentMainActivity.this, CategoryActivity.class);
				startActivity(c);
				break;
			case 3:
				if (GetSet.isLogged()) {
					Intent i = new Intent(FragmentMainActivity.this, Profile.class);
					i.putExtra("userId", GetSet.getUserId());
					startActivity(i);
				} else {
					Intent i = new Intent(FragmentMainActivity.this, WelcomeActivity.class);
					startActivity(i);
				}
				break;
			case 4:
				if (GetSet.isLogged()) {
					Intent i = new Intent(FragmentMainActivity.this, ExchangeActivity.class);
					startActivity(i);
				} else {
					Intent i = new Intent(FragmentMainActivity.this, WelcomeActivity.class);
					startActivity(i);
				}
				break;
			case 5:
				if (GetSet.isLogged()) {
					Intent i = new Intent(FragmentMainActivity.this, MyPromotions.class);
					startActivity(i);
				} else {
					Intent i = new Intent(FragmentMainActivity.this, WelcomeActivity.class);
					startActivity(i);
				}
				break;
			case 6:
				Intent Hl = new Intent(FragmentMainActivity.this, Help.class);
				startActivity(Hl);
				break;
			default:
				if (FragmentMainActivity.this == null)
					return;
				break;
		}
	}

	/** class for get the address from lat, lon **/
	private class GetLocationAsync extends AsyncTask<String, Void, String> {

		// boolean duplicateResponse;
		double x, y;
		StringBuilder str;

		public GetLocationAsync(double latitude, double longitude) {
			x = latitude;
			y = longitude;
		}

		@Override
		protected void onPreExecute() {

		}

		@Override
		protected String doInBackground(String... params) {

			try {
				geocoder = new Geocoder(FragmentMainActivity.this, Locale.ENGLISH);
				addresses = geocoder.getFromLocation(x, y, 1);
				str = new StringBuilder();
				if (geocoder.isPresent() && addresses.size() > 0) {

					Address returnAddress = addresses.get(0);

					String localityString = returnAddress.getLocality();
					String city = returnAddress.getCountryName();
					String region_code = returnAddress.getCountryCode();
					String zipcode = returnAddress.getPostalCode();

					str.append(localityString + "");
					str.append(city + "" + region_code + "");
					str.append(zipcode + "");

				} else {
				}
			} catch (IOException e) {
				Log.e("tag", e.getMessage());
			}
			return null;

		}

		@Override
		protected void onPostExecute(String result) {
			try {
				if (addresses.size() > 0) {
					LocationActivity.location = addresses.get(0).getAddressLine(0)
							+ addresses.get(0).getAddressLine(1) + " ";
					locationTxt.setText(LocationActivity.location);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		protected void onProgressUpdate(Void... values) {

		}
	}

	@Override
	public void onBackPressed() {
		if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
			DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							//moveTaskToBack(true);
							if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
								Log.v("finishjb", "finishjb");
								FragmentMainActivity.this.finishAffinity();
							} else {
								Log.v("finish", "finish");
								moveTaskToBack(true);
								FragmentMainActivity.this.finish();
								//ActivityCompat.finishAffinity(FragmentChangeActivity.this);
							}
							break;

						case DialogInterface.BUTTON_NEGATIVE:
							break;
					}
				}
			};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage(getResources().getString(R.string.reallyExit))
					.setPositiveButton(getResources().getString(R.string.exit),
							dialogClickListener)
					.setNegativeButton(getResources().getString(R.string.keep),
							dialogClickListener).show();
		}
		else{
            super.onBackPressed();
        }
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
						 int visibleItemCount, int totalItemCount) {
		if (firstVisibleItem == 0) {
			swipeLayout.setEnabled(true);
		} else {
			swipeLayout.setEnabled(false);
		}
		if (!pulldown){
			if (loading) {
				if (totalItemCount > previousTotal) {
					loading = false;
					previousTotal = totalItemCount;
					currentPage++;
				}
			}

			if (!loading
					&& (totalItemCount - visibleItemCount) <= (firstVisibleItem + visibleThreshold)) {
				// I load the next page of thumbnails using a background task,
				if(currentPage != 0){
					new homeLoadItems().execute(currentPage);
					loading = true;
				}
			}
		}
	}

	@Override
	public void onRefresh() {
		if (!pulldown) {
			currentPage = 0;
			previousTotal = 0;
			pulldown = true;
			if (wallafyApplication.isNetworkAvailable(FragmentMainActivity.this)) {
				setLocationTxt();
				new homeLoadItems().execute(0);
			} else {
				swipeLayout.setRefreshing(false);
			}
		} else {
			swipeLayout.setRefreshing(false);
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		Log.v("onActivityResult", "onActivityResult");
		if (networkEnable && requestCode == 3){
			swipeRefresh();
			Log.v("networkEnable", "networkEnable");
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					gps = new GPSTracker(FragmentMainActivity.this);
					if (gps.canGetLocation()){
						Log.v("gps", "gps");
						networkEnable = false;
						LocationActivity.lat = gps.getLatitude();
						LocationActivity.lon = gps.getLongitude();
						Log.v("lat", "lat=" + LocationActivity.lat);
						Log.v("lon", "lon=" + LocationActivity.lon);
						try {
							new GetLocationAsync(LocationActivity.lat, LocationActivity.lon).execute().get();
						} catch (InterruptedException e) {
							e.printStackTrace();
						} catch (ExecutionException e) {
							e.printStackTrace();
						}
						currentPage = 0;
						previousTotal = 0;
						pulldown = true;
						if (wallafyApplication.isNetworkAvailable(FragmentMainActivity.this)) {
							new homeLoadItems().execute(0);
						}
					}
				}
			}, 3000);
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// For Internet checking disconnect
		wallafyApplication.unregisterReceiver(FragmentMainActivity.this);

	}

	@Override
	protected void onResume() {
		super.onResume();
		Log.v("resume", "resume");
		// For Internet checking
		wallafyApplication.registerReceiver(FragmentMainActivity.this);

		if (SearchAdvance.applyFilter) {
			SearchAdvance.applyFilter = false;
			homeAdapter = new HomeAdapter(FragmentMainActivity.this, HomeItems);
			gridView.setAdapter(homeAdapter);
			swipeRefresh();
			currentPage = 0;
			previousTotal = 0;
			pulldown = true;
			if (wallafyApplication.isNetworkAvailable(FragmentMainActivity.this)) {
				new homeLoadItems().execute(0);
			}
		}

	}

	@Override
	public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		Log.v("requestCode", "requestCode=" + requestCode);
		if (requestCode == 102 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
			Toast.makeText(FragmentMainActivity.this, getString(R.string.location_permission_access), Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(FragmentMainActivity.this, getString(R.string.need_permission_to_access), Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()){
			case R.id.login:
				Intent i = new Intent(FragmentMainActivity.this,WelcomeActivity.class);
				startActivity(i);
				break;
			case R.id.filterbtn:
				Intent j = new Intent(FragmentMainActivity.this,SearchAdvance.class);
				startActivity(j);
				break;
			case R.id.menubtn:
				drawer.openDrawer(Gravity.LEFT);
				break;
			case R.id.locationLay:
				Intent k = new Intent(FragmentMainActivity.this, LocationActivity.class);
				k.putExtra("from", "home");
				startActivity(k);
				break;
			case R.id.searchbtn:
				Intent l = new Intent(FragmentMainActivity.this, SearchActivity.class);
				startActivity(l);
				break;
			case R.id.floatingBtn:
				if (GetSet.isLogged()) {
					Intent m = new Intent(FragmentMainActivity.this, CameraActivity.class);
					m.putExtra("from", "camera");
					startActivity(m);
				} else {
					Intent m = new Intent(FragmentMainActivity.this, WelcomeActivity.class);
					startActivity(m);
				}
				break;
			case R.id.header:
				drawer.closeDrawers();
				if (GetSet.isLogged()) {
					Intent n = new Intent(FragmentMainActivity.this, Profile.class);
					n.putExtra("userId", GetSet.getUserId());
					startActivity(n);
				} else {
					Intent n = new Intent(FragmentMainActivity.this, WelcomeActivity.class);
					startActivity(n);
				}
				break;
		}
	}

}
