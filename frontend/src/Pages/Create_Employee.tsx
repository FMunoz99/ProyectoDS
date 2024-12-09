import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/NavBar';
import { EmpleadoService } from '../services/Employees/employeeService';

const CreateEmployee = () => {
  const navigate = useNavigate();
  const [formData, setFormData] = useState({
    firstName: '',
    lastName: '',
    email: '',
    phoneNumber: '',
    password: '',
  });

  const [error, setError] = useState<string | null>(null); // Estado para manejar errores
  const [loading, setLoading] = useState<boolean>(false); // Estado para manejar el estado de carga

  const handleChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData({
      ...formData,
      [name]: value,
    });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setLoading(true); // Inicia la carga

    try {
      // Verificación de formato de correo o teléfono, si es necesario
      if (!formData.email || !formData.phoneNumber) {
        setError('El correo electrónico y el teléfono son obligatorios');
        setLoading(false);
        return;
      }

      const empleadoService = new EmpleadoService();
      await empleadoService.createEmpleado(formData);

      // Redirige al usuario en caso de éxito
      navigate('/users');
    } catch (err) {
      console.error('Error al crear el empleado:', err);
      setError("Ocurrió un error al crear el empleado. Inténtalo nuevamente.");
    } finally {
      setLoading(false); // Finaliza la carga
    }
  };

  return (
    <div className="bg-[#f5f5ff] flex min-h-screen">
      <Navbar />
      <main className="flex-1 p-4">
        <h2 className="text-2xl font-bold mb-4">Crear Empleado</h2>
        {error && <p className="text-red-500 mb-4">{error}</p>} {/* Muestra errores */}
        <form onSubmit={handleSubmit} className="bg-white p-6 rounded-lg shadow-md">
          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="firstName">Nombre</label>
            <input
              type="text"
              id="firstName"
              name="firstName"
              value={formData.firstName}
              onChange={handleChange}
              className="input input-bordered w-full"
              required
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="lastName">Apellido</label>
            <input
              type="text"
              id="lastName"
              name="lastName"
              value={formData.lastName}
              onChange={handleChange}
              className="input input-bordered w-full"
              required
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="email">Correo Electrónico</label>
            <input
              type="email"
              id="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              className="input input-bordered w-full"
              required
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="phoneNumber">Teléfono</label>
            <input
              type="text"
              id="phoneNumber"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              className="input input-bordered w-full"
              required
            />
          </div>

          <div className="mb-4">
            <label className="block text-sm font-medium" htmlFor="password">Contraseña</label>
            <input
              type="text"
              id="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              className="input input-bordered w-full"
              required
            />
          </div>

          <div className="flex justify-end">
            <button
              type="submit"
              className="btn bg-green-500 text-white"
              disabled={loading} // Deshabilita el botón mientras se está procesando
            >
              {loading ? 'Creando...' : 'Crear Empleado'}
            </button>
          </div>
        </form>
      </main>
    </div>
  );
};

export default CreateEmployee;
