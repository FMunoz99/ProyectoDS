import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/NavBar';
import fernandoImage from '../img/team/fernando_munoz.jpeg';
import { EstudianteService } from '../services/Student/studentService';
import { EmpleadoService } from '../services/Employees/employeeService'; // Importa el servicio para Empleados
import { AdminService } from '../services/Admin/adminService';
import { AuthService } from '../services/Auth/authService';

const UserProfile_Overview = () => {
  const [userInfo, setUserInfo] = useState<any | null>(null); // Cambié el tipo para manejar tanto estudiante como empleado y administrador
  const [role, setRole] = useState<string | null>(null); // Estado para el rol

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        // Obtiene el rol del JWT usando AuthService
        const userRole = AuthService.getUserInfo()?.role?? null;
        setRole(userRole); // Guarda el rol en el estado

        if (userRole === 'Estudiante') {
          // Si el rol es estudiante, obtiene la información del estudiante
          const service = new EstudianteService();
          const info = await service.getEstudianteOwnInfo();
          console.log(info);
          setUserInfo(info);

        } else if (userRole === 'Empleado') {
          // Si el rol es empleado, obtiene la información del empleado
          const service = new EmpleadoService();
          const info = await service.getEmpleadoSelf();
          setUserInfo(info);
        } else if (userRole === 'Administrador') {
          // Si el rol es administrador, obtiene la información del administrador
          const service = new AdminService();
          const info = await service.getCurrentAdmin();
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
              <span className="material-icons">notificaciones</span>
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
            <img src={userInfo?.fotoPerfilUrl || fernandoImage} alt="User Avatar" className="w-32 h-32 object-cover rounded-full mb-2" />
            <div className="text-center">
              <h3 className="text-2xl font-semibold">{userInfo?.firstName ? `${userInfo.firstName} ${userInfo.lastName}` : "Nombre Usuario"}</h3>
              <h4 className="text-lg text-gray-500">{role}</h4>
            </div>
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
            <div className="flex mb-1">
              <a
                href="/profile/overview"
                className="text-lg font-semibold text-blue-500 hover:text-blue-700 mr-10 ml-4 relative">
                General
                <span
                  className="absolute -bottom-3 left-0 w-24 h-0.5 bg-blue-500 transition-all duration-300"
                  style={{ width: '120%', left: '-10%' }}
                ></span>
              </a>
              <Link to="/profile/edit" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">Editar Perfil</Link>
              <Link to="/profile/settings" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">Configuración</Link>
              <Link to="/profile/change-password" className="text-lg font-semibold hover:text-blue-500">Cambiar Contraseña</Link>
            </div>
            <hr className="my-2" />

            <h3 className="text-xl font-semibold ml-4">Información Personal</h3>
            <p className="text-base mb-4 ml-4">Bienvenido a tu página de información personal, donde puedes consultar y actualizar tus datos de manera fácil y segura.</p>

            <h3 className="text-xl font-semibold mb-4 ml-4">Detalles del Perfil</h3>
            <ul className="text-base space-y-4 ml-4">
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Nombre Completo</span>
                <span className="text-left w-3/4">{userInfo?.firstName && userInfo?.lastName ? `${userInfo.firstName} ${userInfo.lastName}` : "Nombre Completo"}</span>
              </li>
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Institución</span>
                <span className="text-left w-3/4">Universidad de Ingeniería y Tecnología</span>
              </li>

              {/* Mostrar el rol dependiendo de si es Estudiante, Empleado o Administrador */}
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Ocupación</span>
                <span className="text-left w-3/4">{role || "Ocupación"}</span> {/* Mostrar el rol aquí */}
              </li>

              {/* Dirección y Teléfono dependen del rol */}
              {role === 'Estudiante' ? (
                <>
                  <li className="flex mb-2">
                    <span className="text-left w-1/4 font-semibold">Dirección</span>
                    <span className="text-left w-3/4">{userInfo?.address || "Dirección"}</span>
                  </li>
                  <li className="flex mb-2">
                    <span className="text-left w-1/4 font-semibold">Teléfono</span>
                    <span className="text-left w-3/4">{userInfo?.phoneNumber || "Teléfono"}</span>
                  </li>
                </>
              ) : role === 'Empleado' && (
                <>
                  <li className="flex mb-2">
                    <span className="text-left w-1/4 font-semibold">Teléfono</span>
                    <span className="text-left w-3/4">{userInfo?.phone || "Teléfono"}</span>
                  </li>
                </>
              )}

              {role === 'Administrador' && (
                <>
                  <li className="flex mb-2">
                    <span className="text-left w-1/4 font-semibold">Teléfono</span>
                    <span className="text-left w-3/4">{userInfo?.phone || "Teléfono"}</span>
                  </li>
                </>
              )}

              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Correo Electrónico</span>
                <span className="text-left w-3/4">{userInfo?.email || "Correo Electrónico"}</span>
              </li>
            </ul>
          </div>
        </div>
      </main>
    </div>
  );
};

export default UserProfile_Overview;
