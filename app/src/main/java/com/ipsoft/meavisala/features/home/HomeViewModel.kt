package com.ipsoft.meavisala.features.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.ipsoft.meavisala.core.utils.PermissionInfo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel(), PermissionInfo.OnPermissionListener {

    private val _hasPermissions = mutableStateOf(false)
    val hasPermissions: State<Boolean> = _hasPermissions


    init {
        PermissionInfo.addListener(this)
    }

    override fun onCleared() {
        super.onCleared()
        PermissionInfo.removeListener(this)
    }

    override fun onPermissionUpdated(hasPermissions: Boolean) {
        _hasPermissions.value = hasPermissions
    }
}