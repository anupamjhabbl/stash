package com.bbl.stash.common.auth

import android.app.Activity
import android.content.Context
import androidx.activity.result.ActivityResult
import com.bbl.stash.R
import com.bbl.stash.auth.entity.User
import com.bbl.stash.auth.usecases.AuthPreferencesUseCase
import com.bbl.stash.common.StartActivityForResultEventController
import com.bbl.stash.common.StartActivityForResultEventResult
import com.bbl.stash.common.StartActivityIntentType
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

class GoogleAuthentication(
    val authPreferencesUseCase: AuthPreferencesUseCase
) {
    fun provideGoogleSignInClient(context: Context): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestEmail()
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    fun handleGoogleAuthResult(result: ActivityResult) {
        if (result.resultCode != Activity.RESULT_OK) {
            StartActivityForResultEventController.sendResult(
                StartActivityForResultEventResult(
                    type = StartActivityIntentType.GOOGLE_AUTH,
                    result = false
                )
            )
            return
        }

        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
        try {
            val account = task.getResult(ApiException::class.java)

            val idToken = account.idToken
            val email = account.email
            val name = account.displayName

            if (idToken != null && email != null && name != null) {
                authPreferencesUseCase.saveAccessToken(idToken)
                authPreferencesUseCase.saveLoggedUser(User(id = email, name = name))
                StartActivityForResultEventController.sendResult(
                    StartActivityForResultEventResult(
                        type = StartActivityIntentType.GOOGLE_AUTH,
                        result = true
                    )
                )
                return
            }

        } catch (_: ApiException) {}
        StartActivityForResultEventController.sendResult(
            StartActivityForResultEventResult(
                type = StartActivityIntentType.GOOGLE_AUTH,
                result = false
            )
        )
    }
}

