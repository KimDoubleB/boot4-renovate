package com.kirndoubleb.labs.metrics

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import org.springframework.stereotype.Component

@Component
class PetClinicMetrics(
    private val meterRegistry: MeterRegistry
) {
    private val petsCreatedCounter: Counter = Counter.builder("petclinic.pets.created")
        .description("Number of pets created")
        .register(meterRegistry)

    private val appointmentsScheduledCounter: Counter = Counter.builder("petclinic.appointments.scheduled")
        .description("Number of appointments scheduled")
        .register(meterRegistry)

    private val paymentAttemptsCounter: Counter = Counter.builder("petclinic.payments.attempts")
        .description("Number of payment attempts")
        .register(meterRegistry)

    private val paymentFailuresCounter: Counter = Counter.builder("petclinic.payments.failures")
        .description("Number of payment failures")
        .register(meterRegistry)

    fun incrementPetsCreated() {
        petsCreatedCounter.increment()
    }

    fun incrementAppointmentsScheduled() {
        appointmentsScheduledCounter.increment()
    }

    fun incrementPaymentAttempts() {
        paymentAttemptsCounter.increment()
    }

    fun incrementPaymentFailures() {
        paymentFailuresCounter.increment()
    }
}
