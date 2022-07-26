package com.uni.todoary.feature.setting.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.uni.todoary.databinding.ActivityProfileEditBinding
import com.uni.todoary.feature.auth.data.dto.User
import android.app.Activity
import android.provider.MediaStore
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts


class ProfileEditActivity : AppCompatActivity(){
    lateinit var binding: ActivityProfileEditBinding
    private val userModel : ProfileViewModel by viewModels()
    lateinit var getContent : ActivityResultLauncher<Intent>
    val mIntent = Intent(this,ProfileActivity::class.java)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                result: ActivityResult ->
            binding.profileImageIv.setImageURI(result.data?.data)

        }



        initClickListeners()

        // Data Binding
        val userObserver = Observer<User>{ user ->
            binding.profileeditNameEt.setText(user.name)
            binding.profileeditIntroEt.setText(user.intro)
        }
        userModel.user.observe(this, userObserver)
    }


    private fun editpic() {
        //Todo: 사진변경 기능 추가
        val intent = Intent()
        intent.type = "image/*"
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        intent.action = Intent.ACTION_GET_CONTENT
        getContent.launch(intent)
    }

    private fun initClickListeners(){
        //툴바
        binding.profileEdit.toolbarBackMainTv.text = "계정"
        binding.profileEdit.toolbarBackIv.setOnClickListener {
            //finish()
            Changename()
            Changeintro()
            startActivity(mIntent)
        }

        binding.profileditPiceditTv.setOnClickListener {
            editpic()
        }
    }
    private fun Changename() {
        val name = binding.profileeditNameEt.text.toString()
        if(name!=null) {
            mIntent.putExtra("user_name", name)
            Log.d("user_name: ",name)
        }
    }
    private fun Changeintro(){
        val intro = binding.profileeditIntroEt.text.toString()
        if(intro!=null) {
            mIntent.putExtra("user_intro", intro)
            Log.d("user_intro: ",intro)

        }
    }
    override fun onBackPressed() {
        userModel.updateUser(binding.profileeditNameEt.text.toString(), binding.profileeditIntroEt.text.toString())
        finish()
    }
}
