<div align="center">
  <img src="images/image11.jpg" alt="Logo UTEC" width="150">
</div>

<div align="center">
  <h1><b>Diseño de Software</b></h1>
  <h2>Manual del sistema</h2>
  <p><b>Profesor:</b> Jaime Farfán</p>
  <p><b>Alumno(s):</b></p>
  <p>Aguinaga Pizarro, Piero Alessandro - 100%</p>
  <p>Arellano, Jorge - 100%</p>
  <p>Tabraj Morales, Sebastian - 100%</p>
  <p><b>Fecha:</b> 28/10/2024</p>
  <p><b>Ciclo:</b> 2024-2</p>
</div>
---

## **1. Introducción**

### **Descripción del Sistema**
El sistema de rastreo de vehículos es una solución basada en visión por computadora que utiliza cámaras IP para detectar y rastrear vehículos en tiempo real. Este proyecto busca mejorar la seguridad y eficiencia en el seguimiento de vehículos sospechosos o robados, proporcionando datos valiosos para autoridades y usuarios.

### **Propósito y Objetivos**
- Detectar y rastrear vehículos en tiempo real.
- Mejorar la capacidad de respuesta ante incidentes vehiculares.
- Reducir el índice de impunidad en robos vehiculares mediante una herramienta tecnológica eficiente.

### **Alcance**
- **Incluye**: Detección de vehículos, administración de cámaras IP y usuarios.
- **No incluye**: Reportes de incidentes, rastreo en tiempo real, identificación de conductores, acciones legales post-rastreo, y hardware fuera de compatibilidad.

---

## **2. Requisitos del Sistema**

### **Hardware**
- **Servidor Backend**:
  - Procesador: Intel i5 o superior.
  - RAM: 16 GB.
  - Almacenamiento: 100 GB SSD.
- **Servidor de Visión por Computadora**:
  - GPU: NVIDIA GTX 1060 o superior (recomendado).
- **Cámaras IP**:
  - Compatibilidad con RTSP.
  - Resolución mínima: 720p.
- **Dispositivos Cliente**:
  - Navegador moderno (Chrome, Firefox, Edge) en PC, laptop o smartphone.

### **Software**
- **Backend**: Java 11+, Spring Boot.
- **Frontend**: React.js, Node.js 14+.
- **Visión por Computadora**: Python 3.8+, Flask, OpenCV, YOLOv8.
  - **Nota**: No se recomienda usar la última versión de Python, ya que algunas dependencias podrían no ser compatibles. Es preferible utilizar versiones anteriores a Python 3.13.1
- **Base de Datos**: PostgreSQL.
- **Herramientas Adicionales**: Docker, GitHub.
- **IDE Utilizado**: IntelliJ IDEA (para el desarrollo del backend con Spring Boot).

---

## **3. Instalación**

### **Pasos de Instalación**

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/PieroAguinaga/proyectoDS.git
   cd proyectoDS
   ```

2. **Instalar dependencias del computer vision**:
   ```bash
   cd yoloDETECTIOON
   pip install -r requirements.txt
   ```

3. **Ejecutar la aplicacion de computer vision**:
   ```bash
   python /app.py
   ```

4. **Crear y ejecutar un contenedor de docker**:
   ```bash
   docker run --name postgres-db -e POSTGRES_PASSWORD=postgres -p 5555:5432 -d postgres
   ```

5. **Ejecutar el backend**:
   1. Abre el proyecto en IntelliJ IDEA.
      ```bash
      idea .\backend\
      ```
   2. Navega a la clase principal de la aplicación en el directorio `backend` anotada con `@BackendApplication`.
   3. Presiona **Mayús + F10** para iniciar el servidor.

6. **Instalar dependencias del frontend**:
   ```bash
   cd FRONT
   npm install
   ```
   
7. **Ejecutar el frontend**:
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
  - sebastian.tabraj@utec.edu.pe
  - piero.aguinaga@utec.edu.pe
  - jorge.arellano@utec.edu.pe
- **Repositorio GitHub**: [proyectoDS](https://github.com/PieroAguinaga/proyectoDS.git)
- **Prototipo en Figma**: [Figma](https://www.figma.com/design/krOYk4TfxayrRUi2EkPnG1/Untitled?node-id=0-1&node-type=canvas&t=dQD6UwBkkeykYFyn-0)


