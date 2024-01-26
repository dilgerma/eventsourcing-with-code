package de.nebulit.eventsourcing.testsupport

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import mu.KotlinLogging
import org.apache.avro.specific.SpecificRecord
import org.assertj.core.api.Assertions
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions.assertTrue
import java.util.*

class CommandHandlerBddTestBuilder<T : AggregateRoot>(
    val aggregateId: UUID,
    val aggregateService: AggregateService<T>,
    val commandHandler: CommandHandler
) {

    var expectedFailure = false
    var failureOccurred = false
    var resultEvents = emptyList<InternalEvent>()
    val logger = KotlinLogging.logger {}
    fun givenInternalEvent(vararg events: InternalEvent): CommandHandlerBddTestBuilder<T> {
        if (events.isEmpty()) {
            return this
        }
        val aggregate = this.aggregateService.findByAggregateId(aggregateId) ?: Assertions.fail("Aggregate not found")
        aggregate.events.addAll(events)
        aggregateService.persist(aggregate)
        return this
    }

    fun assumeAggregate(aggregate: T): CommandHandlerBddTestBuilder<T> {
        aggregateService.persist(aggregate)
        return this
    }

    fun <U : Command> whenever(
        command: U
    ): CommandHandlerBddTestBuilder<T> {
        try {
            resultEvents = this.commandHandler.handle(command)
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

    fun assumeFailure(): CommandHandlerBddTestBuilder<T> {
        this.expectedFailure = true
        return this
    }

    fun expectFailure() {
        assertTrue(failureOccurred)
    }

    fun expect(vararg expectedEvents: SpecificRecord) {
        assertThat(
            resultEvents.map { it.value })
            .contains(
                *expectedEvents.toList().toTypedArray()
            )
    }

    companion object {
        // Extension function to create an instance of FluentInterfaceBuilder
        inline fun <reified T : AggregateRoot> take(
            aggregateId: UUID,
            aggregateService: AggregateService<T>,
            commandHandler: CommandHandler
        ): CommandHandlerBddTestBuilder<T> {
            return CommandHandlerBddTestBuilder(aggregateId, aggregateService, commandHandler)
        }
    }
}

