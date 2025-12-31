package com.bbl.stash.presentation.composables

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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.bbl.stash.common.ObserveAsEventsLatest
import com.bbl.stash.common.SnackbarController
import com.bbl.stash.common.SnackbarEvent
import com.bbl.stash.domain.model.dto.StashCategory
import com.bbl.stash.domain.model.dto.StashCategoryWithItem
import com.bbl.stash.presentation.viewmodels.HomeStashScreenEffect
import com.bbl.stash.presentation.viewmodels.HomeStashScreenViewModel
import com.bbl.stash.presentation.viewmodels.koinViewModel
import kotlinx.coroutines.launch
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
import stash.composeapp.generated.resources.deletion_category_failure_message
import stash.composeapp.generated.resources.home_empty_page_action
import stash.composeapp.generated.resources.home_empty_page_description
import stash.composeapp.generated.resources.home_empty_page_title
import stash.composeapp.generated.resources.ic_add
import stash.composeapp.generated.resources.ic_delete
import stash.composeapp.generated.resources.ic_edit
import stash.composeapp.generated.resources.ic_logo
import stash.composeapp.generated.resources.logout_failure_message

@Composable
fun HomeStashScreen(
    onItemClick: (String) -> Unit,
    stopSync: () -> Unit,
    onLogOut: () -> Unit
) {
    val logOutFailureMessage = stringResource(Res.string.logout_failure_message)
    var selectedCategory = remember<StashCategory?> { null }
    val deletionFailureMessage = stringResource(Res.string.deletion_category_failure_message)
    val viewModel = koinViewModel<HomeStashScreenViewModel>()
    val scope = rememberCoroutineScope()
    val stashScreenState by viewModel.stashScreenState.collectAsStateWithLifecycle()
    val painter = painterResource(Res.drawable.bg_gradient)
    var isDialogVisible by remember {
        mutableStateOf(false)
    }

    ObserveAsEventsLatest(
        flow = viewModel.homeStashScreenEffect
    ) { viewEffect ->
        when (viewEffect) {
            HomeStashScreenEffect.LogOutFailure -> {
                scope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = logOutFailureMessage
                        )
                    )
                }
            }

            HomeStashScreenEffect.LogOutUser -> {
                stopSync()
                onLogOut()
            }

            HomeStashScreenEffect.DeleteFailure -> {
                scope.launch {
                    SnackbarController.sendEvent(
                        SnackbarEvent(
                            message = deletionFailureMessage
                        )
                    )
                }
            }
        }
    }

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerContent(
                viewModel.getLoggedUser()
            ) {
                viewModel.logOutUser()
            }
        }
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Scaffold(
                modifier = Modifier
                    .widthIn(max = 550.dp)
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
                            modifier = Modifier.size(40.dp).clickable {
                                scope.launch {
                                    if (drawerState.isClosed) {
                                        drawerState.open()
                                    } else {
                                        drawerState.close()
                                    }
                                }
                            }
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
                        tint = Color.Unspecified,
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
                            categoryName = selectedCategory?.categoryName ?: "",
                            onCategoryAdd = { categoryName ->
                                viewModel.addCategoryItem(selectedCategory?.categoryId, categoryName)
                                isDialogVisible = false
                                selectedCategory = null
                            }
                        ) {
                            isDialogVisible = false
                            selectedCategory = null
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

                                    StashCategoryItem(
                                        stashCategory,
                                        onItemClick,
                                        { itemId ->
                                            itemId?.let {
                                                viewModel.deleteCategory(it)
                                            }
                                        }
                                    ) {
                                        selectedCategory = stashCategory.stashCategory
                                        isDialogVisible = true
                                    }
                                }
                            }
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
    onItemClick: (String) -> Unit,
    onItemDelete: (String?) -> Unit,
    onCategoryEdit: (String?) -> Unit
) {
    var itemExpanded by remember { mutableStateOf(false) }
    val painterResource = if (itemExpanded) {
        painterResource(Res.drawable.arrow_up)
    } else {
        painterResource(Res.drawable.arrow_down)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
                .border(
                    1.dp,
                    MaterialTheme.colorScheme.tertiary,
                    shape = MaterialTheme.shapes.medium
                )
                .background(color = Color.White, shape = MaterialTheme.shapes.medium)
                .clickable {
                    stashCategory.stashCategory?.let {
                        onItemClick(it.categoryId)
                    }
                }
                .padding(horizontal = 16.dp, vertical = 8.dp)
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
                            .fillMaxWidth()
                    ) {
                        stashCategory.stashItems.take(3).forEach { item ->
                            Column(
                                modifier = Modifier
                                    .padding(8.dp)
                                    .weight(1f),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                AsyncImage(
                                    model = item.stashItemUrl,
                                    modifier = Modifier
                                        .size(70.dp)
                                        .clip(RoundedCornerShape(6.dp)),
                                    contentScale = ContentScale.Fit,
                                    contentDescription = "Item image",
                                    placeholder = painterResource(Res.drawable.ic_logo),
                                    error = painterResource(Res.drawable.ic_logo),
                                    fallback = painterResource(Res.drawable.ic_logo)
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

        if (itemExpanded) {
            Box(
                modifier = Modifier
                    .matchParentSize()
                    .padding(end = 32.dp),
                contentAlignment = Alignment.BottomEnd
            ) {
                Row {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Blue, shape = CircleShape)
                            .clickable {
                                onCategoryEdit(stashCategory.stashCategory?.categoryId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(Res.drawable.ic_edit),
                            contentDescription = "Edit",
                            tint = Color.White
                        )
                    }

                    Spacer(Modifier.width(8.dp))

                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .background(Color.Red, shape = CircleShape)
                            .clickable {
                                onItemDelete(stashCategory.stashCategory?.categoryId)
                            },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            modifier = Modifier.size(16.dp),
                            painter = painterResource(Res.drawable.ic_delete),
                            contentDescription = "Delete",
                            tint = Color.White
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun CategoryAdderDialog(
    categoryName: String,
    onCategoryAdd: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    var textValue by remember { mutableStateOf(categoryName) }

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
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                        disabledContainerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        disabledContentColor = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.7f)
                    ),
                    enabled = textValue.isNotEmpty()
                ) {
                    Text(text = stringResource(Res.string.add))
                }
            }
        }
    }
}