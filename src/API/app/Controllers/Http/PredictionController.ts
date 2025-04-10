import { PredictionProvider } from "../../providers/prediction-provider.js";
import { HttpContext } from "@adonisjs/core/http";
import cache from '@adonisjs/cache/services/main'

export default class PredictionController{

    public async savePrediction(ctx: HttpContext){
        return await(await PredictionProvider.getInstance()).savePrediction(ctx);
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

    public async loadPrediction(ctx: HttpContext){
        return await (await PredictionProvider.getInstance()).loadPrediction(ctx);
    }

}