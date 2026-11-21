package com.kenai.atlasexpoalert

import java.util.Calendar
import java.util.TimeZone

data class ExpoEvent(
    val day: Int, val hour: Int, val minute: Int,
    val title: String, val place: String, val presenter: String = ""
) {
    fun timeMillis(): Long {
        val c = Calendar.getInstance(TimeZone.getTimeZone("Europe/Madrid"))
        c.set(2026, Calendar.NOVEMBER, day, hour, minute, 0)
        c.set(Calendar.MILLISECOND, 0)
        return c.timeInMillis
    }
}

object EventData {
    val events = listOf(
        // Sábado 21 de noviembre
        ExpoEvent(21,16,20,"Introducción al evento","Escenario principal","supapap, Roland_Mack, einc123 y Chezzer_"),
        ExpoEvent(21,16,45,"Guía de viaje a Disneyland Paris","Escenario Panel","mrbernard"),
        ExpoEvent(21,17,0,"El futuro de CubeCove","Escenario Panel","Gabecol08"),
        ExpoEvent(21,17,30,"From the Stammtisch - Bienvenidos de nuevo","Estudio From the Stammtisch","Roland_Mack"),
        ExpoEvent(21,18,0,"Crear un plataformas con TrainCarts","Bundle Park","Bergerkiller"),
        ExpoEvent(21,19,0,"Taller ccRides","Escenario Workshop","TheClowner"),
        ExpoEvent(21,19,15,"Planificación de una atracción","Bundle Park","Roland_Mack y supapap"),
        ExpoEvent(21,20,0,"Taller de construcción de atracciones","Bundle Park","Roland_Mack y supapap"),
        ExpoEvent(21,21,15,"From the Stammtisch","Estudio From the Stammtisch","Roland_Mack"),
        ExpoEvent(21,21,45,"EPMC - Gestión y organización del equipo","Escenario Panel","Marco0Z"),
        ExpoEvent(21,22,15,"La historia detrás de ser creador de contenido de Minecraft","Escenario Talks","Meneer Uil"),
        ExpoEvent(21,22,30,"Little Britain: De gira","Little Britain","Voidem"),
        ExpoEvent(21,23,0,"MCT - Una noche de musicales","Aquadrome"),
        ExpoEvent(21,23,30,"From the Stammtisch - Mesa nocturna","Estudio From the Stammtisch","Roland_Mack"),
        ExpoEvent(22,0,0,"DJ Moland Rack","Night Club","Moland_Rack"),
        ExpoEvent(22,0,30,"DJ EF","Night Club"),

        // Domingo 22 de noviembre
        ExpoEvent(22,16,0,"From the Stammtisch - Introducción del día 2","Estudio From the Stammtisch","Roland_Mack"),
        ExpoEvent(22,16,15,"Give Kids the World","Escenario principal","einc123"),
        ExpoEvent(22,16,45,"Visita guiada: World Celebration, Germany & USA","Boothhall","Roland_Mack y supapap"),
        ExpoEvent(22,17,30,"MCParks","Escenario Panel","Darren y Ryan"),
        ExpoEvent(22,18,0,"Taller de construcción de atracciones","Bundle Park","Roland_Mack y supapap"),
        ExpoEvent(22,19,15,"Studios of Imagination: Voyage to the Next Chapter","Escenario Panel","Jurrick y Ruud_S"),
        ExpoEvent(22,20,0,"Entrega de premios - Alfombra roja","Escenario principal","Roland_Mack"),
        ExpoEvent(22,20,15,"Ceremonia de premios","Escenario principal","Roland_Mack, supapap y einc123"),
        ExpoEvent(22,22,0,"From the Stammtisch - Charla con los ganadores","Estudio From the Stammtisch","Roland_Mack"),
        ExpoEvent(22,22,15,"Klocky/Imaginears","Escenario Panel","Klocky"),
        ExpoEvent(22,22,30,"Espectáculo nocturno","Escenario principal"),
        ExpoEvent(22,23,0,"From the Stammtisch - Mesa nocturna","Estudio From the Stammtisch","Roland_Mack"),
        ExpoEvent(23,0,0,"DJ Koyas","Night Club"),
        ExpoEvent(23,0,30,"DJ SUPACRAP","Night Club")
    )
}
