import logger from "@adonisjs/core/services/logger";
import { HttpContext } from "@adonisjs/core/http";
import { IPredictionData, userDataModel, predictionSchema } from "#models/predictionCollection";




export class PredictionProvider{
    private static instance: PredictionProvider;


    private constructor() {
        logger.info("Prediction Provider created!");
    }
    
    public static async getInstance(): Promise<PredictionProvider> {
        if (!PredictionProvider.instance) {
          PredictionProvider.instance = new PredictionProvider();
        }

        return PredictionProvider.instance;
    }

    public async loadPrediction(ctx: HttpContext){

    }

    private async parsePredictionReqeust(ctx: HttpContext): Promise<IPredictionData>{
        logger.info(ctx.request.headers())
        logger.info(ctx.request.body())
    }

    public async savePrediction(ctx: HttpContext){
        // logger.info(ctx.request.pa)
        await this.parsePredictionReqeust(ctx);
    }


}