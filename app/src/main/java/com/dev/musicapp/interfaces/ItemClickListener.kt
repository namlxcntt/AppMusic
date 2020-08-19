/*
 * Copyright (c) 2020. Carlos René Ramos López. All rights reserved.
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dev.musicapp.interfaces

import android.view.View

interface ItemClickListener<T> {
    fun onItemClick(view: View, position: Int, item: T)
    fun onPopupMenuClick(view: View, position: Int, item: T, itemList: List<T>)
    fun onShuffleClick(view: View)
    fun onSortClick(view: View)
    fun onPlayAllClick(view: View)
}