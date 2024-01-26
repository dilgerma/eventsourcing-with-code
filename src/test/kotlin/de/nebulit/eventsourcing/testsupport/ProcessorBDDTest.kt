package de.nebulit.eventsourcing.testsupport

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import mu.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

class ProcessorBddTestBuilder<T : AggregateRoot>(
    val aggregateId: UUID,
    val aggregateService: AggregateService<T>,
    val processor: Processor<*>
) {

    var expectedFailure = false
    var failureOccurred = false
    val logger = KotlinLogging.logger {}
    fun givenInternalEvent(vararg events: InternalEvent): ProcessorBddTestBuilder<T> {
        val aggregate = this.aggregateService.findByAggregateId(aggregateId) ?: Assertions.fail("Aggregate not found")
        aggregate.events.addAll(events.toList())
        aggregateService.persist(aggregate)
        return this
    }

    fun assumeAggregates(vararg aggregate: T): ProcessorBddTestBuilder<T> {
        aggregate.forEach {
            aggregateService.persist(it)
        }
        return this
    }

    fun wheneverProcessorInvoked(
    ): ProcessorBddTestBuilder<T> {
        try {
            this.processor.process()
            return this
        } catch (exception: CommandException) {
            logger.error("command handling failed", exception)
            failureOccurred = true
            if (!expectedFailure) {
                logger.warn { "Did not expect this to fail. Sorry!" }
                throw exception
            } else {
                logger.info { "failure was expected. All good." }
            }
        }
        return this
    }

    fun assumeFailure(): ProcessorBddTestBuilder<T> {
        this.expectedFailure = true
        return this
    }

    fun expectFailure() {
        assertTrue(failureOccurred)
    }

    fun expect(aggregateId: UUID, vararg expectedEvents: SpecificRecord): ProcessorBddTestBuilder<T> {
        assertThat(
            aggregateService.findEventsByAggregateId(aggregateId).map { it.value })
            .containsAll(
                expectedEvents.toList()
            )
        return this
    }

    fun expect(aggregateId: UUID, verifyFn: (events: List<InternalEvent>) -> Unit): ProcessorBddTestBuilder<T> {
        verifyFn(aggregateService.findEventsByAggregateId(aggregateId))
        return this
    }

    fun expectNot(aggregateId: UUID, vararg expectedEvents: SpecificRecord) {
        assertThat(
            aggregateService.findEventsByAggregateId(aggregateId).map { it.value })
            .doesNotContainAnyElementsOf(
                expectedEvents.toList()
            )
    }

    companion object {
        // Extension function to create an instance of FluentInterfaceBuilder
        inline fun <reified T : AggregateRoot> take(
            aggregateId: UUID,
            aggregateService: AggregateService<T>,
            processor: Processor<*>
        ): ProcessorBddTestBuilder<T> {
            return ProcessorBddTestBuilder(aggregateId, aggregateService, processor)
        }
    }
}

