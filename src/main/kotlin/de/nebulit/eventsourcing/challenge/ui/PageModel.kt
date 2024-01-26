package de.nebulit.eventsourcing.challenge.ui

import de.nebulit.eventsourcing.common.ReadModel
import de.nebulit.eventsourcing.common.persistence.InternalEvent
import de.nebulit.eventsourcing.challenge.debug.DebugEventsReadModel
import de.nebulit.eventsourcing.challenge.slices.achievement.AchievementsReadModel
import de.nebulit.eventsourcing.challenge.slices.addgoal.PlannedGoalsReadModel
import de.nebulit.eventsourcing.challenge.slices.blocktime.BlockedTimeReadModel
import de.nebulit.eventsourcing.challenge.slices.finishtask.ActiveTaskReadModel
import de.nebulit.eventsourcing.challenge.slices.finishtask.FinishedItemsReadModel
import de.nebulit.eventsourcing.challenge.slices.schedule.ActivePlannedTasksReadModel
import de.nebulit.eventsourcing.challenge.slices.schedule.ScheduleStatusReadModel
import java.util.*

class PageModel(var challengeId: UUID): ReadModel<PageModel> {

    var plannedGoals = PlannedGoalsReadModel()
    var blockedTime = BlockedTimeReadModel()
    var scheduledTasks = ActivePlannedTasksReadModel()
    var finishedItems = FinishedItemsReadModel()
    var activeTask = ActiveTaskReadModel()
    var scheduleStatus = ScheduleStatusReadModel()
    var debugEventsModel = DebugEventsReadModel()
    var achievements = AchievementsReadModel()

    override fun applyEvents(events: List<InternalEvent>): PageModel {
        plannedGoals.applyEvents(events)
        activeTask.applyEvents(events)
        blockedTime.applyEvents(events)
        finishedItems.applyEvents(events)
        scheduledTasks.applyEvents(events)
        debugEventsModel.applyEvents(events)
        scheduleStatus.applyEvents(events)
        achievements.applyEvents(events)
        return this
    }
}
