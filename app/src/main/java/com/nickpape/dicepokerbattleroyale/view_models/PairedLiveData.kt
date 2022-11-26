package com.nickpape.dicepokerbattleroyale.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData

class PairedLiveData<OutType, InType1, InType2> public constructor(
    liveData1: LiveData<InType1>,
    liveData2: LiveData<InType2>,
    transform: (v1: InType1, v2: InType2) -> OutType
) : MediatorLiveData<OutType>() {
    private var lastValue1: InType1? = null
    private var lastValue2: InType2? = null

    init {
        lastValue1 = liveData1.value
        lastValue2 = liveData2.value

        Log.d(javaClass.simpleName, "LV1: $lastValue1")
        Log.d(javaClass.simpleName, "LV2: $lastValue2")

        if (lastValue1 != null && lastValue2 != null) {
            value = transform(lastValue1!!, lastValue2!!)
        }

        addSource(liveData1) {
            lastValue1 = it
            if (lastValue1 != null && lastValue2 != null) {
                value = transform(lastValue1!!, lastValue2!!)
            }
        }
        addSource(liveData2) {
            lastValue2 = it
            if (lastValue1 != null && lastValue2 != null) {
                value = transform(lastValue1!!, lastValue2!!)
            }
        }
    }
}