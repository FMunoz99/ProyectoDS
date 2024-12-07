import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist', // Carpeta donde se guardarán los archivos generados para producción
  },
  base: '/', // Define la base para el despliegue en Amplify
})
