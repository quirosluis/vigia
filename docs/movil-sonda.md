- Móvil Oneplus 3 (A3003)
- Android 9 (OxygenOS 9.0.6)

Optimización de batería desactivada para Termux
Wifi siempre activo

Pruebas realizadas:
	ping -c 5 ip-router -> OK
	ping -c 5 1.1.1.1 -> OK
	ping -c 5 www.google.com ->OK

	comando termux-battery-status -> OK, retorna json con estado de la batería, probando cargando y sin cargar
	