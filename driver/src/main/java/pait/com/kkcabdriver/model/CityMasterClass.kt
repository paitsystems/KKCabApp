package pait.com.kkcabdriver.model

class CityMasterClass{

    var City : String? = null
    var Id : Int? = 0


    constructor(){

    }

    constructor(cityName: String, id:Int){
        this.City = cityName
        this.Id = id
    }
}