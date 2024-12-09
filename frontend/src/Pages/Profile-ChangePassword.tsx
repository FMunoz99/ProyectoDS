import { useState, useEffect } from 'react';
import fernandoImage from '../img/team/fernando_munoz.jpeg';
import { Link } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { AuthService } from '../services/Auth/authService';
import { EstudianteService } from '../services/Student/studentService'; 
import { EmpleadoService, EmpleadoSelfResponseDto } from '../services/Employees/employeeService'; // Servicio para empleados
import { EstudianteSelfResponseDto } from '../services/Student/studentService'; // Importa el DTO de estudiante


const UserProfile_ChangePassword = () => {
  const [role, setRole] = useState<string | null>(null); // Estado para el rol
  const [userInfo, setUserInfo] = useState<EstudianteSelfResponseDto | EmpleadoSelfResponseDto | null>(null); // Estado para la info del usuario

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const userRole = AuthService.getUserInfo()?.role ?? null;
        setRole(userRole); // Establece el rol del usuario
  
        let info: EstudianteSelfResponseDto | EmpleadoSelfResponseDto | undefined;
        if (userRole === 'Estudiante') {
          const studentService = new EstudianteService();
          info = await studentService.getEstudianteOwnInfo(); // Obtiene información del estudiante
        } else if (userRole === 'Empleado') {
          const employeeService = new EmpleadoService();
          info = await employeeService.getEmpleadoSelf(); // Obtiene información del empleado
        }
  
        if (info) {
          setUserInfo(info); // Solo establece la info si no es undefined
        }
      } catch (error) {
        console.error('Error al obtener la información del usuario:', error);
      }
    };
  
    fetchUserInfo(); // Llama a la función cuando el componente se monta
  }, []); 
  

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />

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
            src={role === 'Estudiante' && userInfo && 'fotoPerfilUrl' in userInfo ? userInfo.fotoPerfilUrl : fernandoImage} 
            alt="Usuario" 
            className="w-32 h-32 object-cover rounded-full mb-2" 
          />

            <div className="text-center">
              <h3 className="text-2xl font-semibold">{userInfo?.firstName ? `${userInfo.firstName} ${userInfo.lastName}` : "Nombre Usuario"}</h3>
              <h4 className="text-lg text-gray-500">{role}</h4>
            </div>
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
            <div className="flex mb-1">
              <Link to="/profile/overview" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">
                General
              </Link>

              <Link to="/profile/edit" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">Editar Perfil</Link>
              <Link to="/profile/settings" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">Configuración</Link>

              <a href="/profile/change-password" className="text-lg font-semibold text-blue-500 hover:text-blue-700 mr-10 relative">
                Cambiar Contraseña
                <span 
                  className="absolute -bottom-3 left-0 w-24 h-0.5 bg-blue-500 transition-all duration-300" 
                  style={{ width: '120%', left: '-10%' }}
                ></span>
              </a>
            </div>
            <hr className="my-6" />

            <ul className="text-base space-y-4 ml-4">         
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Contraseña Actual</span>
                <input type="text" className="text-left w-3/4 border rounded p-1 focus:border-blue-500" />
              </li>
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Nueva Contraseña</span>
                <input type="text" className="text-left w-3/4 border rounded p-1 focus:border-blue-500"  />
              </li>
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Reingresa la Nueva Contraseña</span>
                <input type="text" className="text-left w-3/4 border rounded p-1 focus:border-blue-500" />
              </li>
            </ul>

            <div className="flex justify-center mt-4">
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

export default UserProfile_ChangePassword;
