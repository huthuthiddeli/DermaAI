import app from "@adonisjs/core/services/app";
import logger from "@adonisjs/core/services/logger";
import mongoose from 'mongoose'
import { prettyPrintError } from "@adonisjs/core";
import env from '#start/env'
import { pictureModel, pictureSchema } from "../datastructure/pictureCollection.js";

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
        mongoose.connect(connectionString)
    }catch(err){
  
      logger.error("a critical error has occured: %s", err)
      prettyPrintError(err)
      process.exitCode = -1
      return false
    }
  
    logger.info("Connected!")
    return true
}

const savePicture = async (pictureString: String): Promise<{status: boolean, data: any}>  => {

    await checkState()

    if(!pictureString){
        logger.info("No picturestring was given!")
        return {status: false, data: undefined}        
    }

    let savedPicture;

    try{
        const schema = new pictureModel({
            picture: pictureString
        })

        savedPicture = await schema.save()

    }catch(ex){
        logger.error("Error occured: %s", ex)
        prettyPrintError(ex)
        return {status: false, data: savedPicture}
    }

    logger.info("Picture saved: %s", savedPicture)
    return {status: true, data: savedPicture}
}

const getAllPictues = async () => {
    return pictureModel.find({})
}



export {savePicture, getAllPictues}