import env from '#start/env'
import { defineConfig } from '@adonisjs/core/logger'

const isProduction = env.get('NODE_ENV') === 'production'

const loggerConfig = defineConfig({
  default: 'app',
  loggers: {
    app: {
      enabled: true,
      name: env.get('APP_NAME', 'DermaAI'),
      level: env.get('LOG_LEVEL', 'info'),
      transport: isProduction
        ? undefined // Use default JSON logger
        : {
            targets: [
              {
                target: 'pino-pretty',
                level: 'info',
                options: {},
              },
            ],
          },
    },
  },
})

export default loggerConfig
