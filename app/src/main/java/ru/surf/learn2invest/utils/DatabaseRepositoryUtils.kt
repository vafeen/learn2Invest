package ru.surf.learn2invest.utils

import androidx.lifecycle.LifecycleCoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.surf.learn2invest.noui.database_components.DatabaseRepository

fun updateProfile(lifecycleCoroutineScope: LifecycleCoroutineScope) =
    lifecycleCoroutineScope.launch(Dispatchers.IO) {
        DatabaseRepository.updateProfile(DatabaseRepository.profile)
    }
