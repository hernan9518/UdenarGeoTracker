# GeoTracker 📍

Aplicación nativa para Android desarrollada en Kotlin que integra el SDK de Google Maps para el rastreo de ubicación en tiempo real, cálculo de distancias geodésicas y estimación matemática de tiempos de llegada peatonales.

## Características Principales
* **Ubicación en Tiempo Real:** Integración de `FusedLocationProviderClient` para centrar la cámara dinámicamente en la ubicación exacta del dispositivo.
* **Interactividad y Marcadores:** Inserción de puntos de interés (Pines) mediante eventos `onMapLongClick`.
* **Cálculos Locales en Dispositivo:** Uso de `Location.distanceBetween` para calcular la distancia métrica exacta sobre el modelo elipsoidal de la Tierra, sin consumir cuotas de APIs de enrutamiento externas.
* **Estimación de Tiempo (ETA):** Procesamiento matemático local para calcular el tiempo de llegada caminando, basado en la velocidad peatonal promedio (83.3 m/min).
* **Gestión de Memoria Visual:** Eventos `onInfoWindowClick` para destruir instancias de marcadores y limpiar la interfaz gráfica.

## Tecnologías y Herramientas Utilizadas
* **Lenguaje:** Kotlin
* **Interfaz de Usuario:** XML y Views (SupportMapFragment)
* **SDKs:** Google Maps SDK for Android, Google Play Services Location
* **Plataforma Cloud:** Google Cloud Console (Gestión de API Keys)

## Requisitos de Ejecución
Para compilar y ejecutar este proyecto de forma local:
1. Clonar el repositorio.
2. Generar una API Key válida en Google Cloud con el servicio **Maps SDK for Android** habilitado.
3. Insertar la API Key en el archivo `AndroidManifest.xml` (`<meta-data android:name="com.google.android.geo.API_KEY"...>`).
4. Otorgar permisos de GPS al iniciar la aplicación en el dispositivo físico o emulador.
