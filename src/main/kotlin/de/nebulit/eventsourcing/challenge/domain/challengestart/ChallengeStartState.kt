package de.nebulit.eventsourcing.challenge.domain.challengestart

import de.nebulit.eventsourcing.common.EventState
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.challengestart.ChallengeStarted
import java.util.*

class ChallengeStartState : EventState<ChallengeStartState> {

    var activeItems: MutableList<UUID> = mutableListOf()
    override fun applyEvents(events: List<InternalEvent>): ChallengeStartState {
        events.forEach {
            when (it.value) {
                is ChallengeStarted -> activeItems.add(it.aggregateId)
            }
        }
        return this
    }
}
