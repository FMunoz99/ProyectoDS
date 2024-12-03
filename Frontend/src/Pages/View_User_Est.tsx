import React, { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { EstudianteService } from '../services/Student/studentService';
import { AdminService } from '../services/Admin/adminService';

const ITEMS_PER_PAGE = 5; // Cantidad de elementos por página

const ViewReportsEst = () => {
  const { id } = useParams();
  const [user, setUser] = useState(null);
  const [lostItems, setLostItems] = useState([]);
  const [incidents, setIncidents] = useState([]);

  const [lostItemsPage, setLostItemsPage] = useState(1);
  const [incidentsPage, setIncidentsPage] = useState(1);

  const navigate = useNavigate();

  const estudianteService = new EstudianteService();
  const adminService = new AdminService();

  useEffect(() => {
    const loadData = async () => {
      try {
        const userData = await estudianteService.getEstudiante(id);
        setUser(userData);

        const lostItemsData = await adminService.getObjetosPerdidosPorEstudiante(id);
        setLostItems(lostItemsData);

        const incidentsData = await adminService.getIncidentesPorEstudiante(id);
        setIncidents(incidentsData);
      } catch (error) {
        console.error('Error al obtener los datos:', error);
      }
    };

    loadData();
  }, [id]);

  const handleGoBack = () => {
    navigate('/users');
  };

  const paginate = (items, page) => {
    const start = (page - 1) * ITEMS_PER_PAGE;
    const end = start + ITEMS_PER_PAGE;
    return items.slice(start, end);
  };

  const totalPages = (items) => Math.ceil(items.length / ITEMS_PER_PAGE);

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />
      <main className="flex-1 p-4">
        <h2 className="text-2xl font-bold mb-4">Reportes de Usuario {user?.firstName} {user?.lastName}</h2>

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
              {paginate(lostItems, lostItemsPage).map((item) => (
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

          <div className="mt-2 flex justify-center">
            <button 
              disabled={lostItemsPage === 1} 
              onClick={() => setLostItemsPage(lostItemsPage - 1)} 
              className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50">
              Anterior
            </button>
            <span className="px-4 py-2">{lostItemsPage} / {totalPages(lostItems)}</span>
            <button 
              disabled={lostItemsPage === totalPages(lostItems)} 
              onClick={() => setLostItemsPage(lostItemsPage + 1)} 
              className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50">
              Siguiente
            </button>
          </div>
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
              {paginate(incidents, incidentsPage).map((incident) => (
                <tr key={incident.id}>
                  <td className="px-4 py-2">{incident.piso}</td>
                  <td className="px-4 py-2">{incident.ubicacion}</td>
                  <td className="px-4 py-2">{incident.estadoReporte}</td>
                  <td className="px-4 py-2">{incident.estadoTarea}</td>
                  <td className="px-4 py-2">{incident.detalle}</td>
                </tr>
              ))}
            </tbody>
          </table>

          <div className="mt-2 flex justify-center">
            <button 
              disabled={incidentsPage === 1} 
              onClick={() => setIncidentsPage(incidentsPage - 1)} 
              className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50">
              Anterior
            </button>
            <span className="px-4 py-2">{incidentsPage} / {totalPages(incidents)}</span>
            <button 
              disabled={incidentsPage === totalPages(incidents)} 
              onClick={() => setIncidentsPage(incidentsPage + 1)} 
              className="px-4 py-2 bg-gray-200 rounded disabled:opacity-50">
              Siguiente
            </button>
          </div>
        </div>

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

export default ViewReportsEst;
