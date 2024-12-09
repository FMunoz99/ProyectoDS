import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';  // Importamos useNavigate
import Navbar from '../components/NavBar';
import { EmpleadoService } from '../services/Employees/employeeService';


interface LostItem {
  id: number;
  piso: string;
  ubicacion: string;
  estadoReporte: string;
  estadoTarea: string;
  detalle: string;
}

interface Incident {
  id: number;
  piso: string;
  ubicacion: string;
  estadoReporte: string;
  estadoTarea: string;
  hecho: string;
}

interface User {
  firstName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  // Add any other properties that are part of the employee's data
}

const ViewReportsEmp = () => {
  const { id } = useParams();
  const [user, setUser] = useState<User | null>(null);
  const [lostItems, ] = useState<LostItem[]>([]);
  const [incidents, ] = useState<Incident[]>([]);

  
  const navigate = useNavigate();  // Creamos el hook de navegación

  // Crear una instancia del servicio
  const empleadoService = new EmpleadoService();  

  useEffect(() => {
    // Función para cargar los datos
    const loadData = async () => {
      try {
        // Obtener información del empleado por ID
        const userData = await empleadoService.getEmpleado(Number(id));  
        setUser(userData);

      } catch (error) {
        console.error('Error al obtener los datos:', error);
      }
    };

    // Llamada a la función para cargar los datos
    loadData();
  }, [id]);

  // Función para redirigir al endpoint de usuarios
  const handleGoBack = () => {
    navigate('/users');  // Redirige a la ruta /users
  };

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />
      <main className="flex-1 p-4">
        <h2 className="text-2xl font-bold mb-4">Reportes de Empleado {user?.firstName} {user?.lastName}</h2>

        {/* Información del Empleado */}
        <div className="bg-white p-6 rounded-lg shadow-md mb-4">
          <p><strong>Nombre:</strong> {user?.firstName} {user?.lastName}</p>
          <p><strong>Teléfono:</strong> {user?.phoneNumber}</p>
          <p><strong>Correo Electrónico:</strong> {user?.email}</p>
        </div>

        {/* Objetos Perdidos */}
        <div className="bg-white p-6 rounded-lg shadow-md mb-4">
          <h3 className="text-xl font-semibold mb-2">Objetos Perdidos</h3>
          <table className="min-w-full table-auto">
            <thead>
              <tr>
                <th className="px-4 py-2 text-left">Piso</th>
                <th className="px-4 py-2 text-left">Ubicación</th>
                <th className="px-4 py-2 text-left">Estado del Reporte</th>
                <th className="px-4 py-2 text-left">Estado de la Tarea</th>
                <th className="px-4 py-2 text-left">Detalle</th>
              </tr>
            </thead>
            <tbody>
              {lostItems.map((item) => (
                <tr key={item.id}>
                  <td className="px-4 py-2">{item.piso}</td>
                  <td className="px-4 py-2">{item.ubicacion}</td>
                  <td className="px-4 py-2">{item.estadoReporte}</td>
                  <td className="px-4 py-2">{item.estadoTarea}</td>
                  <td className="px-4 py-2">{item.detalle}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Incidentes */}
        <div className="bg-white p-6 rounded-lg shadow-md">
          <h3 className="text-xl font-semibold mb-2">Incidentes</h3>
          <table className="min-w-full table-auto">
            <thead>
              <tr>
                <th className="px-4 py-2 text-left">Piso</th>
                <th className="px-4 py-2 text-left">Ubicación</th>
                <th className="px-4 py-2 text-left">Estado del Reporte</th>
                <th className="px-4 py-2 text-left">Estado de la Tarea</th>
                <th className="px-4 py-2 text-left">Hecho</th>
              </tr>
            </thead>
            <tbody>
              {incidents.map((incident) => (
                <tr key={incident.id}>
                  <td className="px-4 py-2">{incident.piso}</td>
                  <td className="px-4 py-2">{incident.ubicacion}</td>
                  <td className="px-4 py-2">{incident.estadoReporte}</td>
                  <td className="px-4 py-2">{incident.estadoTarea}</td>
                  <td className="px-4 py-2">{incident.hecho}</td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {/* Botón para regresar */}
        <button 
          onClick={handleGoBack} 
          className="bg-blue-500 text-white py-2 px-4 rounded-md hover:bg-blue-600 mt-4"
        >
          Regresar
        </button>
      </main>
    </div>
  );
};

export default ViewReportsEmp;
