package com.example.rageamp.utils.enums

enum class MusicAction(val action: Int, val actionName: String) {
    START(0, "START"),
    PAUSE(1, "PAUSE"),
    RESUME(2, "RESUME"),
    NEXT(3, "NEXT"),
    PREVIOUS(4, "PREVIOUS"),
    REWIND_BACK(5, "REWIND_BACK"),
    REWIND_FORWARD(6, "REWIND_FORWARD"),
    REPEAT(7, "REPEAT"),
    SHUFFLE(8, "SHUFFLE"),
}
