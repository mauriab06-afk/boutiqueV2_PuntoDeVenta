
Sistema de Punto de Venta (POS) de escritorio para gestión de inventarios, ventas y administración de accesos, desarrollado en **Java 17 (Swing)** y **SQLite** bajo la arquitectura **Modelo-Vista-Controlador (MVC)**.

---

## Tecnologías y Librerías

* **Lenguaje:** Java 17 (Java Swing GUI)
* **Base de Datos:** SQLite 3 (`boutique.db`)
* **Librerías (ubicadas en `lib/`):**
  * `sqlite-jdbc-3.45.1.0.jar`
  * `slf4j-api-2.0.12.jar`
  * `slf4j-simple-2.0.12.jar`

---

## Características Principales

* **Control de Acceso Basado en Roles (RBAC):**
  * **Administrador:** Gestión total de catálogo, reabastecimiento de inventario, reportes de ventas y creación/bloqueo de cuentas.
  * **Cajero:** Limitado a la terminal de ventas y cobranza en efectivo.
* **Venta e Inventario en Tiempo Real:**
  * Comanda dinámica en memoria con validación estricta de existencias.
  * Cálculo de cambio en tiempo real y descuento transaccional del stock en base de datos.
* **Respaldo y Resiliencia:**
  * Copia de seguridad automática diaria (`boutique_backup.db`).
  * Autoreparación de base de datos en caso de corrupción o borrado accidental.
* **Módulo de Reportes Analíticos:**
  * Historial filtrado por rango de fechas (`AAAA-MM-DD`).
  * Desglose mensual acumulado por año.
  * Top 3 productos más y menos vendidos.

---

## Estructura del Proyecto

```text
boutiquev2/
├── lib/                      # Archivos JAR de dependencias
├── modelo/                   # Entidades POJO, conexional y DAOs SQL
├── controlador/              # Lógica de negocio y backups
├── vista/                    # Interfaces gráficas Swing (JDialog/JFrame)
├── run.sh                    # Script bash de compilación y ejecución
└── README.md
