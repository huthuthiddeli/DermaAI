import mongoose, {Schema, Document} from "mongoose";
import paginate from "mongoose-paginate-v2";


export interface IPredictionData extends Document{
    email: string,
    password: string,
    image: any,
    prediction: Record<string, any>
}

export const predictionSchema = new Schema<IPredictionData>({
        email: {
            type: Schema.Types.String,
            required: true
        },
        password: {
            type: Schema.Types.String,
            required: true
        },
        image: {
            type: Schema.Types.Mixed,
            required: true
        },
        prediction: {
            type: Schema.Types.Map,
            required: true
        }
    },
    {
        collection: "predictions"
    }
)

predictionSchema.plugin(paginate);

export const predictionModel = mongoose.model<IPredictionData, mongoose.PaginateModel<IPredictionData>>('predictionData', predictionSchema)
