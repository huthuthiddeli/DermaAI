import type { HttpContext } from "@adonisjs/core/http";
import {saveUser, validateUser} from '../../providers/userProvider.js'

export default class UserController{

    async saveUser(ctx: HttpContext){
        return await saveUser(ctx);
    }
    
    async validateUser(ctx: HttpContext){
        return await validateUser(ctx);
    }

    async clearCollection(ctx: HttpContext){
        await this.clearCollection(ctx);
    }

    async getAllUsers(): Promise<any>{
        return await this.getAllUsers();
    }
}