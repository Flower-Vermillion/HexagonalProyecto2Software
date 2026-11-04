# sw2Hexagonal — Gestión de Drones

Aplicación de escritorio en **JavaFX** para administrar drones (crear, consultar, modificar y eliminar), construida con **arquitectura hexagonal** (puertos y adaptadores) y persistencia en **MySQL** mediante JDBC.

Proyecto desarrollado para la asignatura de Software 2 — Politécnico Grancolombiano.

## Características

- CRUD completo de drones desde un formulario con tabla.
- Tipos de dron: `AGRICULTURA` y `VIGILANCIA`.
- Al seleccionar una fila de la tabla, el formulario se llena automáticamente.
- Validaciones de negocio en los servicios: id, serial, modelo y fabricante obligatorios; peso mayor que cero; no se permiten ids duplicados; no se modifica ni elimina un dron inexistente.
- Confirmación antes de eliminar.
- El núcleo de la aplicación no depende de la interfaz ni de la base de datos.

## Tecnologías

| Componente | Versión |
|---|---|
| Java | 17 o superior |
| JavaFX (controls, fxml) | 21.0.5 |
| Maven | 3.8+ |
| MySQL Connector/J | 9.1.0 |
| dotenv-java | 3.0.2 |
| JUnit Jupiter | 5.11.3 |

## Arquitectura

Las dependencias siempre apuntan hacia el dominio: la interfaz y la base de datos conocen al núcleo, pero el núcleo no conoce a ninguna de las dos.

```
Adaptador de entrada  →  Puertos de entrada  →  Servicios de aplicación  →  Puerto de salida  ←  Adaptador de salida
 ControlFormDrone        *CasosDeUso             *DroneServicio             DroneRepository      MySqlDroneRepository
```

```
src/main/java/co/edu/poli/sw2Hexagonal/
├── dominio/
│   └── modelo/                 # Drone (abstracta), Agricultura, Vigilancia, Sensor, Piloto, Mision
├── aplicacion/
│   ├── puerto/
│   │   ├── entrada/            # CrearDrone, LeerUnoDrone, LeerTodosDrone, ModificarDrone, EliminarDrone (CasosDeUso)
│   │   └── salida/             # DroneRepository (contrato de persistencia)
│   └── servicio/               # Implementación de los casos de uso y sus validaciones
├── infraestructura/
│   ├── persistencia/           # MySqlDroneRepository, ConexionBD (adaptador de salida)
│   └── ui/                     # ControlFormDrone (adaptador de entrada)
└── vista/
    └── App.java                # Punto de entrada y composición manual de dependencias

src/main/resources/co/edu/poli/sw2Hexagonal/vista/
└── formDrone.fxml              # Vista del formulario (enlazada a ControlFormDrone)
```

`App` es el único lugar que conoce todas las capas: crea el repositorio, lo inyecta en los servicios y entrega los casos de uso al controlador de la interfaz.

### Modelo de dominio

| Clase | Descripción | Atributos propios |
|---|---|---|
| `Drone` (abstracta) | Base de todos los drones | `id`, `serial`, `modelo`, `fabricante`, `peso` |
| `Agricultura` | Dron agrícola | `capacidadTanque` |
| `Vigilancia` | Dron de vigilancia | `deteccionTermica` |
| `Sensor` | Sensor | `id`, `tipo`, `fabricante` |
| `Piloto` | Piloto | `id`, `nombre`, `experiencia`, `telefono` |
| `Mision` | Misión asociada a un dron | `id`, `nombre`, `ubicacion`, `fecha`, `drone` |

> Por ahora la aplicación gestiona únicamente drones. `Sensor`, `Piloto` y `Mision` están modelados en el dominio, pero aún no tienen casos de uso ni persistencia.

## Requisitos previos

1. **JDK 17+** instalado y configurado.
2. **Maven** (o el Maven integrado de Eclipse/IntelliJ).
3. **MySQL** en ejecución.

## Configuración

### 1. Clonar el repositorio

