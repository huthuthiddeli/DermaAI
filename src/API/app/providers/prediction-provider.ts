import logger from "@adonisjs/core/services/logger";
import { HttpContext } from "@adonisjs/core/http";
import { predictionModel } from "../Models/predictionCollection.js";
import { userDataModel } from "../Models/userDataCollection.js";
import { hashPassword } from "../utils/hash.js";

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
        if(data === null){
            logger.error("Coudln't find Prediction")
            return ctx.response.notFound("Couldn't find Prediction")
        }
        let foundItems = await predictionModel.find({email: data.email, password: data.password})
        
        for(let i = 0; i < foundItems.length; i++){
            foundItems[i].image = "bogenskind";
        }

        return foundItems
    }

    public async parsePredictionReqeust(ctx: HttpContext){
        const {email = "", password = "", image = "", prediction ="" } = ctx.request.body()
        return {email, password, image, prediction}
    }

    public async savePrediction(ctx: HttpContext){
        try{
            let data = await this.parsePredictionReqeust(ctx);

            if(data === null){
                logger.error("Couldn't parse PredictionReqeuest!");
                return ctx.response.notFound("Couldn't parse PredictionReqeuest!");
            }
    
            let foundUser = await this.findOneDB(await hashPassword(data.password), data.email);
            
            if(foundUser === null){
                logger.info("Couldn't find specified user!");
                return ctx.response.notFound("Couldn't find specified user!");
            }
    
    
            const schema = new predictionModel(data);
            let savedSchema = await schema.save();
    
            return ctx.response.status(200).ok(savedSchema);
        }catch(e){
            logger.error("Error in savePrediction: " + e);
            return ctx.response.internalServerError("Error in savePrediction: " + e);
        }
    }

    private async findOneDB(password: string, email: string){

        let data = await userDataModel.find({password: password, email: email})

        if(data === null || data === undefined){
            logger.info("Couldn't find specified user in findOneDB!");
            return null
        }

        return data
    }

}