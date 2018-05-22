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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class PlaylistAdapter extends RecyclerView.Adapter<PlaylistAdapter.ItemHolder> implements Filterable {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private List<M3UItem> mItem = new ArrayList<>();
    private TextDrawable textDrawable;
    private ColorGenerator generator = ColorGenerator.MATERIAL;

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

    void update(List<M3UItem> _list) {
        this.mItem = _list;
        notifyDataSetChanged();
    }

    @Override
    public Filter getFilter() {
        return new Filter() { //TODO search it on github
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mItem.clear();
                //mItem.addAll((ArrayList<M3UItem>) results.values);
                notifyDataSetChanged();
            }
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                if (!(constraint.length() == 0)){
                    mItem.clear();
                    final String filtePatt = constraint.toString().toLowerCase().trim();
                    for (M3UItem itm: mItem){
                        if (itm.getItemName().toLowerCase().contains(filtePatt)){
                            Toast.makeText(mContext, "Google", Toast.LENGTH_SHORT).show();
                            mItem.add(itm);
                        }
                        mItem.add(itm);
                    }
                }
                results.values = mItem;
                results.count = mItem.size();
                return results;
            }
        };
    }

    public class ItemHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        final PackageManager pm = mContext.getPackageManager();
        final boolean isApp = Utils.getInstance().isPackageInstalled(pm);
        TextView name;
        ImageView cImg;

        ItemHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            name = view.findViewById(R.id.item_name);
            cImg = view.findViewById(R.id.cimg);
        }

        void update(final M3UItem item) {
            try {
            name.setText(item.getItemName());
                int color = generator.getRandomColor();
                if (item.getItemIcon().isEmpty()) {
                    textDrawable = TextDrawable.builder()
                            .buildRoundRect(String.valueOf(item.getItemName().charAt(0)), color, 100);
                    cImg.setImageDrawable(textDrawable);
                } else {
                    if (Utils.getInstance().isNetworkAvailable(mContext)) {
                        Picasso.with(mContext).load(item.getItemIcon()).into(cImg);
                    } else {
                        textDrawable = TextDrawable.builder()
                                .buildRoundRect(String.valueOf(item.getItemName().charAt(0)), color, 100);
                        cImg.setImageDrawable(textDrawable);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        public void onClick(View v) {
            try {
                int position = getLayoutPosition();
                final M3UItem imm = mItem.get(position);
//                if (isApp) { //isApplicationInstalled
//                    playy(imm.getItemUrl(), imm.getItemName());
//                } else { // isApplicationNotFound
//                    playerNotFound(mContext,"iPtv","Player not found. Install MX Player.");
//                }
                Intent intent = new Intent(mContext, playerExo.class);
                intent.putExtra("Name", imm.getItemName());
                intent.putExtra("Url", imm.getItemUrl());
                Log.e("Google", imm.getItemUrl());
                mContext.startActivity(intent);
            } catch (Exception ignored) {}
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
            } catch (Exception ignored) {}
        }

        void playerNotFound(Context context,String title,String message) {
            AlertDialog.Builder localBuilder = new AlertDialog.Builder(context);
            localBuilder.setTitle(title);
            localBuilder.setMessage(message);
            localBuilder.setPositiveButton("Install", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {
                    try {
                        mContext.startActivity(new Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.mxtech.videoplayer.ad")));
                    } catch (ActivityNotFoundException ActivityNotFoundException) {
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
