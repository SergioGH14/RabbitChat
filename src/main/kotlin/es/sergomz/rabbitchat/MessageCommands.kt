package es.sergomz.rabbitchat

import org.springframework.amqp.rabbit.core.RabbitTemplate
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod

@ShellComponent
class MessageCommands(
    private val rabbitTemplate: RabbitTemplate

) {
    @ShellMethod("Enviar un mensaje a una cola")
    fun sendMessage(queueName: String, message: String) {
        println("Enviando mensaje a la cola: $queueName")
        try {
            rabbitTemplate.convertAndSend(queueName, message)
            println("Mensaje enviado: $message")
        } catch (e: Exception) {
            println("Error al enviar el mensaje: ${e.message}")
        }
    }


    @ShellMethod("Enviar un mensaje a un exchange con una routing key")
    fun sendMessageToExchange(exchangeName: String, routingKey: String?, message: String) {
        println("Enviando mensaje al exchange: $exchangeName con routing key: $routingKey")
        try {
            rabbitTemplate.convertAndSend(exchangeName, routingKey?:"", message)
            println("Mensaje enviado: $message")
        } catch (e: Exception) {
            println("Error al enviar el mensaje: ${e.message}")
        }
    }
}