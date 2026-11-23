# Sistema de Punto de Venta (POS) — Boutique

Sistema de Punto de Venta (POS) de escritorio para gestión de inventarios, ventas, analítica y administración de accesos, desarrollado en **Java 17 (Swing)** y **SQLite** bajo la arquitectura **Modelo-Vista-Controlador (MVC)**.

---

## Tecnologías y Librerías

- **Lenguaje:** Java 17 (Java Swing GUI)
- **Base de Datos:** SQLite 3 (`boutique.db`)
- **Librerías (ubicadas en `lib/`):**
  - `flatlaf-3.5.4.jar` (Interfaz gráfica moderna con tema claro unificado)
  - `jbcrypt-0.4.jar` (Cifrado seguro de contraseñas con hash BCrypt)
  - `sqlite-jdbc-3.45.1.0.jar`
  - `slf4j-api-2.0.12.jar`
  - `slf4j-simple-2.0.12.jar`

---

## Características Principales

- **Seguridad y Control de Acceso Basado en Roles (RBAC):**
  - Autenticación con contraseñas cifradas mediante **BCrypt** y migración automática de credenciales heredadas.
  - **Administrador:** Gestión total de catálogo, reabastecimiento de inventario, reportes financieros, matriz gráfica de ventas, respaldo manual y administración de usuarios.
  - **Cajero:** Acceso enfocado a la terminal de ventas, cobranza en efectivo y guía de usuario.
- **Venta e Inventario en Tiempo Real:**
  - Comanda dinámica en memoria con validación estricta de existencias y alertas visuales de stock bajo/agotado.
  - Cálculo de cambio en tiempo real y descuento transaccional del inventario en SQLite.
- **Respaldo y Resiliencia (`backups/`):**
  - Copia de seguridad automática diaria (`boutique_auto_AAAA-MM-DD.db`) y respaldos manuales bajo demanda (`boutique_manual_AAAAMMDD_HHMMSS.db`).
  - Detección y restauración automática desde el respaldo más reciente si la base de datos principal falta.
- **Módulo de Reportes y Matriz Analítica:**
  - Historial de ventas filtrado por rango de fechas (`AAAA-MM-DD`) con vista detallada por ticket y eliminación controlada.
  - Desglose mensual acumulado por año y ranking Top 3 de productos más y menos vendidos.
  - Matriz semanal/mensual de unidades vendidas por producto con comparativas de crecimiento (suma/resta entre periodos) y gráfica interactiva.

---

## Estructura del Proyecto

```text
boutiquev3/
├── lib/                      # Archivos JAR de dependencias (SQLite, SLF4J, FlatLaf, jBCrypt)
├── modelo/                   # Entidades POJO, conexión SQLite, matriz de ventas y DAOs
├── controlador/              # Lógica de negocio, eventos MVC y gestión de backups
├── vista/                    # Interfaces gráficas Swing, gráficas personalizadas y diálogos
├── backups/                  # Respaldos automáticos y manuales de la base de datos
├── Main.java                 # Punto de entrada y configuración del tema FlatLaf
├── run.sh                    # Script bash de compilación y ejecución
└── README.md
