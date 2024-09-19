[![Open in GitHub Codespaces](https://github.com/codespaces/badge.svg)](https://github.com/codespaces/new?hide_repo_select=true&ref=main&repo=785624943)
# Introducción a RabbitMQ y Ejercicios Prácticos
Este tutorial está diseñado para ayudarte a estructurar una sesión de una hora sobre **RabbitMQ** , enfocada en la nomenclatura clave y ejercicios prácticos que demuestran los conceptos principales. También incluye una comparación con Azure Service Bus para entender las similitudes y diferencias entre ambas tecnologías. Está orientado a participantes con conocimientos previos en comunicación asíncrona.


## Antes de empezar
### Configuración de RabbitMQ con Docker
Para ejecutar RabbitMQ localmente con la interfaz de administración habilitada, puedes utilizar **Docker** . A continuación, se presentan los comandos necesarios para descargar e iniciar una instancia de RabbitMQ con el plugin de management y exponer los puertos necesarios.**Ejecutar RabbitMQ con Docker**
Abre una terminal y ejecuta el siguiente comando:


```bash
docker run -d --hostname mi-rabbit --name mi-rabbit -p 5672:5672 -p 15672:15672 rabbitmq:3-management
```

Este comando realiza las siguientes acciones:

- `-d`: Ejecuta el contenedor en segundo plano (modo "detached").

- `--hostname mi-rabbit`: Asigna el nombre de host `mi-rabbit` al contenedor.

- `--name mi-rabbit`: Asigna el nombre `mi-rabbit` al contenedor.

- `-p 5672:5672`: Mapea el puerto **AMQP (5672)**  del contenedor al puerto **5672**  de tu máquina local.

- `-p 15672:15672`: Mapea el puerto de la **interfaz de administración (15672)**  del contenedor al puerto **15672**  de tu máquina local.

- `rabbitmq:3-management`: Utiliza la imagen de Docker de RabbitMQ con el plugin de management habilitado.
  **Acceder a la Interfaz de Administración**
  Una vez que el contenedor esté en ejecución, puedes acceder a la interfaz de administración de RabbitMQ desde tu navegador web en:


```
http://localhost:15672/
```

Las credenciales predeterminadas son:

- **Usuario:**  `guest`

- **Contraseña:**  `guest`


---

### Configuración de la Aplicación para Conectarse a RabbitMQ
Si deseas que tu aplicación se conecte al RabbitMQ que acabas de iniciar en local, asegúrate de que tu archivo de configuración (`application.properties` o `application.yml`) contenga las siguientes propiedades:

```properties
spring.rabbitmq.host=localhost
spring.rabbitmq.port=5672
spring.rabbitmq.username=guest
spring.rabbitmq.password=guest
```
Esto le indica a tu aplicación que se conecte al RabbitMQ en **localhost**  utilizando el puerto y las credenciales predeterminadas.**Conexión a una Instancia Remota de RabbitMQ**
En caso de que desees conectarte a una instancia de RabbitMQ que ya esté en funcionamiento en otro servidor, necesitas actualizar las propiedades de conexión en tu archivo de configuración con los valores correspondientes:


```properties
spring.rabbitmq.host=tu_dominio_o_ip
spring.rabbitmq.port=puerto
spring.rabbitmq.username=tu_usuario
spring.rabbitmq.password=tu_contraseña
```

Reemplaza:

- `tu_dominio_o_ip`: La dirección IP o el dominio donde se encuentra RabbitMQ.

- `puerto`: El puerto de conexión (por defecto es `5672`).

- `tu_usuario`: El nombre de usuario proporcionado para acceder a RabbitMQ.

- `tu_contraseña`: La contraseña correspondiente al usuario.
  **Ejemplo:**

```properties
spring.rabbitmq.host=192.168.1.100
spring.rabbitmq.port=5672
spring.rabbitmq.username=admin
spring.rabbitmq.password=admin123
```


---

**Verificación de la Conexión**
Para asegurarte de que tu aplicación puede conectarse correctamente a RabbitMQ:

1. Verifica que la instancia de RabbitMQ esté en ejecución y accesible desde tu máquina.

2. Comprueba que las credenciales y el puerto son correctos.

3. Si hay firewalls o reglas de seguridad, asegúrate de que el puerto **5672**  (y **15672**  si accedes a la interfaz web) estén abiertos.



## Contenido

- [1. Nomenclatura y Conceptos Clave de RabbitMQ](#1-nomenclatura-y-conceptos-clave-de-rabbitmq)

- [2. Plan de Sesión de Una Hora con Ejercicios Prácticos](#2-plan-de-sesión-de-una-hora-con-ejercicios-prácticos)
    - [2.1. Introducción a RabbitMQ (10 minutos)](#21-introducción-a-rabbitmq-10-minutos)

    - [2.2. Ejercicio 1: Envío y Recepción de Mensajes Simples (15 minutos)](#22-ejercicio-1-envío-y-recepción-de-mensajes-simples-15-minutos)

    - [2.3. Ejercicio 2: Trabajo con Exchanges y Claves de Enrutamiento (15 minutos)](#23-ejercicio-2-trabajo-con-exchanges-y-claves-de-enrutamiento-15-minutos)

    - [2.4. Ejercicio 3: Patrón Publicar/Suscribir con Exchanges Fanout (10 minutos)](#24-ejercicio-3-patrón-publicarsuscribir-con-exchanges-fanout-10-minutos)

    - [2.5. Ejercicio 4: Enrutamiento con Exchanges de Tipo Topic (15 minutos)](#25-ejercicio-4-enrutamiento-con-exchanges-de-tipo-topic-15-minutos)

    - [2.6. Conclusión y Preguntas (10 minutos)](#26-conclusión-y-preguntas-10-minutos)

- [3. Consideraciones Adicionales para la Sesión](#3-consideraciones-adicionales-para-la-sesión)

- [4. Recursos Adicionales](#4-recursos-adicionales)

- [5. Consejos para una Sesión Exitosa](#5-consejos-para-una-sesión-exitosa)

- [6. Comparación entre RabbitMQ y Azure Service Bus](#6-comparación-entre-rabbitmq-y-azure-service-bus)

- [7. Resumen de Objetivos de Aprendizaje](#7-resumen-de-objetivos-de-aprendizaje)

---


## 1. Nomenclatura y Conceptos Clave de RabbitMQ

Antes de sumergirnos en los ejercicios prácticos, es importante entender la terminología básica de RabbitMQ:

1. **Producer (Productor):**  Aplicación que envía mensajes.

2. **Consumer (Consumidor):**  Aplicación que recibe mensajes.

3. **Queue (Cola):**  Almacena mensajes que los consumidores pueden recibir.

4. **Exchange (Intercambiador):**  Recibe mensajes de los productores y los dirige a las colas según reglas llamadas *bindings*.

5. **Binding:**  Relación entre un exchange y una cola, define cómo los mensajes se enrutan a las colas.

6. **Routing Key (Clave de Enrutamiento):**  Cadena que el exchange utiliza para determinar cómo enrutar el mensaje.

7. **Tipos de Exchange:**
- **Direct:**  Envía mensajes a las colas cuyas claves de enrutamiento coinciden exactamente.

- **Fanout:**  Envía mensajes a todas las colas vinculadas, ignorando la clave de enrutamiento.

- **Topic:**  Enruta mensajes a una o varias colas basándose en patrones en las claves de enrutamiento.

- **Headers:**  Enruta mensajes basándose en las cabeceras en lugar de la clave de enrutamiento.


---


## 2. Plan de Sesión de Una Hora con Ejercicios Prácticos

### 2.1. Introducción a RabbitMQ (10 minutos)
**Objetivo:**  Proporcionar una visión general de RabbitMQ y su papel en la comunicación asíncrona.
- **Contenido:**
  - Qué es RabbitMQ y sus casos de uso.

  - Arquitectura básica: productores, exchanges, colas y consumidores.

  - Modelo de mensajería AMQP.
    **Nota:**  No se requiere ejercicio práctico en esta sección.

---


### 2.2. Ejercicio 1: Envío y Recepción de Mensajes Simples (15 minutos)
**Objetivo:**  Demostrar cómo enviar y recibir mensajes utilizando una cola directa.
#### Pasos Prácticos:

1. **Crear una Cola:**

```shell
shell:>create-queue simple_queue
```

2. **Enviar un Mensaje:**

```shell
shell:>send-message simple_queue "Mensaje simple de prueba"
```

3. **Suscribirse y Recibir el Mensaje:**

```shell
shell:>subscribe simple_queue
```

Observar en la consola cómo se recibe el mensaje.

#### Discusión:

- Cómo el mensaje fue enviado por el productor y almacenado en la cola hasta que el consumidor lo recibió.

- La naturaleza asíncrona y desacoplada de este modelo.


---


### 2.3. Ejercicio 2: Trabajo con Exchanges y Claves de Enrutamiento (15 minutos)
**Objetivo:**  Mostrar cómo los exchanges y las claves de enrutamiento dirigen mensajes a colas específicas.
#### Pasos Prácticos:

1. **Crear un Exchange Directo:**

```shell
shell:>create-direct-exchange direct_exchange
```

2. **Crear Colas y Vincularlas al Exchange:**
   Crear dos colas:


```shell
shell:>create-queue info_queue
shell:>create-queue error_queue
```

Vincular las colas al exchange con diferentes claves de enrutamiento:


```shell
shell:>bind-queue info_queue direct_exchange info
shell:>bind-queue error_queue direct_exchange error
```

3. **Enviar Mensajes con Diferentes Claves de Enrutamiento:**

```shell
shell:>send-message-to-exchange direct_exchange info "Mensaje de información"
shell:>send-message-to-exchange direct_exchange error "Mensaje de error"
```

4. **Suscribirse a las Colas y Recibir Mensajes:**
   En terminales separadas:


```shell
shell:>subscribe info_queue
```


```shell
shell:>subscribe error_queue
```

Observar cómo cada cola recibe solo los mensajes correspondientes a su clave de enrutamiento.

#### Discusión:

- El papel del exchange directo y las claves de enrutamiento.

- Cómo este patrón permite enrutar mensajes a consumidores específicos.


---


### 2.4. Ejercicio 3: Patrón Publicar/Suscribir con Exchanges Fanout (10 minutos)
**Objetivo:**  Ilustrar cómo distribuir mensajes a múltiples colas utilizando un exchange fanout.
#### Pasos Prácticos:

1. **Crear un Exchange Fanout:**

```shell
shell:>create-fanout-exchange fanout_exchange
```

2. **Crear y Vincular Colas al Exchange:**
   Crear dos colas:


```shell
shell:>create-queue queue1
shell:>create-queue queue2
```

Vincular las colas al exchange:


```shell
shell:>bind-queue-to-fanout queue1 fanout_exchange
shell:>bind-queue-to-fanout queue2 fanout_exchange
```

3. **Enviar un Mensaje al Exchange Fanout:**

```shell
shell:>send-message-to-exchange fanout_exchange "" "Mensaje de difusión"
```

4. **Suscribirse a las Colas y Recibir el Mensaje:**
   En terminales separadas:


```shell
shell:>subscribe queue1
```


```shell
shell:>subscribe queue2
```

Ambas colas deberían recibir el mismo mensaje.

#### Discusión:

- Cómo los exchanges fanout envían mensajes a todas las colas vinculadas.

- Casos de uso como notificaciones o difusión de eventos.


---


### 2.5. Ejercicio 4: Enrutamiento con Exchanges de Tipo Topic (15 minutos)
**Objetivo:**  Demostrar cómo los exchanges de tipo `topic` permiten enrutamiento basado en patrones.
#### Pasos Prácticos:

1. **Crear un Exchange Topic:**

```shell
shell:>create-topic-exchange topic_exchange
```

2. **Crear y Vincular Colas con Patrones de Clave de Enrutamiento:**
   Crear dos colas:


```shell
shell:>create-queue logs_queue
shell:>create-queue errors_queue
```

Vincular las colas al exchange con patrones:


```shell
shell:>bind-queue-to-topic logs_queue topic_exchange "#"
shell:>bind-queue-to-topic errors_queue topic_exchange "*.error"
```

3. **Enviar Mensajes con Diferentes Claves de Enrutamiento:**

```shell
shell:>send-message-to-exchange topic_exchange "system.info" "Mensaje de información del sistema"
shell:>send-message-to-exchange topic_exchange "application.error" "Error en la aplicación"
```

4. **Suscribirse y Observar el Enrutamiento:**
   En terminales separadas:


```shell
shell:>subscribe logs_queue
```


```shell
shell:>subscribe errors_queue
```

Observar cómo los mensajes se enrutan según los patrones de clave de enrutamiento.

#### Discusión:

- Cómo los patrones de clave de enrutamiento permiten un enrutamiento flexible y potente.

- Casos de uso, como sistemas de logging o notificaciones categorizadas.


---


### 2.6. Conclusión y Preguntas (10 minutos)
**Objetivo:**  Resumir los conceptos aprendidos y responder preguntas.
- **Contenido:**
  - Repaso de los tipos de exchanges y su efecto en el enrutamiento.

  - Flexibilidad de RabbitMQ para implementar patrones de mensajería.

  - Sesión de preguntas y respuestas.


---


## 3. Consideraciones Adicionales para la Sesión

- **Preparación Previa:**
  - Asegurar que los participantes tienen acceso al proyecto y RabbitMQ instalado.

  - Proporcionar instrucciones de configuración.

- **Interactividad:**
  - Involucrar a los participantes en la ejecución de comandos.

  - Animar modificaciones en los ejemplos.

- **Recursos Visuales:**
  - Utilizar diagramas para ilustrar el flujo de mensajes.


---


## 4. Recursos Adicionales

- **Documentación Oficial:**
  - [Tutoriales de RabbitMQ]()

- **Código Fuente:**
  - Proporcionar acceso al repositorio del proyecto utilizado.


---


## 5. Consejos para una Sesión Exitosa

- **Gestión del Tiempo:**
  - Ajustar el contenido según el ritmo de la sesión.

- **Flexibilidad:**
  - Estar preparado para profundizar en temas de interés para los participantes.

- **Feedback:**
  - Solicitar opiniones al finalizar la sesión para mejoras futuras.


---


## 6. Comparación entre RabbitMQ y Azure Service Bus

A continuación, se presenta una tabla que compara los términos y conceptos clave entre RabbitMQ y Azure Service Bus, destacando sus similitudes, diferencias y limitaciones.

| Concepto | RabbitMQ | Azure Service Bus | Comentarios |
| --- | --- | --- | --- |
| Producto | RabbitMQ | Azure Service Bus | Ambos son sistemas de mensajería orientados a mensajes. |
| Productor | Producer | Publisher | Funcionalidad equivalente. |
| Consumidor | Consumer | Subscriber | Funcionalidad equivalente. |
| Cola | Queue | Queue | Ambos utilizan colas para almacenar mensajes. |
| Exchange | Exchange | Topic | En Azure, un Topic es similar a un Exchange en RabbitMQ. |
| Binding | Binding | Subscription Rule | Las reglas de suscripción en Azure definen el enrutamiento. |
| Clave de Enrutamiento | Routing Key | Subscription Filter | Los filtros en Azure funcionan como las claves en RabbitMQ. |
| Tipos de Exchange | Direct, Fanout, Topic, Headers | No aplica directamente | Azure utiliza Topics y Subscriptions para enrutamiento. |
| Topic | Tipo de Exchange para patrones | Topic (Entidad de mensajería) | Aunque comparten nombre, funcionan de manera diferente. |
| Patrones de Enrutamiento | Basado en Routing Key y Bindings | Basado en Filtros y Subscriptions | Ambos permiten enrutamiento avanzado. |
| Protocolos Soportados | AMQP, MQTT, STOMP, etc. | AMQP, HTTP, SBMP | RabbitMQ es más flexible en protocolos. |
| Gestión de Mensajes | Ack/Nack manual o automático | Recepción PEEK-LOCK o RECEIVE AND DELETE | Diferencias en cómo se confirman los mensajes. |
| Persistencia | Configurable por mensaje | Persistencia por defecto | Ambos soportan persistencia de mensajes. |
| Transacciones | Soportado | Soportado | Ambos permiten transacciones en el procesamiento de mensajes. |
| Orden de Mensajes | Garantizado en colas individuales | Sessions para orden garantizado | Azure requiere configuraciones adicionales para orden. |
| Escalabilidad | Clustering y Federation | Escalado horizontal automático | Azure ofrece escalabilidad gestionada por la nube. |
| Seguridad | Autenticación y permisos configurables | Integrado con Azure AD y RBAC | Azure proporciona integración con servicios de seguridad de Azure. |
| Limitaciones | Requiere gestión y hosting propio | Dependiente de los límites de Azure | RabbitMQ necesita infraestructura; Azure es PaaS. |
| Costo | Depende de la infraestructura | Modelo de pago por uso | Azure puede ser más costoso para altos volúmenes. |
**Comentarios Adicionales:**
- **Terminología Diferente:**  Aunque algunos términos coinciden, como "Queue" y "Topic", su funcionalidad puede variar entre ambas tecnologías.

- **Enrutamiento:**  RabbitMQ utiliza Exchanges y Bindings para enrutar mensajes, mientras que Azure Service Bus utiliza Topics y Subscriptions con filtros.

- **Protocolos:**  RabbitMQ soporta múltiples protocolos, lo que ofrece mayor flexibilidad en integraciones heterogéneas.

- **Gestión:**  Azure Service Bus es un servicio gestionado en la nube, lo que reduce la carga de administración y mantenimiento.


---


## 7. Resumen de Objetivos de Aprendizaje

Al finalizar la sesión, los participantes deberían ser capaces de:

- Entender la terminología básica de RabbitMQ.

- Crear colas y exchanges de diferentes tipos.

- Enviar y recibir mensajes utilizando estrategias de enrutamiento.

- Aplicar patrones de mensajería en sistemas asíncronos.

- Comprender las similitudes y diferencias entre RabbitMQ y Azure Service Bus.

