package ru.surf.learn2invest.ui.main

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import javax.inject.Inject

@HiltViewModel
class MainActivityViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var databaseRepository: DatabaseRepository
}