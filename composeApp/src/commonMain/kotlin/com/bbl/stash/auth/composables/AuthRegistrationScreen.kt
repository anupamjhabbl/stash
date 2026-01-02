package com.bbl.stash.auth.composables

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
import com.bbl.stash.auth.entity.PasswordStrengthValidityStatus
import com.bbl.stash.auth.viewModels.UseRegistrationViewModel
import com.bbl.stash.auth.viewModels.UserAuthIntent
import com.bbl.stash.common.Constants
import com.bbl.stash.common.ObserveAsEvents
import com.bbl.stash.common.ObserveAsEventsLatest
import com.bbl.stash.common.Platform
import com.bbl.stash.common.RequestStatus
import com.bbl.stash.common.controllers.SnackbarController
import com.bbl.stash.common.controllers.SnackbarEvent
import com.bbl.stash.common.controllers.StartActivityForResultEvent
import com.bbl.stash.common.controllers.StartActivityForResultEventController
import com.bbl.stash.common.controllers.StartActivityIntentType
import com.bbl.stash.common.getPlatform
import com.bbl.stash.presentation.viewmodels.koinViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.already_have_an_account
import stash.composeapp.generated.resources.auth_or
import stash.composeapp.generated.resources.email
import stash.composeapp.generated.resources.email_hint
import stash.composeapp.generated.resources.generic_error
import stash.composeapp.generated.resources.ic_google
import stash.composeapp.generated.resources.ic_visibility_off
import stash.composeapp.generated.resources.ic_visibility_on
import stash.composeapp.generated.resources.invalid_email_alert
import stash.composeapp.generated.resources.login
import stash.composeapp.generated.resources.password
import stash.composeapp.generated.resources.password_hint
import stash.composeapp.generated.resources.password_not_empty
import stash.composeapp.generated.resources.register_with_google
import stash.composeapp.generated.resources.start_your_jorney
import stash.composeapp.generated.resources.username
import stash.composeapp.generated.resources.username_hint

@Composable
fun AuthRegistrationScreen(
    onLoginClick: () -> Unit,
    goToHomeScreen:  () -> Unit,
    onGoToOtpVerificationScreen: (email: String, origin: String, userId: String) -> Unit
) {
    val scope = rememberCoroutineScope()
    val genericMessage = stringResource(Res.string.generic_error)
    val viewModel: UseRegistrationViewModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var isEmailFocused by remember { mutableStateOf(false) }
    var hasEmailFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var hasPasswordFocused by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()
    var isLoading by remember {
        mutableStateOf(false)
    }

    ObserveAsEventsLatest(viewModel.userRegisterRequestStatus) { authRegistrationRequestStatus ->
        when (authRegistrationRequestStatus) {
            is RequestStatus.Error -> {
                isLoading = false
                if (authRegistrationRequestStatus.message == Constants.DEFAULT_ERROR) {
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
                                message = authRegistrationRequestStatus.message ?: ""
                            )
                        )
                    }
                }
            }

            RequestStatus.Loading -> {
                isLoading = true
            }

            is RequestStatus.Success -> {
                isLoading = false
                onGoToOtpVerificationScreen(
                    state.email,
                    Constants.Origin.REGISTRATION,
                    authRegistrationRequestStatus.data.userId
                )
            }

            RequestStatus.Idle -> { isLoading = false }
        }
    }

    ObserveAsEvents(
        flow = StartActivityForResultEventController.resultChannel
    ) {
        if (
            it.type == StartActivityIntentType.GOOGLE_AUTH
            && it.result is Boolean
            && it.result
        ) {
            goToHomeScreen()
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
                text = stringResource(Res.string.username),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = state.userName,
                onValueChange = {
                    viewModel.processEvent(
                        UserAuthIntent.RegisterAuth.ViewEvent.UpdateUsername(
                            it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth(),
                placeholder = {
                    ComposeTextView.TextView(
                        text = stringResource(Res.string.username_hint),
                        fontSize = 16.sp,
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(8.dp),
                colors = OutlinedTextFieldDefaults.colors().copy(
                    focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                    focusedIndicatorColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            ComposeTextView.TextView(
                text = stringResource(Res.string.email),
                textColor = MaterialTheme.colorScheme.onPrimaryContainer,
                fontSize = 16.sp
            )

            Spacer(Modifier.height(4.dp))

            OutlinedTextField(
                value = state.email,
                onValueChange = {
                    viewModel.processEvent(UserAuthIntent.RegisterAuth.ViewEvent.UpdateEmail(it))
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
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
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
                isError = !state.emailValid && hasEmailFocused && !isEmailFocused,
                supportingText = {
                    if (!state.emailValid && hasEmailFocused && !isEmailFocused) {
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
                    viewModel.processEvent(
                        UserAuthIntent.RegisterAuth.ViewEvent.UpdatePassword(
                            it
                        )
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        if (focusState.isFocused) {
                            hasPasswordFocused = true
                        }
                        isPasswordFocused = focusState.isFocused
                    },
                placeholder = {
                    ComposeTextView.TextView(
                        text = stringResource(Res.string.password_hint),
                        fontSize = 16.sp,
                        textColor =MaterialTheme.colorScheme.onTertiaryContainer
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
                    focusedIndicatorColor = MaterialTheme.colorScheme.onTertiaryContainer,
                    unfocusedTextColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    errorIndicatorColor = MaterialTheme.colorScheme.onErrorContainer
                ),
                isError = state.passwordValid != PasswordStrengthValidityStatus.VALID && !isPasswordFocused && hasPasswordFocused,
                supportingText = {
                    if (state.passwordValid != PasswordStrengthValidityStatus.VALID && !isPasswordFocused && hasPasswordFocused) {
                        ComposeTextView.TextView(
                            text = state.passwordValid?.message ?: stringResource(Res.string.password_not_empty),
                            textColor = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    viewModel.processEvent(UserAuthIntent.RegisterAuth.ViewEvent.RegisterUser)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                shape = RoundedCornerShape(8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),
                enabled = state.passwordValid == PasswordStrengthValidityStatus.VALID && state.emailValid && state.userNameValid
            ) {
                ComposeTextView.TitleTextView(
                    stringResource(Res.string.start_your_jorney),
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 16.sp
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (getPlatform() == Platform.ANDROID || getPlatform() == Platform.APPLE) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Spacer(
                        modifier = Modifier.height(1.dp).weight(1f)
                            .background(MaterialTheme.colorScheme.onTertiaryContainer)
                    )

                    ComposeTextView.TextView(
                        stringResource(Res.string.auth_or),
                        textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        fontSize = 16.sp
                    )

                    Spacer(
                        modifier = Modifier.height(1.dp).weight(1f)
                            .background(MaterialTheme.colorScheme.onTertiaryContainer)
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))

                OutlinedButton(
                    onClick = {
                        scope.launch {
                            StartActivityForResultEventController.sendEvent(
                                StartActivityForResultEvent(
                                    type = StartActivityIntentType.GOOGLE_AUTH,
                                )
                            )
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(
                        1.dp,
                        color = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.ic_google),
                        contentDescription = null,
                        tint = Color.Unspecified,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    ComposeTextView.TextView(
                        text = stringResource(Res.string.register_with_google),
                        fontSize = 16.sp
                    )
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                ComposeTextView.TextView(
                    text = stringResource(Res.string.already_have_an_account),
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )

                Spacer(Modifier.width(2.dp))

                ComposeTextView.TextView(
                    text = stringResource(Res.string.login),
                    textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                    fontSize = 16.sp,
                    modifier = Modifier.clickable { onLoginClick() }
                )
            }
        }
    }
}