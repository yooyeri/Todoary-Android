package com.uni.todoary.feature.auth.ui.view

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.uni.todoary.databinding.ActivityLoginBinding
import com.uni.todoary.feature.auth.data.dto.User
import com.uni.todoary.feature.main.ui.view.MainActivity
import com.uni.todoary.util.*
import android.widget.Toast
import androidx.activity.viewModels
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.uni.todoary.base.ApiResult
import com.uni.todoary.feature.auth.data.module.LoginRequest
import com.uni.todoary.feature.auth.data.view.GetProfileView
import com.uni.todoary.feature.auth.ui.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity : AppCompatActivity(), GetProfileView {
    lateinit var binding : ActivityLoginBinding
    private val loginModel : LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 키보드에서 엔터 입력시 바로 로그인 되도록 구현
        binding.loginPwEt.setOnEditorActionListener(object : TextView.OnEditorActionListener{
            override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
                if (actionId == EditorInfo.IME_ACTION_DONE){
                    login()
                    hideKeyboard(binding.loginPwEt)
                    return true
                }
                return false
            }
        })

        binding.loginBtnTv.setOnClickListener {
            login()
        }
        binding.loginBtnGoogleLayout.setOnClickListener {
            // TODO : 아래부분은 더미데이터 User 삽입하는 부분 추후 지우고 소셜로그인 구현
            val userData = User(null, "규규>.<", "귀여운 규규얌 쀼쀼쀼", "hyuns6677@gmail.com", "madpotato0606")
            saveUser(userData)
        }
        binding.loginBtnSignInTv.setOnClickListener {
            val mIntent = Intent(this, TermscheckActivity::class.java)
            mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            startActivity(mIntent)
        }
        binding.loginForgotPwTv.setOnClickListener {
            val mIntent = Intent(this, FindPwActivity::class.java)
            startActivity(mIntent)
        }

        addObservers()
    }

    private fun addObservers(){
        // 로그인 Response 옵저버
        loginModel.login_resp.observe(this, {
            when (it.status){
                ApiResult.Status.SUCCESS -> {
                    // 유저 정보 가져와서 캐싱
                    loginModel.getUserProfile(binding.loginPwEt.text.toString())
                }
                ApiResult.Status.LOADING -> {

                }
                ApiResult.Status.API_ERROR -> {
                    when (it.code){
                        2005, 2011 -> {
                            binding.loginPwEt.startAnimation(AnimationUtils.loadAnimation(this, com.uni.todoary.R.anim.shake))
                            binding.loginIdEt.startAnimation(AnimationUtils.loadAnimation(this, com.uni.todoary.R.anim.shake))
                            Snackbar.make(binding.loginBtnTv, "아이디 또는 비밀번호가 틀렸습니다.", Snackbar.LENGTH_SHORT).show()
                        }
                        2112 -> {
                            binding.loginPwEt.startAnimation(AnimationUtils.loadAnimation(this, com.uni.todoary.R.anim.shake))
                            Snackbar.make(binding.loginBtnTv, "비밀번호를 확인해 주세요.", Snackbar.LENGTH_SHORT).show()
                        }
                        2012 -> {
                            Snackbar.make(binding.loginBtnTv, "정지된 계정입니다.", Snackbar.LENGTH_SHORT).show()
                        }
                        4000 -> {
                            Snackbar.make(binding.loginBtnTv, "데이터베이스 연결에 실패하였습니다. 반복될 시 개발자에게 문의해 주세요.", Snackbar.LENGTH_SHORT).show()
                        }
                    }
                }
                ApiResult.Status.NETWORK_ERROR -> {
                    Toast.makeText(this, "인터넷 연결이 원활하지 않습니다.", Toast.LENGTH_SHORT).show()
                    Log.d("Login_Api_Network_Error", it.message!!)
                }
            }
        })
        loginModel.isProfileSuccess.observe(this, {
            if (it){
                // 자동 로그인 여부 캐싱
                if (binding.loginAutoCheckBox.isChecked){
                    loginModel.saveIsAutoLogin(true)
                } else {
                    loginModel.saveIsAutoLogin(false)
                }
                // 보안키 설정 해 두었는지 여부 확인
                if (getSecureKey() == null){
                    val mIntent = Intent(this, MainActivity::class.java)
                    mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(mIntent)
                    finish()
                } else {
                    val mIntent = Intent(this, PwLockActivity::class.java)
                    mIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    startActivity(mIntent)
                    finish()
                }
            } else {
                Toast.makeText(this, "제대로된 유저 정보를 불러오지 못하였습니다.", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun getFCMToken() : String{
        var token = ""
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener(OnCompleteListener { task ->
                if (!task.isSuccessful) {
                    Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                    return@OnCompleteListener
                }

                // Get new FCM registration token
                token = task.result

                // Log and toast
//                Log.d("registration token", token) // 로그에 찍히기에 서버에게 보내줘야됨
//                Toast.makeText(this@MainActivity, token, Toast.LENGTH_SHORT).show()
            })
        return token
    }

    // 아이디 패스워드 sharedPreferences에서 확인 후 맞으면 로그인, 틀리면 애니메이션 & 안내메시지
    private fun login() {
        val fcmToken = getFCMToken()
        // 로그인 API 호출
        val request =
            LoginRequest(binding.loginIdEt.text.toString(), binding.loginPwEt.text.toString(), fcmToken)
        loginModel.login(request)
    }

    fun hideKeyboard(v: View) {
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
        imm?.hideSoftInputFromWindow(v.windowToken, 0)
    }

    override fun getProfileLoading() {

    }

    override fun getProfileSuccess(result: User) {
        result.password = binding.loginPwEt.text.toString()
        Log.d("user", result.toString())
        saveUser(result)
    }

    override fun getProfileFailure(code: Int) {
        when (code){
            2010, 4000 -> {
                Toast.makeText(this, "제대로 된 유저 정보를 불러오지 못하였습니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}