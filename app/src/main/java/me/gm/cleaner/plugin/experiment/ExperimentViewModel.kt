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

package me.gm.cleaner.plugin.experiment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import me.gm.cleaner.plugin.data.unsplash.UnsplashPhoto
import me.gm.cleaner.plugin.data.unsplash.UnsplashRepository
import javax.inject.Inject

@HiltViewModel
class ExperimentViewModel @Inject constructor(private val repository: UnsplashRepository) :
    ViewModel() {
    private val _unsplashPhotosFlow: MutableStateFlow<Result<List<UnsplashPhoto>>> =
        MutableStateFlow(Result.failure(IllegalStateException()))
    val unsplashPhotosFlow = _unsplashPhotosFlow.asLiveData()

    fun loadPhotos() {
        viewModelScope.launch(Dispatchers.IO) {
            _unsplashPhotosFlow.emit(repository.fetchUnsplashPhotoList())
        }
    }
}