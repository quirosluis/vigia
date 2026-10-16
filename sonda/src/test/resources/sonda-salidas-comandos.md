# Comportamiento de `ping` y `termux-battery-status` en Termux

Salidas reales del móvil de la sonda (OnePlus A3003, Android 9, Termux), usadas como base para el módulo `sonda` y para el parser de resultados.

## 1. `ping`: tres resultados y sus códigos de salida

Siempre con `-c 1` (un solo paquete) y `-W 2` (espera máxima de 2 s).

| Caso | Código de salida (`$?`) | Significado para la sonda |
|---|---|---|
| Destino responde | `0` | `ok=1`, con latencia |
| Sin respuesta (timeout) | `1` | `ok=0`, sin latencia |
| El nombre no se resuelve | `2` | `ok=0`, fallo de DNS |

### 1.1 Éxito

```console
~ $ ping -c 1 -W 2 <ip-router>
PING <ip-router> (<ip-router>) 56(84) bytes of data.
64 bytes from <ip-router>: icmp_seq=1 ttl=64 time=3.68 ms

--- <ip-router> ping statistics ---
1 packets transmitted, 1 received, 0% packet loss, time 0ms
rtt min/avg/max/mdev = 3.687/3.687/3.687/0.000 ms

~ $ echo $?
0
```

### 1.2 Fallo por tiempo (IP que no existe)

Se usa `192.0.2.1`, del rango de documentación (RFC 5737), que nunca responde.

```console
~ $ ping -c 1 -W 2 192.0.2.1
PING 192.0.2.1 (192.0.2.1) 56(84) bytes of data.

--- 192.0.2.1 ping statistics ---
1 packets transmitted, 0 received, 100% packet loss, time 0ms

~ $ echo $?
1
```

### 1.3 Fallo de DNS (nombre que no resuelve)

Se usa el dominio `.invalid`, reservado para pruebas.

```console
~ $ ping -c 1 -W 2 noexiste.invalid
ping: unknown host noexiste.invalid

~ $ echo $?
2
```

## 2. `termux-battery-status`

Devuelve JSON. Los campos que usa la sonda son `percentage` (→ `bateria`) y `status`/`plugged` (→ `cargando`).

### 2.1 Sin el cargador

```console
~ $ termux-battery-status
{
  "present": true,
  "technology": "Li-ion",
  "health": "GOOD",
  "plugged": "UNPLUGGED",
  "status": "DISCHARGING",
  "temperature": 26.8,
  "voltage": 3736,
  "current": 144000,
  "percentage": 22,
  "level": 22,
  "scale": 100,
  "charge_counter": 913107
}
```

### 2.2 Con el cargador

```console
~ $ termux-battery-status
{
  "present": true,
  "technology": "Li-ion",
  "health": "GOOD",
  "plugged": "PLUGGED_AC",
  "status": "CHARGING",
  "temperature": 26.9,
  "voltage": 3999,
  "current": -2306000,
  "percentage": 22,
  "level": 22,
  "scale": 100,
  "charge_counter": 909668
}
```

### 2.3 Diferencias relevantes

| Campo | Sin cargador | Con cargador |
|---|---|---|
| `plugged` | `UNPLUGGED` | `PLUGGED_AC` |
| `status` | `DISCHARGING` | `CHARGING` |
| `current` | positivo (`144000`) | negativo (`-2306000`) |

## 3. Conclusiones para el diseño de la sonda

- El **código de salida** distingue tres casos (`0`, `1`, `2`), pero el formato de log solo tiene `ok=0/1`: el fallo de DNS se tratará aparte cuando se añada el objetivo `dns`.
- Con `ok=0` la latencia no se escribe (queda vacía en el log, `null` en el modelo).
- Para `cargando` basta con `plugged != UNPLUGGED` (o `status == CHARGING`); conviene decidir cuál y qué hacer con `FULL` o `NOT_CHARGING`.
- 