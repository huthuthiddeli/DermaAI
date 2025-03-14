import mongoose, {Schema, Document, mongo} from "mongoose";
import paginate from 'mongoose-paginate-v2';


export const pictureSchema = new Schema<IPictureData>({
        picture: {
            type: Schema.Types.Mixed,
            required: true
        },
        diagnosis: {
            type: Schema.Types.String,
            required: true
        }
    },
        {
            collection: "testimages"
            // collection: "images"
        }
)

pictureSchema.plugin(paginate);

interface IPictureData extends Document{
    picture: any,
    diagnosis: String
}


// export const pictureModel = mongoose.model<IPictureData,mongoose.Model<IPictureData>>('testimages', pictureSchema, 'testimages')
export const pictureModel = mongoose.model<IPictureData, mongoose.PaginateModel<IPictureData>>('images', pictureSchema, 'images')