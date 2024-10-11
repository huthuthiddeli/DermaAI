import env from '#start/env'
import { prettyPrintError } from '@adonisjs/core'
import app from '@adonisjs/core/services/app'
import logger from '@adonisjs/core/services/logger'
import mongoose from 'mongoose'
import { userDataModel } from '../datastructure/userDataCollection.js'


app.ready(() => {
    logger.info("userProvider ready!")
    connectToDatabase()
})

interface IUserData {
    firstname: string,
    secondname: string,
    email: string,
    password: string
}


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

function isCreateUserRequest(body: any): body is IUserData {
    return (
      typeof body.firstname === 'string' &&
      typeof body.email === 'string' &&
      typeof body.secondname === 'string' &&
      typeof body.password === 'string'
    );
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

    return true
}

const saveUser = async (json: Record<string, string>) : Promise<{status: boolean, data: any}> => {

    await checkState()
    let savedData

    //CHECKS IF THERE IS AN EMPTY BODY
    if(Object.keys(json).length == 0){
        logger.info("No Body!")
        return {status: false, data: "NO BODY"}
    }

    //VALIDATE IF STRING IS IN RIGHT FORMAT
    if(!isCreateUserRequest(json)){
        logger.info("Request was wrong format!")
        return {status: false, data: "FORMAT WRONG"}
    }

    try{
        let schema = new userDataModel({
            firstname: json['firstname'],
            secondname: json['secondname'],
            email: json['email'],
            password: json['password']
        })

        savedData = await schema.save()
    }catch(ex){
        logger.error("Error has been throw: %s", ex)
        prettyPrintError(ex)
        return {status: false, data: "Error!"}
    }

    return {status: true, data: savedData}
}


export default saveUser