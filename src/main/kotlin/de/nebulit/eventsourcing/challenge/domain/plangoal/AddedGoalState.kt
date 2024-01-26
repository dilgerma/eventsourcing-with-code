package de.nebulit.eventsourcing.challenge.domain.plangoal

import de.nebulit.eventsourcing.common.EventState
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.additem.GoalAdded
import java.util.*

class AddedGoalState: EventState<AddedGoalState> {
    var addedGoals: MutableList<UUID> = mutableListOf()

    override fun applyEvents(events: List<InternalEvent>): AddedGoalState {
        events.forEach {
            when (it.value) {
                is GoalAdded -> addedGoals.add((it.value as GoalAdded).itemId)
            }
        }
        return this
    }

}
