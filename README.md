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
