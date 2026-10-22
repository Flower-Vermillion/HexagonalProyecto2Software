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

## Autor

[Flower-Vermillion](https://github.com/Flower-Vermillion) — Proyecto 2, Software 2.
