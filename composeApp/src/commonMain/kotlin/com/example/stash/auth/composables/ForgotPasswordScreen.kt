package com.example.stash.auth.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.stash.auth.viewModels.ForgotPasswordAuthViewModel
import com.example.stash.auth.viewModels.UserAuthIntent
import com.example.stash.common.Constants
import com.example.stash.common.ObserveAsEvents
import com.example.stash.common.RequestStatus
import com.example.stash.common.SnackbarController
import com.example.stash.common.SnackbarEvent
import com.example.stash.presentation.viewmodels.koinViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.email
import stash.composeapp.generated.resources.email_hint
import stash.composeapp.generated.resources.forgot_password
import stash.composeapp.generated.resources.forgot_password_subtitle
import stash.composeapp.generated.resources.generic_error
import stash.composeapp.generated.resources.ic_arrow_back
import stash.composeapp.generated.resources.invalid_email_alert
import stash.composeapp.generated.resources.reset_password

@Composable
fun ForgotPasswordScreen(
    onGoToOtpVerificationScreen: (email: String, origin: String, userId: String) -> Unit,
    onGoBack: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val genericMessage = stringResource(Res.string.generic_error)
    val viewModel: ForgotPasswordAuthViewModel =  koinViewModel()
    val state by viewModel.state.collectAsState()
    var isEmailFocused by remember { mutableStateOf(false) }
    var hasEmailFocused by remember { mutableStateOf(false) }
    var isLoading by remember {
        mutableStateOf(false)
    }

    ObserveAsEvents(
        flow = viewModel.userForgetPasswordRequestStatus
    ) { forgotPasswordRequestStatus ->
        when (forgotPasswordRequestStatus) {
            is RequestStatus.Error -> {
                isLoading = false
                if (forgotPasswordRequestStatus.message == Constants.DEFAULT_ERROR) {
                    scope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = genericMessage,
                                duration = SnackbarDuration.Short
                            )
                        )
                    }
                } else {
                    scope.launch {
                        SnackbarController.sendEvent(
                            SnackbarEvent(
                                message = forgotPasswordRequestStatus.message ?: "",
                                duration = SnackbarDuration.Short
                            )
                        )
                    }
                }
            }
            RequestStatus.Idle -> { isLoading = false }

            RequestStatus.Loading -> { isLoading = true }

            is RequestStatus.Success -> {
                isLoading = false
                onGoToOtpVerificationScreen(
                    state.email,
                    Constants.Origin.FORGOT_PASSWORD,
                    forgotPasswordRequestStatus.data.userId
                )
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
                .padding(horizontal = 20.dp, vertical = 32.dp)
        ) {
            Box(
                modifier = Modifier.background(MaterialTheme.colorScheme.secondaryContainer, CircleShape)
                    .size(32.dp)
            ) {
                IconButton(
                    onClick = onGoBack
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_arrow_back),
                        contentDescription = "Back",
                        tint = Color.Unspecified
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            ComposeTextView.TitleTextView(
                text = stringResource(Res.string.forgot_password)
            )

            Spacer(modifier = Modifier.height(8.dp))

            ComposeTextView.TextView(
                text = stringResource(Res.string.forgot_password_subtitle),
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(32.dp))

            ComposeTextView.TextView(
                text = stringResource(Res.string.email),
                fontSize = 16.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    viewModel.processEvent(
                        UserAuthIntent.ForgetPasswordAuth.ViewEvent.UpdateEmail(
                            it
                        )
                    )
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
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 16.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    errorIndicatorColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                isError = !state.isValid && hasEmailFocused && !isEmailFocused,
                supportingText = {
                    if (!state.isValid && hasEmailFocused && !isEmailFocused) {
                        ComposeTextView.TextView(
                            text = stringResource(Res.string.invalid_email_alert),
                            textColor = MaterialTheme.colorScheme.onTertiaryContainer
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.processEvent(UserAuthIntent.ForgetPasswordAuth.ViewEvent.ResetPassword)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(8.dp),
                enabled = state.isValid,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                )
            ) {
                ComposeTextView.TitleTextView(
                    text = stringResource(Res.string.reset_password),
                    textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontSize = 16.sp
                )
            }
        }
    }
}