```bash
git clone https://github.com/Flower-Vermillion/HexagonalProyecto2Software.git
cd HexagonalProyecto2Software
```

### 2. Base de datos

Por defecto la aplicación se conecta a una base de datos llamada `drones`. Crea la base de datos y la tabla `drone`:

```sql
CREATE DATABASE IF NOT EXISTS drones;
USE drones;

CREATE TABLE IF NOT EXISTS drone (
    id          VARCHAR(50)  PRIMARY KEY,
    serial      VARCHAR(100) NOT NULL,
    modelo      VARCHAR(100) NOT NULL,
    fabricante  VARCHAR(100) NOT NULL,
    peso        DOUBLE       NOT NULL,
    tipo        VARCHAR(20)  NOT NULL   -- AGRICULTURA o VIGILANCIA
);
```

> Los tipos de columna del script son una propuesta: ajústalos si tu tabla real es diferente.

### 3. Variables de entorno

La conexión se configura con un archivo `.env` en la raíz del proyecto. Si el archivo no existe o falta alguna variable, se usan los valores por defecto:

| Variable | Valor por defecto |
|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/drones?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true` |
| `DB_USER` | `root` |
| `DB_PASSWORD` | *(vacío)* |

Ejemplo de `.env`:

```env
DB_URL=jdbc:mysql://localhost:3306/drones?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
DB_USER=root
DB_PASSWORD=tu_contraseña
```

> El archivo `.env` contiene credenciales: **no lo subas a GitHub**. Agrégalo a `.gitignore`.

## Ejecución

### Con Maven (recomendado)

```bash
mvn clean javafx:run
```

### Desde Eclipse

JavaFX llega por classpath, por lo que ejecutar `App` directamente produce el error *"faltan los componentes de JavaFX runtime"*. Usa alguna de estas opciones:

- **Run As → Maven build...** con el goal `javafx:run`, o
- Ejecuta una clase lanzadora que no extienda `Application`:

```java
package co.edu.poli.sw2Hexagonal.vista;

public class Launcher {
    public static void main(String[] args) {
        App.main(args);
    }
}
```

## Documentación (JavaDoc)

El código está documentado con JavaDoc. Para generar el sitio HTML:

```bash
mvn javadoc:javadoc
```

La documentación queda en `target/site/apidocs/index.html`.

Desde Eclipse también puedes generarla con **Project → Generate Javadoc...**, eligiendo el proyecto y una carpeta de destino.

## Pruebas

```bash
mvn test
```

## Uso

La ventana "Gestión de Drones" tiene un formulario (Id, Serial, Modelo, Fabricante, Peso y Tipo), una fila de botones y una tabla con los drones registrados.

| Botón | Acción |
|---|---|
| **Crear** | Registra un dron con los datos del formulario y actualiza la tabla. |
| **Buscar por id** | Carga en el formulario el dron cuyo id está en el campo *Id*. |
| **Modificar** | Actualiza el dron que corresponde al id del formulario. |
| **Eliminar** | Elimina el dron del id indicado, previa confirmación. |
| **Limpiar** | Vacía el formulario y la selección de la tabla. |

Al hacer clic en una fila de la tabla, sus datos se cargan en el formulario para poder modificarlos o eliminarlos. El peso se escribe como número (se acepta coma o punto decimal) y el tipo se elige entre `AGRICULTURA` y `VIGILANCIA`.

## Limitaciones conocidas

- La tabla `drone` guarda solo los datos comunes y el tipo. Los atributos propios de cada subclase (`capacidadTanque` y `deteccionTermica`) todavía no se persisten ni se muestran en el formulario.
- Al leer de la base de datos se construye un `DroneBase` interno que conserva el tipo; queda pendiente reemplazarlo por las subclases `Agricultura` y `Vigilancia` según el valor de `tipo`.

## Archivos que no deben versionarse

```gitignore
bin/
target/
.env
.classpath
.project
.settings/
```
# Clima de vuelo: patrones Observer y Chain of Responsibility

Esta sección documenta la integración con la API de clima de **Open-Meteo** y los dos patrones de diseño que se agregaron al sistema de gestión de drones:

- **Observer**: consulta el clima y notifica automáticamente los cambios a múltiples drones.
- **Chain of Responsibility**: evalúa, regla por regla, si el clima permite volar.

Los dos patrones son **independientes**: el Observer funciona sin el Chain y el Chain funciona sin el Observer. Ambos usan el mismo caso de uso para obtener el clima.

---

## Información climática

El sistema obtiene exactamente cinco aspectos meteorológicos, representados por la clase de dominio `Weather`:

| Aspecto | Variable de la API | Unidad |
|---|---|---|
| Temperatura | `temperature_2m` | °C |
| Humedad | `relative_humidity_2m` | % |
| Velocidad del viento | `wind_speed_10m` | km/h |
| Presión atmosférica | `pressure_msl` (a nivel del mar) | hPa |
| Visibilidad | `visibility` | m |

### APIs consumidas (en este orden)

1. **Geocodificación**: `https://geocoding-api.open-meteo.com/v1/search` → obtiene la latitud y longitud de la ciudad.
2. **Clima actual**: `https://api.open-meteo.com/v1/forecast` → obtiene los cinco aspectos en esas coordenadas.

