package com.tarumt.techswift.User.Datasource

import com.tarumt.techswift.R
import com.tarumt.techswift.Model.Service

class ServiceDataSource {
    fun loadServices() : List<Service>{
        return listOf<Service>(
            Service(0,R.string.air_conditional, R.drawable.airconditional),
            Service(1, R.string.refrigerator, R.drawable.refrigerator),
            Service(2, R.string.washing_machine, R.drawable.washingmachine),
            Service(3, R.string.ceiling_fan, R.drawable.fan),
            Service(4, R.string.electricity, R.drawable.electricity),
            Service(5, R.string.light, R.drawable.light)
        )
    }


}