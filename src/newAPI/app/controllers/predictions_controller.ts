import { HttpContext } from '@adonisjs/core/http'
import { PredictionProviderService } from '#services/prediction_provider_service'
import { inject } from '@adonisjs/core'

@inject()
export default class PredictionController {
  constructor(protected predictionProvider: PredictionProviderService) {}

  public async savePrediction(ctx: HttpContext) {
    return await this.predictionProvider.savePrediction(ctx)
  }

  // public async loadPrediction(ctx: HttpContext){
  // let body = await (await PredictionProvider.getInstance()).parsePredictionReqeust(ctx);
  //     return cache.getOrSet({
  //         key: `user:${body.email+body.password}`,
  //         factory: async () => {
  //             const data = await (await PredictionProvider.getInstance()).loadPrediction(ctx);
  //             return data
  //         },
  //         ttl: 60 * 60 * 24, // 1 day
  //     })
  // }

  public async loadPrediction(ctx: HttpContext) {
    return await this.predictionProvider.loadPrediction(ctx)
  }
}
