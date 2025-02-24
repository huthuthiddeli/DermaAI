import { PredictionProvider } from "#providers/prediction-provider.js";
import { HttpContext } from "@adonisjs/core/http";

export default class PredictionController{

    public async savePrediction(ctx: HttpContext){
        return await(await PredictionProvider.getInstance()).savePrediction(ctx);
    }

    public async loadPrediction(ctx: HttpContext){
        return await (await PredictionProvider.getInstance()).loadPrediction(ctx);
    }

}