package com.app.wallafy;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.app.external.FragmentChangeListener;
import com.app.utils.Constants;
import com.app.utils.ExchangeParsing;
import com.app.utils.GetSet;
import com.app.utils.SOAPParsing;
import com.squareup.picasso.Picasso;
import com.wang.avi.AVLoadingIndicatorView;

import org.ksoap2.serialization.SoapObject;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by hitasoft on 13/6/16.
 **/
public class IncomeExchange extends Fragment implements FragmentChangeListener, SwipeRefreshLayout.OnRefreshListener {

    ListView mListView;
    LinearLayout nullLay;
    AVLoadingIndicatorView progress;
    public static ExchangeAdapter exchangeAdapter;
    SwipeRefreshLayout swipeLayout;
    public static ArrayList<HashMap<String,String>> incomingAry=new ArrayList<HashMap<String,String>>();
    boolean pulldown = false, loadmore = false;
    public static String type="incoming";
    public static Context context;

    public IncomeExchange(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.exchangefragment, container , false);
        Log.v("checkadapter","income");
        return v;
    }
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mListView = (ListView) getView().findViewById(R.id.listView);
        progress = (AVLoadingIndicatorView) getView().findViewById(R.id.progress);
        nullLay = (LinearLayout) getView().findViewById(R.id.nullLay);
        swipeLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipeLayout);
        Log.v("checkadapter","income1");

        context = getActivity();
        swipeLayout.setColorSchemeColors(getResources().getColor(R.color.swipeColor));
        swipeLayout.setOnRefreshListener(this);

        incomingAry.clear();
        new exchanges().execute("incoming");
        exchangeAdapter = new ExchangeAdapter(getActivity(), incomingAry);
        mListView.setAdapter(exchangeAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (incomingAry.size() > 0) {
                    Intent i = new Intent(context, ExchangeView.class);
                    i.putExtra("data", incomingAry.get(position));
                    i.putExtra("position", position);
                    i.putExtra("type", incomingAry.get(position).get(Constants.TAG_TYPE));
                    startActivity(i);
                }
            }
        });
    }

    @Override
    public void onCentered() {

    }

    private void swipeRefresh(){
        swipeLayout.post(new Runnable() {
            @Override
            public void run() {
                swipeLayout.setRefreshing(true);
            }
        });
    }

    /** class for get the incoming exchanges **/
    class exchanges extends AsyncTask<String, Void, Void> {

        @Override
        protected Void doInBackground(String... params) {
            loadmore = false;
            String SOAP_ACTION = Constants.NAMESPACE + Constants.API_MY_EXCHANGE;

            SoapObject req = new SoapObject(Constants.NAMESPACE, Constants.API_MY_EXCHANGE);
            req.addProperty(Constants.SOAP_USERNAME, Constants.SOAP_USERNAME_VALUE);
            req.addProperty(Constants.SOAP_PASSWORD, Constants.SOAP_PASSWORD_VALUE);
            req.addProperty("user_id", GetSet.getUserId());
            req.addProperty("type", params[0]);

            SOAPParsing soap = new SOAPParsing();
            final String json = soap.getJSONFromUrl(SOAP_ACTION, req);

            if (pulldown){
                incomingAry.clear();
            }
            ((Activity)context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<HashMap<String,String>> temp=new ArrayList<HashMap<String,String>>();
                    ExchangeParsing parse = new ExchangeParsing(context);
                    temp.addAll(parse.parsing(json));
                    if (!incomingAry.contains(temp)){
                        incomingAry.addAll(temp);
                        Log.v("incomingAry","incomingAry=="+incomingAry);
                    }
                }
            });


            return null;
        }

        @Override
        protected void onPreExecute() {
            loadmore = false;
            nullLay.setVisibility(View.INVISIBLE);
            if (pulldown) {
                mListView.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
            } else if (incomingAry.size() > 0) {
                mListView.setVisibility(View.VISIBLE);
                progress.setVisibility(View.GONE);
                swipeRefresh();
            } else {
                mListView.setVisibility(View.INVISIBLE);
                progress.setVisibility(View.VISIBLE);
            }
        }

        @Override
        protected void onPostExecute(Void unused) {
            progress.setVisibility(View.GONE);
            if(pulldown){
                pulldown = false;
            }
            swipeLayout.setRefreshing(false);
            mListView.setVisibility(View.VISIBLE);
            exchangeAdapter.notifyDataSetChanged();
            if(type.equals("incoming")){
                if(incomingAry.size()==0){
                    nullLay.setVisibility(View.VISIBLE);
                }else{
                    nullLay.setVisibility(View.INVISIBLE);
                }
            }
            loadmore = true;
        }

    }

    public class ExchangeAdapter extends BaseAdapter {
        ArrayList<HashMap<String, String>> Items;
        private Context mContext;
        ViewHolder holder = null;
        public ExchangeAdapter(Context ctx,ArrayList<HashMap<String, String>> data) {
            mContext = ctx;
            Items=data;
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
            ImageView myitemImage,exitemImage,userImage;
            TextView exitemName,myitemName,view,status,status2,time,userName;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext
                        .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.exchange_list_item, parent, false);//layout
                holder = new ViewHolder();
                holder.myitemImage = (ImageView) convertView.findViewById(R.id.myitemImage);
                holder.exitemImage = (ImageView) convertView.findViewById(R.id.exitemImage);
                //	holder.exRequest = (TextView) convertView.findViewById(R.id.exRequest);
                holder.exitemName = (TextView) convertView.findViewById(R.id.exitemName);
                holder.myitemName = (TextView) convertView.findViewById(R.id.myitemName);
                holder.view = (TextView) convertView.findViewById(R.id.view);
                holder.status = (TextView) convertView.findViewById(R.id.status);
                holder.status2 = (TextView) convertView.findViewById(R.id.status2);
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.userImage = (ImageView) convertView.findViewById(R.id.userImage);
                holder.userName = (TextView) convertView.findViewById(R.id.userName);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            try{

                final HashMap<String, String> tempMap=Items.get(position);

                if(tempMap.get(Constants.TAG_TYPE).equals("success") || tempMap.get(Constants.TAG_TYPE).equals("failed")){
                    holder.view.setVisibility(View.GONE);
                } else{
                    holder.view.setVisibility(View.VISIBLE);
                }

                Picasso.with(getActivity()).load(tempMap.get("e"+Constants.TAG_ITEMIMAGE)).into(holder.exitemImage);
                Picasso.with(getActivity()).load(tempMap.get("m"+Constants.TAG_ITEMIMAGE)).into(holder.myitemImage);
                Picasso.with(getActivity()).load(tempMap.get(Constants.TAG_EXCHANGERIMG)).placeholder(R.drawable.appicon).into(holder.userImage);

                holder.myitemName.setText(tempMap.get("m"+Constants.TAG_ITEM_NAME));
                holder.exitemName.setText(tempMap.get("e"+Constants.TAG_ITEM_NAME));
                holder.userName.setText(tempMap.get(Constants.TAG_EXCHANGERNAME));
                holder.time.setText(tempMap.get(Constants.TAG_EXCHANGETIME));
                if(tempMap.get(Constants.TAG_STATUS).equals("Pending")){
                    holder.status2.setVisibility(View.VISIBLE);
                    holder.status.setVisibility(View.GONE);
                    holder.status2.setText(tempMap.get(Constants.TAG_STATUS));
                }else  {
                    holder.status.setVisibility(View.VISIBLE);
                    holder.status2.setVisibility(View.GONE);
                    holder.status.setText(tempMap.get(Constants.TAG_STATUS));
                }
                holder.view.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Intent i = new Intent(context, ExchangeView.class);
                        i.putExtra("data", Items.get(position));
                        i.putExtra("position", position);
                        i.putExtra("type", tempMap.get(Constants.TAG_TYPE));
                        startActivity(i);

                    }
                });

            }catch(NullPointerException e){
                e.printStackTrace();
            } catch(Exception e){
                e.printStackTrace();
            }
            return convertView;
        }

    }

    @Override
    public void onRefresh() {
        if (!pulldown && loadmore) {
            pulldown = true;
            new exchanges().execute("incoming");
        } else {
            swipeLayout.setRefreshing(false);
        }
    }

}
