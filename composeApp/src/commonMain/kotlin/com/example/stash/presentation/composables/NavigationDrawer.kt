package com.example.stash.presentation.composables

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.stash.auth.entity.User
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.app_name
import stash.composeapp.generated.resources.ic_account
import stash.composeapp.generated.resources.ic_logo
import stash.composeapp.generated.resources.ic_logout
import stash.composeapp.generated.resources.logout
import stash.composeapp.generated.resources.profile

@Composable
fun ModalDrawerContent(
    loggedUser: User?,
    logOutUser: () -> Unit
) {
    ModalDrawerSheet {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp, 0.dp)
        ) {
            Icon(
                modifier = Modifier
                    .size(30.dp),
                painter = painterResource(Res.drawable.ic_logo),
                contentDescription = stringResource(Res.string.profile),
                tint = Color.Unspecified,
            )

            Text(
                text = stringResource(Res.string.app_name),
                modifier = Modifier.padding(16.dp)
            )
        }

        HorizontalDivider()

        NavigationDrawerItem(
            label = {
                Text(text = loggedUser?.name ?: "")
            },
            icon = {
                Icon(
                    painter = painterResource(Res.drawable.ic_account),
                    contentDescription = stringResource(Res.string.profile),
                    tint = Color.Unspecified
                )
            },
            selected = false,
            onClick = {}
        )
        NavigationDrawerItem(
            label = {
                Text(text = stringResource(Res.string.logout))
            },
            icon = {
                Icon(
                    painterResource(Res.drawable.ic_logout),
                    contentDescription = stringResource(Res.string.logout)
                )
            },
            selected = false,
            onClick = logOutUser
        )
    }
}
