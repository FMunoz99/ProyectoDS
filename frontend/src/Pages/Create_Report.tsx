import { useState, useEffect } from 'react';
import incidente from "../img/crearReporte/incidente.png";
import objetoPerdido from "../img/crearReporte/objetoPerdido.png";
import { useNavigate } from "react-router-dom";
import Navbar from '../components/NavBar';
import { EstudianteService } from '../services/Student/studentService';
import { IncidenteService, IncidenteRequestDto, EstadoReporte, EstadoTarea } from '../services/Incidente/incidenteService';
import { ObjetoPerdidoService, ObjetoPerdidoRequestDto } from '../services/ObjetoPerdido/objetoPerdidoService';

const Create_Report = () => {
  const navigate = useNavigate();
  const [reportType, setReportType] = useState('Objeto');
  const [currentImage, setCurrentImage] = useState(objetoPerdido);
  const [formData, setFormData] = useState({
    piso: '',
    ubicacion: '',
    detalle: '',
    description: '',
    fechaReporte: '',
    email: '',
    celular: '',
  });
  const [selectedFile, setSelectedFile] = useState<string | File | null>(null); // Nuevo estado para la foto
  const [imagePreview, setImagePreview] = useState<string | null>(null); // Vista previa de la imagen
  const estudianteService = new EstudianteService();
  const incidenteService = new IncidenteService();
  const objetoPerdidoService = new ObjetoPerdidoService();

  useEffect(() => {
    const fetchEstudianteInfo = async () => {
      try {
        const estudianteInfo = await estudianteService.getEstudianteOwnInfo();
        setFormData(prevFormData => ({
          ...prevFormData,
          email: estudianteInfo.email,
          celular: estudianteInfo.phoneNumber,
        }));
      } catch (error) {
        console.error("Error al obtener la información del estudiante:", error);
      }
    };
    fetchEstudianteInfo();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLSelectElement>) => {
    const { name, value } = e.target;
    setFormData({ ...formData, [name]: value });
  };
  

  const handleFileChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files ? e.target.files[0] : null; // Safe check if files exist
    setSelectedFile(file);
  
    // Crear una URL de vista previa para el archivo
    if (file) {
      const fileUrl = URL.createObjectURL(file);
      setImagePreview(fileUrl);
    }
  };
  

  useEffect(() => {
    setCurrentImage(reportType === 'Objeto' ? objetoPerdido : incidente);
  }, [reportType]);

  const handleSubmit = async (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
  
    if (reportType === 'Objeto') {
      let fotoObjetoPerdidoUrl = '';
      if (selectedFile) {
        fotoObjetoPerdidoUrl = imagePreview || '';
      }
  
      const objetoPerdidoRequest: ObjetoPerdidoRequestDto = {
        piso: formData.piso,
        ubicacion: formData.ubicacion,
        detalle: formData.detalle,
        email: formData.email,
        phoneNumber: formData.celular,
        description: formData.description,
        fechaReporte: formData.fechaReporte,
        estadoReporte: EstadoReporte.PENDIENTE,
        estadoTarea: EstadoTarea.NO_FINALIZADO,
        fotoObjetoPerdidoUrl: fotoObjetoPerdidoUrl,
      };
  
      try {
        const response = await objetoPerdidoService.saveObjetoPerdido(objetoPerdidoRequest, selectedFile as File | null);
        console.log("Reporte de Objeto enviado exitosamente:", response);
        navigate("/reports");
      } catch (error) {
        console.error("Error al enviar el reporte de Objeto:", error);
      }
    } else if (reportType === 'Incidente') {
      let fotoIncidenteUrl = '';
      if (selectedFile) {
        fotoIncidenteUrl = imagePreview || '';
      }
  
      const incidenteRequest: IncidenteRequestDto = {
        piso: formData.piso,
        ubicacion: formData.ubicacion,
        detalle: formData.detalle,
        description: formData.description,
        email: formData.email,
        phoneNumber: formData.celular,
        fechaReporte: formData.fechaReporte,
        estadoReporte: EstadoReporte.PENDIENTE,
        estadoTarea: EstadoTarea.NO_FINALIZADO,
        fotoIncidenteUrl: fotoIncidenteUrl, // Include the incident photo URL
      };
  
      try {
        const response = await incidenteService.createIncidente(incidenteRequest);
        console.log("Reporte de Incidente enviado exitosamente:", response);
        navigate("/reports");
      } catch (error) {
        console.error("Error al enviar el reporte de Incidente:", error);
      }
    }
  };
  
  


  return (
    <div className="bg-[#f5f5ff] min-h-screen flex">
      <Navbar />
      <main className="flex-1 p-4">
        <h2 className="text-2xl font-bold mb-1 ml-4">Nuevo Reporte</h2>
        <div className="flex space-x-4 mb-1 ml-4">
          <span className="text-base font-semibold">Inicio / Reporte / Nuevo Reporte</span>
        </div>

        <div className="flex justify-between mt-4 space-x-6">
          <div className="bg-[#ffffff] rounded-lg shadow-md p-4 w-2/5 ml-14">
            <h3 className="text-lg font-semibold mb-2 text-center">Reporte</h3>

            <div className="flex mb-2 justify-center">
              <button onClick={() => setReportType('Objeto')}
                className={`px-4 py-2 rounded ${reportType === 'Objeto' ? 'bg-red-600 text-white' : 'bg-red-200'}`}>
                Objeto
              </button>
              <button onClick={() => setReportType('Incidente')}
                className={`px-4 py-2 rounded ${reportType === 'Incidente' ? 'bg-blue-600 text-white' : 'bg-blue-200'}`}>
                Incidente
              </button>
            </div>

            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Piso:</label>
              <input type="text" name="piso" value={formData.piso} onChange={handleChange} className="border p-2 w-full" />
            </div>
            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Ubicación:</label>
              <input type="text" name="ubicacion" value={formData.ubicacion} onChange={handleChange} className="border p-2 w-full" />
            </div>
            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Detalle:</label>
              <input type="text" name="detalle" value={formData.detalle} onChange={handleChange} className="border p-2 w-full" />
            </div>

            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Descripción:</label>
              <input type="text" name="description" value={formData.description} onChange={handleChange} className="border p-2 w-full" />
            </div>

            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Fecha de Reporte:</label>
              <input type="date" name="fechaReporte" value={formData.fechaReporte} onChange={handleChange} className="border p-2 w-full" />
            </div>
            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Foto:</label>
              <input type="file" onChange={handleFileChange} className="border p-2 w-full" />
              {/* Mostrar la vista previa de la imagen seleccionada */}
              {imagePreview && <img src={imagePreview} alt="Vista previa" className="mt-2 w-full h-auto" />}
            </div>
            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Celular:</label>
              <input type="text" name="celular" value={formData.celular} className="border p-2 w-full" readOnly />
            </div>
            <div className="mb-2">
              <label className="block text-sm font-semibold mb-1">Email:</label>
              <input type="email" name="email" value={formData.email} className="border p-2 w-full" readOnly />
            </div>

            <div className="text-center mt-4">
              <button className="btn btn-primary w-full" onClick={handleSubmit}>
                Crear Reporte
              </button>
            </div>
          </div>

          <div className="w-2/5">
            <img src={currentImage} alt="Report Image" className="w-full h-auto rounded-lg shadow-md" />
          </div>
        </div>
      </main>
    </div>
  );
};

export default Create_Report;
