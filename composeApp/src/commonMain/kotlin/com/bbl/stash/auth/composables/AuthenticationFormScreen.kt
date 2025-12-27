package com.bbl.stash.auth.composables

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults.SecondaryIndicator
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import stash.composeapp.generated.resources.Res
import stash.composeapp.generated.resources.login
import stash.composeapp.generated.resources.sign_up

@Composable
fun AuthenticationFormScreen(
    onForgotPasswordClick: () -> Unit,
    goToHomeScreen: () -> Unit,
    onGoToOtpVerificationScreen: (email: String, origin: String, userId: String) -> Unit,
    startSync: () -> Unit
) {
    val pagerState = rememberPagerState(
        pageCount = { 2 }
    )
    val scope = rememberCoroutineScope()
    val logInPageIndex = 0
    val registerPageIndex = 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        TabRow(
            selectedTabIndex = pagerState.currentPage,
            divider = {},
            containerColor = Color.Transparent,
            indicator = { tabPositions ->
                SecondaryIndicator(
                    Modifier.tabIndicatorOffset(tabPositions[pagerState.currentPage]),
                    height = 2.dp,
                    color = MaterialTheme.colorScheme.secondaryContainer
                )
            }
        ) {
            Tab(
                modifier = Modifier.padding(16.dp, 0.dp),
                selected = pagerState.currentPage == logInPageIndex,
                onClick = {
                    if (pagerState.currentPage != logInPageIndex) {
                        scope.launch {
                            pagerState.scrollToPage(logInPageIndex)
                        }
                    }
                },
                text = {
                    ComposeTextView.TitleTextView(
                        text = stringResource(Res.string.login),
                        textColor = if (pagerState.currentPage == logInPageIndex) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            )

            Tab(
                selected = pagerState.currentPage == registerPageIndex,
                onClick = {
                    if (pagerState.currentPage != registerPageIndex) {
                    scope.launch {
                        pagerState.scrollToPage(registerPageIndex)
                    }
                        }
                },
                text = {
                    ComposeTextView.TitleTextView(
                        text = stringResource(Res.string.sign_up),
                        textColor = if (pagerState.currentPage == registerPageIndex) MaterialTheme.colorScheme.onSecondaryContainer else MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            )
        }

        Spacer(Modifier.height(48.dp))

        HorizontalPager(
            state = pagerState
        ) { index ->
            when (index) {
                logInPageIndex ->  {
                    AuthLoginScreen(
                        onSignUpClick = {
                            scope.launch {
                                pagerState.scrollToPage(registerPageIndex)
                            }
                        },
                        onForgotPasswordClick = onForgotPasswordClick,
                        onAppleLoginClick = {},
                        onGoogleLoginClick = {},
                        goToHomeScreen = goToHomeScreen,
                        startSync = startSync
                    )
                }
                registerPageIndex -> {
                    AuthRegistrationScreen(
                        onLoginClick = {
                            scope.launch {
                                pagerState.scrollToPage(logInPageIndex)
                            }
                        },
                        onAppleLoginClick = {},
                        onGoogleLoginClick = {},
                        onGoToOtpVerificationScreen = onGoToOtpVerificationScreen
                    )
                }
            }
        }
    }
}
