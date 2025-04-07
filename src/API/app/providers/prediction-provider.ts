import logger from "@adonisjs/core/services/logger";
import { HttpContext } from "@adonisjs/core/http";
import { predictionModel } from "../Models/predictionCollection.js";
import { userDataModel } from "../Models/userDataCollection.js";

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
        let data = await this.parsePredictionReqeust(ctx);
        if(!data){
            logger.error("Coudln't find Prediction")
            return ctx.response.notFound("Couldn't find Prediction")
        }

        let foundItems = await predictionModel.find({email: data.email, password: data.password})
        return foundItems
    }

    private async parsePredictionReqeust(ctx: HttpContext){
        const {email = "", password = "", image = "", prediction ="" } = ctx.request.body()
        return {email, password, image, prediction}
    }

    public async savePrediction(ctx: HttpContext){
        let data = await this.parsePredictionReqeust(ctx);

        if(await this.findOneDB(data.password, data.email)){
            return ctx.response.notFound("Could not find specified user!");
        }

        if(!data){
            logger.error("Couldn't parse PredictionReqeuest!");
            return;
        }

        const schema = new predictionModel(data);
        let savedSchema = await schema.save();

        return ctx.response.status(200).ok(savedSchema);
    }

    private async findOneDB(password: string, email: string){
        let data = await userDataModel.find({password: password, email: email})
        if(!data){
            return null
        }

        return data[0].toObject()
    }

}