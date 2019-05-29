package com.example.longhours

class JobDetails(val id : Int, val name : String, val rate : Int, var latitude : Double, var longitude : Double) {

    override fun toString(): String {
        return name
    }

}