Ninguna de las dos requiere API key. Se consumen con `java.net.http.HttpClient`, incluido en el JDK, por lo que no se agregaron dependencias al `pom.xml`.

---

## Ubicación en la arquitectura hexagonal

Las entidades y reglas de negocio del clima están en el **dominio**. La integración con la API, los casos de uso y la interfaz están **fuera del dominio**. Las dependencias siempre apuntan hacia adentro.

```
dominio
├── modelo
│   ├── Drone                         (Subject del Observer; no se modificó)
│   ├── weather
│   │   ├── Weather                   (los cinco aspectos climáticos)
│   │   └── WeatherObserver           (contrato del Observer)
│   └── chain
│       ├── WeatherHandler            (eslabón base + Resultado)
│       ├── TemperatureHandler
│       ├── HumidityHandler
│       ├── WindHandler
│       ├── PressureHandler
│       └── VisibilityHandler

aplicacion
├── weather
│   ├── GetWeatherByCityCasoDeUso     (geocodificación → clima actual)
│   └── WeatherNotificationService    (suscripciones y notificaciones)
└── observer
    ├── ConsoleObserver
    ├── LoggerObserver
    ├── FlightStatusObserver
    └── DashboardObserver

infraestructura
├── weather
│   ├── GeoApiClient                  (HTTP geocodificación)
│   ├── GeoApiAdapter                 (JSON → coordenadas)
│   ├── OpenWeatherClient             (HTTP clima actual)
│   └── OpenWeatherAdapter            (JSON → Weather)
└── ui
    └── ControlFormDrone              (controlador JavaFX)
```

`GetWeatherByCityCasoDeUso` define dos interfaces anidadas, `GeocodingPort` y `CurrentWeatherPort`, que implementan `GeoApiAdapter` y `OpenWeatherAdapter`. Así la capa de aplicación no depende de la infraestructura.

---

## Patrón Observer

| Rol | Clase |
|---|---|
| Subject | `Drone` |
| Observer (interfaz) | `WeatherObserver` |
| Observers concretos | `ConsoleObserver`, `LoggerObserver`, `FlightStatusObserver`, `DashboardObserver` |
| Gestor de suscripciones | `WeatherNotificationService` |

### Métodos de `WeatherObserver`

| Método | Descripción |
|---|---|
| `suscribir(Drone drone)` | El observador empieza a recibir el clima de ese drone. |
| `desuscribir(Drone drone)` | El observador deja de recibir el clima de ese drone. |
| `notificar(Drone drone, Weather clima)` | Recibe los cinco aspectos climáticos actualizados. |

### ¿Por qué hay un gestor de suscripciones?

