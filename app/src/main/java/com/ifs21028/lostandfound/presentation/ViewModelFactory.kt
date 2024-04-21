package com.ifs21028.lostandfound.presentation

import com.ifs21028.lostandfound.di.Injection
import com.ifs21028.lostandfound.presentation.login.LoginViewModel
import com.ifs21028.lostandfound.presentation.main.MainViewModel
import com.ifs21028.lostandfound.presentation.profile.ProfileViewModel
import com.ifs21028.lostandfound.presentation.register.RegisterViewModel
import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.ifs21028.lostandfound.data.repository.AuthRepository
import com.ifs21028.lostandfound.data.repository.LafRepository
import com.ifs21028.lostandfound.data.repository.LocalLafRepository
import com.ifs21028.lostandfound.data.repository.UserRepository
import com.ifs21028.lostandfound.presentation.laf.LafViewModel

class ViewModelFactory(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val lafRepository: LafRepository,
    private val localLafRepository: LocalLafRepository,
) : ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(RegisterViewModel::class.java) -> {
                RegisterViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel
                    .getInstance(authRepository) as T
            }
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel
                    .getInstance(authRepository, lafRepository) as T
            }
            modelClass.isAssignableFrom(ProfileViewModel::class.java) -> {
                ProfileViewModel
                    .getInstance(authRepository, userRepository) as T
            }
            modelClass.isAssignableFrom(LafViewModel::class.java) -> {
                LafViewModel
                    .getInstance(lafRepository, localLafRepository) as T
            }
            else -> throw IllegalArgumentException(
                "Unknown ViewModel class: " + modelClass.name
            )
        }
    }
    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            synchronized(ViewModelFactory::class.java) {
                INSTANCE = ViewModelFactory(
                    Injection.provideAuthRepository(context),
                    Injection.provideUserRepository(context),
                    Injection.provideTodoRepository(context),
                    Injection.provideLocalTodoRepository(context),
                )
            }
            return INSTANCE as ViewModelFactory
        }
    }
}
