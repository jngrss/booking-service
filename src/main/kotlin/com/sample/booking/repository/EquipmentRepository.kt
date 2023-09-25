package com.sample.booking.repository

import com.sample.booking.domain.entity.Equipment
import org.springframework.data.jpa.repository.JpaRepository

interface EquipmentRepository : JpaRepository<Equipment, Long>
