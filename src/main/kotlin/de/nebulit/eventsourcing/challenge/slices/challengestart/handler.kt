package de.nebulit.eventsourcing.challenge.slices.challengestart

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.stereotype.Component

@Component
class StartChallengeCommandHandler(
    private var aggregateService: AggregateService<Challenge>
) : BaseCommandHandler<Challenge>(aggregateService) {

    override fun handle(command: Command): List<InternalEvent> {
        assert(command is StartChallengeCommand)
        val startChallengeCommand = command as StartChallengeCommand

        val challenge = aggregateService.findByAggregateId(startChallengeCommand.aggregateId)?:Challenge(command.aggregateId)
        val events = aggregateService.findEventsByAggregateId(command.aggregateId)
        challenge.applyEvents(events)
        challenge.start(command)
        aggregateService.persist(challenge)
        return challenge.events
    }

    override fun supports(command: Command): Boolean {
        return command is StartChallengeCommand
    }

}
