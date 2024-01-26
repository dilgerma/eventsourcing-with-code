package de.nebulit.eventsourcing.challenge.slices.challengestart

import de.nebulit.eventsourcing.common.EventState
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.challengestart.ChallengeStarted
import java.util.*



class ActiveChallengesReadModel : EventState<ActiveChallengesReadModel> {

    var activeChallenges: MutableList<UUID> = mutableListOf()
    override fun applyEvents(events: List<InternalEvent>): ActiveChallengesReadModel {
        events.forEach {
            when (it.value) {
                is ChallengeStarted -> activeChallenges.add(it.aggregateId)
            }
        }
        return this
    }
}