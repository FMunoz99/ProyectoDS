import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { EmpleadoService } from '../services/Employees/employeeService';
import { ObjetoPerdidoPatchRequestDto, EstadoTarea } from '../services/ObjetoPerdido/objetoPerdidoService';
import { ObjetoPerdidoService } from '../services/ObjetoPerdido/objetoPerdidoService';
import { IncidenteService } from '../services/Incidente/incidenteService';
import { FiEdit, FiTrash, FiEye } from 'react-icons/fi'; // Importamos los íconos de react-icons/fi

const TasksEmployee = () => {
  const navigate = useNavigate();
  const empleadoService = new EmpleadoService();
  const objetoPerdidoService = new ObjetoPerdidoService(); // Crear una instancia aquí
  const incidenteService = new IncidenteService(); // Crear una instancia aquí

  const [entriesPerPage, setEntriesPerPage] = useState(10);
  const [currentPage, setCurrentPage] = useState(1);
  const [userRole, setUserRole] = useState('employee');
  const [lostObjects, setLostObjects] = useState([]);
  const [incidents, setIncidents] = useState([]);
  const [view, setView] = useState('lostObjects'); // Usamos un state para gestionar la vista

  // Obtener los datos de objetos perdidos e incidentes desde la API
  useEffect(() => {
    const fetchData = async () => {
      try {
        const objetosPerdidos = await empleadoService.getObjetosPerdidosAsignados();
        const incidentes = await empleadoService.getIncidentesAsignados();
        setLostObjects(objetosPerdidos);
        setIncidents(incidentes);
      } catch (error) {
        console.error("Error al obtener los datos:", error);
      }
    };
    fetchData();
  }, []);

  const totalPages = Math.ceil((view === 'lostObjects' ? lostObjects : incidents).length / entriesPerPage);

  const handleEntriesChange = (e) => {
    setEntriesPerPage(Number(e.target.value));
    setCurrentPage(1); // Reinicia a la primera página al cambiar el número de entradas
  };

  const handlePageChange = (pageNumber) => {
    if (pageNumber > 0 && pageNumber <= totalPages) {
      setCurrentPage(pageNumber);
    }
  };

  const startIndex = (currentPage - 1) * entriesPerPage;
  const currentEntries = (view === 'lostObjects' ? lostObjects : incidents).slice(startIndex, startIndex + entriesPerPage);

  const updateStatus = async (index: number, newStatus: EstadoTarea) => {
    try {
      const patchDto: ObjetoPerdidoPatchRequestDto = {
        estadoTarea: newStatus,
      };

      // Verifica si la vista es de objetos perdidos
      if (view === 'lostObjects') {
        // Aquí se asegura de que el objeto sea actualizado correctamente
        const objetoPerdido = lostObjects[index];
        await objetoPerdidoService.updateObjetoPerdidoStatus(objetoPerdido.id, patchDto); // Usando la instancia
      } else {
        // Si no es un objeto perdido, usa la función correcta para el tipo de incidente
        const incidente = incidents[index];
        await incidenteService.updateIncidenteStatus(incidente.id, patchDto);
      }

      // Si la actualización es exitosa, actualiza el estado en el frontend
      const updatedEntries = (view === 'lostObjects' ? lostObjects : incidents).map((item, idx) => {
        if (idx === index) {
          return { ...item, estadoTarea: newStatus };
        }
        return item;
      });

      if (view === 'lostObjects') {
        setLostObjects(updatedEntries);
      } else {
        setIncidents(updatedEntries);
      }
    } catch (error) {
      console.error('Error al actualizar el estado:', error);
    }
  };

  const statusColors = {
    "NO_FINALIZADO": "bg-red-600 text-white",
    "FINALIZADO": "bg-green-600 text-white",
  };

  const handleEditClick = (id) => {
    if (view === 'lostObjects') {
      navigate(`/lost-objects/${id}`);
    } else {
      navigate(`/incidents/${id}`);
    }
  };

  const handleDeleteLostObject = async (id: number) => {
    const confirmed = window.confirm('¿Está seguro de que desea eliminar este objeto perdido?');
    if (confirmed) {
      try {
        const service = new ObjetoPerdidoService();
        await service.deleteObjetoPerdido(id);
        setLostObjects(lostObjects.filter((lostObject) => lostObject.id !== id));
      } catch (error) {
        console.error('Error al eliminar el objeto perdido:', error);
      }
    }
  };

  const handleDeleteIncident = async (id: number) => {
    const confirmed = window.confirm('¿Está seguro de que desea eliminar este incidente?');
    if (confirmed) {
      try {
        const service = new IncidenteService();
        await service.deleteIncidente(id);
        setIncidents(incidents.filter((incident) => incident.id !== id));
      } catch (error) {
        console.error('Error al eliminar el incidente:', error);
      }
    }
  };


  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />

      <main className="flex-1 p-4">
        <header className="navbar bg-base-100 shadow-lg mb-4">
          <div className="flex items-center">
            <div className="toggle-sidebar-btn mr-4" style={{ fontSize: '32px', paddingLeft: '10px', cursor: 'pointer', color: '#012970' }}>&#9776;</div>
          </div>
          <div className="flex items-center ml-auto">
            <button className="btn btn-ghost">
              <span className="material-icons">notifications</span>
            </button>
            <span className="badge">3</span>
          </div>
        </header>

        <h2 className="text-2xl font-bold mb-1 ml-4">Tareas Asignadas</h2>
        <div className="flex space-x-4 mb-1 ml-4">
          <span className="text-base font-semibold">Inicio / Tareas</span>
        </div>

        <div className="flex space-x-4 mt-4">
          <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
            <div className="flex justify-between items-center mb-4">
              <a className="text-lg font-semibold hover:text-blue-500">Tus Tareas</a>
              <button onClick={() => setView(view === 'lostObjects' ? 'incidents' : 'lostObjects')} 
                className="px-4 py-2 bg-[#3661ff] text-white font-semibold rounded-lg hover:bg-blue-500 transition duration-300">
                {view === 'lostObjects' ? 'Ver Incidentes' : 'Ver Objetos Perdidos'}
              </button>
            </div>

            {/* Paginación */}
            <div className="flex items-center mb-4">
              <select
                value={entriesPerPage}
                onChange={handleEntriesChange}
                className="border border-gray-300 rounded p-1 mr-2"
              >
                <option value={5}>5</option>
                <option value={10}>10</option>
                <option value={15}>15</option>
                <option value={20}>20</option>
              </select>
              <span className="text-base">Entradas por página</span>
            </div>
            <hr className="my-4" />

            {/* Tabla dinámica */}
            <table className="min-w-full bg-white">
              <thead>
                <tr className="w-full text-left text-sm text-black uppercase tracking-wider">
                  <th className="py-2 px-4">Fecha</th>
                  <th className="py-2 px-4">Descripción</th>
                  <th className="py-2 px-4">Piso</th>
                  <th className="py-2 px-4">Ubicación</th>
                  <th className="py-2 px-4">Acciones</th>
                </tr>
              </thead>
              <tbody>
                {currentEntries.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="py-2 px-4 text-center">No hay tareas disponibles</td>
                  </tr>
                ) : (
                  currentEntries.map((entry, index) => (
                    <tr className="border-t" key={entry.id}>
                      <td className="py-2 px-4">{entry.fechaReporte}</td>
                      <td className="py-2 px-4">{entry.detalle}</td>
                      <td className="py-2 px-4">{entry.piso}</td>
                      <td className="py-2 px-4">{entry.ubicacion}</td>
                      <td className="py-2 px-4 flex items-center">
                        <button className="text-blue-500 hover:text-blue-700" onClick={() => handleEditClick(entry.id)}>
                          <FiEdit className="text-xl" />
                        </button>

                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>

            {/* Paginación */}
            <div className="flex justify-between items-center mt-4">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                className="px-4 py-2 bg-gray-200 text-black rounded-lg"
                disabled={currentPage === 1}
              >
                Anterior
              </button>
              <span className="text-base">{`Página ${currentPage} de ${totalPages}`}</span>
              <button
                onClick={() => handlePageChange(currentPage + 1)}
                className="px-4 py-2 bg-gray-200 text-black rounded-lg"
                disabled={currentPage === totalPages}
              >
                Siguiente
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default TasksEmployee;
