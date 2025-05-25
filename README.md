Gacha Fit - Perfil de Usuario
Este repositorio contiene el código fuente (src) y el archivo APK para la aplicación Android Gacha Fit, que permite gestionar el perfil de usuario, incluyendo nombre y foto de perfil, con integración a Firebase Authentication y Firestore.

Contenido del repositorio
src/ — Código fuente de la aplicación Android.

app-debug.apk — APK para instalar directamente en dispositivos Android compatibles.

Requisitos para compilar desde código fuente
Android Studio instalado.

Configurar Firebase Authentication y Firestore en un proyecto Firebase.

Descargar y colocar el archivo google-services.json en la carpeta app/ dentro del proyecto.

Configurar permisos y FileProvider en el AndroidManifest.xml (ver sección Configuración abajo).

Instalación APK
Copia el archivo app-debug.apk a tu dispositivo Android.

En tu dispositivo, habilita la instalación desde fuentes desconocidas (Ajustes > Seguridad).

Abre el APK para instalar la aplicación.

Ejecuta la app y accede al fragmento de perfil para gestionar usuario y foto.

Configuración necesaria para el código fuente
Para que la aplicación funcione correctamente al compilar desde el código:

Debes configurar Firebase en el proyecto (añadir google-services.json).

Asegúrate de incluir los permisos siguientes en AndroidManifest.xml:

xml
Copiar
Editar
<uses-permission android:name="android.permission.CAMERA"/>
<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
Para Android 13 y posteriores, usa el permiso READ_MEDIA_IMAGES en lugar de READ_EXTERNAL_STORAGE.

Configura FileProvider para compartir imágenes tomadas con la cámara:

xml
Copiar
Editar
<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="com.example.gacha_fit.fileprovider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data
        android:name="android.support.FILE_PROVIDER_PATHS"
        android:resource="@xml/file_paths" />
</provider>
Crea el archivo res/xml/file_paths.xml:

xml
Copiar
Editar
<?xml version="1.0" encoding="utf-8"?>
<paths xmlns:android="http://schemas.android.com/apk/res/android">
    <external-files-path name="images" path="Pictures" />
</paths>
Uso
El fragmento Perfil carga automáticamente el correo y nombre del usuario autenticado.

Se puede cambiar la foto de perfil con cámara o galería.

Se puede actualizar el nombre de usuario validando que no esté repetido en Firebase.

La imagen de perfil se guarda localmente para uso offline.
