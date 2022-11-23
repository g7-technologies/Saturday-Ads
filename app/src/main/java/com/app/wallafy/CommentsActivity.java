package com.app.wallafy;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.utils.Constants;
import com.app.utils.DefensiveClass;
import com.app.utils.GetSet;
import com.app.utils.SOAPParsing;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;

public class CommentsActivity extends AppCompatActivity implements OnClickListener {

    ListView listView;
    EditText commentText;
    ImageView back, productImg;
    ArrayList<HashMap<String, String>> commentsList = null;
    String from, itemId, productName, productImage;
    AVLoadingIndicatorView progress;
    CommentsAdapter commentsAdapter;
    LinearLayout nullLay;
    TextView title, sendtxt, productTitle;
    InputMethodManager imm;
    int position;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.comments_page);

        imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

        listView = (ListView) findViewById(R.id.comments_list);
        commentText = (EditText) findViewById(R.id.commentEditText);
        nullLay = (LinearLayout) findViewById(R.id.nullLay);
        back = (ImageView) findViewById(R.id.backbtn);
        title = (TextView) findViewById(R.id.username);
        sendtxt = (TextView) findViewById(R.id.sendtxt);
        progress = (AVLoadingIndicatorView) findViewById(R.id.progress);
        productTitle = (TextView) findViewById(R.id.productTitle);
        productImg = (ImageView) findViewById(R.id.productImg);

        title.setText(getResources().getString(R.string.comments));
        imm.hideSoftInputFromWindow(commentText.getWindowToken(), 0);

        commentsList = new ArrayList<HashMap<String, String>>();
        from = getIntent().getExtras().getString("from");
        itemId = getIntent().getExtras().getString("itemId");
        position = getIntent().getExtras().getInt("position");
        productName = getIntent().getExtras().getString("productName");
        productImage = getIntent().getExtras().getString("productImage");

        back.setVisibility(View.VISIBLE);
        title.setVisibility(View.VISIBLE);
        productTitle.setVisibility(View.VISIBLE);
        productImg.setVisibility(View.VISIBLE);
        commentText.setFilters(new InputFilter[]{wallafyApplication.EMOJI_FILTER, new InputFilter.LengthFilter(120)});

        productTitle.setText(productName);
        Picasso.with(CommentsActivity.this).load(productImage.replace("350", "70")).into(productImg);

        back.setOnClickListener(this);
        sendtxt.setOnClickListener(this);

        if (wallafyApplication.isNetworkAvailable(CommentsActivity.this)) {
            new getComments().execute();
            commentsAdapter = new CommentsAdapter(CommentsActivity.this, commentsList);
            listView.setAdapter(commentsAdapter);
        } else {
            //wallafyApplication.dialog(CommentsActivity.this, "Error!", getResources().getString(R.string.checkconnection));
        }

    }

    /** class for get the comments by product **/
    class getComments extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            String SOAP_ACTION = Constants.NAMESPACE + Constants.API_GET_COMMENTS;

            SoapObject req = new SoapObject(Constants.NAMESPACE, Constants.API_GET_COMMENTS);
            req.addProperty(Constants.SOAP_USERNAME, Constants.SOAP_USERNAME_VALUE);
            req.addProperty(Constants.SOAP_PASSWORD, Constants.SOAP_PASSWORD_VALUE);
            req.addProperty("item_id", itemId);

            SOAPParsing soap = new SOAPParsing();
            String jsonString = soap.getJSONFromUrl(SOAP_ACTION, req);

            try {
                JSONObject json = new JSONObject(jsonString);
                String response = DefensiveClass.optString(json, Constants.TAG_STATUS);
                if (response.equalsIgnoreCase("true")) {
                    JSONObject result = json.optJSONObject(Constants.TAG_RESULT);
                    if (result != null) {
                        JSONArray commentsjson = result.optJSONArray("comments");
                        if (commentsjson != null) {
                            for (int k = 0; k < commentsjson.length(); k++) {

                                JSONObject commentsTemp = commentsjson.getJSONObject(k);
                                HashMap<String, String> tmpMap = new HashMap<String, String>();

                                String comment_id = DefensiveClass.optInt(commentsTemp, Constants.TAG_COMMENTID);
                                String comment = DefensiveClass.optString(commentsTemp, Constants.TAG_COMMENT);
                                String user_id = DefensiveClass.optInt(commentsTemp, Constants.TAG_USERID);
                                String user_img = Constants.url + "user/resized/150/" + DefensiveClass.optString(commentsTemp, Constants.TAG_USERIMG);
                                String username = DefensiveClass.optString(commentsTemp, "user_name");
                                String commentTime = DefensiveClass.optString(commentsTemp, Constants.TAG_COMMENTTIME);

                                tmpMap.put(Constants.TAG_COMMENTID, comment_id);
                                tmpMap.put(Constants.TAG_COMMENT, comment);
                                tmpMap.put(Constants.TAG_USERID, user_id);
                                tmpMap.put(Constants.TAG_USERIMG, user_img);
                                tmpMap.put(Constants.TAG_USERNAME, username);
                                tmpMap.put(Constants.TAG_COMMENTTIME, commentTime);
                                commentsList.add(tmpMap);
                            }
                        }
                    }
                } else if (response.equalsIgnoreCase("error")) {
                    wallafyApplication.disabledialog(CommentsActivity.this, json.optString("message"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            listView.setVisibility(View.INVISIBLE);
            progress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void unused) {
            listView.setVisibility(View.VISIBLE);
            progress.setVisibility(View.INVISIBLE);
            commentsAdapter.notifyDataSetChanged();
            if (commentsList.size() == 0) {
                nullLay.setVisibility(View.VISIBLE);
            }
        }

    }

    public class CommentsAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> HomePageItems;
        private Context mContext;
        ViewHolder holder = null;

        public CommentsAdapter(Context ctx,
                               ArrayList<HashMap<String, String>> data) {
            mContext = ctx;
            HomePageItems = data;
        }

        @Override
        public int getCount() {
            return HomePageItems.size();
        }

        @Override
        public Object getItem(int position) {

            return null;
        }

        @Override
        public long getItemId(int position) {

            return position;
        }

        public class ViewHolder {
            ImageView userImage;
            TextView username;
            TextView comments;
            TextView date;
        }


        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.comments_item, parent, false);// layout
                holder = new ViewHolder();

                holder.userImage = (ImageView) convertView.findViewById(R.id.userimg);
                holder.username = (TextView) convertView.findViewById(R.id.username);
                holder.comments = (TextView) convertView.findViewById(R.id.comments);
                holder.date = (TextView) convertView.findViewById(R.id.date);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            try {
                final HashMap<String, String> tempMap = HomePageItems.get(position);

                holder.username.setText(tempMap.get(Constants.TAG_USERNAME));
                holder.comments.setText(tempMap.get(Constants.TAG_COMMENT));
                if (tempMap.get(Constants.TAG_COMMENTTIME).equals(" ago")){
                    holder.date.setText("Just now");
                } else {
                    holder.date.setText(tempMap.get(Constants.TAG_COMMENTTIME));
                }

                Picasso.with(CommentsActivity.this).load(tempMap.get(Constants.TAG_USERIMG)).into(holder.userImage);
                holder.userImage.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent u = new Intent(CommentsActivity.this, Profile.class);
                        u.putExtra("userId", tempMap.get(Constants.TAG_USERID));
                        startActivity(u);
                    }
                });

                holder.username.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent u = new Intent(CommentsActivity.this, Profile.class);
                        u.putExtra("userId", tempMap.get(Constants.TAG_USERID));
                        startActivity(u);
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

    /** class for send comments to product **/
    class sendComment extends AsyncTask<String, Void, String> {
        String json = "";
        String comment = "";

        @Override
        protected String doInBackground(String... params) {
            String SOAP_ACTION = Constants.NAMESPACE + Constants.API_POST_COMMENTS;

            try {
                comment = params[0];
                SoapObject req = new SoapObject(Constants.NAMESPACE, Constants.API_POST_COMMENTS);
                req.addProperty(Constants.SOAP_USERNAME, Constants.SOAP_USERNAME_VALUE);
                req.addProperty(Constants.SOAP_PASSWORD, Constants.SOAP_PASSWORD_VALUE);
                req.addProperty("comment", params[0]);
                req.addProperty("user_id", GetSet.getUserId());
                req.addProperty("item_id", itemId);

                SOAPParsing soap = new SOAPParsing();
                json = soap.getJSONFromUrl(SOAP_ACTION, req);
            } catch (Exception e) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        //commentsList.remove(commentsList.size()-1);
                        //commentsAdapter.notifyDataSetChanged();
                        json = "";
                        comment = "";
                        commentText.setText("");
                        wallafyApplication.dialog(CommentsActivity.this, getString(R.string.alert), getString(R.string.symbols_not_supported));
                    }
                });
            }

            return json;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            try {
                sendtxt.setOnClickListener(CommentsActivity.this);
                JSONObject json = new JSONObject(result);
                String response = json.getString(Constants.TAG_STATUS);
                Log.v("comment", "status" + response);
                if (response.equalsIgnoreCase("true")) {
                    HashMap<String, String> tempmap = new HashMap<String, String>();
                    String comment_id = json
                            .getString(Constants.TAG_COMMENTID);
                    String comment = json
                            .getString(Constants.TAG_COMMENT);
                    String user_id = json
                            .getString(Constants.TAG_USERID);
                    String user_img = Constants.url + "user/resized/150/" + json
                            .getString(Constants.TAG_USERIMG);
                    String username = json
                            .getString("user_name");
                    String commentTime = json
                            .getString(Constants.TAG_COMMENTTIME);

                    tempmap.put(Constants.TAG_COMMENTID, comment_id);
                    tempmap.put(Constants.TAG_COMMENT, comment);
                    tempmap.put(Constants.TAG_USERID, user_id);
                    tempmap.put(Constants.TAG_USERIMG, user_img);
                    tempmap.put(Constants.TAG_USERNAME, username);
                    tempmap.put(Constants.TAG_COMMENTTIME, commentTime);

                    commentsList.add(tempmap);
                    commentText.setText("");
                    Commentnotify();
                    commentsAdapter.notifyDataSetChanged();
                    nullLay.setVisibility(View.GONE);
                } else {
                    wallafyApplication.dialog(CommentsActivity.this, getResources().getString(R.string.alert), json.getString("message"));
                }
            } catch (JSONException e) {
                if (!comment.equals("")) {

                    HashMap<String, String> tempmap = new HashMap<String, String>();
                    tempmap.put(Constants.TAG_COMMENTID, "");
                    tempmap.put(Constants.TAG_COMMENT, comment);
                    tempmap.put(Constants.TAG_USERID, GetSet.getUserId());
                    tempmap.put(Constants.TAG_USERIMG, GetSet.getImageUrl());
                    tempmap.put(Constants.TAG_USERNAME, GetSet.getUserName());
                    tempmap.put(Constants.TAG_COMMENTTIME, "");

                    commentsList.add(tempmap);
                    commentText.setText("");
                    commentsAdapter.notifyDataSetChanged();
                    Commentnotify();
                    nullLay.setVisibility(View.GONE);
                }
                e.printStackTrace();
            } catch (NullPointerException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * This method is to update the comments count in previous pages
     **/
    private void Commentnotify() {
        int count = 0;
        String comment_count = "";
        int pos = getIntent().getExtras().getInt("position");
        if (DetailActivity.itemMap.size() > 0 && DetailActivity.commentCount != null){
            count = Integer.parseInt(DetailActivity.itemMap.get(Constants.TAG_COMMENTCOUNT));
            comment_count =Integer.toString((count + 1));
            DetailActivity.itemMap.put(Constants.TAG_COMMENTCOUNT, comment_count);
            DetailActivity.commentCount.setText(comment_count + " " + getResources().getString(R.string.comments));
            switch (from) {
                case "home":
                    notifyPage(FragmentMainActivity.HomeItems, pos, comment_count);
                    FragmentMainActivity.homeAdapter.notifyDataSetChanged();
                    break;
                case "search":
                    notifyPage(SearchActivity.HomeItems, pos, comment_count);
                    SearchActivity.homeAdapter.notifyDataSetChanged();
                    break;
                case "mylisting":
                    notifyPage(MyListing.AddedItems, pos, comment_count);
                    MyListing.itemAdapter.notifyDataSetChanged();
                    break;
                case "liked":
                    notifyPage(LikedItems.likedItems, pos, comment_count);
                    LikedItems.itemAdapter.notifyDataSetChanged();
                    break;
            }
        }
    }

    private void notifyPage(ArrayList<HashMap<String, String>> data, int pos, String comment_count) {
        try {
            data.get(pos).put(Constants.TAG_COMMENTCOUNT, comment_count);
        } catch (NullPointerException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // For Internet checking disconnect
        wallafyApplication.unregisterReceiver(CommentsActivity.this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // For Internet checking
        wallafyApplication.registerReceiver(CommentsActivity.this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backbtn:
                wallafyApplication.hideSoftKeyboard(CommentsActivity.this);
                finish();
                break;
            case R.id.sendtxt:
                if (GetSet.isLogged()) {
                    if (commentText.getText().toString().trim().length() == 0) {
                        commentText.setError(getResources().getString(R.string.please_give_comments));
                    } else {
                        if (wallafyApplication.isNetworkAvailable(CommentsActivity.this)) {
                            sendtxt.setOnClickListener(null);
                            wallafyApplication.hideSoftKeyboard(CommentsActivity.this);
                            new sendComment().execute(commentText.getText().toString());
                        } else {
                            //wallafyApplication.dialog(CommentsActivity.this, getResources().getString(R.string.error), getResources().getString(R.string.checkconnection));
                        }
                    }
                } else {
                    Intent i = new Intent(CommentsActivity.this, WelcomeActivity.class);
                    startActivity(i);
                }
                break;
        }
    }

}