La clase `Drone` no se puede modificar, así que no puede guardar su propia lista de observadores. `WeatherNotificationService` guarda, para cada drone (identificado por su `id`), la lista de observadores suscritos. Es la variante del Observer con **gestor de cambios** (ChangeManager, descrita en GoF). Como se usa el `id`, las suscripciones se conservan aunque la tabla se recargue desde la base de datos.

### Observers concretos

| Observer | Qué hace al ser notificado |
|---|---|
| `ConsoleObserver` | Imprime los cinco aspectos en la consola. |
| `LoggerObserver` | Registra la notificación con `java.util.logging`. |
| `FlightStatusObserver` | Guarda el último clima recibido por cada drone. |
| `DashboardObserver` | Envía un resumen a la interfaz, que lo muestra en una ventana emergente. No depende de JavaFX. |

### Flujo de una notificación

```
ControlFormDrone.onNotificarClima()
  └─ WeatherNotificationService.actualizarClima(ciudad, drones)
       ├─ GetWeatherByCityCasoDeUso.ejecutar(ciudad)
       │    ├─ GeoApiAdapter.obtenerCoordenadas(ciudad)
       │    └─ OpenWeatherAdapter.obtenerClimaActual(lat, lon)
       └─ por cada drone → por cada observer suscrito:
            observer.notificar(drone, clima)
```

---

## Patrón Chain of Responsibility

Cada eslabón valida **una sola** condición. Si se cumple, pasa al siguiente; si falla, la cadena se detiene y el clima queda como **no apto**. Si el último eslabón aprueba, el clima es **apto para volar**.

```
TemperatureHandler → HumidityHandler → WindHandler → PressureHandler → VisibilityHandler
```

| Eslabón | Regla |
|---|---|
| `TemperatureHandler` | Temperatura entre 0 °C y 40 °C |
| `HumidityHandler` | Humedad ≤ 85 % |
| `WindHandler` | Velocidad del viento ≤ 40 km/h |
| `PressureHandler` | Presión atmosférica entre 950 hPa y 1050 hPa |
| `VisibilityHandler` | Visibilidad ≥ 5 km |

El resultado se guarda en `WeatherHandler.Resultado` (clase anidada), no en `Weather`, para que el clima solo contenga los cinco aspectos. Se usa así:

```java
WeatherHandler.Resultado resultado = WeatherHandler.evaluar(clima);
resultado.isApto();          // true si se puede volar
resultado.getDetalle();      // recorrido completo de la cadena
```

> La presión se consulta **a nivel del mar** (`pressure_msl`). La presión en superficie de ciudades altas como Bogotá ronda los 750 hPa y haría fallar siempre la regla de 950 a 1050 hPa.

---

## Cómo probarlo en la interfaz

La ventana tiene dos pestañas.

### Pestaña "Drones" (Observer)

1. Selecciona uno o varios drones en la tabla (Ctrl o Shift para selección múltiple).
2. Marca los observers que quieras usar y presiona **Suscribir a seleccionados**.
3. Escribe una ciudad y presiona **Consultar y notificar**.
4. Se abre la ventana emergente con las notificaciones del `DashboardObserver`. Los demás observers escriben en la consola.
5. Para probar la eliminación dinámica, selecciona un drone, presiona **Desuscribir de seleccionados** y vuelve a consultar: ese drone ya no genera notificaciones para los observers quitados.

### Pestaña "Clima de vuelo" (Chain of Responsibility)

1. Escribe una ciudad y presiona **Consultar clima**.
2. Se muestran los cinco aspectos, el resultado (apto o no apto) y el detalle de cada regla evaluada.

Esta pestaña no notifica a ningún observer.

---

## Requisitos

- Java 21 y JavaFX 21.
- Conexión a internet para consultar Open-Meteo.
- Si el proyecto usa `module-info.java`, debe incluir:

```java
requires java.net.http;
requires java.logging;
```

---

## Documentación

Todas las clases de ambos patrones y el controlador tienen Javadoc. Para generarlo:

```bash
mvn javadoc:javadoc
```

Para incluir también los miembros privados:

```bash
mvn javadoc:javadoc -Dshow=private
```

## Consumo energético: patrón Strategy

