package es.sergomz.rabbitchat

import org.springframework.beans.factory.annotation.Value
import org.springframework.http.*
import org.springframework.shell.standard.ShellComponent
import org.springframework.shell.standard.ShellMethod
import org.springframework.shell.table.*
import org.springframework.web.client.RestTemplate
import java.util.Base64

@ShellComponent
class ManagementCommands {

    @Value("\${rabbitmq.management.url}")
    private lateinit var managementUrl: String

    @Value("\${rabbitmq.management.username}")
    private lateinit var username: String

    @Value("\${rabbitmq.management.password}")
    private lateinit var password: String

    // Clase interna para mapear la información de las colas
    data class QueueInfo(
        val name: String,
        val messages: Int,
        val consumers: Int
    )

    // Clase interna para mapear la información de los exchanges
    data class ExchangeInfo(
        val name: String,
        val type: String
    )

    data class BindingInfo(
        val source: String,
        val destination: String,
        val destination_type: String,
        val routing_key: String,
        val properties_key: String
    )

    @ShellMethod("Listar las colas con mensajes pendientes")
    fun listQueues() {
        val restTemplate = RestTemplate()

        // Configuración de headers para autenticación básica
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val auth = "$username:$password"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
        val authHeader = "Basic $encodedAuth"
        headers["Authorization"] = authHeader

        val entity = HttpEntity<String>(headers)

        try {
            // Obtener las colas
            val response = restTemplate.exchange(
                "$managementUrl/queues",
                HttpMethod.GET,
                entity,
                Array<QueueInfo>::class.java
            )
            val queues = response.body

            if (queues != null && queues.isNotEmpty()) {
                // Crear los datos de la tabla
                val headers = arrayOf("Nombre de la Cola", "Mensajes Pendientes", "Consumidores")
                val rows = queues.map { arrayOf(it.name, it.messages.toString(), it.consumers.toString()) }

                // Construir la tabla
                val tableModel = ArrayTableModel(arrayOf(headers) + rows)
                val tableBuilder = TableBuilder(tableModel)

                // Aplicar estilos a la tabla
                tableBuilder.addFullBorder(BorderStyle.oldschool)

                // Mostrar la tabla
                println(tableBuilder.build().render(80))
            } else {
                println("No se encontraron colas.")
            }

        } catch (e: Exception) {
            println("Error al obtener las colas: ${e.message}")
        }
    }

    @ShellMethod("Listar los exchanges")
    fun listExchanges() {
        val restTemplate = RestTemplate()

        // Configuración de headers para autenticación básica
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val auth = "$username:$password"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
        val authHeader = "Basic $encodedAuth"
        headers["Authorization"] = authHeader

        val entity = HttpEntity<String>(headers)

        try {
            // Obtener los exchanges
            val response = restTemplate.exchange(
                "$managementUrl/exchanges",
                HttpMethod.GET,
                entity,
                Array<ExchangeInfo>::class.java
            )
            val exchanges = response.body

            if (exchanges != null && exchanges.isNotEmpty()) {
                // Crear los datos de la tabla
                val headers = arrayOf("Nombre del Exchange", "Tipo")
                val rows = exchanges.map { arrayOf(it.name, it.type) }

                // Construir la tabla
                val tableModel = ArrayTableModel(arrayOf(headers) + rows)
                val tableBuilder = TableBuilder(tableModel)

                // Aplicar estilos a la tabla
                tableBuilder.addFullBorder(BorderStyle.oldschool)

                // Mostrar la tabla
                println(tableBuilder.build().render(80))
            } else {
                println("No se encontraron exchanges.")
            }

        } catch (e: Exception) {
            println("Error al obtener los exchanges: ${e.message}")
        }
    }

    @ShellMethod("Listar los bindings (enlaces) entre exchanges y colas")
    fun listBindings() {
        val restTemplate = RestTemplate()

        // Configuración de headers para autenticación básica
        val headers = HttpHeaders()
        headers.contentType = MediaType.APPLICATION_JSON
        val auth = "$username:$password"
        val encodedAuth = Base64.getEncoder().encodeToString(auth.toByteArray())
        val authHeader = "Basic $encodedAuth"
        headers["Authorization"] = authHeader

        val entity = HttpEntity<String>(headers)

        try {
            // Obtener los bindings
            val response = restTemplate.exchange(
                "$managementUrl/bindings",
                HttpMethod.GET,
                entity,
                Array<BindingInfo>::class.java
            )
            val bindings = response.body

            if (bindings != null && bindings.isNotEmpty()) {
                // Crear los datos de la tabla
                val headers = arrayOf("Exchange Origen", "Destino", "Tipo Destino", "Routing Key")
                val rows = bindings.map { arrayOf(it.source, it.destination, it.destination_type, it.routing_key) }

                // Construir la tabla
                val tableModel = ArrayTableModel(arrayOf(headers) + rows)
                val tableBuilder = TableBuilder(tableModel)

                // Aplicar estilos a la tabla
                tableBuilder.addFullBorder(BorderStyle.oldschool)

                // Mostrar la tabla
                println(tableBuilder.build().render(120))
            } else {
                println("No se encontraron bindings.")
            }

        } catch (e: Exception) {
            println("Error al obtener los bindings: ${e.message}")
        }
    }
}
