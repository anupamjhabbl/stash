package com.bbl.stash.presentation.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.bbl.stash.domain.model.dto.StashItem
import com.bbl.stash.domain.model.entity.StashItemCategoryStatus
import com.bbl.stash.presentation.viewmodels.StashDockerViewModel
import com.bbl.stash.presentation.viewmodels.koinViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.add
import stash.composeapp.generated.resources.add_new_item
import stash.composeapp.generated.resources.app_name
import stash.composeapp.generated.resources.arrow_down
import stash.composeapp.generated.resources.bg_gradient
import stash.composeapp.generated.resources.ic_add
import stash.composeapp.generated.resources.ic_arrow_back
import stash.composeapp.generated.resources.ic_logo
import stash.composeapp.generated.resources.ic_star
import stash.composeapp.generated.resources.item
import stash.composeapp.generated.resources.item_name
import stash.composeapp.generated.resources.stash_docker_empty_page_action
import stash.composeapp.generated.resources.stash_docker_empty_page_description
import stash.composeapp.generated.resources.stash_docker_empty_page_title
import kotlin.math.roundToInt


@Composable
fun StashDockerScreen(
    stashCategoryId: String,
    onGoBack: () -> Unit
) {
    val stashDockerViewModel = koinViewModel<StashDockerViewModel>()
    var selectedItem by remember { mutableStateOf<StashItem?>(null) }
    val stashScreenState by stashDockerViewModel.stashScreenState.collectAsStateWithLifecycle()
    val painter = painterResource(Res.drawable.bg_gradient)
    var isDialogVisible by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        stashDockerViewModel.init(stashCategoryId)
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .drawBehind {
                with(painter) {
                    draw(
                        size = size,
                        alpha = 1f
                    )
                }
            },
        topBar = {
            Box(
                modifier = Modifier.padding(16.dp)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_arrow_back),
                    contentDescription = "Back",
                    modifier = Modifier
                        .size(32.dp)
                        .clickable { onGoBack() }
                )

                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = stringResource(Res.string.app_name),
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        },
        floatingActionButton = {
            Icon(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = stringResource(Res.string.add),
                tint = Color.Unspecified,
                modifier = Modifier
                    .size(60.dp)
                    .clickable {
                        isDialogVisible = true
                    }
            )
        }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            if (isDialogVisible) {
                ItemAdderDialog(
                    onItemAdd = { itemId, itemName, ratingValue ->
                        stashDockerViewModel.addStashItem(itemId, itemName, "", ratingValue, selectedItem?.stashItemCompleted ?: StashItemCategoryStatus.NOT_STARTED.status)
                        isDialogVisible = false
                        selectedItem = null
                    },
                    onDismissRequest = {
                        isDialogVisible = false
                        selectedItem = null
                    },
                    itemName = selectedItem?.stashItemName ?: "",
                    ratingValue = selectedItem?.stashItemRating ?: 0f,
                    itemId = selectedItem?.stashItemId
                )
            }

            if (stashScreenState.isLoading) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.fillMaxSize()
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(40.dp)
                    )
                }
            } else if (stashScreenState.stashItemList?.stashItems.isNullOrEmpty()) {
                val categoryName = stashScreenState.stashItemList?.stashCategory?.categoryName ?: stringResource(Res.string.item)
                EmptyView(
                    title = stringResource(Res.string.stash_docker_empty_page_title, categoryName),
                    description = stringResource(Res.string.stash_docker_empty_page_description, categoryName),
                    actionText = stringResource(Res.string.stash_docker_empty_page_action, categoryName),
                    onActionClick = {
                        isDialogVisible = true
                    }
                )
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp, 8.dp)
                ) {
                    items(stashScreenState.stashItemList?.stashItems ?: emptyList()) { stashItem ->
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))

                            StashItemView(
                                stashItem = stashItem,
                                onItemClick = {
                                    selectedItem = stashItem
                                    isDialogVisible = true
                                }
                            ) { stashItemCompletedStatus, stashItem ->
                                stashDockerViewModel.addStashItem(stashItem.stashItemId, stashItem.stashItemName, stashItem.stashItemUrl, stashItem.stashItemRating, stashItemCompletedStatus)
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StashItemView(
    stashItem: StashItem,
    onItemClick: (stashItem: StashItem) -> Unit,
    onCompleteStatusUpdate: (String, StashItem) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.tertiary, shape = MaterialTheme.shapes.medium)
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .padding(12.dp, 10.dp)
            .clickable {
                onItemClick(stashItem)
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = stashItem.stashItemUrl,
                modifier = Modifier
                    .size(80.dp)
                    .clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Fit,
                contentDescription = "Item image",
                placeholder = painterResource(Res.drawable.ic_logo),
                error = painterResource(Res.drawable.ic_logo),
                fallback = painterResource(Res.drawable.ic_logo)
            )

            Spacer(Modifier.width(12.dp))

            Column (
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stashItem.stashItemName,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                Spacer(Modifier.height(8.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RatingView(
                        modifier = Modifier.weight(1f),
                        stashItemRating = stashItem.stashItemRating
                    )

                    DropDownView(
                        stashItem = stashItem,
                        stashItemCompleted = stashItem.stashItemCompleted,
                        updateCompletedStatus = onCompleteStatusUpdate
                    )
                }
            }
        }
    }
}

@Composable
fun RatingView(
    modifier: Modifier,
    stashItemRating: Float
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_star),
            contentDescription = "Star",
            tint = Color.Unspecified,
            modifier = Modifier.size(20.dp)
        )

        Spacer(Modifier.width(4.dp))

        Text(
            text = "$stashItemRating/10",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@Composable
fun DropDownView(
    stashItem: StashItem,
    stashItemCompleted: String,
    updateCompletedStatus: (String, StashItem) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val items = StashItemCategoryStatus.entries.toList()

    Box(
        modifier = Modifier
            .clickable {
                expanded = !expanded
            }
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stashItemCompleted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(Modifier.width(4.dp))

            Icon(
                painter = painterResource(Res.drawable.arrow_down),
                contentDescription = "More options",
                modifier = Modifier
                    .size(20.dp)
            )
        }
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text = item.status) },
                    onClick = {
                        updateCompletedStatus(item.status, stashItem)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Composable
fun ItemAdderDialog(
    onItemAdd: (String?, String, Float) -> Unit,
    onDismissRequest: () -> Unit,
    itemName: String = "",
    ratingValue: Float = 0f,
    itemId: String? = null,
) {
    var itemName by remember { mutableStateOf(itemName) }
    var ratingValue by remember { mutableStateOf(ratingValue) }

    Dialog(onDismissRequest = { onDismissRequest() }) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.add_new_item),
                    modifier = Modifier.wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(Res.string.item_name)) },
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = "Rating: $ratingValue")

                        Slider(
                            value = ratingValue,
                            onValueChange = { raw ->
                                ratingValue = (raw * 10).roundToInt() / 10f
                            },
                            valueRange = 0f..10f
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onItemAdd(itemId, itemName, ratingValue)
                    },
                    modifier = Modifier.width(80.dp).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    ),
                    enabled = itemName.isNotEmpty()
                ) {
                    Text(text = stringResource(Res.string.add))
                }
            }
        }
    }
}

