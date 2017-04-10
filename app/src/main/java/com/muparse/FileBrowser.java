package com.muparse; /*
  Created by Sajjad on 2/25/2017.
 */

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.HashMap;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.muparse.R;

public class FileBrowser {

    public interface OnFileSelectedListener {
        void onFileSelected(String path);
    }

    private static final String KEY_ICON = "Icon";
    private static final String KEY_NAME = "Name";

    private static void refreshData(File root, File now,
                                    FileFilter filter, ArrayList<HashMap<String, Object>> data,
                                    ArrayList<File> files, SimpleAdapter adapter) {
        data.clear();
        files.clear();
        boolean getRoot = root.equals(now);
        if (!getRoot) {
            files.add(now.getParentFile());
        }
        File[] list = now.listFiles(filter);
        ArrayList<File> dirs = new ArrayList<File>();
        ArrayList<File> fils = new ArrayList<File>();
        if (list != null) {
            for (File f : list) {
                if (f.isDirectory()) {
                    dirs.add(f);
                } else {
                    fils.add(f);
                }
            }
        }
        files.addAll(dirs);
        files.addAll(fils);
        for (File f : files) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put(KEY_ICON, (f.isDirectory() ? R.drawable.ic_dir : R.drawable.ic_doc));
            map.put(KEY_NAME, f.getName());
            data.add(map);
        }
        if (!getRoot) {
            data.get(0).put(KEY_ICON, R.drawable.ic_dir);
            data.get(0).put(KEY_NAME, "../");
        }
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    public static AlertDialog createFileBrowser(Context ctx, final OnFileSelectedListener listener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        LinearLayout ll = new LinearLayout(ctx);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.setPadding(8, 8, 8, 8);
        final TextView head = new TextView(ctx);
        final ListView list = new ListView(ctx);
        final File rootDir = new File("/");
        head.setText(rootDir.getPath());
        final FileFilter filter = new FileFilter() {
            @Override
            public boolean accept(File pathname) {
                return pathname.isDirectory() || (pathname.isFile() && pathname.getName().endsWith(".m3u"));
            }
        };
        final ArrayList<HashMap<String, Object>> mData = new ArrayList<HashMap<String, Object>>();
        final ArrayList<File> mFiles = new ArrayList<File>();
        final OnItemClickListener onItemClickListener = new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                File f = mFiles.get(position);
                if (f.isDirectory()) {
                    head.setText(f.getPath());
                    refreshData(rootDir, f, filter, mData, mFiles,
                            (SimpleAdapter) list.getAdapter());
                } else {
                    if (listener != null) {
                        listener.onFileSelected(f.getPath());
                    }
                }
            }
        };
        refreshData(rootDir, rootDir, filter, mData, mFiles, null);
        list.setAdapter(new SimpleAdapter(ctx, mData, R.layout.listitem,
                new String[] { KEY_ICON, KEY_NAME }, new int[] {
                R.id.item_icon, R.id.item_name }));
        list.setOnItemClickListener(onItemClickListener);
        ll.addView(head);
        ll.addView(list);
        builder.setView(ll);
        return builder.create();
    }

}
