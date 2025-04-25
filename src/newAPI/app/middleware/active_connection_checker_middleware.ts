import type { HttpContext } from '@adonisjs/core/http'
import type { NextFn } from '@adonisjs/core/types/http'
import { checkState, connectToDatabase } from '#services/db_func_service'

export default class ActiveConnectionCheckerMiddleware {
  async handle(ctx: HttpContext, next: NextFn) {
    /**
     * Middleware logic goes here (before the next call)
     */

    if (!(await connectToDatabase())) {
      return ctx.response
        .status(500)
        .send({ message: "Databaseconnection couldn't be established!" })
    }

    await checkState()

    /**
     * Call next method in the pipeline and return its output
     */
    const output = await next()
    return output
  }
}
