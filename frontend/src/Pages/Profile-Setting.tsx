import { useState, useEffect } from 'react';
import fernandoImage from '../img/team/fernando_munoz.jpeg';
import { Link } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { AuthService } from '../services/Auth/authService';
import { EstudianteService } from '../services/Student/studentService';
import { EstudianteSelfResponseDto } from '../services/Student/studentService'; // Importa el DTO
import { EmpleadoService, EmpleadoSelfResponseDto } from '../services/Employees/employeeService'; // Importa el servicio para Empleado

const UserProfile_Setting = () => {
  type UserInfo = EstudianteSelfResponseDto | EmpleadoSelfResponseDto | null;


  const [role, setRole] = useState<string | null>(null); // Nuevo estado para el rol
  const [userInfo, setUserInfo] = useState<UserInfo>(null);
  const [employeeInfo, ] = useState<any | null>(null); // Estado para la información del empleado

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        // Primero obtiene la información del estudiante
        // Obtiene el rol del JWT usando AuthService
        const userRole = AuthService.getUserInfo()?.role ?? null; // Default to null if undefined
        setRole(userRole);
        

        // Si el rol es "Empleado", obtiene la información adicional
        if (userRole === 'Estudiante') {
          // Si el rol es estudiante, obtiene la información del estudiante
          const service = new EstudianteService();
          const info = await service.getEstudianteOwnInfo();
          setUserInfo(info);
        } else if (userRole === 'Empleado') {
          // Si el rol es empleado, obtiene la información del empleado
          const service = new EmpleadoService();
          const info = await service.getEmpleadoSelf();
          setUserInfo(info);
        }

      } catch (error) {
        console.error("Error al obtener la información del usuario:", error);
      }
    };

    fetchUserInfo(); // Llama a la función cuando el componente se monta
  }, []);
  
  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar/>

      <main className="flex-1 p-4">
        <header className="navbar bg-base-100 shadow-lg mb-4">
          <div className="flex items-center">
            <div className="toggle-sidebar-btn mr-4" style={{ fontSize: '32px', paddingLeft: '10px', cursor: 'pointer', color: '#012970' }}>
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

        <h2 className="text-2xl font-bold mb-1 ml-4">Perfil</h2>
        <div className="flex space-x-4 mb-1 ml-4">
          <span className="text-base font-semibold">Inicio / Usuarios / Perfil</span>
        </div>

        <div className="flex space-x-4 mt-4">
          <div className="bg-white p-4 rounded-lg shadow-md flex flex-col items-center w-96 h-60">
          <img
            src={userInfo && 'fotoPerfilUrl' in userInfo ? userInfo.fotoPerfilUrl : fernandoImage}
            alt="Fernando Muñoz"
            className="w-32 h-32 object-cover rounded-full mb-2"
          />
            <div className="text-center">
              <h3 className="text-2xl font-semibold">{userInfo?.firstName ? `${userInfo.firstName} ${userInfo.lastName}` : "Nombre Usuario"}</h3>
              <h4 className="text-lg text-gray-500">{role}</h4>
            </div>
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
            <div className="flex mb-1">
              <Link to="/profile/overview" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">General</Link>
              <a href="/profile/edit" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">Editar Perfil</a>
              <Link to="/profile/settings" className="text-lg font-semibold text-blue-500 hover:text-blue-700 mr-10 ml-4 relative">
                Configuración
                <span className="absolute -bottom-3 left-0 w-24 h-0.5 bg-blue-500 transition-all duration-300" style={{ width: '120%', left: '-10%' }}></span>
              </Link>
              <Link to="/profile/change-password" className="text-lg font-semibold hover:text-blue-500">Cambiar Contraseña</Link>
            </div>
            <hr className="my-6" />

            {/* Sección de notificaciones de correo en línea */}
            <div className="flex items-start mb-4">
              <h3 className="text-xl font-semibold mr-4">Notificaciones Email:</h3>
              <div className="flex flex-col">
                <label className="flex items-center mb-1">
                  <input type="checkbox" className="mr-2 transform scale-125" />
                  Cambios en tu cuenta
                </label>
                <label className="flex items-center mb-1">
                  <input type="checkbox" className="mr-2 transform scale-125" />
                  Información sobre nuevos productos y servicios
                </label>
                <label className="flex items-center mb-1">
                  <input type="checkbox" className="mr-2 transform scale-125" />
                  Ofertas y promociones
                </label>
              </div>
            </div>

            {/* Mostrar los detalles específicos si el rol es Empleado */}
            {role === 'Empleado' && employeeInfo && (
              <div className="mt-6">
                <h3 className="text-xl font-semibold mb-4">Información del Empleado</h3>
                <ul className="text-base space-y-4">
                  <li className="flex mb-2">
                    <span className="text-left w-1/4 font-semibold">Nombre Completo</span>
                    <span className="text-left w-3/4">{employeeInfo?.fullName || "Nombre Completo"}</span>
                  </li>
                  <li className="flex mb-2">
                    <span className="text-left w-1/4 font-semibold">Cargo</span>
                    <span className="text-left w-3/4">{employeeInfo?.position || "Cargo"}</span>
                  </li>
                </ul>
              </div>
            )}

            <div className="flex justify-center mt-6">
              <button className="px-6 py-2 bg-[#3661ff] text-white font-semibold rounded-lg hover:bg-blue-500 transition duration-300">
                Guardar Cambios
              </button>
            </div>
          </div>
        </div>
      </main>
    </div>
  );
};

export default UserProfile_Setting;
