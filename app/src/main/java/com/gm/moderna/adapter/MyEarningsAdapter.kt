package com.gm.moderna.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.gm.moderna.R
import com.gm.moderna.model.MyEarnings
import com.squareup.picasso.Picasso


class MyEarningsAdapter (
    val activity: Activity,
    myEarnings : ArrayList<MyEarnings>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val myEarnings : ArrayList<MyEarnings>
    val activitys: Activity

    init {
        this.myEarnings = myEarnings
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.layout_myearnings, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder:ItemHolder = holderParent as ItemHolder
        val report: MyEarnings = myEarnings[position]
        holder.txtOrderId.text=report.id
        holder.txtProductname.text=report.product_name
        holder.txtEarnAmount.text=report.earnings
        holder.txtUserName.text=report.user_name
        holder.txtDate.text=report.date

        Picasso.get()
            .load(report.image)
            .fit()
            .centerInside()
            .placeholder(R.drawable.placeholder)
            .error(R.drawable.placeholder)
            .into(holder.imgProduct)

    }



    override fun getItemCount(): Int {
        return myEarnings.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val txtOrderId: TextView
        val txtProductname: TextView
        val txtEarnAmount: TextView
        val txtUserName: TextView
        val txtDate: TextView
        val imgProduct: ImageView


        init {
            imgProduct = itemView.findViewById(R.id.imgProduct)
            txtDate = itemView.findViewById(R.id.txtDate)
            txtUserName = itemView.findViewById(R.id.txtUserName)
            txtEarnAmount = itemView.findViewById(R.id.txtEarnAmount)
            txtProductname = itemView.findViewById(R.id.txtProductname)
            txtOrderId = itemView.findViewById(R.id.txtOrderId)

        }
    }
}