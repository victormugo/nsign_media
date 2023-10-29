# nsign_media

__Especificaciones:__
Aplicación donde se muestra a partir de la descarga y descompresión por parte del usuario de un fichero RAR versión 5, 
se visualiza en bucle los archivos multimedia definidos en un fichero de configuración de tipo JSON.


__Requisitos mínimos:__
- Dispositivo Android con versión mínima 7.0 (api level 24 - Nougat)
- Conexión a internet para la descarga de fichero de configuración
- Tener instalada la aplicación WINRAR para la descompresión del fichero de configuración con el nombre NSIGN_Prueba_Android.rar


__Permisos necesarios:__
Todos los permisos detallados a continuación están definidos en el fichero Manifest
- INTERNET: Conexión a internet para la descarga de ficheros vía API REST
- READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE: Android inferior a versión 11, permiso de almacén
- MANAGE_EXTERNAL_STORAGE: Android igual o superior a versión 11, permiso de manipulación total de ficheros
- FOREGROUND_SERVICE: Gestión de servicios en segundo plano


__Estructura del proyecto:__
- Activities:
	- MainActivity: Actividad principal donde se visualizan las imágenes y los vídeos definidos en el fichero events.json
	- Core: Actividad secundaria (sin vista) donde se gestionan las variables globales utilizadas en el proyecto y métodos de gestión del servicio en background
- Services:
	- LoadMediaData: Servicio en background donde se gestiona el siguiente elemento multimedia y duración que tiene que mostrarse en la actividad principal
	- La comunicación entre el servicio en background y la vista en foreground se realiza con la librería eventBus
- Bus:
	- Clase que se envía en la comunicación entre el servicio (background) y la vista principal (foreground) con el elemento a mostrar, la posición y la duración en pantalla
- API donde se guardan todas las clases del fichero events.json:
	- VoMedia
		- VoSchedule
		- VoPlaylists
			- VoResource
	- Interficie API para realizar la petición al servidor y obtener el fichero comprimido en RAR
- Utils:
	- Dialogs: Definición por defecto de los cuadros de dialogos mostrados en pantalla. Se definen los de tipo "loading" y los mensajes de interacción con el usuario.
	- ExtractArchive: Herramienta para la descompresión de los ficheros RAR a partir de la librería JUNRAR. (NO se utiliza porque no está adaptada a la versión V5 de los ficheros RAR).
	- Utils: Métodos que sirven para gestionar los elementos mostrados en pantalla a partir de las clases almacenadas desde la gestión del fichero events.json:
		- loadJSONFromAsset: Carga en las clases de los models la configuración indicada en el fichero events.json que se almacena en la carpeta DOWNLOAD de la memoria interna del dispositivo.
		- loadNextMediaFile: Carga el siguiente elemento a mostrar en pantalla. Información enviada desde el servicio en background a la vista principal (MainActivity) mediante eventBus.
		- totalResources: Función no utilizada en esta versión. Devuelve el número total de elementos definidos en el fichero events.json.
		- allResourcesDone: Función que devuelve un boolean indicando si todos los recursos definidos en el fichero events.json ya han sido tratados para volver a empezar y así repetir el bucle hasta que se cierre el servicio.
		- verifyAllResourcesLoaded: Método que verifica a partir del método allResourcesDone si se tiene que reiniciar el bucle de elementos multimedia a mostrar en pantalla.
		- inicializeMediaDone: Método que reinicia a partir del método verifyAllResourcesLoaded los elementos a mostrar en pantalla.
		- writeResponseBodyToDisk: Método que graba en la carpeta DOWNLOAD de la memoria interna del dispositivo el fichero NSIGN_Prueba_Android.rar descargado mediante petición API.


__Puesta en marcha y Flujo de trabajo:__
- Al iniciar la aplicación por primera vez:
	- en un dispositivo con versión de Android inferior a 11, se muestra un cuadro de dialogo preguntando si se desea Permitir el permiso de Almacén.
	- en un dispositivo con versión de Android superior a 11, se dirige automáticamente a la aceptación del permiso de manipulación de ficheros.
