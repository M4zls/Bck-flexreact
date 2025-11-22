# 🚀 Flexreact Backend API

Backend API completo para la aplicación de e-commerce Flexreact. Construido con Node.js, Express y Supabase.



## 🚀 Tecnologías

- Node.js
- Express
- Supabase (Base de datos y autenticación)
- CORS




### 📦 Productos (`/api/productos`)

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/api/productos` | ❌ | Listar todos los productos |
| GET | `/api/productos/:id` | ❌ | Obtener producto por ID |
| POST | `/api/productos` | ✅ | Crear producto (Admin) |
| PUT | `/api/productos/:id` | ✅ | Actualizar producto |
| DELETE | `/api/productos/:id` | ✅ | Eliminar producto |

### 🛒 Pedidos (`/api/pedidos`)

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| POST | `/api/pedidos` | ✅ | Crear nuevo pedido |
| GET | `/api/pedidos` | ✅ | Listar pedidos del usuario |
| GET | `/api/pedidos/:id` | ✅ | Obtener pedido específico |
| PATCH | `/api/pedidos/:id/estado` | ✅ | Actualizar estado |

### 👤 Usuarios (`/api/usuarios`)

| Método | Endpoint | Auth | Descripción |
|--------|----------|------|-------------|
| GET | `/api/usuarios/perfil` | ✅ | Obtener perfil del usuario |
| PUT | `/api/usuarios/perfil` | ✅ | Actualizar perfil |



## 📁 Estructura del Proyecto

```
src/
├── config/         # Configuración (Supabase)
├── controllers/    # Controladores de rutas
├── middleware/     # Middleware (auth, errorHandler)
├── routes/         # Definición de rutas
├── services/       # Lógica de negocio
└── server.js       # Punto de entrada
database/
└── schema.sql      # Script SQL para Supabase
```




