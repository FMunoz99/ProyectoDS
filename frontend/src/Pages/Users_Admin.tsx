import { useState, useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { FiEdit, FiTrash, FiEye } from 'react-icons/fi';
import { EmpleadoService } from '../services/Employees/employeeService';
import { EstudianteService } from '../services/Student/studentService';

export interface EmpleadoResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  horarioDeTrabajo: Record<string, string>;
}

export interface EstudianteResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  fotoPerfilUrl: string;
}

const User = () => {
  const navigate = useNavigate();
  const [userRole, setUserRole] = useState('users');
  const [users, setUsers] = useState<EstudianteResponseDto[]>([]);
  const [employees, setEmployees] = useState<EmpleadoResponseDto[]>([]);

  useEffect(() => {
    const fetchEmployees = async () => {
      try {
        const service = new EmpleadoService();
        const employeeData = await service.getAllEmpleados();
        setEmployees(employeeData);
      } catch (error) {
        console.error('Error al obtener empleados:', error);
      }
    };

    fetchEmployees();
  }, []);

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const service = new EstudianteService();
        const studentData = await service.getAllEstudiantes();
        setUsers(studentData);
        console.log(studentData);
      } catch (error) {
        console.error('Error al obtener estudiantes:', error);
      }
    };

    fetchStudents();
  }, []);

  const handleAddUser = () => {
    navigate('/users/create-user');
  };

  const handleAddEmployee = () => {
    navigate('/users/create-employee');
  };

  const handleAddAdmin = () => {
    navigate('/users/create-admin');
  };



  const handleDeleteUser = async (id: number) => {
    const confirmed = window.confirm('¿Está seguro de que desea eliminar este usuario? Esta acción es irreversible.');
    if (confirmed) {
      try {
        const service = new EstudianteService();
        await service.deleteEstudiante(id);
        setUsers(users.filter(user => user.id !== id));
      } catch (error) {
        console.error('Error al eliminar el estudiante:', error);
      }
    }
  };

  const handleUpdateEmployee = (id: number) => {
    navigate(`/users/edit/${id}`);
  };
  
  const handleDeleteEmployee = async (id: number) => {
    const confirmed = window.confirm('¿Está seguro de que desea eliminar este empleado? Esta acción es irreversible.');
    if (confirmed) {
      try {
        const service = new EmpleadoService();
        await service.deleteEmpleado(id);
        setEmployees(employees.filter((employee) => employee.id !== id));
      } catch (error) {
        console.error('Error al eliminar el empleado:', error);
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

        <h2 className="text-2xl font-bold mb-1 ml-4">Estudiantes y Empleados</h2>
        <div className="flex space-x-4 mb-1 ml-4">
          <span className="text-base font-semibold">Inicio / Tareas</span>
        </div>

        <div className="flex items-center space-x-4 mb-4 ml-4">
          {userRole === 'employees' && (
            <button onClick={() => setUserRole('users')} className="btn bg-blue-500 text-white">
              Ver Estudiantes
            </button>
          )}
          {userRole === 'users' && (
            <button onClick={() => setUserRole('employees')} className="btn bg-blue-500 text-white">
              Ver Empleados
            </button>
          )}
        </div>

        <div className="flex space-x-4 mt-4">
          {userRole === 'users' && (
            <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
              <h3 className="text-lg font-semibold">Estudiantes</h3>
              <div className="flex justify-end mb-4">
                <button onClick={handleAddUser} className="btn bg-green-500 text-white mr-2">
                  Crear Estudiante
                </button>
                <button onClick={handleAddEmployee} className="btn bg-blue-500 text-white mr-2">
                  Crear Empleado
                </button>
                <button onClick={handleAddAdmin} className="btn bg-red-500 text-white">
                  Crear Administrador
                </button>
              </div>
              <table className="min-w-full bg-white">
                <thead>
                  <tr className="w-full text-left text-sm text-black uppercase tracking-wider">
                    <th className="py-2 px-3">ID</th>
                    <th className="py-2 px-3">Nombre</th>
                    <th className="py-2 px-3">Apellido</th>
                    <th className="py-2 px-3">Teléfono</th>
                    <th className="py-2 px-3">Correo</th>
                    <th className="py-2 px-3">Foto</th>
                    <th className="py-2 px-3">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {users.map((user, index) => (
                    <tr key={index} className="border-t">
                      <td className="py-2 px-3">{user.id}</td>
                      <td className="py-2 px-3">{user.firstName}</td>
                      <td className="py-2 px-3">{user.lastName}</td>
                      <td className="py-2 px-3">{user.phoneNumber}</td>
                      <td className="py-2 px-3">{user.email}</td>
                      <td className="py-2 px-3">
                      <img
                        src={user.fotoPerfilUrl || 'https://via.placeholder.com/150'}
                        alt={`${user.firstName} ${user.lastName}`}
                        className="w-10 h-10 object-cover rounded-full"
                        onError={(e) => (e.currentTarget.src = 'https://via.placeholder.com/150')}
                      />
                      </td>

                      <td className="py-2 px-3 flex items-center space-x-2">
                        <FiTrash onClick={() => handleDeleteUser(user.id)} className="text-red-600 cursor-pointer" />
                        <FiEye onClick={() => navigate(`/users/reports/estudiante/${user.id}`)} className="text-green-600 cursor-pointer" />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}

          {userRole === 'employees' && (
            <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
              <h3 className="text-lg font-semibold">Empleados</h3>
              <div className="flex justify-end mb-4">
                <button onClick={handleAddEmployee} className="btn bg-green-500 text-white mr-2">
                  Crear Empleado
                </button>
                <button onClick={handleAddUser} className="btn bg-blue-500 text-white mr-2">
                  Crear Usuario
                </button>
                <button onClick={handleAddAdmin} className="btn bg-red-500 text-white">
                  Crear Administrador
                </button>
              </div>
              <table className="min-w-full bg-white">
                <thead>
                  <tr className="w-full text-left text-sm text-black uppercase tracking-wider">
                    <th className="py-2 px-3">ID</th>
                    <th className="py-2 px-3">Nombre</th>
                    <th className="py-2 px-3">Apellido</th>
                    <th className="py-2 px-3">Teléfono</th>
                    <th className="py-2 px-3">Correo</th>
                    <th className="py-2 px-3">Horario de Trabajo</th>
                    <th className="py-2 px-3">Acciones</th>
                  </tr>
                </thead>
                <tbody>
                  {employees.map((employee) => (
                    <tr key={employee.id} className="border-t">
                      <td className="py-2 px-3">{employee.id}</td>
                      <td className="py-2 px-3">{employee.firstName}</td>
                      <td className="py-2 px-3">{employee.lastName}</td>
                      <td className="py-2 px-3">{employee.phoneNumber}</td>
                      <td className="py-2 px-3">{employee.email}</td>
                      <td className="py-2 px-3">{Object.values(employee.horarioDeTrabajo).join(', ')}</td>
                      <td className="py-2 px-3 flex items-center space-x-2">
                        <FiEdit onClick={() => handleUpdateEmployee(employee.id)} className="text-yellow-600 cursor-pointer" />
                        <FiTrash onClick={() => handleDeleteEmployee(employee.id)} className="text-red-600 cursor-pointer" />
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          )}
        </div>
      </main>
    </div>
  );
};

export default User;
