package de.nebulit.eventsourcing.common

import de.nebulit.eventsourcing.common.persistence.EventsEntityRepository
import de.nebulit.eventsourcing.testsupport.BaseIntegrationTest
import de.nebulit.eventsourcing.testsupport.RandomData
import de.nebulit.events.schedule.TaskPlanned
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import java.time.LocalDateTime
import java.util.*

class EventsEntityRepositoryTest : BaseIntegrationTest() {

    @Autowired
    private lateinit var eventsEntityRepository: EventsEntityRepository

    @BeforeEach
    fun beforeEach() {
        eventsEntityRepository.deleteAll()
    }

    @Test
    fun `finds all events between 2 dates`() {

        val payload = RandomData.newInstance<TaskPlanned> { }
        eventsEntityRepository.save(payload.toInternalRecord(payload.aggregateId))

        assertThat(
            eventsEntityRepository.findAllByCreatedBetween(
                LocalDateTime.now().minusDays(1),
                LocalDateTime.now().plusDays(1)
            )
        ).hasSize(1)
    }



    @Test
       fun `finds no events between 2 dates if out of range`() {
           val payload = RandomData.newInstance<TaskPlanned> { }
           eventsEntityRepository.save(payload.toInternalRecord(payload.aggregateId))

           assertThat(
               eventsEntityRepository.findAllByCreatedBetween(
                   LocalDateTime.now().minusDays(3),
                   LocalDateTime.now().minusDays(1)
               )
           ).isEmpty()
       }

    @Test
      fun `finds all events by aggregateId between 2 dates`() {
          val aggregateId1 = UUID.randomUUID()
          val aggregateId2 = UUID.randomUUID()

          val payload = RandomData.newInstance<TaskPlanned> {
              this.aggregateId = aggregateId1
          }
        val payload2 = RandomData.newInstance<TaskPlanned> {
                      this.aggregateId = aggregateId2
                  }
          eventsEntityRepository.save(payload.toInternalRecord(payload.aggregateId))
          eventsEntityRepository.save(payload2.toInternalRecord(payload2.aggregateId))

          assertThat(
              eventsEntityRepository.findByAggregateIdAndCreatedBetween(
                  aggregateId1,
                  LocalDateTime.now().minusDays(1),
                  LocalDateTime.now().plusDays(1)
              )
          ).hasSize(1)
      }
}
