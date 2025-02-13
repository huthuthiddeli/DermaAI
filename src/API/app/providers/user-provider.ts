import logger from '@adonisjs/core/services/logger'
import { HttpContext } from '@adonisjs/core/http'
import { userDataModel } from '../Models/userDataCollection.js'
import { checkState } from '../utils/db-funcs.js'
import { hashPassword } from '../utils/hash.js'

// type Response = {
//     status: boolean,
//     data: any
// }

interface IUserData {
    email: string,
    password: string
}

export class UserProvider{
    private static instance: UserProvider;

    private constructor() {
        logger.info("User Provider created!");
    }   

    public static async getInstance(): Promise<UserProvider> {
        if (!UserProvider.instance) {
            UserProvider.instance = new UserProvider();
        }

        return UserProvider.instance;
    }

    private getRequestProperties(body: Record<string, any>) {
        const { email = "", password = "", id = "" } = body;
        return id ? { email, password, id } : { email, password };
    }
    
    // *************************************************
    // FORMAT OF REQUEST AS FOLLOWS:
    // {
    //    "email": "test@gmail.com",
    //    "password": "testpassword"
    // }
    //
    // PASSWORD IS HASHED ONCE IN AS THE PARAMETER
    // *************************************************
    
    public async saveUser(ctx:HttpContext) {
        await checkState()
        let body = ctx.request.body();
    
        if(!this.isCreateUserRequest(body)){
          return ctx.response.status(400).send("Request was not right!");
        }
    
        let data = this.getRequestProperties(body);
        data["password"] = await hashPassword(data["password"]);
        const schema = new userDataModel(data);
        let savedUser = await schema.save();
    
        return ctx.response.ok(savedUser);
    }

    private isCreateUserRequest(body: any): body is IUserData {
        // logger.info(typeof body.email === 'string' &&
        //   typeof body.password === 'string')
    
        return (
          typeof body.email === 'string' &&
          typeof body.password === 'string'
        );
    }


    // *************************************************
    // FORMAT OF REQUEST AS FOLLOWS:
    // {
    //   "email": "test@gmail.com",
    //   "password": "9f735e0df9a1ddc702bf0a1a7b83033f9f7153a00c29de82cedadc9957289b05",
    //   "id": "6797324c15addfc1bc992240"
    // }
    //
    // PASSWORD IS HASHED ONCE IN AS THE PARAMETER
    // *************************************************

    public async validateUser(ctx: HttpContext){
        await checkState();
        let body = ctx.request.body();
    
        !this.isCreateUserRequest(body) ? ctx.response.status(400).send("Request was not right!") : null;

        let data = this.getRequestProperties(body);
        data["password"] = await hashPassword(data["password"]);
        const existingUser = await userDataModel.find({email: data.email, password: data.password});

        if (existingUser.length === 0) {
            return ctx.response.status(404).send("User not found");
        }
    
        // Removes password so that is does not get sent to client
        const {password, ...safeData} = existingUser[0].toObject();
    
        return ctx.response.status(200).json(safeData);
    }

    clearCollection = async (ctx: HttpContext) => {
        await userDataModel.deleteMany({});

        (await this.getAllUsers()).length === 0 ? ctx.response.status(200) : ctx.response.status(500).send("Failed to clear collection");
    }
  
  public async getAllUsers(){
    return await userDataModel.find({});
  }
}