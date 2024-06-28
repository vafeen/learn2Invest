package ru.surf.learn2invest.ui.components.screens.sign_up

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import javax.inject.Inject

@HiltViewModel
class SignUpActivityViewModel @Inject constructor() : ViewModel() {
    @Inject
    lateinit var databaseRepository: DatabaseRepository
    var name: String = ""
    var lastname: String = ""
    val lengthLimit = 24
}