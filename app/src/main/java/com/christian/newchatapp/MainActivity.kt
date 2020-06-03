package com.christian.newchatapp

import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        replaceFragment(FriendsFragment())
        navigation.setOnNavigationItemSelectedListener {
            when(it.itemId){
                R.id.navigation_friends->{
                    replaceFragment(FriendsFragment())
                    true
                }
                R.id.navigation_my_account->{
                    replaceFragment(MyAccountFragment())
                    true
                }
               else -> false


            }
        }

    }

    private fun replaceFragment(fragment: Fragment) {

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_layout,fragment)
            .commit()

    }
}
