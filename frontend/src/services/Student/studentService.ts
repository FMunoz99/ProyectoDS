import axios from 'axios';
import { AuthService } from '../Auth/authService';

// Configuración de la URL base
const BASE_URL = `${import.meta.env.VITE_BASE_URL}/estudiante`;

// DTOs: Definición de interfaces para garantizar la estructura de los datos
export interface EstudiantePatchRequestDto {
  firstName?: string; // Campo opcional
  lastName?: string; // Campo opcional
  phoneNumber?: string; // Campo opcional
  email?: string; // Campo opcional
  password?:string; // Campo opcional
  fotoPerfilUrl?:string; // Campo opcional
}

export interface EstudianteRequestDto {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

export interface EstudianteResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  phoneNumber: string;
  email: string;
  fotoPerfilUrl: string;
}

export interface EstudianteSelfResponseDto {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  fotoPerfilUrl:string; 
}

export interface IncidenteResponseDto {
  id: number;
  piso: string;
  detalle: string;
  ubicacion: string;
  estadoReporte: EstadoReporte;
  estadoTarea: EstadoTarea;
  email: string;
  phoneNumber: string;
  description?: string;
  estudianteId?: number;
  empleadoId?: number;
  fechaReporte: string;
  fotoIncidenteUrl:string;
}

export interface ObjetoPerdidoResponseDto {
  id: number;
  piso: string;
  ubicacion: string;
  estadoReporte: EstadoReporte;  // O enum, dependiendo del tipo en el backend
  estadoTarea: EstadoTarea;    // O enum, dependiendo del tipo en el backend
  detalle: string;
  email: string;
  phoneNumber: string;
  fechaReporte: string;
  fotoObjetoPerdidoUrl:string;
}

// Enums de Estado
export enum EstadoReporte {
  RECHAZADO = 'RECHAZADO',
  PENDIENTE = 'PENDIENTE',
  ACEPTADO = 'ACEPTADO',
}

export enum EstadoTarea {
  NO_FINALIZADO = 'NO_FINALIZADO',
  FINALIZADO = 'FINALIZADO',
}

// Servicio para las operaciones relacionadas con Estudiante
export class EstudianteService {
  private baseUrl: string = BASE_URL;

  private getToken() {
    return AuthService.getToken();
  }

  async getAllEstudiantes(): Promise<EstudianteResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<EstudianteResponseDto[]>(`${this.baseUrl}/lista`,{
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data; // Devuelve los datos de los estudiantes
    } catch (error) {
      console.error('Error al obtener la lista de estudiantes:', error);
      throw error;
    }
  }

  // Obtener información de un estudiante por ID
  async getEstudiante(id: number): Promise<EstudianteResponseDto> {
    const token = AuthService.getToken();

    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }
    const response = await axios.get<EstudianteResponseDto>(`${this.baseUrl}/${id}`, {
      headers: { Authorization: `Bearer ${token}`}
    });
    return response.data;
  }

  // Obtener la información del estudiante autenticado
  async getEstudianteOwnInfo(): Promise<EstudianteSelfResponseDto> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }
    const response = await axios.get<EstudianteSelfResponseDto>(`${this.baseUrl}/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Crear un nuevo estudiante
  async createEstudiante(dto: EstudianteRequestDto): Promise<EstudianteResponseDto> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }
    const response = await axios.post<EstudianteResponseDto>(this.baseUrl, dto, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Actualizar información del estudiante autenticado  // Función para actualizar la información del estudiante con foto de perfil
  async updateEstudianteInfo(
    dto: EstudiantePatchRequestDto,
    fotoPerfil: File | null
  ): Promise<EstudianteResponseDto> {
    const token = AuthService.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    try {
      // Crear un FormData para enviar los datos
      const formData = new FormData();

      // Añadir el JSON del estudiante al FormData
      formData.append('estudiante', JSON.stringify(dto));

      // Si se proporciona una foto de perfil, añadirla al FormData
      if (fotoPerfil) {
        formData.append('fotoPerfil', fotoPerfil);
        console.log(formData);
      }

      // Realizar la solicitud PATCH con el FormData
      const response = await axios.patch<EstudianteResponseDto>(`${BASE_URL}/me`, formData, {
        headers: {
          Authorization: `Bearer ${token}`,
          'Content-Type': 'multipart/form-data',  // Importante para el envío de archivos
        },
      });

      // Retornar la respuesta del servidor
      return response.data;
    } catch (error) {
      console.error("Error al actualizar la información del estudiante:", error);
      throw error;
    }
  }



  // Delete an employee by ID
  async deleteEstudiante(id: number): Promise<void> {
    const token = AuthService.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    await axios.delete(`${this.baseUrl}/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  // Obtener los incidentes asociados al estudiante autenticado
  async getEstudianteIncidentes(): Promise<IncidenteResponseDto[]> {
    const token = AuthService.getToken();

    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.get<IncidenteResponseDto[]>(`${this.baseUrl}/me/incidentes`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Obtener los objetos perdidos asociados al estudiante autenticado
    async getEstudianteObjetosPerdidos(): Promise<ObjetoPerdidoResponseDto[]> {
      const token = AuthService.getToken();
    
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
    
      try {
        const response = await axios.get<ObjetoPerdidoResponseDto[]>(`${this.baseUrl}/me/objetos-perdidos`, {
          headers: { Authorization: `Bearer ${token}` },
        });
        console.log('Objetos perdidos obtenidos:', response.data);  // Verifica los datos recibidos desde el backend
        
        if (response.data.length === 0) {
          console.log('No hay objetos perdidos para este estudiante.');
        }
    
        return response.data;
      } catch (error) {
        console.error('Error al obtener objetos perdidos:', error);
        throw error;
      }
    }
  

  // Reportar un objeto perdido
  async reportarObjetoPerdido(dto: ObjetoPerdidoResponseDto): Promise<ObjetoPerdidoResponseDto> {
    const token = AuthService.getToken();

    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.post<ObjetoPerdidoResponseDto>(`${this.baseUrl}/me/objetos-perdidos`, dto, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Actualizar estado de un objeto perdido
  async actualizarEstadoObjetoPerdido(id: number, estadoReporte: string, estadoTarea: string): Promise<ObjetoPerdidoResponseDto> {
    const token = AuthService.getToken();

    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.patch<ObjetoPerdidoResponseDto>(`${this.baseUrl}/me/objetos-perdidos/${id}`, {
      estadoReporte,
      estadoTarea,
    }, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }
}
