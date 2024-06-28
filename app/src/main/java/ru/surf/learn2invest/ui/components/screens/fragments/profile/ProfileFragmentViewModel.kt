package ru.surf.learn2invest.ui.components.screens.fragments.profile

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import ru.surf.learn2invest.noui.database_components.DatabaseRepository
import javax.inject.Inject

@HiltViewModel
class ProfileFragmentViewModel @Inject constructor(var databaseRepository: DatabaseRepository) :
    ViewModel() {


}