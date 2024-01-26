package de.nebulit.eventsourcing.challenge.slices.addgoal

import de.nebulit.eventsourcing.common.toInternalRecord
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.eventsourcing.testsupport.ReadModelBddTestBuilder
import de.nebulit.events.additem.GoalAdded
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import java.util.*

class ReadModelTest {

    @Nested
    inner class PlannedGoal {

        @Test
        fun `builds a list of planned goals`() {
            val aggregateId: UUID = UUID.randomUUID()
            val goalAdded = RandomData.newInstance<GoalAdded> {
                this.aggregateId = aggregateId
            }

            ReadModelBddTestBuilder.take(PlannedGoalsReadModel())
                .given(goalAdded.toInternalRecord(aggregateId))
                .expect {
                    assertThat(it.plannedGoals).hasSize(1)
                    assertThat(it.plannedGoals).containsExactly(
                        PlannedGoalsReadModel.PlannedGoal(
                            goalAdded.itemId,
                            goalAdded.description.toString()
                        )
                    )
                }
        }
    }

}
