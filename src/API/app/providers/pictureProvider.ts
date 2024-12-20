import app from "@adonisjs/core/services/app";
import logger from "@adonisjs/core/services/logger";
import mongoose from 'mongoose'
import { prettyPrintError } from "@adonisjs/core";
import env from '#start/env'
import { pictureModel } from "../datastructure/pictureCollection.js";
import { HttpContext } from "@adonisjs/core/http";

app.ready(() => {
    logger.info("PictureProvider ready!")
    connectToDatabase()
})


const checkState = async (): Promise<void> => {
    let state = mongoose.connection.readyState
  
    switch(state){
      case 3:
        logger.info('Client disconnected! Have to reconnect!')
        await connectToDatabase()
        return
      case 0: 
        logger.info('Client disconnected! Have to reconnect!')
        await connectToDatabase()
        return
    }
}

const connectToDatabase = async (): Promise<boolean> => {
    let connectionString = env.get("DB_CONNECTION_STRING", 'null') as string
  
    if(connectionString === 'null'){
        logger.error("No connectionstring detected!")
        return false
    }
  
    try{
        mongoose.connect(connectionString, {
            

        })
    }catch(err){
  
      logger.error("a critical error has occured: %s", err)
      prettyPrintError(err)
      process.exitCode = -1
      return false
    }
    return true
}


/*
* Acutal function used to save to the databse
*/
const savePicture = async (record: Record<string, string>): Promise<{status: boolean, data: any}>  => {

    await checkState()

    if(Object.keys(record).length != 2){
        logger.info("Parameters didnt work!")
        return {status: false, data: undefined}        
    }

    let savedPicture;

    try{
        const schema = new pictureModel({
            picture: record["picture"],
            diagnosis: record["diagnosis"]
        })

        savedPicture = await schema.save()

    }catch(ex){
        logger.error("Error occured: %s", ex)
        prettyPrintError(ex)
        return {status: false, data: savedPicture}
    }

    return {status: true, data: savedPicture}
}

let page: number = 1;

const findPictures = async (ctx: HttpContext) => {
    let queryParams = ctx.request.qs();

    Object.keys(queryParams).forEach(e => logger.info(query));

    const options = {
        page: page,
        limit: Number(env.get('DOC_AMMOUNT')),
        collation: {
            locale: 'en',
        },
    };
 
    page += 1;

    try {

        let data = await pictureModel.paginate({}, options)

        //console.log(data)

        return ctx.response.status(200).json(data);
        // await pictureModel.paginate({}, options)
        // return ctx.response.status(200).json(allPictures);
    } catch (error) {
        console.error(error);
        return ctx.response.status(500).json({ message: 'An error occurred' });
    }
   
    
    return;
}

export {savePicture, findPictures}