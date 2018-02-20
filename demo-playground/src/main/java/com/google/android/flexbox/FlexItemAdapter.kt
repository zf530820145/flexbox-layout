/*
 * Copyright 2017 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.android.flexbox

import android.support.v7.app.AppCompatActivity
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.ViewGroup
import com.google.android.apps.flexbox.R
import java.util.*

/**
 * [RecyclerView.Adapter] implementation for [FlexItemViewHolder].
 */
internal class FlexItemAdapter(private val activity: AppCompatActivity, private val flexContainer: FlexContainer) : RecyclerView.Adapter<FlexItemViewHolder>() {

    private var layoutParams = mutableListOf<FlexboxLayoutManager.LayoutParams>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlexItemViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.viewholder_flex_item, parent, false)

        return FlexItemViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlexItemViewHolder, position: Int) {
        val adapterPosition = holder.adapterPosition
        // TODO: More optimized set the click listener inside the view holder
        holder.itemView.setOnClickListener(FlexItemClickListener(activity,
                FlexItemChangedListenerImplRecyclerView(flexContainer, this),
                adapterPosition))
        holder.bindTo(layoutParams[position])
    }

    fun addItem(lp: FlexboxLayoutManager.LayoutParams) {
        layoutParams.add(lp)
        notifyItemInserted(layoutParams.size - 1)
    }

    fun addRandomItem(lp: FlexboxLayoutManager.LayoutParams) {
        val random = Random()
        val oldItems = layoutParams.toMutableList()
        val bound = if (layoutParams.size == 0) 1 else layoutParams.size
        layoutParams.add(random.nextInt(bound), lp)
        val diffcallback = MyDiffUtilCallback(oldItems, layoutParams)
        val diffResult = DiffUtil.calculateDiff(diffcallback, true)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeItem(position: Int) {
        if (position < 0 || position >= layoutParams.size) {
            return
        }
//        layoutParams.removeAt(position)
//        notifyItemRemoved(layoutParams.size)
//        notifyItemRangeChanged(position, layoutParams.size)

        val newItems = layoutParams.toMutableList()
        newItems.removeAt(position)
        val diffcallback = MyDiffUtilCallback(layoutParams.toList(), newItems)
        layoutParams = newItems
        val diffResult = DiffUtil.calculateDiff(diffcallback, true)
        diffResult.dispatchUpdatesTo(this)
    }

    fun removeRandomItem() {
        val newItems = layoutParams.toMutableList()
        val random = Random()
        val index = if (newItems.size == 1) 0 else random.nextInt(newItems.size - 1)
        newItems.removeAt(index)
        val diffcallback = MyDiffUtilCallback(layoutParams.toList(), newItems)
        layoutParams = newItems
        val diffResult = DiffUtil.calculateDiff(diffcallback, true)
        diffResult.dispatchUpdatesTo(this)
    }

    fun updateItems(newItems: MutableList<FlexboxLayoutManager.LayoutParams>) {
        val diffcallback = MyDiffUtilCallback(layoutParams.toList(), newItems)
        layoutParams = newItems
        val diffResult = DiffUtil.calculateDiff(diffcallback, true)
        diffResult.dispatchUpdatesTo(this)
    }

    val items get() = layoutParams

    override fun getItemCount() = layoutParams.size

    class MyDiffUtilCallback(private val oldItems: List<FlexboxLayoutManager.LayoutParams>,
                             private val newItems: List<FlexboxLayoutManager.LayoutParams>)
        : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] === newItems[newItemPosition]
        }

        override fun getOldListSize(): Int {
            return oldItems.size
        }

        override fun getNewListSize(): Int {
            return newItems.size
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return oldItems[oldItemPosition] == newItems[newItemPosition]
        }
    }
}
