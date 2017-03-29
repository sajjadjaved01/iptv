package com.muparse;

/*
  Created by fedor on 28.11.2016.
 */


import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final MainActivity min = new MainActivity();
    Uri marketUri = Uri.parse("market://details?id=com.mxtech.videoplayer.ad");//("market://search?q=com.mxtech.videoplayer.ad");
    private List<M3UItem> mItem = new ArrayList<M3UItem>();

    public PlaylistAdapter(Context c) {
        mContext = c;
        mInflater = LayoutInflater.from(mContext);
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

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final PackageManager pm = mContext.getPackageManager();
        final boolean isApp = min.isPackageInstalled(pm);
        //TextView url;
        TextView name;
        ImageView cImg;

        public ItemHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = (TextView) view.findViewById(R.id.item_name);
            cImg = (ImageView) view.findViewById(R.id.cimg);
            //url = (TextView) view.findViewById(R.id.item_url);
        }

        public void update(final M3UItem item) {
            name.setText(item.getItemName());
            cImg.setImageResource(R.drawable.info_ico);
            // url.setText(item.getItemUrl());
        }

        public void onClick(View v) {
            try {
                int position = getLayoutPosition();
                final M3UItem imm = mItem.get(position);
                if (isApp) { //isApplicationInstalled
                    playy(imm.getItemUrl(), imm.getItemName());
                } else { // isApplicationNotFound
                    playerNotFound();
                }
            } catch (Exception e) {
                Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        void playy(String urli, String channel) {
            try {
                Uri videoUri = Uri.parse(urli);
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(videoUri, "application/x-mpegURL");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setPackage("com.mxtech.videoplayer.ad");
                intent.putExtra("title", channel);
                mContext.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(mContext, "Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
            }
        }

        void playerNotFound() {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(mContext);
            localBuilder.setTitle("iPtv");
            localBuilder.setMessage("Player not found. Install MX Player.");
            localBuilder.setPositiveButton("Install", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    try {
                        mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.mxtech.videoplayer.ad")));
                    } catch (ActivityNotFoundException localActivityNotFoundException) {
                        mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad&hl=en")));
                    }
                }
            });
//        localBuilder.setNegativeButton("Google", new DialogInterface.OnClickListener()
//        {
//            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
//        });
            //localBuilder.setCancelable(false);
            localBuilder.create().show();
        }

    }
}
