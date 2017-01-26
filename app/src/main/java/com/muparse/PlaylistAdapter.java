package com.muparse;

/**
 * Created by fedor on 28.11.2016.
 */


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;


import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.List;

import static android.R.id.content;
import static android.provider.AlarmClock.EXTRA_MESSAGE;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> {

    private List<M3UItem> mItem = new ArrayList<M3UItem>();
    private Context mContext;
    private LayoutInflater mInflater;

    Uri marketUri = Uri.parse("market://details?id=com.mxtech.videoplayer.ad");//("market://search?q=com.mxtech.videoplayer.ad");
    private Intent intent = new Intent();

    public PlaylistAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView name;
        TextView url;

        public ItemHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.item_name);
            url = (TextView) view.findViewById(R.id.item_url);
        }

        public void update(final M3UItem item) {
            name.setText(item.getItemName());
            url.setText(item.getItemUrl());
        }

        public void onClick(View v){
            int position = getLayoutPosition();
            final M3UItem imm = mItem.get(position);
            removeLastChar(imm.getItemUrl());

            Toast.makeText(mContext, "Url: "+imm.getItemUrl(), Toast.LENGTH_LONG).show();
            playy(imm.getItemUrl());
        }

        public void playy(String uriil){
            try{
                Uri videoUri = Uri.parse(uriil); //http://portal.onlineiptv.net:5210/live/000/000/8517.ts
                intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(videoUri, "application/x-mpegURL" );
//                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage( "com.mxtech.videoplayer.ad" );
                mContext.startActivity( intent );

            } catch (Exception e) {
                Toast.makeText(mContext, "Error: "+e.getMessage(), Toast.LENGTH_LONG).show();
            }
//            Intent marketIntent = new Intent(Intent.ACTION_VIEW, marketUri);
//            mContext.startActivity(marketIntent);
        }
    }


    private static String removeLastChar(String str) {
        return str.substring(0,str.length()-1);
    }

    @Override
    public ItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View sView = mInflater.inflate(R.layout.item_playlist, parent, false);
        return new ItemHolder(sView);
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    @Override
    public void onBindViewHolder(final ItemHolder holder, final int position) {
        final M3UItem item = mItem.get(position);
        if (item != null) {
            holder.update(item);
        }
    }

    @Override
    public int getItemCount() {
        return mItem.size();
    }


    public void update(List<M3UItem> _list) {
        this.mItem = _list;
        notifyDataSetChanged();
    }
}
