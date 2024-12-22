import env from '#start/env'
import { prettyPrintError } from '@adonisjs/core'
import app from '@adonisjs/core/services/app'
import logger from '@adonisjs/core/services/logger'
import mongoose from 'mongoose'
import { userDataModel } from '../datastructure/userDataCollection.js'
import { HttpContext } from '@adonisjs/core/http'
import { hashPassword } from '../utils/hash.js'
import { ObjectId } from 'mongodb'

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
    return !(
      typeof body.firstname === 'string' &&
      typeof body.email === 'string' &&
      typeof body.secondname === 'string' &&
      typeof body.password === 'string'
    );
}

function getRequestProperties(body: Record<string, any>) {
  let password: string = "";
  let firstname: string = "";
  let lastname: string = "";
  let email: string = "";
  let id: any = undefined;

  Object.keys(body).forEach(key => {
    switch (key){
      case "firstname": 
        firstname = body[key];
        break;
      case "lastname":
        lastname = body[key];
        break;
      case "email": 
        email = body[key];
        break;
      case "password":
        password = body[key];
        break;
      case "id":
        id = body[key];
        break;
    }
  });

  return id === undefined ? {firstname, lastname, email, password}: {firstname, lastname, email, password, id};
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

export const saveUser = async (ctx:HttpContext) => {
    await checkState()
    let body = ctx.request.body();

    if(!isCreateUserRequest(body)){
      return ctx.response.status(400).send("Request was not right!");
    }

    let data = getRequestProperties(body);
    data["password"] = await hashPassword(data["password"]);
    const schema = new userDataModel(data);
    let savedUser = await schema.save();

    return ctx.response.ok(savedUser);
}


export const validateUser = async (ctx: HttpContext) => {
  let body = ctx.request.body();

  if(!isCreateUserRequest(body)){
    return ctx.response.status(400).send("Request was not right!");
  }

  let data = getRequestProperties(body);
  data["password"] = await hashPassword(data["password"]);
  const newOBj = new ObjectId(data.id);

  
  const existingUser = await userDataModel.findOne({_id: newOBj})

  if (!existingUser) {
    return ctx.response.status(404).send("User not found");
  }

  // Removes password so that is does not get sent to client
  const {password, ...safeData} = existingUser.toObject();

  return ctx.response.status(200).json(safeData);
}

export const clearCollection = async (ctx: HttpContext) => {
  await userDataModel.deleteMany({});

  let data = await getAllUsers();

  if(data.length === 0){
    return ctx.response.status(200);
  }else{
    return ctx.response.status(500);
  }
}


export const getAllUsers = async () =>{
  let data = await userDataModel.find({});

  return data;
}