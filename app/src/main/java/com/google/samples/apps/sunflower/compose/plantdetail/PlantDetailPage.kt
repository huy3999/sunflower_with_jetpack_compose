/*
 * Copyright 2021 Google LLC
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.samples.apps.jetpack.sunflower.ui.layout.plantdetail

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberImagePainter
import com.google.samples.apps.sunflower.PlantDetailFragment
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.compose.plantdetail.PlantDetailDescription
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel

@Composable
fun PlantDetailPage(
    plantDetailViewModel: PlantDetailViewModel,
    callback: PlantDetailFragment.Callback
) {
    val scrollState = rememberScrollState()
    val plant by plantDetailViewModel.plant.observeAsState()
    val isPlanted by plantDetailViewModel.isPlanted.observeAsState()
    val imageOffset = (-scrollState.value * 0.18f).dp
    Surface {
        Box {
            Image(
                rememberImagePainter(plant?.imageUrl),
                null,
                contentScale = ContentScale.FillWidth,
                modifier = Modifier
                    .graphicsLayer { translationY = imageOffset.value }
                    .height(dimensionResource(R.dimen.plant_detail_app_bar_height))
                    .fillMaxWidth()
            )
            PlantDetailDescription(
                plantDetailViewModel,
                modifier = Modifier.verticalScroll(scrollState)
            )
            if (isPlanted != true) {
                FloatingActionButton(
                    onClick = {
                        callback.add(plant)
                    },
                ) {
                    Icon(
                        painterResource(R.drawable.ic_plus),
                        null
                    )
                }
            }
        }
    }
}
