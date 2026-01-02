package com.bbl.stash.auth.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bbl.stash.auth.entity.PasswordStrengthValidityStatus
import com.bbl.stash.auth.viewModels.PasswordResetVieModel
import com.bbl.stash.auth.viewModels.UserAuthIntent
import com.bbl.stash.common.Constants
import com.bbl.stash.common.ObserveAsEventsLatest
import com.bbl.stash.common.RequestStatus
import com.bbl.stash.common.controllers.SnackbarController
import com.bbl.stash.common.controllers.SnackbarEvent
import com.bbl.stash.presentation.viewmodels.koinViewModel
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.both_password_not_matching_alert
import stash.composeapp.generated.resources.confirm_password
import stash.composeapp.generated.resources.confirm_password_hint
import stash.composeapp.generated.resources.generic_error
import stash.composeapp.generated.resources.ic_arrow_back
import stash.composeapp.generated.resources.ic_visibility_off
import stash.composeapp.generated.resources.ic_visibility_on
import stash.composeapp.generated.resources.new_password_hint
import stash.composeapp.generated.resources.new_password_subtitle
import stash.composeapp.generated.resources.new_password_title
import stash.composeapp.generated.resources.password
import stash.composeapp.generated.resources.password_not_empty
import stash.composeapp.generated.resources.reset_password

@Composable
fun PasswordResetScreen(
    goToHome: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val genericMessage = stringResource(Res.string.generic_error)
    val viewModel: PasswordResetVieModel = koinViewModel()
    val state by viewModel.state.collectAsState()
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var hasPasswordFocused by remember { mutableStateOf(false) }
    var isPasswordFocused by remember { mutableStateOf(false) }
    var hasCPasswordFocused by remember { mutableStateOf(false) }
    var isCPasswordFocused by remember { mutableStateOf(false) }
    var isLoading  by remember { mutableStateOf(false) }

    ObserveAsEventsLatest( viewModel.userResetPasswordRequestStatus) { resetPasswordRequestStatus ->
        when (resetPasswordRequestStatus) {
            is RequestStatus.Error -> {
                isLoading = false
                if (resetPasswordRequestStatus.message == Constants.DEFAULT_ERROR) {
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
                                message = resetPasswordRequestStatus.message ?: "",
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
                goToHome()
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
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(
                modifier = Modifier
                    .widthIn(max = 450.dp)
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 32.dp)
            ) {
                Box(
                    modifier = Modifier.background(
                        MaterialTheme.colorScheme.secondaryContainer,
                        CircleShape
                    )
                        .size(32.dp)
                ) {
                    IconButton(
                        onClick = goToHome
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
                    text = stringResource(Res.string.new_password_title)
                )

                Spacer(modifier = Modifier.height(8.dp))

                ComposeTextView.TextView(
                    text = stringResource(Res.string.new_password_subtitle),
                    fontSize = 14.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                ComposeTextView.TextView(
                    text = stringResource(Res.string.password),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = state.password,
                    onValueChange = {
                        viewModel.processEvent(
                            UserAuthIntent.ResetPasswordAuth.ViewEvent.UpdatePassword(
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
                            text = stringResource(Res.string.new_password_hint),
                            textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 16.sp
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
                        focusedIndicatorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        errorIndicatorColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    isError = state.passwordValid != PasswordStrengthValidityStatus.VALID && !isPasswordFocused && hasPasswordFocused,
                    supportingText = {
                        if (state.passwordValid != PasswordStrengthValidityStatus.VALID && !isPasswordFocused && hasPasswordFocused) {
                            ComposeTextView.TextView(
                                text = state.passwordValid?.message
                                    ?: stringResource(Res.string.password_not_empty),
                                textColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                )

                Spacer(Modifier.height(16.dp))

                ComposeTextView.TextView(
                    text = stringResource(Res.string.confirm_password),
                    fontSize = 16.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = state.confirmPassword,
                    onValueChange = {
                        viewModel.processEvent(
                            UserAuthIntent.ResetPasswordAuth.ViewEvent.UpdateConfirmPassword(
                                it
                            )
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .onFocusChanged { focusState ->
                            if (focusState.isFocused) {
                                hasCPasswordFocused = true
                            }
                            isCPasswordFocused = focusState.isFocused
                        },
                    placeholder = {
                        ComposeTextView.TextView(
                            text = stringResource(Res.string.confirm_password_hint),
                            textColor = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontSize = 16.sp
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(8.dp),
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    trailingIcon = {
                        IconButton(
                            onClick = { confirmPasswordVisible = !confirmPasswordVisible },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                painter = if (confirmPasswordVisible) painterResource(Res.drawable.ic_visibility_on) else painterResource(
                                    Res.drawable.ic_visibility_off
                                ),
                                contentDescription = null
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors().copy(
                        focusedTextColor = MaterialTheme.colorScheme.onPrimaryContainer,
                        focusedIndicatorColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        unfocusedTextColor = MaterialTheme.colorScheme.onTertiaryContainer,
                        errorIndicatorColor = MaterialTheme.colorScheme.onErrorContainer
                    ),
                    isError = !state.confirmPasswordValid && !isCPasswordFocused && hasCPasswordFocused,
                    supportingText = {
                        if (!state.confirmPasswordValid && !isCPasswordFocused && hasCPasswordFocused) {
                            ComposeTextView.TextView(
                                text = stringResource(Res.string.both_password_not_matching_alert),
                                textColor = MaterialTheme.colorScheme.onErrorContainer
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        viewModel.processEvent(UserAuthIntent.ResetPasswordAuth.ViewEvent.ResetPassword)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(8.dp),
                    enabled = state.confirmPasswordValid && state.passwordValid == PasswordStrengthValidityStatus.VALID,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.tertiaryContainer,
                        disabledContentColor = MaterialTheme.colorScheme.onTertiaryContainer
                    )
                ) {
                    ComposeTextView.TitleTextView(
                        text = stringResource(Res.string.reset_password),
                        textColor = MaterialTheme.colorScheme.onSecondaryContainer,
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}