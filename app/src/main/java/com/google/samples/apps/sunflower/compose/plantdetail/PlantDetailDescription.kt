/*
 * Copyright 2020 Google LLC
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

package com.google.samples.apps.sunflower.compose.plantdetail

import android.text.method.LinkMovementMethod
import android.widget.TextView
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.text.HtmlCompat
import com.google.android.material.composethemeadapter.MdcTheme
import com.google.samples.apps.sunflower.R
import com.google.samples.apps.sunflower.data.Plant
import com.google.samples.apps.sunflower.viewmodels.PlantDetailViewModel

@Composable
fun PlantDetailDescription(
    plantDetailViewModel: PlantDetailViewModel,
    navClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    // Observes values coming from the VM's LiveData<Plant> field as State<Plant?>
    val plant by plantDetailViewModel.plant.observeAsState()

    // New emissions from plant will make PlantDetailDescription recompose as the state's read here
    plant?.let {
        // If plant is not null, display the content
        PlantDetailContent(it, !plantDetailViewModel.hasValidUnsplashKey(), navClick, modifier)
    }
}

@Composable
fun PlantDetailContent(
    plant: Plant,
    navIsGone: Boolean,
    navClick: () -> Unit,
    modifier: Modifier = Modifier
) {
//    Surface {
    ConstraintLayout(modifier.padding(dimensionResource(R.dimen.margin_normal))) {
        val (name, water, desc, nav) = createRefs()
        Text(
            text = plant.name,
            style = MaterialTheme.typography.h5,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = dimensionResource(R.dimen.margin_small))
                .wrapContentWidth(align = Alignment.CenterHorizontally)
                .constrainAs(name) {}
        )
        PlantWatering(plant.wateringInterval, Modifier
            .fillMaxWidth()
            .constrainAs(water) {
                top.linkTo(name.bottom)
            })
        PlantDescription(plant.description, Modifier.constrainAs(desc) {
            top.linkTo(water.bottom)
        })
        if (!navIsGone) {
            Image(
                painter = painterResource(R.drawable.ic_photo_library),
                contentDescription = stringResource(R.string.gallery_content_description),
                modifier = Modifier
                    .absolutePadding(
                        top = dimensionResource(R.dimen.margin_normal),
                        right = dimensionResource(R.dimen.margin_small)
                    )
                    .clickable { navClick() }
                    .constrainAs(nav) {
                        top.linkTo(name.bottom)
                        end.linkTo(parent.end)
                    }
            )
        }
    }
//    }
}

@Composable
private fun PlantName(name: String) {
    Text(
        text = name,
        style = MaterialTheme.typography.h5,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .wrapContentWidth(align = Alignment.CenterHorizontally)
    )
}

@Composable
private fun PlantWatering(wateringInterval: Int, modifier: Modifier) {
    Column(modifier) {
        // Same modifier used by both Texts
        val centerWithPaddingModifier = Modifier
            .padding(horizontal = dimensionResource(R.dimen.margin_small))
            .align(Alignment.CenterHorizontally)

        val normalPadding = dimensionResource(R.dimen.margin_normal)

        Text(
            text = stringResource(R.string.watering_needs_prefix),
            color = MaterialTheme.colors.primaryVariant,
            fontWeight = FontWeight.Bold,
            modifier = centerWithPaddingModifier.padding(top = normalPadding)
        )

        val wateringIntervalText = LocalContext.current.resources.getQuantityString(
            R.plurals.watering_needs_suffix, wateringInterval, wateringInterval
        )
        Text(
            text = wateringIntervalText,
            modifier = centerWithPaddingModifier.padding(bottom = normalPadding)
        )
    }
}

@Composable
private fun PlantDescription(description: String, modifier: Modifier) {
    // Remembers the HTML formatted description. Re-executes on a new description
    val htmlDescription = remember(description) {
        HtmlCompat.fromHtml(description, HtmlCompat.FROM_HTML_MODE_COMPACT)
    }

    // Displays the TextView on the screen and updates with the HTML description when inflated
    // Updates to htmlDescription will make AndroidView recompose and update the text
    AndroidView(
        factory = { context ->
            TextView(context).apply {
                movementMethod = LinkMovementMethod.getInstance()
            }
        },
        update = {
            it.text = htmlDescription
        },
        modifier = modifier
    )
}

@Preview
@Composable
private fun PlantNamePreview() {
    MdcTheme {
        PlantName("Apple")
    }
}

@Preview
@Composable
private fun PlantWateringPreview() {
    MdcTheme {
        PlantWatering(7, Modifier.padding())
    }
}

@Preview
@Composable
private fun PlantDescriptionPreview() {
    MdcTheme {
        PlantDescription("HTML<br><br>description", Modifier.padding())
    }
}
