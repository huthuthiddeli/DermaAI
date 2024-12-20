import mongoose, {Schema, Document} from "mongoose";
import paginate from "mongoose-paginate-v2";


interface IUserData extends Document{
    firstname: string,
    lastname: string,
    email: string,
    password: string
}

export const userSchema = new Schema<IUserData>({

        firstname: {
            type: Schema.Types.String,
            required: true
        },
        lastname: {
            type: Schema.Types.String,
            required: true
        },
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

userSchema.plugin(paginate);


export const userDataModel = mongoose.model<IUserData, mongoose.PaginateModel<IUserData>>('userData', userSchema)
