package com.example.stepsapp.viewmodel

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.stepsapp.BaseApplication

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        // Initializer for ItemEditViewModel
        initializer {
            MainScreenViewModel(stepsApplication().healthConnectManager)
        }
        initializer {
            NewRecordViewModel(stepsApplication().healthConnectManager)
        }
    }
}

fun CreationExtras.stepsApplication():BaseApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as BaseApplication)
