package com.example.stash.presentation.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.stash.domain.model.dto.StashItem
import com.example.stash.domain.model.entity.StashItemCategoryStatus
import com.example.stash.koinViewModel
import com.example.stash.presentation.viewmodels.StashDockerViewModel
import org.jetbrains.compose.resources.painterResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.arrow_down
import stash.composeapp.generated.resources.bg_gradient
import stash.composeapp.generated.resources.ic_add
import stash.composeapp.generated.resources.ic_logo
import kotlin.math.round

@Composable
fun StashDockerScreen(
    stashCategoryId: Long
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
            .windowInsetsPadding(WindowInsets.systemBars)
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Text(
                    text = stashScreenState.stashItemList?.stashCategory?.categoryName ?: "Your Category",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        floatingActionButton = {
            Icon(
                painter = painterResource(Res.drawable.ic_add),
                contentDescription = "Add",
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
                val categoryName = stashScreenState.stashItemList?.stashCategory?.categoryName ?: "Item"
                EmptyView(
                    "No $categoryName yet",
                    "Start by adding your first $categoryName to keep track of it.",
                    "Add $categoryName",
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
                                stashItem,
                                {
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
            .padding(16.dp, 8.dp)
            .clickable {
                onItemClick(stashItem)
            }
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                painter = painterResource(Res.drawable.ic_logo),
                modifier = Modifier
                    .size(90.dp),
                contentDescription = "Item image"
            )

            Spacer(Modifier.width(8.dp))

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
                    Box(
                        Modifier.weight(1f)
                    ) {
                        RatingView(stashItem.stashItemRating)
                    }

                    DropDownView(
                        stashItem,
                        stashItem.stashItemCompleted,
                        onCompleteStatusUpdate
                    )
                }
            }
        }
    }
}

@Composable
fun RatingView(stashItemRating: Float) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(Res.drawable.ic_add),
            contentDescription = "Star",
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
    onItemAdd: (Long?, String, Float) -> Unit,
    onDismissRequest: () -> Unit,
    itemName: String = "",
    ratingValue: Float = 0f,
    itemId: Long? = null,
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
                    text = "Add a new Item",
                    modifier = Modifier.wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = itemName,
                    onValueChange = { itemName = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Item Name") },
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column {
                        Text(text = "Rating: ${round(ratingValue * 100) / 10}")

                        Slider(
                            value = ratingValue,
                            onValueChange = { ratingValue = it }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onItemAdd(itemId, itemName, round(ratingValue * 100) / 10 )
                    },
                    modifier = Modifier.width(80.dp).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Add")
                }
            }
        }
    }
}

