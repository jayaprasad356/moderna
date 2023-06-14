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
import com.gm.moderna.model.WithdrawalList
import com.squareup.picasso.Picasso


class WithdrawalListAdapter (
    val activity: Activity,
    withdrawalList : ArrayList<WithdrawalList>,
) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    val withdrawalList : ArrayList<WithdrawalList>
    val activitys: Activity

    init {
        this.withdrawalList = withdrawalList
        this.activitys = activity
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view: View =
            LayoutInflater.from(activity).inflate(R.layout.withdrawallist_layout, parent, false)
        return ItemHolder(view)
    }

    override fun onBindViewHolder(holderParent: RecyclerView.ViewHolder, position: Int) {
        val holder:ItemHolder = holderParent as ItemHolder
        val report: WithdrawalList = withdrawalList[position]
        holder.tvAmount.text=report.amount
        holder.tvWithdrawal.text=report.tittle


    }



    override fun getItemCount(): Int {
        return withdrawalList.size
    }

    internal class ItemHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvAmount: TextView
        val tvWithdrawal: TextView



        init {
            tvAmount = itemView.findViewById(R.id.tvAmount)
            tvWithdrawal = itemView.findViewById(R.id.tvWithdrawal)


        }
    }
}