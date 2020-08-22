

package com.dev.musicapp.extensions

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.Observer
import com.dev.musicapp.alias.LiveDataFilter

fun <T> LiveData<T>.observe(owner: LifecycleOwner, onEmission: (T) -> Unit) {
    return observe(owner, Observer {
        if (it != null) {
            onEmission(it)
        }
    })
}

class FilterLiveData<T>(
    source1: LiveData<T>,
    private val filter: LiveDataFilter<T>
) : MediatorLiveData<T>() {

    init {
        super.addSource(source1) {
            if (filter(it)) {
                value = it
            }
        }
    }

    override fun <S : Any?> addSource(
        source: LiveData<S>,
        onChanged: Observer<in S>
    ) {
        throw UnsupportedOperationException()
    }

    override fun <T : Any?> removeSource(toRemote: LiveData<T>) {
        throw UnsupportedOperationException()
    }
}

fun <T> LiveData<T>.filter(filter: LiveDataFilter<T>): MediatorLiveData<T> =
    FilterLiveData(this, filter)
