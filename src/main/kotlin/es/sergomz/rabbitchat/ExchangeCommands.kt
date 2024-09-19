package es.sergomz.rabbitchat

import org.springframework.amqp.core.*
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class ExchangeCommands(
    private val amqpAdmin: AmqpAdmin,
) {

    @ShellMethod("Crear un exchange directo")
    fun createDirectExchange(exchangeName: String) {
        println("Creando exchange directo: $exchangeName")
        try {
            val exchange = DirectExchange(exchangeName)
            amqpAdmin.declareExchange(exchange)
            println("Exchange '$exchangeName' creado exitosamente.")
        } catch (e: Exception) {
            println("Error al crear el exchange: ${e.message}")
        }
    }

    @ShellMethod("Crear un exchange fanout")
    fun createFanoutExchange(exchangeName: String) {
        println("Creando exchange fanout: $exchangeName")
        try {
            val exchange = FanoutExchange(exchangeName)
            amqpAdmin.declareExchange(exchange)
            println("Exchange '$exchangeName' creado exitosamente.")
        } catch (e: Exception) {
            println("Error al crear el exchange: ${e.message}")
        }
    }

    @ShellMethod("Crear un exchange topic")
    fun createTopicExchange(exchangeName: String) {
        println("Creando exchange topic: $exchangeName")
        try {
            val exchange = TopicExchange(exchangeName)
            amqpAdmin.declareExchange(exchange)
            println("Exchange '$exchangeName' creado exitosamente.")
        } catch (e: Exception) {
            println("Error al crear el exchange: ${e.message}")
        }
    }
}
