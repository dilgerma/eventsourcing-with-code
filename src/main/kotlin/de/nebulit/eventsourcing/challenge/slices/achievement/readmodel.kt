package de.nebulit.eventsourcing.challenge.slices.achievement

import de.nebulit.eventsourcing.common.ReadModel
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.events.finishgoal.TaskFinished
import de.nebulit.events.finishtask.ScheduleFinished
import de.nebulit.events.itemscheduler.TaskDiscarded
import de.nebulit.events.schedule.TaskPlanned
import java.util.*


class AchievementsReadModel : ReadModel<AchievementsReadModel> {

    var achievedPoints: Int = 0
    var overallPoints: Int = 0
    var finished = false
    override fun applyEvents(events: List<InternalEvent>): AchievementsReadModel {
        var pointsPerItem = mutableMapOf<UUID, Int>()
        var achievedPoints: Int = 0
        var overallPoints: Int = 0
        events.forEach {
            when (it.value) {
                is TaskPlanned -> pointsPerItem.put((it.value as TaskPlanned).itemId, (it.value as TaskPlanned).points)
                is TaskFinished -> {
                    achievedPoints += pointsPerItem[(it.value as TaskFinished).itemId] ?: 0
                    overallPoints += pointsPerItem[(it.value as TaskFinished).itemId] ?: 0
                }

                is TaskDiscarded -> {
                    overallPoints += pointsPerItem[(it.value as TaskDiscarded).itemId] ?: 0
                }
                is ScheduleFinished -> {
                    this.achievedPoints = achievedPoints
                    this.overallPoints = overallPoints
                    this.finished = true
                }
            }
        }
        return this
    }

}
