package com.dna.beyoureyes.ui.myInfo

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.dna.beyoureyes.data.model.FoodHistory

class FoodHistoryAdapter(
    private val items: MutableList<FoodHistory>,
    private val onItemClickListener: (FoodHistory) -> Unit)
    : RecyclerView.Adapter<FoodHistoryAdapter.FoodHistoryViewHolder>() {

    // 뷰 홀더 생성
    inner class FoodHistoryViewHolder(private val historyView: FoodHistoryView)
        : RecyclerView.ViewHolder(historyView)
    {
        fun bind(history: FoodHistory) {
            historyView.setData(history.timestamp, history.kcal, history.imgUri)
            historyView.setOnItemClickListener(history, onItemClickListener)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodHistoryViewHolder {
        val historyView = FoodHistoryView(parent.context, null)
        return FoodHistoryViewHolder(historyView)
    }

    // 뷰 홀더 데이터 바인딩
    override fun onBindViewHolder(holder: FoodHistoryViewHolder, position: Int) {
        val history = items[position]
        holder.bind(history)
    }

    override fun getItemCount() = items.size

}