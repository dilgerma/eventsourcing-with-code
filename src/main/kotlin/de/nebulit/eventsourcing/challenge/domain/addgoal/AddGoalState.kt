package de.nebulit.eventsourcing.challenge.domain.addgoal

import de.nebulit.eventsourcing.common.EventState
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.additem.GoalAdded
import java.util.*

data class Goal(val id: UUID, val description: String)
class AddGoalState : EventState<AddGoalState> {

    var goals = mutableListOf<Goal>()

    override fun applyEvents(events: List<InternalEvent>): AddGoalState {
        events.forEach {
            when (it.value) {
                is GoalAdded -> {
                    (this.goals.add(
                        Goal(
                            (it.value as GoalAdded).itemId,
                            (it.value as GoalAdded).description.toString()
                        )
                    ))
                }
            }
        }
        return this
    }
}
