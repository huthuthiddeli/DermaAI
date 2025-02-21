import logger from '@adonisjs/core/services/logger'
import { HttpContext } from '@adonisjs/core/http'
import { userDataModel } from '../Models/userDataCollection.js'
import { checkState } from '../utils/db-funcs.js'
import { hashPassword } from '../utils/hash.js'

roles: enum {

}


interface IUserData {
    email: string,
    password: string
    mfa: boolean | null
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
        const { email = "", password = "", mfa = false, id = "" } = body;
        return id ? { email, password, mfa, id } : { email, password, mfa };
    }
    
    // *************************************************
    // FORMAT OF REQUEST AS FOLLOWS:
    // {
    //    "email": "test@gmail.com",
    //    "password": "testpassword",
    //    "mfa": true
    // }
    //
    // PASSWORD IS HASHED ONCE IN AS THE PARAMETER
    // *************************************************
    
    public async saveUser(ctx:HttpContext) {
        await checkState()
        let body = ctx.request.body();    
        // if(!this.isCreateUserRequest(body)){ return ctx.response.status(400).send("Request was not right!");}
        let data = await this.getBodyWithHashedPassword(body)
        let obj = await this.getOneFromDB(data.password, data.email);
        if(obj){ return ctx.response.badRequest("Duplicate Credentials"); }
        const schema = new userDataModel(data);
        let savedUser = await schema.save();
    
        return ctx.response.ok(savedUser);
    }

    // private isCreateUserRequest(body: any): body is IUserData {
    //     return (
    //       typeof body.email === 'string' &&
    //       typeof body.password === 'string'
    //     );
    // }


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

    public async validateUser(ctx: HttpContext){
        await checkState();
        let body = ctx.request.body();

        // !this.isCreateUserRequest(body) ? ctx.response.status(401).send("Request was not right!") : null;
        let data = await this.getBodyWithHashedPassword(body);
        logger.info(data);
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

   public async getUserMfa(ctx: HttpContext) {
        await checkState();

        let obj = await this.getBodyWithHashedPassword(ctx.request.body());
        const foundObj = await userDataModel.find({email: obj.email, password: obj.password})

        if(foundObj === null){ return ctx.response.notFound("Body has not been found!"); }
        if(foundObj.length === 0 || foundObj.length > 1){
            logger.error("Error when looking for MFA")
            return ctx.response.internalServerError("Unexpected Server error");
        }

        const {password, ...safeData} = foundObj[0].toObject()
        return ctx.response.status(200).json(safeData);
    }

    public async switchMfa(ctx: HttpContext){
        await checkState();

        let obj = await this.getBodyWithHashedPassword(ctx.request.body());
        let foundObj = await this.getOneFromDB(obj.password, obj.email); 
        if(!foundObj){ return ctx.response.notFound(); }
        foundObj.mfa = !foundObj.mfa;
        await userDataModel.updateOne({password: foundObj.password, email: foundObj.email}, foundObj)
        let item = await this.getOneFromDB(foundObj.password, foundObj.email);
        if(!item) { return ctx.response.badGateway();}
        const {password, ...params} = item;
        
        return ctx.response.status(200).json(params);
    }

    private async getBodyWithHashedPassword(item: Record<string, any>){
        let obj: IUserData = this.getRequestProperties(item);
        obj.password = await hashPassword(obj.password)
        return obj
    }

    private async getOneFromDB(password: string, email: string){
        let data = await userDataModel.find({email: email, password: password})
        if(!data || data.length != 1){
            return null;
        }

        return data[0].toObject();
    }
}