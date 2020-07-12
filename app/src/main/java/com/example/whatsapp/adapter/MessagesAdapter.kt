package com.example.whatsapp.adapter

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.example.whatsapp.R
import com.example.whatsapp.helper.UserFirebase
import com.example.whatsapp.model.MessageModel

class MessagesAdapter(var messageList: MutableList<MessageModel>, var context: Context) :
    RecyclerView.Adapter<MessagesAdapter.MyMessageViewHolder>() {

    companion object {
        private var SENDER_TYPE = 0
        private var RECEIVER_TYPE = 1
    }

    class MyMessageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val message: TextView = itemView.findViewById(R.id.textMessageChat)
            val image: ImageView = itemView.findViewById(R.id.imageMessagePhoto)
            val name: TextView = itemView.findViewById(R.id.textUserMessageName)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyMessageViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)

        var itemList: View? = null

        if (viewType == SENDER_TYPE) {
            itemList = inflater.inflate(R.layout.message_adapter_sender, parent, false)
        } else if (viewType == RECEIVER_TYPE) {
            itemList = inflater.inflate(R.layout.message_adapter_receiver, parent, false)
        }

        return MyMessageViewHolder(itemList!!)
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    override fun getItemViewType(position: Int): Int {
        val message = messageList[position]

        val userId = UserFirebase.getEncodedEmail()

        if (userId == message.userId) {
            return SENDER_TYPE
        }
        return RECEIVER_TYPE
    }

    override fun onBindViewHolder(holder: MyMessageViewHolder, position: Int) {
        val message = messageList[position]
        val msg = message.message
        val image: String = message.image

       if(image != "") {
            val url = Uri.parse(image)
            Glide.with(context).load(url).into(holder.image)

            //Hide text
            holder.message.visibility = View.GONE

           val _name = message.name

           if( _name.isNotEmpty()) {
               holder.name.text = _name
           } else {
               holder.name.visibility = View.GONE
           }

        } else {
            holder.message.text = msg

           val _name = message.name

           if( _name.isNotEmpty()) {
               holder.name.text = _name
           } else {
               holder.name.visibility = View.GONE
           }

            holder.image.visibility = View.GONE

       }
   }

}