Esta sección documenta el patrón **Strategy**, que estima la batería que requiere un dron para recorrer una ruta. El origen y el destino de la ruta se cargan desde un archivo JSON. La batería se calcula con una de tres estrategias de consumo energético, que se pueden cambiar en tiempo de ejecución.

El cálculo se hace en memoria y no se guarda en la base de datos. Por eso no hay un servicio con repositorio: el caso de uso `CalcularConsumoCasosDeUso` cumple directamente el rol de **Context**.

---

### Ciclo de uso

1. Seleccionar un dron.
2. Cargar la ruta desde un archivo JSON.
3. Seleccionar una estrategia de consumo energético.
4. Visualizar en pantalla la distancia entre origen y destino, la estrategia seleccionada y el cálculo.

---

### Estrategias

| Estrategia | Clase | Descripción | Fórmula |
|---|---|---|---|
| Modo Ahorro | `AhorroStrategy` | Minimiza el consumo de batería | distancia (km) × 0.5 |
| Modo Normal | `NormalStrategy` | Consumo estándar | distancia (km) × 1 |
| Modo Alto Rendimiento | `AltoRendimientoStrategy` | Aumenta la velocidad sacrificando batería | distancia (km) × 1.5 |

---

### Roles del patrón

| Rol | Clase | Paquete |
|---|---|---|
| Strategy (interfaz) | `ConsumoStrategy` | `dominio.modelo.strategy` |
| Estrategias concretas | `AhorroStrategy`, `NormalStrategy`, `AltoRendimientoStrategy` | `dominio.modelo.strategy` |
| Context | `CalcularConsumoCasosDeUso` | `aplicacion.puerto.entrada` |
| Cliente | `ControlFormDrone` | `infraestructura.ui` |

La interfaz `ConsumoStrategy` define tres métodos:

| Método | Descripción |
|---|---|
| `calcularConsumo(double distanciaKm)` | Devuelve la batería requerida para esa distancia. |
| `getNombre()` | Devuelve el nombre de la estrategia, por ejemplo "Modo Ahorro". |
| `getFactor()` | Devuelve el factor que se multiplica por la distancia. |

El Context guarda la estrategia seleccionada con `setEstrategia(...)` y le delega el cálculo con `calcularConsumo(...)`. Para agregar una estrategia nueva basta con crear otra clase que implemente `ConsumoStrategy`; el Context no se modifica.

---

### Ubicación en la arquitectura hexagonal

```
dominio
└── modelo
    └── strategy
        ├── PuntoGeografico            (punto con nombre, latitud y longitud)
        ├── ConsumoStrategy            (interfaz Strategy)
        ├── AhorroStrategy
        ├── NormalStrategy
        └── AltoRendimientoStrategy

aplicacion
└── puerto
    └── entrada
        └── CalcularConsumoCasosDeUso  (Context)

infraestructura
├── loader
│   └── JsonRutaLoader                 (lee origen y destino desde JSON)
├── util
│   └── HaversineUtil                  (distancia entre coordenadas)
└── ui
    └── ControlFormDrone               (pestaña "Consumo energético")
```

Para que la capa de aplicación no dependa de la infraestructura, `CalcularConsumoCasosDeUso` define una interfaz funcional anidada, `CalculadoraDistancia`. En `App` se le pasa la implementación de Haversine:

```java
CalcularConsumoCasosDeUso consumoUC = new CalcularConsumoCasosDeUso(HaversineUtil::distanciaKm);
controlador.setCalcularConsumo(consumoUC);
```

---

### Archivo de ruta (JSON)

El archivo `ruta.json` está en la raíz del proyecto, al mismo nivel que el `pom.xml`. Tiene este formato:

```json
{
  "origen": {
    "nombre": "Punto Origen",
    "latitud": 4.60971,
    "longitud": -74.08175
  },
  "destino": {
    "nombre": "Punto Destino",
    "latitud": 4.70159,
    "longitud": -74.14690
  }
}
```

