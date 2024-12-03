// objetoPerdido.service.ts
import axios from 'axios';
import { AuthService } from '../Auth/authService';

const BASE_URL = 'http://localhost:8080/objetoPerdido';

// DTOs

export interface ObjetoPerdidoRequestDto {
  piso: string;
  ubicacion: string;
  detalle: string;
  email: string;
  phoneNumber: string;
  description: string;
  fechaReporte: string; // Formato ISO 'YYYY-MM-DD'
  estadoReporte?: EstadoReporte;
  estadoTarea?: EstadoTarea;
  fotoObjetoPerdidoUrl: string;
}

export interface ObjetoPerdidoPatchRequestDto {
  estadoReporte?: EstadoReporte;
  estadoTarea?: EstadoTarea;
}

export interface ObjetoPerdidoResponseDto {
  id: number;
  piso: string;
  ubicacion: string;
  estadoReporte: EstadoReporte;
  estadoTarea: EstadoTarea;
  detalle: string;
  email: string;
  phoneNumber: string;
  fechaReporte: string;
  fotoObjetoPerdidoUrl: string;
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

export class ObjetoPerdidoService {
  private baseUrl: string = BASE_URL;

  // Obtener todos los objetos perdidos
  async getAllObjetosPerdidos(): Promise<ObjetoPerdidoResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<ObjetoPerdidoResponseDto[]>(this.baseUrl, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching all objetos perdidos:', error);
      throw new Error(error.response?.data?.message || 'Error al obtener los objetos perdidos');
    }
  }

  // Obtener un objeto perdido por ID
  async getObjetoPerdidoById(id: number): Promise<ObjetoPerdidoResponseDto> {
    try {
      const response = await axios.get<ObjetoPerdidoResponseDto>(`${this.baseUrl}/${id}`);
      return response.data;
    } catch (error) {
      console.error(`Error fetching objeto perdido with ID ${id}:`, error.response?.data || error.message);
      throw new Error(`Failed to fetch objeto perdido with ID ${id}`);
    }
  }

  // Guardar un nuevo objeto perdido
  async saveObjetoPerdido(
    requestDto: ObjetoPerdidoRequestDto,
    fotoObjetoPerdido?: File | null
  ): Promise<ObjetoPerdidoResponseDto> {
    try {
      // Obtener el token de autenticación
      const token = AuthService.getToken(); // Usamos AuthService para obtener el token
  
      if (!token) {
        throw new Error('Token de autenticación no encontrado');
      }
  
      // Crear el FormData
      const formData = new FormData();
      formData.append('objetoPerdido', JSON.stringify(requestDto)); // Convertir a JSON y agregarlo
      if (fotoObjetoPerdido) {
        formData.append('fotoObjetoPerdido', fotoObjetoPerdido); // Agregar archivo si existe
      }
  
      // Realizar la solicitud
      const response = await axios.post<ObjetoPerdidoResponseDto>(`${BASE_URL}`, formData, {
        headers: {
          Authorization: `Bearer ${token}`, // Encabezado para autenticación
          'Content-Type': 'multipart/form-data',
        },
      });
  
      return response.data; // Devolver la respuesta del servidor
    } catch (error) {
      console.error('Error al guardar el objeto perdido:', error);
      throw error;
    }
  }

  // Actualizar el estado de un objeto perdido
  async updateObjetoPerdidoStatus(id: number, patchDto: ObjetoPerdidoPatchRequestDto): Promise<ObjetoPerdidoResponseDto> {
    const token = AuthService.getToken();

    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    try {
      const response = await axios.patch<ObjetoPerdidoResponseDto>(`${this.baseUrl}/${id}/estado`, patchDto, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error(`Error updating status for objeto perdido with ID ${id}:`, error.response?.data || error.message);
      throw new Error('No se pudo actualizar el estado del objeto perdido');
    }
  }

  // Eliminar un objeto perdido
  async deleteObjetoPerdido(id: number): Promise<void> {
    const token = AuthService.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }
    try {
      await axios.delete(`${this.baseUrl}/${id}`, {
        headers: { Authorization: `Bearer ${token}` },
      });
    } catch (error) {
      console.error(`Error deleting objeto perdido with ID ${id}:`, error.response?.data || error.message);
      throw new Error('No se pudo eliminar el objeto perdido');
    }
  }
}
