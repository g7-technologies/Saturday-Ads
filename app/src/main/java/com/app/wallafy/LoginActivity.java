package com.app.wallafy;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.app.utils.DefensiveClass;
import com.app.utils.GetSet;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gcm.GCMRegistrar;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.app.utils.Constants;
import com.app.utils.SOAPParsing;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.HashMap;

import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

public class LoginActivity extends AppCompatActivity implements OnClickListener, OnConnectionFailedListener{

	EditText email, password;
	TextView login,skip, fbTxt, twtTxt, gplusTxt, register,title, forgetPassword,newusr;
	ImageView fbBtn, twtBtn, gplusBtn;
	ImageButton   backBtn;
	String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
	private BroadcastReceiver networkStateReceiver;
	Display display;
	RelativeLayout main;
	ProgressDialog dialog;

	// for google plus //
	private static final int RC_SIGN_IN = 9001;
	private GoogleApiClient mGoogleApiClient;
	private boolean mSignInClicked;
	private ProgressDialog mConnectionProgressDialog;
	CallbackManager callbackManager;

	// ***For Twitter Login***//
	private int TWITTER_AUTH;
	private Twitter mTwitter;
	private RequestToken mRequestToken;
	public static final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
	static final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
	static final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
	public static final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login_activity);

		Constants.pref = getApplicationContext().getSharedPreferences("wallafyPref",
				MODE_PRIVATE);
		Constants.editor = Constants.pref.edit();

		email = (EditText) findViewById(R.id.email);
		password = (EditText) findViewById(R.id.password);
		login = (TextView) findViewById(R.id.login);
		fbTxt = (TextView) findViewById(R.id.fbTxt);
		twtTxt = (TextView) findViewById(R.id.twtTxt);
		gplusTxt = (TextView) findViewById(R.id.gpTxt);
		register = (TextView) findViewById(R.id.register);
		fbBtn = (ImageView) findViewById(R.id.fbBtn);
		twtBtn = (ImageView) findViewById(R.id.twtBtn);
		gplusBtn = (ImageView) findViewById(R.id.gpBtn);
		title = (TextView) findViewById(R.id.title);
		//backBtn = (ImageButton) findViewById(R.id.backbtn);
		forgetPassword = (TextView) findViewById(R.id.forgetpassword);
		main=(RelativeLayout) findViewById(R.id.main);

	//	title.setText(getString(R.string.login));

		wallafyApplication.setupUI(LoginActivity.this, main);
		skip = (TextView)findViewById(R.id.skip);
		newusr = (TextView)findViewById(R.id.skip);
		skip.setOnClickListener(this);
		fbTxt.setOnClickListener(this);
		twtTxt.setOnClickListener(this);
		gplusTxt.setOnClickListener(this);
		fbBtn.setOnClickListener(this);
		twtBtn.setOnClickListener(this);
		gplusBtn.setOnClickListener(this);
		login.setOnClickListener(this);
		register.setOnClickListener(this);
		//backBtn.setOnClickListener(this);
		forgetPassword.setOnClickListener(this);
		newusr.setOnClickListener(this);

		//backBtn.setVisibility(View.VISIBLE);
		//title.setVisibility(View.VISIBLE);

		loginToFacebook();

		GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
				.requestEmail()
				.build();

		mGoogleApiClient = new GoogleApiClient.Builder(this)
				.enableAutoManage(this, this)
				.addApi(Auth.GOOGLE_SIGN_IN_API, gso)
				.build();
		mConnectionProgressDialog = new ProgressDialog(this);
		mConnectionProgressDialog.setMessage("Signing in...");
		mConnectionProgressDialog.setCanceledOnTouchOutside(false);

		display = this.getWindowManager().getDefaultDisplay();

		dialog = new ProgressDialog(LoginActivity.this);
		dialog.setMessage("Please Wait...");
		dialog.setCancelable(false);
		dialog.setCanceledOnTouchOutside(false);

	}

	// for login //

	class loginAsync extends AsyncTask<Void, Void, String> {

		@Override
		protected String doInBackground(Void... arg0) {

			String SOAP_ACTION = Constants.NAMESPACE + Constants.API_LOGIN;

			SoapObject req = new SoapObject(Constants.NAMESPACE, Constants.API_LOGIN);
			req.addProperty(Constants.SOAP_USERNAME, Constants.SOAP_USERNAME_VALUE);
			req.addProperty(Constants.SOAP_PASSWORD, Constants.SOAP_PASSWORD_VALUE);
			req.addProperty("email", email.getText().toString());
			req.addProperty("password", password.getText().toString());

			SOAPParsing soap = new SOAPParsing();
			String result = soap.getJSONFromUrl(SOAP_ACTION, req);
			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.show();

		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			//	if (dialog.isShowing()) {
			//		dialog.dismiss();
			//	}
			try {
				dialog.dismiss();
				Log.v("result",""+result);
				JSONObject jonj = new JSONObject(result);
				if (jonj.getString("status").equalsIgnoreCase(
						"true")) {
					GetSet.setLogged(true);
					GetSet.setEmail(email.getText().toString());
					GetSet.setPassword(password.getText().toString());
					GetSet.setUserId(jonj.getString("user_id"));
					GetSet.setUserName(jonj.getString("user_name"));
					GetSet.setFullName(jonj.getString("full_name"));
					GetSet.setImageUrl(Constants.url + "user/resized/150/" +jonj.getString("photo"));

					Constants.editor.putBoolean("isLogged", true);
					Constants.editor.putString("userId", GetSet.getUserId());
					Constants.editor.putString("userName", GetSet.getUserName());
					Constants.editor.putString("Email", GetSet.getEmail());
					Constants.editor.putString("Password", GetSet.getPassword());
					Constants.editor.putString("photo", GetSet.getImageUrl());
					Constants.editor.putString("fullName", GetSet.getFullName());
					Constants.editor.putString("language", "English");
					Constants.editor.commit();

					Registernotifi();
					finish();
					Intent i = new Intent(LoginActivity.this, FragmentMainActivity.class);
					startActivity(i);

				} else if (jonj.getString("status").equalsIgnoreCase("error")){
					wallafyApplication.dialog(LoginActivity.this, getString(R.string.alert), jonj.getString("message"));
				} else {
					dialog.dismiss();
					wallafyApplication.dialog(LoginActivity.this, getString(R.string.alert), jonj.getString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
			} catch (NullPointerException e) {
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
			} catch (Exception e){
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
			}
		}
	}

	/**
	 * Function to login into facebook
	 * */
	@SuppressWarnings("deprecation")
	public void loginToFacebook() {

		callbackManager = CallbackManager.Factory.create();

		LoginManager.getInstance().registerCallback(callbackManager,
				new FacebookCallback<LoginResult>() {
					@Override
					public void onSuccess(LoginResult loginResult) {

						GraphRequest request = GraphRequest.newMeRequest(
								loginResult.getAccessToken(),
								new GraphRequest.GraphJSONObjectCallback() {
									@Override
									public void onCompleted(
											JSONObject profile,
											GraphResponse response) {
										final HashMap<String, String> fbdata = new HashMap<String, String>();
										Log.v("json", "object" + profile);
										// Application code
										try {
											if (profile.has("email")) {
												// getting name of the user
												final String name = profile.getString("name");

												// getting email of the user
												final String email = profile.getString("email");

												// getting userId of the user
												final String userId = profile.getString("id");

												// getting firstName of the user
												final String firstName = profile.getString("first_name");

												// getting lastName of the user
												final String lastName = profile.getString("last_name");

												URL image_value = null;
												try {
													image_value = new URL("http://graph.facebook.com/" + userId + "/picture?type=large");

												} catch (MalformedURLException e) {
													e.printStackTrace();
												}

												fbdata.put("type", "facebook");
												fbdata.put("email", email);
												fbdata.put("id", userId);
												fbdata.put("firstName", firstName);
												fbdata.put("lastName", lastName);
												fbdata.put("image", "http://graph.facebook.com/" + userId + "/picture?type=large");
												Log.v("fbdata", "" + fbdata);
												LoginActivity.this.runOnUiThread(new Runnable() {

													@SuppressWarnings("unchecked")
													@Override
													public void run() {
														if (dialog != null && dialog.isShowing()) {
															dialog.dismiss();
														}
														new sendData().execute(fbdata);

													}
												});
											}else{
												Toast.makeText(LoginActivity.this, "Please check your Facebook permissions",Toast.LENGTH_SHORT).show();
											}

										} catch (JSONException e) {
											e.printStackTrace();
										}
									}
								});
						Bundle parameters = new Bundle();
						parameters.putString("fields", "id,name,email,first_name,last_name");
						request.setParameters(parameters);
						request.executeAsync();
					}

					@Override
					public void onCancel() {
						Toast.makeText(LoginActivity.this, "Facebook - Cancelled", Toast.LENGTH_SHORT).show();
					}

					@Override
					public void onError(FacebookException exception) {
						Toast.makeText(LoginActivity.this, "Facebook - " + exception.getMessage(), Toast.LENGTH_SHORT).show();
						if (exception instanceof FacebookAuthorizationException) {
							if (com.facebook.AccessToken.getCurrentAccessToken() != null) {
								LoginManager.getInstance().logOut();
							}
						}
					}
				});

	}

	/** Funtion for login into twitter **/
	private void loginToTwitter() {
		if (!isTwitterLoggedInAlready()) {
			ConfigurationBuilder builder = new ConfigurationBuilder();
			builder.setOAuthConsumerKey(Constants.CONSUMER_KEY);
			builder.setOAuthConsumerSecret(Constants.CONSUMER_SECRET);
			//builder.setUseSSL(true);
			Configuration configuration = builder.build();
			mTwitter = new TwitterFactory(configuration).getInstance();
			mRequestToken = null;
			try {
				mRequestToken = mTwitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
				Log.v("mRequestToken",""+mRequestToken);
				Intent i = new Intent(this, TwitterWebviewActivity.class);
				i.putExtra("URL", mRequestToken.getAuthenticationURL());
				startActivityForResult(i, TWITTER_AUTH);
			} catch (TwitterException e) {
				e.printStackTrace();
				twtBtn.setOnClickListener(this);
				twtTxt.setOnClickListener(this);
			}

		}
		else{
			Log.v("already logged in","already logged in");
			/*GetSet.setLogged(true);
			GetSet.setEmail(Constants.pref.getString("twtEmail", ""));
			GetSet.setPassword(Constants.pref.getString("twtPassword", ""));
			GetSet.setUserId(Constants.pref.getString("twtuserId", ""));
			GetSet.setUserName(Constants.pref.getString("twtuserName", ""));
			GetSet.setFullName(Constants.pref.getString("twtfullname", ""));
			GetSet.setProfileUserId(Constants.pref.getString("twtuserId", ""));
			GetSet.setImageUrl(Constants.pref.getString("twtphoto", ""));


			Intent datas = new Intent();
		//	datas.putExtra("returnKey1", "Swinging on a star. ");
			setResult(RESULT_OK, datas);

			LoginActivity.this.finish();*/

		}
	}

	private boolean isTwitterLoggedInAlready() {
		// return twitter login status from Shared Preferences
		Log.v("isloggedtwitter", "isloggedtwitter" + Constants.pref.getBoolean(PREF_KEY_TWITTER_LOGIN, false));
		return Constants.pref.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
	}

	private void getEmailForTwitter(final HashMap<String, String> twitterData){
		final Dialog dialog = new Dialog(this ,R.style.AlertDialog);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.forget_password);
		dialog.getWindow().setLayout(display.getWidth()*80/100, LayoutParams.WRAP_CONTENT);
		dialog.setCancelable(true);

		TextView title = (TextView) dialog.findViewById(R.id.alert_title);
		final EditText msg = (EditText) dialog.findViewById(R.id.alert_msg);
		TextView ok = (TextView) dialog.findViewById(R.id.alert_button);

		title.setText(getString(R.string.twitter));
		ok.setText(getString(R.string.ok));

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				dialog.dismiss();
				if ((!msg.getText().toString().matches(emailPattern))
						|| (msg.getText().toString().trim().length() == 0)) {
					msg.setError(getString(R.string.please_verify_mail));
				}else{
					Constants.editor.putString(PREF_KEY_OAUTH_TOKEN,twitterData.get("oauth_token"));
					Constants.editor.putString(PREF_KEY_OAUTH_SECRET,twitterData.get("oauth_secret"));
					Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
					Constants.editor.commit(); // save changes
					twitterData.put("email", msg.getText().toString());

					new sendData().execute(twitterData);
				}
			}
		});

		if(!dialog.isShowing()){
			dialog.show();
		}

	}

	/** Funtions for login into G+ **/
	private void handleSignInResult(GoogleSignInResult result) {
		Log.d("handleSignInResult", "handleSignInResult:" + result.isSuccess());
		if (result.isSuccess()) {
			// Signed in successfully, show authenticated UI.
			try {
				GoogleSignInAccount acct = result.getSignInAccount();

				String personName = acct.getDisplayName();
				String email = acct.getEmail();
				String id = acct.getId();
				String personPhoto = "";

				if (acct.getPhotoUrl() == null){
					personPhoto = "";
				} else {
					personPhoto = acct.getPhotoUrl().toString();
				}

				HashMap<String, String> gplusData = new HashMap<String, String>();

				gplusData.put("type", "google");
				gplusData.put("email", email);
				gplusData.put("id", id);
				gplusData.put("firstName", personName);
				gplusData.put("lastName", "");
				gplusData.put("image", personPhoto);

				new sendData().execute(gplusData);

				Log.v("personName", "personName" + personName);
				Log.v("personPhoto", "personPhoto" + personPhoto);
				Log.v("email", "email" + email);

			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			// Signed out, show unauthenticated UI.
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		Log.v("onConnectionFailed", "onConnectionFailed");
		if (mSignInClicked) {
			mConnectionProgressDialog.dismiss();
			// The user has already clicked 'sign-in' so we attempt to resolve all
			// errors until the user is signed in, or they cancel.
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
        Log.v("onactivity", "onactivtiy");
		if (requestCode == RC_SIGN_IN) {
			GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
			handleSignInResult(result);
		}
		if (requestCode == TWITTER_AUTH) {
			twtTxt.setOnClickListener(this);
			twtBtn.setOnClickListener(this);

			if (resultCode == Activity.RESULT_OK) {
				final String oauthVerifier = (String) data.getExtras().get(
						"oauth_verifier");
				if(oauthVerifier==null){

				}
				else {

					Thread th = new Thread(new Runnable() {
						public void run() {
							try {
								AccessToken at = mTwitter
										.getOAuthAccessToken(oauthVerifier);
								String theToken = at.getToken();
								Constants.editor.putString(PREF_KEY_OAUTH_TOKEN,at.getToken());
								Constants.editor.putString(PREF_KEY_OAUTH_SECRET,at.getTokenSecret());
								Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
								Constants.editor.commit(); // save changes
								//twitterData.put("email", msg.getText().toString());
								Log.v("stre", theToken);
								long userID = at.getUserId();
								User user = mTwitter.showUser(userID);
								String name = user.getName();
								String[] names = name.split(" ");


								final HashMap<String, String> twitterData = new HashMap<String, String>();
								twitterData.put("type", "twitter");
								twitterData.put("id", Long.toString(at.getUserId()));
								twitterData.put("firstName", names[0]);
								if(names.length>1) {
									twitterData.put("lastName", names[1]);
								}else{
									twitterData.put("lastName", "");
								}
								twitterData.put("image", user.getOriginalProfileImageURL());
								twitterData.put("url", user.getURL());
								twitterData.put("oauth_token", at.getToken());
								twitterData.put("oauth_secret", at.getTokenSecret());
								twitterData.put("email", "");

								Log.v("userimage", ""+user.getOriginalProfileImageURL());

								LoginActivity.this.runOnUiThread(new Runnable() {

									@Override
									public void run() {
										//getEmailForTwitter(twitterData);
										new sendData().execute(twitterData);

									}
								});

							} catch (TwitterException e) {
								e.printStackTrace();
							}
						}
					});

					th.start();
				}
			}
		}
		callbackManager.onActivityResult(requestCode, resultCode, data);

	}

	/** For send social login user datas to server **/
	class sendData extends AsyncTask<HashMap<String, String>, Void, String> {
		HashMap<String, String> datas;
		@Override
		protected String doInBackground(HashMap<String, String>... params) {

			datas = params[0];

			String SOAP_ACTION = Constants.NAMESPACE + Constants.API_SOCIAL_LOGIN;

			SoapObject req = new SoapObject(Constants.NAMESPACE, Constants.API_SOCIAL_LOGIN);
			req.addProperty(Constants.SOAP_USERNAME, Constants.SOAP_USERNAME_VALUE);
			req.addProperty(Constants.SOAP_PASSWORD, Constants.SOAP_PASSWORD_VALUE);
			req.addProperty("type", datas.get("type"));
			req.addProperty("id", datas.get("id"));
			req.addProperty("first_name", datas.get("firstName"));
			req.addProperty("last_name", datas.get("lastName"));
			req.addProperty("email", datas.get("email"));
			req.addProperty("image_url", datas.get("image"));

			SOAPParsing soap = new SOAPParsing();
			String response = soap.getJSONFromUrl(SOAP_ACTION, req);

			return response;
		}


		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			try {
				dialog.show();
			} catch (WindowManager.BadTokenException e){
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
		}


		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			try {
				dialog.dismiss();
				JSONObject results = new JSONObject(result);
				String status = results.getString("status");
				Log.d("status","result="+result);
				if (status.equalsIgnoreCase("true")) {

					GetSet.setLogged(true);
					GetSet.setEmail(DefensiveClass.optString(results, "email"));
					GetSet.setPassword("");
					GetSet.setUserId(results.getString("user_id"));
					GetSet.setUserName(results.getString("user_name"));
					GetSet.setFullName(DefensiveClass.optString(results, "full_name"));
					GetSet.setImageUrl(Constants.url + "user/resized/150/" +results.getString("photo"));

					Constants.editor.putBoolean("isLogged", true);
					Constants.editor.putString("userId", GetSet.getUserId());
					Constants.editor.putString("userName", GetSet.getUserName());
					Constants.editor.putString("Email", GetSet.getEmail());
					Constants.editor.putString("Password", "");
					Constants.editor.putString("photo", GetSet.getImageUrl());
					Constants.editor.putString("fullName", GetSet.getFullName());
					Constants.editor.putString("language", "English");
					Constants.editor.commit();

					Constants.editor.putBoolean("twtisLogged", true);
					Constants.editor.putString("twtuserId", GetSet.getUserId());
					Constants.editor.putString("twtuserName", GetSet.getUserName());
					Constants.editor.putString("twtEmail", GetSet.getEmail());
					Constants.editor.putString("twtPassword", "");
					Constants.editor.putString("twtphoto", GetSet.getImageUrl());
					Constants.editor.putString("twtfullname", GetSet.getFullName());
					Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
					Constants.editor.commit();

					Registernotifi();
					finish();
					Intent i = new Intent(LoginActivity.this, FragmentMainActivity.class);
					startActivity(i);

				} else if(status.equalsIgnoreCase("false")){
					if(results.getString(Constants.TAG_MESSAGE).equalsIgnoreCase("Account not found")){
					getEmailForTwitter(datas);}
					else{
						wallafyApplication.dialog(LoginActivity.this, getString(R.string.alert), results.getString(Constants.TAG_MESSAGE));
						Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
						Constants.editor.commit();
					}
				}else if (status.equalsIgnoreCase("error")){
					wallafyApplication.dialog(LoginActivity.this, getString(R.string.alert), results.getString("message"));
					Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
					Constants.editor.commit();
				} else {
					dialog.dismiss();
					String msg = results.getString(Constants.TAG_MESSAGE);
					wallafyApplication.dialog(LoginActivity.this, getString(R.string.alert), msg);
				}
			} catch (JSONException e) {
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
				Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
				Constants.editor.commit();
			} catch (NullPointerException e) {
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
				Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
				Constants.editor.commit();
			} catch (Exception e) {
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
				Constants.editor.putBoolean(PREF_KEY_TWITTER_LOGIN, false);
				Constants.editor.commit();
			}
		}
	}

	/**  For register push notification **/
	public void  Registernotifi(){
		Constants.REGISTER_ID = GCMRegistrar.getRegistrationId(getApplicationContext());
		Log.v("enetered push","registerid="+Constants.REGISTER_ID);
		Constants.editor.putString("registerId", Constants.REGISTER_ID);
		Constants.editor.commit();

		if(Constants.REGISTER_ID=="" ||Constants.REGISTER_ID.equals("")){
			GCMRegistrar.register(this, Constants.SENDER_ID);
		}else{
			if (GCMRegistrar.isRegisteredOnServer(this)) {
				Log.v("GCM", "already registered on device");
			} else {
				Log.v("GCM", "Registering in db");
				wallafyApplication aController = (wallafyApplication) getApplicationContext();
				aController.register(LoginActivity.this);
			}
		}
	}

	/** Dialog for forget password **/
	@SuppressWarnings("deprecation")
	private void forgetPassword(){
		final Dialog dialog = new Dialog(this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
		dialog.setContentView(R.layout.forget_password);
		dialog.getWindow().setLayout(display.getWidth()*90/100, LayoutParams.WRAP_CONTENT);
		dialog.setCancelable(true);
		dialog.setCanceledOnTouchOutside(false);

		TextView title = (TextView) dialog.findViewById(R.id.alert_title);
		final EditText msg = (EditText) dialog.findViewById(R.id.alert_msg);
		TextView ok = (TextView) dialog.findViewById(R.id.alert_button);
		TextView cancel = (TextView) dialog.findViewById(R.id.alert_cancel);

		ok.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (!(msg.getText().toString().matches(emailPattern))
						|| (msg.getText().toString().trim().length() == 0)) {
					msg.setError(getString(R.string.please_verify_mail));
				}else{
					dialog.dismiss();
					new resetAsync().execute(msg.getText().toString());
				}
			}
		});

		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				dialog.dismiss();
			}
		});

		if(!dialog.isShowing()){
			dialog.show();
		}
	}

	// for reset password //

	class resetAsync extends AsyncTask<String, Void, String> {
		private ProgressDialog dialog = new ProgressDialog(LoginActivity.this);
		@Override
		protected String doInBackground(String... params) {
			String SOAP_ACTION = Constants.NAMESPACE + Constants.API_FORGET_PASSWORD;

			SoapObject req = new SoapObject(Constants.NAMESPACE, Constants.API_FORGET_PASSWORD);
			req.addProperty(Constants.SOAP_USERNAME, Constants.SOAP_USERNAME_VALUE);
			req.addProperty(Constants.SOAP_PASSWORD, Constants.SOAP_PASSWORD_VALUE);
			req.addProperty("email", params[0]);

			SOAPParsing soap = new SOAPParsing();
			String result = soap.getJSONFromUrl(SOAP_ACTION, req);
			return result;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			dialog.setMessage("Please Wait...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			if (dialog.isShowing()) {
				dialog.dismiss();
			}
			JSONObject jonj;
			try {
				jonj = new JSONObject(result);
				if (jonj.getString("status").equalsIgnoreCase("true")) {
					wallafyApplication.dialog(LoginActivity.this, getString(R.string.success), jonj.getString("message"));
				}else{
					wallafyApplication.dialog(LoginActivity.this, getString(R.string.alert), jonj.getString("message"));
				}
			} catch (JSONException e) {
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
			}catch (NullPointerException e) {
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
			} catch (Exception e){
				e.printStackTrace();
				wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), e.getMessage());
			}

		}

	}

	@Override
	protected void onPause() {
		super.onPause();
		// For Internet checking disconnect
		wallafyApplication.unregisterReceiver(LoginActivity.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		// For Internet checking
		wallafyApplication.registerReceiver(LoginActivity.this);
	}

	@Override
	public void onClick(View v) {
		switch(v.getId()){
			case R.id.login:
				if (!wallafyApplication.isNetworkAvailable(this)) {
					wallafyApplication.dialog(LoginActivity.this, getString(R.string.error), getString(R.string.network_error));
				} else if (email.getText().toString().trim().length() == 0) {
					email.setError(getString(R.string.please_type_mail));
				} else if (!email.getText().toString().matches(emailPattern)) {
					email.setError(getString(R.string.please_verify_mail));
				} else if (password.getText().toString().length() == 0) {
					password.setError(getString(R.string.please_type_password));
				} else {
					new loginAsync().execute();
				}
				break;
			case R.id.skip:
				finish();
				Intent y = new Intent(LoginActivity.this, FragmentMainActivity.class);
				startActivity(y);
				break;
			case R.id.register:
				Intent i = new Intent(LoginActivity.this, RegisterActivity.class);
				i.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				startActivity(i);
				break;
			case R.id.fbBtn:
			case R.id.fbTxt:
				LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));
				break;
			case R.id.twtBtn:
				Thread th1 = new Thread(new Runnable() {
					public void run() {
						loginToTwitter();
					}
				});
				th1.start();
				break;
			case R.id.twtTxt:
				twtBtn.setOnClickListener(null);
				twtTxt.setOnClickListener(null);
				Thread th = new Thread(new Runnable() {
					public void run() {
						loginToTwitter();
					}
				});
				th.start();
				break;
			case R.id.gpBtn:
			case R.id.gpTxt:
				mSignInClicked = true;
				Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
				startActivityForResult(signInIntent, RC_SIGN_IN);
				break;
			/*case R.id.backbtn:
				finish();
				break;*/
			case R.id.forgetpassword:
				forgetPassword();
				break;
		}

	}

}