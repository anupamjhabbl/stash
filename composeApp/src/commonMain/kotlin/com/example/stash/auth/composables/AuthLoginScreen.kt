package com.example.stash.auth.composables

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stash.auth.viewModels.UserAuthIntent
import com.example.stash.auth.viewModels.UserLoginAuthViewModel
import com.example.stash.common.Constants
import com.example.stash.common.ObserveAsEventsLatest
import com.example.stash.common.RequestStatus
import com.example.stash.common.SnackbarController
import com.example.stash.common.SnackbarEvent
import com.example.stash.presentation.viewmodels.koinViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.auth_or
import stash.composeapp.generated.resources.continue_text
import stash.composeapp.generated.resources.donot_have_account
import stash.composeapp.generated.resources.email
import stash.composeapp.generated.resources.email_hint
import stash.composeapp.generated.resources.forgot_password
import stash.composeapp.generated.resources.generic_error
import stash.composeapp.generated.resources.ic_apple
import stash.composeapp.generated.resources.ic_google
import stash.composeapp.generated.resources.ic_visibility_off
import stash.composeapp.generated.resources.ic_visibility_on
import stash.composeapp.generated.resources.invalid_email_alert
import stash.composeapp.generated.resources.login_with_apple
import stash.composeapp.generated.resources.login_with_google
import stash.composeapp.generated.resources.password
import stash.composeapp.generated.resources.password_hint
import stash.composeapp.generated.resources.sign_up

@Composable
fun AuthLoginScreen(
    onSignUpClick: () -> Unit,
    onForgotPasswordClick: () -> Unit,
    onAppleLoginClick: () -> Unit,
    onGoogleLoginClick: () -> Unit,
    goToHomeScreen: () -> Unit,
    startSync: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val genericMessage = stringResource(Res.string.generic_error)
    val viewModel: UserLoginAuthViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var isEmailFocused by remember { mutableStateOf(false) }
    var hasEmailFocused by remember { mutableStateOf(false) }
    var isLoading  by remember {
        mutableStateOf(false)
    }

    ObserveAsEventsLatest(
        viewModel.userLoginRequestStatus
    ) { userLoginRequestStatus ->
        when (userLoginRequestStatus) {
            is RequestStatus.Error -> {
                isLoading = false
                if (userLoginRequestStatus.message == Constants.DEFAULT_ERROR) {
                    scope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = genericMessage
                            )
                        )
                    }
                } else {
                    scope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = userLoginRequestStatus.message ?: "",
                                duration = SnackbarDuration.Short
                            )
                        )
                    }
                }
            }

            RequestStatus.Idle -> {  isLoading = false }

            RequestStatus.Loading -> { isLoading = true }

            is RequestStatus.Success -> {
                isLoading = false
                startSync()
                goToHomeScreen()
            }
        }

    }

    if (isLoading) {
        Box(
            Modifier.fillMaxSize()
        ) {
            ComposeViewUtils.Loading(
                modifier = Modifier.fillMaxSize().wrapContentSize()
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp, 0.dp)
                .verticalScroll(scrollState)
        ) {
            ComposeTextView.TextView(
                text = stringResource(Res.string.email),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    viewModel.processEvent(UserAuthIntent.LoginAuth.ViewEvent.UpdateEmail(it))
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            hasEmailFocused = true
                        }
                        isEmailFocused = focusState.isFocused
                    },
                placeholder = {
                    ComposeTextView.TextView(
                        text = stringResource(Res.string.email_hint),
                        fontSize = 16.sp,
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedIndicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    errorIndicatorColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                isError = !isEmailFocused && !state.isValid && hasEmailFocused,
                supportingText = {
                    if (!isEmailFocused && !state.isValid && hasEmailFocused) {
                        ComposeTextView.TextView(
                            text = stringResource(Res.string.invalid_email_alert),
                            textColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComposeTextView.TextView(
                text = stringResource(Res.string.password),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = state.password,
                onValueChange = {
                    viewModel.processEvent(UserAuthIntent.LoginAuth.ViewEvent.UpdatePassword(it))
                },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    ComposeTextView.TextView(
                        text = stringResource(Res.string.password_hint),
                        fontSize = 16.sp,
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(
                        onClick = { passwordVisible = !passwordVisible },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            painter = if (passwordVisible) painterResource(Res.drawable.ic_visibility_on) else painterResource(
                                Res.drawable.ic_visibility_off
                            ),
                            contentDescription = null
                        )
                    }
                },
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )

            Spacer(modifier = Modifier.height(4.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                TextButton(onClick = onForgotPasswordClick) {
                    ComposeTextView.TextView(
                        stringResource(Res.string.forgot_password),
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp
                    )
                }
            }

            Button(
                onClick = {
                    viewModel.processEvent(UserAuthIntent.LoginAuth.ViewEvent.LoginUser)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                enabled = state.isValid
            ) {
                ComposeTextView.TitleTextView(
                    stringResource(Res.string.continue_text),
                    textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.height(1.dp).weight(1f).background(MaterialTheme.colorScheme.onTertiaryContainer))
                ComposeTextView.TextView(
                    stringResource(Res.string.auth_or),
                    textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    fontSize = 16.sp
                )
                Spacer(modifier = Modifier.height(1.dp).weight(1f).background(MaterialTheme.colorScheme.onTertiaryContainer))
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = onGoogleLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onTertiaryContainer)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_google),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(24.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                ComposeTextView.TextView(
                    text = stringResource(Res.string.login_with_google),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onAppleLoginClick,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, color = MaterialTheme.colorScheme.onTertiaryContainer)
            ) {
                Icon(
                    painter = painterResource(Res.drawable.ic_apple),
                    contentDescription = null,
                    tint = Color.Unspecified,
                    modifier = Modifier.size(32.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                ComposeTextView.TextView(
                    text = stringResource(Res.string.login_with_apple),
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ComposeTextView.TextView(
                    text = stringResource(Res.string.donot_have_account),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onSignUpClick() }
                )

                Spacer(Modifier.width(2.dp))

                ComposeTextView.TextView(
                    text = stringResource(Res.string.sign_up),
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onSignUpClick() }
                )
            }
        }
    }
}