- NOTA: Si no se acepta el permiso, la aplicación no va a continuar. Este permiso es obligatorio para la gestión interna de los datos y su vialización.
- Al aceptar, se muestra un cuadro de dialogo con una barra de progeso indicando al usuario que se están cargando los recursos:
	- El funcionamiento interno es:
		- Activación del tunel de comunicación entre el servicio en background y la vista principal.
		- Activación del servicio en segundo plano (LoadMediaData).
			- Al iniciar el servicio se verifica la existencia del fichero NSIGN_Prueba_Android.rar en la carpeta DOWNLOAD (/storage/emulated/0/Download) de la memoria interna del dispositivo.
				- Si no existe, se realiza petición API al servidor para descargar el fichero RAR. Se descargará en la carpeta DOWNLOAD (/storage/emulated/0/Download) de la memoria interna del dispositivo.
				- Se mostrará un cuadro de dialogo detallando al usuario cómo debe proceder para la descompresión del fichero:
					- Instalar aplicación WINRAR desde play store.
					- Abrir aplicación WINRAR y descomprimir los ficheros directamente en la carpeta DOWNLOAD (/storage/emulated/0/Download) de la memoria interna del dispositivo.
						- En la carpeta DOWNLOAD (/storage/emulated/0/Download) se tienen que visualizar los siguientes ficheros una vez descomprimido:
							- NSIGN_Prueba_Android.rar
							- events.json
							- Imagen Test 1.png
							- Imagen Test 2.png
							- Video Test 1.mp4
					- Volver a inciar la aplicación.

				- Si existe, se generan las clases definidas en la carpeta models en API (loadJSONFromAsset).
				- Se reagenda el timerTask del servicio con el tiempo definido en el campo duración del elemento a mostrar.
				- Se envía a la vista principal los elementos necesarios para su gestión en la clase IntentServiceResult.

		- En el método EventBus de la vista principal se está esperando la clase enviada por el servicio en segundo plano.
		- El funcionamiento interno es:
			- Se comprueba si elemento a mostrar es una imagen o un video.
			- Si es imagen, se cambia a visible el elemento de la vista de imagen y se cambia a no visible (GONE) el elemento video de la vista.
			- Si es video, se cambia a visible el elemento de la vista de video y se cambia a no visible (GONE el elemento imagen de la vista.
			- Se carga el recurso indicado en la clase IntentServiceResult de la carpeta DOWNLOAD previamente descomprimido y se muestra en la posición, tamaño y duración definidos en el fichero events.json.

NOTA: 
- Si se minimiza la aplicación o se cierra completamente, el servicio en background (LoadMediaData) se finaliza para evitar el consumo de recursos. No es necesario tenerlo activo porque el dispositivo no está mostrando 
los elementos en bucle.
- Al volver a iniciar, se realizan todas las verificaciones especificadas anteriormente.


__Gestión de errores:__
- Si no se acepta el permiso de almacenamiento, la aplicación no puede continuar. La gestión está controlada para que no provoque un cierre inesperado.
- Si no se detecta el fichero NSIGN_Prueba_Android.rar se muestra mensaje en pantalla.
- Si no se detecta el fichero events.json se muestra mensaje en pantalla.
- Si no se detecta algún elemento multimedia definido en events.json, se muestra mensaje en pantalla.
- Todos los mensajes que se muestran en pantalla están definidos en el fichero strings.
- Todos los colores que se muestran en pantalla están definidos en el fichero colors.
- Se han dejado mensajes Log.d en todo el código para poder hacer un mejor seguimiento. (Al subir a producción, se tienen que quitar para evitar el consumo innecesario de recursos).
- Todos los métodos que no son nativos tienen comentario para saber qué función realizan.
- No existe código repetido. Si se tiene que repetir, se ha creado un método para su gestión.


__Versiones de la compilación:__
- AGP versión: 8.1.2
- Gradle versión: 8.4
- Compile SDK version: 34 (Android 14)
- Tarjet SDK Version: 34 (Android 14)
- Min SDK Version (24) (Android 7.0)
- Versión de la app: 1.0.0


__Aplicación Testeada en:__
- Tablet Samsung con Android 7.0.
- Móvil Samsung A12 con Android 12.
- Móvil Samsung A52 con Android 13.
