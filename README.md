# Cafe Journal App 
Favourite Cafe Manager App, for Mobile App Dev.

## Features:
- Add, edit, and delete café entries
- Image picker to attach café photos
- View binding and clean UI layouts
- JSON-based data storage
- Modular architecture (Activities, Adapters, Models, Stores)

## Project Architecture

app/
 ├── java/org/wit/placemark/
 │   ├── activities/      → UI screens (CafeActivity, CafeListActivity)
 │   ├── adapters/        → RecyclerView adapters (CafeAdapter)
 │   └── models/          → Data models and stores (CafeModel, CafeStore)
 ├── res/                 → Layouts, drawables, menus, and values
 └── build.gradle.kts     → App dependencies and configuration

