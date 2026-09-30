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

## Autor

[Flower-Vermillion](https://github.com/Flower-Vermillion) — Proyecto 2, Software 2.
