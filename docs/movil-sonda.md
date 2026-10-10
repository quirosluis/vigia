- Móvil Oneplus 3 (A3003)
- Android 9 (OxygenOS 9.0.6)

Origen de las apps: F-Droid
Termux 0.118.3
Termux:API 0.53.0
Termux:Boot 0.8.1

Optimización de batería desactivada para Termux
Wifi siempre activo incluso con pantalla desactivada
Desactivado ahorro de energía
Configuración de Termux.Boot pendiente de realizar


Pruebas realizadas:
	ping -c 5 ip-router -> OK
	ping -c 5 1.1.1.1 -> OK
	ping -c 5 www.google.com ->OK

	comando termux-battery-status -> OK, retorna json con estado de la batería, probando cargando y sin cargar
	