package com.gm.moderna.model

class MyEarnings {
    var id: String? = null
    var image : String? = null
    var product_name: String? = null
    var earnings: String? = null
    var user_name: String? = null
    var date: String? = null

    constructor(
        id: String?,
        image: String?,
        product_name: String?,
        earnings: String?,
        user_name: String?,
        date: String?


    ) {
        this.id = id
        this.image = image
        this.product_name = product_name
        this.earnings = earnings
        this.user_name = user_name
        this.date = date
    }
}
