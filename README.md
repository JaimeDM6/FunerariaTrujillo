# Funeraria Trujillo — Sistema ERP de Gestión Funeraria

> \*"Entierros para cualquier bolsillo"\*

Aplicación de escritorio desarrollada en **JavaFX** y **MongoDB** para la gestión operativa integral de **Funeraria Trujillo** (proyecto de 2DAMi - Grupo 5). El sistema cubre el ciclo completo del tanatorio —desde el alta de clientes y difuntos hasta la asignación de tanatopractores y el seguimiento en tiempo real de cada preparación— y se complementa con **Odoo 19** para la gestión administrativa auxiliar.

## Tabla de contenidos

* [Funcionalidades principales](#funcionalidades-principales)
* [Arquitectura y tecnologías](#arquitectura-y-tecnologías)
* [Estructura del proyecto](#estructura-del-proyecto)
* [Requisitos previos](#requisitos-previos)
* [Instalación y ejecución](#instalación-y-ejecución)
* [Usuarios de prueba (Login)](#usuarios-de-prueba-login)
* [Pruebas automatizadas](#pruebas-automatizadas)
* [Integración complementaria con Odoo 19](#integración-complementaria-con-odoo-19)
* [Equipo de desarrollo](#equipo-de-desarrollo)

## Funcionalidades principales

* **Autenticación y control de acceso por roles:** inicio de sesión con contraseñas cifradas mediante **BCrypt** y 5 roles corporativos (`ADMINISTRACION`, `AGENTE\_FUNERARIO`, `TANATOPRAXIA`, `TRANSPORTE` y `CONTABILIDAD`).
* **Gestión de clientes y difuntos:** alta, consulta, edición y baja de expedientes, con buscador dinámico de cliente responsable, selección de tipo de servicio (`ENTIERRO` / `CREMACIÓN`) y asignación de materiales.
* **Catálogo de recursos y espacios:** mantenimiento completo (CRUD) de **féretros**, **urnas**, **servicios adicionales** y **salas de velatorio**.
* **Recursos Humanos (Trabajadores):** administración de plantilla, cargos, roles, datos bancarios (IBAN), número de la Seguridad Social, salario y tipo de jornada.
* **Asignación de servicios:** panel dividido entre servicios pendientes de asignar y trabajos asignados, con indicador de carga de trabajo actual por tanatopractor.
* **Ficha del servicio:** seguimiento detallado para tanatopractores con control de estados (`Pendiente`, `En curso`, `Completado`), lista de hitos de preparación (identificación, conservación temporal y estética), observaciones y registro de última actualización.

## Arquitectura y tecnologías

El proyecto sigue una **arquitectura en tres capas (MVC + DAO)**:

```text
+-------------------------------------------------------+
|          CAPA DE PRESENTACIÓN (UI - JavaFX)           |
|      Vistas FXML + styles.css + Fuente Inter 18pt     |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
|        CAPA DE LÓGICA DE NEGOCIO (Controladores)      |
|   Controladores Java, validaciones y tareas async     |
+-------------------------------------------------------+
                           |
                           v
+-------------------------------------------------------+
|          CAPA DE ACCESO A DATOS (Patrón DAO)          |
|    DAOs MongoDB Driver -> Colecciones con JSON Schema |
+-------------------------------------------------------+
```

### Stack tecnológico

|Área|Tecnología|
|-|-|
|**Lenguaje y Runtime**|Java 17+|
|**Interfaz de escritorio**|JavaFX (FXML + CSS personalizado)|
|**Gestión de dependencias**|Apache Maven 3.8+|
|**Base de datos principal**|MongoDB (con `$jsonSchema` Validators)|
|**Seguridad**|BCrypt para hash de contraseñas|
|**Testing**|JUnit 5 + TestFX (con Mock DAOs)|
|**ERP complementario**|Odoo 19 + PostgreSQL (Kanban y Facturación)|

### Modelo de datos en MongoDB

La base de datos utiliza un enfoque híbrido (documentos embebidos para datos dependientes como `direccion`, `datos\_bancarios` o `seguimiento`, y referencias por `ObjectId` para relaciones entre entidades principales) distribuido en las siguientes colecciones:

* `cliente`
* `difunto`
* `trabajador`
* `feretro`
* `urna`
* `servicios`
* `sala`
* `asignacion`

## Estructura del proyecto

```text
funeraria-trujillo/
├── docs/                                   # Documentación APA 7 y diagramas (Casos de Uso, DIN, BPMN)
├── scripts/
│   └── MongoDB (Create + Insert).js        # Script de creación de colecciones, validadores y datos iniciales
├── src/
│   ├── main/
│   │   ├── java/com/tartanga/funerariatrujillo/
│   │   │   ├── DAO/                        # Clases DAO y configuración de conexión (MongoConfig)
│   │   │   ├── Objects/                    # Modelos de dominio (Cliente, Difunto, Trabajador, Asignacion...)
│   │   │   ├── Utils/                      # Utilidades comunes de tablas, fechas y animaciones
│   │   │   └── \*Controller.java            # Controladores JavaFX por vista
│   │   └── resources/com/tartanga/funerariatrujillo/
│   │       ├── fonts/                      # Archivos estáticos .ttf de la fuente Inter 18pt
│   │       ├── styles.css                  # Hoja de estilos global
│   │       └── \*.fxml                      # Vistas FXML
│   └── test/
│       └── java/com/tartanga/funerariatrujillo/
│           └── \*ControllerTest.java        # Pruebas automatizadas de interfaz con TestFX
└── pom.xml
```

## Requisitos previos

* **JDK 17** o superior
* **Apache Maven 3.8+**
* **MongoDB Server** en ejecución local
* **Git**
* *(Opcional)* **Odoo 19** y **PostgreSQL** para el entorno ERP complementario

## Instalación y ejecución

### 1\. Clonar el repositorio

```bash
git clone \[URL-del-repositorio]
cd \[nombre-carpeta-repo]
```

### 2\. Inicializar la base de datos en MongoDB

Asegúrate de tener el servicio de MongoDB levantado y ejecuta el script de creación e inserción de datos iniciales desde `mongosh` (o MongoDB Compass):

```bash
mongosh < "db\MongoDB (Create + Insert).js"
```

> El script crea los validadores `$jsonSchema` e inserta salas, féretros, urnas, servicios, 15 trabajadores, 200 clientes y 225 difuntos de demostración.

### 3\. Compilar el proyecto

```bash
mvn clean install
```

*(Si deseas compilar sin lanzar la suite de pruebas de interfaz: `mvn clean install -DskipTests`)*

### 4\. Ejecutar la aplicación

```bash
mvn javafx:run
```

## Usuarios de prueba (Login)

Todas las cuentas generadas por el script inicial comparten la contraseña en texto plano **`trujillo123`** (almacenada en MongoDB como hash BCrypt):

|Rol|Cargo|Usuario|Contraseña|
|-|-|-|-|
|`ADMINISTRACION`|Director|`m.ruiz`|`password123`|
|`TANATOPRAXIA`|Tanatopractor|`g.ibarretxe`|`password123`|
|`AGENTE\_FUNERARIO`|Agente funerario|`a.lopez`|`password123`|
|`TRANSPORTE`|Conductor|`j.uriarte`|`password123`|
|`CONTABILIDAD`|Recepcionista|`l.garcia`|`password123`|

## Pruebas automatizadas

El proyecto cuenta con pruebas de integración de interfaz construidas con **JUnit 5** y **TestFX**, aislando la persistencia mediante **Mock DAOs** inyectados en cada controlador:

* `LoginControllerTest`
* `ClientesControllerTest`
* `DifuntosControllerTest`
* `TrabajadorControllerTest`
* `FeretroControllerTest`
* `UrnasControllerTest`
* `ServiciosControllerTest`
* `SalasControllerTest`
* `AsignacionesControllerTest`
* `FichaServicioControllerTest`

Para ejecutar toda la batería de tests:

```bash
mvn test
```

## Integración complementaria con Odoo 19

De forma independiente a la aplicación JavaFX (centrada en la operativa del tanatorio), el proyecto incluye un despliegue local de **Odoo 19** sobre **PostgreSQL** (`localhost:5432`, interfaz web en `http://localhost:8069`, base de datos `2DAMi\_Grupo3`) con dos módulos activos:

1. **Proyecto (Kanban):** gestión visual de tareas generales de la empresa (limpieza de salas, logística de conductores y coordinación administrativa), documentado en el diagrama BPMN del proyecto.
