package de.nebulit.eventsourcing.challenge.slices.agenda.openai

import com.fasterxml.jackson.annotation.JsonProperty
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.http.client.ClientHttpRequestInterceptor
import org.springframework.stereotype.Component
import org.springframework.web.client.RestTemplate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@Configuration
class OpenAIRestTemplateConfig {

    @Value("\${open-ai.token}")
    private lateinit var token: String

    @Bean
    @Primary
    @Qualifier("open-ai-template")
    fun restTemplate(): RestTemplate {
        val restTemplate = RestTemplate()
        restTemplate.interceptors.add(ClientHttpRequestInterceptor { request, body, execution ->
            request.headers.setBearerAuth(token)
            execution.execute(request, body)

        })
        return restTemplate
    }
}

class Message {
    @JsonProperty("role")
    var role: String? = "user"

    @JsonProperty("content")
    var content: String? = null
}


class ChatResponse {
    @JsonProperty("choices")
    val choices: List<Choice>? = null

    // constructors, getters and setters
    class Choice {
        @JsonProperty("index")
        val index = 0

        @JsonProperty("message")
        val message: Message? = null // constructors, getters and setters
    }
}


class ChatRequest(messageList: List<Message>) {
    @JsonProperty("model")
    private val model: String = "gpt-3.5-turbo"

    @JsonProperty("messages")
    private val messages: MutableList<Message> = mutableListOf()

    init {
        messages.addAll(messageList)
    } // getters and setters
}


interface TaskScheduleCalculator {
    fun calculateSchedule(goals: List<String>, durationInMinutes: Int): ScheduleResponse
}

class ScheduleResponse(
        var schedule: List<ScheduleTask>
)

class ScheduleTask(
        var time: String,
        var description: String,
        var points: Int
)

@ConditionalOnProperty("open-ai.mock", havingValue = "true", matchIfMissing = false)
@Component
class DummyOpenAiGoalScheduleCalculator(var openAiConnector: OpenAiConnector) : TaskScheduleCalculator {
    override fun calculateSchedule(goals: List<String>, durationInMinutes: Int): ScheduleResponse {
        return ScheduleResponse(listOf(ScheduleTask(LocalTime.now().plusSeconds(30).format(DateTimeFormatter.ISO_TIME), "task 1", 1),
                ScheduleTask(LocalTime.now().plusMinutes(1).format(DateTimeFormatter.ISO_TIME), "task 2", 5),
                ScheduleTask(LocalTime.now().plusMinutes(2).format(DateTimeFormatter.ISO_TIME), "task 3", 5)))
    }

}


@ConditionalOnProperty("open-ai.mock", havingValue = "false", matchIfMissing = true)
@Component
class OpenAiGoalScheduleCalculator(var openAiConnector: OpenAiConnector) : TaskScheduleCalculator {
    override fun calculateSchedule(goals: List<String>, durationInMinutes: Int): ScheduleResponse {
        return jacksonObjectMapper().readValue(openAiConnector.requestResponse(ChatRequest(listOf(Message().apply {
            this.content = prompt(goals, durationInMinutes)
        }))), ScheduleResponse::class.java)
    }


    private fun prompt(activities: List<String>, durationInMinutes: Int): String {
        return """
        Given I have $durationInMinutes time available from now until 10 PM german time, I have three activities I'd like to accomplish: ${
            activities.joinToString(
                    ","
            )
        } . Please provide a detailed schedule with sub-tasks for each activity, distributing them well across the timeframe, but only from now on forward. 
        Do not schedule more than 4 tasks. Assign points to each sub-task, ensuring the total points for all tasks together sum up to 100. Provide the response in parseable JSON format. Here is a sample JSON structure as an example of the expected output:
    
        Expected JSON:
        {
          "schedule": [
                {"time": "12:45:00", "description": "Task description", "points": 15}
                {"time": "13:35:00", "description": "Task2 description", "points": 5},
                ..
          ]
        }
        Make sure to give distinct descriptions.
        Adjust the tasks, time ranges, and points based on your preferences while ensuring the total points sum up to 100. Please provide the response promptly. SubTask in German please."
    """.trimIndent()
    }
}


@Component
class OpenAiConnector(
        @Value("\${open-ai.url}") var openAI: String,
        @Qualifier("open-ai-template") var restTemplate: RestTemplate
) {

    fun requestResponse(request: ChatRequest): String? {
        var result: ChatResponse? = restTemplate.postForObject(openAI, request, ChatResponse::class.java)
        return result?.choices?.firstOrNull()?.message?.content
    }
}
