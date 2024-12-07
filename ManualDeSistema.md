<div align="center">
  <img src="images/logo_utec.jpg" alt="Logo UTEC" width="150">
</div>

<div align="center">
  <h1><b>Diseño de Software</b></h1>
  <h2>Manual del sistema</h2>
  <p><b>Profesor:</b> Jaime Farfán</p>
  <p><b>Alumno(s):</b></p>
  <p>Muñoz Paucar, Fernando Jose</p>
  <p>Ramírez Encinas, Oscar Gabriel</p>
  <p>Tinco Aliaga, César</p>
  <p><b>Fecha:</b> 06/12/2024</p>
  <p><b>Ciclo:</b> 2024-2</p>
</div>
---

## **1. Introducción**

### **Descripción del Sistema**
**Lost&Found** es una aplicación diseñada para que los estudiantes puedan reportar incidentes y objetos perdidos de manera rápida y eficiente. La plataforma centraliza la información de los reportes, permitiendo a los administradores dar seguimiento y coordinar la devolución de objetos encontrados. Los usuarios pueden detallar características del objeto o incidente, recibir notificaciones sobre el estado de su reporte y acceder a una base de datos organizada. Esto facilita la recuperación de pertenencias y mejora la eficiencia en la gestión de incidentes dentro de la institución.

### **Propósito y Objetivos**
1. Centralización de Reportes
Recopilar y organizar todos los reportes de incidentes y objetos perdidos en una plataforma única, facilitando su gestión y seguimiento.

2. Eficiencia en la Recuperación
Reducir el tiempo y esfuerzo necesario para devolver objetos encontrados a sus propietarios mediante un sistema claro y accesible.

3. Accesibilidad para los Estudiantes
Ofrecer una herramienta digital intuitiva y de fácil uso para que los estudiantes puedan registrar y consultar reportes en tiempo real.

4. Mejora de la Comunicación
Optimizar la interacción entre estudiantes y administradores, proporcionando actualizaciones y notificaciones sobre el estado de los reportes.

5. Fomento de la Organización
Crear un entorno más ordenado y confiable dentro de la institución al minimizar la pérdida de objetos y promover la responsabilidad compartida.

### **Alcance**
1. **Incluye:**
 - Registro de reportes: Permite a los estudiantes ingresar información sobre incidentes y objetos perdidos, como descripción, lugar y fecha.
 - Base de datos centralizada: Organiza los reportes para facilitar la búsqueda y el seguimiento por parte de los administradores.
 - Notificaciones y actualizaciones: Envía información a los estudiantes sobre el estado de sus reportes y objetos encontrados.
 - Coordinación de entregas: Facilita la logística para devolver objetos encontrados a sus propietarios legítimos.
 - Interfaz accesible: Brinda una plataforma intuitiva para su uso tanto por estudiantes como por administradores.
2. **No incluye:**
 - Seguimiento físico de objetos: No realiza rastreo en tiempo real de los objetos extraviados fuera del sistema.
 - Resolución automática: No garantiza la recuperación de todos los objetos reportados, ya que depende de que sean encontrados y entregados.
 - Soporte fuera de la institución: Solo cubre incidentes y objetos perdidos dentro de las instalaciones o áreas definidas por la institución.
 - Gestión de objetos de terceros: No se responsabiliza por objetos que no sean propiedad de estudiantes, como los pertenecientes a visitantes o personal externo.

---

## **2. Requisitos del Sistema**

### **Hardware**
- **Servidor Backend**:
  - Procesador: Intel i5 o superior.
  - RAM: 16 GB.
  - Almacenamiento: 100 GB SSD.
  - Conexión estable a internet con al menos 100 Mbps de velocidad de carga/descarga.
  - GPU: NVIDIA GTX 1650 o superior (recomendado).
- **Dispositivos Cliente**:
  - Navegador moderno (Chrome, Firefox, Edge) en PC, laptop o smartphone.

### **Software**
- **Backend**: Java 11+, Spring Boot.
- **Frontend**: React.js, Node.js 14+.
- **Base de Datos**: PostgreSQL.
- **Herramientas Adicionales**: Docker Desktop, Git y GitHub, Postman.
- **IDE Utilizado**: IntelliJ IDEA (para el desarrollo del backend con el framework Spring Boot).

---

## **3. Instalación**

### **Pasos de Instalación**

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/FMunoz99/ProyectoDS.git
   ```

2. **Ejecutar el backend**:
   a. Abre el proyecto en IntelliJ IDEA.
      ```bash
      idea .\backend\
      ```
   b. Abre el terminal del IDE
   c. Ejecutar el comando: docker run --name dsLostAndFound -e POSTGRES_PASSWORD=postgres -p 5555:5432 -d postgres
   d. Abrir Docker Desktop y ejecutar el nuevo contenedor creado
   e. Crear un archivo .env.local al mismo nivel del directorio /src
   f. Navega a la clase principal de la aplicación en el directorio `backend` anotada con `@BackendApplication`.
   g. Presiona **Mayús + F10** para iniciar el servidor.

5. **Instalar dependencias del frontend**:
   ```bash
   cd Frontend
   npm install
   ```
   
6. **Ejecutar el frontend**:
   ```bash
   npm run dev
   ```

## **4. Interfaz de Usuario**

### **Pantallas del Sistema**
- **Página de Bienvenida**: Opciones para iniciar sesión o registrarse.
- **Página Principal**: Visualización del mapa de la ciudad de Lima y sus camaras disponibles en tiempo real.
- **Panel de Administración**: Gestión de usuarios y cámaras.
- **Panel de Trackings**: Muestra los trackings realizados por un usuario

### **Interacción**
- **Botones**: Iniciar rastreos, gestionar usuarios y cámaras.
- **Formularios**: Reportes y registros.
- **Menús**: Navegación entre secciones.

---

## **5. Funcionalidades del Sistema**

1. **Inicio de Sesión y Registro**:
   - Roles: Tracker, autoridad, peatón y administrador.

2. **Detección de Vehículos**:
   - Algoritmo YOLOv5 para identificar vehículos.

3. **Rastreo en Tiempo Real**:
   - Actualización constante en el mapa interactivo.

---

## **6. Mantenimiento**

1. **Actualizaciones**:
   - Mantener modelos de YOLO actualizados.
   - Actualizar dependencias del backend y frontend regularmente.

2. **Copias de Seguridad**:
   - Configurar respaldos automáticos para la base de datos.

3. **Resolución de Problemas**:
   - **Alta latencia**: Activar GPU en el servidor de visión.
   - **Cámaras no detectadas**: Verificar credenciales y red.

---

## **7. Seguridad**

1. **Autenticación y Autorización**:
   - Basado en JWT para sesiones seguras.
   - Control de acceso según rol.

2. **Recomendaciones**:
   - Cambiar contraseñas predeterminadas.
   - Utilizar HTTPS para comunicación segura.

---

## **8. Glosario**
- **Tracker**: Usuario encargado de iniciar rastreos.
- **YOLO**: Algoritmo de detección de objetos.
- **RTSP**: Protocolo para transmisión de video en tiempo real.

---

## **9. Soporte Técnico**

- **Email**: 
  - fernando.munoz.p@utec.edu.pe
  - oscar.ramirez.e@utec.edu.pe
  - cesar.tinco@utec.edu.pe
- **Repositorio GitHub**: [ProyectoDS](https://github.com/FMunoz99/ProyectoDS.git)


