import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Navbar from '../components/NavBar';
import fernandoImage from '../img/team/fernando_munoz.jpeg';
import { AuthService } from '../services/Auth/authService';
import { EstudianteService, EstudiantePatchRequestDto, EstudianteSelfResponseDto } from '../services/Student/studentService';
import { EmpleadoService, EmpleadoPatchRequestDto, EmpleadoSelfResponseDto } from '../services/Employees/employeeService';

const UserProfile_EditProfile = () => {
  const [userInfo, setUserInfo] = useState<EstudianteSelfResponseDto | EmpleadoSelfResponseDto | null>(null);
  const [role, setRole] = useState<string | null>(null);
  const [firstName, setFirstName] = useState<string>('');
  const [lastName, setLastName] = useState<string>('');
  const [address, setAddress] = useState<string>('');
  const [phone, setPhone] = useState<string>('');
  const [email, setEmail] = useState<string>('');
  const [fotoPerfilUrl, setfotoPerfilUrl] = useState<string | File | null>(null);

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const userRole = AuthService.getUserInfo()?.role;
        setRole(userRole);

        let info;

        if (userRole === 'Estudiante') {
          const estudianteService = new EstudianteService();
          info = await estudianteService.getEstudianteOwnInfo();
        } else if (userRole === 'Empleado') {
          const empleadoService = new EmpleadoService();
          info = await empleadoService.getEmpleadoSelf();
        }

        if (info) {  
          setUserInfo(info);
          setFirstName(info.firstName || "");
          setLastName(info.lastName || "");
          setPhone(info.phoneNumber || "");
          setEmail(info.email || "");
          setfotoPerfilUrl(info.fotoPerfilUrl || '');
        } else {
          setUserInfo(null); 
        }
      } catch (error) {
        console.error("Error al obtener la información del usuario:", error);
        setUserInfo(null); 
      }
    };

    fetchUserInfo();
  }, []);

  const handleImageChange = (e: React.ChangeEvent<HTMLInputElement>) => {
    const file = e.target.files?.[0];
    if (file) {
      setfotoPerfilUrl(file);
    }
  };

  const handleSaveChanges = async () => {
    let updateData;

    if (role === 'Estudiante') {
      const estudianteService = new EstudianteService();
      updateData = {
        firstName: firstName,
        lastName: lastName,
        phoneNumber: phone,
        email: email,
      } as EstudiantePatchRequestDto;

      try {
        const updatedInfo = await estudianteService.updateEstudianteInfo(updateData, typeof fotoPerfilUrl === "object" ? fotoPerfilUrl : null);
        console.log("Updated info:", updatedInfo);
        setUserInfo(updatedInfo);
        alert("Información actualizada exitosamente.");
      } catch (error) {
        console.error("Error al actualizar la información del estudiante:", error);
        alert("Hubo un error al actualizar la información.");
      }
    } else if (role === 'Empleado') {
      const empleadoService = new EmpleadoService();
      updateData = {
        firstName: firstName,
        lastName: lastName,
        phoneNumber: phone,
        email: email
      } as EmpleadoPatchRequestDto;
      
      try {
        const updatedInfo = await empleadoService.updateEmpleadoInfo(updateData);
        console.log("Updated info:", updatedInfo);
        setUserInfo(updatedInfo);
        alert("Información actualizada exitosamente.");
      } catch (error) {
        console.error("Error al actualizar la información del empleado:", error);
        alert("Hubo un error al actualizar la información.");
      }
    }
  };

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
            <div className="relative group w-32 h-32 mb-2">
              <label htmlFor="upload-photo" className="cursor-pointer">
                <img
                  src={
                    typeof fotoPerfilUrl === 'object' && fotoPerfilUrl instanceof File
                      ? URL.createObjectURL(fotoPerfilUrl)
                      : typeof fotoPerfilUrl === 'string'
                      ? fotoPerfilUrl
                      : fernandoImage
                  }
                  alt="Foto de perfil"
                  className="w-32 h-32 object-cover rounded-full transition-opacity duration-300 group-hover:opacity-70"
                />
                <div className="absolute inset-0 flex items-center justify-center bg-black bg-opacity-50 rounded-full opacity-0 group-hover:opacity-100 transition-opacity duration-300">
                  <span className="text-white font-semibold">Cambiar Foto</span>
                </div>
              </label>
              <input
                id="upload-photo"
                type="file"
                accept="image/*"
                className="hidden"
                onChange={handleImageChange}
              />
            </div>

            <div className="text-center">
              <h3 className="text-2xl font-semibold">{firstName || "Nombre Usuario"} {lastName || "Apellido Usuario"}</h3>
              <h4 className="text-lg text-gray-500">{role || "Estudiante"}</h4>
            </div>
          </div>

          <div className="bg-white p-4 rounded-lg shadow-md flex-1 flex flex-col">
            <div className="flex mb-1">
              <Link to="/profile/overview" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">General</Link>
              <Link to="/profile/edit" className="text-lg font-semibold text-blue-500 hover:text-blue-700 mr-10 ml-4 relative">
                Editar Perfil
                <span className="absolute -bottom-3 left-0 w-24 h-0.5 bg-blue-500 transition-all duration-300" style={{ width: '120%', left: '-10%' }}></span>
              </Link>
              <Link to="/profile/settings" className="text-lg font-semibold hover:text-blue-500 mr-10 ml-4">Configuración</Link>
              <Link to="/profile/change-password" className="text-lg font-semibold hover:text-blue-500">Cambiar Contraseña</Link>
            </div>
            <hr className="my-6" />

            <ul className="text-base space-y-4 ml-4">
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Nombres</span>
                <input
                  type="text"
                  className="text-left w-3/4 border rounded p-1 focus:border-blue-500"
                  value={firstName}
                  onChange={(e) => setFirstName(e.target.value)}
                />
              </li>
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Apellidos</span>
                <input
                  type="text"
                  className="text-left w-3/4 border rounded p-1 focus:border-blue-500"
                  value={lastName}
                  onChange={(e) => setLastName(e.target.value)}
                />
              </li>
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Ocupación</span>
                <span className="text-left w-3/4 border rounded p-1 focus:border-blue-500">{role}</span>
              </li>
              {role === 'Estudiante' && ( 
                <li className="flex mb-2">
                  <span className="text-left w-1/4 font-semibold">Dirección</span>
                  <input
                    type="text"
                    className="text-left w-3/4 border rounded p-1 focus:border-blue-500"
                    value={address}
                    onChange={(e) => setAddress(e.target.value)}
                  />
                </li>
              )}
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Teléfono</span>
                <input
                  type="text"
                  className="text-left w-3/4 border rounded p-1 focus:border-blue-500"
                  value={phone}
                  onChange={(e) => setPhone(e.target.value)}
                />
              </li>
              <li className="flex mb-2">
                <span className="text-left w-1/4 font-semibold">Correo</span>
                <input
                  type="text"
                  className="text-left w-3/4 border rounded p-1 focus:border-blue-500"
                  value={email}
                  onChange={(e) => setEmail(e.target.value)}
                />
              </li>
            </ul>
          </div>
        </div>

        <div className="flex justify-end mt-4">
          <button className="btn btn-primary" onClick={handleSaveChanges}>Guardar Cambios</button>
        </div>
      </main>
    </div>
  );
};

export default UserProfile_EditProfile;
