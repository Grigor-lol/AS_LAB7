package com.example.stepsapp

import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit

object SharedData {

    var selectedSharedDay = ZonedDateTime.now().truncatedTo(ChronoUnit.DAYS)
}