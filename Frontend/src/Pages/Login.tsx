import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { AuthService, LoginRequestDto, AuthResponseDto } from '../services/Auth/authService'; // Importa el servicio y DTO

const Login = () => {
  const [email, setEmail] = useState('');  // Cambié 'username' a 'email' para reflejar el DTO correcto
  const [password, setPassword] = useState('');
  const [rememberMe, setRememberMe] = useState(false);
  const [error, setError] = useState('');
  
  const navigate = useNavigate(); // Inicializa useNavigate

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (email === '' || password === '') {
      setError('Por favor, ingresa tu correo electrónico y contraseña.');
      return;
    }

    const loginData: LoginRequestDto = {
      email,
      password,
    };

    try {
      // Llama al servicio de login
      const response: AuthResponseDto = await AuthService.login(loginData);
      console.log('Inicio de sesión exitoso:', response.message);
      setError('');

      // Aquí puedes guardar el token en localStorage o cookies si necesitas mantener la sesión
      localStorage.setItem('authToken', response.token);

      // Redirigir al usuario después de un login exitoso
      navigate('/dashboard');
    } catch (error) {
      console.error('Error al iniciar sesión:', error);
      setError('Correo o contraseña incorrectos. Intenta de nuevo.');
    }
  };

  

  return (
    <div className="flex min-h-screen items-center justify-center bg-base-200">
      <div className="card w-full max-w-md shadow-xl bg-base-100">
        <div className="card-body">
          <h2 className="text-center text-2xl font-bold mb-2">Inicia sesión en tu cuenta</h2>
          <p className="text-center text-sm mb-4">Ingresa tu correo electrónico y contraseña para iniciar sesión</p>

          {error && <div className="alert alert-error mb-4">{error}</div>}

          <form onSubmit={handleSubmit}>
            <div className="form-control mb-4">
              <label htmlFor="email" className="label">
                <span className="label-text">Correo electrónico</span>
              </label>
              <input
                type="email"
                id="email"
                className="input input-bordered w-full"
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                required
              />
            </div>

            <div className="form-control mb-4">
              <label htmlFor="password" className="label">
                <span className="label-text">Contraseña</span>
              </label>
              <input
                type="password"
                id="password"
                className="input input-bordered w-full"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                required
              />
            </div>

            <div className="form-control mb-4 flex flex-row justify-between items-center">
              <label className="cursor-pointer label">
                <input
                  type="checkbox"
                  className="checkbox checkbox-primary"
                  checked={rememberMe}
                  onChange={(e) => setRememberMe(e.target.checked)}
                />
                <span className="label-text ml-2">Recuérdame</span>
              </label>
              <a href="/forgot-password" className="text-sm text-primary">¿Olvidaste tu contraseña?</a>
            </div>

            <div className="form-control">
              <button className="btn btn-primary w-full" type="submit">Iniciar sesión</button>
            </div>
          </form>

          <div className="mt-4 text-center text-sm">
            ¿No tienes una cuenta? <Link to="/register" className="text-primary">Crea una cuenta</Link>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
