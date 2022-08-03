package com.uni.todoary.feature.category.ui.view

import android.util.Log
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.uni.todoary.base.BaseActivity
import com.uni.todoary.databinding.ActivityCategoryBinding
import com.uni.todoary.feature.main.data.dto.TodoListAlarm
import com.uni.todoary.feature.main.data.dto.TodoListInfo

class CategoryActivity : BaseActivity<ActivityCategoryBinding>(ActivityCategoryBinding::inflate) {

    companion object {
        private val todoListAdapter = CategoryTodoListRVAdapter()
    }

    override fun initAfterBinding() {
        initView()

        // TODO : API 연결하면서 더미데이터 생성하는 부분 없애기
        val todoLists = arrayListOf<TodoListInfo>()
        todoLists.add(TodoListInfo(true, true, "뛝쁅뽥쬻뀷뀛끵꽓뜛춁뒑퉳줡뚊뀖꾧", arrayListOf("아랄아랄", "기릴기릴"), TodoListAlarm(false, 6, 30)))
        todoLists.add(TodoListInfo(true, false, "뛝쁅뽥쬻뀷뀛끵꽓뜛춁뒑퉳줡뚊뀖꾧", arrayListOf("오롤오롤", "기릴기릴"), TodoListAlarm(true, 7, 30)))
        todoLists.add(TodoListInfo(false, true, "뛝쁅뽥쬻뀷뀛끵꽓뜛춁뒑퉳줡뚊뀖꾧", arrayListOf("구룰구룰", "기릴기릴"), TodoListAlarm(true, 6, 45)))
        todoLists.add(TodoListInfo(false, false, "뛝쁅뽥쬻뀷뀛끵꽓뜛춁뒑퉳줡뚊뀖꾧", arrayListOf("끼릭끼릭", "기릴기릴"), TodoListAlarm(false, 6, 45)))
        setTodolist(todoLists)
    }

    private fun initView(){
        binding.categoryTodoListsRv.apply {
            adapter = todoListAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            overScrollMode = RecyclerView.OVER_SCROLL_NEVER
        }
    }

    private fun setTodolist(dataset : ArrayList<TodoListInfo>){
        Log.d("dataset", dataset.toString())
        todoListAdapter.initTodoLists(dataset)
    }
}