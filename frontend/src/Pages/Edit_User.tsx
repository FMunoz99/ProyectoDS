import React, { useState, useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { EmpleadoService } from '../services/Employees/employeeService';

const EditUser = () => {
  const navigate = useNavigate();
  const { id } = useParams();
  
  // Estado para los datos del usuario
  const [user, setUser] = useState({
    firstName: '',
    lastName: '',
    phoneNumber: '',
    email: '',
    password: '',
    horarioDeTrabajo: {} as Record<string, string>, // Para almacenar el horario como un objeto clave-valor
  });

  // Estado temporal para las entradas de horario
  const [nuevoDia, setNuevoDia] = useState('');
  const [horaInicio, setHoraInicio] = useState('');
  const [horaFin, setHoraFin] = useState('');

  useEffect(() => {
    if (id) {
      const empleadoService = new EmpleadoService();
      empleadoService.getEmpleado(parseInt(id))
        .then((data) => {
          setUser({
            firstName: data.firstName,
            lastName: data.lastName,
            phoneNumber: data.phoneNumber,
            email: data.email,
            password: '', // No se debería cargar la contraseña por seguridad
            horarioDeTrabajo: data.horarioDeTrabajo || {}, // Aseguramos que el horario sea un objeto
          });
        })
        .catch((error) => {
          console.error("Error al cargar los datos del empleado:", error);
        });
    }
  }, [id]);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    const { name, value } = e.target;
    setUser({
      ...user,
      [name]: value,
    });
  };

  const handleRemoveHorario = (key: string) => {
    setUser((prevState) => {
      const newHorario = { ...prevState.horarioDeTrabajo };
      delete newHorario[key];
      return { ...prevState, horarioDeTrabajo: newHorario };
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    try {
      const empleadoService = new EmpleadoService();
      await empleadoService.updateEmpleadoInfo(Number(id), {
        ...user,
         // Aseguramos que el horario se guarde correctamente
      });
      navigate('/users');
    } catch (error) {
      console.error("Error al actualizar los datos del empleado:", error);
    }
  };

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />
      <main className="flex-1 p-4">
        <h2 className="text-2xl font-bold mb-4">Editar Empleado</h2>
        <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-md">
          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="firstName">Nombre</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={user.firstName}
              onChange={handleChange}
              className="input input-bordered w-full"
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="lastName">Apellido</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={user.lastName}
              onChange={handleChange}
              className="input input-bordered w-full"
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="phoneNumber">Teléfono</label>
            <input
              type="text"
              id="phoneNumber"
              name="phoneNumber"
              value={user.phoneNumber}
              onChange={handleChange}
              className="input input-bordered w-full"
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="email">Correo Electrónico</label>
            <input
              type="email"
              id="email"
              name="email"
              value={user.email}
              onChange={handleChange}
              className="input input-bordered w-full"
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="password">Contraseña</label>
            <input
              type="password"
              id="password"
              name="password"
              value={user.password}
              onChange={handleChange}
              className="input input-bordered w-full"
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium mb-2">Horario de Trabajo</label>
            {Object.keys(user.horarioDeTrabajo).map((key) => (
              <div key={key} className="mb-2 flex items-center justify-between">
                <div>
                  <label className="block text-sm">{key}</label>
                  <input
                    type="text"
                    name={key}
                    value={user.horarioDeTrabajo[key]}
                    onChange={handleChange}
                    className="input input-bordered w-full"
                  />
                </div>
                <button
                  type="button"
                  onClick={() => handleRemoveHorario(key)}
                  className="btn bg-red-500 text-white ml-2"
                >
                  Eliminar
                </button>
              </div>
            ))}

            {/* Agregar un nuevo horario */}
            <div className="mb-4 flex items-center">
              <select
                value={nuevoDia}
                onChange={(e) => setNuevoDia(e.target.value)}
                className="input input-bordered w-full mr-2"
              >
                <option value="">Selecciona un día</option>
                {['Lunes', 'Martes', 'Miércoles', 'Jueves', 'Viernes', 'Sábado', 'Domingo'].map((dia) => (
                  <option key={dia} value={dia}>
                    {dia}
                  </option>
                ))}
              </select>

              {/* Select para hora de inicio */}
              <select
                value={horaInicio}
                onChange={(e) => setHoraInicio(e.target.value)}
                className="input input-bordered w-full mr-2"
              >
                <option value="">Hora de inicio</option>
                {['08:00', '09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'].map((hora) => (
                  <option key={hora} value={hora}>
                    {hora}
                  </option>
                ))}
              </select>

              {/* Select para hora de fin */}
              <select
                value={horaFin}
                onChange={(e) => setHoraFin(e.target.value)}
                className="input input-bordered w-full"
              >
                <option value="">Hora de fin</option>
                {['09:00', '10:00', '11:00', '12:00', '13:00', '14:00', '15:00', '16:00', '17:00'].map((hora) => (
                  <option key={hora} value={hora}>
                    {hora}
                  </option>
                ))}
              </select>

              <button
                type="button"
                onClick={() => {
                  if (nuevoDia && horaInicio && horaFin) {
                    setUser((prevState) => ({
                      ...prevState,
                      horarioDeTrabajo: {
                        ...prevState.horarioDeTrabajo,
                        [nuevoDia]: `${horaInicio}-${horaFin}`,
                      },
                    }));
                    setNuevoDia('');
                    setHoraInicio('');
                    setHoraFin('');
                  }
                }}
                className="btn bg-blue-500 text-white ml-2"
              >
                Agregar Horario
              </button>
            </div>
          </div>

          <div className="flex justify-end">
            <button type="submit" className="btn bg-green-500 text-white">Guardar Cambios</button>
          </div>
        </form>
      </main>
    </div>
  );
};

export default EditUser;
