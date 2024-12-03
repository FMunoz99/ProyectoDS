import axios from 'axios';
import { AuthService } from '../Auth/authService';

const BASE_URL = 'http://localhost:8080/empleado';

// DTOs

export interface EmpleadoRequestDto {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  password: string;
}

export interface EmpleadoPatchRequestDto {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
  email?: string;          // Agregado
  password?: string;       // Agregado
  horarioDeTrabajo?: Record<string, string>;
}


export interface EmpleadoResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  horarioDeTrabajo: Record<string, string>;
}

export interface EmpleadoSelfResponseDto {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
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
  fechaReporte?: string;
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


export class EmpleadoService {
  private baseUrl: string = BASE_URL;

  // Helper function to get the token
  private getToken() {
    return AuthService.getToken();
  }

  async getAllEmpleados(): Promise<EmpleadoResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<EmpleadoResponseDto[]>(`${this.baseUrl}/lista`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data; // Devuelve los datos de los empleados
    } catch (error) {
      console.error('Error al obtener la lista de empleados:', error);
      throw error;
    }
  }

  // Fetch a single employee by ID
  async getEmpleado(id: number): Promise<EmpleadoResponseDto> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }
    const response = await axios.get<EmpleadoResponseDto>(`${this.baseUrl}/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Create a new employee
  async createEmpleado(dto: EmpleadoRequestDto): Promise<EmpleadoResponseDto> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }
    const response = await axios.post<EmpleadoResponseDto>(this.baseUrl, dto, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Get the authenticated user's employee information
  async getEmpleadoSelf(): Promise<EmpleadoSelfResponseDto> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.get<EmpleadoSelfResponseDto>(`${this.baseUrl}/me`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Delete an employee by ID
  async deleteEmpleado(id: number): Promise<void> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    await axios.delete(`${this.baseUrl}/${id}`, {
      headers: { Authorization: `Bearer ${token}` },
    });
  }

  // Update employee information
  async updateEmpleadoInfo(id: number, empleadoInfo: EmpleadoPatchRequestDto): Promise<EmpleadoResponseDto> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }
    console.log('Empleado ID:', id);
    console.log('Datos:', empleadoInfo);
    const response = await axios.patch<EmpleadoResponseDto>(`${this.baseUrl}/${id}`, empleadoInfo, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }


  // Get incidents assigned to the authenticated employee
  async getIncidentesAsignados(): Promise<IncidenteResponseDto[]> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.get<IncidenteResponseDto[]>(`${this.baseUrl}/me/incidentes`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }

  // Get lost objects assigned to the authenticated employee
  async getObjetosPerdidosAsignados(): Promise<ObjetoPerdidoResponseDto[]> {
    const token = this.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.get<ObjetoPerdidoResponseDto[]>(`${this.baseUrl}/me/objetos-perdidos`, {
      headers: { Authorization: `Bearer ${token}` },
    });
    return response.data;
  }
}
