package com.sample.booking.domain.entity

import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "equipment")
class Equipment(
    var name: String?
) : BaseEntity<Long>()
