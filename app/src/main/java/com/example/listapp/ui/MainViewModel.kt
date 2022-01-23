package com.example.listapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.listapp.models.DataResponseItem
import com.example.listapp.repository.MainRepository
import com.example.listapp.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val mainRepository: MainRepository
): ViewModel(){

    private val _res = MutableLiveData<Resource<List<DataResponseItem>>>()

    val res : LiveData<Resource<List<DataResponseItem>>>
        get() = _res

    init {
        getData()
    }

    private fun getData()  = viewModelScope.launch {
        _res.postValue(Resource.loading(null))
        mainRepository.getResponseData().let {
            if (it.isSuccessful){
                it.body()?.let {
                    if (it.isEmpty())
                        _res.postValue(Resource.empty(null))
                    else
                        _res.postValue(Resource.success(it))
                }
            }else{
                _res.postValue(Resource.error(it.errorBody().toString(), null))
            }
        }
    }

}