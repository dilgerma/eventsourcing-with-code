package de.nebulit.eventsourcing.challenge.slices.schedule

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.stereotype.Component


@Component
class ScheduleNextTaskCommandHandler(
        private var aggregateService: AggregateService<Challenge>
) : BaseCommandHandler<Challenge>(aggregateService) {

    override fun handle(command: Command): List<InternalEvent> {
        assert(command is ScheduleNextTaskCommand)
        val scheduleNextTaskCommand = command as ScheduleNextTaskCommand
        val aggregate = findAggregate(command.aggregateId)
        aggregate.handle(scheduleNextTaskCommand)
        aggregateService.persist(aggregate)
        return aggregate.events
    }

    override fun supports(command: Command): Boolean {
        return command is ScheduleNextTaskCommand
    }

}