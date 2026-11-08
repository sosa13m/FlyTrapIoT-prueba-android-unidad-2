# FlyTrap IoT

Prototipo funcional para la gestión y monitoreo en terreno de trampas inteligentes para la detección de Mosca de la Fruta (*Ceratitis capitata*).

**Evaluación Final Unidad II — Programación Android (Santo Tomás)**

## Tecnologías
- Java · Android Studio
- SQLite con `SQLiteOpenHelper` (base de datos `flytrap_db`, tabla `trampas`)
- RecyclerView + CardView
- Intents con `putExtra` para traspasar la clave primaria

## Estructura
| Archivo | Rol |
|---|---|
| `DBHelper.java` | Extiende `SQLiteOpenHelper`, crea la BD/tabla y expone el CRUD (insert con `ContentValues`, select, update, delete) |
| `Trampa.java` | Modelo del registro |
| `MainActivity.java` | Listado de trampas (RecyclerView) recargado desde la BD en `onResume` + botón Agregar |
| `TrampaAdapter.java` | Adapter; al presionar un ítem envía su `id` con `putExtra("id_trampa", id)` |
| `AgregarActivity.java` | Formulario de creación (INSERT) |
| `DetalleActivity.java` | Recibe el `id` vía Intent, carga el registro, permite UPDATE y DELETE directo (sin diálogo de confirmación) |

## Esquema
```sql
CREATE TABLE trampas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    codigo_sector TEXT NOT NULL,
    tipo_trampa TEXT NOT NULL,
    nivel_captura INTEGER NOT NULL,
    observaciones TEXT )
```
