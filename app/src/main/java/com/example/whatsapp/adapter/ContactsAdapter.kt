package com.example.whatsapp.adapter


import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.whatsapp.R
import com.example.whatsapp.model.UserModel
import de.hdodenhof.circleimageview.CircleImageView

class ContactsAdapter(var contactList: MutableList<UserModel>, var context: FragmentActivity?): RecyclerView.Adapter<ContactsAdapter.MyContactsViewHolder>() {

    class MyContactsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val username = itemView.findViewById<TextView>(R.id.textUsername)
        val userEmail = itemView.findViewById<TextView>(R.id.textUserEmail)
        val image = itemView.findViewById<CircleImageView>(R.id.circleImageContacts)


    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyContactsViewHolder {
        val context = parent.context
        val inflater = LayoutInflater.from(context)
        val itemList = inflater.inflate(R.layout.contact_list_adapter, parent, false)

        return MyContactsViewHolder(itemList)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    override fun onBindViewHolder(holder: MyContactsViewHolder, position: Int) {
        val contact = contactList[position]
        val header = contact.email.isNullOrEmpty()

        holder.username.text = contact.name
        holder.userEmail.text = contact.email

        if(contact.photo != "") {
            val uri = Uri.parse(contact.photo)
            Glide.with(context!!).load(uri).into(holder.image)
        } else {
            if(header) {
                holder.image.setImageResource(R.drawable.icone_grupo)
                holder.userEmail.visibility = GONE
            } else {
                holder.image.setImageResource(R.drawable.padrao)
            }
        }
    }

    fun getContacts(): MutableList<UserModel> {
        return contactList
    }
}