# Diario de fallos

Una entrada por cada vez que la app de EZVIZ pierde una cámara.
Hora local, con la zona (Madrid). La sonda registra en UTC; convierto al comparar.

## Decisión sobre Idempotencia
- clave natural única (sitio, sonda, timestamp_utc, objetivo).
- Evidencia: el 25/10, dos sondas con ciclos coincidentes repitieron marcas de tiempo.
- Solape parcial: se insertan las filas nuevas y se ignoran las existentes.
- Política ante conflicto: DO NOTHING No queremos conflicto. Lo nuevo se añade, lo existente se ignora.
- Límite: no cubre dos sondas con un offset distinto; eso se resuelve evitando el arranque duplicado, no en la ingestión.

## Cómo anotar

- **Fecha y hora** en que lo noto, y desde dónde miro (casa, móvil con datos...).
- **Qué pasaba:** mensaje de la app, qué cámara, si fallan las dos.
- **Qué comprobé:** si hay luz en el sitio (si sé algo), si el resto de internet funciona, si otras cámaras responden.
- **Cuánto duró** y **cómo se resolvió** (volvió sola, reinicié algo, esperé).
- **Sospecha:** mi hipótesis, aunque sea dudosa.
- **Modo batería:** configuración de modo batería, por defecto partimos de "Ahorro de energía".

## Entradas reales

### 2026-10-12 · 21:30
- Qué pasaba:
- Qué comprobé:
- Duración / resolución:
- Sospecha:

## Fallos anteriores (de memoria, baja precisión)

- (anota los que recuerdes, sin hora exacta)

## Pruebas provocadas (no son fallos reales)

| Fecha y hora | Qué provoqué | Qué esperaba ver |
| 16:59:48Z, 18:59:48 | informamos conttraseña incorrecta | Nos retorna error: FATAL: password authentication failed for user "vigia" |
| 17:01:48Z, 19:01:48 | cerramos el tunel ssh | Nos retorna error: Connection to 127.0.0.1:5432 refused. Check that the hostname and port are correct and that the postmaster is accepting TCP/IP connections. |
| 17:02:48Z, 19:02:48 | informamos DB errónea | Nos retorna error: FATAL: database "vigiaaa" does not exist |
| 19:11:48Z, 21:11:48 | desconecté el cargador | Indicador de carga a 0 |
| 19:11:48Z, 21:11:48 | desconecté el cargador | Indicador de carga a 0 |