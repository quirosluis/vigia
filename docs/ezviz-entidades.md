# Entidades EZVIZ en Home Assistant

> Sesión 3 (13/10/2026). Integración EZVIZ de Home Assistant, de tipo *cloud polling* (HA consulta la nube de EZVIZ cada cierto tiempo).
> No se publican números de serie, IPs (local ni WAN) ni la cuenta usada. La correspondencia entre "cámara 1 / cámara 2" y cada dispositivo físico se guarda fuera del repositorio.

## Dispositivos

| Dispositivo en HA | Modelo | Entidades | Observación |
|---|---|---|---|
| Acceso cocina | eLife | 46 | Agrupa las entidades de **las dos cámaras**: `camera.acceso_cocina` y `camera.jardin` (cada sensor aparece dos veces, con IP local distinta) |
| EZVIZ Alarm | EZVIZ Alarm | 1 | Panel de alarma de la cuenta (armar/desarmar). No es una cámara |

Las dos cámaras aparecen además como "descubiertas" para configurar el vídeo por RTSP. No se configuran: HA no está en la red de la segunda residencia.

## Sensores (por cámara)

Valores del 13/10/2026, iguales en las dos cámaras salvo la IP local.

| Entidad | Tipo | Valor ejemplo | ¿Se actualiza dormida? | ¿Útil para diagnóstico? |
|---|---|---|---|---|
| Estado de la cámara (nombre del dispositivo) | Texto | Grabando | Pendiente (historial) | Quizá: comprobar qué valores toma cuando la app pierde la cámara |
| Batería | % | 100 | Pendiente (historial) | **Sí**: batería baja → causa "cámara (energía)" |
| IP local | Texto | (no publicada) | Pendiente (historial) | **Sí**: si cambia, la cámara se ha reconectado al wifi (nueva asignación DHCP) |
| IP WAN | Texto | (IP pública, no publicada) | Pendiente (historial) | **Sí**: si cambia, el proveedor ha reasignado la IP; posible corte de internet |
| Movimiento | Sí/No | No detectado | — | No |
| Estado del PIR | Número | 0 | — | No |
| Imagen de último movimiento | Imagen | Desconocido | — | No |
| Código del último tipo de alarma | Texto | 0000 | — | No |
| Nombre del último tipo de alarma | Texto | NoAlarm | — | No |
| Programaciones de alarma habilitadas | Encendido/Apagado | Apagado | — | No |
| Cifrado | Encendido/Apagado | Encendido | — | No |
| Canales admitidos | Número | 1 | — | No |

## Configuración (por cámara)

| Entidad | Tipo | Valor ejemplo | ¿Útil para diagnóstico? |
|---|---|---|---|
| Modo de funcionamiento con batería | Selector | Ahorro de energía | **Sí**: define cuánto duerme la cámara; un cambio de modo cambia qué es "normal" |
| Firmware | Actualización | Actualizado | Poco: una actualización puede explicar una desconexión puntual |
| Sensibilidad de detección | Número | — | No |
| Sonido de advertencia | Selector | Silencioso | No |

## Pendiente de revisar

- Frecuencia real de actualización: mirar el historial de Batería, IP local e IP WAN tras 24 h.

## Conclusiones provisionales

- Útiles para el motor de diagnóstico: batería, IP local, IP WAN y, si existen, señal wifi y estado en línea.
- Ninguna de estas señales es en tiempo real: llegan por la nube con retraso, y si la nube de EZVIZ falla dejan de llegar aunque las cámaras estén bien. La sonda local es la que desempata.
- Entidades no localizadas: **señal wifi** y **estado en línea**
