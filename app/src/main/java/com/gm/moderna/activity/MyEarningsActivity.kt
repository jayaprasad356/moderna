package com.gm.moderna.activity

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.gm.moderna.R
import com.gm.moderna.adapter.MyEarningsAdapter
import com.gm.moderna.fragment.AddressListFragment.activity
import com.gm.moderna.model.MyEarnings

class MyEarningsActivity : AppCompatActivity() {

    lateinit var rvMyEarnings: RecyclerView
    lateinit var activity: Activity
    lateinit var tvWithdrawal : TextView
    lateinit var ivBack : ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_earnings)

        activity = this


        rvMyEarnings = findViewById(R.id.rvMyEarnings);
        tvWithdrawal = findViewById(R.id.tvWithdrawal)
        ivBack = findViewById(R.id.ivBack)

        ivBack.setOnClickListener {

            onBackPressed()

        }

        tvWithdrawal.setOnClickListener {

            val Intent = Intent(this, WithdrawalActivity::class.java)
            startActivity(Intent)

        }

        val linearLayoutManager = LinearLayoutManager(activity, LinearLayoutManager.VERTICAL, false)
        rvMyEarnings.layoutManager = linearLayoutManager

        Myearnings()
    }


    private fun Myearnings() {

        val myEarnings = ArrayList<MyEarnings>()


        myEarnings.add(MyEarnings("1","https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.pexels.com%2Fsearch%2Fbeauty%2F&psig=AOvVaw0QZ4Z4Z4Z4Z4Z4Z4Z4Z4Z4&ust=1629789855554000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJjQ4ZqH9_ICFQAAAAAdAAAAABAD","Product Name","$ 100","Tamil","12/12/2020"))
        myEarnings.add(MyEarnings("2","https://www.google.com/url?sa=i&url=https%3A%2F%2Fwww.pexels.com%2Fsearch%2Fbeauty%2F&psig=AOvVaw0QZ4Z4Z4Z4Z4Z4Z4Z4Z4Z4&ust=1629789855554000&source=images&cd=vfe&ved=0CAsQjRxqFwoTCJjQ4ZqH9_ICFQAAAAAdAAAAABAD","Product Name","$ 100","Tamil","12/12/2020"))

        val adapter = MyEarningsAdapter(activity, myEarnings)
        rvMyEarnings.adapter = adapter


    }

}

