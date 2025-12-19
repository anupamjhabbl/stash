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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.stash.domain.model.dto.StashCategoryWithItem
import com.example.stash.presentation.viewmodels.HomeStashScreenViewModel
import com.example.stash.presentation.viewmodels.koinViewModel
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.add
import stash.composeapp.generated.resources.app_name
import stash.composeapp.generated.resources.arrow_down
import stash.composeapp.generated.resources.arrow_up
import stash.composeapp.generated.resources.bg_gradient
import stash.composeapp.generated.resources.category_adder_dialog_title
import stash.composeapp.generated.resources.category_name
import stash.composeapp.generated.resources.home_empty_page_action
import stash.composeapp.generated.resources.home_empty_page_description
import stash.composeapp.generated.resources.home_empty_page_title
import stash.composeapp.generated.resources.ic_add
import stash.composeapp.generated.resources.ic_logo

@Composable
fun HomeStashScreen(
    onItemClick: (Long) -> Unit
) {
    val viewModel = koinViewModel<HomeStashScreenViewModel>()
    val stashScreenState by viewModel.stashScreenState.collectAsStateWithLifecycle()
    val painter = painterResource(Res.drawable.bg_gradient)
    var isDialogVisible by remember {
        mutableStateOf(false)
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
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = painterResource(Res.drawable.ic_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(40.dp)
                )

                Spacer(Modifier.width(16.dp))

                Text(
                    text = stringResource(Res.string.app_name),
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
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            if (isDialogVisible) {
                CategoryAdderDialog(
                    onCategoryAdd = { categoryName ->
                        viewModel.addCategoryItem(categoryName)
                        isDialogVisible = false
                    }
                ) {
                    isDialogVisible = false
                }
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
            } else if (stashScreenState.stashCategoryList.isEmpty()) {
                EmptyView(
                    title = stringResource(Res.string.home_empty_page_title),
                    description = stringResource(Res.string.home_empty_page_description),
                    actionText = stringResource(Res.string.home_empty_page_action),
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
                    items(stashScreenState.stashCategoryList) { stashCategory ->
                        Column {
                            Spacer(modifier = Modifier.height(8.dp))

                            StashCategoryItem(stashCategory, onItemClick)

                            Spacer(modifier = Modifier.height(8.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun StashCategoryItem(
    stashCategory: StashCategoryWithItem,
    onItemClick: (Long) -> Unit
) {
    var itemExpanded by remember { mutableStateOf(false) }
    val painterResource = if (itemExpanded) {
        painterResource(Res.drawable.arrow_up)
    } else {
        painterResource(Res.drawable.arrow_down)
    }

    Column(
        Modifier
            .fillMaxWidth()
            .border(1.dp, MaterialTheme.colorScheme.tertiary, shape = MaterialTheme.shapes.medium)
            .background(color = Color.White, shape = MaterialTheme.shapes.medium)
            .clickable {
                stashCategory.stashCategory?.let {
                    onItemClick(it.categoryId)
                }
            }
            .padding(16.dp, 8.dp)

    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stashCategory.stashCategory?.categoryName ?: "",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                painter = painterResource,
                contentDescription = "Expand",
                modifier = Modifier
                    .size(30.dp)
                    .clickable {
                        itemExpanded = !itemExpanded
                    }
            )
        }

        if (itemExpanded) {
            Column {
                Spacer(Modifier.height(12.dp))

                HorizontalDivider(thickness = 2.dp)

                Spacer(Modifier.height(12.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    stashCategory.stashItems.take(4).forEach { item ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.ic_logo),
                                modifier = Modifier
                                    .size(40.dp),
                                contentDescription = "Item image"
                            )

                            Spacer(Modifier.height(4.dp))

                            Text(
                                text = item.stashItemName,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Normal,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryAdderDialog(
    onCategoryAdd: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var textValue by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismissRequest
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            shape = RoundedCornerShape(16.dp),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = stringResource(Res.string.category_adder_dialog_title) ,
                    modifier = Modifier.wrapContentSize(Alignment.Center),
                    textAlign = TextAlign.Center,
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = textValue,
                    onValueChange = { textValue = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(Res.string.category_name)) },
                    shape = RoundedCornerShape(8.dp),
                    maxLines = 1
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                    onClick = {
                        onCategoryAdd(textValue)
                    },
                    modifier = Modifier.width(80.dp).height(40.dp),
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = stringResource(Res.string.add))
                }
            }
        }
    }
}