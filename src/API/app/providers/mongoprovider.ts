import mongoose from "mongoose"
import env from '#start/env'
import { prettyPrintError } from "@adonisjs/core"
import { dataModel } from "../datastructure/test.js"
import logger from '@adonisjs/core/services/logger'
import app from '@adonisjs/core/services/app'

app.ready(() => {
    logger.info("Boot completed! Connecting!...");
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

const writeToDatabase = async (json: Record<string, any>): Promise<{status: boolean, data: any}> => {

  let savedUser;
  await checkState()

  if(Object.keys(json).length == 0){
    return {status: false, data: "NO BODY"}
  }

  try{
    const schema = new dataModel({
      data: json
    })

    savedUser = await schema.save();
  
  } catch (error) {
    logger.error('Error saving user: ' + error)
    prettyPrintError(error)
    return {status: false, data: undefined}
  }

  return {status: true, data: savedUser};
}


const getAllFromDatabase = async (): Promise<any> => {
  logger.info("Getting all Data from Database!")
  await checkState()
  
  return JSON.stringify(await dataModel.find({}))
}

const gracefulShutdown = () => {
  logger.info("Various connections will be closed!")
  mongoose.connection.close()
  logger.info("STR-C detected!")
  process.exit(0)
};

app.terminating(() => {
  gracefulShutdown()
})


// Listen for termination signals (e.g., Ctrl+C in terminal)
process.on('SIGINT', gracefulShutdown);
process.on('SIGTERM', gracefulShutdown);

export {connectToDatabase, writeToDatabase, getAllFromDatabase}