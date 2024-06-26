/*
 * Copyright 2021 Green Mushroom
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.gm.cleaner.plugin.ui.drawer.playground

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.keyIterator
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import dagger.hilt.android.AndroidEntryPoint
import me.gm.cleaner.plugin.R
import me.gm.cleaner.plugin.app.BaseFragment
import me.gm.cleaner.plugin.databinding.PlaygroundFragmentBinding
import me.gm.cleaner.plugin.ktx.fitsSystemWindowInsets
import me.gm.cleaner.plugin.ktx.overScrollIfContentScrollsPersistent
import me.gm.cleaner.plugin.ui.drawer.playground.PlaygroundContentItems.findIndexById
import rikka.recyclerview.fixEdgeEffect
import java.lang.ref.WeakReference

@AndroidEntryPoint
class PlaygroundFragment : BaseFragment() {
    private val viewModel: PlaygroundViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        val binding = PlaygroundFragmentBinding.inflate(layoutInflater)

        val adapter = PlaygroundAdapter(this, viewModel).apply {
            setHasStableIds(true)
        }
        val list = binding.list
        liftOnScrollTargetView = WeakReference(list)
        list.adapter = adapter
        list.layoutManager = GridLayoutManager(requireContext(), 1)
        list.setHasFixedSize(true)
        list.fixEdgeEffect(false)
        list.overScrollIfContentScrollsPersistent()
        list.fitsSystemWindowInsets()
        list.addItemDecoration(object : RecyclerView.ItemDecoration() {
            private var dividerHeight = resources.getDimensionPixelSize(R.dimen.card_margin)

            override fun getItemOffsets(
                outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
            ) {
                outRect.bottom = dividerHeight
            }
        })

        viewModel.prepareContentItems(this, adapter)
        viewModel.unsplashPhotosLiveData.observe(viewLifecycleOwner) {
            val changedItemIds = mutableListOf<Int>()
            viewModel.actions.keyIterator().forEach { id ->
                if (!viewModel.actions[id].isActive) {
                    changedItemIds.add(id)
                    val position = adapter.currentList.findIndexById(id)
                    adapter.notifyItemChanged(position)
                }
            }
            changedItemIds.forEach { id ->
                viewModel.actions.remove(id)
            }
        }
        return binding.root
    }
}