`JsonRutaLoader` lee el archivo y crea dos objetos `PuntoGeografico`. Valida que existan `origen` y `destino`, que cada uno tenga `latitud` y `longitud`, y que las coordenadas estén en rango (latitud entre -90 y 90, longitud entre -180 y 180). No requiere dependencias adicionales en el `pom.xml`.

Se pueden crear más rutas con otros nombres (por ejemplo `ruta_larga.json`) manteniendo la misma estructura.

---

### Cálculo de la distancia

`HaversineUtil` calcula la distancia en kilómetros entre dos coordenadas con la **fórmula de Haversine**, que tiene en cuenta la curvatura de la Tierra (radio medio de 6371 km).

---

### Flujo del cálculo

```
ControlFormDrone.onCargarRuta()
  └─ JsonRutaLoader.cargar(archivo)  →  origen y destino (PuntoGeografico)

ControlFormDrone.onCalcularConsumo()
  ├─ CalcularConsumoCasosDeUso.setEstrategia(estrategia)
  ├─ CalcularConsumoCasosDeUso.calcularDistanciaKm(origen, destino)
  │    └─ HaversineUtil.distanciaKm(origen, destino)
  └─ CalcularConsumoCasosDeUso.calcularConsumo(distancia)
       └─ estrategia.calcularConsumo(distancia)
```

---

### Ejemplo de resultados

Con la ruta de ejemplo la distancia es de aproximadamente **12.51 km**:

| Estrategia | Cálculo | Batería requerida |
|---|---|---|
| Modo Ahorro | 12.51 km × 0.5 | ≈ 6.26 |
| Modo Normal | 12.51 km × 1.0 | ≈ 12.51 |
| Modo Alto Rendimiento | 12.51 km × 1.5 | ≈ 18.77 |

---

### Cómo probarlo en la interfaz

1. Abre la pestaña **Consumo energético**.
2. Selecciona un dron.
3. Presiona **Cargar ruta (JSON)**. El selector se abre en la carpeta del proyecto; elige `ruta.json`.
4. Selecciona una estrategia y presiona **Calcular consumo**.
5. Se muestran el dron, la distancia, la estrategia, la operación realizada y la batería requerida.
6. Cambia de estrategia y vuelve a calcular. La distancia es la misma y solo cambia el factor: así se ve el intercambio de estrategias en tiempo de ejecución.

## Misión del dron: patrón Command

Esta sección documenta el patrón **Command**, que permite programar, antes de iniciar una operación, la secuencia de tareas que un dron ejecutará durante una misión. Cada tarea se encapsula en un objeto independiente. Una vez programadas, el sistema las ejecuta en el orden registrado y muestra los resultados.

Las tareas y sus resultados se manejan en memoria y no se guardan en la base de datos.

---

### Comandos disponibles

| Tarea | Clase | Resultado |
|---|---|---|
| Tomar fotografía | `TomarFotografiaCommand` | Nombre del archivo de la foto capturada |
| Escanear zona | `EscanearZonaCommand` | Área escaneada (km²) y objetos detectados |
| Medir temperatura | `MedirTemperaturaCommand` | Temperatura ambiente (°C) |

Como el sistema no tiene sensores reales, los resultados son **simulados**: el nombre de la foto incluye el id del dron y la hora de captura, el área escaneada está entre 0.5 y 2 km² con hasta 10 objetos, y la temperatura está entre 10 °C y 30 °C.

---

### Roles del patrón

| Rol | Clase | Paquete |
|---|---|---|
| Command (interfaz) | `DroneCommand` | `dominio.modelo.command` |
| Comandos concretos | `TomarFotografiaCommand`, `EscanearZonaCommand`, `MedirTemperaturaCommand` | `dominio.modelo.command` |
| Receiver / Wrapper | `DroneOperacionServicio` | `aplicacion.servicio` |
| Invoker | `ProgramadorTareas` | `aplicacion.servicio` |
| Cliente | `ControlFormDrone` | `infraestructura.ui` |

#### `DroneCommand` (Command)

