package com.example.whatsapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsapp.R
import com.example.whatsapp.model.ChatModel

import de.hdodenhof.circleimageview.CircleImageView

class ChatAdapter(var contactList: MutableList<ChatModel>, var context: FragmentActivity?) :
    RecyclerView.Adapter<ChatAdapter.MyChatViewHolder>() {

    class MyChatViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var userName: TextView = itemView.findViewById(R.id.textUsername)
        var lastMessage: TextView = itemView.findViewById(R.id.textUserEmail)
        var circleImage: CircleImageView = itemView.findViewById(R.id.circleImageContacts)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyChatViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemList = inflater.inflate(R.layout.contact_list_adapter, parent, false)

        return MyChatViewHolder(itemList)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: MyChatViewHolder, position: Int) {
        val chat = contactList[position]

        val user = chat.userShown


        if (chat.isItGroup == "true") {
            val group = chat.group
            holder.userName.text = group.nameGroup
            holder.lastMessage.text = chat.lastMessage

            if (group.photo != "") {
                val url = Uri.parse(group.photo)

                Glide.with(context!!).load(url).into(holder.circleImage)
            } else {
                holder.circleImage.setImageResource(R.drawable.padrao)
            }

        } else {
            holder.userName.text = user.name
            holder.lastMessage.text = chat.lastMessage

            val image = user.photo
            if (image != "") {
                val url = Uri.parse(image)

                Glide.with(context!!).load(url).into(holder.circleImage)
            } else {
                holder.circleImage.setImageResource(R.drawable.padrao)
            }
        }

    }

    fun getList(): MutableList<ChatModel> {
        return contactList
    }
}
