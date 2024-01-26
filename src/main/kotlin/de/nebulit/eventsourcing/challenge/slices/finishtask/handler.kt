package de.nebulit.eventsourcing.challenge.slices.finishtask

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.stereotype.Component

@Component
class FinishGoalCommandHandler(
    private var aggregateService: AggregateService<Challenge>
) : BaseCommandHandler<Challenge>(aggregateService) {

    override fun handle(command: Command): List<InternalEvent> {
        assert(command is FinishTaskCommand)
        val addGoalCommand = command as FinishTaskCommand
        val aggregate = findAggregate(command.aggregateId)
        aggregate.handle(addGoalCommand)
        aggregateService.persist(aggregate)
        return aggregate.events
    }

    override fun supports(command: Command): Boolean {
        return command is FinishTaskCommand
    }

}
