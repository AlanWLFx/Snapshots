package com.app.snapshots

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.firebase.ui.auth.AuthUI
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.app.snapshots.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.*
import com.firebase.ui.auth.IdpResponse

class MainActivity : AppCompatActivity() {

    private val RC_SIGN_IN = 84

    private lateinit var mBinding: ActivityMainBinding

    private lateinit var mActivityFragment: Fragment
    private lateinit var mFragmentManager: FragmentManager

    private lateinit var mAuthListener: FirebaseAuth.AuthStateListener
    private var mFirebaseAuth: FirebaseAuth? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setupAuth()
        setupBottomNav()
    }

    private fun setupAuth() {
        mFirebaseAuth = FirebaseAuth.getInstance()
        mAuthListener = FirebaseAuth.AuthStateListener {
            val user = it.currentUser
            if (user == null) {
                startActivityForResult(
                    AuthUI.getInstance().createSignInIntentBuilder()
                        .setAvailableProviders(
                            Arrays.asList(
                                AuthUI.IdpConfig.EmailBuilder().build(),
                                AuthUI.IdpConfig.GoogleBuilder().build()
                            )
                        ).build(), RC_SIGN_IN
                )
            }
        }
    }

    private fun setupBottomNav() {
        mFragmentManager = supportFragmentManager

        val homeFragment = HomeFragment()
        val addFragment = AddFragment()
        val profileFragment = ProfileFragment()

        mActivityFragment = homeFragment

        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, profileFragment, ProfileFragment::class.java.name)
            .hide(profileFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, addFragment, AddFragment::class.java.name)
            .hide(addFragment).commit()
        mFragmentManager.beginTransaction()
            .add(R.id.hostFragment, homeFragment, HomeFragment::class.java.name).commit()

        mBinding.bottomNav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.action_home -> {
                    mFragmentManager.beginTransaction().hide(mActivityFragment).show(homeFragment)
                        .commit()
                    mActivityFragment = homeFragment
                    true
                }
                R.id.action_add -> {
                    mFragmentManager.beginTransaction().hide(mActivityFragment).show(addFragment)
                        .commit()
                    mActivityFragment = addFragment
                    true
                }
                R.id.action_profile -> {
                    mFragmentManager.beginTransaction().hide(mActivityFragment)
                        .show(profileFragment).commit()
                    mActivityFragment = profileFragment
                    true
                }
                else -> false
            }
        }

        override fun onResume() {
            super.onResume()
            mFirebaseAuth?.addAuthStateListener(mAuthListener)
        }

        override fun onPause() {
            super.onPause()
            mFirebaseAuth?.removeAuthStateListener(mAuthListener)
        }

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == RC_SIGN_IN){
            if(resultCode == RESULT_OK){
                Toast.makeText(this, "Bienvenido....", Toast.LENGTH_SHORT).show()
            } else {
                if(IdpResponse.fromResultIntent(data) == null){
                    finish()
                }
            }
        }
    }
}