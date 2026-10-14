# BuyABeer 🍺

Aplicación Android para pedir cervezas desde el móvil, desarrollada como Trabajo de Fin de Grado.

El usuario navega por un catálogo de cervezas organizado por categorías, consulta el detalle de cada producto, las añade a la cesta, aplica códigos de descuento y confirma el pedido, que se guarda en la nube y se muestra en forma de ticket.

![Android CI](https://github.com/eliasamarillo1/TFG/actions/workflows/android.yml/badge.svg)

## Funcionalidades

- **Pantalla de inicio** y **panel principal** con banner, categorías, productos populares, buscador y un "explorador" que sugiere una cerveza al azar.
- **Detalle de producto** con descripción, valoración, precio y selección de cantidad.
- **Cesta de la compra**: sumar, restar y eliminar productos, códigos de descuento validados contra Firebase y cálculo de IVA.
- **Ticket** con el código del pedido, que se sube a Firebase.
- **Último pedido**: consulta del pedido más reciente guardado en el dispositivo.

## Stack técnico

| Ámbito | Tecnología |
|---|---|
| Lenguaje | Kotlin (y una clase de utilidades en Java) |
| Plataforma | Android, `minSdk 24`, `targetSdk 35` |
| Interfaz | Layouts XML con ViewBinding, RecyclerView, Material Components |
| Arquitectura | Activities + ViewModel y LiveData (`DashViewModel`) + repositorio (`DashBoardRepo`) |
| Datos remotos | Firebase Realtime Database (nodos `Banner`, `Category`, `Popular`, `Items`, `Descuentos`, `Pedidos`) |
| Persistencia local | `SharedPreferences` a través de `TinyDB` (cesta y último pedido) |
| Imágenes | Glide |
| Monitorización | Firebase Crashlytics |
| Build | Gradle (Kotlin DSL) con catálogo de versiones (`gradle/libs.versions.toml`) |
| Tests | JUnit 4 |
| Integración continua | GitHub Actions |

## Estructura del proyecto

```
app/src/main/java/com/example/buyacoffee/
├── activity/      Pantallas (Inicio, DashBoard, Detail, ItemsList, Cart, Ticket, LastOrder)
├── adapter/       Adaptadores de RecyclerView
├── Helper/        Cesta (ManagmentCar), cálculo de la cesta (CartCalculator), TinyDB
├── model/         Modelos de datos (ItemsModel, CategoryModel, BannerModel)
├── repositorio/   Acceso a Firebase (DashBoardRepo)
└── viewmodel/     DashViewModel
app/src/test/      Tests unitarios (JVM)
```

## Cómo ejecutar

### Requisitos

- [Android Studio](https://developer.android.com/studio) reciente (incluye el JDK 17 necesario).
- Android SDK con la plataforma 35 instalada.
- Un emulador o un móvil Android 7.0 (API 24) o superior.

### Pasos

1. Clona el repositorio:
   ```bash
   git clone https://github.com/eliasamarillo1/TFG.git
   ```
2. Ábrelo en Android Studio (`File > Open`) y espera a que Gradle sincronice.
3. Pulsa **Run ▶** con un emulador o dispositivo seleccionado.

La configuración de Firebase (`app/google-services.json`) ya está incluida, así que la app se conecta a la base de datos del proyecto sin pasos adicionales.

### Desde la línea de comandos

```bash
./gradlew assembleDebug         # genera app/build/outputs/apk/debug/app-debug.apk
./gradlew testDebugUnitTest     # ejecuta los tests unitarios
```

En Windows usa `gradlew.bat` en lugar de `./gradlew`.

## Tests

Los tests unitarios están en `app/src/test/` y se ejecutan en la JVM, sin emulador.

`CartCalculatorTest` cubre el cálculo de la cesta: subtotal, descuento en porcentaje, IVA, total y redondeo a dos decimales. Para poder probarlo, la lógica se ha separado de la interfaz en `Helper/CartCalculator.kt`, que no depende de Android.

## Integración continua

El workflow [`.github/workflows/android.yml`](.github/workflows/android.yml) se ejecuta en cada push a `master` y en cada pull request. Compila la app en modo debug y pasa los tests unitarios. El informe de tests se guarda como artefacto de la ejecución.
