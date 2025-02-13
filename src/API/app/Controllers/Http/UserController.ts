import type { HttpContext } from "@adonisjs/core/http";
import { UserProvider } from "../../providers/user-provider.js";

export default class UserController{
    public saveUser = async(ctx: HttpContext) =>{
        return await (await UserProvider.getInstance()).saveUser(ctx);
    }
    
    public validateUser = async(ctx: HttpContext) => {
        return await (await (await UserProvider.getInstance()).validateUser(ctx));
    }

    public clearCollection = async(ctx: HttpContext) => {
        await await (await UserProvider.getInstance()).clearCollection(ctx);
    }

    public getAllUsers = async() => {
        return await (await UserProvider.getInstance()).getAllUsers();  
    }
}