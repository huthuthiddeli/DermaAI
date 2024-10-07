import mongoose, {Schema, Document} from "mongoose";

interface IPictureData extends Document{
    picture: String;
}

let pictureSchema = new Schema<IPictureData>({
        picture: {
            type: Schema.Types.String,
            required: true
        }
    },
        {
            collection: "images"
        }
)

const pictureModel = mongoose.model<IPictureData>('images', pictureSchema, 'images')

export {pictureModel, pictureSchema}