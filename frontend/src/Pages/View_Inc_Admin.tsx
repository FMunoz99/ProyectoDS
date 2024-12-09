import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { IncidenteService } from '../services/Incidente/incidenteService';
import { FiEdit } from 'react-icons/fi';
import { AuthService } from '../services/Auth/authService';

interface Incidente {
  piso: string;
  ubicacion: string;
  estadoReporte: EstadoReporte;
  estadoTarea: EstadoTarea;
  detalle: string;
  email: string;
  phoneNumber: string;
  fechaReporte: string;
}



// Enums definidos
export enum EstadoReporte {
  RECHAZADO = 'RECHAZADO',
  PENDIENTE = 'PENDIENTE',
  ACEPTADO = 'ACEPTADO',
}

export enum EstadoTarea {
  NO_FINALIZADO = 'NO_FINALIZADO',
  FINALIZADO = 'FINALIZADO',
}

const View_Inc = () => {
  const { id } = useParams();
  const [user, setUser] = useState<Incidente | null>(null);
  const [isModalOpen, setIsModalOpen] = useState(false);
  const [statusType, setStatusType] = useState<'reporte' | 'tarea'>('reporte');
  const [newStatus, setNewStatus] = useState<string>('');
  const [role, setRole] = useState<string>('');
  const navigate = useNavigate();
  
  const incidenteService = new IncidenteService();

  // Establecer el rol en el estado al cargar la página
  useEffect(() => {
    const userRole = AuthService.getUserInfo()?.role;
    if (userRole) {
      setRole(userRole); // Guarda el rol en el estado
    }

    const loadData = async () => {
      try {
        const incidentData = await incidenteService.getIncidenteById(Number(id));
        setUser(incidentData);
      } catch (error) {
        console.error('Error al obtener los datos:', error);
      }
    };
    loadData();
  }, [id]);

  // Redirección basada en el rol
  const handleGoBack = () => {
    console.log('role', role);
    if (role === 'Empleado') {
      navigate('/tasksEmployee'); // Redirige a /tasksEmployee si el rol es Empleado
    } else {
      navigate('/all-reports'); // Redirige a /all-reports si el rol es admin
    }
  };

  const handleOpenModal = (type: 'reporte' | 'tarea') => {
    setStatusType(type);
    setIsModalOpen(true);
  };

  const handleCloseModal = () => {
    setIsModalOpen(false);
    setNewStatus('');
  };

  const handleUpdateStatus = async () => {
    try {
      const patchDto = {
        [statusType === 'reporte' ? 'estadoReporte' : 'estadoTarea']: newStatus,
      };
      await incidenteService.updateIncidenteStatus(Number(id), patchDto);
      setUser((prevUser) => {
        if (!prevUser) return null; // Retorna null si no hay un valor previo
      
        return {
          ...prevUser,
          [statusType === 'reporte' ? 'estadoReporte' : 'estadoTarea']: newStatus,
        };
      });
      
      
      handleCloseModal();
    } catch (error) {
      console.error('Error al actualizar el estado:', error);
    }
  };

  // Renderizamos el selector según el tipo de estado
  const renderStatusSelector = () => {
    if (statusType === 'reporte') {
      return (
        <select
          value={newStatus}
          onChange={(e) => setNewStatus(e.target.value)}
          className="w-full p-2 border border-gray-300 rounded-md"
        >
          <option value="">Seleccionar estado</option>
          {Object.values(EstadoReporte).map((estado) => (
            <option key={estado} value={estado}>
              {estado}
            </option>
          ))}
        </select>
      );
    } else {
      return (
        <select
          value={newStatus}
          onChange={(e) => setNewStatus(e.target.value)}
          className="w-full p-2 border border-gray-300 rounded-md"
        >
          <option value="">Seleccionar estado</option>
          {Object.values(EstadoTarea).map((estado) => (
            <option key={estado} value={estado}>
              {estado}
            </option>
          ))}
        </select>
      );
    }
  };

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />
      <main className="flex-1 p-6">
        <h2 className="text-3xl font-bold mb-6 text-center text-black-800">
          Detalles del Incidente
        </h2>

        {user ? (
          <div className="space-y-6">
            {/* Información del Incidente */}
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold text-gray-800 mb-4">Información General</h3>
              <div className="grid grid-cols-2 gap-4">
                <p>
                  <strong className="text-gray-600">Piso:</strong> {user.piso}
                </p>
                <p>
                  <strong className="text-gray-600">Ubicación:</strong> {user.ubicacion}
                </p>
                <p>
                  <strong className="text-gray-600">Estado del Reporte:</strong>{' '}
                  <span
                    className={`${
                      user.estadoReporte === 'PENDIENTE'
                        ? 'text-yellow-500'
                        : user.estadoReporte === 'ACEPTADO'
                        ? 'text-green-500'
                        : 'text-red-500'
                    } font-bold`}
                  >
                    {user.estadoReporte}
                    {role === 'Empleado' && (
                      <FiEdit
                        onClick={() => handleOpenModal('reporte')}
                        className="inline-block ml-2 text-blue-500 cursor-pointer hover:text-blue-700"
                      />
                    )}
                  </span>
                </p>
                <p>
                  <strong className="text-gray-600">Estado de la Tarea:</strong>{' '}
                  <span
                    className={`${
                      user.estadoTarea === 'NO_FINALIZADO'
                        ? 'text-red-500'
                        : 'text-green-500'
                    } font-bold`}
                  >
                    {user.estadoTarea}
                    {role === 'Empleado' && (
                      <FiEdit
                        onClick={() => handleOpenModal('tarea')}
                        className="inline-block ml-2 text-blue-500 cursor-pointer hover:text-blue-700"
                      />
                    )}
                  </span>
                </p>
                <p className="col-span-2">
                  <strong className="text-gray-600">Detalle:</strong> {user.detalle}
                </p>
              </div>
            </div>

            {/* Información de Contacto */}
            <div className="bg-white p-6 rounded-lg shadow-md">
              <h3 className="text-xl font-semibold text-gray-800 mb-4">Información de Contacto</h3>
              <p>
                <strong className="text-gray-600">Correo Electrónico:</strong> {user.email}
              </p>
              <p>
                <strong className="text-gray-600">Teléfono:</strong> {user.phoneNumber}
              </p>
              <p>
                <strong className="text-gray-600">Fecha de Reporte:</strong> {user.fechaReporte}
              </p>
            </div>

            {/* Botón para regresar */}
            <div className="flex justify-center">
              <button
                onClick={handleGoBack}
                className="bg-blue-500 text-white py-2 px-6 rounded-md hover:bg-blue-600"
              >
                Regresar
              </button>
            </div>
          </div>
        ) : (
          <p className="text-center text-gray-500">Cargando...</p>
        )}

        {/* Modal para actualizar estado */}
        {isModalOpen && (
          <div className="fixed inset-0 bg-gray-600 bg-opacity-50 flex items-center justify-center">
            <div className="bg-white p-6 rounded-lg shadow-md w-1/3">
              <h3 className="text-xl font-semibold text-gray-800 mb-4">Actualizar Estado</h3>
              {renderStatusSelector()}
              <div className="flex justify-between mt-4">
                <button
                  onClick={handleUpdateStatus}
                  className="bg-green-500 text-white py-2 px-6 rounded-md hover:bg-green-600"
                >
                  Actualizar
                </button>
                <button
                  onClick={handleCloseModal}
                  className="bg-gray-500 text-white py-2 px-6 rounded-md hover:bg-gray-600"
                >
                  Cancelar
                </button>
              </div>
            </div>
          </div>
        )}
      </main>
    </div>
  );
};

export default View_Inc;
