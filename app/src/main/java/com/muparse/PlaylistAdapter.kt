package com.muparse

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.amulyakhare.textdrawable.TextDrawable
import com.amulyakhare.textdrawable.util.ColorGenerator
import com.bumptech.glide.Glide
import java.util.*

class PlaylistAdapter internal constructor(private val mContext: Context) : RecyclerView.Adapter<PlaylistAdapter.ItemHolder>(), Filterable {
    private lateinit var mItem: MutableList<M3UItem>
    private var filteredList: MutableList<M3UItem> = ArrayList()
    private var textDrawable: TextDrawable? = null
    private val generator = ColorGenerator.MATERIAL
    private val playerId = 0
    private val pm = mContext.packageManager

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        val sView = LayoutInflater.from(parent.context).inflate(R.layout.item_playlist, parent, false)
        return ItemHolder(sView)
    }

    override fun getItemViewType(position: Int): Int {
        return 0
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        mItem[position].apply {
            holder.update(this)

            holder.rel.setOnClickListener {
                when (playerId) {
                    0 -> {
                        val intent = Intent(mContext, PlayerExo::class.java)
                        intent.putExtra("Name", itemName)
                        intent.putExtra("Url", itemUrl)
                        Log.e("Google", itemUrl)
                        mContext.startActivity(intent)
                    }
                    1 -> {
                        val isApp = Utils.getInstance().isPackageInstalled(pm)
                        if (isApp) {
                            holder.playy(itemUrl!!, itemName!!)
                        } else { // isApplicationNotFound
                            holder.playerNotFound(mContext, "iPtv", "Player not found. Install MX Player.")
                        }
                    }
                }
            }

        }
    }

    override fun getItemCount(): Int {
        return mItem.size
    }

    fun update(_list: MutableList<M3UItem>) {
        this.mItem = _list
        notifyDataSetChanged()
    }

    override fun getFilter(): Filter {
        return object : Filter() { //TODO search it on github
            override fun publishResults(constraint: CharSequence, results: FilterResults) {
//                mItem.clear()
                //mItem.addAll((ArrayList<M3UItem>) results.values);
                notifyDataSetChanged()
            }

            override fun performFiltering(constraint: CharSequence): FilterResults {
                filteredList = mItem
                return FilterResults().apply {
                    if (constraint.isNotEmpty()) {
                        filteredList.clear()
                        val filtePatt = constraint.toString().toLowerCase().trim { it <= ' ' }
                        for (itm in mItem) {
                            if (itm.itemName!!.toLowerCase().contains(filtePatt)) {
                                Toast.makeText(mContext, "Google", Toast.LENGTH_SHORT).show()
                                filteredList.add(itm)
                            }
//                            mItem.add(itm)
                        }
                    }
                    values = filteredList
                    count = filteredList.size
                }

            }
        }
    }

    inner class ItemHolder internal constructor(view: View) : RecyclerView.ViewHolder(view) {

        internal var name: TextView = view.findViewById(R.id.item_name)
        private var cImg: ImageView = view.findViewById(R.id.cimg)
        var rel: RelativeLayout = view.findViewById(R.id.relLayout)

        internal fun update(item: M3UItem) {
            try {
                name.text = item.itemName
                val color = generator.randomColor
                if (item.itemIcon!!.isEmpty()) {
                    textDrawable = TextDrawable.builder()
                            .buildRoundRect(item.itemName!![0].toString(), color, 100)
                    cImg.setImageDrawable(textDrawable)
                } else {
                    if (Utils.getInstance().isNetworkAvailable(mContext)) {
                        Glide.with(mContext).load(item.itemIcon).into(cImg)
                    } else {
                        textDrawable = TextDrawable.builder()
                                .buildRoundRect(item.itemName!![0].toString(), color, 100)
                        cImg.setImageDrawable(textDrawable)
                    }
                }
            } catch (ignored: Exception) {
            }

        }

        internal fun playy(urli: String, channel: String) {
            try {
                val videoUri = Uri.parse(urli)
                val intent = Intent(Intent.ACTION_VIEW)
                intent.setDataAndType(videoUri, "application/x-mpegURL")
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                intent.setPackage("com.mxtech.videoplayer.ad")
                intent.putExtra("title", channel)
                mContext.startActivity(intent)
            } catch (ignored: Exception) {
            }

        }

        internal fun playerNotFound(context: Context, title: String, message: String) {
            val localBuilder = AlertDialog.Builder(context)
            localBuilder.setTitle(title)
            localBuilder.setMessage(message)
            localBuilder.setPositiveButton("Install") { paramAnonymousDialogInterface, paramAnonymousInt ->
                try {
                    mContext.startActivity(Intent("android.intent.action.VIEW", Uri.parse("market://details?id=com.mxtech.videoplayer.ad")))
                } catch (ActivityNotFoundException: ActivityNotFoundException) {
                    mContext.startActivity(Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/details?id=com.mxtech.videoplayer.ad&hl=en")))
                }
            }
            //        localBuilder.setNegativeButton("Google", new DialogInterface.OnClickListener()
            //        {
            //            public void onClick(DialogInterface paramAnonymousDialogInterface, int paramAnonymousInt) {}
            //        });
            //localBuilder.setCancelable(false);
            localBuilder.create().show()
        }
    }
}
