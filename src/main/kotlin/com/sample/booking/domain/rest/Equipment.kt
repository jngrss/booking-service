package com.sample.booking.domain.rest

data class Equipment(
    val id: Long,
    val name: String,
) {
    companion object {
        fun mapFrom(input: com.sample.booking.domain.entity.Equipment): Equipment {
            return Equipment(
                id = input.getId()!!,
                name = input.name!!,
            )
        }
    }
}
