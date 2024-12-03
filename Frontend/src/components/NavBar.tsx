import React, { useState, useEffect } from 'react';
import { Link, useLocation } from 'react-router-dom';
import { AuthService } from '../services/Auth/authService';

const Navbar = () => {
  const [role, setRole] = useState(null); // Estado para guardar el rol del usuario
  const location = useLocation();

  // Obtener el rol del usuario en el useEffect
  useEffect(() => {
    const fetchRole = async () => {
      try {
        const userRole = AuthService.getUserInfo()?.role; // Obtén el rol del usuario
        setRole(userRole); // Guarda el rol en el estado
      } catch (error) {
        console.error("Error al obtener la información del usuario:", error);
      }
    };
    fetchRole(); // Llama a la función para obtener el rol
  }, []);

  // Función para verificar si el enlace está activo
  const isActive = (path) => location.pathname === path;

  // Asegúrate de que el rol esté cargado antes de renderizar
  if (role === null) {
    return null; // Puedes agregar un cargando aquí si prefieres mostrar un loader
  }

  return (
    <aside className="bg-white w-64 p-4 shadow-md">
      <ul className="menu">
        {/* Tablero es accesible para todos los roles */}
        <li className="mb-2 transition duration-300">
          <Link
            to="/dashboard"
            className={isActive('/dashboard')
              ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
              : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
            }
          >
            Tablero
          </Link>
        </li>

        {/* Reportes, solo para el rol Estudiante */}
        {role === 'Estudiante' && (
          <li className="mb-2 transition duration-300">
            <Link
              to="/reports"
              className={isActive('/reports')
                ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
                : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
              }
            >
              Mis Reportes
            </Link>
          </li>
        )}

        {/* Tareas, solo para roles admin*/}
        {role === 'Administrador' && (
          <li className="mb-2 transition duration-300">
            <Link
              to="/all-reports"
              className={isActive('/all-reports')
                ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
                : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
              }
            >
              Reportes
            </Link>
          </li>
        )}

        {/* Mis Tareas, solo para empleados */}
        {role === 'Empleado' && (
          <li className="mb-2 transition duration-300">
            <Link
              to="/tasksEmployee"
              className={isActive('/tasksEmployee')
                ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
                : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
              }
            >
              Mis Tareas
            </Link>
          </li>
        )}

        {/* Usuarios, solo para admin */}
        {role === 'Administrador' && (
          <li className="mb-2 transition duration-300">
            <Link
              to="/users"
              className={isActive('/users')
                ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
                : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
              }
            >
              Usuarios
            </Link>
          </li>
        )}

        {/* Perfil es accesible para todos los roles */}
        <li className="mb-2 transition duration-300">
          <Link
            to="/profile"
            className={isActive('/profile')
              ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
              : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
            }
          >
            Perfil
          </Link>
        </li>

        {/* Equipo es accesible para todos los roles */}
        <li className="mb-2 transition duration-300">
          <Link
            to="/team"
            className={isActive('/team')
              ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
              : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
            }
          >
            Equipo
          </Link>
        </li>

        {/* Cerrar Sesión es accesible para todos los roles */}
        <li className="mb-2 transition duration-300">
          <Link
            to="/login"
            className={isActive('/login')
              ? 'block text-blue-500 font-bold bg-[#f5f5ff] p-2 rounded'
              : 'block text-[#012970] hover:text-[#717ff5] font-bold hover:bg-[#f5f5ff] p-2 rounded'
            }
          >
            Cerrar Sesión
          </Link>
        </li>
      </ul>
    </aside>
  );
};

export default Navbar;
