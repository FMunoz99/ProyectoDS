// incidente.service.ts
import axios from 'axios';
import { AuthService } from '../Auth/authService';

const BASE_URL = 'http://localhost:8080/incidente';

// DTOs

export interface IncidenteRequestDto {
  piso: string;
  ubicacion: string;
  detalle: string;
  email: string;
  phoneNumber: string;
  description?: string;
  fechaReporte: string; // Usar formato ISO: 'YYYY-MM-DD'
  estadoReporte?: EstadoReporte;
  estadoTarea?: EstadoTarea;
  fotoIncidenteUrl:string;
}

export interface IncidentePatchRequestDto {
  estadoReporte?: EstadoReporte;
  estadoTarea?: EstadoTarea;
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

// Servicio

export class IncidenteService {
  private baseUrl: string = BASE_URL;

  private getToken() {
    return AuthService.getToken();
  }

  async getAllIncidentes(): Promise<IncidenteResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<IncidenteResponseDto[]>(this.baseUrl,{
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching all objetos perdidos:', error);
      throw error;
    }
  }

  async getIncidenteById(id: number): Promise<IncidenteResponseDto> {
    const response = await axios.get<IncidenteResponseDto>(`${this.baseUrl}/${id}`);
    return response.data;
  }

  async createIncidente(requestDto: IncidenteRequestDto): Promise<IncidenteResponseDto> {
    try {
      // Obtener el token (puedes obtenerlo de localStorage, cookies, o de alguna parte de tu aplicación)
      const token = localStorage.getItem('authToken');  // O de tu estado de sesión

      // Si no hay token, puedes lanzar un error o manejarlo de acuerdo a tus necesidades
      if (!token) {
        throw new Error('Token de autenticación no encontrado');
      }

      // Configurar los headers con el token
      const headers = {
        'Authorization': `Bearer ${token}`,
        'Content-Type': 'application/json',
      };

      // Realizar la solicitud POST con los headers de autorización
      const response = await axios.post<IncidenteResponseDto>(this.baseUrl, requestDto, { headers });

      return response.data;
    } catch (error) {
      console.error('Error saving objeto perdido:', error.response?.data || error.message);
      throw new Error('Failed to save objeto perdido');
    }
  }

  async updateIncidenteStatus(id: number, patchDto: IncidentePatchRequestDto): Promise<IncidenteResponseDto> {
    const token = AuthService.getToken();

    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.patch<IncidenteResponseDto>(`${this.baseUrl}/${id}/estado`, patchDto,{
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  async deleteIncidente(id: number): Promise<void> {
  const token = this.getToken();
  if (!token) {
    throw new Error('No autorizado. El token de autenticación no está presente.');
  }

  await axios.delete(`${this.baseUrl}/${id}`, {
    headers: { Authorization: `Bearer ${token}` },
  });
  }
}
