package de.nebulit.eventsourcing.common.persistence

import com.thoughtworks.xstream.XStream
import jakarta.persistence.*
import org.hibernate.annotations.JdbcTypeCode
import org.springframework.data.repository.CrudRepository
import java.sql.Types
import java.util.*

@Entity
@Table(name = "events_snapshots")
open class InternalEventSnapshot {
    @GeneratedValue(
        strategy = GenerationType.SEQUENCE,
        generator = "seq"

    )
    @SequenceGenerator(name = "seq", sequenceName = "snapshot_seq", allocationSize = 1)
    @Id
    open var id: Long? = null

    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "aggregate_id")
    open lateinit var aggregateId: UUID


    @Convert(converter = SnapshotXmlPayloadConverter::class)
    @Column(name = "events")
    open lateinit var events: List<InternalEvent>
}

interface EventsSnapshotRepository : CrudRepository<InternalEventSnapshot, Long> {
    fun findByAggregateId(aggregateId: UUID): List<InternalEventSnapshot>

    fun findFirstByAggregateIdOrderByIdDesc(aggregateId: UUID): InternalEventSnapshot?
}

@Converter
class SnapshotXmlPayloadConverter : AttributeConverter<List<InternalEvent>?, String?> {
    override fun convertToDatabaseColumn(record: List<InternalEvent>?): String? {
        val xStream = XStream()
        xStream.allowTypesByRegExp(listOf("de.nebulit.*").toTypedArray())
        return xStream.toXML(record)
    }

    override fun convertToEntityAttribute(record: String?): List<InternalEvent> {
        val xStream = XStream()
        xStream.allowTypesByRegExp(listOf("de.nebulit.*").toTypedArray())
        return xStream.fromXML(record) as List<InternalEvent>
    }

}
