# 🔭 Vigía

> *Sistema de monitorización y diagnóstico de causa para ubicaciones remotas.*

---

## 🎯 Propósito

**Vigía** está diseñado para solucionar la incógnita de saber qué ha pasado con las cámaras de vigilancia en una segunda residencia. 

Cuando los dispositivos dejan de responder en la aplicación nativa **EZVIZ**, el objetivo es identificar con precisión dónde se encuentra el fallo real. La sonda desplegada en el sitio permite conocer el estado de:
* La conectividad de red
* El router local
* El estado de las cámaras

---

## 📄 Especificación del Log

La sonda escribe un registro estructurado con el estado actual de los elementos monitorizados.

### Formato de línea

```csv
timestamp_utc,objetivo,ok,latencia_ms,bateria,cargando
```

### Definición de Campos

| Campo | Tipo / Formato | Descripción |
| :--- | :--- | :--- |
| `timestamp_utc` | `ISO 8601` en UTC | Marca temporal de la medición (ej. `2026-10-10T18:42:07Z`). |
| `objetivo` | Valores cerrados | Elemento evaluado (ej. `router`, `internet`, `dns`, y más adelante `camara-1`). |
| `ok` | `1` o `0` | Estado de la comprobación (`1` si hay éxito, `0` si falla). |
| `latencia_ms` | Numérico / Vacío | Tiempo de respuesta en milisegundos. Queda **null si `ok=0`**. |
| `bateria` | Porcentaje entero | Nivel de batería actual del dispositivo sonda (`0` a `100`). |
| `cargando` | `1` o `0` | Estado de carga del dispositivo (`1` si está conectado a la corriente, `0` si descarga). |

---

## 🌐 Enfoque Multi-Sitio

El envío de este log desde la sonda incluye de forma inherente la identificación del origen o **sitio** emisor, arquitectura pensada desde el diseño para escalar y soportar múltiples ubicaciones de forma centralizada.

## Deuda

- No disponemos de copia de seguridad de la base de datos PostgreSQL