import { useState } from 'react';
import { AuthService, RegisterRequestDto, AuthResponseDto } from '../services/Auth/authService';  // Asegúrate de importar AuthService desde el archivo correcto

const Register = () => {
  const [formData, setFormData] = useState<RegisterRequestDto>({
    firstName: '',
    lastName: '',
    email: '',
    phone: '',
    isEmpleado: false,  // Si tienes este campo en tu backend, defínelo como false por defecto
    password: '',       // Asegúrate de agregar el campo password
  });

  const handleInputChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const { name, value } = e.target;
    setFormData(prevData => ({ ...prevData, [name]: value }));
  };

  const validateForm = () => {
    const { firstName, lastName, email, phone, password } = formData;
    return firstName && lastName && email && phone && password; // Asegúrate de validar el password también
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!validateForm()) {
      alert("Por favor, complete todos los campos requeridos.");
      return;
    }

    try {
      const response: AuthResponseDto = await AuthService.register(formData);  // Llamando al servicio AuthService
      console.log('Registro exitoso:', response.message);
      alert("Cuenta creada con éxito");
      
      // Puedes redirigir al usuario o limpiar el formulario después del registro
      setFormData({
        firstName: '',
        lastName: '',
        email: '',
        phone: '',
        isEmpleado: false,
        password: '',  // Asegúrate de limpiar el password también
      });

    } catch (error) {
      console.error('Error al registrar el usuario:', error);
      alert("Ocurrió un error al registrar la cuenta");
    }
  };

  return (
    <div className="flex min-h-screen items-center justify-center bg-base-200">
      <div className="card w-full max-w-lg shadow-xl bg-base-100">
        <div className="card-body">
          <h2 className="text-center text-2xl font-bold">Crear una Cuenta</h2>
          <p className="text-center text-sm mb-6">Ingresa los espacios para crear una cuenta</p>

          <form onSubmit={handleSubmit} className="space-y-4">
            <div className="form-control">
              <label htmlFor="firstName" className="label">
                <span className="label-text">Nombres</span>
              </label>
              <input
                type="text"
                id="firstName"
                name="firstName"
                className="input input-bordered w-full"
                value={formData.firstName}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-control">
              <label htmlFor="lastName" className="label">
                <span className="label-text">Apellidos</span>
              </label>
              <input
                type="text"
                id="lastName"
                name="lastName"
                className="input input-bordered w-full"
                value={formData.lastName}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-control">
              <label htmlFor="phone" className="label">
                <span className="label-text">Número de Teléfono</span>
              </label>
              <input
                type="tel"
                id="phone"
                name="phone"
                className="input input-bordered w-full"
                value={formData.phone}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-control">
              <label htmlFor="email" className="label">
                <span className="label-text">Correo Electrónico</span>
              </label>
              <input
                type="email"
                id="email"
                name="email"
                className="input input-bordered w-full"
                value={formData.email}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-control">
              <label htmlFor="password" className="label">
                <span className="label-text">Contraseña</span>
              </label>
              <input
                type="password"
                id="password"
                name="password"
                className="input input-bordered w-full"
                value={formData.password}
                onChange={handleInputChange}
                required
              />
            </div>

            <div className="form-control mt-4">
              <button className="btn btn-primary w-full" type="submit">Crear Cuenta</button>
            </div>
          </form>

          <p className="text-center text-sm mt-4">
            ¿Ya tienes una cuenta? <a href="/login" className="text-primary">Iniciar sesión</a>
          </p>
        </div>
      </div>
    </div>
  );
};

export default Register;
