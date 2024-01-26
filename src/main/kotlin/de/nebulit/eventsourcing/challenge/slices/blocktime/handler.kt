package de.nebulit.eventsourcing.challenge.slices.blocktime

import de.nebulit.eventsourcing.common.*
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.domain.Challenge
import org.springframework.stereotype.Component

@Component
class BlockTimeCommandHandler(
    private var aggregateService: AggregateService<Challenge>
) : BaseCommandHandler<Challenge>(aggregateService) {

    override fun handle(command: Command): List<InternalEvent> {
        assert(command is BlockTimeCommand)
        val blockTimeCommand = command as BlockTimeCommand
        val aggregate = findAggregate(command.aggregateId)
        aggregate.handle(blockTimeCommand)
        aggregateService.persist(aggregate)
        return aggregate.events
    }

    override fun supports(command: Command): Boolean {
        return command is BlockTimeCommand
    }

}
