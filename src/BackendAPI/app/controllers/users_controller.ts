import type { HttpContext } from '@adonisjs/core/http'
import { UserProviderService } from '#services/user_provider_service'
import { inject } from '@adonisjs/core'

@inject()
export default class UsersController {
  constructor(protected UserProvider: UserProviderService) {}

  public saveUser = async (ctx: HttpContext) => {
    return await this.UserProvider.saveUser(ctx)
  }

  public validateUser = async (ctx: HttpContext) => {
    return await this.UserProvider.validateUser(ctx)
  }

  public clearCollection = async (ctx: HttpContext) => {
    await await this.UserProvider.clearCollection(ctx)
  }

  public getAllUsers = async () => {
    return await this.UserProvider.getAllUsers()
  }

  public getMfaFromUser = async (ctx: HttpContext) => {
    return await this.UserProvider.getUserMfa(ctx)
  }

  public activateMfa = async (ctx: HttpContext) => {
    return await this.UserProvider.switchMfa(ctx)
  }

  public checkIfAdmin = async (ctx: HttpContext) => {
    return await this.UserProvider.setIsAdmin(ctx)
  }

  public checkIfUser = async (ctx: HttpContext) => {
    return await this.UserProvider.setIsNotAdmin(ctx)
  }
}
