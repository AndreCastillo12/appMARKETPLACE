package com.redsystemstudio.appcomprayventa.Modelo

class CarritoItem {
    var id : String = ""
    var titulo : String = ""
    var precio : Double = 0.0
    var cantidad : Int = 0

    constructor()
    constructor(
        id: String,
        titulo: String,
        precio: Double,
        cantidad: Int
    ) {
        this.id = id
        this.titulo = titulo
        this.precio = precio
        this.cantidad = cantidad
    }
}
