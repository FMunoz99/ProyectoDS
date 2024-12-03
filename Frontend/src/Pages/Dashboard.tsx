import React, { useState, useEffect } from 'react';
import { Line } from 'react-chartjs-2';
import { Chart as ChartJS, LineElement, PointElement, LinearScale, CategoryScale } from 'chart.js';
import Navbar from '../components/NavBar';
import { AdminService } from '../services/Admin/adminService';

ChartJS.register(LineElement, PointElement, LinearScale, CategoryScale);

const Dashboard = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [last7Days, setLast7Days] = useState([]);
  const [OpHoy, setOpHoy] = useState([0]);
  const [InHoy, setInHoy] = useState([0]);
  const [objetosPerdidos, setObjetosPerdidos] = useState<number[]>([]);
  const [incidentes, setIncidentes] = useState<number[]>([]);
  const [objetosPerdidosAceptados, setObjetosPerdidosAceptados] = useState<any[]>([]);

  useEffect(() => {
    const fetchReportes = async () => {
      const adminService = new AdminService();
      const reportes = await adminService.getReportesGenerales();

      setObjetosPerdidos(reportes[0].slice(-7));
      setIncidentes(reportes[1].slice(-7));
      setOpHoy(reportes[0].slice(-1));
      setInHoy(reportes[1].slice(-1));
      console.log("Reportes en Gráfica:", objetosPerdidos)
      console.log("Data de todos los Objetos Perdidos",reportes[0])
    };

    const fetchObjetosPerdidosAceptados = async () => {
      try {
        const adminService = new AdminService();
        const dataObj = await adminService.getObjetosPerdidosAceptados();
        setObjetosPerdidosAceptados(dataObj);
      } catch (error) {
        console.error('Error fetching accepted lost items:', error);
      }
    };

    fetchReportes();
    fetchObjetosPerdidosAceptados();
    setLast7Days(getLast7Days());
  }, []);

  const getLast7Days = () => {
    const days = [];
    const today = new Date();

    for (let i = 6; i >= 0; i--) {
      const day = new Date(today);
      day.setDate(today.getDate() - i);
      const dayString = day.toLocaleDateString('es-PE', { weekday: 'short', day: '2-digit', month: 'short' });
      days.push(dayString);
    }

    return days;
  };

  const handleSearchChange = (e) => {
    setSearchTerm(e.target.value);
  };

  const options = {
    responsive: true,
    scales: {
      y: {
        beginAtZero: true,
        ticks: {
          stepSize: 2,
          padding: 4,
        },
        grid: {
          borderDash: [2, 2],
        },
      },
      x: {
        grid: {
          display: false,
        },
      },
    },
  };

  const data = {
    labels: last7Days,
    datasets: [
      {
        label: 'Objetos Perdidos',
        data: objetosPerdidos,
        borderColor: '#3b82f6',
        backgroundColor: 'rgba(59, 130, 246, 0.2)',
        tension: 0.2,
        fill: true,
      },
      {
        label: 'Incidentes',
        data: incidentes,
        borderColor: '#22c55e',
        backgroundColor: 'rgba(34, 197, 94, 0.2)',
        tension: 0.2,
        fill: true,
      },
    ],
  };

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />

      <main className="flex-1 p-4 flex">
        <div className="flex-1 flex flex-col space-y-4">
          <h1 className="text-3xl font-bold mb-4">Tablero</h1>

          <div className="flex space-x-4 mb-4">
            <div className="flex-1">
              <div className="bg-white p-4 rounded-lg shadow-md flex items-center justify-between">
                <div className="text-lg font-bold">Objetos Perdidos | Hoy</div>
                <div className="w-8 h-8 bg-blue-500 rounded-full flex items-center justify-center text-white font-bold">{OpHoy}</div>
              </div>
            </div>
            <div className="flex-1">
              <div className="bg-white p-4 rounded-lg shadow-md flex items-center justify-between">
                <div className="text-lg font-bold">Incidentes | Hoy</div>
                <div className="w-8 h-8 bg-green-500 rounded-full flex items-center justify-center text-white font-bold">{InHoy}</div>
              </div>
            </div>
            <div className="flex-1">
              <div className="bg-white p-4 rounded-lg shadow-md flex items-center justify-between">
                <div className="text-lg font-bold">Reportes | Hoy</div>
                <div className="w-8 h-8 bg-orange-500 rounded-full flex items-center justify-center text-white font-bold">{Number(OpHoy) + Number(InHoy)}</div>
              </div>
            </div>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-md mb-4">
            <h2 className="text-2xl font-bold mb-4">Reportes por Día</h2>
            <Line data={data} options={options} height={300} width={600} />

            <div className="flex mt-4">
              {data.datasets.map((dataset, index) => (
                <div key={index} className="flex items-center mr-6">
                  <div
                    className="w-4 h-4 rounded-full"
                    style={{ backgroundColor: dataset.borderColor }}
                  />
                  <span className="ml-2">{dataset.label}</span>
                </div>
              ))}
            </div>
          </div>

          <div className="bg-white p-6 rounded-lg shadow-md mb-4">
            <h2 className="text-2xl font-bold mb-4">Objetos Perdidos</h2>

            <input
              type="text"
              placeholder="Buscar objeto..."
              value={searchTerm}
              onChange={handleSearchChange}
              className="mb-4 p-2 border rounded w-full"
            />

            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-gray-100">
                  <th className="border-b-2 pb-2">Fecha</th>
                  <th className="border-b-2 pb-2">Objeto</th>
                  <th className="border-b-2 pb-2">Piso</th>
                  <th className="border-b-2 pb-2">Ubicación</th>
                  <th className="border-b-2 pb-2">Descripción</th>
                </tr>
              </thead>
              <tbody>
                {objetosPerdidosAceptados
                  .filter(objeto => objeto.description.toLowerCase().includes(searchTerm.toLowerCase())) // Filtro por búsqueda
                  .map((objeto) => (
                    <tr key={objeto.id}>
                      <td className="border-b px-4 py-2">{objeto.fechaReporte}</td>
                      <td className="border-b px-4 py-2">{objeto.description}</td>
                      <td className="border-b px-4 py-2">{objeto.piso}</td>
                      <td className="border-b px-4 py-2">{objeto.ubicacion}</td>
                      <td className="border-b px-4 py-2">{objeto.detalle}</td>
                    </tr>
                  ))}
              </tbody>
            </table>
          </div>
        </div>

        <div className="w-96 bg-white p-6 rounded-lg shadow-md ml-4 mt-16">
          <h2 className="text-2xl font-bold mb-4">Noticias</h2>
          <ul>
            <li className="mb-2">Noticia 1: Se encontraron objetos perdidos...</li>
            <li className="mb-2">Noticia 2: Nuevos incidentes reportados...</li>
            <li className="mb-2">Noticia 3: Se ha actualizado el sistema de reportes...</li>
          </ul>
        </div>
      </main>
    </div>
  );
};

export default Dashboard;
