import mongoose, {Schema, Document} from "mongoose";

interface IUserData extends Document{
    email: string,
    password: string
}

let userSchema = new Schema<IUserData>({
        email: {
            type: Schema.Types.String,
            required: true
        },
        password: {
            type: Schema.Types.String,
            required: true
        }       
    },
    {
        collection: "userData"
    }
)

const userDataModel = mongoose.model<IUserData>('userData', userSchema)

export {userSchema, userDataModel}