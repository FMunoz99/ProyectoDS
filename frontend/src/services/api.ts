// api.ts
import axios from 'axios';

// Aquí se configura la URL base de tu API (ajusta según tu configuración)
const api = axios.create({
  baseURL: 'http://localhost:8080/api', // Asegúrate de que esta URL sea la correcta
  headers: {
    'Content-Type': 'application/json',
  },
});

export default api;


//
