import mongoose, {Schema, Document} from "mongoose";
import paginate from "mongoose-paginate-v2";


interface IUserData extends Document{
    email: string,
    password: string,
    mfa: boolean
}

export const userSchema = new Schema<IUserData>({
        email: {
            type: Schema.Types.String,
            required: true
        },
        password: {
            type: Schema.Types.String,
            required: true
        },
        mfa: {
            type:Schema.Types.Boolean,
            required: true
        }
    },
    {
        collection: "userData"
    }
)

userSchema.plugin(paginate);

export const userDataModel = mongoose.model<IUserData, mongoose.PaginateModel<IUserData>>('userData', userSchema)
