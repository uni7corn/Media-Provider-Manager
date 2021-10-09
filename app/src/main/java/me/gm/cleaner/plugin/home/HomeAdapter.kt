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

package me.gm.cleaner.plugin.home

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import me.gm.cleaner.plugin.BinderReceiver
import me.gm.cleaner.plugin.BuildConfig
import me.gm.cleaner.plugin.R
import me.gm.cleaner.plugin.dao.ModulePreferences
import me.gm.cleaner.plugin.databinding.HomeButtonBinding
import me.gm.cleaner.plugin.databinding.HomeCardBinding
import me.gm.cleaner.plugin.databinding.HomeCardButtonBinding
import me.gm.cleaner.plugin.settings.AppListActivity
import me.gm.cleaner.plugin.test.TestActivity
import me.gm.cleaner.plugin.usage.UsageRecordActivity

class HomeAdapter(private val activity: HomeActivity) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun getItemViewType(position: Int): Int = when (position) {
        0 -> TYPE_CARD
        1 -> TYPE_CARD_BUTTON
        else -> TYPE_BUTTON
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder =
        when (viewType) {
            TYPE_CARD -> CardHolder(HomeCardBinding.inflate(LayoutInflater.from(parent.context)))
            TYPE_CARD_BUTTON -> CardButtonHolder(
                HomeCardButtonBinding.inflate(LayoutInflater.from(parent.context))
            )
            TYPE_BUTTON -> ButtonHolder(HomeButtonBinding.inflate(LayoutInflater.from(parent.context)))
            else -> throw IllegalArgumentException("undefined view type")
        }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val ver = BinderReceiver.serviceVersion
        when (position) {
            0 -> {
                val binding = (holder as CardHolder).binding
                when {
                    ver == -1 -> {
                        binding.status.setCardBackgroundColor(activity.getColor(R.color.service_hint_error))
                        binding.icon.setImageResource(R.drawable.ic_baseline_error_24)
                        binding.title.setText(R.string.module_not_active)
                        binding.summary.visibility = View.GONE
                    }
                    BuildConfig.VERSION_CODE != ver -> {
                        binding.status.setCardBackgroundColor(activity.getColor(R.color.service_hint_warn))
                        binding.title.setText(R.string.restart_system)
                        binding.summary.text = activity.getString(R.string.module_version, ver)
                    }
                    else -> {
                        binding.summary.text = activity.getString(R.string.module_version, ver)
                    }
                }
                return
            }
            1 -> {
                val binding = (holder as CardButtonHolder).binding
                val count = 114514
                binding.summary.text = activity.getString(R.string.enabled_app_count, count)
                if (ver == -1) {
                    binding.root.isEnabled = false
                } else {
                    binding.root.setOnClickListener {
                        activity.startActivity(Intent(activity, AppListActivity::class.java))
                    }
                }
                return
            }
        }
        val binding = (holder as ButtonHolder).binding
        when (position - OFFSET) {
            0 -> {
                binding.icon.setImageResource(R.drawable.ic_outline_history_24)
                binding.title.setText(R.string.usage_record)
                binding.background.setOnClickListener {
                    activity.startActivity(Intent(activity, UsageRecordActivity::class.java))
                }
            }
            1 -> {
                binding.icon.setImageResource(R.drawable.ic_outline_insert_photo_24)
                binding.title.setText(R.string.test)
                binding.background.setOnClickListener {
                    activity.startActivity(Intent(activity, TestActivity::class.java))
                }
            }
            2 -> {
                binding.icon.setImageResource(R.drawable.ic_outline_info_24)
                binding.title.setText(R.string.about)
                binding.background.setOnClickListener {
                    activity.startActivity(Intent(activity, AboutActivity::class.java))
                }
            }
        }
    }

    override fun getItemCount(): Int = 3 + OFFSET

    class CardHolder(val binding: HomeCardBinding) : RecyclerView.ViewHolder(binding.root)

    class CardButtonHolder(val binding: HomeCardButtonBinding) :
        RecyclerView.ViewHolder(binding.root)

    class ButtonHolder(val binding: HomeButtonBinding) : RecyclerView.ViewHolder(binding.root)

    companion object {
        const val OFFSET = 2
        private val TYPE_CARD = View.generateViewId()
        private val TYPE_CARD_BUTTON = View.generateViewId()
        private val TYPE_BUTTON = View.generateViewId()
    }

    init {
        BinderReceiver.MODULE_VER.observe(activity) {
            notifyItemRangeChanged(0, 2)
        }
        ModulePreferences.setOnPreferenceChangeListener(object :
            ModulePreferences.PreferencesChangeListener {
            override fun getLifecycleState() = activity
            override fun onPreferencesChanged(isNotifyService: Boolean) {
                notifyItemChanged(1)
            }
        })
    }
}
