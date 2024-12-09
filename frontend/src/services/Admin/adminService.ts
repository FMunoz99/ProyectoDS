// admin.service.ts
import axios from 'axios';
import { AuthService } from '../Auth/authService';

const BASE_URL = `${import.meta.env.VITE_BASE_URL}/admin`;

// DTOs

export interface AdminRequestDto {
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  password: string;
}

export interface AdminPatchRequestDto {
  firstName?: string;
  lastName?: string;
  phoneNumber?: string;
}

export interface AdminResponseDto {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
}

export interface AdminSelfResponseDto {
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
    fechaReporte: string;
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


export class AdminService {
  private baseUrl: string = BASE_URL;

  // Obtener la información del administrador actual
  async getCurrentAdmin(): Promise<AdminSelfResponseDto> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<AdminSelfResponseDto>(`${this.baseUrl}/me`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching current admin:', error);
      throw error;
    }
  }

  // Crear un nuevo administrador
  async createAdmin(requestDto: AdminRequestDto): Promise<AdminResponseDto> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.post<AdminResponseDto>(this.baseUrl, requestDto, {
        headers: { Authorization: `Bearer ${token}`, 'Content-Type': 'application/json' },
      });
      return response.data;
    } catch (error) {
      console.error('Error creating admin:', error);
      throw error;
    }
  }

  // Actualizar la información de un administrador
  async updateAdmin(id: number, patchDto: AdminPatchRequestDto): Promise<AdminResponseDto> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.patch<AdminResponseDto>(`${this.baseUrl}/${id}`, patchDto, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error(`Error updating admin with ID ${id}:`, error);
      throw error;
    }
  }

  // Obtener los reportes de incidentes aceptados
  async getIncidentesAceptados(): Promise<IncidenteResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<IncidenteResponseDto[]>(`${this.baseUrl}/reportes/incidentes/aceptados`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching accepted incidents:', error);
      throw error;
    }
  }

  // Obtener los reportes de objetos perdidos aceptados
  async getObjetosPerdidosAceptados(): Promise<ObjetoPerdidoResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<ObjetoPerdidoResponseDto[]>(`${this.baseUrl}/reportes/objetos-perdidos/aceptados`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching accepted lost items:', error);
      throw error;
    }
  }

  // Obtener reportes de incidentes finalizados
  async getIncidentesFinalizados(): Promise<IncidenteResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<IncidenteResponseDto[]>(`${this.baseUrl}/reportes/incidentes/finalizados`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching finalized incidents:', error);
      throw error;
    }
  }

  // Obtener reportes de objetos perdidos finalizados
  async getObjetosPerdidosFinalizados(): Promise<ObjetoPerdidoResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<ObjetoPerdidoResponseDto[]>(`${this.baseUrl}/reportes/objetos-perdidos/finalizados`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching finalized lost items:', error);
      throw error;
    }
  }

  // Obtener reportes de incidentes no finalizados
  async getIncidentesNoFinalizados(): Promise<IncidenteResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<IncidenteResponseDto[]>(`${this.baseUrl}/reportes/incidentes/no-finalizados`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching non-finalized incidents:', error);
      throw error;
    }
  }

  async getIncidentesPorEstudiante(id: number): Promise<IncidenteResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<IncidenteResponseDto[]>(`${this.baseUrl}/reportes/estudiante/${id}/incidentes`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error(`Error fetching incidents for student ID ${id}:`, error);
      throw error;
    }
  }

  // Obtener reportes de objetos perdidos por ID de estudiante
  async getObjetosPerdidosPorEstudiante(id: number): Promise<ObjetoPerdidoResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<ObjetoPerdidoResponseDto[]>(`${this.baseUrl}/reportes/estudiante/${id}/objetos-perdidos`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error(`Error fetching lost items for student ID ${id}:`, error);
      throw error;
    }
  }
  
  // Obtener reportes de objetos perdidos no finalizados
  async getObjetosPerdidosNoFinalizados(): Promise<ObjetoPerdidoResponseDto[]> {
    try {
      const token = AuthService.getToken();
      if (!token) {
        throw new Error('No autorizado. El token de autenticación no está presente.');
      }
      const response = await axios.get<ObjetoPerdidoResponseDto[]>(`${this.baseUrl}/reportes/objetos-perdidos/no-finalizados`, {
        headers: { Authorization: `Bearer ${token}` },
      });
      return response.data;
    } catch (error) {
      console.error('Error fetching non-finalized lost items:', error);
      throw error;
    }
  }

// admin.service.ts

async getReportesGenerales(): Promise<[number[], number[]]> {
  try {
    const token = AuthService.getToken();
    if (!token) {
      throw new Error('No autorizado. El token de autenticación no está presente.');
    }

    const response = await axios.get<{
      incidentesPorDia: Record<string, number>;
      objetosPerdidosPorDia: Record<string, number>;
    }>(
      `${this.baseUrl}/dashboard/reportes-general`,
      {
        headers: { Authorization: `Bearer ${token}` },
      }
    );

    const incidentesPorDia = response.data.incidentesPorDia;
    const objetosPerdidosPorDia = response.data.objetosPerdidosPorDia;

    // Crear un rango de fechas desde la fecha más antigua hasta ayer (sin incluir hoy)
    const fechas = new Set([
      ...Object.keys(incidentesPorDia),
      ...Object.keys(objetosPerdidosPorDia),
    ]);

    const fechaInicio = new Date(
      Math.min(
        ...Array.from(fechas).map(fecha => new Date(fecha).getTime())
      )
    );

    // Ajustar la fecha actual para el huso horario de Perú (GMT-5)
    const fechaFin = new Date();
    fechaFin.setHours(0, 0, 0, 0); // Resetear horas, minutos, segundos y milisegundos para evitar incluir hoy

    const rangoFechas: string[] = [];
    let fecha = fechaInicio;

    // Generar el rango de fechas hasta ayer (excluyendo hoy)
    while (fecha < fechaFin) {
      rangoFechas.push(fecha.toISOString().split('T')[0]); // Formato YYYY-MM-DD
      fecha.setDate(fecha.getDate() + 1);
    }

    // Rellenar los datos faltantes con 0
    const incidentesArray = rangoFechas.map(date => ({
      date,
      count: incidentesPorDia[date] || 0,
    }));
    const objetosPerdidosArray = rangoFechas.map(date => ({
      date,
      count: objetosPerdidosPorDia[date] || 0,
    }));

    // Extraer los valores ordenados de incidentes y objetos perdidos
    const incidentes = incidentesArray.map(item => item.count);
    const objetosPerdidos = objetosPerdidosArray.map(item => item.count);

    // Retornar como un array de dos listas
    return [objetosPerdidos, incidentes];
  } catch (error) {
    console.error('Error fetching general reports:', error);
    throw error;
  }
}

}

