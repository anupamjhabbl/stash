package com.example.stash.di

import com.example.stash.auth.viewModels.ForgotPasswordAuthViewModel
import com.example.stash.auth.viewModels.OTPAuthViewModel
import com.example.stash.auth.viewModels.PasswordResetVieModel
import com.example.stash.auth.viewModels.UseRegistrationViewModel
import com.example.stash.auth.viewModels.UserLoginAuthViewModel
import com.example.stash.presentation.viewmodels.HomeStashScreenViewModel
import com.example.stash.presentation.viewmodels.StashDockerViewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

actual val viewModelModule = module {
    viewModelOf(::HomeStashScreenViewModel)
    viewModelOf(::StashDockerViewModel)
    viewModelOf(::OTPAuthViewModel)
    viewModelOf(::ForgotPasswordAuthViewModel)
    viewModelOf(::PasswordResetVieModel)
    viewModelOf(::UseRegistrationViewModel)
    viewModelOf(::UserLoginAuthViewModel)
}