package com.sample.booking.configuration

import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling

@Configuration
@ConfigurationPropertiesScan("com.sample.booking.configuration")
@EnableScheduling
class BookingConfiguration
