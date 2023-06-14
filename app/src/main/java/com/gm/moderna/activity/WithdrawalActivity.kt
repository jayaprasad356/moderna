package com.gm.moderna.activity

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.moderna.R
import com.gm.moderna.adapter.WithdrawalListAdapter
import com.gm.moderna.model.WithdrawalList

class WithdrawalActivity : AppCompatActivity() {

    lateinit var ivBack : ImageView
    lateinit var  rvWithdrawallist : RecyclerView
    lateinit var activity: Activity


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_withdrawal)

        activity = this


        ivBack = findViewById(R.id.ivBack)
        rvWithdrawallist = findViewById(R.id.rvWithdrawallist)

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvWithdrawallist.layoutManager = linearLayoutManager

        Withdrawallist()

        ivBack.setOnClickListener {

            onBackPressed()

        }
    }


    private fun Withdrawallist() {

        val Withdrawallist = ArrayList<WithdrawalList>()


        Withdrawallist.add(WithdrawalList("1", "Withdrawal", "200"))
        Withdrawallist.add(WithdrawalList("2", "Withdrawal", "200"))

        val adapter = WithdrawalListAdapter(activity, Withdrawallist)
        rvWithdrawallist.adapter = adapter


    }


}