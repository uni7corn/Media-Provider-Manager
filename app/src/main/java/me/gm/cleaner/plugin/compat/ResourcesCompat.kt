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

package me.gm.cleaner.plugin.compat

import android.content.res.Configuration
import android.os.Build
import android.view.View

val Configuration.isNightModeActivated
    get() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        isNightModeActive
    } else {
        uiMode and Configuration.UI_MODE_NIGHT_MASK == Configuration.UI_MODE_NIGHT_YES
    }

val Configuration.isRtl
    get() = layoutDirection == View.LAYOUT_DIRECTION_RTL
