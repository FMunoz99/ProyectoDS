import axios from 'axios';
import { jwtDecode } from 'jwt-decode';

// DTOs (Data Transfer Objects)
export interface LoginRequestDto {
    email: string;
    password: string;
}

export interface RegisterRequestDto {
    firstName: string;
    lastName: string;
    email: string;
    password: string;
    phone: string;
    isEmpleado: boolean;
}

export interface AuthResponseDto {
    token: string;
    message: string;
}

export interface TestConnectionResponse {
    message: string;
}

// Servicio de autenticación
const API_URL = `${import.meta.env.VITE_BASE_URL}`;

export class AuthService {

    // Método para login
    static async login(data: LoginRequestDto): Promise<AuthResponseDto> {
        const response = await axios.post<AuthResponseDto>(`${API_URL}/auth/login`, data);
        
        // Guardamos el token en el localStorage
        if (response.data.token) {
            localStorage.setItem('authToken', response.data.token);
        }
        
        return response.data;
    }

    // Método para registro
    static async register(data: RegisterRequestDto): Promise<AuthResponseDto> {
        const response = await axios.post<AuthResponseDto>(`${API_URL}/auth/register`, data);
        return response.data;
    }
    
    // Método para probar la conexión
    static async testConnection(): Promise<TestConnectionResponse> {
        const response = await axios.get<TestConnectionResponse>(`${API_URL}/test/connection`);
        return response.data;
    }

    // Método para obtener el token del localStorage
    static getToken(): string | null {
        return localStorage.getItem('authToken');
    }

    // Método para eliminar el token (logout)
    static logout(): void {
        localStorage.removeItem('authToken');
    }

    // Método para obtener la información completa del usuario a partir del JWT
    static getUserInfo(): { email: string, role: string } | null {
        const token = AuthService.getToken();
        if (!token) {
            console.log('No hay token en el localStorage');
            return null;
        }
    
        console.log('Token encontrado:', token); // Muestra el token encontrado
    
        try {
            const decodedToken: any = jwtDecode(token);
            console.log('Token decodificado:', decodedToken); // Aquí vemos el contenido del token
    
            // Función para mapear el rol a un formato más amigable
            const mapRoleToFriendlyName = (role: string) => {
                switch(role) {
                    case 'ROLE_ESTUDIANTE':
                        return 'Estudiante';
                    case 'ROLE_ADMIN':
                        return 'Administrador';
                    case 'ROLE_EMPLEADO':
                        return 'Empleado';
                    default:
                        return 'Usuario';
                }
            };
    
            // Devolvemos solo el correo y el rol
            return {
                email: decodedToken.sub || 'Correo Electrónico',  // Usar 'sub' para correo
                role: mapRoleToFriendlyName(decodedToken.role) // Usamos la función para mapear el rol
            };
        } catch (error) {
            console.error('Error al decodificar el token:', error);
            return null;
        }
    }
}
