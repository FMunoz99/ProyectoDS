import React, { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { EstudianteService } from '../services/Student/studentService';

const Reports = () => {
  const navigate = useNavigate();
  const estudianteService = new EstudianteService();

  const [entriesPerPage, setEntriesPerPage] = useState(10);
  const [currentPage, setCurrentPage] = useState(1);
  const [userRole, setUserRole] = useState('student');
  const [reports, setReports] = useState([]);
  const [incidents, setIncidents] = useState([]); // Estado para incidentes
  const [loading, setLoading] = useState(true);
  const [currentView, setCurrentView] = useState('Objetos Perdidos'); // Alterna entre tablas

  // Obtener reportes e incidentes
  useEffect(() => {
    const fetchData = async () => {
      try {
        setLoading(true);
        const lostObjects = await estudianteService.getEstudianteObjetosPerdidos();
        const incidentsData = await estudianteService.getEstudianteIncidentes(); // Servicio para incidentes
        setReports(lostObjects);
        setIncidents(incidentsData);
      } catch (error) {
        console.error('Error al obtener datos:', error);
      } finally {
        setLoading(false);
      }
    };

    fetchData();
  }, []);

  const totalPages = Math.ceil(
    (currentView === 'Objetos Perdidos' ? reports.length : incidents.length) / entriesPerPage
  );

  const handleEntriesChange = (e) => {
    setEntriesPerPage(Number(e.target.value));
    setCurrentPage(1);
  };

  const handlePageChange = (pageNumber) => {
    if (pageNumber > 0 && pageNumber <= totalPages) {
      setCurrentPage(pageNumber);
    }
  };

  const startIndex = (currentPage - 1) * entriesPerPage;
  const currentEntries =
    currentView === 'Objetos Perdidos'
      ? reports.slice(startIndex, startIndex + entriesPerPage)
      : incidents.slice(startIndex, startIndex + entriesPerPage);

  const updateReportStatus = (email, newStatus) => {
    const updatedReports = reports.map((report) => {
      if (report.email === email) {
        return { ...report, estadoReporte: newStatus };
      }
      return report;
    });
    setReports(updatedReports);
  };

  if (loading) {
    return <div>Cargando...</div>;
  }

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />

      <main className="flex-1 p-4">
        <header className="navbar bg-base-100 shadow-lg mb-4">
          <div className="flex items-center">
            <div
              className="toggle-sidebar-btn mr-4"
              style={{
                fontSize: '32px',
                paddingLeft: '10px',
                cursor: 'pointer',
                color: '#012970',
              }}
            >
              &#9776;
            </div>
          </div>
          <div className="flex items-center ml-auto">
            <button className="btn btn-ghost">
              <span className="material-icons">notifications</span>
            </button>
            <span className="badge">3</span>
          </div>
        </header>

        <h2 className="text-2xl font-bold mb-1 ml-4">Tabla de Reportes</h2>
        <div className="flex space-x-4 mb-1 ml-4">
          <span className="text-base font-semibold">Inicio / Reportes</span>
        </div>

        <div className="flex space-x-4 mt-4">
          <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
            <div className="flex justify-between items-center mb-4">
              <a className="text-lg font-semibold hover:text-blue-500">
                {currentView}
              </a>
              <div className="flex space-x-2">
                <button
                  onClick={() =>
                    setCurrentView(
                      currentView === 'Objetos Perdidos'
                        ? 'Incidentes'
                        : 'Objetos Perdidos'
                    )
                  }
                  className="px-4 py-2 bg-[#3661ff] text-white font-semibold rounded-lg hover:bg-blue-500 transition duration-300"
                >
                  {currentView === 'Objetos Perdidos' ? 'Incidentes' : 'Objetos Perdidos'}
                </button>
                <button
                  onClick={() => navigate('/reports/create-report')}
                  className="px-4 py-2 bg-[#3661ff] text-white font-semibold rounded-lg hover:bg-blue-500 transition duration-300"
                >
                  + Nuevo Reporte
                </button>
              </div>
            </div>
            <div className="mb-2">
              <span className="text-base">Reportes</span>
            </div>
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
                  <th className="py-2 px-4">Piso</th>
                  <th className="py-4 px-4">Detalle</th>
                  <th className="py-2 px-4">Estado Reporte</th>
                  {userRole === 'admin' && <th className="py-2 px-4">Acción</th>}
                </tr>
              </thead>
              <tbody>
                {currentEntries.map((entry) => {
                  let statusColor = '';

                  switch (entry.estadoReporte) {
                    case 'PENDIENTE':
                      statusColor = 'bg-yellow-600';
                      break;
                    case 'RECHAZADO':
                      statusColor = 'bg-red-600';
                      break;
                    case 'ACEPTADO':
                      statusColor = 'bg-green-600';
                      break;
                    default:
                      statusColor = '';
                  }

                  return (
                    <tr className="border-t" key={entry.id}>
                      <td className="py-2 px-4">{entry.fechaReporte}</td>
                      <td className="py-2 px-4">{entry.piso}</td>
                      <td className="py-2 px-4">{entry.detalle}</td>
                      <td className="py-2 px-4 flex items-center">
                        <span
                          className={`inline-block w-3 h-3 rounded mr-2 ${statusColor}`}
                        ></span>
                        {entry.estadoReporte}
                      </td>
                      {userRole === 'admin' && (
                        <td className="py-2 px-4">
                          <select
                            onChange={(e) =>
                              updateReportStatus(entry.email, e.target.value)
                            }
                            value={entry.estadoReporte}
                            className="border border-gray-300 rounded p-1"
                          >
                            <option value="PENDIENTE">Pendiente</option>
                            <option value="ACEPTADO">Aceptado</option>
                            <option value="RECHAZADO">Rechazado</option>
                          </select>
                        </td>
                      )}
                    </tr>
                  );
                })}
              </tbody>
            </table>

            {/* Paginación */}
            <div className="flex justify-between items-center mt-4">
              <button
                onClick={() => handlePageChange(currentPage - 1)}
                className="btn btn-outline"
                disabled={currentPage === 1}
              >
                Anterior
              </button>
              <div>
                Página {currentPage} de {totalPages}
              </div>
              <button
                onClick={() => handlePageChange(currentPage + 1)}
                className="btn btn-outline"
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

export default Reports;
