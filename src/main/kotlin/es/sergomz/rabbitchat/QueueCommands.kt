package es.sergomz.rabbitchat

import org.springframework.amqp.core.*
import org.springframework.amqp.rabbit.connection.ConnectionFactory
import org.springframework.amqp.rabbit.listener.SimpleMessageListenerContainer
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.standard.ShellOption

@ShellComponent
class QueueCommands(private val connectionFactory: ConnectionFactory, private val amqpAdmin: AmqpAdmin,
) {

    private var container: SimpleMessageListenerContainer? = null


    @ShellMethod("Crear una nueva cola en RabbitMQ")
    fun createQueue(queueName: String) {
        println("Creando la cola: $queueName")
        try {
            val queue = Queue(queueName)
            amqpAdmin.declareQueue(queue)
            println("Cola '$queueName' creada exitosamente.")
        } catch (e: Exception) {
            println("Error al crear la cola: ${e.message}")
        }
    }

    @ShellMethod("Crear una nueva cola con opciones avanzadas")
    fun createQueueAdvanced(
        queueName: String,
        durable: Boolean,
        exclusive: Boolean,
        autoDelete: Boolean
    ) {
        println("Creando la cola: $queueName")
        try {
            val queue = Queue(queueName, durable, exclusive, autoDelete)
            amqpAdmin.declareQueue(queue)
            println("Cola '$queueName' creada exitosamente con opciones avanzadas.")
        } catch (e: Exception) {
            println("Error al crear la cola: ${e.message}")
        }
    }

    @ShellMethod("Vincular una cola a un exchange con una clave de enrutamiento")
    fun bindQueueToDirect(queueName: String, exchangeName: String, routingKey: String) {
        println("Vinculando cola '$queueName' al exchange '$exchangeName' con clave '$routingKey'")
        try {
            val binding = BindingBuilder.bind(Queue(queueName)).to(DirectExchange(exchangeName)).with(routingKey)
            amqpAdmin.declareBinding(binding)
            println("Binding creado exitosamente.")
        } catch (e: Exception) {
            println("Error al crear el binding: ${e.message}")
        }
    }

    @ShellMethod("Vincular una cola a un exchange fanout")
    fun bindQueueToFanout(queueName: String, exchangeName: String) {
        println("Vinculando cola '$queueName' al exchange fanout '$exchangeName'")
        try {
            val binding = BindingBuilder.bind(Queue(queueName)).to(FanoutExchange(exchangeName))
            amqpAdmin.declareBinding(binding)
            println("Binding creado exitosamente.")
        } catch (e: Exception) {
            println("Error al crear el binding: ${e.message}")
        }
    }

    @ShellMethod("Vincular una cola a un exchange topic con un patrón de clave de enrutamiento")
    fun bindQueueToTopic(
        @ShellOption(help = "Nombre de la cola") queueName: String,
        @ShellOption(help = "Nombre del exchange") exchangeName: String,
        @ShellOption(help = "Patrón de clave de enrutamiento") routingPattern: String
    ) {
        println("Vinculando cola '$queueName' al exchange topic '$exchangeName' con patrón '$routingPattern'")
        try {
            val binding = BindingBuilder.bind(Queue(queueName))
                .to(TopicExchange(exchangeName))
                .with(routingPattern)
            amqpAdmin.declareBinding(binding)
            println("Binding creado exitosamente.")
        } catch (e: Exception) {
            println("Error al crear el binding: ${e.message}")
        }
    }

    @ShellMethod("Suscribirse a una cola existente de RabbitMQ")
    fun subscribe(queueName: String) {
        println("Suscribiéndose a la cola: $queueName")

        // Verifica si ya hay un listener activo y lo detiene
        container?.let {
            it.stop()
            println("Listener anterior detenido.")
        }

        // Crea un nuevo listener para la cola especificada
        container = SimpleMessageListenerContainer().apply {
            this.connectionFactory = this@QueueCommands.connectionFactory
            setQueueNames(queueName)
            setMessageListener { message ->
                val body = String(message.body)
                println("Mensaje recibido: $body")
            }
            start()
        }

        println("Listener iniciado. Esperando mensajes...")
    }

    @ShellMethod("Detener la suscripción actual")
    fun stop() {
        container?.let {
            it.stop()
            println("Suscripción detenida.")
        } ?: println("No hay ninguna suscripción activa.")
    }

}