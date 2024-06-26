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

package me.gm.cleaner.plugin.ui.mediastore.files

import android.Manifest
import android.os.Build
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import me.gm.cleaner.plugin.R
import me.gm.cleaner.plugin.dao.RootPreferences
import me.gm.cleaner.plugin.dao.RootPreferences.SORT_BY_DATE_TAKEN
import me.gm.cleaner.plugin.dao.RootPreferences.SORT_BY_PATH
import me.gm.cleaner.plugin.dao.RootPreferences.SORT_BY_SIZE
import me.gm.cleaner.plugin.databinding.MediaStoreFragmentBinding
import me.gm.cleaner.plugin.ktx.buildSpannableString
import me.gm.cleaner.plugin.ktx.fitsSystemWindowInsets
import me.gm.cleaner.plugin.ui.mediastore.MediaStoreAdapter
import me.gm.cleaner.plugin.ui.mediastore.MediaStoreFragment
import me.zhanghai.android.fastscroll.FastScrollerBuilder
import me.zhanghai.android.fastscroll.PopupStyle

open class FilesFragment : MediaStoreFragment() {
    override val viewModel: FilesViewModel by viewModels()
    override val requesterFragmentClass: Class<out MediaPermissionsRequesterFragment> =
        FilesPermissionsRequesterFragment::class.java

    override fun onCreateAdapter(): FilesAdapter = FilesAdapter(this)

    override fun onBindView(
        binding: MediaStoreFragmentBinding,
        list: RecyclerView,
        adapter: MediaStoreAdapter
    ) {
        list.layoutManager = GridLayoutManager(requireContext(), 1)
        val fastScroller = FastScrollerBuilder(list)
            .useMd2Style()
            .setPopupStyle(PopupStyle.MD3)
            .setViewHelper(MediaStoreRecyclerViewHelper(list) { adapter.currentList })
            .build()
        list.fitsSystemWindowInsets(fastScroller)
    }

    class FilesPermissionsRequesterFragment : MediaPermissionsRequesterFragment() {
        override val requiredPermissions: Array<String> =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO
                )
            } else {
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        if (selectionTracker.hasSelection()) {
            return
        }
        inflater.inflate(R.menu.files_toolbar, menu)
        val searchItem = menu.findItem(R.id.menu_search)
        if (viewModel.isSearching) {
            searchItem.expandActionView()
        }
        searchItem.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                viewModel.isSearching = true
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                viewModel.isSearching = false
                return true
            }
        })
        val searchView = searchItem.actionView as SearchView
        searchView.setQuery(viewModel.queryText, false)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                viewModel.queryText = query
                return true
            }

            override fun onQueryTextChange(newText: String): Boolean {
                viewModel.queryText = newText
                return false
            }
        })

        when (RootPreferences.sortMediaByFlowable.value) {
            SORT_BY_PATH ->
                menu.findItem(R.id.menu_sort_by_path).isChecked = true

            SORT_BY_DATE_TAKEN ->
                menu.findItem(R.id.menu_sort_by_date_taken).isChecked = true

            SORT_BY_SIZE ->
                menu.findItem(R.id.menu_sort_by_size).isChecked = true
        }
        arrayOf(menu.findItem(R.id.menu_header_sort)).forEach {
            it.title = requireContext().buildSpannableString(it.title!!)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_sort_by_path -> {
                item.isChecked = true
                RootPreferences.sortMediaByFlowable.value = SORT_BY_PATH
            }

            R.id.menu_sort_by_date_taken -> {
                item.isChecked = true
                RootPreferences.sortMediaByFlowable.value = SORT_BY_DATE_TAKEN
            }

            R.id.menu_sort_by_size -> {
                item.isChecked = true
                RootPreferences.sortMediaByFlowable.value = SORT_BY_SIZE
            }

            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }
}
