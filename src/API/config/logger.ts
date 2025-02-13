// filepath: /c:/Users/Jonas/Documents/Programming/DermaAI/src/API/config/logger.ts
import env from '#start/env'
import { defineConfig } from '@adonisjs/core/logger'

const loggerConfig = defineConfig({
  default: 'app',

  /**
   * The loggers object can be used to define multiple loggers.
   * By default, we configure only one logger (named "app").
   */
  loggers: {
    app: {
      enabled: true,
      name: env.get('APP_NAME'),
      level: env.get('LOG_LEVEL', 'info'),
    
      transport: {
        targets: [
          {
            target: 'pino-pretty',
            level: 'info',
            options: {}
          },
          {
            target: 'pino-rotate',
            level: 'info',
            options: {
              file: 'logs/log',
              period: '1d', // Rotate daily
              count: 7, // Keep 7 days of logs
              rotate: true, // Enable rotation
              compress: true // Compress rotated files
            }
          }
        ]
      }
    },
  },
})

export default loggerConfig

/**
 * Inferring types for the list of loggers you have configured
 * in your application.
 */
declare module '@adonisjs/core/types' {
  export interface LoggersList extends InferLoggers<typeof loggerConfig> {}
}