import type { HttpContext } from "@adonisjs/core/http";
import {saveUser, validateUser} from '../../providers/userProvider.js'

export default class UserController{

    public async saveUser(ctx: HttpContext){
        return await saveUser(ctx);
    }
    
    public async validateUser(ctx: HttpContext){
        return await validateUser(ctx);
    }

    public async clearCollection(ctx: HttpContext){
        await this.clearCollection(ctx);
    }

    public async getAllUsers(): Promise<any>{
        return await this.getAllUsers();
    }
}