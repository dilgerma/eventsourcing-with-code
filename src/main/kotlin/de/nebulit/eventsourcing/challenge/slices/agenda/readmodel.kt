package de.nebulit.eventsourcing.challenge.slices.agenda

import de.nebulit.eventsourcing.common.ReadModel
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.additem.GoalAdded
import de.nebulit.events.blocktime.SchedulePlanned
import de.nebulit.events.schedule.ScheduleStarted
import java.util.*

data class PlannedGoal(val aggregateId: UUID, val itemId: UUID, val description: String)

class ActiveSchedulesToBeProcessed : ReadModel<ActiveSchedulesToBeProcessed> {

    var activeSchedulesToBeProcessed = mutableListOf<UUID>()

    override fun applyEvents(events: List<InternalEvent>): ActiveSchedulesToBeProcessed {
        events.forEach {
            when (it.value) {
                is SchedulePlanned -> activeSchedulesToBeProcessed.add(it.aggregateId)
                is ScheduleStarted -> activeSchedulesToBeProcessed.removeIf { item -> it.aggregateId == item }
            }
        }
        return this
    }

}

class PlannedGoalsToBeProcessedReadModel : ReadModel<PlannedGoalsToBeProcessedReadModel> {

    var goalsToBePlanned = mutableListOf<PlannedGoal>()
    var timeAvailable: Int? = null

    override fun applyEvents(events: List<InternalEvent>): PlannedGoalsToBeProcessedReadModel {
        events.forEach {
            when (it.value) {
                is ScheduleStarted -> {
                    timeAvailable = (it.value as ScheduleStarted).durationInMinutes
                }

                is GoalAdded -> goalsToBePlanned.add(PlannedGoal(it.aggregateId, (it.value as GoalAdded).itemId, (it.value as GoalAdded).description.toString()))
            }
        }

        return this
    }
}

