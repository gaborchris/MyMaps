package edu.stanford.gaborc.mymaps

import android.content.Context
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import edu.stanford.gaborc.mymaps.models.UserMap
import kotlinx.android.synthetic.main.item_user_map2.view.*

class MapsAdapter(val context: Context, val userMaps : List<UserMap>, val onClickListener: OnClickListener) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {

    interface OnClickListener {
        fun onItemClick(position: Int)
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_map2, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount() = userMaps.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userMap = userMaps[position]
        holder.itemView.setOnClickListener {
            onClickListener.onItemClick(position)
        }
        val textViewTitle = holder.itemView.findViewById<LinearLayout>(R.id.tvMapInfo)
        textViewTitle.tvMapTitle.text = userMap.title
        textViewTitle.tvCount.text =  "Markers: " + userMap.places.size.toString()
//        textViewTitle.text = userMap.title
    }


}
