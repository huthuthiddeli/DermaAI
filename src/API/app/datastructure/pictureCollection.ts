import mongoose, {Schema, Document, Model} from "mongoose";
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
            collection: "images"
        }
)

pictureSchema.plugin(paginate);

interface IPictureData extends Document{
    picture: any,
    diagnosis: String
}

// const model = mongoose.model<InstitutionDocument,mongoose.PaginateModel<InstitutionDocument>>('Institutions', institutionSchema, 'institutions');

export const pictureModel = mongoose.model<IPictureData, mongoose.PaginateModel<IPictureData>>('images', pictureSchema, 'images')