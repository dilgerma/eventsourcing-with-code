package de.nebulit.eventsourcing.challenge.slices.agenda

import de.nebulit.eventsourcing.common.AggregateService
import de.nebulit.eventsourcing.common.BaseCommandHandler
import de.nebulit.eventsourcing.common.Command
import de.nebulit.eventsourcing.common.CommandException
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.stereotype.Component

@Component
class PlanNewTasksCommandHandler(
        private var aggregateService: AggregateService<Challenge>
) : BaseCommandHandler<Challenge>(aggregateService) {

    override fun handle(command: Command): List<InternalEvent> {
        assert(command is PlanNewTasksCommand)
        val addGoalCommand = command as PlanNewTasksCommand
        val aggregate = findAggregate(command.aggregateId)
        aggregate.handle(addGoalCommand)
        aggregateService.persist(aggregate)
        return aggregate.events
    }

    override fun supports(command: Command): Boolean {
        return command is PlanNewTasksCommand
    }

}

@Component
class StartScheduleCommandHandler(
        private var aggregateService: AggregateService<Challenge>
) : BaseCommandHandler<Challenge>(aggregateService) {

    override fun handle(command: Command): List<InternalEvent> {
        assert(command is StartScheduleCommand)
        val startScheduleCommand = command as StartScheduleCommand

        val challenge = aggregateService.findByAggregateId(startScheduleCommand.aggregateId)?:throw CommandException("Challenge ${startScheduleCommand.aggregateId} not available")
        val events = aggregateService.findEventsByAggregateId(command.aggregateId)
        challenge.applyEvents(events)
        challenge.handle(startScheduleCommand)
        aggregateService.persist(challenge)
        return challenge.events
    }

    override fun supports(command: Command): Boolean {
        return command is StartScheduleCommand
    }

}
