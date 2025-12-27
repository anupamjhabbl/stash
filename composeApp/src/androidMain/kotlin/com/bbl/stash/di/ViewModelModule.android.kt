package com.bbl.stash.di

import com.bbl.stash.auth.viewModels.ForgotPasswordAuthViewModel
import com.bbl.stash.auth.viewModels.OTPAuthViewModel
import com.bbl.stash.auth.viewModels.PasswordResetVieModel
import com.bbl.stash.auth.viewModels.UseRegistrationViewModel
import com.bbl.stash.auth.viewModels.UserLoginAuthViewModel
import com.bbl.stash.presentation.viewmodels.HomeStashScreenViewModel
import com.bbl.stash.presentation.viewmodels.StashDockerViewModel
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