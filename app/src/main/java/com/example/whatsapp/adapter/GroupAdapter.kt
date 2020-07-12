package com.example.whatsapp.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.TextureView
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsapp.R
import com.example.whatsapp.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView


class GroupAdapter(var membersListSelected: MutableList<UserModel>, var context: android.content.Context): RecyclerView.Adapter<GroupAdapter.MyGroupViewHolder>() {

    class MyGroupViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val userName: TextView = itemView.findViewById(R.id.textGroupMemberName)
        val image: CircleImageView = itemView.findViewById(R.id.circleImageGroup)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyGroupViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemList = inflater.inflate(R.layout.group_list_adapter, parent, false)

        return MyGroupViewHolder(itemList)
    }

    override fun getItemCount(): Int {
       return membersListSelected.size
    }

    override fun onBindViewHolder(holder: MyGroupViewHolder, position: Int) {
        val user = membersListSelected[position]

        holder.userName.text = user.name

        if(user.photo != "") {
            val uri = Uri.parse(user.photo)
            Glide.with(context).load(uri).into(holder.image)
        } else {
            holder.image.setImageResource(R.drawable.padrao)
        }
    }
}