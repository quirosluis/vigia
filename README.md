Vigía

Pensado para solucionar la incognita de saber que ha pasado con las cámaras de vigilancia en una segunda residencia.
Cuando estas dejan de responder en la aplicación nativa EZVIZ, queremos saber donde está el problema.
La sonda nos permitirá conocer el estado de la conexión, el router y las cámaras.

Escribiremos un log con el estado.
El formato de línea de log será el siguiente:

timestamp_utc,objetivo,ok,latencia_ms,bateria,cargando

Donde:
timestamp_utc: ISO 8601 en UTC, por ejemplo 2026-10-10T18:42:07Z.
objetivo: valores cerrados, por ejemplo router, internet, dns, y más adelante camara-1.
ok: 1 o 0.
latencia_ms: número; vacío si ok=0, nunca 0, porque 0 ms parece una medición perfecta.
bateria: porcentaje entero.
cargando: 1 o 0.

El envío de este log desde la sonda, indicará el origen de la misma (o sitio), pensado para múltiples sitios.

