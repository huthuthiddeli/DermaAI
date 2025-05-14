import logger from '@adonisjs/core/services/logger'
import { HttpContext } from '@adonisjs/core/http'
import { checkState } from './db_func_service.js'
import { userDataModel } from '#models/userDataCollection'
import { inject } from '@adonisjs/core'
import hash from '@adonisjs/core/services/hash'

interface IUserData {
  email: string
  password: string
  mfa: boolean | null
  isAdmin: boolean | null
}

@inject()
export class UserProviderService {
  private constructor() {
    logger.info('User Provider created!')
  }

  private getRequestProperties(body: Record<string, any>) {
    const { email = '', password = '', mfa = false, isAdmin = false, id = '' } = body
    return id ? { email, password, mfa, isAdmin, id } : { email, password, mfa, isAdmin }
  }

  // *************************************************
  // FORMAT OF REQUEST AS FOLLOWS:
  // {
  //    "email": "test@gmail.com",
  //    "password": "testpassword",
  //    "mfa": true,
  //    "isAdmin": true
  // }
  //
  // PASSWORD IS HASHED ONCE IN AS THE PARAMETER
  // *************************************************

  public async saveUser(ctx: HttpContext) {
    await checkState()
    let body = ctx.request.body()
    let data = await this.getBodyWithHashedPassword(body)
    let obj = await this.getOneFromDB(data.password, data.email)
    if (obj) {
      return ctx.response.badRequest('Duplicate Credentials')
    }
    const schema = new userDataModel(data)
    let savedUser = await schema.save()

    return ctx.response.ok(savedUser)
  }
  // *************************************************
  // FORMAT OF REQUEST AS FOLLOWS:
  // {
  //   "email": "test@gmail.com",
  //   "password": "testpassword",
  //   "mfa": true
  // }
  //
  // PASSWORD IS HASHED ONCE IN AS THE PARAMETER
  // *************************************************

  public async validateUser(ctx: HttpContext) {
    await checkState()
    let body = ctx.request.body()
    let data = await this.getBodyWithHashedPassword(body)
    const existingUser = await this.getOneFromDB(data.password, data.email)
    if (!existingUser) {
      return ctx.response.status(404).send('User not found')
    }
    // Removes password so that is does not get sent to client
    const { password, ...safeData } = existingUser
    return ctx.response.status(200).json(safeData)
  }

  clearCollection = async (ctx: HttpContext) => {
    await userDataModel.deleteMany({})
    return ctx.response.ok("Collection Cleared!")
  }

  public async getAllUsers() {
    return await userDataModel.find({})
  }

  public async getUserMfa(ctx: HttpContext) {
    await checkState()

    let obj = await this.getBodyWithHashedPassword(ctx.request.body())
    const foundObj = await this.getOneFromDB(obj.password, obj.email)

    if (foundObj === null) {
      return ctx.response.notFound('User has not been found!')
    }

    const { password, ...safeData } = foundObj
    return ctx.response.status(200).json(safeData)
  }

  public async setIsAdmin(ctx: HttpContext) {
    let obj = await this.getBodyWithHashedPassword(ctx.request.body())
    const foundObj = await this.getOneFromDB(obj.password, obj.email)

    if (foundObj === null) {
      return ctx.response.notFound('User has not been found!')
    }

    foundObj.isAdmin = true
    await userDataModel.updateOne({ _id: foundObj.id }, foundObj)
    let item = await this.getOneFromDB(obj.password, obj.email)
    if (!item) {
      return ctx.response.internalServerError('User has not been found!')
    }
    const { password, ...params } = item

    return ctx.response.status(200).json(params)
  }

  public async setIsNotAdmin(ctx: HttpContext) {
    let obj = await this.getBodyWithHashedPassword(ctx.request.body())
    const foundObj = await this.getOneFromDB(obj.password, obj.email)
    if (foundObj === null) {
      return ctx.response.notFound('User has not been found!')
    }
    foundObj.isAdmin = false
    await userDataModel.updateOne({ id: foundObj.id }, foundObj)
    let item = await this.getOneFromDB(obj.password, foundObj.email)
    if (!item) {
      return ctx.response.internalServerError('An internal server error has occured!')
    }
    const { password, ...params } = item

    return ctx.response.status(200).json(params)
  }

  public async switchMfa(ctx: HttpContext) {
    await checkState()

    let obj = await this.getBodyWithHashedPassword(ctx.request.body())
    let foundObj = await this.getOneFromDB(obj.password, obj.email)
    if (!foundObj) {
      return ctx.response.notFound('User has not been found!')
    }

    foundObj.mfa = !foundObj.mfa
    await userDataModel.updateOne({ id: foundObj.id }, foundObj)
    let item = await this.getOneFromDB(obj.password, foundObj.email)
    if (!item) {
      logger.error('Item not found in DB!')
      return ctx.response.internalServerError('An internal server error has occured!')
    }
    const { password, ...params } = item

    return ctx.response.status(200).json(params)
  }

  private async getBodyWithHashedPassword(item: Record<string, any>, doHash: boolean = false) {
    let obj: IUserData = this.getRequestProperties(item)
    if(doHash){
      obj.password = await hash.make(obj.password)
    }
    return obj
  }

  private async getOneFromDB(password: string, email: string) {
    let items = await userDataModel.find({});
    for (let x of items) {
      const isPasswordValid = await hash.verify(x.password, password);
      
      if (isPasswordValid && x.email === email) {
        return x.toObject(); 
      }
    }
  
    return null;
  }
  
}
