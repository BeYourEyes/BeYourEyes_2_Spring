package com.dna.beyoureyes.ui.myInfo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dna.beyoureyes.R
import com.dna.beyoureyes.data.model.FoodHistory

class FoodHistoryAdapter(
    private val items: MutableList<FoodHistory>,
    private val onItemClickListener: (FoodHistory) -> Unit)
    : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_EMPTY = 1
    }

    // 빈 뷰 개수만 따로 관리
    private var emptyItemCount = 0

    // 섭취 기록 아이템 뷰 홀더 생성
    inner class FoodHistoryViewHolder(private val historyView: FoodHistoryView)
        : RecyclerView.ViewHolder(historyView)
    {
        fun bind(history: FoodHistory) {
            historyView.setData(history.timestamp, history.kcal, history.imgUrl)
            historyView.setOnItemClickListener(history, onItemClickListener)
        }
    }

    // 빈 아이템 뷰 홀더 생성
    inner class EmptyViewHolder(view: View) : RecyclerView.ViewHolder(view)

    override fun getItemCount(): Int = items.size + emptyItemCount

    override fun getItemViewType(position: Int): Int {
        return if (position < items.size) TYPE_ITEM else TYPE_EMPTY
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == TYPE_ITEM) {
            val historyView = FoodHistoryView(parent.context, null)
            FoodHistoryViewHolder(historyView)
        } else {
            val view = LayoutInflater.from(parent.context).inflate(R.layout.food_history_item_null, parent, false)
            EmptyViewHolder(view)
        }
    }

    // 뷰 홀더 데이터 바인딩
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder is FoodHistoryViewHolder && position < items.size) {
            holder.bind(items[position])
        }
    }

    fun updateList(newHistories: List<FoodHistory>) {
        items.clear()
        items.addAll(newHistories)
        emptyItemCount = when {
            newHistories.size >= 5 -> 1
            else -> 5 - newHistories.size
        }
        notifyDataSetChanged()
    }

}