| Método | Descripción |
|---|---|
| `ejecutar()` | Ejecuta la tarea delegando en el Receiver y devuelve el resultado. |
| `getNombre()` | Devuelve el nombre de la tarea, por ejemplo "Tomar fotografía". |

#### `DroneOperacionServicio` (Receiver / Wrapper)

La clase `Drone` no tiene operaciones de misión y no se modifica. `DroneOperacionServicio` **envuelve** un `Drone` (lo recibe en el constructor) y le agrega desde afuera las operaciones `tomarFotografia()`, `escanearZona()` y `medirTemperatura()`. Es además el **Receiver**: los comandos no hacen el trabajo, se lo delegan a él.

#### `ProgramadorTareas` (Invoker)

Guarda la secuencia de comandos y los ejecuta en el orden en que se registraron. No sabe qué hace cada tarea ni qué dron la realiza: solo llama a `ejecutar()`.

| Método | Descripción |
|---|---|
| `programar(DroneCommand)` | Agrega una tarea al final de la secuencia. |
| `eliminar(int)` | Quita la tarea de una posición. |
| `limpiar()` | Elimina todas las tareas. |
| `getTareas()` | Devuelve las tareas programadas, en orden. |
| `ejecutarMision()` | Ejecuta todas las tareas en orden y devuelve los resultados numerados. Si una tarea falla, registra el error y continúa con la siguiente. |

---

### Ubicación en la arquitectura hexagonal

```
dominio
└── modelo
    ├── Drone                            (no se modificó)
    └── command
        ├── DroneCommand                 (interfaz Command + interfaz anidada Receptor)
        ├── TomarFotografiaCommand
        ├── EscanearZonaCommand
        └── MedirTemperaturaCommand

aplicacion
└── servicio
    ├── DroneOperacionServicio           (Receiver / Wrapper)
    └── ProgramadorTareas                (Invoker)

infraestructura
└── ui
    └── ControlFormDrone                 (pestaña "Misión")
```

Los comandos están en el dominio y el Receiver en la capa de aplicación. Para que el dominio no dependa de la aplicación, `DroneCommand` define una **interfaz anidada `Receptor`** con las operaciones que necesitan los comandos, y `DroneOperacionServicio` la implementa. Así los comandos solo conocen esa interfaz del dominio y las dependencias apuntan hacia adentro.

---

### Flujo de una misión

```
ControlFormDrone (Cliente)
  ├─ new DroneOperacionServicio(drone)          → Wrapper / Receiver del dron
  ├─ new TomarFotografiaCommand(operacion)      → Command concreto
  ├─ programador.programar(comando)             → el Invoker guarda la tarea
  └─ programador.ejecutarMision()               → el Invoker ejecuta en orden
        └─ comando.ejecutar()
              └─ operacion.tomarFotografia()    → el Wrapper realiza la operación
```

---

### Ejemplo de resultados

Misión programada: Escanear zona → Tomar fotografía → Medir temperatura.

```
Misión del dron D01 - Phantom 4 (VIGILANCIA)

1. Escanear zona → Zona escaneada: 1.37 km², 4 objeto(s) detectado(s)
2. Tomar fotografía → Fotografía capturada: IMG_D01_152310.jpg
3. Medir temperatura → Temperatura medida: 21.6 °C
```

Los valores cambian en cada ejecución porque son simulados.

---

### Cómo probarlo en la interfaz

1. Abre la pestaña **Misión** y selecciona un dron.
2. Agrega tareas con los botones **Tomar fotografía**, **Escanear zona** y **Medir temperatura**, en el orden que quieras. Se pueden repetir.
3. Si te equivocas, selecciona una tarea en la lista y presiona **Quitar seleccionada**, o usa **Limpiar tareas** para empezar de nuevo.
4. Presiona **Ejecutar misión**. Los resultados aparecen numerados en el mismo orden en que se programaron las tareas.

Si cambias de dron, la secuencia se reinicia, porque los comandos programados apuntan al Receiver del dron anterior.

## Autor

[Flower-Vermillion](https://github.com/Flower-Vermillion) — Proyecto 2, Software 2.
