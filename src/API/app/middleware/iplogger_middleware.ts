import type { HttpContext } from '@adonisjs/core/http'
import logger from '@adonisjs/core/services/logger'
import type { NextFn } from '@adonisjs/core/types/http'

const logStorage: Record<string, {count: number}> = {}

export default class IploggerMiddleware {
  async handle(ctx: HttpContext, next: NextFn) {
    const ip = ctx.request.ip();
    const route = ctx.request.url();
    const key = `${ip} -> ${route}`

    if(logStorage[key]){
      logStorage[key].count++;
      logger.info(`${key} [${logStorage[key].count}]`)
    }else{
      logStorage[key] = {count: 1};
      logger.info(`${key} [1]`)
    }

    const output = await next()
    return output
  }
}