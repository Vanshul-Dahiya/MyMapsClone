package com.example.mymaps

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.view.menu.MenuView
import androidx.recyclerview.widget.RecyclerView
import com.example.mymaps.model.UserMap

const val TAG = "MapsAdapter"
class MapsAdapter(val context: Context,val usermaps : List<UserMap>,val onclickListener: onClickListener) : RecyclerView.Adapter<MapsAdapter.ViewHolder>() {

//    notify when item is clicked
    interface onClickListener{
        fun onItemClick(position: Int)
    }


    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_user_map,parent ,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val userMap = usermaps[position]
//        the idea of this click listener is
//        when the itemview is clicked on we want to fire the method on that interface
        holder.itemView.setOnClickListener{
            Log.i(TAG, "onBindViewHolder: item clicked @ $position ")
            Toast.makeText(context,"Item clicked -> $position", Toast.LENGTH_SHORT).show()
            onclickListener.onItemClick(position)
        }
         val textViewTitle = holder.itemView.findViewById<TextView>(R.id.tvMapTitle)
        textViewTitle.text = userMap.title
    }

    override fun getItemCount() = usermaps.size

}