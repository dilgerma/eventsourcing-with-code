package de.nebulit.eventsourcing.challenge.slices.addgoal

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.stereotype.Component

@Component
class AddGoalCommandHandler(
    private var aggregateService: AggregateService<Challenge>
) : BaseCommandHandler<Challenge>(aggregateService) {

    override fun handle(command: Command): List<InternalEvent> {
        assert(command is AddGoalCommand)
        val addGoalCommand = command as AddGoalCommand
        val aggregate = findAggregate(command.aggregateId)
        aggregate.handle(addGoalCommand)
        aggregateService.persist(aggregate)
        return aggregate.events
    }

    override fun supports(command: Command): Boolean {
        return command is AddGoalCommand
    }

}
